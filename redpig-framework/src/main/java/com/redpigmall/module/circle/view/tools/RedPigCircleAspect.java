package com.redpigmall.module.circle.view.tools;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redpigmall.service.RedPigSysConfigService;


/**
 * 
 * <p>
 * Title: CircleAspect.java
 * </p>
 * 
 * <p>
 * Description: 圈子控制器切面工具类
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
 * @date 2014-12-25
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Aspect
@Component
public class RedPigCircleAspect {
	@Autowired
	private RedPigSysConfigService configService;

	@Before("execution(* com.redpigmall.module.circle.view.action..*.*(..))&&args(request,response,..)")
	public void circle_aspect(JoinPoint joinPoint, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if ((!joinPoint.getSignature().getName().equals("circle_error"))
				&& (this.configService.getSysConfig().getCircle_open() == 0)) {
			response.sendRedirect("error");
		}
	}
}
