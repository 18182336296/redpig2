package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ChattingLog.java
 * </p>
 * 
 * <p>
 * Description: 聊天记录管理类,用户向平台或者商家发送消息，
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
 * @date 2014-5-22
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chattinglog")
public class ChattingLog extends IdEntity {
	private Long service_id;
	private String service_name;//客服对应管理员
	private Long user_id;// 该记录用户发言人id
	private String user_name;
	private String send_from;
	@Column(columnDefinition = "int default 0")
	private int user_read;// 用户是否已读标记，0为未读，1为已读，如该条信息由商家客服发送，该字段表示用户是否已读商家信息，
	@Column(columnDefinition = "int default 0")
	private int service_read;// 客服是否已读标记，0为未读，1为已读，如该条信息由商家客服发送，该字段表示用户是否已读商家信息，
	private String font;// 保存聊天记录时字体信息
	private String font_size;// 保存聊天记录时字体大小信息
	private String font_colour;// 保存聊天记录时字体颜色信息
	@Column(columnDefinition = "LongText")
	private String content;// 聊天内容
	
	private Chatting chatting;//系统客服与用户会话管理类
	
	public String getSend_from() {
		return this.send_from;
	}

	public void setSend_from(String send_from) {
		this.send_from = send_from;
	}

	public Long getService_id() {
		return this.service_id;
	}

	public void setService_id(Long service_id) {
		this.service_id = service_id;
	}

	public String getService_name() {
		return this.service_name;
	}

	public void setService_name(String service_name) {
		this.service_name = service_name;
	}

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public int getUser_read() {
		return this.user_read;
	}

	public void setUser_read(int user_read) {
		this.user_read = user_read;
	}

	public int getService_read() {
		return this.service_read;
	}

	public void setService_read(int service_read) {
		this.service_read = service_read;
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

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Chatting getChatting() {
		return chatting;
	}

	public void setChatting(Chatting chatting) {
		this.chatting = chatting;
	}

	
}
