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
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreSlide;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: StoreSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心店铺控制器
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
 * @date 2014-4-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigStoreSellerAction extends BaseAction{
	/**
	 * 店铺设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "店铺设置", value = "/store_set*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_set" })
	public ModelAndView store_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		mv.addObject("areaViewTools", this.areaViewTools);
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		if (user.getStore().getGc_detail_info() != null) {
			Set<GoodsClass> detail_gcs = this.storeTools
					.query_store_DetailGc(user.getStore().getGc_detail_info());
			mv.addObject("detail_gcs", detail_gcs);
		}
		mv.addObject("user", user);
		GoodsClass main_gc = this.goodsClassService.selectByPrimaryKey(user.getStore()
				.getGc_main_id());
		mv.addObject("main_gc", main_gc);
		return mv;
	}
	
	/**
	 * 店铺设置保存
	 * @param request
	 * @param response
	 * @param area_id
	 * @param store_second_domain
	 * @param mobile
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "店铺设置保存", value = "/store_set_save*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_set_save" })
	public void store_set_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String area_id,
			String store_second_domain, String mobile) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		WebForm.toPo(request, store);

		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + "/store_logo";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = store.getStore_logo() == null ? "" : store
					.getStore_logo().getName();
			map = CommUtil.saveFileToServer(request, "logo", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_logo = new Accessory();
					store_logo
							.setName(CommUtil.null2String(map.get("fileName")));
					store_logo.setExt(CommUtil.null2String(map.get("mime")));
					store_logo.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					store_logo.setPath(uploadFilePath + "/store_logo");
					store_logo.setWidth(CommUtil.null2Int(map.get("width")));
					store_logo.setHeight(CommUtil.null2Int(map.get("height")));
					store_logo.setAddTime(new Date());
					this.accessoryService.saveEntity(store_logo);
					store.setStore_logo(store_logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory store_logo = store.getStore_logo();
				store_logo.setName(CommUtil.null2String(map.get("fileName")));
				store_logo.setExt(CommUtil.null2String(map.get("mime")));
				store_logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				store_logo.setPath(uploadFilePath + "/store_logo");
				store_logo.setWidth(CommUtil.null2Int(map.get("width")));
				store_logo.setHeight(CommUtil.null2Int(map.get("height")));
				this.accessoryService.updateById(store_logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveFilePathName =

		request.getSession().getServletContext().getRealPath("/")
				+ this.configService.getSysConfig().getUploadFilePath()
				+ "/store_banner";
		try {
			String fileName = store.getStore_banner() == null ? "" : store
					.getStore_banner().getName();
			map = CommUtil.saveFileToServer(request, "banner",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory store_banner = new Accessory();
					store_banner.setName(CommUtil.null2String(map
							.get("fileName")));
					store_banner.setExt(CommUtil.null2String(map.get("mime")));
					store_banner.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					store_banner.setPath(uploadFilePath + "/store_banner");
					store_banner.setWidth(CommUtil.null2Int(map.get("width")));
					store_banner
							.setHeight(CommUtil.null2Int(map.get("height")));
					store_banner.setAddTime(new Date());
					this.accessoryService.saveEntity(store_banner);
					store.setStore_banner(store_banner);
				}
			} else if (map.get("fileName") != "") {
				Accessory store_banner = store.getStore_banner();
				store_banner.setName(CommUtil.null2String(map.get("fileName")));
				store_banner.setExt(CommUtil.null2String(map.get("mime")));
				store_banner.setSize(BigDecimal.valueOf(CommUtil
						.null2Double(map.get("fileSize"))));
				store_banner.setPath(uploadFilePath + "/store_banner");
				store_banner.setWidth(CommUtil.null2Int(map.get("width")));
				store_banner.setHeight(CommUtil.null2Int(map.get("height")));
				this.accessoryService.updateById(store_banner);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		store.setArea(area);
		if (this.configService.getSysConfig().getSecond_domain_open()) {
			if ((this.configService.getSysConfig().getDomain_allow_count() > store
					.getDomain_modify_count())
					&& (!CommUtil.null2String(store_second_domain).equals(""))) {
				if (!store_second_domain.equals(store.getStore_second_domain())) {
					store.setStore_second_domain(store_second_domain);
					store.setDomain_modify_count(store.getDomain_modify_count() + 1);
				}
			}
		}
		this.storeService.updateById(store);
		if ((mobile != null) && (!mobile.equals(""))) {
			user.setMobile(mobile);
			this.userService.updateById(user);
		}
		
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(true));
		json.put("op_title", "店铺设置成功");
		json.put("url", CommUtil.getURL(request) + "/store_set");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺二级域名申请
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@SecurityMapping(title = "店铺二级域名申请", value = "/store_sld*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_sld" })
	public ModelAndView store_sld(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_sld.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sysconfig = this.configService.getSysConfig();
		String serverName = request.getServerName();
		if ((serverName.equals("localhost")) || (CommUtil.isIp(serverName))
				|| (serverName.indexOf("www.") > 0)) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未绑定顶级域名，无法设定二级域名");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else if (sysconfig.getSecond_domain_open()) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			String store_second_domain = "";
			if (!CommUtil.null2String(store.getStore_second_domain())
					.equals("")) {
				store_second_domain = store.getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
			} else {
				store_second_domain = user.getUsername() + "."
						+ CommUtil.generic_domain(request);
			}
			mv.addObject("store_second_domain",
					store_second_domain.split("\\.")[0]);
			mv.addObject("store", store);
			mv.addObject("serverName", serverName.substring(4));
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启二级域名");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 二级域名申请保存
	 * @param request
	 * @param response
	 * @param store_second_domain
	 * @return
	 */
	@SecurityMapping(title = "二级域名申请保存", value = "/store_sld_save*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_sld_save" })
	public String store_sld_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String store_second_domain) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		WebForm.toPo(request, store);
		if (this.configService.getSysConfig().getSecond_domain_open()) {
			if ((this.configService.getSysConfig().getDomain_allow_count() > store
					.getDomain_modify_count())
					&& (!CommUtil.null2String(store_second_domain).equals(""))) {
				if (!store_second_domain.equals(store.getStore_second_domain())) {
					store.setStore_second_domain(store_second_domain
							.toLowerCase());
					store.setDomain_modify_count(store.getDomain_modify_count() + 1);
				}
			}
		}
		this.storeService.updateById(store);
		request.getSession(false).setAttribute("url",
				CommUtil.getURL(request) + "/store_sld");
		request.getSession(false).setAttribute("op_title", "店铺二级域名设置成功");
		return "redirect:/success";
	}
	
	/**
	 * 店铺二级域名验证
	 * @param request
	 * @param response
	 * @param sld_name
	 * @param store_id
	 * @throws IOException
	 */
	@SecurityMapping(title = "店铺二级域名验证", value = "/store_sld_verify*", rtype = "seller", rname = "二级域名", rcode = "store_sld_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_sld_verify" })
	public void store_sld_verify(HttpServletRequest request,
			HttpServletResponse response, String sld_name, String store_id)
			throws IOException {
		SysConfig sysconfig = this.configService.getSysConfig();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean ret = true;
		for (String domain : CommUtil.str2list(sysconfig.getSys_domain())) {
			if (domain.equals(sld_name)) {
				ret = false;
				break;
			}
		}
		if (sysconfig.getDomain_allow_count() > 0) {
			if (store.getDomain_modify_count() >= sysconfig
					.getDomain_allow_count()) {
				ret = false;
			}
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_second_domain", CommUtil.null2String(sld_name));
		params.put("id_no", CommUtil.null2Long(store_id));
		
		List<Store> stores = this.storeService.queryPageList(params);
		
		if (stores.size() > 0) {
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
	 * 店铺幻灯
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "店铺幻灯", value = "/store_slide*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_slide" })
	public ModelAndView store_slide(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_slide.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", store.getId());
		params.put("slide_type", Integer.valueOf(0));
		
		List<StoreSlide> slides = this.storeSlideService.queryPageList(params);
		
		mv.addObject("slides", slides);
		return mv;
	}
	
	/**
	 * 店铺幻灯上传窗口
	 * @param request
	 * @param response
	 * @param index
	 * @return
	 */
	@SecurityMapping(title = "店铺幻灯上传窗口", value = "/store_slide_upload*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_slide_upload" })
	public ModelAndView store_slide_upload(HttpServletRequest request,
			HttpServletResponse response, String index) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_slide_upload.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		mv.addObject("index", Integer.valueOf(CommUtil.null2Int(index)));
		return mv;
	}
	
	/**
	 * 店铺幻灯保存
	 * @param request
	 * @param response
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "店铺幻灯保存", value = "/store_slide_save*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_slide_save" })
	public void store_slide_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String index) {
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
		if (!CommUtil.fileExist(saveFilePathName)) {
			CommUtil.createFolder(saveFilePathName);
		}
		Map<String, Object> map = Maps.newHashMap();
		String fileName = "";
		StoreSlide slide = null;
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", store.getId());
		params.put("slide_type", Integer.valueOf(0));
		
		List<StoreSlide> slides = this.storeSlideService.queryPageList(params);
		
		if (slides.size() >= CommUtil.null2Int(index)) {
			fileName = ((StoreSlide) slides.get(CommUtil.null2Int(index) - 1))
					.getAcc().getName();
			slide = (StoreSlide) slides.get(CommUtil.null2Int(index) - 1);
		}
		try {
			map = CommUtil.saveFileToServer(request, "acc", saveFilePathName,
					fileName, null);
			Accessory acc = null;
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					acc = new Accessory();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					acc.setPath(uploadFilePath + "/store_slide/"
							+ store.getId());
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.accessoryService.saveEntity(acc);
				}
			} else if (map.get("fileName") != "") {
				acc = slide.getAcc();
				acc.setName(CommUtil.null2String(map.get("fileName")));
				acc.setExt(CommUtil.null2String(map.get("mime")));
				acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				acc.setPath(uploadFilePath + "/store_slide/" + store.getId());
				acc.setWidth(CommUtil.null2Int(map.get("width")));
				acc.setHeight(CommUtil.null2Int(map.get("height")));
				acc.setAddTime(new Date());
				this.accessoryService.updateById(acc);
			}
			if (acc != null) {
				if (slide == null) {
					slide = new StoreSlide();
					slide.setAcc(acc);
					slide.setAddTime(new Date());
					slide.setStore(store);
					slide.setSlide_type(0);
				}
				slide.setUrl(request.getParameter("acc_url"));
				if (slide.getId() == null) {
					this.storeSlideService.saveEntity(slide);
				} else {
					this.storeSlideService.updateById(slide);
				}
			} else if (slide != null) {
				slide.setUrl(request.getParameter("acc_url"));
				this.storeSlideService.updateById(slide);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(true));
		json.put("op_title", "店铺幻灯设置成功");
		json.put("url", CommUtil.getURL(request) + "/store_slide");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺幻灯删除
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "店铺幻灯删除", value = "/store_slide_image_del*", rtype = "seller", rname = "店铺设置", rcode = "store_set_seller", rgroup = "我的店铺")
	@RequestMapping({ "/store_slide_image_del" })
	public void store_slide_del(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean flag = false;
		if ((id != null) && (!"".equals(id))) {
			this.storeSlideService.deleteById(CommUtil.null2Long(id));
			flag = true;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(flag);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
