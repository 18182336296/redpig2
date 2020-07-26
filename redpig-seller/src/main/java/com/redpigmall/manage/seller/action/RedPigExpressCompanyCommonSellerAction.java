package com.redpigmall.manage.seller.action;

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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.domain.ExpressCompanyCommon;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigExpressCompanyCommonSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家快递公司相关管理
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
 * @date 2014-10-29
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
public class RedPigExpressCompanyCommonSellerAction extends BaseAction {
	/**
	 * 常用快递公司配置
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "常用快递公司配置", value = "/ecc_set*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_set" })
	public ModelAndView ecc_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ecc_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("company_status", Integer.valueOf(0));
		params.put("orderBy", "company_sequence");
		params.put("orderType", "asc");
		
		List<ExpressCompany> ecs = this.expressCompanyService.queryPageList(params);
		
		mv.addObject("ecs", ecs);
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 常用快递公司列表
	 * @param request
	 * @param response
	 * @param ids
	 */
	@SecurityMapping(title = "常用快递公司列表", value = "/ecc_list*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_save" })
	public void ecc_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String ids) {
		boolean ret = true;
		String[] ec_ids = ids.split(",");
		Map<String, Object> params = Maps.newHashMap();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		params.put("ecc_type", Integer.valueOf(0));
		params.put("ecc_store_id", store.getId());
		
		List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.queryPageList(params);
		
		for (ExpressCompanyCommon ecc : eccs) {
			boolean delete = true;

			for (String ec_id : ec_ids) {

				if ((!CommUtil.null2String(ec_id).equals(""))
						&& (ecc.getEcc_ec_id()
								.equals(CommUtil.null2Long(ec_id)))) {
					delete = false;
				}
			}
			if (delete) {
				this.expressCompanyCommonService.deleteById(ecc.getId());
			}
		}

		for (String ec_id : ec_ids) {
			if (!CommUtil.null2String(ec_id).equals("")) {
				params.clear();
				params.put("ecc_ec_id", CommUtil.null2Long(ec_id));
				params.put("ecc_type", Integer.valueOf(0));
				params.put("ecc_store_id", store.getId());
				
				eccs = this.expressCompanyCommonService.queryPageList(params);
				
				if (eccs.size() == 0) {
					ExpressCompany ec = this.expressCompanyService.selectByPrimaryKey(CommUtil.null2Long(ec_id));
					ExpressCompanyCommon ecc = new ExpressCompanyCommon();
					ecc.setAddTime(new Date());
					ecc.setEcc_code(ec.getCompany_mark());
					ecc.setEcc_ec_id(ec.getId());
					ecc.setEcc_name(ec.getCompany_name());
					ecc.setEcc_store_id(store.getId());
					ecc.setEcc_template(ec.getCompany_template());
					ecc.setEcc_template_heigh(ec.getCompany_template_heigh());
					ecc.setEcc_template_width(ec.getCompany_template_width());
					ecc.setEcc_template_offset(ec.getCompany_template_offset());
					ecc.setEcc_type(0);
					ecc.setEcc_ec_type(ec.getCompany_type());
					this.expressCompanyCommonService.saveEntity(ecc);
				}
			}
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
	 * 常用快递公司列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "常用快递公司列表", value = "/ecc_list*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_list" })
	public ModelAndView ecc_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ecc_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,25, "addTime", "desc");
		maps.put("ecc_type", 0);
		maps.put("ecc_store_id", store.getId());
		
		IPageList pList = this.expressCompanyCommonService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 常用快递公司删除
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "常用快递公司删除", value = "/ecc_delete*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_delete" })
	public String ecc_deleteById(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ExpressCompanyCommon ecc = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (ecc.getEcc_store_id().equals(store.getId())) {
			this.expressCompanyCommonService.deleteById(CommUtil.null2Long(id));
		}
		return "redirect:ecc_list?currentPage=" + currentPage;
	}
	
	/**
	 * 设置默认快递公司
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "设置默认快递公司", value = "/ecc_default*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_default" })
	public void ecc_default(HttpServletRequest request,
			HttpServletResponse response, String id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		ExpressCompanyCommon obj = this.expressCompanyCommonService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((!CommUtil.null2String(id).equals(""))
				&& (obj.getEcc_store_id().equals(store.getId()))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("ecc_default", Integer.valueOf(1));
			params.put("ecc_type", Integer.valueOf(0));
			params.put("ecc_store_id", store.getId());
			
			List<ExpressCompanyCommon> eccs = this.expressCompanyCommonService.queryPageList(params);
			
			for (ExpressCompanyCommon ecc : eccs) {
				ecc.setEcc_default(0);
				this.expressCompanyCommonService.updateById(ecc);
			}
			
			obj.setEcc_default(1);
			this.expressCompanyCommonService.updateById(obj);
		} else {
			
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 取消默认快递公司
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "取消默认快递公司", value = "/ecc_default_cancle*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_default_cancle" })
	public void ecc_default_cancle(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		ExpressCompanyCommon obj = this.expressCompanyCommonService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((!CommUtil.null2String(id).equals(""))
				&& (obj.getEcc_store_id().equals(store.getId()))) {
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
	 * 运费单打印测试
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "运费单打印测试", value = "/ecc_print_view*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_print_view" })
	public ModelAndView ecc_print_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ecc_print_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((obj != null) && (obj.getEcc_store_id().equals(store.getId()))) {
			Map offset_map = JSON.parseObject(obj.getEcc_template_offset());
			if (CommUtil.null2String(obj.getEcc_template()).equals("")) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
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
	 * 运费单打印设置
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "运费单打印设置", value = "/ecc_print_set*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_print_set" })
	public ModelAndView ecc_print_set(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ecc_print_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		ExpressCompanyCommon obj = this.expressCompanyCommonService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((obj != null) && (obj.getEcc_store_id().equals(store.getId()))) {
			Map offset_map = JSON.parseObject(obj.getEcc_template_offset());
			if (CommUtil.null2String(obj.getEcc_template()).equals("")) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该快递暂无模板，无法设置");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/ecc_list");
			} else {
				mv.addObject("obj", obj);
				mv.addObject("offset_map", offset_map);
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request) + "/ecc_list");
		}
		return mv;
	}
	
	/**
	 * 快递单偏移量保存
	 * @param request
	 * @param response
	 * @param id
	 * @param left_offset
	 * @param top_offset
	 */
	@SecurityMapping(title = "快递单偏移量保存", value = "/ecc_print_set_save*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_print_set_save" })
	public void ecc_print_set_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String left_offset,
			String top_offset) {
		boolean ret = true;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Store store = user.getStore();
		
		ExpressCompanyCommon obj = this.expressCompanyCommonService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if ((obj != null) && (obj.getEcc_store_id().equals(store.getId()))) {
			Map offset_map = JSON.parseObject(obj.getEcc_template_offset());
			if (offset_map.get("receipt_user_left") != null) {
				Iterator it = offset_map.keySet().iterator();
				while (it.hasNext()) {
					String key = CommUtil.null2String(it.next());
					if (key.indexOf("_left") > 0) {
						float value = CommUtil.null2Float(offset_map.get(key));
						value += CommUtil.null2Float(left_offset);
						offset_map.put(key,CommUtil.null2String(Float.valueOf(value)));
					}
					if (key.indexOf("_top") > 0) {
						float value = CommUtil.null2Float(offset_map.get(key));
						value += CommUtil.null2Float(top_offset);
						offset_map.put(key,CommUtil.null2String(Float.valueOf(value)));
					}
				}
				obj.setEcc_template_offset(JSON.toJSONString(offset_map));
				this.expressCompanyCommonService.updateById(obj);
				ret = true;
			}
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
	 * 自建物流模板配置
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自建物流模板配置", value = "/ecc_create*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_create" })
	public ModelAndView ecc_create(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ecc_create.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((obj != null) && (obj.getEcc_store_id().equals(store.getId()))) {
			mv.addObject("obj", obj);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request) + "/ecc_list");
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
	@SecurityMapping(title = "自建物流模板保存", value = "/ecc_template_save*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_template_save" })
	public ModelAndView ecc_template_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String ecc_template_width, String ecc_template_heigh) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((obj != null) && (obj.getEcc_store_id().equals(store.getId()))) {
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "ecc_template";
			Map<String, Object> map = Maps.newHashMap();
			try {
				map = CommUtil.saveFileToServer(request, "ecc_template_acc", saveFilePathName, "", null);
				if (map.get("fileName") != "") {
					String company_template = uploadFilePath + "/ecc_template/" + CommUtil.null2String(map.get("fileName"));
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
			ModelAndView mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", CommUtil.getURL(request)
					+ "/ecc_list?currentPage=" + currentPage);
			mv.addObject("op_title", "自建物流模板保存成功");
			return mv;
		}
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "物流参数错误，无法设置");
		mv.addObject("url", CommUtil.getURL(request) + "/ecc_list");
		return mv;
	}
	
	/**
	 * 恢复系统默认物流
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "恢复系统默认物流", value = "/ecc_bind_defalut_template*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_bind_defalut_template" })
	public ModelAndView ecc_bind_defalut_template(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		if ((id != null) && (!"".equals(id))) {
			ExpressCompanyCommon obj = this.expressCompanyCommonService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if ((obj != null) && (obj.getEcc_store_id().equals(store.getId()))) {
				ExpressCompany ec = this.expressCompanyService.selectByPrimaryKey(obj.getEcc_ec_id());
				obj.setEcc_code(ec.getCompany_mark());
				obj.setEcc_name(ec.getCompany_name());
				obj.setEcc_store_id(store.getId());
				obj.setEcc_template(ec.getCompany_template());
				obj.setEcc_template_heigh(ec.getCompany_template_heigh());
				obj.setEcc_template_width(ec.getCompany_template_width());
				obj.setEcc_template_offset(ec.getCompany_template_offset());
				obj.setEcc_from_type(0);
				obj.setEcc_ec_type(ec.getCompany_type());
				this.expressCompanyCommonService.updateById(obj);
				ModelAndView mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/ecc_list?currentPage=" + currentPage);
				mv.addObject("op_title", "恢复系统默认模板成功");
				return mv;
			}
			ModelAndView mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error?currentPage="
							+ currentPage, this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/ecc_list?currentPage=" + currentPage);
			return mv;
		}
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_error?currentPage="
						+ currentPage, this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "物流参数错误，无法设置");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/ecc_list?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 自建物流模板设计
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自建物流模板设计", value = "/ecc_design*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_design" })
	public ModelAndView ecc_design(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ecc_design.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((obj != null) && (obj.getEcc_store_id().equals(store.getId()))) {
			Map offset_map = JSON.parseObject(CommUtil.null2String(obj.getEcc_template_offset()));
			mv.addObject("offset_map", offset_map);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "物流参数错误，无法设置");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/ecc_list?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 自建物流模板设计
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "自建物流模板设计", value = "/ecc_design*", rtype = "seller", rname = "常用物流", rcode = "seller_ecc", rgroup = "交易管理")
	@RequestMapping({ "/ecc_design_save" })
	public void ecc_design_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ExpressCompanyCommon obj = this.expressCompanyCommonService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		boolean ret = true;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((obj != null) && (obj.getEcc_store_id().equals(store.getId()))) {
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
