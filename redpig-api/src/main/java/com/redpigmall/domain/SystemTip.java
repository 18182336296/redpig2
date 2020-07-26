package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: SystemTip.java
 * </p>
 * 
 * <p>
 * Description:系统提醒管理类，用来向系统管理员发送相关提醒信息，如手机充值时平台余额不足需要向管理员发送余额不足信息，
 * 管理员登陆后台即可看到该提醒，并进行优先处理
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
 * @date 2014-5-21
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "sys_tip")
public class SystemTip extends IdEntity {
	private String st_title;// 提醒标题
	private int st_level;// 提醒等级，等级越高在管理后台显示越靠前，优先提醒管理员
	private int st_status;// 提醒状态，0为为未处理，1为已经处理
	@Column(columnDefinition = "LongText")
	private String st_content;// 提醒内容

	public SystemTip() {
	}

	public SystemTip(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getSt_status() {
		return this.st_status;
	}

	public void setSt_status(int st_status) {
		this.st_status = st_status;
	}

	public String getSt_title() {
		return this.st_title;
	}

	public void setSt_title(String st_title) {
		this.st_title = st_title;
	}

	public int getSt_level() {
		return this.st_level;
	}

	public void setSt_level(int st_level) {
		this.st_level = st_level;
	}

	public String getSt_content() {
		return this.st_content;
	}

	public void setSt_content(String st_content) {
		this.st_content = st_content;
	}
}
