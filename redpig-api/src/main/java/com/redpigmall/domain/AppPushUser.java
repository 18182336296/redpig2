package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: AppPushUser.java
 * </p>
 * 
 * <p>
 * Description: 推送，绑定用户信息
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
 * @date 2015-2-7
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "app_push_user")
public class AppPushUser extends IdEntity {
	private String app_id;// 设备id
	private String app_type;// 设备类别，Android，ios
	private String user_id;// 用户id，若果没登陆即为空
	private String app_userRole;// App用户角色，预留，区分买家app和商家app,"buyer"为买家，seller为商家
	@Column(columnDefinition = "int default 0")
	private int unread_count;

	public String getApp_id() {
		return this.app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getApp_type() {
		return this.app_type;
	}

	public void setApp_type(String app_type) {
		this.app_type = app_type;
	}

	public String getApp_userRole() {
		return this.app_userRole;
	}

	public void setApp_userRole(String app_userRole) {
		this.app_userRole = app_userRole;
	}

	public String getUser_id() {
		return this.user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getUnread_count() {
		return this.unread_count;
	}

	public void setUnread_count(int unread_count) {
		this.unread_count = unread_count;
	}
}
