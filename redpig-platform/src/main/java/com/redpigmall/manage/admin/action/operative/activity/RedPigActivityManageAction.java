package com.redpigmall.manage.admin.action.operative.activity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.Goods;

/**
 * 
 * <p>
 * Title: RedPigActivityManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城活动管理类
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
 * @date 2014年5月21日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigActivityManageAction extends BaseAction {
	
	/**
	 * 活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param q_ac_title
	 * @param ac_status
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "活动列表", value = "/activity_list*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_list" })
	public ModelAndView activity_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String q_ac_title, 
			String ac_status,
			String beginTime, 
			String endTime) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/activity_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "ac_sequence", "asc");
		
		if (!RedPigCommonUtil.null2String(q_ac_title).equals("")) {
			params.put("ac_title_like", q_ac_title.trim());
		}
		
		if (!RedPigCommonUtil.null2String(ac_status).equals("")) {
			params.put("ac_status", RedPigCommonUtil.null2Int(ac_status));
		}
		
		if (!RedPigCommonUtil.null2String(beginTime).equals("")) {
			params.put("ac_begin_time_more_than_equal", RedPigCommonUtil.formatDate(beginTime));
		}
		
		if (!RedPigCommonUtil.null2String(endTime).equals("")) {
			params.put("ac_begin_time_less_than_equal", RedPigCommonUtil.formatDate(endTime));
		}
		
		IPageList pList = this.redPigActivityService.list(params);
		
		RedPigCommonUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("q_ac_title", q_ac_title);
		mv.addObject("ac_status", ac_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		return mv;
	}
	
	/**
	 * 活动添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "活动添加", value = "/activity_add*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_add" })
	public ModelAndView activity_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/activity_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		List<Map> level_list = this.redPigIntegralViewTools.query_all_level();
		mv.addObject("level_list", level_list);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 活动编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "活动编辑", value = "/activity_edit*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_edit" })
	public ModelAndView activity_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/activity_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		if ((id != null) && (!id.equals(""))) {
			Activity activity = this.redPigActivityService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			List<Map> level_list = this.redPigIntegralViewTools.query_all_level();
			mv.addObject("level_list", level_list);
			if ((activity.getAc_rebate_json() != null)
					&& (!activity.getAc_rebate_json().equals(""))) {
				List<Map> maps = JSON.parseArray(activity.getAc_rebate_json(),Map.class);
				mv.addObject("maps", maps);
			}
			mv.addObject("obj", activity);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 活动保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param ac_begin_time
	 * @param ac_end_time
	 * @param rebate_date
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "活动保存", value = "/activity_save*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_save" })
	public ModelAndView activity_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String ac_begin_time, 
			String ac_end_time,
			String rebate_date) {
		
		Activity activity = null;
		if (id.equals("")) {
			activity = (Activity) WebForm.toPo(request, Activity.class);
			activity.setAddTime(new Date());
		} else {
			Activity obj = this.redPigActivityService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			activity = (Activity) WebForm.toPo(request, obj);
		}
		
		Map<String,Object> map;
		if ((rebate_date != null) && (!rebate_date.equals(""))) {
			String[] rebates = rebate_date.split("\\|");
			List list = Lists.newArrayList();
			for (String rebate : rebates) {
				if (!rebate.equals("")) {
					map = Maps.newHashMap();
					String[] strs = rebate.split("_");
					map.put("level", strs[1]);
					map.put("rebate", strs[0]);
					list.add(map);
				}
			}
			activity.setAc_rebate_json(JSON.toJSONString(list));
		}
		
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "activity";
		Map map1 = Maps.newHashMap();
		try {
			String fileName = activity.getAc_acc() == null ? "" : activity
					.getAc_acc().getName();
			map1 = CommUtil.saveFileToServer(request, "acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map1.get("fileName") != "") {
					Accessory ac_acc = new Accessory();
					ac_acc.setName(RedPigCommonUtil.null2String(map1
							.get("fileName")));
					ac_acc.setExt(RedPigCommonUtil.null2String(map1.get("mime")));
					ac_acc.setSize(BigDecimal.valueOf(RedPigCommonUtil
							.null2Double(map1.get("fileSize"))));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(RedPigCommonUtil.null2Int(map1.get("width")));
					ac_acc.setHeight(RedPigCommonUtil.null2Int(map1
							.get("height")));
					ac_acc.setAddTime(new Date());
					this.redPigAccessoryService.saveEntity(ac_acc);
					activity.setAc_acc(ac_acc);
				}
			} else if (map1.get("fileName") != "") {
				Accessory ac_acc = activity.getAc_acc();
				ac_acc.setName(RedPigCommonUtil.null2String(map1
						.get("fileName")));
				ac_acc.setExt(RedPigCommonUtil.null2String(map1.get("mime")));
				ac_acc.setSize(BigDecimal.valueOf(RedPigCommonUtil
						.null2Double(map1.get("fileSize"))));
				ac_acc.setPath(uploadFilePath + "/activity");
				ac_acc.setWidth(RedPigCommonUtil.null2Int(map1.get("width")));
				ac_acc.setHeight(RedPigCommonUtil.null2Int(map1.get("height")));
				ac_acc.setAddTime(new Date());
				this.redPigAccessoryService.updateById(ac_acc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,Object> map3 = Maps.newHashMap();
		try {
			String fileName = activity.getAc_acc3() == null ? "" : activity
					.getAc_acc3().getName();
			map3 = CommUtil.saveFileToServer(request, "acc3",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if ( map3.get("fileName") != "") {
					Accessory ac_acc = new Accessory();
					ac_acc.setName(RedPigCommonUtil.null2String(map3
							.get("fileName")));
					ac_acc.setExt(RedPigCommonUtil.null2String(map3
							.get("mime")));
					ac_acc.setSize(BigDecimal.valueOf(RedPigCommonUtil
							.null2Double(map3.get("fileSize"))));
					ac_acc.setPath(uploadFilePath + "/activity");
					ac_acc.setWidth(RedPigCommonUtil.null2Int(map3
							.get("width")));
					ac_acc.setHeight(RedPigCommonUtil.null2Int(map3
							.get("height")));
					ac_acc.setAddTime(new Date());
					this.redPigAccessoryService.saveEntity(ac_acc);
					activity.setAc_acc3(ac_acc);
				}
			} else if (map3.get("fileName") != "") {
				Accessory ac_acc = activity.getAc_acc3();
				ac_acc.setName(RedPigCommonUtil.null2String(map3
						.get("fileName")));
				ac_acc.setExt(RedPigCommonUtil.null2String(map3
						.get("mime")));
				ac_acc.setSize(BigDecimal.valueOf(RedPigCommonUtil
						.null2Double(map3.get("fileSize"))));
				ac_acc.setPath(uploadFilePath + "/activity");
				ac_acc.setWidth(RedPigCommonUtil.null2Int(map3
						.get("width")));
				ac_acc.setHeight(RedPigCommonUtil.null2Int(map3
						.get("height")));
				ac_acc.setAddTime(new Date());
				this.redPigAccessoryService.updateById(ac_acc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		activity.setAc_begin_time(RedPigCommonUtil.formatDate(ac_begin_time));
		activity.setAc_end_time(RedPigCommonUtil.formatDate(ac_end_time));
		if (id.equals("")) {
			this.redPigActivityService.saveEntity(activity);
		} else {
			this.redPigActivityService.updateById(activity);
		}
		int status = activity.getAc_status();
		if (status == 0) {
			for (ActivityGoods ag : activity.getAgs()) {
				if (ag.getAg_status() == 1) {
					ag.getAg_goods().setActivity_status(0);
					this.redPigGoodsService.updateById(ag.getAg_goods());
				}
			}
		}
		if (status == 1) {
			for (ActivityGoods ag : activity.getAgs()) {
				if (ag.getAg_status() == 1) {
					ag.getAg_goods().setActivity_status(2);
					this.redPigGoodsService.updateById(ag.getAg_goods());
				}
			}
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("list_url",
				RedPigCommonUtil.getURL(request) + "/activity_list");
		mv.addObject("op_title", "保存商城活动成功");
		mv.addObject("add_url",
				RedPigCommonUtil.getURL(request) + "/activity_add"
						+ "?currentPage=" + currentPage);
		return  mv;
	}
	
	/**
	 * 活动删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "活动删除", value = "/activity_del*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_del" })
	public String activity_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Activity activity = this.redPigActivityService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if (activity.getAgs().size() > 0) {
					for (ActivityGoods ag : activity.getAgs()) {
						ag.getAg_goods().setActivity_status(0);
						this.redPigGoodsService.updateById(ag.getAg_goods());
						this.redPigActivityGoodsService.deleteById(ag.getId());
					}
				}
				
				activity.getAgs().clear();
				
				RedPigCommonUtil.del_acc(request, activity.getAc_acc());
				RedPigCommonUtil.del_acc(request, activity.getAc_acc3());
				this.redPigActivityService.deleteById(Long.valueOf(Long
						.parseLong(id)));
			}
		}
		return "redirect:activity_list?currentPage=" + currentPage;
	}
	
	/**
	 * 活动AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "活动AJAX更新", value = "/activity_ajax*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_ajax" })
	public void activity_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String fieldName,
			String value) throws ClassNotFoundException {
		
		Activity obj = this.redPigActivityService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		
		Field[] fields = Activity.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.redPigActivityService.updateById(obj);
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
	 * 活动商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param act_id
	 * @param goods_name
	 * @param ag_status
	 * @return
	 */
	@SecurityMapping(title = "活动商品列表", value = "/activity_goods_list*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_goods_list" })
	public ModelAndView activity_goods_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String act_id, 
			String goods_name, 
			String ag_status) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/activity_goods_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		params.put("act_id", RedPigCommonUtil.null2Long(act_id));
		
		if (!RedPigCommonUtil.null2String(ag_status).equals("")) {
			params.put("ag_status", RedPigCommonUtil.null2Int(ag_status));
		}else{
			params.put("ag_status",0);
		}
		
		if (!RedPigCommonUtil.null2String(goods_name).equals("")) {
			params.put("ag_goods_goods_name", goods_name.trim());
		}
		
		IPageList pList = this.redPigActivityGoodsService.list(params);
		
		RedPigCommonUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ag_status", ag_status);
		mv.addObject("goods_name", goods_name);
		mv.addObject("act_id", act_id);
		return mv;
	}
	
	/**
	 * 活动通过
	 * @param request
	 * @param response
	 * @param act_id
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "活动通过", value = "/activity_goods_audit*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_goods_audit" })
	public String activity_goods_audit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String act_id, String mulitId,
			String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ActivityGoods ac = this.redPigActivityGoodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				ac.setAg_status(1);
				this.redPigActivityGoodsService.updateById(ac);
				Goods goods = ac.getAg_goods();
				goods.setActivity_status(2);
				this.redPigGoodsService.updateById(goods);
			}
		}
		return "redirect:activity_goods_list?act_id=" + act_id
				+ "&currentPage=" + currentPage;
	}
	
	/**
	 * 活动拒绝
	 * @param request
	 * @param response
	 * @param act_id
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "活动拒绝", value = "/activity_goods_refuse*", rtype = "admin", rname = "活动管理", rcode = "activity_admin", rgroup = "运营")
	@RequestMapping({ "/activity_goods_refuse" })
	public String activity_goods_refuse(
			HttpServletRequest request,
			HttpServletResponse response, 
			String act_id, 
			String mulitId,
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				ActivityGoods ac = this.redPigActivityGoodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				ac.setAg_status(-1);
				this.redPigActivityGoodsService.updateById(ac);

				Goods goods = ac.getAg_goods();
				goods.setActivity_status(0);
				this.redPigGoodsService.updateById(goods);
			}
		}
		return "redirect:activity_goods_list?act_id=" + act_id
				+ "&currentPage=" + currentPage;
	}
}
