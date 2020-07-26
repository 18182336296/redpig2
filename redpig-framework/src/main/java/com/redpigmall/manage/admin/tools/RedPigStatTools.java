package com.redpigmall.manage.admin.tools;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigCircleInvitationService;
import com.redpigmall.service.RedPigCircleService;
import com.redpigmall.service.RedPigComplaintService;
import com.redpigmall.service.RedPigConsultService;
import com.redpigmall.service.RedPigDeliveryAddressService;
import com.redpigmall.service.RedPigFreeGoodsService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigGroupInfoService;
import com.redpigmall.service.RedPigInformationService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigPayoffLogService;
import com.redpigmall.service.RedPigPredepositCashService;
import com.redpigmall.service.RedPigReturnGoodsLogService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: RedPigStatTools.java
 * </p>
 * 
 * <p>
 * Description:统计工具类，用来在超级后台的首页显示统计信息，统计不是及时的，为了节约系统开支，系统定时进行数据统计并保存到数据库中
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
 * @date 2015-3-16
 * 
 * @version redpigmall_b2b2c 2016
 */
@Component
public class RedPigStatTools {
	@Autowired
	private RedPigStoreService storeService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigDeliveryAddressService deliveryService;
	@Autowired
	private RedPigCircleService circleService;
	@Autowired
	private RedPigCircleInvitationService invitationService;
	@Autowired
	private RedPigInformationService informationService;
	@Autowired
	private RedPigConsultService consultService;
	@Autowired
	private RedPigOrderFormService orderFormService;
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigPayoffLogService plService;
	@Autowired
	private RedPigFreeGoodsService freegoodsService;
	@Autowired
	private RedPigGroupInfoService groupinfoService;
	@Autowired
	private RedPigComplaintService complaintService;
	@Autowired
	private RedPigReturnGoodsLogService returngoodslogService;
	@Autowired
	private RedPigPredepositCashService PredepositCashService;

	public int query_store(int count) {

		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());
		return this.storeService.selectCount(params);
	}

	public int query_user(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());

		return this.userService.selectCount(params);

	}

	public int query_live_user(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("lastLoginDate_more_than_equal", cal.getTime());
		params.put("redpig_no_userRole", "ADMIN");

		return this.userService.selectCount(params);
	}

	public int query_goods(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());
		return this.goodsService.selectCount(params);
	}

	public int query_order(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());

		return this.orderFormService.selectCount(params);
	}

	public int query_ztc(int count) {

		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());
		params.put("ztc_status", 2);

		return this.goodsService.selectCount(params);
	}

	public int query_delivery(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());
		params.put("da_status", 10);
		return this.deliveryService.selectCount(params);
	}

	public int query_circle(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());
		params.put("status", 5);

		return this.circleService.selectCount(params);
	}

	public int query_information(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());
		params.put("status", 20);
		return this.informationService.selectCount(params);
	}

	public int query_invitation(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());
		return this.invitationService.selectCount(params);
	}

	public int query_all_user() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("redpig_no_userRole", "ADMIN");

		return this.userService.selectCount(params);
	}

	public int query_all_goods() {
		return this.goodsService.selectCount(RedPigMaps.newMap());
	}

	public int query_all_store() {
		return this.storeService.selectCount(RedPigMaps.newMap());
	}

	public int query_audit_store() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_status1", 5);
		params.put("store_status2", 10);

		return this.storeService.selectCount(params);

	}

	public double query_all_amount() {
		double price = 0.0D;
		Map<String, Object> params = Maps.newHashMap();
		params.put("order_status", 60);

		List<OrderForm> ofs = this.orderFormService.queryPageList(params);
		for (OrderForm of : ofs) {
			price = CommUtil.null2Double(of.getTotalPrice()) + price;
		}
		return price;
	}

	public int query_complaint(int count) {
		Map<String, Object> params = Maps.newHashMap();
		Calendar cal = Calendar.getInstance();
		cal.add(6, count);
		params.put("add_Time_more_than_equal", cal.getTime());
		params.put("status", 0);
		return this.complaintService.selectCount(params);
	}

	public int query_payoff() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("status", 1);
		return this.plService.selectCount(params);
	}

	public double query_all_user_balance() {
		List<User> users = this.userService.queryPageList(RedPigMaps.newMap());

		double banlance = 0.0D;
		for (int i = 0; i < users.size(); i++) {
			banlance = CommUtil.add(Double.valueOf(CommUtil.null2Double(users
					.get(i).getAvailableBalance())), Double.valueOf(banlance));
		}
		return banlance;
	}

	public int query_refund() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("refund_status", 0);
		params.put("goods_return_status", 10);
		return this.plService.selectCount(params);
	}

	public int query_grouplife_refund() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("status", 5);
		return this.groupinfoService.selectCount(params);
	}

	public int query_ztc_audit() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("ztc_status", 1);
		return this.goodsService.selectCount(params);
	}

	public int query_delivery_audit() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("da_status", 0);
		return this.deliveryService.selectCount(params);
	}

	public int query_information_audit() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("status", 10);
		return this.informationService.selectCount(params);
	}

	public int query_self_goods() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		return this.goodsService.selectCount(params);
	}

	public int query_self_storage_goods() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_type", 0);
		params.put("goods_status", 1);
		return this.goodsService.selectCount(params);
	}

	public int query_self_consult() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("reply", false);
		params.put("whether_self", 1);
		return this.consultService.selectCount(params);
	}

	public int query_self_order_shipping() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("order_status1", 20);
		params.put("order_status2", 16);
		params.put("order_form", 1);
		params.put("order_cat_no", 2);
		return this.orderFormService.selectCount(params);
	}

	public int query_self_order_pay() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("order_status1", 10);
		params.put("order_status2", 16);
		params.put("order_form", 1);
		params.put("order_cat_no", 2);
		return this.orderFormService.selectCount(params);
	}

	public int query_self_order_evaluate() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("order_status", 40);
		params.put("order_form", 1);
		params.put("order_cat_no", 2);

		return this.orderFormService.selectCount(params);
	}

	public int query_self_all_order() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("order_form", 1);
		params.put("order_cat_no", 2);

		return this.orderFormService.selectCount(params);
	}

	public int query_self_return_apply() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_return_status", 5);
		params.put("goods_type", 0);

		return this.returngoodslogService.selectCount(params);
	}

	public int query_self_groupinfo_return_apply() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("status", 3);
		params.put("lifeGoods_goods_type", 1);

		return this.groupinfoService.selectCount(params);
	}

	public int query_goods_audit() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_status", -5);

		return this.groupinfoService.selectCount(params);
	}

	public int query_self_activity_goods() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_type", 0);
		params.put("activity_status", 2);

		return this.goodsService.selectCount(params);
	}

	public int query_self_group_goods() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_type", 0);
		params.put("group_buy", 2);

		return this.goodsService.selectCount(params);
	}

	public int query_self_group_life() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_type", 0);
		params.put("group_type", 1);
		params.put("group_buy", 2);

		return this.goodsService.selectCount(params);
	}

	public int query_self_free_goods() {

		Map<String, Object> params = Maps.newHashMap();
		params.put("freeStatus", 5);
		params.put("freeType", 1);

		return this.freegoodsService.selectCount(params);
	}

	public int query_predepositcash_apply() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("cash_status", 0);

		return this.PredepositCashService.selectCount(params);
	}
}
