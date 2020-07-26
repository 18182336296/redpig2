package com.redpigmall.domain;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: UserLevel.java
 * </p>
 * 
 * <p>
 * Description:用户等级
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_level")
public class UserLevel extends IdEntity {
	private String level_name;//等级名称
	private int level_up;//
	private int level_down;
	private String level_icon;
	private int level_icon_type;
	private String sys_seq;

	public String getSys_seq() {
		return this.sys_seq;
	}

	public void setSys_seq(String sys_seq) {
		this.sys_seq = sys_seq;
	}

	public String getLevel_name() {
		return this.level_name;
	}

	public void setLevel_name(String level_name) {
		this.level_name = level_name;
	}

	public int getLevel_up() {
		return this.level_up;
	}

	public void setLevel_up(int level_up) {
		this.level_up = level_up;
	}

	public int getLevel_down() {
		return this.level_down;
	}

	public void setLevel_down(int level_down) {
		this.level_down = level_down;
	}

	public String getLevel_icon() {
		return this.level_icon;
	}

	public void setLevel_icon(String level_icon) {
		this.level_icon = level_icon;
	}

	public int getLevel_icon_type() {
		return this.level_icon_type;
	}

	public void setLevel_icon_type(int level_icon_type) {
		this.level_icon_type = level_icon_type;
	}
}
