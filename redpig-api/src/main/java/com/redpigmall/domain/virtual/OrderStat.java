package com.redpigmall.domain.virtual;

import java.util.Date;

/**
 * 
 * <p>
 * Title: OrderStat.java
 * </p>
 * 
 * <p>
 * Description:订单统计管理类，该类用来在后台显示统计数据，不和数据表对应，根据时间区间查询数据并以图形、图表显示
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
 * @date 2014-6-5
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class OrderStat {
	/** 统计时间 */
	private Date stat_time;
	/** 当日订单总数*/
	private int order_count;
	/** 当日付款订单数*/
	private int order_pay_count;
	/** 当日发货订单数*/
	private int order_ship_count;
	/** 当日付完订单总金额*/
	private float order_amount;

	public Date getStat_time() {
		return this.stat_time;
	}

	public void setStat_time(Date stat_time) {
		this.stat_time = stat_time;
	}

	public int getOrder_count() {
		return this.order_count;
	}

	public void setOrder_count(int order_count) {
		this.order_count = order_count;
	}

	public int getOrder_pay_count() {
		return this.order_pay_count;
	}

	public void setOrder_pay_count(int order_pay_count) {
		this.order_pay_count = order_pay_count;
	}

	public int getOrder_ship_count() {
		return this.order_ship_count;
	}

	public void setOrder_ship_count(int order_ship_count) {
		this.order_ship_count = order_ship_count;
	}

	public float getOrder_amount() {
		return this.order_amount;
	}

	public void setOrder_amount(float order_amount) {
		this.order_amount = order_amount;
	}
}
