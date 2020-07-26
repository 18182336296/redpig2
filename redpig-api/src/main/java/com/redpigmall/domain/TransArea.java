package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: TransArea.java
 * </p>
 * 
 * <p>
 * Description:运费区域管理类，用来管理配送模板的配送区域，如：华东->安徽->安庆
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "trans_area")
public class TransArea extends IdEntity {
	private String areaName;// 区域名称
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<TransArea> childs = new ArrayList<TransArea>();// 下级区域
	@ManyToOne(fetch = FetchType.LAZY)
	private TransArea parent;// 上级区域
	private int sequence;// 序号
	private int level;// 层级

	public TransArea() {
	}

	public TransArea(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getAreaName() {
		return this.areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public List<TransArea> getChilds() {
		return this.childs;
	}

	public void setChilds(List<TransArea> childs) {
		this.childs = childs;
	}

	public TransArea getParent() {
		return this.parent;
	}

	public void setParent(TransArea parent) {
		this.parent = parent;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
