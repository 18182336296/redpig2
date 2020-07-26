package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: VMessage.java
 * </p>
 * 
 * <p>
 * Description: 微信信息管理类，系统可以接受用户发送的微信信息，并进行回复
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
 * @date 2014-12-20
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "vmessage")
public class VMessage extends IdEntity {
	private String FromUserName;// 消息发送方姓名
	@Column(columnDefinition = "LongText")
	private String content;// 微信信息内容
	@Column(columnDefinition = "LongText")
	private String reply;// 回复信息内容
	private String MsgType;// 微信消息类型
	@Column(columnDefinition = "int default 0")
	private int status;// 消息状态，0为未回复，1为已回复

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsgType() {
		return this.MsgType;
	}

	public void setMsgType(String msgType) {
		this.MsgType = msgType;
	}

	public String getFromUserName() {
		return this.FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.FromUserName = fromUserName;
	}

	public String getReply() {
		return this.reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
