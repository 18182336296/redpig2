package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ArticleClass.java
 * </p>
 * 
 * <p>
 * Description: 系统文章分类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "articleclass")
public class ArticleClass extends IdEntity {
	private String className;// 名称
	private int sequence;// 排序
	private int level;// 层级
	private String mark;// 分类标识，用来标注系统分类
	private boolean sysClass;// 是否系统分类
	@ManyToOne(fetch = FetchType.LAZY)
	private ArticleClass parent;//文章类型父类
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<ArticleClass> childs = new ArrayList<ArticleClass>();//文章类型子类
	
	@OneToMany(mappedBy = "articleClass", cascade = CascadeType.REMOVE)
	@OrderBy(value = "sequence asc")
	private List<Article> articles = new ArrayList<Article>();//所属文章
	
	@Column(columnDefinition = "int default 0")
	private int one_type;//文章位置: 1:商城公告 2:商家公告
	
	private String two_type;//文章位置:right bottom chat

	public int getOne_type() {
		return this.one_type;
	}

	public void setOne_type(int one_type) {
		this.one_type = one_type;
	}

	public String getTwo_type() {
		return this.two_type;
	}

	public void setTwo_type(String two_type) {
		this.two_type = two_type;
	}

	public ArticleClass(Long id, Date addTime) {
		super(id, addTime);
	}

	public ArticleClass() {
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Boolean getSysClass() {
		return this.sysClass;
	}
	
	public void setSysClass(boolean sysClass) {
		this.sysClass = sysClass;
	}

	public ArticleClass getParent() {
		return this.parent;
	}

	public void setParent(ArticleClass parent) {
		this.parent = parent;
	}

	public List<ArticleClass> getChilds() {
		return this.childs;
	}

	public void setChilds(List<ArticleClass> childs) {
		this.childs = childs;
	}

	public String getMark() {
		return this.mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public List<Article> getArticles() {
		return this.articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}
}
