package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: Area.java
 * </p>
 * 
 * <p>
 * Description: 系统区域类，默认导入全国区域省、市、县（区）三级数据
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "area")
public class Area extends IdEntity {
	private String areaName;// 区域名称
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	private List<Area> childs = new ArrayList<Area>();// 下级区域
	@ManyToOne(fetch = FetchType.LAZY)
	private Area parent;// 上级区域
	private int sequence;// 序号
	private int level;// 层级
	@Column(columnDefinition = "bit default false")
	private boolean common;// 常用地区，设置常用地区后该地区出现在在店铺搜索页常用地区位置
	private String first_word;//省市区的第一个中文的收个拼音,比如北京:B,南京:N,深圳:S

	public String getFirst_word() {
		return this.first_word;
	}

	public void setFirst_word(String first_word) {
		this.first_word = first_word;
	}

	public Area(Long id, Date addTime) {
		super(id, addTime);
	}

	public Area() {
	}

	public boolean getCommon() {
		return this.common;
	}
	
	public void setCommon(boolean common) {
		this.common = common;
	}

	public List<Area> getChilds() {
		return this.childs;
	}

	public void setChilds(List<Area> childs) {
		this.childs = childs;
	}

	public Area getParent() {
		if (this.parent != null) {
			return this.parent;
		}
		return new Area(super.getId(), super.getAddTime());
	}

	public void setParent(Area parent) {
		this.parent = parent;
	}

	public String getAreaName() {
		return this.areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
