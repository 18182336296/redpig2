package com.redpigmall.manage.seller.action.base;


import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.domain.ChattingLog;
import com.redpigmall.domain.Complaint;
import com.redpigmall.domain.Consult;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Menu;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.domain.RoleGroup;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: BaseSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台基础管理器 主要功能包括商家后台的基础管理、快捷菜单设置等
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
 * @date 2014-6-10
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class BaseSellerAction extends BaseAction{
	/**
	 * 卖家登陆
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/login" })
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_login.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		request.getSession(false).removeAttribute("verify_code");
		return mv;
	}
	
	@RequestMapping({ "/redpig_seller_login" })
	public ModelAndView redpig_login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_login.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		request.getSession(false).removeAttribute("verify_code");
		return mv;
	}
	
	/**
	 * 卖家顶部
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/top" })
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_top.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 商家中心
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/index*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping({ "/index" })
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String, Object> params = Maps.newHashMap();
		params.put("two_type", "seller");
		params.put("one_type", "2");
		
		List<ArticleClass> ac = this.articleClassService.queryPageList(params);
		
		mv.addObject("ac_list", ac);
		params.clear();
		params.put("goods_store_id", user.getStore().getId());
		params.put("goods_status", 0);
		params.put("orderBy", "goods_salenum");
		params.put("orderType", "desc");
		
		List<Goods> goods_sale_list = this.goodsService.queryPageList(params,0,5);
		
		params.clear();
		params.put("store_id", user.getStore().getId());
		params.put("goods_return_status", "5");
		
		List<ReturnGoodsLog> returngoods = this.returngoodslogService.queryPageList(params);
		
		params.clear();
		params.put("store_id", user.getStore().getId().toString());
		
		List<OrderForm> orders = this.orderformService.queryPageList(params);
		
		params.clear();
		params.put("reply", Boolean.valueOf(false));
		params.put("store_id", user.getStore().getId());
		
		List<Consult> msgs = this.consultService.queryPageList(params);
		
		params.clear();
		params.put("status", Integer.valueOf(0));
		params.put("to_user_id", user.getId());
		
		List<Complaint> complaints = this.complaintService.queryPageList(params);
		
		params.clear();
		params.put("toStore_id", user.getStore().getId());
		params.put("status", Integer.valueOf(0));
		params.put("parent", -1);
		
		List<Message> message_list = this.messageService.queryPageList(params);
		
		mv.addObject("message_list", message_list);
		mv.addObject("complaints", complaints);
		mv.addObject("msgs", msgs);
		mv.addObject("orders", orders);
		mv.addObject("returngoods", returngoods);
		mv.addObject("goods_sale_list", goods_sale_list);
		mv.addObject("user", user);
		mv.addObject("store", user.getStore());
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("orderViewTools", this.orderViewTools);
		mv.addObject("menuTools", this.menuTools);
		mv.addObject("article_Tools", this.article_Tools);
		return mv;
	}
	
	/**
	 * 商家中心
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/index*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping({ "/index2" })
	public ModelAndView index2(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_index2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String, Object> params = Maps.newHashMap();
		params.put("two_type", "seller");
		params.put("one_type", 2);
		List<ArticleClass> ac = this.articleClassService.queryPageList(params);
		
		mv.addObject("ac_list", ac);
		params.clear();
		params.put("store_id", user.getStore().getId());
		params.put("goods_status", Integer.valueOf(0));
		params.put("orderBy", "goods_salenum");
		params.put("orderType", "desc");
		
		List<Goods> goods_sale_list = this.goodsService.queryPageList(params,0,5);
		
		params.clear();
		params.put("store_id", user.getStore().getId());
		params.put("goods_return_status", "5");
		
		List<ReturnGoodsLog> returngoods = this.returngoodslogService.queryPageList(params);
		
		params.clear();
		params.put("store_id", user.getStore().getId().toString());
		List<OrderForm> orders = this.orderformService.queryPageList(params);
		
		params.clear();
		params.put("reply", Boolean.valueOf(false));
		params.put("store_id", user.getStore().getId());
		
		List<Consult> msgs = this.consultService.queryPageList(params);
		
		params.clear();
		params.put("status", Integer.valueOf(0));
		params.put("to_user_id", user.getId());
		
		List<Complaint> complaints = this.complaintService.queryPageList(params);
		
		params.clear();
		params.put("toStore_id", user.getStore().getId());
		params.put("status", Integer.valueOf(0));
		params.put("parent", -1);
		
		List<Message> message_list = this.messageService.queryPageList(params);
		
		mv.addObject("message_list", message_list);
		mv.addObject("complaints", complaints);
		mv.addObject("msgs", msgs);
		mv.addObject("orders", orders);
		mv.addObject("returngoods", returngoods);
		mv.addObject("goods_sale_list", goods_sale_list);
		mv.addObject("user", user);
		mv.addObject("store", user.getStore());
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("orderViewTools", this.orderViewTools);
		mv.addObject("menuTools", this.menuTools);
		mv.addObject("article_Tools", this.article_Tools);
		return mv;
	}
	
	/**
	 * 商家中心
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/index*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping({ "/manage" })
	public ModelAndView seller_manage(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_manage.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
	
	/**
	 * 商家中心导航
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商家中心导航", value = "/nav*", rtype = "seller", rname = "商家中心导航", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping({ "/nav" })
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("op", op);
		mv.addObject("user", user);
		int store_status = user.getStore() == null ? 0 : CommUtil.null2Int(user
				.getStore().getStore_status());
		if (store_status != 15) {
			mv.addObject("limit", Boolean.valueOf(true));
		}
		Long service_id = this.ChattingViewTools.getCurrentServiceId();
		List<ChattingLog> logs = this.chattinglogService.queryServiceUnread(service_id, null);
		
		if (logs.size() > 0) {
			mv.addObject("chatting", logs.get(0));
		}
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("type", "SELLER");
		params.put("orderBy", "addTime");
		params.put("orderType", "asc");
		
		List<RoleGroup> rgs = this.roleGroupService.queryPageList(params);
		
		mv.addObject("rgs", rgs);
		mv.addObject("roleTools",this.roleTools);
		
		params.clear();
		
		params.put("parent_id", -1);
		params.put("type", "SELLER");
		
		List<Menu> menus = this.menuService.queryPageList(params);
		
		mv.addObject("menus", menus);
		mv.addObject("userMenuTools", this.userMenuTools);
		mv.addObject("user", SecurityUserHolder.getCurrentUser());
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@RequestMapping({ "/new_chatting_remind" })
	public ModelAndView new_chatting_remind(
			HttpServletRequest request,
			HttpServletResponse response, String userName) {
		
		ModelAndView mv= new RedPigJModelAndView(
				"user/default/sellercenter/new_chatting_remind.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (user != null) {
				Long service_id = this.ChattingViewTools.getCurrentServiceId();
				List<ChattingLog> logs = this.chattinglogService
						.queryServiceUnread(service_id, null);
				if (logs.size() > 0) {
					mv.addObject("chatting", logs.get(0));
				}
			}
		}
		return mv;
	}
	
	/**
	 * 商家中心快捷功能设置保存
	 * @param request
	 * @param response
	 * @param menus
	 */
	@SecurityMapping(title = "商家中心快捷功能设置保存", value = "/store_quick_menu_save*", rtype = "seller", rname = "商家中心快捷功能设置保存", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping({ "/store_quick_menu_save" })
	public void store_quick_menu_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String menus) {
		String[] menu_navs = menus.split(";");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<Map<String,Object>> list = Lists.newArrayList();
		
		for (String menu_nav : menu_navs) {

			if (!menu_nav.equals("")) {
				String[] infos = menu_nav.split(",");
				Map<String, Object> map = Maps.newHashMap();
				map.put("menu_name", infos[0]);
				map.put("menu_url", infos[1]);
				list.add(map);
			}
		}
		String ret = JSON.toJSONString(list);
		store.setStore_quick_menu(ret);
		this.storeService.updateById(store);
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
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/success" })
	public ModelAndView success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/RedPigError" })
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("op_title",request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}
	
	/**
	 * 商家中心申请重新开店
	 * @param request
	 * @param response
	 */
	@SecurityMapping(title = "商家中心申请重新开店", value = "/store_renew*", rtype = "seller", rname = "商家中心", rcode = "user_center_seller", rgroup = "商家中心")
	@RequestMapping({ "/store_renew" })
	public void store_renew(
			HttpServletRequest request,
			HttpServletResponse response) {
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		Boolean ret = Boolean.valueOf(false);
		if (user.getStore() != null) {
			Store store = user.getStore();
			store.setStore_status(26);
			this.storeService.updateById(store);
			ret = Boolean.valueOf(true);
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
