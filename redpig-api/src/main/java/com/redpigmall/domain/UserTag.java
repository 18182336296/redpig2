package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: UserTag.java
 * </p>
 * 
 * <p>
 * Description:用户标记
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_tag")
public class UserTag extends IdEntity {
	String tagname;

	public UserTag(Long id, Date addTime) {
		super(id, addTime);
	}

	public UserTag(Long id) {
		super.setId(id);
	}

	public UserTag() {
	}

	public String getTagname() {
		return this.tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}
}
