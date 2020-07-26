package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: GoodsReturn.java
 * </p>
 * 
 * <p>
 * Description: 退货管理类
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
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_return")
public class GoodsReturn extends IdEntity {
	private String return_id;//退货id
	@ManyToOne(fetch = FetchType.LAZY)
	private OrderForm of;//订单
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;//用户
	@Column(columnDefinition = "LongText")
	private String return_info;//退货信息

	public GoodsReturn(Long id, Date addTime) {
		super(id, addTime);
	}

	public GoodsReturn() {
	}

	public OrderForm getOf() {
		return this.of;
	}

	public void setOf(OrderForm of) {
		this.of = of;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getReturn_id() {
		return this.return_id;
	}

	public void setReturn_id(String return_id) {
		this.return_id = return_id;
	}

	public String getReturn_info() {
		return this.return_info;
	}

	public void setReturn_info(String return_info) {
		this.return_info = return_info;
	}
}
