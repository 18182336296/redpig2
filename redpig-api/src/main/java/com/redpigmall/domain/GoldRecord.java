package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: GoldRecord.java
 * </p>
 * 
 * <p>
 * Description: 金币充值记录管理类
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
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "gold_record")
public class GoldRecord extends IdEntity {
	private String gold_sn;// 金币充值编号，以gold开头
	@ManyToOne(fetch = FetchType.LAZY)
	private User gold_user;// 操作用户
	private String gold_payment;// 兑换时的支付方式
	private int gold_money;// 兑换时的人民币数，只接受整数兑换
	private int gold_count;// 兑换金币数
	@Column(columnDefinition = "LongText")
	private String gold_exchange_info;// 兑换说明
	@ManyToOne(fetch = FetchType.LAZY)
	private User gold_admin;// 操作管理员
	@Column(columnDefinition = "LongText")
	private String gold_admin_info;// 操作说明
	private Date gold_admin_time;// 管理员操作时间
	private int gold_status;// 金币兑换状态，0为未完成，1为成功，-1已关闭
	private int gold_pay_status;// 支付状态，0为等待支付，1为线下提交支付完成申请，2为支付成功
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "gr")
	private GoldLog log;// 金币日志，反向映射

	@Column(columnDefinition = "LongText")
	private String order_sign;

	public GoldRecord() {
	}

	public GoldRecord(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getOrder_sign() {
		return this.order_sign;
	}

	public void setOrder_sign(String order_sign) {
		this.order_sign = order_sign;
	}

	public String getGold_sn() {
		return this.gold_sn;
	}

	public void setGold_sn(String gold_sn) {
		this.gold_sn = gold_sn;
	}

	public User getGold_user() {
		return this.gold_user;
	}

	public void setGold_user(User gold_user) {
		this.gold_user = gold_user;
	}

	public String getGold_payment() {
		return this.gold_payment;
	}

	public void setGold_payment(String gold_payment) {
		this.gold_payment = gold_payment;
	}

	public int getGold_money() {
		return this.gold_money;
	}

	public void setGold_money(int gold_money) {
		this.gold_money = gold_money;
	}

	public String getGold_exchange_info() {
		return this.gold_exchange_info;
	}

	public void setGold_exchange_info(String gold_exchange_info) {
		this.gold_exchange_info = gold_exchange_info;
	}

	public User getGold_admin() {
		return this.gold_admin;
	}

	public void setGold_admin(User gold_admin) {
		this.gold_admin = gold_admin;
	}

	public String getGold_admin_info() {
		return this.gold_admin_info;
	}

	public void setGold_admin_info(String gold_admin_info) {
		this.gold_admin_info = gold_admin_info;
	}

	public Date getGold_admin_time() {
		return this.gold_admin_time;
	}

	public void setGold_admin_time(Date gold_admin_time) {
		this.gold_admin_time = gold_admin_time;
	}

	public int getGold_status() {
		return this.gold_status;
	}

	public void setGold_status(int gold_status) {
		this.gold_status = gold_status;
	}

	public int getGold_pay_status() {
		return this.gold_pay_status;
	}

	public void setGold_pay_status(int gold_pay_status) {
		this.gold_pay_status = gold_pay_status;
	}

	public int getGold_count() {
		return this.gold_count;
	}

	public void setGold_count(int gold_count) {
		this.gold_count = gold_count;
	}

	public GoldLog getLog() {
		return this.log;
	}

	public void setLog(GoldLog log) {
		this.log = log;
	}
}
