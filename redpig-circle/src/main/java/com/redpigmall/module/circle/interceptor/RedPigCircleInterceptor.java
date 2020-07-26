package com.redpigmall.module.circle.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.service.RedPigSysConfigService;
/**
 * <p>
 * Title: RedPigCircleInterceptor.java
 * </p>
 * 
 * <p>
 * Description:拦截器 
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
 * @date 2014-11-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
public class RedPigCircleInterceptor implements HandlerInterceptor {
	
	@Autowired
	private RedPigSysConfigService configService;
	
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}
	
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
	}
	
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj) throws Exception {
		boolean ret = false;
		if (this.configService.getSysConfig().getCircle_open() == 1) {
			ret = true;
		} else {
			response.sendRedirect(CommUtil.getURL(request) + "/404");
		}
		return ret;
	}
}
