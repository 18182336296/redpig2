package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ZTCGoldLog.java
 * </p>
 * 
 * <p>
 * Description: 直通车金币日志,记录所有直通金币日志
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "ztc_gold_log")
public class ZTCGoldLog extends IdEntity {
	private Long zgl_goods_id;// 日志商品id
	private String goods_name;//商品名称
	private String store_name;//店铺名称
	private String user_name;//用户名称
	private int zgl_gold;// 金币数量
	private int zgl_type;// 0为增加，1为减少
	private String zgl_content;// 描述

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getGoods_name() {
		return this.goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public String getStore_name() {
		return this.store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public Long getZgl_goods_id() {
		return this.zgl_goods_id;
	}

	public void setZgl_goods_id(Long zgl_goods_id) {
		this.zgl_goods_id = zgl_goods_id;
	}

	public ZTCGoldLog() {
	}

	public ZTCGoldLog(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getZgl_gold() {
		return this.zgl_gold;
	}

	public void setZgl_gold(int zgl_gold) {
		this.zgl_gold = zgl_gold;
	}

	public int getZgl_type() {
		return this.zgl_type;
	}

	public void setZgl_type(int zgl_type) {
		this.zgl_type = zgl_type;
	}

	public String getZgl_content() {
		return this.zgl_content;
	}

	public void setZgl_content(String zgl_content) {
		this.zgl_content = zgl_content;
	}
}
