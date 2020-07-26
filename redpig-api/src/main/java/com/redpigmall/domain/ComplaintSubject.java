package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ComplaintSubject.java
 * </p>
 * 
 * <p>
 * Description: 投诉主题管理类，用来管理投诉主题信息，用户投诉都需要选择一个投诉主题，便于平台归类处理
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "complaint_subject")
public class ComplaintSubject extends IdEntity {
	private String type;// 卖家seller，买家buyer
	private String title;// 主题
	@Column(columnDefinition = "LongText")
	private String content;// 主题描述

	public ComplaintSubject(Long id, Date addTime) {
		super(id, addTime);
	}

	public ComplaintSubject() {
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
}
