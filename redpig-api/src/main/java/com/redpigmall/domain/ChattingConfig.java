package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ChattingConfig.java
 * </p>
 * 
 * <p>
 * Description: 每个店铺或者平台的聊天组件信息设置类，可以设置的内容包括客服名称，自动回复内容、字体、字体大小、字体颜色
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
 * @date 2014年5月26日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chatting_config")
public class ChattingConfig extends IdEntity {
	private Long user_id;//用户id
	private Long service_id;//聊天会话id
	private String kf_name;// 客服自定义名称，
	private Long last_service_id;//最后聊天会话id
	@Column(columnDefinition = "LongText")
	private String quick_reply_content;// 客服快速回复信息
	@Column(columnDefinition = "int default 0")
	private int quick_reply_open;// 自动回复是否开启，0为未开启，1为开启
	private String font;// 字体，商家发布保存信息是保存该信息
	private String font_size;// 字体大小
	private String font_colour;// 字体颜色

	public Long getLast_service_id() {
		return this.last_service_id;
	}

	public void setLast_service_id(Long last_service_id) {
		this.last_service_id = last_service_id;
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public Long getService_id() {
		return this.service_id;
	}

	public void setService_id(Long service_id) {
		this.service_id = service_id;
	}

	public String getKf_name() {
		return this.kf_name;
	}

	public void setKf_name(String kf_name) {
		this.kf_name = kf_name;
	}

	public String getQuick_reply_content() {
		return this.quick_reply_content;
	}

	public void setQuick_reply_content(String quick_reply_content) {
		this.quick_reply_content = quick_reply_content;
	}

	public int getQuick_reply_open() {
		return this.quick_reply_open;
	}

	public void setQuick_reply_open(int quick_reply_open) {
		this.quick_reply_open = quick_reply_open;
	}

	public String getFont() {
		return this.font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getFont_size() {
		return this.font_size;
	}

	public void setFont_size(String font_size) {
		this.font_size = font_size;
	}

	public String getFont_colour() {
		return this.font_colour;
	}

	public void setFont_colour(String font_colour) {
		this.font_colour = font_colour;
	}
}
