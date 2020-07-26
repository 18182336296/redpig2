package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.ip.IPSeeker;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Area;
import com.redpigmall.service.RedPigAreaService;

/**
 * 
 * <p>
 * Title: RedPigAreaViewTools.java
 * </p>
 * 
 * <p>
 * Description:区域工具类,根据id生成完整的区域信息
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
 * @date 2014-11-10
 * 
 * @version redpigmall_b2b2c 2016
 */
@Component
public class RedPigAreaViewTools {
	@Autowired
	private RedPigAreaService areaService;

	/**
	 * 根据区域生成区域信息字符串
	 * 
	 * @param area
	 * @return
	 */
	public String generic_area_info(String area_id) {
		String area_info = "";
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		if (area != null) {
			String last = area.getAreaName();
			String second = getSecond_Area_info(area_id);
			String first = getFirst_Area_info(area_id);
			area_info = first + second + last;
		}
		return area_info;
	}

	private String getSecond_Area_info(String area_id) {
		String areaName = "";
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		if ((area != null) && (area.getParent() != null)) {
			areaName = area.getParent().getAreaName();
		}
		return areaName;
	}

	private String getFirst_Area_info(String area_id) {
		String areaName = "";
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		if ((area != null) && (area.getParent() != null)) {
			areaName = area.getParent().getParent().getAreaName();
		}
		return areaName;
	}

	public void getUserAreaInfo(HttpServletRequest request, ModelAndView mv) {
		String default_city_json = CommUtil.null2String(request.getSession(
				false).getAttribute("default_city_json"));
		String default_area_id = CommUtil.null2String(request.getSession(false)
				.getAttribute("default_area_id"));
		if ((default_city_json != null) && (!default_city_json.equals(""))) {
			List<String> area_list = JSON.parseArray(default_city_json,
					String.class);
			if ((area_list != null) && (area_list.size() > 0)) {
				List<Area> default_area_list = Lists.newArrayList();
				for (Object temp_id : area_list) {
					Area area = this.areaService.selectByPrimaryKey(CommUtil
							.null2Long(temp_id));
					if (area != null) {
						default_area_list.add(area);
					}
				}
				mv.addObject("default_areas", default_area_list);
			}
		}
		if (default_area_id == null || default_area_id.equals("")
				|| default_area_id.equals("-1")) {
			default_area_id = "4521986";// 默认给个北京市的
		}
		if (!default_area_id.equals("")) {
			Area area = this.areaService.selectByPrimaryKey(CommUtil
					.null2Long(default_area_id));
			mv.addObject("city", area);
		} else {
			String current_ip = CommUtil.getIpAddr(request);
			if (current_ip != null && current_ip.equals("127.0.0.1")
					|| current_ip.equals("localhost")) {
				// 默认任意给的百度的IP地址,因为QQwry这个工具定位不了本地的地址
				current_ip = "202.108.22.5";
			}
			if (CommUtil.isIp(current_ip)) {
				IPSeeker ip = new IPSeeker(null, null);
				String current_city = ip.getIPLocation(current_ip).getCountry();
				current_city = cityNameDeal(current_city);
				Map<String,Object> maps = Maps.newHashMap();
		        maps.put("areaName_like",current_city);
		        
				List<Area> areas = this.areaService.queryPageList(maps,0,1);
				
				if (areas.size() == 1) {
					Area area = areas.get(0);
					Area area_temp = null;
					if (area.getLevel() == 1) {
						area_temp = (Area) area.getChilds().get(0);
					}
					if (area.getLevel() == 2) {
						area_temp = area;
					}
					mv.addObject("city", area_temp);
				} else {
					mv.addObject("current_city", current_city);
				}
			}
		}
	}

	public void setDefaultArea(HttpServletRequest request, String area_id) {
		String default_city_json = CommUtil.null2String(request.getSession(
				false).getAttribute("default_city_json"));
		
		List<String> area_list = Lists.newArrayList();
		if (area_id != null) {
			if (!default_city_json.equals("")) {
				area_list = JSON.parseArray(default_city_json, String.class);
				if (default_city_json.indexOf(area_id) < 0) {
					area_list.add(area_id);
					if (area_list.size() >= 2) {
						area_list = area_list.subList(area_list.size() - 2,
								area_list.size());
					}
					request.getSession(false).setAttribute("default_city_json",
							JSON.toJSONString(area_list));
				}
			} else if ((area_id != null) && (!area_id.equals(""))) {
				area_list.add(area_id);
				request.getSession(false).setAttribute("default_city_json",
						JSON.toJSONString(area_list));
			}
			if ((area_id != null) && (!area_id.equals(""))) {
				request.getSession(false).setAttribute("default_area_id",
						area_id);
			}
		}
	}

	public String getAreaIdByRequest(HttpServletRequest request) {
		String area_id = null;

		String current_ip = CommUtil.getIpAddr(request);
		if (CommUtil.isIp(current_ip)) {
			IPSeeker ip = new IPSeeker(null, null);
			String current_city = ip.getIPLocation(current_ip).getCountry();
			current_city = cityNameDeal(current_city);
			Map<String,Object> maps = Maps.newHashMap();
	        maps.put("areaName_like",current_city);
	        
			List<Area> areas = this.areaService.queryPageList(maps,0,1);
			
			if (areas.size() >= 1) {
				Area area = (Area) areas.get(0);
				Area area_temp = null;
				if (area.getLevel() == 1) {
					area_temp = (Area) area.getChilds().get(0);
				}
				if (area.getLevel() == 2) {
					area_temp = area;
				}
				area_id = area_temp.getId().toString();
			}
		}
		return area_id;
	}

	public String getAreaNameByRequest(HttpServletRequest request) {
		String area_name = null;

		String current_ip = CommUtil.getIpAddr(request);
		if (CommUtil.isIp(current_ip)) {
			IPSeeker ip = new IPSeeker(null, null);
			String current_city = ip.getIPLocation(current_ip).getCountry();
			current_city = cityNameDeal(current_city);
			Map<String,Object> maps = Maps.newHashMap();
	        maps.put("areaName_like",current_city);
	        
			List<Area> areas = this.areaService.queryPageList(maps,0,1);
			if (areas.size() >= 1) {
				Area area = (Area) areas.get(0);
				Area area_temp = null;
				if (area.getLevel() == 1) {
					area_temp = (Area) area.getChilds().get(0);
				}
				if (area.getLevel() == 2) {
					area_temp = area;
				}
				area_name = area_temp.getAreaName();
			} else {
				area_name = current_city;
			}
		}
		return area_name;
	}

	private String cityNameDeal(String current_city) {
		if (current_city.indexOf("省") > 0) {
			current_city = current_city.split("省")[1];
		}
		if (current_city.indexOf("新疆") >= 0) {
			current_city = current_city.split("新疆")[1];
		}
		if (current_city.indexOf("内蒙古") >= 0) {
			current_city = current_city.split("内蒙古")[1];
		}
		if (current_city.indexOf("广西") >= 0) {
			current_city = current_city.split("广西")[1];
		}
		if (current_city.indexOf("宁夏") >= 0) {
			current_city = current_city.split("宁夏")[1];
		}
		if (current_city.indexOf("西藏") >= 0) {
			current_city = current_city.split("西藏")[1];
		}
		return current_city;
	}

	public void getUserAreaInfoStaticHtml(
			HttpServletRequest request,
			Map<String, Object> velocityParams) {
		
		String default_city_json = CommUtil.null2String(request.getSession(
				false).getAttribute("default_city_json"));
		String default_area_id = CommUtil.null2String(request.getSession(false)
				.getAttribute("default_area_id"));
		if ((default_city_json != null) && (!default_city_json.equals(""))) {
			List<String> area_list = JSON.parseArray(default_city_json,
					String.class);
			if ((area_list != null) && (area_list.size() > 0)) {
				List<Area> default_area_list = Lists.newArrayList();
				for (Object temp_id : area_list) {
					Area area = this.areaService.selectByPrimaryKey(CommUtil
							.null2Long(temp_id));
					if (area != null) {
						default_area_list.add(area);
					}
				}
				velocityParams.put("default_areas", default_area_list);
			}
		}
		if (default_area_id == null || default_area_id.equals("")
				|| default_area_id.equals("-1")) {
			default_area_id = "4521986";// 默认给个北京市的
		}
		if (!default_area_id.equals("")) {
			Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(default_area_id));
			velocityParams.put("city", area);
		} else {
			String current_ip = CommUtil.getIpAddr(request);
			if (current_ip != null && current_ip.equals("127.0.0.1")
					|| current_ip.equals("localhost")) {
				// 默认任意给的百度的IP地址,因为QQwry这个工具定位不了本地的地址
				current_ip = "202.108.22.5";
			}
			if (CommUtil.isIp(current_ip)) {
				IPSeeker ip = new IPSeeker(null, null);
				String current_city = ip.getIPLocation(current_ip).getCountry();
				current_city = cityNameDeal(current_city);
				Map<String,Object> maps = Maps.newHashMap();
		        maps.put("areaName_like",current_city);
		        
				List<Area> areas = this.areaService.queryPageList(maps,0,1);
				
				if (areas.size() == 1) {
					Area area = areas.get(0);
					Area area_temp = null;
					if (area.getLevel() == 1) {
						area_temp = (Area) area.getChilds().get(0);
					}
					if (area.getLevel() == 2) {
						area_temp = area;
					}
					velocityParams.put("city", area_temp);
				} else {
					velocityParams.put("current_city", current_city);
				}
			}
		}
	}
}
