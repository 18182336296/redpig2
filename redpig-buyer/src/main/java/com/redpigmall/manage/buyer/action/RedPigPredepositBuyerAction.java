package com.redpigmall.manage.buyer.action;

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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.Predeposit;


/**
 * 
 * <p>
 * Title: RedPigPredepositBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 前端买家充值管理控制器，用来处理买家充值等
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
 * @date 2014-5-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigPredepositBuyerAction extends BaseAction{
	
	/**
	 * 会员充值
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "会员充值", value = "/buyer/predeposit*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/predeposit" })
	public ModelAndView predeposit(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_predeposit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if (this.configService.getSysConfig().getDeposit()) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("install", Boolean.valueOf(true));
			
			List<String> marks = Lists.newArrayList();
			marks.add("alipay_wap");
			marks.add("balance");
			marks.add("payafter");
			marks.add("alipay_app");
			marks.add("wx_app");
			marks.add("wx_pay");
			
			params.put("marks_no", marks);
			params.put("name", "-1");
			
			List<Payment> payments = this.paymentService.queryPageList(params);
			
			mv.addObject("payments", payments);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		}
		mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}
	
	/**
	 * 会员充值保存
	 * @param request
	 * @param response
	 * @param id
	 * @param pd_payment
	 * @return
	 */
	@SecurityMapping(title = "会员充值保存", value = "/buyer/predeposit_save*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/predeposit_save" })
	public ModelAndView predeposit_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String pd_payment) {
		ModelAndView mv = new RedPigJModelAndView("line_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().getDeposit()) {

			Predeposit obj = null;
			if (CommUtil.null2String(id).equals("")) {
				obj = WebForm.toPo(request, Predeposit.class);
				obj.setAddTime(new Date());
				if (pd_payment.equals("outline")) {
					obj.setPd_pay_status(1);
				} else {
					obj.setPd_pay_status(0);
				}
				
				obj.setPd_sn("pd"
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
						+ SecurityUserHolder.getCurrentUser().getId());
				
				obj.setPd_user(SecurityUserHolder.getCurrentUser());
				obj.setPd_payment(pd_payment);
				this.predepositService.saveEntity(obj);
			} else {
				Predeposit pre = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(id));
				obj = (Predeposit) WebForm.toPo(request, pre);
				this.predepositService.updateById(obj);
			}
			
			if (pd_payment.equals("outline")) {
				mv = new RedPigJModelAndView("success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				
				mv.addObject("op_title", "线下支付提交成功，等待审核");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list");
			} else {
				mv.addObject("payType", pd_payment);
				mv.addObject("type", "cash");
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", this.payTools);
				mv.addObject("cash_id", obj.getId());
				Map<String, Object> params = Maps.newHashMap();
				params.put("install", Boolean.valueOf(true));
				params.put("mark", obj.getPd_payment());
				
				List<Payment> payments = this.paymentService.queryPageList(params);
				
				mv.addObject("payment_id", payments.size() > 0 ? payments.get(0).getId() : new Payment());
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		}
		return mv;
	}
	
	/**
	 * 会员充值列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "会员充值列表", value = "/buyer/predeposit_list*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/predeposit_list" })
	public ModelAndView predeposit_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/predeposit_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
			
			maps.put("pd_user_id", SecurityUserHolder.getCurrentUser().getId());
			
			IPageList pList = this.predepositService.queryPagesWithNoRelations(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		}
		return mv;
	}
	
	/**
	 * 会员充值详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "会员充值详情", value = "/buyer/predeposit_view*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/predeposit_view" })
	public ModelAndView predeposit_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/predeposit_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			
			Predeposit obj = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (obj.getPd_user().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				
				mv.addObject("op_title", "参数错误，您没有该充值信息！");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/predeposit_list");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		}
		return mv;
	}
	
	/**
	 * 会员充值支付
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "会员充值支付", value = "/buyer/predeposit_pay*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/predeposit_pay" })
	public ModelAndView predeposit_pay(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/predeposit_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			Predeposit obj = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (obj.getPd_user().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv.addObject("obj", obj);
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，您没有该充值信息！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/predeposit_list");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		}
		return mv;
	}
	
	/**
	 * 会员收入明细
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "会员收入明细", value = "/buyer/predeposit_log*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/predeposit_log" })
	public ModelAndView predeposit_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_predeposit_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
			maps.put("pd_log_user_id",SecurityUserHolder.getCurrentUser().getId());
			
			IPageList pList = this.predepositLogService.queryPagesWithNoRelations(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId()));
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		}
		return mv;
	}
}
