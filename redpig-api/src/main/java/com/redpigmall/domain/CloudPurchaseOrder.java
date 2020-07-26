package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cloud_purchase_order")
public class CloudPurchaseOrder extends IdEntity {
	private String odrdersn;
	private Long user_id;
	private String orderInfo;
	private int price;
	private int status;
	private String payment;
	private Date payTime;
	@Column(columnDefinition = "LongText")
	private String order_sign;
	private String igo_order_sn;

	public CloudPurchaseOrder() {
	}

	public CloudPurchaseOrder(Long id) {
		super.setId(id);
	}

	public CloudPurchaseOrder(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getOdrdersn() {
		return this.odrdersn;
	}

	public void setOdrdersn(String odrdersn) {
		this.odrdersn = odrdersn;
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getOrderInfo() {
		return this.orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}

	public int getPrice() {
		return this.price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getPayment() {
		return this.payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getOrder_sign() {
		return this.order_sign;
	}

	public void setOrder_sign(String order_sign) {
		this.order_sign = order_sign;
	}

	public Date getPayTime() {
		return this.payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getIgo_order_sn() {
		return this.igo_order_sn;
	}

	public void setIgo_order_sn(String igo_order_sn) {
		this.igo_order_sn = igo_order_sn;
	}
}
