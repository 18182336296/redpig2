package com.redpigmall.manage.seller.action;

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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreNavigation;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title:RedPigStoreNavSellerAction.java
 * </p>
 * 
 * <p>
 * Description:商家后台店铺导航管理控制器
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
 * @date 2014年4月24日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigStoreNavSellerAction extends BaseAction{
	
	/**
	 * 卖家导航管理
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "卖家导航管理", value = "/store_nav*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping({ "/store_nav" })
	public ModelAndView store_nav(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		maps.put("store_id", user.getStore().getId());
		
		IPageList pList = this.storenavigationService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 卖家导航添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家导航添加", value = "/store_nav_add*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping({ "/store_nav_add" })
	public ModelAndView store_nav_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_nav_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 卖家导航编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家导航编辑", value = "/store_nav_edit*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping({ "/store_nav_edit" })
	public ModelAndView store_nav_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_nav_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			StoreNavigation storenavigation = this.storenavigationService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", storenavigation);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 卖家导航保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 */
	@SecurityMapping(title = "卖家导航保存", value = "/store_nav_save*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping({ "/store_nav_save" })
	public void store_nav_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd) {
		
		StoreNavigation storenavigation = null;
		if (id.equals("")) {
			storenavigation = (StoreNavigation) WebForm.toPo(request,
					StoreNavigation.class);
			storenavigation.setAddTime(new Date());
		} else {
			StoreNavigation obj = this.storenavigationService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			storenavigation = (StoreNavigation) WebForm.toPo(request, obj);
			storenavigation.setDisplay(Boolean.valueOf(request.getParameter("display")));
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		storenavigation.setStore(store);
		if (id.equals("")) {
			this.storenavigationService.saveEntity(storenavigation);
		} else {
			this.storenavigationService.updateById(storenavigation);
		}
		
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(true));
		json.put("op_title", "保存导航成功");
		json.put("url", CommUtil.getURL(request) + "/store_nav");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 卖家导航删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家导航删除", value = "/store_nav_del*", rtype = "seller", rname = "店铺导航", rcode = "store_nav", rgroup = "我的店铺")
	@RequestMapping({ "/store_nav_del" })
	public String store_nav_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				StoreNavigation storenavigation = this.storenavigationService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				if (CommUtil.null2String(storenavigation.getStore().getId())
						.equals(store.getId())) {
					this.storenavigationService.deleteById(Long.valueOf(Long
							.parseLong(id)));
				}
			}
		}
		return "redirect:store_nav?currentPage=" + currentPage;
	}
}
