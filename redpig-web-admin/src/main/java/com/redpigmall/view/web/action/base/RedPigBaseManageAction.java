package com.redpigmall.view.web.action.base;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.Log;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.qrcode.QRCodeUtil;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommUtil;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.corework.domain.LogType;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.Menu;
import com.redpigmall.domain.PredepositCash;
import com.redpigmall.domain.SnsAttention;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.SysLog;
import com.redpigmall.domain.SystemTip;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: BaseManageAction.java
 * </p>
 * 
 * <p>
 * Description:基础类
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
 * @date 2014-5-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({  "unused" })
@Controller
public class RedPigBaseManageAction extends BaseAction{
	
	private Logger logger = LoggerFactory.getLogger(RedPigBaseManageAction.class);

	/**
	 * 用户登陆
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Log(title = "用户登陆", type = LogType.LOGIN)
	@SecurityMapping(title = "系统提醒页", value = "/login_success*", rtype = "admin", rname = "系统", rcode = "login_success", display = false, rgroup = "设置")
	@RequestMapping({"/login_success"})
	public void login_success(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			
			user.setLoginDate(new Date());//用户登录时间
			user.setLoginIp(CommUtil.getIpAddr(request));//用户登录IP
			user.setLoginCount(user.getLoginCount() + 1);//用户登录次数加1
			user.setLastLoginDate(new Date());
			user.setLastLoginIp(CommUtil.getIpAddr(request));
			this.userService.updateById(user);
			/**
			 * HttpServletRequest.getSession(ture)等同于 HttpServletRequest.getSession() 
			 *	HttpServletRequest.getSession(false)等同于 如果当前Session没有就为null； 
			 */
			HttpSession session = request.getSession(false);
			session.setAttribute("user", user);
			
 			SecurityUserHolder.setCurrentUser(user);
			
			session.setAttribute("userName", user.getUsername());//用户名放到session中
			session.setAttribute("lastLoginDate", new Date());// 设置登录时间
			session.setAttribute("loginIp", CommUtil.getIpAddr(request));// 设置登录IP
			session.setAttribute("login", true);// 设置登录标识
			
			String role = user.getUserRole();
			String url = CommUtil.getURL(request) + "/user_login_success";
			if (!CommUtil.null2String(
					request.getSession(false).getAttribute("refererUrl"))
					.equals("")) {
				
				url = CommUtil.null2String(request.getSession(false).getAttribute("refererUrl"));
			}
			String login_role = (String) session.getAttribute("login_role");
			boolean ajax_login = CommUtil.null2Boolean(session
					.getAttribute("ajax_login"));
			if (ajax_login) {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print("success");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (login_role.equalsIgnoreCase("admin")) {
					if (role.indexOf("ADMIN") >= 0) {
						url = CommUtil.getURL(request) + "/index";
						//admin首页地址
						request.getSession(false).setAttribute("admin_login",true);
					}
				}
				if (login_role.equalsIgnoreCase("seller")
						&& role.indexOf("SELLER") >= 0) {
					url = CommUtil.getURL(request) + "/index";
					request.getSession(false)
							.setAttribute("seller_login", true);
				}
				if (!CommUtil.null2String(
						request.getSession(false).getAttribute("refererUrl"))
						.equals("")) {
					url = CommUtil.null2String(request.getSession(false)
							.getAttribute("refererUrl"));
					request.getSession(false).removeAttribute("refererUrl");
				}
				String userAgent = request.getHeader("user-agent");
				if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
					url = CommUtil.getURL(request) + "/index";
				}
				response.sendRedirect(url);
			}
		} else {
			String url = CommUtil.getURL(request) + "/index";
			response.sendRedirect(url);
		}

	
	}
}
