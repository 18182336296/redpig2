package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: GroupClass.java
 * </p>
 * 
 * <p>
 * Description: 团购楼层
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
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_floor")
public class GroupFloor extends IdEntity {
	private String gf_name;//楼层名称
	private String gf_sequence;//楼层序号
	private Long gf_group_class_id;//团购分类id
	@Column(columnDefinition = "int default 0")
	private int common;//是否通用

	public String getGf_name() {
		return this.gf_name;
	}

	public void setGf_name(String gf_name) {
		this.gf_name = gf_name;
	}

	public String getGf_sequence() {
		return this.gf_sequence;
	}

	public void setGf_sequence(String gf_sequence) {
		this.gf_sequence = gf_sequence;
	}

	public Long getGf_group_class_id() {
		return this.gf_group_class_id;
	}

	public void setGf_group_class_id(Long gf_group_class_id) {
		this.gf_group_class_id = gf_group_class_id;
	}

	public int getCommon() {
		return this.common;
	}

	public void setCommon(int common) {
		this.common = common;
	}
}
