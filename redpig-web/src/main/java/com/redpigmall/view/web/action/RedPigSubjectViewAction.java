package com.redpigmall.view.web.action;

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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Subject;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigSubjectViewAction.java
 * </p>
 * 
 * <p>
 * Description: 专题控制器
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
 * @date 2014-11-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("rawtypes")
@Controller
public class RedPigSubjectViewAction extends BaseAction{
	
	/**
	 * 专题首页,专题列表
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping({ "/subject/index" })
	public ModelAndView subject(HttpServletRequest request, HttpServletResponse response, String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("subject.html", 
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, 5, "sequence", "asc");
		maps.put("display", 1);
		maps.put("display", 1);
		
		IPageList pList = this.subjectService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}
	
	/**
	 * 专题详情
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/subject/view" })
	public ModelAndView subject_view(HttpServletRequest request, HttpServletResponse response, String id) {
		
		ModelAndView mv = new RedPigJModelAndView("subject_view.html", 
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Subject obj = this.subjectService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if ((obj != null) && (obj.getSubject_detail() != null)) {
			List<Map> objs = JSON.parseArray(obj.getSubject_detail(), Map.class);
			mv.addObject("objs", objs);
		}
		
		mv.addObject("obj", obj);
		mv.addObject("SubjectTools", this.SubjectTools);
		return mv;
	}
	
	/**
	 * 解析前台热点坐标
	 * @param areaInfo
	 * @param img_id
	 * @return
	 */
	@RequestMapping({ "/subject/getAreaInfo" })
	public void subject_getAreaInfo(HttpServletRequest request, HttpServletResponse response, String areaInfo,
			String img_id, String width) {
		
		Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(img_id));
		double rate = 1.0D;
		if ((img != null) && (img.getWidth() > 1640)) {
			rate = CommUtil.div(Integer.valueOf(img.getWidth()), Integer.valueOf(1640));
		}
		List<Map> maps = Lists.newArrayList();
		if ((areaInfo != null) && (!areaInfo.equals(""))) {
			String[] infos = areaInfo.split("-");

			for (String obj : infos) {

				if (!obj.equals("")) {
					Map<String, Object> map = Maps.newHashMap();
					String[] detail_infos = obj.split("==");
					detail_infos[0] = detail_infos[0].replace("_", ",");
					String coords = detail_infos[0];
					String[] nums = detail_infos[0].split(",");
					String temp_coords = "";
					for (String num : nums) {

						String coor = CommUtil
								.null2String(Long.valueOf(Math.round(CommUtil.mul(Double.valueOf(rate), num))));
						if (temp_coords.equals("")) {
							temp_coords = coor;
						} else {
							temp_coords = temp_coords + "," + coor;
						}
					}
					if (!temp_coords.equals("")) {
						coords = temp_coords;
					}
					map.put("coords", coords);

					int real_width = CommUtil.null2Int(width);
					if (img.getWidth() > real_width) {
						double rate2 = CommUtil.div(Integer.valueOf(real_width), Integer.valueOf(img.getWidth()));
						String[] temp_real_coors = temp_coords.split(",");
						String real_coors = "";
						for (String real : temp_real_coors) {

							String coor = CommUtil
									.null2String(Long.valueOf(Math.round(CommUtil.mul(Double.valueOf(rate2), real))));
							if (real_coors.equals("")) {
								real_coors = coor;
							} else {
								real_coors = real_coors + "," + coor;
							}
						}
						if (!real_coors.equals("")) {
							coords = real_coors;
						}
						map.put("coords", real_coors);
					}
					map.put("url", detail_infos[1]);
					map.put("width", Integer.valueOf(this.SubjectTools.getWidth(detail_infos[0])));
					map.put("height", Integer.valueOf(this.SubjectTools.getHeight(detail_infos[0])));
					map.put("top", Integer.valueOf(this.SubjectTools.getTop(detail_infos[0])));
					map.put("left", Integer.valueOf(this.SubjectTools.getLeft(detail_infos[0])));
					maps.add(map);
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(maps));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
