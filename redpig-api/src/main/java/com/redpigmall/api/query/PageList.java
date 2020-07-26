package com.redpigmall.api.query;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.query.support.IQuery;

/**
 * 
 * <p>
 * Title: PageList.java
 * </p>
 * 
 * <p>
 * Description:实现通过调用IQuery实现分页处理，其它特殊形式的分页查询需求只需要继承该类即可，比如DbPageList。
 * 具体的分页查询算法可以根据实际应用中的记录数、响应时间要求等选择适合的查询处理器IQuery
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
@SuppressWarnings("serial")
public class PageList implements IPageList {
	/** 记录数 */
	private int rowCount;
	/** 总页数 */
	private int pages;
	/** 实际页数 */
	private int currentPage;
	/** 没页条数 */
	private int pageSize;
	/** 查询结果集 */
	@SuppressWarnings("rawtypes")
	private List result;
	/** 查询器 */
	private IQuery query;

	public PageList() {
	}

	/**
	 * 根据查询器q构造一个分页处理对象
	 * 
	 * @param q
	 */
	public PageList(IQuery q) {
		this.query = q;
	}

	/**
	 * 设置查询器
	 */
	public void setQuery(IQuery q) {
		this.query = q;
	}

	/**
	 * 返回查询结果集，只有在执行doList方法后才能取得正确的查询结果
	 */
	@SuppressWarnings("rawtypes")
	public List getResult() {
		return this.result;
	}

	/**
	 * 根据每页记录数，页码，统计sql及实际查询sql执行查询操作
	 */
	public void doList(int pageSize, int pageNo, String totalSQL,
			String construct, String queryHQL) {
		doList(pageSize, pageNo, totalSQL, construct, queryHQL, null);
	}

	/**
	 * 根据每页记录数，页码，统计sql及实际查询sql及参数执行查询操作
	 */
	@SuppressWarnings("rawtypes")
	public void doList(int pageSize, int pageNo, String totalSQL,
			String construct, String queryHQL, Map params) {
		List rs = null;
		this.pageSize = pageSize;
		if (params != null) {
			this.query.setParaValues(params);
		}
		int total = this.query.getRows(totalSQL);
		if (total > 0) {
			this.rowCount = total;
			this.pages = ((this.rowCount + pageSize - 1) / pageSize);
			int intPageNo = pageNo > this.pages ? this.pages : pageNo;
			if (intPageNo < 1) {
				intPageNo = 1;
			}
			this.currentPage = intPageNo;
			if (pageSize > 0) {
				this.query.setFirstResult((intPageNo - 1) * pageSize);
				this.query.setMaxResults(pageSize);
			}
			rs = this.query.getResult(construct, queryHQL);
		}
		if (rs != null) {
			this.result = rs;
		} else {
			this.result = Lists.newArrayList();
		}
	}

	public int getPages() {
		return this.pages;
	}

	public int getRowCount() {
		return this.rowCount;
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getNextPage() {
		int p = this.currentPage + 1;
		if (p > this.pages) {
			p = this.pages;
		}
		return p;
	}

	public int getPreviousPage() {
		int p = this.currentPage - 1;
		if (p < 1) {
			p = 1;
		}
		return p;
	}
}
