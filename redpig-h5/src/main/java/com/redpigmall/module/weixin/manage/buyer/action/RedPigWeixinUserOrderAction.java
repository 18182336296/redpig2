package com.redpigmall.module.weixin.manage.buyer.action;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.User;
import com.redpigmall.domain.virtual.TransInfo;

import com.redpigmall.module.weixin.view.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigRedPigWeixinUserOrderAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端买家中心订单控制器
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
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({"rawtypes","unchecked"})
@Controller
public class RedPigWeixinUserOrderAction extends BaseAction{
	
	/**
	 * 订单列表
	 * @param request
	 * @param response
	 * @param type
	 * @param order_status
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "订单列表", value = "/buyer/order_list*", rtype = "buyer", rname = "移动端用户订单列表", rcode = "wap_order_list", rgroup = "移动端用户订单")
	@RequestMapping({ "/buyer/order_list" })
	public ModelAndView order_list(HttpServletRequest request,
			HttpServletResponse response, String type, String order_status,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Int(currentPage) == 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/order_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		} else if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/order_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");

		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		maps.put("user_id",SecurityUserHolder.getCurrentUser().getId().toString());
		
		maps.put("order_main",1);
		maps.put("order_cat_no",2);
		maps.put("deleteStatus_no",-1);
		
		if (!"".equals(CommUtil.null2String(order_status))) {
			if (order_status.equals("10")) {
				maps.put("order_status1",10);
				maps.put("order_status2",11);
				mv.addObject("text", "待付款");
			}
			
			if (order_status.equals("20")) {
				maps.put("order_status",16);
				maps.put("order_status2",20);
				mv.addObject("text", "待发货");
			}
			
			if (order_status.equals("30")) {
				maps.put("order_status",30);
				mv.addObject("text", "待收货");
			}
			
			if (order_status.equals("40")) {
				maps.put("order_status",40);
				mv.addObject("text", "待评价");
			}
			
			if (order_status.equals("50")) {
				maps.put("order_status",50);
				mv.addObject("text", "已完成");
			}
			
			if (order_status.equals("0")) {
				maps.put("order_status",0);
				mv.addObject("text", "已取消");
			}
			
		}
		IPageList pList = this.orderFormService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		int[] status = { 10, 30, 50 };
		String[] string_status = { "order_submit", "order_shipping","order_finish" };
		
		Map<String,Object> orders_status = Maps.newLinkedHashMap();
		
		for (int i = 0; i < status.length; i++) {
			maps.clear();
			
	        maps.put("order_main", 1);
	        maps.put("user_id", user.getId().toString());
	        maps.put("order_status", status[i]);
	        
			int size = this.orderFormService.selectCount(maps);
			
			mv.addObject("order_size_" + status[i], Integer.valueOf(size));
			orders_status.put(string_status[i], Integer.valueOf(size));
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		return mv;
	}
	
	
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param type
	 * @param order_status
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/buyer/order_data" })
	public ModelAndView order_data(HttpServletRequest request,
			HttpServletResponse response, String type, String order_status,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
	        maps.put("user_id",SecurityUserHolder.getCurrentUser().getId().toString());
	        
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			
			maps.put("order_main",1);
			maps.put("order_cat_no",2);
			maps.put("deleteStatus_no",-1);
			
			if (!"".equals(CommUtil.null2String(order_status))) {
				if (order_status.equals("10")) {
					maps.put("order_status",10);
					mv.addObject("text", "待付款");
				}
				
				if (order_status.equals("20")) {
					maps.put("order_status",20);
					mv.addObject("text", "待发货");
				}
				
				if (order_status.equals("30")) {
					maps.put("order_status",30);
				}
				
				if (order_status.equals("40")) {
					maps.put("order_status",40);
				}
				
				if (order_status.equals("50")) {
					maps.put("order_status",50);
				}
				
				if (order_status.equals("0")) {
					maps.put("order_status",0);
				}
			}
			
			IPageList pList = this.orderFormService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			
			int[] status = { 10, 30, 50 };
			String[] string_status = { "order_submit", "order_shipping","order_finish" };
			
			Map orders_status = new LinkedHashMap();
			for (int i = 0; i < status.length; i++) {
				maps.clear();
		        maps.put("order_main",1);
		        maps.put("user_id",user.getId().toString());
		        maps.put("order_status",status[i]);
		        
				int size = this.orderFormService.selectCount(maps);
				
				mv.addObject("order_size_" + status[i], Integer.valueOf(size));
				orders_status.put(string_status[i], Integer.valueOf(size));
			}
			mv.addObject("orderFormTools", this.orderFormTools);
			mv.addObject("order_status", order_status);
			mv.addObject("type", type);
		} else {
			mv.addObject("unuser", "unuser");
		}
		return mv;
	}
	
	/**
	 * 订单详情
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @param mark
	 * @return
	 */
	@SecurityMapping(title = "订单详情", value = "/buyer/order_view*", rtype = "buyer", rname = "移动端用户订单详情", rcode = "wap_order_detail", rgroup = "移动端用户订单")
	@RequestMapping({ "/buyer/order_view" })
	public ModelAndView order_detail(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String mark) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj != null)
				&& (obj.getUser_id()
						.toString()
						.compareTo(
								SecurityUserHolder.getCurrentUser().getId()
										.toString()) == 0)) {
			if (obj.getOrder_cat() == 1) {
				mv = new RedPigJModelAndView(
						"user/default/usercenter/weixin/recharge_order_detail.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
			boolean query_ship = false;
			if (!CommUtil.null2String(obj.getShipCode()).equals("")) {
				query_ship = true;
			}
			mv.addObject("obj", obj);
			if (obj != null) {
				String temp = obj.getSpecial_invoice();
				if ((temp != null) && (!"".equals(temp))) {
					Map of_va = (Map) JSON.parseObject(temp, Map.class);
					mv.addObject("of_va", of_va);
				}
			}
			mv.addObject("orderFormTools", this.orderFormTools);
			mv.addObject("query_ship", Boolean.valueOf(query_ship));
			mv.addObject("mark", mark);
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/mobile/index");
		}
		if ((!CommUtil.null2String(type).equals("")) && (!type.equals("0"))) {
			mv.addObject("type", type);
		}
		return mv;
	}
	
	
	
	/**
	 * 订单取消
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "订单取消", value = "/buyer/order_cancel*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_order_cancel", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/order_cancel" })
	public String order_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (of.getUser_id().equals(
				SecurityUserHolder.getCurrentUser().getId().toString())) {
			of.setOrder_status(0);
			this.orderFormService.updateById(of);
		}
		return "redirect:/buyer/group_list";
	}
	
	/**
	 * 订单取消保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param state_info
	 * @param other_state_info
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "订单取消保存", value = "/buyer/order_cancel_save*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_order_cancel_save", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/order_cancel_save" })
	public String order_cancel_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		List<OrderForm> objs = Lists.newArrayList();
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		objs.add(obj);
		boolean all_verify = true;
		Date nowDate = new Date();
		OrderForm child_order;
		if (obj != null) {
			if (obj.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId().toString())) {
				if ((obj.getOrder_main() == 1)
						&& (obj.getChild_order_detail() != null)) {
					List<Map<String, Object>> maps = (List) JSON.parseObject(
							CommUtil.null2String(obj.getChild_order_detail()),
							new TypeReference() {
							}, new Feature[0]);
					if (maps != null) {
						for (Map<String, Object> map : maps) {
							child_order = this.orderFormService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("order_id")));
							objs.add(child_order);
						}
					}
				}
				for (OrderForm of : objs) {
					if (of.getOrder_status() >= 20) {
						all_verify = false;
					}
				}
			}
		}
		if ((all_verify) && (obj != null)) {
			if (obj.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId().toString())) {
				if ((obj.getOrder_main() == 1)
						&& (obj.getChild_order_detail() != null)) {
					List<Map> maps = (List) JSON.parseObject(
							CommUtil.null2String(obj.getChild_order_detail()),
							new TypeReference() {
							}, new Feature[0]);
					if (maps != null) {
						for (Map map : maps) {
							child_order = this.orderFormService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("order_id")));
							child_order.setOrder_status(0);
							if ((child_order.getCoupon_info() != null)
									&& (!"".equals(child_order.getCoupon_info()))) {
								Map m = (Map) JSON
										.parseObject(
												child_order.getCoupon_info(),
												Map.class);
								CouponInfo cpInfo = this.couponInfoService
										.selectByPrimaryKey(CommUtil.null2Long(m
												.get("couponinfo_id")));
								if (cpInfo != null) {
									if (nowDate.before(cpInfo.getEndDate())) {
										cpInfo.setStatus(0);
									} else {
										cpInfo.setStatus(-1);
									}
									this.couponInfoService.updateById(cpInfo);
								}
							}
							this.orderFormService.updateById(child_order);
						}
					}
				}
				obj.setOrder_status(0);
				if ((obj.getCoupon_info() != null)
						&& (!"".equals(obj.getCoupon_info()))) {
					Map m = (Map) JSON.parseObject(obj.getCoupon_info(),
							Map.class);
					CouponInfo cpInfo = this.couponInfoService
							.selectByPrimaryKey(CommUtil.null2Long(m
									.get("couponinfo_id")));
					if (cpInfo != null) {
						if (nowDate.before(cpInfo.getEndDate())) {
							cpInfo.setStatus(0);
						} else {
							cpInfo.setStatus(-1);
						}
					}
				}
				this.orderFormService.updateById(obj);
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("取消订单");
				ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
				ofl.setLog_user_name(SecurityUserHolder.getCurrentUser()
						.getUserName());
				ofl.setOf(obj);
				if (state_info.equals("other")) {
					ofl.setState_info(other_state_info);
				} else {
					ofl.setState_info(state_info);
				}
				this.orderFormLogService.saveEntity(ofl);

				this.orderFormTools.sendMsgWhenHandleOrder(
						CommUtil.getURL(request), obj, null,
						"toseller_order_cancel_notify");
			}
		}
		return "redirect:/buyer/order_list";
	}
	
	/**
	 * 买家已经购买商品付款
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "买家已经购买商品付款", value = "/buyer/go_pay*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/go_pay" })
	public ModelAndView go_pay(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_cart3.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (order.getUser_id().toString()
				.equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			double all_of_price = this.orderFormTools.query_order_price(id);
			mv.addObject("order", order);
			mv.addObject("all_of_price", Double.valueOf(all_of_price));
			mv.addObject("paymentTools", this.paymentTools);
			List<Payment> payments = Lists.newArrayList();
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", "wx_pay");
			
			payments = this.paymentService.queryPageList(params);
			
			Payment payment = null;
			if (payments.size() > 0) {
				payment = (Payment) payments.get(0);
				mv.addObject("appid", payment.getWx_appid());
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			if (order.getOrder_cat() == 2) {
				mv.addObject("op_title", "团购订单编号错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/group_list");
			} else {
				mv.addObject("op_title", "订单编号错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order_list");
			}
		}
		return mv;
	}
	
	/**
	 * 确认收货
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "确认收货", value = "/buyer/order_cofirm*", rtype = "buyer", rname = "移动端用户订单确认收货", rcode = "wap_order_cofirm", rgroup = "移动端用户订单")
	@RequestMapping({ "/buyer/order_cofirm" })
	public ModelAndView order_cofirm(HttpServletRequest request,
			HttpServletResponse response, String id) throws Exception {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((obj != null)
				&& (obj.getUser_id()
						.toString()
						.compareTo(
								SecurityUserHolder.getCurrentUser().getId()
										.toString()) == 0)) {
			this.handelOrderFormService.confirmOrder(request, obj);
			mv.addObject("op_title", "确认成功");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_list");
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_list");
		}
		return mv;
	}
	
	/**
	 * 订单评论
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "订单评论", value = "/buyer/order_discuss*", rtype = "buyer", rname = "移动端用户订单评论", rcode = "wap_order_cofirm", rgroup = "移动端用户订单")
	@RequestMapping({ "/buyer/order_discuss" })
	public ModelAndView order_discuss(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_discuss.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj != null)
				&& (obj.getUser_id()
						.toString()
						.compareTo(
								SecurityUserHolder.getCurrentUser().getId()
										.toString()) == 0)) {
			mv.addObject("obj", obj);
			mv.addObject("orderFormTools", this.orderFormTools);
			String evaluate_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("evaluate_session",
					evaluate_session);
			mv.addObject("evaluate_session", evaluate_session);
			if (obj.getOrder_status() >= 50) {
				mv = new RedPigJModelAndView("weixin/success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单已经评价！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order_list");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 买家评价保存
	 * @param request
	 * @param response
	 * @param evaluate_session
	 * @param id
	 * @param checkState
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "买家评价保存", value = "/buyer/order_discuss_save*", rtype = "buyer", rname = "移动端用户订单评论", rcode = "user_center", rgroup = "移动端用户订单")
	@RequestMapping({ "/buyer/order_discuss_save" })
	public ModelAndView order_discuss_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String evaluate_session, String id,
			String checkState) throws Exception {
		String evaluate_session1 = (String) request.getSession(false)
				.getAttribute("evaluate_session");
		if ((evaluate_session1 != null)
				&& (!evaluate_session1.equals(evaluate_session))) {
			ModelAndView mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "禁止重复评价");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_list");
		}
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj == null) || (!obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString()))) {
			ModelAndView mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，禁止评价");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order_list");
		}
		List<Map> json = Lists.newArrayList();
		if ((obj.getOrder_status() != 40) && (obj.getOrder_status() != 25)) {
			ModelAndView mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "必须是已收货才能评价！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
			return mv;
		}
		if (obj.getOrder_status() == 40) {
			json = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
			for (Map map : json) {
				String goods_gsp_ids = map.get("goods_gsp_ids").toString();
				goods_gsp_ids = goods_gsp_ids.replaceAll(",", "_");
				String goods_id = map.get("goods_id") + "_" + goods_gsp_ids;
				int description = eva_rate(request
						.getParameter("description_evaluate" + goods_id));
				int service = eva_rate(request.getParameter("service_evaluate"
						+ goods_id));
				int ship = eva_rate(request.getParameter("ship_evaluate"
						+ goods_id));
				int total = eva_total_rate(request
						.getParameter("evaluate_buyer_val" + goods_id));
				if ((description == 0) || (service == 0) || (ship == 0)
						|| (total == -5)) {
					ModelAndView mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "未选择评价，请重新评价！");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/order");
					return mv;
				}
			}
		}
		this.handelOrderFormService.evaluateOrderForm(request, checkState, obj);
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "订单评价成功！");
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/order_list");
		return mv;
	}

	private int eva_rate(String rate) {
		int score = 0;
		if (rate.equals("a")) {
			score = 1;
		} else if (rate.equals("b")) {
			score = 2;
		} else if (rate.equals("c")) {
			score = 3;
		} else if (rate.equals("d")) {
			score = 4;
		} else if (rate.equals("e")) {
			score = 5;
		}
		return score;
	}

	private int eva_total_rate(String rate) {
		int score = -5;
		if (rate.equals("a")) {
			score = 1;
		} else if (rate.equals("b")) {
			score = 0;
		} else if (rate.equals("c")) {
			score = -1;
		}
		return score;
	}
	
	/**
	 * 物流信息1
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "物流信息1", value = "/buyer/ship_detail1*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "移动端用户订单")
	@RequestMapping({ "/buyer/ship_detail1" })
	public ModelAndView ship_detail1(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/ship_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<TransInfo> transInfo_list = Lists.newArrayList();
		Map<String, OrderForm> order_map = Maps.newHashMap();
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		if (order.getShipCode() != null) {
			order_map.put(order.getShipCode(), order);
		}
		TransInfo transInfo = this.ShipTools.query_Ordership_getData(CommUtil
				.null2String(order_id));
		if (transInfo != null) {
			transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
					order.getExpress_info(), "express_company_name"));
			transInfo.setExpress_ship_code(order.getShipCode());
			transInfo_list.add(transInfo);
		}
		if (order.getOrder_main() == 1) {
			if (!CommUtil.null2String(order.getChild_order_detail()).equals("")) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(child_map
									.get("order_id")));
					if (child_order.getShipCode() != null) {
						order_map.put(child_order.getShipCode(), child_order);
					}
					TransInfo transInfo1 = this.ShipTools
							.query_Ordership_getData(CommUtil
									.null2String(child_order.getId()));
					if (transInfo1 != null) {
						transInfo1.setExpress_company_name(this.orderFormTools
								.queryExInfo(child_order.getExpress_info(),
										"express_company_name"));
						transInfo1.setExpress_ship_code(child_order
								.getShipCode());
						transInfo_list.add(transInfo1);
					}
				}
			}
		}
		mv.addObject("order_map", order_map);
		mv.addObject("order", order);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("transInfo_list", transInfo_list);
		if ((!CommUtil.null2String(type).equals("")) && (!type.equals("0"))) {
			mv.addObject("type", type);
		}
		return mv;
	}
	
	/**
	 * 物流信息2
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "物流信息2", value = "/buyer/ship_detail2*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "移动端用户订单")
	@RequestMapping({ "/buyer/ship_detail2" })
	public ModelAndView ship_detail2(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/ship_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<TransInfo> transInfo_list = Lists.newArrayList();
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		TransInfo transInfo = this.ShipTools.query_Ordership_getData(CommUtil
				.null2String(order_id));
		if (transInfo != null) {
			transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
					order.getExpress_info(), "express_company_name"));
			transInfo.setExpress_ship_code(order.getShipCode());
			transInfo_list.add(transInfo);
		}
		mv.addObject("transInfo_list", transInfo_list);
		mv.addObject("order", order);
		mv.addObject("orderFormTools", this.orderFormTools);
		if ((!CommUtil.null2String(type).equals("")) && (!type.equals("0"))) {
			mv.addObject("type", type);
		}
		return mv;
	}
	
	/**
	 * 买家物流详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "买家物流详情", value = "/user/ship_view*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "移动端用户订单")
	@RequestMapping({ "/mobile/ship_view" })
	public ModelAndView order_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/ship_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (order != null) {
			if (order.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId().toString())) {
				List<TransInfo> transInfo_list = Lists.newArrayList();
				TransInfo transInfo = this.ShipTools
						.query_Ordership_getData(id);
				transInfo.setExpress_company_name(this.orderFormTools
						.queryExInfo(order.getExpress_info(),
								"express_company_name"));
				transInfo.setExpress_ship_code(order.getShipCode());
				transInfo_list.add(transInfo);
				mv.addObject("transInfo_list", transInfo_list);
				mv.addObject("obj", order);
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您查询的物流不存在！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "user/buyer/order");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查询的物流不存在！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "user/buyer/order");
		}
		return mv;
	}
}
