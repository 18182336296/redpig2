package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: CloudPurchaseCart.java
 * </p>
 * 
 * <p>
 * Description: 云购购物车
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
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cloud_purchase_cart")
public class CloudPurchaseCart extends IdEntity {
	private Long user_id;//用户id
	@ManyToOne
	private CloudPurchaseLottery cloudPurchaseLottery;//云购旗号
	@Column(columnDefinition = "int default 1")
	private int purchased_times;//云购时间

	public CloudPurchaseCart() {
	}

	public CloudPurchaseCart(Long id) {
		super.setId(id);
	}

	public CloudPurchaseCart(Long id, Date addTime) {
		super(id, addTime);
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public CloudPurchaseLottery getCloudPurchaseLottery() {
		return this.cloudPurchaseLottery;
	}

	public void setCloudPurchaseLottery(
			CloudPurchaseLottery cloudPurchaseLottery) {
		this.cloudPurchaseLottery = cloudPurchaseLottery;
	}

	public int getPurchased_times() {
		return this.purchased_times;
	}

	public void setPurchased_times(int purchased_times) {
		this.purchased_times = purchased_times;
	}
}
