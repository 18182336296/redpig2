package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
import com.redpigmall.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: Message.java
 * </p>
 * 
 * <p>
 * Description:站内短信类，用户之间可以根据用户名发送站内短信息
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "message")
public class Message extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User fromUser;// 发送用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User toUser;// 接收用户
	private int status;// 短信状态,0是未读，1为已读
	@Column(columnDefinition = "int default 0")
	private int reply_status;// 短信回复状态，0为没有回复，1为有新回复
	private String title;// 短信标题
	@Column(columnDefinition = "LongText")
	private String content;// 短信内容
	@ManyToOne(fetch = FetchType.LAZY)
	private Message parent;
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	List<Message> replys = new ArrayList<Message>();// 短信回复，和parent进行映射
	private int type;// 短信类型，0为系统短信，1为用户之间的短信
	@Column(columnDefinition = "int default 0")
	private int msg_cat;// 0为第一次发送的短信，1为短信回复
	private Long toStore_id;//店铺id

	public Message() {
	}

	public Long getToStore_id() {
		return this.toStore_id;
	}

	public void setToStore_id(Long toStore_id) {
		this.toStore_id = toStore_id;
	}

	public Message(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getMsg_cat() {
		return this.msg_cat;
	}

	public void setMsg_cat(int msg_cat) {
		this.msg_cat = msg_cat;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public User getFromUser() {
		return this.fromUser;
	}

	public void setFromUser(User fromUser) {
		this.fromUser = fromUser;
	}

	public User getToUser() {
		return this.toUser;
	}

	public void setToUser(User toUser) {
		this.toUser = toUser;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		content = HtmlFilterTools.delAllTag(content);
		this.content = content;
	}

	public Message getParent() {
		return this.parent;
	}

	public void setParent(Message parent) {
		this.parent = parent;
	}

	public List<Message> getReplys() {
		return this.replys;
	}

	public void setReplys(List<Message> replys) {
		this.replys = replys;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getReply_status() {
		return this.reply_status;
	}

	public void setReply_status(int reply_status) {
		this.reply_status = reply_status;
	}
}
