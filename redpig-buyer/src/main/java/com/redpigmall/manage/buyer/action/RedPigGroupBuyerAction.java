package com.redpigmall.manage.buyer.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigGroupBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家生活购控制器，查看列表，使用过的状况
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
 * @date 2014-5-23
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigGroupBuyerAction extends BaseAction{
	
	/**
	 * 买家生活购订单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @return
	 */
	@SecurityMapping(title = "买家生活购订单列表", value = "/buyer/group*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/group" })
	public ModelAndView buyer_group(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_group.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		maps.put("user_id", user.getId().toString());
		
		maps.put("order_main", 0);
		
		maps.put("order_cat", 2);//团购订单
		
		if (!CommUtil.null2String(order_id).equals("")) {
			maps.put("order_id_like", order_id);
			
			mv.addObject("order_id", order_id);
		}
		
		mv.addObject("orderFormTools", this.orderFormTools);
		IPageList pList = this.orderFormService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}

	/**
	 * 买家中心消费码列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "买家生活购消费码列表", value = "/buyer/groupinfo_list*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/groupinfo_list" })
	public ModelAndView groupinfo_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String info_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/groupinfo_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("user_id", user.getId());
		
		if (!CommUtil.null2String(info_id).equals("")) {
			
			maps.put("group_sn_like", info_id);
			mv.addObject("info_id", info_id);
		}
		
		IPageList pList = this.groupinfoService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/groupinfo_list.html",
				"", params, pList, mv);
		return mv;
	}

	/**
	 * 支付生活购订单
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "支付生活购订单", value = "/buyer/pay_lifeorder*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/pay_lifeorder" })
	public ModelAndView pay_lifeorder(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("order_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (obj != null
				&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())
				&& (obj.getOrder_status() == 10)) {
				
				mv.addObject("order", obj);
				mv.addObject("all_of_price", obj.getTotalPrice());
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("group", Boolean.valueOf(true));
				mv.addObject("from", "grouplife");
				
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/group");
		}
		return mv;
	}
	
	/**
	 * 生活购订单详情
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "生活购订单详情", value = "/buyer/lifeorder_view*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/lifeorder_view" })
	public ModelAndView lifeorder_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/lifeorder_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		if (obj != null 
				&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString()) 
				&& obj.getOrder_status() == 20) {
				
				Map json = this.orderFormTools.queryGroupInfo(obj.getGroup_info());
				String url = this.configService.getSysConfig().getAddress();
				if ((url == null) || (url.equals(""))) {
					url = CommUtil.getURL(request);
				}
				String params = "";
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, null, null);
				
				maps.put("user_id", user.getId());
				
				maps.put("order_id", obj.getId());
				
				maps.put("lifeGoods_id",json.get("goods_id").toString());
				
				IPageList pList = this.groupinfoService.queryPagesWithNoRelations(maps);
				
				CommUtil.saveIPageList2ModelAndView(url + "/buyer/groupinfo_list.html", "", params, pList, mv);
				GroupLifeGoods goods = this.groupLifeGoodsService
						.selectByPrimaryKey(CommUtil.null2Long(json.get("goods_id")));
				mv.addObject("infos", pList.getResult());
				mv.addObject("order", obj);
				mv.addObject("goods", goods);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/group");
		}
		return mv;
	}
	
	/**
	 * 订单取消
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "订单取消", value = "/buyer/lifeorder_cancel*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/lifeorder_cancel" })
	public String lifeorder_cancel(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String id) {
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (of.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			of.setOrder_status(0);
			this.orderFormService.updateById(of);
		}
		return "redirect:/buyer/group?currentPage" + currentPage;
	}
}
