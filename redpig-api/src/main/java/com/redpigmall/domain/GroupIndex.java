package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: GroupIndex.java
 * </p>
 * 
 * <p>
 * Description:团购索引
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_index_design")
public class GroupIndex extends IdEntity {
	private String gid_name;//团购id
	private int gid_default;//
	@Column(columnDefinition = "LongText")
	private String gid_nav_gc_list;
	@Column(columnDefinition = "LongText")
	private String gid_nav_word_list;
	private Long gid_nav_advp_id;
	@Column(columnDefinition = "LongText")
	private String gid_nav_right_img_list;
	@Column(columnDefinition = "LongText")
	private String gid_nav_hot_class;
	private int gid_sequence;

	public String getGid_name() {
		return this.gid_name;
	}

	public void setGid_name(String gid_name) {
		this.gid_name = gid_name;
	}

	public int getGid_default() {
		return this.gid_default;
	}

	public void setGid_default(int gid_default) {
		this.gid_default = gid_default;
	}

	public String getGid_nav_gc_list() {
		return this.gid_nav_gc_list;
	}

	public void setGid_nav_gc_list(String gid_nav_gc_list) {
		this.gid_nav_gc_list = gid_nav_gc_list;
	}

	public String getGid_nav_word_list() {
		return this.gid_nav_word_list;
	}

	public void setGid_nav_word_list(String gid_nav_word_list) {
		this.gid_nav_word_list = gid_nav_word_list;
	}

	public Long getGid_nav_advp_id() {
		return this.gid_nav_advp_id;
	}

	public void setGid_nav_advp_id(Long gid_nav_advp_id) {
		this.gid_nav_advp_id = gid_nav_advp_id;
	}

	public String getGid_nav_right_img_list() {
		return this.gid_nav_right_img_list;
	}

	public void setGid_nav_right_img_list(String gid_nav_right_img_list) {
		this.gid_nav_right_img_list = gid_nav_right_img_list;
	}

	public String getGid_nav_hot_class() {
		return this.gid_nav_hot_class;
	}

	public void setGid_nav_hot_class(String gid_nav_hot_class) {
		this.gid_nav_hot_class = gid_nav_hot_class;
	}

	public int getGid_sequence() {
		return this.gid_sequence;
	}

	public void setGid_sequence(int gid_sequence) {
		this.gid_sequence = gid_sequence;
	}
}
