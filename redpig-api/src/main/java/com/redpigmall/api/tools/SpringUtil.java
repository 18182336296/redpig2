package com.redpigmall.api.tools;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 
 * <p>
 * Title: SpringUtil.java
 * </p>
 * 
 * <p>
 * Description: Spring工具类
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
 * @date 2017-10-07
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class SpringUtil {
	private static ApplicationContext ctx;
	static String webRootAbsPath;

	public static void setWebRootAbsPath(String webRootAbsPath) {
		SpringUtil.webRootAbsPath = webRootAbsPath;
	}

	/**
	 * 构造方法
	 */
	private SpringUtil() {

	}

	/**
	 * 由web容器初始化（用于服务环境）
	 * 
	 * @param sc
	 *            servlet上下文
	 */
	static void init(ServletContext sc) {
		ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
	}

	/**
	 * 由类路径下的配置文件初始化（用于测试环境）
	 * 
	 * @param ctxFilePath
	 *            配置文件路径
	 */
	static void init(String... ctxFilePath) {
		ctx = new ClassPathXmlApplicationContext(ctxFilePath);
	}

	/**
	 * 直接传入applicationContext
	 * 
	 * @param actx
	 *            应用程序上下文对象
	 */
	static void init(ApplicationContext actx) {
		ctx = actx;
	}

	/**
	 * 获取bean
	 * 
	 * @param id
	 *            id标识符
	 * @return bean对象
	 */
	public static Object getBean(String id) {
		return ctx.getBean(id);
	}

	/**
	 * 
	 * Description: 按类型获取bean
	 *
	 * @param
	 * @return T
	 * @throws
	 * @Author fei Create Date: 2013-9-6 下午2:15:18
	 */
	public static <T> T getBean(Class<T> clazz) {
		return ctx.getBean(clazz);
	}

	/**
	 * 
	 * Description: 按类型及ID获取bean
	 *
	 * @param
	 * @return T
	 * @throws
	 * @Author fei Create Date: 2013-9-6 下午2:15:29
	 */
	public static <T> T getBean(String id, Class<T> clazz) {
		return ctx.getBean(id, clazz);
	}

	public static String getWebRootAbsPath() {
		return webRootAbsPath;
	}

}
