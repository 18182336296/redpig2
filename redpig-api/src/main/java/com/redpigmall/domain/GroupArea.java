package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: GroupArea.java
 * </p>
 * 
 * <p>
 * Description: 团购区域,团购商品申请可以选择允许团购的区域
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_area")
public class GroupArea extends IdEntity {
	private String ga_name;// 区域名称
	private int ga_sequence;// 区域序号，升序排序
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupArea parent;// 父级区域
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@OrderBy(value = "ga_sequence asc")
	private List<GroupArea> childs = new ArrayList<GroupArea>();// 子集区域
	private int ga_level;// 区域层级，用在区域显示梯次

	public GroupArea() {
	}

	public GroupArea(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getGa_level() {
		return this.ga_level;
	}

	public void setGa_level(int ga_level) {
		this.ga_level = ga_level;
	}

	public String getGa_name() {
		return this.ga_name;
	}

	public void setGa_name(String ga_name) {
		this.ga_name = ga_name;
	}

	public int getGa_sequence() {
		return this.ga_sequence;
	}

	public void setGa_sequence(int ga_sequence) {
		this.ga_sequence = ga_sequence;
	}

	public GroupArea getParent() {
		return this.parent;
	}

	public void setParent(GroupArea parent) {
		this.parent = parent;
	}

	public List<GroupArea> getChilds() {
		return this.childs;
	}

	public void setChilds(List<GroupArea> childs) {
		this.childs = childs;
	}
}
