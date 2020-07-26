package com.redpigmall.view.web.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.FreeClass;
import com.redpigmall.domain.FreeGoods;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.User;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigFreeViewAction.java
 * </p>
 * 
 * <p>
 * Description: 前台0元试用控制器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-11-18
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Controller
public class RedPigFreeViewAction extends BaseAction{
	
	/**
	 * 试用
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param cls
	 * @return
	 */
	@RequestMapping({ "/free/index" })
	public ModelAndView freegoods_index(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String cls) {
		ModelAndView mv = new RedPigJModelAndView("free_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,6, "addTime", "desc");
		
		if ((cls != null) && (!cls.equals(""))) {
			maps.put("class_id", CommUtil.null2Long(cls));
			mv.addObject("cls_id", cls);
		}
		maps.put("freeStatus", 5);
		
		IPageList pList = this.freegoodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		maps.clear();
		List<FreeClass> fcls = this.freeClassService.queryPageList(maps);
		maps.put("freeStatus", 5);
		maps.put("orderBy", "apply_count");
		maps.put("orderType", "desc");
		
		List<FreeGoods> hot_fgs = this.freegoodsService.queryPageList(maps, 0, 6);
		
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", this.freeTools);
		mv.addObject("hots", hot_fgs);
		return mv;
	}
	
	/**
	 * 免费商品
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/free/view" })
	public ModelAndView free_view(String id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("free_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		FreeGoods obj = this.freegoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj == null) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查看的商品已经下架");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		if ((obj.getFreeStatus() == -5) || (obj.getFreeStatus() == 0)) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else if (obj.getEndTime().before(new Date())) {
			obj.setFreeStatus(10);
			this.freegoodsService.updateById(obj);
			Goods goods = this.goodsService.selectByPrimaryKey(obj.getGoods_id());
			if (goods != null) {
				goods.setWhether_free(0);
				this.goodsService.updateById(goods);
			}
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查看的商品已经下架");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			Map<String,Object> maps = Maps.newHashMap();
	        maps.put("freeStatus", 5);
	        maps.put("orderBy", "apply_count");
	        maps.put("orderType", "desc");
	        
			List<FreeGoods> hot_fgs = this.freegoodsService.queryPageList(maps,0,6);
			
			mv.addObject("hots", hot_fgs);
			mv.addObject("freeTools", this.freeTools);
			mv.addObject("obj", obj);
		}
		return mv;
	}
	
	/**
	 * 免费日志
	 * @param id
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/free/logs" })
	public ModelAndView free_logs(String id, HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("free_logs_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20, "evaluate_time", "desc");
		maps.put("evaluate_status", 1);
		
		maps.put("freegoods_id", CommUtil.null2Long(id));
		
		IPageList pList = this.freeapplylogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/free/logs.html", "",params, pList, mv);
		mv.addObject("freeTools", this.freeTools);
		mv.addObject("id", id);
		return mv;
	}
	
	/**
	 * 0元试用申请
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "0元试用申请", value = "/free_apply*", rtype = "buyer", rname = "0元试用申请", rcode = "free_apply", rgroup = "在线购物")
	@RequestMapping({ "/free_apply" })
	public ModelAndView free_apply(String id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("free_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		FreeGoods fg = this.freegoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("freegoods_id", CommUtil.null2Long(id));
		
		List<FreeApplyLog> fals1 = this.freeapplylogService.queryPageList(params);
		
		params.clear();
		params.put("user_id", user.getId());
		params.put("evaluate_status", 0);
		
		List<FreeApplyLog> fals2 = this.freeapplylogService.queryPageList(params);
		
		if ((fals1.size() > 0) || (fals2.size() > 0)
				|| (fg.getFreeStatus() != 5) || (fg.getCurrent_count() == 0)
				|| (fg.getEndTime().before(new Date()))) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
			if ((fg.getFreeStatus() != 5)
					|| (fg.getEndTime().before(new Date()))) {
				mv.addObject("op_title", "此0元试用已结束");
				if (fg.getEndTime().before(new Date())) {
					fg.setFreeStatus(10);
					this.freegoodsService.updateById(fg);
				}
			}
			if (fg.getCurrent_count() == 0) {
				mv.addObject("op_title", "此0元试用库存不足");
			}
			mv.addObject("url", CommUtil.getURL(request) + "/free/index");
		} else {
			params.put("orderBy", "default_val desc,");
			params.put("orderType", "addTime desc");
			
			List<Address> addrs = this.addressService.queryPageList(params);
			
			mv.addObject("addrs", addrs);
			String apply_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("apply_session",
					apply_session);
			mv.addObject("apply_session", apply_session);
			mv.addObject("id", id);
		}
		return mv;
	}
	
	/**
	 * 0元试用申请
	 * @param id
	 * @param request
	 * @param response
	 * @param apply_reason
	 * @param apply_session
	 * @param addr_id
	 * @return
	 */
	@SuppressWarnings({ "unused" })
	@SecurityMapping(title = "0元试用申请", value = "/free_apply*", rtype = "buyer", rname = "0元试用申请", rcode = "free_apply", rgroup = "在线购物")
	@RequestMapping({ "/free_apply_save" })
	public ModelAndView free_apply_saveEntity(String id, HttpServletRequest request,
			HttpServletResponse response, String apply_reason,
			String apply_session, String addr_id) {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String apply_session1 = (String) request.getSession(false)
				.getAttribute("apply_session");
		if (apply_session1.equals(apply_session)) {
			FreeGoods fg = this.freegoodsService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			User user = SecurityUserHolder.getCurrentUser();
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			params.put("freegoods_id", CommUtil.null2Long(id));
			
			List<FreeApplyLog> fals1 = this.freeapplylogService.queryPageList(params);
			
			params.clear();
			params.put("user_id", user.getId());
			params.put("evaluate_status", 0);
			
			List<FreeApplyLog> fals2 = this.freeapplylogService.queryPageList(params);
			
			if ((fals1.size() > 0) || (fals2.size() > 0)
					|| (fg.getFreeStatus() != 5)
					|| (fg.getCurrent_count() == 0)
					|| (fg.getEndTime().before(new Date()))) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
				if ((fg.getFreeStatus() != 5)
						|| (fg.getEndTime().before(new Date()))) {
					mv.addObject("op_title", "此0元试用已结束");
					if (fg.getEndTime().before(new Date())) {
						fg.setFreeStatus(10);
						this.freegoodsService.updateById(fg);
					}
				}
				if (fg.getCurrent_count() == 0) {
					mv.addObject("op_title", "此0元试用库存不足");
				}
				mv.addObject("url", CommUtil.getURL(request) + "/free/index");
			} else {
				Address addr = this.addressService.selectByPrimaryKey(CommUtil
						.null2Long(addr_id));
				if (fg != null) {
					FreeApplyLog fal = new FreeApplyLog();
					fal.setAddTime(new Date());
					fal.setFreegoods_id(fg.getId());
					fal.setWhether_self(fg.getFreeType());
					fal.setStore_id(fg.getStore_id());
					fal.setFreegoods_name(fg.getFree_name());

					fal.setReceiver_Name(addr.getTrueName());
					fal.setReceiver_area(addr.getArea().getParent().getParent()
							.getAreaName()
							+ addr.getArea().getParent().getAreaName()
							+ addr.getArea().getAreaName());
					fal.setReceiver_area_info(addr.getArea_info());
					fal.setReceiver_mobile(addr.getMobile());
					fal.setReceiver_telephone(addr.getTelephone());
					fal.setReceiver_zip(addr.getZip());
					fal.setUser_id(user.getId());
					fal.setUser_name(user.getUserName());
					this.freeapplylogService.saveEntity(fal);
					fg.setApply_count(fg.getApply_count() + 1);
					this.freegoodsService.updateById(fg);
					fal.setApply_reason(CommUtil.filterHTML(apply_reason));
					mv.addObject("op_title", "申请成功，请耐心等待审核");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/free/index");
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您有尚未评价的0元试用或您申请过此0元试用");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/free/index");
				}
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
}
