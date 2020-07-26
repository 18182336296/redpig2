package com.redpigmall.module.weixin.interceptor;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.service.RedPigSysConfigService;

/**
 * <p>
 * Title: RedPigWeixinInterceptor.java
 * </p>
 * 
 * <p>
 * Description:拦截非法请求向微信接口发送消息
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
public class RedPigWeixinInterceptor implements HandlerInterceptor {
	@Autowired
	private RedPigSysConfigService configService;

	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object obj, Exception exc)
			throws Exception {
	}

	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj, ModelAndView mv)
			throws Exception {
	}

	@SuppressWarnings("deprecation")
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object obj) throws Exception {
		boolean ret = false;
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String token = this.configService.getSysConfig().getWeixin_token();
		String signature = request.getParameter("signature");
		String[] str = { token, timestamp, nonce };
		Arrays.sort(str);
		String sort_str = str[0] + str[1] + str[2];
		String mark = DigestUtils.shaHex(sort_str);
		if (mark.equals(signature)) {
			ret = true;
		}
		return ret;
	}
}
