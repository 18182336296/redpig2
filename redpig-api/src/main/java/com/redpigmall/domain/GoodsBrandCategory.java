package com.redpigmall.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * 
 * <p>
 * Title: GoodsBrandCategory.java
 * </p>
 * 
 * <p>
 * Description:品牌类别管理类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "brandcategory")
public class GoodsBrandCategory extends IdEntity {
	private String name;//商品品牌分类名称
	private int sequence;
	@OneToMany(mappedBy = "category")
	private List<GoodsBrand> brands = Lists.newArrayList();//品牌

	public GoodsBrandCategory(Long id, Date addTime) {
		super(id, addTime);
	}

	public GoodsBrandCategory() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public List<GoodsBrand> getBrands() {
		return this.brands;
	}

	public void setBrands(List<GoodsBrand> brands) {
		this.brands = brands;
	}
}
