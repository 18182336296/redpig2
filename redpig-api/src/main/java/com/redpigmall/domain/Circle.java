package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
import com.redpigmall.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: Circle.java
 * </p>
 * 
 * <p>
 * Description: 圈子管理类，用户进入相应的圈子后可以发布帖子
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
 * @date 2014-11-18
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "circle")
public class Circle extends IdEntity {
	private String title;// 圈子名称
	@Column(columnDefinition = "LongText")
	private String photoInfo;// 圈子图标信息，使用json管理{"id":3234,"src":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg"}
	@Column(columnDefinition = "int default 0")
	private int status;// 圈子状态，平台审核该圈子的状态，0为未审核，5为审核通过，用户可以在该圈子发帖，-1为审核失败
	private long class_id;// 圈子所属分类id
	private String class_name;// 圈子所属分类名称
	private long user_id;// 圈子创建人id（圈子管理员id）,平台可以管理圈子，如圈子中存在违规帖子
	private String userName;// 圈子创建人姓名
	@Column(columnDefinition = "LongText")
	private String content;// 圈子说明
	@Column(columnDefinition = "int default 0")
	private int attention_count;// 总关注人数
	@Column(columnDefinition = "int default 0")
	private int invitation_count;// 总帖子数量
	@Column(columnDefinition = "LongText")
	private String refuseMsg;// 圈子创建审核拒绝理由
	@Column(columnDefinition = "int default 0")
	private int recommend;// 是否推荐，1为推荐

	public int getRecommend() {
		return this.recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getRefuseMsg() {
		return this.refuseMsg;
	}

	public void setRefuseMsg(String refuseMsg) {
		this.refuseMsg = refuseMsg;
	}

	public String getClass_name() {
		return this.class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPhotoInfo() {
		return this.photoInfo;
	}

	public void setPhotoInfo(String photoInfo) {
		this.photoInfo = photoInfo;
	}

	public int getStatus() {
		return this.status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getClass_id() {
		return this.class_id;
	}

	public void setClass_id(long class_id) {
		this.class_id = class_id;
	}

	public long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		content = HtmlFilterTools.delAllTag(content);
		this.content = content;
	}

	public int getAttention_count() {
		return this.attention_count;
	}

	public void setAttention_count(int attention_count) {
		this.attention_count = attention_count;
	}

	public int getInvitation_count() {
		return this.invitation_count;
	}

	public void setInvitation_count(int invitation_count) {
		this.invitation_count = invitation_count;
	}
}
