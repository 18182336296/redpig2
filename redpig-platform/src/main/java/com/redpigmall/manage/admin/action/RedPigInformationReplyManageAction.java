package com.redpigmall.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.InformationReply;

/**
 * 
 * <p>
 * Title: RedPigInformationReplyManageAction.java
 * </p>
 * 
 * <p>
 * Description:咨询回复
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
@Controller
public class RedPigInformationReplyManageAction extends BaseAction {

	/**
	 * 资讯回复列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "资讯回复列表", value = "/information_reply*", rtype = "admin", rname = "资讯回复", rcode = "information_reply", rgroup = "网站")
	@RequestMapping({ "/information_reply" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_reply.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, orderBy, orderType);

		IPageList pList = this.redPigInformationreplyService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/informationreply_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/informationreply_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	@RequestMapping({ "/informationreply_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/informationreply_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if ((id != null) && (!id.equals(""))) {
			InformationReply informationreply = this.redPigInformationreplyService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", informationreply);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}

	@RequestMapping({ "/informationreply_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {

		InformationReply informationreply = null;
		if (id.equals("")) {
			informationreply = (InformationReply) WebForm.toPo(request,
					InformationReply.class);
			informationreply.setAddTime(new Date());
		} else {
			InformationReply obj = this.redPigInformationreplyService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			informationreply = (InformationReply) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.redPigInformationreplyService.saveEntity(informationreply);
		} else {
			this.redPigInformationreplyService.updateById(informationreply);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存informationreply成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 资讯回复删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯回复删除", value = "/information_reply_del*", rtype = "admin", rname = "资讯回复", rcode = "information_reply", rgroup = "网站")
	@RequestMapping({ "/information_reply_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {

				this.redPigInformationreplyService.deleteById(Long.valueOf(Long
						.parseLong(id)));
			}
		}
		return "redirect:information_reply?currentPage=" + currentPage;
	}

	@RequestMapping({ "/informationreply_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		InformationReply obj = this.redPigInformationreplyService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = InformationReply.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		this.redPigInformationreplyService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
