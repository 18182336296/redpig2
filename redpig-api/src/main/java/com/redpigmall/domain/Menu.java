package com.redpigmall.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: Menu.java
 * </p>
 * 
 * <p>
 * Description:系统菜单类
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
 * @date 2017-03-22
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "menu")
@SuppressWarnings("serial")
public class Menu extends IdEntity {
	
	private String name;// 菜单名称
	private String url;// 菜单地址
	private String type;//ADMIN、SELLER、BUYER
	private String value;//英文名称
	private String op;//操作标识
	private Menu parent;//父菜单
	private List<Menu> childs;//此为用户子菜单
	private List<Menu> allChilds;//所有的子菜单
	
	public List<Menu> getChilds() {
		return childs;
	}

	public void setChilds(List<Menu> childs) {
		this.childs = childs;
	}

	
	public Menu(Long id, Date addTime) {
		super(id, addTime);
	}
	
	public Menu(Long id) {
		super.setId(id);
	}
	
	public Menu() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public List<Menu> getAllChilds() {
		return allChilds;
	}

	public void setAllChilds(List<Menu> allChilds) {
		this.allChilds = allChilds;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}
	
}
