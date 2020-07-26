package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: Snapshot.java
 * </p>
 * 
 * <p>
 * Description:订单快照
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
 * @date 2014-10-31
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "snapshot")
public class Snapshot extends IdEntity {
	private String good_name;//商品名称
	private String good_id;//商品id
	private String good_num;//商品数量
	private String good_price;//商品价格
	private String good_source;//商品来源
	private String address;//商品地址
	private String fee;//报酬
	private String good_cod;//运费
	private String good_mian_img;//主图
	@Column(columnDefinition = "LongText")
	private String good_info;//商品信息
	@Column(columnDefinition = "LongText")
	private String goods_service;//商品服务
	@Column(columnDefinition = "LongText")
	private String good_pack_details;//保证清单
	private String gc;//商品类型
	@Column(columnDefinition = "LongText")
	private String store_info;//店铺
	private Long user_id;//用户id
	private String activity_status;//活动状态
	private String order_id;//订单id

	public String getOrder_id() {
		return this.order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getActivity_status() {
		return this.activity_status;
	}

	public void setActivity_status(String activity_status) {
		this.activity_status = activity_status;
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getGc() {
		return this.gc;
	}

	public void setGc(String gc) {
		this.gc = gc;
	}

	public String getGood_name() {
		return this.good_name;
	}

	public void setGood_name(String good_name) {
		this.good_name = good_name;
	}

	public String getGood_price() {
		return this.good_price;
	}

	public void setGood_price(String good_price) {
		this.good_price = good_price;
	}

	public String getGood_source() {
		return this.good_source;
	}

	public void setGood_source(String good_source) {
		this.good_source = good_source;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGood_mian_img() {
		return this.good_mian_img;
	}

	public void setGood_mian_img(String good_mian_img) {
		this.good_mian_img = good_mian_img;
	}

	public String getGood_info() {
		return this.good_info;
	}

	public void setGood_info(String good_info) {
		this.good_info = good_info;
	}

	public String getGood_id() {
		return this.good_id;
	}

	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}

	public String getGood_num() {
		return this.good_num;
	}

	public String getStore_info() {
		return this.store_info;
	}

	public String getFee() {
		return this.fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getGood_cod() {
		return this.good_cod;
	}

	public void setGood_cod(String good_cod) {
		this.good_cod = good_cod;
	}

	public void setStore_info(String store_info) {
		this.store_info = store_info;
	}

	public void setGood_num(String good_num) {
		this.good_num = good_num;
	}

	public String getGoods_service() {
		return this.goods_service;
	}

	public void setGoods_service(String goods_service) {
		this.goods_service = goods_service;
	}

	public String getGood_pack_details() {
		return this.good_pack_details;
	}

	public void setGood_pack_details(String good_pack_details) {
		this.good_pack_details = good_pack_details;
	}
}
