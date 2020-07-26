package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: AppPushLog.java
 * </p>
 * 
 * <p>
 * Description: 记录推送内容
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
 * @author lixiaoyang
 * 
 * @date 2015-2-7
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "app_push_log")
public class AppPushLog extends IdEntity {
	private String title;// 通知标题
	private String description;// 通知内容
	@Column(columnDefinition = "LongText")
	private String custom_content;// 自定内容，不显示，json解析，决定安卓页面跳转
	private int device;// 设备类别 0 全部 1android 2 ios
	@Column(columnDefinition = "int default 0")
	private int app_type;
	@Column(columnDefinition = "int default 0")
	private int send_type;// 0立即发送 1 定时发送
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendtime;// 发送时间
	@Column(columnDefinition = "int default 0")
	private int status;// 发送状态,0为未发送，1为已发送
	@Column(columnDefinition = "int default 0")
	private int reciever_type;
	@Column(columnDefinition = "LongText")
	private String reciever_list;

	public int getApp_type() {
		return this.app_type;
	}

	public void setApp_type(int app_type) {
		this.app_type = app_type;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCustom_content() {
		return this.custom_content;
	}

	public void setCustom_content(String custom_content) {
		this.custom_content = custom_content;
	}

	public int getDevice() {
		return this.device;
	}

	public void setDevice(int device) {
		this.device = device;
	}

	public int getSend_type() {
		return this.send_type;
	}

	public void setSend_type(int send_type) {
		this.send_type = send_type;
	}

	public Date getSendtime() {
		return this.sendtime;
	}

	public void setSendtime(Date sendtime) {
		this.sendtime = sendtime;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getReciever_type() {
		return this.reciever_type;
	}

	public void setReciever_type(int reciever_type) {
		this.reciever_type = reciever_type;
	}

	public String getReciever_list() {
		return this.reciever_list;
	}

	public void setReciever_list(String reciever_list) {
		this.reciever_list = reciever_list;
	}
}
