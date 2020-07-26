package com.redpigmall.manage.admin.action.systemset;

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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.TransArea;

/**
 * 
 * <p>
 * Title: RedPigTransAreaManageAction.java
 * </p>
 * 
 * <p>
 * Description:运费区域管理控制器，用来管理控制系统配送区域信息，和系统常用区域不同
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
 * @date 2016-10-16
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigTransAreaManageAction extends BaseAction{
	
	
	/**
	 * 运费地区Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 */
	@SecurityMapping(title = "运费地区Ajax更新", value = "/trans_area_ajax*", rtype = "admin", rname = "运费区域", rcode = "admin_trans_area", rgroup = "设置")
	@RequestMapping({ "/trans_area_ajax" })
	public void trans_area_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String fieldName,String value)   {
		
		TransArea obj = this.redPigTransAreaService.selectByPrimaryKey(Long.valueOf(id));
		Field[] fields = TransArea.class.getDeclaredFields();
		Object val  =  ObjectUtils.setValue(fieldName, value, obj, fields);
		this.redPigTransAreaService.update(obj);
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
	 * 运费地区删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "运费地区删除", value = "/trans_area_del*", rtype = "admin", rname = "运费区域", rcode = "admin_trans_area", rgroup = "设置")
	@RequestMapping({ "/trans_area_del" })
	public String trans_area_del(HttpServletRequest request, String mulitId,String currentPage, String pid) {
		String[] ids = mulitId.split(",");

		List<Long> listIds = Lists.newArrayList();
		for (String id : ids) {
			if (!id.equals("")) {
				genericIds(Long.valueOf(id), listIds);

			}
		}

		this.redPigTransAreaService.deleteByIds(listIds);

		return "redirect:trans_area_list?pid=" + pid + "&currentPage="+ currentPage;
	}

	private void genericIds(Long id, List<Long> listIds) {
		TransArea transArea = this.redPigTransAreaService.selectByPrimaryKey(id);
		if (transArea != null && transArea.getChilds() != null){
			listIds.add(transArea.getId());
		}
		
		if (transArea != null && transArea.getChilds() != null && transArea.getChilds().size() > 0) {
			genericIds(transArea.getId(), listIds);
		}
	}
	  
	/**
	 * 运费地区保存
	 * @param request
	 * @param response
	 * @param areaId
	 * @param pid
	 * @param count
	 * @param list_url
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("unused")
	@SecurityMapping(title = "运费地区保存", value = "/trans_area_save*", rtype = "admin", rname = "运费区域", rcode = "admin_trans_area", rgroup = "设置")
	@RequestMapping({ "/trans_area_save" })
	public ModelAndView trans_area_save(HttpServletRequest request,
			HttpServletResponse response, String areaId, String pid,
			String count, String list_url, String currentPage) {
		if (areaId != null) {
			String[] ids = areaId.split(",");
			int i = 1;
			for (String id : ids) {
				String areaName = request.getParameter("areaName_" + i);
				TransArea area = this.redPigTransAreaService
						.selectByPrimaryKey(Long.parseLong(request
								.getParameter("id_" + i)));
				area.setAreaName(areaName);
				area.setSequence(CommUtil.null2Int(request
						.getParameter("sequence_" + i)));
				this.redPigTransAreaService.update(area);
				i++;
			}
		}
		TransArea parent = null;
		if (!pid.equals("")) {
			parent = this.redPigTransAreaService.selectByPrimaryKey(Long
					.parseLong(pid));
		}
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			TransArea area = new TransArea();
			area.setAddTime(new Date());
			String areaName = request.getParameter("new_areaName_" + i);
			int sequence = CommUtil.null2Int(request
					.getParameter("new_sequence_" + i));
			if (parent != null) {
				area.setLevel(parent.getLevel() + 1);
				area.setParent(parent);
			}
			area.setAreaName(areaName);
			area.setSequence(sequence);
			this.redPigTransAreaService.save(area);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("op_title", "更新配送区域成功");
		mv.addObject("list_url", list_url + "?currentPage=" + currentPage
				+ "&pid=" + pid);
		return mv;
	}
	
	/**
	 * 运费地区列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param pid
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "运费地区列表", value = "/trans_area_list*", rtype = "admin", rname = "运费区域", rcode = "admin_trans_area", rgroup = "设置")
	@RequestMapping({ "/trans_area_list" })
	public ModelAndView trans_area_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/trans_area_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		

		if ((pid == null) || (pid.equals(""))) {
			maps.put("orderBy", orderBy);
			maps.put("orderType", orderType);
			maps.put("parent_id", -1);

		} else {
			maps.put("orderBy", orderBy);
			maps.put("orderType", orderType);
			maps.put("parent_id", pid);

			params = "&pid=" + pid;
			TransArea parent = this.redPigTransAreaService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(pid)));
			mv.addObject("parent", parent);
			if (parent.getLevel() == 0) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("parent_id", parent.getId());
				List<TransArea> seconds = this.redPigTransAreaService.queryPageList(map);
				mv.addObject("seconds", seconds);
				mv.addObject("first", parent);
			}
			if (parent.getLevel() == 1) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("parent_id", parent.getId());
				List<TransArea> thirds = this.redPigTransAreaService
						.queryPageList(map);
				map.clear();
				map.put("parent_id", parent.getParent().getId());
				List<TransArea> seconds = this.redPigTransAreaService
						.queryPageList(map);
				mv.addObject("thirds", thirds);
				mv.addObject("seconds", seconds);
				mv.addObject("second", parent);
				mv.addObject("first", parent.getParent());
			}
			if (parent.getLevel() == 2) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("parent_id", parent.getParent().getId());
				List<TransArea> thirds = this.redPigTransAreaService
						.queryPageList(map);
				map.clear();
				map.put("parent_id", parent.getParent().getParent().getId());
				List<TransArea> seconds = this.redPigTransAreaService
						.queryPageList(map);
				mv.addObject("thirds", thirds);
				mv.addObject("seconds", seconds);
				mv.addObject("third", parent);
				mv.addObject("second", parent.getParent());
				mv.addObject("first", parent.getParent().getParent());
			}
		}

		maps.put("orderBy", "sequence");
		maps.put("orderType", "asc");

		IPageList pList = this.redPigTransAreaService.list(maps);
		CommUtil.saveIPageList2ModelAndView(
				url + "/trans_area_list.html", "", params, pList, mv);
		maps.clear();
		maps.put("parent_id", -1);

		List<TransArea> areas = this.redPigTransAreaService.queryPageList(maps);
		mv.addObject("areas", areas);
		return mv;
	}

}
