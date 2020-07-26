package com.redpigmall.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.RechargeCard;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigRechargeCardBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 用户中心充值卡管理控制器，用来列表显示充值卡使用记录，使用充值卡充值等等充值操作
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
public class RedPigRechargeCardBuyerAction extends BaseAction{
	
	/**
	 * 充值卡使用记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "充值卡使用记录", value = "/buyer/recharge_card*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/recharge_card" })
	public ModelAndView recharge_card(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/recharge_card.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = SecurityUserHolder.getCurrentUser();
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, null, null);
		maps.put("rc_user_id", user.getId());
		
		IPageList pList = this.rechargeCardService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 充值卡充值
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "充值卡充值", value = "/buyer/recharge_card_add*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/recharge_card_add" })
	public ModelAndView recharge_card_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/recharge_card_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
	
	/**
	 * 充值卡验证
	 * @param request
	 * @param response
	 * @param card_sn
	 */
	@SecurityMapping(title = "充值卡验证", value = "/buyer/recharge_card_validate*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/recharge_card_validate" })
	public void recharge_card_validate(HttpServletRequest request,
			HttpServletResponse response, String card_sn) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("rc_sn", card_sn);
		params.put("rc_status_less_than", Integer.valueOf(2));
		
		List<RechargeCard> rcs = this.rechargeCardService.queryPageList(params);
		
		boolean ret = false;
		if (rcs.size() > 0) {
			ret = true;
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
	 * 充值卡充值
	 * @param request
	 * @param response
	 * @param card_sn
	 */
	@SecurityMapping(title = "充值卡充值", value = "/buyer/recharge_card_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/recharge_card_save" })
	public void recharge_card_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String card_sn) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("rc_sn", card_sn);
		
		List<RechargeCard> rcs = this.rechargeCardService.queryPageList(params);
		
		boolean ret = false;
		if (rcs.size() > 0) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			RechargeCard rc = (RechargeCard) rcs.get(0);
			if (rc.getRc_status() == 0) {
				user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(
						user.getAvailableBalance(), rc.getRc_amount())));
				this.userService.updateById(user);
				boolean flag = true;
				
				if (flag) {
					rc.setRc_status(2);
					rc.setRc_time(new Date());
					rc.setRc_user_id(user.getId());
					rc.setRc_user_name(user.getUserName());
					rc.setRc_info(CommUtil.formatLongDate(new Date()) + "  "
							+ user.getUserName() + "充值使用");
					rc.setRc_ip(CommUtil.getIpAddr(request));
					this.rechargeCardService.updateById(rc);
					ret = true;
					
					PredepositLog log = new PredepositLog();
					log.setAddTime(new Date());
					log.setPd_log_info("充值卡充值增加可用预存款" + rc.getRc_amount() + "元");
					log.setPd_log_user(user);
					log.setPd_type("可用预存款");
					log.setPd_op_type("充值");
					log.setPd_log_amount(rc.getRc_amount());
					this.predepositLogService.saveEntity(log);
				}
			}
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
}
