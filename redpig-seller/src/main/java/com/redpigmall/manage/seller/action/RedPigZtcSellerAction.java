package com.redpigmall.manage.seller.action;

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
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigZtcSellerAction.java 
 * </p>
 * 
 * <p>
 * Description:直通车
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
 * @date 2014-7-17
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigZtcSellerAction extends BaseAction{
	
	/**
	 * 直通车申请
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "直通车申请", value = "/ztc_apply*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping({ "/ztc_apply" })
	public ModelAndView ztc_apply(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ztc_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getZtc_status()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("user", user);
		return mv;
	}
	
	/**
	 * 直通车加载商品
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "直通车加载商品", value = "/ztc_items*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping({ "/ztc_items" })
	public ModelAndView ztc_goods(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ztc_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,15,null,null);
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			maps.put("goods_name_like", CommUtil.null2String(goods_name));
		}
		
		maps.put("goods_store_id", store.getId());//必须是当前店铺所属商品
		maps.put("goods_status", Integer.valueOf(0));
		// 直通车状态，1为开通申请待审核，2为审核通过,-1为审核失败,3为已经开通
		//所以查询直通车商品条件为>=4或者<=0
		maps.put("ztc_status_1", 0);
		maps.put("ztc_status_2", 4);
		
		IPageList pList = this.goodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/ztc_items.html", "", "&goods_name=" + goods_name,
				pList, mv);
		return mv;
	}
	
	/**
	 * 直通车申请保存
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param ztc_price
	 * @param ztc_begin_time
	 * @param ztc_gold
	 * @param ztc_session
	 * @return
	 */
	@SecurityMapping(title = "直通车申请保存", value = "/ztc_apply_save*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping({ "/ztc_apply_save" })
	public String ztc_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String ztc_price,
			String ztc_begin_time, String ztc_gold, String ztc_session) {
		String url = "redirect:/ztc_apply";
		if (!this.configService.getSysConfig().getZtc_status()) {
			request.getSession(false).setAttribute("url",
					CommUtil.getURL(request) + "/ztc_apply");
			request.getSession(false).setAttribute("op_title", "系统未开启直通车");
			url = "redirect:/error";
		} else {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods_id));
			goods.setZtc_status(1);
			goods.setZtc_pay_status(1);
			goods.setZtc_begin_time(CommUtil.formatDate(ztc_begin_time));
			goods.setZtc_gold(CommUtil.null2Int(ztc_gold));
			goods.setZtc_price(CommUtil.null2Int(ztc_price));
			goods.setZtc_apply_time(new Date());
			this.goodsService.updateById(goods);
			request.getSession(false).setAttribute("url",
					CommUtil.getURL(request) + "/ztc_list");
			request.getSession(false).setAttribute("op_title", "直通车申请成功,等待审核");
			url = "redirect:/success";
		}
		return url;
	}
	
	/**
	 * 直通车申请列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "直通车申请列表", value = "/ztc_apply_list*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping({ "/ztc_apply_list" })
	public ModelAndView ztc_apply_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ztc_apply_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getZtc_status()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "ztc_begin_time", "desc");
			maps.put("goods_store_user_id", SecurityUserHolder.getCurrentUser().getId());
			
			maps.put("ztc_status", Integer.valueOf(1));
			if (!CommUtil.null2String(goods_name).equals("")) {
				maps.put("goods_name_like", goods_name.trim());
			}
			
			IPageList pList = this.goodsService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("goods_name", goods_name);
		}
		return mv;
	}
	
	/**
	 * 直通车商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "直通车商品列表", value = "/ztc_list*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping({ "/ztc_list" })
	public ModelAndView ztc_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ztc_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getZtc_status()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "ztc_apply_time", "desc");
			
			maps.put("goods_store_user_id", SecurityUserHolder.getCurrentUser().getId());
			
			maps.put("ztc_status_more_than_equal", Integer.valueOf(2));
			
			if (!CommUtil.null2String(goods_name).equals("")) {
				maps.put("goods_name_like", goods_name.trim());
			}
			IPageList pList = this.goodsService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		return mv;
	}
	
	/**
	 * 直通车申请查看
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "直通车申请查看", value = "/ztc_apply_view*", rtype = "seller", rname = "竞价直通车", rcode = "ztc_seller", rgroup = "促销推广")
	@RequestMapping({ "/ztc_apply_view" })
	public ModelAndView ztc_apply_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ztc_apply_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getZtc_status()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启直通车");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (obj.getGoods_store().getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "参数错误，不存在该直通车信息");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/ztc_list");
			}
		}
		return mv;
	}
}
