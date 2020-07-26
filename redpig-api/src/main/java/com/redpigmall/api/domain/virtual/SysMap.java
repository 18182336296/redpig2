package com.redpigmall.api.domain.virtual;

/**
 * 
 * <p>
 * Title: SysMap.java
 * </p>
 * 
 * <p>
 * Description: 一个类似Map的实体类，提供构造函数，使用根据方便，主要用在系统面向对象查询的参数传递
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
 * @author redpigmall
 * 
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
public class SysMap {
	private Object key;
	private Object value;

	public SysMap() {
	}

	public SysMap(Object key, Object value) {
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return this.key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return this.value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
