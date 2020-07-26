package com.redpigmall.api.mv;

import java.util.Map;

import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.HttpInclude;
import com.redpigmall.api.tools.LangUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.UserConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

/**
 * <p>
 * Title: RedPigJModelAndView.java
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
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c 5.0
 */
public class RedPigJModelAndView extends ModelAndView {

	/**
	 * 普通视图，根据velocity配置文件的路径直接加载视图
	 * 
	 * @param viewName
	 *            视图名称
	 */
	public RedPigJModelAndView(String viewName) {
		super.setViewName(viewName);
	}

	/**
	 * 
	 * @param viewName
	 *            用户自定义的视图，可以添加任意路径
	 * @param request
	 */
	public RedPigJModelAndView(String viewName, SysConfig config,
			UserConfig uconfig, HttpServletRequest request,
			HttpServletResponse response) {
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
			super.addObject("cdnServer", config.getCdnServer());
		} else {
			super.addObject("imageWebServer", webPath);
			super.addObject("cdnServer", webPath);
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
	 * @param sysConfig
	 *            系统配置
	 * @param userConfig
	 *            用户配置
	 * @param type
	 *            0:跳转到system系统管理 1:跳转到shop前台店铺 其他的跳转到viewName
	 *            2:跳转到viewName目录,不拼接目录
	 * @param request
	 * @param response
	 */
	public RedPigJModelAndView(String viewName, SysConfig sysConfig,
			UserConfig userConfig, int type, HttpServletRequest request,
			HttpServletResponse response) {		
 		if (sysConfig.getSysLanguage() != null) {//判断是不是多语言
			if (type == 0) {//管理平台
				super.setViewName("templates/WEB-INF/templates/"+LangUtils.getLang(sysConfig,type,request)+"/system/" + viewName);
			}
			if (type == 1) {//PC端
				super.setViewName("templates/WEB-INF/templates/"+LangUtils.getLang(sysConfig,type,request)+"/shop/" + viewName);
			}
			if (type > 1) {//其他页面
				super.setViewName(viewName);
			}
		} else {
			super.setViewName(viewName);
		}
		
		super.addObject("CommUtil", new CommUtil());//
		super.addObject("RedPigCommUtil", new RedPigCommonUtil());
		String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
		
		String webPath = CommUtil.getURL(request);
		String port = ":" + CommUtil.null2Int(request.getServerPort());
		
		super.addObject("current_webPath", webPath);
		
		if ((sysConfig.getSecond_domain_open())
				&& (!CommUtil.generic_domain(request).equals("localhost"))
				&& (!CommUtil.isIp(request.getServerName()))) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port
					+ contextPath;
		}
		
		super.addObject("domainPath", CommUtil.generic_domain(request));
		super.addObject("webPath", webPath);
		
		super.addObject("imageWebServer", sysConfig.getImageWebServer());
		super.addObject("cdnServer", sysConfig.getCdnServer());
		
		super.addObject("config", sysConfig);
		super.addObject("uconfig", userConfig);
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
				&& (sysConfig.getSecond_domain_open())) {
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
	 * @param sysConfig
	 *            系统配置
	 * @param userConfig
	 *            用户配置
	 * @param request
	 * @param response
	 */
	public RedPigJModelAndView(
			SysConfig sysConfig, 
			UserConfig userConfig,
			HttpServletRequest request, 
			HttpServletResponse response,
			Map<String, Object> velocityParams) {
		
		velocityParams.put("CommUtil", new CommUtil());
		String contextPath = request.getContextPath().equals("/") ? ""
				: request.getContextPath();

		String webPath = CommUtil.getURL(request);
		String port = ":"
				+ CommUtil.null2Int(Integer.valueOf(request.getServerPort()));

		velocityParams.put("current_webPath", webPath);

		if ((sysConfig.getSecond_domain_open())
				&& (!CommUtil.generic_domain(request).equals("localhost"))
				&& (!CommUtil.isIp(request.getServerName()))) {
			webPath = "http://www." + CommUtil.generic_domain(request) + port
					+ contextPath;
		}

		velocityParams.put("domainPath", CommUtil.generic_domain(request));
		velocityParams.put("webPath", webPath);
		
		velocityParams.put("imageWebServer", sysConfig.getImageWebServer());
		velocityParams.put("cdnServer", sysConfig.getCdnServer());
		
		velocityParams.put("config", sysConfig);
		velocityParams.put("uconfig", userConfig);
		velocityParams.put("user", SecurityUserHolder.getCurrentUser());
		velocityParams.put("httpInclude", new HttpInclude(request, response));

		String query_url = "";
		if ((request.getQueryString() != null)
				&& (!request.getQueryString().equals(""))) {
			query_url = "?" + request.getQueryString();
		}

		velocityParams.put("current_url", request.getRequestURI() + query_url);
		boolean second_domain_view = false;
		String serverName = request.getServerName().toLowerCase();

		if ((serverName.indexOf("www.") < 0) && (serverName.indexOf(".") >= 0)
				&& (serverName.indexOf(".") != serverName.lastIndexOf("."))
				&& (sysConfig.getSecond_domain_open())) {

			String secondDomain = serverName.substring(0,
					serverName.indexOf("."));
			second_domain_view = true;
			velocityParams.put("secondDomain", secondDomain);
		}
		velocityParams.put("second_domain_view",
				Boolean.valueOf(second_domain_view));
	}

}
