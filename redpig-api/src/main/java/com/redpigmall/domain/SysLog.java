package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: SysLog.java
 * </p>
 * 
 * <p>
 * Description: 系统日志类，用来记录所有用户操作系统日志,系统默认未开启日志记录
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
 * @date 2014-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "syslog")
public class SysLog extends IdEntity {
	private String title;
	private int type;// 0为普通日志，1为异常日志
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;
	private String user_name;// 用户名称
	private String ip;
	private Long user_id;// 对应的用户
	private String ip_city;// ip所在城市

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getIp_city() {
		return this.ip_city;
	}

	public void setIp_city(String ip_city) {
		this.ip_city = ip_city;
	}

	public SysLog() {
	}

	public SysLog(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
