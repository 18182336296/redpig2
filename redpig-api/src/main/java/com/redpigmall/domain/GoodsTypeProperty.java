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
 * Title: GoodsTypeProperty.java
 * </p>
 * 
 * <p>
 * Description: 商品类型属性
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodstypeproperty")
public class GoodsTypeProperty extends IdEntity {
	private int sequence;// 属性排序
	private String name;// 属性名称
	@Column(columnDefinition = "LongText")
	private String value;// 属性可选值
	private boolean display;// 属性可见性
	@Column(columnDefinition = "LongText")
	private String hc_value;// 互斥属性值
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsType goodsType;// 商品类型
    private Long store_id;//店铺id

	public Long getStore_id() {
		return this.store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public GoodsTypeProperty(Long id, Date addTime) {
		super(id, addTime);
	}

	public GoodsTypeProperty() {
	}

	public String getHc_value() {
		return this.hc_value;
	}

	public void setHc_value(String hc_value) {
		this.hc_value = hc_value;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public GoodsType getGoodsType() {
		return this.goodsType;
	}

	public void setGoodsType(GoodsType goodsType) {
		this.goodsType = goodsType;
	}

	public Boolean getDisplay() {
		return this.display;
	}
	
	public void setDisplay(boolean display) {
		this.display = display;
	}
}
