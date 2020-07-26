package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: Inventory.java
 * </p>
 * 
 * <p>
 * Description:库
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
 * @date 2014年4月25日
 * 
 * @version redp
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "inventory")
public class Inventory extends IdEntity {
	private Long storehouse_id;//库id
	private String storehouse_name;//库名称
	private Long goods_id;//商品id
	private String goods_name;//商品名称
	private String spec_id;//规格id
	private String spec_name;//规格名称
	@Column(precision = 12, scale = 2)
	private BigDecimal price;//脚骨
	private int count;//库存数量

	public Inventory() {
	}

	public Inventory(Long id, Date addTime) {
		super(id, addTime);
	}

	public Long getStorehouse_id() {
		return this.storehouse_id;
	}

	public void setStorehouse_id(Long storehouse_id) {
		this.storehouse_id = storehouse_id;
	}

	public String getStorehouse_name() {
		return this.storehouse_name;
	}

	public void setStorehouse_name(String storehouse_name) {
		this.storehouse_name = storehouse_name;
	}

	public Long getGoods_id() {
		return this.goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_name() {
		return this.goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getSpec_id() {
		return this.spec_id;
	}

	public void setSpec_id(String spec_id) {
		this.spec_id = spec_id;
	}

	public String getSpec_name() {
		return this.spec_name;
	}

	public void setSpec_name(String spec_name) {
		this.spec_name = spec_name;
	}

	public BigDecimal getPrice() {
		return this.price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
