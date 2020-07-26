package com.redpigmall.manage.admin.action.user;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: IntegralLogManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统积分日志管理类
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigIntegralLogManageAction extends BaseAction{
	
	/**
	 * 积分明细
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param userName
	 * @return
	 */
	@SecurityMapping(title = "积分明细", value = "/integrallog_list*", rtype = "admin", rname = "积分明细", rcode = "user_integral", rgroup = "会员")
	@RequestMapping({ "/integrallog_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String userName) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/integrallog_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		if ((userName != null) && (!userName.equals(""))) {
			maps.put("integral_user_userName", userName);
		}
		mv.addObject("userName", userName);
		IPageList pList = this.integrallogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url
				+ "/integrallog_list.html", "",
				"&userName=" + CommUtil.null2String(userName), pList, mv);
		return mv;
	}
	
	/**
	 * 积分管理
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "积分管理", value = "/user_integral*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping({ "/user_integral" })
	public ModelAndView user_integral(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/user_integral.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (!config.getIntegral()) {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分功能，设置失败");
			mv.addObject("open_url", "admin/operation_base_set");
			mv.addObject("open_op", "积分开启");
			mv.addObject("open_mark", "operation_base_op");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/welcome");
		}
		return mv;
	}
	
	/**
	 * 积分动态获取
	 * @param request
	 * @param response
	 * @param userName
	 */
	@SecurityMapping(title = "积分动态获取", value = "/verify_user_integral*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping({ "/verify_user_integral" })
	public void verify_user_integral(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		
		Map<String,Object> maps = Maps.newHashMap();
		maps.put("userName", userName);
		User user = this.userService.getObjByProperty("userName","=", userName);
		
		int ret = -1;
		if (user != null) {
			ret = user.getIntegral();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 积分管理保存
	 * @param request
	 * @param response
	 * @param userName
	 * @param operate_type
	 * @param integral
	 * @param content
	 * @return
	 */
	@SecurityMapping(title = "积分管理保存", value = "/user_integral_save*", rtype = "admin", rname = "积分管理", rcode = "user_integral_manage", rgroup = "会员")
	@RequestMapping({ "/user_integral_save" })
	public ModelAndView user_integral_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String userName, String operate_type,
			String integral, String content) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		User user = this.userService.getObjByProperty("userName","=",userName);
		if (operate_type.equals("add")) {
			user.setIntegral(user.getIntegral() + CommUtil.null2Int(integral));
		} else if (user.getIntegral() > CommUtil.null2Int(integral)) {
			user.setIntegral(user.getIntegral() - CommUtil.null2Int(integral));
		} else {
			user.setIntegral(0);
		}
		this.userService.updateById(user);

		IntegralLog log = new IntegralLog();
		log.setAddTime(new Date());
		log.setContent(content);
		if (operate_type.equals("add")) {
			log.setIntegral(CommUtil.null2Int(integral));
		} else {
			log.setIntegral(-CommUtil.null2Int(integral));
		}
		log.setOperate_user(SecurityUserHolder.getCurrentUser());
		log.setIntegral_user(user);
		log.setType("system");
		this.integrallogService.saveEntity(log);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/user_integral");
		mv.addObject("op_title", "操作用户积分成功");
		return mv;
	}
}
