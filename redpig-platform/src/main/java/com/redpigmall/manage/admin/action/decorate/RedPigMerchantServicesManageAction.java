package com.redpigmall.manage.admin.action.decorate;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
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
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.MerchantServices;

/**
 * 
 * <p>
 * Title: RedPigMerchantServicesManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商家服务管理类
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
public class RedPigMerchantServicesManageAction extends BaseAction {

	/**
	 * 商家服务列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param name
	 * @return
	 */
	@SecurityMapping(title = "商家服务列表", value = "/merchant_services_list*", rtype = "admin", rname = "商家服务", rcode = "merchant_services", rgroup = "店铺")
	@RequestMapping({ "/merchant_services_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/merchant_services_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "sequence asc,", "sequence asc");
		
		if ((name != null) && (!"".equals(name))) {
			
			maps.put("serve_name_like", name);
			mv.addObject("name", name);
		}
		
		IPageList pList = this.merchantservicesService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView(null, "", null, pList, mv);
		return mv;
	}
	
	/**
	 * 添加商家服务
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "添加商家服务", value = "/merchant_services_add*", rtype = "admin", rname = "商家服务", rcode = "merchant_services", rgroup = "店铺")
	@RequestMapping({ "/merchant_services_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/merchant_services_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 商家服务编辑管理
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商家服务编辑管理", value = "/merchant_services_edit*", rtype = "admin", rname = "商家服务", rcode = "merchant_services", rgroup = "店铺")
	@RequestMapping({ "/merchant_services_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/merchant_services_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			MerchantServices merchantservices = this.merchantservicesService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", merchantservices);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}	
	
	/**
	 * 商家服务保存管理
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
	@SecurityMapping(title = "商家服务保存管理", value = "/merchant_services_save*", rtype = "admin", rname = "商家服务", rcode = "merchant_services", rgroup = "店铺")
	@RequestMapping({ "/merchant_services_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url) {
		
		MerchantServices merchantservices = null;
		if (id.equals("")) {
			merchantservices = (MerchantServices) WebForm.toPo(request,
					MerchantServices.class);
			merchantservices.setAddTime(new Date());
		} else {
			MerchantServices obj = this.merchantservicesService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			merchantservices = (MerchantServices) WebForm.toPo(request, obj);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "system";
		CommUtil.createFolder(saveFilePathName);
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = merchantservices.getService_img() == null ? ""
					: merchantservices.getService_img().getName();
			map = CommUtil.saveFileToServer(request, "service_img",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = new Accessory();
					logo.setName(CommUtil.null2String(map.get("fileName")));
					logo.setExt((String) map.get("mime"));
					logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(CommUtil.null2Int(map.get("width")));
					logo.setHeight(CommUtil.null2Int(map.get("heigh")));
					logo.setAddTime(new Date());
					this.accessoryService.saveEntity(logo);
					merchantservices.setService_img(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = merchantservices.getService_img();
				logo.setName(CommUtil.null2String(map.get("fileName")));
				logo.setExt(CommUtil.null2String(map.get("mime")));
				logo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				logo.setPath(uploadFilePath + "/system");
				logo.setWidth(CommUtil.null2Int(map.get("width")));
				logo.setHeight(CommUtil.null2Int(map.get("height")));
				this.accessoryService.updateById(logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.merchantservicesService.saveEntity(merchantservices);
		} else {
			this.merchantservicesService.updateById(merchantservices);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存商家服务成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 商家服务删除管理
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商家服务删除管理", value = "/merchant_services_del*", rtype = "admin", rname = "商家服务", rcode = "merchant_services", rgroup = "店铺")
	@RequestMapping({ "/merchant_services_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.merchantservicesService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		
		this.merchantservicesService.batchDeleteByIds(ids);
		
		return "redirect:merchant_services_list?currentPage=" + currentPage;
	}
	
	/**
	 * 商家服务ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商家服务ajax更新", value = "/merchant_services_ajax*", rtype = "admin", rname = "商家服务", rcode = "merchant_services", rgroup = "店铺")
	@RequestMapping({ "/merchant_services_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		MerchantServices obj = this.merchantservicesService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = MerchantServices.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.merchantservicesService.updateById(obj);
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
}
