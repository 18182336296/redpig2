package com.redpigmall.manage.admin.action.operative.ztc;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.domain.ZTCGoldLog;

/**
 * 
 * <p>
 * Title: RedPigZtcManageAction.java
 * </p>
 * 
 * <p>
 * Description: 直通车管理类
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
 * @date 2014-10-31
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigZtcManageAction extends BaseAction {
	/**
	 * 直通车设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "直通车设置", value = "/ztc_set*", rtype = "admin", rname = "竞价直通车", rcode = "ztc_set", rgroup = "运营")
	@RequestMapping({ "/ztc_set" })
	public ModelAndView ztc_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ztc_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 直通车设置保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ztc_status
	 * @param ztc_price
	 * @param ztc_goods_view
	 * @return
	 */
	@SecurityMapping(title = "直通车设置保存", value = "/ztc_set_save*", rtype = "admin", rname = "竞价直通车", rcode = "ztc_set", rgroup = "运营")
	@RequestMapping({ "/ztc_set_save" })
	public ModelAndView ztc_set_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String ztc_status,
			String ztc_price, String ztc_goods_view) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		config.setZtc_price(CommUtil.null2Int(ztc_price));
		config.setZtc_status(CommUtil.null2Boolean(ztc_status));
		config.setZtc_goods_view(CommUtil.null2Int(ztc_goods_view));
		if (id.equals("")) {
			this.configService.saveEntity(config);
		} else {
			this.configService.updateById(config);
		}
		mv.addObject("op_title", "直通车设置成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/ztc_set");
		return mv;
	}
	
	/**
	 * 直通车申请列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param goods_name
	 * @param userName
	 * @param store_name
	 * @param ztc_status
	 * @param ztc_pay_status
	 * @return
	 */
	@SecurityMapping(title = "直通车申请列表", value = "/ztc_apply*", rtype = "admin", rname = "竞价直通车", rcode = "ztc_set", rgroup = "运营")
	@RequestMapping({ "/ztc_apply" })
	public ModelAndView ztc_apply(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String goods_name, String userName, String store_name,
			String ztc_status, String ztc_pay_status) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ztc_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getZtc_status()) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "ztc_apply_time", "desc");
			
			maps.put("ztc_status", 1);
			if (!CommUtil.null2String(goods_name).equals("")) {
				
				maps.put("goods_name_like", goods_name.trim());
			}
			if (!CommUtil.null2String(userName).equals("")) {
				
				
				maps.put("goods_store_user_userName", userName.trim());
			}
			if (!CommUtil.null2String(store_name).equals("")) {
				
				maps.put("redPig_store_name", store_name);
				
			}
			if (!CommUtil.null2String(ztc_status).equals("")) {
				
				maps.put("ztc_status", CommUtil.null2Int(ztc_status));
			}
			if (!CommUtil.null2String(ztc_pay_status).equals("")) {
				
				maps.put("ztc_pay_status", CommUtil.null2Int(ztc_pay_status));
			}
			
			IPageList pList = this.goodsService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
			mv.addObject("userName", userName);
			mv.addObject("store_name", store_name);
			mv.addObject("ztc_status", ztc_status);
			mv.addObject("ztc_pay_status", ztc_pay_status);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ztc_set");
		}
		return mv;
	}
	
	/**
	 * 直通车商品审核
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "直通车商品审核", value = "/ztc_apply_edit*", rtype = "admin", rname = "竞价直通车", rcode = "ztc_set", rgroup = "运营")
	@RequestMapping({ "/ztc_apply_edit" })
	public ModelAndView ztc_apply_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ztc_apply_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getZtc_status()) {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ztc_set");
		}
		return mv;
	}
	
	/**
	 * 直通车商品查看
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "直通车商品查看", value = "/ztc_apply_view*", rtype = "admin", rname = "竞价直通车", rcode = "ztc_set", rgroup = "运营")
	@RequestMapping({ "/ztc_apply_view" })
	public ModelAndView ztc_apply_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ztc_apply_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getZtc_status()) {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ztc_set");
		}
		return mv;
	}
	
	/**
	 * 直通车商品审核保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param ztc_status
	 * @param ztc_admin_content
	 * @return
	 */
	@SecurityMapping(title = "直通车商品审核保存", value = "/ztc_apply_save*", rtype = "admin", rname = "竞价直通车", rcode = "ztc_set", rgroup = "运营")
	@RequestMapping({ "/ztc_apply_save" })
	public ModelAndView ztc_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String ztc_status, String ztc_admin_content) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getZtc_status()) {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			obj.setZtc_admin(SecurityUserHolder.getCurrentUser());
			obj.setZtc_admin_content(ztc_admin_content);
			Calendar cal = Calendar.getInstance();
			if ((CommUtil.null2Int(ztc_status) == 2)
					&& (cal.after(obj.getZtc_begin_time()))) {
				obj.setZtc_dredge_price(obj.getZtc_price());
			} else {
				obj.setZtc_status(CommUtil.null2Int(ztc_status));
			}
			this.goodsService.updateById(obj);
			boolean ret = true;
			if ((ret) && (obj.getZtc_status() == 2)) {
				User user = obj.getGoods_store().getUser();
				user.setGold(user.getGold() - obj.getZtc_gold());
				this.userService.updateById(user);
				ZTCGoldLog log = new ZTCGoldLog();
				log.setAddTime(new Date());
				log.setZgl_content("开通直通车，消耗金币");
				log.setZgl_gold(obj.getZtc_gold());
				log.setZgl_goods_id(obj.getId());
				log.setGoods_name(obj.getGoods_name());
				log.setStore_name(obj.getGoods_store().getStore_name());
				log.setUser_name(obj.getGoods_store().getUser().getUsername());
				log.setZgl_type(1);
				this.ztcGoldLogService.saveEntity(log);

				GoldLog glog = new GoldLog();
				glog.setAddTime(new Date());
				glog.setGl_content("申请直通车成功扣除金币");
				glog.setGl_count(CommUtil.null2Int(Integer.valueOf(obj
						.getZtc_gold())));
				glog.setGl_user(user);
				glog.setGl_type(-1);
				this.goldLogService.saveEntity(glog);
			}
			mv.addObject("currentPage", currentPage);
			mv.addObject("op_title", "直通车审核成功");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ztc_apply?currentPage=" + currentPage);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ztc_set");
		}
		return mv;
	}
	
	/**
	 * 直通车商品
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param goods_name
	 * @param userName
	 * @param store_name
	 * @return
	 */
	@SecurityMapping(title = "直通车商品", value = "/ztc_items*", rtype = "admin", rname = "竞价直通车", rcode = "ztc_set", rgroup = "运营")
	@RequestMapping({ "/ztc_items" })
	public ModelAndView ztc_items(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String goods_name, String userName, String store_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ztc_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getZtc_status()) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "ztc_apply_time", "desc");
			
			maps.put("ztc_status_more_than_equal", 2);
			if (!CommUtil.null2String(goods_name).equals("")) {
				
				maps.put("goods_name_like", goods_name.trim());
			}
			if (!CommUtil.null2String(userName).equals("")) {
				
				maps.put("goods_store_user_userName", goods_name.trim());
			}
			if (!CommUtil.null2String(store_name).equals("")) {
				
				maps.put("redPig_store_name", store_name);
			}
			
			IPageList pList = this.goodsService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
			mv.addObject("userName", userName);
			mv.addObject("store_name", store_name);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ztc_set");
		}
		return mv;
	}
	
	/**
	 * 直通车金币日志
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param goods_name
	 * @param store_name
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "直通车金币日志", value = "/ztc_gold_log*", rtype = "admin", rname = "竞价直通车", rcode = "ztc_set", rgroup = "运营")
	@RequestMapping({ "/ztc_gold_log" })
	public ModelAndView ztc_gold_log(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_name, String store_name, String beginTime,
			String endTime) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ztc_gold_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getZtc_status()) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
			if (!CommUtil.null2String(goods_name).equals("")) {
				maps.put("goods_name_like", goods_name.trim());
			}
			
			if (!CommUtil.null2String(store_name).equals("")) {
				maps.put("store_name_like", store_name.trim());
			}
			
			if (!CommUtil.null2String(beginTime).equals("")) {
				maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			}
			
			if (!CommUtil.null2String(endTime).equals("")) {
				maps.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
			}
			
			IPageList pList = this.ztcGoldLogService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
			mv.addObject("store_name", store_name);
			mv.addObject("beginTime", beginTime);
			mv.addObject("endTime", endTime);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ztc_set");
		}
		return mv;
	}
}
