package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: UserConfig.java
 * </p>
 * 
 * <p>
 * Description:用户个性化信息管理类，该类系统暂未使用，预留，可以保存用户登录后个性化信息
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "userconfig")
public class UserConfig extends IdEntity {
	@OneToOne(fetch = FetchType.LAZY)
	private User user;

	public UserConfig() {
	}

	public UserConfig(Long id, Date addTime) {
		super(id, addTime);
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
