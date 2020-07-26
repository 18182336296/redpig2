package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreNavigation;
import com.redpigmall.domain.StoreSlide;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigStoreDecorateSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 店铺装修功能管理控制器
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
 * @date 2014-12-18
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigStoreDecorateSellerAction extends BaseAction{
	/**
	 * 店铺装修
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "店铺装修", value = "/decorate*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate" })
	public ModelAndView decorate(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", store.getId());
		params.put("display", Boolean.valueOf(true));
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<StoreNavigation> navs = this.storenavigationService.queryPageList(params);
		
		if ((store.getStore_decorate_info() != null)
				&& (!store.getStore_decorate_info().equals(""))) {
			List<Map> old_maps = JSON.parseArray(
					store.getStore_decorate_info(), Map.class);
			mv.addObject("maps", old_maps);
		}
		if ((store.getStore_decorate_base_info() != null)
				&& (!store.getStore_decorate_base_info().equals(""))) {
			List<Map> fundations = JSON.parseArray(
					store.getStore_decorate_base_info(), Map.class);
			for (Map fun : fundations) {
				mv.addObject("fun_" + fun.get("key"), fun.get("val"));
			}
		}
		params.clear();
		params.put("store_id", store.getId());
		List<StoreSlide> slides = this.storeSlideService.queryPageList(params);
		
		String store_theme = "default";
		if (store.getStore_decorate_theme() != null) {
			store_theme = store.getStore_decorate_theme();
		}
		if (store.getStore_decorate_background_info() != null) {
			Object bg = JSON.parseObject(store
					.getStore_decorate_background_info());
			mv.addObject("bg", bg);
		}
		mv.addObject("store_theme", store_theme);
		mv.addObject("navs", navs);
		mv.addObject("store", store);
		mv.addObject("default_slides", slides);
		return mv;
	}
	
	/**
	 * 店铺装修预览
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "店铺装修预览", value = "/decorate_preview*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_preview" })
	public ModelAndView decorate_preview(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/index_preview.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		generic_evaluate(store, mv);
		Map<String, Object> params = RedPigMaps.newMaps("sequence", "asc");
		params.put("store_id", store.getId());
		params.put("display", Boolean.valueOf(true));
		
		List<StoreNavigation> navs = this.storenavigationService.queryPageList(params);
		
		mv.addObject("navs", navs);
		mv.addObject("store", store);
		mv.addObject("userTools", this.userTools);
		if (store.getStore_decorate_base_info() != null) {
			List<Map> fundations = JSON.parseArray(
					store.getStore_decorate_base_info(), Map.class);
			for (Map fun : fundations) {
				mv.addObject("fun_" + fun.get("key"), fun.get("val"));
			}
		}
		if ((store.getStore_decorate_info() != null)
				&& (!store.getStore_decorate_info().equals(""))) {
			List<Map> old_maps = JSON.parseArray(
					store.getStore_decorate_info(), Map.class);
			mv.addObject("maps", old_maps);
		}
		params.clear();
		params.put("store_id", store.getId());
		params.put("slide_type", Integer.valueOf(0));
		List<StoreSlide> slides = this.storeSlideService.queryPageList(params);
		
		mv.addObject("default_slides", slides);
		String store_theme = "default";
		if (store.getStore_decorate_theme() != null) {
			store_theme = store.getStore_decorate_theme();
		}
		mv.addObject("store_theme", store_theme);
		if (store.getStore_decorate_background_info() != null) {
			Object bg = JSON.parseObject(store
					.getStore_decorate_background_info());
			mv.addObject("bg", bg);
		}
		return mv;
	}
	
	/**
	 * 店铺装修主题设置
	 * @param request
	 * @param response
	 * @param theme
	 */
	@SecurityMapping(title = "店铺装修主题设置", value = "/decorate_theme_set*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_theme_set" })
	public void decorate_theme_set(HttpServletRequest request,
			HttpServletResponse response, String theme) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		store.setStore_decorate_theme(CommUtil.null2String(theme));
		this.storeService.updateById(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修保存并退出
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "店铺装修保存并退出", value = "/decorate_subquite*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_subquite" })
	public void decorate_subquite(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		store.setStore_decorate_old_info(store.getStore_decorate_info());
		store.setStore_decorate_background_old_info(store
				.getStore_decorate_background_info());
		store.setStore_decorate_base_old_info(store
				.getStore_decorate_base_info());
		store.setStore_decorate_old_theme(store.getStore_decorate_theme());
		this.storeService.updateById(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修撤销
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "店铺装修撤销", value = "/decorate_backout*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_backout" })
	public void decorate_backout(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		store.setStore_decorate_info(store.getStore_decorate_old_info());
		store.setStore_decorate_background_info(store
				.getStore_decorate_background_old_info());
		store.setStore_decorate_theme(store.getStore_decorate_old_theme());
		store.setStore_decorate_base_info(store
				.getStore_decorate_base_old_info());
		this.storeService.updateById(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修背景设置
	 * @param request
	 * @param response
	 * @param type
	 * @param bg_img_id
	 * @param repeat
	 * @param bg_color
	 */
	@SecurityMapping(title = "店铺装修背景设置", value = "/decorate_background_set*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_background_set" })
	public void decorate_background_set(HttpServletRequest request,
			HttpServletResponse response, String type, String bg_img_id,
			String repeat, String bg_color) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (type.equals("save")) {
			Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
					.null2Long(bg_img_id));
			Map<String, Object> map = Maps.newHashMap();
			if (img != null) {
				map.put("bg_img_id", bg_img_id);
				map.put("bg_img_src", img.getPath() + "/" + img.getName());
			}
			if ((bg_color != null) && (!bg_color.equals(""))) {
				map.put("bg_color", bg_color);
			}
			map.put("repeat", repeat);
			store.setStore_decorate_background_info(JSON.toJSONString(map));
		} else {
			store.setStore_decorate_background_info(null);
		}
		this.storeService.updateById(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修背景图片上传
	 * @param request
	 * @param response
	 * @param img_id
	 */
	@SecurityMapping(title = "店铺装修背景图片上传", value = "/decorate_background_upload*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_background_upload" })
	public void decorate_background_upload(HttpServletRequest request,
			HttpServletResponse response, String img_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath
				+ File.separator
				+ "store_slide"
				+ File.separator + store.getId();
		CommUtil.createFolder(saveFilePathName);
		Map<String, Object> map = Maps.newHashMap();
		String fileName = "";
		Map json_map = Maps.newHashMap();
		Accessory photo = new Accessory();
		if ((img_id != null) && (!img_id.equals(""))) {
			photo = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(img_id));
			
			RedPigCommonUtil.del_acc(request, photo);
			
			fileName = photo.getName();
		}
		SysConfig config = this.configService.getSysConfig();
		String Suffix = config.getImageSuffix();
		String[] suffs = Suffix.split("\\|");
		List<String> list = Lists.newArrayList();

		for (String temp : suffs) {

			list.add(temp);
		}
		String[] suf = (String[]) list.toArray(new String[0]);
		try {
			map = CommUtil.saveFileToServer(request, "bg_img",
					saveFilePathName, "", suf);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_slide/"
							+ store.getId());
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
				}
			} else if (map.get("fileName") != "") {
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/store_slide/" + store.getId());
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.updateById(photo);
				json_map.put("src", photo.getPath() + "/" + photo.getName());
				json_map.put("id", photo.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修布局加载
	 * @param request
	 * @param response
	 * @param layout
	 * @return
	 */
	@SecurityMapping(title = "店铺装修布局加载", value = "/decorate_layout*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_layout" })
	public ModelAndView decorate_layout(HttpServletRequest request,
			HttpServletResponse response, String layout) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/" + layout + ".html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String mark = CommUtil.randomInt(5) + "_" + store.getId();
		List<Map> map_list = Lists.newArrayList();
		if (store.getStore_decorate_info() != null && store.getStore_decorate_info().trim().length()>0) {
			map_list = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("layout", layout);
		map.put("mark", mark);
		if ((layout.equals("layout4")) || (layout.equals("layout3"))) {
			map.put("div1", "true");
			map.put("div2", "true");
		}
		map_list.add(map);
		store.setStore_decorate_info(JSON.toJSONString(map_list));
		mv.addObject("mark", mark);
		mv.addObject("layout", layout);
		this.storeService.updateById(store);
		return mv;
	}
	
	/**
	 * 店铺装修布局移除
	 * @param request
	 * @param response
	 * @param layout
	 * @param mark
	 * @param div
	 */
	@SecurityMapping(title = "店铺装修布局移除", value = "/decorate_layout_remove*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_layout_remove" })
	public void decorate_layout_remove(HttpServletRequest request,
			HttpServletResponse response, String layout, String mark, String div) {
		String code = "true";
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Map> map_list = Lists.newArrayList();
		if (store.getStore_decorate_info() != null) {
			map_list = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			Map obj_map = null;
			for (Map map : map_list) {
				if (CommUtil.null2String(map.get("mark")).equals(mark)) {
					obj_map = map;
					break;
				}
			}
			if ((layout.equals("layout1")) || (layout.equals("layout0"))
					|| (layout.equals("layout2"))) {
				map_list.remove(obj_map);
			} else if ((layout.equals("layout3")) || (layout.equals("layout4"))) {
				obj_map.put(div, "false");
				if (CommUtil.null2String(obj_map.get("div1")).equals("false")) {
					if (CommUtil.null2String(obj_map.get("div2")).equals(
							"false")) {
						map_list.remove(obj_map);
					}
				}
			}
		} else {
			code = "false";
		}
		if (map_list.size() > 0) {
			store.setStore_decorate_info(JSON.toJSONString(map_list));
		} else {
			store.setStore_decorate_info(null);
		}
		this.storeService.updateById(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修布局模块编辑窗口加载
	 * @param request
	 * @param response
	 * @param layout
	 * @param mark
	 * @param div
	 * @param position
	 * @return
	 */
	@SecurityMapping(title = "店铺装修布局模块编辑窗口加载", value = "/decorate_module*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module" })
	public ModelAndView decorate_module(HttpServletRequest request,
			HttpServletResponse response, String layout, String mark,
			String div, String position) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/module.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("layout", layout);
		mv.addObject("mark", mark);
		mv.addObject("position", position);
		if (div != null) {
			mv.addObject("div", div);
		}
		return mv;
	}
	
	/**
	 * 店铺装修布局模块位置记录
	 * @param request
	 * @param response
	 * @param marks
	 */
	@SecurityMapping(title = "店铺装修布局模块位置记录", value = "/decorate_module_location_record*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_location_record" })
	public void decorate_module_location_record(HttpServletRequest request,
			HttpServletResponse response, String marks) {
		if ((marks != null) && (!marks.equals(""))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			List<Map> new_maps = Lists.newArrayList();
			String[] temp_marks = marks.split(",");

			for (String mark : temp_marks) {

				if (!mark.equals("")) {
					for (Map map : maps) {
						if (map.get("mark").equals(mark)) {
							new_maps.add(map);
							break;
						}
					}
				}
			}
			store.setStore_decorate_info(JSON.toJSONString(new_maps));
			this.storeService.updateById(store);
		}
	}
	
	/**
	 * 店铺装修布局模块保存
	 * @param request
	 * @param response
	 * @param type
	 * @param mark
	 * @param div
	 * @param defined_content
	 * @param defined_goods_ids
	 * @param position
	 * @param title
	 * @param img_ids
	 * @param coor_datas
	 * @param coor_img
	 * @return
	 */
	@SecurityMapping(title = "店铺装修布局模块保存", value = "/decorate_module_save*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_save" })
	public String decorate_module_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String type, String mark, String div,
			String defined_content, String defined_goods_ids, String position,
			String title, String img_ids, String coor_datas, String coor_img) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean flag = false;
		String fundation_url = ",nav,slide,class,goods_sale,store_info";
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					if (fundation_url.indexOf("," + type) >= 0) {
						if ((div != null) && (!div.equals(""))) {
							if ((div.equals("div1")) || (div.equals("div2"))) {
								map.put(div + "_url", type);
							}
						} else {
							map.put("url", type);
						}
					} else {
						if (type.equals("defined")) {
							if ((div != null) && (!div.equals(""))) {
								if ((div.equals("div1"))
										|| (div.equals("div2"))) {
									map.put(div + "_content", defined_content);
									map.put(div + "_url", type);
								}
							} else {
								map.put("content", defined_content);
								map.put("url", type);
							}
						}
						if (type.equals("defined_goods")) {
							map.put("goods_ids", defined_goods_ids);
							if (position.equals("goods_top")) {
								map.put("url", position);
								map.put("title", title);
							} else {
								map.put("div2_url", position);
							}
							type = position;
						}
						if (type.equals("defined_slide")) {
							String[] ids = img_ids.split(",");
							String slide_info = "";
							int i = 1;
							for (String id : ids) {
								if (!id.equals("")) {
									Accessory img = this.accessoryService
											.selectByPrimaryKey(CommUtil.null2Long(id));
									String url = request.getParameter("url_"
											+ i);
									slide_info = slide_info + "|" + img.getId()
											+ "==" + url;
									i++;
								}
							}
							map.put("slide_info", slide_info);
							if ((div != null) && (!div.equals(""))) {
								map.put("div2_url", "defined_slide");
							} else {
								map.put("url", "defined_slide");
							}
							map.put("height", Integer.valueOf(300));
							map.put("effect", "fade");
							map.put("delayTime", Integer.valueOf(500));
							map.put("autoPlay", Boolean.valueOf(true));
						}
						if (type.equals("hotspot")) {
							String[] datas = coor_datas.split("\\|");
							List<Map> coors_list = Lists.newArrayList();
							for (String data : datas) {
								if ((data != null) && (data != "")
										&& (!data.equals("undefined"))) {
									String[] coors = data.split("==");
									Map temp_map = Maps.newHashMap();
									String temp_url = coors[0];
									if (temp_url.indexOf("http://") < 0) {
										temp_url = "http://" + temp_url;
									}
									temp_map.put("url", temp_url);
									if (coors.length >= 1) {
										temp_map.put("coor", coors[1]);
									}
									coors_list.add(temp_map);
								}
							}
							if ((div != null) && (!div.equals(""))) {
								if ((div.equals("div1"))
										|| (div.equals("div2"))) {
									map.put(div + "_url", type);
									map.put("coors_list_" + div, coors_list);
									map.put("coors_img_id_" + div, coor_img);
								}
							} else {
								map.put("coors_list", coors_list);
								map.put("coors_img_id", coor_img);
								map.put("url", type);
							}
						}
					}
					flag = true;
					break;
				}
			}
			if (flag) {
				store.setStore_decorate_info(JSON.toJSONString(maps));
				this.storeService.updateById(store);
			}
		}
		return

		"redirect:/module_loading?url=" + type + "&id=" + store.getId()
				+ "&mark=" + mark + "&decorate_view=true&div=" + div;
	}
	
	/**
	 * 店铺装修自定义模块编辑窗口加载
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @return
	 */
	@SecurityMapping(title = "店铺装修自定义模块编辑窗口加载", value = "/decorate_module_set*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_set" })
	public ModelAndView decorate_module_set(HttpServletRequest request,
			HttpServletResponse response, String mark, String div) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/module_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map obj = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					obj = map;
					break;
				}
			}
		}
		mv.addObject("obj", obj);
		if ((obj.get("layout").equals("layout0"))
				|| (obj.get("layout").equals("layout1"))
				|| (obj.get("layout").equals("layout2"))) {
			mv.addObject("url", obj.get("url"));
		} else {
			mv.addObject("url", obj.get(div + "_url"));
		}
		mv.addObject("div", div);
		return mv;
	}
	
	/**
	 * 店铺装修自定义模块编辑图片上传
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @param type
	 * @param img_id
	 */
	@SuppressWarnings("unused")
	@SecurityMapping(title = "店铺装修自定义模块编辑图片上传", value = "/decorate_module_set_load*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_set_load" })
	public void decorate_module_set_load(HttpServletRequest request,
			HttpServletResponse response, String mark, String div, String type,
			String img_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map json_map = Maps.newHashMap();
		Map obj = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					obj = map;
					break;
				}
			}
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		if (type.equals("goods")) {
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "store_decorate";
			Map<String,Object> map = Maps.newHashMap();
			String fileName = "";
			Accessory img = null;
			if ((img_id != null) && (!img_id.equals("undefined"))
					&& (!img_id.equals(""))) {
				img = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(img_id));
				
				RedPigCommonUtil.del_acc(request, img);
				fileName = img.getName();
			}
			SysConfig config = this.configService.getSysConfig();
			String Suffix = config.getImageSuffix();
			String[] suffs = Suffix.split("\\|");
			List<String> list = Lists.newArrayList();

			for (String temp : suffs) {

				list.add(temp);
			}
			String[] suf = (String[]) list.toArray(new String[0]);
			try {
				map = CommUtil.saveFileToServer(request, "font_img",
						saveFilePathName, "", suf);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						Accessory photo = new Accessory();
						photo.setName(CommUtil.null2String(map
								.get("fileName")));
						photo.setExt(CommUtil.null2String(map
								.get("mime")));
						photo.setSize(BigDecimal.valueOf(CommUtil
								.null2Double(map.get("fileSize"))));
						photo.setPath(uploadFilePath + "/store_decorate");
						photo.setWidth(CommUtil.null2Int(map
								.get("width")));
						photo.setHeight(CommUtil.null2Int(map
								.get("height")));
						photo.setAddTime(new Date());
						this.accessoryService.saveEntity(photo);
						json_map.put("src",
								photo.getPath() + "/" + photo.getName());
						json_map.put("id", photo.getId());
					}
				} else if (map.get("fileName") != "") {
					Accessory photo = img;
					photo.setName(CommUtil.null2String(map
							.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_decorate");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.updateById(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修自定义模块编辑保存
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @param url
	 * @param height
	 * @param font_size
	 * @param font_color
	 * @param nav_color
	 * @param back_color
	 * @param hover_color
	 * @param effect
	 * @param delayTime
	 * @param autoPlay
	 * @param title
	 * @param font_img_id
	 * @param goods_count
	 * @param font_back_color
	 * @param board_back_color
	 */
	@SecurityMapping(title = "店铺装修自定义模块编辑保存", value = "/decorate_module_set_save*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_set_save" })
	public void decorate_module_set_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String mark, String div, String url,
			String height, String font_size, String font_color,
			String nav_color, String back_color, String hover_color,
			String effect, String delayTime, String autoPlay, String title,
			String font_img_id, String goods_count, String font_back_color,
			String board_back_color) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map obj_map = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					obj_map = map;
					break;
				}
			}
			if (url.equals("nav")) {
				obj_map.put("height", height);
				obj_map.put("font_size", font_size);
				obj_map.put("font_color", font_color);
				obj_map.put("nav_color", nav_color);
				obj_map.put("back_color", back_color);
				obj_map.put("hover_color", hover_color);
			}
			if (url.equals("defined_slide")) {
				obj_map.put("height", height);
				obj_map.put("effect", effect);
				obj_map.put("delayTime", delayTime);
				obj_map.put("autoPlay",
						Boolean.valueOf(CommUtil.null2Boolean(autoPlay)));
			}
			if (url.equals("goods_top")) {
				if ((font_img_id != null) && (!font_img_id.equals(""))) {
					Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
							.null2Long(font_img_id));
					obj_map.put("font_img_id", font_img_id);
					obj_map.put("font_img_src",
							img.getPath() + "/" + img.getName());
				}
				obj_map.put("title", title);
				obj_map.put("font_color", font_color);
				obj_map.put("back_color", back_color);
			}
			if (url.equals("goods_right")) {
				obj_map.put("hover_color", hover_color);
				obj_map.put("back_color", back_color);
			}
			if (url.equals("goods_sale")) {
				obj_map.put("goods_count", goods_count);
				obj_map.put("font_back_color", font_back_color);
				obj_map.put("font_color", font_color);
				obj_map.put("board_back_color", board_back_color);
			}
			if ((url.equals("class")) || (url.equals("store_info"))) {
				obj_map.put("font_back_color", font_back_color);
				obj_map.put("font_color", font_color);
				obj_map.put("board_back_color", board_back_color);
			}
			store.setStore_decorate_info(JSON.toJSONString(maps));
			this.storeService.updateById(store);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修自定义模块编辑窗口加载
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @return
	 */
	@SecurityMapping(title = "店铺装修自定义模块编辑窗口加载", value = "/decorate_module_defined*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_defined" })
	public ModelAndView decorate_module_defined(HttpServletRequest request,
			HttpServletResponse response, String mark, String div) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/module_defined.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String content = "";
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					if ((div != null) && (!div.equals("undefined"))
							&& (!div.equals("")) && (!div.equals("null"))) {
						content = CommUtil.null2String(temp.get(div
								+ "_content"));
						break;
					}
					content = CommUtil.null2String(temp.get("content"));

					break;
				}
			}
		}
		mv.addObject("mark", mark);
		mv.addObject("div", div);
		mv.addObject("content", content);
		return mv;
	}
	
	/**
	 * 店铺装修商品模块窗口加载
	 * @param request
	 * @param response
	 * @param mark
	 * @param position
	 * @return
	 */
	@SecurityMapping(title = "店铺装修商品模块窗口加载", value = "/decorate_module_items*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_items" })
	public ModelAndView decorate_module_items(HttpServletRequest request,
			HttpServletResponse response, String mark, String position) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/module_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Goods> objs = Lists.newArrayList();
		String ids = null;
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					ids = CommUtil.null2String(temp.get("goods_ids"));
					break;
				}
			}
		}
		if (ids != null) {
			String[] temp_ids = ids.split(",");

			for (String id : temp_ids) {

				if (!id.equals("")) {
					Goods obj = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(id));
					objs.add(obj);
				}
			}
		}
		mv.addObject("objs", objs);
		mv.addObject("position", position);
		mv.addObject("mark", mark);
		return mv;
	}
	
	/**
	 * 店铺装修商品模块窗口加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "店铺装修商品模块窗口加载", value = "/decorate_module_goods_load*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_goods_load" })
	public ModelAndView decorate_module_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/module_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,6, "addTime","desc");
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			maps.put("goods_name_like", goods_name);
		}
		
		maps.put("goods_status", 0);
		
		maps.put("goods_store_id", store.getId());
		IPageList pList = this.goodsService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/decorate_module_goods_load.html", "", "&goods_name="
				+ goods_name, pList, mv);
		return mv;
	}
	
	/**
	 * 店铺装修热点区域模块窗口加载
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @return
	 */
	@SecurityMapping(title = "店铺装修热点区域模块窗口加载", value = "/decorate_module_hotspot*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_hotspot" })
	public ModelAndView decorate_module_hotspot(HttpServletRequest request,
			HttpServletResponse response, String mark, String div) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/module_hotspot.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map temp_map = Maps.newHashMap();
		Map obj = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					temp_map = temp;
					break;
				}
			}
		}
		String coors_img_id_mark = "coors_img_id";
		String coors_list_mark = "coors_list";
		if ((div != null) && (!div.equals("undefined"))
				&& (!div.equals("null")) && (!div.equals(""))) {
			coors_img_id_mark = "coors_img_id_" + div;
			coors_list_mark = "coors_list_" + div;
		}
		Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
				.null2Long(temp_map.get(coors_img_id_mark)));
		if (img != null) {
			List<Map> coors_list = (List) temp_map.get(coors_list_mark);
			obj.put("src", CommUtil.getURL(request) + "/" + img.getPath() + "/"
					+ img.getName());
			obj.put("id", img.getId());
			obj.put("height", Integer.valueOf(img.getHeight()));
			obj.put("width", Integer.valueOf(img.getWidth()));
			if (img.getWidth() > 680) {
				obj.put("height", Double.valueOf(CommUtil.mul(
						Double.valueOf(CommUtil.div(
								Integer.valueOf(img.getHeight()),
								Integer.valueOf(img.getWidth()))),
						Integer.valueOf(680))));
			}
			List<Map> coors_list_temp = Lists.newArrayList();
			for (Map temp_coor : coors_list) {
				String coor = CommUtil.null2String(temp_coor.get("coor"));
				String url = CommUtil.null2String(temp_coor.get("url"));
				String[] coor_datas = coor.split(",");
				int height = CommUtil.null2Int(coor_datas[3])
						- CommUtil.null2Int(coor_datas[1]);
				int width = CommUtil.null2Int(coor_datas[2])
						- CommUtil.null2Int(coor_datas[0]);
				int top = CommUtil.null2Int(coor_datas[1]);
				int left = CommUtil.null2Int(coor_datas[0]);
				Map temp = Maps.newHashMap();
				temp.put("height", Integer.valueOf(height));
				temp.put("width", Integer.valueOf(width));
				temp.put("top", Integer.valueOf(top));
				temp.put("left", Integer.valueOf(left));
				temp.put("coor_data", url + "==" + coor);
				temp.put("coor_url", url);
				coors_list_temp.add(temp);
			}
			mv.addObject("coors_list", coors_list_temp);
			mv.addObject("obj", obj);
		}
		mv.addObject("mark", mark);
		mv.addObject("div", div);
		return mv;
	}
	
	/**
	 * 店铺装修热点区域模块图片上传
	 * @param request
	 * @param response
	 * @param mark
	 * @param img_id
	 */
	@SecurityMapping(title = "店铺装修热点区域模块图片上传", value = "/decorate_module_hotspot_img_upload*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_hotspot_img_upload" })
	public void decorate_module_hotspot_img_upload(HttpServletRequest request,
			HttpServletResponse response, String mark, String img_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "store_decorate";
		Map<String, Object> map = Maps.newHashMap();
		String fileName = "";
		Map json_map = Maps.newHashMap();
		Accessory img = null;
		if ((img_id != null) && (!img_id.equals("undefined"))
				&& (!img_id.equals(""))) {
			img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(img_id));
			
			RedPigCommonUtil.del_acc(request, img);
			fileName = img.getName();
		}
		SysConfig config = this.configService.getSysConfig();
		String Suffix = config.getImageSuffix();
		String[] suffs = Suffix.split("\\|");
		List<String> list = Lists.newArrayList();

		for (String temp : suffs) {

			list.add(temp);
		}
		String[] suf = (String[]) list.toArray(new String[0]);
		try {
			map = CommUtil.saveFileToServer(request, "img", saveFilePathName,
					"", suf);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_decorate");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
					json_map.put("height", Integer.valueOf(photo.getHeight()));
					json_map.put("width", Integer.valueOf(photo.getWidth()));
					if (photo.getWidth() > 680) {
						json_map.put("height", Double.valueOf(CommUtil.mul(
								Double.valueOf(CommUtil.div(
										Integer.valueOf(photo.getHeight()),
										Integer.valueOf(photo.getWidth()))),
								Integer.valueOf(680))));
					}
				}
			} else if (map.get("fileName") != "") {
				Accessory photo = img;
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/store_decorate");
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.updateById(photo);
				json_map.put("src", photo.getPath() + "/" + photo.getName());
				json_map.put("id", photo.getId());
				json_map.put("height", Integer.valueOf(photo.getHeight()));
				json_map.put("width", Integer.valueOf(photo.getWidth()));
				if (photo.getWidth() > 680) {
					json_map.put("height", Double.valueOf(CommUtil.mul(
							Double.valueOf(CommUtil.div(
									Integer.valueOf(photo.getHeight()),
									Integer.valueOf(photo.getWidth()))),
							Integer.valueOf(680))));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修热点区设置保存
	 * @param request
	 * @param response
	 * @param coor_datas
	 * @param img_id
	 * @param mark
	 */
	@SuppressWarnings("unused")
	@SecurityMapping(title = "店铺装修热点区设置保存", value = "/decorate_module_hotspot_save*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_hotspot_save" })
	public void decorate_module_hotspot_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String coor_datas, String img_id,
			String mark) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map obj_map = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					obj_map = temp;
					break;
				}
			}
		}
	}
	
	/**
	 * 店铺装修自定义幻灯设置窗口加载
	 * @param request
	 * @param response
	 * @param mark
	 * @param div
	 * @return
	 */
	@SecurityMapping(title = "店铺装修自定义幻灯设置窗口加载", value = "/decorate_module_slide*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_slide" })
	public ModelAndView decorate_module_slide(HttpServletRequest request,
			HttpServletResponse response, String mark, String div) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/decorate/module_defined_slide.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map obj_map = null;
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					obj_map = temp;
					break;
				}
			}
		}
		if (obj_map != null) {
			List<Map> objs = Lists.newArrayList();
			String[] temp_str = CommUtil.null2String(obj_map.get("slide_info"))
					.split("\\|");

			for (String str : temp_str) {

				if (!str.equals("")) {
					String[] temp = str.split("==");
					Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
							.null2Long(temp[0]));
					Map obj = Maps.newHashMap();
					obj.put("id", img.getId());
					obj.put("src", img.getPath() + "/" + img.getName());
					if (temp.length > 1) {
						obj.put("url", temp[1]);
					}
					objs.add(obj);
				}
			}
			mv.addObject("slides", objs);
		}
		mv.addObject("div", div);
		mv.addObject("mark", mark);
		return mv;
	}
	
	/**
	 * 店铺装修自定义幻灯模块图片上传
	 * @param request
	 * @param response
	 * @param img_id
	 * @param count
	 */
	@SecurityMapping(title = "店铺装修自定义幻灯模块图片上传", value = "/decorate_module_slide_upload*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_module_slide_upload" })
	public void decorate_module_slide_upload(HttpServletRequest request,
			HttpServletResponse response, String img_id, String count) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath
				+ File.separator
				+ "store_slide"
				+ File.separator + store.getId();
		CommUtil.createFolder(saveFilePathName);
		Map<String, Object> map = Maps.newHashMap();
		String fileName = "";
		Map json_map = Maps.newHashMap();
		Accessory photo = new Accessory();
		if ((img_id != null) && (!img_id.equals(""))) {
			photo = this.accessoryService
					.selectByPrimaryKey(CommUtil.null2Long(img_id));
			
			RedPigCommonUtil.del_acc(request, photo);
			fileName = photo.getName();
		}
		SysConfig config = this.configService.getSysConfig();
		String Suffix = config.getImageSuffix();
		String[] suffs = Suffix.split("\\|");
		List<String> list = Lists.newArrayList();

		for (String temp : suffs) {

			list.add(temp);
		}
		String[] suf = (String[]) list.toArray(new String[0]);
		try {
			map = CommUtil.saveFileToServer(request, "img_file_" + count,
					saveFilePathName, "", suf);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/store_slide/"
							+ store.getId());
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					json_map.put("src", photo.getPath() + "/" + photo.getName());
					json_map.put("id", photo.getId());
				}
			} else if (map.get("fileName") != "") {
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/store_slide/" + store.getId());
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				photo.setAddTime(new Date());
				this.accessoryService.updateById(photo);
				json_map.put("src", photo.getPath() + "/" + photo.getName());
				json_map.put("id", photo.getId());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺装修基础模块设置
	 * @param request
	 * @param response
	 * @param type
	 * @param status
	 */
	@SecurityMapping(title = "店铺装修基础模块设置", value = "/decorate_fundation_set*", rtype = "seller", rname = "店铺装修", rcode = "store_decorate_seller", rgroup = "我的店铺")
	@RequestMapping({ "/decorate_fundation_set" })
	public void decorate_fundation_set(HttpServletRequest request,
			HttpServletResponse response, String type, String status) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Map> maps = Lists.newArrayList();
		if (store.getStore_decorate_base_info() != null) {
			maps = JSON.parseArray(store.getStore_decorate_base_info(),
					Map.class);
			for (Map map : maps) {
				if (map.get("key").equals(type)) {
					map.put("val", status);
					break;
				}
			}
		} else {
			String[] types = { "nav", "info", "banner", "slide" };

			for (String temp : types) {

				Map<String, Object> map = Maps.newHashMap();
				if (type.equals(temp)) {
					map.put("key", temp);
					map.put("val", status);
				} else {
					map.put("key", temp);
					map.put("val", "on");
				}
				maps.add(map);
			}
		}
		store.setStore_decorate_base_info(JSON.toJSONString(maps));
		this.storeService.updateById(store);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0.0D;
		double service_result = 0.0D;
		double ship_result = 0.0D;
		GoodsClass gc = this.goodsClassService
				.selectByPrimaryKey(store.getGc_main_id());
		if ((store != null) && (gc != null) && (store.getPoint() != null)) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());

			description_result = CommUtil.div(
					Float.valueOf(store_description_evaluate
							- description_evaluate),
					Float.valueOf(description_evaluate));
			service_result = CommUtil.div(
					Float.valueOf(store_service_evaluate - service_evaluate),
					Float.valueOf(service_evaluate));
			ship_result = CommUtil.div(
					Float.valueOf(store_ship_evaluate - ship_evaluate),
					Float.valueOf(ship_evaluate));
		}
		if (description_result > 0.0D) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",

					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(description_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(description_result),
									Integer.valueOf(100))))
							+ "%");
		}
		if (description_result == 0.0D) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0.0D) {
			mv.addObject("description_css", "value_light");
			mv.addObject(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-description_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result > 0.0D) {
			mv.addObject("service_css", "value_strong");
			mv.addObject(
					"service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result == 0.0D) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0.0D) {
			mv.addObject("service_css", "value_light");
			mv.addObject(
					"service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result > 0.0D) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(ship_result), Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result == 0.0D) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0.0D) {
			mv.addObject("ship_css", "value_light");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-ship_result), Integer.valueOf(100))))
							+ "%");
		}
	}
}
