package com.redpigmall.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.CircleClass;

/**
 * 
 * <p>
 * Title: RedPigCircleClassManageAction.java
 * </p>
 * 
 * <p>
 * Description:圈子类型管理类
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCircleClassManageAction extends BaseAction {

	/**
	 * 圈子类型列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/circleclass_list*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping({ "/circleclass_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/circleclass_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = this.redPigQueryTools.getParams(
				currentPage, "sequence", "asc");

		IPageList pList = this.circleclassService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}

	/**
	 * 圈子类型列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/circleclass_add*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping({ "/circleclass_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/circleclass_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}

	/**
	 * 圈子类型列表
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/circleclass_edit*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping({ "/circleclass_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/circleclass_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			CircleClass circleclass = this.circleclassService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", circleclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}

	/**
	 * 圈子类型列表
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/circleclass_save*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping({ "/circleclass_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		CircleClass circleclass = null;
		if (id.equals("")) {
			circleclass = (CircleClass) WebForm
					.toPo(request, CircleClass.class);
			circleclass.setAddTime(new Date());
		} else {
			CircleClass obj = this.circleclassService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			circleclass = (CircleClass) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.circleclassService.saveEntity(circleclass);
		} else {
			this.circleclassService.updateById(circleclass);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", "/circleclass_list");
		mv.addObject("add_url", "/circleclass_add");
		mv.addObject("op_title", "类型保存成功");
		return mv;
	}

	/**
	 * 圈子类型列表
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/circleclass_del*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping({ "/circleclass_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.circleclassService.deleteById(Long.valueOf(Long
						.parseLong(id)));
			}
		}
		return "redirect:circleclass_list?currentPage=" + currentPage;
	}

	/**
	 * 圈子类型列表
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "圈子类型列表", value = "/circleclass_ajax*", rtype = "admin", rname = "圈子类型", rcode = "circle_class_admin", rgroup = "网站")
	@RequestMapping({ "/circleclass_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		CircleClass obj = this.circleclassService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		if (fieldName.equals("recommend")) {
			if (obj.getRecommend() == 1) {
				obj.setRecommend(0);
			} else {
				obj.setRecommend(1);
			}
		}
		if (fieldName.equals("nav_index")) {
			if (obj.getNav_index() == 1) {
				obj.setNav_index(0);
			} else {
				obj.setNav_index(1);
			}
		}
		this.circleclassService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			if (fieldName.equals("recommend")) {
				writer.print(obj.getRecommend());
			} else {
				writer.print(obj.getNav_index());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
