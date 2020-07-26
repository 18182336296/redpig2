package com.redpigmall.api.query;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * <p>
 * Title: QueryObject.java
 * </p>
 * 
 * <p>
 * Description:基础查询对象，封装基础查询条件，包括页大小、当前页、排序信息等
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
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class RedPigQueryPage {
	/** 查询构造器，为空时查询obj所有字段 */
	private String construct;
	/** 默认分页数据，表示每页12条记录 */
	protected Integer pageSize = 12;
	/** 当前页，默认为0，jpa查询从0开始计算，表示第一页 */
	protected Integer currentPage = 0;
	/** 排序字段，默认为addTime */
	protected String orderBy;
	/** 排序类型，默认为倒叙 */
	protected String orderType;

	protected Map<Object, Object> params = Maps.newHashMap();

	public String getConstruct() {
		return this.construct;
	}

	public void setConstruct(String construct) {
		this.construct = construct;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void setParams(Map params) {
		this.params = params;
	}

	public String getOrderType() {
		return this.orderType;
	}

	public Integer getCurrentPage() {
		if (this.currentPage == null) {
			this.currentPage = Integer.valueOf(-1);
		}
		return this.currentPage;
	}

	public String getOrder() {
		return this.orderType;
	}

	public String getOrderBy() {
		return this.orderBy;
	}

	public Integer getPageSize() {
		if (this.pageSize == null) {
			this.pageSize = Integer.valueOf(-1);
		}
		return this.pageSize;
	}

	public RedPigQueryPage() {

	}

	public PageObject getPageObj() {
		PageObject pageObj = new PageObject();
		pageObj.setCurrentPage(getCurrentPage());
		pageObj.setPageSize(getPageSize());
		if ((this.currentPage == null) || (this.currentPage.intValue() <= 0)) {
			pageObj.setCurrentPage(Integer.valueOf(1));
		}
		return pageObj;
	}

	protected String orderString() {
		String orderString = " ";
		if ((getOrderBy() != null) && (!"".equals(getOrderBy()))) {
			orderString = orderString + " order by obj." + getOrderBy();
		}
		if ((getOrderType() != null) && (!"".equals(getOrderType()))) {
			orderString = orderString + " " + getOrderType();
		}
		return orderString;
	}

	@SuppressWarnings("rawtypes")
	public Map getParameters() {
		return this.params;
	}

	public void customizeQuery() {
	}
}
