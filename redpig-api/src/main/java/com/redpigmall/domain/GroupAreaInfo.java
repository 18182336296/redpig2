package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: GroupAreaInfo.java
 * </p>
 * 
 * <p>
 * Description: 生活类团购
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_areainfo")
public class GroupAreaInfo extends IdEntity {
	private String gai_name;//常用地址
	@ManyToOne(fetch = FetchType.LAZY)
	private Area area;//地址
	@Column(columnDefinition = "int default 0")
	private int gai_type;//类型
	private Long store_id;//店铺id
	private double gai_lng;//标注的坐标
	private double gai_lat;//

	public String getGai_name() {
		return this.gai_name;
	}

	public void setGai_name(String gai_name) {
		this.gai_name = gai_name;
	}

	public Area getArea() {
		return this.area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public int getGai_type() {
		return this.gai_type;
	}

	public void setGai_type(int gai_type) {
		this.gai_type = gai_type;
	}

	public Long getStore_id() {
		return this.store_id;
	}

	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}

	public double getGai_lng() {
		return this.gai_lng;
	}

	public void setGai_lng(double gai_lng) {
		this.gai_lng = gai_lng;
	}

	public double getGai_lat() {
		return this.gai_lat;
	}

	public void setGai_lat(double gai_lat) {
		this.gai_lat = gai_lat;
	}
}
