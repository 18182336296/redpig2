package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: Document.java
 * </p>
 * 
 * <p>
 * Description:
 * 系统文章类，管理系统文章，包括注册协议、商铺协议等等,文章通过mark进行访问，使用urlrewrite静态化处理，将mark作为参数传递
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "document")
public class Document extends IdEntity {
	private String mark;// 文章标识
	private String title;// 文章标题
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;// 文章内容

	public Document() {
	}

	public Document(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getMark() {
		return this.mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
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
