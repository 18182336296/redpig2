package com.redpigmall.api.mv;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.HttpInclude;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.UserConfig;

/**
 * 
 * <p>
 * Title: JModelAndView.java
 * </p>
 * 
 * <p>
 * Description: 顶级视图管理类，封装ModelAndView并进行系统扩展
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
 * @version redpigmall_b2b2c 8.0
 */
public class JModelAndView extends ModelAndView {
	/**
	 * 普通视图，根据velocity配置文件的路径直接加载视图
	 * 
	 * @param viewName
	 *            视图名称
	 */
	public JModelAndView(String viewName) {
		super.setViewName(viewName);
	}

	/**
	 * 
	 * @param viewName
	 *            用户自定义的视图，可以添加任意路径
	 * @param request
	 */
	public JModelAndView(String viewName, SysConfig config, UserConfig uconfig,
			HttpServletRequest request, HttpServletResponse response) {
		String contextPath = request.getContextPath().equals("/") ? ""
				: request.getContextPath();
		String webPath = CommUtil.getURL(request);
		super.addObject("current_webPath", webPath);
		String port = ":"
				+ CommUtil.null2Int(Integer.valueOf(request.getServerPort()));
		if ((config.getSecond_domain_open())
				&& (!CommUtil.generic_domain(request).equals("localhost"))
				&& (!CommUtil.isIp(request.getServerName()))) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port
					+ contextPath;
		}
		super.setViewName(viewName);
		super.addObject("domainPath", CommUtil.generic_domain(request));
		if ((config.getImageWebServer() != null)
				&& (!config.getImageWebServer().equals(""))) {
			super.addObject("imageWebServer", config.getImageWebServer());
		} else {
			super.addObject("imageWebServer", webPath);
		}
		super.addObject("webPath", webPath);
		super.addObject("config", config);
		super.addObject("uconfig", uconfig);
		super.addObject("user", SecurityUserHolder.getCurrentUser());
		super.addObject("httpInclude", new HttpInclude(request, response));
		String query_url = "";
		if ((request.getQueryString() != null)
				&& (!request.getQueryString().equals(""))) {
			query_url = "?" + request.getQueryString();
		}
		super.addObject("current_url", request.getRequestURI() + query_url);
		boolean second_domain_view = false;
		String serverName = request.getServerName().toLowerCase();
		if ((serverName.indexOf("www.") < 0) && (serverName.indexOf(".") >= 0)
				&& (serverName.indexOf(".") != serverName.lastIndexOf("."))
				&& (config.getSecond_domain_open())) {
			String secondDomain = serverName.substring(0,
					serverName.indexOf("."));
			second_domain_view = true;
			super.addObject("secondDomain", secondDomain);
		}
		super.addObject("second_domain_view",
				Boolean.valueOf(second_domain_view));
	}

	/**
	 * 
	 * @param viewName
	 *            需要转到的页面
	 * @param config
	 *            系统配置
	 * @param uconfig
	 *            用户配置
	 * @param type
	 *            0:跳转到system系统管理 1:跳转到shop前台店铺 其他的跳转到viewName
	 * @param request
	 * @param response
	 */
	public JModelAndView(String viewName, SysConfig config, UserConfig uconfig,
			int type, HttpServletRequest request, HttpServletResponse response) {
		if (config.getSysLanguage() != null) {
			if (config.getSysLanguage().equals("zh_cn")) {
				if (type == 0) {
					super.setViewName("WEB-INF/templates/zh_cn/system/"
							+ viewName);
				}
				if (type == 1) {
					super.setViewName("WEB-INF/templates/zh_cn/shop/"
							+ viewName);
				}
				if (type > 1) {
					super.setViewName(viewName);
				}
			} else {
				if (type == 0) {
					super.setViewName("WEB-INF/templates/"
							+ config.getSysLanguage() + "/system/" + viewName);
				}
				if (type == 1) {
					super.setViewName("WEB-INF/templates/"
							+ config.getSysLanguage() + "/shop/" + viewName);
				}
				if (type > 1) {
					super.setViewName(viewName);
				}
			}
		} else {
			super.setViewName(viewName);
		}

		super.addObject("CommUtil", new CommUtil());
		super.addObject("RedPigCommonUtil",new RedPigCommonUtil());
		
		String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();

		String webPath = CommUtil.getURL(request);
		String port = ":" + CommUtil.null2Int(Integer.valueOf(request.getServerPort()));

		super.addObject("current_webPath", webPath);

		if ((config.getSecond_domain_open())
				&& (!CommUtil.generic_domain(request).equals("localhost"))
				&& (!CommUtil.isIp(request.getServerName()))) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port
					+ contextPath;
		}

		super.addObject("domainPath", CommUtil.generic_domain(request));
		super.addObject("webPath", webPath);

		if ((config.getImageWebServer() != null) && (!config.getImageWebServer().equals(""))) {
			super.addObject("imageWebServer", config.getImageWebServer());
		} else {
			super.addObject("imageWebServer", webPath);
		}

		super.addObject("config", config);
		super.addObject("uconfig", uconfig);
		super.addObject("user", SecurityUserHolder.getCurrentUser());
		super.addObject("httpInclude", new HttpInclude(request, response));

		String query_url = "";
		if ((request.getQueryString() != null) && (!request.getQueryString().equals(""))) {
			query_url = "?" + request.getQueryString();
		}

		super.addObject("current_url", request.getRequestURI() + query_url);
		boolean second_domain_view = false;
		String serverName = request.getServerName().toLowerCase();

		if ((serverName.indexOf("www.") < 0) && (serverName.indexOf(".") >= 0)
				&& (serverName.indexOf(".") != serverName.lastIndexOf("."))
				&& (config.getSecond_domain_open())) {
			String secondDomain = serverName.substring(0,serverName.indexOf("."));
			second_domain_view = true;
			super.addObject("secondDomain", secondDomain);
		}
		super.addObject("second_domain_view",Boolean.valueOf(second_domain_view));
	}
}
