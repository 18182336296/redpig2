package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.RefundApplyForm;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RefundApplyFormSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 订单退款管理类
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
 * @date 2014年5月5日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigRefundApplyFormSellerAction extends BaseAction{
	/**
	 * 订单退款申请单
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "订单退款申请单", value = "/refund_apply_form*", rtype = "seller", rname = "退款管理", rcode = "refund_seller", rgroup = "客户服务")
	@RequestMapping({ "/refund_apply_form" })
	public ModelAndView refund_apply_form(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/refund_apply_form.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		if ((status == null) || (status.equals(""))) {
			maps.put("status", CommUtil.null2Int(Integer.valueOf(0)));
		} else if (status.equals("all")) {
			mv.addObject("status", status);
		} else {
			maps.put("status", CommUtil.null2Int(Integer.valueOf(status)));
			mv.addObject("status", status);
		}
		
		maps.put("store_id", user.getStore().getId().toString());
		IPageList pList = this.refundapplyformService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/refund_apply_form.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 订单退款申请单详情
	 * @param request
	 * @param response
	 * @param order_id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "订单退款申请单详情", value = "/refund_orderform_view*", rtype = "seller", rname = "退款管理", rcode = "refund_seller", rgroup = "客户服务")
	@RequestMapping({ "/refund_orderform_view" })
	public ModelAndView refund_orderform_view(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/refund_orderform_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_id));
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj.getStore_id()));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (obj != null) {
			String temp = obj.getSpecial_invoice();
			if ((temp != null) && (!"".equals(temp))) {
				Map of_va = JSON.parseObject(temp);
				mv.addObject("of_va", of_va);
			}
		}
		mv.addObject("obj", obj);
		mv.addObject("store", store);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("express_company_name", this.orderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
		return mv;
	}
	
	/**
	 * 订单退款申请单审核
	 * @param request
	 * @param result
	 * @param apply_form_id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "订单退款申请单审核", value = "/refund_audit*", rtype = "seller", rname = "退款管理", rcode = "refund_seller", rgroup = "客户服务")
	@RequestMapping({ "/refund_audit" })
	public String refund_audit(HttpServletRequest request, String result,
			String apply_form_id, String currentPage) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		RefundApplyForm refundApplyForm = this.refundapplyformService.selectByPrimaryKey(CommUtil.null2Long(apply_form_id));
		Store store = (user.getParent() == null ? user : user.getParent()).getStore();
		if ((store != null)
				&& (store.getId().toString().equals(refundApplyForm.getStore_id()))) {
			OrderForm orderForm = this.orderFormService.selectByPrimaryKey(refundApplyForm.getOrder_form_id());
			refundApplyForm.setAudit_date(new Date());
			refundApplyForm.setAudit_user_name(user.getUserName());
			if ((result != null) && (!result.equals(""))) {
				refundApplyForm.setStatus(10);
				this.refundapplyformService.updateById(refundApplyForm);
				orderForm.setOrder_status(22);
				this.orderFormService.updateById(orderForm);
				Message msg = new Message();
				String msg_content = "您订单号为：" + orderForm.getOrder_id()
						+ "的订单退款申请已通过，我们会尽快将" + "退款金额打入您的预存款中。";
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(user);
				msg.setToUser(this.userService.selectByPrimaryKey(CommUtil
						.null2Long(orderForm.getUser_id())));
				this.messageService.saveEntity(msg);
			} else {
				refundApplyForm.setStatus(5);
				this.refundapplyformService.updateById(refundApplyForm);
				orderForm.setOrder_status(20);
				this.orderFormService.updateById(orderForm);
				Message msg = new Message();
				String msg_content = "您订单号为：" + orderForm.getOrder_id()
						+ "的订单退款申请未通过。";
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(user);
				msg.setToUser(this.userService.selectByPrimaryKey(CommUtil
						.null2Long(orderForm.getUser_id())));
				this.messageService.saveEntity(msg);
			}
		}
		return "redirect:refund_apply_form?currentPage=" + currentPage;
	}
	
	/**
	 * 订单退款
	 * @param request
	 * @param response
	 * @param order_form_id
	 */
	@SecurityMapping(title = "订单退款", value = "/order_form_refund*", rtype = "seller", rname = "订单管理", rcode = "refund_seller", rgroup = "订单管理")
	@RequestMapping({ "/order_form_refund" })
	public void order_form_refund(HttpServletRequest request,
			HttpServletResponse response, String order_form_id) {
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_form_id));
		if (of.getOrder_status() == 11) {
			of.setOrder_status(21);
			this.orderFormService.updateById(of);
			RefundApplyForm refundApplyForm = new RefundApplyForm();
			refundApplyForm.setAddTime(new Date());
			refundApplyForm.setUser_id(CommUtil.null2Long(of.getUser_id()));
			refundApplyForm.setUser_name(of.getUser_name());
			refundApplyForm.setStatus(0);
			refundApplyForm.setOrder_id(of.getOrder_id());
			refundApplyForm.setOrder_form_id(of.getId());
			double wei_price = this.orderFormTools
					.query_order_pay_price(order_form_id);
			BigDecimal din = of.getTotalPrice().subtract(
					BigDecimal.valueOf(wei_price));
			refundApplyForm.setRefund_price(din);
			if (of.getOrder_form() == 0) {
				refundApplyForm.setStore_id(of.getStore_id());
				refundApplyForm.setStore_name(of.getStore_name());
			} else {
				refundApplyForm.setStore_id("self");
				refundApplyForm.setStore_name("自营");
			}
			this.refundapplyformService.saveEntity(refundApplyForm);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(order_form_id);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
