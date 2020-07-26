package com.redpigmall.manage.seller.action;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.GoldRecord;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoldSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家金币控制器
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
 * @date 2014年11月12日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigGoldSellerAction extends BaseAction {
	/**
	 * 金币兑换
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "金币兑换", value = "/gold_record*", rtype = "seller", rname = "金币管理", rcode = "gold_seller", rgroup = "其他管理")
	@RequestMapping({ "/gold_record" })
	public ModelAndView gold_record(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/gold_record.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if (!this.configService.getSysConfig().getGold()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "商城未开启金币功能");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			Map<String, Object> params = Maps.newHashMap();
			Set<String> marks = Sets.newTreeSet();
			marks.add("alipay_wap");
			marks.add("alipay_app");
			marks.add("wx_pay");
			marks.add("wx_app");
			params.put("install", Boolean.valueOf(true));
			params.put("marks", marks);
			
			List<Payment> payments = this.paymentService.queryPageList(params);
			
			String gold_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("gold_session", gold_session);
			mv.addObject("gold_session", gold_session);
			mv.addObject("payments", payments);
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			mv.addObject("balance", Float.valueOf(CommUtil.null2Float(user.getAvailableBalance())));
		}
		return mv;
	}
	
	/**
	 * 金币兑换保存
	 * @param request
	 * @param response
	 * @param id
	 * @param gold_payment
	 * @param gold_exchange_info
	 * @param gold_session
	 * @return
	 */
	@SecurityMapping(title = "金币兑换保存", value = "/buyer/gold_record_save*", rtype = "seller", rname = "金币管理", rcode = "gold_seller", rgroup = "其他管理")
	@RequestMapping({ "/gold_record_save" })
	public ModelAndView gold_record_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String gold_payment,
			String gold_exchange_info, String gold_session) {
		ModelAndView mv = new RedPigJModelAndView("line_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().getGold()) {
			String gold_session1 = CommUtil.null2String(request.getSession(
					false).getAttribute("gold_session"));
			if ((!gold_session1.equals(""))
					&& (gold_session1.equals(gold_session))) {
				request.getSession(false).removeAttribute("gold_session");
				
				GoldRecord obj = null;
				if (CommUtil.null2String(id).equals("")) {
					obj = (GoldRecord) WebForm.toPo(request, GoldRecord.class);
					obj.setAddTime(new Date());
					obj.setGold_pay_status(0);
					User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					obj.setGold_sn("gold"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ user.getId());
					obj.setGold_user(user);
					obj.setGold_count(obj.getGold_money()
							* this.configService.getSysConfig()
									.getGoldMarketValue());
					this.goldRecordService.saveEntity(obj);
				} else {
					GoldRecord gr = this.goldRecordService.selectByPrimaryKey(CommUtil.null2Long(id));
					obj = (GoldRecord) WebForm.toPo(request, gr);
					this.goldRecordService.updateById(obj);
				}
				if (gold_payment.equals("balance")) {
					User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					double balance = CommUtil.null2Double(user
							.getAvailableBalance());
					if (balance > obj.getGold_money()) {
						user.setGold(user.getGold() + obj.getGold_count());
						user.setAvailableBalance(BigDecimal.valueOf(CommUtil
								.subtract(user.getAvailableBalance(),
										Integer.valueOf(obj.getGold_money()))));
						this.userService.updateById(user);

						obj.setGold_pay_status(2);
						obj.setGold_status(1);
						this.goldRecordService.updateById(obj);

						PredepositLog pre_log = new PredepositLog();
						pre_log.setAddTime(new Date());
						pre_log.setPd_log_user(user);
						pre_log.setPd_op_type("兑换金币");
						pre_log.setPd_log_amount(BigDecimal.valueOf(-obj
								.getGold_money()));
						pre_log.setPd_log_info("兑换金币减少可用预存款");
						pre_log.setPd_type("可用预存款");
						this.predepositLogService.saveEntity(pre_log);

						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_payment(obj.getGold_payment());
						log.setGl_content("预存款支付");
						log.setGl_money(obj.getGold_money());
						log.setGl_count(obj.getGold_count());
						log.setGl_type(0);
						log.setGl_user(obj.getGold_user());
						log.setGr(obj);
						this.goldLogService.saveEntity(log);
						mv = new RedPigJModelAndView(
								"user/default/sellercenter/seller_success.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 0,
								request, response);
						mv.addObject("op_title", "金币兑换成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/gold_record_list");
					} else {
						mv = new RedPigJModelAndView(
								"user/default/sellercenter/seller_error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 0,
								request, response);
						mv.addObject("op_title", "预存款金额不足");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/gold_record");
					}
				} else {
					mv.addObject("payType", gold_payment);
					mv.addObject("type", "gold");
					mv.addObject("url", CommUtil.getURL(request));
					mv.addObject("payTools", this.payTools);
					mv.addObject("gold_id", obj.getId());
					Map<String, Object> params = Maps.newHashMap();
					params.put("install", Boolean.valueOf(true));
					params.put("mark", obj.getGold_payment());
					
					List<Payment> payments = this.paymentService.queryPageList(params);
					
					mv.addObject(
							"payment_id",
							payments.size() > 0 ? ((Payment) payments.get(0))
									.getId() : new Payment());
				}
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您已经提交过该请求");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/gold_record_list");
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 金币兑换
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "金币兑换", value = "/gold_record_list*", rtype = "seller", rname = "金币管理", rcode = "gold_seller", rgroup = "其他管理")
	@RequestMapping({ "/gold_record_list" })
	public ModelAndView gold_record_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/gold_record_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getGold()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
			maps.put("gold_user_id", user.getId());
			
			IPageList pList = this.goldRecordService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		return mv;
	}
	
	/**
	 * 金币兑换支付
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "金币兑换支付", value = "/gold_pay*", rtype = "seller", rname = "金币管理", rcode = "gold_seller", rgroup = "其他管理")
	@RequestMapping({ "/gold_pay" })
	public ModelAndView gold_pay(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/gold_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if (this.configService.getSysConfig().getGold()) {
			GoldRecord obj = this.goldRecordService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (obj.getGold_user().getId().equals(user.getId())) {
				String gold_session = CommUtil.randomString(32);
				request.getSession(false).setAttribute("gold_session",
						gold_session);
				mv.addObject("gold_session", gold_session);
				mv.addObject("obj", obj);
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "参数错误，您没有该兑换信息");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/gold_record_list");
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 金币兑换详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "金币兑换详情", value = "/gold_view*", rtype = "seller", rname = "金币管理", rcode = "gold_seller", rgroup = "其他管理")
	@RequestMapping({ "/gold_view" })
	public ModelAndView gold_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/gold_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getGold()) {
			GoldRecord obj = this.goldRecordService.selectByPrimaryKey(CommUtil.null2Long(id));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (obj.getGold_user().getId().equals(user.getId())) {
				mv.addObject("obj", obj);
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "参数错误，您没有该兑换信息");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/gold_record_list");
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 兑换日志
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param endTime
	 * @param beginTime
	 * @param day
	 * @param word
	 * @return
	 */
	@SecurityMapping(title = "兑换日志", value = "/gold_log*", rtype = "seller", rname = "金币管理", rcode = "gold_seller", rgroup = "其他管理")
	@RequestMapping({ "/gold_log" })
	public ModelAndView gold_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String endTime,
			String beginTime, String day, String word) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/gold_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!this.configService.getSysConfig().getGold()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启金币");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
			
			if (!CommUtil.null2String(day).equals("")) {
				Date etime = new Date();
				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(etime);
				if (day.equals("30")) {
					rightNow.add(2, -1);
				} else {
					rightNow.add(2, -3);
				}
				Date btime = rightNow.getTime();
				
				maps.put("add_Time_more_than_equal", btime);
				maps.put("add_Time_less_than_equal", etime);
				
				mv.addObject("endTime", CommUtil.formatShortDate(etime));
				mv.addObject("beginTime", CommUtil.formatShortDate(btime));
				mv.addObject("day", day);
			} else {
				if (CommUtil.null2String(beginTime) != "") {
					maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
					mv.addObject("beginTime", beginTime);
				}
				if (CommUtil.null2String(endTime) != "") {
					maps.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
					mv.addObject("endTime", endTime);
				}
			}
			if (!CommUtil.null2String(word).equals("")) {
				maps.put("gl_content_like", word);
				mv.addObject("word", word);
			}
			maps.put("gl_user_id", user.getId());
			IPageList pList = this.goldLogService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("user", user);
		}
		return mv;
	}
}
