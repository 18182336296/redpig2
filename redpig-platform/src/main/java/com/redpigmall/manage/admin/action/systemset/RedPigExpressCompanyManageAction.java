package com.redpigmall.manage.admin.action.systemset;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.domain.SysConfig;

/**
 * 
 * <p>
 * Title: RedPigExpressCompanyManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城平台快递公司管理，通过快递公司代码查询对应的订单信息数据
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
public class RedPigExpressCompanyManageAction extends BaseAction{

	/**
	 * 快递推送详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "快递推送详情", value = "/express_info_view*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_info_view" })
	public ModelAndView express_info_view(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/express_info_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		mv.addObject("obj", this.redPigExpressCompanyService.selectByPrimaryKey(CommUtil.null2Long(id)));
		
		mv.addObject("orderFormTools", this.redPigOrderFormTools);
		return mv;
	}
	
	/**
	 * 快递推送列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_express_id
	 * @param order_status
	 * @return
	 */
	@SecurityMapping(title = "快递推送列表", value = "/express_info_list*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_info_list" })
	public ModelAndView express_info_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage,
			String order_express_id, 
			String order_status) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/express_info_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, null, null);
		

		if (!CommUtil.null2String(order_express_id).equals("")) {
			maps.put("order_express_id", order_express_id);
		}

		if (!CommUtil.null2String(order_status).equals("")) {
			maps.put("order_status", order_status);
		}

		IPageList pList = this.redPigExpressCompanyService.list(maps);

		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

		mv.addObject("order_express_id", order_express_id);
		mv.addObject("order_status", order_status);
		return mv;
	}
	
	/**
	 * 运单模板加载
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "运单模板加载", value = "/express_company_template_load*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_template_load" })
	public ModelAndView express_company_template_load(
			HttpServletRequest request, 
			HttpServletResponse response,
			String id, String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/express_company_template_load.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		if ((id != null) && (!id.equals(""))) {
			ExpressCompany expresscompany = this.redPigExpressCompanyService
					.selectByPrimaryKey(Long.parseLong(id));
			mv.addObject("obj", expresscompany);
		}
		return mv;
	}
	
	/**
	 * 运单模板保存成功
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "运单模板保存成功", value = "/express_company_template_success*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_template_success" })
	public ModelAndView express_company_template_success(
			HttpServletRequest request, 
			HttpServletResponse response,
			String id, String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/express_company_list");
		mv.addObject("op_title", "快递运单模板保存成功");
		return mv;
	}
	
	/**
	 * 运单模板保存
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "运单模板保存", value = "/express_company_template_print*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_template_save" })
	public void express_company_template_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id) {
		
		ExpressCompany obj = this.redPigExpressCompanyService.selectByPrimaryKey(CommUtil.null2Long(id));
		boolean ret = true;
		if (obj != null) {
			Map<String, Object> map = Maps.newHashMap();
			Enumeration<?> enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String value = request.getParameter(paramName);
				if ((!paramName.equals("id"))
						&& (!CommUtil.null2String(value).equals(""))
						&& (!CommUtil.null2String(value).equals("null"))) {
					map.put(paramName, value);
				}
			}
			obj.setCompany_template_offset(JSON.toJSONString(map));
			
			this.redPigExpressCompanyService.update(obj);
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
	 * 运单模板打印测试
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "运单模板打印测试", value = "/express_company_template_print*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_template_print" })
	public ModelAndView express_company_template_print(
			HttpServletRequest request, 
			HttpServletResponse response,
			String id, String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/express_company_template_print.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		if ((id != null) && (!id.equals(""))) {
			ExpressCompany expresscompany = this.redPigExpressCompanyService.selectByPrimaryKey(Long.valueOf(id));
			Map offset_map = JSON.parseObject(expresscompany.getCompany_template_offset());
			mv.addObject("offset_map", offset_map);
			mv.addObject("obj", expresscompany);
		}
		return mv;
	}
	
	/**
	 * 运单模板编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "运单模板编辑", value = "/express_company_template_edit*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_template_edit" })
	public ModelAndView express_company_template_edit(
			HttpServletRequest request, 
			HttpServletResponse response,
			String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/express_company_template_edit.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		if ((id != null) && (!id.equals(""))) {
			ExpressCompany obj = this.redPigExpressCompanyService.selectByPrimaryKey(CommUtil.null2Long(id));
			Map offset_map = JSON.parseObject(obj.getCompany_template_offset());
			mv.addObject("offset_map", offset_map);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		}
		return mv;
	}
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param company_mark
	 * @param id
	 */
	@RequestMapping({ "/express_company_mark" })
	public void express_company_mark(
			HttpServletRequest request,
			HttpServletResponse response, 
			String company_mark, String id) {
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("company_mark", company_mark.trim());
		params.put("redPigId", CommUtil.null2Long(id));

		int ecs = this.redPigExpressCompanyService.selectCountByNotId(params);
		
		boolean ret = true;
		if (ecs > 0) {
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
	 * 快递公司Ajax更新数据
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 */
	@SecurityMapping(title = "快递公司Ajax更新数据", value = "/express_company_ajax*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_ajax" })
	public void express_company_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String fieldName,
			String value)  {
		
		ExpressCompany obj = this.redPigExpressCompanyService
				.selectByPrimaryKey(Long.valueOf(id));
		
		Field[] fields = ExpressCompany.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigExpressCompanyService.update(obj);
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
	 * 快递公司删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "快递公司删除", value = "/express_company_del*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_del" })
	public String express_company_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		
		this.redPigExpressCompanyService.deleteByIds(ids);
		
		return "redirect:express_company_list?currentPage=" + currentPage;
	}
	
	/**
	 * 快递公司保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "快递公司保存", value = "/express_company_save*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_save" })
	public ModelAndView express_company_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String currentPage) {
		
		ExpressCompany expresscompany = null;
		if (id.equals("")) {
			expresscompany = (ExpressCompany) WebForm.toPo(request,ExpressCompany.class);
			expresscompany.setAddTime(new Date());
		} else {
			ExpressCompany obj = this.redPigExpressCompanyService.selectByPrimaryKey(Long.valueOf(id));
			expresscompany = (ExpressCompany) WebForm.toPo(request, obj);
		}
		String uploadFilePath = this.redPigSysConfigService.getSysConfig()
				.getUploadFilePath();
		
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "express_template";
		
		Map<String, Object> map = Maps.newHashMap();
		try {
			map = CommUtil.saveFileToServer(request, "company_template_acc",
					saveFilePathName, "", null);
			
			if (map.get("fileName") != "") {
				String company_template = uploadFilePath + "/express_template/"
						+ CommUtil.null2String(map.get("fileName"));
				
				expresscompany.setCompany_template(company_template);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id.equals("")) {
			this.redPigExpressCompanyService.save(expresscompany);
		} else {
			this.redPigExpressCompanyService.update(expresscompany);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/express_company_list");

		mv.addObject("op_title", "保存快递公司成功");

		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/express_company_add?currentPage=" + currentPage);

		return mv;
	}

	/**
	 * 快递公司编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "快递公司编辑", value = "/express_company_edit*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_edit" })
	public ModelAndView express_company_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/express_company_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		if ((id != null) && (!id.equals(""))) {
			ExpressCompany expresscompany = this.redPigExpressCompanyService.selectByPrimaryKey(Long.parseLong(id));
			mv.addObject("obj", expresscompany);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 快递公司添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "快递公司添加", value = "/express_company_add*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_add" })
	public ModelAndView express_company_add(HttpServletRequest request,
			HttpServletResponse response) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/express_company_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		return mv;
	}
	
	/**
	 * 快递公司列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "快递公司列表", value = "/express_company_list*", rtype = "admin", rname = "快递公司", rcode = "admin_express_company", rgroup = "设置")
	@RequestMapping({ "/express_company_list" })
	public ModelAndView express_company_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, String orderBy,String orderType) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/express_company_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		String params = "";

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, "company_sequence", "asc");

		IPageList pList = this.redPigExpressCompanyService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/expresscompany_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 快递设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "快递设置", value = "/set_kuaidi*", rtype = "admin", rname = "快递设置", rcode = "admin_set_kuaidi", rgroup = "设置")
	@RequestMapping({ "/set_kuaidi" })
	public ModelAndView set_kuaidi(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/set_kuaidi.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
	
	/**
	 * 保存快递设置
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "保存快递设置", value = "/set_kuaidi_save*", rtype = "admin", rname = "快递设置", rcode = "admin_set_kuaidi", rgroup = "设置")
	@RequestMapping({ "/set_kuaidi_save" })
	public ModelAndView set_kuaidi_save(HttpServletRequest request,
			HttpServletResponse response, String id) {
		SysConfig obj = this.redPigSysConfigService.getSysConfig();
		SysConfig config = null;
		if (id.equals("")) {
			config = (SysConfig) WebForm.toPo(request, SysConfig.class);
			config.setAddTime(new Date());
		} else {
			config = (SysConfig) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.redPigSysConfigService.save(config);
		} else {
			this.redPigSysConfigService.update(config);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("op_title", "快递设置成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/set_kuaidi");
		return mv;
	}
	
	
}
