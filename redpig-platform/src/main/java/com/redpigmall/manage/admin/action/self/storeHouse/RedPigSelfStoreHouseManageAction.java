package com.redpigmall.manage.admin.action.self.storeHouse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.StoreHouse;
/**
 * 
 * <p>
 * Title: RedPigSelfStoreHouseManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营库房管理
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
 * @date 2014年4月25日
 * 
 * @version redpig
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigSelfStoreHouseManageAction extends BaseAction{
	/**
	 * 库房列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param store_house_name
	 * @return
	 */
	@SecurityMapping(title = "库房列表", value = "/store_house_list*", rtype = "admin", rname = "仓库管理", rcode = "admin_store_house", rgroup = "自营")
	@RequestMapping({ "/store_house_list" })
	public ModelAndView store_house_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String store_house_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_house_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "sh_sequence", "asc");
		
		if (!CommUtil.null2String(store_house_name).equals("")) {
			params.put("sh_name_like", store_house_name);
			
			mv.addObject("store_house_name", store_house_name);
		}
		
		IPageList pList = this.storehouseService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 库房添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "库房添加", value = "/store_house_add*", rtype = "admin", rname = "仓库管理", rcode = "admin_store_house", rgroup = "自营")
	@RequestMapping({ "/store_house_add" })
	public ModelAndView store_house_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_house_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		
		List<Area> list = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", list);
		return mv;
	}
	
	/**
	 * 库房编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "库房编辑", value = "/store_house_edit*", rtype = "admin", rname = "仓库管理", rcode = "admin_store_house", rgroup = "自营")
	@RequestMapping({ "/store_house_edit" })
	public ModelAndView store_house_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/store_house_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String str = "";
		String str2 = "";
		if ((id != null) && (!id.equals(""))) {
			StoreHouse storehouse = this.storehouseService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			mv.addObject("obj", storehouse);
			if (storehouse.getSh_area() != null) {
				List<Map> list = JSON.parseArray(storehouse.getSh_area(),
						Map.class);
				Set set = Sets.newHashSet();
				for (Map map : list) {
					str = str + map.get("id") + ",";
					Area area = this.areaService.selectByPrimaryKey(CommUtil
							.null2Long(map.get("id")));
					if (!set.contains(area.getParent().getId())) {
						set.add(area.getParent().getId());
						str2 = str2 + area.getParent().getAreaName() + " ";
					}
				}
			}
			mv.addObject("area_ids", str);
			mv.addObject("chosen_area", str2);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		
		List<Area> list = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		mv.addObject("areas", list);
		return mv;
	}
	
	/**
	 * 库房保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param status
	 * @param area_ids
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "库房保存", value = "/store_house_save*", rtype = "admin", rname = "仓库管理", rcode = "admin_store_house", rgroup = "自营")
	@RequestMapping({ "/store_house_save" })
	public ModelAndView store_house_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String status,
			String area_ids) {
		
		StoreHouse storehouse = null;
		if (id.equals("")) {
			storehouse = (StoreHouse) WebForm.toPo(request, StoreHouse.class);
			storehouse.setAddTime(new Date());
		} else {
			StoreHouse obj = this.storehouseService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			storehouse = (StoreHouse) WebForm.toPo(request, obj);
		}
		if (CommUtil.null2Boolean(status)) {
			storehouse.setSh_status(1);
		} else {
			storehouse.setSh_status(0);
		}
		if ((area_ids != null) && (!area_ids.equals(""))) {
			Set set = Sets.newHashSet();
			List<Map> list = Lists.newArrayList();

			for (String str : area_ids.split(",")) {

				Area area = this.areaService
						.selectByPrimaryKey(CommUtil.null2Long(str));
				if (!set.contains(area.getId())) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("id", area.getId());
					map.put("area_name", area.getAreaName());
					list.add(map);
				}
			}
			storehouse.setSh_area(JSON.toJSONString(list));
		}
		if (id.equals("")) {
			this.storehouseService.saveEntity(storehouse);
		} else {
			this.storehouseService.updateById(storehouse);
		}
		this.inventoryTools.async_updateInventory(storehouse.getId(),storehouse.getSh_name());
		
		System.out.println("外部方法" + CommUtil.formatLongDate(new Date()));
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存仓库成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 库房删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "库房删除", value = "/store_house_del*", rtype = "admin", rname = "仓库管理", rcode = "admin_store_house", rgroup = "自营")
	@RequestMapping({ "/store_house_del" })
	public String store_house_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId != null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		
		this.storehouseService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		return "redirect:/store_house_list?currentPage=" + currentPage;
	}
	
	/**
	 * 库房Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "库房Ajax更新", value = "/store_house_del*", rtype = "admin", rname = "仓库管理", rcode = "admin_store_house", rgroup = "自营")
	@RequestMapping({ "/store_house_ajax" })
	public void store_house_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		StoreHouse obj = this.storehouseService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = StoreHouse.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.storehouseService.updateById(obj);
		if (fieldName.equals("sh_name")) {
			this.inventoryTools.async_updateInventory(CommUtil.null2Long(id),
					value);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(CommUtil.null2String(val));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
