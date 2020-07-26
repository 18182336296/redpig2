package com.redpigmall.api.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.print.attribute.HashAttributeSet;

import com.google.common.collect.HashBiMap;
import com.redpigmall.api.annotation.Lock;
import com.redpigmall.api.annotation.RedPigColumn;

/**
 * 
 * <p>
 * Title: IdEntity.java
 * </p>
 * 
 * <p>
 * Description:
 * 系统域模型基类，该类包含3个常用字段，其中id为自增长类型，该类实现序列化，只有序列化后才可以实现tomcat集群配置session共享
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
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@MappedSuperclass
public class IdEntity implements Serializable {

	
	/**
	 * 序列化接口，自动生成序列号
	 */
	private static final long serialVersionUID = -7741168269971132706L;
	
	/** 域模型id，这里为自增类型 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(unique = true, nullable = false)
	@RedPigColumn(value="id")
	private Long id;
	
	/** 添加时间，这里为长时间格式 */
	@RedPigColumn(value="addTime")
	private Date addTime;
	
	/** 是否删除,默认为0未删除，-1表示删除状态 */
	@Lock
	@Column(columnDefinition = "int default 0")
	@RedPigColumn(value="deleteStatus")
	private int deleteStatus;
	
	public IdEntity() {
	}

	public IdEntity(Long id, Date addTime) {
		this.id = id;
		this.addTime = addTime;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getAddTime() {
		return this.addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getDeleteStatus() {
		return this.deleteStatus;
	}

	public void setDeleteStatus(int deleteStatus) {
		this.deleteStatus = deleteStatus;
	}
}
