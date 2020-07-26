package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ConsultSatis.java
 * </p>
 * 
 * <p>
 * Description: 商品评论满意度统计
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
 * @date 2014-10-8
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "consult_statis")
public class ConsultSatis extends IdEntity {
	private Long cs_consult_id;// 对应的评论id
	private Long cs_user_id;// 评论人id
	@Column(columnDefinition = "int default 0")
	private int cs_type;// 满意度类型0为满意，-1为不满意
	private String cs_ip;// 评论满意度的ip

	public ConsultSatis(Long id, Date addTime) {
		super(id, addTime);
	}

	public ConsultSatis() {
	}

	public Long getCs_consult_id() {
		return this.cs_consult_id;
	}

	public void setCs_consult_id(Long cs_consult_id) {
		this.cs_consult_id = cs_consult_id;
	}

	public Long getCs_user_id() {
		return this.cs_user_id;
	}

	public void setCs_user_id(Long cs_user_id) {
		this.cs_user_id = cs_user_id;
	}

	public int getCs_type() {
		return this.cs_type;
	}

	public void setCs_type(int cs_type) {
		this.cs_type = cs_type;
	}

	public String getCs_ip() {
		return this.cs_ip;
	}

	public void setCs_ip(String cs_ip) {
		this.cs_ip = cs_ip;
	}
}
