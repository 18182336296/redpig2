package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: IntegralLog.java
 * </p>
 * 
 * <p>
 * Description:用户积分操作日志记录
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "integrallog")
public class IntegralLog extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User integral_user;// 积分用户
	@ManyToOne(fetch = FetchType.LAZY)
	private User operate_user;// 操作用户
	private int integral;// 操作积分数
	private String type;// 操作类型，包括reg：注册赠送，system：管理员操作,login:用户登录,order:订单获得,integral_order:积分兑换
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;// 操作说明

	public IntegralLog() {
	}

	public IntegralLog(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getIntegral_user() {
		return this.integral_user;
	}

	public void setIntegral_user(User integral_user) {
		this.integral_user = integral_user;
	}

	public User getOperate_user() {
		return this.operate_user;
	}

	public void setOperate_user(User operate_user) {
		this.operate_user = operate_user;
	}

	public int getIntegral() {
		return this.integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
