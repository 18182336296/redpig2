package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: IntegralGoodsCart.java
 * </p>
 * 
 * <p>
 * Description: 积分商城兑换购物车
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integral_goodscart")
public class IntegralGoodsCart extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private IntegralGoods goods;// 兑换的礼品
	private int count;// 兑换数量
	@Column(precision = 12, scale = 2)
	private BigDecimal trans_fee;// 购物车运费
	private int integral;// 积分小计
	private String user_id;//用户id

	public String getUser_id() {
		return this.user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public IntegralGoodsCart() {
	}

	public IntegralGoodsCart(Long id, Date addTime) {
		super(id, addTime);
	}

	public BigDecimal getTrans_fee() {
		return this.trans_fee;
	}

	public void setTrans_fee(BigDecimal trans_fee) {
		this.trans_fee = trans_fee;
	}

	public int getIntegral() {
		return this.integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public IntegralGoods getGoods() {
		return this.goods;
	}

	public void setGoods(IntegralGoods goods) {
		this.goods = goods;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
