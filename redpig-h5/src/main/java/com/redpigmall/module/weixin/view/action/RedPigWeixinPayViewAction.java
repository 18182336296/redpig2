package com.redpigmall.module.weixin.view.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
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
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import com.redpigmall.pay.alipay.config.AlipayConfig;
import com.redpigmall.pay.alipay.util.AlipayNotify;
import com.redpigmall.pay.tenpay.RequestHandler;
import com.redpigmall.pay.tenpay.util.Sha1Util;
import com.redpigmall.pay.unionpay.acp.sdk.SDKUtil;

/**
 * 移动端在线支付毁掉控制器
 * 
 * <p>
 * Title: RedPigWeixinPayViewAction.java
 * </p>
 * 
 * <p>
 * Description:
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
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigWeixinPayViewAction extends BaseAction{
	
	/**
	 * 支付宝回调
	 * @param request
	 * @param response
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping({"/aplipay_return"})
	public ModelAndView aplipay_return(HttpServletRequest request,HttpServletResponse response) throws Exception {
		System.out.println("h5_alipay_return");
		ModelAndView mv = new RedPigJModelAndView("weixin/order_pay_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String trade_no = request.getParameter("trade_no");
		String order_nos = request.getParameter("out_trade_no");
		String order_no = order_nos.split("-")[2];
		String total_fee = request.getParameter("price");
		String subject = request.getParameter("subject");
		String result = request.getParameter("result");

		String type = CommUtil.null2String(request.getParameter("pay_body"))
				.trim();
		if (type.equals("")) {
			type = "goods";
		}
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
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
		Map<String, String> params = Maps.newHashMap();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = valueStr + values[i] + ",";
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
				|| (type.equals("integral"))) {
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
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			config.setKey(((Payment) payments.get(0)).getSafeKey());
			config.setPartner(((Payment) payments.get(0)).getPartner());
			config.setSeller_email(((Payment) payments.get(0))
					.getSeller_email());
		}
		config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify");
		config.setReturn_url(CommUtil.getURL(request) + "/alipay_return");
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result) {
			if ((type.equals("goods")) || (type.equals("group"))) {
				boolean flag = false;
				flag = this.handelOrderFormService.payByOnline(main_order,
						trade_no, CommUtil.getURL(request));
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
			if (type.equals("integral")) {
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
				mv = new RedPigJModelAndView("weixin/integral_order_finish.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ig_order);
			}
			if (type.equals("cloudpurchase")) {
				CloudPurchaseOrder order = this.cloudPurchaseOrderService
						.selectByPrimaryKey(CommUtil.null2Long(order_no));
				if (order.getStatus() == 0) {
					order.setStatus(5);
					order.setPayment("alipay_wap");
					order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(order);
					boolean ret = true;
					
					if (ret) {
						this.cloudPurchaseOrderService.reduce_inventory(order,
								request);
					}
				}
				mv = new RedPigJModelAndView("weixin/cloud_order_finish.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", order);
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付回调失败");
			mv.addObject("url", CommUtil.getURL(request) + "index");
		}
		return mv;
	}
	
	/**
	 * 支付宝通知
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping({ "/alipay_notify" })
	public void wap_alipay_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Map<String, String> params = Maps.newHashMap();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = valueStr + values[i] + ",";
			}
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		String trade_no = params.get("trade_no");

		String trade_status = params.get("trade_status").replace(",", "");

		String type = CommUtil.null2String(request.getParameter("pay_body")).trim();
		if ("".endsWith(type)) {
			type = "goods";
		}
		OrderForm main_order = null;
		Predeposit obj = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		String order_no = params.get("out_trade_no").split("-")[2].replace(",", "");
		if ((type.equals("goods")) || (type.equals("group"))) {
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
				|| (type.equals("integral"))) {
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
			
			List<Payment> payments = this.paymentService.queryPageList(q_params);
			
			config.setKey(((Payment) payments.get(0)).getSafeKey());
			config.setPartner(((Payment) payments.get(0)).getPartner());
			config.setSeller_email(((Payment) payments.get(0))
					.getSeller_email());
		}
		config.setNotify_url(CommUtil.getURL(request) + "/alipay_notify");
		config.setReturn_url(CommUtil.getURL(request) + "/alipay_return");
		boolean verify_result = AlipayNotify.verify(config, params);
		if (verify_result) {
			if (((type.equals("goods")) || (type.equals("group")))
					&& ((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
							|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
								.equals("TRADE_SUCCESS")))) {
				boolean flag = false;
				flag = this.handelOrderFormService.payByOnline(main_order,
						trade_no, CommUtil.getURL(request));
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
			if (type.equals("cloudpurchase")) {
				CloudPurchaseOrder order = this.cloudPurchaseOrderService
						.selectByPrimaryKey(CommUtil.null2Long(order_no));
				if (((trade_status.equals("WAIT_SELLER_SEND_GOODS"))
						|| (trade_status.equals("TRADE_FINISHED")) || (trade_status
							.equals("TRADE_SUCCESS")))
						&& (order.getStatus() == 0)) {
					order.setStatus(5);
					order.setPayment("alipay_wap");
					order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(order);
					boolean ret = true;
					if (ret) {
						this.cloudPurchaseOrderService.reduce_inventory(order,
								request);
					}
				}
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
	 * 微信支付回调
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
		System.out.println(strBuf.toString().trim());
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
				if ("gole".equals(type)) {
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
					CloudPurchaseOrder order = this.cloudPurchaseOrderService
							.selectByPrimaryKey(CommUtil.null2Long(CommUtil
									.null2Long(attachs[0])));
					if ((order.getStatus() == 0)
							&& (sign.equals(order.getOrder_sign()))) {
						order.setStatus(5);
						order.setPayment("wx_pay");
						order.setPayTime(new Date());
						this.cloudPurchaseOrderService
								.updateById(order);
						boolean ret = true;
						if (ret) {
							this.cloudPurchaseOrderService.reduce_inventory(
									order, request);
						}
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
	 * 微信支付成功
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping({ "/wx_pay_success" })
	public ModelAndView wx_pay_success(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new RedPigJModelAndView("weixin/order_pay_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("type", type);
		if ("integral".equals(type)) {
			IntegralGoodsOrder of = this.integralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (of != null) {
				mv.addObject("obj", of);
				mv.addObject("all_price", of.getIgo_trans_fee());
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付失败");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
			return mv;
		}
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			mv.addObject("obj", obj);
			mv.addObject("all_price", obj.getTotalPrice());
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "支付失败");
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
	 * @param openid
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/weixin/pay/wx_pay" })
	public ModelAndView wx_pay(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String openid)
			throws Exception {
		ModelAndView mv = new RedPigJModelAndView("weixin/wx_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		RequestHandler reqHandler = new RequestHandler(request, response);
		mv.addObject("type", type);

		List<Payment> payments = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", "wx_pay");
		
		payments = this.paymentService.queryPageList(params);
		
		Payment payment = null;
		if (payments.size() > 0) {
			payment = (Payment) payments.get(0);
		}
		if ("integral".equals(type)) {
			IntegralGoodsOrder of = this.integralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if ((payment != null) && (of != null)) {
				String app_id = payment.getWx_appid();
				String app_key = payment.getWx_paySignKey();
				String partner = payment.getTenpay_partner();
				String noncestr = Sha1Util.getNonceStr();
				String timestamp = Sha1Util.getTimeStamp();
				double total_fee = Double.valueOf(
						of.getIgo_trans_fee().toString()).doubleValue() * 100.0D;
				int order_price = (int) total_fee;
				String path = request.getContextPath();
				String basePath = request.getScheme() + "://"
						+ request.getServerName() + path + "/";
				reqHandler.setParameter("appid", app_id);
				reqHandler.setParameter("mch_id", partner);
				reqHandler.setParameter("nonce_str", noncestr);
				reqHandler.setParameter("device_info", "WEB");
				reqHandler.setParameter("body", of.getIgo_order_sn());
				reqHandler.setParameter("attach",
						of.getId() + "_" + of.getIgo_order_sn() + "_"
								+ of.getIgo_user().getId() + "_" + type);
				reqHandler.setParameter("out_trade_no", of.getIgo_order_sn());
				reqHandler.setParameter("total_fee", order_price + "");
				reqHandler.setParameter("spbill_create_ip",
						CommUtil.getIpAddr(request));
				reqHandler.setParameter("notify_url", basePath
						+ "weixin_return");
				reqHandler.setParameter("trade_type", "JSAPI");
				reqHandler.setParameter("openid", openid);
				String requestUrl = reqHandler.reqToXml(app_key);
				HttpURLConnection conn = creatConnection(requestUrl);
				String result = getInput(conn);
				Map<String, String> map = doXMLParse(result);
				String return_code = ((String) map.get("return_code"))
						.toString();
				String prepay_id = "";
				if ("SUCCESS".equals(return_code)) {
					String result_code = ((String) map.get("result_code"))
							.toString();
					if ("SUCCESS".equals(result_code)) {
						prepay_id = (String) map.get("prepay_id");
					}
				}
				reqHandler.getAllParameters().clear();
				reqHandler.setParameter("appId", app_id);
				reqHandler.setParameter("timeStamp", timestamp);
				reqHandler.setParameter("nonceStr", noncestr);
				reqHandler.setParameter("signType", "MD5");
				reqHandler.setParameter("package", "prepay_id=" + prepay_id);
				reqHandler.genSign(app_key);
				mv.addObject("app_id", app_id);
				mv.addObject("timestamp", timestamp);
				mv.addObject("noncestr", noncestr);
				mv.addObject("packageValue", "prepay_id=" + prepay_id);
				mv.addObject("sign", reqHandler.getParameter("sign"));
				mv.addObject("obj", of);
				reqHandler.getAllParameters().clear();
				reqHandler.setParameter("appId", app_id);
				reqHandler.setParameter("nonceStr", noncestr);
				reqHandler.genSign(app_key);
				of.setOrder_sign(reqHandler.getParameter("sign"));
				of.setIgo_payment(payment.getMark());
				this.integralGoodsOrderService.updateById(of);
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
		} else if ("cloudpurchase".equals(type)) {
			CloudPurchaseOrder order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if ((payment != null) && (order != null)) {
				String app_id = payment.getWx_appid();
				String app_key = payment.getWx_paySignKey();
				String partner = payment.getTenpay_partner();
				String noncestr = Sha1Util.getNonceStr();
				String timestamp = Sha1Util.getTimeStamp();
				double total_fee = Double.valueOf(order.getPrice() * 100)
						.doubleValue();
				int order_price = (int) total_fee;
				String path = request.getContextPath();
				String basePath = request.getScheme() + "://"
						+ request.getServerName() + path + "/";
				reqHandler.setParameter("appid", app_id);
				reqHandler.setParameter("mch_id", partner);
				reqHandler.setParameter("nonce_str", noncestr);
				reqHandler.setParameter("device_info", "WEB");
				reqHandler.setParameter("body", order.getOdrdersn());
				reqHandler.setParameter(
						"attach",
						order.getId() + "_" + order.getOdrdersn() + "_"
								+ order.getUser_id() + "_" + type);
				reqHandler.setParameter("out_trade_no", order.getOdrdersn());
				reqHandler.setParameter("total_fee", order_price + "");
				reqHandler.setParameter("spbill_create_ip",
						CommUtil.getIpAddr(request));
				reqHandler.setParameter("notify_url", basePath
						+ "weixin_return");
				reqHandler.setParameter("trade_type", "JSAPI");
				reqHandler.setParameter("openid", openid);
				String requestUrl = reqHandler.reqToXml(app_key);
				HttpURLConnection conn = creatConnection(requestUrl);
				String result = getInput(conn);
				Map<String, String> map = doXMLParse(result);
				String return_code = ((String) map.get("return_code"))
						.toString();
				String prepay_id = "";
				if ("SUCCESS".equals(return_code)) {
					String result_code = ((String) map.get("result_code"))
							.toString();
					if ("SUCCESS".equals(result_code)) {
						prepay_id = (String) map.get("prepay_id");
					}
				}
				reqHandler.getAllParameters().clear();
				reqHandler.setParameter("appId", app_id);
				reqHandler.setParameter("timeStamp", timestamp);
				reqHandler.setParameter("nonceStr", noncestr);
				reqHandler.setParameter("signType", "MD5");
				reqHandler.setParameter("package", "prepay_id=" + prepay_id);
				reqHandler.genSign(app_key);
				mv.addObject("app_id", app_id);
				mv.addObject("timestamp", timestamp);
				mv.addObject("noncestr", noncestr);
				mv.addObject("packageValue", "prepay_id=" + prepay_id);
				mv.addObject("sign", reqHandler.getParameter("sign"));
				mv.addObject("obj", order);
				order.setOrder_sign(reqHandler.getParameter("sign"));
				order.setPayment("微信支付");
				this.cloudPurchaseOrderService.updateById(order);
			}
		} else {
			OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if ((payment != null) && (of != null)) {
				String app_id = payment.getWx_appid();
				String app_key = payment.getWx_paySignKey();
				String partner = payment.getTenpay_partner();
				String noncestr = Sha1Util.getNonceStr();
				String timestamp = Sha1Util.getTimeStamp();
				double total_fee = Double
						.valueOf(of.getTotalPrice().toString()).doubleValue() * 100.0D;
				int order_price = (int) total_fee;
				String path = request.getContextPath();
				String basePath = request.getScheme() + "://"
						+ request.getServerName() + path + "/";
				reqHandler.setParameter("appid", app_id);
				reqHandler.setParameter("mch_id", partner);
				reqHandler.setParameter("nonce_str", noncestr);
				reqHandler.setParameter("device_info", "WEB");
				reqHandler.setParameter("body", of.getOrder_id());
				reqHandler.setParameter(
						"attach",
						of.getId() + "_" + of.getOrder_id() + "_"
								+ of.getUser_id() + "_" + type);
				reqHandler.setParameter("out_trade_no", of.getOrder_id());
				reqHandler.setParameter("total_fee", order_price + "");
				reqHandler.setParameter("spbill_create_ip",
						CommUtil.getIpAddr(request));
				reqHandler.setParameter("notify_url", basePath
						+ "weixin_return");
				reqHandler.setParameter("trade_type", "JSAPI");
				reqHandler.setParameter("openid", openid);
				String requestUrl = reqHandler.reqToXml(app_key);
				HttpURLConnection conn = creatConnection(requestUrl);
				String result = getInput(conn);
				Map<String, String> map = doXMLParse(result);
				String return_code = ((String) map.get("return_code"))
						.toString();
				String prepay_id = "";
				if ("SUCCESS".equals(return_code)) {
					String result_code = ((String) map.get("result_code"))
							.toString();
					if ("SUCCESS".equals(result_code)) {
						prepay_id = (String) map.get("prepay_id");
					}
				}
				reqHandler.getAllParameters().clear();
				reqHandler.setParameter("appId", app_id);
				reqHandler.setParameter("timeStamp", timestamp);
				reqHandler.setParameter("nonceStr", noncestr);
				reqHandler.setParameter("signType", "MD5");
				reqHandler.setParameter("package", "prepay_id=" + prepay_id);
				reqHandler.genSign(app_key);
				mv.addObject("app_id", app_id);
				mv.addObject("timestamp", timestamp);
				mv.addObject("noncestr", noncestr);
				mv.addObject("packageValue", "prepay_id=" + prepay_id);
				mv.addObject("sign", reqHandler.getParameter("sign"));
				mv.addObject("obj", of);
				reqHandler.getAllParameters().clear();
				reqHandler.setParameter("appId", app_id);
				reqHandler.setParameter("nonceStr", noncestr);
				reqHandler.genSign(app_key);
				of.setOrder_sign(reqHandler.getParameter("sign"));
				of.setPayment_id(payment.getId());
				of.setPayment_mark(payment.getMark());
				of.setPayment_name(payment.getName());
				this.orderFormService.updateById(of);
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
		}
		return mv;
	}
	
	/**
	 * 银联支付回调
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping({ "/unionpay_return" })
	public ModelAndView unionpay_wap_return(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("weixin/order_pay_finish.html",
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
			mv = new RedPigJModelAndView("weixin/error.html",
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
				mv.addObject("orderFormTools", this.orderFormTools);
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
				mv = new RedPigJModelAndView("weixin/success.html",
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
				mv = new RedPigJModelAndView("weixin/success.html",
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
				mv = new RedPigJModelAndView("weixin/integral_order_finish.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ig_order);
			}
			if (type.equals("cloudpurchase")) {
				CloudPurchaseOrder order = this.cloudPurchaseOrderService
						.selectByPrimaryKey(CommUtil.null2Long(CommUtil
								.null2Long(order_no)));
				if (order.getStatus() == 0) {
					order.setStatus(5);
					order.setPayment("unionpay");
					order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(order);
					boolean ret = true;
					
					if (ret) {
						this.cloudPurchaseOrderService.reduce_inventory(order,
								request);
					}
				}
				mv = new RedPigJModelAndView("weixin/cloud_order_finish.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", order);
			}
		}
		return mv;
	}
	
	/**
	 * 银联支付通知
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping({ "/unionpay_notify" })
	public void unionpay_wap_notify(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String result = "200";
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
			result = "01";
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
			if (type.equals("cloudpurchase")) {
				CloudPurchaseOrder order = this.cloudPurchaseOrderService
						.selectByPrimaryKey(CommUtil.null2Long(CommUtil
								.null2Long(order_no)));
				if (order.getStatus() == 0) {
					order.setStatus(5);
					order.setPayment("unionpay");
					order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(order);
					boolean ret = true;
					
					if (ret) {
						this.cloudPurchaseOrderService.reduce_inventory(order,
								request);
					}
				}
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

		org.jdom.Document doc = builder.build(in);

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
