package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.domain.Menu;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.RoleGroup;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigSubAccountSellerAction.java
 * </p>
 * 
 * <p>
 * Description:
 * 卖家子账户管理，卖家根据店铺等级，可以有多个子账户，子账户可以协助卖家管理店铺，卖家自行添加子账户信息，并可以自行给子账户赋予相关卖家中心权限
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigSubAccountSellerAction extends BaseAction{
	/**
	 * 子账户列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "子账户列表", value = "/sub_account_list*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping({ "/sub_account_list" })
	public ModelAndView sub_account_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/sub_account_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		mv.addObject("store", store);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("parent_id", user.getId());
		
		IPageList pList = this.userService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("user", user);
		return mv;
	}
	
	/**
	 * 子账户添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "子账户添加", value = "/sub_account_add*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping({ "/sub_account_add" })
	public ModelAndView sub_account_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/sub_account_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store == null) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未开设店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		if (user.getChilds().size() >= store.getGrade().getAcount_num()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的店铺等级不能继续添加子账户,请升级店铺等级");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/sub_account_list");
		}
		mv.addObject("store", store);
		Map<String, Object> params = Maps.newHashMap();
		params.put("type", "SELLER");
		params.put("orderBy", "addTime");
		params.put("orderType", "asc");
		
		List<RoleGroup> rgs = this.roleGroupService.queryPageList(params);
		
		mv.addObject("rgs", rgs);
		mv.addObject("user", user);
		
		params.clear();
		params.put("type", "SELLER");
		params.put("deleteStatus", 0);
		params.put("parent_id", -1);
		List<Menu> menus = this.menuService.queryPageList(params);
		
		
		
		mv.addObject("menus", menus);
		mv.addObject("menuTools", this.menuTools);
		return mv;
	}
	
	/**
	 * 子账户编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "子账户编辑", value = "/sub_account_edit*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping({ "/sub_account_edit" })
	public ModelAndView sub_account_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/sub_account_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store == null) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未开设店铺");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		mv.addObject("store", store);
		Map<String, Object> params = Maps.newHashMap();
		params.put("type", "SELLER");
		params.put("orderBy", "addTime");
		params.put("orderType", "asc");
		
		List<RoleGroup> rgs = this.roleGroupService.queryPageList(params);
		
		mv.addObject("rgs", rgs);
		mv.addObject("obj", this.userService.selectByPrimaryKey(CommUtil.null2Long(id)));
		mv.addObject("user", user);
		return mv;
	}

	private String clearContent(String inputString) {
		String htmlStr = inputString;
		String textStr = "";
		try {
			String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>";
			String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>";
			String regEx_html = "<[^>]+>";
			String regEx_html1 = "<[^>]+";
			Pattern p_script = Pattern.compile(regEx_script, 2);
			Matcher m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");

			Pattern p_style = Pattern.compile(regEx_style, 2);
			Matcher m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");

			Pattern p_html = Pattern.compile(regEx_html, 2);
			Matcher m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");

			Pattern p_html1 = Pattern.compile(regEx_html1, 2);
			Matcher m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll("");

			textStr = htmlStr;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}
		return textStr;
	}
	
	/**
	 * 子账户保存
	 * @param request
	 * @param response
	 * @param id
	 * @param userName
	 * @param trueName
	 * @param sex
	 * @param birthday
	 * @param QQ
	 * @param telephone
	 * @param mobile
	 * @param password
	 * @param menu_ids
	 */
	@SecurityMapping(title = "子账户保存", value = "/sub_account_save*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping({ "/sub_account_save" })
	public void sub_account_save(HttpServletRequest request,
			HttpServletResponse response, String id, String userName,
			String trueName, String sex, String birthday, String QQ,
			String telephone, String mobile, String password, String menu_ids) {
		String ret = "succeed";
		
		String msg = "子账户创建成功";
		User parent = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (parent.getParent() != null) {
			parent = parent.getParent();
		}
		Store store = parent.getStore();
		userName = clearContent(userName);
		if (parent.getChilds().size() >= store.getGrade().getAcount_num()) {
			ret = "error";
			msg = "已经超过子账户上线";
		} else {

			if (CommUtil.null2String(id).equals("")) {
				User user = new User();
				user.setAddTime(new Date());
				user.setUserName(userName);
				user.setTrueName(trueName);
				user.setSex(CommUtil.null2Int(sex));
				user.setBirthday(CommUtil.formatDate(birthday));
				user.setQQ(QQ);
				user.setMobile(mobile);
				user.setTelephone(telephone);
				user.setParent(parent);
				user.setUserRole("SELLER");
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				
				Map<String, Object> params = Maps.newHashMap();
				params.put("type", "BUYER");
				
				List<Role> roles = this.roleService.queryPageList(params);
				
				user.getRoles().addAll(roles);
				
				params.clear();
				params.put("type", "SELLER");
				params.put("roleCode", "ROLE_USER_CENTER_SELLER");
				List<Role> center_roles = this.roleService.queryPageList(params);
				
				for (Role r : center_roles) {
					user.getRoles().add(r);
				}
				
				this.userService.saveEntity(user);
				
				this.userService.saveUserRole(user.getId(), user.getRoles());
				
				/**
				 * 1、把新用户先存进去
				 * 2、建立关联关系
				 */
				for (String menu_id : menu_ids.split(",")) {
					if (menu_id!=null && !menu_id.equals("")) {
						Menu menu = this.menuService.selectByPrimaryKey(CommUtil.null2Long(menu_id));
						if (menu.getType().equals("SELLER")) {
							user.getMenus().add(menu);
						}
					}
				}
				
				this.userService.saveUserMenu(user.getId(), user.getMenus());
				
			} else {
				User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
				user.setUserName(userName);
				user.setTrueName(trueName);
				user.setSex(CommUtil.null2Int(sex));
				user.setBirthday(CommUtil.formatDate(birthday));
				user.setQQ(QQ);
				user.setMobile(mobile);
				user.setTelephone(telephone);
				
				this.userService.deleteUserRole(user.getId(), user.getRoles());
				
				this.userService.deleteUserMenu(user.getId());
				
				Map<String, Object> params = Maps.newHashMap();
				params.put("type", "BUYER");
				List<Role> roles = this.roleService.queryPageList(params);
				
				user.getRoles().addAll(roles);
				String[] menuIds = menu_ids.split(",");
				
				params.clear();
				params.put("ids",menuIds);
				
				List<Menu> menus = this.menuService.queryPageList(params);
				
				user.getMenus().addAll(menus);
				
				this.userService.saveUserMenu(user.getId(), user.getMenus());
				
				params.clear();
				params.put("type", "SELLER");
				params.put("roleCode", "ROLE_USER_CENTER_SELLER");
				List<Role> center_roles = this.roleService.queryPageList(params);
				
				for (Role r : center_roles) {
					user.getRoles().add(r);
				}
				
				this.userService.saveUserRole(user.getId(), user.getRoles());
				
				this.userService.updateById(user);
				
				
				
				msg = "子账户更新成功";
			}
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("ret", ret);
		map.put("msg", msg);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 子账户删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "子账户删除", value = "/sub_account_del*", rtype = "seller", rname = "子账户管理", rcode = "sub_account_seller", rgroup = "我的店铺")
	@RequestMapping({ "/sub_account_del" })
	public String sub_account_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(mulitId));
		user.getRoles().clear();
		this.userService.deleteUserRole(user.getId(), user.getRoles());
		this.userService.deleteById(user.getId());
		return "redirect:sub_account_list";
	}
}
