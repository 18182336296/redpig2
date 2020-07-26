package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Address.java
 * </p>
 * 
 * <p>
 * Description: 用户收货地址类，用来管理所有买家的收货地址信息，买家在提交订单时候可以选择这里的收货地址
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "address")
public class Address extends IdEntity {
	private String trueName;// 收货人姓名
	@ManyToOne(fetch = FetchType.LAZY)
	private Area area;// 收货人地区
	private String area_info;// 收货人详细地址
	private String zip;// 邮政编码
	private String telephone;// 联系电话
	private String mobile;// 手机号码
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 地址对应的用户
	@Column(columnDefinition = "int default 0")
	private int default_val;// 是否为默认收货地址，1为默认地址

	public Address(Long id, Date addTime) {
		super(id, addTime);
	}

	public Address() {
	}

	public int getDefault_val() {
		return this.default_val;
	}

	public void setDefault_val(int default_val) {
		this.default_val = default_val;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTrueName() {
		return this.trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public Area getArea() {
		return this.area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public String getArea_info() {
		return this.area_info;
	}

	public void setArea_info(String area_info) {
		this.area_info = area_info;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
