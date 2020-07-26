package com.redpigmall.domain;

import java.math.BigDecimal;
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
 * Title: Transport.java
 * </p>
 * 
 * <p>
 * Description:系统运费模板类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "transport")
public class Transport extends IdEntity {
	private String trans_name;// 运费模板名称
	@Column(columnDefinition = "int default 0")
	private int trans_time;// 发货时间
	@Column(columnDefinition = "int default 0")
	private int trans_type;// 0按件数，1按重量，2按体积
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;
	private boolean trans_mail;// 是否启用平邮
	@Column(columnDefinition = "LongText")
	private String trans_mail_info;// 平邮信息,使用json管理[{"city_id":-1,"city_name":"全国","trans_weight":1,"trans_fee":13.5,"trans_add_weight":1,"trans_add_fee":2},{"city_id":1,"city_name":"沈阳","trans_weight":1,"trans_fee":13.5,"trans_add_weight":1,"trans_add_fee":2}]
	private boolean trans_express;// 是否启用快递
	@Column(columnDefinition = "LongText")
	private String trans_express_info;// 快递信息,使用json管理[{"city_id":-1,"city_name":"全国","trans_weight":1,"trans_fee":13.5,"trans_add_weight":1,"trans_add_fee":2},{"city_id":1,"city_name":"沈阳","trans_weight":1,"trans_fee":13.5,"trans_add_weight":1,"trans_add_fee":2}]
	private boolean trans_ems;// 是否启用EMS
	@Column(columnDefinition = "LongText")
	private String trans_ems_info;// EMS信息,使用json管理[{"city_id":-1,"city_name":"全国","trans_weight":1,"trans_fee":13.5,"trans_add_weight":1,"trans_add_fee":2},{"city_id":1,"city_name":"沈阳","trans_weight":1,"trans_fee":13.5,"trans_add_weight":1,"trans_add_fee":2}]

	@Column(columnDefinition = "int default 0")
	private int trans_user;// 运费模板类型，0为自营模板，1为商家模板
	
	private int free_postage_status;
	
	private BigDecimal free_postage;
	
	public int getFree_postage_status() {
		return this.free_postage_status;
	}

	public BigDecimal getFree_postage() {
		return this.free_postage;
	}

	public void setFree_postage(BigDecimal free_postage) {
		this.free_postage = free_postage;
	}

	public void setFree_postage_status(int free_postage_status) {
		this.free_postage_status = free_postage_status;
	}

	public Transport() {
	}

	public Transport(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getTrans_user() {
		return this.trans_user;
	}

	public void setTrans_user(int trans_user) {
		this.trans_user = trans_user;
	}

	public int getTrans_time() {
		return this.trans_time;
	}

	public void setTrans_time(int trans_time) {
		this.trans_time = trans_time;
	}

	public int getTrans_type() {
		return this.trans_type;
	}

	public void setTrans_type(int trans_type) {
		this.trans_type = trans_type;
	}

	public Boolean getTrans_mail() {
		return this.trans_mail;
	}

	public void setTrans_mail(boolean trans_mail) {
		this.trans_mail = trans_mail;
	}

	public Boolean getTrans_express() {
		return this.trans_express;
	}
	
	public void setTrans_express(boolean trans_express) {
		this.trans_express = trans_express;
	}

	public Boolean getTrans_ems() {
		return this.trans_ems;
	}

	public void setTrans_ems(boolean trans_ems) {
		this.trans_ems = trans_ems;
	}

	public String getTrans_name() {
		return this.trans_name;
	}

	public void setTrans_name(String trans_name) {
		this.trans_name = trans_name;
	}

	public String getTrans_mail_info() {
		return this.trans_mail_info;
	}

	public void setTrans_mail_info(String trans_mail_info) {
		this.trans_mail_info = trans_mail_info;
	}

	public String getTrans_express_info() {
		return this.trans_express_info;
	}

	public void setTrans_express_info(String trans_express_info) {
		this.trans_express_info = trans_express_info;
	}

	public String getTrans_ems_info() {
		return this.trans_ems_info;
	}

	public void setTrans_ems_info(String trans_ems_info) {
		this.trans_ems_info = trans_ems_info;
	}

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
}
