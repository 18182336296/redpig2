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
 * Title: CouponInfo.java
 * </p>
 * 
 * <p>
 * Description:系统优惠券详情类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "coupon_info")
public class CouponInfo extends IdEntity {
	private String coupon_sn;// 优惠券编号
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 优惠券拥有的用户
	@ManyToOne(fetch = FetchType.LAZY)
	private Coupon coupon;// 对应的优惠券信息
	@Column(columnDefinition = "int default 0")
	private int status;// 优惠券信息状态，默认为0，,使用后为1,-1为过期
	private Long store_id;//店铺id
	private String store_name;//店铺名称
	private BigDecimal coupon_amount;//使用优惠券可以抵扣的钱
	private BigDecimal coupon_order_amount;//满足多少钱可以使用
	private Date endDate;//优惠券结束日期

	public Long getStore_id() {
		return this.store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public String getStore_name() {
		return this.store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public BigDecimal getCoupon_amount() {
		return this.coupon_amount;
	}

	public void setCoupon_amount(BigDecimal coupon_amount) {
		this.coupon_amount = coupon_amount;
	}

	public BigDecimal getCoupon_order_amount() {
		return this.coupon_order_amount;
	}

	public void setCoupon_order_amount(BigDecimal coupon_order_amount) {
		this.coupon_order_amount = coupon_order_amount;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public CouponInfo(Long id, Date addTime) {
		super(id, addTime);
	}

	public CouponInfo() {
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCoupon_sn() {
		return this.coupon_sn;
	}

	public void setCoupon_sn(String coupon_sn) {
		this.coupon_sn = coupon_sn;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Coupon getCoupon() {
		return this.coupon;
	}

	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
}
