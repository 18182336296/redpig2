package com.redpigmall.module.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.domain.Area;
import com.redpigmall.module.weixin.view.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigWapLoadViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端地区加载控制器
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
 * @date 2014-8-5
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinLoadViewAction extends BaseAction{
	/**
	 * 手机端地区加载
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/area_load" })
	public void area_load(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Map json_map = Maps.newHashMap();
		List area_list = Lists.newArrayList();
		List<Area> areas = null;
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		if (verify) {
			if ((id != null) && (!id.equals(""))) {
				Area parent = this.areaService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				areas = parent.getChilds();
			} else {
				areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
			}
			for (Area area : areas) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", area.getId());
				map.put("name", area.getAreaName());
				area_list.add(map);
			}
			json_map.put("area_list", area_list);
		}
		json_map.put("ret", CommUtil.null2String(Boolean.valueOf(verify)));
		String json = JSON.toJSONString(json_map);
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
	
	/**
	 * 加载地区
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
			params.put("parent", CommUtil.null2Long(pid));
			
			areas = this.areaService.queryPageList(params);
			
		} else {
			params.put("parent", -1);
			
			areas = this.areaService.queryPageList(params);
			
		}
		List list = Lists.newArrayList();
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
	 * 地区列表
	 * @param request
	 * @param response
	 * @param pid
	 * @return
	 */
	@RequestMapping({ "/area_list" })
	public ModelAndView area_list(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new RedPigJModelAndView("weixin/area_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Area> areas = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		if ((pid != null) && (!pid.equals(""))) {
			params.put("parent", CommUtil.null2Long(pid));
			
			areas = this.areaService.queryPageList(params);
			
		} else {
			params.put("parent", -1);
			
			areas = this.areaService.queryPageList(params);
			
		}
		mv.addObject("objs", areas);
		return mv;
	}
}
