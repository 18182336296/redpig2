package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: Res.java
 * </p>
 * 
 * <p>
 * Description:系统权限资源管理控制器，用来记录系统权限资源信息，系统权限资源对应URL信息，SpringSecurity根据权限资源信息进行URL拦截处理
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
 * @date 2017-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "res")
public class Res extends IdEntity {
	
	private String resName;//资源名称
	private String type;// 这里只需要进行URL类型的过滤
	private String value;//资源值
	@ManyToMany(mappedBy = "reses", targetEntity = Role.class, fetch = FetchType.EAGER)
	private List<Role> roles = new ArrayList<Role>();//角色
	private int sequence;//序号
	private String info;//信息

	public Res() {
	}

	public Res(Long id, String value) {
		super.setId(id);
		this.value = value;
	}

	public Res(Long id, Date addTime) {
		super(id, addTime);
	}
	
	@Transient
	public String getRoleAuthorities(List<Role> list) {
		List<String> roleAuthorities = Lists.newArrayList();
		for (Role role : list) {
			roleAuthorities.add(role.getRoleCode());
		}
		return StringUtils.join(roleAuthorities.toArray(), ",");
	}
	
	public String getResName() {
		return this.resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
