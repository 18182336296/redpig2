package com.redpigmall.logic.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.redpigmall.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.domain.virtual.SysMap;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.logic.service.RedPigHandleOrderFormService;
import com.redpigmall.manage.admin.tools.RedPigCartTools;
import com.redpigmall.manage.admin.tools.RedPigGoodsTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigShipTools;
import com.redpigmall.manage.delivery.tools.RedPigDeliveryAddressTools;
import com.redpigmall.manage.seller.tools.RedPigTransportTools;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigActivityGoodsService;
import com.redpigmall.service.RedPigActivityService;
import com.redpigmall.service.RedPigAddressService;
import com.redpigmall.service.RedPigAreaService;
import com.redpigmall.service.RedPigBuyGiftService;
import com.redpigmall.service.RedPigCombinPlanService;
import com.redpigmall.service.RedPigCouponInfoService;
import com.redpigmall.service.RedPigDeliveryAddressService;
import com.redpigmall.service.RedPigEnoughReduceService;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.RedPigExpressCompanyCommonService;
import com.redpigmall.service.RedPigExpressInfoService;
import com.redpigmall.service.RedPigFavoriteService;
import com.redpigmall.service.RedPigFreeGoodsService;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsLogService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigGoodsSpecPropertyService;
import com.redpigmall.service.RedPigGroupGoodsService;
import com.redpigmall.service.RedPigGroupInfoService;
import com.redpigmall.service.RedPigGroupLifeGoodsService;
import com.redpigmall.service.RedPigGroupService;
import com.redpigmall.service.RedPigIntegralGoodsCartService;
import com.redpigmall.service.RedPigIntegralLogService;
import com.redpigmall.service.RedPigMessageService;
import com.redpigmall.service.RedPigOrderFormLogService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigPaymentService;
import com.redpigmall.service.RedPigPayoffLogService;
import com.redpigmall.service.RedPigPredepositLogService;
import com.redpigmall.service.RedPigReturnGoodsLogService;
import com.redpigmall.service.RedPigShipAddressService;
import com.redpigmall.service.RedPigStoreHouseService;
import com.redpigmall.service.RedPigStorePointService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigStoreStatService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigUserService;
import com.redpigmall.service.RedPigVatInvoiceService;
import com.redpigmall.service.RedPigVerifyCodeService;
import com.redpigmall.pay.alipay.config.AlipayConfig;
import com.redpigmall.pay.alipay.util.AlipaySubmit;
import com.redpigmall.pay.tools.RedPigPayTools;
import com.redpigmall.view.web.tools.RedPigAreaViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigStoreViewTools;

/**
 * <p>
 * Title: RedPigHandleOrderFormServiceImpl.java
 * </p>
 * 
 * <p>
 * Description: 处理订单的业务类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
@Service
@Transactional
public class RedPigHandleOrderFormService {
	
	@Autowired
	private RedPigOrderFormService orderFormDAO;
	
	@Autowired
	private RedPigOrderFormLogService orderFormLogDao;
	
	@Autowired
	private RedPigPredepositLogService PredepositLogDAO;
	
	@Autowired
	private RedPigSysConfigService sysConfigDAO;
	
	@Autowired
	private RedPigGoodsService goodsDAO;
	
	@Autowired
	private RedPigGoodsLogService goodsLogDAO;
	
	@Autowired
	private RedPigStoreService storeDAO;
	
	@Autowired
	private RedPigUserService userDAO;
	
	@Autowired
	private RedPigMessageService messageDAO;
	
	@Autowired
	private RedPigGoodsCartService goodsCartDAO;
	
	@Autowired
	private RedPigGoodsSpecPropertyService goodsSpecPropertyDao;
	@Autowired
	private RedPigPayoffLogService payoffLogDAO;
	
	@Autowired
	private RedPigGroupLifeGoodsService groupLifeGoodsDAO;
	
	@Autowired
	private RedPigGroupGoodsService groupGoodsDAO;
	
	@Autowired
	private RedPigGroupInfoService groupInfoDAO;
	
	@Autowired
	private RedPigOrderFormLogService orderFormLogDAO;
	
	@Autowired
	private RedPigCouponInfoService couponInfoDAO;
	
	@Autowired
	private RedPigFreeGoodsService freeGoodsDAO;
	
	@Autowired
	private RedPigCombinPlanService combinPlanDAO;
	
	@Autowired
	private RedPigBuyGiftService buyGiftDAO;
	
	@Autowired
	private RedPigIntegralGoodsCartService integralGoodsCartDAO;
	
	@Autowired
	private RedPigEnoughReduceService enoughReduceDAO;
	
	@Autowired
	private RedPigStoreStatService storeStatDAO;
	
	@Autowired
	private RedPigVerifyCodeService mobileVerifyCodeDAO;
	
	@Autowired
	private RedPigGoodsClassService goodsClassDAO;
	
	@Autowired
	private RedPigStorePointService storePointDAO;
	
	@Autowired
	private RedPigGroupService groupDAO;
	
	@Autowired
	private RedPigActivityService activityDAO;
	
	@Autowired
	private RedPigActivityGoodsService activityGoodsDAO;
	
	@Autowired
	private RedPigReturnGoodsLogService returnGoodsLogDAO;
	
	@Autowired
	private RedPigEvaluateService evaluateDAO;
	
	@Autowired
	private RedPigAccessoryService accessoryDAO;
	
	@Autowired
	private RedPigFavoriteService favoriteDAO;
	
	@Autowired
	private RedPigIntegralLogService integralLogDao;
	
	@Autowired
	private RedPigPaymentService paymentDao;
	
	@Autowired
	private RedPigExpressCompanyCommonService expresscompanyCommDao;
	
	@Autowired
	private RedPigShipAddressService shipAddressDao;
	
	@Autowired
	private RedPigAreaService areaDAO;
	
	@Autowired
	private RedPigExpressInfoService expressInfoDAO;
	
	@Autowired
	private RedPigDeliveryAddressService deliveryAddressDAO;
	
	@Autowired
	private RedPigAddressService addressDAO;
	
	@Autowired
	private RedPigStoreHouseService storeHouseDAO;
	
	@Autowired
	private RedPigVatInvoiceService vatInvoiceDAO;
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigAreaViewTools areaViewTools;
	@Autowired
	private RedPigGoodsTools goodsTools;
	@Autowired
	private RedPigPayTools payTools;
	@Autowired
	private RedPigTransportTools transportTools;
	@Autowired
	private RedPigGoodsViewTools goodsViewTools;
	@Autowired
	private RedPigStoreViewTools storeViewTools;
	@Autowired
	private RedPigDeliveryAddressTools deliveryAddressTools;
	@Autowired
	private RedPigCartTools cartTools;
	@Autowired
	private RedPigOrderFormTools orderFormTools;
	@Autowired
	private RedPigShipTools ShipTools;
	@Autowired
	private RedPigMsgTools msgTools;
	
	/**
	 * 
	 * 余额支付
	 * @see com.redpigmall.logic.service.RedPigHandleOrderFormService#payByBalance(com.redpigmall.domain.OrderForm, java.lang.String, java.lang.String)
	 */
	@Transactional(rollbackFor = { Exception.class })
	public boolean payByBalance(OrderForm order, String webPath, String pay_msg) {
		
		List<SysConfig> configs = this.sysConfigDAO.queryPageList(RedPigMaps.newMap());
		if (order.getOrder_status() == 20) {//20为已付款待发货
			return false;
		}
		
		boolean flag = true;
		//订单总价
		double order_total_price = this.orderFormTools
				.query_order_pay_price(CommUtil.null2String(order.getId()));
		//买家
		User buyer = (User) this.userDAO.selectByPrimaryKey(CommUtil.null2Long(order.getUser_id()));
		SysConfig config = (SysConfig) configs.get(0);
		// 订单分类，0为购物订单，1为手机充值订单 2为生活类团购订单
		if ((order.getOrder_cat() == 0) || (order.getOrder_cat() == 1)) {
			List<OrderForm> order_list = Lists.newArrayList();
			if (order.getOrder_status() != 0) {
				order_list.add(order);
			}
			
			if (order.getOrder_main() == 1) {
				// 子订单详情
				if ((!CommUtil.null2String(order.getChild_order_detail()).equals("")) && (order.getOrder_cat() != 2)) {
					List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
					for (Map child_map : maps ) {
						
						OrderForm child_order = (OrderForm) this.orderFormDAO.selectByPrimaryKey(CommUtil.null2Long(child_map.get("order_id")));
						
						if (child_order.getOrder_status() != 0) {
							order_list.add(child_order);
						}
					}
				}
			}
			for (OrderForm obj : order_list) {
				String paymsg = order.getPayment_name() + "支付";//支付名称
				int judge = 0;
				Date startDate = null;
				Date endDate = null;
				List<Map> goods_info = JSON.parseArray(obj.getGoods_info(),Map.class);
				for (Map m : goods_info) {
					if (m.get("advance_type") != null) {
						Goods goods = (Goods) this.goodsDAO.selectByPrimaryKey(CommUtil.null2Long(m.get("goods_id")));
						
						if ((goods != null) && (goods.getAdvance_sale_type() == 1)) {
							List<Map> ad_list = JSON.parseArray(goods.getAdvance_sale_info(), Map.class);
							Map<String,Object> map = (Map) ad_list.get(0);
							startDate = CommUtil.formatDate(CommUtil.null2String(map.get("rest_start_date")));
							endDate = CommUtil.formatDate(CommUtil.null2String(map.get("rest_end_date")));
						}
						if ("1".equals(CommUtil.null2String(m.get("advance_type")))) {//
							if ("0".equals(CommUtil.null2String(m.get("status")))) {
								judge = 1;
								obj.setOrder_status(11);
								m.put("status", "1");
								paymsg = order.getPayment_name() + "支付预售定金";
								obj.setGoods_info(JSON.toJSONString(goods_info));
							} else if ((new Date().after(startDate)) && (new Date().before(endDate))) {
								paymsg = order.getPayment_name() + "支付预售尾款";
							} else {
								judge = -1;
							}
						}
					}
				}
				if (judge == 0) {
					obj.setOrder_status(20);//20为已付款待发货
				}
				obj.setOut_order_id("");
				obj.setPayTime(new Date());
				obj.setPayment_id(order.getPayment_id());
				obj.setPayment_mark(order.getPayment_mark());
				obj.setPayment_name(order.getPayment_name());
				this.orderFormDAO.updateById(obj);
				OrderFormLog order_ofl = new OrderFormLog();//订单日志类,用来记录所有订单操作，包括订单提交、付款、发货、收货等等
				order_ofl.setAddTime(new Date());
				order_ofl.setLog_info(paymsg);
				order_ofl.setLog_user_id(buyer.getId());
				order_ofl.setLog_user_name(buyer.getUserName());
				order_ofl.setOf(obj);
				this.orderFormLogDao.saveEntity(order_ofl);
			}
		}
		// 订单分类，0为购物订单，1为手机充值订单 2为生活类团购订单
		if (order.getOrder_cat() == 2) {
			Payment payment = (Payment) this.paymentDao.selectByPrimaryKey(CommUtil.null2Long(order.getPayment_id()));
			order.setOrder_status(20);//20为已付款待发货
			order.setOut_order_id("");
			order.setPayTime(new Date());
			
			Calendar ca = Calendar.getInstance();
			ca.add(5, config.getGrouplife_order_return());
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String latertime = bartDateFormat.format(ca.getTime());
			order.setReturn_shipTime(CommUtil.formatDate(latertime));
			this.orderFormDAO.updateById(order);
			OrderFormLog ofl = new OrderFormLog();//订单日志类,用来记录所有订单操作，包括订单提交、付款、发货、收货等等
			ofl.setAddTime(new Date());
			ofl.setLog_info(pay_msg);
			ofl.setLog_user_id(buyer.getId());
			ofl.setLog_user_name(buyer.getUserName());
			ofl.setOf(order);
			this.orderFormLogDao.saveEntity(ofl);
			//解析生活类团购订单json数据
			Map<String,Object> map = this.orderFormTools.queryGroupInfo(order.getGroup_info());
			int count = CommUtil.null2Int(map.get("goods_count").toString());
			String goods_id = map.get("goods_id").toString();
			GroupLifeGoods goods = (GroupLifeGoods) this.groupLifeGoodsDAO.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			goods.setGroup_count(goods.getGroup_count() - CommUtil.null2Int(Integer.valueOf(count)));
			goods.setSelled_count(goods.getSelled_count() + CommUtil.null2Int(Integer.valueOf(count)));
			this.groupLifeGoodsDAO.updateById(goods);
			int i = 0;
			List<String> code_list = Lists.newArrayList();
			String codes = "";
			while (i < count) {
				GroupInfo info = new GroupInfo();
				info.setAddTime(new Date());
				info.setLifeGoods(goods);
				info.setPayment(payment);
				info.setUser_id(buyer.getId());
				info.setUser_name(buyer.getUserName());
				info.setOrder_id(order.getId());
				info.setGroup_sn(buyer.getId()
						+ CommUtil.formatTime(new StringBuilder("yyMMddHH")
								.append(CommUtil.randomInt(4)).toString(),
								new Date()));
				Calendar ca2 = Calendar.getInstance();
				ca2.add(5, config.getGrouplife_order_return());
				SimpleDateFormat bartDateFormat2 = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String latertime2 = bartDateFormat2.format(ca2.getTime());
				info.setRefund_Time(CommUtil.formatDate(latertime2));
				this.groupInfoDAO.saveEntity(info);
				codes = codes + info.getGroup_sn() + " ";
				code_list.add(info.getGroup_sn());
				i++;
			}
			// 订单种类，0为商家商品订单，1为平台自营商品订单
			if (order.getOrder_form() == 0) {
				Store store = (Store) this.storeDAO.selectByPrimaryKey(CommUtil.null2Long(order.getStore_id()));
				PayoffLog plog = new PayoffLog();
				plog.setPl_sn("pl"
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
						+ store.getUser().getId());
				plog.setPl_info("团购码生成成功");
				plog.setAddTime(new Date());
				plog.setSeller(store.getUser());
				plog.setO_id(CommUtil.null2String(order.getId()));
				plog.setOrder_id(order.getOrder_id().toString());
				plog.setCommission_amount(BigDecimal.valueOf(CommUtil
						.null2Double("0.00")));

				List<Map> Map_list = Lists.newArrayList();
				Map group_map = this.orderFormTools.queryGroupInfo(order
						.getGroup_info());
				Map_list.add(group_map);
				plog.setGoods_info(JSON.toJSONString(Map_list));
				plog.setOrder_total_price(order.getTotalPrice());
				plog.setTotal_amount(order.getTotalPrice());
				this.payoffLogDAO.saveEntity(plog);
				store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
						order.getTotalPrice(), store.getStore_sale_amount())));

				store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(
						order.getTotalPrice(), store.getStore_payoff_amount())));
				this.storeDAO.updateById(store);
			}
			SysConfig sc = config;
			sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
					order.getTotalPrice(), sc.getPayoff_all_sale())));
			this.sysConfigDAO.updateById(sc);
			String msg_content = "恭喜您成功购买团购" + map.get("goods_name")
					+ ",团购消费码分别为：" + codes + "您可以到用户中心-我的生活购中查看消费码的使用情况";

			Message tobuyer_msg = new Message();
			tobuyer_msg.setAddTime(new Date());
			tobuyer_msg.setStatus(0);
			tobuyer_msg.setType(0);
			tobuyer_msg.setContent(msg_content);
			Map<String,Object> params = Maps.newHashMap();
			params.put("userName", "admin");
			
			tobuyer_msg.setFromUser(this.userDAO.queryByProperty(params));
			tobuyer_msg.setToUser(buyer);
			this.messageDAO.saveEntity(tobuyer_msg);
			try {
				this.orderFormTools.send_groupInfo_sms(webPath, order,
						buyer.getMobile(),
						"sms_tobuyer_online_ok_send_groupinfo", code_list,
						buyer.getId().toString(), goods.getUser().getId().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//买家减去余额
		buyer.setAvailableBalance(BigDecimal.valueOf(CommUtil.subtract(buyer.getAvailableBalance(), Double.valueOf(order_total_price))));
		/**-------------------------------直销佣金提成Start------------------------------------------*/
		User directSellingFirstParent = buyer.getDirectSellingParent();
		User directSellingSecondParent = null;
		if(directSellingFirstParent!=null) {
			directSellingSecondParent = directSellingFirstParent.getDirectSellingParent();
		}
		//一级获得佣金
		if(directSellingFirstParent != null) {
			directSellingFirstParent = this.userDAO.selectByPrimaryKey(directSellingFirstParent.getId());
			
			BigDecimal availableBalance = directSellingFirstParent.getAvailableBalance();
			//第一级佣金:原来余额+订单总金额*佣金比例
			
			availableBalance = availableBalance.add(
					BigDecimal.valueOf(
							CommUtil.mul(order_total_price, this.configService.getSysConfig().getDirect_selling_first_level_rate())));
			
			directSellingFirstParent.setAvailableBalance(availableBalance);
			this.userDAO.updateById(directSellingFirstParent);
			
			PredepositLog log = new PredepositLog();
			log.setAddTime(new Date());
			log.setPd_log_user(directSellingFirstParent);
			log.setPd_op_type("获得佣金");//佣金：订单总金额*佣金比例
			log.setPd_log_amount(
					BigDecimal.valueOf(
							CommUtil.mul(order_total_price, this.configService.getSysConfig().getDirect_selling_first_level_rate())));
			log.setPd_log_info("买家:"+buyer.getUserName()+" 下单:"+order.getOrder_id() + " 获得佣金");
			log.setPd_type("可用预存款");
			this.PredepositLogDAO.saveEntity(log);
		}
		
		//二级获得佣金
		if(directSellingSecondParent != null) {
			directSellingSecondParent = this.userDAO.selectByPrimaryKey(directSellingSecondParent.getId());
			
			BigDecimal availableBalance = directSellingSecondParent.getAvailableBalance();
			//第二级佣金:原来余额+订单总金额*佣金比例
			
			availableBalance.add(
					BigDecimal.valueOf(
							CommUtil.mul(order_total_price, this.configService.getSysConfig().getDirect_selling_second_level_rate())));
			
			directSellingSecondParent.setAvailableBalance(availableBalance);
			this.userDAO.updateById(directSellingSecondParent);
			
			PredepositLog log = new PredepositLog();
			log.setAddTime(new Date());
			log.setPd_log_user(directSellingSecondParent);
			log.setPd_op_type("获得佣金");//佣金：订单总金额*佣金比例
			log.setPd_log_amount(
					BigDecimal.valueOf(
							CommUtil.mul(order_total_price, this.configService.getSysConfig().getDirect_selling_second_level_rate())));
			log.setPd_log_info("买家:"+buyer.getUserName()+" 下单:"+order.getOrder_id() + " 获得佣金");
			log.setPd_type("可用预存款");
			this.PredepositLogDAO.saveEntity(log);
		}
		
		/**------------------------------直销佣金提成End-------------------------------------------*/
		
		this.userDAO.updateById(buyer);
		PredepositLog log = new PredepositLog();
		log.setAddTime(new Date());
		log.setPd_log_user(buyer);
		log.setPd_op_type("消费");
		log.setPd_log_amount(BigDecimal.valueOf(-CommUtil.null2Double(Double
				.valueOf(order_total_price))));
		log.setPd_log_info(order.getOrder_id() + "订单购物减少可用预存款");
		log.setPd_type("可用预存款");
		this.PredepositLogDAO.saveEntity(log);
		try {
			this.orderFormTools.sendMsgWhenHandleOrder(webPath, order,
					"tobuyer_balance_pay_ok_notify",
					"toseller_balance_pay_ok_notify");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Transactional(rollbackFor = { Exception.class })
	public boolean payByPayafter(OrderForm order, String webPath, String pay_msg) {
		if (order.getOrder_status() == 16) {
			return false;
		}
		boolean flag = true;
		List<OrderForm> order_list = Lists.newArrayList();
		if (order.getOrder_status() != 0) {
			order_list.add(order);
		}
		if (order.getOrder_main() == 1) {
			if ((!CommUtil.null2String(order.getChild_order_detail())
					.equals("")) && (order.getOrder_cat() != 2)) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = (OrderForm) this.orderFormDAO.selectByPrimaryKey(CommUtil.null2Long(child_map.get("order_id")));
					if (child_order.getOrder_status() != 0) {
						order_list.add(child_order);
					}
				}
			}
		}
		User buyer = (User) this.userDAO.selectByPrimaryKey(CommUtil.null2Long(order
				.getUser_id()));
		for (OrderForm obj : order_list) {
			obj.setPayment_name("货到付款");
			obj.setPay_msg(pay_msg);
			obj.setPayTime(new Date());
			obj.setPayType("payafter");
			obj.setOrder_status(16);
			this.orderFormDAO.updateById(obj);

			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info("提交货到付款申请");
			ofl.setLog_user_id(buyer.getId());
			ofl.setLog_user_name(buyer.getUserName());
			ofl.setOf(obj);
			this.orderFormLogDAO.saveEntity(ofl);
		}
		try {
			this.orderFormTools.sendMsgWhenHandleOrder(webPath, order,
					"tobuyer_payafter_pay_ok_notify",
					"toseller_payafter_pay_ok_notify");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Transactional(rollbackFor = { Exception.class })
	public boolean payByOnline(OrderForm main_order, String trade_no,
			String webPath) {
		if (main_order.getOrder_status() == 20) {
			return false;
		}
		User buyer = (User) this.userDAO.selectByPrimaryKey(CommUtil.null2Long(main_order
				.getUser_id()));
		List<SysConfig> configs = this.sysConfigDAO.queryPageList(RedPigMaps.newMap());
		
		boolean flag = true;
		SysConfig config = (SysConfig) configs.get(0);
		if ((main_order.getOrder_cat() == 0)
				|| (main_order.getOrder_cat() == 1)) {
			List<OrderForm> order_list = Lists.newArrayList();
			if (main_order.getOrder_status() != 0) {
				order_list.add(main_order);
			}
			if (main_order.getOrder_main() == 1) {
				if ((!CommUtil.null2String(main_order.getChild_order_detail())
						.equals("")) && (main_order.getOrder_cat() != 2)) {
					List<Map> maps = this.orderFormTools
							.queryGoodsInfo(main_order.getChild_order_detail());
					for (Map child_map : maps) {
						
						OrderForm child_order = (OrderForm) this.orderFormDAO
								.selectByPrimaryKey(CommUtil.null2Long(child_map
										.get("order_id")));
						if (child_order.getOrder_status() != 0) {
							order_list.add(child_order);
						}
					}
				}
			}
			for (OrderForm obj : order_list) {
				String paymsg = main_order.getPayment_name() + "支付";
				int judge = 0;
				Date startDate = null;
				Date endDate = null;
				List<Map> goods_info = JSON.parseArray(obj.getGoods_info(),
						Map.class);
				for (Map m : goods_info) {
					if (m.get("advance_type") != null) {
						Goods goods = (Goods) this.goodsDAO.selectByPrimaryKey(CommUtil
								.null2Long(m.get("goods_id")));
						if ((goods != null)
								&& (goods.getAdvance_sale_type() == 1)) {
							List<Map> ad_list = JSON.parseArray(
									goods.getAdvance_sale_info(), Map.class);
							Map<String,Object> map = (Map) ad_list.get(0);
							startDate = CommUtil.formatDate(CommUtil
									.null2String(map.get("rest_start_date")));
							endDate = CommUtil.formatDate(CommUtil
									.null2String(map.get("rest_end_date")));
						}
						if ("1".equals(CommUtil.null2String(m
								.get("advance_type")))) {
							if ("0".equals(CommUtil.null2String(m.get("status")))) {
								judge = 1;
								obj.setOrder_status(11);
								m.put("status", "1");
								paymsg = main_order.getPayment_name()
										+ "支付预售定金";
								obj.setGoods_info(JSON.toJSONString(goods_info));
							} else if ((new Date().after(startDate))
									&& (new Date().before(endDate))) {
								paymsg = main_order.getPayment_name()
										+ "支付预售尾款";
							} else {
								judge = -1;
							}
						}
					}
				}
				if (judge == 0) {
					obj.setOrder_status(20);
				}
				obj.setOut_order_id(trade_no);
				obj.setPayTime(new Date());
				obj.setPayment_id(main_order.getPayment_id());
				obj.setPayment_mark(main_order.getPayment_mark());
				obj.setPayment_name(main_order.getPayment_name());
				this.orderFormDAO.updateById(obj);
				OrderFormLog order_ofl = new OrderFormLog();
				order_ofl.setAddTime(new Date());
				order_ofl.setLog_info(paymsg);
				order_ofl.setLog_user_id(buyer.getId());
				order_ofl.setLog_user_name(buyer.getUserName());
				order_ofl.setOf(obj);
				this.orderFormLogDAO.saveEntity(order_ofl);
			}
		}
		if (main_order.getOrder_cat() == 2) {
			Payment payment = (Payment) this.paymentDao.selectByPrimaryKey(CommUtil
					.null2Long(main_order.getPayment_id()));
			main_order.setOrder_status(20);
			main_order.setOut_order_id("");
			main_order.setPayTime(new Date());

			Calendar ca = Calendar.getInstance();
			ca.add(5, config.getGrouplife_order_return());
			SimpleDateFormat bartDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String latertime = bartDateFormat.format(ca.getTime());
			main_order.setReturn_shipTime(CommUtil.formatDate(latertime));
			this.orderFormDAO.updateById(main_order);
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setLog_info(main_order.getPayment_mark());
			ofl.setLog_user_id(buyer.getId());
			ofl.setLog_user_name(buyer.getUserName());
			ofl.setOf(main_order);
			this.orderFormLogDao.saveEntity(ofl);
			Map<String,Object> map = this.orderFormTools.queryGroupInfo(main_order
					.getGroup_info());
			int count = CommUtil.null2Int(map.get("goods_count").toString());
			String goods_id = map.get("goods_id").toString();
			GroupLifeGoods goods = (GroupLifeGoods) this.groupLifeGoodsDAO
					.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			goods.setGroup_count(goods.getGroup_count()
					- CommUtil.null2Int(Integer.valueOf(count)));
			goods.setSelled_count(goods.getSelled_count()
					+ CommUtil.null2Int(Integer.valueOf(count)));
			this.groupLifeGoodsDAO.updateById(goods);
			int i = 0;
			List<String> code_list = Lists.newArrayList();
			String codes = "";
			while (i < count) {
				GroupInfo info = new GroupInfo();
				info.setAddTime(new Date());
				info.setLifeGoods(goods);
				info.setPayment(payment);
				info.setUser_id(buyer.getId());
				info.setUser_name(buyer.getUserName());
				info.setOrder_id(main_order.getId());
				info.setGroup_sn(buyer.getId()
						+ CommUtil.formatTime(new StringBuilder("yyMMddHH")
								.append(CommUtil.randomInt(4)).toString(),
								new Date()));
				Calendar ca2 = Calendar.getInstance();
				ca2.add(5, config.getGrouplife_order_return());
				SimpleDateFormat bartDateFormat2 = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String latertime2 = bartDateFormat2.format(ca2.getTime());
				info.setRefund_Time(CommUtil.formatDate(latertime2));
				this.groupInfoDAO.saveEntity(info);
				codes = codes + info.getGroup_sn() + " ";
				code_list.add(info.getGroup_sn());
				i++;
			}
			if (main_order.getOrder_form() == 0) {
				Store store = (Store) this.storeDAO.selectByPrimaryKey(CommUtil
						.null2Long(main_order.getStore_id()));
				PayoffLog plog = new PayoffLog();
				plog.setPl_sn("pl"
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
						+ store.getUser().getId());
				plog.setPl_info("团购码生成成功");
				plog.setAddTime(new Date());
				plog.setSeller(store.getUser());
				plog.setO_id(CommUtil.null2String(main_order.getId()));
				plog.setOrder_id(main_order.getOrder_id().toString());
				plog.setCommission_amount(BigDecimal.valueOf(CommUtil
						.null2Double("0.00")));

				List<Map> Map_list = Lists.newArrayList();
				Map group_map = this.orderFormTools.queryGroupInfo(main_order
						.getGroup_info());
				Map_list.add(group_map);
				plog.setGoods_info(JSON.toJSONString(Map_list));
				plog.setOrder_total_price(main_order.getTotalPrice());
				plog.setTotal_amount(main_order.getTotalPrice());
				this.payoffLogDAO.saveEntity(plog);
				store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
						main_order.getTotalPrice(),
						store.getStore_sale_amount())));

				store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(
						main_order.getTotalPrice(),
						store.getStore_payoff_amount())));
				this.storeDAO.updateById(store);
			}
			SysConfig sc = config;
			sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
					main_order.getTotalPrice(), sc.getPayoff_all_sale())));
			this.sysConfigDAO.updateById(sc);
			String msg_content = "恭喜您成功购买团购" + map.get("goods_name")
					+ ",团购消费码分别为：" + codes + "您可以到用户中心-我的生活购中查看消费码的使用情况";

			Message tobuyer_msg = new Message();
			tobuyer_msg.setAddTime(new Date());
			tobuyer_msg.setStatus(0);
			tobuyer_msg.setType(0);
			tobuyer_msg.setContent(msg_content);
			Map<String,Object> params = Maps.newHashMap();
			params.put("userName", "admin");
			tobuyer_msg.setFromUser(this.userDAO.queryByProperty(params));
			tobuyer_msg.setToUser(buyer);
			this.messageDAO.saveEntity(tobuyer_msg);
			try {
				this.orderFormTools.send_groupInfo_sms(webPath, main_order,
						buyer.getMobile(),
						"sms_tobuyer_online_ok_send_groupinfo", code_list,
						buyer.getId().toString(), goods.getUser().getId()
								.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			this.orderFormTools.sendMsgWhenHandleOrder(webPath, main_order,
					"tobuyer_online_pay_ok_notify",
					"toseller_online_pay_ok_notify");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * 收货确认保存
	 */
	@SuppressWarnings("static-access")
	@Transactional(rollbackFor = { Exception.class })
	public void confirmOrder(HttpServletRequest request, OrderForm obj) {
		try {
			boolean ret = true;
			List<SysConfig> configs = this.sysConfigDAO.queryPageList(RedPigMaps.newMap());
			
			SysConfig sc = configs.get(0);
			
			User user =  this.userDAO.selectByPrimaryKey(CommUtil.null2Long(obj.getUser_id()));
			
			Calendar ca = Calendar.getInstance();
			ca.add(ca.DATE, sc.getAuto_order_return());//  买家申请退货，到达该时间未能输入退货单号及物流公司，退货失败并且订单变为待评价，订单状态为49
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String latertime = bartDateFormat.format(ca.getTime());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(latertime);
			
			if ((obj.getOrder_status() != 25) && (obj.getOrder_status() < 40)) {//必须是未确认收货状态
				
				obj.setOrder_status(40);//设置为确认收货状态
				obj.setReturn_shipTime(date);// 设置买家退货发货截止时间
				obj.setConfirmTime(new Date());// 设置确认收货时间
				this.orderFormDAO.updateById(obj);
				
				/**
				 * 注:这里的意思是:
				 * 1、每个订单有一个赠送每个订单(赠送积分)最大上限
				 * 2、每个订单又有一个消费比例(赠送积分)
				 * 如果用户下了一单,金额非常大那么会导致积分非常多,这里不能让他出现这个情况,有一个上限积分比例,最大只能这么多(sc.getEveryIndentLimit())
				 */
				int user_integral = (int) CommUtil.div(obj.getTotalPrice(),Integer.valueOf(sc.getConsumptionRatio()));
				if (user_integral > sc.getEveryIndentLimit()) {
					user_integral = sc.getEveryIndentLimit();
				}
				
				User orderUser = this.userDAO.selectByPrimaryKey(CommUtil.null2Long(obj.getUser_id()));
				
				if (orderUser != null) {
					//用户积分=用户原有积分+此次赠送积分
					orderUser.setIntegral(CommUtil.null2Int(Integer.valueOf(orderUser.getIntegral())) + user_integral);
					
					this.userDAO.updateById(orderUser);
					if (sc.getIntegral()) {//如果系统开启了积分商城,记录积分获得日志
						IntegralLog log = new IntegralLog();
						log.setAddTime(new Date());
						log.setContent("购物增加" + user_integral + "分");
						log.setIntegral(user_integral);
						log.setIntegral_user(orderUser);
						log.setOperate_user(SecurityUserHolder.getCurrentUser());
						log.setType("order");
						this.integralLogDao.saveEntity(log);
					}
				}
				
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user_id(user.getId());
				ofl.setLog_user_name(user.getUserName());
				ofl.setOf(obj);
				this.orderFormLogDAO.saveEntity(ofl);
				
				if ((obj.getOrder_form() == 0) && (obj.getOrder_status() != 25)) {
					Store store = this.storeDAO.selectByPrimaryKey(CommUtil.null2Long(obj.getStore_id()));
					PayoffLog plog = new PayoffLog();
					// 设置结算结算账单唯一编号记录，使用pl为前缀
					plog.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					
					plog.setPl_info("订单到期自动收货");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(obj.getId()));
					plog.setOrder_id(obj.getOrder_id().toString());
					plog.setCommission_amount(obj.getCommission_amount());
					plog.setGoods_info(obj.getGoods_info());
					plog.setOrder_total_price(obj.getGoods_amount());
					
					// 如果买家支付方式为货到付款，买家确认收货时更新商品库存
					if ((obj.getPayment_mark() != null) && (obj.getPayment_mark().equals("payafter"))) {
						plog.setTotal_amount(BigDecimal.valueOf(CommUtil.subtract(Integer.valueOf(0),obj.getCommission_amount())));
					} else {
						plog.setTotal_amount(BigDecimal.valueOf(CommUtil.subtract(obj.getGoods_amount(),obj.getCommission_amount())));
					}
					//生成计算日志
					this.payoffLogDAO.saveEntity(plog);
					// 店铺本次结算总销售金额，生成账单时总销售金额增加，账单结算完成后总结算金额减少
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(obj.getGoods_amount(), store.getStore_sale_amount())));
					// 店铺本次结算总佣金,生成账单时增加，结算账单完成时减少
					store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.add(obj.getCommission_amount(),store.getStore_commission_amount())));
					// 店铺本次结算总结算金额,生成账单时增加，结算账单完成时减少
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.add(plog.getTotal_amount(),store.getStore_payoff_amount())));
					
					this.storeDAO.updateById(store);
					// 系统总销售金额
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(obj.getGoods_amount(), sc.getPayoff_all_sale())));
					// 系统总销售佣金
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil.add(obj.getCommission_amount(),sc.getPayoff_all_commission())));
					this.sysConfigDAO.updateById(sc);
				}
			}
			if (ret) {
				this.orderFormTools.sendMsgWhenHandleOrder(CommUtil.getURL(request), obj, null,"toseller_order_receive_ok_notify");
			}
		} catch (Exception e) {
			
		}
	}

	
	@SuppressWarnings("static-access")
	@Transactional(rollbackFor = { Exception.class })
	public void shippingOrder(HttpServletRequest request, OrderForm obj,
			String shipCode, String state_info, String ecc_id, String sa_id) {
		List<SysConfig> configs = this.sysConfigDAO.queryPageList(RedPigMaps.newMap());
		SysConfig sc = (SysConfig) configs.get(0);
		boolean flag = true;
		ExpressCompanyCommon ecc = (ExpressCompanyCommon) this.expresscompanyCommDao.selectByPrimaryKey(CommUtil.null2Long(ecc_id));
		obj.setOrder_status(30);
		obj.setShipCode(shipCode);
		obj.setShipTime(new Date());
		if (ecc != null) {
			Map json_map = Maps.newHashMap();
			json_map.put("express_company_id", ecc.getId());
			json_map.put("express_company_name", ecc.getEcc_name());
			json_map.put("express_company_mark", ecc.getEcc_code());
			json_map.put("express_company_type", ecc.getEcc_ec_type());
			obj.setExpress_info(JSON.toJSONString(json_map));
		}
		String[] order_seller_intros = request
				.getParameterValues("order_seller_intro");
		String[] goods_ids = request.getParameterValues("goods_id");
		String[] goods_names = request.getParameterValues("goods_name");
		String[] goods_counts = request.getParameterValues("goods_count");
		if ((order_seller_intros != null) && (order_seller_intros.length > 0)) {
			List<Map> list_map = Lists.newArrayList();
			for (int i = 0; i < goods_ids.length; i++) {
				Map json_map = Maps.newHashMap();
				json_map.put("goods_id", goods_ids[i]);
				json_map.put("goods_name", goods_names[i]);
				json_map.put("goods_count", goods_counts[i]);
				json_map.put("order_seller_intro", order_seller_intros[i]);
				json_map.put("order_id", CommUtil.null2String(obj.getId()));
				list_map.add(json_map);
			}
			obj.setOrder_seller_intro(JSON.toJSONString(list_map));
		}
		ShipAddress sa = (ShipAddress) this.shipAddressDao.selectByPrimaryKey(CommUtil
				.null2Long(sa_id));
		if (sa != null) {
			obj.setShip_addr_id(sa.getId());
			Area area = (Area) this.areaDAO.selectByPrimaryKey(sa.getSa_area_id());
			obj.setShip_addr(area.getParent().getParent().getAreaName()
					+ area.getParent().getAreaName() + area.getAreaName()
					+ sa.getSa_addr());
		}
		obj.setEva_user_id(SecurityUserHolder.getCurrentUser().getId());
		
		this.orderFormDAO.updateById(obj);
		OrderFormLog ofl = new OrderFormLog();
		ofl.setAddTime(new Date());
		if (1 == obj.getOrder_form()) {
			ofl.setLog_info("自营确认发货，商品已从" + obj.getShip_storehouse_name()
					+ "出库");
		} else {
			ofl.setLog_info("确认发货");
		}
		ofl.setState_info(state_info);
		ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
		ofl.setLog_user_name(SecurityUserHolder.getCurrentUser().getUserName());
		ofl.setOf(obj);
		this.orderFormLogDao.saveEntity(ofl);
		Map ec_map;
		if (sc.getKuaidi_type() == 1) {
			JSONObject info = new JSONObject();
			Map express_map = JSON.parseObject(obj.getExpress_info());
			if(express_map != null){
				info.put("company", CommUtil.null2String(express_map.get("express_company_mark")));
			}
			info.put("number", obj.getShipCode());
			info.put("from", CommUtil.null2String(obj.getShip_addr()));
			info.put("to", obj.getReceiver_area());
			info.put("key", sc.getKuaidi_id2());
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
				if ("success"
						.equals(CommUtil.null2String(remap.get("message")))) {
					ExpressInfo ei = new ExpressInfo();
					ei.setAddTime(new Date());
					ei.setOrder_id(obj.getId());
					ei.setOrder_express_id(obj.getShipCode());
					ei.setOrder_type(0);
					ec_map = JSON.parseObject(CommUtil.null2String(obj
							.getExpress_info()));
					if (ec_map != null) {
						ei.setOrder_express_name(CommUtil.null2String(ec_map
								.get("express_company_name")));
					}
					this.expressInfoDAO.saveEntity(ei);
					System.out.println("订阅成功");
				} else {
					flag = false;
					System.out.println("订阅失败");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (flag) {
			Payment payment = (Payment) this.paymentDao.selectByPrimaryKey(obj.getPayment_id());
			
			if ((payment != null) && (payment.getMark().equals("alipay"))
					&& (payment.getInterfaceType() == 1)) {
				boolean synch = false;
				String safe_key = "";
				String partner = "";
				if (!CommUtil.null2String(payment.getSafeKey()).equals("")) {
					if (!CommUtil.null2String(payment.getPartner()).equals("")) {
						safe_key = payment.getSafeKey();
						partner = payment.getPartner();
						synch = true;
					}
				}
				if (synch) {
					AlipayConfig config = new AlipayConfig();
					config.setKey(safe_key);
					config.setPartner(partner);
					Map<String, String> sParaTemp = Maps.newHashMap();
					sParaTemp.put("service", "send_goods_confirm_by_platform");
					sParaTemp.put("partner", config.getPartner());
					sParaTemp.put("_input_charset", config.getInput_charset());
					sParaTemp.put("trade_no", obj.getOut_order_id());
					sParaTemp.put("logistics_name", ecc.getEcc_name());
					sParaTemp.put("invoice_no", shipCode);
					sParaTemp.put("transport_type", ecc.getEcc_ec_type());
					try {
						AlipaySubmit.buildRequest(config, "web", sParaTemp, "",
								"");
					} catch (Exception localException) {
					}
				}
			}
		}
	}
	
	/**
	 * 提交订单
	 */
	@Transactional(rollbackFor={Exception.class})
	public OrderForm submitOrderForm(
			HttpServletRequest request, 
			String store_id, String gcs, String delivery_time,
			String delivery_type, String delivery_id, 
			String payType, List<GoodsCart> order_carts, 
			User buyer,Address addr, String order_type){
		
		int max_size = request.getParameterMap().keySet().size();
		String[] coupon_ids = new String[max_size];
		String[] transport_ids = new String[max_size];
		String[] msg_ids = new String[max_size];
		Enumeration pNames = request.getParameterNames();
		int temp_j = 0;
		while (pNames.hasMoreElements()) {
			String name = (String) pNames.nextElement();
			if (name.indexOf("coupon_") >= 0) {
				coupon_ids[temp_j] = name;
			}
			if (name.indexOf("transport_") >= 0) {
				transport_ids[temp_j] = name;
			}
			if (name.indexOf("msg_") >= 0) {
				msg_ids[temp_j] = name;
			}
			temp_j++;
		}
		//发票
		int invoiceType = CommUtil.null2Int(request.getParameter("invoiceType"));
		String invoice = CommUtil.null2String(request.getParameter("invoice"));
		
		OrderForm main_order = null;
		SysConfig config = this.configService.getSysConfig();
		String default_goods_img = this.configService.getSysConfig().getGoodsImage().getPath() + "/"
				+ this.configService.getSysConfig().getGoodsImage().getName();
		
		String web_url = CommUtil.getURL(request);
		BigDecimal ad_price = new BigDecimal(0.0D);
		String order_special = "";
		String[] store_ids = store_id.split(",");
		List<Map> child_order_maps = Lists.newArrayList();
		String order_suffix = CommUtil.formatTime("yyyyMMddHHmmss", new Date());

		//遍历商家
		for (int i = 0; i < store_ids.length; i++) {
			String sid = store_ids[i];
			Store store = null;
			List<GoodsCart> gc_list = Lists.newArrayList();
			List<Map> map_list = Lists.newArrayList();
			String order_store_id = "0";
			if ((sid != "self") && (!sid.equals("self"))) {
				order_store_id = CommUtil.null2String(sid);
				store = (Store) this.storeDAO.selectByPrimaryKey(CommUtil.null2Long(sid));
			}
			List<Map> mapList = Lists.newArrayList();
			Map jsonMap = Maps.newHashMap();
			if (!CommUtil.null2String(buyer.getBuy_goods_limit_info()).equals("")) {
				jsonMap = JSON.parseObject(buyer.getBuy_goods_limit_info());
				mapList = (List) jsonMap.get("data");
			}
			List<Goods> gift_goods = Lists.newArrayList();
			Map temp;
			//订单类型
			Nuke nuke = new Nuke();
			CollageBuy collageBuy = new CollageBuy();
			//遍历购物车
			for (GoodsCart gc : order_carts) {

				int count = gc.getCount();
				if ((gc.getGoods().getGoods_limit() == 1) && (gc.getGoods().getGoods_limit() > 0)) {
					for (Map mapp : mapList) {
						if (CommUtil.null2String(gc.getGoods().getId()).equals(CommUtil.null2String(mapp.get("gid")))) {
							count = CommUtil.null2Int(mapp.get("count")) + gc.getCount();
							mapList.remove(mapp);
							break;
						}
					}
					Map<String, Object> map = Maps.newHashMap();
					map.put("gid", gc.getGoods().getId());
					map.put("spec", "");
					map.put("count", Integer.valueOf(count));
					mapList.add(map);
				}
				//如果选择了赠品
				if (gc.getWhether_choose_gift() == 1) {
					Map cart_gift = JSON.parseObject(gc.getGift_info());
					Goods obj = (Goods) this.goodsDAO.selectByPrimaryKey(CommUtil.null2Long(cart_gift.get("goods_id")));
					gift_goods.add(obj);
				}
				//如果是第三方经销商
				if (gc.getGoods().getGoods_type() == 1) {
					if (gc.getGoods().getGoods_store().getId().equals(CommUtil.null2Long(sid))) {
						String goods_type = "";
						//如果是组合销售
						if ((("combin" == gc.getCart_type()) || ("combin".equals(gc.getCart_type())))
								&& (gc.getCombin_main() == 1)) {
							goods_type = "combin";
						}
						if (("group" == gc.getCart_type()) || ("group".equals(gc.getCart_type()))) {
							goods_type = "group";
						}
						Map<String, Object> json_map = Maps.newHashMap();
						json_map.put("goods_id", gc.getGoods().getId());
						json_map.put("goods_name", gc.getGoods().getGoods_name());
						if ((gc.getCart_gsp() == null) || (gc.getCart_gsp().equals(""))) {
							json_map.put("goods_serial", gc.getGoods().getGoods_serial());
						} else {
							String[] gsp_ids = gc.getCart_gsp().split(",");
							String goods_inventory_detail = gc.getGoods().getGoods_inventory_detail();
							List<HashMap> list = JSON.parseArray(goods_inventory_detail, HashMap.class);
							for (Map temp1 : list) {
								String[] temp_ids = CommUtil.null2String(temp1.get("id")).split("_");
								Arrays.sort(gsp_ids);

								Arrays.sort(temp_ids);
								if (Arrays.equals(gsp_ids, temp_ids)) {
									json_map.put("goods_serial", temp1.get("code"));
								}
							}
						}
						json_map.put("goods_choice_type", Integer.valueOf(gc.getGoods().getGoods_choice_type()));
						json_map.put("goods_count", Integer.valueOf(gc.getCount()));
						json_map.put("goods_price", gc.getPrice());
						if (gc.getGoods().getAdvance_sale_type() == 1) {
							order_special = "advance";
							BigDecimal din_price = this.goodsTools
									.queryGoodsAdvancePrice(CommUtil.null2String(gc.getId()), "1");
							BigDecimal wei_price = this.goodsTools
									.queryGoodsAdvancePrice(CommUtil.null2String(gc.getId()), "2");
							ad_price = ad_price.add(din_price);
							json_map.put("advance_type", "1");
							json_map.put("advance_din", din_price);
							json_map.put("advance_wei", wei_price);
							json_map.put("status", "0");
							goods_type = "advance";
						} else {
							json_map.put("advance_type", "0");
						}
						json_map.put("goods_type", goods_type);
						json_map.put("goods_all_price",
								Double.valueOf(CommUtil.mul(gc.getPrice(), Integer.valueOf(gc.getCount()))));
						json_map.put("goods_commission_price", Double.valueOf(this.cartTools.getCommissionByCart(gc)));
						json_map.put("goods_commission_rate", this.cartTools.getCommissionRateByCart(gc));
						json_map.put("goods_payoff_price",
								Double.valueOf(CommUtil.subtract(
										Double.valueOf(CommUtil.mul(gc.getPrice(), Integer.valueOf(gc.getCount()))),
										Double.valueOf(this.cartTools.getCommissionByCart(gc)))));
						json_map.put("goods_gsp_val", gc.getSpec_info());
						json_map.put("goods_gsp_ids", gc.getCart_gsp());
						if (gc.getGoods().getGoods_main_photo() != null) {
							json_map.put("goods_mainphoto_path",
									gc.getGoods().getGoods_main_photo().getPath() + "/"
											+ gc.getGoods().getGoods_main_photo().getName() + "_small."
											+ gc.getGoods().getGoods_main_photo().getExt());
						} else {
							json_map.put("goods_mainphoto_path", default_goods_img);
						}
						String goods_domainPath = web_url + "/items_" + gc.getGoods().getId() + "";
						String store_domainPath = web_url + "/store_" + gc.getGoods().getGoods_store().getId() + "";
						if ((config.getSecond_domain_open())
								&& (gc.getGoods().getGoods_store().getStore_second_domain() != "")) {
							if (gc.getGoods().getGoods_type() == 1) {
								String store_second_domain = "http://"
										+ gc.getGoods().getGoods_store().getStore_second_domain() + "."
										+ CommUtil.generic_domain(request);
								goods_domainPath = store_second_domain + "/items_" + gc.getGoods().getId() + "";
								store_domainPath = store_second_domain;
							}
						}
						json_map.put("goods_domainPath", goods_domainPath);
						json_map.put("store_domainPath", store_domainPath);
						if (goods_type.equals("combin")) {
							json_map.put("combin_suit_info", gc.getCombin_suit_info());
						}
						map_list.add(json_map);
						gc_list.add(gc);
					}
					//如果是自营商品
				} else if ((sid == "self") || (sid.equals("self"))) {
					String goods_type = "";
					if ((("combin" == gc.getCart_type()) || ("combin".equals(gc.getCart_type())))
							&& (gc.getCombin_main() == 1)) {
						goods_type = "combin";
					}
					if (("group" == gc.getCart_type()) || ("group".equals(gc.getCart_type()))) {
						goods_type = "group";
					}
					Map<String, Object> json_map = Maps.newHashMap();
					json_map.put("goods_id", gc.getGoods().getId());
					//判断是否为预售商品
					if (gc.getGoods().getAdvance_sale_type() == 1) {
						order_special = "advance";
						BigDecimal din_price = this.goodsTools.queryGoodsAdvancePrice(CommUtil.null2String(gc.getId()),
								"1");
						BigDecimal wei_price = this.goodsTools.queryGoodsAdvancePrice(CommUtil.null2String(gc.getId()),
								"2");
						ad_price = ad_price.add(din_price);
						json_map.put("advance_type", "1");
						json_map.put("advance_din", din_price);
						json_map.put("advance_wei", wei_price);
						json_map.put("status", "0");
					} else {
						json_map.put("advance_type", "0");
					}
					json_map.put("goods_name", gc.getGoods().getGoods_name());
					if ((gc.getCart_gsp() == null) || (gc.getCart_gsp().equals(""))) {
						json_map.put("goods_serial", gc.getGoods().getGoods_serial());
					} else {
						String[] gsp_ids = gc.getCart_gsp().split(",");
						String goods_inventory_detail = gc.getGoods().getGoods_inventory_detail();
						List<HashMap> list = JSON.parseArray(goods_inventory_detail, HashMap.class);
						if(goods_inventory_detail==null || list == null){
							json_map.put("goods_serial", gc.getGoods().getId());
						}else{
							for (Map temp1 : list) {
								String[] temp_ids = CommUtil.null2String(temp1.get("id")).split("_");
								Arrays.sort(gsp_ids);
								Arrays.sort(temp_ids);
								if (Arrays.equals(gsp_ids, temp_ids)) {
									json_map.put("goods_serial", temp1.get("code"));
								}
							}	
						}
						
					}
					json_map.put("goods_choice_type", Integer.valueOf(gc.getGoods().getGoods_choice_type()));
					json_map.put("goods_type", goods_type);
					json_map.put("goods_count", Integer.valueOf(gc.getCount()));
					json_map.put("goods_price", gc.getPrice());
					json_map.put("goods_all_price",
							Double.valueOf(CommUtil.mul(gc.getPrice(), Integer.valueOf(gc.getCount()))));
					json_map.put("goods_gsp_val", gc.getSpec_info());
					json_map.put("goods_gsp_ids", gc.getCart_gsp());
					if (gc.getGoods().getGoods_main_photo() != null) {
						json_map.put("goods_mainphoto_path",
								gc.getGoods().getGoods_main_photo().getPath() + "/"
										+ gc.getGoods().getGoods_main_photo().getName() + "_small."
										+ gc.getGoods().getGoods_main_photo().getExt());
					} else {
						json_map.put("goods_mainphoto_path", default_goods_img);
					}
					json_map.put("goods_domainPath", web_url + "/items_" + gc.getGoods().getId() + "");
					if (goods_type.equals("combin")) {
						json_map.put("combin_suit_info", gc.getCombin_suit_info());
					}
					map_list.add((Map) json_map);
					gc_list.add(gc);
				}
				//如果该订单中存在秒杀的类型
				if (gc.getGoods().getNuke_buy()==2&&gc.getGoods().getNuke()!=null){
					nuke = gc.getGoods().getNuke();
				}
				//如果该订单中存在拼团的类型
				if (gc.getGoods().getCollage_buy()==2&&gc.getGoods().getCollage()!=null){
					collageBuy = gc.getGoods().getCollage();
				}
			}
			if (mapList.size() > 0) {
				jsonMap.put("data", mapList);
				buyer.setBuy_goods_limit_info(JSON.toJSONString(jsonMap));
				this.userDAO.updateById(buyer);
			}
			List<Map> gift_map = Lists.newArrayList();
			for (int j = 0; (gift_goods.size() > 0) && (j < gift_goods.size()); j++) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("goods_id", ((Goods) gift_goods.get(j)).getId());
				map.put("goods_name", ((Goods) gift_goods.get(j)).getGoods_name());
				map.put("goods_main_photo",
						(gift_goods.get(j)).getGoods_main_photo().getPath() + "/"
								+ (gift_goods.get(j)).getGoods_main_photo().getName() + "_small."
								+ ( gift_goods.get(j)).getGoods_main_photo().getExt());
				map.put("goods_price", ((Goods) gift_goods.get(j)).getGoods_current_price());
				String goods_domainPath = web_url + "/items_" + ((Goods) gift_goods.get(j)).getId() + "";
				if ((config.getSecond_domain_open())
						&& (( gift_goods.get(j)).getGoods_store().getStore_second_domain() != "")) {
					if (( gift_goods.get(j)).getGoods_type() == 1) {
						String store_second_domain = "http://"
								+ ( gift_goods.get(j)).getGoods_store().getStore_second_domain() + "."
								+ CommUtil.generic_domain(request);
						goods_domainPath = store_second_domain + "/items_" + ((Goods) gift_goods.get(j)).getId() + "";
					}
				}
				map.put("goods_domainPath", goods_domainPath);
				map.put("buyGify_id", ( gift_goods.get(j)).getBuyGift_id());
				if (( gift_goods.get(j)).getBuyGift_id() != null) {
					BuyGift buyGift = (BuyGift) this.buyGiftDAO
							.selectByPrimaryKey(( gift_goods.get(j)).getBuyGift_id());
					List<Map> goods_list = this.orderFormTools.queryGoodsInfo(buyGift.getGoods_info());

					for (Map m : goods_list) {
						map.put("goods_main_id", m.get("goods_id"));
					}
				} else {
					map.put("goods_main_id", "");
				}
				gift_map.add(map);
			}
			double goods_amount = this.cartTools.getPriceByCarts(gc_list, gcs);
			List<SysMap> sms = this.transportTools.query_cart_trans(gc_list,
					CommUtil.null2String(addr.getArea().getId()));

			//快递相关的信息
			String transport_id = "";
			for (String temp_id : transport_ids) {
				if ((temp_id != null) && (!temp_id.equals("")) && (temp_id.equals("transport_" + sid))) {
					transport_id = temp_id;
				}
			}
			String transport = CommUtil.null2String(request.getParameter(transport_id));
			if ((CommUtil.null2String(transport).indexOf("平邮") < 0)
					&& (CommUtil.null2String(transport).indexOf("快递") < 0)
					&& (CommUtil.null2String(transport).indexOf("EMS") < 0)) {
				transport = "快递";
			}
			double ship_price = 0.0D;
			for (SysMap sm : sms) {
				if (CommUtil.null2String(sm.getKey()).indexOf(transport) >= 0) {
					ship_price = CommUtil.null2Double(sm.getValue());
				}
			}
			double totalPrice = CommUtil.add(Double.valueOf(goods_amount), Double.valueOf(ship_price));
			double commission_amount = this.cartTools.getCommissionByCarts(gc_list);//佣金
			Map ermap = this.cartTools.getEnoughReducePriceByCarts(gc_list, gcs);
			String er_json = (String) ermap.get("er_json");
			double all_goods = Double.parseDouble(ermap.get("all").toString());
			double reduce = Double.parseDouble(ermap.get("reduce").toString());
			OrderForm of = new OrderForm();
			of.setAddTime(new Date());

			of.setOrder_id(buyer.getId() + order_suffix + order_store_id);

			of.setReceiver_Name(addr.getTrueName());
			of.setReceiver_area(addr.getArea().getParent().getParent().getAreaName()
					+ addr.getArea().getParent().getAreaName() + addr.getArea().getAreaName());
			of.setReceiver_area_info(addr.getArea_info());
			of.setReceiver_mobile(addr.getMobile());
			of.setReceiver_telephone(addr.getTelephone());
			of.setReceiver_zip(addr.getZip());
			of.setReceiver_addr_id(addr.getId());
			of.setEnough_reduce_amount(BigDecimal.valueOf(reduce));
			of.setEnough_reduce_info(er_json);
			of.setTransport(transport);
			of.setOrder_status(10);
			of.setUser_id(buyer.getId().toString());
			of.setUser_name(buyer.getUserName());
			of.setGoods_info(JSON.toJSONString(map_list));
			of.setOrder_special(order_special);
			of.setShip_price(BigDecimal.valueOf(ship_price));
			of.setGoods_amount(BigDecimal.valueOf(all_goods));
			of.setTotalPrice(BigDecimal.valueOf(totalPrice));

			// 设置对应的秒杀或拼团活动信息
			of.setNuke(nuke);
			of.setCollage(collageBuy);

			String msg_id = "";
			for (String temp_id : msg_ids) {
				if ((temp_id != null) && (!temp_id.equals("")) && (temp_id.equals("msg_" + sid))) {
					msg_id = temp_id;
				}
			}
			of.setMsg(CommUtil.null2String(request.getParameter(msg_id)));

			of.setInvoiceType(invoiceType);
			VatInvoice va;
			Map temp_map;
			String special_invoice;
			if (invoiceType == 0) {
				of.setInvoice(invoice);
			} else {
				Map va_map = Maps.newHashMap();
				va_map.put("user_id", buyer.getId());
				va_map.put("status", 1);
				List<VatInvoice> va_list = this.vatInvoiceDAO.queryPageList(va_map);
				if (va_list.size() > 0) {
					va = (VatInvoice) ((List) va_list).get(0);
					temp_map = Maps.newHashMap();
					temp_map.put("companyName", va.getCompanyName());
					temp_map.put("taxpayerCode", va.getTaxpayerCode());
					temp_map.put("registerAddress", va.getRegisterAddress());
					temp_map.put("registerPhone", va.getRegisterPhone());
					temp_map.put("registerbankName", va.getRegisterbankName());
					temp_map.put("registerbankAccount", va.getRegisterbankAccount());
					special_invoice = JSON.toJSONString(temp_map);
					of.setSpecial_invoice(special_invoice);
				}
			}
			String coupon_id = "";
			for (String temp_id : coupon_ids) {
				if ((temp_id != null) && (!temp_id.equals(""))
						&& ((temp_id.equals("coupon_id_" + sid)) || (temp_id.equals("coupon_id")))) {
					coupon_id = temp_id;
				}
			}
			BigDecimal ci_price;
			if ((coupon_id != null) && (!coupon_id.equals(""))) {
				CouponInfo ci = (CouponInfo) this.couponInfoDAO
						.selectByPrimaryKey(CommUtil.null2Long(request.getParameter(coupon_id)));
				boolean next = false;
				if (ci != null) {
					if (ci.getStore_id() != null) {
						if (ci.getStore_id().equals(CommUtil.null2Long(sid))) {
							next = true;
						}else if (sid.equals("self")) {
							next = true;
						}
					} else if (sid.equals("self")) {
						next = true;
					}
					if ((next) && (buyer.getId().equals(ci.getUser().getId()))) {
						ci.setStatus(1);
						this.couponInfoDAO.updateById(ci);
						Map<String,Object> coupon_map = Maps.newHashMap();
						coupon_map.put("couponinfo_id", ci.getId());
						coupon_map.put("couponinfo_sn", ci.getCoupon_sn());
						coupon_map.put("coupon_amount", ci.getCoupon().getCoupon_amount());
						double rate = CommUtil.div(ci.getCoupon().getCoupon_amount(), Double.valueOf(goods_amount));
						coupon_map.put("coupon_goods_rate", Double.valueOf(rate));
						coupon_map.put("price", Double.valueOf(goods_amount));
						of.setCoupon_info(JSON.toJSONString(coupon_map));
						ci_price = ci.getCoupon().getCoupon_amount();
						totalPrice = CommUtil.subtract(Double.valueOf(totalPrice), ci_price);
						of.setTotalPrice(BigDecimal.valueOf(totalPrice));
					}
				}
			}
			if ((sid.equals("self")) || (sid == "self")) {
				of.setOrder_form(1);
			} else {
				of.setCommission_amount(BigDecimal.valueOf(commission_amount));
				of.setOrder_form(0);
				of.setStore_id(store.getId().toString());
				of.setStore_name(store.getStore_name());
			}
			of.setOrder_type(order_type);

			of.setDelivery_time(delivery_time);
			of.setDelivery_type(0);
			if ((CommUtil.null2Int(delivery_type) == 1) && (delivery_id != null) && (!delivery_id.equals(""))) {
				of.setDelivery_type(1);
				DeliveryAddress deliveryAddr = (DeliveryAddress) this.deliveryAddressDAO
						.selectByPrimaryKey(CommUtil.null2Long(delivery_id));
				String service_time = "全天";
				if (deliveryAddr.getDa_service_type() == 1) {
					service_time = deliveryAddr.getDa_begin_time() + "点至" + deliveryAddr.getDa_end_time() + "点";
				}
				Map<String,Object> params = Maps.newHashMap();
				params.put("id", deliveryAddr.getId());
				params.put("da_name", deliveryAddr.getDa_name());
				params.put("da_content", deliveryAddr.getDa_content());
				params.put("da_contact_user", deliveryAddr.getDa_contact_user());
				params.put("da_tel", deliveryAddr.getDa_tel());
				params.put("da_address",
						deliveryAddr.getDa_area().getParent().getParent().getAreaName()
								+ deliveryAddr.getDa_area().getParent().getAreaName()
								+ deliveryAddr.getDa_area().getAreaName() + deliveryAddr.getDa_address());
				params.put("da_service_day",
						this.deliveryAddressTools.query_service_day(deliveryAddr.getDa_service_day()));
				params.put("da_service_time", service_time);
				of.setDelivery_address_id(deliveryAddr.getId());
				of.setDelivery_info(JSON.toJSONString(params));
			}
			if (i == store_ids.length - 1) {
				of.setOrder_main(1);
				if (gift_map.size() > 0) {
					of.setGift_infos(JSON.toJSONString(gift_map));
					of.setWhether_gift(1);
				}
				if (child_order_maps.size() > 0) {
					of.setChild_order_detail(JSON.toJSONString(child_order_maps));
				}
			}
			//保存订单信息
			this.orderFormDAO.saveEntity(of);
			main_order = of;
			if (store_ids.length > 1) {
				Map<String,Object> order_map = Maps.newHashMap();
				order_map.put("order_id", of.getId());
				order_map.put("order_goods_info", of.getGoods_info());
				child_order_maps.add(order_map);
			}
			for (Object service_time = gc_list.iterator(); ((Iterator) service_time).hasNext();) {
				GoodsCart gc = (GoodsCart) ((Iterator) service_time).next();
				if ((gc.getCart_type() != null) && (gc.getCart_type().equals("combin")) && (gc.getCombin_main() == 1)) {
					Map<String, Object> combin_map = Maps.newHashMap();
					combin_map.put("combin_mark", gc.getCombin_mark());
					combin_map.put("combin_main_no", Integer.valueOf(1));

					List<GoodsCart> suits = this.goodsCartDAO.queryPageList(combin_map);

					for (GoodsCart suit : suits) {
						suit.getGsps().clear();

						this.goodsSpecPropertyDao.deleteGoodsCartAndGoodsSpecProperty(suit.getId(), suit.getGsps());

						this.goodsCartDAO.deleteById(suit.getId());
					}
					gc.getGsps().clear();

					this.goodsSpecPropertyDao.deleteGoodsCartAndGoodsSpecProperty(gc.getId(), gc.getGsps());

					this.goodsCartDAO.deleteById(gc.getId());
				} else {
					gc.getGsps().clear();
					this.goodsSpecPropertyDao.deleteGoodsCartAndGoodsSpecProperty(gc.getId(), gc.getGsps());
					this.goodsCartDAO.deleteById(gc.getId());
				}
			}
			OrderFormLog ofl = new OrderFormLog();
			ofl.setAddTime(new Date());
			ofl.setOf(of);
			ofl.setLog_info("提交订单");
			ofl.setLog_user_id(buyer.getId());
			ofl.setLog_user_name(buyer.getUserName());
			this.orderFormLogDAO.saveEntity(ofl);
		}
		this.orderFormTools.updateGoodsInventory(main_order);

		try {
			this.orderFormTools.sendMsgWhenHandleOrder(web_url, main_order, "tobuyer_order_submit_ok_notify",
					"toseller_order_submit_ok_notify");
		} catch (Exception e) {
			System.out.println("系统未开启短信通知服务。。。。");
		}
		this.orderFormTools.createOrderGoodsSnapshot(main_order,
				request.getSession().getServletContext().getRealPath("/"), buyer);
		return main_order;
	}
	
	/**
	 * 评价订单
	 */
	@Transactional(rollbackFor = { Exception.class })
	public void evaluateOrderForm(HttpServletRequest request,
			String checkState, OrderForm obj) {
		
		User buyer = this.userDAO.selectByPrimaryKey(CommUtil.null2Long(obj.getUser_id()));
		
		String username = buyer.getUserName();
		
		if ("0".equals(checkState)) {
			String head = username.substring(0, 1);
			String bottom = username.substring(username.length() - 1, username.length());
			username = head + "*****" + bottom;
		}
		
		request.getSession(false).removeAttribute("evaluate_session");//删除评价session
		obj.setOrder_status(50); //50买家评价完毕
		obj.setFinishTime(new Date());//评价完成时间
		this.orderFormDAO.updateById(obj);
		
		//记录订单的日志类:用来记录所有订单操作，包括订单提交、付款、发货、收货等等
		OrderFormLog ofl = new OrderFormLog();
		ofl.setAddTime(new Date());
		ofl.setLog_info("评价订单");//订单日志为评价订单操作
		ofl.setLog_user_id(buyer.getId());
		ofl.setLog_user_name(buyer.getUserName());
		ofl.setOf(obj);
		this.orderFormLogDao.saveEntity(ofl);
		//获取商品信息:json格式转换为List<Map>格式
		/**
		 * 使用json管理"
		 * 	[{  "goods_id":1,
		 * 		"goods_name":"佐丹奴男装翻领T恤 条纹修身商务男POLO纯棉短袖POLO衫",
		 *      "goods_type":"group",
		 *      "goods_choice_type":1,
		 *      "goods_cat":0,
		 *      "goods_commission_rate":"0.8",
		 *      "goods_commission_price":"16.00",
		 *      "goods_payoff_price":"234",
		 *      "goods_type":"combin",
		 *      "goods_count":2,
		 *      "goods_price":100,
		 *      "goods_all_price":200,
		 *      "goods_gsp_ids":"/1/3",
		 *      "goods_gsp_val":"尺码：XXL",
		 *      "goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg",
		 *      "goods_return_status":"商品退货状态 当有此字段为“”时可退货状态 5为已申请退货，6商家已同意退货，7用户填写退货物流，8商家已确认，提交平台已退款 -1已经超出可退货时间",
		 *      "goods_complaint_status" 没有此字段时可投诉 投诉后的状态为1不可投诉
		 *  }]"
		 */
		List<Map> json = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
		
		for (Map map : json) {
			
			Evaluate eva = new Evaluate();
			String goods_gsp_ids = map.get("goods_gsp_ids").toString();
			goods_gsp_ids = goods_gsp_ids.replaceAll(",", "_");
			String goods_id = map.get("goods_id") + "_" + goods_gsp_ids;
			Goods goods = this.goodsDAO.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
			eva.setAddTime(new Date());//添加时间
			eva.setEvaluate_goods(goods);//被评价的商品
			goods.setEvaluate_count(goods.getEvaluate_count() + 1);//评价数量
			eva.setGoods_num(CommUtil.null2Int(map.get("goods_count")));//评价商品数量
			eva.setGoods_price(map.get("goods_price").toString());//评价商品价格
			eva.setGoods_spec(map.get("goods_gsp_val").toString());//评价商品规格
			eva.setEvaluate_info(request.getParameter("evaluate_info_" + goods_id));//评价商品id信息
			eva.setEvaluate_photos(request.getParameter("evaluate_photos_" + goods_id));//评价商品图片
			
			//买家评价，评价类型，1为好评，0为中评，-1为差评
			eva.setEvaluate_buyer_val(CommUtil.null2Int(eva_total_rate(request.getParameter("evaluate_buyer_val" + goods_id))));
			// 描述相符评价
			eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request.getParameter("description_evaluate" + goods_id)))));
			// 服务态度评价
			eva.setService_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request.getParameter("service_evaluate" + goods_id)))));
			// 发货速度评价
			eva.setShip_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request.getParameter("ship_evaluate" + goods_id)))));
			
			eva.setEvaluate_type("goods");//评价类型为商品
			eva.setEvaluate_user(buyer);//评价用户
			eva.setOf(obj);//评价订单
			eva.setReply_status(0);//评价回复的状态默认为0未回复 已回复为1
			eva.setUserName(username);//评价用户名
			this.evaluateDAO.saveEntity(eva);//保存评价
			
			String im_str = request.getParameter("evaluate_photos_" + goods_id);
			
			if ((im_str != null) 
					&& (!im_str.equals(""))
					&& (im_str.length() > 0)) {
				
				for (String str:im_str.split(",")) {
					if ((str != null) && (!str.equals(""))) {
						Accessory image = (Accessory) this.accessoryDAO.selectByPrimaryKey(CommUtil.null2Long(str));
						if ((image.getInfo() != null) && (image.getInfo().equals("eva_temp"))) {
							image.setInfo("eva_img");//修改附件图片类型为评价图片
							this.accessoryDAO.updateById(image);
						}
					}
				}
			}
			
			Map<String,Object> params = Maps.newHashMap();
			StorePoint point = null;
			double store_evaluate = 0.0D;
			double description_evaluate = 0.0D;
			double description_evaluate_total = 0.0D;
			double service_evaluate = 0.0D;
			double service_evaluate_total = 0.0D;
			double ship_evaluate = 0.0D;
			double ship_evaluate_total = 0.0D;
			
			List<StorePoint> sps = Lists.newArrayList();
			List<Evaluate> evas = Lists.newArrayList();
			if (goods.getGoods_type() == 1) {
				Store store = (Store) this.storeDAO.selectByPrimaryKey(CommUtil.null2Long(goods.getGoods_store().getId()));
				params.put("store_id", store.getId().toString());
				
				evas = this.evaluateDAO.queryPageList(params);
				
				store.setStore_credit(store.getStore_credit() + eva.getEvaluate_buyer_val());
				this.storeDAO.updateById(store);
				params.clear();
				params.put("store_id", store.getId());
				sps = this.storePointDAO.queryPageList(params);
				
				if (sps.size() > 0) {
					point = sps.get(0);
				} else {
					point = new StorePoint();
				}
				point.setStore(store);
				
			} else {
				User sp_user = this.userDAO.selectByPrimaryKey(obj.getEva_user_id());
				params.put("user_id", buyer.getId().toString());
				evas = this.evaluateDAO.queryPageList(params);
				params.clear();
				params.put("user_id", obj.getEva_user_id());
				sps = this.storePointDAO.queryPageList(params);
				if (sps.size() > 0) {
					point = (StorePoint) sps.get(0);
				} else {
					point = new StorePoint();
				}
				point.setAddTime(new Date());
				point.setUser(sp_user);
			}
			
			DecimalFormat df = new DecimalFormat("0.0");
			for (Evaluate eva1 : evas) {
				// 描述相符评价
				description_evaluate_total = description_evaluate_total + CommUtil.null2Double(eva1.getDescription_evaluate());
				// 服务态度评价
				service_evaluate_total = service_evaluate_total + CommUtil.null2Double(eva1.getService_evaluate());
				// 发货速度评价
				ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
			}
			// 描述相符评价
			description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
			// 服务态度评价
			service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
			// 发货速度评价
			ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
			//店铺总体评价
			store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3.0D;
			
			point.setAddTime(new Date());//添加时间
			// 设置描述相符评价
			point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate > 5.0D ? 5.0D	: description_evaluate));
			// 设置服务态度评价
			point.setService_evaluate(BigDecimal.valueOf(service_evaluate > 5.0D ? 5.0D : service_evaluate));
			// 设置发货速度评价
			point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate > 5.0D ? 5.0D : ship_evaluate));
			// 设置店铺总体评价
			point.setStore_evaluate(BigDecimal.valueOf(store_evaluate > 5.0D ? 5.0D : store_evaluate));
			
			if (sps.size() > 0) {//更新店铺统计类
				this.storePointDAO.updateById(point);
			} else {//保存店铺统计类
				this.storePointDAO.saveEntity(point);
			}
			this.goodsDAO.updateById(goods);
		}
		//买家积分 = 原有积分 + 订单评论(赠送积分)
		buyer.setIntegral(buyer.getIntegral() + this.configService.getSysConfig().getIndentComment());
		
		/**
		 * 该用户总商品消费金额=原来消费金额+当前订单消费金额
		 * 1、用于计算用户等级，消费越高，等级越高。
		 * 2、平台发放优惠券时（如：限制人数100人），按照用户消费金额排序，前100人可以得到该优惠券
		 */
		buyer.setUser_goods_fee(BigDecimal.valueOf(CommUtil.add(buyer.getUser_goods_fee(), obj.getTotalPrice())));
		
		this.userDAO.updateById(buyer);//更新买家信息
		//如果系统开启了积分商城
		if (this.configService.getSysConfig().getIntegral()) {
			IntegralLog log = new IntegralLog();
			log.setAddTime(new Date());
			log.setContent("订单评价增加"
					+ this.configService.getSysConfig().getIndentComment()
					+ "分");
			
			log.setIntegral(this.configService.getSysConfig().getIndentComment());
			log.setIntegral_user(buyer);//获得积分用户
			log.setType("order");//积分类型:订单
			this.integralLogDao.saveEntity(log);//保存积分
		}
		
		if ((this.configService.getSysConfig().getEmailEnable()) 
				&& (obj.getOrder_form() == 0) 
				&& (obj.getOrder_status() != 25)) {
			
			Store store = this.storeDAO.selectByPrimaryKey(CommUtil.null2Long(obj.getStore_id()));
			Map<String,Object> map = Maps.newHashMap();
			map.put("seller_id", store.getUser().getId().toString());
			map.put("order_id", obj.getId().toString());
			String str = JSON.toJSONString(map);
			try {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),"email_toseller_evaluate_ok_notify", store.getUser().getEmail(), str, null, obj.getStore_id());
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println("邮件发送未开启");
			}
		}
		//更新订单商铺销量
		this.orderFormTools.updateOrderGoodsSaleNum(obj);
	}

	private StoreHouse getStoreHouse(Address addr) {
		StoreHouse sh = new StoreHouse();
		Map<String,Object> params = Maps.newHashMap();
		params.put("sh_area_like", "id\":" + addr.getArea().getParent().getId());
		
		List<StoreHouse> storeHouses = this.storeHouseDAO.queryPageList(params,0,1);
		sh = (StoreHouse) storeHouses.get(0);
		return sh;
	}

	private int eva_rate(String rate) {
		
		int score = 0;
		if (rate.equals("a")) {
			score = 1;
		} else if (rate.equals("b")) {
			score = 2;
		} else if (rate.equals("c")) {
			score = 3;
		} else if (rate.equals("d")) {
			score = 4;
		} else if (rate.equals("e")) {
			score = 5;
		}
		return score;
	}

	private int eva_total_rate(String rate) {
		
		int score = -5;
		if (rate.equals("a")) {
			score = 1;
		} else if (rate.equals("b")) {
			score = 0;
		} else if (rate.equals("c")) {
			score = -1;
		}
		return score;
	}
}
