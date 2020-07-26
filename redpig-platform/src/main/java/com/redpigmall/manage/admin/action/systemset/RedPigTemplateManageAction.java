package com.redpigmall.manage.admin.action.systemset;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Template;

/**
 * 
 * <p>
 * Title: TemplateManageAction.java
 * </p>
 * 
 * <p>
 * Description: 通知模板管理控制器，用来管理系统各类通知模板，包括站内短信通知、邮件通知、手机短信通知
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
 * @date 2017-04-16
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigTemplateManageAction extends BaseAction {
	/**
	 * 模板列表
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "模板列表", value = "/template_list*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping({ "/template_list" })
	public ModelAndView template_list(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/template_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage,orderBy, orderType);

		if ((type == null) || (type.equals(""))) {
			type = "msg";
		}
		
		maps.put("type", type);
		
		IPageList pList = this.templateService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/template_list.html",
				"", params, pList, mv);
		mv.addObject("type", type);
		return mv;
	}

	/**
	 * 模板添加
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "模板添加", value = "/template_add*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping({ "/template_add" })
	public ModelAndView template_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/template_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 模板编辑
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "模板编辑", value = "/template_edit*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping({ "/template_edit" })
	public ModelAndView template_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/template_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			Template template = this.templateService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", template);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}

	/**
	 * 模板保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "模板保存", value = "/template_save*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping({ "/template_save" })
	public ModelAndView template_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		
		Template template = null;
		if (id.equals("")) {
			template = (Template) WebForm.toPo(request, Template.class);
			template.setAddTime(new Date());
		} else {
			Template obj = this.templateService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			template = (Template) WebForm.toPo(request, obj);
		}
		if (template.getType().equals("sms")) {
			String content = Jsoup
					.clean(template.getContent(), Whitelist.none())
					.replace("&nbsp;", "").trim();
			template.setContent(content);
		}
		if (id.equals("")) {
			this.templateService.saveEntity(template);
		} else {
			this.templateService.updateById(template);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url + "?type=" + template.getType());
		mv.addObject("op_title", "保存模板成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 模板AJAX更新
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "模板AJAX更新", value = "/template_ajax*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping({ "/template_ajax" })
	public void template_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Template obj = this.templateService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = Template.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.templateService.updateById(obj);
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

	/**
	 * 模板开启
	 * 
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "模板开启", value = "/template_open*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping({ "/template_open" })
	public String template_open(HttpServletRequest request, String mulitId,
			String currentPage, String type) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");

		this.templateService.batchDeleteByIds(ids);
		
		
		return "redirect:template_list?currentPage=" + currentPage + "&type=" + type;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param mark
	 * @param id
	 */
	@RequestMapping({ "/template/verify_mark" })
	public void verify_mark(HttpServletRequest request,
			HttpServletResponse response, String mark, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark);
		params.put("id_no", CommUtil.null2Long(id));
		
		if (this.templateService.selectCount(params) > 0) {
			ret = false;
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
	 * 模板编辑
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "模板编辑", value = "/template_copy*", rtype = "admin", rname = "通知模板", rcode = "template_set", rgroup = "设置")
	@RequestMapping({ "/template_copy" })
	public ModelAndView template_copy(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/template_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			Template template = this.templateService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("res", "copy");
			mv.addObject("obj", template);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}
}
