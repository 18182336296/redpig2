package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Article.java
 * </p>
 * 
 * <p>
 * Description:系统文章管理类
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
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "article")
public class Article extends IdEntity {
	private String title;// 文章标题
	@Column(columnDefinition = "varchar(255) default 'user' ")
	private String type;// 文章类型，默认为user，商家公告为store，store类型只能商家才能查看
	@ManyToOne(fetch = FetchType.LAZY)
	private ArticleClass articleClass;// 文章分类
	private String url;// 文章链接，如果存在该值则直接跳转到url，不显示文章内容
	private int sequence;// 文章序号，根据序号正序排序
	private boolean display;// 是否显示文章
	private String mark;// 文章标识，存在唯一性，通过标识可以查询对应的文章
	@Column(columnDefinition = "LongText")
	private String content;// 文章内容

	public Article() {
	}

	public Article(Long id, Date addTime) {
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

	public ArticleClass getArticleClass() {
		return this.articleClass;
	}

	public void setArticleClass(ArticleClass articleClass) {
		this.articleClass = articleClass;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Boolean getDisplay() {
		return this.display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
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
}
