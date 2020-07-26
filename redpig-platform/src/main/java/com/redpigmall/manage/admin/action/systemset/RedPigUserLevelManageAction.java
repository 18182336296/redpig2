package com.redpigmall.manage.admin.action.systemset;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.UserLevel;
import com.redpigmall.manage.admin.action.base.BaseAction;
/**
 * 
 * <p>
 * Title: RedPigUserLevelManageAction.java
 * </p>
 * 
 * <p>
 * Description: 会员等级
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
 * @date 2017-04-16
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigUserLevelManageAction extends BaseAction {
	/**
	 * 会员等级列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "会员等级列表", value = "/user_level*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping({ "/user_level" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/user_level.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		orderBy="sys_seq";
		orderType="desc";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.userlevelService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/user_level.html", "",
				params, pList, mv);
		return mv;
	}
	
	/**
	 * 会员等级删除
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "会员等级删除", value = "/user_level_del*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping({ "/user_level_del" })
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String id) {
		if (!id.equals("")) {
			
			this.userlevelService.deleteById(Long.valueOf(Long.parseLong(id)));
			
			SysConfig sc = this.configService.getSysConfig();
			List<Map> list = Lists.newArrayList();
			list = UpdateLevelJson(list);
			String user_creditrule = JSON.toJSONString(list);
			sc.setUser_level(user_creditrule);
			this.configService.updateById(sc);
		}
		return "redirect:user_level";
	}
	
	/**
	 * 会员等级新增与编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "会员等级新增与编辑", value = "/user_level_update*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping({ "/user_level_update" })
	public ModelAndView user_level_updateById(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/user_level_update.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> map = Maps.newHashMap();
		map.put("orderBy", "id");
		map.put("orderType", "desc");
		
		List<UserLevel> list = this.userlevelService.queryPageList(map);
		
		UserLevel last_level = null;
		if ((id != null) && (!"".equals(id))) {
			UserLevel userlevel = this.userlevelService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			for (int last_id = CommUtil.null2Int(id) - 1; last_id > 0; last_id--) {
				last_level = this.userlevelService.selectByPrimaryKey(CommUtil.null2Long(Integer.valueOf(last_id)));
				if (last_level != null) {
					break;
				}
			}
			
			mv.addObject("edit", Boolean.valueOf(true));
			mv.addObject("obj", userlevel);
		} else if (list.size() > 0) {
			last_level = (UserLevel) list.get(0);
		}
		mv.addObject("last_obj", last_level);
		return mv;
	}
	
	/**
	 * 会员等级保存
	 * @param request
	 * @param response
	 * @param level_name
	 * @param level_down
	 * @param level_up
	 * @param ico_style
	 * @param icon_sys
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "会员等级保存", value = "/user_level_save*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping({ "/user_level_save" })
	public ModelAndView user_level_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String level_name, String level_down,
			String level_up, String ico_style, String icon_sys, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		SysConfig sc = this.configService.getSysConfig();
		UserLevel userlevel = null;
		List<Map> list = Lists.newArrayList();
		if (id.equals("")) {
			userlevel = (UserLevel) WebForm.toPo(request, UserLevel.class);
			userlevel.setAddTime(new Date());
		} else {
			UserLevel obj = this.userlevelService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			userlevel = (UserLevel) WebForm.toPo(request, obj);
		}
		userlevel.setLevel_name(level_name);
		userlevel.setLevel_down(CommUtil.null2Int(level_down));
		userlevel.setLevel_up(CommUtil.null2Int(level_up));
		UserLevelInfoSave(request, ico_style, icon_sys, userlevel);
		if ((id != null) && (!"".equals(id))) {
			this.userlevelService.updateById(userlevel);
		} else {
			this.userlevelService.saveEntity(userlevel);
		}
		list = UpdateLevelJson(list);
		String user_creditrule = JSON.toJSONString(list);
		sc.setUser_level(user_creditrule);
		this.configService.updateById(sc);
		mv.addObject("list_url", CommUtil.getURL(request) + "/user_level");
		mv.addObject("op_title", "保存会员等级成功");
		return mv;
	}

	private List UpdateLevelJson(List<Map> list) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("orderBy", "sys_seq");
		params.put("orderType", "asc");
		
		List<UserLevel> level_list = this.userlevelService.queryPageList(params);
		
		int i = 1;
		for (UserLevel level : level_list) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("level", Integer.valueOf(i));
			map.put("name", level.getLevel_name());
			map.put("credit_down", Integer.valueOf(level.getLevel_down()));
			map.put("credit_up", Integer.valueOf(level.getLevel_up()));
			map.put("icon", level.getLevel_icon());
			if (level.getLevel_icon_type() == 0) {
				map.put("sys_seq", level.getSys_seq());
				map.put("style", "system");
			} else {
				map.put("style", "line");
			}
			list.add(map);
			i++;
		}
		return list;
	}

	private void UserLevelInfoSave(HttpServletRequest request,
			String ico_style, String icon_sys, UserLevel userlevel) {
		String path = "";
		if ((ico_style != null) && (!"".equals(ico_style))) {
			if (("0".equals(ico_style)) && (icon_sys != null)
					&& (!"".equals(icon_sys))) {
				path = "resources/style/common/images/userlevel_" + icon_sys
						+ ".png";
				userlevel.setLevel_icon_type(0);
				userlevel.setLevel_icon(path);
				userlevel.setSys_seq(icon_sys);
			} else if ("1".equals(ico_style)) {
				String uploadFilePath = this.configService.getSysConfig()
						.getUploadFilePath();
				String saveFilePathName = request.getSession()
						.getServletContext().getRealPath("/")
						+ uploadFilePath + File.separator + "acc";
				Map m = Maps.newHashMap();
				try {
					m = CommUtil.saveFileToServer(request, "ico_line",
							saveFilePathName, "", null);
					if (m.get("fileName") != "") {
						Accessory ico_img = new Accessory();
						ico_img.setName(CommUtil.null2String(m.get("fileName")));
						ico_img.setExt(CommUtil.null2String(m.get("mime")));
						ico_img.setSize(BigDecimal.valueOf(Double.parseDouble(m
								.get("fileSize").toString())));
						ico_img.setPath(uploadFilePath + "/acc");
						ico_img.setWidth(CommUtil.null2Int(m.get("width")));
						ico_img.setHeight(CommUtil.null2Int(m.get("height")));
						this.accessoryService.saveEntity(ico_img);
						path = ico_img.getPath() + "/" + ico_img.getName();
						userlevel.setLevel_icon_type(1);
						userlevel.setLevel_icon(path);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 判断会员等级名称唯一
	 * @param request
	 * @param response
	 * @param name
	 * @param id
	 * @throws IOException
	 */
	@SecurityMapping(title = "判断会员等级名称唯一", value = "/user_level_verify*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping({ "/user_level_verify" })
	public void level_name_verify(HttpServletRequest request,
			HttpServletResponse response, String name, String id)
			throws IOException {
		boolean ret = true;
		Map param = Maps.newHashMap();
		param.put("id_no", CommUtil.null2Long(id));
		param.put("level_name", name);
		
		List<UserLevel> level_list = this.userlevelService.queryPageList(param);
		
		if (level_list.size() > 0) {
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
	 * 更换会员等级图标
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "更换会员等级图标", value = "/user_level_ico*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping({ "/user_level_ico" })
	public ModelAndView level_ico(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/user_level_ico.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!"".equals(id))) {
			UserLevel userlevel = this.userlevelService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			mv.addObject("obj", userlevel);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 更换会员等级图标保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ico_style
	 * @param icon_sys
	 * @return
	 */
	@SecurityMapping(title = "更换会员等级图标保存", value = "/user_level_ico_save*", rtype = "admin", rname = "会员等级", rcode = "user_level", rgroup = "会员")
	@RequestMapping({ "/user_level_ico_save" })
	public String level_ico_sava(HttpServletRequest request,
			HttpServletResponse response, String id, String ico_style,
			String icon_sys) {
		SysConfig sc = this.configService.getSysConfig();
		Map<String, Object> map = Maps.newHashMap();
		if ((id != null) && (!"".equals(id))) {
			UserLevel userlevel = this.userlevelService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (userlevel != null) {
				UserLevelInfoSave(request, ico_style, icon_sys, userlevel);
				this.userlevelService.updateById(userlevel);
				List<Map> list = Lists.newArrayList();
				list = UpdateLevelJson(list);
				String user_creditrule = JSON.toJSONString(list);
				sc.setUser_level(user_creditrule);
				this.configService.updateById(sc);
			}
		}
		return "redirect:user_level";
	}
}
