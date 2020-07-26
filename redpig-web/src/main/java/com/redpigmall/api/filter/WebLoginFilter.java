package com.redpigmall.api.filter;

import com.redpigmall.api.constant.Globals;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * 
 * 
 * Title: WebLoginFilter.java
 * 
 * Description:登录过滤器
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月21日 下午5:40:31
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@WebFilter(filterName="webLoginFilter",urlPatterns="/*",dispatcherTypes={DispatcherType.REQUEST,DispatcherType.FORWARD,DispatcherType.ERROR,DispatcherType.INCLUDE})
public class WebLoginFilter extends AbstractProcessLoginFilter {

	@Override
	public void doHttpFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

		try {
			// 取得根目录所对应的绝对路径:
			String currentURL = request.getRequestURI();
			// 截取到当前文件名用于比较
			HttpSession session = request.getSession(false);
			// 根据URL拦截,如果未登录则不能访问/buyer/的url
			if (currentURL.contains("/buyer/") && Globals.WEB_NAME.equals(Globals.WEB)) {

				// 用户未登录,跳转到提示接口
				if (session == null || session.getAttribute(Globals.USER_LOGIN) == null) {
					response.sendRedirect("/user/login");
					return;
				}
			}
			// 加入filter链继续向下执行
			chain.doFilter(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
