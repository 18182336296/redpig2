package com.redpigmall.manage.buyer.action;

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
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.Area;


/**
 * 
 * <p>
 * Title: RedPigAddressBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:买家中心地址管理控制器，该控制用来添加、修改、删除地址、设置常用地址
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
 * @date 2014-9-17
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigAddressBuyerAction extends BaseAction{
	
	/**
	 * Address列表页
	 * 
	 * @param request 请求
	 * @param response 响应
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "收货地址列表", value = "/buyer/address*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/address" })
	public ModelAndView address(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		//地址
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/address.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		//查询条件
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "default_val desc,addTime", "desc");
		
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		//分页
		IPageList pList = this.addressService.list(maps);
		
		//参数
		String params = "";
		//保存分页
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/buyer/address.html", "",params, pList, mv);
		//查询所有第一级别-父级别
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		return mv;
	}

	/**
	 * address保存管理
	 * 
	 * @param request 请求
	 * @param response 响应
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "新增收货地址", value = "/buyer/address_add*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/address_add" })
	public ModelAndView address_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		//新增收货地址
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		//查询所有第一级别-父级别
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 新增收货地址
	 * @param request 请求
	 * @param response 响应
	 * @param id ID主键
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "新增收货地址", value = "/buyer/address_edit*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/address_edit" })
	public ModelAndView address_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		//新增收货地址
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//查询所有父类地区
		
		//查询所有第一级别-父级别
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		//根据ID主键查询地址
		Address obj = this.addressService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (obj.getUser().getId()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
			mv.addObject("areas", areas);
			mv.addObject("currentPage", currentPage);
		}
		
		return mv;
	}

	/**
	 * address保存管理
	 * 
	 * @param request 请求
	 * @param response 响应
	 * @param id ID主键
	 * @param area_id 地区ID主键
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "收货地址保存", value = "/buyer/address_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/address_save" })
	public String address_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String currentPage) {
		//用户收货地址
		Address address = null;
		if (id.equals("")) {//ID为空情况新增
			address = (Address) WebForm.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {//更新
			//先查询老的信息,将需要更新的赋值上
			Address obj = this.addressService.selectByPrimaryKey(Long.valueOf(id));
			if (obj.getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				address = (Address) WebForm.toPo(request, obj);
			}
		}
		
		//设置user
		address.setUser(SecurityUserHolder.getCurrentUser());
		//根据地区ID查询地区
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		
		//设置地区
		System.out.println("address_area="+address.getArea());
		address.setArea(area);
		if (id.equals("")) {//ID为空情况新增
			this.addressService.saveEntity(address);
		} else {//更新
			this.addressService.updateById(address);
		}
		
		return "redirect:/buyer/address?currentPage=" + currentPage;
	}
	
	/**
	 * 收货地址删除
	 * @param request 请求
	 * @param response 响应
	 * @param mulitId 多个ID
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "收货地址删除", value = "/buyer/address_del*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/address_del" })
	public String address_del(
			HttpServletRequest request, 
			HttpServletResponse response, 
			String mulitId,
			String currentPage) {
		//ID根据逗号分隔
		String[] ids = mulitId.split(",");
		
		for (String id : ids) {// 批量删除
			if (!id.equals("")) {
				Address address = this.addressService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if (address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
					this.addressService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		return "redirect:address?currentPage=" + currentPage;
	}
	
	/**
	 * 收货地址默认设置
	 * @param request 请求
	 * @param response 响应
	 * @param mulitId 多个ID
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "收货地址默认设置", value = "/buyer/address_default*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/address_default" })
	public String address_default(
			HttpServletRequest request, 
			HttpServletResponse response, 
			String mulitId,
			String currentPage) {
		//ID主键分隔
		String[] ids = mulitId.split(",");
		for (String id : ids) {//批量操作
			if (!id.equals("")) {
				Address address = this.addressService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if (address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("user_id", SecurityUserHolder.getCurrentUser().getId());//用户id
					params.put("id_no", CommUtil.null2Long(id));//id不等于判断
					params.put("default_val", Integer.valueOf(1));//默认地址值
					//查询地址
					List<Address> addrs = this.addressService.queryPageList(params);
					
					for (Address addr1 : addrs) {//更新
						addr1.setDefault_val(0);
						this.addressService.updateById(addr1);
					}
					
					address.setDefault_val(1);
					this.addressService.updateById(address);
				}
			}
		}
		return "redirect:address?currentPage=" + currentPage;
	}
	
	/**
	 * 收货地址默认取消
	 * @param request 请求
	 * @param response 响应
	 * @param mulitId 多个ID
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "收货地址默认取消", value = "/buyer/address_default_cancle*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/address_default_cancle" })
	public String address_default_cancle(
			HttpServletRequest request, 
			HttpServletResponse response, 
			String mulitId,
			String currentPage) {
		//ID主键分隔
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Address address = this.addressService.selectByPrimaryKey(Long.valueOf(id));
				if (address.getUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
					address.setDefault_val(0);
					this.addressService.updateById(address);
				}
			}
		}
		return "redirect:address?currentPage=" + currentPage;
	}
}
