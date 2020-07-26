package com.redpigmall.manage.admin.action.operative.version;

import java.io.File;
import java.io.IOException;
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
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.AppOperativeVersion;
import com.redpigmall.manage.admin.action.base.BaseAction;

@Controller
public class RedPigOperativeVersionManagementAction extends BaseAction{
	
	@SecurityMapping(title = "运营版本管理", value = "/app_op_version_list*", rtype = "admin", rname = "运营版本管理", rcode = "op_version", rgroup = "运营")
	@RequestMapping({ "/app_op_version_list" })
	public ModelAndView app_op_version_list(HttpServletRequest request,
			HttpServletResponse response,
			String currentPage, 
			String orderBy,
			String orderType, 
			String ad_title) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/app_op_version_management.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		if (!CommUtil.null2String(ad_title).equals("")) {
			params.put("ad_title_like", ad_title.trim());
		}
		
		IPageList pList = this.redPigOperativeVersionService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("ad_title", ad_title);
		return mv;
	}
	
	@SecurityMapping(title = "添加运营版本页面", value = "/app_op_version_add*", rtype = "admin", rname = "运营版本管理", rcode = "op_version", rgroup = "运营")
	@RequestMapping({ "/app_op_version_add" })
	public ModelAndView app_op_version_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/app_op_version_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "保存运营版本", value = "/app_op_version_save*", rtype = "admin", rname = "运营版本管理", rcode = "op_version", rgroup = "运营")
	@RequestMapping({ "/app_op_version_save" })
	public ModelAndView app_op_version_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		AppOperativeVersion version = WebForm.toPo(request, AppOperativeVersion.class);
		version.setAddTime(new Date());
		String uploadFilePath = this.redPigSysConfigService.getSysConfig().getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "app_op_version_pop";
		String saveFilePathName1 = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "app_op_version_icon";
		Map<String,Object> map = Maps.newHashMap();
		try {
				map = CommUtil.saveFileToServer(request, "idx_pop_img", saveFilePathName,null, null);
				if (map.get("fileName") != null && !map.get("fileName").equals("")) {
					// 拼出图片服务器地址
					version.setIdx_pop_img(uploadFilePath + File.separator + "app_op_version_pop" + File.separator + CommUtil.null2String(map.get("fileName")));
				}
				map = CommUtil.saveFileToServer(request, "icon_a", saveFilePathName1,null, null);
				if (map.get("fileName") != null && !map.get("fileName").equals("")) {
					version.setIcon_a(uploadFilePath + File.separator + "app_op_version_icon" + File.separator + CommUtil.null2String(map.get("fileName")));
				}
				map = CommUtil.saveFileToServer(request, "icon_b", saveFilePathName1,null, null);
				if (map.get("fileName") != null && !map.get("fileName").equals("")) {
					version.setIcon_b(uploadFilePath + File.separator + "app_op_version_icon" + File.separator + CommUtil.null2String(map.get("fileName")));
				}
				map = CommUtil.saveFileToServer(request, "icon_c", saveFilePathName1,null, null);
				if (map.get("fileName") != null && !map.get("fileName").equals("")) {
					version.setIcon_c(uploadFilePath + File.separator + "app_op_version_icon" + File.separator + CommUtil.null2String(map.get("fileName")));
				}
				map = CommUtil.saveFileToServer(request, "icon_d", saveFilePathName1,null, null);
				if (map.get("fileName") != null && !map.get("fileName").equals("")) {
					version.setIcon_d(uploadFilePath + File.separator + "app_op_version_icon" + File.separator + CommUtil.null2String(map.get("fileName")));
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (id != null && !id.equals("")) {
			version.setId(CommUtil.null2Long(id));
			this.redPigOperativeVersionService.updateByPrimaryKeySelective(version);
		} else {
			this.redPigOperativeVersionService.insertSelective(version);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/app_op_version_list?currentPage=" + currentPage);
		mv.addObject("op_title", "保存成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/app_op_version_add?currentPage=" + currentPage);
		return mv;
	}
	
	@SecurityMapping(title = "运营版本编辑", value = "/app_op_version_edit*", rtype = "admin", rname = "运营版本管理", rcode = "advert_admin", rgroup = "运营")
	@RequestMapping({ "/app_op_version_edit" })
	public ModelAndView app_op_version_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/app_op_version_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			AppOperativeVersion version = this.redPigOperativeVersionService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", version);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
			
}
