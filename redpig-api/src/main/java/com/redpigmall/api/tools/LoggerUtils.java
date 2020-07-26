package com.redpigmall.api.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * <p>
 * Title: LoggerUtils.java
 * </p>
 * 
 * <p>
 * Description:日志工具类
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
 * @date 2017-2-16
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class LoggerUtils {
	/**
	 * 打印日志
	 * @param t
	 * @param clazz
	 */
	@SuppressWarnings("rawtypes")
	public static void printMsg(Throwable t, Class clazz) {
		Logger logger = LoggerFactory.getLogger(clazz);
		t.printStackTrace();
		logger.error(t.getMessage());
	}
	
	/**
	 * 打印日志
	 * @param t
	 */
	public static void printMsg(Throwable t) {
		Logger logger = LoggerFactory.getLogger(LoggerUtils.class);
		t.printStackTrace();
		logger.error(t.getMessage());
		throw new RuntimeException(t.getMessage());
	}
	
	/**
	 * 
	 * @param args
	 */
	public static void testmai(String[] args) {
		try {
			System.out.println(1 / 0);
		} catch (Exception e) {
			LoggerUtils.printMsg(e);
		}
	}

}
