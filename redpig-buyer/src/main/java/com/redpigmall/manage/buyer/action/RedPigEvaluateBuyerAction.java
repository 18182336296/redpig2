package com.redpigmall.manage.buyer.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigEvaluateBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户评价管理
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
 * @date 2015-3-3
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigEvaluateBuyerAction extends BaseAction{
	
	
	/**
	 * 买家评价列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家评价列表", value = "/buyer/evaluate_list*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/evaluate_list" })
	public ModelAndView evaluate_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/evaluate_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		maps.put("evaluate_user_id", SecurityUserHolder.getCurrentUser().getId());
		
		IPageList pList = this.evaluateService.list(maps);

		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("evaluateTools", this.evaluateTools);
		mv.addObject("imageTools", this.imageTools);
		return mv;
	}
	
	/**
	 * 买家评价修改
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "买家评价修改", value = "/buyer/evaluate_edit*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/evaluate_edit" })
	public ModelAndView evaluate_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/evaluate_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate evaluate = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		OrderForm obj = evaluate.getOf();//判断是否是当前登陆用户
		if ( evaluate != null 
				&& obj != null 
				&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				
				mv.addObject("evaluate", evaluate);
				mv.addObject("id", id);
				mv.addObject("imageTools", this.imageTools);
				List<Map> list = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
				for (Map map : list) {
					if (map.get("goods_id")
							.toString()
							.equals(evaluate.getEvaluate_goods().getId()
									.toString())) {
						mv.addObject("obj", map);
					}
				}
				mv.addObject("orderFormTools", this.orderFormTools);
				String evaluate_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("evaluate_session",
						evaluate_session);
				mv.addObject("evaluate_session", evaluate_session);
				if (obj.getOrder_status() < 50) {
					mv = new RedPigJModelAndView("success.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单已关闭评价");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/evaluate_list");
				}
		}else{
			mv = new RedPigJModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "订单信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/evaluate_list");
		}
		if (this.orderFormTools.evaluate_able(obj.getFinishTime()) == 0) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "已超出评价期限");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list");
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("jsessionid", request.getSession().getId());

		return mv;
	}
	
	/**
	 * 买家评价保存
	 * @param request
	 * @param response
	 * @param id
	 * @param evaluate_session
	 * @return
	 */
	@SecurityMapping(title = "买家评价保存", value = "/buyer/evaluate_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/evaluate_save" })
	public ModelAndView evaluate_saveEntity(HttpServletRequest request, HttpServletResponse response, String id,
			String evaluate_session) {
		
		RedPigJModelAndView mv = null;
		Evaluate eva = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		String goods_id = eva.getEvaluate_goods().getId().toString();
		
		int description = eva_rate(request.getParameter("description_evaluate" + goods_id));
		
		int service = eva_rate(request.getParameter("service_evaluate" + goods_id));
		int ship = eva_rate(request.getParameter("ship_evaluate" + goods_id));
		
		if ((description == 0) || (service == 0) || (ship == 0)) {
			mv = new RedPigJModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "参数错误，禁止评价");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/evaluate_list");
			return mv;
		}
		
		OrderForm obj = eva.getOf();
		String evaluate_session1 = (String) request.getSession(false).getAttribute("evaluate_session");
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		
		if (this.orderFormTools.evaluate_able(eva.getAddTime()) == 0) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "已超出评价期限");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list");
			return mv;
		}
		//保存评价
		if (evaluate_session1 != null
				&& evaluate_session1.equals(evaluate_session)
				&& obj != null 
				&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				
				request.getSession(false).removeAttribute("evaluate_session");
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("修改评价订单");
				ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
				ofl.setLog_user_name(SecurityUserHolder.getCurrentUser().getUserName());
				ofl.setOf(obj);
				this.orderFormLogService.saveEntity(ofl);
				
				List<Accessory> img_list = this.imageTools.queryImgs(eva.getEvaluate_photos());
				
				eva.setEvaluate_info(request.getParameter("evaluate_info_" + goods_id));
				eva.setEvaluate_photos(request.getParameter("evaluate_photos_" + goods_id));
				eva.setEvaluate_buyer_val(1);
				eva.setDescription_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request.getParameter("description_evaluate" + goods_id)))));
				
				eva.setService_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request.getParameter("service_evaluate" + goods_id)))));
				
				eva.setShip_evaluate(BigDecimal.valueOf(CommUtil.null2Double(eva_rate(request.getParameter("ship_evaluate" + goods_id)))));
				//修改评价
				this.evaluateService.updateById(eva);
				
				String im_str = request.getParameter("evaluate_photos_"	+ goods_id);
				
				if ((im_str != null) && (!im_str.equals(""))
						&& (im_str.length() > 0)) {
					for (String str : im_str.split(",")) {
						if ((str != null) && (!str.equals(""))) {
							Accessory image = this.accessoryService
									.selectByPrimaryKey(CommUtil.null2Long(str));
							if (image.getInfo().equals("eva_temp")) {
								image.setInfo("eva_img");
								this.accessoryService.saveEntity(image);
							}
							img_list.remove(image);
						}
					}
				}
				//注:如果是图片分离的形式,需要通过远程删除方式
				for (Accessory acc : img_list) {
					RedPigCommonUtil.del_acc(request, acc);
				}
				
				Map<String, Object> params = Maps.newHashMap();
				List<StorePoint> sps;
				//如果是第三方卖家商品
				if (goods.getGoods_type() == 1) {
					Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(goods.getGoods_store().getId()));
					params.put("store_id", store.getId().toString());
					params.put("evaluate_status_less_than", 2);
					
					List<Evaluate> evas = this.evaluateService.queryPageList(params);
					
					double store_evaluate = 0.0D;
					double description_evaluate = 0.0D;
					double description_evaluate_total = 0.0D;
					double service_evaluate = 0.0D;
					double service_evaluate_total = 0.0D;
					double ship_evaluate = 0.0D;
					double ship_evaluate_total = 0.0D;
					int store_credit = 0;
					DecimalFormat df = new DecimalFormat("0.0");
					
					for (Evaluate eva1 : evas) {
						description_evaluate_total = description_evaluate_total + CommUtil.null2Double(eva1.getDescription_evaluate());
						
						service_evaluate_total = service_evaluate_total + CommUtil.null2Double(eva1.getService_evaluate());
						
						ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
						store_credit += eva1.getEvaluate_buyer_val();
					}
					
					description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3.0D;
					store.setStore_credit(store_credit);
					
					this.storeService.updateById(store);
					params.clear();
					params.put("store_id", store.getId());
					
					sps = this.storePointService.queryPageList(params);
					
					StorePoint point = null;
					if (sps.size() > 0) {
						point = sps.get(0);
					} else {
						point = new StorePoint();
					}
					
					point.setAddTime(new Date());
					point.setStore(store);
					point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate > 5.0D ? 5.0D : description_evaluate));
					point.setService_evaluate(BigDecimal.valueOf(service_evaluate > 5.0D ? 5.0D : service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate > 5.0D ? 5.0D : ship_evaluate));
					point.setStore_evaluate(BigDecimal.valueOf(store_evaluate > 5.0D ? 5.0D : store_evaluate));
					
					if (sps.size() > 0) {
						this.storePointService.updateById(point);
					} else {
						this.storePointService.saveEntity(point);
					}
					
				} else {//如果是自营商品
					User sp_user = this.userService.selectByPrimaryKey(obj.getEva_user_id());
					params.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
					
					List<Evaluate> evas = this.evaluateService.queryPageList(params);
					
					double store_evaluate = 0.0D;
					double description_evaluate = 0.0D;
					double description_evaluate_total = 0.0D;
					double service_evaluate = 0.0D;
					double service_evaluate_total = 0.0D;
					double ship_evaluate = 0.0D;
					double ship_evaluate_total = 0.0D;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						description_evaluate_total = description_evaluate_total + CommUtil.null2Double(eva1.getDescription_evaluate());
						
						service_evaluate_total = service_evaluate_total + CommUtil.null2Double(eva1.getService_evaluate());
						
						ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
					}
					
					description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3.0D;
					
					params.clear();
					params.put("user_id", obj.getEva_user_id());
					
					List<StorePoint> sps1 = this.storePointService.queryPageList(params);
					
					StorePoint point = null;
					if (sps1.size() > 0) {
						point = sps1.get(0);
					} else {
						point = new StorePoint();
					}
					
					point.setAddTime(new Date());
					point.setUser(sp_user);
					point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate));
					point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
					point.setStore_evaluate(BigDecimal.valueOf(store_evaluate));
					
					if (sps1.size() > 0) {
						this.storePointService.updateById(point);
					} else {
						this.storePointService.saveEntity(point);
					}
				}
				this.goodsService.updateById(goods);
				
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				
				mv.addObject("op_title", "评价修改成功");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/evaluate_list");
		}else{
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "禁止重复评价");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/evaluate_list");
		}
		
		return mv;
	}
	
	/**
	 * 买家评价删除
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家评价删除", value = "/buyer/evaluate_del*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/evaluate_del" })
	public String evaluate_del(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		Evaluate evaluate = this.evaluateService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		OrderForm obj = evaluate.getOf();
		if (evaluate != null 
				&& obj != null 
				&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				
				evaluate.setEvaluate_status(2);
				this.evaluateService.updateById(evaluate);
				
				Goods goods = evaluate.getEvaluate_goods();
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("删除评价");
				ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
				ofl.setLog_user_name(SecurityUserHolder.getCurrentUser().getUserName());
				ofl.setOf(obj);
				this.orderFormLogService.saveEntity(ofl);
				
				Map<String, Object> params = Maps.newHashMap();
				List<StorePoint> sps;
				if (goods.getGoods_type() == 1) {
					Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(goods.getGoods_store().getId()));
					params.put("store_id", store.getId().toString());
					params.put("evaluate_status_less_than", 2);
					
					List<Evaluate> evas = this.evaluateService.queryPageList(params);
					
					double store_evaluate = 0.0D;
					double description_evaluate = 0.0D;
					double description_evaluate_total = 0.0D;
					double service_evaluate = 0.0D;
					double service_evaluate_total = 0.0D;
					double ship_evaluate = 0.0D;
					double ship_evaluate_total = 0.0D;
					int store_credit = 0;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						description_evaluate_total = description_evaluate_total + CommUtil.null2Double(eva1.getDescription_evaluate());
						
						service_evaluate_total = service_evaluate_total + CommUtil.null2Double(eva1.getService_evaluate());
						
						ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
						store_credit += eva1.getEvaluate_buyer_val();
					}
					
					description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3.0D;
					store.setStore_credit(store_credit);
					this.storeService.updateById(store);
					
					params.clear();
					params.put("store_id", store.getId());
					
					sps = this.storePointService.queryPageList(params);
					
					StorePoint point = null;
					if (sps.size() > 0) {
						point = sps.get(0);
					} else {
						point = new StorePoint();
					}
					
					point.setAddTime(new Date());
					point.setStore(store);
					point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate));
					point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
					point.setStore_evaluate(BigDecimal.valueOf(store_evaluate));
					
					if (sps.size() > 0) {
						this.storePointService.updateById(point);
					} else {
						this.storePointService.saveEntity(point);
					}
				} else {
					User sp_user = this.userService.selectByPrimaryKey(obj.getEva_user_id());
					params.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
					
					List<Evaluate> evas = this.evaluateService.queryPageList(params);
					
					double store_evaluate = 0.0D;
					double description_evaluate = 0.0D;
					double description_evaluate_total = 0.0D;
					double service_evaluate = 0.0D;
					double service_evaluate_total = 0.0D;
					double ship_evaluate = 0.0D;
					double ship_evaluate_total = 0.0D;
					DecimalFormat df = new DecimalFormat("0.0");
					for (Evaluate eva1 : evas) {
						description_evaluate_total = description_evaluate_total + CommUtil.null2Double(eva1.getDescription_evaluate());
						
						service_evaluate_total = service_evaluate_total + CommUtil.null2Double(eva1.getService_evaluate());
						
						ship_evaluate_total = ship_evaluate_total + CommUtil.null2Double(eva1.getShip_evaluate());
					}
					description_evaluate = CommUtil.null2Double(df.format(description_evaluate_total / evas.size()));
					service_evaluate = CommUtil.null2Double(df.format(service_evaluate_total / evas.size()));
					ship_evaluate = CommUtil.null2Double(df.format(ship_evaluate_total / evas.size()));
					store_evaluate = (description_evaluate + service_evaluate + ship_evaluate) / 3.0D;
					params.clear();
					params.put("user_id", obj.getEva_user_id());
					
					List<StorePoint> sps1 = this.storePointService.queryPageList(params);
					
					StorePoint point = null;
					if (sps1.size() > 0) {
						point = sps1.get(0);
					} else {
						point = new StorePoint();
					}
					
					point.setAddTime(new Date());
					point.setUser(sp_user);
					point.setDescription_evaluate(BigDecimal.valueOf(description_evaluate));
					point.setService_evaluate(BigDecimal.valueOf(service_evaluate));
					point.setShip_evaluate(BigDecimal.valueOf(ship_evaluate));
					point.setStore_evaluate(BigDecimal.valueOf(store_evaluate));
					if (sps1.size() > 0) {
						this.storePointService.updateById(point);
					} else {
						this.storePointService.saveEntity(point);
					}
				}
				goods.setEvaluate_count(goods.getEvaluate_count() - 1);
				this.goodsService.updateById(goods);
			
		}
		return "redirect:/buyer/evaluate_list?currentPage=" + currentPage;
	}
	
	/**
	 * 买家追加评价
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "买家追加评价", value = "/buyer/order_evaluate_add*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/evaluate_add" })
	public ModelAndView order_evaluate_add(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/evaluate_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate evaluate = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		OrderForm obj = evaluate.getOf();
		Goods goods = evaluate.getEvaluate_goods();
		
		if (this.orderFormTools.evaluate_add_able(evaluate.getAddTime()) == 0) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "已超出评价追加期限");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/evaluate_list");
		} else {
			if (evaluate != null 
					&& evaluate.getAddeva_status() == 0 
					&& obj != null 
					&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
					mv.addObject("evaluate", evaluate);
					mv.addObject("imageTools", this.imageTools);
					
					List<Map> list = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
					for (Map map : list) {
						if (map.get("goods_id").toString()
								.equals(goods.getId().toString())) {
							mv.addObject("obj", map);
						}
					}
					
					mv.addObject("id", id);
					mv.addObject("orderFormTools", this.orderFormTools);
					String evaluate_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("evaluate_session",evaluate_session);
					mv.addObject("evaluate_session", evaluate_session);
					if (obj.getOrder_status() < 50) {
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1, request,
								response);
						mv.addObject("op_title", "订单已关闭评价");
						mv.addObject("url", CommUtil.getURL(request) + "/buyer/evaluate_list");
					}
			}else{
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,response);
				mv.addObject("op_title", "评价信息错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/evaluate_list");
			}
		}
		
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("jsessionid", request.getSession().getId());
		
		return mv;
	}
	
	/**
	 * 买家追加评价保存
	 * @param request
	 * @param response
	 * @param id
	 * @param evaluate_session
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "买家追加评价保存", value = "/buyer/evaluate_add_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/evaluate_add_save" })
	public ModelAndView evaluate_add_save(HttpServletRequest request,
			HttpServletResponse response, String id, String evaluate_session)
			throws Exception {
		Evaluate eva = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		String goods_id = eva.getEvaluate_goods().getId().toString();
		OrderForm obj = eva.getOf();
		String evaluate_session1 = (String) request.getSession(false)
				.getAttribute("evaluate_session");
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		RedPigJModelAndView mv = null;
		if (this.orderFormTools.evaluate_able(obj.getFinishTime()) == 0) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "已超出评价追加期限");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list");
		} else {
			if ((evaluate_session1 != null) && (evaluate_session1.equals(evaluate_session))) {
				
				request.getSession(false).removeAttribute("evaluate_session");
				
				if (obj != null 
						&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
						if (obj.getOrder_status() == 50) {
							OrderFormLog ofl = new OrderFormLog();
							ofl.setAddTime(new Date());
							ofl.setLog_info("追加评价订单");
							ofl.setLog_user_id(SecurityUserHolder
									.getCurrentUser().getId());
							ofl.setLog_user_name(SecurityUserHolder
									.getCurrentUser().getUserName());
							ofl.setOf(obj);
							this.orderFormLogService.saveEntity(ofl);
							if (eva.getAddeva_status() == 0) {
								eva.setAddeva_status(1);
								eva.setAddeva_info(request
										.getParameter("evaluate_info_"
												+ goods.getId()));
								eva.setAddeva_photos(request
										.getParameter("evaluate_photos_"
												+ goods.getId()));
								eva.setAddeva_time(new Date());
								this.evaluateService.updateById(eva);
								String im_str = request
										.getParameter("evaluate_photos_"
												+ goods.getId());
								if ((im_str != null) && (!im_str.equals(""))
										&& (im_str.length() > 0)) {
									for (String str : im_str.split(",")) {
										if ((str != null) && (!str.equals(""))) {
											Accessory image = this.accessoryService
													.selectByPrimaryKey(CommUtil
															.null2Long(str));
											if (image.getInfo().equals(
													"eva_temp")) {
												image.setInfo("eva_img");
												this.accessoryService
														.saveEntity(image);
											}
										}
									}
								}
							}
						}
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "订单追加评价成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/evaluate_list");
						return mv;
				}
				
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单信息错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/evaluate_list");
				return mv;
			}
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "禁止重复评价");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/evaluate_list");
			return mv;
		}
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
}
