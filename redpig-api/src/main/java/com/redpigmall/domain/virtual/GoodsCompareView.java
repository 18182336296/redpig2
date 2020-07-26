package com.redpigmall.domain.virtual;

import java.math.BigDecimal;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * <p>
 * Title: GoodsCompareView.java
 * </p>
 * 
 * <p>
 * Description: 商品对比栏信息管理类，用来封装商品对比栏数据，用在商品对比页
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
 * @version redpigmall_b2b2c 2016
 */
public class GoodsCompareView {
	/** 商品id*/
	private Long goods_id;
	/** 商品图地址，使用中号尺寸图片*/
	private String goods_img;
	/** 商品名称*/
	private String goods_name;
	/** 商品价格*/
	private BigDecimal goods_price;
	/** 商品地址*/
	private String goods_url;
	/** 商品品牌*/
	private String goods_brand;
	/** 是否支持增值税发票*/
	private String tax_invoice;
	/** 是否支持货到付款*/
	private String goods_cod;
	/** 商品重量*/
	private BigDecimal goods_weight;
	/** 好评率*/
	private String well_evaluate;
	/** 中评率*/
	private String middle_evaluate;
	/** 差评率*/
	private String bad_evaluate;
	/** 商品相关属性键值对，如：颜色-红,黄，蓝*/
	@SuppressWarnings("rawtypes")
	private Map props = Maps.newHashMap();

	@SuppressWarnings("rawtypes")
	public Map getProps() {
		return this.props;
	}

	@SuppressWarnings("rawtypes")
	public void setProps(Map props) {
		this.props = props;
	}

	public Long getGoods_id() {
		return this.goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getGoods_img() {
		return this.goods_img;
	}

	public void setGoods_img(String goods_img) {
		this.goods_img = goods_img;
	}

	public String getGoods_name() {
		return this.goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public BigDecimal getGoods_price() {
		return this.goods_price;
	}

	public void setGoods_price(BigDecimal goods_price) {
		this.goods_price = goods_price;
	}

	public String getGoods_url() {
		return this.goods_url;
	}

	public void setGoods_url(String goods_url) {
		this.goods_url = goods_url;
	}

	public String getGoods_brand() {
		return this.goods_brand;
	}

	public void setGoods_brand(String goods_brand) {
		this.goods_brand = goods_brand;
	}

	public String getTax_invoice() {
		return this.tax_invoice;
	}

	public void setTax_invoice(String tax_invoice) {
		this.tax_invoice = tax_invoice;
	}

	public String getGoods_cod() {
		return this.goods_cod;
	}

	public void setGoods_cod(String goods_cod) {
		this.goods_cod = goods_cod;
	}

	public BigDecimal getGoods_weight() {
		return this.goods_weight;
	}

	public void setGoods_weight(BigDecimal goods_weight) {
		this.goods_weight = goods_weight;
	}

	public String getWell_evaluate() {
		return this.well_evaluate;
	}

	public void setWell_evaluate(String well_evaluate) {
		this.well_evaluate = well_evaluate;
	}

	public String getMiddle_evaluate() {
		return this.middle_evaluate;
	}

	public void setMiddle_evaluate(String middle_evaluate) {
		this.middle_evaluate = middle_evaluate;
	}

	public String getBad_evaluate() {
		return this.bad_evaluate;
	}

	public void setBad_evaluate(String bad_evaluate) {
		this.bad_evaluate = bad_evaluate;
	}
}
