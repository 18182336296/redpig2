package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: SmsGoldLog.java
 * </p>
 * 
 * <p>
 * Description:短消息购买详情类
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
 * @date 2014-10-31
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "sms_gold_log")
public class SmsGoldLog extends IdEntity {
	private String title;// 标题内容
	private long seller_id;// 对应的商家id
	private String store_name;// 对应的商家店铺名称
	@Column(columnDefinition = "int default 0")
	private int gold;// 单价
	@Column(columnDefinition = "int default 0")
	private int count;// 购买数量
	@Column(columnDefinition = "int default 0")
	private int all_gold;// 总花费金币（=单价*数量）
	@Column(columnDefinition = "int default 0")
	private int log_status;// 购买日志状态，0为待付款，5为已付款，-5为已取消
	private String log_type;// "sms"为短信购买记录，“email”为邮件购买记录
	private String log_content;// 描述
	private Date pay_time;// 付款时间

	public SmsGoldLog() {
	}

	public SmsGoldLog(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getPay_time() {
		return this.pay_time;
	}

	public void setPay_time(Date pay_time) {
		this.pay_time = pay_time;
	}

	public int getLog_status() {
		return this.log_status;
	}

	public void setLog_status(int log_status) {
		this.log_status = log_status;
	}

	public int getGold() {
		return this.gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getAll_gold() {
		return this.all_gold;
	}

	public void setAll_gold(int all_gold) {
		this.all_gold = all_gold;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getLog_type() {
		return this.log_type;
	}

	public void setLog_type(String log_type) {
		this.log_type = log_type;
	}

	public String getLog_content() {
		return this.log_content;
	}

	public void setLog_content(String log_content) {
		this.log_content = log_content;
	}

	public long getSeller_id() {
		return this.seller_id;
	}

	public void setSeller_id(long seller_id) {
		this.seller_id = seller_id;
	}

	public String getStore_name() {
		return this.store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
}
