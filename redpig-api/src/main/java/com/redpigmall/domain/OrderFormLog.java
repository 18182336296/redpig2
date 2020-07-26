package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: OrderFormLog.java
 * </p>
 * 
 * <p>
 * Description: 订单日志类,用来记录所有订单操作，包括订单提交、付款、发货、收货等等
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "order_log")
public class OrderFormLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;// 对应的订单
	private String log_info;// 日志信息
	private Long log_user_id;// 日志操作员
	private String log_user_name;
	@Column(columnDefinition = "LongText")
	private String state_info;// 操作日志详情

	public OrderFormLog() {
	}

	public OrderFormLog(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getState_info() {
		return this.state_info;
	}

	public void setState_info(String state_info) {
		this.state_info = state_info;
	}

	public OrderForm getOf() {
		return this.of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public String getLog_info() {
		return this.log_info;
	}

	public void setLog_info(String log_info) {
		this.log_info = log_info;
	}

	public Long getLog_user_id() {
		return this.log_user_id;
	}

	public void setLog_user_id(Long log_user_id) {
		this.log_user_id = log_user_id;
	}

	public String getLog_user_name() {
		return this.log_user_name;
	}

	public void setLog_user_name(String log_user_name) {
		this.log_user_name = log_user_name;
	}
}
