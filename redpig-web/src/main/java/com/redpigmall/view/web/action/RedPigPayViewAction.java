package com.redpigmall.view.web.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.qrcode.QRCodeUtil;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.domain.CloudPurchaseOrder;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.GoldRecord;
import com.redpigmall.domain.IntegralGoods;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.Predeposit;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.User;
import com.redpigmall.pay.alipay.config.AlipayConfig;
import com.redpigmall.pay.alipay.util.AlipayNotify;
import com.redpigmall.pay.bill.util.MD5Util;
import com.redpigmall.pay.tenpay.RequestHandler;
import com.redpigmall.pay.tenpay.ResponseHandler;
import com.redpigmall.pay.tenpay.util.Sha1Util;
import com.redpigmall.pay.tenpay.util.TenpayUtil;
import com.redpigmall.pay.unionpay.acp.sdk.SDKUtil;
import com.redpigmall.view.web.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigPayViewAction.java
 * </p>
 * 
 * <p>
 * Description:在线支付回调控制器,处理系统支持的所有支付方式回调业务处理，包括支付宝、财付通、快钱、paypal、网银在线
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
 * @date 2014-5-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigPayViewAction extends BaseAction{
	
	/**
	 * 支付宝在线支付成功回调控制
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/aplipay_return" })
	public ModelAndView aplipay_return(HttpServletRequest request,HttpServletResponse response) throws Exception {
		System.out.println("pc_alipay_return");
		ModelAndView mv = new RedPigJModelAndView("order_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String trade_no = request.getParameter("trade_no");
		String[] order_nos = request.getParameter("out_trade_no").split("-");
		String total_fee = request.getParameter("price");
		String subject = request.getParameter("subject");
		
		String order_no = order_nos[2];
		String type = CommUtil.null2String(request.getParameter("body")).trim();
		String trade_status = request.getParameter("trade_status");
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		
		if (type.equals("goods")) {
			main_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("cash")) {
			obj = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("group")) {
			main_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		
		Map<String, String> params = Maps.newHashMap();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr =

				valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		AlipayConfig config = new AlipayConfig();
		Payment payment = null;
		if ((type.equals("goods")) || (type.equals("group"))) {
			payment = this.paymentService
					.selectByPrimaryKey(main_order.getPayment_id());
			config.setKey(payment.getSafeKey());
			config.setPartner(payment.getPartner());
			config.setSeller_email(payment.getSeller_email());
		}
		if ((type.equals("cash")) || (type.equals("gold"))
				|| (type.equals("integral")) || (type.equals("cloudpurchase"))) {
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			if (type.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (type.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (type.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			if (type.equals("cloudpurchase")) {
				q_params.put("mark", cp_order.getPayment());
			}
			
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			config.setKey(((Payment) payments.get(0)).getSafeKey());
			config.setPartner(((Payment) payments.get(0)).getPartner());
			config.setSeller_email(((Payment) payments.get(0))
					.getSeller_email());
		}
		config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify");
		config.setReturn_url(CommUtil.getURL(request) + "/aplipay_return");
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result) {
			if (((type.equals("goods")) || (type.equals("group")))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))) {
				boolean flag = this.handelOrderFormService.payByOnline(
						main_order, trade_no, CommUtil.getURL(request));
				if (flag) {
					this.orderFormTools.updateGoodsInventory(main_order);
				}
				this.orderFormTools.sendMsgWhenHandleOrder(
						CommUtil.getURL(request), main_order,
						"tobuyer_online_pay_ok_notify",
						"toseller_online_pay_ok_notify");
				mv.addObject("all_price", Double.valueOf(this.orderFormTools
						.query_order_pay_price(CommUtil.null2String(main_order
								.getId()))));
				mv.addObject("obj", main_order);
			}
			if ((type.equals("cash"))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))) {
				if (obj.getPd_pay_status() != 2) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.updateById(obj);
					User user = this.userService.selectByPrimaryKey(obj.getPd_user()
							.getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							user.getAvailableBalance(), obj.getPd_amount())));
					this.userService.updateById(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("支付宝在线支付");
					this.predepositLogService.saveEntity(log);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "恭喜您，成功充值" + obj.getPd_amount() + "元");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list");
			}
			if ((type.equals("gold"))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))) {
				if (gold.getGold_pay_status() != 2) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.updateById(gold);
					User user = this.userService.selectByPrimaryKey(gold.getGold_user()
							.getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.updateById(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("支付宝在线支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.saveEntity(log);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/gold_record_list");
			}
			if ((type.equals("integral"))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))) {
				if (ig_order.getIgo_status() < 20) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("alipay");
					this.integralGoodsOrderService.updateById(ig_order);
					List<Map> ig_maps = this.orderFormTools
							.query_integral_goodsinfo(ig_order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService
								.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
						goods.setIg_goods_count(goods.getIg_goods_count()
								- CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.updateById(goods);
					}
				}
				mv = new RedPigJModelAndView("integral_order_finish.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ig_order);
			}
			if (type.equals("cloudpurchase")) {
				if (cp_order.getStatus() < 5) {
					cp_order.setStatus(5);
					cp_order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(cp_order);
					this.cloudPurchaseOrderService.reduce_inventory(cp_order,request);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "预付款支付成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/cloudbuy_order");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付回调失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 支付宝异步通知
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping({ "/alipay_notify" })
	public void alipay_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String trade_no = request.getParameter("trade_no");
		String[] order_nos = request.getParameter("out_trade_no").split("-");
		String total_fee = request.getParameter("price");
		String subject = request.getParameter("subject");

		String order_no = order_nos[2];
		String type = CommUtil.null2String(request.getParameter("body")).trim();
		String trade_status = request.getParameter("trade_status");
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if ((type.equals("goods")) || (type.equals("group"))) {
			main_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("cash")) {
			obj = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil.null2Long(order_no));
		}
		Map<String, String> params = Maps.newHashMap();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr =

				valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		AlipayConfig config = new AlipayConfig();
		Payment payment = null;
		if ((type.equals("goods")) || (type.equals("group"))) {
			payment = this.paymentService.selectByPrimaryKey(main_order.getPayment_id());
			config.setKey(payment.getSafeKey());
			config.setPartner(payment.getPartner());
			config.setSeller_email(payment.getSeller_email());
		}
		if ((type.equals("cash")) || (type.equals("gold"))
				|| (type.equals("integral")) || (type.equals("cloudpurchase"))) {
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			if (type.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (type.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (type.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			if (type.equals("cloudpurchase")) {
				q_params.put("mark", cp_order.getPayment());
			}
			
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			config.setKey(((Payment) payments.get(0)).getSafeKey());
			config.setPartner(((Payment) payments.get(0)).getPartner());
			config.setSeller_email(((Payment) payments.get(0)).getSeller_email());
		}
		config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify");
		config.setReturn_url(CommUtil.getURL(request) + "/aplipay_return");
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result) {
			if (((type.equals("goods")) || (type.equals("group")))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))) {
				boolean flag = this.handelOrderFormService.payByOnline(
						main_order, trade_no, CommUtil.getURL(request));
				if (flag) {
					this.orderFormTools.updateGoodsInventory(main_order);
				}
				this.orderFormTools.sendMsgWhenHandleOrder(
						CommUtil.getURL(request), main_order,
						"tobuyer_online_pay_ok_notify",
						"toseller_online_pay_ok_notify");
			}
			if ((type.equals("cash"))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))
					&& (obj.getPd_pay_status() < 2)) {
				obj.setPd_status(1);
				obj.setPd_pay_status(2);
				this.predepositService.updateById(obj);
				User user = this.userService.selectByPrimaryKey(obj.getPd_user()
						.getId());
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						user.getAvailableBalance(), obj.getPd_amount())));
				this.userService.updateById(user);
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_amount(obj.getPd_amount());
				log.setPd_log_user(obj.getPd_user());
				log.setPd_op_type("充值");
				log.setPd_type("可用预存款");
				log.setPd_log_info("支付宝在线支付");
				this.predepositLogService.saveEntity(log);
			}
			if ((type.equals("gold"))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))
					&& (gold.getGold_pay_status() < 2)) {
				gold.setGold_status(1);
				gold.setGold_pay_status(2);
				this.goldRecordService.updateById(gold);
				User user = this.userService.selectByPrimaryKey(gold.getGold_user()
						.getId());
				user.setGold(user.getGold() + gold.getGold_count());
				this.userService.updateById(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_payment(gold.getGold_payment());
				log.setGl_content("支付宝在线支付");
				log.setGl_money(gold.getGold_money());
				log.setGl_count(gold.getGold_count());
				log.setGl_type(0);
				log.setGl_user(gold.getGold_user());
				log.setGr(gold);
				this.goldLogService.saveEntity(log);
			}
			if ((type.equals("integral"))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))
					&& (ig_order.getIgo_status() < 20)) {
				ig_order.setIgo_status(20);
				ig_order.setIgo_pay_time(new Date());
				ig_order.setIgo_payment("alipay");
				this.integralGoodsOrderService.updateById(ig_order);
				List<Map> ig_maps = this.orderFormTools
						.query_integral_goodsinfo(ig_order.getGoods_info());
				for (Map map : ig_maps) {
					IntegralGoods goods = this.integralGoodsService
							.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
					goods.setIg_goods_count(goods.getIg_goods_count()
							- CommUtil.null2Int(map.get("ig_goods_count")));
					goods.setIg_exchange_count(goods.getIg_exchange_count()
							+ CommUtil.null2Int(map.get("ig_goods_count")));
					this.integralGoodsService.updateById(goods);
				}
			}
			if ((type.equals("cloudpurchase")) && (cp_order.getStatus() < 5)) {
				cp_order.setStatus(5);
				cp_order.setPayTime(new Date());
				this.cloudPurchaseOrderService.updateById(cp_order);
				this.cloudPurchaseOrderService.reduce_inventory(cp_order,
						request);
			}
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = response.getWriter();
				writer.print("success");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = response.getWriter();
				writer.print("fail");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 快钱在线支付回调处理控制
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/bill_return" })
	public ModelAndView bill_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("order_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String ext1 = request.getParameter("ext1").trim();
		String ext2 = CommUtil.null2String(request.getParameter("ext2").trim());
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if ((ext2.equals("goods")) || (ext2.equals("group"))) {
			order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("cash")) {
			obj = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
					.null2Long(ext1));
		}
		if (ext2.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil.null2Long(ext1));
		}
		String merchantAcctId = request.getParameter("merchantAcctId").trim();

		String key = "";
		Payment payment = null;
		if ((ext2.equals("goods")) || (ext2.equals("group"))) {
			payment = this.paymentService.selectByPrimaryKey(order.getPayment_id());
			key = payment.getRmbKey();
		}
		if ((ext2.equals("cash")) || (ext2.equals("gold"))
				|| (ext2.equals("integral")) || (ext2.equals("cloudpurchase"))) {
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			if (ext2.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (ext2.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (ext2.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			if (ext2.equals("cloudpurchase")) {
				q_params.put("mark", cp_order.getPayment());
			}
			
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			key = ((Payment) payments.get(0)).getRmbKey();
		}
		String version = request.getParameter("version").trim();

		String language = request.getParameter("language").trim();

		String signType = request.getParameter("signType").trim();

		String payType = request.getParameter("payType").trim();

		String bankId = request.getParameter("bankId").trim();

		String orderId = request.getParameter("orderId").trim();

		String orderTime = request.getParameter("orderTime").trim();

		String orderAmount = request.getParameter("orderAmount").trim();

		String dealId = request.getParameter("dealId").trim();

		String bankDealId = request.getParameter("bankDealId").trim();

		String dealTime = request.getParameter("dealTime").trim();

		String payAmount = request.getParameter("payAmount").trim();

		String fee = request.getParameter("fee").trim();

		String payResult = request.getParameter("payResult").trim();

		String errCode = request.getParameter("errCode").trim();

		String signMsg = request.getParameter("signMsg").trim();

		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId",
				merchantAcctId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "language",
				language);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType",
				signType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime",
				orderTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount",
				orderAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId",
				bankDealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime",
				dealTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount",
				payAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult",
				payResult);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);

		String merchantSignMsg = MD5Util.md5Hex(merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
		if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())) {
			switch (Integer.parseInt(payResult)) {
			case 10:
				if ((ext2.equals("goods")) || (ext2.equals("group"))) {
					boolean flag = this.handelOrderFormService.payByOnline(
							order, "", CommUtil.getURL(request));
					if (flag) {
						this.orderFormTools.updateGoodsInventory(order);
					}
					this.orderFormTools.sendMsgWhenHandleOrder(
							CommUtil.getURL(request), order,
							"tobuyer_online_pay_ok_notify",
							"toseller_online_pay_ok_notify");
					mv.addObject("all_price", Double
							.valueOf(this.orderFormTools
									.query_order_pay_price(CommUtil
											.null2String(order.getId()))));
					mv.addObject("obj", order);
				}
				if (ext2.equals("cash")) {
					if (obj.getPd_pay_status() < 2) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.updateById(obj);
						User user = this.userService.selectByPrimaryKey(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.updateById(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("快钱在线支付");
						this.predepositLogService.saveEntity(log);
					}
					mv = new RedPigJModelAndView("success.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list");
				}
				if (ext2.equals("gold")) {
					if (gold.getGold_pay_status() < 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.updateById(gold);
						User user = this.userService.selectByPrimaryKey(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.updateById(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("快钱在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.saveEntity(log);
					}
					mv = new RedPigJModelAndView("success.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换" + gold.getGold_count()
							+ "金币成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list");
				}
				if (ext2.equals("integral")) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.updateById(ig_order);
						List<Map> ig_maps = this.orderFormTools
								.query_integral_goodsinfo(ig_order
										.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("id")));
							goods.setIg_goods_count(goods.getIg_goods_count()
									- CommUtil.null2Int(map
											.get("ig_goods_count")));
							goods.setIg_exchange_count(goods
									.getIg_exchange_count()
									+ CommUtil.null2Int(map
											.get("ig_goods_count")));
							this.integralGoodsService.updateById(goods);
						}
					}
					mv = new RedPigJModelAndView("integral_order_finish.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
				if (!ext2.equals("cloudpurchase")) {
					break;
				}
				if (cp_order.getStatus() < 5) {
					cp_order.setStatus(5);
					cp_order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(cp_order);
					this.cloudPurchaseOrderService.reduce_inventory(cp_order,
							request);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "预付款支付成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/cloudbuy_order");

				break;
			default:
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "快钱支付失败！");
				mv.addObject("url", CommUtil.getURL(request) + "/index");

				break;
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "快钱支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}

	public String appendParam(String returnStr, String paramId,
			String paramValue) {
		if (!returnStr.equals("")) {
			if (!paramValue.equals("")) {
				returnStr = returnStr + "&" + paramId + "=" + paramValue;
			}
		} else if (!paramValue.equals("")) {
			returnStr = paramId + "=" + paramValue;
		}
		return returnStr;
	}
	
	/**
	 * 快钱异步回调处理，如果同步回调出错，异步回调会弥补该功能
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping({ "/bill_notify_return" })
	public void bill_notify_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int rtnOK = 0;
		
		String ext1 = request.getParameter("ext1").trim();
		String ext2 = CommUtil.null2String(request.getParameter("ext2").trim());
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if ((ext2.equals("goods")) || (ext2.equals("group"))) {
			order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("cash")) {
			obj = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil.null2Long(ext1));
		}
		if (ext2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
					.null2Long(ext1));
		}
		if (ext2.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil
					.null2Long(ext1));
		}
		String merchantAcctId = request.getParameter("merchantAcctId").trim();

		String key = "";
		Payment payment = null;
		if ((ext2.equals("goods")) || (ext2.equals("group"))) {
			payment = this.paymentService.selectByPrimaryKey(order.getPayment_id());
			key = payment.getRmbKey();
		}
		if ((ext2.equals("cash")) || (ext2.equals("gold"))
				|| (ext2.equals("integral")) || (ext2.equals("cloudpurchase"))) {
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			if (ext2.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (ext2.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (ext2.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			if (ext2.equals("cloudpurchase")) {
				q_params.put("mark", cp_order.getPayment());
			}
			
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			key = ((Payment) payments.get(0)).getRmbKey();
		}
		String version = request.getParameter("version").trim();

		String language = request.getParameter("language").trim();

		String signType = request.getParameter("signType").trim();

		String payType = request.getParameter("payType").trim();

		String bankId = request.getParameter("bankId").trim();

		String orderId = request.getParameter("orderId").trim();

		String orderTime = request.getParameter("orderTime").trim();

		String orderAmount = request.getParameter("orderAmount").trim();

		String dealId = request.getParameter("dealId").trim();

		String bankDealId = request.getParameter("bankDealId").trim();

		String dealTime = request.getParameter("dealTime").trim();

		String payAmount = request.getParameter("payAmount").trim();

		String fee = request.getParameter("fee").trim();

		String payResult = request.getParameter("payResult").trim();

		String errCode = request.getParameter("errCode").trim();

		String signMsg = request.getParameter("signMsg").trim();

		String merchantSignMsgVal = "";
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId",
				merchantAcctId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "language",
				language);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType",
				signType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime",
				orderTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount",
				orderAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId",
				bankDealId);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime",
				dealTime);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount",
				payAmount);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult",
				payResult);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
		merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);

		String merchantSignMsg = MD5Util.md5Hex(
				merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
		if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())) {
			switch (Integer.parseInt(payResult)) {
			case 10:
				if ((ext2.equals("goods")) || (ext2.equals("group"))) {
					boolean flag = this.handelOrderFormService.payByOnline(
							order, dealId, CommUtil.getURL(request));
					if (flag) {
						this.orderFormTools.updateGoodsInventory(order);
					}
					this.orderFormTools.sendMsgWhenHandleOrder(
							CommUtil.getURL(request), order,
							"tobuyer_online_pay_ok_notify",
							"toseller_online_pay_ok_notify");
					rtnOK = 1;
				}
				if (ext2.equals("cash")) {
					if (obj.getPd_pay_status() < 2) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.updateById(obj);
						User user = this.userService.selectByPrimaryKey(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.updateById(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("快钱在线支付");
						this.predepositLogService.saveEntity(log);
					}
					rtnOK = 1;
				}
				if (ext2.equals("gold")) {
					if (gold.getGold_pay_status() < 2) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.updateById(gold);
						User user = this.userService.selectByPrimaryKey(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.updateById(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("快钱在线支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.saveEntity(log);
					}
					rtnOK = 1;
				}
				if (ext2.equals("integral")) {
					if (ig_order.getIgo_status() < 20) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.updateById(ig_order);
						List<Map> ig_maps = this.orderFormTools
								.query_integral_goodsinfo(ig_order
										.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("id")));
							goods.setIg_goods_count(goods.getIg_goods_count()
									- CommUtil.null2Int(map
											.get("ig_goods_count")));
							goods.setIg_exchange_count(goods
									.getIg_exchange_count()
									+ CommUtil.null2Int(map
											.get("ig_goods_count")));
							this.integralGoodsService.updateById(goods);
						}
					}
					rtnOK = 1;
				}
				if (ext2.equals("cloudpurchase")) {
					if (cp_order.getStatus() < 5) {
						cp_order.setStatus(5);
						cp_order.setPayTime(new Date());
						this.cloudPurchaseOrderService.updateById(cp_order);
						this.cloudPurchaseOrderService.reduce_inventory(
								cp_order, request);
					}
					rtnOK = 1;
				}
				break;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(rtnOK);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 财付通支付
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @param payment_id
	 * @throws IOException
	 */
	@RequestMapping({ "/tenpay" })
	public void tenpay(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String payment_id) throws IOException {
		boolean submit = true;
		OrderForm of = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if ((type.equals("goods")) || (type.equals("group"))) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cash")) {
			obj = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (obj.getPd_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil
					.null2Long(id));
		}
		if (submit) {
			String order_price = "";
			if ((type.equals("goods")) || (type.equals("gruop"))) {
				order_price = CommUtil.null2String(of.getTotalPrice());
			}
			if (type.equals("cash")) {
				order_price = CommUtil.null2String(obj.getPd_amount());
			}
			if (type.equals("gold")) {
				order_price = CommUtil.null2String(Integer.valueOf(gold
						.getGold_money()));
			}
			if (type.equals("integral")) {
				order_price = CommUtil.null2String(ig_order.getIgo_trans_fee());
			}
			if (type.equals("cloudpurchase")) {
				order_price = CommUtil.null2String(Integer.valueOf(cp_order
						.getPrice()));
			}
			double total_fee = CommUtil.null2Double(order_price) * 100.0D;
			int fee = (int) total_fee;

			String product_name = "";
			if ((type.equals("goods")) || (type.equals("group"))) {
				product_name = of.getOrder_id();
			}
			if (type.equals("cash")) {
				product_name = obj.getPd_sn();
			}
			if (type.equals("gold")) {
				product_name = gold.getGold_sn();
			}
			if (type.equals("integral")) {
				product_name = ig_order.getIgo_order_sn();
			}
			if (type.equals("cloudpurchase")) {
				product_name = cp_order.getOdrdersn();
			}
			String remarkexplain = "";
			if ((type.equals("goods")) || (type.equals("group"))) {
				remarkexplain = of.getMsg();
			}
			if (type.equals("cash")) {
				remarkexplain = obj.getPd_remittance_info();
			}
			if (type.equals("gold")) {
				remarkexplain = gold.getGold_exchange_info();
			}
			if (type.equals("integral")) {
				remarkexplain = ig_order.getIgo_msg();
			}
			if (type.equals("cloudpurchase")) {
				product_name = cp_order.getIgo_order_sn();
			}
			String attach = "";
			if ((type.equals("goods")) || (type.equals("group"))) {
				attach = type + "," + of.getId().toString();
			}
			if (type.equals("cash")) {
				attach = type + "," + obj.getId().toString();
			}
			if (type.equals("gold")) {
				attach = type + "," + gold.getId().toString();
			}
			if (type.equals("integral")) {
				attach = type + "," + ig_order.getId().toString();
			}
			if (type.equals("cloudpurchase")) {
				attach = type + "," + cp_order.getId().toString();
			}
			String desc = "商品：" + product_name;

			String out_trade_no = "";
			if ((type.equals("goods")) || (type.equals("group"))) {
				out_trade_no = of.getOrder_id();
			}
			if (type.endsWith("cash")) {
				out_trade_no = obj.getPd_sn();
			}
			if (type.endsWith("gold")) {
				out_trade_no = gold.getGold_sn();
			}
			if (type.equals("integral")) {
				out_trade_no = ig_order.getIgo_order_sn();
			}
			if (type.equals("cloudpurchase")) {
				out_trade_no = cp_order.getOdrdersn();
			}
			Payment payment = this.paymentService.selectByPrimaryKey(CommUtil
					.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			String trade_mode = CommUtil.null2String(Integer.valueOf(payment
					.getTrade_mode()));
			String currTime = TenpayUtil.getCurrTime();

			RequestHandler reqHandler = new RequestHandler(request, response);
			reqHandler.init();

			reqHandler.setKey(payment.getTenpay_key());

			reqHandler.setGateUrl("https://gw.tenpay.com/gateway/pay");

			reqHandler.setParameter("partner", payment.getTenpay_partner());
			reqHandler.setParameter("out_trade_no", out_trade_no);
			reqHandler.setParameter("total_fee", String.valueOf(fee));
			reqHandler.setParameter("return_url", CommUtil.getURL(request)
					+ "/tenpay_return");
			reqHandler.setParameter("notify_url", CommUtil.getURL(request)
					+ "/tenpay_notify");
			reqHandler.setParameter("body", desc);
			reqHandler.setParameter("bank_type", "DEFAULT");
			reqHandler
					.setParameter("spbill_create_ip", request.getRemoteAddr());
			reqHandler.setParameter("fee_type", "1");
			reqHandler.setParameter("subject", desc);

			reqHandler.setParameter("sign_type", "MD5");
			reqHandler.setParameter("service_version", "1.0");
			reqHandler.setParameter("input_charset", "UTF-8");
			reqHandler.setParameter("sign_key_index", "1");

			reqHandler.setParameter("attach", attach);
			reqHandler.setParameter("product_fee", "");

			reqHandler.setParameter("transport_fee", "0");
			reqHandler.setParameter("time_start", currTime);
			reqHandler.setParameter("time_expire", "");
			reqHandler.setParameter("buyer_id", "");
			reqHandler.setParameter("goods_tag", "");
			reqHandler.setParameter("trade_mode", trade_mode);
			reqHandler.setParameter("transport_desc", "");
			reqHandler.setParameter("trans_type", "1");
			reqHandler.setParameter("agentid", "");
			reqHandler.setParameter("agent_type", "");
			reqHandler.setParameter("seller_id", "");

			String requestUrl = reqHandler.getRequestURL();
			response.sendRedirect(requestUrl);
		} else {
			response.getWriter().write("该订单已经完成支付！");
		}
	}
	
	/**
	 *  财付通在线支付回调控制
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/tenpay_return" })
	public ModelAndView tenpay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("order_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ResponseHandler resHandler = new ResponseHandler(request, response);
		String[] attachs = request.getParameter("attach").split(",");

		String out_trade_no = resHandler.getParameter("out_trade_no");
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if (attachs[0].equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil
					.null2Long(attachs[1]));
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			q_params.put("mark", cp_order.getPayment());
			
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			resHandler.setKey(((Payment) payments.get(0)).getTenpay_key());
		}
		if (attachs[0].equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
					.null2Long(attachs[1]));
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			q_params.put("mark", ig_order.getIgo_payment());
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			resHandler.setKey(((Payment) payments.get(0)).getTenpay_key());
		}
		if (attachs[0].equals("cash")) {
			obj = this.predepositService.selectByPrimaryKey(CommUtil
					.null2Long(attachs[1]));
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			q_params.put("mark", obj.getPd_payment());
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			resHandler.setKey(((Payment) payments.get(0)).getTenpay_key());
		}
		if (attachs[0].equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(attachs[1]));
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			q_params.put("mark", gold.getGold_payment());
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			resHandler.setKey(((Payment) payments.get(0)).getTenpay_key());
		}
		Payment payment = null;
		if ((attachs[0].equals("goods")) || (attachs[0].equals("group"))) {
			order = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(attachs[1]));
			payment = this.paymentService.selectByPrimaryKey(order.getPayment_id());
			resHandler.setKey(payment.getTenpay_key());
		}
		if (resHandler.isTenpaySign()) {
			String notify_id = resHandler.getParameter("notify_id");

			String transaction_id = resHandler.getParameter("transaction_id");

			String total_fee = resHandler.getParameter("total_fee");

			String discount = resHandler.getParameter("discount");

			String trade_state = resHandler.getParameter("trade_state");

			String trade_mode = resHandler.getParameter("trade_mode");
			if ("1".equals(trade_mode)) {
				if ("0".equals(trade_state)) {
					if (attachs[0].equals("cash")) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.updateById(obj);
						User user = this.userService.selectByPrimaryKey(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.updateById(user);
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "充值" + obj.getPd_amount()
								+ "成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/predeposit_list");
					}
					if ((attachs[0].equals("goods"))
							|| (attachs[0].equals("group"))) {
						boolean flag = this.handelOrderFormService
								.payByOnline(order, transaction_id,
										CommUtil.getURL(request));
						if (flag) {
							this.orderFormTools.updateGoodsInventory(order);
						}
						this.orderFormTools.sendMsgWhenHandleOrder(
								CommUtil.getURL(request), order,
								"tobuyer_online_pay_ok_notify",
								"toseller_online_pay_ok_notify");
						mv.addObject("all_price", Double
								.valueOf(this.orderFormTools
										.query_order_pay_price(CommUtil
												.null2String(order.getId()))));
						mv.addObject("obj", order);
					}
					if (attachs[0].equals("gold")) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.updateById(gold);
						User user = this.userService.selectByPrimaryKey(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.updateById(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("财付通及时到账支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.saveEntity(log);
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "兑换" + gold.getGold_count()
								+ "金币成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/seller/gold_record_list");
					}
					if (attachs[0].equals("integral")) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.updateById(ig_order);
						List<Map> ig_maps = this.orderFormTools
								.query_integral_goodsinfo(ig_order
										.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("id")));
							goods.setIg_goods_count(goods.getIg_goods_count()
									- CommUtil.null2Int(map
											.get("ig_goods_count")));
							goods.setIg_exchange_count(goods
									.getIg_exchange_count()
									+ CommUtil.null2Int(map
											.get("ig_goods_count")));
							this.integralGoodsService.updateById(goods);
						}
						mv = new RedPigJModelAndView("integral_order_finish.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("obj", ig_order);
					}
					if (attachs[0].equals("cloudpurchase")) {
						if (cp_order.getStatus() < 5) {
							cp_order.setStatus(5);
							cp_order.setPayTime(new Date());
							this.cloudPurchaseOrderService.updateById(cp_order);
							this.cloudPurchaseOrderService.reduce_inventory(
									cp_order, request);
						}
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "预付款支付成功！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/cloudbuy_order");
					}
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "财付通支付失败！");
					mv.addObject("url", CommUtil.getURL(request) + "/index");
				}
			} else if ("2".equals(trade_mode)) {
				if ("0".equals(trade_state)) {
					if (attachs[0].equals("cash")) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.updateById(obj);
						User user = this.userService.selectByPrimaryKey(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.updateById(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("财付通中介担保付款");
						this.predepositLogService.saveEntity(log);
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "充值" + obj.getPd_amount()
								+ "成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/predeposit_list");
					}
					if (attachs[0].equals("goods")) {
						boolean flag = this.handelOrderFormService
								.payByOnline(order, transaction_id,
										CommUtil.getURL(request));
						if (flag) {
							this.orderFormTools.updateGoodsInventory(order);
						}
						this.orderFormTools.sendMsgWhenHandleOrder(
								CommUtil.getURL(request), order,
								"tobuyer_online_pay_ok_notify",
								"toseller_online_pay_ok_notify");
						mv.addObject("all_price", Double
								.valueOf(this.orderFormTools
										.query_order_pay_price(CommUtil
												.null2String(order.getId()))));
						mv.addObject("obj", order);
					}
					if (attachs[0].equals("gold")) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.updateById(gold);
						User user = this.userService.selectByPrimaryKey(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.updateById(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("财付通中介担保付款成功");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.saveEntity(log);
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "兑换" + gold.getGold_count()
								+ "金币成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/seller/gold_record_list");
					}
					if (attachs[0].equals("integral")) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("bill");
						this.integralGoodsOrderService.updateById(ig_order);
						List<Map> ig_maps = this.orderFormTools
								.query_integral_goodsinfo(ig_order
										.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("id")));
							goods.setIg_goods_count(goods.getIg_goods_count()
									- CommUtil.null2Int(map
											.get("ig_goods_count")));
							goods.setIg_exchange_count(goods
									.getIg_exchange_count()
									+ CommUtil.null2Int(map
											.get("ig_goods_count")));
							this.integralGoodsService.updateById(goods);
						}
						mv = new RedPigJModelAndView("integral_order_finish.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("obj", ig_order);
					}
					if (attachs[0].equals("cloudpurchase")) {
						if (cp_order.getStatus() < 5) {
							cp_order.setStatus(5);
							cp_order.setPayTime(new Date());
							this.cloudPurchaseOrderService.updateById(cp_order);
							this.cloudPurchaseOrderService.reduce_inventory(
									cp_order, request);
						}
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "预付款支付成功！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/cloudbuy_order");
					}
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "财付通支付失败！");
					mv.addObject("url", CommUtil.getURL(request) + "/index");
				}
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "财付通认证签名失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 网银在线回调函数
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/chinabank_return" })
	public ModelAndView chinabank_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("order_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String remark1 = request.getParameter("remark1");
		String remark2 = CommUtil.null2String(request.getParameter("remark2"));
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if ((remark2.equals("goods")) || (remark2.equals("group"))) {
			order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(remark1
					.trim()));
		}
		if (remark2.equals("cash")) {
			obj = this.predepositService
					.selectByPrimaryKey(CommUtil.null2Long(remark1));
		}
		if (remark2.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(remark1));
		}
		if (remark2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
					.null2Long(remark1));
		}
		if (remark2.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil
					.null2Long(remark1));
		}
		String key = "";
		Payment payment = null;
		if ((remark2.equals("goods")) || (remark2.equals("group"))) {
			payment = this.paymentService.selectByPrimaryKey(order.getPayment_id());
			key = payment.getChinabank_key();
		}
		if ((remark2.equals("cash")) || (remark2.equals("gold"))
				|| (remark2.equals("integral"))
				|| (remark2.equals("cloudpurchase"))) {
			Map q_params = Maps.newHashMap();
			q_params.put("install", Boolean.valueOf(true));
			if (remark2.equals("cash")) {
				q_params.put("mark", obj.getPd_payment());
			}
			if (remark2.equals("gold")) {
				q_params.put("mark", gold.getGold_payment());
			}
			if (remark2.equals("integral")) {
				q_params.put("mark", ig_order.getIgo_payment());
			}
			if (remark2.equals("cloudpurchase")) {
				q_params.put("mark", cp_order.getPayment());
			}
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			key = ((Payment) payments.get(0)).getChinabank_key();
		}
		String v_oid = request.getParameter("v_oid");
		String v_pmode = request.getParameter("v_pmode");

		String v_pstatus = request.getParameter("v_pstatus");
		String v_pstring = request.getParameter("v_pstring");

		String v_amount = request.getParameter("v_amount");
		String v_moneytype = request.getParameter("v_moneytype");
		String v_md5str = request.getParameter("v_md5str");
		String text = v_oid + v_pstatus + v_amount + v_moneytype + key;
		String v_md5text = Md5Encrypt.md5(text).toUpperCase();
		if (v_md5str.equals(v_md5text)) {
			if ("20".equals(v_pstatus)) {
				if ((remark2.equals("goods")) || (remark2.equals("group"))) {
					boolean flag = this.handelOrderFormService.payByOnline(
							order, v_oid, CommUtil.getURL(request));
					if (flag) {
						this.orderFormTools.updateGoodsInventory(order);
					}
					this.orderFormTools.sendMsgWhenHandleOrder(
							CommUtil.getURL(request), order,
							"tobuyer_online_pay_ok_notify",
							"toseller_online_pay_ok_notify");
					mv.addObject("all_price", Double
							.valueOf(this.orderFormTools
									.query_order_pay_price(CommUtil
											.null2String(order.getId()))));
					mv.addObject("obj", order);
				}
				if (remark2.endsWith("cash")) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.updateById(obj);
					User user = this.userService.selectByPrimaryKey(obj.getPd_user()
							.getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							user.getAvailableBalance(), obj.getPd_amount())));
					this.userService.updateById(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("网银在线支付");
					this.predepositLogService.saveEntity(log);
					mv = new RedPigJModelAndView("success.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "充值" + obj.getPd_amount() + "成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/predeposit_list");
				}
				if (remark2.equals("gold")) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.updateById(gold);
					User user = this.userService.selectByPrimaryKey(gold.getGold_user()
							.getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.updateById(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("网银在线支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.saveEntity(log);
					mv = new RedPigJModelAndView("success.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换" + gold.getGold_count()
							+ "金币成功");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/seller/gold_record_list");
				}
				if (remark2.equals("integral")) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("bill");
					this.integralGoodsOrderService.updateById(ig_order);
					List<Map> ig_maps = this.orderFormTools
							.query_integral_goodsinfo(ig_order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService
								.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
						goods.setIg_goods_count(goods.getIg_goods_count()
								- CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.updateById(goods);
					}
					mv = new RedPigJModelAndView("integral_order_finish.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("obj", ig_order);
				}
				if (remark2.equals("cloudpurchase")) {
					if (cp_order.getStatus() < 5) {
						cp_order.setStatus(5);
						cp_order.setPayTime(new Date());
						this.cloudPurchaseOrderService.updateById(cp_order);
						this.cloudPurchaseOrderService.reduce_inventory(
								cp_order, request);
					}
					mv = new RedPigJModelAndView("success.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "预付款支付成功！");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/cloudbuy_order");
				}
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "网银在线支付失败！");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * paypal回调方法,paypal支付成功了后，调用该方法进行后续处理
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping({ "/paypal_return" })
	public ModelAndView paypal_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("order_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Enumeration en = request.getParameterNames();
		String str = "cmd=_notify-validate";
		while (en.hasMoreElements()) {
			String paramName = (String) en.nextElement();
			String paramValue = request.getParameter(paramName);
			str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
		}
		String[] customs = request.getParameter("custom").split(",");
		String remark1 = customs[0];
		String remark2 = customs[1];
		String item_name = request.getParameter("item_name");
		String txnId = request.getParameter("txn_id");
		OrderForm order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		Payment payment = null;
		if ((remark2.equals("goods")) || (remark2.equals("group"))) {
			order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(remark1
					.trim()));
			payment = this.paymentService.selectByPrimaryKey(order.getPayment_id());
		}
		if (remark2.equals("cash")) {
			obj = this.predepositService
					.selectByPrimaryKey(CommUtil.null2Long(remark1));
		}
		if (remark2.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(remark1));
		}
		if (remark2.equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
					.null2Long(remark1));
		}
		if (remark2.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil
					.null2Long(remark1));
		}
		String txn_id = request.getParameter("txn_id");

		String itemName = request.getParameter("item_name");
		String paymentStatus = request.getParameter("payment_status");
		String paymentAmount = request.getParameter("mc_gross");
		String paymentCurrency = request.getParameter("mc_currency");
		String receiverEmail = request.getParameter("receiver_email");
		String payerEmail = request.getParameter("payer_email");
		if ((paymentStatus.equals("Completed"))
				|| (paymentStatus.equals("Pending"))) {
			if (((remark2.equals("goods")) || (remark2.equals("group")))
					&& (order.getOrder_status() < 20)) {
				if (CommUtil.null2String(order.getTotalPrice()).equals(
						paymentAmount)) {
					boolean flag = this.handelOrderFormService.payByOnline(
							order, txnId, CommUtil.getURL(request));
					if (flag) {
						this.orderFormTools.updateGoodsInventory(order);
					}
					this.orderFormTools.sendMsgWhenHandleOrder(
							CommUtil.getURL(request), order,
							"tobuyer_online_pay_ok_notify",
							"toseller_online_pay_ok_notify");
					mv.addObject("all_price", Double
							.valueOf(this.orderFormTools
									.query_order_pay_price(CommUtil
											.null2String(order.getId()))));
					mv.addObject("obj", order);
				}
			}
			if (remark2.endsWith("cash")) {
				if (obj.getPd_pay_status() < 2) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.updateById(obj);
					User user = this.userService.selectByPrimaryKey(obj.getPd_user()
							.getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							user.getAvailableBalance(), obj.getPd_amount())));
					this.userService.updateById(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("Paypal在线支付");
					this.predepositLogService.saveEntity(log);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "成功充值：" + obj.getPd_amount());
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list");
			}
			if (remark2.equals("gold")) {
				if (gold.getGold_pay_status() < 2) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.updateById(gold);
					User user = this.userService.selectByPrimaryKey(gold.getGold_user()
							.getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.updateById(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("Paypal");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.saveEntity(log);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "成功充值金币:" + gold.getGold_count());
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/gold_record_list");
			}
			if (remark2.equals("integral")) {
				if (ig_order.getIgo_status() < 20) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("paypal");
					this.integralGoodsOrderService.updateById(ig_order);
					List<Map> ig_maps = this.orderFormTools
							.query_integral_goodsinfo(ig_order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService
								.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
						goods.setIg_goods_count(goods.getIg_goods_count()
								- CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.updateById(goods);
					}
				}
				mv = new RedPigJModelAndView("integral_order_finish.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ig_order);
			}
			if (remark2.equals("cloudpurchase")) {
				if (cp_order.getStatus() < 5) {
					cp_order.setStatus(5);
					cp_order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(cp_order);
					this.cloudPurchaseOrderService.reduce_inventory(cp_order,
							request);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,

						response);
				mv.addObject("op_title", "预付款支付成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/cloudbuy_order");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "Paypal支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 微信支付
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/pay/wx_pay" })
	public ModelAndView wx_pay(HttpServletRequest request,
			HttpServletResponse response, String id, String type)
			throws Exception {
		ModelAndView mv = new RedPigJModelAndView("wx_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		RequestHandler reqHandler = new RequestHandler(request, response);

		List<Payment> payments = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", "wx_pay");
		payments = this.paymentService.queryPageList(params);
		
		Payment payment = null;
		if (payments.size() > 0) {
			payment = (Payment) payments.get(0);
		}
		String body = "";
		String attach = "";
		String out_trade_no = "";
		double total_fee = 0.0D;
		boolean submit = true;
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if ((type.equals("goods")) || (type.equals("group"))) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			} else {
				body = of.getOrder_id();
				attach = of.getId() + "_" + of.getOrder_id() + "_"
						+ of.getUser_id() + "_" + type;
				out_trade_no = of.getOrder_id();
				total_fee = Double.valueOf(of.getTotalPrice().toString()).doubleValue() * 100.0D;
				mv.addObject("obj", of);
			}
		}
		if (type.equals("cash")) {
			pd = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
				submit = false;
			} else {
				body = pd.getPd_sn();
				attach = pd.getId() + "_" + pd.getPd_sn() + "_"
						+ pd.getPd_user().getId() + "_" + type;
				out_trade_no = pd.getPd_sn();
				total_fee = Double.valueOf(pd.getPd_amount().toString()).doubleValue() * 100.0D;
				mv.addObject("obj", pd);
			}
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false;
			} else {
				body = gold.getGold_sn();
				attach = gold.getId() + "_" + gold.getGold_sn() + "_"
						+ gold.getGold_user().getId() + "_" + type;
				out_trade_no = gold.getGold_sn();
				total_fee = Double.valueOf(of.getTotalPrice().toString()).doubleValue() * 100.0D;
				mv.addObject("obj", gold);
			}
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false;
			} else {
				body = ig_order.getIgo_order_sn();
				attach = ig_order.getId() + "_" + ig_order.getIgo_order_sn()
						+ "_" + ig_order.getIgo_user().getId() + "_" + type;
				out_trade_no = ig_order.getIgo_order_sn();
				total_fee = Double.valueOf(ig_order.getIgo_trans_fee().toString()).doubleValue() * 100.0D;
				mv.addObject("obj", ig_order);
			}
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (cp_order.getStatus() == 5) {
				submit = false;
			} else {
				body = cp_order.getIgo_order_sn();
				attach = cp_order.getId() + "_" + cp_order.getIgo_order_sn()
						+ "_" + cp_order.getUser_id() + "_" + type;
				out_trade_no = cp_order.getIgo_order_sn();
				total_fee = Double.valueOf(cp_order.getPrice()).doubleValue() * 100.0D;
				mv.addObject("obj", cp_order);
			}
		}
		if ((submit) && (payment != null)) {
			if (payment != null) {
				String app_id = payment.getWx_appid();
				String app_key = payment.getWx_paySignKey();
				String partner = payment.getTenpay_partner();
				String noncestr = Sha1Util.getNonceStr();
				String timestamp = Sha1Util.getTimeStamp();
				String path = request.getContextPath();
				String basePath = request.getScheme() + "://"
						+ request.getServerName() + path + "/";
				reqHandler.setParameter("appid", app_id);
				reqHandler.setParameter("mch_id", partner);
				reqHandler.setParameter("nonce_str", noncestr);
				reqHandler.setParameter("device_info", "WEB");
				reqHandler.setParameter("body", body);
				reqHandler.setParameter("attach", attach);
				reqHandler.setParameter("out_trade_no", out_trade_no);
				System.out.println(String.valueOf(total_fee).replaceAll(".0", ""));
				reqHandler.setParameter("total_fee", String.valueOf(total_fee).replaceAll(".0", ""));
				reqHandler.setParameter("spbill_create_ip",
						CommUtil.getIpAddr(request));
				reqHandler.setParameter("notify_url", basePath
						+ "weixin_return");
				reqHandler.setParameter("trade_type", "NATIVE");
				String requestUrl = reqHandler.reqToXml(app_key);
				HttpURLConnection conn = creatConnection(requestUrl);
				String result = getInput(conn);
				Map<String, String> map = doXMLParse(result);
				String return_code = ((String) map.get("return_code"))
						.toString();
				String prepay_id = "";
				String code_url = "";
				if ("SUCCESS".equals(return_code)) {
					String result_code = ((String) map.get("result_code"))
							.toString();
					if ("SUCCESS".equals(result_code)) {
						prepay_id = (String) map.get("prepay_id");
						code_url = (String) map.get("code_url");
					}
				}
				mv.addObject("code_url", code_url);
				reqHandler.getAllParameters().clear();
				reqHandler.setParameter("appId", app_id);
				reqHandler.setParameter("nonceStr", noncestr);
				reqHandler.genSign(app_key);
				mv.addObject("app_id", app_id);
				mv.addObject("timestamp", timestamp);
				mv.addObject("noncestr", noncestr);
				mv.addObject("packageValue", "prepay_id=" + prepay_id);
				String order_sign = reqHandler.getParameter("sign");
				mv.addObject("sign", order_sign);
				if ((type.equals("goods")) || (type.equals("group"))) {
					of.setOrder_sign(order_sign);
					this.orderFormService.updateById(of);
				}
				if (type.equals("cash")) {
					pd.setOrder_sign(order_sign);
					this.predepositService.updateById(pd);
				}
				if (type.equals("gold")) {
					gold.setOrder_sign(order_sign);
					this.goldRecordService.updateById(gold);
				}
				if (type.equals("integral")) {
					ig_order.setOrder_sign(order_sign);
					this.integralGoodsOrderService.updateById(ig_order);
				}
				if (type.equals("cloudpurchase")) {
					cp_order.setOrder_sign(order_sign);
					this.cloudPurchaseOrderService.updateById(cp_order);
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付方式错误！");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 微信回调
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping({ "/weixin_return" })
	public void weixin_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				request.getInputStream(), "UTF-8"));
		String line = "";
		StringBuffer strBuf = new StringBuffer();
		while ((line = in.readLine()) != null) {
			strBuf.append(line).append("\n");
		}
		in.close();
		Map<String, String> xml = doXMLParse(strBuf.toString().trim());

		List<Payment> payments = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", "wx_pay");
		payments = this.paymentService.queryPageList(params);
		
		Payment payment = null;
		if (payments.size() > 0) {
			payment = (Payment) payments.get(0);
		}
		String appid = ((String) xml.get("appid")).toString();
		if ("SUCCESS".equals(((String) xml.get("return_code")).toString())) {
			if ("SUCCESS".equals(((String) xml.get("result_code")).toString())) {
				RequestHandler reqHandler = new RequestHandler(request,
						response);
				reqHandler.setParameter("appId", payment.getWx_appid());
				reqHandler.setParameter("nonceStr",
						((String) xml.get("nonce_str")).toString());
				reqHandler.genSign(payment.getWx_paySignKey());

				String sign = reqHandler.getParameter("sign");

				String total_fee = (String) xml.get("total_fee");
				String[] attachs = ((String) xml.get("attach")).split("_");
				String type = attachs[3];
				OrderForm main_order = null;
				Predeposit obj = null;
				GoldRecord gold = null;
				IntegralGoodsOrder ig_order = null;
				CloudPurchaseOrder cp_order = null;
				if (("goods".equals(type)) || ("group".equals(type))) {
					main_order = this.orderFormService.selectByPrimaryKey(CommUtil
							.null2Long(attachs[0]));
					if (sign.equals(main_order.getOrder_sign())) {
						boolean flag = false;
						flag = this.handelOrderFormService.payByOnline(
								main_order, "", CommUtil.getURL(request));
						if (flag) {
							this.orderFormTools
									.updateGoodsInventory(main_order);
						}
						this.orderFormTools.sendMsgWhenHandleOrder(
								CommUtil.getURL(request), main_order,
								"tobuyer_online_pay_ok_notify",
								"toseller_online_pay_ok_notify");
					}
				}
				if ("integral".equals(type)) {
					ig_order = this.integralGoodsOrderService
							.selectByPrimaryKey(CommUtil.null2Long(attachs[0]));
					if ((ig_order != null) && (ig_order.getIgo_status() < 20)
							&& (sign.equals(ig_order.getOrder_sign()))) {
						ig_order.setIgo_status(20);
						ig_order.setIgo_pay_time(new Date());
						ig_order.setIgo_payment("wx_pay");
						this.integralGoodsOrderService.updateById(ig_order);
						List<Map> ig_maps = this.orderFormTools
								.query_integral_goodsinfo(ig_order
										.getGoods_info());
						for (Map map : ig_maps) {
							IntegralGoods goods = this.integralGoodsService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("id")));
							goods.setIg_goods_count(goods.getIg_goods_count()
									- CommUtil.null2Int(map
											.get("ig_goods_count")));
							goods.setIg_exchange_count(goods
									.getIg_exchange_count()
									+ CommUtil.null2Int(map
											.get("ig_goods_count")));
							this.integralGoodsService.updateById(goods);
						}
					}
				}
				if ("cash".equals(type)) {
					obj = this.predepositService.selectByPrimaryKey(CommUtil
							.null2Long(attachs[0]));
					if ((obj != null) && (obj.getPd_pay_status() != 2)
							&& (sign.equals(obj.getOrder_sign()))) {
						obj.setPd_status(1);
						obj.setPd_pay_status(2);
						this.predepositService.updateById(obj);
						User user = this.userService.selectByPrimaryKey(obj
								.getPd_user().getId());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.add(user.getAvailableBalance(),
										obj.getPd_amount())));
						this.userService.updateById(user);
						PredepositLog log = new PredepositLog();
						log.setAddTime(new Date());
						log.setPd_log_amount(obj.getPd_amount());
						log.setPd_log_user(obj.getPd_user());
						log.setPd_op_type("充值");
						log.setPd_type("可用预存款");
						log.setPd_log_info("微信支付");
						this.predepositLogService.saveEntity(log);
					}
				}
				if ("gold".equals(type)) {
					gold = this.goldRecordService.selectByPrimaryKey(CommUtil
							.null2Long(attachs[0]));
					if ((gold != null) && (gold.getGold_pay_status() != 2)
							&& (sign.equals(gold.getOrder_sign()))) {
						gold.setGold_status(1);
						gold.setGold_pay_status(2);
						this.goldRecordService.updateById(gold);
						User user = this.userService.selectByPrimaryKey(gold
								.getGold_user().getId());
						user.setGold(user.getGold() + gold.getGold_count());
						this.userService.updateById(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(gold.getGold_payment());
						log.setGl_content("微信支付");
						log.setGl_money(gold.getGold_money());
						log.setGl_count(gold.getGold_count());
						log.setGl_type(0);
						log.setGl_user(gold.getGold_user());
						log.setGr(gold);
						this.goldLogService.saveEntity(log);
					}
				}
				if (type.equals("cloudpurchase")) {
					cp_order = this.cloudPurchaseOrderService
							.selectByPrimaryKey(CommUtil.null2Long(attachs[0]));
					if (cp_order.getStatus() < 5) {
						cp_order.setStatus(5);
						cp_order.setPayTime(new Date());
						this.cloudPurchaseOrderService.updateById(cp_order);
						this.cloudPurchaseOrderService.reduce_inventory(
								cp_order, request);
					}
				}
				PrintWriter write = response.getWriter();
				write.print("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");
			} else {
				PrintWriter write = response.getWriter();
				write.print("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>");
			}
		} else {
			PrintWriter write = response.getWriter();
			write.print("<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>");
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/unionpay_return" })
	public ModelAndView unionpay_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("order_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.setCharacterEncoding("ISO-8859-1");
		String encoding = request.getParameter("encoding");

		Map<String, String> reqParam = getAllRequestParam(request);
		Map<String, String> valideData = null;
		if ((reqParam != null) && (!reqParam.isEmpty())) {
			Iterator<Map.Entry<String, String>> it = reqParam.entrySet()
					.iterator();
			valideData = new HashMap(reqParam.size());
			while (it.hasNext()) {
				Map.Entry<String, String> e = (Map.Entry) it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes("ISO-8859-1"), encoding);
				valideData.put(key, value);
			}
		}
		if (!SDKUtil.validate(valideData, encoding)) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付回调失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			String orderId = (String) valideData.get("orderId");
			String respCode = (String) valideData.get("respCode");
			String txtAmt = (String) valideData.get("txtAmt");
			String reqReserved = (String) valideData.get("reqReserved");
			String[] infos = reqReserved.split("_");
			String type = infos[0];
			String order_no = infos[1];
			OrderForm main_order = null;
			Predeposit obj = null;
			GoldRecord gold = null;
			IntegralGoodsOrder ig_order = null;
			CloudPurchaseOrder cp_order = null;
			if (type.equals("goods")) {
				main_order = this.orderFormService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("cash")) {
				obj = this.predepositService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("gold")) {
				gold = this.goldRecordService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("integral")) {
				ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("group")) {
				main_order = this.orderFormService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("cloudpurchase")) {
				cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			Payment payment = null;
			if ((type.equals("goods")) || (type.equals("group"))) {
				payment = this.paymentService.selectByPrimaryKey(main_order
						.getPayment_id());
			}
			if ((type.equals("goods")) || (type.equals("group"))) {
				boolean flag = this.handelOrderFormService.payByOnline(
						main_order, main_order.getTrade_no(),
						CommUtil.getURL(request));
				if (flag) {
					this.orderFormTools.updateGoodsInventory(main_order);
				}
				this.orderFormTools.sendMsgWhenHandleOrder(
						CommUtil.getURL(request), main_order,
						"tobuyer_online_pay_ok_notify",
						"toseller_online_pay_ok_notify");
				mv.addObject("all_price", Double.valueOf(this.orderFormTools
						.query_order_pay_price(CommUtil.null2String(main_order
								.getId()))));
				mv.addObject("obj", main_order);
			}
			if (type.equals("cash")) {
				if (obj.getPd_pay_status() != 2) {
					obj.setPd_status(1);
					obj.setPd_pay_status(2);
					this.predepositService.updateById(obj);
					User user = this.userService.selectByPrimaryKey(obj.getPd_user()
							.getId());
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
							user.getAvailableBalance(), obj.getPd_amount())));
					this.userService.updateById(user);
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_amount(obj.getPd_amount());
					log.setPd_log_user(obj.getPd_user());
					log.setPd_op_type("充值");
					log.setPd_type("可用预存款");
					log.setPd_log_info("银联在线支付");
					this.predepositLogService.saveEntity(log);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "恭喜您，成功充值" + obj.getPd_amount() + "元");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list");
			}
			if (type.equals("gold")) {
				if (gold.getGold_pay_status() != 2) {
					gold.setGold_status(1);
					gold.setGold_pay_status(2);
					this.goldRecordService.updateById(gold);
					User user = this.userService.selectByPrimaryKey(gold.getGold_user()
							.getId());
					user.setGold(user.getGold() + gold.getGold_count());
					this.userService.updateById(user);
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_payment(gold.getGold_payment());
					log.setGl_content("银联支付");
					log.setGl_money(gold.getGold_money());
					log.setGl_count(gold.getGold_count());
					log.setGl_type(0);
					log.setGl_user(gold.getGold_user());
					log.setGr(gold);
					this.goldLogService.saveEntity(log);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "兑换" + gold.getGold_count() + "金币成功");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/seller/gold_record_list");
			}
			if (type.equals("integral")) {
				if (ig_order.getIgo_status() < 20) {
					ig_order.setIgo_status(20);
					ig_order.setIgo_pay_time(new Date());
					ig_order.setIgo_payment("unionpay");
					this.integralGoodsOrderService.updateById(ig_order);
					List<Map> ig_maps = this.orderFormTools
							.query_integral_goodsinfo(ig_order.getGoods_info());
					for (Map map : ig_maps) {
						IntegralGoods goods = this.integralGoodsService
								.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
						goods.setIg_goods_count(goods.getIg_goods_count()
								- CommUtil.null2Int(map.get("ig_goods_count")));
						goods.setIg_exchange_count(goods.getIg_exchange_count()
								+ CommUtil.null2Int(map.get("ig_goods_count")));
						this.integralGoodsService.updateById(goods);
					}
				}
				mv = new RedPigJModelAndView("integral_order_finish.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ig_order);
			}
			if (type.equals("cloudpurchase")) {
				if (cp_order.getStatus() < 5) {
					cp_order.setStatus(5);
					cp_order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(cp_order);
					this.cloudPurchaseOrderService.reduce_inventory(cp_order,
							request);
				}
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,

						response);
				mv.addObject("op_title", "预付款支付成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/cloudbuy_order");
			}
		}
		return mv;
	}

	@RequestMapping({ "/unionpay_notify" })
	public void unionpay_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String result = "ok";
		request.setCharacterEncoding("ISO-8859-1");
		String encoding = request.getParameter("encoding");

		Map<String, String> reqParam = getAllRequestParam(request);
		Map<String, String> valideData = null;
		if ((reqParam != null) && (!reqParam.isEmpty())) {
			Iterator<Map.Entry<String, String>> it = reqParam.entrySet()
					.iterator();
			valideData = new HashMap(reqParam.size());
			while (it.hasNext()) {
				Map.Entry<String, String> e = (Map.Entry) it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				value = new String(value.getBytes("ISO-8859-1"), encoding);
				valideData.put(key, value);
			}
		}
		if (!SDKUtil.validate(valideData, encoding)) {
			result = "no";
		} else {
			String orderId = (String) valideData.get("orderId");
			String respCode = (String) valideData.get("respCode");
			String txtAmt = (String) valideData.get("txtAmt");
			String reqReserved = (String) valideData.get("reqReserved");
			String[] infos = reqReserved.split("_");
			String type = infos[0];
			String order_no = infos[1];
			OrderForm main_order = null;
			Predeposit obj = null;
			GoldRecord gold = null;
			IntegralGoodsOrder ig_order = null;
			CloudPurchaseOrder cp_order = null;
			if (type.equals("goods")) {
				main_order = this.orderFormService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("cash")) {
				obj = this.predepositService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("gold")) {
				gold = this.goldRecordService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("integral")) {
				ig_order = this.integralGoodsOrderService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("group")) {
				main_order = this.orderFormService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			if (type.equals("cloudpurchase")) {
				cp_order = this.cloudPurchaseOrderService.selectByPrimaryKey(CommUtil
						.null2Long(order_no));
			}
			Payment payment = null;
			if ((type.equals("goods")) || (type.equals("group"))) {
				payment = this.paymentService.selectByPrimaryKey(main_order
						.getPayment_id());
			}
			if ((type.equals("goods")) || (type.equals("group"))) {
				boolean flag = this.handelOrderFormService.payByOnline(
						main_order, main_order.getTrade_no(),
						CommUtil.getURL(request));
				if (flag) {
					this.orderFormTools.updateGoodsInventory(main_order);
				}
				this.orderFormTools.sendMsgWhenHandleOrder(
						CommUtil.getURL(request), main_order,
						"tobuyer_online_pay_ok_notify",
						"toseller_online_pay_ok_notify");
			}
			if ((type.equals("cash")) && (obj.getPd_pay_status() != 2)) {
				obj.setPd_status(1);
				obj.setPd_pay_status(2);
				this.predepositService.updateById(obj);
				User user = this.userService.selectByPrimaryKey(obj.getPd_user()
						.getId());
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						user.getAvailableBalance(), obj.getPd_amount())));
				this.userService.updateById(user);
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_amount(obj.getPd_amount());
				log.setPd_log_user(obj.getPd_user());
				log.setPd_op_type("充值");
				log.setPd_type("可用预存款");
				log.setPd_log_info("银联在线支付");
				this.predepositLogService.saveEntity(log);
			}
			if ((type.equals("gold")) && (gold.getGold_pay_status() != 2)) {
				gold.setGold_status(1);
				gold.setGold_pay_status(2);
				this.goldRecordService.updateById(gold);
				User user = this.userService.selectByPrimaryKey(gold.getGold_user()
						.getId());
				user.setGold(user.getGold() + gold.getGold_count());
				this.userService.updateById(user);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				log.setGl_payment(gold.getGold_payment());
				log.setGl_content("银联支付");
				log.setGl_money(gold.getGold_money());
				log.setGl_count(gold.getGold_count());
				log.setGl_type(0);
				log.setGl_user(gold.getGold_user());
				log.setGr(gold);
				this.goldLogService.saveEntity(log);
			}
			if ((type.equals("integral")) && (ig_order.getIgo_status() < 20)) {
				ig_order.setIgo_status(20);
				ig_order.setIgo_pay_time(new Date());
				ig_order.setIgo_payment("unionpay");
				this.integralGoodsOrderService.updateById(ig_order);
				List<Map> ig_maps = this.orderFormTools
						.query_integral_goodsinfo(ig_order.getGoods_info());
				for (Map map : ig_maps) {
					IntegralGoods goods = this.integralGoodsService
							.selectByPrimaryKey(CommUtil.null2Long(map.get("id")));
					goods.setIg_goods_count(goods.getIg_goods_count()
							- CommUtil.null2Int(map.get("ig_goods_count")));
					goods.setIg_exchange_count(goods.getIg_exchange_count()
							+ CommUtil.null2Int(map.get("ig_goods_count")));
					this.integralGoodsService.updateById(goods);
				}
			}
			if ((type.equals("cloudpurchase")) && (cp_order.getStatus() < 5)) {
				cp_order.setStatus(5);
				cp_order.setPayTime(new Date());
				this.cloudPurchaseOrderService.updateById(cp_order);
				this.cloudPurchaseOrderService.reduce_inventory(cp_order,
						request);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, String> getAllRequestParam(
			HttpServletRequest request) {
		Map<String, String> res = Maps.newHashMap();
		Enumeration<?> temp = request.getParameterNames();
		if (temp != null) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				if ((res.get(en) == null) || ("".equals(res.get(en)))) {
					res.remove(en);
				}
			}
		}
		return res;
	}

	@RequestMapping({ "/pay_code/generate" })
	public void pay_code_generate(HttpServletRequest request,
			HttpServletResponse response, String url) throws IOException {
		String logoPath = "";
		if (this.configService.getSysConfig().getQr_logo() != null) {
			logoPath =

			request.getSession().getServletContext().getRealPath("/")
					+ this.configService.getSysConfig().getQr_logo().getPath()
					+ File.separator
					+ this.configService.getSysConfig().getQr_logo().getName();
		}
		QRCodeUtil.encode(url, logoPath, response, true);
	}

	private HttpURLConnection creatConnection(String requestUrl)
			throws IOException {
		URL url = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(30000);
		conn.setReadTimeout(30000);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);

		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Length", requestUrl.length() + "");
		String encode = "utf-8";
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(),
				encode);
		out.write(requestUrl.toString());
		out.flush();
		out.close();
		return conn;
	}

	private String getInput(HttpURLConnection conn) throws IOException {
		if (conn.getResponseCode() != 200) {
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		String line = "";
		StringBuffer strBuf = new StringBuffer();
		while ((line = in.readLine()) != null) {
			strBuf.append(line).append("\n");
		}
		in.close();
		return strBuf.toString().trim();
	}

	public Map doXMLParse(String strxml) throws JDOMException, IOException {
		strxml = strxml.replaceFirst("encoding=\".*\"", "encoding=\"UTF-8\"");
		if ((strxml == null) || ("".equals(strxml))) {
			return null;
		}
		Map m = Maps.newHashMap();

		InputStream in = new ByteArrayInputStream(strxml.getBytes("UTF-8"));

		SAXBuilder builder = new SAXBuilder();

		Document doc = builder.build(in);

		Element root = doc.getRootElement();

		List list = root.getChildren();

		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();

			String k = e.getName();

			String v = "";

			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}
			m.put(k, v);
		}
		in.close();

		return m;
	}

	public String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}
		return sb.toString();
	}
}
