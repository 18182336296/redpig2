package com.redpigmall.manage.admin.action.deal.consult;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: ConsultManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统咨询管理类，
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
 * @date 2014年4月24日
 *
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigConsultManageAction extends BaseAction{

	/**
	 * 咨询列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param consult_user_userName
	 * @param consult_content
	 * @return
	 */
	@SecurityMapping(title = "咨询列表", value = "/consult_list*", rtype = "admin", rname = "咨询管理", rcode = "consult_admin", rgroup = "交易")
	@RequestMapping({ "/consult_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String consult_user_userName,
			String consult_content) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/consult_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if ((consult_user_userName != null)
				&& (!consult_user_userName.equals(""))) {
			params.put("consult_user_name", CommUtil.null2String(consult_user_userName).trim());
		}
		
		IPageList pList = this.redPigConsultService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("consult_user_userName", consult_user_userName);
		mv.addObject("consult_content", consult_content);
		return mv;
	}
	
	/**
	 * 咨询删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "咨询删除", value = "/consult_del*", rtype = "admin", rname = "咨询管理", rcode = "consult_admin", rgroup = "交易")
	@RequestMapping({ "/consult_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.redPigConsultService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:consult_list?currentPage=" + currentPage;
	}
}
