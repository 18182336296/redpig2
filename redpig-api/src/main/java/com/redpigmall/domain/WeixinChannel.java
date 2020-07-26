package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: WeixinChannel.java
 * </p>
 * 
 * <p>
 * Description: 微信频道
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "weixin_channel")
public class WeixinChannel extends IdEntity {
	private String ch_name;//频道名称
	@Column(columnDefinition = "LongText")
	private String floor_info;//楼层信息
	private int ch_sequence;//序号
	private Long adv_pos_id;//广告

	public Long getAdv_pos_id() {
		return this.adv_pos_id;
	}

	public void setAdv_pos_id(Long adv_pos_id) {
		this.adv_pos_id = adv_pos_id;
	}

	public String getFloor_info() {
		return this.floor_info;
	}

	public void setFloor_info(String floor_info) {
		this.floor_info = floor_info;
	}

	public String getCh_name() {
		return this.ch_name;
	}

	public void setCh_name(String ch_name) {
		this.ch_name = ch_name;
	}

	public int getCh_sequence() {
		return this.ch_sequence;
	}

	public void setCh_sequence(int ch_sequence) {
		this.ch_sequence = ch_sequence;
	}
}
