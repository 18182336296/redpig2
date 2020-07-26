package com.redpigmall.api.tools;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <p>
 * Title: InitSpringUtilListener.java
 * </p>
 * 
 * <p>
 * Description: InitSpringUtilListener获取ServletContext
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c v6.0 2016版
 */
@WebListener
public class InitSpringUtilListener implements ServletContextListener {
	private Logger log = LoggerFactory.getLogger(InitSpringUtilListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent evt) {
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		
		log.debug("开始初始化SpringUtil...");
		ServletContext ctx = evt.getServletContext();
		SpringUtil.init(ctx);
		
		final String webRootAbsPath = evt.getServletContext().getRealPath("/");
		log.debug("web root abs path=" + webRootAbsPath);
		SpringUtil.webRootAbsPath = webRootAbsPath;
		log.debug("SpringUtil初始化完成.");

	}
}
