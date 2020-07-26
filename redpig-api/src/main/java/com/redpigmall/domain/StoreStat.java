package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: StoreStat.java
 * </p>
 * 
 * <p>
 * Description:商城统计类，每个30分钟统计一次,超级管理员登录后台后显示该数据信息
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
 * @date 2014-4-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_stat")
public class StoreStat extends IdEntity {

	private int predepositcash_apply_count;//现金支付申请

	@Column(precision = 12, scale = 2)
	private BigDecimal order_amount;//订单金额
	private Date next_time;//下一次时间
	// 一周动态
	private int week_user;// 一周新增会员
	private int week_live_user;// 一周活跃会员数量
	private int week_goods;// 一周新增商品数
	private int week_store;// 一周新增点店铺数
	private int week_order;// 一周新增订单数
	private int week_complaint;// 一周新增投诉数量
	private int week_goods_group;// 一周新增商品团购数量
	private int week_life_group;// 一周新增生活团购数量
	private int week_ztc;// 一周新增直通车数量
	private int week_delivery;// 一周新增自提点数量
	private int week_circle;// 一周新增圈子数量
	private int week_information;// 一周新增资讯数量
	private int week_invitation;// 一周新增帖子数量
	// 商城统计
	private int all_user;// 会员总数
	private int all_store;// 店铺总数
	private int store_audit;// 店铺待审核数量
	private int all_goods;// 商品总数
	private int not_payoff_num;// 未结算账单数
	private int not_refund;// 未处理的商品退款申请数
	private int not_grouplife_refund;// 未处理的消费码退款申请数
	private int all_sale_amount;// 总销售金额
	private int all_commission_amount;// 总销售佣金
	private int all_payoff_amount;// 总结算金额
	private int all_payoff_amount_reality;// 总实际结算金额
	@Column(precision = 12, scale = 2)
	private BigDecimal all_user_balance;// 商城所有会员总预存款
	private int ztc_audit_count;// 直通车申请数量
	private int delivery_audit_count;// 自提点申请数量
	private int information_audit_count;// 资讯申请数量
	// 自营统计
	private int self_goods;// 自营出售中的商品
	private int self_storage_goods;// 自营仓库中的商品
	private int self_order_shipping;// 自营待发货订单
	private int self_order_pay;// 自营待付款订单
	private int self_order_evaluate;// 自营待评价订单
	private int self_all_order;// 自营总交易订单数
	private int self_return_apply;// 自营新退货申请
	private int self_grouplife_refund;// 自营未处理的消费码退款申请数
	// 2015版新增统计信息
	private int goods_audit;// 未审核商品数量
	private int self_goods_consult;// 自营未回复咨询信息
	private int self_activity_goods;// 自营促销商品
	private int self_group_goods;// 自营商品购
	private int self_group_life;// 自营生活购
	private int self_free_goods;// 自营0元试用
	public StoreStat() {
	}

	public int getSelf_activity_goods() {
		return this.self_activity_goods;
	}

	public int getPredepositcash_apply_count() {
		return this.predepositcash_apply_count;
	}

	public void setPredepositcash_apply_count(int predepositcash_apply_count) {
		this.predepositcash_apply_count = predepositcash_apply_count;
	}

	public void setSelf_activity_goods(int self_activity_goods) {
		this.self_activity_goods = self_activity_goods;
	}

	public int getSelf_group_goods() {
		return this.self_group_goods;
	}

	public void setSelf_group_goods(int self_group_goods) {
		this.self_group_goods = self_group_goods;
	}

	public int getSelf_group_life() {
		return this.self_group_life;
	}

	public void setSelf_group_life(int self_group_life) {
		this.self_group_life = self_group_life;
	}

	public int getSelf_free_goods() {
		return this.self_free_goods;
	}

	public void setSelf_free_goods(int self_free_goods) {
		this.self_free_goods = self_free_goods;
	}

	public int getZtc_audit_count() {
		return this.ztc_audit_count;
	}

	public void setZtc_audit_count(int ztc_audit_count) {
		this.ztc_audit_count = ztc_audit_count;
	}

	public int getDelivery_audit_count() {
		return this.delivery_audit_count;
	}

	public void setDelivery_audit_count(int delivery_audit_count) {
		this.delivery_audit_count = delivery_audit_count;
	}

	public int getInformation_audit_count() {
		return this.information_audit_count;
	}

	public void setInformation_audit_count(int information_audit_count) {
		this.information_audit_count = information_audit_count;
	}

	public int getWeek_complaint() {
		return this.week_complaint;
	}

	public void setWeek_complaint(int week_complaint) {
		this.week_complaint = week_complaint;
	}

	public int getWeek_ztc() {
		return this.week_ztc;
	}

	public void setWeek_ztc(int week_ztc) {
		this.week_ztc = week_ztc;
	}

	public int getWeek_delivery() {
		return this.week_delivery;
	}

	public void setWeek_delivery(int week_delivery) {
		this.week_delivery = week_delivery;
	}

	public int getWeek_circle() {
		return this.week_circle;
	}

	public void setWeek_circle(int week_circle) {
		this.week_circle = week_circle;
	}

	public int getWeek_information() {
		return this.week_information;
	}

	public void setWeek_information(int week_information) {
		this.week_information = week_information;
	}

	public int getWeek_invitation() {
		return this.week_invitation;
	}

	public void setWeek_invitation(int week_invitation) {
		this.week_invitation = week_invitation;
	}

	public StoreStat(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getSelf_goods_consult() {
		return this.self_goods_consult;
	}

	public void setSelf_goods_consult(int self_goods_consult) {
		this.self_goods_consult = self_goods_consult;
	}

	public int getGoods_audit() {
		return this.goods_audit;
	}

	public void setGoods_audit(int goods_audit) {
		this.goods_audit = goods_audit;
	}

	public int getSelf_grouplife_refund() {
		return this.self_grouplife_refund;
	}

	public void setSelf_grouplife_refund(int self_grouplife_refund) {
		this.self_grouplife_refund = self_grouplife_refund;
	}

	public int getNot_grouplife_refund() {
		return this.not_grouplife_refund;
	}

	public void setNot_grouplife_refund(int not_grouplife_refund) {
		this.not_grouplife_refund = not_grouplife_refund;
	}

	public BigDecimal getAll_user_balance() {
		return this.all_user_balance;
	}

	public void setAll_user_balance(BigDecimal all_user_balance) {
		this.all_user_balance = all_user_balance;
	}

	public int getWeek_goods_group() {
		return this.week_goods_group;
	}

	public void setWeek_goods_group(int week_goods_group) {
		this.week_goods_group = week_goods_group;
	}

	public int getWeek_life_group() {
		return this.week_life_group;
	}

	public void setWeek_life_group(int week_life_group) {
		this.week_life_group = week_life_group;
	}

	public int getWeek_live_user() {
		return this.week_live_user;
	}

	public void setWeek_live_user(int week_live_user) {
		this.week_live_user = week_live_user;
	}

	public int getSelf_goods() {
		return this.self_goods;
	}

	public void setSelf_goods(int self_goods) {
		this.self_goods = self_goods;
	}

	public int getSelf_storage_goods() {
		return this.self_storage_goods;
	}

	public void setSelf_storage_goods(int self_storage_goods) {
		this.self_storage_goods = self_storage_goods;
	}

	public int getSelf_order_shipping() {
		return this.self_order_shipping;
	}

	public void setSelf_order_shipping(int self_order_shipping) {
		this.self_order_shipping = self_order_shipping;
	}

	public int getSelf_order_pay() {
		return this.self_order_pay;
	}

	public void setSelf_order_pay(int self_order_pay) {
		this.self_order_pay = self_order_pay;
	}

	public int getSelf_order_evaluate() {
		return this.self_order_evaluate;
	}

	public void setSelf_order_evaluate(int self_order_evaluate) {
		this.self_order_evaluate = self_order_evaluate;
	}

	public int getSelf_all_order() {
		return this.self_all_order;
	}

	public void setSelf_all_order(int self_all_order) {
		this.self_all_order = self_all_order;
	}

	public int getSelf_return_apply() {
		return this.self_return_apply;
	}

	public void setSelf_return_apply(int self_return_apply) {
		this.self_return_apply = self_return_apply;
	}

	public int getNot_payoff_num() {
		return this.not_payoff_num;
	}

	public void setNot_payoff_num(int not_payoff_num) {
		this.not_payoff_num = not_payoff_num;
	}

	public int getNot_refund() {
		return this.not_refund;
	}

	public void setNot_refund(int not_refund) {
		this.not_refund = not_refund;
	}

	public int getAll_sale_amount() {
		return this.all_sale_amount;
	}

	public void setAll_sale_amount(int all_sale_amount) {
		this.all_sale_amount = all_sale_amount;
	}

	public int getAll_commission_amount() {
		return this.all_commission_amount;
	}

	public void setAll_commission_amount(int all_commission_amount) {
		this.all_commission_amount = all_commission_amount;
	}

	public int getAll_payoff_amount() {
		return this.all_payoff_amount;
	}

	public void setAll_payoff_amount(int all_payoff_amount) {
		this.all_payoff_amount = all_payoff_amount;
	}

	public int getAll_payoff_amount_reality() {
		return this.all_payoff_amount_reality;
	}

	public void setAll_payoff_amount_reality(int all_payoff_amount_reality) {
		this.all_payoff_amount_reality = all_payoff_amount_reality;
	}

	public int getWeek_user() {
		return this.week_user;
	}

	public void setWeek_user(int week_user) {
		this.week_user = week_user;
	}

	public int getWeek_goods() {
		return this.week_goods;
	}

	public void setWeek_goods(int week_goods) {
		this.week_goods = week_goods;
	}

	public int getWeek_store() {
		return this.week_store;
	}

	public void setWeek_store(int week_store) {
		this.week_store = week_store;
	}

	public int getWeek_order() {
		return this.week_order;
	}

	public void setWeek_order(int week_order) {
		this.week_order = week_order;
	}

	public int getAll_user() {
		return this.all_user;
	}

	public void setAll_user(int all_user) {
		this.all_user = all_user;
	}

	public int getAll_store() {
		return this.all_store;
	}

	public void setAll_store(int all_store) {
		this.all_store = all_store;
	}

	public int getStore_audit() {
		return this.store_audit;
	}

	public void setStore_audit(int store_audit) {
		this.store_audit = store_audit;
	}

	public int getAll_goods() {
		return this.all_goods;
	}

	public void setAll_goods(int all_goods) {
		this.all_goods = all_goods;
	}

	public BigDecimal getOrder_amount() {
		return this.order_amount;
	}

	public void setOrder_amount(BigDecimal order_amount) {
		this.order_amount = order_amount;
	}

	public Date getNext_time() {
		return this.next_time;
	}

	public void setNext_time(Date next_time) {
		this.next_time = next_time;
	}
}
