package com.redpigmall.module.weixin.manage.buyer.action;

import java.util.Date;
import java.util.List;
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
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import com.redpigmall.domain.IntegralGoods;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.User;
import com.redpigmall.domain.virtual.TransInfo;

/**
 * 
 * 
 * <p>
 * Title: RedPigWeixinUserIntegralAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端买家中心积分订单控制器
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
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class RedPigWeixinUserIntegralAction extends BaseAction{
	
	
	/**
	 * 用户中心积分订单列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param igo_status
	 * @return
	 */
	@SecurityMapping(title = "用户中心积分订单列表", value = "/buyer/integral_order_list*", rtype = "buyer", rname = "积分订单", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/integral_order_list" })
	public ModelAndView integral_order_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String igo_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/integral_order_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/integral_order_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		if (this.configService.getSysConfig().getIntegralStore()) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
	        maps.put("igo_user_id", SecurityUserHolder.getCurrentUser().getId());
	        
			if (!CommUtil.null2String(igo_status).equals("")) {
				
				maps.put("igo_status", CommUtil.null2Int(igo_status));
				mv.addObject("igo_status", igo_status);
			}
			IPageList pList = this.integralGoodsOrderService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("orderFormTools", this.orderFormTools);
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}

	/**
	 * 取消积分订单
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "取消积分订单", value = "/buyer/integral_order_cancel*", rtype = "buyer", rname = "积分订单", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/integral_order_cancel" })
	public ModelAndView integral_order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getIgo_user().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				obj.setIgo_status(-1);
				this.integralGoodsOrderService.updateById(obj);
				List<IntegralGoods> igs = this.integralGoodsOrderService
						.queryIntegralGoods(CommUtil.null2String(obj.getId()));
				for (IntegralGoods ig : igs) {
					IntegralGoods goods = ig;
					int sale_count = this.integralGoodsOrderService
							.queryIntegralOneGoodsCount(obj,
									CommUtil.null2String(ig.getId()));
					goods.setIg_goods_count(goods.getIg_goods_count()
							+ sale_count);
					this.integralGoodsService.updateById(goods);
				}
				User user = obj.getIgo_user();
				user.setIntegral(user.getIntegral()
						+ obj.getIgo_total_integral());
				this.userService.updateById(user);
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("取消" + obj.getIgo_order_sn() + "积分兑换，返还积分");
				log.setIntegral(obj.getIgo_total_integral());
				log.setIntegral_user(obj.getIgo_user());
				log.setType("integral_order");
				this.integralLogService.saveEntity(log);
				mv.addObject("op_title", "积分兑换取消成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/integral_order_list");
			}
		} else {

			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，无该积分订单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list");
		}
		return mv;
	}

	/**
	 * 用户中心积分订单详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "用户中心积分订单详情", value = "/buyer/integral_order_view*", rtype = "buyer", rname = "积分订单", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/integral_order_view" })
	public ModelAndView integral_order_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/integral_order_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getIgo_user().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
				mv.addObject("currentPage", currentPage);
				mv.addObject("integralViewTools", this.integralViewTools);
				
				mv.addObject("orderFormTools", this.orderFormTools);
				boolean query_ship = false;
				if (!CommUtil.null2String(obj.getIgo_ship_code()).equals("")) {
					query_ship = true;
				}
				mv.addObject("query_ship", Boolean.valueOf(query_ship));
			}
		} else {

			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，无该积分订单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list");
		}
		return mv;
	}
	
	/**
	 * 用户中心积分订单确认收货
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "用户中心积分订单确认收货", value = "/buyer/integral_order_cofirm*", rtype = "buyer", rname = "积分订单", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/integral_order_cofirm" })
	public ModelAndView integral_order_cofirm(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/integral_order_cofirm.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getIgo_user().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			}
		} else {

			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，无该积分订单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list");
		}
		return mv;
	}
	
	/**
	 * 用户中心积分订单确认收货保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "用户中心积分订单确认收货保存", value = "/buyer/integral_order_cofirm_save*", rtype = "buyer", rname = "积分订单", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/integral_order_cofirm_save" })
	public ModelAndView integral_order_cofirm_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getIgo_user().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				obj.setIgo_status(40);
				this.integralGoodsOrderService.updateById(obj);
				mv.addObject("op_title", "确认收货成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/integral_order_list");
			}
		} else {

			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，无该积分订单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list?currentPage="
					+ currentPage);
		}
		return mv;
	}
	
	/**
	 * 用户中心积分订单物流信息
	 * @param request
	 * @param response
	 * @param order_id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "用户中心积分订单物流信息", value = "/buyer/integral_ship_detail*", rtype = "buyer", rname = "积分订单", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/integral_ship_detail" })
	public ModelAndView integral_ship_detail(HttpServletRequest request,
			HttpServletResponse response, String order_id, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/integral_ship_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		IntegralGoodsOrder order = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(order_id));
		TransInfo transInfo = this.ShipTools
				.query_IntegralOrdership_getData(CommUtil.null2String(order_id));
		if (transInfo != null) {
			transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
					order.getIgo_express_info(), "express_company_name"));
			transInfo.setExpress_ship_code(order.getIgo_ship_code());
		}
		if (!CommUtil.null2String(type).equals("")) {
			mv.addObject("type", type);
		}
		mv.addObject("order", order);
		mv.addObject("transInfo", transInfo);
		return mv;
	}
}
