package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;


/**
 * <p>
 * Title: CloudPurchaseCode.java
 * </p>
 * 
 * <p>
 * Description: 云购号码
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cloud_purchase_code")
public class CloudPurchaseCode extends IdEntity {
	private Long user_id;//用户id
	private Long code;//号码
	private Long lottery_id;//云购旗号id
	@Column(columnDefinition = "int default 0")
	private int status;//云购状态

	public CloudPurchaseCode() {
	}

	public CloudPurchaseCode(Long id) {
		super.setId(id);
	}

	public CloudPurchaseCode(Long id, Date addTime) {
		super(id, addTime);
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getCode() {
		return this.code;
	}

	public void setCode(Long code) {
		this.code = code;
	}

	public Long getLottery_id() {
		return this.lottery_id;
	}

	public void setLottery_id(Long lottery_id) {
		this.lottery_id = lottery_id;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
