package com.redpigmall.api.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>
 * Title: LoggerTools.java
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
public class LoggerTools {

	@SuppressWarnings("rawtypes")
	public static void printLogger(Class clazz, String message) {

		Logger logger = LoggerFactory.getLogger(clazz);

		logger.debug(clazz + "=" + message);
	}
}
