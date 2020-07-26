package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.ShipAddress;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigShipAddressSellerAction.java
 * </p>
 * 
 * <p>
 * Description:卖家发货地址管理控制器，用来添加、删除、编辑卖家发货地址信息，发货地址主要用在发货设置、快递跟踪等
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
 * @date 2014-11-12
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigShipAddressSellerAction extends BaseAction{
	/**
	 * 发货地址列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "发货地址列表", value = "/ship_address*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping({ "/ship_address" })
	public ModelAndView ship_address(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ship_address.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		List<Long> sa_user_ids = Lists.newArrayList();
		sa_user_ids.add(user.getId());
		if(user!=null && user.getParent()!=null){
			User userParent = this.userService.selectByPrimaryKey(user.getParent().getId());
			sa_user_ids.add(userParent.getId());
			List<User> childs = userParent.getChilds();
			for (User uc : childs) {
				sa_user_ids.add(uc.getId());
			}
		}
		
		if(user!=null && user.getChilds()!=null && user.getChilds().size()>0){
			List<User> childs = user.getChilds();
			for (User uc : childs) {
				sa_user_ids.add(uc.getId());
			}
		}
		
		maps.put("sa_type", 0);
		maps.put("sa_user_ids", sa_user_ids);
		
		IPageList pList = this.shipAddressService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 新增发货地址
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "新增发货地址", value = "/ship_address_add*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping({ "/ship_address_add" })
	public ModelAndView ship_address_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ship_address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 编辑发货地址
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "编辑发货地址", value = "/ship_address_add*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping({ "/ship_address_edit" })
	public ModelAndView ship_address_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/ship_address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ShipAddress obj = this.shipAddressService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if ((obj != null) && (obj.getSa_user_id().equals(user.getId()))) {
			
			List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
			
			mv.addObject("sa_area",
					this.areaService.selectByPrimaryKey(obj.getSa_area_id()));
			mv.addObject("areas", areas);
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/ship_address");
			mv.addObject("op_title", "参数错误");
		}
		return mv;
	}
	
	/**
	 * 编辑发货地址
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 */
	@SecurityMapping(title = "编辑发货地址", value = "/ship_address_add*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping({ "/ship_address_save" })
	public void ship_address_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ShipAddress shipaddress = null;
		boolean ret = true;
		if (id.equals("")) {
			shipaddress = (ShipAddress) WebForm
					.toPo(request, ShipAddress.class);
			shipaddress.setAddTime(new Date());
		} else {
			ShipAddress obj = this.shipAddressService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			shipaddress = (ShipAddress) WebForm.toPo(request, obj);
		}
		shipaddress.setSa_type(0);
		shipaddress.setSa_user_id(SecurityUserHolder.getCurrentUser().getId());
		shipaddress.setSa_user_name(SecurityUserHolder.getCurrentUser()
				.getUsername());
		if (id.equals("")) {
			this.shipAddressService.saveEntity(shipaddress);
			ret = true;
		} else {
			this.shipAddressService.updateById(shipaddress);
			ret = true;
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
	 * 发货地址删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "发货地址删除", value = "/ship_address_del*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping({ "/ship_address_del" })
	public String ship_address_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();

		for (String id : ids) {

			if (!id.equals("")) {
				ShipAddress obj = this.shipAddressService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				if ((obj != null) && (obj.getSa_type() == 0)
						&& (obj.getSa_user_id().equals(user.getId()))) {
					this.shipAddressService.deleteById(obj.getId());
				}
			}
		}
		return "redirect:ship_address?currentPage=" + currentPage;
	}
	
	/**
	 * 设置默认发货地址
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "设置默认发货地址", value = "/ship_address_default*", rtype = "seller", rname = "发货信息", rcode = "ship_address_seller", rgroup = "交易管理")
	@RequestMapping({ "/ship_address_default" })
	public String ship_address_default(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Map<String, Object> params = Maps.newHashMap();
		params.put("sa_default", Integer.valueOf(1));
		params.put("sa_user_id", user.getId());
		
		List<ShipAddress> sa_list = this.shipAddressService.queryPageList(params);
		
		for (ShipAddress sa : sa_list) {
			sa.setSa_default(0);
			this.shipAddressService.updateById(sa);
		}
		ShipAddress obj = this.shipAddressService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (obj.getSa_user_id().equals(user.getId())) {
			obj.setSa_default(1);
			this.shipAddressService.updateById(obj);
		}
		return "redirect:ship_address?currentPage=" + currentPage;
	}
}
