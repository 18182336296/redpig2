package com.redpigmall.manage.admin.action.self.refundApplyForm;

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
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.RefundApplyForm;
import com.redpigmall.domain.User;

/**
 * <p>
 * Title: RedPigSelfRefundApplyFormManageAction.java
 * </p>
 * 
 * <p>
 * Description: 订单退款
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
 * @date 2014年5月12日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSelfRefundApplyFormManageAction extends BaseAction {
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
	@SecurityMapping(title = "订单退款申请单", value = "/self_refund_apply_form*", rtype = "admin", rname = "订单退款", rcode = "self_refund", rgroup = "自营")
	@RequestMapping({ "/self_refund_apply_form" })
	public ModelAndView self_refund_apply_form(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_refund_apply_form.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		if ((status == null) || (status.equals(""))) {
			
			maps.put("status", 0);
		} else if (status.equals("all")) {
			mv.addObject("status", status);
		} else {
			maps.put("status", CommUtil.null2Int(status));
			
			mv.addObject("status", status);
		}
		
		maps.put("store_id", "self");
		IPageList pList = this.refundapplyformService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/self_refund_apply_form.html", "", params, pList, mv);
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
	@SecurityMapping(title = "订单退款申请单审核", value = "/refund_audit*", rtype = "admin", rname = "订单退款", rcode = "self_refund", rgroup = "自营")
	@RequestMapping({ "/refund_audit" })
	public String refund_audit(HttpServletRequest request, String result,
			String apply_form_id, String currentPage) {
		RefundApplyForm refundApplyForm = this.refundapplyformService
				.selectByPrimaryKey(CommUtil.null2Long(apply_form_id));
		if ((refundApplyForm != null)
				&& ("self".equals(refundApplyForm.getStore_id()))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			OrderForm orderForm = this.orderFormService
					.selectByPrimaryKey(refundApplyForm.getOrder_form_id());
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
		return "redirect:self_refund_apply_form?currentPage=" + currentPage;
	}
	
	/**
	 * 订单退款申请单详情
	 * @param request
	 * @param response
	 * @param order_id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "订单退款申请单详情", value = "/refund_orderform_view*", rtype = "admin", rname = "订单退款", rcode = "self_refund", rgroup = "自营")
	@RequestMapping({ "/refund_orderform_view" })
	public ModelAndView refund_orderform_view(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/refund_orderform_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		mv.addObject("obj", order);
		if (order != null) {
			String temp = order.getSpecial_invoice();
			if ((temp != null) && (!"".equals(temp))) {
				Map of_va = JSON.parseObject(temp);
				mv.addObject("of_va", of_va);
			}
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("express_company_name", this.orderFormTools.queryExInfo(
				order.getExpress_info(), "express_company_name"));
		return mv;
	}
	
	/**
	 * 预售商品订单退款
	 * @param request
	 * @param response
	 * @param order_form_id
	 */
	@SecurityMapping(title = "预售商品订单退款", value = "/order_form_refund*", rtype = "admin", rname = "订单管理", rcode = "self_refund", rgroup = "自营")
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
