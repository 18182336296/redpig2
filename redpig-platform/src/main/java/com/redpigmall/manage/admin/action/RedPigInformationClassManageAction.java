package com.redpigmall.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
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
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Information;
import com.redpigmall.domain.InformationClass;
import com.redpigmall.domain.InformationReply;

/**
 * 
 * <p>
 * Title: RedPigInformationClassManageAction.java
 * </p>
 * 
 * <p>
 * Description:资讯分类管理控制器，用来操作资讯分类信息，资讯分类使用两级管理
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
 * @date 2015-2-6
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigInformationClassManageAction extends BaseAction {

	/**
	 * 资讯分类列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "资讯分类列表", value = "/information_class_list*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping({ "/information_class_list" })
	public ModelAndView information_class_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_class_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, "ic_sequence", "asc");
		
		params.put("ic_pid", -1);
		IPageList pList = this.redPigInformationClassService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("cmsTools", this.cmsTools);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "资讯分类添加", value = "/information_class_add*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping({ "/information_class_add" })
	public ModelAndView information_class_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_class_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, null, null);
		params.put("ic_pid", -1);

		List<InformationClass> informationClasses = this.redPigInformationClassService
				.queryPageList(params);

		mv.addObject("informationClasses", informationClasses);
		mv.addObject("id", id);
		return mv;
	}

	/**
	 * 资讯分类编辑
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯分类编辑", value = "/information_class_edit*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping({ "/information_class_edit" })
	public ModelAndView information_class_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/information_class_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		if ((id != null) && (!id.equals(""))) {
			InformationClass informationclass = this.redPigInformationClassService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", informationclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));

			Map<String, Object> params = Maps.newHashMap();
			params.put("ic_pid", -1);

			List<InformationClass> informationClasses = this.redPigInformationClassService
					.queryPageList(params);
			mv.addObject("informationClasses", informationClasses);
		}
		return mv;
	}

	/**
	 * 资讯分类保存
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "资讯分类保存", value = "/information_class_save*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping({ "/information_class_save" })
	public ModelAndView information_class_saveEntity(
			HttpServletRequest request, HttpServletResponse response,
			String id, String currentPage, String cmd, String list_url,
			String add_url, String pid) {

		InformationClass informationclass = null;
		if (id.equals("")) {
			informationclass = (InformationClass) WebForm.toPo(request,
					InformationClass.class);
			informationclass.setAddTime(new Date());
		} else {
			InformationClass obj = this.redPigInformationClassService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			informationclass = (InformationClass) WebForm.toPo(request, obj);
		}
		if (!CommUtil.null2String(pid).equals("")) {
			informationclass.setIc_pid(CommUtil.null2Long(pid));
		}
		if (id.equals("")) {
			this.redPigInformationClassService.saveEntity(informationclass);
		} else {
			this.redPigInformationClassService.updateById(informationclass);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/information_class_list");
		mv.addObject("op_title", "保存资讯分类成功");
		if (add_url != null) {
			mv.addObject("add_url", CommUtil.getURL(request)
					+ "/information_class_add?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 资讯分类删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "资讯分类删除", value = "/information_class_del*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping({ "/information_class_del" })
	public String information_class_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				InformationClass informationclass = this.redPigInformationClassService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				Map<String, Object> map = Maps.newHashMap();
				map.put("ic_pid", informationclass.getId());

				List<InformationClass> informationClasses = this.redPigInformationClassService
						.queryPageList(map);

				for (InformationClass informationClass2 : informationClasses) {

					this.redPigInformationClassService
							.deleteById(informationClass2.getId());
					map.clear();
					map.put("classid", informationClass2.getId());

					List<Information> informations = this.redPigInformationService
							.queryPageList(map);

					for (Information information : informations) {
						map.clear();
						map.put("info_id", information.getId());

						List<InformationReply> informationReplies = this.redPigInformationReplyService
								.queryPageList(map);

						for (InformationReply informationReply : informationReplies) {
							this.redPigInformationReplyService
									.deleteById(informationReply.getId());
						}
						this.redPigInformationService.deleteById(information
								.getId());
					}
				}
				this.redPigInformationClassService.deleteById(Long.valueOf(Long
						.parseLong(id)));
			}
		}
		return "redirect:information_class_list?currentPage=" + currentPage;
	}

	/**
	 * 资讯分类ajax
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "资讯分类ajax", value = "/information_class_ajax*", rtype = "admin", rname = "资讯分类", rcode = "information_class_admin", rgroup = "网站")
	@RequestMapping({ "/information_class_ajax" })
	public void information_class_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		InformationClass obj = this.redPigInformationClassService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = InformationClass.class.getDeclaredFields();

		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		this.redPigInformationClassService.updateById(obj);
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
