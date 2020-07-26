package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * 
 * <p>
 * Title: ChattingUser.java
 * </p>
 * 
 * <p>
 * Description: 聊天用户
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chatting_user")
public class ChattingUser extends IdEntity {
	private String chatting_name;
	private String chatting_photo;
	private Long chatting_user_id;
	private String chatting_user_name;
	private String chatting_user_form;
	@Column(columnDefinition = "int default 0")
	private int chatting_type;
	private Date chatting_last_login;
	@Column(columnDefinition = "int default 0")
	private int chatting_status;
	@Column(columnDefinition = "int default 0")
	private int chatting_count;
	@Column(columnDefinition = "int default 0")
	private int chatting_user_default;

	public int getChatting_count() {
		return this.chatting_count;
	}

	public String getChatting_user_form() {
		return this.chatting_user_form;
	}

	public void setChatting_user_form(String chatting_user_form) {
		this.chatting_user_form = chatting_user_form;
	}

	public int getChatting_user_default() {
		return this.chatting_user_default;
	}

	public void setChatting_user_default(int chatting_user_default) {
		this.chatting_user_default = chatting_user_default;
	}

	public void setChatting_count(int chatting_count) {
		this.chatting_count = chatting_count;
	}

	public ChattingUser() {
	}

	public ChattingUser(Long id) {
		super.setId(id);
	}

	public ChattingUser(Long id, String chatting_user_name) {
		super.setId(id);
		this.chatting_user_name = chatting_user_name;
	}

	public int getChatting_type() {
		return this.chatting_type;
	}

	public void setChatting_type(int chatting_type) {
		this.chatting_type = chatting_type;
	}

	public int getChatting_status() {
		return this.chatting_status;
	}

	public void setChatting_status(int chatting_status) {
		this.chatting_status = chatting_status;
	}

	public Date getChatting_last_login() {
		return this.chatting_last_login;
	}

	public void setChatting_last_login(Date chatting_last_login) {
		this.chatting_last_login = chatting_last_login;
	}

	public String getChatting_name() {
		return this.chatting_name;
	}

	public void setChatting_name(String chatting_name) {
		this.chatting_name = chatting_name;
	}

	public String getChatting_photo() {
		return this.chatting_photo;
	}

	public void setChatting_photo(String chatting_photo) {
		this.chatting_photo = chatting_photo;
	}

	public Long getChatting_user_id() {
		return this.chatting_user_id;
	}

	public void setChatting_user_id(Long chatting_user_id) {
		this.chatting_user_id = chatting_user_id;
	}

	public String getChatting_user_name() {
		return this.chatting_user_name;
	}

	public void setChatting_user_name(String chatting_user_name) {
		this.chatting_user_name = chatting_user_name;
	}
}
