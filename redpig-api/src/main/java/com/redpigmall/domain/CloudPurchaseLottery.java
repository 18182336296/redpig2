package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cloud_purchase_lottery")
public class CloudPurchaseLottery extends IdEntity {
	private Long goods_id;
	private String period;
	@Column(columnDefinition = "int default 0")
	private int status;
	@Column(columnDefinition = "int default 0")
	private int purchased_times;
	@Column(columnDefinition = "int default 0")
	private int purchased_left_times;
	private Date soldout_date;
	private Date announced_date;
	private BigDecimal user_time_num_count;
	private BigDecimal lottery_num;
	private String expect;
	private String change_code;
	@Column(columnDefinition = "int default -1")
	private Long lucky_code;
	@ManyToOne
	private CloudPurchaseGoods cloudPurchaseGoods;
	private String lucky_userid;
	private String lucky_username;
	private String lucky_truename;
	private String lucky_userbuytime;
	private String lucky_userphoto;
	private String lucky_usertimes;
	private String lucky_address;
	private String lucky_phone;
	@Column(columnDefinition = "int default -1")
	private int delivery_status;

	public String getLucky_truename() {
		return this.lucky_truename;
	}

	public void setLucky_truename(String lucky_truename) {
		this.lucky_truename = lucky_truename;
	}

	public String getLucky_userbuytime() {
		return this.lucky_userbuytime;
	}

	public void setLucky_userbuytime(String lucky_userbuytime) {
		this.lucky_userbuytime = lucky_userbuytime;
	}

	public String getChange_code() {
		return this.change_code;
	}

	public void setChange_code(String change_code) {
		this.change_code = change_code;
	}

	public String getLucky_phone() {
		return this.lucky_phone;
	}

	public void setLucky_phone(String lucky_phone) {
		this.lucky_phone = lucky_phone;
	}

	public String getLucky_address() {
		return this.lucky_address;
	}

	public void setLucky_address(String lucky_address) {
		this.lucky_address = lucky_address;
	}

	public String getExpect() {
		return this.expect;
	}

	public void setExpect(String expect) {
		this.expect = expect;
	}

	public int getDelivery_status() {
		return this.delivery_status;
	}

	public void setDelivery_status(int delivery_status) {
		this.delivery_status = delivery_status;
	}

	public CloudPurchaseLottery() {
	}

	public CloudPurchaseLottery(Long id) {
		super.setId(id);
	}

	public CloudPurchaseLottery(Long id, Date addTime) {
		super(id, addTime);
	}

	public Long getGoods_id() {
		return this.goods_id;
	}

	public void setGoods_id(Long goods_id) {
		this.goods_id = goods_id;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getPurchased_times() {
		return this.purchased_times;
	}

	public void setPurchased_times(int purchased_times) {
		this.purchased_times = purchased_times;
	}

	public int getPurchased_left_times() {
		return this.purchased_left_times;
	}

	public void setPurchased_left_times(int purchased_left_times) {
		this.purchased_left_times = purchased_left_times;
	}

	public Date getAnnounced_date() {
		return this.announced_date;
	}

	public void setAnnounced_date(Date announced_date) {
		this.announced_date = announced_date;
	}

	public BigDecimal getUser_time_num_count() {
		return this.user_time_num_count;
	}

	public void setUser_time_num_count(BigDecimal user_time_num_count) {
		this.user_time_num_count = user_time_num_count;
	}

	public BigDecimal getLottery_num() {
		return this.lottery_num;
	}

	public void setLottery_num(BigDecimal lottery_num) {
		this.lottery_num = lottery_num;
	}

	public Long getLucky_code() {
		return this.lucky_code;
	}

	public void setLucky_code(Long lucky_code) {
		this.lucky_code = lucky_code;
	}

	public CloudPurchaseGoods getCloudPurchaseGoods() {
		return this.cloudPurchaseGoods;
	}

	public void setCloudPurchaseGoods(CloudPurchaseGoods cloudPurchaseGoods) {
		this.cloudPurchaseGoods = cloudPurchaseGoods;
	}

	public String getLucky_userid() {
		return this.lucky_userid;
	}

	public void setLucky_userid(String lucky_userid) {
		this.lucky_userid = lucky_userid;
	}

	public String getLucky_username() {
		return this.lucky_username;
	}

	public void setLucky_username(String lucky_username) {
		this.lucky_username = lucky_username;
	}

	public String getLucky_userphoto() {
		return this.lucky_userphoto;
	}

	public void setLucky_userphoto(String lucky_userphoto) {
		this.lucky_userphoto = lucky_userphoto;
	}

	public String getLucky_usertimes() {
		return this.lucky_usertimes;
	}

	public void setLucky_usertimes(String lucky_usertimes) {
		this.lucky_usertimes = lucky_usertimes;
	}

	public Date getSoldout_date() {
		return this.soldout_date;
	}

	public void setSoldout_date(Date soldout_date) {
		this.soldout_date = soldout_date;
	}
}
