package com.redpigmall.logic.service;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.logic.service.RedPigQuartzService;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.manage.admin.tools.RedPigGoodsTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigStatTools;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.msg.SpelTemplate;
import com.redpigmall.dao.AccessoryMapper;
import com.redpigmall.dao.ActivityGoodsMapper;
import com.redpigmall.dao.ActivityMapper;
import com.redpigmall.dao.AppPushLogMapper;
import com.redpigmall.dao.BuyGiftMapper;
import com.redpigmall.dao.CombinPlanMapper;
import com.redpigmall.dao.CouponInfoMapper;
import com.redpigmall.dao.EnoughReduceMapper;
import com.redpigmall.dao.EvaluateMapper;
import com.redpigmall.dao.FavoriteMapper;
import com.redpigmall.dao.FreeApplyLogMapper;
import com.redpigmall.dao.FreeGoodsMapper;
import com.redpigmall.dao.GoodsCartMapper;
import com.redpigmall.dao.GoodsClassMapper;
import com.redpigmall.dao.GoodsLogMapper;
import com.redpigmall.dao.GoodsMapper;
import com.redpigmall.dao.GroupGoodsMapper;
import com.redpigmall.dao.GroupInfoMapper;
import com.redpigmall.dao.GroupLifeGoodsMapper;
import com.redpigmall.dao.GroupMapper;
import com.redpigmall.dao.IntegralGoodsCartMapper;
import com.redpigmall.dao.IntegralLogMapper;
import com.redpigmall.dao.MessageMapper;
import com.redpigmall.dao.OrderFormLogMapper;
import com.redpigmall.dao.OrderFormMapper;
import com.redpigmall.dao.PaymentMapper;
import com.redpigmall.dao.PayoffLogMapper;
import com.redpigmall.dao.ReturnGoodsLogMapper;
import com.redpigmall.dao.StoreMapper;
import com.redpigmall.dao.StorePointMapper;
import com.redpigmall.dao.StoreStatMapper;
import com.redpigmall.dao.SysConfigMapper;
import com.redpigmall.dao.TemplateMapper;
import com.redpigmall.dao.UserMapper;
import com.redpigmall.dao.VerifyCodeMapper;
import com.redpigmall.dao.ZTCGoldLogMapper;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.CombinPlan;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.FreeGoods;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupInfo;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.IntegralGoodsCart;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.domain.PayoffLog;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.domain.StoreStat;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.Template;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VerifyCode;
import com.redpigmall.domain.ZTCGoldLog;
import com.redpigmall.service.RedPigCloudPurchaseLotteryService;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;

@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
@Service
@Transactional
public class RedPigQuartzService {
	@Autowired
	private GoodsMapper goodsDAO;
	@Autowired
	private GoodsLogMapper goodsLogDAO;
	@Autowired
	private ZTCGoldLogMapper zTCGlodLogDAO;
	@Autowired
	private StoreMapper storeDAO;
	@Autowired
	private TemplateMapper templateDAO;
	@Autowired
	private UserMapper userDAO;
	@Autowired
	private MessageMapper messageDAO;
	@Autowired
	private GoodsCartMapper goodsCartDAO;
	@Autowired
	private PayoffLogMapper payoffLogDAO;
	@Autowired
	private OrderFormMapper orderFormDAO;
	@Autowired
	private GroupLifeGoodsMapper groupLifeGoodsDAO;
	@Autowired
	private GroupGoodsMapper groupGoodsDAO;
	@Autowired
	private CouponInfoMapper couponInfoDAO;
	@Autowired
	private GroupInfoMapper groupInfoDAO;
	@Autowired
	private FreeGoodsMapper freeGoodsDAO;
	@Autowired
	private CombinPlanMapper combinPlanDAO;
	@Autowired
	private BuyGiftMapper buyGiftDAO;
	@Autowired
	private IntegralGoodsCartMapper integralGoodsCartDAO;
	@Autowired
	private EnoughReduceMapper enoughReduceDAO;
	@Autowired
	private StoreStatMapper storeStatDAO;
	@Autowired
	private VerifyCodeMapper mobileVerifyCodeDAO;
	@Autowired
	private GoodsClassMapper goodsClassDAO;
	@Autowired
	private StorePointMapper storePointDAO;
	@Autowired
	private GroupMapper groupDAO;
	@Autowired
	private ActivityMapper activityDAO;
	@Autowired
	private ActivityGoodsMapper activityGoodsDAO;
	@Autowired
	private OrderFormLogMapper orderFormLogDAO;
	@Autowired
	private ReturnGoodsLogMapper returnGoodsLogDAO;
	@Autowired
	private EvaluateMapper evaluateDAO;
	@Autowired
	private AccessoryMapper accessoryDAO;
	@Autowired
	private FavoriteMapper favoriteDAO;
	@Autowired
	private IntegralLogMapper integralLogDao;
	@Autowired
	private PaymentMapper paymentDao;
	@Autowired
	private AppPushLogMapper appPushLogDAO;
	@Autowired
	private SysConfigMapper sysConfigDAO;
	@Autowired
	private FreeApplyLogMapper freeApplyLogDAO;
	
	@Autowired
	private RedPigStatTools statTools;
	@Autowired
	private RedPigGoodsTools goodsTools;
	@Autowired
	private RedPigMsgTools msgTools;
	@Autowired
	private RedPigOrderFormTools orderFormTools;
	@Autowired
	private RedPigGoodsViewTools goodsViewTools;
	@Autowired
	private RedPigCloudPurchaseLotteryService cloudPurchaseLotteryService;

	private boolean send_email(OrderForm order, String mark) throws Exception {
		SysConfig sc = getSysConfig();
		Map<String,Object> params = Maps.newHashMap();
		
		params.put("operation_property", "mark");
		
		params.put("operation_symbol", "=");
		
		params.put("operation_value", mark);
		
		Template template = (Template) this.templateDAO.selectObjByProperty(params);
		
		if ((template != null) && (template.getOpen())) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			Store store = (Store) this.storeDAO.selectByPrimaryKey(CommUtil.null2Long(order.getStore_id()));
			String email = store.getUser().getEmail();
			String subject = template.getTitle();
			User buyer = (User) this.userDAO.selectByPrimaryKey(CommUtil.null2Long(order.getUser_id()));
			context.setVariable("buyer", buyer);
			context.setVariable("seller", store.getUser());
			context.setVariable("config", sc);
			context.setVariable("send_time",CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", sc.getAddress());
			context.setVariable("order", order);
			Expression ex = exp.parseExpression(template.getContent(),new SpelTemplate());
			String content = (String) ex.getValue(context, String.class);
			
			boolean ret = this.msgTools.sendEmail(email, subject, content);
			return ret;
		}
		return false;
	}

	private boolean send_sms(OrderForm order, String mobile, String mark)
			throws Exception {
		SysConfig sc = getSysConfig();
		Store store = (Store) this.storeDAO.selectByPrimaryKey(CommUtil.null2Long(order.getStore_id()));
		Map<String,Object> params = Maps.newHashMap();
		params.put("mark",mark);
		
		Template template = (Template) this.templateDAO.queryPageList(params);
		
		if ((template != null) && (template.getOpen())) {
			ExpressionParser exp = new SpelExpressionParser();
			EvaluationContext context = new StandardEvaluationContext();
			User buyer = (User) this.userDAO.selectByPrimaryKey(CommUtil.null2Long(order.getUser_id()));
			context.setVariable("buyer", buyer);
			context.setVariable("seller", store.getUser());
			context.setVariable("config", sc);
			context.setVariable("send_time",
					CommUtil.formatLongDate(new Date()));
			context.setVariable("webPath", sc.getAddress());
			context.setVariable("order", order);
			Expression ex = exp.parseExpression(template.getContent(),
					new SpelTemplate());
			String content = (String) ex.getValue(context, String.class);
			boolean ret = this.msgTools.sendSMS(mobile, content);
			return ret;
		}
		return false;
	}

	/**
	 * 更新库存
	 * @param order
	 */
	private void update_goods_inventory(OrderForm order) {
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(order);
		for (Goods goods : goods_list) {
			int goods_count = this.orderFormTools.queryOfGoodsCount(
					CommUtil.null2String(order.getId()),
					CommUtil.null2String(goods.getId()));
			if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						gg.setGg_count(gg.getGg_count() - goods_count);
						this.groupGoodsDAO.updateById(gg);
						this.goodsTools.updateGroupGoodsLucene(gg);
					}
				}
			}
			List<String> gsps = Lists.newArrayList();
			List<GoodsSpecProperty> temp_gsp_list = this.orderFormTools
					.queryOfGoodsGsps(CommUtil.null2String(order.getId()),
							CommUtil.null2String(goods.getId()));
			String spectype = "";
			for (GoodsSpecProperty gsp : temp_gsp_list) {
				gsps.add(gsp.getId().toString());
				spectype = spectype + gsp.getSpec().getName() + ":"
						+ gsp.getValue() + " ";
			}
			String[] gsp_list = new String[gsps.size()];
			gsps.toArray(gsp_list);
			this.goodsTools.save_salenum_goodsLog(order, goods, goods_count,
					spectype);
			String inventory_type = goods.getInventory_type() == null ? "all"
					: goods.getInventory_type();
			if (inventory_type.equals("all")) {
				goods.setGoods_inventory(goods.getGoods_inventory()
						- goods_count);
			} else {
				List<HashMap> list = JSON.parseArray(
						goods.getGoods_inventory_detail(), HashMap.class);
				for (Map temp : list) {

					String[] temp_ids = CommUtil.null2String(temp.get("id"))
							.split("_");
					Arrays.sort(temp_ids);
					Arrays.sort(gsp_list);
					if (Arrays.equals(temp_ids, gsp_list)) {
						temp.put(
								"count",
								Integer.valueOf(CommUtil.null2Int(temp
										.get("count")) - goods_count));
					}
				}
				goods.setGoods_inventory_detail(JSON.toJSONString(list));
			}
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if ((gg.getGroup().getId().equals(goods.getGroup().getId()))
						&& (gg.getGg_count() == 0)) {
					goods.setGroup_buy(3);
				}
			}
			this.goodsDAO.updateById(goods);

			this.goodsTools.updateGoodsLucene(goods);
		}
	}

	@Transactional(readOnly = true)
	public SysConfig getSysConfig() {
		List<SysConfig> configs = this.sysConfigDAO.queryPageList(RedPigMaps.newMap());
		
		if ((configs != null) && (configs.size() > 0)) {
			SysConfig sc = (SysConfig) configs.get(0);
			if (sc.getUploadFilePath() == null) {
				sc.setUploadFilePath("upload");
			}
			if (sc.getSysLanguage() == null) {
				sc.setSysLanguage("zh_cn");
			}
			if ((sc.getWebsiteName() == null)
					|| (sc.getWebsiteName().equals(""))) {
				sc.setWebsiteName("RedPigMall");
			}
			if ((sc.getCloseReason() == null)
					|| (sc.getCloseReason().equals(""))) {
				sc.setCloseReason("系统维护中...");
			}
			if ((sc.getTitle() == null) || (sc.getTitle().equals(""))) {
				sc.setTitle("RedPigMall B2B2C商城系统");
			}
			if ((sc.getImageSaveType() == null)
					|| (sc.getImageSaveType().equals(""))) {
				sc.setImageSaveType("sidImg");
			}
			if (sc.getImageFilesize() == 0) {
				sc.setImageFilesize(1024);
			}
			if (sc.getSmallWidth() == 0) {
				sc.setSmallWidth(160);
			}
			if (sc.getSmallHeight() == 0) {
				sc.setSmallHeight(160);
			}
			if (sc.getMiddleWidth() == 0) {
				sc.setMiddleWidth(300);
			}
			if (sc.getMiddleHeight() == 0) {
				sc.setMiddleHeight(300);
			}
			if (sc.getBigHeight() == 0) {
				sc.setBigHeight(1024);
			}
			if (sc.getBigWidth() == 0) {
				sc.setBigWidth(1024);
			}
			if ((sc.getImageSuffix() == null)
					|| (sc.getImageSuffix().equals(""))) {
				sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
			}
			if (sc.getStoreImage() == null) {
				Accessory storeImage = new Accessory();
				storeImage.setPath("resources/style/common/images");
				storeImage.setName("store.jpg");
				sc.setStoreImage(storeImage);
			}
			if (sc.getGoodsImage() == null) {
				Accessory goodsImage = new Accessory();
				goodsImage.setPath("resources/style/common/images");
				goodsImage.setName("good.jpg");
				sc.setGoodsImage(goodsImage);
			}
			if (sc.getMemberIcon() == null) {
				Accessory memberIcon = new Accessory();
				memberIcon.setPath("resources/style/common/images");
				memberIcon.setName("member.jpg");
				sc.setMemberIcon(memberIcon);
			}
			if (sc.getCurrency_code() == null) {
				sc.setCurrency_code("¥");
			}
			if ((sc.getSecurityCodeType() == null)
					|| (sc.getSecurityCodeType().equals(""))) {
				sc.setSecurityCodeType("normal");
			}
			if ((sc.getWebsiteCss() == null) || (sc.getWebsiteCss().equals(""))) {
				sc.setWebsiteCss("blue");
			}
			if (sc.getPayoff_date() == null) {
				Calendar cale = Calendar.getInstance();
				cale.set(5, cale.getActualMaximum(5));
				sc.setPayoff_date(cale.getTime());
			}
			if ((sc.getSmsURL() == null) || (sc.getSmsURL().equals(""))) {
				sc.setSmsURL("http://service.winic.org/sys_port/gateway/");
			}
			if (sc.getAuto_order_notice() == 0) {
				sc.setAuto_order_notice(3);
			}
			if (sc.getAuto_order_evaluate() == 0) {
				sc.setAuto_order_evaluate(7);
			}
			if (sc.getAuto_order_return() == 0) {
				sc.setAuto_order_return(7);
			}
			if (sc.getAuto_order_confirm() == 0) {
				sc.setAuto_order_confirm(7);
			}
			if (sc.getGrouplife_order_return() == 0) {
				sc.setGrouplife_order_return(7);
			}
			return sc;
		}
		SysConfig sc = new SysConfig();
		sc.setUploadFilePath("upload");
		sc.setWebsiteName("RedPigMall");
		sc.setSysLanguage("zh_cn");
		sc.setTitle("RedPigMall B2B2C商城系统");
		sc.setSecurityCodeType("normal");
		sc.setEmailEnable(true);
		sc.setCloseReason("系统维护中...");
		sc.setImageSaveType("sidImg");
		sc.setImageFilesize(1024);
		sc.setSmallWidth(160);
		sc.setSmallHeight(160);
		sc.setMiddleHeight(300);
		sc.setMiddleWidth(300);
		sc.setBigHeight(1024);
		sc.setBigWidth(1024);
		sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
		sc.setComplaint_time(30);
		sc.setWebsiteCss("blue");
		sc.setSmsURL("http://service.winic.org/sys_port/gateway/");
		Accessory goodsImage = new Accessory();
		goodsImage.setPath("resources/style/common/images");
		goodsImage.setName("good.jpg");
		sc.setGoodsImage(goodsImage);
		Accessory storeImage = new Accessory();
		storeImage.setPath("resources/style/common/images");
		storeImage.setName("store.jpg");
		sc.setStoreImage(storeImage);
		Accessory memberIcon = new Accessory();
		memberIcon.setPath("resources/style/common/images");
		memberIcon.setName("member.jpg");
		sc.setMemberIcon(memberIcon);
		Calendar cale = Calendar.getInstance();
		cale.set(5, cale.getActualMaximum(5));
		sc.setCurrency_code("¥");
		sc.setPayoff_date(cale.getTime());
		sc.setAuto_order_notice(3);
		sc.setAuto_order_evaluate(7);
		sc.setAuto_order_return(7);
		sc.setAuto_order_confirm(7);
		sc.setGrouplife_order_return(7);
		return sc;
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_1() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.goodsClassDAO.queryPageList(params);
		
		for (GoodsClass gc : gcs) {
			double description_evaluate = 0.0D;
			double service_evaluate = 0.0D;
			double ship_evaluate = 0.0D;
			params.clear();
			params.put("gc_main_id", gc.getId());
			params.put("store_status", Integer.valueOf(15));
			
			List<StorePoint> sp_list = this.storePointDAO.queryPageList(params);
			
			for (StorePoint sp : sp_list) {
				description_evaluate = CommUtil.add(
						Double.valueOf(description_evaluate),
						sp.getDescription_evaluate());
				service_evaluate = CommUtil.add(
						Double.valueOf(service_evaluate),
						sp.getService_evaluate());
				ship_evaluate = CommUtil.add(Double.valueOf(ship_evaluate),
						sp.getShip_evaluate());
			}
			gc.setDescription_evaluate(BigDecimal.valueOf(CommUtil.div(
					Double.valueOf(description_evaluate),
					Integer.valueOf(sp_list.size()))));
			gc.setService_evaluate(BigDecimal.valueOf(CommUtil.div(
					Double.valueOf(service_evaluate),
					Integer.valueOf(sp_list.size()))));
			gc.setShip_evaluate(BigDecimal.valueOf(CommUtil.div(
					Double.valueOf(ship_evaluate),
					Integer.valueOf(sp_list.size()))));
			this.goodsClassDAO.updateById(gc);
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_2() {
		SysConfig sc = getSysConfig();
		
		List<StoreStat> stats = this.storeStatDAO.queryPageList(RedPigMaps.newMap());
		
		StoreStat stat = null;
		if (stats.size() > 0) {
			stat = (StoreStat) stats.get(0);
		} else {
			stat = new StoreStat();
		}
		stat.setAddTime(new Date());
		Calendar cal = Calendar.getInstance();
		cal.add(12, 30);
		stat.setNext_time(cal.getTime());
		stat.setWeek_complaint(this.statTools.query_complaint(-7));
		stat.setWeek_goods(this.statTools.query_goods(-7));
		stat.setWeek_order(this.statTools.query_order(-7));
		stat.setWeek_store(this.statTools.query_store(-7));
		stat.setWeek_user(this.statTools.query_user(-7));
		stat.setWeek_live_user(this.statTools.query_live_user(-7));
		stat.setWeek_ztc(this.statTools.query_ztc(-7));
		stat.setWeek_delivery(this.statTools.query_delivery(-7));
		stat.setWeek_information(this.statTools.query_information(-7));
		stat.setWeek_invitation(this.statTools.query_invitation(-7));
		stat.setWeek_circle(this.statTools.query_circle(-7));
		stat.setAll_goods(this.statTools.query_all_goods());
		stat.setAll_store(this.statTools.query_all_store());
		stat.setAll_user(this.statTools.query_all_user());
		stat.setStore_audit(this.statTools.query_audit_store());
		stat.setOrder_amount(BigDecimal.valueOf(this.statTools
				.query_all_amount()));
		stat.setNot_payoff_num(this.statTools.query_payoff());
		stat.setNot_refund(this.statTools.query_refund());
		stat.setNot_grouplife_refund(this.statTools.query_grouplife_refund());
		stat.setAll_sale_amount(CommUtil.null2Int(sc.getPayoff_all_sale()));
		stat.setAll_commission_amount(CommUtil.null2Int(sc
				.getPayoff_all_commission()));
		stat.setAll_payoff_amount(CommUtil.null2Int(sc.getPayoff_all_amount()));
		stat.setAll_payoff_amount_reality(CommUtil.null2Int(sc
				.getPayoff_all_amount_reality()));
		stat.setAll_user_balance(BigDecimal.valueOf(this.statTools
				.query_all_user_balance()));
		stat.setZtc_audit_count(this.statTools.query_ztc_audit());
		stat.setDelivery_audit_count(this.statTools.query_delivery_audit());
		stat.setInformation_audit_count(this.statTools
				.query_information_audit());
		stat.setSelf_goods(this.statTools.query_self_goods());
		stat.setSelf_storage_goods(this.statTools.query_self_storage_goods());
		stat.setSelf_order_shipping(this.statTools.query_self_order_shipping());
		stat.setSelf_order_pay(this.statTools.query_self_order_pay());
		stat.setSelf_order_evaluate(this.statTools.query_self_order_evaluate());
		stat.setSelf_all_order(this.statTools.query_self_all_order());
		stat.setSelf_return_apply(this.statTools.query_self_return_apply());
		stat.setSelf_grouplife_refund(this.statTools
				.query_self_groupinfo_return_apply());
		stat.setGoods_audit(this.statTools.query_goods_audit());
		stat.setSelf_goods_consult(this.statTools.query_self_consult());
		stat.setSelf_activity_goods(this.statTools.query_self_activity_goods());
		stat.setSelf_group_goods(this.statTools.query_self_group_goods());
		stat.setSelf_group_life(this.statTools.query_self_group_life());
		stat.setSelf_free_goods(this.statTools.query_self_free_goods());
		stat.setPredepositcash_apply_count(this.statTools
				.query_predepositcash_apply());
		if (stats.size() > 0) {
			this.storeStatDAO.updateById(stat);
		} else {
			this.storeStatDAO.saveEntity(stat);
		}
		cal.setTime(new Date());
		cal.add(12, -30);
		Map<String, Object> params = Maps.newHashMap();
		params.put("add_Time_less_than_equal", cal.getTime());
		
		List<VerifyCode> mvcs = this.mobileVerifyCodeDAO.queryPageList(params);
		
		for (VerifyCode vc : mvcs) {
			this.mobileVerifyCodeDAO.deleteById(vc.getId());
		}
		params.clear();
		params.put("info", "eva_temp");
		
		List<Accessory> acc = this.accessoryDAO.queryPageList(params);
		
		boolean ret;
		for (Accessory accessory : acc) {
			ret = CommUtil.deleteFile(System.getProperty("redpigmall.root")
					+ File.separator + accessory.getPath() + File.separator
					+ accessory.getName());
			if (ret) {
				this.accessoryDAO.deleteById(accessory.getId());
			}
		}
		params.clear();
		cal = Calendar.getInstance();
		cal.add(6, -2);
		params.put("add_Time_less_than_equal", cal.getTime());
		params.put("status", Integer.valueOf(1));
		
		List<User> users = this.userDAO.queryPageList(params);
		
		for (User user : users) {
			this.userDAO.deleteById(user.getId());
		}
		
		params.clear();
		params.put("send_type", Integer.valueOf(1));
		params.put("status", Integer.valueOf(0));
		params.put("sendtime", new Date());
		
//		List<AppPushLog> appPushLoglist = this.appPushLogDAO
//				.query("select obj from AppPushLog obj where obj.send_type=:send_type and obj.status=:status and obj.sendtime<=:sendtime",
//						params, -1, -1);
//		
//		for (AppPushLog appPushLog : appPushLoglist) {
//			if (appPushLog.getDevice() == 0) {
//				this.appPushTools.android_push_msg_to_all(appPushLog);
//				this.appPushTools.ios_push_msg_to_all(appPushLog);
//			} else if (appPushLog.getDevice() == 1) {
//				this.appPushTools.android_push_msg_to_all(appPushLog);
//			} else if (appPushLog.getDevice() == 2) {
//				this.appPushTools.ios_push_msg_to_all(appPushLog);
//			}
//		}
	}

	
	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_3() {
		Map<String, Object> params = Maps.newHashMap();
		params.clear();
		params.put("ac_begin_time_less_than_equal", new Date());
		params.put("ac_status", Integer.valueOf(1));
		
		List<Activity> acts = this.activityDAO.queryPageList(params);
		
		for (Activity act : acts) {
			act.setAc_status(0);
			this.activityDAO.updateById(act);
			for (ActivityGoods ac : act.getAgs()) {
				ac.setAg_status(-2);
				this.activityGoodsDAO.updateById(ac);
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(0);
				goods.setActivity_goods_id(null);
				this.goodsDAO.updateById(goods);
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_4() {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		SysConfig sc = getSysConfig();
		int auto_order_notice = sc.getAuto_order_notice();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(6, -auto_order_notice);
		params.put("return_shipTime_less_than_equal", cal.getTime());
		params.put("auto_confirm_email1", Boolean.valueOf(true));
		params.put("auto_confirm_sms1", Boolean.valueOf(true));
		
		List<OrderForm> notice_ofs = this.orderFormDAO.queryPageList(params);
		
		for (OrderForm of : notice_ofs) {
			if (!of.getAuto_confirm_email()) {
				try {
					boolean email = send_email(of,
							"email_tobuyer_order_will_confirm_notify");
					if (email) {
						of.setAuto_confirm_email(true);
						this.orderFormDAO.updateById(of);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!of.getAuto_confirm_sms()) {
				try {
					User buyer = (User) this.userDAO.selectByPrimaryKey(CommUtil.null2Long(of
							.getUser_id()));
					boolean sms = send_sms(of, buyer.getMobile(),
							"sms_tobuyer_order_will_confirm_notify");
					if (sms) {
						of.setAuto_confirm_sms(true);
						this.orderFormDAO.updateById(of);
					}
				} catch (Exception localException1) {
				}
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_5() {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		SysConfig sc = getSysConfig();
		int auto_order_confirm = sc.getAuto_order_confirm();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(6, -auto_order_confirm);
		params.put("shipTime_less_than_equal", cal.getTime());
		params.put("order_status", Integer.valueOf(30));
		
		List<OrderForm> confirm_ofs = this.orderFormDAO.queryPageList(params);
		
		for (OrderForm of : confirm_ofs) {
			cal.setTime(of.getShipTime());
			cal.add(6, auto_order_confirm + of.getOrder_confirm_delay());
			if (cal.getTime().before(new Date())) {
				of.setOrder_status(40);
				of.setConfirmTime(new Date());
				this.orderFormDAO.updateById(of);
				Store store = (Store) this.storeDAO.selectByPrimaryKey(CommUtil.null2Long(of.getStore_id()));
				
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("确认收货");
				ofl.setLog_user_id(CommUtil.null2Long(of.getUser_id()));
				ofl.setLog_user_name(of.getUser_name());
				ofl.setOf(of);
				this.orderFormLogDAO.saveEntity(ofl);
				try {
					if ((sc.getEmailEnable()) && (of.getOrder_form() == 0)) {
						send_email(of, "email_toseller_order_receive_ok_notify");
					}
					if ((sc.getSmsEnbale()) && (of.getOrder_form() == 0)) {
						send_sms(of, store.getUser().getMobile(),"sms_toseller_order_receive_ok_notify");
					}
				} catch (Exception localException) {
				}
				if (of.getOrder_form() == 0) {
					PayoffLog plog = new PayoffLog();
					plog.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					plog.setPl_info("订单到期自动收货");
					plog.setAddTime(new Date());
					plog.setSeller(store.getUser());
					plog.setO_id(CommUtil.null2String(of.getId()));
					plog.setOrder_id(of.getOrder_id().toString());
					plog.setCommission_amount(of.getCommission_amount());
					plog.setGoods_info(of.getGoods_info());
					plog.setOrder_total_price(of.getGoods_amount());
					if ((of.getPayType() != null)
							&& (of.getPayType().equals("payafter"))) {
						plog.setTotal_amount(BigDecimal.valueOf(CommUtil
								.subtract(Integer.valueOf(0),
										of.getCommission_amount())));
					} else {
						plog.setTotal_amount(BigDecimal.valueOf(CommUtil
								.subtract(of.getGoods_amount(),
										of.getCommission_amount())));
					}
					this.payoffLogDAO.saveEntity(plog);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), store.getStore_sale_amount())));
					store.setStore_commission_amount(BigDecimal
							.valueOf(CommUtil.add(of.getCommission_amount(),
									store.getStore_commission_amount())));
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.add(plog.getTotal_amount(),
									store.getStore_payoff_amount())));
					this.storeDAO.updateById(store);

					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.add(
							of.getGoods_amount(), sc.getPayoff_all_sale())));
					sc.setPayoff_all_commission(BigDecimal.valueOf(CommUtil
							.add(of.getCommission_amount(),
									sc.getPayoff_all_commission())));
					this.sysConfigDAO.updateById(sc);
				}
			}
			int user_integral = (int) CommUtil.div(of.getTotalPrice(),
					Integer.valueOf(sc.getConsumptionRatio()));
			if (user_integral > sc.getEveryIndentLimit()) {
				user_integral = sc.getEveryIndentLimit();
			}
			User orderUser = (User) this.userDAO.selectByPrimaryKey(CommUtil.null2Long(of
					.getUser_id()));
			if (orderUser != null) {
				orderUser.setIntegral(orderUser.getIntegral() + user_integral);
				if (sc.getIntegral()) {
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("购物增加" + user_integral + "分");
					log.setIntegral(user_integral);
					log.setIntegral_user(orderUser);
					log.setType("order");
					this.integralLogDao.saveEntity(log);
				}
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_6() {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		SysConfig sc = getSysConfig();
		int auto_order_evaluate = sc.getAuto_order_evaluate();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(6, -auto_order_evaluate);
		params.put("confirmTime_less_than_equal", cal.getTime());
		params.put("order_status_40", Integer.valueOf(40));
		
		List<OrderForm> confirm_evaluate_ofs = this.orderFormDAO.queryPageList(params);
		
		List<Map> json = Lists.newArrayList();
		for (OrderForm obj : confirm_evaluate_ofs) {
			if (obj != null) {
				json = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
				obj.setOrder_status(65);
				obj.setFinishTime(new Date());
				this.orderFormDAO.updateById(obj);
				for (Map map : json) {
					map.put("orderForm", obj.getId());
					map.put("user_id", obj.getUser_id());
				}
				User user = (User) this.userDAO.selectByPrimaryKey(CommUtil.null2Long(obj.getUser_id()));
				
				OrderFormLog ofl = new OrderFormLog();

				ofl.setAddTime(new Date());
				ofl.setLog_info("自动评价订单");
				ofl.setLog_user_id(user.getId());
				ofl.setLog_user_name(user.getUserName());
				ofl.setOf(obj);
				this.orderFormLogDAO.saveEntity(ofl);
				
				user.setUser_goods_fee(BigDecimal.valueOf(CommUtil.add(
						user.getUser_goods_fee(), obj.getTotalPrice())));
				this.userDAO.updateById(user);
			}
			for (Object ofl = json.iterator(); ((Iterator) ofl).hasNext();) {
				Map<String, Object> map = (Map) ((Iterator) ofl).next();
				Evaluate eva = new Evaluate();
				String goods_gsp_ids = map.get("goods_gsp_ids").toString();
				goods_gsp_ids = goods_gsp_ids.replaceAll(",", "_");
				String goods_id = map.get("goods_id") + "_" + goods_gsp_ids;
				Goods goods = (Goods) this.goodsDAO.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
				eva.setAddTime(new Date());
				eva.setEvaluate_goods(goods);
				goods.setEvaluate_count(goods.getEvaluate_count() + 1);
				eva.setGoods_num(CommUtil.null2Int(map.get("goods_count")));
				eva.setGoods_price(map.get("goods_price").toString());
				eva.setGoods_spec(map.get("goods_gsp_val").toString());
				eva.setEvaluate_info("");
				eva.setEvaluate_buyer_val(1);
				eva.setDescription_evaluate(BigDecimal.valueOf(5.0D));
				eva.setService_evaluate(BigDecimal.valueOf(5.0D));
				eva.setShip_evaluate(BigDecimal.valueOf(5.0D));
				eva.setEvaluate_type("goods");
				eva.setEvaluate_user((User) this.userDAO.selectByPrimaryKey(CommUtil
						.null2Long(map.get("user_id"))));
				eva.setOf((OrderForm) this.orderFormDAO.selectByPrimaryKey(CommUtil
						.null2Long(map.get("orderForm"))));
				eva.setReply_status(0);
				this.evaluateDAO.saveEntity(eva);
				params.clear();
				StorePoint point;
				if (goods.getGoods_type() == 1) {
					Store store = (Store) this.storeDAO.selectByPrimaryKey(CommUtil
							.null2Long(goods.getGoods_store().getId()));
					params.put("store_id", store != null ? store.getId()
							.toString() : "");
					
					List<Evaluate> evas = this.evaluateDAO.queryPageList(params);
					
					double store_evaluate = 0.0D;
					double description_evaluate = 0.0D;
					double description_evaluate_total = 0.0D;
					double service_evaluate = 0.0D;
					double service_evaluate_total = 0.0D;
					double ship_evaluate = 0.0D;
					double ship_evaluate_total = 0.0D;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						description_evaluate_total = description_evaluate_total
								+ CommUtil.null2Double(eva1
										.getDescription_evaluate());

						service_evaluate_total = service_evaluate_total
								+ CommUtil.null2Double(eva1
										.getService_evaluate());

						ship_evaluate_total = ship_evaluate_total
								+ CommUtil.null2Double(eva1.getShip_evaluate());
					}
					description_evaluate = CommUtil.null2Double(df
							.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df
							.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df
							.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3.0D;
					store.setStore_credit(store.getStore_credit()
							+ eva.getEvaluate_buyer_val());
					this.storeDAO.updateById(store);
					params.clear();
					params.put("store_id", store != null ? store.getId() : "");
					
					List<StorePoint> sps = this.storePointDAO.queryPageList(params);
					
					point = null;
					if (sps.size() > 0) {
						point = (StorePoint) sps.get(0);
					} else {
						point = new StorePoint();
					}
					point.setAddTime(new Date());
					point.setStore(store);
					point.setDescription_evaluate(BigDecimal.valueOf(5L));
					point.setService_evaluate(BigDecimal.valueOf(5L));
					point.setShip_evaluate(BigDecimal.valueOf(5L));
					point.setStore_evaluate(BigDecimal.valueOf(5L));
					if (sps.size() > 0) {
						this.storePointDAO.updateById(point);
					} else {
						this.storePointDAO.saveEntity(point);
					}
				} else {
					User sp_user = (User) this.userDAO.selectByPrimaryKey(obj.getEva_user_id());
					params.put("evaluate_goods_goods_type", Integer.valueOf(0));
					
					List<Evaluate> evas = this.evaluateDAO.queryPageList(params);
					
					double store_evaluate = 0.0D;
					double description_evaluate = 0.0D;
					double description_evaluate_total = 0.0D;
					double service_evaluate = 0.0D;
					double service_evaluate_total = 0.0D;
					double ship_evaluate = 0.0D;
					double ship_evaluate_total = 0.0D;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						description_evaluate_total = description_evaluate_total
								+ CommUtil.null2Double(eva1
										.getDescription_evaluate());

						service_evaluate_total = service_evaluate_total
								+ CommUtil.null2Double(eva1
										.getService_evaluate());

						ship_evaluate_total = ship_evaluate_total
								+ CommUtil.null2Double(eva1.getShip_evaluate());
					}
					description_evaluate = CommUtil.null2Double(df
							.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df
							.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df
							.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3.0D;
					params.clear();
					params.put("user_id", obj.getEva_user_id());
					
					List<StorePoint> sps = this.storePointDAO.queryPageList(params);
					
					if (sps.size() > 0) {
						point = (StorePoint) sps.get(0);
					} else {
						point = new StorePoint();
					}
					point.setAddTime(new Date());
					point.setUser(sp_user);
					point.setDescription_evaluate(BigDecimal
							.valueOf(description_evaluate));
					point.setService_evaluate(BigDecimal
							.valueOf(service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
					point.setStore_evaluate(BigDecimal.valueOf(store_evaluate));
					if (sps.size() > 0) {
						this.storePointDAO.updateById(point);
					} else {
						this.storePointDAO.saveEntity(point);
					}
				}
				this.goodsDAO.updateById(goods);
			}
			this.orderFormTools.updateOrderGoodsSaleNum(obj);
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_7() {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		SysConfig sc = getSysConfig();
		int auto_order_return = sc.getAuto_order_return();
		cal = Calendar.getInstance();
		params.clear();
		cal.add(6, -auto_order_return);
		params.put("return_shipTime_more_than", cal.getTime());
		params.put("order_status_more_than", Integer.valueOf(40));
		params.put("goods_info_like", "\"goods_return_status\":\"\"");
		
		List<OrderForm> confirm_return_ofs = this.orderFormDAO.queryPageList(params);
		
		for (OrderForm order : confirm_return_ofs) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
			List<Map> new_maps = Lists.newArrayList();
			Map gls = Maps.newHashMap();
			for (Map m : maps) {
				m.put("goods_return_status", Integer.valueOf(-1));
				gls.putAll(m);
				new_maps.add(m);
			}
			order.setGoods_info(JSON.toJSONString(new_maps));
			this.orderFormDAO.updateById(order);
			Map rgl_params = Maps.newHashMap();
			rgl_params.put("goods_return_status_no", "-2");
			rgl_params.put("return_order_id", order.getId());
			
			List<ReturnGoodsLog> rgl = this.returnGoodsLogDAO.queryPageList(rgl_params);
			
			for (ReturnGoodsLog r : rgl) {
				r.setGoods_return_status("-2");
				this.returnGoodsLogDAO.updateById(r);
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_8() {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		SysConfig sc = getSysConfig();
		
		List<Goods> goods_list = this.goodsDAO.queryPageList(RedPigMaps.newMap());
		
		for (Goods goods : goods_list) {
			double description_evaluate = 0.0D;
			params.clear();
			params.put("evaluate_goods_id", goods.getId());
			
			List<Evaluate> eva_list = this.evaluateDAO.queryPageList(params);
			
			for (Evaluate eva : eva_list) {
				description_evaluate = CommUtil.add(
						eva.getDescription_evaluate(),
						Double.valueOf(description_evaluate));
			}
			description_evaluate = CommUtil.div(
					Double.valueOf(description_evaluate),
					Integer.valueOf(eva_list.size()));
			goods.setDescription_evaluate(BigDecimal
					.valueOf(description_evaluate));
			if (eva_list.size() > 0) {
				double well_evaluate = 0.0D;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", Integer.valueOf(1));
				
				List<Evaluate> well_list = this.evaluateDAO.queryPageList(params);
				
				well_evaluate = CommUtil.div(Integer.valueOf(well_list.size()),
						Integer.valueOf(eva_list.size()));
				goods.setWell_evaluate(BigDecimal.valueOf(well_evaluate));

				double middle_evaluate = 0.0D;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", Integer.valueOf(0));
				
				List<Evaluate> middle_list = this.evaluateDAO.queryPageList(params);
				
				middle_evaluate = CommUtil.div(
						Integer.valueOf(middle_list.size()),
						Integer.valueOf(eva_list.size()));
				goods.setMiddle_evaluate(BigDecimal.valueOf(middle_evaluate));

				double bad_evaluate = 0.0D;
				params.clear();
				params.put("evaluate_goods_id", goods.getId());
				params.put("evaluate_buyer_val", Integer.valueOf(-1));
				
				List<Evaluate> bad_list = this.evaluateDAO.queryPageList(params);
				
				bad_evaluate = CommUtil.div(Integer.valueOf(bad_list.size()),
						Integer.valueOf(eva_list.size()));
				goods.setBad_evaluate(BigDecimal.valueOf(bad_evaluate));
			}
			this.goodsDAO.updateById(goods);
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_9() {
		Map<String, Object> params = Maps.newHashMap();
		params.clear();
		params.put("goods_status", Integer.valueOf(2));
		
		List<Goods> goods_list2 = this.goodsDAO.queryPageList(params);
		
		for (Goods goods : goods_list2) {
			if (goods.getGoods_seller_time().after(new Date())) {
				goods.setGoods_status(0);
				this.goodsDAO.updateById(goods);

				this.goodsTools.addGoodsLucene(goods);
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_10() {

		Map<String,Object> maps = Maps.newHashMap();
        maps.put("type",0);
        
		List<Favorite> favs = this.favoriteDAO.queryPageList(maps);
		
		SysConfig sc = getSysConfig();
		BigDecimal bd = new BigDecimal(0.0D);
		

		Map<String,Object> params = Maps.newHashMap();
		
        
		params.put("operation_property", "userName");
		
		params.put("operation_symbol", "=");
		
		params.put("operation_value", "admin");
		
		List<User> fromUser = this.userDAO.selectObjByProperty(params);
		for (Favorite fav : favs) {
			Goods goods = (Goods) this.goodsDAO.selectByPrimaryKey(fav.getGoods_id());
			
			if ((goods != null)
					&& (goods.getPrice_history() != null)
					&& (fav.getGoods_current_price() != null)
					&& (goods != null)
					&& (goods.getGoods_current_price().compareTo(
							fav.getGoods_current_price()) < 0)) {
				String msg_content = "您收藏的商品" + goods.getGoods_name()
						+ "已降价，请注意查看";
				
				User user = (User) this.userDAO.selectByPrimaryKey(fav.getUser_id());
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				
				if(fromUser!=null && !fromUser.isEmpty()){
					msg.setFromUser(fromUser.get(0));
				}
				
				msg.setToUser(user);
				this.messageDAO.saveEntity(msg);
				fav.setGoods_current_price(goods.getGoods_current_price());
				this.favoriteDAO.updateById(fav);

				String store_id = null;
				if (goods.getGoods_store() != null) {
					store_id = CommUtil.null2String(goods.getGoods_store()
							.getId());
				}
				Map<String, Object> map = Maps.newHashMap();
				map.put("buyer_id", user.getId());
				map.put("goods_id", goods.getId());
				String json = JSON.toJSONString(map);
				try {
					if (goods.getGoods_type() == 1) {
						this.msgTools.sendSmsCharge(null,
								"sms_tobuyer_goods_price_down_notify",
								user.getMobile(), json, null, store_id);
						this.msgTools.sendEmailCharge(null,
								"email_tobuyer_goods_price_down_notify",
								user.getEmail(), json, null, store_id);
					} else {
						this.msgTools.sendSmsFree(null,
								"sms_tobuyer_goods_price_down_notify",
								user.getMobile(), json, null);
						this.msgTools.sendEmailFree(null,
								"email_tobuyer_goods_price_down_notify",
								user.getEmail(), json, null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_11() {

		Map<String,Object> maps = Maps.newHashMap();
        maps.put("order_status1",10);
        maps.put("order_status2",10);
        
		List<OrderForm> order_list = this.orderFormDAO.queryPageList(maps);
		
		 for (OrderForm order : order_list) {
			if (order.getGoods_info() != null) {
				List<Map> goods_list = JSON.parseArray(order.getGoods_info(),
						Map.class);
				for (Map<String, Object> map : goods_list) {
					Goods goods = (Goods) this.goodsDAO.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
					
					if ((goods == null) || (goods.getAdvance_sale_type() != 1)
							|| (goods.getAdvance_sale_info() == null)) {
						
					}
					List<Map> ad_list = JSON.parseArray(
							goods.getAdvance_sale_info(), Map.class);

					for (Map m : ad_list) {
						Date nowTime = new Date();
						Date addTime = order.getAddTime();
						Date ret_endTime = CommUtil.formatDate(CommUtil
								.null2String(m.get("rest_end_date")));
						Date ret_startTime = CommUtil.formatDate(CommUtil
								.null2String(m.get("rest_start_date")));
						int din_minute = CommUtil
								.null2Int(Long.valueOf((nowTime.getTime() - addTime
										.getTime()) / 1000L / 60L % 60L));
						int ret_end_totalS = CommUtil.null2Int(Long
								.valueOf((ret_endTime.getTime() - nowTime
										.getTime()) / 1000L));
						int ret_start_totalS = CommUtil.null2Int(Long
								.valueOf((ret_startTime.getTime() - nowTime
										.getTime()) / 1000L));
						if ((order.getOrder_status() == 10)
								&& (din_minute >= 30)) {
							order.setOrder_status(0);
						} else if ((order.getOrder_status() == 11)
								&& (ret_end_totalS <= 0)) {
							order.setOrder_status(0);
						} else if ((order.getOrder_status() == 11)
								&& (ret_start_totalS <= 0)
								&& (ret_end_totalS > 0)) {
							User user = (User) this.userDAO.selectByPrimaryKey(CommUtil
									.null2Long(order.getUser_id()));
							String msg_content = "订单号：" + order.getOrder_id()
									+ "，尾款支付已经开始，请在规定的时间里前往支付。";

							Message msg = new Message();
							msg.setAddTime(new Date());
							msg.setStatus(0);
							msg.setType(0);
							msg.setContent(msg_content);
							
							Map<String,Object> params = Maps.newHashMap();
							params.put("operation_property", "userName");
							
							params.put("operation_symbol", "=");
							
							params.put("operation_value", "admin");
							
							msg.setFromUser((User) this.userDAO.selectObjByProperty(params));
							msg.setToUser(user);
							this.messageDAO.saveEntity(msg);
							try {
								this.msgTools.sendSMS(user.getMobile(), "您的订单："
										+ order.getOrder_id()
										+ "尾款支付已经开始，请在规定的时间里前往支付。");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_12() {
		SysConfig sc = getSysConfig();
		int minute = sc.getAuto_order_cancel() * 60;
		Date nowDate = new Date();

		Map<String,Object> params = Maps.newHashMap();
		params.put("order_status",10);
        
		List<OrderForm> order_list = this.orderFormDAO.queryPageList(params);
		
		List<Map> advance_list = Lists.newArrayList();
		for (OrderForm of : order_list) {
			if (of != null) {
				List<Map> map_list = JSON.parseArray(of.getGoods_info(),Map.class);
				List<Goods> goods_list = Lists.newArrayList();
				if(map_list!=null && map_list.size()>0){
					for (Map map : map_list) {
						Goods goods = (Goods) this.goodsDAO.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
						
						goods_list.add(goods);
					}
				}
				List<Map> ad_list;
				for (Goods g : goods_list) {
					ad_list = Lists.newArrayList();
					if ((g != null) && (g.getAdvance_sale_type() == 1)
							&& (g.getAdvance_sale_info() != null)
							&& (!"".equals(g.getAdvance_sale_info()))) {
						ad_list = JSON.parseArray(g.getAdvance_sale_info(),
								Map.class);
					}
					advance_list.addAll(ad_list);
				}
				if ((nowDate.getTime() >= of.getAddTime().getTime() + minute
						* 60 * 1000)
						&& (advance_list.size() == 0)) {
					if (of.getChild_order_detail() != null) {
						List<Map> maps = JSON.parseArray(of.getChild_order_detail(), Map.class);
						for (Map m : maps) {
							OrderForm ch = (OrderForm) this.orderFormDAO.selectByPrimaryKey(CommUtil.null2Long(m.get("order_id")));
							
							if ((ch != null) && (ch.getOrder_status() == 10)) {
								ch.setOrder_status(0);
							}
							this.orderFormDAO.updateById(ch);
						}
					}
					if (of.getOrder_status() == 10) {
						of.setOrder_status(0);
					}
					this.orderFormDAO.updateById(of);
				}
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void halfHour_13() {
		this.cloudPurchaseLotteryService.runALottery();
	}

	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_1() {
		SysConfig sysConfig = getSysConfig();
		Map<String, Object> params = Maps.newHashMap();
		params.clear();
		params.put("ztc_status", Integer.valueOf(2));
		
		List<Goods> goods_audit_list = this.goodsDAO.queryPageList(params);
		
		for (Goods goods : goods_audit_list) {
			if (goods.getZtc_begin_time().before(new Date())) {
				goods.setZtc_dredge_price(goods.getZtc_price());
				goods.setZtc_status(3);
				this.goodsDAO.updateById(goods);
			}
		}
		params.clear();
		params.put("ztc_status", Integer.valueOf(3));
		
		goods_audit_list = this.goodsDAO.queryPageList(params);
		
		for (Goods goods : goods_audit_list) {
			if (goods.getZtc_gold() >= goods.getZtc_price()) {
				goods.setZtc_gold(goods.getZtc_gold() - goods.getZtc_price());
				goods.setZtc_dredge_price(goods.getZtc_price());
				this.goodsDAO.updateById(goods);
				ZTCGoldLog log = new ZTCGoldLog();
				log.setAddTime(new Date());
				log.setZgl_content("直通车消耗金币");
				log.setZgl_gold(goods.getZtc_price());
				log.setZgl_goods_id(goods.getId());
				log.setGoods_name(goods.getGoods_name());
				log.setStore_name(goods.getGoods_store().getStore_name());
				log.setUser_name(goods.getGoods_store().getUser().getUsername());
				log.setZgl_type(1);
				this.zTCGlodLogDAO.saveEntity(log);
			} else {
				goods.setZtc_status(0);
				goods.setZtc_dredge_price(0);
				goods.setZtc_pay_status(0);
				goods.setZtc_apply_time(null);
				this.goodsDAO.updateById(goods);
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_2() {
		SysConfig sysConfig = getSysConfig();
		Map<String, Object> params = Maps.newHashMap();
		params.put("validity_no", -2);
		
		List<Store> stores = this.storeDAO.queryPageList(params);
		
		for (Store store : stores) {
			if (store.getValidity().before(new Date())) {
				store.setStore_status(25);
				this.storeDAO.updateById(store);
				Map<String,Object> maps = Maps.newHashMap();
		        
		        params.put("operation_property", "mark");
				
				params.put("operation_symbol", "=");
				
				params.put("operation_value", "msg_toseller_store_auto_closed_notify");
		        
				Template template = (Template) this.templateDAO.selectObjByProperty(maps);
				
				if ((template != null) && (template.getOpen())) {
					Message msg = new Message();
					msg.setAddTime(new Date());
					msg.setContent(template.getContent());
					maps.clear();
					maps.put("operation_property", "userName");
					
					maps.put("operation_symbol", "=");
					
					maps.put("operation_value", "admin");
					
					msg.setFromUser((User) this.userDAO.selectObjByProperty(maps));
					msg.setStatus(0);
					msg.setTitle(template.getTitle());
					msg.setToUser(store.getUser());
					msg.setType(0);
					this.messageDAO.saveEntity(msg);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_3() {
		SysConfig sysConfig = getSysConfig();
		Map<String, Object> params = Maps.newHashMap();
		int payoff_count = sysConfig.getPayoff_count();
		Calendar a = Calendar.getInstance();
		a.set(5, 1);
		a.roll(5, -1);
		int allDate = a.get(5);
		String selected = "";
		if (payoff_count == 1) {
			selected = CommUtil.null2String(Integer.valueOf(allDate));
		} else if (payoff_count == 2) {
			if (allDate == 31) {
				selected = "15,31";
			}
			if (allDate == 30) {
				selected = "15,30";
			}
			if (allDate == 29) {
				selected = "14,29";
			}
			if (allDate == 28) {
				selected = "14,28";
			}
		} else if (payoff_count == 3) {
			if (allDate == 31) {
				selected = "10,20,31";
			}
			if (allDate == 30) {
				selected = "10,20,30";
			}
			if (allDate == 29) {
				selected = "10,20,29";
			}
			if (allDate == 28) {
				selected = "10,20,28";
			}
		} else if (payoff_count == 4) {
			if (allDate == 31) {
				selected = "7,14,21,31";
			}
			if (allDate == 30) {
				selected = "7,14,21,30";
			}
			if (allDate == 29) {
				selected = "7,14,21,29";
			}
			if (allDate == 28) {
				selected = "7,14,21,28";
			}
		}
		Date payoff_data = new Date();
		int now_date = payoff_data.getDate();
		String[] str = selected.split(",");
		for (String payoff_date : str) {
			if (CommUtil.null2Int(payoff_date) >= now_date) {
				payoff_data.setDate(CommUtil.null2Int(payoff_date));
				payoff_data.setHours(0);
				payoff_data.setMinutes(0);
				payoff_data.setSeconds(1);
				break;
			}
		}
		String ms = "";
		for (int i = 0; i < str.length; i++) {
			if (i + 1 == str.length) {
				ms = ms + str[i] + "日";
			} else {
				ms = ms + str[i] + "日、";
			}
		}
		ms =

		"今天是" + DateFormat.getDateInstance(0).format(new Date()) + "，本月的结算日期为"
				+ ms + "，请于结算日申请结算。";
		sysConfig.setPayoff_mag_default(ms);
		sysConfig.setPayoff_date(payoff_data);
		this.sysConfigDAO.updateById(sysConfig);
		params.clear();
		params.put("status", Integer.valueOf(1));
		
		List<PayoffLog> payofflogs_1 = this.payoffLogDAO.queryPageList(params);
		
		for (PayoffLog temp : payofflogs_1) {

			temp.setStatus(0);
			this.payoffLogDAO.updateById(temp);
		}
		params.clear();
		params.put("status", Integer.valueOf(0));
		params.put("add_Time_less_than_equal", sysConfig.getPayoff_date());
		
		List<PayoffLog> payofflogs = this.payoffLogDAO.queryPageList(params);
		
		for (PayoffLog obj : payofflogs) {
			OrderForm of = (OrderForm) this.orderFormDAO.selectByPrimaryKey(CommUtil.null2Long(obj.getO_id()));
			
			Date Payoff_date = sysConfig.getPayoff_date();
			Date now = new Date();
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			Date next = new Date();
			next.setDate(next.getDate() + 1);
			next.setHours(0);
			next.setMinutes(0);
			next.setSeconds(0);
			boolean payoff = false;
			if ((Payoff_date.after(now)) && (Payoff_date.before(next))) {
				payoff = true;
			}
			if ((of.getOrder_cat() == 2) && (of.getOrder_status() == 20)
					&& (payoff)) {
				obj.setStatus(1);
			}
			if (of.getOrder_cat() == 0) {
				if ((of.getOrder_status() >= 40) && (payoff)) {
					obj.setStatus(1);
				}
				if ((obj.getPayoff_type() == -1)
						&& (of.getOrder_status() >= 40) && (payoff)) {
					obj.setStatus(3);
					obj.setApply_time(new Date());
				}
			}
			this.payoffLogDAO.updateById(obj);
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_4() {
		SysConfig sysConfig = getSysConfig();
		Map<String, Object> params = Maps.newHashMap();
		params.clear();
		params.put("status", Integer.valueOf(1));
		params.put("endTime_less_than_equal", new Date());
		
		List<GroupLifeGoods> groups = this.groupLifeGoodsDAO.queryPageList(params);
		
		for (GroupLifeGoods glg : groups) {
			glg.setGroup_status(-2);
			this.groupLifeGoodsDAO.updateById(glg);

			this.goodsTools.deleteGroupLifeLucene(glg);
		}
		params.clear();
		params.put("gg_status_no", Integer.valueOf(-2));
		params.put("endTime_less_than_equal", new Date());
		
		List<GroupGoods> groupgoodes = this.groupGoodsDAO.queryPageList(params);
		
		RedPigLuceneUtil lucene;
		for (GroupGoods gg : groupgoodes) {
			gg.setGg_status(-2);
			this.groupGoodsDAO.updateById(gg);

			Goods goods = gg.getGg_goods();
			goods.setGroup(null);
			goods.setGroup_buy(0);
			goods.setGoods_current_price(goods.getStore_price());
			this.goodsDAO.updateById(goods);
			this.goodsTools.updateGoodsLucene(goods);

			String goodsgroup_lucene_path = ClusterSyncTools.getClusterRoot()
					+ File.separator + "luence" + File.separator + "groupgoods";
			File filegroup = new File(goodsgroup_lucene_path);
			if (!filegroup.exists()) {
				CommUtil.createFolder(goodsgroup_lucene_path);
			}
			lucene = RedPigLuceneUtil.instance();
			RedPigLuceneUtil.setIndex_path(goodsgroup_lucene_path);
			lucene.delete_index(CommUtil.null2String(gg.getId()));
		}
		params.clear();
		params.put("gg_status", Integer.valueOf(2));
		params.put("endTime_less_than_equal", new Date());
		
		List<GroupGoods> begin_groupgoodes = this.groupGoodsDAO.queryPageList(params);
		
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		RedPigLuceneUtil lucene1 = RedPigLuceneUtil.instance();
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		Goods goods;
		for (GroupGoods gg : begin_groupgoodes) {
			gg.setGg_status(1);
			this.groupGoodsDAO.updateById(gg);
			goods = gg.getGg_goods();
			goods.setGroup_buy(2);
			goods.setGroup(gg.getGroup());
			goods.setGoods_current_price(gg.getGg_price());
			this.goodsDAO.updateById(goods);
			this.goodsTools.addGroupGoodsLucene(gg);
		}
		params.clear();
		params.put("status", Integer.valueOf(0));
		params.put("lifeGoods_endTime_less_than_equal", new Date());
		
		List<GroupInfo> groupInfos = this.groupInfoDAO.queryPageList(params);
		
		for (GroupInfo info : groupInfos) {
			info.setStatus(-1);
			this.groupInfoDAO.updateById(info);
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_5() {
		SysConfig sysConfig = getSysConfig();
		Map<String, Object> params = Maps.newHashMap();
		params.clear();
		params.put("combin_status1", Integer.valueOf(1));
		params.put("combin_status2", Integer.valueOf(0));
		params.put("endTime_less_than_equal", new Date());
		
		List<CombinPlan> combins = this.combinPlanDAO.queryPageList(params);
		
		for (CombinPlan obj : combins) {
			obj.setCombin_status(-2);
			this.combinPlanDAO.updateById(obj);
			Goods goods = (Goods) this.goodsDAO.selectByPrimaryKey(obj.getMain_goods_id());
			if (goods.getCombin_status() == 1) {
				if (obj.getCombin_type() == 0) {
					if (goods.getCombin_suit_id().equals(obj.getId())) {
						goods.setCombin_suit_id(null);
					}
				} else if (goods.getCombin_parts_id().equals(obj.getId())) {
					goods.setCombin_parts_id(null);
				}
				goods.setCombin_status(0);
				this.goodsDAO.updateById(goods);
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_6() {
		SysConfig sysConfig = getSysConfig();
		Map<String, Object> params = Maps.newHashMap();
		params.clear();
		params.put("gift_status", Integer.valueOf(10));
		params.put("endTime_less_than_equal", new Date());
		
		List<BuyGift> bgs = this.buyGiftDAO.queryPageList(params);
		
		for (BuyGift bg : bgs) {
			bg.setGift_status(20);
			List<Map> maps = JSON.parseArray(bg.getGift_info(), Map.class);
			maps.addAll(JSON.parseArray(bg.getGoods_info(), Map.class));
			for (Map map : maps) {
				Goods goods = (Goods) this.goodsDAO.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
				if (goods != null) {
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					this.goodsDAO.updateById(goods);
				}
			}
			this.buyGiftDAO.updateById(bg);
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_7() {

		Map<String, Object> params = Maps.newHashMap();
		params.clear();
		params.put("erstatus", Integer.valueOf(10));
		params.put("erend_time_less_than_equal", new Date());
		
		List<EnoughReduce> er = this.enoughReduceDAO.queryPageList(params);
		
		for (EnoughReduce enoughReduce : er) {
			enoughReduce.setErstatus(20);
			this.enoughReduceDAO.updateById(enoughReduce);
			String goods_json = enoughReduce.getErgoods_ids_json();
			List<String> goods_id_list = JSON.parseArray(goods_json,
					String.class);
			for (String goods_id : goods_id_list) {
				Goods ergood = (Goods) this.goodsDAO.selectByPrimaryKey(CommUtil.null2Long(goods_id));
				
				ergood.setEnough_reduce(0);
				ergood.setOrder_enough_reduce_id(null);
				this.goodsDAO.updateById(ergood);
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_8() {
		SysConfig sysConfig = getSysConfig();
		Map<String, Object> params = Maps.newHashMap();
		params.clear();
		params.put("freeStatus", Integer.valueOf(5));
		params.put("endTime_less_than_equal", new Date());
		
		List<FreeGoods> fgs = this.freeGoodsDAO.queryPageList(params);
		
		for (FreeGoods fg : fgs) {
			fg.setFreeStatus(10);
			this.freeGoodsDAO.updateById(fg);
			params.clear();
			params.put("freeId", fg.getId());
			params.put("evaluate_status", 0);
			
			List<FreeApplyLog> fals = this.freeApplyLogDAO.queryPageList(params);
			
			for (FreeApplyLog fal : fals) {
				fal.setEvaluate_status(2);
				this.freeApplyLogDAO.updateById(fal);
			}
		}
	}

	@Transactional(rollbackFor = { Exception.class })
	public void oneDay_9() {
		SysConfig sysConfig = getSysConfig();
		Map<String, Object> params = Maps.newHashMap();

		params.put("day_msg_count_no", Integer.valueOf(0));
		
		List<User> users = this.userDAO.queryPageList(params);
		
		for (User obj : users) {
			obj.setDay_msg_count(0);
			this.userDAO.updateById(obj);
		}
		params.clear();
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		cal.add(6, -1);
		params.put("add_Time_less_than_equal", cal.getTime());
		params.put("sc_status", Integer.valueOf(0));
		
		List<GoodsCart> cart_list = this.goodsCartDAO.queryPageList(params);
		
		for (GoodsCart gc : cart_list) {
			gc.getGsps().clear();
			this.goodsCartDAO.deleteById(gc.getId());
		}
		params.clear();
		cal = Calendar.getInstance();
		cal.add(6, -7);
		params.put("add_Time_less_than_equal", cal.getTime());
		params.put("sc_status", Integer.valueOf(0));
		
		cart_list = this.goodsCartDAO.queryPageList(params);
		
		for (GoodsCart gc : cart_list) {
			gc.getGsps().clear();
			this.goodsCartDAO.deleteById(gc.getId());
		}
		params.clear();
		cal = Calendar.getInstance();
		cal.add(6, -7);
		params.put("add_Time_less_than_equal", cal.getTime());
		
		List<IntegralGoodsCart> ig_cart_list = this.integralGoodsCartDAO.queryPageList(params);
		
		for (IntegralGoodsCart igc : ig_cart_list) {
			this.integralGoodsCartDAO.deleteById(igc.getId());
		}
		params.clear();
		params.put("status", Integer.valueOf(0));
		params.put("coupon_coupon_begin_time", new Date());
		
		List<CouponInfo> couponInfos = this.couponInfoDAO.queryPageList(params);
		
		for (CouponInfo couponInfo : couponInfos) {
			couponInfo.setStatus(-1);
			this.couponInfoDAO.updateById(couponInfo);
		}
	}
}
