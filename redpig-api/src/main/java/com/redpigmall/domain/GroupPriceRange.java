package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: GroupPriceRange.java
 * </p>
 * 
 * <p>
 * Description: 团购价格区间,用在用户快速选择团购商品
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_price_range")
public class GroupPriceRange extends IdEntity {
	private String gpr_name;// 价格区间名称
	private int gpr_begin;// 区间下限
	private int gpr_end;// 区间上限

	public GroupPriceRange() {
	}

	public GroupPriceRange(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getGpr_name() {
		return this.gpr_name;
	}

	public void setGpr_name(String gpr_name) {
		this.gpr_name = gpr_name;
	}

	public int getGpr_begin() {
		return this.gpr_begin;
	}

	public void setGpr_begin(int gpr_begin) {
		this.gpr_begin = gpr_begin;
	}

	public int getGpr_end() {
		return this.gpr_end;
	}

	public void setGpr_end(int gpr_end) {
		this.gpr_end = gpr_end;
	}
}
