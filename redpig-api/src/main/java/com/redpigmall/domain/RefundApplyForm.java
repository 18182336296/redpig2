package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.annotation.Lock;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: RefundApplyForm.java
 * </p>
 * 
 * <p>
 * Description:退款申请
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
 * @date 2014-10-15
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "refund_apply_form")
public class RefundApplyForm extends IdEntity {
	private Long user_id;//用户id
	private String user_name;//用户名
	@Column(columnDefinition = "int default 0")
	private int status;//状态
	private Date audit_date;//查账时间
	private String audit_user_name;//查账人
	private String order_id;//订单id
	private Long order_form_id;//订单id
	@Lock
	@Column(precision = 12, scale = 2)
	private BigDecimal refund_price;//退款金额
	private String store_id;//店铺id
	private String store_name;//店铺名称

	public Long getOrder_form_id() {
		return this.order_form_id;
	}

	public void setOrder_form_id(Long order_form_id) {
		this.order_form_id = order_form_id;
	}

	public String getStore_id() {
		return this.store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getStore_name() {
		return this.store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getAudit_date() {
		return this.audit_date;
	}

	public void setAudit_date(Date audit_date) {
		this.audit_date = audit_date;
	}

	public String getAudit_user_name() {
		return this.audit_user_name;
	}

	public void setAudit_user_name(String audit_user_name) {
		this.audit_user_name = audit_user_name;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getOrder_id() {
		return this.order_id;
	}

	public BigDecimal getRefund_price() {
		return this.refund_price;
	}

	public void setRefund_price(BigDecimal refund_price) {
		this.refund_price = refund_price;
	}
}
