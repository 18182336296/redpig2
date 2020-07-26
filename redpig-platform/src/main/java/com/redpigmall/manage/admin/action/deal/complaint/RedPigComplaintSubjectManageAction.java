package com.redpigmall.manage.admin.action.deal.complaint;

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
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.ComplaintSubject;

/**
 * 
 * <p>
 * Title: RedPigComplaintSubjectManageAction.java
 * </p>
 * 
 * <p>
 * Description:投诉主题类
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
@Controller
public class RedPigComplaintSubjectManageAction extends BaseAction{
	/**
	 * 投诉主题列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param q_type
	 * @return
	 */
	@SecurityMapping(title = "投诉主题列表", value = "/complaintsubject_list*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaintsubject_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String q_type) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/complaintsubject_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";

		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
        maps.put("type", q_type);
        

		if (!CommUtil.null2String(q_type).equals("")) {
			
			mv.addObject("type", q_type);
		}
		
		IPageList pList = this.complaintsubjectService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/complaintsubject_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 投诉主题添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "投诉主题添加", value = "/complaintsubject_add*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaintsubject_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/complaintsubject_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 投诉主题编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "投诉主题编辑", value = "/complaintsubject_edit*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaintsubject_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/complaintsubject_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			ComplaintSubject complaintsubject = this.complaintsubjectService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", complaintsubject);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 投诉主题保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "投诉主题保存", value = "/complaintsubject_save*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaintsubject_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		
		ComplaintSubject complaintsubject = null;
		if (id.equals("")) {
			complaintsubject = (ComplaintSubject) WebForm.toPo(request,
					ComplaintSubject.class);
			complaintsubject.setAddTime(new Date());
		} else {
			ComplaintSubject obj = this.complaintsubjectService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			complaintsubject = (ComplaintSubject) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.complaintsubjectService.saveEntity(complaintsubject);
		} else {
			this.complaintsubjectService.updateById(complaintsubject);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存投诉主题成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 投诉主题删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "投诉主题删除", value = "/complaintsubject_del*", rtype = "admin", rname = "投诉管理", rcode = "complaint_manage", rgroup = "交易")
	@RequestMapping({ "/complaintsubject_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		
		this.complaintsubjectService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		
		return "redirect:complaintsubject_list?currentPage=" + currentPage;
	}
}
