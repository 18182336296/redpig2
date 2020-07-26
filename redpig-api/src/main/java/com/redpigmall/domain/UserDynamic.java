package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
import com.redpigmall.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: UserDynamic.java
 * </p>
 * 
 * <p>
 * Description:用户动态
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_dynamic")
public class UserDynamic extends IdEntity {
	private Long user_id;//发布用户的id
	private String user_name;//发布用户的name
	@Column(columnDefinition = "LongText")
	private String dynamic_content;//动态的内容
	@Column(columnDefinition = "LongText")
	private String img_info;//动态中的图片信息，json管理

	public Long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getDynamic_content() {
		return this.dynamic_content;
	}

	public void setDynamic_content(String dynamic_content) {
		dynamic_content = HtmlFilterTools.delAllTag(dynamic_content);
		this.dynamic_content = dynamic_content;
	}

	public String getImg_info() {
		return this.img_info;
	}

	public void setImg_info(String img_info) {
		this.img_info = img_info;
	}

	public String getUser_name() {
		return this.user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
}
