package com.redpigmall.module.cloudpurchase.view.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.module.cloudpurchase.manage.admin.base.BaseAction;
import com.redpigmall.domain.CloudPurchaseCart;
import com.redpigmall.domain.CloudPurchaseOrder;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.User;

/**
 * <p>
 * Title: RedPigCloudPurchaseCartAction.java
 * </p>
 * 
 * <p>
 * Description: 云购商品管理
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
 * @date 2016年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCloudPurchaseCartAction extends BaseAction{
	
	@RequestMapping({ "/cloudbuy/cart_add" })
	public void cart_add(HttpServletRequest request,
			HttpServletResponse response, String lottery_id, String count) {
		User user = SecurityUserHolder.getCurrentUser();
		int cartNum = this.cloudPurchaseCartService.addCloudsCart(
				CommUtil.null2Long(user.getId()),
				CommUtil.null2Long(lottery_id), CommUtil.null2Int(count));
		Map<String, Object> map = Maps.newHashMap();
		map.put("status", Integer.valueOf(cartNum == 1 ? 1 : 0));
		map.put("inventory", Integer.valueOf(cartNum));
		User user1 = SecurityUserHolder.getCurrentUser();
		if (user1 != null) {
			
			List<CloudPurchaseCart> list = getUser(user1);
			
			map.put("count", Integer.valueOf(list.size()));
		}
//		AppResponse appResponse = new AppResponse(AppResponse.SUCCESS,AppResponse.DATA_MAP, "", map);
//		AppResponseTools.sendJson(response, appResponse.toString());
	}

	@RequestMapping({ "/cloudbuy/cart" })
	public ModelAndView cart(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_cart0.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			
			List<CloudPurchaseCart> list = getUser(user);
			
			mv.addObject("carts", list);
		}
		return mv;
	}

	private List<CloudPurchaseCart> getUser(User user) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user.getId());
		params.put("cloudPurchaseLottery_status_no", 5);
		
		List<CloudPurchaseCart> cart_id = this.cloudPurchaseCartService.queryPageList(params);
		
		if (cart_id.size() > 0) {
			for (CloudPurchaseCart cloudPurchaseCart : cart_id) {
				this.cloudPurchaseCartService.deleteById(cloudPurchaseCart.getId());
			}
		}
		params.clear();
		params.put("user_id", user.getId());
		
		List<CloudPurchaseCart> list = this.cloudPurchaseCartService.queryPageList(params);
		return list;
	}
	
	@RequestMapping({ "/cloudbuy/cartlist" })
	public void cartlist(HttpServletRequest request,
			HttpServletResponse response) {
//		List<CloudPurchaseCart> list = Lists.newArrayList();
//		User user = SecurityUserHolder.getCurrentUser();
//		if (user != null) {
//			list = getUser(user);
//		}
//		AppResponse appResponse = new AppResponse(AppResponse.SUCCESS,AppResponse.DATA_ARRAY, "", list);
//		AppResponseTools.sendJson(response, appResponse.toString());
	}
	
	@RequestMapping({ "/cloudbuy/cartdel" })
	public void cartdel(HttpServletRequest request,
			HttpServletResponse response, String cart_id) {
//		AppResponse appResponse = new AppResponse(AppResponse.SUCCESS,AppResponse.DATA_EMPTY, "", null);
		
		User user = SecurityUserHolder.getCurrentUser();
		if ((user != null) && (cart_id != null) && (!cart_id.equals(""))) {
			
			for (String id : cart_id.split(",")) {
				dle(user.getId(),CommUtil.null2Long(id));
			}
//			appResponse = new AppResponse(AppResponse.SUCCESS,AppResponse.DATA_ARRAY, "", null);
		}
//		AppResponseTools.sendJson(response, appResponse.toString());
	}
	
	public int dle(Long user_id, Long car_id) {
		CloudPurchaseCart cart = this.cloudPurchaseCartService.selectByPrimaryKey(car_id);
		if (cart.getUser_id().equals(user_id)) {
			this.cloudPurchaseCartService.deleteById(car_id);
			return 1;
		}
		return 0;
	}
	
	@RequestMapping({ "/cloudbuy/cartadjust" })
	public void cartadjust(HttpServletRequest request,
			HttpServletResponse response, String cart_id, String count) {
		User user = SecurityUserHolder.getCurrentUser();
//		AppResponse appResponse = new AppResponse(AppResponse.SUCCESS,AppResponse.CLOUDPURCHASE_CART_MODIFY_FAIL, "", null);
		if (user != null) {
			CloudPurchaseCart cloudPurchaseCart = this.cloudPurchaseCartService
					.selectByPrimaryKey(CommUtil.null2Long(cart_id));
			if ((cloudPurchaseCart.getUser_id().equals(CommUtil.null2Long(user
					.getId())))
					&& (this.cloudPurchaseCartService.updateCloudGoodsNum(
							cloudPurchaseCart, CommUtil.null2Int(count)) == 1)) {
//				appResponse = new AppResponse(AppResponse.SUCCESS,AppResponse.CLOUDPURCHASE_CART_MODIFY_SUCCESS, "", null);
			}
		}
//		AppResponseTools.sendJson(response, appResponse.toString());
	}

	@SecurityMapping(title = "确认购物车第二步", value = "/cloudbuy/cart1*", rtype = "buyer", rname = "购物流程2", rcode = "cloud_cart", rgroup = "云购")
	@RequestMapping({ "/cloudbuy/cart1" })
	public ModelAndView cart1(HttpServletRequest request,
			HttpServletResponse response, String cart_ids) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_cart1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			List<CloudPurchaseCart> list = Lists.newArrayList();
			
			for (String id : cart_ids.split(",")) {

				CloudPurchaseCart cart = this.cloudPurchaseCartService.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((cart != null) && (cart.getUser_id().equals(user.getId()))) {
					list.add(cart);
				}
			}
			mv.addObject("carts", list);
		}
		mv.addObject("cart_ids", cart_ids);

		return mv;
	}
	
	/**
	 * 确认购物车第三步
	 * @param request
	 * @param response
	 * @param cart_ids
	 * @return
	 */
	@SecurityMapping(title = "确认购物车第三步", value = "/cloudbuy/cart2*", rtype = "buyer", rname = "购物流程2", rcode = "cloud_cart", rgroup = "云购")
	@RequestMapping({ "/cloudbuy/cart2" })
	public ModelAndView cart2(HttpServletRequest request,
			HttpServletResponse response, String cart_ids) {
		ModelAndView mv = new RedPigJModelAndView("clouds/cloudpurchase_cart2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String,Object> map = Maps.newHashMap();
		if (user != null) {
			
			for (String id : cart_ids.split(",")) {
				
				CloudPurchaseCart cart = this.cloudPurchaseCartService
						.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((cart != null) && (cart.getUser_id().equals(user.getId()))) {
					map.put(cart.getCloudPurchaseLottery().getId().toString(),Integer.valueOf(cart.getPurchased_times()));
				}
			}
			CloudPurchaseOrder order = this.cloudPurchaseOrderService.submitOrder(user.getId(), map);
			mv.addObject("obj", order);
			mv.addObject("paymentTools", this.paymentTools);
		}
		return mv;
	}
	
	/**
	 * 确认购物车支付
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @param pay_password
	 * @return
	 */
	@SecurityMapping(title = "确认购物车支付", value = "/cloudbuy/order_pay*", rtype = "buyer", rname = "购物流程", rcode = "cloud_cart", rgroup = "云购")
	@RequestMapping({ "/cloudbuy/order_pay" })
	public ModelAndView order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_password) {
		ModelAndView mv = null;

		CloudPurchaseOrder order = this.cloudPurchaseOrderService
				.selectByPrimaryKey(CommUtil.null2Long(order_id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (("balance".equals(payType)) && (user.getPay_password() != null)
				&& (!user.getPay_password().equals(""))) {
			if ((pay_password != null) && (!"".equals(pay_password))) {
				if (!user.getPay_password()
						.equals(Md5Encrypt.md5(pay_password))) {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "请不要违规操作");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/cart");
					return mv;
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "请输入您的支付密码");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/cart");
				return mv;
			}
		}
		if (order.getStatus() == 0) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/cloudbuy/index");
			} else {
				order.setPayment(payType);
				this.cloudPurchaseOrderService.updateById(order);
				if (payType.equals("balance")) {
					mv = new RedPigJModelAndView(
							"clouds/balance_pay_cloudpurchase.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("order_total_price",
							Integer.valueOf(order.getPrice()));
					mv.addObject("order_id", order.getId());
					mv.addObject("user", user);
					String pay_session = CommUtil.randomString(32);
					request.getSession(false).setAttribute("pay_session",
							pay_session);
					mv.addObject("pay_session", pay_session);
				} else {
					mv = new RedPigJModelAndView("line_pay.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("payType", payType);
					mv.addObject("url", CommUtil.getURL(request));
					mv.addObject("payTools", this.payTools);
					mv.addObject("type", "cloudpurchase");
					Map<String, Object> params = Maps.newHashMap();
					params.put("install", Boolean.valueOf(true));
					params.put("mark", payType);
					
					List<Payment> payments = this.paymentService.queryPageList(params);
					
					mv.addObject(
							"payment_id",
							payments.size() > 0 ? ((Payment) payments.get(0))
									.getId() : new Payment());
					if ("wx_pay".equals(payType)) {
						try {
							response.sendRedirect("/pay/wx_pay?id=" + order_id
									+ "&showwxpaytitle=1&type="
									+ "cloudpurchase");
						} catch (IOException e) {
							e.printStackTrace();
							mv = new RedPigJModelAndView("error.html",
									this.configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "支付方式错误！");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/index");
						}
					}
				}
				mv.addObject("order_id", order_id);
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单不能进行付款！");
			mv.addObject("url", CommUtil.getURL(request) + "/cloudbuy/index");
		}
		return mv;
	}
	
	/**
	 * 订单预付款支付
	 * @param request
	 * @param response
	 * @param order_id
	 * @param pay_session
	 * @return
	 */
	@SecurityMapping(title = "订单预付款支付", value = "/cloudbuy/order_pay_balance*", rtype = "buyer", rname = "购物流程", rcode = "cloud_cart", rgroup = "云购")
	@RequestMapping({ "/cloudbuy/order_pay_balance" })
	public ModelAndView order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_session) {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			CloudPurchaseOrder order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(order_id));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			if ((order.getUser_id().equals(user.getId()))
					&& (order.getStatus() == 0)) {
				if (CommUtil.null2Double(user.getAvailableBalance()) >= CommUtil
						.null2Double(Integer.valueOf(order.getPrice()))) {
					order.setStatus(5);
					order.setPayment("balance");
					order.setPayTime(new Date());
					this.cloudPurchaseOrderService.updateById(order);
					boolean ret = true;
					if (ret) {
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.subtract(user.getAvailableBalance(),Integer.valueOf(order.getPrice()))));
						this.userService.updateById(user);
						
						this.cloudPurchaseOrderService.reduce_inventory(order,request);
					}
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(user);
					log.setPd_log_amount(new BigDecimal(order.getPrice()));
					log.setPd_op_type("消费");
					log.setPd_type("可用预存款");
					log.setPd_log_info("订单" + order.getOdrdersn() + "购买云购商品减少可用预存款");
					this.predepositLogService.saveEntity(log);
					mv.addObject("op_title", "预付款支付成功！");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/cloudbuy_order");
					request.getSession(false).removeAttribute("pay_session");
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "可用余额不足，支付失败！");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/cart");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "请求参数错误");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/cart");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/cloudbuy_order");
		}
		return mv;
	}
}
