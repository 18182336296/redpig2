package com.redpigmall.manage.admin.action.systemset;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.SysLog;
import com.redpigmall.domain.User;
/**
 * 
 * <p>
 * Title: RedPigSuperManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统设置栏目下面的功能
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
 * @date 2016-5-9
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({"unused"})
@Controller
public class RedPigSystemSetManageAction extends BaseAction implements ServletContextAware{
	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	/**
	 * 二级域名设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "二级域名设置", value = "/set_second_domain*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping({ "/set_second_domain" })
	public ModelAndView set_second_domain(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_second_domain.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
	
	/**
	 * 短信设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "短信设置", value = "/set_sms*", rtype = "admin", rname = "短信设置", rcode = "admin_set_sms", rgroup = "设置")
	@RequestMapping({ "/set_sms" })
	public ModelAndView set_sms(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_sms_setting.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
	
	/**
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@SecurityMapping(title = "管理员列表", value = "/admin_list*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping({ "/admin_list" })
	public ModelAndView admin_list(String currentPage, String orderBy,
			String orderType, HttpServletRequest request,
			HttpServletResponse response, String userName) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/admin_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("redPigUserName", userName);
		maps.put("redPigUserRole1", "ADMIN");
		maps.put("redPigUserRole2", "ADMIN_SELLER");
		maps.put("deleteStatus", 0);

		IPageList pList = this.redPigUserService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("userRole", "ADMIN");
		return mv;
	}
	
	/**
	 * 管理员操作日志列表
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "管理员操作日志", value = "/admin_log_list*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping({ "/admin_log_list" })
	public ModelAndView admin_log_list(String currentPage, String orderBy,
			String orderType, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/admin_log_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		
		IPageList pList = this.redPigSysLogService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 管理员删除操作日志
	 * @param currentPage
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "管理员操作日志", value = "/admin_log_delete*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping({ "/admin_log_delete" })
	public String admin_log_delete(String currentPage,
			HttpServletRequest request, HttpServletResponse response,
			String mulitId, String type) {
		if ((type != null) && (type.equals("all"))) {
			List<SysLog> list = this.redPigSysLogService.queryPageList(null);
			this.redPigSysLogService.batchDeleteObj(list);
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					this.redPigSysLogService.delete(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:/admin_log_list?currentPage=" + currentPage;
	}
	
	/**
	 * 管理员删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "管理员删除", value = "/admin_del*", rtype = "admin", rname = "管理员管理", rcode = "admin_manage", rgroup = "设置")
	@RequestMapping({ "/admin_del" })
	public String admin_del(HttpServletRequest request, String mulitId,String currentPage) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				User user = this.redPigUserService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if (!user.getUsername().equals("admin")) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("user_id", CommUtil.null2Long(id));
					List<SysLog> logs = this.redPigSysLogService.queryPageList(params);
					List<Long> list = Lists.newArrayList();
					for (SysLog log : logs) {
						list.add(log.getId());
					}
					this.redPigSysLogService.batchDelete(list);
					
					String ran = CommUtil.randomInt(5);
					user.setDeleteStatus(-1);
					
					// 将角色关系同时删除
					redPigUserService.deleteUserRole(user.getId(),user.getRoles());
					
					user.getRoles().clear();
					
					user.setUserName("_" + user.getUserName() + "_" + ran);
					user.setMobile("_" + user.getMobile() + "_" + ran);
					this.redPigUserService.updateById(user);
				}
			}
		}
		return "redirect:admin_list?currentPage=" + currentPage;
	}

	/**
	 * Email设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "Email设置", value = "/set_email*", rtype = "admin", rname = "Email设置", rcode = "admin_set_email", rgroup = "设置")
	@RequestMapping({ "/set_email" })
	public ModelAndView set_email(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_email_setting.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
	
	/**
	 * SEO设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "SEO设置", value = "/set_seo*", rtype = "admin", rname = "SEO设置", rcode = "admin_set_seo", rgroup = "设置")
	@RequestMapping({ "/set_seo" })
	public ModelAndView set_seo(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_seo_setting.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
	
	/**
	 * 上传设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "上传设置", value = "/set_image*", rtype = "admin", rname = "上传设置", rcode = "admin_set_image", rgroup = "设置")
	@RequestMapping({ "/set_image" })
	public ModelAndView set_image(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_image_setting.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
	
	/**
	 * 常规设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "常规设置", value = "/set_site*", rtype = "admin", rname = "常规设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping({ "/set_site" })
	public ModelAndView site_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_site_setting.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		return mv;
	}
}
