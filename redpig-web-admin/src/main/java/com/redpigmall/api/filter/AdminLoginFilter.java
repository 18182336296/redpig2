package com.redpigmall.api.filter;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.redpigmall.api.constant.Globals;
/**
 * 
 * 
 * Title: AdminLoginFilter.java
 * 
 * Description:登录过滤器
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月21日 下午5:37:51
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@WebFilter(filterName="adminLoginFilter",urlPatterns="/*",dispatcherTypes={DispatcherType.REQUEST,DispatcherType.FORWARD,DispatcherType.ERROR,DispatcherType.INCLUDE})
public class AdminLoginFilter extends AbstractProcessLoginFilter {
	
	@Override
	public void doHttpFilter(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws IOException, ServletException {
		
        // 取得根目录所对应的绝对路径:
        String currentURL = request.getRequestURI();
        
        // 截取到当前文件名用于比较
        HttpSession session = request.getSession(false);
        
        String suffix = currentURL.contains(".")?currentURL.substring(currentURL.lastIndexOf(".")):"null";
        
        /**
         * urls里面的内容需要被过滤掉
         */
        if (!Globals.urls.contains(currentURL) 
        		&& !Globals.urls.contains(suffix)
        		&& Globals.WEB_NAME.equals(Globals.ADMIN)) {
        		
                // 用户未登录,跳转到提示接口
                if (session == null || session.getAttribute(Globals.USER_LOGIN) == null) {
                    response.sendRedirect("/login");
                    return;
                }
                
                // 用户已登录,判断权限
                else{
                	
                	if(currentURL!=null && currentURL.trim().equals("/")){
                		currentURL = "/index";
                	}
                	
                }
        
        }
        
        // 加入filter链继续向下执行
        chain.doFilter(request, response);
		
	}

	
	
}
