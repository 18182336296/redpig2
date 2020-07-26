package com.redpigmall.api.sec;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: SecurityUserHolder.java
 * </p>
 * 
 * <p>
 * Description: SpringSecurity用户获取工具类，该类的静态方法可以直接获取已经登录的用户信息
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
public class SecurityUserHolder {
	
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(SecurityUserHolder.class);
	
	public static User getCurrentUser() {
		
		User user = null;
		
		if (RequestContextHolder.getRequestAttributes() != null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			try {
				user = (User) request.getSession().getAttribute(Globals.USER_LOGIN);
			} catch (Exception e) {
				request.getSession().removeAttribute(Globals.USER_LOGIN);
				return null;
			}
			
		}
		
		return user;
	}
	
	public static void setCurrentUser(User user){
		if (RequestContextHolder.getRequestAttributes() != null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			request.getSession().setAttribute(Globals.USER_LOGIN, user);
		}
	}
	
	public static void removeCurrentUser(){
		if (RequestContextHolder.getRequestAttributes() != null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			request.getSession().setAttribute(Globals.USER_LOGIN, null);
			request.getSession().removeAttribute(Globals.USER_LOGIN);
		}
	}
	
}
