package com.redpigmall.api.query.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * <p>
 * Title: IPageList.java
 * </p>
 * 
 * <p>
 * Description: 分页业务引擎
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
public interface IPageList extends Serializable {
	/**
	 * 得到查询结果集
	 * 
	 * @return 查询结果集
	 */
	@SuppressWarnings("rawtypes")
	List getResult();

	/**
	 * 设置分页查询处理器
	 * 
	 * @param q
	 */
	void setQuery(IQuery paramIQuery);

	/**
	 * 返回总页数
	 * 
	 * @return 查询结果总页数
	 */
	int getPages();

	/**
	 * 返回查询总记录数
	 * 
	 * @return 查询结果总记录数
	 */
	int getRowCount();

	/**
	 * 返回有效的当前页
	 * 
	 * @return 有效的当前页
	 */
	int getCurrentPage();

	/**
	 * 每页记录数
	 * 
	 * @return 每页记录数
	 */
	int getPageSize();

	/**
	 * 执行查询操作
	 * 
	 * @param pageSize
	 *            每页记录数
	 * @param pageNo
	 *            页码
	 * @param totalSQL
	 *            统计sql
	 * @param construct
	 *            查询构造函数
	 * @param queryHQL
	 *            查询sql
	 */
	void doList(int pageSize, int pageNo, String totalSQL, String construct,
			String queryHQL);

	/**
	 * 执行查询操作
	 * 
	 * @param pageSize
	 *            每页记录数
	 * @param pageNo
	 *            页码
	 * @param totalSQL
	 *            统计sql
	 * @param 查询构造函数
	 * @param queryHQL
	 *            查询sql
	 * @param params
	 *            查询参数
	 */
	@SuppressWarnings("rawtypes")
	public void doList(int pageSize, int pageNo, String totalSQL,
			String construct, String queryHQL, Map params);
}
