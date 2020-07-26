package com.redpigmall.manage.admin.action.deal.complaint;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Complaint;
import com.redpigmall.domain.SysConfig;

/**
 * 
 * <p>
 * Title: RedPigComplaintManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统投诉管理类
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
 * @date 2014年5月27日
 * 
 * @version 1.0
 */
@Controller
public class RedPigComplaintManageAction extends BaseAction{
	
	/**
	 * 投诉列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param status
	 * @param from_user
	 * @param to_user
	 * @param title
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "投诉列表", value = "/complaint_list*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_list" })
	public ModelAndView complaint_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String status, String from_user, String to_user,
			String title, String beginTime, String endTime) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/complaint_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("status", CommUtil.null2String(status).equals("") ? "new"
				: status);
		if ((CommUtil.null2String(status).equals(""))
				|| (CommUtil.null2String(status).equals("new"))) {
			status = "0";
		}
		if (CommUtil.null2String(status).equals("complain")) {
			status = "1";
		}
		if (CommUtil.null2String(status).equals("talk")) {
			status = "2";
		}
		if (CommUtil.null2String(status).equals("arbitrate")) {
			status = "3";
		}
		if (CommUtil.null2String(status).equals("close")) {
			status = "4";
		}
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		params.put("status", CommUtil.null2Int(status));
		
		if (!CommUtil.null2String(from_user).equals("")) {
			params.put("from_user_userName", from_user);
		}
		
		if (!CommUtil.null2String(to_user).equals("")) {
			params.put("to_user_userName", to_user);
		}
		
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("addTime_more_than_equal", CommUtil.formatDate(beginTime));
		}
		
		if (!CommUtil.null2String(endTime).equals("")) {
			params.put("addTime_less_than_equal", CommUtil.formatDate(endTime));
		}
		
		IPageList pList = this.redPigComplaintService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("from_user", from_user);
		mv.addObject("to_user", to_user);
		mv.addObject("title", title);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		return mv;
	}
	
	/**
	 * 投诉设置
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "投诉设置", value = "/complaint_set*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_set" })
	public ModelAndView complaint_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/complaint_set.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("config", this.redPigSysConfigService.getSysConfig());
		return mv;
	}
	
	/**
	 * 投诉设置保存
	 * @param request
	 * @param response
	 * @param id
	 * @param complaint_time
	 * @return
	 */
	@SecurityMapping(title = "投诉设置保存", value = "/complaint_set_save*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_set_save" })
	public ModelAndView complaint_set_save(HttpServletRequest request,
			HttpServletResponse response, String id, String complaint_time) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if (id.equals("")) {
			SysConfig config = this.redPigSysConfigService.getSysConfig();
			config.setComplaint_time(CommUtil.null2Int(complaint_time));
			this.redPigSysConfigService.save(config);
		} else {
			SysConfig config = this.redPigSysConfigService.getSysConfig();
			config.setComplaint_time(CommUtil.null2Int(complaint_time));
			this.redPigSysConfigService.update(config);
		}
		mv.addObject("op_title", "投诉设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/complaint_set");
		return mv;
	}
	
	/**
	 * 投诉详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "投诉详情", value = "/complaint_view*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_view" })
	public ModelAndView complaint_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/complaint_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.redPigComplaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("orderFormTools", this.redPigOrderFormTools);
		return mv;
	}
	
	/**
	 * 投诉图片
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "投诉图片", value = "/complaint_img*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_img" })
	public ModelAndView complaint_img(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/complaint_img.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.redPigComplaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));

		mv.addObject("obj", obj);
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 投诉审核
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "投诉审核", value = "/complaint_audit*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_audit" })
	public ModelAndView complaint_audit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.redPigComplaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj != null) && (obj.getStatus() < 3)) {
			obj.setStatus(obj.getStatus() + 1);
			this.redPigComplaintService.updateById(obj);
			mv.addObject("op_title", "审核成功");
		} else {
			mv.addObject("op_title", "审核失败");
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/complaint_list");
		return mv;
	}
	
	/**
	 * 投诉关闭
	 * @param request
	 * @param response
	 * @param id
	 * @param handle_content
	 * @return
	 */
	@SecurityMapping(title = "投诉关闭", value = "/complaint_close*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_close" })
	public ModelAndView complaint_close(HttpServletRequest request,
			HttpServletResponse response, String id, String handle_content) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.redPigComplaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setStatus(4);
		obj.setHandle_content(handle_content);
		obj.setHandle_time(new Date());
		this.redPigComplaintService.updateById(obj);
		mv.addObject("op_title", "关闭投诉成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/complaint_list");
		return mv;
	}
	
	/**
	 * 发布投诉对话
	 * @param request
	 * @param response
	 * @param id
	 * @param talk_content
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "发布投诉对话", value = "/complaint_talk*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_talk" })
	public void complaint_talk(HttpServletRequest request,
			HttpServletResponse response, String id, String talk_content)
			throws IOException {
		Complaint obj = this.redPigComplaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (!CommUtil.null2String(talk_content).equals("")) {
			talk_content = talk_content.replace("\n", "<br>");
			String temp = "管理员["
					+ SecurityUserHolder.getCurrentUser().getUsername() + "] "
					+ CommUtil.formatLongDate(new Date()) + "说: "
					+ talk_content;
			if (obj.getTalk_content() == null) {
				obj.setTalk_content(temp);
			} else {
				obj.setTalk_content(temp + "\n\r" + obj.getTalk_content());
			}
			this.redPigComplaintService.updateById(obj);
		}
		List<Map> maps = Lists.newArrayList();
		for (String s : CommUtil.str2list(obj.getTalk_content())) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("content", s);
			if (s.indexOf("管理员") == 0) {
				map.put("role", "admin");
			}
			if (s.indexOf("投诉") == 0) {
				map.put("role", "from_user");
			}
			if (s.indexOf("申诉") == 0) {
				map.put("role", "to_user");
			}
			maps.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(maps));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 投诉仲裁
	 * @param request
	 * @param response
	 * @param id
	 * @param handle_content
	 * @return
	 */
	@SecurityMapping(title = "投诉仲裁", value = "/complaint_handle_close*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaint_handle_close" })
	public ModelAndView complaint_handle_close(HttpServletRequest request,
			HttpServletResponse response, String id, String handle_content) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.redPigComplaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setStatus(4);
		obj.setHandle_content(handle_content);
		obj.setHandle_time(new Date());
		obj.setHandle_user(SecurityUserHolder.getCurrentUser());
		this.redPigComplaintService.updateById(obj);
		mv.addObject("op_title", "投诉仲裁成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/complaint_list");
		return mv;
	}
}
