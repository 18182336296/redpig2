package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: VatInvoice.java
 * </p>
 * 
 * <p>
 * Description:增值发票
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "vatinvoice")
public class VatInvoice extends IdEntity {
	private String companyName;//公司名称
	private String registerAddress;//注册地址
	private String taxpayerCode;//纳税人代码
	private String registerPhone;//注册电话
	private String registerbankName;//注册银行
	private String registerbankAccount;//银行账号
	@Column(columnDefinition = "int default 0")
	private int status;//状态
	@OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	private Accessory user_license;//
	@OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	private Accessory tax_reg_card;
	@OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	private Accessory tax_general_card;
	@OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	private Accessory bank_permit_image;
	private Long user_id;
	private String user_name;
	@Column(columnDefinition = "LongText")
	private String remark;

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getCompanyName() {
		return this.companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getRegisterAddress() {
		return this.registerAddress;
	}

	public void setRegisterAddress(String registerAddress) {
		this.registerAddress = registerAddress;
	}

	public String getTaxpayerCode() {
		return this.taxpayerCode;
	}

	public void setTaxpayerCode(String taxpayerCode) {
		this.taxpayerCode = taxpayerCode;
	}

	public String getRegisterPhone() {
		return this.registerPhone;
	}

	public void setRegisterPhone(String registerPhone) {
		this.registerPhone = registerPhone;
	}

	public String getRegisterbankName() {
		return this.registerbankName;
	}

	public void setRegisterbankName(String registerbankName) {
		this.registerbankName = registerbankName;
	}

	public String getRegisterbankAccount() {
		return this.registerbankAccount;
	}

	public void setRegisterbankAccount(String registerbankAccount) {
		this.registerbankAccount = registerbankAccount;
	}

	public Accessory getUser_license() {
		return this.user_license;
	}

	public void setUser_license(Accessory user_license) {
		this.user_license = user_license;
	}

	public Accessory getTax_reg_card() {
		return this.tax_reg_card;
	}

	public void setTax_reg_card(Accessory tax_reg_card) {
		this.tax_reg_card = tax_reg_card;
	}

	public Accessory getTax_general_card() {
		return this.tax_general_card;
	}

	public void setTax_general_card(Accessory tax_general_card) {
		this.tax_general_card = tax_general_card;
	}

	public Accessory getBank_permit_image() {
		return this.bank_permit_image;
	}

	public void setBank_permit_image(Accessory bank_permit_image) {
		this.bank_permit_image = bank_permit_image;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
}
