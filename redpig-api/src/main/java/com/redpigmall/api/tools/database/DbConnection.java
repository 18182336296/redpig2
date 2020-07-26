package com.redpigmall.api.tools.database;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * 
 * <p>
 * Title: DbConnection.java
 * </p>
 * 
 * <p>
 * Description: 数据库的连接,使用线程安全管理，确保数据库链接只存在一个，维护系统性能正常
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
@Repository
public class DbConnection {
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	// 线程安全
	public static final ThreadLocal<Connection> thread = new ThreadLocal<Connection>();

	public Connection getConnection() {
		Connection conn = (Connection) thread.get();
		if (conn == null) {
			try {
				conn = this.dataSource.getConnection();
				thread.set(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return conn;
	}
	/**
	 * 关闭链接
	 */
	public void closeAll() {
		try {
			Connection conn = (Connection) thread.get();
			if (conn != null) {
				conn.close();
				thread.set(null);
			}
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
