package com.redpigmall.manage.admin.action.operative.groupFloor;

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
import com.redpigmall.domain.GroupClass;
import com.redpigmall.domain.GroupFloor;

/**
 * 
 * <p>
 * Title: RedPigGroupFloorManageAction.java
 * </p>
 * 
 * <p>
 * Description: 团购楼层
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
 * @date 2014-5-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigGroupFloorManageAction extends BaseAction {
	
	/**
	 * 团购楼层列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "团购楼层列表", value = "/groupfloor_list*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "装修")
	@RequestMapping({ "/groupfloor_list" })
	public ModelAndView groupfloor_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/groupfloor_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.redPigGroupfloorService.list(maps);
		CommUtil.saveIPageList2ModelAndView(
				url + "/groupfloor_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 团购楼层添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购楼层添加", value = "/groupfloor_add*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "装修")
	@RequestMapping({ "/groupfloor_add" })
	public ModelAndView groupfloor_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/groupfloor_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("currentPage", currentPage);
		Map<String,Object> params = Maps.newHashMap();
		params.put("gc_level", 0);
		List<GroupClass> groupClass_list = this.redPigGroupClassService.queryPageList(params);
		mv.addObject("groupClass_list", groupClass_list);
		return mv;
	}
	
	/**
	 * 团购楼层编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购楼层编辑", value = "/groupfloor_edit*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "装修")
	@RequestMapping({ "/groupfloor_edit" })
	public ModelAndView groupfloor_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/groupfloor_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			GroupFloor groupfloor = this.redPigGroupfloorService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", groupfloor);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
			Map<String,Object> params = Maps.newHashMap();
			params.put("gc_level", 0);
			List<GroupClass> groupClass_list = this.redPigGroupClassService.queryPageList(params);
			
			mv.addObject("groupClass_list", groupClass_list);
		}
		return mv;
	}
	
	/**
	 * 团购楼层保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param gf_gc_id
	 * @return
	 */
	@SecurityMapping(title = "团购楼层保存", value = "/groupfloor_save*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "装修")
	@RequestMapping({ "/groupfloor_save" })
	public ModelAndView groupfloor_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String cmd, 
			String list_url, 
			String add_url, 
			String gf_gc_id) {
		
		GroupFloor groupfloor = null;
		if (id.equals("")) {
			groupfloor = (GroupFloor) WebForm.toPo(request, GroupFloor.class);
			groupfloor.setAddTime(new Date());
		} else {
			GroupFloor obj = this.redPigGroupfloorService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			groupfloor = (GroupFloor) WebForm.toPo(request, obj);
		}
		if ((gf_gc_id != null) && (!"".equals(gf_gc_id))) {
			groupfloor.setGf_group_class_id(CommUtil.null2Long(gf_gc_id));
		}
		if (id.equals("")) {
			this.redPigGroupfloorService.saveEntity(groupfloor);
		} else {
			this.redPigGroupfloorService.updateById(groupfloor);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存团购楼层成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 团购楼层删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购楼层删除", value = "/groupfloor_del*", rtype = "admin", rname = "团购管理", rcode = "group", rgroup = "装修")
	@RequestMapping({ "/groupfloor_del" })
	public String groupfloor_deleteById(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.redPigGroupfloorService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:groupfloor_list?currentPage=" + currentPage;
	}
	
	/**
	 * 团购楼层ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "团购楼层ajax更新", value = "/groupfloor_ajax*", rtype = "admin", rname = "团购设置", rcode = "group", rgroup = "装修")
	@RequestMapping({ "/groupfloor_ajax" })
	public void groupfloor_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String fieldName,
			String value) throws ClassNotFoundException {
		
		GroupFloor obj = this.redPigGroupfloorService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = GroupFloor.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.redPigGroupfloorService.updateById(obj);
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
