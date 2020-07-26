package com.redpigmall.manage.seller.action;

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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.ChattingConfig;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.domain.User;
import com.redpigmall.module.chatting.manage.base.BaseAction;

/**
 * <p>
 * Title: RedPigChattingUserSellerManageAction.java
 * </p>
 * 
 * <p>
 * Description:卖家聊天管理
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
 * @date 2014-9-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigChattingUserSellerManageAction extends BaseAction {
	/**
	 * 商家客服列表
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
	@SecurityMapping(title = "商家客服列表", value = "/chatting_user_list*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_user_list" })
	public ModelAndView chatting_user_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String chatting_nickname, String chatting_name,
			String chatting_user_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/chatting_user_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		User seller = SecurityUserHolder.getCurrentUser();
		seller = this.userService.selectByPrimaryKey(seller.getId());
		Set<Long> ids = Sets.newHashSet();
		
		ids.add(seller.getId());
		for (User child : seller.getChilds()) {
			ids.add(child.getId());
		}
		maps.put("chatting_user_ids", ids);
		maps.put("chatting_user_form", "seller");
		
		
		if (!CommUtil.null2String(chatting_name).equals("")) {
			
			maps.put("chatting_name_like", CommUtil.null2String(chatting_name));
			mv.addObject("chatting_name", chatting_name);
		}
		if (!CommUtil.null2String(chatting_user_name).equals("")) {
			maps.put("chatting_user_name_like", CommUtil.null2String(chatting_user_name));
			mv.addObject("chatting_user_name", chatting_user_name);
		}
		IPageList pList = this.chattingUserService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}
	
	/**
	 * 商家客服添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商家客服添加", value = "/chatting_user_add*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_user_add" })
	public ModelAndView chatting_user_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/chatting_user_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 商家客服编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商家客服编辑", value = "/chatting_user_edit*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_user_edit" })
	public ModelAndView chatting_user_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/chatting_user_add.html",
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
	 * 商家客服保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商家客服保存", value = "/chatting_user_save*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
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
		chattinguser.setChatting_user_form("seller");
		if (id.equals("")) {
			this.chattingUserService.saveEntity(chattinguser);
		} else {
			ChattingUser cu = WebForm.toPo(request, ChattingUser.class);
			cu.setId(CommUtil.null2Long(id));
			
			this.chattingUserService.updateById(cu);
		}
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_success.html",
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
	 * 商家客服禁用
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param uncheck_mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商家客服禁用", value = "/chatting_user_disable*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_user_disable" })
	public String chatting_user_disable(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String uncheck_mulitId, String currentPage) {
		Map map = Maps.newHashMap();
		
		if (mulitId.equals("all")) {
			if ((uncheck_mulitId != null) && (!"".equals(uncheck_mulitId))) {
				uncheck_mulitId = uncheck_mulitId.substring(0,uncheck_mulitId.length() - 1);
				List  ids = Lists.newArrayList(uncheck_mulitId.split(","));
				map.put("no_ids", ids);
			}
			
			List<ChattingUser> cus = this.chattingUserService.queryPageList(map);
			
			for (ChattingUser cu : cus) {
				map.clear();
				map.put("chatting_status", Integer.valueOf(0));
				cu.setChatting_status(0);
				
				this.chattingUserService.updateById(cu);
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					ChattingUser cu = this.chattingUserService.selectByPrimaryKey(Long.parseLong(id));
					cu.setChatting_status(0);
					
					this.chattingUserService.updateById(cu);
				}
			}
		}
		return "redirect:chatting_user_list?currentPage=" + currentPage;
	}
	
	/**
	 * 商家客服启用
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param uncheck_mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商家客服启用", value = "/chatting_user_enable*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_user_enable" })
	public String chatting_user_enable(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String uncheck_mulitId, String currentPage) {
		Map map = Maps.newHashMap();
		
		if (mulitId.equals("all")) {
			
			if ((uncheck_mulitId != null) && (!"".equals(uncheck_mulitId))) {
				uncheck_mulitId = uncheck_mulitId.substring(0,uncheck_mulitId.length() - 1);
				List  ids = Lists.newArrayList(uncheck_mulitId.split(","));
				map.put("no_ids", ids);
			}
			
			List<ChattingUser> cus = this.chattingUserService.queryPageList(map);
			
			for (ChattingUser cu : cus) {
				map.clear();
				map.put("chatting_status", Integer.valueOf(1));
				cu.setChatting_status(1);
				this.chattingUserService.updateById(cu);
			}
		} else {
			String[] ids = mulitId.split(",");
			for (String id : ids) {
				if (!id.equals("")) {
					ChattingUser cu = this.chattingUserService.selectByPrimaryKey(Long.parseLong(id));
					cu.setChatting_status(1);
					this.chattingUserService.updateById(cu);
				}
			}
		}
		return "redirect:chatting_user_list?currentPage=" + currentPage;
	}
	
	/**
	 * 商家客服Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "商家客服Ajax更新", value = "/chatting_user_ajax*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_user_ajax" })
	public void chatting_user_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		ChattingUser obj = this.chattingUserService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = ChattingUser.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.chattingUserService.updateById(obj);

		ChattingConfig config = this.chattingconfigService.getChattingConfig(null, obj.getId());
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
	 * 商家客服名称防重复验证
	 * @param request
	 * @param response
	 * @param chatting_name
	 * @param id
	 */
	@SecurityMapping(title = "商家客服名称防重复验证", value = "/chatting_user_verify*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_user_verify" })
	public void chatting_user_verify(HttpServletRequest request,
			HttpServletResponse response, String chatting_name, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("chatting_name", CommUtil.null2String(chatting_name));
		params.put("id_no", CommUtil.null2Long(id));
		
		int cus = this.chattingUserService.selectCount(params);
		
		if (cus > 0) {
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
	 * 商家客服对应的子账户搜索
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "商家客服对应的子账户搜索", value = "/chatting_seller_search*", rtype = "seller", rname = "客服管理", rcode = "chatting_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_seller_search" })
	public void chatting_seller_search(HttpServletRequest request,
			HttpServletResponse response) {
		User seller = SecurityUserHolder.getCurrentUser();
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent", seller.getId());
		
		List<User> child_sellers = this.userService.queryPageList(params);
		
		Set<Long> ids = Sets.newHashSet();
		
		for (int i = 0; i < child_sellers.size(); i++) {
			ids.add(CommUtil.null2Long(child_sellers.get(i)));
		}
		params.clear();
		params.put("ids", ids);
		params.put("userRole", "seller");
		
		List<Long> no_ids = this.chattingUserService.queryChattingUserIds();
		params.put("no_ids", no_ids);
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<User> cus = this.userService.queryPageList(params);
		
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
	
	/**
	 * 商家客服记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param chatting_user_id
	 * @param chatting_user_name
	 * @return
	 */
	@SecurityMapping(title = "商家客服记录", value = "/chatting_log_list*", rtype = "seller", rname = "客服记录", rcode = "chatting_log_seller", rgroup = "商家")
	@RequestMapping({ "/chatting_log_list" })
	public ModelAndView chatting_log_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String chatting_user_id, String chatting_user_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/chatting_log_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User seller = SecurityUserHolder.getCurrentUser();
		
		seller = this.userService.selectByPrimaryKey(seller.getId());
		Set<Long> ids = Sets.newHashSet();
		
		ids.add(this.chattingviewTools.getServiceIdByUser(seller));
		for (User child : seller.getChilds()) {
			Long temp_id = this.chattingviewTools.getServiceIdByUser(child);
			if (temp_id != null) {
				ids.add(temp_id);
			}
		}
		Long temp_id = this.chattingviewTools.getServiceIdByUser(seller);
		if (temp_id != null) {
			ids.add(temp_id);
		}
		
		Map<String, Object> params = redPigQueryTools.getParams(currentPage, 10, orderBy, orderType);
		params.put("service_ids", ids);
		
		IPageList pList = this.chattinglogService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
