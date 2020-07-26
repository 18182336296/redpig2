package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: FreeClass.java
 * </p>
 * 
 * <p>
 * Description:0元试用商品分类
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "freeclass")
public class FreeClass extends IdEntity {
	private String className;// 分类名称
	@Column(columnDefinition = "int default 0")
	private int sequence;// 默认为0顺序

	public FreeClass(Long id, Date addTime) {
		super(id, addTime);
	}

	public FreeClass() {
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
