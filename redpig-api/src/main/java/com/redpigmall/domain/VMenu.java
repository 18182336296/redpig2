package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: VMenu.java
 * </p>
 * 
 * <p>
 * Description: 微信公众平台菜单设置
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
 * @date 2014-12-20
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "vmenu")
public class VMenu extends IdEntity {
	
	@OneToMany(mappedBy = "reply_content")
	List<ReplyContent> reply_list = Lists.newArrayList();

	private String menu_type;// click或者view
	private String menu_name;// 菜单名称
	private String menu_key;// 菜单key
	@Column(columnDefinition = "LongText")
	private String menu_key_content;// 菜单key对应的文字内容
	private String menu_url;// 菜单url
	@Column(columnDefinition = "int default 0")
	private int menu_sequence;// 菜单序号，正序排列
	@ManyToOne
	private VMenu parent;// 父级菜单
	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
	@OrderBy(value = "menu_sequence")
	private List<VMenu> childs = new ArrayList<VMenu>();// 子菜单
	public List<ReplyContent> getReply_list() {
		return this.reply_list;
	}

	public void setReply_list(List<ReplyContent> reply_list) {
		this.reply_list = reply_list;
	}

	public String getMenu_key_content() {
		return this.menu_key_content;
	}

	public void setMenu_key_content(String menu_key_content) {
		this.menu_key_content = menu_key_content;
	}

	public int getMenu_sequence() {
		return this.menu_sequence;
	}

	public void setMenu_sequence(int menu_sequence) {
		this.menu_sequence = menu_sequence;
	}

	public String getMenu_type() {
		return this.menu_type;
	}

	public void setMenu_type(String menu_type) {
		this.menu_type = menu_type;
	}

	public String getMenu_name() {
		return this.menu_name;
	}

	public void setMenu_name(String menu_name) {
		this.menu_name = menu_name;
	}

	public String getMenu_key() {
		return this.menu_key;
	}

	public void setMenu_key(String menu_key) {
		this.menu_key = menu_key;
	}

	public String getMenu_url() {
		return this.menu_url;
	}

	public void setMenu_url(String menu_url) {
		this.menu_url = menu_url;
	}

	public VMenu getParent() {
		return this.parent;
	}

	public void setParent(VMenu parent) {
		this.parent = parent;
	}

	public List<VMenu> getChilds() {
		return this.childs;
	}

	public void setChilds(List<VMenu> childs) {
		this.childs = childs;
	}
}
