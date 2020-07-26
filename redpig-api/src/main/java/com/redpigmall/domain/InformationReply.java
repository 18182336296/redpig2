package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
import com.redpigmall.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: InformationReply.java
 * </p>
 * 
 * <p>
 * Description: 资讯回复实体类
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
 * @date 2015-2-6
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "Information_reply")
public class InformationReply extends IdEntity {
	private Long info_id;// 回复的资讯id
	@Column(columnDefinition = "LongText")
	private String content;// 回复内容
	private String userName;// 回复用户名
	private Long userId;// 回复用户的id
	private String userPhoto;// 用户用户的照片，保存照片路径

	public Long getInfo_id() {
		return this.info_id;
	}

	public void setInfo_id(Long info_id) {
		this.info_id = info_id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		content = HtmlFilterTools.delAllTag(content);
		this.content = content;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserPhoto() {
		return this.userPhoto;
	}

	public void setUserPhoto(String userPhoto) {
		this.userPhoto = userPhoto;
	}
}
