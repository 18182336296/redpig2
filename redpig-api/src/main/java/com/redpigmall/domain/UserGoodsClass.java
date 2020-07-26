package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: UserGoodsClass.java
 * </p>
 * 
 * <p>
 * Description:用户商品分类,用在卖家店铺内部使用
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_gc")
public class UserGoodsClass extends IdEntity {
	private String className;//分类名称
	private int sequence;//序号
	private Boolean display;//是否显示
	private int level;//等级
	@ManyToOne(fetch = FetchType.LAZY)
	private UserGoodsClass parent;//父类
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@OrderBy("sequence asc")
	private List<UserGoodsClass> childs = new ArrayList<UserGoodsClass>();//子类
	@ManyToMany(mappedBy = "goods_ugcs")
	private List<Goods> goods_list = new ArrayList<Goods>();// 店铺中商品所在分类
	private long user_id;// 所属商家id

	public UserGoodsClass() {
	}

	public UserGoodsClass(Long id, Date addTime) {
		super(id, addTime);
	}

	public UserGoodsClass(Long id) {
		super.setId(id);
	}

	public long getUser_id() {
		return this.user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public List<Goods> getGoods_list() {
		return this.goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Boolean getDisplay() {
		return this.display;
	}
	
	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public UserGoodsClass getParent() {
		return this.parent;
	}

	public void setParent(UserGoodsClass parent) {
		this.parent = parent;
	}

	public List<UserGoodsClass> getChilds() {
		return this.childs;
	}

	public void setChilds(List<UserGoodsClass> childs) {
		this.childs = childs;
	}
}
