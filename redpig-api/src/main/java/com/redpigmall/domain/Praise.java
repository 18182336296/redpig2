package com.redpigmall.domain;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: Praise.java
 * </p>
 * 
 * <p>
 * Description: 点赞
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
 * @date 2014年5月5日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "praise")
public class Praise extends IdEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	private ReplyContent praise_info;//回复内容信息
	private String name;//点赞名称
	private String status;//状态

	public ReplyContent getPraise_info() {
		return this.praise_info;
	}

	public void setPraise_info(ReplyContent praise_info) {
		this.praise_info = praise_info;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
