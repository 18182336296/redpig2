package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cloud_purchase_goods")
public class CloudPurchaseGoods extends IdEntity {
	private Long class_id;
	private String goods_name;
	@Column(columnDefinition = "int default 0")
	private int sell_num;
	@Column(columnDefinition = "int default 0")
	private int goodsNum;
	@Column(columnDefinition = "int default 0")
	private int goods_status;
	private String primary_photo;
	@Column(columnDefinition = "LongText")
	private String secondary_photo;
	@Column(columnDefinition = "LongText")
	private String goods_description;
	@Column(columnDefinition = "LongText")
	private String goods_detail;
	@Column(columnDefinition = "int default 0")
	private int goods_price;
	@Column(columnDefinition = "int default 0")
	private int pay_type;
	@Column(columnDefinition = "int default 0")
	private int least_rmb;

	public int getGoodsNum() {
		return this.goodsNum;
	}

	public void setGoodsNum(int goodsNum) {
		this.goodsNum = goodsNum;
	}

	public int getSell_num() {
		return this.sell_num;
	}

	public void setSell_num(int sell_num) {
		this.sell_num = sell_num;
	}

	public CloudPurchaseGoods() {
	}

	public CloudPurchaseGoods(Long id) {
		super.setId(id);
	}

	public CloudPurchaseGoods(Long id, Date addTime) {
		super(id, addTime);
	}

	public Long getClass_id() {
		return this.class_id;
	}

	public void setClass_id(Long class_id) {
		this.class_id = class_id;
	}

	public int getGoods_status() {
		return this.goods_status;
	}

	public void setGoods_status(int goods_status) {
		this.goods_status = goods_status;
	}

	public String getGoods_name() {
		return this.goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getPrimary_photo() {
		return this.primary_photo;
	}

	public void setPrimary_photo(String primary_photo) {
		this.primary_photo = primary_photo;
	}

	public String getSecondary_photo() {
		return this.secondary_photo;
	}

	public void setSecondary_photo(String secondary_photo) {
		this.secondary_photo = secondary_photo;
	}

	public String getGoods_description() {
		return this.goods_description;
	}

	public void setGoods_description(String goods_description) {
		this.goods_description = goods_description;
	}

	public String getGoods_detail() {
		return this.goods_detail;
	}

	public void setGoods_detail(String goods_detail) {
		this.goods_detail = goods_detail;
	}

	public int getGoods_price() {
		return this.goods_price;
	}

	public void setGoods_price(int goods_price) {
		this.goods_price = goods_price;
	}

	public int getPay_type() {
		return this.pay_type;
	}

	public void setPay_type(int pay_type) {
		this.pay_type = pay_type;
	}

	public int getLeast_rmb() {
		return this.least_rmb;
	}

	public void setLeast_rmb(int least_rmb) {
		this.least_rmb = least_rmb;
	}
}
