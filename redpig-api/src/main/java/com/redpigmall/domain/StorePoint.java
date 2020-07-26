package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: StorePoint.java
 * </p>
 * 
 * <p>
 * Description: 店铺评分统计类,通过定时器类计算店铺评分信息，默认定时器每隔半小时计算一次
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_point")
public class StorePoint extends IdEntity {
	@OneToOne(fetch = FetchType.LAZY)
	private Store store;// 对应的店铺
	@OneToOne(mappedBy = "user",fetch = FetchType.LAZY)
	private User user;// 评价自营商品时，会评价点击确认发货的管理员
	private Date statTime;// 统计时间
	@Column(precision = 4, scale = 1)
	private BigDecimal store_evaluate;// 商品综合评分=（描述相符评价+服务态度评价+发货速度评价)/3
	@Column(precision = 4, scale = 1)
	private BigDecimal description_evaluate;// 描述相符评价
	@Column(precision = 4, scale = 1)
	private BigDecimal service_evaluate;// 服务态度评价
	@Column(precision = 4, scale = 1)
	private BigDecimal ship_evaluate;// 发货速度评价

	public StorePoint() {
	}

	public StorePoint(Long id, Date addTime) {
		super(id, addTime);
	}

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getStatTime() {
		return this.statTime;
	}

	public void setStatTime(Date statTime) {
		this.statTime = statTime;
	}

	public BigDecimal getStore_evaluate() {
		return this.store_evaluate;
	}

	public void setStore_evaluate(BigDecimal store_evaluate) {
		this.store_evaluate = store_evaluate;
	}

	public BigDecimal getDescription_evaluate() {
		return this.description_evaluate;
	}

	public void setDescription_evaluate(BigDecimal description_evaluate) {
		this.description_evaluate = description_evaluate;
	}

	public BigDecimal getService_evaluate() {
		return this.service_evaluate;
	}

	public void setService_evaluate(BigDecimal service_evaluate) {
		this.service_evaluate = service_evaluate;
	}

	public BigDecimal getShip_evaluate() {
		return this.ship_evaluate;
	}

	public void setShip_evaluate(BigDecimal ship_evaluate) {
		this.ship_evaluate = ship_evaluate;
	}
}
