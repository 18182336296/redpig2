package com.redpigmall.view.web.action.base;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.GroupArea;

/**
 * 
 * <p>
 * Title: RedPigLoadAction.java
 * </p>
 * 
 * <p>
 * Description:系统ajax数据加载控制器
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
 * @date 2014-4-30
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "rawtypes" })
@Controller
public class RedPigLoadAction extends BaseAction{
	
	/**
	 * 根据父id加载下级区域，返回json格式数据，这里只返回id和areaName，根据需要可以修改返回数据
	 * 
	 * @param request
	 * @param response
	 * @param pid
	 */
	@RequestMapping({ "/load_area" })
	public void load_area(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		List<Area> areas = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		if ((pid != null) && (!pid.equals(""))) {
			params.put("parent_id", CommUtil.null2Long(pid));
			
			areas = this.areaService.queryPageList(params);
			
		} else {
			params.put("parent", -1);
			areas = this.areaService.queryPageList(params);
		}
		List<Map> list = Lists.newArrayList();
		for (Area area : areas) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", area.getId());
			map.put("level", Integer.valueOf(area.getLevel()));
			map.put("areaName", area.getAreaName());
			list.add(map);
		}
		String temp = JSON.toJSONString(list);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载团购区域
	 * @param request
	 * @param response
	 * @param pid
	 */
	@RequestMapping({ "/load_group_area" })
	public void load_group_area(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent_id", CommUtil.null2Long(pid));
		
		List<GroupArea> areas = this.groupAreaService.queryPageList(params);
		
		List<Map> list = Lists.newArrayList();
		for (GroupArea area : areas) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", area.getId());
			map.put("areaName", area.getGa_name());
			list.add(map);
		}
		String temp = JSON.toJSONString(list);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
