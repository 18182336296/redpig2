package com.redpigmall.manage.admin.action.user;

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
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Predeposit;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.User;

/**
 * <p>
 * Title: RedPigPredepositManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台预存款管理控制器，用来显示预存款信息、审核预存款、修改预存款等所有系统预存款操作
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
 * @date 2014-5-26
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigPredepositManageAction extends BaseAction {
	/**
	 * 预存款列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param pd_payment
	 * @param pd_pay_status
	 * @param pd_status
	 * @param pd_userName
	 * @param beginTime
	 * @param endTime
	 * @param pd_remittance_user
	 * @param pd_remittance_bank
	 * @return
	 */
	@SecurityMapping(title = "预存款列表", value = "/predeposit_list*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_list" })
	public ModelAndView predeposit_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String pd_payment, String pd_pay_status,
			String pd_status, String pd_userName, String beginTime,
			String endTime, String pd_remittance_user, String pd_remittance_bank) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/predeposit_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			String url = this.configService.getSysConfig().getAddress();
			if ((url == null) || (url.equals(""))) {
				url = CommUtil.getURL(request);
			}
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
			if (!CommUtil.null2String(pd_payment).equals("")) {
				
				maps.put("pd_payment", pd_payment);
			}
			if (!CommUtil.null2String(pd_pay_status).equals("")) {
				
				maps.put("pd_pay_status", CommUtil.null2Int(pd_pay_status));
			}
			if (!CommUtil.null2String(pd_status).equals("")) {
				
				maps.put("pd_status", CommUtil.null2Int(pd_status));
			}
			if (!CommUtil.null2String(pd_remittance_user).equals("")) {
				
				maps.put("pd_remittance_user", pd_remittance_user);
			}
			if (!CommUtil.null2String(pd_remittance_bank).equals("")) {
				
				maps.put("pd_remittance_bank", pd_remittance_bank);
			}
			if (!CommUtil.null2String(pd_userName).equals("")) {
				
				maps.put("pd_user_userName", pd_userName);
			}
			if (!CommUtil.null2String(beginTime).equals("")) {
				
				maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			}
			if (!CommUtil.null2String(endTime).equals("")) {
				
				maps.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
			}
			IPageList pList = this.predepositService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("pd_payment", pd_payment);
			mv.addObject("pd_pay_status", pd_pay_status);
			mv.addObject("pd_status", pd_status);
			mv.addObject("pd_userName", pd_userName);
			mv.addObject("beginTime", beginTime);
			mv.addObject("endTime", endTime);
			mv.addObject("pd_remittance_user", pd_remittance_user);
			mv.addObject("pd_remittance_bank", pd_remittance_bank);
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
	 * 预存款列表
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "预存款列表", value = "/predeposit_list*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_view" })
	public ModelAndView predeposit_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/predeposit_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			Predeposit obj = this.predepositService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", obj);
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
	 * 预存款编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "预存款编辑", value = "/predeposit_edit*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_edit" })
	public ModelAndView predeposit_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/predeposit_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			if ((id != null) && (!id.equals(""))) {
				Predeposit predeposit = this.predepositService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				mv.addObject("obj", predeposit);
				mv.addObject("currentPage", currentPage);
				mv.addObject("edit", Boolean.valueOf(true));
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
	 * 预存款保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @return
	 */
	@SecurityMapping(title = "预存款保存", value = "/predeposit_save*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_save" })
	public ModelAndView predeposit_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			
			Predeposit obj = this.predepositService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			Predeposit predeposit = (Predeposit) WebForm.toPo(request, obj);
			predeposit.setPd_admin(SecurityUserHolder.getCurrentUser());
			this.predepositService.updateById(predeposit);
			if (predeposit.getPd_status() == 1) {
				User pd_user = predeposit.getPd_user();
				pd_user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						pd_user.getAvailableBalance(),
						predeposit.getPd_amount())));
				this.userService.updateById(pd_user);
			}
			PredepositLog log = obj.getLog();
			log.setPd_log_admin(SecurityUserHolder.getCurrentUser());
			this.predepositLogService.updateById(log);

			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "审核预存款成功");
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
	 * 预存款手动修改
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "预存款手动修改", value = "/predeposit_modify*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_modify" })
	public ModelAndView predeposit_modify(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/predeposit_modify.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getDeposit()) {
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
	 * 加载用户预存款信息
	 * @param request
	 * @param response
	 * @param userName
	 */
	@SecurityMapping(title = "加载用户预存款信息", value = "/predeposit_user*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_user" })
	public void predeposit_user(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("userName", userName);
		
		User user = this.userService.queryByProperty(params);
		Map<String, Object> map = Maps.newHashMap();
		if (user != null) {
			map.put("freezeBlance", Double.valueOf(CommUtil.null2Double(user
					.getFreezeBlance())));
			map.put("availableBalance", Double.valueOf(CommUtil
					.null2Double(user.getAvailableBalance())));
			map.put("id", user.getId());
			map.put("status", "success");
		} else {
			map.put("status", "error");
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
	
	/**
	 * 预存款手动修改保存
	 * @param request
	 * @param response
	 * @param user_id
	 * @param amount
	 * @param type
	 * @param info
	 * @param list_url
	 * @param refund_user_id
	 * @param obj_id
	 * @param gi_id
	 * @return
	 */
	@SecurityMapping(title = "预存款手动修改保存", value = "/predeposit_modify_save*", rtype = "admin", rname = "预存款管理", rcode = "predeposit", rgroup = "会员")
	@RequestMapping({ "/predeposit_modify_save" })
	public ModelAndView predeposit_modify_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String user_id, String amount,
			String type, String info, String list_url, String refund_user_id,
			String obj_id, String gi_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			User user = null;
			if ((user_id != null) && (!user_id.equals(""))) {
				user = this.userService.selectByPrimaryKey(CommUtil.null2Long(user_id));
			} else {
				user = this.userService.selectByPrimaryKey(CommUtil.null2Long(refund_user_id));
			}
			if (type.equals("free")) {
				user.setFreezeBlance(BigDecimal.valueOf(CommUtil.add(user.getFreezeBlance(), amount)));
			} else {
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), amount)));
			}
			this.userService.updateById(user);

			PredepositLog log = new PredepositLog();
			log.setPd_log_admin(SecurityUserHolder.getCurrentUser());
			log.setAddTime(new Date());
			log.setPd_log_amount(BigDecimal.valueOf(CommUtil
					.null2Double(amount)));
			log.setPd_log_info(info);
			log.setPd_log_user(user);
			log.setPd_op_type("手动修改");
			if (type.equals("free")) {
				log.setPd_type("冻结预存款");
			} else {
				log.setPd_type("可用预存款");
			}
			this.predepositLogService.saveEntity(log);
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "审核预存款成功");
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
