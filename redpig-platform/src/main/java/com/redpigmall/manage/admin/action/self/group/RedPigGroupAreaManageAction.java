package com.redpigmall.manage.admin.action.self.group;

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
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.GroupArea;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupLifeGoods;

/**
 * 
 * <p>
 * Title: RedPigGroupAreaManageAction.java
 * </p>
 * 
 * <p>
 * Description: 团购区域管理类
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
public class RedPigGroupAreaManageAction extends BaseAction{
	/**
	 * 团购区域列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "团购区域列表", value = "/group_area_list*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_area_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_area_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "ga_sequence", "asc");
		maps.put("parent", -1);
		
		IPageList pList = this.groupareaService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView(
				url + "/group_area_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 团购区域增加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "团购区域增加", value = "/group_area_add*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_area_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_area_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupArea parent = this.groupareaService.selectByPrimaryKey(CommUtil
				.null2Long(pid));
		
		GroupArea obj = new GroupArea();
		obj.setParent(parent);
		
		List<GroupArea> gas = this.groupareaService.queryPageList(RedPigMaps.newParent(-1));
		mv.addObject("gas", gas);
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 团购区域编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购区域编辑", value = "/group_area_edit*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_area_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_area_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			GroupArea grouparea = this.groupareaService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			
			Map<String,Object> maps = Maps.newHashMap();
			maps.put("parent", -1);
			
			List<GroupArea> gas = this.groupareaService.queryPageList(maps);
			mv.addObject("gas", gas);
			mv.addObject("obj", grouparea);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 团购区域保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "团购区域保存", value = "/group_area_save*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_area_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String pid) {
		
		GroupArea grouparea = null;
		if (id.equals("")) {
			grouparea = (GroupArea) WebForm.toPo(request, GroupArea.class);
			grouparea.setAddTime(new Date());
		} else {
			GroupArea obj = this.groupareaService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			grouparea = (GroupArea) WebForm.toPo(request, obj);
		}
		GroupArea parent = this.groupareaService.selectByPrimaryKey(CommUtil.null2Long(pid));
		if (parent != null) {
			grouparea.setParent(parent);
			grouparea.setGa_level(parent.getGa_level() + 1);
		}
		if (id.equals("")) {
			this.groupareaService.saveEntity(grouparea);
		} else {
			this.groupareaService.updateById(grouparea);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/group_area_list");
		mv.addObject("op_title", "保存团购区域成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/group_area_add" + "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 团购区域删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购区域删除", value = "/group_area_del*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_area_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				GroupArea grouparea = this.groupareaService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				Map<String, Object> params = Maps.newHashMap();
				params.put("gg_ga_id", grouparea.getId());
				
				List<GroupGoods> groupGoods = this.groupGoodsService.queryPageList(params);
				
				for (GroupGoods gg : groupGoods) {
					gg.setGg_ga(null);
					this.groupGoodsService.updateById(gg);
				}
				
				List<GroupLifeGoods> groupLifeGoods = this.groupLifeGoodsService.queryPageList(params);
				
				for (GroupLifeGoods gg : groupLifeGoods) {
					gg.setGg_ga(null);
					this.groupLifeGoodsService.updateById(gg);
				}
				this.groupareaService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:group_area_list?currentPage=" + currentPage;
	}
	
	/**
	 * 团购区域Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "团购区域Ajax更新", value = "/group_area_ajax*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_area_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		GroupArea obj = this.groupareaService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = GroupArea.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.groupareaService.updateById(obj);
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
	 * 团购区域下级加载
	 * @param request
	 * @param response
	 * @param pid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购区域下级加载", value = "/group_area_data*", rtype = "admin", rname = "分类管理", rcode = "goods_class", rgroup = "商品")
	@RequestMapping({ "/group_area_data" })
	public ModelAndView group_area_data(HttpServletRequest request,
			HttpServletResponse response, String pid, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_area_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> map = Maps.newHashMap();
		map.put("parent", CommUtil.null2Long(pid));
		
		List<GroupArea> gas = this.groupareaService.queryPageList(map);
		
		mv.addObject("gas", gas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	@RequestMapping({ "/group_area_verify" })
	public void group_area_verify(HttpServletRequest request,
			HttpServletResponse response, String ga_name, String id, String pid) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("ga_name", ga_name);
		params.put("id_no", CommUtil.null2Long(id));
		params.put("parent", CommUtil.null2Long(pid));
		
		List<GroupArea> gcs = this.groupareaService.queryPageList(params);
		
		if ((gcs != null) && (gcs.size() > 0)) {
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
