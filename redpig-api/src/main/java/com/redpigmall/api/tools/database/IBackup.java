package com.redpigmall.api.tools.database;

import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * <p>
 * Title: IBackup.java
 * </p>
 * 
 * <p>
 * Description: 备份数据库和还原接口
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
 * @date 2014-06-30
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public interface IBackup {
	/**
	 * 创建数据库脚本
	 */
	boolean createSqlScript(HttpServletRequest request, String path,
			String name, String size, String tables) throws Exception;

	/**
	 * 执行数据库脚本
	 */
	boolean executSqlScript(String filePath) throws Exception;

	/**
	 * 获取数据表
	 * 
	 * @return
	 * @throws Exception
	 */
	List<String> getTables() throws Exception;

	/**
	 * 获取mysql数据库版本号
	 * 
	 * @return
	 */
	String queryDatabaseVersion();

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 * @return
	 */
	boolean execute(String sql);

	/**
	 * 导出指定表到path路径
	 * 
	 * @param tables
	 * @param path
	 * @return
	 */
	boolean export(String tables, String path);

	/**
	 * 执行指定SQL查询
	 * 
	 * @param sql
	 * @return
	 */
	ResultSet query(String sql);
}
