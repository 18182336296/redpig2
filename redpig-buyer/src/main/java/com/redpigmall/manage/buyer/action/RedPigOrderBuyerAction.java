package com.redpigmall.manage.buyer.action;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.domain.ExpressInfo;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupInfo;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.domain.Snapshot;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.domain.virtual.TransInfo;
import com.redpigmall.manage.buyer.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigOrderBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家订单控制类，用来操作取消订单、查看订单、订单付款、物流查询、订单评价等操作；
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
 * @date 2014-5-19
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigOrderBuyerAction extends BaseAction{
	
	/**
	 * 买家订单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @param order_status
	 * @return
	 */
	@SecurityMapping(title = "买家订单列表", value = "/buyer/order*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order" })
	public ModelAndView order(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String beginTime, String endTime, String order_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_order.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
        maps.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
        
		maps.put("order_main", 1);// 只显示主订单,通过主订单完成子订单的加载
		
		maps.put("order_cat_no", 2);
		
		maps.put("deleteStatus_no", -1);
		
		if (!CommUtil.null2String(order_id).equals("")) {
			
			maps.put("order_id", order_id);
			mv.addObject("order_id", order_id);
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(ends,"yyyy-MM-dd hh:mm:ss"));
			mv.addObject("endTime", endTime);
		}
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {// 已经提交
				
				maps.put("order_status", 10);
			}
			if (order_status.equals("order_pay")) {// 已经付款
				
				Set<Integer> ids = Sets.newTreeSet();
				ids.add(Integer.valueOf(16));
				ids.add(Integer.valueOf(20));
				maps.put("order_statuss", ids);
			}
			if (order_status.equals("order_shipping")) {// 已经发货
				maps.put("order_status", 30);
			}
			if (order_status.equals("order_receive")) {// 已经收货
				
				maps.put("order_status", 40);
			}
			if (order_status.equals("order_finish")) {// 已经完成
				maps.put("order_status", 50);
			}
			if (order_status.equals("order_cancel")) {// 已经取消
				maps.put("order_status", 0);
			}
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("order_status", order_status);
		maps.put("deleteStatus", 0);
		
		IPageList pList = this.orderFormService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		// 查询订单信息
		int[] status = { 10, 30, 50 };// 已提交 已发货 已完成
		String[] string_status = { "order_submit", "order_shipping","order_finish" };
		Map<String,Object> orders_status = Maps.newHashMap();
		
		for (int i = 0; i < status.length; i++) {
			Map<String,Object> params = Maps.newHashMap();
			params.put("order_main", 1);
			params.put("user_id", user.getId().toString());
			params.put("order_status", status[i]);
			
			int size = this.orderFormService.selectCount(params);
			
			mv.addObject("order_size_" + status[i], Integer.valueOf(size));
			orders_status.put(string_status[i], Integer.valueOf(size));
		}
		mv.addObject("orders_status", orders_status);
		mv.addObject("orderFormTools", this.orderFormTools);
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
	@SecurityMapping(title = "订单取消", value = "/buyer/order_cancel*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_cancel" })
	public ModelAndView order_cancel(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/order_cancel.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null 
				&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				mv.addObject("obj", obj);
				mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}
		return mv;
	}
	
	/**
	 * 订单取消确定
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param state_info
	 * @param other_state_info
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "订单取消确定", value = "/buyer/order_cancel_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_cancel_save" })
	public String order_cancel_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String state_info, String other_state_info) throws Exception {
		List<OrderForm> objs = Lists.newArrayList();
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		objs.add(obj);
		boolean all_verify = true;
		Date nowDate = new Date();
		
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				if ((obj.getOrder_main() == 1)
						&& (obj.getChild_order_detail() != null)) {
					List<Map> maps = JSON.parseArray(
							CommUtil.null2String(obj.getChild_order_detail()),
							Map.class);
					if (maps != null) {
						for (Map map : maps) {
							OrderForm child_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(map.get("order_id")));
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
		
		if ((all_verify) && (obj != null)) {
			if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				
				if ((obj.getOrder_main() == 1) && (obj.getChild_order_detail() != null)) {
					List<Map> maps = JSON.parseArray(CommUtil.null2String(obj.getChild_order_detail()),Map.class);
					if (maps != null) {
						for (Map map : maps) {
							OrderForm child_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(map.get("order_id")));
							child_order.setOrder_status(0);
							if (child_order.getCoupon_info() != null) {
								if (!"".equals(child_order.getCoupon_info())) {
									Map m = JSON.parseObject(child_order
											.getCoupon_info());
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
							}
							this.orderFormService.updateById(child_order);
						}
					}
				}
				obj.setOrder_status(0);
				if ((obj.getCoupon_info() != null)
						&& (!"".equals(obj.getCoupon_info()))) {
					Map m = JSON.parseObject(obj.getCoupon_info());
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
				
				this.orderFormTools.sendMsgWhenHandleOrder(CommUtil.getURL(request), obj, null,"toseller_order_cancel_notify");
			}
		}
		return "redirect:order?currentPage=" + currentPage;
	}

	/**
	 * 买家打开收货确认对话框
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "收货确认", value = "/buyer/order_cofirm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_cofirm" })
	public ModelAndView order_cofirm(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/order_cofirm.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null 
				&& obj.getUser_id().equals(CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId()))) {
				mv.addObject("obj", obj);
				mv.addObject("child_order",
						Boolean.valueOf(!CommUtil.null2String(obj.getChild_order_detail()).equals("")));
				mv.addObject("currentPage", currentPage);
			
		} else {
			mv = new RedPigJModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}
		return mv;
	}

	/**
	 * 买家确认收货，确认收货后，订单状态值改变为40，如果是预存款支付，买家冻结预存款中同等订单账户金额自动转入商家预存款，如果开启预存款分润，
	 * 则按照分润比例，买家预存款分别进入商家及平台商的账户
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "收货确认保存", value = "/buyer/order_cofirm_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_cofirm_save" })
	public String order_cofirm_saveEntity(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) throws Exception {
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null 
				&& obj.getUser_id().equals(CommUtil.null2String(SecurityUserHolder.getCurrentUser().getId()))) {
				
				this.handelOrderFormService.confirmOrder(request, obj);
		}
		String url = "redirect:order?currentPage=" + currentPage;
		return url;
	}
	
	/**
	 * 买家评价
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "买家评价", value = "/buyer/order_evaluate*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_evaluate" })
	public ModelAndView order_evaluate(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/order_evaluate.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				mv.addObject("obj", obj);
				mv.addObject("orderFormTools", this.orderFormTools);
				String evaluate_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("evaluate_session",evaluate_session);
				mv.addObject("evaluate_session", evaluate_session);
				
				if (obj.getOrder_status() == 35 || obj.getOrder_status() == 40) {
					mv = new RedPigJModelAndView(
							"user/default/usercenter/order_evaluate.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("obj", obj);
					mv.addObject("orderFormTools", this.orderFormTools);
				} else {
					if (obj.getOrder_status() < 50) {
						mv = new RedPigJModelAndView("error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "您没有编号为" + id + "的订单！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/order");
						mv.addObject("orderFormTools", this.orderFormTools);
						mv.addObject("jsessionid", request.getSession().getId());
					} else {
						mv = new RedPigJModelAndView("success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "订单已经评价！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/order");
					}

				}
		}

		return mv;
	}
	
	/**
	 * 买家评价图片上传
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@SecurityMapping(title = "买家评价图片上传", value = "/buyer/upload_evaluate*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/upload_evaluate" })
	public void upload_evaluate(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Map<String,Object> json_map = Maps.newHashMap();
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		response.setContentType("text/html;charset=UTF-8");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		Map<String,Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("info", "eva_temp");
		List<Accessory> imglist = this.accessoryService.queryPageList(params);
		
		if (imglist.size() > 40) {
			json_map.put("ret", Boolean.valueOf(false));
			json_map.put("msg", "您最近上传过多图片，请稍后重试");
			response.setContentType("text/plain");
			response.getWriter().print(JSON.toJSONString(json_map));
		} else {
			json_map.put("ret", Boolean.valueOf(true));
			try {
				String goods_id = CommUtil.null2String(request
						.getParameter("goods_id"));
				String filePath = request.getSession().getServletContext()
						.getRealPath("/")
						+ uploadFilePath
						+ File.separator
						+ "evaluate"
						+ File.separator + "goods_" + goods_id;
				CommUtil.createFolder(filePath);
				path = uploadFilePath + "/evaluate/goods_" + goods_id;
				Map map = CommUtil.saveFileToServer(request,
						"evaluate_photos_a_" + goods_id, filePath, null, null);
				Accessory image = new Accessory();
				image.setUser(user);
				image.setAddTime(new Date());
				image.setExt((String) map.get("mime"));
				image.setPath(path);
				image.setWidth(CommUtil.null2Int(map.get("width")));
				image.setHeight(CommUtil.null2Int(map.get("height")));
				image.setName(CommUtil.null2String(map.get("fileName")));
				image.setInfo("eva_temp");
				this.accessoryService.saveEntity(image);
				json_map.put("url", CommUtil.getURL(request) + "/" + path + "/"
						+ image.getName());
				json_map.put("id", image.getId());
				json_map.put("goods_id", goods_id);
				response.setContentType("text/plain");
				response.getWriter().print(JSON.toJSONString(json_map));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 买家评价保存
	 * @param request
	 * @param response
	 * @param id
	 * @param evaluate_session
	 * @param checkState
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "买家评价保存", value = "/buyer/order_evaluate_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_evaluate_save" })
	public ModelAndView order_evaluate_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String evaluate_session,
			String checkState) throws Exception {
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		String evaluate_session1 = (String) request.getSession(false).getAttribute("evaluate_session");
		
		if ((evaluate_session1 != null) && (evaluate_session1.equals(evaluate_session))) {
			ModelAndView mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "禁止重复评价!");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
			return mv;
		}
		
		if ((obj == null) || (!obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString()))) {
			ModelAndView mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，禁止评价");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}
		
		List<Map> json = Lists.newArrayList();
		if ((obj.getOrder_status() != 40) && (obj.getOrder_status() != 25)) {
			ModelAndView mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，禁止评价");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
			return mv;
		}
		
		if (obj.getOrder_status() == 40) {
			json = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
			for (Map map : json) {
				String goods_gsp_ids = map.get("goods_gsp_ids").toString();
				goods_gsp_ids = goods_gsp_ids.replaceAll(",", "_");
				String goods_id = map.get("goods_id") + "_" + goods_gsp_ids;
				int description = eva_rate(request.getParameter("description_evaluate" + goods_id));
				int service = eva_rate(request.getParameter("service_evaluate" + goods_id));
				int ship = eva_rate(request.getParameter("ship_evaluate" + goods_id));
				int total = eva_total_rate(request.getParameter("evaluate_buyer_val" + goods_id));
				
				if ((description == 0) 
						|| (service == 0) 
						|| (ship == 0) 
						|| (total == -5)) {
					
					ModelAndView mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "参数错误，禁止评价");
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
					return mv;
				}
			}
		}
		
		this.handelOrderFormService.evaluateOrderForm(request, checkState, obj);
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "订单评价成功");
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		return mv;
	}
	
	/**
	 * 删除订单信息
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "删除订单信息", value = "/buyer/order_delete*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_delete" })
	public String order_delete(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage)
			throws Exception {
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null 
				&& (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) 
				&& (obj.getOrder_status() == 0)) {
				
				if ((obj.getOrder_main() == 1) 
						&& (obj.getOrder_cat() == 0)
						&& (obj.getChild_order_detail() != null)
						&& (!obj.getChild_order_detail().equals(""))) {
					
					List<Map> maps = JSON.parseArray(obj.getChild_order_detail(), Map.class);
					for (Map map : maps) {
						OrderForm child_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(map.get("order_id")));
						child_order.setOrder_status(0);
						child_order.setDeleteStatus(-1);
						this.orderFormService.updateById(child_order);
					}
				}
				obj.setDeleteStatus(-1);
				this.orderFormService.updateById(obj);
		}
		return "redirect:order?currentPage=" + currentPage;
	}
	
	/**
	 * 买家订单详情
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @return
	 */
	@SecurityMapping(title = "买家订单详情", value = "/buyer/order_view*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_view" })
	public ModelAndView order_view(HttpServletRequest request,
			HttpServletResponse response, String id, String mark) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/order_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null && obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				
				if (obj.getOrder_cat() == 1) {
					mv = new RedPigJModelAndView(
							"user/default/usercenter/recharge_order_view.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
				}
				
				boolean query_ship = false;
				if (!CommUtil.null2String(obj.getShipCode()).equals("")) {
					query_ship = true;
				}
				
				if (obj.getOrder_main() == 1) {
					if (!CommUtil.null2String(obj.getChild_order_detail()).equals("")) {
						List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(child_map.get("order_id")));
							if (!CommUtil.null2String(child_order.getShipCode()).equals("")) {
								query_ship = true;
							}
						}
					}
				}
				
				if (obj != null) {
					String temp = obj.getSpecial_invoice();
					if ((temp != null) && (!"".equals(temp))) {
						Map of_va = JSON.parseObject(temp);
						mv.addObject("of_va", of_va);
					}
				}
				mv.addObject("obj", obj);
				mv.addObject("shipTools", this.shipTools);
				mv.addObject("orderFormTools", this.orderFormTools);
				mv.addObject("query_ship", Boolean.valueOf(query_ship));
				mv.addObject("mark", mark);
				Map<String,Object> params = Maps.newHashMap();
				
				params.put("of_id", obj.getId());
				List<OrderFormLog> ofls = this.orderFormLogService.queryPageList(params);
				
				mv.addObject("ofls", ofls);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}

		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("express_company_name", this.orderFormTools.queryExInfo(
				obj.getExpress_info(), "express_company_name"));
		return mv;
	}
	
	/**
	 * 买家物流详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "买家物流详情", value = "/buyer/ship_view*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/ship_view" })
	public ModelAndView order_ship_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/order_ship_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (order != null && order.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				List<TransInfo> transInfo_list = Lists.newArrayList();
				TransInfo transInfo = this.shipTools.query_Ordership_getData(id);
				transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(order.getExpress_info(),"express_company_name"));
				transInfo.setExpress_ship_code(order.getShipCode());
				transInfo_list.add(transInfo);
				if (order.getOrder_main() == 1) {
					if (!CommUtil.null2String(order.getChild_order_detail()).equals("")) {
						List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getChild_order_detail());
						for (Map child_map : maps) {
							OrderForm child_order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(child_map.get("order_id")));
							if (child_order.getExpress_info() != null) {
								TransInfo transInfo1 = this.shipTools.query_Ordership_getData(CommUtil.null2String(child_order.getId()));
								transInfo1.setExpress_company_name(this.orderFormTools.queryExInfo(child_order.getExpress_info(),"express_company_name"));
								transInfo1.setExpress_ship_code(child_order.getShipCode());
								transInfo_list.add(transInfo1);
							}
						}
					}
				}
				mv.addObject("transInfo_list", transInfo_list);
				mv.addObject("obj", order);
				if (order != null) {
					String temp = order.getSpecial_invoice();
					if ((temp != null) && (!"".equals(temp))) {
						Map of_va = JSON.parseObject(temp);
						mv.addObject("of_va", of_va);
					}
				}
			
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您查询的物流不存在！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}
		return mv;
	}
	
	/**
	 * 物流跟踪查询
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "物流跟踪查询", value = "/buyer/query_ship*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/query_ship" })
	public ModelAndView query_ship(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/query_ship_data.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		TransInfo info = this.shipTools.query_Ordership_getData(id);
		mv.addObject("transInfo", info);
		return mv;
	}
	
	/**
	 * 虚拟商品信息
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "虚拟商品信息", value = "/buyer/order_seller_intro*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_seller_intro" })
	public ModelAndView order_seller_intro(HttpServletRequest request, HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/order_seller_intro.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
				mv.addObject("obj", obj);
			}
		}
		return mv;
	}
	
	/**
	 * 买家退货申请
	 * @param request
	 * @param response
	 * @param id
	 * @param oid
	 * @param goods_gsp_ids
	 * @return
	 */
	@SecurityMapping(title = "买家退货申请", value = "/buyer/order_return_apply*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_apply" })
	public ModelAndView order_return_apply(HttpServletRequest request,
			HttpServletResponse response, String id, String oid,
			String goods_gsp_ids) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/order_return_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//订单
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(oid));
		
		//必须是当前用户所属订单
		if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())) {
			List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
			
			Map return_goods = getReturnGoodsMapInJson(obj.getReturn_goods_info(), id, goods_gsp_ids);
			
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			
			for (Map m : maps) {
				if (CommUtil.null2String(m.get("goods_id")).equals(id)) {
					if (CommUtil.null2String(m.get("goods_gsp_ids")).equals(goods_gsp_ids)) {
						mv.addObject("goods", goods);
						int count = CommUtil.null2Int(m.get("goods_count"));
						if ((return_goods != null) && (return_goods.size() > 0)) {
							count = CommUtil.null2Int(m.get("goods_count")) - CommUtil.null2Int(return_goods.get("return_goods_count"));
						}
						
						if (CommUtil.null2String(m.get("goods_return_status")).equals("5")) {
							mv.addObject("view", Boolean.valueOf(true));
							List<Map> return_maps = this.orderFormTools.queryGoodsInfo(obj.getReturn_goods_info());
							for (Map map : return_maps) {
								if (CommUtil.null2String(map.get("return_goods_id")).equals(id)) {
									mv.addObject("return_content",map.get("return_goods_content"));
									count = CommUtil.null2Int(m.get("goods_count")) - CommUtil.null2Int(m.get("return_goods_count"));
								}
							}
						}
						if (m.get("combin_suit_info") != null) {
							mv.addObject("orderFormTools", this.orderFormTools);
							mv.addObject("combin_suit_info",m.get("combin_suit_info"));
						}
						mv.addObject("return_count", count);
					}
				}
			}
		}
		mv.addObject("oid", oid);
		mv.addObject("goods_gsp_ids", goods_gsp_ids);
		return mv;
	}
	
	/**
	 * 买家退货申请保存
	 * @param request
	 * @param response
	 * @param id 订单ID
	 * @param currentPage 当前页
	 * @param return_goods_content 退款内容
	 * @param goods_id 商铺ID
	 * @param return_goods_count 退款数量
	 * @param goods_gsp_ids 商品规格ID
	 * @return 
	 * @throws Exception
	 */
	@SecurityMapping(title = "买家退货申请保存", value = "/buyer/order_return_apply_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_apply_save" })
	public String order_return_apply_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String return_goods_content, String goods_id,
			String return_goods_count, String goods_gsp_ids)  {
		
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Goods goods = null;
		List<Map> goods_maps = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
		
		/**
		 * 套装退款详情，使用json管理，
		 * {
		 * 	"suit_count":2,
		 * 	"plan_goods_price":"429",
		 * 	"all_goods_price":"236.00",
		 * 	"goods_list":
		 * 		[{
		 * 			"id":92,
		 * 			"price":25.0,
		 * 			"inventory":465765,
		 * 			"store_price":25.0,
		 * 			"name":"RAYLI 韩版时尚潮帽 百变女帽子围脖两用 可爱球球保暖毛线 HA048",
		 * 			"img":"upload/system/self_goods/a7c137ef-0933-4c72-8be6-e7eb7fdfb3c7.jpg_small.jpg",
		 * 			"url":"http://localhost/goods_92.htm"
		 * 		}],
		 * 	"suit_all_price":"429.00"}
		 */
		Object combin_suit_info = null;
		for (Map goods_map : goods_maps) {
			if (CommUtil.null2String(goods_map.get("goods_id")).equals(goods_id)) {
				goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
				if (goods_map.get("combin_suit_info") == null) {
					break;
				}
				combin_suit_info = goods_map.get("combin_suit_info");
				
				break;
			}
		}
		
		Map returned_goods = null;
		int got = 0;
		//搜集整理退款详情
		/**
		 * 退货商品详细，
		 * return_goods_id:退货商品id,
		 * return_goods_count：退货数量，
		 * return_goods_commission_rate：退货商品佣金率，
		 * return_goods_price：退货商品单价，
		 * return_goods_content:退货商品说明。
		 * 使用json管理
		 * 格式如下:
		 * "[
		 * 		{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100},
		 * 		{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100}
		 *  ]"
		 */
		if (obj != null 
					&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString())
					&& goods != null) {
				
				List<Map> list = this.orderFormTools.queryGoodsInfo(obj.getReturn_goods_info());
				Map goods_map = getGoodsMapInJson(obj.getGoods_info(),goods_id, goods_gsp_ids);
				if (goods_map == null) {
					return "redirect:order_return_list?currentPage=" + currentPage;
				}
				
				if ((list != null) && (list.size() == 0)) {
					if ((CommUtil.null2Int(goods_map.get("goods_count")) < CommUtil.null2Int(return_goods_count)) 
							|| (CommUtil.null2Int(return_goods_count) < 1)) {
						return "redirect:order_return_list?currentPage=" + currentPage;
					}
					
					list = Lists.newArrayList();
					Map<String,Object> json = Maps.newHashMap();
					json.put("return_goods_id", goods.getId());
					json.put("return_goods_content",CommUtil.filterHTML(return_goods_content));
					json.put("return_goods_count", return_goods_count);
					json.put("return_goods_price", goods.getStore_price());
					json.put("return_goods_commission_rate", 
							Double.valueOf(this.orderFormTools.CommRateOfOrderByGoods(obj.getGoods_info(), goods)));
					
					json.put("return_order_id", id);
					json.put("goods_gsp_ids", goods_gsp_ids);
					list.add(json);
				} else {
					for (Map map : list) {
						if (CommUtil.null2String(map.get("return_goods_id")).equals(goods_id)) {
							if (goods_gsp_ids.equals(CommUtil.null2String(map.get("goods_gsp_ids")))) {
								int count = CommUtil.null2Int(goods_map.get("goods_count")) - CommUtil.null2Int(map.get("return_goods_count"));
								if (count - CommUtil.null2Int(return_goods_count) < 0) {
									return "redirect:order_return_list?currentPage=" + currentPage;
								}
								map.put("return_goods_count",Integer.valueOf(CommUtil.null2Int(map.get("return_goods_count")) + CommUtil.null2Int(return_goods_count)));
								returned_goods = map;
								
								got++;
								break;
							}
						}
					}
					
					if (got == 0) {
						Map json = Maps.newHashMap();
						json.put("return_goods_id", goods.getId());
						json.put("return_goods_content",
								CommUtil.filterHTML(return_goods_content));
						json.put("return_goods_count", return_goods_count);
						json.put("return_goods_price", goods.getStore_price());
						json.put("return_goods_commission_rate", Double
								.valueOf(this.orderFormTools
										.CommRateOfOrderByGoods(
												obj.getGoods_info(), goods)));
						json.put("return_order_id", id);
						json.put("goods_gsp_ids", goods_gsp_ids);
						list.add(json);
					}
				}
				/**
				 * 退货商品详细，
				 * return_goods_id:退货商品id,
				 * return_goods_count：退货数量，
				 * return_goods_commission_rate：退货商品佣金率，
				 * return_goods_price：退货商品单价，
				 * return_goods_content:退货商品说明。
				 * 使用json管理
				 * 格式如下:
				 * "[
				 * 		{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100},
				 * 		{"return_goods_id":1,"return_goods_count":3,"return_goods_commission_rate":"0.8","return_goods_price":100}
				 *  ]"
				 */
				obj.setReturn_goods_info(JSON.toJSONString(list));
				/**
				 * 使用json管理"
				 * 	[{  "goods_id":1,
				 * 		"goods_name":"佐丹奴男装翻领T恤 条纹修身商务男POLO纯棉短袖POLO衫",
				 *      "goods_type":"group",
				 *      "goods_choice_type":1,
				 *      "goods_cat":0,
				 *      "goods_commission_rate":"0.8",
				 *      "goods_commission_price":"16.00",
				 *      "goods_payoff_price":"234",
				 *      "goods_type":"combin",
				 *      "goods_count":2,
				 *      "goods_price":100,
				 *      "goods_all_price":200,
				 *      "goods_gsp_ids":"/1/3",
				 *      "goods_gsp_val":"尺码：XXL",
				 *      "goods_mainphoto_path":"upload/store/1/938a670f-081f-4e37-b355-142a551ef0bb.jpg",
				 *      "goods_return_status":"商品退货状态 当有此字段为“”时可退货状态 5为已申请退货，6商家已同意退货，7用户填写退货物流，8商家已确认，提交平台已退款 -1已经超出可退货时间",
				 *      "goods_complaint_status" 没有此字段时可投诉 投诉后的状态为1不可投诉
				 *  }]"
				 */
				List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
				
				/**
				 * gls:
				 * 1订单getGoods_info字段的json->map,
				 * 2、退款状态
				 */
				Map<String, Object> gls = getGoods_InfoAndGoodsReturnStatus(goods_id, return_goods_count, goods_gsp_ids,returned_goods, maps);
				
				//更新商品信息
				obj.setGoods_info(JSON.toJSONString(maps));
				this.orderFormService.updateById(obj);
				
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
				
				//生成退款日志
				saveReturnGoodsLog(id, return_goods_content, return_goods_count, obj, goods, combin_suit_info, gls,user);
				
				//收费快递查询
				expressQuery(request, obj);
				
				//如果是商家订单还需要发送邮件、手机短信通知等
				sendStoreMsg(request, obj);
		}
		return "redirect:order_return_list?currentPage=" + currentPage;
	}
	
	/**
	 * gls:
	 * 1订单getGoods_info字段的json->map,
	 * 2、退款状态
	 */
	private Map<String, Object> getGoods_InfoAndGoodsReturnStatus(String goods_id, String return_goods_count,
			String goods_gsp_ids, Map returned_goods, List<Map> maps) {
		Map<String,Object> gls = Maps.newHashMap();
		for (Map m : maps) {
			if (CommUtil.null2String(m.get("goods_id")).equals(goods_id)) {
				if (goods_gsp_ids.equals(CommUtil.null2String(m.get("goods_gsp_ids")))) {
					if (returned_goods != null) {
						if (CommUtil.null2String(m.get("goods_count")).equals(
								CommUtil.null2String(returned_goods.get("return_goods_count"))) ) {
							m.put("goods_return_status",5);
							
						}
					}
					if (CommUtil.null2String(m.get("goods_count")).equals(return_goods_count)) {
						m.put("goods_return_status", 5);
					}
					gls.putAll(m);
					
				}
			}
		}
		return gls;
	}
	
	/**
	 * 如果是商家订单还需要发送邮件、手机短信通知
	 * @param request
	 * @param obj
	 * @param user
	 */
	private void sendStoreMsg(HttpServletRequest request, OrderForm obj) {
		if (obj.getOrder_form() == 0) {
			User seller = this.userService.selectByPrimaryKey(this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj.getStore_id())).getUser().getId());
			
			Map map = Maps.newHashMap();
			map.put("buyer_id", SecurityUserHolder.getCurrentUser().getId());
			map.put("seller_id", seller.getId().toString());
			
			//发送邮件通知
			try {
				this.msgTools.sendEmailCharge(CommUtil.getURL(request),
						"email_toseller_order_return_apply_notify",
						seller.getEmail(), JSON.toJSONString(map), null,
						obj.getStore_id());
			} catch (Exception e) {
				System.out.println("Class="+this.getClass()+"\t邮件未开启");
			}
			
			map.clear();
			map.put("buyer_id", SecurityUserHolder.getCurrentUser().getId());
			map.put("seller_id", seller.getId().toString());
			//发送手机短信
			try {
				this.msgTools.sendSmsCharge(CommUtil.getURL(request), "sms_toseller_order_return_apply_notify",
						seller.getMobile(), JSON.toJSONString(map), null, obj.getStore_id());
			} catch (Exception e) {
				System.out.println("Class="+this.getClass()+"\t邮件未开启");
			}
		}
	}
	
	/**
	 * 收费快递查询
	 * @param request
	 * @param obj
	 */
	@SuppressWarnings("static-access")
	private void expressQuery(HttpServletRequest request, OrderForm obj) {
		//如果:为收费版快递查询 
		if (this.configService.getSysConfig().getKuaidi_type() == 1) {
			JSONObject info = new JSONObject();
			/**
			 * 物流公司信息
			 * 格式:
			 * json{
			 * 	"express_company_id":1,
			 * 	"express_company_name":"顺丰快递",
			 * 	"express_company_mark":"shunfeng",
			 * 	"express_company_type":"EXPRESS"
			 * }
			 */
			Map express_map = JSON.parseObject(obj.getExpress_info());
			info.put("company", CommUtil.null2String(express_map.get("express_company_mark")));
			info.put("number", obj.getShipCode());// 物流单号
			info.put("from", CommUtil.null2String(obj.getShip_addr()));// 发货详细地址
			info.put("to", obj.getReceiver_area());// 收货人地区,例如：福建省厦门市海沧区
			info.put("key", this.configService.getSysConfig().getKuaidi_id2());// 快递100收费推送接口，能够快速查询系统快递信息
			
			JSONObject param_info = new JSONObject();
			//请求快递那边的回调地址
			param_info.put("callbackurl", CommUtil.getURL(request)
					+ "/kuaidi_callback?order_id=" + obj.getId()
					+ "&orderType=0");
			
			//加盐
			param_info.put("salt",Md5Encrypt.md5(CommUtil.null2String(obj.getId())).substring(0, 16));
			
			info.put("parameters", param_info);
			try {
				String result = shipTools
						.Post("http://highapi.kuaidi.com/openapi-receive.html",
								info.toString());
				
				Map remap = JSON.parseObject(result);
				
				if ("success".equals(CommUtil.null2String(remap.get("message")))) {
					ExpressInfo ei = new ExpressInfo();
					ei.setAddTime(new Date());
					ei.setOrder_id(obj.getId());
					ei.setOrder_express_id(obj.getShipCode());
					ei.setOrder_type(0);
					Map ec_map = JSON.parseObject(CommUtil.null2String(obj.getExpress_info()));
					if (ec_map != null) {
						ei.setOrder_express_name(CommUtil.null2String(ec_map.get("express_company_name")));//快递公司名称
					}
					//保存快递信息
					this.expressInfoService.saveEntity(ei);
					System.out.println("订阅成功");
				} else {
					System.out.println("订阅失败");
				}
			} catch (Exception e) {
//				e.printStackTrace();
				System.out.println("订阅失败");
			}
		}
	}
	
	/**
	 * 
	 * @param id 退款订单ID
	 * @param return_goods_content 退款商品内容
	 * @param return_goods_count 退款商品数量
	 * @param obj 订单
	 * @param goods 商品
	 * 
	 * @param combin_suit_info  
	 * 套装退款详情，使用json管理，
	 * {
	 * 	"suit_count":2,
	 * 	"plan_goods_price":"429",
	 * 	"all_goods_price":"236.00",
	 * 	"goods_list":
	 * 		[{
	 * 			"id":92,
	 * 			"price":25.0,
	 * 			"inventory":465765,
	 * 			"store_price":25.0,
	 * 			"name":"RAYLI 韩版时尚潮帽 百变女帽子围脖两用 可爱球球保暖毛线 HA048",
	 * 			"img":"upload/system/self_goods/a7c137ef-0933-4c72-8be6-e7eb7fdfb3c7.jpg_small.jpg",
	 * 			"url":"http://localhost/goods_92.htm"
	 * 		}],
	 * 	"suit_all_price":"429.00"}
	 * @param gls 1订单getGoods_info字段的json->map,2、退款状态
	 * @param user 当前用户
	 */
	private void saveReturnGoodsLog(
			String id, 
			String return_goods_content, 
			String return_goods_count, 
			OrderForm obj,Goods goods, 
			Object combin_suit_info, 
			Map<String,Object> gls, 
			User user) {
		
		// 生成退货日志
		ReturnGoodsLog rlog = new ReturnGoodsLog();
		rlog.setReturn_service_id("re" + user.getId() + CommUtil.formatTime("yyyyMMddHHmmss", new Date()));//退款服务ID
		rlog.setUser_name(user.getUserName());//退款用户名
		rlog.setUser_id(user.getId());//退款用户ID
		rlog.setReturn_content(CommUtil.filterHTML(return_goods_content));//退货描述/内容
		//退款总费用 = 商品价格 X 商品数量 
		
		rlog.setGoods_all_price(CommUtil.mul(CommUtil.null2Double(gls.get("goods_price")),return_goods_count) + "");//退款商品总费用
		rlog.setGoods_count(return_goods_count);//退款数量
		rlog.setGoods_id(CommUtil.null2Long(gls.get("goods_id")));//退款商品ID
		
		rlog.setGoods_mainphoto_path(CommUtil.null2String(gls.get("goods_mainphoto_path")));//退款商品主图地址
		
		rlog.setGoods_commission_rate(BigDecimal.valueOf(CommUtil.null2Double(gls.get("goods_commission_rate"))));//退货商品的佣金比例
		
		rlog.setGoods_name(CommUtil.null2String(gls.get("goods_name")));//退款商品名称
		
		rlog.setGoods_price(CommUtil.null2String(gls.get("goods_price")));//商品价格
		
		rlog.setGoods_return_status(CommUtil.null2String(gls.get("goods_return_status")));//退款状态为:退款中
		
		rlog.setAddTime(new Date());//退款申请时间
		rlog.setReturn_order_num(CommUtil.null2Long(obj.getOrder_id()));//退款订单订单号
		rlog.setReturn_order_id(CommUtil.null2Long(id).longValue());//退款订单ID主键
		rlog.setGoods_type(goods.getGoods_type());//商品类型
		rlog.setGoods_gsp_val(CommUtil.null2String(gls.get("goods_gsp_val")));//商品规格
		rlog.setGoods_gsp_ids(CommUtil.null2String(gls.get("goods_gsp_ids")));//商品规格ID主键
		rlog.setReturn_combin_info(CommUtil.null2String(combin_suit_info));//
		if (goods.getGoods_store() != null) {
			rlog.setStore_id(goods.getGoods_store().getId());
		}
		this.returnGoodsLogService.saveEntity(rlog);
	}
	
	/**
	 * 买家退货申请取消
	 * 这里主要是将订单退款信息置为空以及删除退款、退货商品日志
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param goods_id
	 * @param goods_gsp_ids
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "买家退货申请取消", value = "/buyer/order_return_apply_cancel*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_apply_cancel" })
	public String order_return_apply_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_id, String goods_gsp_ids) {
		
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(obj);
		
		Goods goods = null;
		for (Goods g : goods_list) {
			if ((g != null) && (g.getId().toString().equals(goods_id))) {
				goods = g;
			}
		}
		
		if (obj != null
				&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId().toString()) 
				&& (goods != null)) {
				
				obj.setReturn_goods_info("");//退款信息置为空
				List<Map> maps = this.orderFormTools.queryGoodsInfo(obj.getGoods_info());
				Map gls = Maps.newHashMap();
				for (Map m : maps) {
					if (CommUtil.null2String(m.get("goods_id")).equals(goods_id)) {
						if (goods_gsp_ids.equals(CommUtil.null2String(m.get("goods_gsp_ids")))) {
							m.put("goods_return_status", "");
							gls.putAll(m);
						}
					}
				}
				
				obj.setGoods_info(JSON.toJSONString(maps));
				this.orderFormService.updateById(obj);
				
				Map<String,Object> map = Maps.newHashMap();
				map.put("goods_id",CommUtil.null2Long(gls.get("goods_id")));
				map.put("return_order_id", CommUtil.null2Long(id));
				map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
				map.put("goods_gsp_ids", goods_gsp_ids);
				
				List<ReturnGoodsLog> objs = this.returnGoodsLogService.queryPageList(map);
				//删除退款信息
				for (ReturnGoodsLog rl : objs) {
					this.returnGoodsLogService.deleteById(rl.getId());
				}
		}
		return "redirect:order_return_list?currentPage=" + currentPage;
	}
	
	/**
	 * 买家退货申请列表
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @return
	 */
	@SecurityMapping(title = "买家退货申请列表", value = "/buyer/order_return_list*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_list" })
	public ModelAndView order_return_list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String order_id) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/order_return_list.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");

		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());

		if (!CommUtil.null2String(order_id).equals("")) {
			maps.put("order_id_like", order_id);
			mv.addObject("order_id", order_id);
		}

		maps.put("order_cat_no", 2);

		maps.put("order_status_more_than_equal", 40);

//		maps.put("return_shipTime_more_than_equal", new Date());
		IPageList pList = this.orderFormService.list(maps);

		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 生活购退款列表
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param view
	 * @param currentPage
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "生活购退款列表", value = "/buyer/group_life_return*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/group_life_return" })
	public ModelAndView group_life_return(HttpServletRequest request, HttpServletResponse response, String id,
			String view, String currentPage, String order_id, String beginTime, String endTime) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/group_life_return.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");

		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());

		maps.put("status_no", 1);

		maps.put("status_no1", -1);

		maps.put("refund_Time_more_than_equal", new Date());

		IPageList pList = this.groupinfoService.queryPagesWithNoRelations(maps);

		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 生活购退款申请
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "生活购退款申请", value = "/buyer/group_life_return_apply*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/group_life_return_apply" })
	public ModelAndView group_life_return_apply(HttpServletRequest request, HttpServletResponse response, String id) {
		
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/group_life_return_apply.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		
		GroupInfo obj = this.groupinfoService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId())) && (obj.getStatus() == 0)) {
			mv.addObject("obj", obj);
		}
		return mv;
	}
	
	/**
	 * 生活购退款申请取消
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "生活购退款申请取消", value = "/buyer/grouplife_return_apply_cancel*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/grouplife_return_apply_cancel" })
	public String grouplife_return_apply_cancel(HttpServletRequest request, HttpServletResponse response, String id) {
		GroupInfo obj = this.groupinfoService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId())) {
			obj.setStatus(0);
			this.groupinfoService.updateById(obj);
		}
		return "redirect:group_life_return";
	}

	/**
	 * 生活购退款申请保存
	 * @param request
	 * @param response
	 * @param id 生活类商品团购消费码类ID主键
	 * @param return_group_content 退款说明
	 * @param reasion 退款理由
	 * 	1 买错了/重新买
	 *  2 计划有变，没时间消费
	 *  3 预约不上
	 *  4 去过了，不太满意
	 *  5 其他
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "生活购退款申请保存", value = "/buyer/grouplife_return_apply_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/grouplife_return_apply_save" })
	public String grouplife_return_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id,
			String return_group_content, String reasion) {
		
		GroupInfo obj = this.groupinfoService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (obj != null 
				&& obj.getUser_id().equals(SecurityUserHolder.getCurrentUser().getId())) {
				
				String mark = "";
				if ((reasion != null) && (!reasion.equals(""))) {
					String[] rs_ids = reasion.split(",");
					for (String rid : rs_ids) {
						if (!rid.equals("")) {
							if (rid.equals("1")) {
								mark = "买错了/重新买";
							} else if (rid.equals("2")) {
								mark = "计划有变，没时间消费";
							} else if (rid.equals("3")) {
								mark = "预约不上";
							} else if (rid.equals("4")) {
								mark = "去过了，不太满意";
							} else if (rid.equals("5")) {
								mark = "其他";
							}
						}
						// 团购信息状态，默认为0，,使用后为1,-1为过期,3申请退款、5退款中、7退款完成
						obj.setStatus(5);
						obj.setRefund_reasion(mark + "[" + return_group_content + "]");
						this.groupinfoService.updateById(obj);
						
						OrderForm order = this.orderFormService.selectByPrimaryKey(obj.getOrder_id());
						
						//如果是商家订单还需要发送邮件、手机短信通知等
						sendStoreMsg(request, order);
						
					}
				}
		}
		return "redirect:group_life_return";
	}
	
	/**
	 * 买家退货申请列表记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家退货申请列表记录", value = "/buyer/order_return_listlog*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_listlog" })
	public ModelAndView order_return_listlog(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/order_return_listlog.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		maps.put("user_id", user.getId());
		maps.put("goods_return_status_no", 1);
		
		IPageList pList = this.returnGoodsLogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("userTools", this.userTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	/**
	 * 买家退货物流信息保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param express_id
	 * @param express_code
	 * @return
	 */
	@SecurityMapping(title = "买家退货物流信息保存", value = "/buyer/order_return_ship_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_ship_save" })
	public ModelAndView order_return_ship_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String express_id, String express_code) {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		ReturnGoodsLog obj = this.returnGoodsLogService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		if ((obj != null) && (obj.getUser_id().equals(user.getId()))) {
			
			ExpressCompany ec = this.expressCompayService.selectByPrimaryKey(CommUtil.null2Long(express_id));
			Map json_map = Maps.newHashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			String express_json = JSON.toJSONString(json_map);
			obj.setReturn_express_info(express_json);
			obj.setExpress_code(express_code);
			obj.setGoods_return_status("7");
			this.returnGoodsLogService.updateById(obj);
			
			OrderForm order = this.orderFormService.selectByPrimaryKey(Long.valueOf(obj.getReturn_order_id()));
			
			List<Map> maps = this.orderFormTools.queryGoodsInfo(order.getGoods_info());
			
			Map gls = Maps.newHashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString().equals(CommUtil.null2String(obj.getGoods_id()))) {
					
					if (obj.getGoods_gsp_ids().equals(m.get("goods_gsp_ids").toString())) {
						m.put("goods_return_status", "7");
						gls.putAll(m);
						break;
					}
				}
			}
			
			order.setGoods_info(JSON.toJSONString(maps));
			this.orderFormService.updateById(order);
			mv.addObject("op_title", "保存退货物流成功！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order_return_listlog");
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有为" + obj.getReturn_service_id()
					+ "的退货服务号！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog");
		}
		return mv;
	}
	
	/**
	 * 买家退货填写物流
	 * @param request
	 * @param response
	 * @param g_id
	 * @param o_id
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "买家退货填写物流", value = "/buyer/order_returnlog_view*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_returnlog_view" })
	public ModelAndView order_returnlog_view(HttpServletRequest request,
			HttpServletResponse response, String g_id, String o_id, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/order_returnlog_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLog obj = null;
		Map params = Maps.newHashMap();
		if ((id != null) && (!id.equals(""))) {
			obj = this.returnGoodsLogService.selectByPrimaryKey(CommUtil.null2Long(id));
		} else {
			params.put("goods_id", Long.valueOf(Long.parseLong(g_id)));
			params.put("return_order_id", Long.valueOf(Long.parseLong(o_id)));
			List<ReturnGoodsLog> rgl = this.returnGoodsLogService.queryPageList(params,0,1);
			
			if (rgl.size() > 0) {
				obj = (ReturnGoodsLog) rgl.get(0);
			}
		}
		if (obj == null) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有该退货服务单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog");
			return mv;
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((obj != null) && (obj.getUser_id().equals(user.getId()))) {
			if ((obj.getGoods_return_status().equals("6"))
					|| (obj.getGoods_return_status().equals("7"))) {
				
				params.clear();
				params.put("company_status", Integer.valueOf(0));
				params.put("orderBy", "company_sequence");
				params.put("orderType", "asc");
				List<ExpressCompany> expressCompanys = this.expressCompayService.queryPageList(params);
				
				mv.addObject("expressCompanys", expressCompanys);
				mv.addObject("obj", obj);
				mv.addObject("user", user);
				OrderForm of = this.orderFormService.selectByPrimaryKey(Long.valueOf(obj.getReturn_order_id()));
				mv.addObject("of", of);
				Goods goods = this.goodsService.selectByPrimaryKey(obj.getGoods_id());
				if (goods.getGoods_type() == 1) {
					mv.addObject("name", goods.getGoods_store().getStore_name());
					mv.addObject("store_id", goods.getGoods_store().getId());
				} else {
					mv.addObject("name", "平台商");
				}
				if (obj.getGoods_return_status().equals("7")) {
					TransInfo transInfo = this.shipTools.query_Returnship_getData(CommUtil.null2String(obj.getId()));
					mv.addObject("transInfo", transInfo);
					Map map = JSON.parseObject(obj.getReturn_express_info());
					mv.addObject("express_company_name",map.get("express_company_name"));
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "当前状态无法对退货服务单进行操作");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order_return_listlog");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有为" + obj.getReturn_service_id()
					+ "的退货服务号！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog");
		}
		return mv;
	}
	
	/**
	 * 物流ajax
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param mark
	 * @return
	 */
	@SecurityMapping(title = "物流ajax", value = "/buyer/ship_ajax*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/ship_ajax" })
	public ModelAndView ship_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String mark) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/ship_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		List<TransInfo> transInfo_list = Lists.newArrayList();
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		TransInfo transInfo = this.shipTools.query_Ordership_getData(CommUtil
				.null2String(order_id));
		if (transInfo != null) {
			transInfo.setExpress_company_name(this.orderFormTools.queryExInfo(
					order.getExpress_info(), "express_company_name"));
			transInfo.setExpress_ship_code(order.getShipCode());
			transInfo_list.add(transInfo);
		}
		if (("".equals(mark)) && (order.getOrder_main() == 1)) {
			if (!CommUtil.null2String(order.getChild_order_detail()).equals("")) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(order
						.getChild_order_detail());
				for (Map child_map : maps) {
					OrderForm child_order = this.orderFormService
							.selectByPrimaryKey(CommUtil.null2Long(child_map
									.get("order_id")));
					TransInfo transInfo1 = this.shipTools
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
		mv.addObject("transInfo_list", transInfo_list);
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
	
	private Map getGoodsMapInJson(String json, String goods_id,String goods_gsp_ids) {
		Map map = null;
		List<Map> map_list = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			map_list = JSON.parseArray(json, Map.class);
			for (Map m : map_list) {
				if (CommUtil.null2String(m.get("goods_id")).equals(goods_id)) {
					if (CommUtil.null2String(m.get("goods_gsp_ids")).equals(
							goods_gsp_ids)) {
						map = m;
						break;
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 将退款商品json格式信息转换为map格式
	 * @param json
	 * @param goods_id
	 * @param goods_gsp_ids
	 * @return
	 */
	private Map getReturnGoodsMapInJson(String json, String goods_id,String goods_gsp_ids) {
		Map map = null;
		List<Map> map_list = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			map_list = JSON.parseArray(json, Map.class);
			for (Map m : map_list) {
				if (CommUtil.null2String(m.get("return_goods_id")).equals(goods_id)) {
					if (CommUtil.null2String(m.get("goods_gsp_ids")).equals(goods_gsp_ids)) {
						map = m;
						break;
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 商品快照
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品快照", value = "/buyer/goods_snapshoot*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/goods_snapshoot" })
	public ModelAndView snapshot(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = null;
		Snapshot snap = this.snapshotService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (snap != null) {
			if (snap.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId())) {
				mv = new RedPigJModelAndView("user/default/usercenter/snapshot.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("snap", snap);
				mv.addObject("storeTools", this.storeTools);
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "不要违规操作，谢谢");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "不存在该ID");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 更新订单状态
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "更新订单状态", value = "/buyer/update_status*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/update_status" })
	public void UpdateOrderStatus(HttpServletRequest request,
			HttpServletResponse response, String id) {
		this.orderFormTools.updateOrderStatus(id, "1");
	}
	
	/**
	 * 猜你喜欢列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 * @throws Exception 
	 */
	@SecurityMapping(title = "猜你喜欢列表", value = "/your_like_goods*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/your_like_goods" })
	public ModelAndView your_like_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage) throws Exception {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/your_like_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		List<Goods> your_like_goods = Lists.newArrayList();
		Long your_like_GoodsClass = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			
			for (Cookie cookie : cookies) {
				
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = URLDecoder.decode(cookie.getValue(), "UTF-8").split(",", 2);
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(like_gcid[0]));
					if (goods == null) {
						break;
					}
					if (goods.getGc() != null) {
						your_like_GoodsClass = goods.getGc().getId();
					}
					Map<String,Object> maps = Maps.newHashMap();
					maps.put("goods_status", 0);
					maps.put("gc_id", your_like_GoodsClass);
					maps.put("id_no", goods.getId());
					maps.put("orderBy", "goods_salenum");
					maps.put("orderType", "desc");
					
					your_like_goods = this.goodsService.queryPageList(maps, 0, 20);
					
					int gcs_size = your_like_goods.size();
					if (gcs_size >= 20) {
						break;
					}
					maps.clear();
					maps.put("goods_status", 0);
					maps.put("id_no", goods.getId());
					maps.put("orderBy", "goods_salenum");
					maps.put("orderType", "desc");
					
					List<Goods> like_goods = this.goodsService.queryPageList(maps,0,20 - gcs_size);
					
					for (int i1 = 0; i1 < like_goods.size(); i1++) {
						int k = 0;
						for (int j1 = 0; j1 < your_like_goods.size(); j1++) {
							if (((Goods) like_goods.get(i1)).getId().equals(
									((Goods) your_like_goods.get(j1)).getId())) {
								k++;
							}
						}
						if (k == 0) {
							your_like_goods.add((Goods) like_goods.get(i1));
						}
					}
					break;
				}
				Map<String,Object> maps = Maps.newHashMap();
				maps.put("goods_status", 0);
				maps.put("orderBy", "goods_salenum");
				maps.put("orderType", "desc");
				
				your_like_goods = this.goodsService.queryPageList(maps, 0,20);
				
			}
		} else {
			Map<String,Object> maps = Maps.newHashMap();
			maps.put("goods_status", 0);
			maps.put("orderBy", "goods_salenum");
			maps.put("orderType", "desc");
			your_like_goods = this.goodsService.queryPageList(maps, 0,20);
			
		}
		mv.addObject("your_like_goods", your_like_goods);
		return mv;
	}
}
