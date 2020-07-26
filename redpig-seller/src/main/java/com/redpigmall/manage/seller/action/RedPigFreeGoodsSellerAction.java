package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.domain.ExpressCompanyCommon;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.FreeClass;
import com.redpigmall.domain.FreeGoods;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.ShipAddress;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigFreeGoodsSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家0元试用商品控制器
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
 * @date 2014年11月12日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigFreeGoodsSellerAction extends BaseAction{
	/**
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param id
	 */
	@RequestMapping({ "/goods_brand_verify" })
	public void goods_brand_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("name", name);
		params.put("redPig_id", CommUtil.null2Long(id));
		List<GoodsBrand> gcs = this.redPigGoodsBrandService.queryPageList(params);
		
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
	
	/**
	 * 0元试用商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param free_name
	 * @param beginTime
	 * @param endTime
	 * @param cls
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品列表", value = "/freegoods_list*", rtype = "seller", rname = "0元试用商品", rcode = "freegoods_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/freegoods_list" })
	public ModelAndView freegoods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String free_name, String beginTime,
			String endTime, String cls, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/freegoods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if (config.getWhether_free() == 1) {
			if (user.getStore().getGrade().getWhether_free() != 1) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您的店铺类型不支持0元试用功能");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
			maps.put("freeType", 0);
			maps.put("store_id", user.getStore().getId());
			
			if ((free_name != null) && (!free_name.equals(""))) {
				maps.put("free_name_like", free_name);
				mv.addObject("free_name", free_name);
			}
			
			if ((cls != null) && (!cls.equals(""))) {
				maps.put("class_id", CommUtil.null2Long(cls));
				mv.addObject("cls_id", cls);
			}
			
			if ((status != null) && (status.equals("going"))) {
				maps.put("freeStatus", Integer.valueOf(5));
				mv.addObject("status", status);
			}
			
			if ((status != null) && (status.equals("finish"))) {
				maps.put("freeStatus", Integer.valueOf(10));
				mv.addObject("status", status);
			}
			
			if ((status != null) && (status.equals("failed"))) {
				maps.put("freeStatus", Integer.valueOf(-5));
				mv.addObject("status", status);
			}
			
			if ((status != null) && (status.equals("waiting"))) {
				maps.put("freeStatus", Integer.valueOf(0));
				mv.addObject("status", status);
			}
			
			if ((beginTime != null) && (!beginTime.equals(""))) {
				maps.put("beginTime_more_than_equal", CommUtil.formatDate(beginTime));
				mv.addObject("beginTime", beginTime);
			}
			
			if ((endTime != null) && (!endTime.equals(""))) {
				maps.put("beginTime_less_than_equal", CommUtil.formatDate(endTime));
				mv.addObject("endTime", endTime);
			}
			
			IPageList pList = this.freegoodsService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
			
			List<FreeClass> fcls = this.freeClassService.queryPageList(RedPigMaps.newMap());
			
			mv.addObject("fcls", fcls);
			mv.addObject("freeTools", this.freeTools);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启0元试用功能");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 0元试用商品添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品添加", value = "/freegoods_add*", rtype = "seller", rname = "0元试用商品添加", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/freegoods_add" })
	public ModelAndView freegoods_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/freegoods_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if (config.getWhether_free() == 1) {
			if (user.getStore().getGrade().getWhether_free() != 1) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您的店铺类型不支持0元试用功能");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
			mv.addObject("currentPage", currentPage);
			List<FreeClass> cls = this.freeClassService.queryPageList(RedPigMaps.newMap());
			mv.addObject("cls", cls);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启0元试用功能");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 0元试用商品保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param class_id
	 * @param goods_id
	 * @param default_count
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "0元试用商品保存", value = "/freegoods_save*", rtype = "seller", rname = "0元试用商品添加", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/freegoods_save" })
	public void freegoods_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String class_id, String goods_id, String default_count) {
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(false));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if ((config.getWhether_free() == 1)
				&& (user.getStore().getGrade().getWhether_free() == 1)) {
			json.put("ret", Boolean.valueOf(true));
			
			FreeGoods freegoods = null;
			if (id.equals("")) {
				freegoods = (FreeGoods) WebForm.toPo(request, FreeGoods.class);
				freegoods.setAddTime(new Date());
			} else {
				FreeGoods obj = this.freegoodsService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				freegoods = (FreeGoods) WebForm.toPo(request, obj);
			}
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "free";
			Map<String, Object> map = Maps.newHashMap();
			try {
				String fileName = freegoods.getFree_acc() == null ? "" : freegoods.getFree_acc().getName();
				map = CommUtil.saveFileToServer(request, "free_acc",saveFilePathName, fileName, null);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						Accessory free_acc = new Accessory();
						free_acc.setName(CommUtil.null2String(map.get("fileName")));
						free_acc.setExt(CommUtil.null2String(map.get("mime")));
						free_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
						free_acc.setPath(uploadFilePath + "/free");
						free_acc.setWidth(CommUtil.null2Int(map.get("width")));
						free_acc.setHeight(CommUtil.null2Int(map.get("height")));
						free_acc.setAddTime(new Date());
						this.accessoryService.saveEntity(free_acc);
						freegoods.setFree_acc(free_acc);
					}
				} else if (map.get("fileName") != "") {
					Accessory free_acc = freegoods.getFree_acc();
					free_acc.setName(CommUtil.null2String(map.get("fileName")));
					free_acc.setExt(CommUtil.null2String(map.get("mime")));
					free_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					free_acc.setPath(uploadFilePath + "/free");
					free_acc.setWidth(CommUtil.null2Int(map.get("width")));
					free_acc.setHeight(CommUtil.null2Int(map.get("height")));
					free_acc.setAddTime(new Date());
					this.accessoryService.updateById(free_acc);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			freegoods.setClass_id(CommUtil.null2Long(class_id));
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			if (goods != null) {
				freegoods.setGoods_name(goods.getGoods_name());
			}
			freegoods.setCurrent_count(CommUtil.null2Int(default_count));
			freegoods.setDefault_count(CommUtil.null2Int(default_count));
			freegoods.setFreeType(0);
			freegoods.setFreeStatus(0);
			freegoods.setStore_id(user.getStore().getId());
			freegoods.setGoods_id(goods.getId());
			if (id.equals("")) {
				this.freegoodsService.saveEntity(freegoods);
			} else {
				this.freegoodsService.updateById(freegoods);
			}
			json.put("op_title", "申请0元试用成功");
			json.put("url", CommUtil.getURL(request) + "/freegoods_list");
			return_json(JSON.toJSONString(json), response);
		} else {
			json.put("op_title", "系统未开启0元试用功能");
			json.put("url", CommUtil.getURL(request) + "/index");
			return_json(JSON.toJSONString(json), response);
		}
	}
	
	/**
	 * 0元试用商品
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品", value = "/free_items*", rtype = "seller", rname = "0元试用商品添加", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/free_items" })
	public ModelAndView free_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/free_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = this.storeService.selectByPrimaryKey(user.getStore().getId());
		List<GoodsClass> gcs = this.StoreTools.query_store_gc(store);
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 0元试用商品编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品编辑", value = "/freegoods_edit*", rtype = "seller", rname = "0元试用商品管理", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/freegoods_edit" })
	public ModelAndView freegoods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/freegoods_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if ((config.getWhether_free() == 1)
				&& (user.getStore().getGrade().getWhether_free() == 1)) {
			if ((id != null) && (!id.equals(""))) {
				FreeGoods freegoods = this.freegoodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if ((freegoods.getFreeStatus() == -5) || (freegoods.getFreeStatus() == 0)) {
					
					List<FreeClass> cls = this.freeClassService.queryPageList(RedPigMaps.newMap());
					
					mv.addObject("cls", cls);
					if (freegoods != null) {
						Goods goods = this.goodsService.selectByPrimaryKey(freegoods
								.getClass_id());
						mv.addObject("goods", goods);
					}
					mv.addObject("obj", freegoods);
					mv.addObject("currentPage", currentPage);
					mv.addObject("edit", Boolean.valueOf(true));
				} else {
					mv = new RedPigJModelAndView(
							"user/default/sellercenter/seller_error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "您无此0元试用商品");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/freegoods_list");
				}
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您无此0元试用商品");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/freegoods_list");
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启0元试用功能");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 0元试用商品加载
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param gc_id
	 */
	@SuppressWarnings({  "rawtypes" })
	@SecurityMapping(title = "0元试用商品加载", value = "/free_goods_load*", rtype = "seller", rname = "0元试用商品加载", rcode = "free_goods_load", rgroup = "0元试用管理")
	@RequestMapping({ "/free_goods_load" })
	public void free_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_type", Integer.valueOf(1));
		params.put("goods_store_id", user.getStore().getId());
		
		
		this.queryTools.shildGoodsStatusParams(params);
		
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			Set<Long> ids = genericGcIds(gc);
			params.put("ids", ids);
			
		}
		List<Goods> goods = this.goodsService.queryPageList(params);
		
		List<Map> list = Lists.newArrayList();
		for (Goods obj : goods) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getStore_price());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_inventory",
					Integer.valueOf(obj.getGoods_inventory()));
			list.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 0元试用商品删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品删除", value = "/free_goods_del*", rtype = "seller", rname = "0元试用商品添加", rcode = "freegoodsadd_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/free_goods_del" })
	public String free_goods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		for (String id : ids) {
			if (!id.equals("")) {
				FreeGoods freegoods = this.freegoodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if ((freegoods != null) 
						&& (freegoods.getFreeStatus() != 5)
						&& (freegoods.getFreeType() == 0)) {
					if (freegoods.getStore_id().equals(user.getStore().getId())) {
						Goods goods = this.goodsService.selectByPrimaryKey(freegoods.getGoods_id());
						goods.setWhether_free(0);
						this.goodsService.updateById(goods);
						this.freegoodsService.deleteById(Long.valueOf(Long.parseLong(id)));
					}
				}
			}
		}
		return "redirect:freegoods_list?currentPage=" + currentPage;
	}
	
	/**
	 * 申请记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param id
	 * @param userName
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "申请记录", value = "/apply_logs*", rtype = "seller", rname = "0元试用商品", rcode = "freegoods_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/apply_logs" })
	public ModelAndView apply_logs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String id, String userName, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/freeapplylog_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		FreeGoods fg = this.freegoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((fg != null) 
				&& (fg.getStore_id().equals(user.getStore().getId()))
				&& (config.getWhether_free() == 1)
				&& (user.getStore().getGrade().getWhether_free() == 1)) {
			
			String url = this.configService.getSysConfig().getAddress();
			if ((url == null) || (url.equals(""))) {
				url = CommUtil.getURL(request);
			}
			String params = "";
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
			maps.put("freegoods_id", CommUtil.null2Long(id));
			
			if ((userName != null) && (!userName.equals(""))) {
				
				maps.put("user_name_like", userName);
				
				mv.addObject("userName", userName);
			}
			
			if ((status != null) && (status.equals("yes"))) {
				maps.put("apply_status", 5);
				mv.addObject("status", status);
			}
			
			if ((status != null) && (status.equals("waiting"))) {
				maps.put("apply_status", 0);
				mv.addObject("status", status);
			}
			
			if ((status != null) && (status.equals("no"))) {
				maps.put("apply_status", -5);
				mv.addObject("status", status);
			}
			
			IPageList pList = this.freeapplylogService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView(url + "/apply_logs.html", "", params, pList, mv);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启0元试用功能");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		mv.addObject("id", id);
		return mv;
	}
	
	/**
	 * 申请记录详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "申请记录详情", value = "/apply_log_info*", rtype = "seller", rname = "0元试用商品", rcode = "freegoods_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/apply_log_info" })
	public ModelAndView apply_log_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/freeapplylog_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			FreeApplyLog freeapplylog = this.freeapplylogService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			SysConfig config = this.configService.getSysConfig();
			FreeGoods fg = this.freegoodsService.selectByPrimaryKey(freeapplylog.getFreegoods_id());
			if ((fg != null)
					&& (fg.getStore_id().equals(user.getStore().getId()))
					&& (config.getWhether_free() == 1)
					&& (user.getStore().getGrade().getWhether_free() == 1)) {
				
				Map<String, Object> params = Maps.newHashMap();
				params.put("ecc_type", Integer.valueOf(0));
				params.put("ecc_store_id", user.getStore().getId());
				
				List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.queryPageList(params);
				
				params.clear();
				params.put("sa_type", Integer.valueOf(0));
				params.put("sa_user_id", user.getId());
				params.put("orderBy", "sa_default desc,sa_sequence");
				params.put("orderType", "asc");
				
				List<ShipAddress> shipAddrs = this.shipAddressService.queryPageList(params);
				
				mv.addObject("eccs", eccs);
				mv.addObject("shipAddrs", shipAddrs);
				mv.addObject("obj", freeapplylog);
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "系统未开启0元试用功能");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
		}
		return mv;
	}
	
	/**
	 * 申请记录状态修改
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param status
	 * @param shipCode
	 * @param ecc_id
	 * @param sa_id
	 */
	@SecurityMapping(title = "申请记录状态修改", value = "/apply_log_save*", rtype = "seller", rname = "0元试用商品", rcode = "freegoods_seller", rgroup = "0元试用管理")
	@RequestMapping({ "/apply_log_save" })
	public void apply_log_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String status, String shipCode, String ecc_id, String sa_id) {
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(false));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		SysConfig config = this.configService.getSysConfig();
		if ((config.getWhether_free() == 1)
				&& (user.getStore().getGrade().getWhether_free() == 1)) {
			
			FreeApplyLog freeapplylog = null;
			if (id.equals("")) {
				freeapplylog = (FreeApplyLog) WebForm.toPo(request,FreeApplyLog.class);
				freeapplylog.setAddTime(new Date());
			} else {
				FreeApplyLog obj = this.freeapplylogService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				freeapplylog = (FreeApplyLog) WebForm.toPo(request, obj);
			}
			if (status.equals("yes")) {
				ExpressCompany ec = this.expressCompayService.selectByPrimaryKey(CommUtil.null2Long(ecc_id));
				ShipAddress sa = this.shipAddressService.selectByPrimaryKey(CommUtil.null2Long(sa_id));
				freeapplylog.setShip_addr_id(sa.getId());
				Area area = this.areaService.selectByPrimaryKey(sa.getSa_area_id());
				freeapplylog.setShip_addr(area.getParent().getParent()
						.getAreaName()
						+ area.getParent().getAreaName()
						+ area.getAreaName()
						+ sa.getSa_addr());
				
				freeapplylog.setShipCode(shipCode);
				freeapplylog.setShip_addr_id(sa.getId());
				
				Map<String,Object> json_map = Maps.newHashMap();
				json_map.put("express_company_id", ec.getId());
				json_map.put("express_company_name", ec.getCompany_name());
				json_map.put("express_company_mark", ec.getCompany_mark());
				json_map.put("express_company_type", ec.getCompany_type());
				
				freeapplylog.setExpress_info(JSON.toJSONString(json_map));
				freeapplylog.setApply_status(5);
				FreeGoods fg = this.freegoodsService.selectByPrimaryKey(freeapplylog.getFreegoods_id());
				int count = fg.getCurrent_count() - 1;
				fg.setCurrent_count(count);
				if (count <= 0) {
					fg.setFreeStatus(10);
					this.freegoodsService.updateById(fg);
				}
				String msg_content = "您申请的0元试用：" + freeapplylog.getFreegoods_name() + "已通过审核。";
				
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				User buyer = this.userService.selectByPrimaryKey(freeapplylog.getUser_id());
				msg.setToUser(buyer);
				this.messageService.saveEntity(msg);
			} else {
				freeapplylog.setApply_status(-5);
				freeapplylog.setEvaluate_status(2);
				String msg_content = "您申请的0元试用：" + freeapplylog.getFreegoods_name() + "未过审核。";
				
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				User buyer = this.userService.selectByPrimaryKey(freeapplylog.getUser_id());
				msg.setToUser(buyer);
				this.messageService.saveEntity(msg);
			}
			if (id.equals("")) {
				this.freeapplylogService.saveEntity(freeapplylog);
			} else if (freeapplylog.getStore_id().equals(
					user.getStore().getId())) {
				this.freeapplylogService.updateById(freeapplylog);
			}
			json.put("ret", Boolean.valueOf(true));
			json.put("op_title", "修改0元试用申请状态成功");
			json.put("url", CommUtil.getURL(request) + "/apply_logs?id="
					+ freeapplylog.getFreegoods_id());
			return_json(JSON.toJSONString(json), response);
		} else {
			json.put("op_title", "系统未开启0元试用功能");
			json.put("url", CommUtil.getURL(request) + "/index");
			return_json(JSON.toJSONString(json), response);
		}
	}

	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	public void return_json(String json, HttpServletResponse response) {
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
