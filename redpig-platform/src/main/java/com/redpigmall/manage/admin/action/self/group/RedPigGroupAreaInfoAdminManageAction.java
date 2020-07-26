package com.redpigmall.manage.admin.action.self.group;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.GroupAreaInfo;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: GroupAreaInfoAdminManageAction.java
 * </p>
 * 
 * <p>
 * Description: 生活购常用地址管理类
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
 * @date 2014-9-16
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigGroupAreaInfoAdminManageAction extends BaseAction{
	/**
	 * 生活购常用地址列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "生活购常用地址列表", value = "/groupareainfo_list*", rtype = "admin", rname = "生活购常用地址管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/groupareainfo_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/groupareainfo_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("gai_type", 0);
		IPageList pList = this.groupareainfoService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/seller/groupareainfo_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 生活购常用地址添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "生活购常用地址添加", value = "/groupareainfo_add*", rtype = "admin", rname = "生活购常用地址管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/groupareainfo_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/groupareainfo_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("level", 0);
		params.put("parent", -1);
		
		List<Area> arealist = this.areaService.queryPageList(params);
		
		mv.addObject("currentPage", currentPage);
		mv.addObject("objs", arealist);
		return mv;
	}
	
	/**
	 * 生活购常用地址编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "生活购常用地址编辑", value = "/groupareainfo_edit*", rtype = "admin", rname = "生活购常用地址管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/groupareainfo_edit" })
	public ModelAndView groupareainfo_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/groupareainfo_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			GroupAreaInfo groupareainfo = this.groupareainfoService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			Map<String, Object> params = Maps.newHashMap();
			params.put("level", 0);
			params.put("parent", -1);
			
			List<Area> arealist = this.areaService.queryPageList(params);
			mv.addObject("obj", groupareainfo);
			mv.addObject("objs", arealist);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 生活购常用地址保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param area2
	 * @param lng
	 * @param lat
	 * @param gai_name
	 * @return
	 */
	@SecurityMapping(title = "生活购常用地址保存", value = "/groupareainfo_save*", rtype = "admin", rname = "生活购常用地址管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/groupareainfo_save" })
	public String groupareainfo_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String area2,
			String lng, String lat, String gai_name) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		GroupAreaInfo groupareainfo = null;
		if (id.equals("")) {
			groupareainfo = new GroupAreaInfo();
			groupareainfo.setAddTime(new Date());
			groupareainfo.setArea(this.areaService.selectByPrimaryKey(CommUtil.null2Long(area2)));
			groupareainfo.setDeleteStatus(0);
			groupareainfo.setGai_lng(CommUtil.null2Double(lng));
			groupareainfo.setGai_lat(CommUtil.null2Double(lat));
			groupareainfo.setGai_name(gai_name);
			groupareainfo.setGai_type(0);
			this.groupareainfoService.saveEntity(groupareainfo);
		} else {
			GroupAreaInfo obj = this.groupareainfoService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			if (area2 != null) {
				if(obj.getArea() != null) {
					System.out.println(obj.getArea().getId());
				}
				obj.setArea(this.areaService.selectByPrimaryKey(CommUtil.null2Long(area2)));
			}
			if (lat != null) {
				obj.setGai_lat(CommUtil.null2Double(lat));
			}
			if (lng != null) {
				obj.setGai_lng(CommUtil.null2Double(lng));
			}
			if (gai_name != null) {
				obj.setGai_name(gai_name);
			}
			this.groupareainfoService.updateById(obj);
		}
		return "redirect:groupareainfo_list?currentPage=" + currentPage;
	}
	
	/**
	 * 生活购常用地址删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "生活购常用地址删除", value = "/groupareainfo_del*", rtype = "admin", rname = "生活购常用地址管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/groupareainfo_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				
				this.groupareainfoService.deleteById(Long.valueOf(Long
						.parseLong(id)));
			}
		}
		
		this.groupareainfoService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		
		return "redirect:groupareainfo_list?currentPage=" + currentPage;
	}
	
	/**
	 * 生活购编辑获取下一级地址
	 * @param request
	 * @param response
	 * @param areaId
	 */
	@SecurityMapping(title = "生活购编辑获取下一级地址", value = "/getAreaById*", rtype = "admin", rname = "生活购常用地址管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/getAreaById" })
	public void getAreaById(HttpServletRequest request,
			HttpServletResponse response, String areaId) {
		Map<String, Object> params = Maps.newHashMap();
		List list = Lists.newArrayList();
		params.put("parent_id", CommUtil.null2Long(areaId));
		
		List<Area> arealist = this.areaService.queryPageList(params);
		
		if ((arealist != null) && (arealist.size() > 0)) {
			for (Area area : arealist) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", area.getId());
				map.put("areaName", area.getAreaName());
				list.add(map);
			}
		}
		String json = JSON.toJSONString(list);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
