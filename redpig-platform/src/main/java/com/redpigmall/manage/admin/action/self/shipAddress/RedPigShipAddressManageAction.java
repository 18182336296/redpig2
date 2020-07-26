package com.redpigmall.manage.admin.action.self.shipAddress;

import java.util.Date;
import java.util.List;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.ShipAddress;

/**
 * 
 * <p>
 * Title: RedPigShipAddressManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商家发货地址管理控制器，用来管理自营发货地址信息
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
 * @date 2014-11-11
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigShipAddressManageAction extends BaseAction{
	/**
	 * 自营发货地址列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址列表", value = "/ship_address_list*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping({ "/ship_address_list" })
	public ModelAndView ship_address_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/ship_address_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		params.put("sa_type", 1);
		IPageList pList = this.shipAddressService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 自营发货地址添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址添加", value = "/ship_address_add*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping({ "/ship_address_add" })
	public ModelAndView ship_address_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ship_address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 自营发货地址编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址编辑", value = "/ship_address_edit*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping({ "/ship_address_edit" })
	public ModelAndView ship_address_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/ship_address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			ShipAddress shipaddress = this.shipAddressService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
			
			mv.addObject("sa_area",
					this.areaService.selectByPrimaryKey(shipaddress.getSa_area_id()));
			mv.addObject("areas", areas);
			mv.addObject("obj", shipaddress);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 自营发货地址保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址保存", value = "/ship_address_save*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping({ "/ship_address_save" })
	public ModelAndView ship_address_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		
		ShipAddress shipaddress = null;
		if (id.equals("")) {
			shipaddress = (ShipAddress) WebForm
					.toPo(request, ShipAddress.class);
			shipaddress.setAddTime(new Date());
		} else {
			ShipAddress obj = this.shipAddressService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			shipaddress = (ShipAddress) WebForm.toPo(request, obj);
		}
		shipaddress.setSa_type(1);
		shipaddress.setSa_user_id(SecurityUserHolder.getCurrentUser().getId());
		shipaddress.setSa_user_name(SecurityUserHolder.getCurrentUser()
				.getUsername());
		if (id.equals("")) {
			this.shipAddressService.saveEntity(shipaddress);
		} else {
			this.shipAddressService.updateById(shipaddress);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "发货地址保存成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/ship_address_list?currentPage=" + currentPage);
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/ship_address_add?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 自营发货地址删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营发货地址删除", value = "/ship_address_del*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping({ "/ship_address_del" })
	public String ship_address_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");

		if(ids!=null && ids.length>0){
			this.shipAddressService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		}
		return "redirect:ship_address_list?currentPage=" + currentPage;
	}
	
	/**
	 * 设置默认发货地址
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "设置默认发货地址", value = "/ship_address_default*", rtype = "admin", rname = "发货地址", rcode = "ship_adress", rgroup = "自营")
	@RequestMapping({ "/ship_address_default" })
	public String ship_address_default(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("sa_default", Integer.valueOf(1));
		params.put("sa_type", Integer.valueOf(1));
		
		List<ShipAddress> sa_list = this.shipAddressService.queryPageList(params);
		for (ShipAddress sa : sa_list) {
			sa.setSa_default(0);
			this.shipAddressService.updateById(sa);
		}
		ShipAddress obj = this.shipAddressService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (obj.getSa_type() == 1) {
			obj.setSa_default(1);
			this.shipAddressService.updateById(obj);
		}
		return "redirect:ship_address_list?currentPage=" + currentPage;
	}
}
