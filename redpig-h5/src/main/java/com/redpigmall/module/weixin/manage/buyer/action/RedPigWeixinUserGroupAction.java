package com.redpigmall.module.weixin.manage.buyer.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.redpigmall.logic.service.RedPigHandleOrderFormService;
import com.redpigmall.lucene.tools.RedPigLuceneVoTools;
import com.redpigmall.manage.admin.tools.RedPigOrderFormTools;
import com.redpigmall.manage.admin.tools.RedPigPaymentTools;
import com.redpigmall.manage.admin.tools.RedPigShipTools;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import com.redpigmall.msg.RedPigMsgTools;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.RedPigExpressCompanyService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigGroupGoodsService;
import com.redpigmall.service.RedPigGroupInfoService;
import com.redpigmall.service.RedPigGroupLifeGoodsService;
import com.redpigmall.service.RedPigOrderFormLogService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigPayoffLogService;
import com.redpigmall.service.RedPigReturnGoodsLogService;
import com.redpigmall.service.RedPigStorePointService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigUserConfigService;
import com.redpigmall.service.RedPigUserService;


/**
 * <p>
 * Title: RedPigWeixinUserGroupAction.java
 * </p>
 * 
 * <p>
 * Description: 团购订单
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
 * @date 2015年1月6日
 * 
 * @version b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
@Controller
public class RedPigWeixinUserGroupAction extends BaseAction{
	
	
	/**
	 * 团购订单列表
	 * @param request
	 * @param response
	 * @param type
	 * @param order_status
	 * @return
	 */
	@SecurityMapping(title = "团购订单列表", value = "/buyer/group_list*", rtype = "buyer", rname = "移动端用户团购订单列表", rcode = "wap_order_list", rgroup = "移动端用户团购订单")
	@RequestMapping({ "/buyer/group_list" })
	public ModelAndView group_list(HttpServletRequest request,
			HttpServletResponse response, String type, String order_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/group_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<OrderForm> orders = null;
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
		map.put("order_main", Integer.valueOf(0));
		map.put("order_cat", 2);
		
		if (CommUtil.null2String(type).equals("order_nopay")) {
			map.put("status", Integer.valueOf(10));
		}
		if (CommUtil.null2String(type).equals("order_noship")) {
			map.put("status", Integer.valueOf(20));
		}
		if (CommUtil.null2String(type).equals("order_notake")) {
			map.put("order_status1", Integer.valueOf(30));
			map.put("order_status2", Integer.valueOf(35));
		}
		if (CommUtil.null2String(type).equals("order_over")) {
			map.put("order_status_more_than", Integer.valueOf(40));
		}
		orders = this.orderFormService.queryPageList(map, 0, 12);
		mv.addObject("orders", orders);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 团购订单列表
	 * @param request
	 * @param response
	 * @param type
	 * @param begin_count
	 * @param order_status
	 * @return
	 */
	@SecurityMapping(title = "团购订单列表", value = "/buyer/group_data*", rtype = "buyer", rname = "移动端用户团购订单列表", rcode = "wap_order_list", rgroup = "移动端用户团购订单")
	@RequestMapping({ "/buyer/group_data" })
	public ModelAndView group_data(HttpServletRequest request,
			HttpServletResponse response, String type, String begin_count,
			String order_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/group_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<OrderForm> orders = null;
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
		map.put("order_main", Integer.valueOf(0));
		map.put("order_cat", 2);
		
		
		if (CommUtil.null2String(type).equals("order_nopay")) {
			map.put("status", Integer.valueOf(10));
		}
		if (CommUtil.null2String(type).equals("order_noship")) {
			map.put("status", Integer.valueOf(20));
		}
		if (CommUtil.null2String(type).equals("order_notake")) {
			map.put("order_status1", Integer.valueOf(30));
			map.put("order_status2", Integer.valueOf(35));
		}
		if (CommUtil.null2String(type).equals("order_over")) {
			map.put("status_more_than", Integer.valueOf(40));
		}
		orders = this.orderFormService.queryPageList(map,CommUtil.null2Int(begin_count), 12);
		
		mv.addObject("orders", orders);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 团购订单详情
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购订单详情", value = "/buyer/group_view*", rtype = "buyer", rname = "移动端用户团购订单详情", rcode = "wap_order_detail", rgroup = "移动端用户团购订单")
	@RequestMapping({ "/buyer/group_view" })
	public ModelAndView group_view(HttpServletRequest request,
			HttpServletResponse response, String id, String type,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/group_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((obj != null) && (obj.getUser_id().equals(user.getId().toString()))) {
			Map json = this.orderFormTools.queryGroupInfo(obj.getGroup_info());
			String url = this.configService.getSysConfig().getAddress();
			if ((url == null) || (url.equals(""))) {
				url = CommUtil.getURL(request);
			}
			String params = "";
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20,"", "");
			
	        maps.put("user_id",user.getId());
	        maps.put("order_id",obj.getId());
	        maps.put("lifeGoods_id",CommUtil.null2Long(json.get("goods_id").toString()));
	        
			IPageList pList = this.groupinfoService.list(maps);
			CommUtil.saveIPageList2ModelAndView(url
					+ "/buyer/groupinfo_list.html", "", params, pList, mv);
			GroupLifeGoods goods = this.groupLifeGoodsService
					.selectByPrimaryKey(CommUtil.null2Long(json.get("goods_id")));
			mv.addObject("infos", pList.getResult());
			mv.addObject("obj", obj);
			mv.addObject("goods", goods);
			mv.addObject("orderFormTools", this.orderFormTools);
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/center");
		}
		if ((!CommUtil.null2String(type).equals("")) && (!type.equals("0"))) {
			mv.addObject("type", type);
		}
		return mv;
	}
	
	/**
	 * 团购订单详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购订单详情", value = "/buyer/group_view_data*", rtype = "buyer", rname = "移动端用户团购订单详情", rcode = "wap_order_detail", rgroup = "移动端用户团购订单")
	@RequestMapping({ "/buyer/group_view_data" })
	public ModelAndView group_view_data(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/group_detail_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((obj != null) && (obj.getUser_id().equals(user.getId().toString()))
				&& (obj.getOrder_status() == 20)) {
			Map json = this.orderFormTools.queryGroupInfo(obj.getGroup_info());
			String url = this.configService.getSysConfig().getAddress();
			if ((url == null) || (url.equals(""))) {
				url = CommUtil.getURL(request);
			}
			String params = "";
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20,"", "");
	        maps.put("user_id",user.getId());
	        maps.put("order_id",obj.getId());
	        maps.put("lifeGoods_id",CommUtil.null2Long(json.get("goods_id").toString()));
	        
			IPageList pList = this.groupinfoService.list(maps);
			mv.addObject("infos", pList.getResult());
			GroupLifeGoods goods = this.groupLifeGoodsService
					.selectByPrimaryKey(CommUtil.null2Long(json.get("goods_id")));
			mv.addObject("goods", goods);
		}
		return mv;
	}
	
	/**
	 * 团购订单取消
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "团购订单取消", value = "/buyer/group_cancel*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_order_cancel", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/group_cancel" })
	public String group_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj != null)
				&& (obj.getUser_id().compareTo(
						SecurityUserHolder.getCurrentUser().getId().toString()) == 0)) {
			if (obj.getOrder_status() == 10) {
				obj.setOrder_status(0);
				this.orderFormService.saveEntity(obj);
			}
		}
		return "redirect:group_view?id=" + id;
	}
	
	/**
	 * 确认收货
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "确认收货", value = "/buyer/group_cofirm*", rtype = "buyer", rname = "移动端用户团购订单确认收货", rcode = "wap_order_cofirm", rgroup = "移动端用户团购订单")
	@RequestMapping({ "/buyer/group_cofirm" })
	public ModelAndView group_cofirm(HttpServletRequest request,
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
					+ "/buyer/group_list");
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购订单编号错误");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/group_list");
		}
		return mv;
	}
	
	/**
	 * 团购订单评论
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "团购订单评论", value = "/buyer/group_discuss*", rtype = "buyer", rname = "移动端用户团购订单评论", rcode = "wap_order_cofirm", rgroup = "移动端用户团购订单")
	@RequestMapping({ "/buyer/group_discuss" })
	public ModelAndView group_discuss(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/group_discuss.html",
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
				mv.addObject("op_title", "团购订单已经评价！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/user/default/usercenter/weixin/group_list");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的团购订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/center");
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 买家评价保存
	 * @param request
	 * @param response
	 * @param evaluate_session
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "买家评价保存", value = "/buyer/group_discuss_save*", rtype = "buyer", rname = "移动端用户团购订单评论", rcode = "user_center", rgroup = "移动端用户团购订单")
	@RequestMapping({ "/buyer/group_discuss_save" })
	public ModelAndView group_discuss_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String evaluate_session)
			throws Exception {
		String[] ids = request.getParameterValues("id");
		String evaluate_session1 = (String) request.getSession(false)
				.getAttribute("evaluate_session");
		if (CommUtil.null2String(evaluate_session1).equals(evaluate_session)) {
			for (String id : ids) {
				OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				if ((obj != null)
						&& (obj.getUser_id()
								.toString()
								.compareTo(
										SecurityUserHolder.getCurrentUser()
												.getId().toString()) == 0)) {
					if (obj.getOrder_status() == 40) {
						obj.setOrder_status(50);
						this.orderFormService.updateById(obj);
						OrderFormLog ofl = new OrderFormLog();
						ofl.setAddTime(new Date());
						ofl.setLog_info("评价团购订单");
						User cuser = SecurityUserHolder.getCurrentUser();
						ofl.setLog_user_id(cuser.getId());
						ofl.setLog_user_name(cuser.getUserName());
						ofl.setOf(obj);
						this.orderFormLogService.saveEntity(ofl);
						List<Map> json = this.orderFormTools.queryGoodsInfo(obj
								.getGoods_info());
						for (Map map : json) {
							map.put("orderForm", obj.getId());
						}
						for (Map map : json) {
							Evaluate eva = new Evaluate();
							Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
									.null2Long(map.get("goods_id")));
							eva.setAddTime(new Date());
							eva.setEvaluate_goods(goods);
							eva.setGoods_num(CommUtil.null2Int(map
									.get("goods_count")));
							eva.setGoods_price(map.get("goods_price")
									.toString());
							eva.setGoods_spec(map.get("goods_gsp_val")
									.toString());
							eva.setEvaluate_info(request
									.getParameter("evaluate_info_"
											+ goods.getId()));
							eva.setEvaluate_buyer_val(CommUtil.null2Int(request
									.getParameter("evaluate_buyer_val"
											+ goods.getId())));
							eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil.null2Double(request
									.getParameter("description_evaluate"
											+ goods.getId()))));
							eva.setService_evaluate(BigDecimal.valueOf(CommUtil
									.null2Double(request
											.getParameter("service_evaluate"
													+ goods.getId()))));
							eva.setShip_evaluate(BigDecimal.valueOf(CommUtil
									.null2Double(request
											.getParameter("ship_evaluate"
													+ goods.getId()))));
							eva.setEvaluate_type("goods");
							eva.setEvaluate_user(SecurityUserHolder
									.getCurrentUser());
							eva.setOf(this.orderFormService.selectByPrimaryKey(CommUtil
									.null2Long(map.get("orderForm"))));
							this.evaluateService.saveEntity(eva);
							Map<String, Object> params = Maps.newHashMap();
							User sp_user = this.userService.selectByPrimaryKey(obj
									.getEva_user_id());
							params.put("user_id", SecurityUserHolder
									.getCurrentUser().getId().toString());
							
							List<Evaluate> evas = this.evaluateService.queryPageList(params);
							
							double user_evaluate1 = 0.0D;
							double user_evaluate1_total = 0.0D;
							double description_evaluate = 0.0D;
							double description_evaluate_total = 0.0D;
							double service_evaluate = 0.0D;
							double service_evaluate_total = 0.0D;
							double ship_evaluate = 0.0D;
							double ship_evaluate_total = 0.0D;
							DecimalFormat df = new DecimalFormat("0.0");
							for (Evaluate eva1 : evas) {
								user_evaluate1_total = user_evaluate1_total
										+ eva1.getEvaluate_buyer_val();

								description_evaluate_total = description_evaluate_total
										+ CommUtil.null2Double(eva1
												.getDescription_evaluate());

								service_evaluate_total = service_evaluate_total
										+ CommUtil.null2Double(eva1
												.getService_evaluate());

								ship_evaluate_total = ship_evaluate_total
										+ CommUtil.null2Double(eva1
												.getShip_evaluate());
							}
							user_evaluate1 = CommUtil
									.null2Double(df.format(user_evaluate1_total
											/ evas.size()));
							description_evaluate = CommUtil.null2Double(df
									.format(description_evaluate_total
											/ evas.size()));
							service_evaluate = CommUtil.null2Double(df
									.format(service_evaluate_total
											/ evas.size()));
							ship_evaluate = CommUtil.null2Double(df
									.format(ship_evaluate_total / evas.size()));
							params.clear();
							params.put("user_id", obj.getEva_user_id());
							
							List<StorePoint> sps = this.storePointService.queryPageList(params);
							
							StorePoint point = null;
							if (sps.size() > 0) {
								point = (StorePoint) sps.get(0);
							} else {
								point = new StorePoint();
							}
							point.setAddTime(new Date());
							point.setUser(sp_user);
							point.setDescription_evaluate(BigDecimal
									.valueOf(description_evaluate > 5.0D ? 5.0D
											: description_evaluate));
							point.setService_evaluate(BigDecimal
									.valueOf(service_evaluate > 5.0D ? 5.0D
											: service_evaluate));
							point.setShip_evaluate(BigDecimal
									.valueOf(ship_evaluate > 5.0D ? 5.0D
											: ship_evaluate));
							if (sps.size() > 0) {
								this.storePointService.updateById(point);
							} else {
								this.storePointService.saveEntity(point);
							}
							User user = this.userService.selectByPrimaryKey(CommUtil
									.null2Long(obj.getUser_id()));
							user.setIntegral(user.getIntegral()
									+ this.configService.getSysConfig()
											.getIndentComment());
							user.setUser_goods_fee(BigDecimal.valueOf(CommUtil
									.add(user.getUser_goods_fee(),
											obj.getTotalPrice())));
							this.userService.updateById(user);
						}
					}
					if (obj.getOrder_form() == 0) {
						Store store = this.storeService.selectByPrimaryKey(CommUtil
								.null2Long(obj.getStore_id()));
						Map<String, Object> map = Maps.newHashMap();
						map.put("seller_id", store.getUser().getId().toString());
						map.put("order_id", obj.getId().toString());
						String json = JSON.toJSONString(map);
						this.msgTools.sendEmailCharge(CommUtil.getURL(request),
								"email_toseller_evaluate_ok_notify", store
										.getUser().getEmail(), json, null, obj
										.getStore_id());
					}
				}
			}
			request.getSession(false).removeAttribute("evaluate_session");
			ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "团购订单评价成功！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/user/buyer/group_list");
			return mv;
		}
		ModelAndView mv = new RedPigJModelAndView("weixin/error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "禁止重复评价!");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/user/buyer/group_list");
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
	@SecurityMapping(title = "订单取消", value = "/buyer/lifeorder_cancel*", rtype = "buyer", rname = "移动端用户团购订单取消", rcode = "wap_order_cancel", rgroup = "移动端用户团购订单")
	@RequestMapping({ "/buyer/lifeorder_cancel" })
	public String lifeorder_cancel(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (of.getUser_id().equals(
				SecurityUserHolder.getCurrentUser().getId().toString())) {
			of.setOrder_status(0);
			this.orderFormService.updateById(of);
		}
		return "redirect:/buyer/group_list";
	}
}
