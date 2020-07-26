package com.redpigmall.api.query;

import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.domain.virtual.SysMap;
import com.redpigmall.api.query.support.IQueryObject;
import com.redpigmall.api.tools.CommUtil;

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
public class QueryObject implements IQueryObject {
	/** 查询构造器，为空时查询obj所有字段 */
	private String construct;
	/** 默认分页数据，表示每页12条记录 */
	protected Integer pageSize = Integer.valueOf(12);
	/** 当前页，默认为0，jpa查询从0开始计算，表示第一页 */
	protected Integer currentPage = Integer.valueOf(0);
	/** 排序字段，默认为addTime */
	protected String orderBy;
	/** 排序类型，默认为倒叙 */
	protected String orderType;
	protected Map<Object, Object> params = Maps.newHashMap();
	protected String queryString = "1=1";

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

	public QueryObject() {
	}

	public QueryObject(String construct, String currentPage, ModelAndView mv,
			String orderBy, String orderType) {
		if ((construct != null) && (!construct.equals(""))) {
			setConstruct(construct);
		}
		if ((currentPage != null) && (!currentPage.equals(""))) {
			setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
		}
		setPageSize(this.pageSize);
		if ((orderBy == null) || (orderBy.equals(""))) {
			setOrderBy("addTime");
			mv.addObject("orderBy", "addTime");
		} else {
			setOrderBy(orderBy);
			mv.addObject("orderBy", orderBy);
		}
		if ((orderType == null) || (orderType.equals(""))) {
			setOrderType("desc");
			mv.addObject("orderType", "desc");
		} else {
			setOrderType(orderType);
			mv.addObject("orderType", orderType);
		}
	}

	public QueryObject(String currentPage, ModelAndView mv, String orderBy,
			String orderType) {
		if ((currentPage != null) && (!currentPage.equals(""))) {
			setCurrentPage(Integer.valueOf(CommUtil.null2Int(currentPage)));
		}
		setPageSize(this.pageSize);
		if ((orderBy == null) || (orderBy.equals(""))) {
			setOrderBy("addTime");
			mv.addObject("orderBy", "addTime");
		} else {
			setOrderBy(orderBy);
			mv.addObject("orderBy", orderBy);
		}
		if ((orderType == null) || (orderType.equals(""))) {
			setOrderType("desc");
			mv.addObject("orderType", "desc");
		} else {
			setOrderType(orderType);
			mv.addObject("orderType", orderType);
		}
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

	public String getQuery() {
		customizeQuery();
		return this.queryString + orderString();
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

	public IQueryObject addQuery(String field, SysMap para, String expression) {
		if ((field != null) && (para != null)) {
			this.queryString = (this.queryString + " and " + field + " "
					+ handleExpression(expression) + ":" + para.getKey()
					.toString());
			this.params.put(para.getKey(), para.getValue());
		}
		return this;
	}

	public IQueryObject addQuery2(String field, SysMap para, String expression) {
		if ((field != null) && (para != null)) {
			this.queryString = (this.queryString + " and " + field + " "
					+ handleExpression(expression) + para.getValue().toString());
			this.params.put(para.getKey(), para.getValue());
		}
		return this;
	}

	public IQueryObject addQuery(String field, SysMap para, String expression,
			String logic) {
		if ((field != null) && (para != null)) {
			this.queryString = (this.queryString + " " + logic + " " + field
					+ " " + handleExpression(expression) + ":" + para.getKey()
					.toString());
			this.params.put(para.getKey(), para.getValue());
		}
		return this;
	}

	@SuppressWarnings("rawtypes")
	public IQueryObject addQuery(String scope, Map paras) {
		if (scope != null) {
			if ((scope.trim().indexOf("and") == 0)
					|| (scope.trim().indexOf("or") == 0)) {
				this.queryString = (this.queryString + " " + scope);
			} else {
				this.queryString = (this.queryString + " and " + scope);
			}
			if ((paras != null) && (paras.size() > 0)) {
				for (Object key : paras.keySet()) {
					this.params.put(key, paras.get(key));
				}
			}
		}
		return this;
	}

	public IQueryObject addQuery(String para, Object obj, String field,
			String expression) {
		if ((field != null) && (para != null)) {
			this.queryString = (this.queryString + " and :" + para + " " + expression + " " + field);
			this.params.put(para, obj);
		}
		return this;
	}

	public IQueryObject addQuery(String para, Object obj, String field,
			String expression, String logic) {
		if ((field != null) && (para != null)) {
			this.queryString = (this.queryString + " " + logic + " :" + para
					+ " " + expression + " " + field);
			this.params.put(para, obj);
		}
		return this;
	}

	public IQueryObject clearQuery() {
		this.queryString = "1=1 ";
		this.params.clear();
		return this;
	}

	private String handleExpression(String expression) {
		if (expression == null) {
			return "=";
		}
		return expression;
	}

	public void customizeQuery() {
	}
}
