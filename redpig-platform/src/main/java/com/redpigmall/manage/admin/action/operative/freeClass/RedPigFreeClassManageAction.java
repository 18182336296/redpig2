package com.redpigmall.manage.admin.action.operative.freeClass;

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
import com.redpigmall.domain.FreeClass;
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigFreeClassManageAction.java
 * </p>
 * 
 * <p>
 * Description: 添加0元试用商品的分类
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
 * @date 2014年11月12日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigFreeClassManageAction extends BaseAction{

	/**
	 * 0元试用分类列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "0元试用分类列表", value = "/freeclass_list*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping({ "/freeclass_list" })
	public ModelAndView freeclass_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/freeclass_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		
		IPageList pList = this.redPigFreeclassService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/freeclass_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 0元试用分类添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用分类添加", value = "/freeclass_add*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping({ "/freeclass_add" })
	public ModelAndView freeclass_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/freeclass_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 0元试用分类编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用分类编辑", value = "/freeclass_edit*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping({ "/freeclass_edit" })
	public ModelAndView freeclass_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/freeclass_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			FreeClass freeclass = this.redPigFreeclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			mv.addObject("obj", freeclass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 0元试用分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "0元试用分类保存", value = "/freeclass_save*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping({ "/freeclass_save" })
	public ModelAndView freeclass_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String cmd, 
			String list_url, 
			String add_url) {
		
		FreeClass freeclass = null;
		if (id.equals("")) {
			freeclass = (FreeClass) WebForm.toPo(request, FreeClass.class);
			freeclass.setAddTime(new Date());
		} else {
			FreeClass obj = this.redPigFreeclassService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			freeclass = (FreeClass) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.redPigFreeclassService.saveEntity(freeclass);
		} else {
			this.redPigFreeclassService.updateById(freeclass);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/freeclass_list");
		mv.addObject("op_title", "保存分类成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/freeclass_add" + "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 0元试用分类删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用分类删除", value = "/freeclass_del*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping({ "/freeclass_del" })
	public String freeclass_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		if (mulitId != null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {

			if (!id.equals("")) {
				this.redPigFreeclassService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:freeclass_list?currentPage=" + currentPage;
	}
	
	/**
	 * 0元试用分类ajax
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "0元试用分类ajax", value = "/freeclass_ajax*", rtype = "admin", rname = "0元试用分类", rcode = "freeclass_admin", rgroup = "运营")
	@RequestMapping({ "/freeclass_ajax" })
	public void freeclass_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String fieldName,
			String value) throws ClassNotFoundException {
		
		FreeClass obj = this.redPigFreeclassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = FreeClass.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigFreeclassService.updateById(obj);
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

	@RequestMapping({ "/verify_freeclass_name" })
	public void verify_freeclass_name(HttpServletRequest request,
			HttpServletResponse response, String className, String id) {
		boolean ret = true;
		System.out.println("a");
		Map<String, Object> params = Maps.newHashMap();
		params.put("className", className);
		params.put("id_no_equal", CommUtil.null2Long(id));
		List<FreeClass> fcs = this.redPigFreeclassService.queryPageList(params);
		
		if ((fcs != null) && (fcs.size() > 0)) {
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
}
