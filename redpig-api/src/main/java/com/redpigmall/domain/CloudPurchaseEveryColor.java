package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: CloudPurchaseEveryColor.java
 * </p>
 * 
 * <p>
 * Description: 
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cloud_purchase_everycolor")
public class CloudPurchaseEveryColor extends IdEntity {
	private String opencode;
	private String expect;
	private Date opentime;

	public CloudPurchaseEveryColor() {
	}

	public CloudPurchaseEveryColor(Long id) {
		super.setId(id);
	}

	public CloudPurchaseEveryColor(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getOpencode() {
		return this.opencode;
	}

	public void setOpencode(String opencode) {
		this.opencode = opencode;
	}

	public String getExpect() {
		return this.expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

	public Date getOpentime() {
		return this.opentime;
	}

	public void setOpentime(Date opentime) {
		this.opentime = opentime;
	}
}
