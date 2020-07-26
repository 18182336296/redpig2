package com.redpigmall.module.weixin.interceptor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.module.weixin.view.tools.Base64Tools;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigUserService;

/**
 * <p>
 * Title: RedPigLoginInterceptor.java
 * </p>
 * 
 * <p>
 * Description:微信端自动进入登陆状态拦截器
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
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
public class RedPigLoginInterceptor implements HandlerInterceptor {
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private Base64Tools base64Tools;
	
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
	}
	
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
	}
	
	@SuppressWarnings({ "unused"})
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		String openid = request.getParameter("openid");
		String url = request.getParameter("url");
		if ((openid != null) && (url != null)) {
			request.getSession(false).setAttribute("his_url",
					url + "&openid=" + openid);
		}
		boolean ret = true;
		if ((openid != null) && (!openid.equals(""))
				&& (SecurityUserHolder.getCurrentUser() == null)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("openid", openid);
			
			List<User> user = this.userService.queryPageList(params);
			
			if (user.size() == 1) {
				String userName = ((User) user.get(0)).getUserName();
				String password = ((User) user.get(0)).getPassword();
				if ((userName != null) && (!userName.equals(""))) {
					String userMark = this.base64Tools.decodeStr(((User) user
							.get(0)).getUserMark());
					
					response.sendRedirect("/redpigmall_login?username="
							+ userName + "&password=" + password
							+ "&encode=true");
				}
			}
		}
		return ret;
	}
}
