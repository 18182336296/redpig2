package com.redpigmall.pay.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.domain.virtual.SysMap;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.domain.CloudPurchaseOrder;
import com.redpigmall.domain.GoldRecord;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.Predeposit;
import com.redpigmall.service.RedPigCloudPurchaseOrderService;
import com.redpigmall.service.RedPigGoldRecordService;
import com.redpigmall.service.RedPigIntegralGoodsOrderService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigPaymentService;
import com.redpigmall.service.RedPigPredepositService;
import com.redpigmall.pay.alipay.config.AlipayConfig;
import com.redpigmall.pay.alipay.services.AlipayService;
import com.redpigmall.pay.bill.config.BillConfig;
import com.redpigmall.pay.bill.services.BillService;
import com.redpigmall.pay.bill.util.BillCore;
import com.redpigmall.pay.bill.util.MD5Util;
import com.redpigmall.pay.chinabank.util.ChinaBankSubmit;
import com.redpigmall.pay.paypal.PaypalTools;
import com.redpigmall.pay.unionpay.acp.sdk.LogUtil;
import com.redpigmall.pay.unionpay.acp.sdk.SDKConfig;
import com.redpigmall.pay.unionpay.acp.sdk.SDKUtil;

/**
 * 
 * <p>
 * Title: RedPigPayTools.java
 * </p>
 * 
 * <p>
 * Description: 支付方式处理工具类，用来管理支付方式信息，主要包括查询支付方式等
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
@Component
public class RedPigPayTools {
	@Autowired
	private RedPigPaymentService paymentService;
	@Autowired
	private RedPigOrderFormService orderFormService;
	@Autowired
	private RedPigOrderFormTools orderFormtools;
	@Autowired
	private RedPigPredepositService predepositService;
	@Autowired
	private RedPigGoldRecordService goldRecordService;
	@Autowired
	private RedPigIntegralGoodsOrderService integralGoodsOrderService;

	@Autowired
	private RedPigCloudPurchaseOrderService cloudPurchaseOrderService;

	public String genericAlipay(String url, String payment_id, String type,
			String id) {
		boolean submit = true;
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if ((type.equals("goods")) || (type.equals("group"))) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cash")) {
			pd = this.predepositService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (cp_order.getStatus() == 5) {
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.selectByPrimaryKey(CommUtil
					.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			int interfaceType = payment.getInterfaceType();
			AlipayConfig config = new AlipayConfig();
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", "alipay");

			List<Payment> payments = this.paymentService.queryPageList(params);

			Payment shop_payment = new Payment();
			if (payments.size() > 0) {
				shop_payment = (Payment) payments.get(0);
			}
			if ((!CommUtil.null2String(payment.getSafeKey()).equals(""))
					&& (!CommUtil.null2String(payment.getPartner()).equals(""))) {
				config.setKey(payment.getSafeKey());
				config.setPartner(payment.getPartner());
			} else {
				config.setKey(shop_payment.getSafeKey());
				config.setPartner(shop_payment.getPartner());
			}
			config.setSeller_email(payment.getSeller_email());
			config.setNotify_url(url + "/alipay_notify");
			config.setReturn_url(url + "/aplipay_return");

			if (interfaceType == 0) {
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss",
						new Date());
				if ((type.equals("goods")) || (type.equals("group"))) {
					of.setTrade_no("order-" + trade_no + "-"
							+ of.getId().toString());
					this.orderFormService.updateById(of);
					boolean flag = true;
					if (flag) {
						out_trade_no = "order-" + trade_no + "-"
								+ of.getId().toString();
					}
				}
				if (type.equals("cash")) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					this.predepositService.updateById(pd);
					boolean flag = true;
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-"
								+ pd.getId().toString();
					}
				}
				if (type.equals("gold")) {
					gold.setGold_sn("gold-" + trade_no + "-"
							+ gold.getId().toString());
					this.goldRecordService.updateById(gold);
					boolean flag = true;
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-"
								+ gold.getId().toString();
					}
				}
				if (type.equals("integral")) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ ig_order.getId().toString());
					this.integralGoodsOrderService.updateById(ig_order);
					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ ig_order.getId().toString();
					}
				}
				if (type.equals("cloudpurchase")) {
					cp_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ cp_order.getId().toString());
					this.cloudPurchaseOrderService.updateById(cp_order);
					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ cp_order.getId().toString();
					}
				}
				String subject = "";
				if ((type.equals("goods")) || (type.equals("group"))) {
					subject = of.getOrder_id();
				}
				if (type.equals("cash")) {
					subject = pd.getPd_sn();
				}
				if (type.equals("gold")) {
					subject = gold.getGold_sn();
				}
				if (type.equals("integral")) {
					subject = ig_order.getIgo_order_sn();
				}
				if (type.equals("cloudpurchase")) {
					subject = cp_order.getOdrdersn();
				}
				if (type.equals("store_deposit")) {
					subject = "store_deposit";
				}
				String body = type;

				String total_fee = "";
				if ((type.equals("goods")) || (type.equals("group"))) {
					double total_price = this.orderFormtools
							.query_order_pay_price(CommUtil.null2String(of
									.getId()));
					total_fee = CommUtil.null2String(Double
							.valueOf(total_price));
				}
				if (type.equals("cash")) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if (type.equals("gold")) {
					total_fee = CommUtil.null2String(Integer.valueOf(gold
							.getGold_money()));
				}
				if (type.equals("integral")) {
					total_fee = CommUtil.null2String(ig_order
							.getIgo_trans_fee());
				}
				if (type.equals("cloudpurchase")) {
					total_fee = CommUtil.null2String(Integer.valueOf(cp_order
							.getPrice()));
				}
				String paymethod = "";

				String defaultbank = "";

				String anti_phishing_key = "";

				String exter_invoke_ip = "";

				String extra_common_param = type;

				String buyer_email = "";

				String show_url = "";

				Map<String, String> sParaTemp = Maps.newHashMap();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("total_fee", total_fee);
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("paymethod", paymethod);
				sParaTemp.put("defaultbank", defaultbank);
				sParaTemp.put("anti_phishing_key", anti_phishing_key);
				sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("buyer_email", buyer_email);

				result = AlipayService.create_direct_pay_by_user(config,
						sParaTemp);
			}
			if (interfaceType == 1) {
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss",
						new Date());
				if ((type.equals("goods")) || (type.equals("group"))) {
					of.setTrade_no("order-" + trade_no + "-"
							+ of.getId().toString());
					this.orderFormService.updateById(of);
					boolean flag = true;
					if (flag) {
						out_trade_no = "order-" + trade_no + "-"
								+ of.getId().toString();
					}
				}
				if (type.equals("cash")) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					this.predepositService.updateById(pd);
					boolean flag = true;
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-"
								+ pd.getId().toString();
					}
				}
				if (type.equals("gold")) {
					gold.setGold_sn("gold-" + trade_no + "-"
							+ gold.getId().toString());
					this.goldRecordService.updateById(gold);
					boolean flag = true;
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-"
								+ gold.getId().toString();
					}
				}
				if (type.equals("integral")) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ ig_order.getId().toString());
					this.integralGoodsOrderService.updateById(ig_order);

					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ ig_order.getId().toString();
					}
				}
				if (type.equals("cloudpurchase")) {
					cp_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ cp_order.getId().toString());
					this.cloudPurchaseOrderService.updateById(cp_order);
					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ cp_order.getId().toString();
					}
				}
				String subject = "";
				if ((type.equals("goods")) || (type.equals("group"))) {
					subject = of.getOrder_id();
				}
				if (type.equals("cash")) {
					subject = pd.getPd_sn();
				}
				if (type.equals("gold")) {
					subject = gold.getGold_sn();
				}
				if (type.equals("integral")) {
					subject = ig_order.getIgo_order_sn();
				}
				if (type.equals("cloudpurchase")) {
					subject = cp_order.getOdrdersn();
				}
				if (type.equals("store_deposit")) {
					subject = "store_deposit";
				}
				String body = type;

				String total_fee = "";
				if ((type.equals("goods")) || (type.equals("group"))) {
					double total_price = this.orderFormtools
							.query_order_pay_price(CommUtil.null2String(of
									.getId()));
					total_fee = CommUtil.null2String(Double
							.valueOf(total_price));
				}
				if (type.equals("cash")) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if (type.equals("gold")) {
					total_fee = CommUtil.null2String(Integer.valueOf(gold
							.getGold_money()));
				}
				if (type.equals("integral")) {
					total_fee = CommUtil.null2String(ig_order
							.getIgo_trans_fee());
				}
				if (type.equals("cloudpurchase")) {
					total_fee = CommUtil.null2String(Integer.valueOf(cp_order
							.getPrice()));
				}
				if (type.equals("group")) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				String price = String.valueOf(total_fee);

				String logistics_fee = "0.00";

				String logistics_type = "EXPRESS";

				String logistics_payment = "SELLER_PAY";

				String quantity = "1";

				String extra_common_param = "";

				String receive_name = "";
				String receive_address = "";
				String receive_zip = "";
				String receive_phone = "";
				String receive_mobile = "";

				String show_url = "";

				Map<String, String> sParaTemp = Maps.newHashMap();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("price", price);
				sParaTemp.put("logistics_fee", logistics_fee);
				sParaTemp.put("logistics_type", logistics_type);
				sParaTemp.put("logistics_payment", logistics_payment);
				sParaTemp.put("quantity", quantity);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("receive_name", receive_name);
				sParaTemp.put("receive_address", receive_address);
				sParaTemp.put("receive_zip", receive_zip);
				sParaTemp.put("receive_phone", receive_phone);
				sParaTemp.put("receive_mobile", receive_mobile);

				result = AlipayService.create_partner_trade_by_buyer(config,
						sParaTemp);
			}
			if (interfaceType == 2) {
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss",
						new Date());
				if ((type.equals("goods")) || (type.equals("group"))) {
					of.setTrade_no("order-" + trade_no + "-"
							+ of.getId().toString());
					this.orderFormService.updateById(of);
					boolean flag = true;
					if (flag) {
						out_trade_no = "order-" + trade_no + "-"
								+ of.getId().toString();
					}
				}
				if (type.equals("cash")) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					this.predepositService.updateById(pd);
					boolean flag = true;
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-"
								+ pd.getId().toString();
					}
				}
				if (type.equals("gold")) {
					gold.setGold_sn("gold-" + trade_no + "-"
							+ gold.getId().toString());
					this.goldRecordService.updateById(gold);
					boolean flag = true;
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-"
								+ gold.getId().toString();
					}
				}
				if (type.equals("integral")) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ ig_order.getId().toString());
					this.integralGoodsOrderService.updateById(ig_order);

					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ ig_order.getId().toString();
					}
				}
				if (type.equals("cloudpurchase")) {
					cp_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ cp_order.getId().toString());
					this.cloudPurchaseOrderService.updateById(cp_order);
					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ cp_order.getId().toString();
					}
				}
				String subject = "";
				if ((type.equals("goods")) || (type.equals("group"))) {
					subject = of.getOrder_id();
				}
				if (type.equals("cash")) {
					subject = pd.getPd_sn();
				}
				if (type.equals("gold")) {
					subject = gold.getGold_sn();
				}
				if (type.equals("integral")) {
					subject = ig_order.getIgo_order_sn();
				}
				if (type.equals("cloudpurchase")) {
					subject = cp_order.getOdrdersn();
				}
				if (type.equals("store_deposit")) {
					subject = "store_deposit";
				}
				String body = type;

				String total_fee = "";
				if ((type.equals("goods")) || (type.equals("group"))) {
					double total_price = this.orderFormtools
							.query_order_pay_price(CommUtil.null2String(of
									.getId()));
					total_fee = CommUtil.null2String(Double
							.valueOf(total_price));
				}
				if (type.equals("cash")) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if (type.equals("gold")) {
					total_fee = CommUtil.null2String(Integer.valueOf(gold
							.getGold_money()));
				}
				if (type.equals("integral")) {
					total_fee = CommUtil.null2String(ig_order
							.getIgo_trans_fee());
				}
				if (type.equals("cloudpurchase")) {
					total_fee = CommUtil.null2String(Integer.valueOf(cp_order
							.getPrice()));
				}
				String price = String.valueOf(total_fee);

				String logistics_fee = "0.00";

				String logistics_type = "EXPRESS";

				String logistics_payment = "SELLER_PAY";

				String quantity = "1";

				String extra_common_param = "";

				String receive_name = "";
				String receive_address = "";
				String receive_zip = "";
				String receive_phone = "";
				String receive_mobile = "";

				String show_url = "";

				Map<String, String> sParaTemp = Maps.newHashMap();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("price", price);
				sParaTemp.put("logistics_fee", logistics_fee);
				sParaTemp.put("logistics_type", logistics_type);
				sParaTemp.put("logistics_payment", logistics_payment);
				sParaTemp.put("quantity", quantity);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("receive_name", receive_name);
				sParaTemp.put("receive_address", receive_address);
				sParaTemp.put("receive_zip", receive_zip);
				sParaTemp.put("receive_phone", receive_phone);
				sParaTemp.put("receive_mobile", receive_mobile);

				result = AlipayService.trade_create_by_buyer(config, sParaTemp);
			}
		} else {
			result = "该订单已经完成支付！";
		}
		return result;
	}

	public String generic99Bill(String url, String payment_id, String type,
			String id) throws UnsupportedEncodingException {
		boolean submit = true;
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if (type.equals("goods")) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cash")) {
			pd = this.predepositService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("group")) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (cp_order.getStatus() == 5) {
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.selectByPrimaryKey(CommUtil
					.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			BillConfig config = new BillConfig(payment.getMerchantAcctId(),
					payment.getRmbKey(), payment.getPid());

			String merchantAcctId = config.getMerchantAcctId();
			String key = config.getKey();
			String inputCharset = "1";
			String bgUrl = url + "/bill_notify_return";
			String pageUrl = url + "/bill_return";
			String version = "v2.0";
			String language = "1";
			String signType = "1";

			String payerName = SecurityUserHolder.getCurrentUser()
					.getUserName();

			String payerContactType = "1";

			String payerContact = "";

			String orderId = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if (type.equals("goods")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				this.orderFormService.updateById(of);
				boolean flag = true;
				if (flag) {
					orderId = "order-" + trade_no + "-" + of.getId().toString();
				}
			}
			if (type.equals("cash")) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				this.predepositService.updateById(pd);
				boolean flag = true;
				if (flag) {
					orderId = "pd-" + trade_no + "-" + pd.getId().toString();
				}
			}
			if (type.equals("gold")) {
				gold.setGold_sn("gold-" + trade_no + "-"
						+ gold.getId().toString());
				this.goldRecordService.updateById(gold);
				boolean flag = true;
				if (flag) {
					orderId = "gold-" + trade_no + "-"
							+ gold.getId().toString();
				}
			}
			if (type.equals("integral")) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ ig_order.getId().toString());
				this.integralGoodsOrderService.updateById(ig_order);
				boolean flag = true;
				if (flag) {
					orderId = "igo-" + trade_no + "-"
							+ ig_order.getId().toString();
				}
			}
			if (type.equals("cloudpurchase")) {
				cp_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ cp_order.getId().toString());
				this.cloudPurchaseOrderService.updateById(cp_order);
				boolean flag = true;
				if (flag) {
					orderId = "igo-" + trade_no + "-"
							+ cp_order.getId().toString();
				}
			}
			if (type.equals("group")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				this.orderFormService.updateById(of);
				boolean flag = true;
				if (flag) {
					orderId = "order-" + trade_no + "-" + of.getId().toString();
				}
			}
			String orderAmount = "";
			if (type.equals("goods")) {
				double total_price = this.orderFormtools
						.query_order_pay_price(CommUtil.null2String(of.getId()));
				orderAmount = String.valueOf((int) Math.floor(CommUtil
						.null2Double(Double.valueOf(total_price)) * 100.0D));
			}
			if (type.equals("cash")) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil
						.null2Double(pd.getPd_amount()) * 100.0D));
			}
			if (type.equals("gold")) {
				orderAmount = String
						.valueOf((int) Math.floor(CommUtil.null2Double(Integer
								.valueOf(gold.getGold_money())) * 100.0D));
			}
			if (type.equals("integral")) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil
						.null2Double(ig_order.getIgo_trans_fee()) * 100.0D));
			}
			if (type.equals("cloudpurchase")) {
				orderAmount = String
						.valueOf((int) Math.floor(CommUtil.null2Double(Integer
								.valueOf(cp_order.getPrice())) * 100.0D));
			}
			if (type.equals("group")) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil
						.null2Double(of.getTotalPrice()) * 100.0D));
			}
			String orderTime = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new Date());

			String productName = "";
			if (type.equals("goods")) {
				productName = of.getOrder_id();
			}
			if (type.equals("cash")) {
				productName = pd.getPd_sn();
			}
			if (type.equals("gold")) {
				productName = gold.getGold_sn();
			}
			if (type.equals("integral")) {
				productName = ig_order.getIgo_order_sn();
			}
			if (type.equals("cloudpurchase")) {
				productName = cp_order.getOdrdersn();
			}
			if (type.equals("store_deposit")) {
				productName = "store_deposit";
			}
			if (type.equals("group")) {
				productName = of.getOrder_id();
			}
			String productNum = "1";

			String productId = "";

			String productDesc = "";

			String ext1 = "";
			if (type.equals("goods")) {
				ext1 = of.getId().toString();
			}
			if (type.equals("cash")) {
				ext1 = pd.getId().toString();
			}
			if (type.equals("gold")) {
				ext1 = gold.getId().toString();
			}
			if (type.equals("integral")) {
				ext1 = ig_order.getId().toString();
			}
			if (type.equals("cloudpurchase")) {
				ext1 = cp_order.getId().toString();
			}
			if (type.equals("group")) {
				ext1 = of.getId().toString();
			}
			String ext2 = type;

			String payType = "00";

			String redoFlag = "0";

			String pid = "";
			if (config.getPid() != null) {
				pid = config.getPid();
			}
			String signMsgVal = "";
			signMsgVal = BillCore.appendParam(signMsgVal, "inputCharset",
					inputCharset);
			signMsgVal = BillCore.appendParam(signMsgVal, "pageUrl", pageUrl);
			signMsgVal = BillCore.appendParam(signMsgVal, "bgUrl", bgUrl);
			signMsgVal = BillCore.appendParam(signMsgVal, "version", version);
			signMsgVal = BillCore.appendParam(signMsgVal, "language", language);
			signMsgVal = BillCore.appendParam(signMsgVal, "signType", signType);
			signMsgVal = BillCore.appendParam(signMsgVal, "merchantAcctId",
					merchantAcctId);
			signMsgVal = BillCore.appendParam(signMsgVal, "payerName",
					payerName);
			signMsgVal = BillCore.appendParam(signMsgVal, "payerContactType",
					payerContactType);
			signMsgVal = BillCore.appendParam(signMsgVal, "payerContact",
					payerContact);
			signMsgVal = BillCore.appendParam(signMsgVal, "orderId", orderId);
			signMsgVal = BillCore.appendParam(signMsgVal, "orderAmount",
					orderAmount);
			signMsgVal = BillCore.appendParam(signMsgVal, "orderTime",
					orderTime);
			signMsgVal = BillCore.appendParam(signMsgVal, "productName",
					productName);
			signMsgVal = BillCore.appendParam(signMsgVal, "productNum",
					productNum);
			signMsgVal = BillCore.appendParam(signMsgVal, "productId",
					productId);
			signMsgVal = BillCore.appendParam(signMsgVal, "productDesc",
					productDesc);
			signMsgVal = BillCore.appendParam(signMsgVal, "ext1", ext1);
			signMsgVal = BillCore.appendParam(signMsgVal, "ext2", ext2);
			signMsgVal = BillCore.appendParam(signMsgVal, "payType", payType);
			signMsgVal = BillCore.appendParam(signMsgVal, "redoFlag", redoFlag);
			signMsgVal = BillCore.appendParam(signMsgVal, "pid", pid);
			signMsgVal = BillCore.appendParam(signMsgVal, "key", key);

			String signMsg = MD5Util.md5Hex(signMsgVal.getBytes("UTF-8"))
					.toUpperCase();

			Map<String, String> sParaTemp = Maps.newHashMap();
			sParaTemp.put("inputCharset", inputCharset);
			sParaTemp.put("pageUrl", pageUrl);
			sParaTemp.put("bgUrl", bgUrl);
			sParaTemp.put("version", version);
			sParaTemp.put("language", language);
			sParaTemp.put("signType", signType);
			sParaTemp.put("signMsg", signMsg);
			sParaTemp.put("merchantAcctId", merchantAcctId);
			sParaTemp.put("payerName", payerName);
			sParaTemp.put("payerContactType", payerContactType);
			sParaTemp.put("payerContact", payerContact);
			sParaTemp.put("orderId", orderId);
			sParaTemp.put("orderAmount", orderAmount);
			sParaTemp.put("orderTime", orderTime);
			sParaTemp.put("productName", productName);
			sParaTemp.put("productNum", productNum);
			sParaTemp.put("productId", productId);
			sParaTemp.put("productDesc", productDesc);
			sParaTemp.put("ext1", ext1);
			sParaTemp.put("ext2", ext2);
			sParaTemp.put("payType", payType);
			sParaTemp.put("redoFlag", redoFlag);
			sParaTemp.put("pid", pid);
			result = BillService.buildForm(config, sParaTemp, "post", "确定");
		} else {
			result = "该订单已经完成支付！";
		}
		return result;
	}

	public String genericChinaBank(String url, String payment_id, String type,
			String id) {
		boolean submit = true;
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if (type.equals("goods")) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cash")) {
			pd = this.predepositService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("group")) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (cp_order.getStatus() == 5) {
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.selectByPrimaryKey(CommUtil
					.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			List<SysMap> list = Lists.newArrayList();
			String v_mid = payment.getChinabank_account();
			list.add(new SysMap("v_mid", v_mid));
			String key = payment.getChinabank_key();
			list.add(new SysMap("key", key));
			String v_url = url + "/chinabank_return";
			list.add(new SysMap("v_url", v_url));
			String v_oid = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if (type.equals("goods")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				this.orderFormService.updateById(of);
				boolean flag = true;
				if (flag) {
					v_oid = "order-" + trade_no + "-" + of.getId().toString();
				}
			}
			if (type.equals("cash")) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				this.predepositService.updateById(pd);
				boolean flag = true;
				if (flag) {
					v_oid = "pd-" + trade_no + "-" + pd.getId().toString();
				}
			}
			if (type.equals("gold")) {
				gold.setGold_sn("gold-" + trade_no + "-"
						+ gold.getId().toString());
				this.goldRecordService.updateById(gold);
				boolean flag = true;
				if (flag) {
					v_oid = "gold-" + trade_no + "-" + gold.getId().toString();
				}
			}
			if (type.equals("integral")) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ ig_order.getId().toString());
				this.integralGoodsOrderService.updateById(ig_order);
				boolean flag = true;
				if (flag) {
					v_oid = "igo-" + trade_no + "-"
							+ ig_order.getId().toString();
				}
			}
			if (type.equals("cloudpurchase")) {
				cp_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ cp_order.getId().toString());
				this.cloudPurchaseOrderService.updateById(cp_order);
				boolean flag = true;
				if (flag) {
					v_oid = "igo-" + trade_no + "-"
							+ cp_order.getId().toString();
				}
			}
			if (type.equals("group")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				this.orderFormService.updateById(of);
				boolean flag = true;
				if (flag) {
					v_oid = "order-" + trade_no + "-" + of.getId().toString();
				}
			}
			list.add(new SysMap("v_oid", v_oid));
			String v_amount = "";
			if (type.equals("goods")) {
				double total_price = this.orderFormtools
						.query_order_pay_price(CommUtil.null2String(of.getId()));
				v_amount = CommUtil.null2String(Double.valueOf(total_price));
			}
			if (type.equals("cash")) {
				v_amount = CommUtil.null2String(pd.getPd_amount());
			}
			if (type.equals("gold")) {
				v_amount = CommUtil.null2String(Integer.valueOf(gold
						.getGold_money()));
			}
			if (type.equals("integral")) {
				v_amount = CommUtil.null2String(ig_order.getIgo_trans_fee());
			}
			if (type.equals("cloudpurchase")) {
				v_amount = CommUtil.null2String(Integer.valueOf(cp_order
						.getPrice()));
			}
			if (type.equals("group")) {
				v_amount = CommUtil.null2String(of.getTotalPrice());
			}
			list.add(new SysMap("v_amount", v_amount));
			String v_moneytype = "CNY";
			list.add(new SysMap("v_moneytype", v_moneytype));
			String temp = v_amount + v_moneytype + v_oid + v_mid + v_url + key;
			String v_md5info = Md5Encrypt.md5(temp).toUpperCase();
			list.add(new SysMap("v_md5info", v_md5info));

			String remark1 = "";
			if (type.equals("goods")) {
				remark1 = of.getId().toString();
			}
			if (type.equals("cash")) {
				remark1 = pd.getId().toString();
			}
			if (type.equals("gold")) {
				remark1 = gold.getId().toString();
			}
			if (type.equals("integral")) {
				remark1 = ig_order.getId().toString();
			}
			if (type.equals("cloudpurchase")) {
				remark1 = cp_order.getId().toString();
			}
			if (type.equals("group")) {
				remark1 = of.getId().toString();
			}
			list.add(new SysMap("remark1", remark1));
			String remark2 = type;
			list.add(new SysMap("remark2", remark2));
			result = ChinaBankSubmit.buildForm(list);
		} else {
			result = "该订单已经完成支付！";
		}
		return result;
	}

	public String genericPaypal(String url, String payment_id, String type,
			String id) {
		boolean submit = true;
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;
		if (type.equals("goods")) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cash")) {
			pd = this.predepositService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("group")) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (cp_order.getStatus() == 5) {
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.selectByPrimaryKey(CommUtil
					.null2Long(payment_id));
			if (payment == null) {
				payment = new Payment();
			}
			List<SysMap> sms = Lists.newArrayList();
			String business = payment.getPaypal_userId();
			sms.add(new SysMap("business", business));
			String return_url = url + "/paypal_return";
			String notify_url = url + "/paypal_return";
			sms.add(new SysMap("return", return_url));
			String item_name = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			if (type.equals("goods")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				this.orderFormService.updateById(of);
				boolean flag = true;
				if (flag) {
					item_name = "order-" + trade_no + "-"
							+ of.getId().toString();
				}
			}
			if (type.equals("cash")) {
				pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
				this.predepositService.updateById(pd);
				boolean flag = true;
				if (flag) {
					item_name = "pd-" + trade_no + "-" + pd.getId().toString();
				}
			}
			if (type.equals("gold")) {
				gold.setGold_sn("gold-" + trade_no + "-"
						+ gold.getId().toString());
				this.goldRecordService.updateById(gold);
				boolean flag = true;
				if (flag) {
					item_name = "gold-" + trade_no + "-"
							+ gold.getId().toString();
				}
			}
			if (type.equals("integral")) {
				ig_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ ig_order.getId().toString());
				this.integralGoodsOrderService.updateById(ig_order);
				boolean flag = true;
				if (flag) {
					item_name = "igo-" + trade_no + "-"
							+ ig_order.getId().toString();
				}
			}
			if (type.equals("group")) {
				of.setTrade_no("order-" + trade_no + "-"
						+ of.getId().toString());
				this.orderFormService.updateById(of);
				boolean flag = true;
				if (flag) {
					item_name = "order-" + trade_no + "-"
							+ of.getId().toString();
				}
			}
			if (type.equals("cloudpurchase")) {
				cp_order.setIgo_order_sn("igo-" + trade_no + "-"
						+ cp_order.getId().toString());
				this.cloudPurchaseOrderService.updateById(cp_order);
				boolean flag = true;
				if (flag) {
					item_name = "igo-" + trade_no + "-"
							+ cp_order.getId().toString();
				}
			}
			sms.add(new SysMap("item_name", item_name));
			String amount = "";
			String item_number = "";
			if (type.equals("goods")) {
				double total_price = this.orderFormtools
						.query_order_pay_price(CommUtil.null2String(of.getId()));
				amount = CommUtil.null2String(Double.valueOf(total_price));
				item_number = of.getOrder_id();
			}
			if (type.equals("cash")) {
				amount = CommUtil.null2String(pd.getPd_amount());
				item_number = pd.getPd_sn();
			}
			if (type.equals("gold")) {
				amount = CommUtil.null2String(Integer.valueOf(gold
						.getGold_money()));
				item_number = gold.getGold_sn();
			}
			if (type.equals("integral")) {
				amount = CommUtil.null2String(ig_order.getIgo_trans_fee());
				item_number = ig_order.getIgo_order_sn();
			}
			if (type.equals("group")) {
				amount = CommUtil.null2String(of.getTotalPrice());
				item_number = of.getOrder_id();
			}
			if (type.equals("cloudpurchase")) {
				amount = CommUtil.null2String(Integer.valueOf(cp_order
						.getPrice()));
				item_number = cp_order.getIgo_order_sn();
			}
			sms.add(new SysMap("amount", amount));
			sms.add(new SysMap("notify_url", notify_url));
			sms.add(new SysMap("cmd", "_xclick"));
			sms.add(new SysMap("currency_code", payment.getCurrency_code()));
			sms.add(new SysMap("item_number", item_number));

			String custom = "";
			if (type.equals("goods")) {
				custom = of.getId().toString();
			}
			if (type.equals("cash")) {
				custom = pd.getId().toString();
			}
			if (type.equals("gold")) {
				custom = gold.getId().toString();
			}
			if (type.equals("integral")) {
				custom = ig_order.getId().toString();
			}
			if (type.equals("group")) {
				custom = of.getId().toString();
			}
			if (type.equals("cloudpurchase")) {
				custom = cp_order.getId().toString();
			}
			custom = custom + "," + type;
			sms.add(new SysMap("custom", custom));
			result = PaypalTools.buildForm(sms);
		} else {
			result = "该订单已经完成支付！";
		}
		return result;
	}

	public String genericAlipayWap(String url, String payment_id, String type,
			String id) throws Exception {
		boolean submit = true;// 是否继续提交支付，防止订单重复支付，pc端打开支付页面，另外一个人用app完成了支付
		String result = "";
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		if (type.equals("goods")) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {// 订单已经处于支付状态
				submit = false;
			}
		}
		if (type.equals("cash")) {
			pd = this.predepositService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
				submit = false;// 预存款已经完成充值
			}
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false;// 金币已经完成充值
			}
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false;// 积分订单已经完成支付
			}
		}
		if (type.equals("group")) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {// 团购订单已经处于支付状态
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.selectByPrimaryKey(CommUtil
					.null2Long(payment_id));
			if (payment == null)
				payment = new Payment();
			int interfaceType = payment.getInterfaceType();
			AlipayConfig config = new AlipayConfig();
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", "alipay");
			List<Payment> payments = this.paymentService.queryPageList(params);

			Payment shop_payment = new Payment();
			if (payments.size() > 0) {
				shop_payment = payments.get(0);
			}
			if (!CommUtil.null2String(payment.getSafeKey()).equals("")
					&& !CommUtil.null2String(payment.getPartner()).equals("")) {
				config.setKey(payment.getSafeKey());
				config.setPartner(payment.getPartner());
			} else {
				config.setKey(shop_payment.getSafeKey());
				config.setPartner(shop_payment.getPartner());
			}
			config.setSeller_email(payment.getSeller_email());
			config.setNotify_url(url + "/alipay_notify");
			config.setReturn_url(url + "/aplipay_return");

			if (interfaceType == 0) {// 及时到账支付
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss",
						new Date());
				if (type.equals("goods") || type.equals("group")) {
					of.setTrade_no("order-" + trade_no + "-"
							+ of.getId().toString());
					this.orderFormService.updateById(of);// 更新订单流水号
					boolean flag = true;
					if (flag) {
						out_trade_no = "order-" + trade_no + "-"
								+ of.getId().toString();
					}
				}

				if (type.equals("cash")) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					this.predepositService.updateById(pd);
					boolean flag = true;
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-"
								+ pd.getId().toString();
					}
				}
				if (type.equals("gold")) {
					gold.setGold_sn("gold-" + trade_no + "-"
							+ gold.getId().toString());
					this.goldRecordService.updateById(gold);
					boolean flag = true;
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-"
								+ gold.getId().toString();
					}
				}
				if (type.equals("integral")) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ ig_order.getId().toString());
					this.integralGoodsOrderService.updateById(ig_order);
					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ ig_order.getId().toString();
					}
				}
				// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
				String subject = "";//
				if (type.equals("goods")) {
					subject = of.getOrder_id();
				}
				if (type.equals("cash")) {
					subject = pd.getPd_sn();
				}
				if (type.equals("gold")) {
					subject = gold.getGold_sn();
				}
				if (type.equals("integral")) {
					subject = ig_order.getIgo_order_sn();
				}
				if (type.equals("store_deposit")) {
					subject = "store_deposit";
				}
				if (type.equals("group")) {
					subject = of.getOrder_id();
				}
				// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
				String body = type;
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String total_fee = "";//
				if (type.equals("goods")) {
					double total_price = this.orderFormtools
							.query_order_price(CommUtil.null2String(of.getId()));
					total_fee = CommUtil.null2String(total_price);
				}
				if (type.equals("cash")) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if (type.equals("gold")) {
					total_fee = CommUtil.null2String(gold.getGold_money());
				}
				if (type.equals("integral")) {
					total_fee = CommUtil.null2String(ig_order
							.getIgo_trans_fee());
				}
				if (type.equals("group")) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				// 扩展功能参数——默认支付方式//
				// 默认支付方式，取值见“即时到帐接口”技术文档中的请求参数列表
				String paymethod = "";
				// 默认网银代号，代号列表见“即时到帐接口”技术文档“附录”→“银行列表”
				String defaultbank = "";
				// 扩展功能参数——防钓鱼//
				// 防钓鱼时间戳
				String anti_phishing_key = "";
				// 获取客户端的IP地址，建议：编写获取客户端IP地址的程序
				String exter_invoke_ip = "";
				// 注意：
				// 1.请慎重选择是否开启防钓鱼功能
				// 2.exter_invoke_ip、anti_phishing_key一旦被设置过，那么它们就会成为必填参数
				// 3.开启防钓鱼功能后，服务器、本机电脑必须支持远程XML解析，请配置好该环境。
				// 4.建议使用POST方式请求数据
				// 示例：
				// anti_phishing_key = AlipayService.query_timestamp();
				// //获取防钓鱼时间戳函数
				// exter_invoke_ip = "202.1.1.1";

				// 扩展功能参数——其他///

				// 自定义参数，可存放任何内容（除=、&等特殊字符外），不会显示在页面上
				String extra_common_param = type;
				// 默认买家支付宝账号
				String buyer_email = "";
				// 商品展示地址，要用http:// 格式的完整路径，不允许加?id=123这类自定义参数
				String show_url = "";
				// 扩展功能参数——分润(若要使用，请按照注释要求的格式赋值)//s
				// 提成类型，该值为固定值：10，不需要修改
				// String royalty_type = "10";
				// 减去支付宝手续费
				// 提成信息集
				// String royalty_parameters = "";
				// 注意：
				// 与需要结合商户网站自身情况动态获取每笔交易的各分润收款账号、各分润金额、各分润说明。最多只能设置10条
				// 各分润金额的总和须小于等于total_fee
				// 提成信息集格式为：收款方Email_1^金额1^备注1|收款方Email_2^金额2^备注2
				// 把请求参数打包成数组
				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("total_fee", total_fee);
				sParaTemp.put("app_pay","Y");//启用此参数可唤起钱包APP支付
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("paymethod", paymethod);
				sParaTemp.put("defaultbank", defaultbank);
				sParaTemp.put("anti_phishing_key", anti_phishing_key);
				sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("buyer_email", buyer_email);
				// 构造函数，生成请求URL
				result = AlipayService.create_direct_pay_by_user(config,
						sParaTemp);
			}
			if (interfaceType == 1) {// 担保支付接口
				// 请与贵网站订单系统中的唯一订单号匹配
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss",
						new Date());
				if (type.equals("goods")) {
					of.setTrade_no("order-" + trade_no + "-"
							+ of.getId().toString());
					this.orderFormService.updateById(of);// 更新订单流水号
					boolean flag = true;
					if (flag) {
						out_trade_no = "order-" + trade_no + "-"
								+ of.getId().toString();
					}
				}

				if (type.equals("cash")) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					this.predepositService.updateById(pd);
					boolean flag = true;
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-"
								+ pd.getId().toString();
					}
				}
				if (type.equals("gold")) {
					gold.setGold_sn("gold-" + trade_no + "-"
							+ gold.getId().toString());
					this.goldRecordService.updateById(gold);
					boolean flag = true;
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-"
								+ gold.getId().toString();
					}
				}
				if (type.equals("integral")) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ ig_order.getId().toString());
					this.integralGoodsOrderService.updateById(ig_order);
					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ ig_order.getId().toString();
					}
				}

				if (type.equals("group")) {
					of.setTrade_no("order-" + trade_no + "-"
							+ of.getId().toString());
					this.orderFormService.updateById(of);// 更新订单流水号
					boolean flag = true;
					if (flag) {
						out_trade_no = "order-" + trade_no + "-"
								+ of.getId().toString();
					}
				}
				// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
				String subject = "";//
				if (type.equals("goods")) {
					subject = of.getOrder_id();
				}
				if (type.equals("cash")) {
					subject = pd.getPd_sn();
				}
				if (type.equals("gold")) {
					subject = gold.getGold_sn();
				}
				if (type.equals("integral")) {
					subject = ig_order.getIgo_order_sn();
				}
				if (type.equals("store_deposit")) {
					subject = "store_deposit";
				}
				if (type.equals("group")) {
					subject = of.getOrder_id();
				}
				// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
				String body = type;
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String total_fee = "";//
				if (type.equals("goods")) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				if (type.equals("cash")) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if (type.equals("gold")) {
					total_fee = CommUtil.null2String(gold.getGold_money());
				}
				if (type.equals("integral")) {
					total_fee = CommUtil.null2String(ig_order
							.getIgo_trans_fee());
				}
				if (type.equals("group")) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String price = String.valueOf(total_fee);
				// 物流费用，即运费。
				String logistics_fee = "0.00";
				// 物流类型，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
				String logistics_type = "EXPRESS";
				// 物流支付方式，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
				String logistics_payment = "SELLER_PAY";
				// 商品数量，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品。
				String quantity = "1";
				// 扩展参数//
				// 自定义参数，可存放任何内容（除=、&等特殊字符外），不会显示在页面上
				String extra_common_param = "";
				// 买家收货信息（推荐作为必填）
				// 该功能作用在于买家已经在商户网站的下单流程中填过一次收货信息，而不需要买家在支付宝的付款流程中再次填写收货信息。
				// 若要使用该功能，请至少保证receive_name、receive_address有值
				String receive_name = "";
				String receive_address = "";
				String receive_zip = "";
				String receive_phone = ""; // 收货人电话号码
				String receive_mobile = "";
				// 网站商品的展示地址，不允许加?id=123这类自定义参数
				String show_url = "";
				// 把请求参数打包成数组
				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("price", price);
				sParaTemp.put("logistics_fee", logistics_fee);
				sParaTemp.put("logistics_type", logistics_type);
				sParaTemp.put("logistics_payment", logistics_payment);
				sParaTemp.put("quantity", quantity);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("receive_name", receive_name);
				sParaTemp.put("receive_address", receive_address);
				sParaTemp.put("receive_zip", receive_zip);
				sParaTemp.put("receive_phone", receive_phone);
				sParaTemp.put("receive_mobile", receive_mobile);

				// 构造函数，生成请求URL
				result = AlipayService.create_partner_trade_by_buyer(config,
						sParaTemp);
			}
			if (interfaceType == 2) {// 标准双接口
				// 请与贵网站订单系统中的唯一订单号匹配
				String out_trade_no = "";
				String trade_no = CommUtil.formatTime("yyyyMMddHHmmss",
						new Date());
				if (type.equals("goods")) {
					of.setTrade_no("order-" + trade_no + "-"
							+ of.getId().toString());
					this.orderFormService.updateById(of);// 更新订单流水号
					boolean flag = true;
					if (flag) {
						out_trade_no = "order-" + trade_no + "-"
								+ of.getId().toString();
					}
				}

				if (type.equals("cash")) {
					pd.setPd_no("pd-" + trade_no + "-" + pd.getId().toString());
					this.predepositService.updateById(pd);
					boolean flag = true;
					if (flag) {
						out_trade_no = "pd-" + trade_no + "-"
								+ pd.getId().toString();
					}
				}
				if (type.equals("gold")) {
					gold.setGold_sn("gold-" + trade_no + "-"
							+ gold.getId().toString());
					this.goldRecordService.updateById(gold);
					boolean flag = true;
					if (flag) {
						out_trade_no = "gold-" + trade_no + "-"
								+ gold.getId().toString();
					}
				}
				if (type.equals("integral")) {
					ig_order.setIgo_order_sn("igo-" + trade_no + "-"
							+ ig_order.getId().toString());
					this.integralGoodsOrderService.updateById(ig_order);
					boolean flag = true;
					if (flag) {
						out_trade_no = "igo-" + trade_no + "-"
								+ ig_order.getId().toString();
					}
				}
				if (type.equals("group")) {
					of.setTrade_no("order-" + trade_no + "-"
							+ of.getId().toString());
					this.orderFormService.updateById(of);// 更新订单流水号
					boolean flag = true;
					if (flag) {
						out_trade_no = "order-" + trade_no + "-"
								+ of.getId().toString();
					}
				}
				// 订单名称，显示在支付宝收银台里的“商品名称”里，显示在支付宝的交易管理的“商品名称”的列表里。
				String subject = "";//
				if (type.equals("goods")) {
					subject = of.getOrder_id();
				}
				if (type.equals("cash")) {
					subject = pd.getPd_sn();
				}
				if (type.equals("gold")) {
					subject = gold.getGold_sn();
				}
				if (type.equals("integral")) {
					subject = ig_order.getIgo_order_sn();
				}
				if (type.equals("store_deposit")) {
					subject = "store_deposit";
				}
				if (type.equals("group")) {
					subject = of.getOrder_id();
				}
				// 订单描述、订单详细、订单备注，显示在支付宝收银台里的“商品描述”里
				String body = type;
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String total_fee = "";//
				if (type.equals("goods")) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				if (type.equals("cash")) {
					total_fee = CommUtil.null2String(pd.getPd_amount());
				}
				if (type.equals("gold")) {
					total_fee = CommUtil.null2String(gold.getGold_money());
				}
				if (type.equals("integral")) {
					total_fee = CommUtil.null2String(ig_order
							.getIgo_trans_fee());
				}
				if (type.equals("group")) {
					total_fee = CommUtil.null2String(of.getTotalPrice());
				}
				// 订单总金额，显示在支付宝收银台里的“应付总额”里
				String price = String.valueOf(total_fee);

				// 物流费用，即运费。
				String logistics_fee = "0.00";
				// 物流类型，三个值可选：EXPRESS（快递）、POST（平邮）、EMS（EMS）
				String logistics_type = "EXPRESS";
				// 物流支付方式，两个值可选：SELLER_PAY（卖家承担运费）、BUYER_PAY（买家承担运费）
				String logistics_payment = "SELLER_PAY";

				// 商品数量，建议默认为1，不改变值，把一次交易看成是一次下订单而非购买一件商品。
				String quantity = "1";
				// 买家收货信息（推荐作为必填）
				String extra_common_param = "";
				// 该功能作用在于买家已经在商户网站的下单流程中填过一次收货信息，而不需要买家在支付宝的付款流程中再次填写收货信息。
				// 若要使用该功能，请至少保证receive_name、receive_address有值
				String receive_name = "";
				String receive_address = "";
				String receive_zip = "";
				String receive_phone = ""; // 收货人电话号码，如：0571-81234567
				String receive_mobile = "";
				// 网站商品的展示地址，不允许加?id=123这类自定义参数
				String show_url = "";
				// 把请求参数打包成数组
				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("payment_type", "1");
				sParaTemp.put("show_url", show_url);
				sParaTemp.put("out_trade_no", out_trade_no);
				sParaTemp.put("subject", subject);
				sParaTemp.put("body", body);
				sParaTemp.put("price", price);
				sParaTemp.put("logistics_fee", logistics_fee);
				sParaTemp.put("logistics_type", logistics_type);
				sParaTemp.put("logistics_payment", logistics_payment);
				sParaTemp.put("quantity", quantity);
				sParaTemp.put("extra_common_param", extra_common_param);
				sParaTemp.put("receive_name", receive_name);
				sParaTemp.put("receive_address", receive_address);
				sParaTemp.put("receive_zip", receive_zip);
				sParaTemp.put("receive_phone", receive_phone);
				sParaTemp.put("receive_mobile", receive_mobile);
				// 构造函数，生成请求URL
				result = AlipayService.trade_create_by_buyer(config, sParaTemp);
			}
		} else {
			result = "该订单已经完成支付！";
		}

		return result;
	}

	public String genericUnionpay(String url, String payment_id, String type,
			String id, String payType) throws IOException {
		boolean submit = true;
		OrderForm of = null;
		Predeposit pd = null;
		GoldRecord gold = null;
		IntegralGoodsOrder ig_order = null;
		CloudPurchaseOrder cp_order = null;

		String html = "";
		if ((type.equals("goods")) || (type.equals("group"))) {
			of = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (of.getOrder_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cash")) {
			pd = this.predepositService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (pd.getPd_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("gold")) {
			gold = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (gold.getGold_pay_status() >= 2) {
				submit = false;
			}
		}
		if (type.equals("integral")) {
			ig_order = this.integralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ig_order.getIgo_status() >= 20) {
				submit = false;
			}
		}
		if (type.equals("cloudpurchase")) {
			cp_order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (cp_order.getStatus() == 5) {
				submit = false;
			}
		}
		if (submit) {
			Payment payment = this.paymentService.selectByPrimaryKey(CommUtil
					.null2Long(payment_id));
			String orderId = "";
			String trade_no = CommUtil.formatTime("yyyyMMddHHmmss", new Date());
			String order_id = "";
			if (type.equals("goods")) {
				of.setTrade_no("order" + trade_no + of.getId().toString());
				this.orderFormService.updateById(of);
				boolean flag = true;
				if (flag) {
					orderId = "order" + trade_no + of.getId().toString();
					order_id = CommUtil.null2String(of.getId());
				}
			}
			if (type.equals("cash")) {
				pd.setPd_no("pd" + trade_no + pd.getId().toString());
				this.predepositService.updateById(pd);
				boolean flag = true;
				if (flag) {
					orderId = "pd" + trade_no + pd.getId().toString();
					order_id = CommUtil.null2String(pd.getId());
				}
			}
			if (type.equals("gold")) {
				gold.setGold_sn("gold" + trade_no + gold.getId().toString());
				this.goldRecordService.updateById(gold);
				boolean flag = true;
				if (flag) {
					orderId = "gold" + trade_no + gold.getId().toString();
					order_id = CommUtil.null2String(gold.getId());
				}
			}
			if (type.equals("integral")) {
				ig_order.setIgo_order_sn("igo" + trade_no
						+ ig_order.getId().toString());
				this.integralGoodsOrderService.updateById(ig_order);
				boolean flag = true;
				if (flag) {
					orderId = "igo" + trade_no + ig_order.getId().toString();
					order_id = CommUtil.null2String(ig_order.getId());
				}
			}
			if (type.equals("cloudpurchase")) {
				cp_order.setIgo_order_sn("igo" + trade_no
						+ cp_order.getId().toString());
				this.cloudPurchaseOrderService.updateById(cp_order);
				boolean flag = true;
				if (flag) {
					orderId = "igo" + trade_no + cp_order.getId().toString();
					order_id = CommUtil.null2String(cp_order.getId());
				}
			}
			if (type.equals("group")) {
				of.setTrade_no("order" + trade_no + of.getId().toString());
				this.orderFormService.updateById(of);
				boolean flag = true;
				if (flag) {
					orderId = "order" + trade_no + of.getId().toString();
					order_id = CommUtil.null2String(of.getId());
				}
			}
			String orderAmount = "";
			if (type.equals("goods")) {
				double total_price = this.orderFormtools
						.query_order_pay_price(CommUtil.null2String(of.getId()));
				orderAmount = String.valueOf((int) Math.floor(CommUtil
						.null2Double(Double.valueOf(total_price)) * 100.0D));
			}
			if (type.equals("cash")) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil
						.null2Double(pd.getPd_amount()) * 100.0D));
			}
			if (type.equals("gold")) {
				orderAmount = String
						.valueOf((int) Math.floor(CommUtil.null2Double(Integer
								.valueOf(gold.getGold_money())) * 100.0D));
			}
			if (type.equals("integral")) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil
						.null2Double(ig_order.getIgo_trans_fee()) * 100.0D));
			}
			if (type.equals("cloudpurchase")) {
				orderAmount = String
						.valueOf((int) Math.floor(CommUtil.null2Double(Integer
								.valueOf(cp_order.getPrice())) * 100.0D));
			}
			if (type.equals("group")) {
				orderAmount = String.valueOf((int) Math.floor(CommUtil
						.null2Double(of.getTotalPrice()) * 100.0D));
			}
			String channelType = "07";
			String backUrl = url + "/unionpay_notify";
			String frontUrl = url + "/unionpay_return";
			if ("wap".equals(payType)) {
				channelType = "08";
				backUrl = url + "/wap/unionpay_notify";
				frontUrl = url + "/wap/unionpay_return";
			}
			Map<String, String> requestData = Maps.newHashMap();

			requestData.put("version", SDKUtil.version);
			requestData.put("encoding", SDKUtil.encoding_UTF8);
			requestData.put("signMethod", "01");
			requestData.put("txnType", "01");
			requestData.put("txnSubType", "01");
			requestData.put("bizType", "000201");
			requestData.put("channelType", channelType);

			requestData.put("merId", payment.getUnionpay_merId());
			requestData.put("accessType", "0");
			requestData.put("orderId", orderId);
			requestData.put("txnTime",
					new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			requestData.put("currencyCode", "156");
			requestData.put("txnAmt", orderAmount);
			requestData.put("reqReserved", type + "_" + order_id);

			requestData.put("backUrl", backUrl);

			requestData.put("frontUrl", frontUrl);

			Map<String, String> submitFromData = SDKUtil.signData(requestData,
					SDKUtil.encoding_UTF8);

			String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();
			html = SDKUtil.createAutoFormHtml(requestFrontUrl, submitFromData,
					SDKUtil.encoding_UTF8);

			LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据：" + html);
		} else {
			return "订单已完成支付";
		}
		return html;
	}
}
