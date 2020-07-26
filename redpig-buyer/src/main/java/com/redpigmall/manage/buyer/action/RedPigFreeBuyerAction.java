package com.redpigmall.manage.buyer.action;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigFreeBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心0元试用中心
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
 * @date 2014-11-18
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigFreeBuyerAction extends BaseAction{
	
	/**
	 * 买家中心
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_logs*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/freeapply_logs" })
	public ModelAndView freeapply_logs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/freeapplylog_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		if ((status != null) && (status.equals("yes"))) {
			
			maps.put("apply_status", 5);
			
			mv.addObject("status", status);
		}
		if ((status != null) && (status.equals("waiting"))) {
			
			maps.put("apply_status", 0);
			
			mv.addObject("status", status);
		}
		if ((status != null) && (status.equals("no"))) {
			
			maps.put("apply_status", -5);
			mv.addObject("status", status);
		}
		
		IPageList pList = this.freeapplylogService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 买家中心
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_log_info*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/freeapply_log_info" })
	public ModelAndView freeapply_log_info(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/freeapplylog_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		
		FreeApplyLog fal = this.freeapplylogService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((fal != null) && (fal.getUser_id().equals(user.getId()))) {
			mv.addObject("obj", fal);
			mv.addObject("shipTools", this.shipTools);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			
			mv.addObject("op_title", "此0元试用申请无效");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/freeapply_logs");
		}
		return mv;
	}
	
	/**
	 * 买家中心
	 * @param request
	 * @param response
	 * @param id
	 * @param use_experience
	 */
	@SecurityMapping(title = "买家中心", value = "/buyer/freeapply_log_info*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/freeapplylog_save" })
	public void freeapplylog_saveEntity(HttpServletRequest request, HttpServletResponse response, String id,
			String use_experience) {
		User user = SecurityUserHolder.getCurrentUser();
		FreeApplyLog fal = this.freeapplylogService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (fal.getUser_id().equals(user.getId())) {
			fal.setUse_experience(use_experience);
			fal.setEvaluate_time(new Date());
			fal.setEvaluate_status(1);
			this.freeapplylogService.updateById(fal);

		}
	}
}
