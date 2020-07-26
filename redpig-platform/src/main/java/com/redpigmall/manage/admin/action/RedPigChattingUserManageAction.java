package com.redpigmall.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.ChattingConfig;
import com.redpigmall.domain.ChattingLog;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigChattingUserManageAction.java
 * </p>
 * 
 * <p>
 * Description: 客服管理
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
public class RedPigChattingUserManageAction extends BaseAction {
	/**
	 * 自营客服列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param chatting_nickname
	 * @param chatting_name
	 * @param chatting_user_name
	 * @return
	 */
	@SecurityMapping(title = "自营客服列表", value = "/chatting_user_list*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_user_list" })
	public ModelAndView chatting_user_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String chatting_nickname, String chatting_name,
			String chatting_user_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/chatting_user_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("chatting_user_form", "plat");
		
		
		if (!CommUtil.null2String(chatting_name).equals("")) {
			
			maps.put("chatting_name_like", CommUtil.null2String(chatting_name));
			mv.addObject("chatting_name", chatting_name);
		}
		if (!CommUtil.null2String(chatting_user_name).equals("")) {
			
			maps.put("chatting_user_name_like", CommUtil.null2String(chatting_user_name));
			mv.addObject("chatting_user_name", chatting_user_name);
		}
		
		IPageList pList = this.chattingUserService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/chatting_user_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 自营客服添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营客服添加", value = "/chatting_user_add*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_user_add" })
	public ModelAndView chatting_user_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/chatting_user_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 自营客服编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营客服编辑", value = "/chatting_user_edit*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_user_edit" })
	public ModelAndView chatting_user_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/chatting_user_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			ChattingUser chattinguser = this.chattingUserService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", chattinguser);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 自营客服保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营客服保存", value = "/chatting_user_save*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_user_save" })
	public ModelAndView chatting_user_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ChattingUser chattinguser = null;
		if (id.equals("")) {
			chattinguser = (ChattingUser) WebForm.toPo(request,
					ChattingUser.class);
			chattinguser.setAddTime(new Date());
		} else {
			chattinguser = this.chattingUserService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			WebForm.toPo(request, chattinguser);
		}
		chattinguser.setChatting_user_form("plat");
		if (id.equals("")) {
			this.chattingUserService.saveEntity(chattinguser);
		} else {
			WebForm.Req2Map(request, ChattingUser.class);
			ChattingUser cu = this.chattingUserService.selectByPrimaryKey(CommUtil.null2Long(id));
			WebForm.toPo(request, cu);
			this.chattingUserService.updateById(cu);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/chatting_user_list?currentPage=" + currentPage);
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/chatting_user_add");
		mv.addObject("op_title", "保存客服成功");
		return mv;
	}
	
	/**
	 * 自营客服禁用
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param uncheck_mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营客服禁用", value = "/chatting_user_disable*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_user_disable" })
	public String chatting_user_disable(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String uncheck_mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}

		Map<String,Object> params = Maps.newHashMap();
		if (mulitId.equals("all")) {
			if ((uncheck_mulitId != null) && (!"".equals(uncheck_mulitId))) {
				uncheck_mulitId = uncheck_mulitId.substring(0,uncheck_mulitId.length() - 1);
			}
			
			String[] ids = uncheck_mulitId.split(",");
			List<Long> nIds = Lists.newArrayList();
			for(String id:ids){
				nIds.add(Long.parseLong(id.trim()));
			}
			params.put("no_ids", nIds);
			
			List<ChattingUser> cus = this.chattingUserService.queryPageList(params);
			
			for (ChattingUser cu : cus) {
				cu.setChatting_status(0);
				this.chattingUserService.updateById(cu);
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					ChattingUser cu = this.chattingUserService.selectByPrimaryKey(Long.parseLong(id.trim()));
					cu.setChatting_status(0);
					this.chattingUserService.updateById(cu);
				}
			}
		}
		return "redirect:chatting_user_list?currentPage=" + currentPage;
	}
	
	/**
	 * 自营客服启用
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param uncheck_mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营客服启用", value = "/chatting_user_enable*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_user_enable" })
	public String chatting_user_enable(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String uncheck_mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		Map<String,Object> params = Maps.newHashMap();
		if (mulitId.equals("all")) {
			StringBuffer query = new StringBuffer("select new ChattingUser(id) from ChattingUser obj where 1=1 ");
			if ((uncheck_mulitId != null) && (!"".equals(uncheck_mulitId))) {
				uncheck_mulitId = uncheck_mulitId.substring(0,
						uncheck_mulitId.length() - 1);
				query.append(" and obj.id not in (" + uncheck_mulitId + ") ");
			}
			String[] ids = uncheck_mulitId.split(",");
			List<Long> nIds = Lists.newArrayList();
			for(String id:ids){
				nIds.add(Long.parseLong(id.trim()));
			}
			params.put("no_ids", nIds);
			
			List<ChattingUser> cus = this.chattingUserService.queryPageList(params);
			
			for (ChattingUser cu : cus) {
				cu.setChatting_status(1);
				this.chattingUserService.updateById(cu);
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					ChattingUser cu = this.chattingUserService.selectByPrimaryKey(Long.parseLong(id.trim()));
					cu.setChatting_status(1);
					this.chattingUserService.updateById(cu);
				}
			}
		}
		return "redirect:chatting_user_list?currentPage=" + currentPage;
	}
	
	/**
	 * 自营客服Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "自营客服Ajax更新", value = "/chatting_user_ajax*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_user_ajax" })
	public void chatting_user_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		ChattingUser obj = this.chattingUserService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = ChattingUser.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.chattingUserService.updateById(obj);

		ChattingConfig config = this.chattingconfigService.getChattingConfig(null,obj.getId());
		config.setKf_name(obj.getChatting_name());
		this.chattingconfigService.updateById(config);
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
	 * 自营客服名称防重复验证
	 * @param request
	 * @param response
	 * @param chatting_name
	 * @param id
	 */
	@SecurityMapping(title = "自营客服名称防重复验证", value = "/chatting_user_verify*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_user_verify" })
	public void chatting_user_verify(HttpServletRequest request,
			HttpServletResponse response, String chatting_name, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("chatting_name", CommUtil.null2String(chatting_name));
		params.put("no_ids", Lists.newArrayList(CommUtil.null2Long(id)));
		
		List<ChattingUser> cus = this.chattingUserService.queryPageList(params);
		
		if (cus.size() > 0) {
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
	 * 自营客服对应的管理员搜索
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "自营客服对应的管理员搜索", value = "/chatting_admin_search*", rtype = "admin", rname = "客服管理", rcode = "chatting_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_admin_search" })
	public void chatting_admin_search(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("userRole", "ADMIN");
		params.put("id_not_in_chatting_user", -1);
		
		List<User> cus = this.userService.queryPageListWithNoRelations(params);
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(cus));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "自营客服窗口", value = "/service_chatting_new*", rtype = "admin", rname = "自营客服", rcode = "self_chatting", rgroup = "自营")
	@RequestMapping({ "/service_chatting_new" })
	public void service_chatting_new(HttpServletRequest request,
			HttpServletResponse response) {
		int size = 0;
		Long service_id = this.ChattTools.getCurrentServiceId();
		List<ChattingLog> logs = this.chattinglogService.queryServiceUnread(service_id, null);
		size = logs.size();
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(size);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 自营客服记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param chatting_user_id
	 * @param chatting_user_name
	 * @param chatting_name
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes"})
	@SecurityMapping(title = "自营客服记录", value = "/chatting_log_list*", rtype = "admin", rname = "客服记录", rcode = "chatting_log_admin", rgroup = "自营")
	@RequestMapping({ "/chatting_log_list" })
	public ModelAndView chatting_log_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String chatting_user_id,
			String chatting_user_name, String chatting_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/chatting_log_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params= Maps.newHashMap();
		chatting_name = CommUtil.null2String(chatting_name);
		chatting_user_name = CommUtil.null2String(chatting_user_name);
		
		params.put("userRole", "ADMIN");
		
		List<User> admins = this.userService.queryPageListWithNoRelations(params);
		Set<Long> ids = Sets.newHashSet();
		for (User user : admins) {
			Long temp_id = this.chattingViewTools.getServiceIdByUser(user);
			if (temp_id != null) {
				ids.add(temp_id);
			}
		}
		
		params= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		params.put("service_ids", ids);
		if (!chatting_name.equals("")) {
			Map para = Maps.newHashMap();
			para.put("chatting_name", chatting_name);
			List<ChattingUser> chattinguser_list = this.chattingUserService.queryPageList(params);
			if (chattinguser_list.size() > 0) {
				if (chattinguser_list.get(0).getChatting_user_form().equals("plat")) {
					chatting_user_id = chattinguser_list.get(0).getId().toString();
				}
			}
		}
		if (!chatting_user_name.equals("")) {
			Map para = Maps.newHashMap();
			para.put("userName", chatting_user_name);
			List<User> user_list = this.userService.queryPageListWithNoRelations(params);
			
			if (user_list !=null && user_list.size()>0) {
				para.clear();
				para.put("chatting_user_id", user_list.get(0).getId());
				
				List<ChattingUser> chattinguser_list = this.chattingUserService.queryPageList(params);
				
				if (chattinguser_list!=null && chattinguser_list.size() > 0) {
					
					if (chattinguser_list.get(0).getChatting_user_form().equals("plat")) {
						chatting_user_id = chattinguser_list.get(0).getId().toString();
					}
				}
			}
		}
		if (!CommUtil.null2String(chatting_user_id).equals("")) {
			params.put("service_id", CommUtil.null2Long(chatting_user_id));
		}
		
		mv.addObject("chatting_user_id", chatting_user_id);
		mv.addObject("chatting_user_name", chatting_user_name);
		mv.addObject("chatting_name", chatting_name);
		mv.addObject("cartTools",this.cartTools);
		
		IPageList pList = this.chattinglogService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
