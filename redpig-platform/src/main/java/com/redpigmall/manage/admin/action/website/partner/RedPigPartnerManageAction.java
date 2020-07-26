package com.redpigmall.manage.admin.action.website.partner;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
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
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Partner;

/**
 * 
 * <p>
 * Title: RedPigPartnerManageAction.java
 * </p>
 * 
 * <p>
 * Description:系统文章管理控制器，用来发布、修改系统文章信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-5-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigPartnerManageAction extends BaseAction{

	/**
	 * 合作伙伴列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param title
	 * @return
	 */
	@SecurityMapping(title = "合作伙伴列表", value = "/partner_list*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping({ "/partner_list" })
	public ModelAndView list(
			HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String title) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/partner_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
				
		if ((title != null) && (!title.equals(""))) {
			
			params.put("title_like", title);
			
		}

		IPageList pList = this.redPigPartnerService.list(params);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		if ((title != null) && (!title.equals(""))) {
			CommUtil.saveIPageList2ModelAndView(url
					+ "/partner_list.html", "", "title=" + title, pList,
					mv);
		} else {
			CommUtil.saveIPageList2ModelAndView(url
					+ "/partner_list.html", "", "", pList, mv);
		}
		return mv;
	}
	
	/**
	 * 合作伙伴添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "合作伙伴添加", value = "/partner_add*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping({ "/partner_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/partner_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 合作伙伴编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "合作伙伴编辑", value = "/partner_edit*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping({ "/partner_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/partner_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Partner partner = this.redPigPartnerService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			mv.addObject("obj", partner);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 合作伙伴保存
	 * @param request
	 * @param response
	 * @param id
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "合作伙伴保存", value = "/partner_save*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping({ "/partner_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String list_url,
			String add_url) {
		Partner partner = null;
		if (id.equals("")) {
			partner = (Partner) WebForm.toPo(request, Partner.class);
			partner.setAddTime(new Date());
		} else {
			Partner obj = this.redPigPartnerService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			partner = (Partner) WebForm.toPo(request, obj);
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "partner";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = partner.getImage() == null ? "" : partner
					.getImage().getName();
			map = CommUtil.saveFileToServer(request, "image", saveFilePathName,
					fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = new Accessory();
					photo.setName(CommUtil.null2String(map.get("fileName")));
					photo.setExt(CommUtil.null2String(map.get("mime")));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/partner");
					photo.setWidth(CommUtil.null2Int(map.get("width")));
					photo.setHeight(CommUtil.null2Int(map.get("height")));
					photo.setAddTime(new Date());
					this.redPigAccessoryService.saveEntity(photo);
					partner.setImage(photo);
				}
			} else if (map.get("fileName") != "") {
				Accessory photo = partner.getImage();
				photo.setName(CommUtil.null2String(map.get("fileName")));
				photo.setExt(CommUtil.null2String(map.get("mime")));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				photo.setPath(uploadFilePath + "/partner");
				photo.setWidth(CommUtil.null2Int(map.get("width")));
				photo.setHeight(CommUtil.null2Int(map.get("height")));
				this.redPigAccessoryService.updateById(photo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.redPigPartnerService.saveEntity(partner);
		} else {
			this.redPigPartnerService.updateById(partner);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存合作伙伴成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}
	
	/**
	 * 合作伙伴删除
	 * @param request
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "合作伙伴删除", value = "/partner_del*", rtype = "admin", rname = "合作伙伴", rcode = "partner_manage", rgroup = "网站")
	@RequestMapping({ "/partner_del" })
	public String deleteById(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Partner partner = this.redPigPartnerService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				RedPigCommonUtil.del_acc(request, partner.getImage());
				this.redPigPartnerService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:partner_list";
	}
}
