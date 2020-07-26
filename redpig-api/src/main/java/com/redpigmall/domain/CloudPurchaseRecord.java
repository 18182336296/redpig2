package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cloud_purchase_record")
public class CloudPurchaseRecord extends IdEntity {
	private Long user_id;
	private String user_name;
	private String user_photo;
	private String payTime;
	private String payTimeStamp;
	@ManyToOne
	private CloudPurchaseLottery cloudPurchaseLottery;
	@Column(columnDefinition = "int default 1")
	private int purchased_times;
	@Column(columnDefinition = "LongText")
	private String purchased_codes;
	@Column(columnDefinition = "int default 0")
	private int status;
	@Transient
	private int total_purchased_times;

	@Transient
	public int getTotal_purchased_times() {
		return this.total_purchased_times;
	}

	public void setTotal_purchased_times(int total_purchased_times) {
		this.total_purchased_times = total_purchased_times;
	}

	public CloudPurchaseRecord() {
	}

	public CloudPurchaseRecord(Long id) {
		super.setId(id);
	}

	public CloudPurchaseRecord(Long id, Date addTime) {
		super(id, addTime);
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public CloudPurchaseLottery getCloudPurchaseLottery() {
		return this.cloudPurchaseLottery;
	}

	public void setCloudPurchaseLottery(
			CloudPurchaseLottery cloudPurchaseLottery) {
		this.cloudPurchaseLottery = cloudPurchaseLottery;
	}

	public int getPurchased_times() {
		return this.purchased_times;
	}

	public void setPurchased_times(int purchased_times) {
		this.purchased_times = purchased_times;
	}

	public String getPurchased_codes() {
		return this.purchased_codes;
	}

	public void setPurchased_codes(String purchased_codes) {
		this.purchased_codes = purchased_codes;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_photo() {
		return this.user_photo;
	}

	public void setUser_photo(String user_photo) {
		this.user_photo = user_photo;
	}

	public String getPayTime() {
		return this.payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getPayTimeStamp() {
		return this.payTimeStamp;
	}

	public void setPayTimeStamp(String payTimeStamp) {
		this.payTimeStamp = payTimeStamp;
	}
}
