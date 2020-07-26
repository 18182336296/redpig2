package com.redpigmall.module.circle.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.module.circle.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Circle;
import com.redpigmall.domain.CircleClass;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigCircleManageAction.java
 * </p>
 * 
 * <p>
 * Description: 圈主管理圈子控制器，包括创建圈子、审核圈子中帖子，管理帖子等操作
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
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigCircleCreateViewAction extends BaseAction{
	
	/**
	 * 创建圈子
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "创建圈子", value = "/circle/create*", rtype = "buyer", rname = "圈子创建", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/create" })
	public ModelAndView circle_create(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView("circle/circle_create.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		boolean ret = false;
		SysConfig sc = this.configService.getSysConfig();
		String op_title = "系统不限制用户等级";
		if (this.configService.getSysConfig().getCircle_limit() == 0) {
			ret = true;
		} else {
			int level = 0;
			Map level_map = this.redPigIntegralViewTools.query_user_level(CommUtil.null2String(user.getId()));
			level = CommUtil.null2Int(level_map.get("level"));
			if (level >= sc.getCircle_limit()) {
				ret = true;
			} else {
				Map<String, Object> map = this.redPigIntegralViewTools
						.query_level(CommUtil.null2String(Integer.valueOf(sc
								.getCircle_limit())));
				op_title = "系统规定" + CommUtil.null2String(map.get("name"))
						+ "可申请并创建圈子";
			}
		}
		if (ret) {
			boolean flag = false;
			String count_msg = "系统设置的用户可创建圈子数量为0";
			if (user.getCircle_create_info() != null) {
				List<Map> maps = JSON.parseArray(user.getCircle_create_info(),
						Map.class);
				if (maps.size() < sc.getCircle_count()) {
					flag = true;
				} else {
					count_msg = "您已超出可申请数量，不可再申请";
				}
			} else if (sc.getCircle_count() != 0) {
				flag = true;
			}
			if (flag) {
				Map<String,Object> params = Maps.newHashMap();
				params.put("orderBy", "sequence");
				params.put("orderType", "asc");
				
				List<CircleClass> c_classes = this.circleclassService.queryPageList(params);
				
				if (c_classes.size() > 0) {
					String session_circle_create = "session_circle_create_" + UUID.randomUUID();
					request.getSession(false).setAttribute("session_circle_create", session_circle_create);
					mv.addObject("session_circle_create", session_circle_create);
					mv.addObject("c_classes", c_classes);
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "系统未添加类型信息，无法申请圈子");
					mv.addObject("url", CommUtil.getURL(request) + "/index");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", count_msg);
				mv.addObject("url", CommUtil.getURL(request) + "/circle/index");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", op_title);
			mv.addObject("url", CommUtil.getURL(request) + "/circle/index");
		}
		return mv;
	}

	/**
	 * 圈子保存
	 * 
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "创建圈子保存", value = "/circle/create_save*", rtype = "buyer", rname = "圈子创建", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/create_save" })
	public ModelAndView circle_create_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String class_id, String img_id,
			String session_circle_create) {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op_title = "申请成功，请等待审核";
		String session_circle_create1 = CommUtil.null2String(request
				.getSession(false).getAttribute("session_circle_create"));
		if (session_circle_create1.equals(session_circle_create)) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			CircleClass cclass = this.circleclassService.selectByPrimaryKey(CommUtil.null2Long(class_id));
			
			Circle circle = null;
			circle = (Circle) WebForm.toPo(request, Circle.class);
			circle.setAddTime(new Date());
			circle.setClass_id(cclass.getId().longValue());
			circle.setClass_name(cclass.getClassName());
			circle.setUser_id(user.getId().longValue());
			circle.setUserName(user.getUserName());
			if (this.configService.getSysConfig().getCircle_audit() == 0) {
				circle.setStatus(5);
				op_title = "您的圈子已成功开通";
			} else {
				circle.setStatus(0);
			}
			Accessory image = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(img_id));
			
			Map<String,Object> img_map = Maps.newHashMap();
			img_map.put("id", image.getId());
			img_map.put("src", image.getPath() + "/" + image.getName());
			circle.setPhotoInfo(JSON.toJSONString(img_map));
			this.circleService.saveEntity(circle);
			List<Map> maps = Lists.newArrayList();
			if (user.getCircle_create_info() != null) {
				maps = JSON.parseArray(user.getCircle_create_info(), Map.class);
			}
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", circle.getId());
			map.put("name", circle.getTitle());
			maps.add(map);
			user.setCircle_create_info(JSON.toJSONString(maps));
			this.userService.updateById(user);
			request.getSession(false).removeAttribute("session_circle_create");
			mv.addObject("url", "/circle/index");
			mv.addObject("op_title", op_title);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", "/circle/index");
			mv.addObject("op_title", "禁止表单重复提交");
		}
		return mv;
	}

	/**
	 * 圈子图片上传
	 * @param request
	 * @param response
	 * @param img_id
	 */
	@SecurityMapping(title = "圈子图片上传", value = "/circle/image_upload*", rtype = "buyer", rname = "圈子创建", rcode = "user_circle", rgroup = "圈子管理")
	@RequestMapping({ "/circle/image_upload" })
	public void circle_image_upload(HttpServletRequest request,
			HttpServletResponse response, String img_id) {
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "circle";
		Map<String, Object> map = Maps.newHashMap();
		Map json_map = Maps.newHashMap();
		Accessory img = null;
		String url = null;
		try {
			map = CommUtil.saveFileToServer(request, "image", saveFilePathName,"", null);
			String reg = ".+(.JPEG|.jpeg|.JPG|.jpg|.GIF|.gif|.BMP|.bmp|.PNG|.png|.tbi|.TBI)$";
			String imgp = (String) map.get("fileName");
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(imgp.toLowerCase());
			if (matcher.find()) {
				map = CommUtil.saveFileToServer(request, "image",
						saveFilePathName, "", null);
				if ((img_id != null) && (!img_id.equals(""))) {
					Accessory old_image = this.accessoryService
							.selectByPrimaryKey(CommUtil.null2Long(img_id));
					img = old_image;
					img.setName((String) map.get("fileName"));
					img.setExt("." + (String) map.get("mime"));
					img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					img.setPath(uploadFilePath + "/circle");
					img.setWidth(((Integer) map.get("width")).intValue());
					img.setHeight(((Integer) map.get("height")).intValue());
					img.setAddTime(new Date());
					this.accessoryService.updateById(img);
					url = CommUtil.getURL(request) + "/" + img.getPath() + "/"
							+ img.getName();
				} else {
					img = new Accessory();
					img.setName((String) map.get("fileName"));
					img.setExt("." + (String) map.get("mime"));
					img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					img.setPath(uploadFilePath + "/circle");
					img.setWidth(((Integer) map.get("width")).intValue());
					img.setHeight(((Integer) map.get("height")).intValue());
					img.setAddTime(new Date());
					this.accessoryService.saveEntity(img);
					url = CommUtil.getURL(request) + "/" + img.getPath() + "/"
							+ img.getName();
				}
				json_map.put("id", img.getId());
				json_map.put("src", url);
				json_map.put("ret", "true");
			} else {
				json_map.put("ret", "false");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
