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
import com.redpigmall.domain.Area;

/**
 * 
 * <p>
 * Title: AreaManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 常用区域管理控制器，用来管理控制系统常用区域信息，常用区域主要用在买家添加配送地址、买家住址信息等，默认为中国大陆三级行政区域信息
 * ，平台管理员可以任意管理该信息
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
public class RedPigAreaManageAction extends BaseAction{
	/**
	 * 地区Ajax编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "地区Ajax编辑", value = "/area_ajax*", rtype = "admin", rname = "常用地区", rcode = "admin_area_set", rgroup = "设置")
	@RequestMapping({ "/area_ajax" })
	public void area_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		
		Area obj = this.redPigAreaService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		
		Field[] fields = Area.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigAreaService.update(obj);
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
	 * 地区保存
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
	@SecurityMapping(title = "地区保存", value = "/area_save*", rtype = "admin", rname = "常用地区", rcode = "admin_area_set", rgroup = "设置")
	@RequestMapping({ "/area_save" })
	public ModelAndView area_save(HttpServletRequest request,
			HttpServletResponse response, String areaId, String pid,
			String count, String list_url, String currentPage) {
		
		if (areaId != null) {
			String[] ids = areaId.split(",");
			int i = 1;
			for (String id : ids) {
				String areaName = request.getParameter("areaName_" + i);
				Area area = this.redPigAreaService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(request.getParameter("id_" + i))));
				area.setAreaName(areaName);
				area.setSequence(CommUtil.null2Int(request.getParameter("sequence_" + i)));
				
				this.redPigAreaService.update(area);
				i++;
			}
		}
		Area parent = null;
		if (!pid.equals("")) {
			parent = this.redPigAreaService.selectByPrimaryKey(Long.valueOf(Long.parseLong(pid)));
		}
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			Area area = new Area();
			area.setAddTime(new Date());
			String areaName = request.getParameter("new_areaName_" + i);
			int sequence = CommUtil.null2Int(request.getParameter("new_sequence_" + i));
			if (parent != null) {
				area.setLevel(parent.getLevel() + 1);
				area.setParent(parent);
			}
			area.setAreaName(areaName);
			area.setSequence(sequence);
			this.redPigAreaService.save(area);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("op_title", "更新区域成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/area_list"
				+ "?currentPage=" + currentPage + "&pid=" + pid);
		return mv;
	}

	/**
	 * 地区删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "地区删除", value = "/area_del*", rtype = "admin", rname = "常用地区", rcode = "admin_area_set", rgroup = "设置")
	@RequestMapping("/area_del")
	public String area_del(HttpServletRequest request, String mulitId,
			String currentPage, String pid) {
		String[] ids = mulitId.split(",");
		
		List<Long> listIds = Lists.newArrayList();
		
		for (String id : ids) {
			if (!id.equals("")) {
				genericIds(Long.parseLong(id),listIds);
			}
		}
		
		//批量删除
		this.redPigAreaService.deleteBatch(listIds);
		
		return "redirect:area_list?pid=" + pid + "&currentPage=" + currentPage;
	}
	
	private void genericIds(Long id,List<Long> listIds) {
		System.out.println("g_id="+id);
		List<Area> objs = this.redPigAreaService.queryChilds(id);
		System.out.println("objs.size="+objs.size());
		//如果子类为空开始删除
		if(objs==null || objs.size() == 0){
			listIds.add(id);
		}
		
		//如果子类不为空继续往下查询
		if(objs != null && objs.size() > 0){
			for (Area area : objs) {
				genericIds(area.getId(),listIds);
			}
		}
	}
	
	/**
	 * 地区列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param pid
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "地区列表", value = "/area_list*", rtype = "admin", rname = "常用地区", rcode = "admin_area_set", rgroup = "设置")
	@RequestMapping({ "/area_list" })
	public ModelAndView area_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/area_setting.html",
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
			maps.put("parent_id", "-1");
			maps.put("orderBy", "sequence");
			maps.put("orderType", "asc");
		} else {
			maps.put("orderBy", orderBy);
			maps.put("orderType", orderType);
			maps.put("parent_id", pid);

			params = "&pid=" + pid;
			Area parent = this.redPigAreaService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(pid)));
			mv.addObject("parent", parent);
			if (parent.getLevel() == 0) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("parent_id", parent.getId());

				List<Area> seconds = this.redPigAreaService.queryPageList(map);

				mv.addObject("seconds", seconds);
				mv.addObject("first", parent);
			}
			if (parent.getLevel() == 1) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("parent_id", parent.getId());
				List<Area> thirds = this.redPigAreaService.queryPageList(map);

				map.clear();
				map.put("parent_id", parent.getParent().getId());
				List<Area> seconds = this.redPigAreaService.queryPageList(map);

				mv.addObject("thirds", thirds);
				mv.addObject("seconds", seconds);
				mv.addObject("second", parent);
				mv.addObject("first", parent.getParent());
			}
			if (parent.getLevel() == 2) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("parent_id", parent.getParent().getId());
				List<Area> thirds = this.redPigAreaService.queryPageList(map);

				map.clear();
				map.put("parent_id", parent.getParent().getParent().getId());
				List<Area> seconds = this.redPigAreaService.queryPageList(map);

				mv.addObject("thirds", thirds);
				mv.addObject("seconds", seconds);
				mv.addObject("third", parent);
				mv.addObject("second", parent.getParent());
				mv.addObject("first", parent.getParent().getParent());
			}
		}

		IPageList pList = this.redPigAreaService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/area_list.html", "",
				params, pList, mv);
		maps = Maps.newHashMap();
		maps.put("parent_id", "-1");
		List<Area> areas = this.redPigAreaService.queryPageList(maps);

		mv.addObject("areas", areas);
		return mv;
	}
}
