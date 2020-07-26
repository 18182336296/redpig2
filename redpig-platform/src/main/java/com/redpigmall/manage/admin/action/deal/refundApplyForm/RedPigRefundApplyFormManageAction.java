package com.redpigmall.manage.admin.action.deal.refundApplyForm;

import java.math.BigDecimal;
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
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.RefundApplyForm;
import com.redpigmall.domain.RefundLog;
import com.redpigmall.domain.User;

/**
 * 
 * 
 * <p>
 * Title: RedPigRefundApplyFormManageAction.java
 * </p>
 * 
 * <p>
 * Description: 订单退款管理
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
 * @date 2014年5月14日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigRefundApplyFormManageAction extends BaseAction{
	
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
	@SecurityMapping(title = "订单退款申请单", value = "/refund_apply_form*", rtype = "admin", rname = "订单退款", rcode = "refund_admin_manage", rgroup = "交易")
	@RequestMapping({ "/refund_apply_form" })
	public ModelAndView refund_apply_form(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/refund_apply_form.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if ((status == null) || (status.equals(""))) {
			maps.put("status", 10);	
		} else if (status.equals("all")) {
			mv.addObject("status", status);
			maps.put("status", status);	
		} else {
			maps.put("status", status);
			mv.addObject("status", status);
		}
		IPageList pList = this.refundapplyformService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/refund_apply_form.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 订单退款
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "订单退款", value = "/order_refund_view*", rtype = "admin", rname = "订单退款", rcode = "refund_admin_manage", rgroup = "交易")
	@RequestMapping({ "/order_refund_view" })
	public ModelAndView order_refund_view(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/order_refund_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		RefundApplyForm refundApplyForm = this.refundapplyformService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (refundApplyForm.getStatus() == 10) {
			mv.addObject("user_name", refundApplyForm.getUser_name());
			mv.addObject("refund_price", refundApplyForm.getRefund_price());
			mv.addObject("raf_id", refundApplyForm.getId());
			mv.addObject("msg", "订单号为" + refundApplyForm.getOrder_id()
					+ "的订单已成功退款，预存款" + refundApplyForm.getRefund_price()
					+ "元已存入您的账户。");
		}
		mv.addObject("currentPage", currentPage);
		String cart_session = CommUtil.randomString(32);
		request.getSession(false).setAttribute("cart_session", cart_session);
		mv.addObject("cart_session", cart_session);
		return mv;
	}
	
	/**
	 * 确认退款
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param refund_price
	 * @param info
	 * @param raf_id
	 * @param cart_session
	 * @return
	 */
	@SecurityMapping(title = "确认退款", value = "/order_refund_finish*", rtype = "admin", rname = "订单退款", rcode = "refund_admin_manage", rgroup = "交易")
	@RequestMapping({ "/order_refund_finish" })
	public ModelAndView order_refund_finish(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String refund_price, String info, String raf_id, String cart_session) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
		if (cart_session.equals(cart_session1)) {
			request.getSession(false).removeAttribute("cart_session");
			RefundApplyForm refundApplyForm = this.refundapplyformService.selectByPrimaryKey(CommUtil.null2Long(raf_id));
			if ((this.configService.getSysConfig().getDeposit())
					&& (refundApplyForm.getStatus() == 10)) {
				OrderForm of = this.orderFormService.selectByPrimaryKey(refundApplyForm.getOrder_form_id());
				Boolean flag = Boolean.valueOf(false);
				if (of != null) {
					of.setOrder_status(25);
					this.orderFormService.updateById(of);
					flag = true;
				}
				if (flag.booleanValue()) {
					refundApplyForm.setStatus(15);
				}
				this.refundapplyformService.updateById(refundApplyForm);
				User user = this.userService.selectByPrimaryKey(refundApplyForm.getUser_id());
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), refund_price)));
				this.userService.updateById(user);
				
				PredepositLog log = new PredepositLog();
				log.setPd_log_admin(SecurityUserHolder.getCurrentUser());
				log.setAddTime(new Date());
				log.setPd_log_amount(BigDecimal.valueOf(CommUtil.null2Double(refund_price)));
				log.setPd_log_info(info);
				log.setPd_log_user(user);
				log.setPd_op_type("人工退款");
				log.setPd_type("可用预存款");
				this.predepositLogService.saveEntity(log);
				
				/**-------------------------------直销佣金提成Start------------------------------------------*/
				User directSellingFirstParent = user.getDirectSellingParent();
				User directSellingSecondParent = null;
				if(directSellingFirstParent!=null) {
					directSellingSecondParent = directSellingFirstParent.getDirectSellingParent();
				}
				//一级获得佣金
				if(directSellingFirstParent != null) {
					directSellingFirstParent = this.userService.selectByPrimaryKey(directSellingFirstParent.getId());
					
					BigDecimal availableBalance = directSellingFirstParent.getAvailableBalance();
					//第一级佣金:原来余额+订单总金额*佣金比例
					
					availableBalance = availableBalance.subtract(
							BigDecimal.valueOf(
									CommUtil.mul(refund_price, this.configService.getSysConfig().getDirect_selling_first_level_rate())));
					
					directSellingFirstParent.setAvailableBalance(availableBalance);
					this.userService.updateById(directSellingFirstParent);
					
					log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(directSellingFirstParent);
					log.setPd_op_type("佣金扣除");//佣金：订单总金额*佣金比例
					log.setPd_log_amount(
							BigDecimal.valueOf(-
									CommUtil.mul(refund_price, this.configService.getSysConfig().getDirect_selling_first_level_rate())));
					log.setPd_log_info("买家:"+user.getUserName()+"退单:"+raf_id + " 佣金退款");
					log.setPd_type("可用预存款");
					this.predepositlogService.saveEntity(log);
				}
				
				//二级获得佣金
				if(directSellingSecondParent != null) {
					directSellingSecondParent = this.userService.selectByPrimaryKey(directSellingSecondParent.getId());
					
					BigDecimal availableBalance = directSellingSecondParent.getAvailableBalance();
					//第二级佣金:原来余额+订单总金额*佣金比例
					
					availableBalance = availableBalance.subtract(
							BigDecimal.valueOf(
									CommUtil.mul(refund_price, this.configService.getSysConfig().getDirect_selling_second_level_rate())));
					
					directSellingSecondParent.setAvailableBalance(availableBalance);
					this.userService.updateById(directSellingSecondParent);
					
					log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_user(directSellingSecondParent);
					log.setPd_op_type("获得佣金");//佣金：订单总金额*佣金比例
					log.setPd_log_amount(
							BigDecimal.valueOf(
									CommUtil.mul(refund_price, this.configService.getSysConfig().getDirect_selling_second_level_rate())));
					log.setPd_log_info("买家:"+user.getUserName()+"退单:"+raf_id + " 佣金退款");
					log.setPd_type("可用预存款");
					this.predepositlogService.saveEntity(log);
				}
				
				/**------------------------------直销佣金提成End-------------------------------------------*/
				
				
				RefundLog r_log = new RefundLog();
				r_log.setAddTime(new Date());
				r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss",new Date()) + user.getId());
				r_log.setReturnLog_id(refundApplyForm.getOrder_form_id());
				r_log.setReturnService_id(refundApplyForm.getOrder_id());
				r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(refund_price)));
				r_log.setRefund_log(info);
				r_log.setRefund_type("预存款");
				r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
				r_log.setReturnLog_userName(refundApplyForm.getUser_name());
				r_log.setReturnLog_userId(refundApplyForm.getUser_id());
				this.refundLogService.saveEntity(r_log);
				
				String msg_content = "成功为订单号：" + refundApplyForm.getOrder_id() + "退款" + refund_price + "元，请到收支明细中查看。";
				
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(user);
				this.messageService.saveEntity(msg);

				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/refund_apply_form?currentPage=" + currentPage);
			} else {
				mv = new RedPigJModelAndView("admin/blue/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "系统未开启预存款");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/refund_apply_form?currentPage=" + currentPage);
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "请勿重复提交");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/refund_apply_form?currentPage=" + currentPage);
		}
		return mv;
	}
}
