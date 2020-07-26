package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.ExpressCompanyCommon;
import com.redpigmall.domain.ExpressInfo;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.ShipAddress;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.virtual.TransInfo;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigOrderSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家订单控制器，卖家中心订单管理所有控制器都在这里
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
 * @date 2014-5-20
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({"deprecation","serial","rawtypes" ,"unchecked","static-access"})
@Controller
public class RedPigOrderSellerAction extends BaseAction{
	
	private static final BigDecimal WHETHER_ENOUGH = new BigDecimal(0.00);
	
	private static final Map<Integer, String> STATUS_MAP = new HashMap<Integer, String>() {
		{
			put(0, "已取消");
			put(10, "待付款");
			put(15, "线下支付待审核");
			put(16, "货到付款待发货");
			put(20, "已付款");
			put(30, "已发货");
			put(40, "已收货");
			put(50, "已完成");
			put(60, "已结束");
		}
	};

	private static final Map<String, String> PAYMENT_MAP = new HashMap<String, String>() {
		{
			put(null, "未支付");
			put("", "未支付");
			put("alipay", "支付宝");
			put("alipay_wap", "手机网页支付宝");
			put("alipay_app", "手机支付宝APP");
			put("tenpay", "财付通");
			put("bill", "快钱");
			put("chinabank", "网银在线");
			put("outline", "线下支付");
			put("balance", "预存款支付");
			put("payafter", "货到付款");
			put("paypal", "paypal");
		}
	};

	private static final Map<String, String> TYPE_MAP = new HashMap<String, String>() {
		{
			put(null, "PC订单");
			put("", "PC订单");
			put("weixin", "微信订单");
			put("android", "Android订单");
			put("ios", "IOS订单");
		}
	};
	
	/**
	 * 卖家订单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_status
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @param buyer_userName
	 * @return
	 */
	@SecurityMapping(title = "卖家订单列表", value = "/order*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order" })
	public ModelAndView order(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String order_status, String order_id, String beginTime,
			String endTime, String buyer_userName) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_order.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		maps.put("store_id", user.getStore().getId().toString());
		maps.put("order_cat_no", 2);
		
		
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {
				maps.put("order_status1", Integer.valueOf(10));
				maps.put("order_status2", Integer.valueOf(16));
			}
			if (order_status.equals("order_pay")) {
				maps.put("order_status", Integer.valueOf(20));
			}
			if (order_status.equals("order_shipping")) {
				maps.put("order_status", Integer.valueOf(30));
			}
			if (order_status.equals("order_evaluate")) {
				maps.put("order_status", Integer.valueOf(40));
			}
			if (order_status.equals("order_finish")) {
				maps.put("order_status", Integer.valueOf(50));
			}
			if (order_status.equals("order_cancel")) {
				maps.put("order_status", Integer.valueOf(0));
			}
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			maps.put("order_id_like", order_id);
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(ends,"yyyy-MM-dd hh:mm:ss"));
			mv.addObject("endTime", endTime);
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			maps.put("user_name", buyer_userName);
		}
		
		IPageList pList = this.orderFormService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("order_status", order_status == null ? "all" : order_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("store", user.getStore());
		return mv;
	}
	
	/**
	 * 卖家待发货订单管理
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @param buyer_userName
	 * @return
	 */
	@SecurityMapping(title = "卖家待发货订单管理", value = "/order_ship*", rtype = "seller", rname = "发货管理", rcode = "order_ship_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_ship" })
	public ModelAndView order_ship(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String beginTime, String endTime, String buyer_userName) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_order_ship.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "payTime", "desc");
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		
		maps.put("store_id", user.getStore().getId().toString());
		
		maps.put("order_cat_no", 2);
		
		maps.put("order_status_more_than_equal", 16);
		
		maps.put("order_status_less_than_equal", 21);
		
		if (!CommUtil.null2String(order_id).equals("")) {
			maps.put("order_id_like", order_id);
		}
		
		if (!CommUtil.null2String(beginTime).equals("")) {
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(ends,"yyyy-MM-dd hh:mm:ss"));
			mv.addObject("endTime", endTime);
		}
		
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			maps.put("user_name", buyer_userName);
		}
		
		IPageList pList = this.orderFormService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("store", user.getStore());
		return mv;
	}
	
	/**
	 * 卖家发货待收货订单管理
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @param buyer_userName
	 * @return
	 */
	@SecurityMapping(title = "卖家发货待收货订单管理", value = "/order_confirm*", rtype = "seller", rname = "收货管理", rcode = "order_confirm_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_confirm" })
	public ModelAndView order_confirm(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String beginTime, String endTime, String buyer_userName) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_order_confirm.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "shipTime", "desc");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		maps.put("store_id", user.getStore().getId().toString());
		
		maps.put("order_cat_no", 2);
		
		maps.put("order_status1", Integer.valueOf(30));
		maps.put("order_status2", Integer.valueOf(35));
		
		if (!CommUtil.null2String(order_id).equals("")) {
			maps.put("order_id_like", order_id);
		}
		
		if (!CommUtil.null2String(beginTime).equals("")) {
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(ends,"yyyy-MM-dd hh:mm:ss"));
			mv.addObject("endTime", endTime);
		}
		
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			maps.put("user_name_like", buyer_userName);
		}
		
		IPageList pList = this.orderFormService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_id", order_id);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("buyer_userName", buyer_userName);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("store", user.getStore());
		mv.addObject("orderTools", this.orderTools);
		return mv;
	}
	
	/**
	 * 卖家订单详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家订单详情", value = "/order_view*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_view" })
	public ModelAndView order_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/order_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			if (obj != null) {
				String temp = obj.getSpecial_invoice();
				if ((temp != null) && (!"".equals(temp))) {
					Map of_va = JSON.parseObject(temp);
					mv.addObject("of_va", of_va);
				}
			}
			mv.addObject("obj", obj);
			mv.addObject("store", store);
			mv.addObject("orderFormTools", this.orderFormTools);
			mv.addObject("express_company_name", this.orderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/order");
		}
		return mv;
	}
	
	/**
	 * 卖家取消订单
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家取消订单", value = "/order_cancel*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_cancel" })
	public ModelAndView order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_order_cancel.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/order");
		}
		return mv;
	}
	
	/**
	 * 卖家取消订单保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param state_info
	 * @param other_state_info
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "卖家取消订单保存", value = "/order_cancel_save*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_cancel_save" })
	public String order_cancel_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Date nowDate = new Date();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setOrder_status(0);
			if ((obj.getCoupon_info() != null)
					&& (!"".equals(obj.getCoupon_info()))) {
				Map m = JSON.parseObject(obj.getCoupon_info());
				CouponInfo cpInfo = this.couponInfoService.selectByPrimaryKey(CommUtil
						.null2Long(m.get("couponinfo_id")));
				if (cpInfo != null) {
					if (nowDate.before(cpInfo.getEndDate())) {
						cpInfo.setStatus(0);
					} else {
						cpInfo.setStatus(-1);
					}
				}
			}
			this.orderFormService.updateById(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("取消订单");
			ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
			ofl.setLog_user_name(SecurityUserHolder.getCurrentUser()
					.getUserName());
			ofl.setOf(obj);
			if (state_info.equals("其他原因")) {
				ofl.setState_info(other_state_info);
			} else {
				ofl.setState_info(state_info);
			}
			this.orderFormLogService.saveEntity(ofl);
			User buyer = this.userService.selectByPrimaryKey(CommUtil.null2Long(obj
					.getUser_id()));
			Map<String, Object> map = Maps.newHashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId());
			String json = JSON.toJSONString(map);
			if (obj.getOrder_form() == 0) {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_tobuyer_order_cancel_notify", buyer.getEmail(),
						json, null, obj.getStore_id());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request),
						"sms_tobuyer_order_cancel_notify", buyer.getMobile(),
						json, null, obj.getStore_id());
			} else {
				this.msgTools.sendEmailFree(CommUtil.getURL(request),
						"email_tobuyer_order_cancel_notify", buyer.getEmail(),
						json, null);
				this.msgTools.sendSmsFree(CommUtil.getURL(request),
						"sms_tobuyer_order_cancel_notify", buyer.getMobile(),
						json, null);
			}
		}
		return "redirect:order?currentPage=" + currentPage;
	}
	
	/**
	 * 卖家调整订单费用
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家调整订单费用", value = "/order_fee*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_fee" })
	public ModelAndView order_fee(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_order_fee.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/order");
		}
		return mv;
	}
	
	/**
	 * 卖家调整订单费用保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param goods_amount
	 * @param ship_price
	 * @param totalPrice
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "卖家调整订单费用保存", value = "/order_fee_save*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_fee_save" })
	public String order_fee_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_amount, String ship_price, String totalPrice)
			throws Exception {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (CommUtil.null2Double(obj.getCommission_amount()) <= CommUtil
				.null2Double(goods_amount)) {
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
					.getStore_id()));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (user.getStore().getId().equals(store.getId())) {
				obj.setGoods_amount(BigDecimal.valueOf(CommUtil
						.null2Double(goods_amount)));
				obj.setShip_price(BigDecimal.valueOf(CommUtil
						.null2Double(ship_price)));
				obj.setTotalPrice(BigDecimal.valueOf(CommUtil
						.null2Double(totalPrice)));
				obj.setOperation_price_count(obj.getOperation_price_count() + 1);
				this.orderFormService.updateById(obj);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("调整订单费用");
				ofl.setState_info("调整订单总金额为:" + totalPrice + ",调整运费金额为:"
						+ ship_price);
				ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
				ofl.setLog_user_name(SecurityUserHolder.getCurrentUser()
						.getUserName());
				ofl.setOf(obj);
				this.orderFormLogService.saveEntity(ofl);
				User buyer = this.userService.selectByPrimaryKey(CommUtil.null2Long(obj
						.getUser_id()));
				Map<String, Object> map = Maps.newHashMap();
				map.put("buyer_id", buyer.getId().toString());
				map.put("seller_id", store.getUser().getId().toString());
				map.put("order_id", obj.getId());
				String json = JSON.toJSONString(map);
				if (obj.getOrder_form() == 0) {
					this.msgTools.sendEmailCharge(CommUtil.getURL(request),
							"email_tobuyer_order_update_fee_notify",
							buyer.getEmail(), json, null, obj.getStore_id());
					this.msgTools.sendSmsCharge(CommUtil.getURL(request),
							"sms_tobuyer_order_fee_notify", buyer.getMobile(),
							json, null, obj.getStore_id());
				} else {
					this.msgTools.sendEmailFree(CommUtil.getURL(request),
							"email_tobuyer_order_update_fee_notify",
							buyer.getEmail(), json, null);
					this.msgTools.sendSmsFree(CommUtil.getURL(request),
							"sms_tobuyer_order_fee_notify", buyer.getMobile(),
							json, null);
				}
			}
		}
		return "redirect:order?currentPage=" + currentPage;
	}
	
	/**
	 * 卖家确认发货
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param page_status
	 * @return
	 */
	@SecurityMapping(title = "卖家确认发货", value = "/order_shipping*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_shipping" })
	public ModelAndView order_shipping(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String page_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_order_shipping.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);

			List<Goods> list_goods = this.orderFormTools.queryOfGoods(obj);
			List<Goods> deliveryGoods = Lists.newArrayList();
			boolean physicalGoods = false;
			for (Goods g : list_goods) {
				if (g.getGoods_choice_type() == 1) {
					deliveryGoods.add(g);
				} else {
					physicalGoods = true;
				}
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("ecc_type", Integer.valueOf(0));
			params.put("ecc_store_id", store.getId());
			List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.queryPageList(params);
			
			params.clear();
			params.put("sa_type", Integer.valueOf(0));
			params.put("sa_user_id", user.getId());
			params.put("orderBy", "obj.sa_default desc,obj.sa_sequence");
			params.put("orderType", "asc");
			List<ShipAddress> shipAddrs = this.shipAddressService.queryPageList(params);
			
			mv.addObject("eccs", eccs);
			mv.addObject("shipAddrs", shipAddrs);
			mv.addObject("physicalGoods", Boolean.valueOf(physicalGoods));
			mv.addObject("deliveryGoods", deliveryGoods);
			mv.addObject("page_status", page_status);
			mv.addObject("orderFormTools", this.orderFormTools);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "订单参数错误！");
			mv.addObject("url", CommUtil.getURL(request) + "/order");
		}
		return mv;
	}
	
	/**
	 * 卖家确认发货保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param shipCode
	 * @param state_info
	 * @param order_seller_intro
	 * @param ecc_id
	 * @param sa_id
	 * @param page_status
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "卖家确认发货保存", value = "/order_shipping_save*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_shipping_save" })
	public String order_shipping_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info, String order_seller_intro,
			String ecc_id, String sa_id, String page_status) throws Exception {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User seller = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		seller = seller.getParent() == null ? seller : seller.getParent();
		if (seller.getStore().getId().equals(store.getId())) {
			this.handelOrderFormService.shippingOrder(request, obj, shipCode,
					state_info, ecc_id, sa_id);

			User buyer = this.userService.selectByPrimaryKey(CommUtil.null2Long(obj
					.getUser_id()));
			Map<String, Object> map = Maps.newHashMap();
			map.put("buyer_id", buyer.getId().toString());
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId());
			String json = JSON.toJSONString(map);
			if (obj.getOrder_form() == 0) {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_tobuyer_order_ship_notify", buyer.getEmail(),
						json, null, obj.getStore_id());
				this.msgTools.sendSmsCharge(CommUtil.getURL(request),
						"sms_tobuyer_order_ship_notify", buyer.getMobile(),
						json, null, obj.getStore_id());
			} else {
				this.msgTools.sendEmailFree(CommUtil.getURL(request),
						"email_tobuyer_order_ship_notify", buyer.getEmail(),
						json, null);
				this.msgTools.sendSmsFree(CommUtil.getURL(request),
						"sms_tobuyer_order_ship_notify", buyer.getMobile(),
						json, null);
			}
			this.msgTools.sendAppPush(CommUtil.getURL(request),
					"app_tobuyer_order_ship_notify", buyer.getId().toString(),
					json, null);
		}
		if (page_status.equals("order_ship")) {
			return "redirect:order_ship?currentPage=" + currentPage;
		}
		return "redirect:order?currentPage=" + currentPage;
	}
	
	/**
	 * 卖家修改物流
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家修改物流", value = "/order_shipping_code*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_shipping_code" })
	public ModelAndView order_shipping_code(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_order_shipping_code.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/order");
		}
		return mv;
	}
	
	/**
	 * 卖家修改物流保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param shipCode
	 * @param state_info
	 * @return
	 */
	@SecurityMapping(title = "卖家修改物流保存", value = "/order_shipping_code_save*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_shipping_code_save" })
	public String order_shipping_code_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String shipCode, String state_info) {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setShipCode(shipCode);
			this.orderFormService.updateById(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("修改物流信息");
			ofl.setState_info(state_info);
			ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
			ofl.setLog_user_name(SecurityUserHolder.getCurrentUser()
					.getUserName());
			ofl.setOf(obj);
			this.orderFormLogService.saveEntity(ofl);
			if (this.configService.getSysConfig().getKuaidi_type() == 1) {
				JSONObject info = new JSONObject();
				Map express_map = JSON.parseObject(obj.getExpress_info());
				info.put("company", CommUtil.null2String(express_map
						.get("express_company_mark")));
				info.put("number", obj.getShipCode());
				info.put("from", CommUtil.null2String(obj.getShip_addr()));
				info.put("to", obj.getReceiver_area());
				info.put("key", this.configService.getSysConfig()
						.getKuaidi_id2());
				JSONObject param_info = new JSONObject();
				param_info.put("callbackurl", CommUtil.getURL(request)
						+ "/kuaidi_callback?order_id=" + obj.getId()
						+ "&orderType=0");
				param_info.put("salt",
						Md5Encrypt.md5(CommUtil.null2String(obj.getId()))
								.substring(0, 16));
				info.put("parameters", param_info);
				try {
					String result = ShipTools.Post(
							"http://highapi.kuaidi.com/openapi-receive.html",
							info.toString());
					Map remap = JSON.parseObject(result);
					if ("success".equals(CommUtil.null2String(remap
							.get("message")))) {
						ExpressInfo ei = new ExpressInfo();
						ei.setAddTime(new Date());
						ei.setOrder_id(obj.getId());
						ei.setOrder_express_id(obj.getShipCode());
						ei.setOrder_type(0);
						Map ec_map = JSON.parseObject(CommUtil.null2String(obj
								.getExpress_info()));
						if (ec_map != null) {
							ei.setOrder_express_name(CommUtil
									.null2String(ec_map
											.get("express_company_name")));
						}
						this.expressInfoService.saveEntity(ei);
						System.out.println("订阅成功");
					} else {
						System.out.println("订阅失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return "redirect:order?currentPage=" + currentPage;
	}
	
	/**
	 * 打印订单
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "打印订单", value = "/order_print*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_print" })
	public ModelAndView order_print(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/order_print.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			OrderForm orderform = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			Store store = this.storeService.selectByPrimaryKey(CommUtil
					.null2Long(orderform.getStore_id()));
			mv.addObject("store", store);
			mv.addObject("obj", orderform);
			mv.addObject("orderFormTools", this.orderFormTools);
			BigDecimal coupon_price = getCouponPrice(orderform);
			mv.addObject("coupon_price", coupon_price);
		}
		return mv;
	}
	
	/**
	 * 卖家物流详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家物流详情", value = "/ship_view*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/ship_view" })
	public ModelAndView order_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/order_ship_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			TransInfo transInfo = this.ShipTools
					.query_Ordership_getData(CommUtil.null2String(obj.getId()));
			mv.addObject("transInfo", transInfo);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您店铺中没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/order");
		}
		return mv;
	}
	
	/**
	 * 卖家订单导出Excel
	 * @param request
	 * @param response
	 * @param order_status
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @param buyer_userName
	 */
	@SuppressWarnings("unused")
	@SecurityMapping(title = "卖家订单导出Excel", value = "/order_excel*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_excel" })
	public void order_excel(HttpServletRequest request,
			HttpServletResponse response, String order_status, String order_id,
			String beginTime, String endTime, String buyer_userName) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String,Object> maps= this.redPigQueryTools.getParams(null,1000000000, "addTime", "desc");
		maps.put("store_id", user.getStore().getId().toString());
		maps.put("order_cat_no", 2);
		
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {
				maps.put("order_status1", Integer.valueOf(10));
				maps.put("order_status2", Integer.valueOf(16));
			}
			if (order_status.equals("order_pay")) {
				maps.put("order_status", Integer.valueOf(20));
			}
			if (order_status.equals("order_shipping")) {
				maps.put("order_status", Integer.valueOf(30));
			}
			if (order_status.equals("order_evaluate")) {
				maps.put("order_status", Integer.valueOf(40));
			}
			if (order_status.equals("order_finish")) {
				maps.put("order_status", Integer.valueOf(50));
			}
			if (order_status.equals("order_cancel")) {
				maps.put("order_status", Integer.valueOf(0));
			}
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			maps.put("order_id_like", order_id);
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(ends,"yyyy-MM-dd hh:mm:ss"));
		}
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			maps.put("user_name_like", buyer_userName);
		}
		Calendar c = Calendar.getInstance();
		c.add(2, 0);
		c.set(5, 1);
		Calendar ca = Calendar.getInstance();
		ca.set(5, ca.getActualMaximum(5));
		
		IPageList pList = this.orderFormService.list(maps);
		
		if (pList.getResult() != null) {
			List<OrderForm> datas = pList.getResult();

			HSSFWorkbook wb = new HSSFWorkbook();

			HSSFSheet sheet = wb.createSheet("订单列表");
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			List<HSSFClientAnchor> anchor = Lists.newArrayList();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1,
						2 + i, (short) 1, 2 + i));
			}
			sheet.setColumnWidth(0, 6000);
			sheet.setColumnWidth(1, 4000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
			sheet.setColumnWidth(9, 6000);
			sheet.setColumnWidth(10, 6000);
			sheet.setColumnWidth(11, 8000);

			HSSFFont font = wb.createFont();
			font.setFontName("Verdana");
			font.setBoldweight((short) 100);
			font.setFontHeight((short) 300);
			font.setColor((short) 12);

			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment((short) 2);
			style.setVerticalAlignment((short) 1);
			style.setFillForegroundColor((short) 41);
			style.setFillPattern((short) 1);

			style.setBottomBorderColor((short) 10);
			style.setBorderBottom((short) 1);
			style.setBorderLeft((short) 1);
			style.setBorderRight((short) 1);
			style.setBorderTop((short) 1);
			style.setFont(font);

			HSSFRow row = sheet.createRow(0);
			row.setHeight((short) 500);

			HSSFCell cell = row.createCell(0);

			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));

			cell.setCellStyle(style);
			String title = "订单列表";
			Date time1 = CommUtil.formatDate(beginTime);
			Date time2 = CommUtil.formatDate(endTime);
			String time = CommUtil.null2String(CommUtil.formatShortDate(time1)
					+ " - " + CommUtil.formatShortDate(time2));
			cell.setCellValue(this.configService.getSysConfig().getTitle()
					+ title + "（" + time + "）");

			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true);
			style1.setAlignment((short) 2);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment((short) 2);
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue("订单号");
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("下单时间");
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("支付方式");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("订单类型");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("商品");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("物流单号");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("运费");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("商品总价");
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("订单总额");
			cell = row.createCell(9);
			cell.setCellStyle(style2);
			cell.setCellValue("订单状态");
			cell = row.createCell(10);
			cell.setCellStyle(style2);
			cell.setCellValue("发货时间");
			cell = row.createCell(11);
			cell.setCellStyle(style2);
			cell.setCellValue("活动信息");
			double all_order_price = 0.0D;
			double all_total_amount = 0.0D;
			for (int j = 2; j <= datas.size() + 1; j++) {
				row = sheet.createRow(j);

				int i = 0;
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue((datas.get(j - 2)).getOrder_id());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate((datas.get(j - 2)).getAddTime()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				Payment payment = this.paymentService.selectByPrimaryKey((datas.get(j - 2)).getPayment_id());
				if (payment != null) {
					cell.setCellValue((String) PAYMENT_MAP.get(payment.getMark()));
				} else {
					cell.setCellValue("未支付");
				}
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue((String) TYPE_MAP.get((datas.get(j - 2)).getOrder_type()));
				
				List<Map> goods_json = JSON.parseArray((datas.get(j - 2)).getGoods_info(),Map.class);
				StringBuilder sb = new StringBuilder();
				boolean whether_combin = false;
				for (Map map : goods_json) {
					sb.append(map.get("goods_name") + "*"
							+ map.get("goods_count") + ",");
					if (map.get("goods_type").toString().equals("combin")) {
						whether_combin = true;
					}
				}
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(sb.toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue((datas.get(j - 2)).getShipCode());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue((datas.get(j - 2))
						.getShip_price().toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue((datas.get(j - 2))
						.getGoods_amount().toString());
				all_total_amount = CommUtil.add(
						Double.valueOf(all_total_amount),
						(datas.get(j - 2)).getGoods_amount());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String((datas
						.get(j - 2)).getTotalPrice()));
				all_order_price = CommUtil.add(Double.valueOf(all_order_price),
						(datas.get(j - 2)).getTotalPrice());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue((String) STATUS_MAP.get(Integer
						.valueOf((datas.get(j - 2))
								.getOrder_status())));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate((datas.get(j - 2)).getShipTime()));
				if (CommUtil.null2Int((datas.get(j - 2)).getWhether_gift()) == 1) {
					List<Map> gifts_json = JSON.parseArray(
							(datas.get(j - 2)).getGift_infos(),
							Map.class);
					StringBuilder gsb = new StringBuilder();
					for (Map map : gifts_json) {
						gsb.append(map.get("goods_name") + ",");
					}
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue(gsb.toString());
				}
				if (((datas.get(j - 2)).getEnough_reduce_amount() != null)
						&& ((datas.get(j - 2))
								.getEnough_reduce_amount().compareTo(
										WHETHER_ENOUGH) == 1)) {
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue("满减");
				}
				if (whether_combin) {
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue("组合销售");
				}
			}
			int m = datas.size() + 2;
			row = sheet.createRow(m);

			int i = 0;
			cell = row.createCell(i);
			cell.setCellStyle(style2);
			cell.setCellValue("总计");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次订单金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_order_price);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次商品总金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_total_amount);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String excel_name = sdf.format(new Date());
			try {
				String path = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator + "excel";
				response.setContentType("application/x-download");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + excel_name + ".xls");
				OutputStream os = response.getOutputStream();
				wb.write(os);
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 延长收货时间
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "延长收货时间", value = "/order_comfirm_delay*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_comfirm_delay" })
	public ModelAndView order_comfirm_delay(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/order_comfirm_delay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/order");
		}
		return mv;
	}
	
	/**
	 * 延长收货时间保存
	 * @param request
	 * @param response
	 * @param id
	 * @param delay_time
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "延长收货时间保存", value = "/order_confirm_delay_save*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_confirm_delay_save" })
	public String order_confirm_delay_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String delay_time,
			String currentPage) throws Exception {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
				.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getId().equals(store.getId())) {
			obj.setOrder_confirm_delay(CommUtil.null2Int(obj.getOrder_confirm_delay())
					+ CommUtil.null2Int(delay_time));
			this.orderFormService.updateById(obj);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("延长收货时间");
			ofl.setState_info("延长收货时间：" + delay_time + "天");
			ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
			ofl.setLog_user_name(SecurityUserHolder.getCurrentUser()
					.getUserName());
			ofl.setOf(obj);
			this.orderFormLogService.saveEntity(ofl);
		}
		return "redirect:order_confirm?currentPage=" + currentPage;
	}
	
	/**
	 * 打印快递运单
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "打印快递运单", value = "/order_ship_print*", rtype = "seller", rname = "订单管理", rcode = "order_seller", rgroup = "交易管理")
	@RequestMapping({ "/order_ship_print" })
	public ModelAndView order_ship_print(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/order_ship_print.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (order.getStore_id().equals(CommUtil.null2String(store.getId()))) {
			Map ec_map = JSON.parseObject(order.getExpress_info());
			ExpressCompanyCommon ecc = this.expressCompanyCommonService
					.selectByPrimaryKey(CommUtil.null2Long(ec_map
							.get("express_company_id")));
			if (ecc != null) {
				Map offset_map = JSON.parseObject(ecc.getEcc_template_offset());
				ShipAddress ship_addr = this.shipAddressService
						.selectByPrimaryKey(order.getShip_addr_id());
				mv.addObject("ecc", this.expressCompanyCommonService
						.selectByPrimaryKey(CommUtil.null2Long(ec_map
								.get("express_company_id"))));
				mv.addObject("offset_map", offset_map);
				mv.addObject("obj", order);
				mv.addObject("ship_addr", ship_addr);
				mv.addObject("area",
						this.areaService.selectByPrimaryKey(ship_addr.getSa_area_id()));
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "老版物流订单，无法打印！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/ecc_list");
			}
		}
		return mv;
	}
	
	public BigDecimal getCouponPrice(OrderForm order) {
		BigDecimal price = BigDecimal.valueOf(0.0D);
		if ((order != null) && (order.getCoupon_info() != null)
				&& (!"".equals(order.getCoupon_info()))) {
			Map<String, Object> map = JSON.parseObject(order.getCoupon_info());
			if (map.size() > 0) {
				price = price.add(new BigDecimal(CommUtil.null2String(map
						.get("coupon_amount"))));
			}
		}
		return price;
	}
}
