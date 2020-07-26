package com.redpigmall.manage.admin.action.website.document;

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
import com.redpigmall.domain.Document;

/**
 * 
 * <p>
 * Title: RedPigDocumentManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统文章管理类
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
public class RedPigDocumentManageAction extends BaseAction{
	
	/**
	 * 系统文章列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "系统文章列表", value = "/document_list*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
	@RequestMapping({ "/document_list" })
	public ModelAndView document_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/document_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.redPigDocumentService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/document_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 系统文章新增
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "系统文章新增", value = "/document_add*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
	@RequestMapping({ "/document_add" })
	public ModelAndView document_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/document_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 系统文章编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "系统文章编辑", value = "/document_edit*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
	@RequestMapping({ "/document_edit" })
	public ModelAndView document_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/document_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Document document = this.redPigDocumentService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			mv.addObject("obj", document);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 系统文章保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "系统文章保存", value = "/document_save*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
	@RequestMapping({ "/document_save" })
	public ModelAndView document_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String cmd, 
			String list_url, 
			String add_url) {
		
		Document document = null;
		if (id.equals("")) {
			document = (Document) WebForm.toPo(request, Document.class);
			document.setAddTime(new Date());
		} else {
			Document obj = this.redPigDocumentService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			document = (Document) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.redPigDocumentService.saveEntity(document);
		} else {
			this.redPigDocumentService.updateById(document);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存系统文章成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 系统文章删除
	 * @param request
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "系统文章删除", value = "/document_del*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
	@RequestMapping({ "/document_del" })
	public String document_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.redPigDocumentService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:document_list";
	}
	
	/**
	 * 系统文章AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "系统文章AJAX更新", value = "/document_ajax*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
	@RequestMapping({ "/document_ajax" })
	public void document_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Document obj = this.redPigDocumentService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = Document.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigDocumentService.updateById(obj);
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
