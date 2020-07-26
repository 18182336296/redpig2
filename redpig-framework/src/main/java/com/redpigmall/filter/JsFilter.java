package com.redpigmall.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * <p>
 * Title: JsFilter.java
 * </p>
 * 
 * <p>
 * Description:js脚本注入转义过滤器
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
 * @date 2014-12-4
 * 
 * @version redpigmall_b2b2c 8.0
 */
public class JsFilter implements Filter {
	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		String url = ((HttpServletRequest) req).getServletPath().split("/")[1];
		if (("admin".equals(url)) || ("seller".equals(url))) {
			chain.doFilter(req, res);
		} else {
			HttpServletRequest request = (HttpServletRequest) req;
			JsRequest wrapRequest = new JsRequest(request,
					request.getParameterMap());
			chain.doFilter(wrapRequest, res);
		}
	}

	public void init(FilterConfig arg0) throws ServletException {
	}
}
