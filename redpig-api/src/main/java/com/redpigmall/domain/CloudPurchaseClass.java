package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: CloudPurchaseClass.java
 * </p>
 * 
 * <p>
 * Description: 云购分类
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
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cloud_purchase_class")
public class CloudPurchaseClass extends IdEntity {
	@Column(columnDefinition = "int default 0")
	private int sequence;//序号
	private String class_name;//类型名称
	private String img_url;//图片url

	public CloudPurchaseClass() {
	}

	public CloudPurchaseClass(Long id) {
		super.setId(id);
	}

	public CloudPurchaseClass(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getClass_name() {
		return this.class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getImg_url() {
		return this.img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}
}
