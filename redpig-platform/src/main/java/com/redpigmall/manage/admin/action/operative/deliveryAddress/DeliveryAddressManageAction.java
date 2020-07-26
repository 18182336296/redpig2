package com.redpigmall.manage.admin.action.operative.deliveryAddress;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.DeliveryAddress;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: DeliveryAddressSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description: 超级后台自提点管理器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-11-14
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class DeliveryAddressManageAction extends BaseAction {

	/**
	 * 自提点列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param da_name
	 * @param da_contact_user
	 * @param da_type
	 * @return
	 */
	@SecurityMapping(title = "自提点列表", value = "/delivery_address_list*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_address_list" })
	public ModelAndView delivery_address_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String da_name, 
			String da_contact_user,
			String da_type) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/delivery_address_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if ((da_name != null) && (!da_name.equals(""))) {
			params.put("da_name_like", da_name);
			
			mv.addObject("da_name", da_name);
		}
		
		if ((da_contact_user != null) && (!da_contact_user.equals(""))) {
			params.put("da_contact_user", da_contact_user);
			
			mv.addObject("da_contact_user", da_contact_user);
		}
		
		if ((da_type != null) && (!da_type.equals(""))) {
			params.put("da_type", CommUtil.null2Int(da_type));
			
			mv.addObject("da_type", da_type);
		}
		
		params.put("da_status_more_than", 4);
		
		IPageList pList = this.redPigDeliveryAddressService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("areaManageTools", this.redPigAreaManageTools);
		return mv;
	}
	
	/**
	 * 自提点申请列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "自提点申请列表", value = "/delivery_apply_list*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_apply_list" })
	public ModelAndView delivery_apply_list(
			HttpServletRequest request,
			HttpServletResponse response,
			String currentPage,
			String orderBy,
			String orderType) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/delivery_apply_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		params.put("da_status_less_than", 5);
		
		IPageList pList = this.redPigDeliveryAddressService.list(params);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("areaManageTools", this.redPigAreaManageTools);
		
		return mv;
	}
	
	/**
	 * 自提点申请审核
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自提点申请审核", value = "/delivery_apply_audit*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_apply_audit" })
	public ModelAndView delivery_apply_audit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/delivery_address_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		DeliveryAddress deliveryAddress = this.redPigDeliveryAddressService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		mv.addObject("obj", deliveryAddress);
		mv.addObject("service_day", this.redPigDeliveryAddressTools.query_service_day(deliveryAddress.getDa_service_day()));
		
		mv.addObject("areaManageTools", this.redPigAreaManageTools);
		
		mv.addObject("currentPage", currentPage);
		mv.addObject("url", "/delivery_apply_list?currentPage=" + currentPage);
		mv.addObject("audit", Boolean.valueOf(true));
		return mv;
	}
	
	/**
	 * 自提点申请拒绝
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自提点申请拒绝", value = "/delivery_apply_refuse*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_apply_refuse" })
	public String delivery_apply_refuse(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String currentPage) {
		
		DeliveryAddress deliveryAddress = this.redPigDeliveryAddressService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (deliveryAddress != null) {
			deliveryAddress.setDa_status(4);
			this.redPigDeliveryAddressService.updateById(deliveryAddress);
		}
		return "redirect:/delivery_apply_list?currentPage=" + currentPage;
	}
	
	/**
	 * 自提点申请通过
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自提点申请通过", value = "/delivery_apply_pass*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_apply_pass" })
	public String delivery_apply_pass(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		DeliveryAddress deliveryAddress = this.redPigDeliveryAddressService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (deliveryAddress != null) {
			deliveryAddress.setDa_status(10);
			this.redPigDeliveryAddressService.updateById(deliveryAddress);

			User user = this.redPigUserService.selectByPrimaryKey(deliveryAddress.getDa_user_id());
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("type", "DELIVERY");
			
			List<Role> roles = this.redPigRoleService.queryPageList(params);
			
			for (Role role : roles) {
				user.getRoles().add(role);
			}
			
			this.redPigUserService.saveUserRole(user.getId(), user.getRoles());
		}
		return "redirect:/delivery_apply_list?currentPage=" + currentPage;
	}
	
	/**
	 * 自提点查看
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自提点查看", value = "/delivery_address_view*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_address_view" })
	public ModelAndView delivery_address_view(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/delivery_address_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		DeliveryAddress deliveryAddress = this.redPigDeliveryAddressService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		mv.addObject("obj", deliveryAddress);
		mv.addObject("service_day", this.redPigDeliveryAddressTools.query_service_day(deliveryAddress.getDa_service_day()));
		
		mv.addObject("areaManageTools", this.redPigAreaManageTools);
		mv.addObject("url", "/delivery_address_list?currentPage="
				+ currentPage);
		return mv;
	}
	
	/**
	 * 自提点新增
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点新增", value = "/delivery_address_add*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_address_add" })
	public ModelAndView delivery_address_add(
			HttpServletRequest request,
			HttpServletResponse response) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/delivery_address_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<Area> areas = this.redPigAreaService.queryPageList(params);

		mv.addObject("areas", areas);
		return mv;
	}
	
	/**
	 * 自提点编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自提点编辑", value = "/delivery_address_edit*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_address_edit" })
	public ModelAndView delivery_address_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/delivery_address_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		DeliveryAddress deliveryAddress = this.redPigDeliveryAddressService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		mv.addObject("obj", deliveryAddress);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		List<Area> areas = this.redPigAreaService.queryPageList(params);
		
		mv.addObject("areas", areas);
		mv.addObject("edit", Boolean.valueOf(true));
		return mv;
	}
	
	/**
	 * 自提点保存
	 * @param request
	 * @param response
	 * @param id
	 * @param add_url
	 * @param list_url
	 * @param area3
	 * @param da_begin_time
	 * @param da_end_time
	 * @param da_service_day
	 * @return
	 */
	@SecurityMapping(title = "自提点保存", value = "/delivery_address_save*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_address_save" })
	public ModelAndView delivery_address_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String add_url,
			String list_url, 
			String area3, 
			String da_begin_time,
			String da_end_time, 
			String da_service_day) {
		
		DeliveryAddress deliveryaddress = null;
		if (id.equals("")) {
			deliveryaddress = (DeliveryAddress) WebForm.toPo(request,DeliveryAddress.class);
			deliveryaddress.setAddTime(new Date());
		} else {
			DeliveryAddress obj = this.redPigDeliveryAddressService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			
			deliveryaddress = (DeliveryAddress) WebForm.toPo(request, obj);
		}
		
		deliveryaddress.setDa_area(this.redPigAreaService.selectByPrimaryKey(CommUtil.null2Long(area3)));
		
		deliveryaddress.setDa_service_day(da_service_day.toString());
		
		if (id.equals("")) {
			this.redPigDeliveryAddressService.saveEntity(deliveryaddress);
		} else {
			this.redPigDeliveryAddressService.updateById(deliveryaddress);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存自提点成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url);
		}
		return mv;
	}
	
	/**
	 * 自提点删除
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自提点删除", value = "/delivery_address_del*", rtype = "admin", rname = "自提点管理", rcode = "delivery_address", rgroup = "运营")
	@RequestMapping({ "/delivery_address_del" })
	public String delivery_address_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		DeliveryAddress deliveryAddress = this.redPigDeliveryAddressService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (deliveryAddress != null) {
			if (deliveryAddress.getDa_type() == 0) {
				this.redPigDeliveryAddressService.deleteById(CommUtil.null2Long(id));
			} else {
				remove_deliveryAddress_userRole(deliveryAddress.getDa_user_id());
				this.redPigDeliveryAddressService.deleteById(CommUtil.null2Long(id));
			}
		}
		return "redirect:delivery_address_list?currentPage=" + currentPage;
	}

	private void remove_deliveryAddress_userRole(Long user_id) {
		User user = this.redPigUserService.selectByPrimaryKey(user_id);
		
		if (user != null) {
			Set<Role> newRoles = Sets.newHashSet();
			for (Role role : user.getRoles()) {
				if (!role.getType().equals("DELIVERY")) {
					newRoles.add(role);
				}
			}
			user.getRoles().clear();
			for (Role role : newRoles) {
				user.getRoles().add(role);
			}
			
			user.getRoles().addAll(newRoles);
			
			this.redPigUserService.saveUserRole(user.getId(), user.getRoles());
			
			user.setDelivery_id(null);
			this.redPigUserService.updateById(user);
		}
	}
}
