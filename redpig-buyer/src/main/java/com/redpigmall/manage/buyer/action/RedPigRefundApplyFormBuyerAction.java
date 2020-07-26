package com.redpigmall.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.RefundApplyForm;
import com.redpigmall.domain.User;
/**
 * 
 * <p>
 * Title: RedPigRefundApplyFormBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 退款申请管理
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
 * @date 2017-04-16
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigRefundApplyFormBuyerAction extends BaseAction{
	
	/**
	 * 订单退款
	 * @param request
	 * @param response
	 * @param order_form_id
	 */
	@SecurityMapping(title = "订单退款", value = "/buyer/order_form_refund*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_form_refund" })
	public void order_form_refund(HttpServletRequest request,
			HttpServletResponse response, String order_form_id) {
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_form_id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		if ((of.getOrder_status() == 20)
				&& (of.getUser_id().equals(user.getId().toString()))) {
			of.setOrder_status(21);
			this.orderFormService.updateById(of);
			RefundApplyForm refundApplyForm = new RefundApplyForm();
			refundApplyForm.setAddTime(new Date());
			refundApplyForm.setUser_id(user.getId());
			refundApplyForm.setUser_name(user.getUserName());
			refundApplyForm.setStatus(0);
			refundApplyForm.setOrder_id(of.getOrder_id());
			refundApplyForm.setOrder_form_id(of.getId());
			refundApplyForm.setRefund_price(of.getTotalPrice());
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
	
	/**
	 * 订单退款
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "订单退款", value = "/buyer/refund_apply_form*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/refund_apply_form" })
	public ModelAndView refund_apply_form(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/refund_apply_form.html",
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
        
		IPageList pList = this.refundapplyformService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/refund_apply_form.html", "", params, pList, mv);
		return mv;
	}
}
