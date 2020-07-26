package com.redpigmall.module.cloudpurchase.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.cloudpurchase.manage.admin.base.BaseAction;
import com.redpigmall.domain.CloudPurchaseCart;
import com.redpigmall.domain.CloudPurchaseOrder;


/**
 * 
 * <p>
 * Title: RedPigWeixinCloudPurchaseCartAction.java
 * </p>
 * 
 * <p>
 * Description: 
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-4-28
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigWeixinCloudPurchaseCartAction extends BaseAction{
	/**
	 * 移动端云购加入购物车
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "移动端云购加入购物车", value = "/add_cloudpurchase_cart*", rtype = "buyer", rname = "移动端云购购物车", rcode = "wap_cloud_cart", rgroup = "移动端云购购物车")
	@RequestMapping({ "/add_cloudpurchase_cart" })
	public void add_cloudpurchase_cart(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Long user_id = SecurityUserHolder.getCurrentUser().getId();
		
		this.cloudPurchaseCartService.addCloudsCart(user_id,CommUtil.null2Long(id), 1);
		
		Map<String,Object> param = Maps.newHashMap();
		param.put("user_id", user_id);
		
		int result = this.cloudPurchaseCartService.selectCount(param);
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "移动端云购加入购物车", value = "/cloudpurchase_cart*", rtype = "buyer", rname = "移动端云购购物车", rcode = "wap_cloud_cart", rgroup = "移动端云购购物车")
	@RequestMapping({ "/cloudpurchase_cart" })
	public ModelAndView cloudpurchase_cart(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Long user_id = SecurityUserHolder.getCurrentUser().getId();
		if (StringUtils.isNotBlank(id)) {
			this.cloudPurchaseCartService.addCloudsCart(user_id,
					CommUtil.null2Long(id), 1);
		}
		ModelAndView mv = new RedPigJModelAndView("weixin/cloudpurchase_cart.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		params.put("cloudPurchaseLottery_status_no", user_id);
		
		List<CloudPurchaseCart> carts = this.cloudPurchaseCartService.queryPageList(params);
		
		if (carts.size() > 0) {
			for (CloudPurchaseCart cart : carts) {
				this.cloudPurchaseCartService.deleteById(cart.getId());
			}
		}
		params.clear();
		params.put("user_id", user_id);
		
		List<CloudPurchaseCart> objs = this.cloudPurchaseCartService.queryPageList(params);
		
		mv.addObject("objs", objs);
		return (ModelAndView) mv;
	}
	
	@SecurityMapping(title = "移动端云购加入购物车", value = "/cloudpurchase_cart2*", rtype = "buyer", rname = "移动端云购购物车", rcode = "wap_cloud_cart", rgroup = "移动端云购购物车")
	@RequestMapping({ "/cloudpurchase_cart2" })
	public ModelAndView cloudpurchase_cart2(HttpServletRequest request,
			HttpServletResponse response) {
		Long user_id = SecurityUserHolder.getCurrentUser().getId();
		ModelAndView mv = new RedPigJModelAndView("weixin/cloudpurchase_cart2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		params.put("cloudPurchaseLottery_status_no", user_id);
		
		List<CloudPurchaseCart> carts = this.cloudPurchaseCartService.queryPageList(params);
		
		if (carts.size() > 0) {
			for (CloudPurchaseCart cart : carts) {
				this.cloudPurchaseCartService.deleteById(cart.getId());
			}
		}
		params.clear();
		params.put("user_id", user_id);
		
		List<CloudPurchaseCart> objs = this.cloudPurchaseCartService.queryPageList(params);
		
		mv.addObject("objs", objs);
		String cart_session_id = UUID.randomUUID().toString();
		request.getSession(false).setAttribute("cart_session_id",
				cart_session_id);
		mv.addObject("cart_session_id", cart_session_id);
		return mv;
	}

	@SecurityMapping(title = "移动端云购加入购物车", value = "/cloudpurchase_cart3*", rtype = "buyer", rname = "移动端云购购物车", rcode = "wap_cloud_cart", rgroup = "移动端云购购物车")
	@RequestMapping({ "/cloudpurchase_cart3" })
	public ModelAndView cloudpurchase_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "提交订单失败");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/cloudpurchase_index");
		String session_id = (String) request.getSession(false).getAttribute(
				"cart_session_id");
		if (session_id.equals(cart_session_id)) {
			Long user_id = SecurityUserHolder.getCurrentUser().getId();
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", user_id);
			params.put("cloudPurchaseLottery_status_no", user_id);
			
			List<CloudPurchaseCart> carts = this.cloudPurchaseCartService.queryPageList(params);
			
			if (carts.size() > 0) {
				for (CloudPurchaseCart cart : carts) {
					this.cloudPurchaseCartService.deleteById(cart.getId());
				}
			}
			params.clear();
			params.put("user_id", user_id);
			
			List<CloudPurchaseCart> objs = this.cloudPurchaseCartService.queryPageList(params);
			params.clear();
			for (CloudPurchaseCart obj : objs) {
				params.put(obj.getCloudPurchaseLottery().getId().toString(),Integer.valueOf(obj.getPurchased_times()));
			}
			CloudPurchaseOrder order = this.cloudPurchaseOrderService.submitOrder(CommUtil.null2Long(user_id), params);
			if (order != null) {
				mv = new RedPigJModelAndView("weixin/cloudpurchase_cart3.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("order", order);
			}
		}
		return mv;
	}

	@SecurityMapping(title = "移动端云购加入购物车", value = "/cloudpurchase_cart_remove*", rtype = "buyer", rname = "移动端云购购物车", rcode = "wap_cloud_cart", rgroup = "移动端云购购物车")
	@RequestMapping({ "/cloudpurchase_cart_remove" })
	public void cloudpurchase_cart_remove(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Long user_id = SecurityUserHolder.getCurrentUser().getId();
		if (StringUtils.isNotBlank(id)) {
			CloudPurchaseCart cart = this.cloudPurchaseCartService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if ((cart != null) && (cart.getUser_id().equals(user_id))) {
				this.cloudPurchaseCartService.deleteById(CommUtil.null2Long(id));
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("success");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SecurityMapping(title = "移动端云购数量选择", value = "/cloudpurchase_cart_adjust*", rtype = "buyer", rname = "移动端云购购物车", rcode = "wap_cloud_cart", rgroup = "移动端云购购物车")
	@RequestMapping({ "/cloudpurchase_cart_adjust" })
	public void cloudpurchase_cart_adjust(HttpServletRequest request,
			HttpServletResponse response, String id, String count) {
		Long user_id = SecurityUserHolder.getCurrentUser().getId();
		Map<String, Object> map = Maps.newHashMap();
		map.put("status", "0");
		if (StringUtils.isNotBlank(id)) {
			CloudPurchaseCart cart = this.cloudPurchaseCartService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if ((cart.getCloudPurchaseLottery().getCloudPurchaseGoods()
					.getLeast_rmb() == 10)
					&& (CommUtil.null2Int(count) % 10 != 0)) {
				count = "10";
			}
			if ((cart != null) && (cart.getUser_id().equals(user_id))) {
				this.cloudPurchaseCartService.updateCloudGoodsNum(cart,CommUtil.null2Int(count));
				map.put("status", "1");
				map.put("count", Integer.valueOf(cart.getPurchased_times()));
			}
		}
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
}
