package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: GoodsSpecProperty.java
 * </p>
 * 
 * <p>
 * Description: 商品规格属性管理类，用来描述商品属性信息
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsspecproperty")
public class GoodsSpecProperty extends IdEntity {
	private int sequence;//序号
	@Column(columnDefinition = "LongText")
	private String value;//规格值
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory specImage;//规格图片
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsSpecification spec;//商品规格
	@ManyToMany(mappedBy = "goods_specs")
	private List<Goods> goods_list = new ArrayList<Goods>();// 规格中所在商品
	@ManyToMany(mappedBy = "gsps")
	private List<GoodsCart> cart_list = new ArrayList<GoodsCart>();// 规格中所在商品购物车

	public GoodsSpecProperty() {
	}

	public GoodsSpecProperty(Long id, Date addTime) {
		super(id, addTime);
	}

	public GoodsSpecProperty(Long id) {
		super.setId(id);
	}

	public List<GoodsCart> getCart_list() {
		return this.cart_list;
	}

	public void setCart_list(List<GoodsCart> cart_list) {
		this.cart_list = cart_list;
	}

	public List<Goods> getGoods_list() {
		return this.goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Accessory getSpecImage() {
		return this.specImage;
	}

	public void setSpecImage(Accessory specImage) {
		this.specImage = specImage;
	}

	public GoodsSpecification getSpec() {
		return this.spec;
	}

	public void setSpec(GoodsSpecification spec) {
		this.spec = spec;
	}
}
