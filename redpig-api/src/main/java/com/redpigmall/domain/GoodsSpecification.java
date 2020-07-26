package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: GoodsSpecification.java
 * </p>
 * 
 * <p>
 * Description: 商品规格管理类，商家可以自定义规格属性，并与商家主营类目及详细类目进行关联，商家发布商品时只显示与当前分类进行关联的规格
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
 * @date 2014-10-22
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsspecification")
public class GoodsSpecification extends IdEntity {
	private String name;// 名称
	private int sequence;// 序号
	private String type;// 规格类型(文字或图片) 文字:text 图片:img
	@OneToMany(mappedBy = "spec")
	@OrderBy(value = "sequence asc")
	private List<GoodsSpecProperty> properties = new ArrayList<GoodsSpecProperty>();
	@Column(columnDefinition = "int default 0")
	private int spec_type;// 规格类型，0为平台自营规格，1为商家规格，商家规格时需要有对应的店铺
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 当规格类型为商家规格时对应的店铺
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass goodsclass;// 规格对应的主营商品分类,该分类为平台二级商品分类（level=1）

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = Globals.DEFAULT_TABLE_SUFFIX + "spec_gc_detail", joinColumns = @JoinColumn(name = "spec_id"), inverseJoinColumns = @JoinColumn(name = "spec_gc_id"))
	private List<GoodsClass> spec_goodsClass_detail = new ArrayList<GoodsClass>();// 规格对应的详细商品分类,该分类为平台三级商品分类（level=2）

	private String remark_name;//标记
	
	@Column(columnDefinition = "int default 0")
	private int spec_color;//规格颜色
	
	public String getRemark_name() {
		return this.remark_name;
	}

	public void setRemark_name(String remark_name) {
		this.remark_name = remark_name;
	}

	public int getSpec_color() {
		return this.spec_color;
	}

	public void setSpec_color(int spec_color) {
		this.spec_color = spec_color;
	}

	public GoodsSpecification() {
	}

	public GoodsSpecification(Long id, Date addTime) {
		super(id, addTime);
	}

	public GoodsClass getGoodsclass() {
		return this.goodsclass;
	}

	public void setGoodsclass(GoodsClass goodsclass) {
		this.goodsclass = goodsclass;
	}

	public List<GoodsClass> getSpec_goodsClass_detail() {
		return this.spec_goodsClass_detail;
	}

	public void setSpec_goodsClass_detail(List<GoodsClass> spec_goodsClass_detail) {
		this.spec_goodsClass_detail = spec_goodsClass_detail;
	}

	public int getSpec_type() {
		return this.spec_type;
	}

	public void setSpec_type(int spec_type) {
		this.spec_type = spec_type;
	}

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
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

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<GoodsSpecProperty> getProperties() {
		return this.properties;
	}

	public void setProperties(List<GoodsSpecProperty> properties) {
		this.properties = properties;
	}
	
}
