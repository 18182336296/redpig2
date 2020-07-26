package com.redpigmall.manage.admin.action.systemset;

import java.io.File;
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

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.StoreGrade;

/**
 * 
 * <p>
 * Title: StoreGradeManageAction.java
 * </p>
 * 
 * <p>
 * Description:店铺类型管理控制器，用来管理商城店铺类型信息
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
public class RedPigStoreGradeManageAction extends BaseAction{
	
	/**
	 * 店铺类型列表
	 * @param request
	 * @param response
	 * @param q_gradeName
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "店铺类型列表", value = "/storegrade_list*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping({ "/storegrade_list" })
	public ModelAndView storegrade_list(HttpServletRequest request,
			HttpServletResponse response, String q_gradeName,
			String currentPage, String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_grade_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		
		if (!CommUtil.null2String(q_gradeName).equals("")) {
			maps.put("gradeName_like", q_gradeName);
		}
		
		IPageList pList = this.storegradeService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		return mv;
	}
	
	/**
	 * 店铺类型添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "店铺类型添加", value = "/storegrade_add*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping({ "/storegrade_add" })
	public ModelAndView storegrade_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_grade_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 店铺类型编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "店铺类型编辑", value = "/storegrade_edit*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping({ "/storegrade_edit" })
	public ModelAndView storegrade_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_grade_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			StoreGrade storegrade = this.storegradeService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", storegrade);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 店铺类型保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param main_limit
	 * @param add_funciton_ck
	 * @param goods_audit
	 * @return
	 */
	@SecurityMapping(title = "店铺类型保存", value = "/storegrade_save*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping({ "/storegrade_save" })
	public ModelAndView storegrade_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String main_limit,
			String add_funciton_ck, String goods_audit) {
		
		StoreGrade storegrade = null;
		if (id.equals("")) {
			storegrade = (StoreGrade) WebForm.toPo(request, StoreGrade.class);
			storegrade.setAddTime(new Date());
		} else {
			StoreGrade obj = this.storegradeService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			storegrade = (StoreGrade) WebForm.toPo(request, obj);
		}
		if (!CommUtil.null2Boolean(add_funciton_ck)) {
			storegrade.setAdd_funciton(null);
		}
		storegrade.setMain_limit(CommUtil.null2Int(main_limit));
		storegrade.setGoods_audit(CommUtil.null2Int(goods_audit));
		if (id.equals("")) {
			this.storegradeService.saveEntity(storegrade);
		} else {
			this.storegradeService.updateById(storegrade);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺类型成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 店铺类型删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "店铺类型删除", value = "/storegrade_del*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping({ "/storegrade_del" })
	public String storegrade_del(HttpServletRequest request, String mulitId,
			String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {
			if (!id.equals("")) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("grade_id", Long.valueOf(Long.parseLong(id)));
				int c = this.storeService.selectCount(map);
				if (c <= 0) {
					this.storegradeService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		
		return "redirect:storegrade_list?currentPage=" + currentPage;
	}
	
	/**
	 * 店铺类型AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "店铺类型AJAX更新", value = "/storegrade_ajax*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping({ "/storegrade_ajax" })
	public void storegrade_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		StoreGrade obj = this.storegradeService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = StoreGrade.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.storegradeService.updateById(obj);
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
	 * 店铺类型模板设置
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "店铺类型模板设置", value = "/storegrade_template*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping({ "/storegrade_template" })
	public ModelAndView storegrade_template(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/store_grade_template.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("path", request.getSession().getServletContext()
				.getRealPath("/"));
		mv.addObject("obj", this.storegradeService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id))));
		mv.addObject("separator", File.separator);
		return mv;
	}
	
	/**
	 * 店铺类型模板保存
	 * @param request
	 * @param response
	 * @param list_url
	 * @param id
	 * @param templates
	 * @return
	 */
	@SecurityMapping(title = "店铺类型模板保存", value = "/storegrade_template_save*", rtype = "admin", rname = "店铺类型", rcode = "store_grade", rgroup = "店铺")
	@RequestMapping({ "/storegrade_template_save" })
	public ModelAndView storegrade_template_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String list_url, String id,
			String templates) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreGrade grade = this.storegradeService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		grade.setTemplates(templates);
		this.storegradeService.updateById(grade);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存店铺类型模板成功");
		return mv;
	}
}
