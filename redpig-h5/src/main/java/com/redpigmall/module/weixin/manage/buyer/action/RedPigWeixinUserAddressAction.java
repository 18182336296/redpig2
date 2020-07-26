package com.redpigmall.module.weixin.manage.buyer.action;

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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.module.weixin.manage.base.BaseAction;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.Area;

/**
 * 
 * 
 * <p>
 * Title: RedPigWeixinUserAddressAction
 * </p>
 * 
 * <p>
 * Description: 用户中心地址管理
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
 * 
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigWeixinUserAddressAction extends BaseAction{
	
	
	/**
	 * 收货地址管理
	 * @param request
	 * @param response
	 * @param return_free_apply
	 * @return
	 */
	@SecurityMapping(title = "收货地址管理", value = "/buyer/address*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/address" })
	public ModelAndView address(HttpServletRequest request,
			HttpServletResponse response, String return_free_apply) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/address.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		List<Address> addresses = this.addressService.queryPageList(params);
		
		mv.addObject("addrs", addresses);
		mv.addObject("return_free_apply", return_free_apply);
		mv.addObject("areaViewTools", this.areaViewTools);
		return mv;
	}
	
	/**
	 * 编辑收货地址
	 * @param request
	 * @param response
	 * @param id
	 * @param return_free_apply
	 * @param integarl_url
	 * @return
	 */
	@SecurityMapping(title = "编辑收货地址", value = "/buyer/address_edit*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_edit", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/address_edit" })
	public ModelAndView address_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String return_free_apply,
			String integarl_url) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		Address obj = this.addressService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("integarl_url", integarl_url);
		mv.addObject("return_free_apply", return_free_apply);
		mv.addObject("obj", obj);
		mv.addObject("areaViewTools", this.areaViewTools);
		return mv;
	}
	
	/**
	 * 收货地址保存
	 * @param request
	 * @param response
	 * @param id
	 * @param area_id
	 * @param return_free_apply
	 * @param integarl_url
	 * @return
	 */
	@SecurityMapping(title = "收货地址保存", value = "/buyer/address_save*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_save", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/address_save" })
	public String address_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String return_free_apply, String integarl_url) {
		
		Address address = null;
		if ((id == null) || (id.equals(""))) {
			address = (Address) WebForm.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			address = (Address) WebForm.toPo(request, obj);
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		address.setArea(area);
		if ((id == null) || (id.equals(""))) {
			this.addressService.saveEntity(address);
		} else {
			this.addressService.updateById(address);
		}
		if (integarl_url.equals("1")) {
			return "redirect:/integral/exchange2";
		}
		if (address.getDefault_val() == 1) {
			if ((return_free_apply != null) && (!return_free_apply.equals(""))) {
				return "redirect:" + return_free_apply;
			}
			return "redirect:/buyer/address";
		}
		if ((return_free_apply != null) && (!return_free_apply.equals(""))) {
			return "redirect:/buyer/address?return_free_apply="
					+ return_free_apply;
		}
		return "redirect:/buyer/address";
	}
	
	/**
	 * 收货地址删除
	 * @param request
	 * @param response
	 * @param id
	 * @param return_free_apply
	 * @return
	 */
	@SecurityMapping(title = "收货地址删除", value = "/buyer/address_del*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_address_del", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/address_del" })
	public String address_del(HttpServletRequest request,
			HttpServletResponse response, String id, String return_free_apply) {
		Address address = this.addressService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (address != null) {
			if (address.getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				this.addressService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		if ((return_free_apply != null) && (!return_free_apply.equals(""))) {
			return "redirect:/buyer/address?return_free_apply="
					+ return_free_apply;
		}
		return "redirect:/buyer/address";
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/buyer/address_add_ajax" })
	public void address_add_ajax(HttpServletRequest request,
			HttpServletResponse response, String aid) {
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(aid));
		Map json_map = Maps.newHashMap();
		List map_list = Lists.newArrayList();
		Map<String,Object> maps = Maps.newHashMap();
       
		if ((area != null) && (!area.equals(""))) {
			if (area.getLevel() != 2) {
				json_map.put("level", Boolean.valueOf(true));
				List<Area> childs = area.getChilds();
				for (Area child : childs) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("addr_id", child.getId());
					map.put("addr_name", child.getAreaName());
					map_list.add(map);
				}
				json_map.put("data", map_list);
			} else {
				
				json_map.put("level", Boolean.valueOf(false));
				maps.put("parent", -1);
				List<Area> areas = this.areaService.queryPageList(maps);
				
				for (Area child : areas) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("addr_id", child.getId());
					map.put("addr_name", child.getAreaName());
					map_list.add(map);
				}
				json_map.put("data", map_list);
				json_map.put("info", this.areaViewTools.generic_area_info(aid));
				json_map.put("aid", aid);
			}
		} else {
			json_map.put("level", Boolean.valueOf(false));
			maps.put("parent", -1);
			List<Area> areas = this.areaService.queryPageList(maps);
			
			for (Area child : areas) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("addr_id", child.getId());
				map.put("addr_name", child.getAreaName());
				map_list.add(map);
			}
			json_map.put("data", map_list);
			json_map.put("info", this.areaViewTools.generic_area_info(aid));
			json_map.put("aid", aid);
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

	@RequestMapping({ "/buyer/address_default" })
	public String address_default(HttpServletRequest request,
			HttpServletResponse response, String mulitId,
			String return_free_apply) {
		String[] ids = mulitId.split(",");
        
		for (String id : ids) {
			if (!id.equals("")) {
				Address address = this.addressService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				if (address.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
					params.put("id_no", CommUtil.null2Long(id));
					params.put("default_val", Integer.valueOf(1));
					
					List<Address> addrs = this.addressService.queryPageList(params);
					
					for (Address addr1 : addrs) {
						addr1.setDefault_val(0);
						this.addressService.updateById(addr1);
					}
					address.setDefault_val(1);
					this.addressService.updateById(address);
				}
			}
		}
		if ((return_free_apply == null) || (return_free_apply.equals(""))) {
			return "redirect:/buyer/address";
		}
		return "redirect:" + return_free_apply;
	}
}
