package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: GroupClass.java
 * </p>
 * 
 * <p>
 * Description: 团购分类管理类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_class")
public class GroupClass extends IdEntity {
	private String gc_name;// 团购分类名称
	private int gc_sequence;// 分了序号，按照升序排列
	private int gc_type;// 0为商品类分类，1为生活类分类
	@Column(columnDefinition = "int default 0")
	private int gc_recommend;
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupClass parent;// 父级分类
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@OrderBy(value = "gc_sequence asc")
	private List<GroupClass> childs = new ArrayList<GroupClass>();// 子级分类
	@OneToMany(mappedBy = "gg_gc", cascade = CascadeType.REMOVE)
	private List<GroupGoods> ggs = new ArrayList<GroupGoods>();// 团购分类中对应的团购商品
	@OneToOne
	private Accessory gc_img;//团购类型图片
	private int gc_level;//团购等级

	public int getGc_level() {
		return this.gc_level;
	}

	public void setGc_level(int gc_level) {
		this.gc_level = gc_level;
	}

	public Accessory getGc_img() {
		return this.gc_img;
	}

	public void setGc_img(Accessory gc_img) {
		this.gc_img = gc_img;
	}

	public GroupClass(Long id, Date addTime) {
		super(id, addTime);
	}

	public GroupClass() {
	}

	public int getGc_recommend() {
		return this.gc_recommend;
	}

	public void setGc_recommend(int gc_recommend) {
		this.gc_recommend = gc_recommend;
	}

	public int getGc_type() {
		return this.gc_type;
	}

	public void setGc_type(int gc_type) {
		this.gc_type = gc_type;
	}

	public List<GroupGoods> getGgs() {
		return this.ggs;
	}

	public void setGgs(List<GroupGoods> ggs) {
		this.ggs = ggs;
	}

	public String getGc_name() {
		return this.gc_name;
	}

	public void setGc_name(String gc_name) {
		this.gc_name = gc_name;
	}

	public int getGc_sequence() {
		return this.gc_sequence;
	}

	public void setGc_sequence(int gc_sequence) {
		this.gc_sequence = gc_sequence;
	}

	public GroupClass getParent() {
		return this.parent;
	}

	public void setParent(GroupClass parent) {
		this.parent = parent;
	}

	public List<GroupClass> getChilds() {
		return this.childs;
	}

	public void setChilds(List<GroupClass> childs) {
		this.childs = childs;
	}
}
