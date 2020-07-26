package com.redpigmall.manage.admin.action.self.express;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
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
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.domain.ExpressCompanyCommon;

/**
 * 
 * <p>
 * Title: RedPigSelfExpressCommonManageAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 平台自营中的常用物流管理，用来处理自营中的常用信息，包括常用物流配置，常用物流模板设置，默认物流设置，常用物流模板打印偏移量配置等等内容
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
 * @date 2014-11-19
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigSelfExpressCommonManageAction extends BaseAction{
	/**
	 * 常用物流配置
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "常用物流配置", value = "/ecc_set*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_set" })
	public ModelAndView ecc_set(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ecc_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("company_status", 0);
		params.put("orderBy", "company_sequence");
		params.put("orderType", "asc");
		
		List<ExpressCompany> ecs = this.expressCompanyService.queryPageList(params);
		
		mv.addObject("ecs", ecs);
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 常用物流配置
	 * @param request
	 * @param response
	 * @param ids
	 * @return
	 */
	@SecurityMapping(title = "常用物流配置", value = "/ecc_save*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_save" })
	public ModelAndView ecc_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String ids) {
		ModelAndView mv = null;
		String[] ec_ids = ids.split(",");
		Map<String, Object> params = Maps.newHashMap();
		params.put("ecc_type",1);
		
		List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.queryPageList(params);
		for (ExpressCompanyCommon ecc : eccs) {
			boolean delete = true;

			for (String ec_id : ec_ids) {

				if ((!RedPigCommonUtil.null2String(ec_id).equals(""))
						&& (ecc.getEcc_ec_id()
								.equals(RedPigCommonUtil.null2Long(ec_id)))) {
					delete = false;
				}
			}
			if (delete) {
				this.expressCompanyCommonService.deleteById(ecc.getId());
			}
		}
		
		for (String ec_id1 : ec_ids) {
			if (!CommUtil.null2String(ec_id1).equals("")) {
				params.clear();
				params.put("ecc_ec_id", CommUtil.null2Long(ec_id1));
				params.put("ecc_type", 1);
				
				eccs = this.expressCompanyCommonService.queryPageList(params);
				for (ExpressCompanyCommon ecc : eccs) {
					this.expressCompanyCommonService.deleteById(ecc.getId());
				}
				
				ExpressCompany ec = this.expressCompanyService
						.selectByPrimaryKey(CommUtil.null2Long(ec_id1));
				ExpressCompanyCommon ecc = new ExpressCompanyCommon();
				ecc.setAddTime(new Date());
				ecc.setEcc_code(ec.getCompany_mark());
				ecc.setEcc_ec_id(ec.getId());
				ecc.setEcc_name(ec.getCompany_name());
				ecc.setEcc_template(ec.getCompany_template());
				ecc.setEcc_template_heigh(ec.getCompany_template_heigh());
				ecc.setEcc_template_width(ec.getCompany_template_width());
				ecc.setEcc_template_offset(ec.getCompany_template_offset());
				ecc.setEcc_type(1);
				ecc.setEcc_ec_type(ec.getCompany_type());
				this.expressCompanyCommonService.saveEntity(ecc);
			}
		}
		mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request) + "/ecc_list");
		return mv;
	}
	
	/**
	 * 常用物流列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "常用物流列表", value = "/ecc_list*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_list" })
	public ModelAndView ecc_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ecc_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		params.put("ecc_type", 1);
		
		params.put("pageSize", 25);
		
		IPageList pList = this.expressCompanyCommonService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 设置为默认物流
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "设置为默认物流", value = "/ecc_default*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_default" })
	public void ecc_default(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((!CommUtil.null2String(id).equals("")) && (obj.getEcc_type() == 1)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("ecc_default", 1);
			params.put("ecc_type", 1);
			
			List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.queryPageList(params);
			
			for (ExpressCompanyCommon ecc : eccs) {
				ecc.setEcc_default(0);
				this.expressCompanyCommonService.updateById(ecc);
			}
			obj.setEcc_default(1);
			this.expressCompanyCommonService.updateById(obj);
			ret = true;
		} else {
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
	 * 取消默认物流
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "取消默认物流", value = "/ecc_default_cancle*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_default_cancle" })
	public void ecc_default_cancle(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((!CommUtil.null2String(id).equals("")) && (obj.getEcc_type() == 1)) {
			obj.setEcc_default(0);
			this.expressCompanyCommonService.updateById(obj);
			ret = true;
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
	 * 常用物流配置
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "常用物流配置", value = "/ecc_print_view*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_print_view" })
	public ModelAndView ecc_print_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ecc_print_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getEcc_type() == 1) {
			Map offset_map = JSON.parseObject(CommUtil.null2String(obj.getEcc_template_offset()));
			if (CommUtil.null2String(obj.getEcc_template()).equals("")) {
				mv = new RedPigJModelAndView("admin/blue/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该快递暂无模板，无法打印");
			} else {
				mv.addObject("offset_map", offset_map);
				mv.addObject("obj", obj);
			}
		}
		return mv;
	}
	
	/**
	 * 常用物流打印偏移量设置
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "常用物流打印偏移量设置", value = "/ecc_print_set*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_print_set" })
	public ModelAndView ecc_print_set(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ecc_print_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getEcc_type() == 1) {
			Map offset_map = JSON.parseObject(obj.getEcc_template_offset());
			if (CommUtil.null2String(obj.getEcc_template()).equals("")) {
				mv = new RedPigJModelAndView("admin/blue/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该快递暂无模板，无法设置");
			} else {
				mv.addObject("obj", obj);
				mv.addObject("offset_map", offset_map);
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request) + "/ecc_list");
		}
		return mv;
	}
	
	/**
	 * 常用物流打印偏移量配置保存
	 * @param request
	 * @param response
	 * @param id
	 * @param left_offset
	 * @param top_offset
	 * @return
	 */
	@SuppressWarnings("unused")
	@SecurityMapping(title = "常用物流打印偏移量配置保存", value = "/ecc_print_set_save*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_print_set_save" })
	public ModelAndView ecc_print_set_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String left_offset,
			String top_offset) {
		boolean ret = true;
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getEcc_type() == 1) {
			Map offset_map = JSON.parseObject(obj.getEcc_template_offset());
			if (offset_map.get("receipt_user_left") != null) {
				Iterator it = offset_map.keySet().iterator();
				while (it.hasNext()) {
					String key = CommUtil.null2String(it.next());
					if (key.indexOf("_left") > 0) {
						float value = CommUtil.null2Float(offset_map.get(key));
						System.out.println(key + "  " + value);
						value += CommUtil.null2Float(left_offset);
						offset_map.put(key,
								CommUtil.null2String(Float.valueOf(value)));
					}
					if (key.indexOf("_top") > 0) {
						float value = CommUtil.null2Float(offset_map.get(key));
						System.out.println(key + "  " + value);
						value += CommUtil.null2Float(top_offset);
						offset_map.put(key,
								CommUtil.null2String(Float.valueOf(value)));
					}
				}
				obj.setEcc_template_offset(JSON.toJSONString(offset_map));
				this.expressCompanyCommonService.updateById(obj);
				ret = true;
			}
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request) + "/ecc_list");
		mv.addObject("op_title", "运费打印模板偏移量保存成功");
		return mv;
	}
	
	/**
	 * 自建物流模板
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自建物流模板", value = "/ecc_create*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_create" })
	public ModelAndView ecc_create(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ecc_create.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj != null) && (obj.getEcc_type() == 1)) {
			mv.addObject("obj", obj);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request) + "/seller/ecc_list");
		}
		return mv;
	}
	
	/**
	 * 自建物流模板保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param ecc_template_width
	 * @param ecc_template_heigh
	 * @return
	 */
	@SecurityMapping(title = "自建物流模板保存", value = "/ecc_template_save*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_template_save" })
	public ModelAndView ecc_template_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String ecc_template_width, String ecc_template_heigh) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		if ((obj != null) && (obj.getEcc_type() == 1)) {
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "ecc_template";
			Map<String, Object> map = Maps.newHashMap();
			try {
				map = CommUtil.saveFileToServer(request, "ecc_template_acc",
						saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					String company_template = uploadFilePath + "/ecc_template/"
							+ CommUtil.null2String(map.get("fileName"));
					obj.setEcc_template(company_template);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			obj.setEcc_from_type(1);
			obj.setEcc_template_offset(null);
			obj.setEcc_template_heigh(CommUtil.null2Int(ecc_template_heigh));
			obj.setEcc_template_width(CommUtil.null2Int(ecc_template_width));
			this.expressCompanyCommonService.updateById(obj);
			ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ecc_list");
			mv.addObject("op_title", "自建物流模板保存成功");
			return mv;
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request) + "/ecc_list");
		mv.addObject("op_title", "物流参数错误，无法设置");
		return mv;
	}
	
	/**
	 * 绑定系统默认物流
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "绑定系统默认物流", value = "/ecc_bind_defalut_template*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_bind_defalut_template" })
	public ModelAndView ecc_bind_defalut_template(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		if ((obj != null) && (obj.getEcc_type() == 1)) {
			ExpressCompany ec = this.expressCompanyService.selectByPrimaryKey(obj
					.getEcc_ec_id());
			obj.setEcc_type(1);
			obj.setEcc_code(ec.getCompany_mark());
			obj.setEcc_name(ec.getCompany_name());
			obj.setEcc_template(ec.getCompany_template());
			obj.setEcc_template_heigh(ec.getCompany_template_heigh());
			obj.setEcc_template_width(ec.getCompany_template_width());
			obj.setEcc_template_offset(ec.getCompany_template_offset());
			obj.setEcc_from_type(0);
			obj.setEcc_ec_type(ec.getCompany_type());
			this.expressCompanyCommonService.updateById(obj);
			ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ecc_list?currentPage=" + currentPage);
			mv.addObject("op_title", "恢复系统默认模板成功");
			return mv;
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/ecc_list?currentPage=" + currentPage);
		mv.addObject("op_title", "物流参数错误，无法设置");
		return mv;
	}
	
	/**
	 * 自建物流模板保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自建物流模板保存", value = "/ecc_design*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_design" })
	public ModelAndView ecc_design(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ecc_design.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj != null) && (obj.getEcc_type() == 1)) {
			Map offset_map = JSON.parseObject(CommUtil.null2String(obj
					.getEcc_template_offset()));
			mv.addObject("offset_map", offset_map);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/ecc_list?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 自建物流模板保存
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "自建物流模板保存", value = "/ecc_design_save*", rtype = "admin", rname = "常用物流", rcode = "ecc_self", rgroup = "自营")
	@RequestMapping({ "/ecc_design_save" })
	public void ecc_design_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		boolean ret = true;
		if ((obj != null) && (obj.getEcc_type() == 1)) {
			Map<String, Object> map = Maps.newHashMap();
			Enumeration enum1 = request.getParameterNames();
			while (enum1.hasMoreElements()) {
				String paramName = (String) enum1.nextElement();
				String value = request.getParameter(paramName);
				if ((!paramName.equals("id"))
						&& (!CommUtil.null2String(value).equals(""))
						&& (!CommUtil.null2String(value).equals("null"))) {
					map.put(paramName, value);
				}
			}
			obj.setEcc_template_offset(JSON.toJSONString(map));
			this.expressCompanyCommonService.updateById(obj);
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
