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
 * Title: Template.java
 * </p>
 * 
 * <p>
 * Description: 系统模板类，包括站内短信模板、邮件模板，手机短信模板,通过velocity模板合成数据
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "template")
public class Template extends IdEntity {
	private String info;// 模板描述
	private String type;// msg:站内短信模板，email:邮件模板，sms:手机短信模板
	private String title;// 模板标题，使用velocity标签管理
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;// 模板内容，使用${#id}模板标签
	private String mark;// 模板代码，根据模板代码获取对应的模板
	
	private boolean open;// 是否开启，开启后方可使用
	
	public Template() {
	}

	public Template(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getMark() {
		return this.mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public boolean getOpen() {
		return this.open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
