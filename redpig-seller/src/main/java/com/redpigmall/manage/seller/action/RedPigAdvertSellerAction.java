package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: AdvertSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家广告管理类，商家可以使用金币购买广告位
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-5-30
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigAdvertSellerAction extends BaseAction{
	/**
	 * 广告列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告列表", value = "/advert_list*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping({ "/advert_list" })
	public ModelAndView advert_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/advert_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,30, "addTime", "desc");
		
		maps.put("ap_status", 1);
		
		maps.put("ap_use_status_no", 1);
		
		maps.put("ap_sys_type_no", 0);
		
		IPageList pList = this.advertPositionService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 广告购买
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "广告购买", value = "/advert_apply*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping({ "/advert_apply" })
	public ModelAndView advert_apply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/advert_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		
		if (ap.getAp_price() > user.getGold()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "金币不足，不能申请");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/advert_list");
		} else {
			String ap_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("ap_session", ap_session);
			mv.addObject("ap_session", ap_session);
			mv.addObject("ap", ap);
			mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId()));
		}
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param day
	 * @param ap_id
	 */
	@RequestMapping( { "/advert_vefity" } )
	public void advert_vefity( 
					HttpServletRequest request,
					HttpServletResponse response, 
					String day, 
					String ap_id ){
		
		boolean ret = true;
		AdvertPosition ap = this.advertPositionService.selectByPrimaryKey( CommUtil.null2Long( ap_id ) );
		int total_price	= ap.getAp_price() * CommUtil.null2Int( day );
		User user		= this.userService.selectByPrimaryKey( SecurityUserHolder.getCurrentUser().getId() );
		user = user.getParent() == null ? user : user.getParent();
		
		if ( total_price > user.getGold() ) {
			ret = false;
		}
		
		response.setContentType( "text/plain" );
		response.setHeader( "Cache-Control", "no-cache" );
		response.setCharacterEncoding( "UTF-8" );
		try {
			PrintWriter writer = response.getWriter();
			writer.print( ret );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}



	
	/**
	 * 广告购买保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ap_id
	 * @param ad_begin_time
	 * @param day
	 * @param ap_session
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "广告购买保存", value = "/advert_apply_save*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping({ "/advert_apply_save" })
	public void advert_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String ap_id,
			String ad_begin_time, String day, String ap_session) {
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(false));
		String ap_session1 = CommUtil.null2String(request.getSession(false).getAttribute("ap_session"));
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		
		if (ap_session1.equals("")) {
			json.put("op_title", "禁止表单重复提交");
			json.put("url", CommUtil.getURL(request) + "/advert_list");
			return_json(JSON.toJSONString(json), response);
		} else {
			request.getSession(false).removeAttribute("ap_session");
			Advert advert = null;
			
			if (id.equals("")) {
				advert = (Advert) WebForm.toPo(request, Advert.class);
				advert.setAddTime(new Date());
				AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil.null2Long(ap_id));
				advert.setAd_ap(ap);
				advert.setAd_begin_time(CommUtil.formatDate(ad_begin_time));
				Calendar cal = Calendar.getInstance();
				cal.add(6, CommUtil.null2Int(day));
				advert.setAd_end_time(cal.getTime());
				advert.setAd_user(user);
				advert.setAd_gold(ap.getAp_price() * CommUtil.null2Int(day));
			} else {
				Advert obj = this.advertService.selectByPrimaryKey(CommUtil.null2Long(id));
				advert = (Advert) WebForm.toPo(request, obj);
			}
			if (!advert.getAd_ap().getAp_type().equals("text")) {
				String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
				String saveFilePathName = request.getSession()
						.getServletContext().getRealPath("/")
						+ uploadFilePath + File.separator + "advert";
				Map<String, Object> map = Maps.newHashMap();
				String fileName = "";
				
				if (advert.getAd_acc() != null) {
					fileName = advert.getAd_acc().getName();
				}
				
				try {
					map = CommUtil.saveFileToServer(request, "acc",saveFilePathName, fileName, null);
					
					Accessory acc = null;
					if (fileName.equals("")) {
						if (map.get("fileName") != "") {
							acc = new Accessory();
							acc.setName(CommUtil.null2String(map.get("fileName")));
							acc.setExt(CommUtil.null2String(map.get("mime")));
							acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
							acc.setPath(uploadFilePath + "/advert");
							acc.setWidth(CommUtil.null2Int(map.get("width")));
							acc.setHeight(CommUtil.null2Int(map.get("height")));
							acc.setAddTime(new Date());
							this.accessoryService.saveEntity(acc);
							advert.setAd_acc(acc);
						}
					} else if (map.get("fileName") != "") {
						acc = advert.getAd_acc();
						acc.setName(CommUtil.null2String(map.get("fileName")));
						acc.setExt(CommUtil.null2String(map.get("mime")));
						acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
						acc.setPath(uploadFilePath + "/advert");
						acc.setWidth(CommUtil.null2Int(map.get("width")));
						acc.setHeight(CommUtil.null2Int(map.get("height")));
						acc.setAddTime(new Date());
						this.accessoryService.updateById(acc);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (id.equals("")) {
				this.advertService.saveEntity(advert);

				user.setGold(user.getGold() - advert.getAd_gold());
				this.userService.updateById(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_content("购买广告扣除金币");
				log.setGl_count(advert.getAd_gold());
				log.setGl_money(advert.getAd_gold());
				log.setGl_user(user);
				log.setGl_type(-1);
				this.goldLogService.saveEntity(log);
			} else {
				this.advertService.updateById(advert);
			}
			json.put("ret", Boolean.valueOf(true));
			json.put("op_title", "广告申请成功");
			json.put("url", CommUtil.getURL(request) + "/advert_my");
			return_json(JSON.toJSONString(json), response);
		}
	}
	
	/**
	 * 广告编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "广告编辑", value = "/advert_apply_edit*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping({ "/advert_apply_edit" })
	public ModelAndView advert_apply_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/advert_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Advert obj = this.advertService.selectByPrimaryKey(CommUtil.null2Long(id));
		String ap_session = CommUtil.randomString(32);
		request.getSession(false).setAttribute("ap_session", ap_session);
		mv.addObject("ap_session", ap_session);
		mv.addObject("ap", obj.getAd_ap());
		mv.addObject("obj", obj);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}
	
	/**
	 * 我的广告
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "我的广告", value = "/advert_my*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping({ "/advert_my" })
	public ModelAndView advert_my(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/advert_my.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		maps.put("ad_user_id", user.getId());
		
		IPageList pList = this.advertService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 广告延时
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "广告延时", value = "/advert_delay*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping({ "/advert_delay" })
	public ModelAndView advert_delay(HttpServletRequest request,
			HttpServletResponse response, String id) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/advert_delay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Advert obj = this.advertService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		
		if (obj.getAd_ap().getAp_price() > user.getGold()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "金币不足，不能申请");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/advert_list");
		} else {
			String delay_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("delay_session",
					delay_session);
			mv.addObject("delay_session", delay_session);
			mv.addObject("obj", obj);
			mv.addObject("ap", obj.getAd_ap());
			mv.addObject("user", user);
		}
		return mv;
	}
	
	/**
	 * 失败广告删除
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "失败广告删除", value = "/advert_delete*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping({ "/advert_delete" })
	public ModelAndView advert_deleteById(
			HttpServletRequest request,
			HttpServletResponse response, String id) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Advert obj = this.advertService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		
		if (obj.getAd_user().getId().toString().equals(user.getId().toString())) {
			if (obj.getAd_status() == -1) {
				this.advertService.deleteById(obj.getId());
				mv.addObject("op_title", "删除成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/advert_my");
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/advert_list");
		}
		return mv;
	}
	
	/**
	 * 广告购买保存
	 * @param request
	 * @param response
	 * @param id
	 * @param day
	 * @param delay_session
	 */
	@SecurityMapping(title = "广告购买保存", value = "/advert_delay_save*", rtype = "seller", rname = "广告管理", rcode = "advert_seller", rgroup = "其他管理")
	@RequestMapping({ "/advert_delay_save" })
	public void advert_delay_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String day,
			String delay_session) {
		Map<String,Object> json = Maps.newHashMap();
		
		json.put("ret", Boolean.valueOf(false));
		String delay_session1 = CommUtil.null2String(request.getSession(false).getAttribute("delay_session"));
		
		if (delay_session1.equals("")) {
			json.put("op_title", "禁止表单重复提交");
			json.put("url", CommUtil.getURL(request) + "/advert_list");
			return_json(JSON.toJSONString(json), response);
		} else {
			request.getSession(false).removeAttribute("delay_session");
			Advert advert = this.advertService.selectByPrimaryKey(CommUtil.null2Long(id));
			
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			
			user = user.getParent() == null ? user : user.getParent();
			
			int total_gold = advert.getAd_ap().getAp_price() * CommUtil.null2Int(day);
			
			if (total_gold <= user.getGold()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(advert.getAd_end_time());
				cal.add(6, CommUtil.null2Int(day));
				advert.setAd_end_time(cal.getTime());
				advert.setAd_gold(advert.getAd_gold() + total_gold);
				this.advertService.updateById(advert);

				user.setGold(user.getGold() - total_gold);
				this.userService.updateById(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_content("广告延时扣除金币");
				log.setGl_count(total_gold);
				log.setGl_user(user);
				log.setGl_type(-1);
				log.setGl_money(advert.getAd_gold());
				this.goldLogService.saveEntity(log);
				json.put("ret", Boolean.valueOf(true));
				json.put("op_title", "广告延时成功");
				json.put("url", CommUtil.getURL(request) + "/advert_my");
				return_json(JSON.toJSONString(json), response);
			} else {
				json.put("op_title", "金币不足，不能延时");
				json.put("url", CommUtil.getURL(request)
						+ "/advert_delay?id=" + id);
				return_json(JSON.toJSONString(json), response);
			}
		}
	}

	public void return_json(String json, HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
