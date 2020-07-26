package com.redpigmall.api.ip;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * <p>
 * Title: LogFactory.java
 * </p>
 * 
 * <p>
 * Description:纯真ip查询日志记录
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
public class LogFactory {
	private static final Logger logger = Logger.getLogger("stdout");

	static {
		logger.setLevel(Level.INFO);
	}

	public static void log(String info, Level level, Throwable ex) {
		logger.log(level, info, ex);
	}

	public static Level getLogLevel() {
		return logger.getLevel();
	}
}
