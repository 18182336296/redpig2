package com.redpigmall.manage.admin.action.operative.advert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigAdvertManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城广告管理器1.0版
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
public class RedPigAdvertManageAction extends BaseAction {

	/**
	 * 广告列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ad_title
	 * @return
	 */
	@SecurityMapping(title = "广告列表", value = "/advert_list*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/advert_list" })
	public ModelAndView advert_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String ad_title) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/advert_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if (!CommUtil.null2String(ad_title).equals("")) {
			params.put("ad_title_like", ad_title.trim());
		}
		
		IPageList pList = this.redPigAdvertService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ad_title", ad_title);
		return mv;
	}
	
	/**
	 * 待审批广告列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ad_title
	 * @return
	 */
	@SecurityMapping(title = "待审批广告列表", value = "/advert_list_audit*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/advert_list_audit" })
	public ModelAndView advert_list_audit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String ad_title) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/advert_list_audit.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if (!CommUtil.null2String(ad_title).equals("")) {
			params.put("ad_title_like", ad_title.trim());
		}
		
		params.put("ad_status", Integer.valueOf(0));
		
		IPageList pList = this.redPigAdvertService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ad_title", ad_title);
		return mv;
	}
	
	/**
	 * 广告增加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告增加", value = "/advert_add*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/advert_add" })
	public ModelAndView advert_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/advert_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = Maps.newHashMap();
		
		List<AdvertPosition> aps = this.redPigAdvertPositionService.queryPageList(params);
		
		mv.addObject("aps", aps);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 广告编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告编辑", value = "/advert_edit*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/advert_edit" })
	public ModelAndView advert_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/advert_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			Advert advert = this.redPigAdvertService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			Map<String,Object> params = Maps.newHashMap();
			
			List<AdvertPosition> aps = this.redPigAdvertPositionService.queryPageList(params);
			
			mv.addObject("aps", aps);
			mv.addObject("obj", advert);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 广告查看
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告查看", value = "/advert_view*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/advert_view" })
	public ModelAndView advert_view(
			HttpServletRequest request,
			HttpServletResponse response,
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/advert_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			Advert advert = this.redPigAdvertService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			mv.addObject("obj", advert);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}
	
	/**
	 * 广告审核
	 * @param request
	 * @param response
	 * @param id
	 * @param ad_status
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告审核", value = "/advert_audit*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/advert_audit" })
	public ModelAndView advert_audit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String ad_status,
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Advert obj = this.redPigAdvertService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		obj.setAd_status(CommUtil.null2Int(ad_status));
		this.redPigAdvertService.updateById(obj);
		
		if ((obj.getAd_status() == 1) && (obj.getAd_ap().getAp_show_type() == 0)) {
			AdvertPosition ap = obj.getAd_ap();
			ap.setAp_use_status(1);
			this.redPigAdvertPositionService.updateById(ap);
		}
		
		if (obj.getAd_status() == -1) {
			User user = obj.getAd_user();
			user.setGold(user.getGold() + obj.getAd_gold());
			this.redPigUserService.updateById(user);
			GoldLog log = new GoldLog();
			log.setAddTime(new Date());
			log.setGl_content("广告审核失败，恢复金币");
			log.setGl_count(obj.getAd_gold());
			log.setGl_user(obj.getAd_user());
			log.setGl_type(0);
			this.redPigGoldlogService.saveEntity(log);
		}
		mv.addObject("op_title", "广告审核成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/advert_list_audit?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 广告保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ad_ap_id
	 * @param currentPage
	 * @param ad_begin_time
	 * @param ad_end_time
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "广告保存", value = "/advert_save*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/advert_save" })
	public ModelAndView advert_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String ad_ap_id,
			String currentPage, 
			String ad_begin_time, 
			String ad_end_time) {
		
		Advert advert = null;
		if (id.equals("")) {
			advert = (Advert) WebForm.toPo(request, Advert.class);
			advert.setAddTime(new Date());
			advert.setAd_user(SecurityUserHolder.getCurrentUser());
		} else {
			Advert obj = this.redPigAdvertService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			advert = (Advert) WebForm.toPo(request, obj);
		}
		AdvertPosition ap = this.redPigAdvertPositionService.selectByPrimaryKey(CommUtil.null2Long(ad_ap_id));
		advert.setAd_ap(ap);
		advert.setAd_begin_time(CommUtil.formatDate(ad_begin_time));
		advert.setAd_end_time(CommUtil.formatDate(ad_end_time));
		String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
		
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "advert";
		Map<String, Object> map = Maps.newHashMap();
		String fileName = "";
		if (advert.getAd_acc() != null) {
			fileName = advert.getAd_acc().getName();
		}
		try {
			map = CommUtil.saveFileToServer(request, "acc", saveFilePathName,fileName, null);
			Accessory acc = null;
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					acc = new Accessory();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.redPigAccessoryService.save(acc);
					advert.setAd_acc(acc);
				}
			} else if (map.get("fileName") != "") {
				acc = advert.getAd_acc();
				acc.setName(CommUtil.null2String(map.get("fileName")));
				acc.setExt(CommUtil.null2String(map.get("mime")));
				acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				acc.setPath(uploadFilePath + "/advert");
				acc.setWidth(CommUtil.null2Int(map.get("width")));
				acc.setHeight(CommUtil.null2Int(map.get("height")));
				acc.setAddTime(new Date());
				this.redPigAccessoryService.update(acc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.redPigAdvertService.saveEntity(advert);
		} else {
			this.redPigAdvertService.updateById(advert);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/advert_list?currentPage=" + currentPage);
		mv.addObject("op_title", "保存广告成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/advert_add?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 广告删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告删除", value = "/advert_del*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/advert_del" })
	public String advert_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Advert advert = this.redPigAdvertService.selectByPrimaryKey(Long.valueOf(Long
						.parseLong(id)));
				if (advert.getAd_status() != 1) {
					RedPigCommonUtil.del_acc(request, advert.getAd_acc());
					if ((advert.getAd_status() == 0)
							&& (advert.getAd_user() != null)) {
						User user = advert.getAd_user();
						user.setGold(user.getGold() + advert.getAd_gold());
						this.redPigUserService.updateById(user);
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("广告审核失败，恢复金币");
						log.setGl_count(advert.getAd_gold());
						log.setGl_user(advert.getAd_user());
						log.setGl_type(0);
						this.redPigGoldLogService.saveEntity(log);
					}
					this.redPigAdvertService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		return "redirect:advert_list?currentPage=" + currentPage;
	}
	
	/**
	 * 广告位添加
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告位添加", value = "/adv_pos_add*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/adv_pos_add" })
	public ModelAndView adv_pos_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/adv_pos_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = Maps.newHashMap();
		
		List<Advert> advs = this.redPigAdvertService.queryPageList(params);
		
		mv.addObject("advs", advs);
		return mv;
	}
	
	/**
	 * 广告位保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "广告位保存", value = "/adv_pos_save*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/adv_pos_save" })
	public ModelAndView adv_pos_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String cmd, 
			String list_url, 
			String add_url) {
		
		AdvertPosition ap = null;
		if (id.equals("")) {
			ap = (AdvertPosition) WebForm.toPo(request, AdvertPosition.class);
			ap.setAddTime(new Date());
		} else {
			AdvertPosition obj = this.redPigAdvertPositionService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			ap = (AdvertPosition) WebForm.toPo(request, obj);
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "advert";
		Map<String, Object> map = Maps.newHashMap();
		String fileName = "";
		if (ap.getAp_acc() != null) {
			fileName = ap.getAp_acc().getName();
		}
		try {
			map = CommUtil.saveFileToServer(request, "acc", saveFilePathName,fileName, null);
			Accessory acc = null;
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					acc = new Accessory();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					acc.setPath(uploadFilePath + "/advert");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.redPigAccessoryService.save(acc);
					ap.setAp_acc(acc);
				}
			} else if (map.get("fileName") != "") {
				acc = ap.getAp_acc();
				acc.setName(CommUtil.null2String(map.get("fileName")));
				acc.setExt(CommUtil.null2String(map.get("mime")));
				acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				acc.setPath(uploadFilePath + "/advert");
				acc.setWidth(CommUtil.null2Int(map.get("width")));
				acc.setHeight(CommUtil.null2Int(map.get("height")));
				acc.setAddTime(new Date());
				this.redPigAccessoryService.update(acc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.redPigAdvertPositionService.saveEntity(ap);
		} else {
			this.redPigAdvertPositionService.updateById(ap);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存广告位成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 广告位列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ap_title
	 * @return
	 */
	@SecurityMapping(title = "广告位列表", value = "/adv_pos_list*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/adv_pos_list" })
	public ModelAndView adv_pos_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String ap_title) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/adv_pos_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if (!CommUtil.null2String(ap_title).equals("")) {
			params.put("ap_title_like", ap_title);
		}
		
		IPageList pList = this.redPigAdvertPositionService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ap_title", ap_title);
		return mv;
	}
	
	/**
	 * 广告位编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告位编辑", value = "/adv_pos_edit*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/adv_pos_edit" })
	public ModelAndView adv_pos_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/adv_pos_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			AdvertPosition obj = this.redPigAdvertPositionService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 广告位删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "广告位删除", value = "/adv_pos_del*", rtype = "admin", rname = "广告管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/adv_pos_del" })
	public String adv_pos_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				AdvertPosition ap = this.redPigAdvertPositionService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if (ap.getAp_sys_type() != 0) {
					RedPigCommonUtil.del_acc(request, ap.getAp_acc());
					this.redPigAdvertPositionService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		return "redirect:adv_pos_list?currentPage=" + currentPage;
	}
}
