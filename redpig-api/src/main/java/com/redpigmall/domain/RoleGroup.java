package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: RoleGroup.java
 * </p>
 * 
 * <p>
 * Description: 角色分类管理类，用来管理角色信息，使用角色分类使得角色管理更加清晰
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
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "rolegroup")
public class RoleGroup extends IdEntity {
	private static final long serialVersionUID = -5430683218883217888L;
	private String name;// 角色分组名称
	private int sequence;// 角色分组序号
	private String type;// 角色分组类型
	@OneToMany(mappedBy = "rg")
	private List<Role> roles = new ArrayList<Role>();// 对应的角色信息
	
	public RoleGroup() {
	}

	public RoleGroup(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public List<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
