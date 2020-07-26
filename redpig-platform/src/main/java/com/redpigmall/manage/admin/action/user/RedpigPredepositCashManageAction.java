package com.redpigmall.manage.admin.action.user;

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
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.PredepositCash;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedpigPredepositCashManageAction.java
 * </p>
 * 
 * <p>
 * Description:商城用户提现管理控制器，用来显示用户提现列表、处理用户提现申请、查询提现详情
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
 * @date 2014-5-30
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Controller
public class RedpigPredepositCashManageAction extends BaseAction {
	
	/**
	 * 提现申请列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param q_pd_userName
	 * @param q_beginTime
	 * @param q_endTime
	 * @param q_cash_payment
	 * @param q_cash_pay_status
	 * @param q_cash_status
	 * @param q_cash_userName
	 * @param q_cash_remittance_user
	 * @param q_cash_remittance_bank
	 * @return
	 */
	@SecurityMapping(title = "提现申请列表", value = "/predeposit_cash*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_cash" })
	public ModelAndView predeposit_cash(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String q_pd_userName, String q_beginTime,
			String q_endTime, String q_cash_payment, String q_cash_pay_status,
			String q_cash_status, String q_cash_userName,
			String q_cash_remittance_user, String q_cash_remittance_bank) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/predeposit_cash.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			String url = this.configService.getSysConfig().getAddress();
			if ((url == null) || (url.equals(""))) {
				url = CommUtil.getURL(request);
			}
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
			if (!CommUtil.null2String(q_cash_payment).equals("")) {
				maps.put("cash_payment", q_cash_payment);
			}
			
			if (!CommUtil.null2String(q_cash_userName).equals("")) {
				maps.put("cash_user_userName_like", q_cash_userName);
			}
			
			if (!CommUtil.null2String(q_cash_status).equals("")) {
				maps.put("cash_status", CommUtil.null2Int(q_cash_status));
			}
			
			if (!CommUtil.null2String(q_cash_remittance_user).equals("")) {
				maps.put("cash_user_userName", q_cash_remittance_user);
			}
			
			if (!CommUtil.null2String(q_cash_remittance_bank).equals("")) {
				maps.put("cash_bank", q_cash_remittance_bank);
			}
			
			if (!CommUtil.null2String(q_cash_pay_status).equals("")) {
				if ("2".equals(q_cash_pay_status)) {
					
					maps.put("cash_status", CommUtil.null2Int(Integer.valueOf(-1)));
				} else {
					maps.put("cash_pay_status", CommUtil.null2Int(q_cash_pay_status));
					
					maps.put("cash_status_no", -1);
				}
			}
			if (!CommUtil.null2String(q_beginTime).equals("")) {
				maps.put("add_Time_more_than_equal", CommUtil.formatDate(q_beginTime));
			}
			
			if (!CommUtil.null2String(q_endTime).equals("")) {
				maps.put("add_Time_less_than_equal", CommUtil.formatDate(q_endTime));
			}
			
			IPageList pList = this.predepositcashService.queryPagesWithNoRelations(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("q_cash_payment", q_cash_payment);
			mv.addObject("q_cash_pay_status", q_cash_pay_status);
			mv.addObject("q_cash_userName", q_cash_userName);
			mv.addObject("q_cash_status", q_cash_status);
			mv.addObject("q_cash_remittance_user", q_cash_remittance_user);
			mv.addObject("q_cash_remittance_bank", q_cash_remittance_bank);
			mv.addObject("q_beginTime", q_beginTime);
			mv.addObject("q_endTime", q_endTime);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 提现申请编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "提现申请编辑", value = "/predeposit_cash_edit*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_cash_edit" })
	public ModelAndView predeposit_cash_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/predeposit_cash_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			if ((id != null) && (!id.equals(""))) {
				PredepositCash predepositcash = this.predepositcashService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				mv.addObject("obj", predepositcash);
				mv.addObject("currentPage", currentPage);
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 提现申请编辑保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @return
	 */
	@SecurityMapping(title = "提现申请编辑保存", value = "/predeposit_cash_save*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_cash_save" })
	public ModelAndView predeposit_cash_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PredepositCash obj = this.predepositcashService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		
		PredepositLog log = new PredepositLog();
		log.setAddTime(new Date());
		log.setPd_op_type("提现返款");
		log.setPd_type("可用预存款");
		
		Message msg = new Message();
		msg.setAddTime(new Date());
		msg.setFromUser(SecurityUserHolder.getCurrentUser());
		msg.setToUser(obj.getCash_user());
		if (this.configService.getSysConfig().getDeposit()) {
			

			PredepositCash predepositcash = (PredepositCash) WebForm.toPo(
					request, obj);
			User user = obj.getCash_user();
			if ((obj.getCash_pay_status() == 1) && (obj.getCash_status() == 1)) {
				user.setFreezeBlance(BigDecimal.valueOf(CommUtil.subtract(
						user.getFreezeBlance(), predepositcash.getCash_amount())));
				
				log.setPd_log_amount(obj.getCash_amount());
				log.setPd_log_info("该笔提现申请，已支付");
				log.setPd_log_user(obj.getCash_user());
				
				
				msg.setContent("该笔提现申请，已支付");
			}
			if ((obj.getCash_pay_status() == 0) && (obj.getCash_status() == 1)) {
				log.setPd_log_amount(obj.getCash_amount());
				log.setPd_log_info("该笔提现申请，已审核成功，等待支付");
				log.setPd_log_user(obj.getCash_user());
				
				msg.setContent("该笔提现申请，已审核成功，等待支付");
			}
			if ((obj.getCash_pay_status() == 0) && (obj.getCash_status() == -1)) {
				user.setFreezeBlance(BigDecimal.valueOf(CommUtil.subtract(
						user.getFreezeBlance(), predepositcash.getCash_amount())));
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						user.getAvailableBalance(),
						predepositcash.getCash_amount())));
				msg.setContent("该笔提现申请，被拒绝");

				log.setAddTime(new Date());
				log.setPd_log_amount(obj.getCash_amount());
				log.setPd_log_info("申请提现失败，返还提现金额到预存款");
				log.setPd_log_user(obj.getCash_user());
				log.setPd_op_type("提现返款");
				log.setPd_type("可用预存款");
				this.predepositLogService.saveEntity(log);
			}
			
			if ((obj.getCash_pay_status() != 0) || (obj.getCash_status() != 0)) {
				this.messageService.saveEntity(msg);
			}
			obj.setCash_admin(SecurityUserHolder.getCurrentUser());
			this.predepositcashService.updateById(predepositcash);
			this.userService.updateById(user);
			this.predepositlogService.saveEntity(log);
			
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "提现状态修改完毕");
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 提现申请详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "提现申请详情", value = "/predeposit_cash_view*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_cash_view" })
	public ModelAndView predeposit_cash_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/predeposit_cash_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			if ((id != null) && (!id.equals(""))) {
				PredepositCash predepositcash = this.predepositcashService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				mv.addObject("obj", predepositcash);
				mv.addObject("currentPage", currentPage);
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
}
