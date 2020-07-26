package com.redpigmall.manage.admin.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigWeixinNavigatorManageAction.java
 * </p>
 * 
 * <p>
 * Description:微信端导航设置
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinNavigatorManageAction extends BaseAction {

	/**
	 * 微信导航栏图标设置
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "微信导航栏图标设置", value = "/weixin_navigator*", rtype = "admin", rname = "微信端导航", rcode = "admin_weixin_navigator", rgroup = "运营")
	@RequestMapping({ "/weixin_navigator" })
	public ModelAndView weixin_navigator(HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_navigator.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String[] strs = this.configService.getSysConfig().getImageSuffix()
				.split("\\|");
		StringBuffer sb = new StringBuffer();
		for (String str : strs) {
			sb.append("." + str + ",");
		}
		mv.addObject("imageSuffix1", sb);
		SysConfig config = this.configService.getSysConfig();
		String weixin_navigator = config.getWeixin_navigator();
		Object weixin_nav_map;
		if ((weixin_navigator != null) && (!weixin_navigator.equals(""))) {
			weixin_nav_map = JSON.parseObject(weixin_navigator);
		} else {
			weixin_nav_map = Maps.newHashMap();
		}
		mv.addObject("weixin_nav_map", weixin_nav_map);

		return mv;
	}

	/**
	 * 微信导航栏图标自定义开关
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param value
	 */
	@SecurityMapping(title = "微信导航栏图标自定义开关", value = "/weixin_navigator_enable*", rtype = "admin", rname = "微信端导航", rcode = "admin_weixin_navigator", rgroup = "运营")
	@RequestMapping({ "/weixin_navigator_enable" })
	public void weixin_navigator_enable(HttpServletRequest request,
			HttpServletResponse response, String name, String value) {

		Map jsonmap = Maps.newHashMap();
		SysConfig config = this.configService.getSysConfig();
		String weixin_navigator = config.getWeixin_navigator();
		Map weixin_nav_map;
		if ((weixin_navigator != null) && (!weixin_navigator.equals(""))) {
			weixin_nav_map = JSON.parseObject(weixin_navigator);
		} else {
			weixin_nav_map = Maps.newHashMap();
		}
		weixin_nav_map.put(name, value);
		config.setWeixin_navigator(JSON.toJSONString(weixin_nav_map));
		this.configService.update(config);
		jsonmap.put("status", value);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(jsonmap));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信导航栏图标上
	 * 
	 * @param request
	 * @param response
	 * @param name
	 */
	@SecurityMapping(title = "微信导航栏图标上传", value = "/weixin_navigator_upload*", rtype = "admin", rname = "微信端导航", rcode = "admin_weixin_navigator", rgroup = "运营")
	@RequestMapping({ "/weixin_navigator_upload" })
	public void weixin_navigator_upload(HttpServletRequest request,
			HttpServletResponse response, String name) {

		Map jsonmap = Maps.newHashMap();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		String img_url = "";

		String weixin_navigator = config.getWeixin_navigator();
		Map weixin_nav_map;
		if ((weixin_navigator != null) && (!weixin_navigator.equals(""))) {
			weixin_nav_map = JSON.parseObject(weixin_navigator);
		} else {
			weixin_nav_map = Maps.newHashMap();
		}
		try {
			String filePath = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "weixin_navigator";
			CommUtil.createFolder(filePath);
			path = uploadFilePath + "/weixin_navigator";
			Map<String, Object> map = CommUtil.saveFileToServer(request, name,
					filePath, null, null);
			Accessory image = new Accessory();
			image.setUser(user);
			image.setAddTime(new Date());
			image.setExt((String) map.get("mime"));
			image.setPath(path);
			image.setWidth(CommUtil.null2Int(map.get("width")));
			image.setHeight(CommUtil.null2Int(map.get("height")));
			image.setName(CommUtil.null2String(map.get("fileName")));
			image.setInfo("");
			this.accessoryService.save(image);
			img_url = image.getPath() + "/" + image.getName();
			weixin_nav_map.put(name, img_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		config.setWeixin_navigator(JSON.toJSONString(weixin_nav_map));
		this.configService.update(config);
		jsonmap.put("img_url", img_url);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(jsonmap));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
