package com.redpigmall.manage.buyer.action;

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

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.PredepositCash;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigPredepositCashBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:用户中心提现管理控制器，用户可以在这里申请提现，查看提现明细、提现列表
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
 * @date 2015-3-16
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigPredepositCashBuyerAction extends BaseAction{
	
	/**
	 * 提现管理
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "提现管理", value = "/buyer/buyer_cash*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/buyer_cash" })
	public ModelAndView buyer_cash(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_cash.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getDeposit()) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		} else {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			mv.addObject("user", user);
			mv.addObject("availableBalance", Double.valueOf(CommUtil
					.null2Double(user.getAvailableBalance())));
		}
		return mv;
	}
	
	/**
	 * 提现管理保存
	 * @param request
	 * @param response
	 * @param cash_payment
	 * @param cash_amount
	 * @param cash_userName
	 * @param cash_bank
	 * @param cash_account
	 * @param cash_info
	 */
	@SecurityMapping(title = "提现管理保存", value = "/buyer/buyer_cash_save*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/buyer_cash_save" })
	public void buyer_cash_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String cash_payment,
			String cash_amount, String cash_userName, String cash_bank,
			String cash_account, String cash_info) {
		int ret = 100;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		if (CommUtil.null2Double(cash_amount) <= CommUtil.null2Double(user.getAvailableBalance())) {
			BigDecimal freeBlance = user.getFreezeBlance();
			if (freeBlance == null) {
				freeBlance = BigDecimal.valueOf(0.0D);
			}
			
			user.setAvailableBalance(user.getAvailableBalance().subtract(
					BigDecimal.valueOf(CommUtil.null2Double(cash_amount))));
			user.setFreezeBlance(freeBlance.add(BigDecimal.valueOf(CommUtil
					.null2Double(cash_amount))));
			
			this.userService.updateById(user);
			
			PredepositCash obj = new PredepositCash();
			obj.setCash_payment(cash_payment);
			obj.setCash_account(cash_account);
			obj.setCash_userName(cash_userName);
			obj.setCash_bank(cash_bank);
			obj.setCash_amount(BigDecimal.valueOf(CommUtil.null2Double(cash_amount)));
			obj.setCash_info(cash_info);
			obj.setCash_sn("cash"
					+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
					+ SecurityUserHolder.getCurrentUser().getId());
			
			obj.setAddTime(new Date());
			obj.setCash_user(SecurityUserHolder.getCurrentUser());
			this.predepositCashService.saveEntity(obj);
			
			PredepositLog log = new PredepositLog();
			log.setAddTime(new Date());
			log.setPd_log_amount(obj.getCash_amount());
			log.setPd_log_info("申请提现，等待审核");
			log.setPd_log_user(obj.getCash_user());
			log.setPd_op_type("提现");
			log.setPd_type("可用预存款");
			this.predepositLogService.saveEntity(log);
		} else {
			ret = -100;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 提现管理
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "提现管理", value = "/buyer/buyer_cash_list*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/buyer_cash_list" })
	public ModelAndView buyer_cash_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_cash_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getDeposit()) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		} else {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
			
			maps.put("cash_user_id", SecurityUserHolder.getCurrentUser().getId());
			
			IPageList pList = this.predepositCashService.queryPagesWithNoRelations(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		return mv;
	}
	
	/**
	 * 会员提现详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "会员提现详情", value = "/buyer/buyer_cash_view*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/buyer_cash_view" })
	public ModelAndView buyer_cash_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_cash_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			PredepositCash obj = this.predepositCashService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
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
	 * 会员提现回调请求
	 * @param request
	 * @param response
	 * @param syntony
	 * @return
	 */
	@SecurityMapping(title = "会员提现回调请求", value = "/buyer/buyer_cash_syntony*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/buyer_cash_syntony" })
	public ModelAndView buyer_cash_syntony(HttpServletRequest request,
			HttpServletResponse response, String syntony) {
		ModelAndView mv = null;
		if ("error".equals(syntony)) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "提现金额大于余额");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/buyer_cash");
			return mv;
		}
		mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "提现申请成功");
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/buyer_cash_list");
		return mv;
	}
}
