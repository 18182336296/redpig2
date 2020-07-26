package com.redpigmall.manage.admin.action.operative.version;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.AppVersion;
import com.redpigmall.manage.admin.action.base.BaseAction;
@Controller
public class RedPigVersionManageAction extends BaseAction{
	
	@SecurityMapping(title = "版本管理", value = "/app_version_list*", rtype = "admin", rname = "版本管理", rcode = "version", rgroup = "运营")
	@RequestMapping({ "/app_version_list" })
	public ModelAndView version_management(HttpServletRequest request,
			HttpServletResponse response,
			String currentPage, 
			String orderBy,
			String orderType, 
			String ad_title) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/app_version_management.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		if (!CommUtil.null2String(ad_title).equals("")) {
			params.put("ad_title_like", ad_title.trim());
		}
		
		IPageList pList = this.redPigVersionService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ad_title", ad_title);
		return mv;
	}
	
	@SecurityMapping(title = "添加版本页面", value = "/app_version_add*", rtype = "admin", rname = "版本管理", rcode = "version", rgroup = "运营")
	@RequestMapping({ "/app_version_add" })
	public ModelAndView app_version_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/app_version_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
//		Map<String,Object> params = Maps.newHashMap();
//		
//		List<AppVersion> advs = this.redPigAdvertService.queryPageList(params);
//		
//		mv.addObject("advs", advs);
		return mv;
	}
	
	/**
	 * 版本保存
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param app_version
	 * @param update_info
	 * @return
	 * @since JDK 1.8
	 */
	@SecurityMapping(title = "版本保存", value = "/app_version_save*", rtype = "admin", rname = "版本管理", rcode = "version", rgroup = "运营")
	@RequestMapping({ "/app_version_save" })
	public ModelAndView app_version_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id,
			String currentPage, 
			String app_version,
			String update_info) {
		
		AppVersion version = new AppVersion();
		version.setAddTime(new Date());
		version.setApp_version(app_version);
		version.setUpdate_info(update_info);
		if (id != null && (!id.equals(""))) {
			version.setId(Long.valueOf(id));
			redPigVersionService.updateByPrimaryKeySelective(version);
		} else {
			redPigVersionService.insertSelective(version);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/app_version_list?currentPage=" + currentPage);
		mv.addObject("op_title", "保存成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/app_version_add?currentPage=" + currentPage);
		return mv;
	}
	
	@SecurityMapping(title = "版本编辑", value = "/app_version_edit*", rtype = "admin", rname = "版本管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/app_version_edit" })
	public ModelAndView app_version_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/app_version_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			AppVersion version = this.redPigVersionService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", version);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
}
