package com.redpigmall.domain;

import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: SnsAttention.java
 * </p>
 * 
 * <p>
 * Description:sns功能中的粉丝与关注
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "sns_attention")
public class SnsAttention extends IdEntity {
	@ManyToOne
	private User fromUser;// 关注者
	@ManyToOne
	private User toUser;// 被关注者

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
}
