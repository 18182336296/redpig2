package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: Subject.java
 * </p>
 * 
 * <p>
 * Description: 商城主题类
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
 * @date 2014-11-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "subject")
public class Subject extends IdEntity {
	private String title;// 主题名称
	@Column(columnDefinition = "int default 0")
	private int sequence;// 序号
	@OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private Accessory banner;// 主题横幅
	@Column(columnDefinition = "int default 1")
	private boolean display;// 是否显示
	@Column(columnDefinition = "LongText")
	private String subject_detail;// 专题详情，使用json管理[{"type":"goods","goods_ids":",16,14,11,4,5"},
									// {"type":"img","img_url":"http://localhost/upload/subject/85c8f939-b099-4821-9f9a-e88dfe3f8ae0.jpg","id":938,"areaInfo":"226_65_366_168=http://www.baidu.com-"}]


	public Subject() {
	}

	public Subject(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public boolean getDisplay() {
		return this.display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Accessory getBanner() {
		return this.banner;
	}

	public void setBanner(Accessory banner) {
		this.banner = banner;
	}

	public String getSubject_detail() {
		return this.subject_detail;
	}

	public void setSubject_detail(String subject_detail) {
		this.subject_detail = subject_detail;
	}
}
