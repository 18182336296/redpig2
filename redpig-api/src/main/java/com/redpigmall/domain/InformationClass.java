package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: InformationClass.java
 * </p>
 * 
 * <p>
 * Description: 资讯信息类型
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
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "information_class")
public class InformationClass extends IdEntity {
	@Column(columnDefinition = "int default 0")
	private int ic_sequence;// 序号，正常按时间排序
	private Long ic_pid;// 上级分类Id
	private String ic_name;// 标题

	public int getIc_sequence() {
		return this.ic_sequence;
	}

	public void setIc_sequence(int ic_sequence) {
		this.ic_sequence = ic_sequence;
	}

	public Long getIc_pid() {
		return this.ic_pid;
	}

	public void setIc_pid(Long ic_pid) {
		this.ic_pid = ic_pid;
	}

	public String getIc_name() {
		return this.ic_name;
	}

	public void setIc_name(String ic_name) {
		this.ic_name = ic_name;
	}
}
