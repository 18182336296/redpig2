package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: InventoryOperation.java
 * </p>
 * 
 * <p>
 * Description:库存操作
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "inventory_operation")
public class InventoryOperation extends IdEntity {
	private Long inventory_id;
	private String storehouse_name;
	private String goods_name;
	private String spec_name;
	private int type;
	private int count;
	private String operation_info;

	public InventoryOperation() {
	}

	public InventoryOperation(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getOperation_info() {
		return this.operation_info;
	}

	public void setOperation_info(String operation_info) {
		this.operation_info = operation_info;
	}

	public String getStorehouse_name() {
		return this.storehouse_name;
	}

	public void setStorehouse_name(String storehouse_name) {
		this.storehouse_name = storehouse_name;
	}

	public String getGoods_name() {
		return this.goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getSpec_name() {
		return this.spec_name;
	}

	public void setSpec_name(String spec_name) {
		this.spec_name = spec_name;
	}

	public Long getInventory_id() {
		return this.inventory_id;
	}

	public void setInventory_id(Long inventory_id) {
		this.inventory_id = inventory_id;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
