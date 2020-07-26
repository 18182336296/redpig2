package com.redpigmall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.IntegralGoods;
import com.redpigmall.domain.IntegralGoodsCart;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.User;
import com.redpigmall.view.web.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigRedPigIntegralViewAction.java
 * </p>
 * 
 * <p>
 * Description:积分商城控制器,用来控制积分商城所有前端展示、兑换、订单信息
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
 * @author redpig,jy
 * 
 * @date 2014-4-30
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigIntegralViewAction extends BaseAction{
	
	/**
	 * 积分商城首页
	 * @param request
	 * @param response
	 * @param begin
	 * @param end
	 * @param rank
	 * @return
	 */
	@RequestMapping({ "/integral/index" })
	public ModelAndView integral(HttpServletRequest request,
			HttpServletResponse response, String begin, String end, String rank) {
		ModelAndView mv = new RedPigJModelAndView("integral/integral.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		if (this.configService.getSysConfig().getIntegralStore()) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("ig_recommend", Boolean.valueOf(true));
			params.put("ig_show", Boolean.valueOf(true));
			List<IntegralGoods> recommend_igs = Lists.newArrayList();
			if ((begin != null) && (!begin.equals("")) && (end != null)
					&& (!end.equals(""))) {
				if (end.equals("0")) {
					params.put("ig_goods_integral_more_than",Integer.valueOf(CommUtil.null2Int(begin)));
					params.put("orderBy", "ig_sequence");
					params.put("orderType", "asc");
					
					recommend_igs = this.integralGoodsService.queryPageList(params, 0, 8);
				} else {
					params.put("ig_goods_integral_more_than",Integer.valueOf(CommUtil.null2Int(begin)));
					params.put("ig_goods_integral_less_than", Integer.valueOf(CommUtil.null2Int(end)));
					params.put("orderBy", "ig_sequence");
					params.put("orderType", "asc");
					
					recommend_igs = this.integralGoodsService.queryPageList(params, 0, 8);
				}
			} else {
				params.put("orderBy", "ig_sequence");
				params.put("orderType", "asc");
				
				recommend_igs = this.integralGoodsService.queryPageList(params, 0, 8);
			}
			
			mv.addObject("recommend_igs", recommend_igs);
			params.clear();
			params.put("orderBy", "ig_exchange_count");
			params.put("orderType", "desc");
			
			List<IntegralGoods> exchange_igs = this.integralGoodsService.queryPageList(params, 0, 13);
			
			mv.addObject("exchange_igs", exchange_igs);
			if (SecurityUserHolder.getCurrentUser() != null) {
				mv.addObject("user",
						this.userService.selectByPrimaryKey(SecurityUserHolder
								.getCurrentUser().getId()));
			}
			mv.addObject("integral_cart", request.getSession(false)
					.getAttribute("integral_goods_cart"));
			mv.addObject("integralViewTools", this.integralViewTools);
			if (rank != null) {
				mv.addObject("rank", rank);
			} else {
				mv.addObject("rank", Integer.valueOf(0));
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 积分商品查询
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/integral/view" })
	public ModelAndView integral_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("integral/integral_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().getIntegralStore()) {
			IntegralGoods obj = this.integralGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (obj != null) {
				obj.setIg_click_count(obj.getIg_click_count() + 1);
				this.integralGoodsService.updateById(obj);
				Map<String,Object> params = Maps.newHashMap();
				params.put("orderBy", "ig_exchange_count");
				params.put("orderType", "desc");
				
				List<IntegralGoods> gcs = this.integralGoodsService.queryPageList(params, 0, 10);
				mv.addObject("gcs", gcs);
				mv.addObject("obj", obj);
				if (SecurityUserHolder.getCurrentUser() != null) {
					mv.addObject("user", this.userService
							.selectByPrimaryKey(SecurityUserHolder.getCurrentUser()
									.getId()));
					mv.addObject("integralViewTools", this.integralViewTools);
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "不存在该商品，参数错误");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 积分商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param rang_begin
	 * @param rang_end
	 * @param rank
	 * @return
	 */
	@RequestMapping({ "/integral/list" })
	public ModelAndView integral_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String rang_begin, String rang_end, String rank) {
		ModelAndView mv = new RedPigJModelAndView("integral/integral_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((orderType != null) && (!orderType.equals(""))) {
			orderBy = "ig_sequence";
		} else {
			orderBy = "addTime";
		}
		
		if (this.configService.getSysConfig().getIntegralStore()) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 16 ,orderBy, orderType);
			if ((rang_begin != null) && (!rang_begin.equals(""))) {
				mv.addObject("rang_begin", rang_begin);
				maps.put("ig_goods_integral_more_than_equal", CommUtil.null2Int(rang_begin));
			}
			
			if ((rang_end != null) && (!rang_end.equals("")) && (!rang_end.equals("0"))) {
				mv.addObject("rang_end", rang_end);
				maps.put("ig_goods_integral_less_than", CommUtil.null2Int(rang_end));
				
			}
			maps.put("ig_show", Boolean.valueOf(true));
			
			IPageList pList = this.integralGoodsService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			maps.put("orderBy", "ig_exchange_count");
			maps.put("orderType", "desc");
			
			List<IntegralGoods> exchange_igs = this.integralGoodsService.queryPageList(maps, 0, 15);
			
			mv.addObject("exchange_igs", exchange_igs);
			if (SecurityUserHolder.getCurrentUser() != null) {
				mv.addObject("user",
						this.userService.selectByPrimaryKey(SecurityUserHolder
								.getCurrentUser().getId()));
			}
			mv.addObject("integral_cart", request.getSession(false)
					.getAttribute("integral_goods_cart"));
			mv.addObject("integralViewTools", this.integralViewTools);
			if (rank != null) {
				mv.addObject("rank", rank);
			} else {
				mv.addObject("rank", Integer.valueOf(0));
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 积分兑换第一步
	 * @param request
	 * @param response
	 * @param id
	 * @param exchange_count
	 * @return
	 */
	@SecurityMapping(title = "积分兑换第一步", value = "/integral/exchange1*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
	@RequestMapping({ "/integral/exchange1" })
	public ModelAndView integral_exchange1(HttpServletRequest request,
			HttpServletResponse response, String id, String exchange_count) {
		ModelAndView mv = new RedPigJModelAndView("integral/integral_exchange1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		if (this.configService.getSysConfig().getIntegralStore()) {
			IntegralGoods obj = this.integralGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			int exchange_status = 0;
			if (obj != null) {
				if ((exchange_count == null) || (exchange_count.equals(""))) {
					exchange_count = "1";
				}
				if (obj.getIg_goods_count() < CommUtil.null2Int(exchange_count)) {
					exchange_status = -1;
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "库存数量不足，重新选择兑换数量");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/integral/view_" + id + "");
				}
				
				if (obj.getIg_limit_type()) {
					if (obj.getIg_limit_count() < CommUtil
							.null2Int(exchange_count)) {
						exchange_status = -2;
						mv = new RedPigJModelAndView("error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title",
								"限制最多兑换" + obj.getIg_limit_count()
										+ "，重新选择兑换数量");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/integral/view_" + id + "");
					}
				}
				int cart_total_integral = obj.getIg_goods_integral()
						* CommUtil.null2Int(exchange_count);
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());
				if (user.getIntegral() < cart_total_integral) {
					exchange_status = -3;
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您的积分不足");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/integral/view_" + id + "");
				}
				
				if ((obj.getIg_time_type())
						&& (obj.getIg_begin_time() != null)
						&& (obj.getIg_end_time() != null)
						&& ((obj.getIg_begin_time().after(new Date())) || (obj
								.getIg_end_time().before(new Date())))) {
					exchange_status = -4;
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换已经过期");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/integral/view_" + id + "");
				}
				
				Map Level_map = this.integralViewTools.query_user_level(CommUtil.null2String(user.getId()));
				if (CommUtil.null2Int(Level_map.get("level")) < obj.getIg_user_Level()) {
					exchange_status = -5;
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "您的会员等级不够");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/integral/view_" + id + "");
				}
			}
			if (exchange_status == 0) {
				List<IntegralGoodsCart> integral_goods_cart = (List) request.getSession(false).getAttribute("integral_goods_cart");
				if (integral_goods_cart == null) {
					integral_goods_cart = Lists.newArrayList();
				}
				boolean add = obj != null;
				for (IntegralGoodsCart igc : integral_goods_cart) {
					if (igc.getGoods().getId().toString().equals(id)) {
						add = false;
						break;
					}
				}
				if (add) {
					IntegralGoodsCart gc = new IntegralGoodsCart();
					gc.setAddTime(new Date());
					gc.setCount(CommUtil.null2Int(exchange_count));
					gc.setGoods(obj);
					gc.setTrans_fee(obj.getIg_transfee());
					gc.setIntegral(CommUtil.null2Int(exchange_count) * obj.getIg_goods_integral());
					integral_goods_cart.add(gc);
				}
				request.getSession(false).setAttribute("integral_goods_cart",integral_goods_cart);
				int total_integral = 0;
				for (IntegralGoodsCart igc : integral_goods_cart) {
					total_integral += igc.getIntegral();
				}
				
				mv.addObject("total_integral", Integer.valueOf(total_integral));
				mv.addObject("integral_cart", integral_goods_cart);
				mv.addObject("user",
						this.userService.selectByPrimaryKey(SecurityUserHolder
								.getCurrentUser().getId()));
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 积分购物车删除
	 * @param request
	 * @param response
	 * @param id
	 */
	
	@RequestMapping({ "/integral/cart_remove" })
	public void integral_cart_remove(HttpServletRequest request,
			HttpServletResponse response, String id) {
		List<IntegralGoodsCart> igcs = (List) request.getSession(false)
				.getAttribute("integral_goods_cart");
		for (IntegralGoodsCart igc : igcs) {
			if (igc.getGoods().getId().toString().equals(id)) {
				igcs.remove(igc);
				break;
			}
		}
		int total_integral = 0;
		for (IntegralGoodsCart igc : igcs) {
			total_integral += igc.getIntegral();
		}
		request.getSession(false).setAttribute("integral_goods_cart", igcs);
		Map<String,Object> map = Maps.newHashMap();
		map.put("status", 100);
		map.put("total_integral", total_integral);
		map.put("size", igcs.size());
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
	 * 积分商品数量修改
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param count
	 */
	@RequestMapping({ "/integral/adjust_count" })
	public void integral_adjust_count(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String count) {
		List<IntegralGoodsCart> igcs = (List) request.getSession(false)
				.getAttribute("integral_goods_cart");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		IntegralGoodsCart obj = null;
		int old_num = 0;
		int num = CommUtil.null2Int(count);
		for (IntegralGoodsCart igc : igcs) {
			if (igc.getGoods().getId().toString().equals(goods_id)) {
				IntegralGoods ig = igc.getGoods();
				old_num = igc.getCount();
				if (num > ig.getIg_goods_count()) {
					num = ig.getIg_goods_count();
				}
				
				if ((ig.getIg_limit_type()) && (ig.getIg_limit_count() < num)) {
					num = ig.getIg_limit_count();
				}
				igc.setCount(num);
				igc.setIntegral(igc.getGoods().getIg_goods_integral()
						* CommUtil.null2Int(Integer.valueOf(num)));
				obj = igc;
				break;
			}
		}
		int total_integral = 0;
		for (IntegralGoodsCart igc : igcs) {
			total_integral += igc.getIntegral();
		}
		if (total_integral > user.getIntegral()) {
			for (IntegralGoodsCart igc : igcs) {
				if (igc.getGoods().getId().toString().equals(goods_id)) {
					num = old_num;
					
					igc.setCount(num);
					igc.setIntegral(igc.getGoods().getIg_goods_integral()
							* CommUtil.null2Int(Integer.valueOf(num)));
					obj = igc;
					break;
				}
			}
			total_integral = 0;
			for (IntegralGoodsCart igc : igcs) {
				total_integral += igc.getIntegral();
			}
		}
		request.getSession(false).setAttribute("integral_goods_cart", igcs);
		Map<String,Object> map = Maps.newHashMap();
		map.put("total_integral", Integer.valueOf(total_integral));
		map.put("integral", Integer.valueOf(obj.getIntegral()));
		map.put("count", Integer.valueOf(num));
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
	 * 积分兑换第二步
	 * @param request
	 * @param response
	 * @param id
	 * @param exchange_count
	 * @return
	 */
	@SecurityMapping(title = "积分兑换第二步", value = "/integral/exchange2*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
	@RequestMapping({ "/integral/exchange2" })
	public ModelAndView integral_exchange2(HttpServletRequest request,
			HttpServletResponse response, String id, String exchange_count) {
		ModelAndView mv = new RedPigJModelAndView("integral/integral_exchange2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		if (this.configService.getSysConfig().getIntegralStore()) {
			List<IntegralGoodsCart> igcs = (List) request.getSession(false)
					.getAttribute("integral_goods_cart");
			if (igcs != null) {
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());
				Map<String, Object> params = Maps.newHashMap();
				params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
				
				List<Address> addrs = this.addressService.queryPageList(params);
				
				if (addrs.size() >= 1) {
					mv.addObject("addrs", addrs);
				}
				mv.addObject("igcs", igcs == null ? new ArrayList() : igcs);
				int total_integral = 0;
				double trans_fee = 0.0D;
				for (IntegralGoodsCart igc : igcs) {
					total_integral += igc.getIntegral();
					trans_fee = CommUtil.null2Double(igc.getTrans_fee())
							+ trans_fee;
				}
				if (user.getIntegral() < total_integral) {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换积分不足");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/integral/index");
					return mv;
				}
				mv.addObject("trans_fee", Double.valueOf(trans_fee));
				mv.addObject("total_integral", Integer.valueOf(total_integral));
				String integral_order_session = CommUtil.randomString(32);
				mv.addObject("integral_order_session", integral_order_session);
				request.getSession(false).setAttribute(
						"integral_order_session", integral_order_session);
				params.clear();
				params.put("parent", -1);
				
				List<Area> areas = this.areaService.queryPageList(params);
				
				mv.addObject("areas", areas);
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "兑换购物车为空");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/integral/index");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 积分兑换第三步
	 * @param request
	 * @param response
	 * @param addr_id
	 * @param igo_msg
	 * @param integral_order_session
	 * @param area_id
	 * @param trueName
	 * @param area_info
	 * @param zip
	 * @param telephone
	 * @param mobile
	 * @return
	 */
	@SecurityMapping(title = "积分兑换第三步", value = "/integral/exchange3*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
	@RequestMapping({ "/integral/exchange3" })
	public ModelAndView integral_exchange3(HttpServletRequest request,
			HttpServletResponse response, String addr_id, String igo_msg,
			String integral_order_session, String area_id, String trueName,
			String area_info, String zip, String telephone, String mobile) {
		ModelAndView mv = new RedPigJModelAndView("integral/integral_exchange3.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		if (this.configService.getSysConfig().getIntegralStore()) {
			List<IntegralGoodsCart> igcs = (List) request.getSession(false)
					.getAttribute("integral_goods_cart");
			String integral_order_session1 = CommUtil.null2String(request
					.getSession(false).getAttribute("integral_order_session"));
			if (integral_order_session1.equals("")) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "表单已经过期");
				mv.addObject("url", CommUtil.getURL(request) + "/integral");
			} else if (integral_order_session1.equals(integral_order_session
					.trim())) {
				if (igcs != null) {
					int total_integral = 0;
					double trans_fee = 0.0D;
					for (IntegralGoodsCart igc : igcs) {
						total_integral += igc.getIntegral();
						trans_fee = CommUtil.null2Double(igc.getTrans_fee())
								+ trans_fee;
					}
					
					IntegralGoodsOrder order = new IntegralGoodsOrder();
					Address addr = null;
					if (addr_id.equals("new")) {
						addr = new Address();
						addr.setAddTime(new Date());
						Area area = this.areaService.selectByPrimaryKey(CommUtil
								.null2Long(area_id));
						addr.setArea_info(area_info);
						addr.setMobile(mobile);
						addr.setTelephone(telephone);
						addr.setTrueName(trueName);
						addr.setZip(zip);
						addr.setArea(area);
						addr.setUser(SecurityUserHolder.getCurrentUser());
						this.addressService.saveEntity(addr);
					} else {
						addr = this.addressService.selectByPrimaryKey(CommUtil
								.null2Long(addr_id));
					}
					order.setAddTime(new Date());

					order.setReceiver_Name(addr.getTrueName());
					order.setReceiver_area(addr.getArea().getParent()
							.getParent().getAreaName()
							+ addr.getArea().getParent().getAreaName()
							+ addr.getArea().getAreaName());
					order.setReceiver_area_info(addr.getArea_info());
					order.setReceiver_mobile(addr.getMobile());
					order.setReceiver_telephone(addr.getTelephone());
					order.setReceiver_zip(addr.getZip());
					List<Map> json_list = Lists.newArrayList();
					for (IntegralGoodsCart gc : igcs) {
						Map json_map = Maps.newHashMap();
						json_map.put("id", gc.getGoods().getId());
						json_map.put("ig_goods_name", gc.getGoods()
								.getIg_goods_name());
						json_map.put("ig_goods_price", gc.getGoods()
								.getIg_goods_price());
						json_map.put("ig_goods_count",
								Integer.valueOf(gc.getCount()));
						json_map.put("ig_goods_integral", Double
								.valueOf(CommUtil.mul(Integer.valueOf(gc
										.getGoods().getIg_goods_integral()),
										Integer.valueOf(gc.getCount()))));
						json_map.put("ig_goods_tran_fee", gc.getGoods()
								.getIg_transfee());
						json_map.put("ig_goods_img", CommUtil.getURL(request)
								+ "/"
								+ gc.getGoods().getIg_goods_img().getPath()
								+ "/"
								+ gc.getGoods().getIg_goods_img().getName()
								+ "_small."
								+ gc.getGoods().getIg_goods_img().getExt());
						json_list.add(json_map);
					}
					String json = JSON.toJSONString(json_list);
					order.setGoods_info(json);
					order.setIgo_msg(igo_msg);
					User user = this.userService.selectByPrimaryKey(SecurityUserHolder
							.getCurrentUser().getId());
					order.setIgo_order_sn("igo"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ user.getId());
					order.setIgo_user(user);
					order.setIgo_trans_fee(BigDecimal.valueOf(trans_fee));
					order.setIgo_total_integral(total_integral);
					
					if (trans_fee == 0.0D) {
						order.setIgo_status(20);
						order.setIgo_pay_time(new Date());
						order.setIgo_payment("no_fee");
						this.integralGoodsOrderService.saveEntity(order);
						for (IntegralGoodsCart igc : igcs) {
							IntegralGoods goods = igc.getGoods();
							goods.setIg_goods_count(goods.getIg_goods_count()
									- igc.getCount());
							goods.setIg_exchange_count(goods
									.getIg_exchange_count() + igc.getCount());
							this.integralGoodsService.updateById(goods);
						}
						request.getSession(false).removeAttribute(
								"integral_goods_cart");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/integral_order_list");
						mv.addObject("order", order);
					} else {
						order.setIgo_status(0);
						this.integralGoodsOrderService.saveEntity(order);
						mv = new RedPigJModelAndView("integral_exchange4.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("obj", order);
						mv.addObject("paymentTools", this.paymentTools);
						mv.addObject("from", "integralGoods");
					}
					for (IntegralGoodsCart igc : igcs) {
						this.integralGoodsCartService.deleteById(igc.getId());
					}
					user.setIntegral(user.getIntegral()
							- order.getIgo_total_integral());
					this.userService.updateById(user);

					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("兑换商品消耗积分");
					log.setIntegral(-order.getIgo_total_integral());
					log.setIntegral_user(user);
					log.setType("integral_order");
					this.integralLogService.saveEntity(log);
					request.getSession(false).removeAttribute(
							"integral_goods_cart");
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "兑换购物车为空");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/integral/index");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，订单提交失败");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/integral/index");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 积分订单支付
	 * @param request
	 * @param response
	 * @param payType
	 * @param integral_order_id
	 * @return
	 */
	@SecurityMapping(title = "积分订单支付", value = "/integral/order_pay*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
	@RequestMapping({ "/integral/order_pay" })
	public ModelAndView integral_order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType,
			String integral_order_id) {
		ModelAndView mv = null;
		IntegralGoodsOrder order = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(integral_order_id));
		if (order.getIgo_status() == 0) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			} else {
				order.setIgo_payment(payType);
				this.integralGoodsOrderService.updateById(order);
				if (payType.equals("balance")) {
					mv = new RedPigJModelAndView("integral/integral_balance_pay.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
				} else {
					mv = new RedPigJModelAndView("line_pay.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("payType", payType);
					mv.addObject("url", CommUtil.getURL(request));
					mv.addObject("payTools", this.payTools);
					mv.addObject("type", "integral");
					Map<String, Object> params = Maps.newHashMap();
					params.put("install", Boolean.valueOf(true));
					params.put("mark", payType);
					
					List<Payment> payments = this.paymentService.queryPageList(params);
					
					mv.addObject(
							"payment_id",
							payments.size() > 0 ? ((Payment) payments.get(0))
									.getId() : new Payment());
				}
				mv.addObject("integral_order_id", integral_order_id);
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单不能进行付款！");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 订单预付款支付
	 * @param request
	 * @param response
	 * @param payType
	 * @param integral_order_id
	 * @param igo_pay_msg
	 * @return
	 */
	@SecurityMapping(title = "订单预付款支付", value = "/integral/order_pay_balance*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/integral/order_pay_balance" })
	public ModelAndView integral_order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String payType,
			String integral_order_id, String igo_pay_msg) {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoodsOrder order = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(integral_order_id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (order.getIgo_user().getId().equals(user.getId())) {
			if (CommUtil.null2Double(user.getAvailableBalance()) >= CommUtil
					.null2Double(order.getIgo_trans_fee())) {
				order.setIgo_pay_msg(igo_pay_msg);
				order.setIgo_status(20);
				order.setIgo_payment("balance");
				order.setIgo_pay_time(new Date());
				this.integralGoodsOrderService.updateById(order);
				boolean ret = true;
				if (ret) {
					user.setAvailableBalance(BigDecimal.valueOf(CommUtil
							.subtract(user.getAvailableBalance(),
									order.getIgo_trans_fee())));
					this.userService.updateById(user);

					List<IntegralGoods> igs = this.integralGoodsOrderService
							.queryIntegralGoods(integral_order_id);
					for (IntegralGoods obj : igs) {
						int count = this.integralGoodsOrderService
								.queryIntegralOneGoodsCount(order,
										CommUtil.null2String(obj.getId()));
						obj.setIg_goods_count(obj.getIg_goods_count() - count);
						obj.setIg_exchange_count(obj.getIg_exchange_count()
								+ count);
						this.integralGoodsService.updateById(obj);
					}
				}
				PredepositLog log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_user(user);
				log.setPd_log_amount(order.getIgo_trans_fee());
				log.setPd_op_type("消费");
				log.setPd_type("可用预存款");
				log.setPd_log_info("订单" + order.getIgo_order_sn()
						+ "兑换礼品减少可用预存款");
				this.predepositLogService.saveEntity(log);
				mv.addObject("op_title", "预付款支付成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/integral_order_list");
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "可用余额不足，支付失败！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/integral_order_list");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list");
		}
		return mv;
	}
	
	/**
	 * 积分订单支付结果
	 * @param request
	 * @param response
	 * @param order_id
	 * @return
	 */
	@SecurityMapping(title = "积分订单支付结果", value = "/integral/order_finish*", rtype = "buyer", rname = "积分兑换", rcode = "integral_exchange", rgroup = "积分兑换")
	@RequestMapping({ "/integral/order_finish" })
	public ModelAndView integral_order_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"integral/integral_order_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(order_id));
		mv.addObject("obj", obj);
		return mv;
	}
	
	/**
	 * 订单支付详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "订单支付详情", value = "/integral/order_pay_view*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/integral/order_pay_view" })
	public ModelAndView integral_order_pay_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("order_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		IntegralGoodsOrder obj = this.integralGoodsOrderService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getIgo_status() == 0) {
			mv.addObject("obj", obj);
			mv.addObject("paymentTools", this.paymentTools);
			mv.addObject("url", CommUtil.getURL(request));
			mv.addObject("from", "integralGoods");
		} else if (obj.getIgo_status() < 0) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已经取消！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list");
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已经付款！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/integral_order_list");
		}
		return mv;
	}
}
