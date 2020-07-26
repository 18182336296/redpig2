package com.redpigmall.api.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * <p>
 * Title: PermissionFilter.java
 * </p>
 * 
 * <p>
 * Description:登录验证
 * 1、此为买家端,也即是PC端的登录验证
 * 2、验证方式为:
 * 		①、主要是针对/buyer/xxx形式的拦截,其他都不需要做拦截操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2017-2-19
 * 
 * @version redpigmall_b2b2c 8.0
 */
public abstract class AbstractProcessLoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    
    public abstract void doHttpFilter(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws IOException, ServletException;
    
    /***
     * 请求服务时
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    	HttpServletRequest request =(HttpServletRequest)req;
    	
    	HttpServletResponse response = (HttpServletResponse) res;
    	
    	doHttpFilter(request, response,chain);
    	
        
    }
    
    /***
     * 服务重启之后
     */
    @Override
    public void destroy() {
    }
}