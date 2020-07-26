package com.redpigmall.manage.admin.action.payOff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.PayoffLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigPayoffLogManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统结算管理类,只要设置一次每月结算次数，系统根据次数自动计算每月的结算日期，
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
@SuppressWarnings("deprecation")
@Controller
public class RedPigPayoffLogManageAction extends BaseAction {
	
	/**
	 * 结算设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "结算设置", value = "/payofflog_set*", rtype = "admin", rname = "结算设置", rcode = "admin_payoff_set", rgroup = "结算")
	@RequestMapping({ "/payofflog_set" })
	public ModelAndView payofflog_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/payofflog_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Calendar a = Calendar.getInstance();
		a.set(5, 1);
		a.roll(5, -1);
		int maxDate = a.get(5);
		List list = Lists.newArrayList();
		for (int i = 1; i <= maxDate; i++) {
			list.add(Integer.valueOf(i));
		}
		SysConfig obj = this.configService.getSysConfig();
		String select = getSelectedDate(obj.getPayoff_count());
		String[] str = select.split(",");
		String ms = "";
		for (int i = 0; i < str.length; i++) {
			if (i + 1 == str.length) {
				ms = ms + str[i] + "日";
			} else {
				ms = ms + str[i] + "日、";
			}
		}
		mv.addObject("ms",
				"今天是" + DateFormat.getDateInstance(0).format(new Date())
						+ "，本月的结算日期为" + ms + "，请于结算日申请结算。");
		mv.addObject("now", DateFormat.getDateInstance(0).format(new Date()));
		mv.addObject("list", list);
		return mv;
	}
	
	/**
	 * 结算设置切换月结算次数
	 * @param request
	 * @param response
	 * @param payoff_count
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "结算设置切换月结算次数", value = "/payofflog_set_ajax*", rtype = "admin", rname = "结算设置", rcode = "admin_payoff_set", rgroup = "结算")
	@RequestMapping({ "/payofflog_set_ajax" })
	public void payofflog_set_ajax(HttpServletRequest request,
			HttpServletResponse response, String payoff_count)
			throws ClassNotFoundException {
		String selected = getSelectedDate(CommUtil.null2Int(payoff_count));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(selected);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getSelectedDate(int payoff_count) {
		Calendar a = Calendar.getInstance();
		a.set(5, 1);
		a.roll(5, -1);
		int allDate = a.get(5);
		String selected = "";
		if (payoff_count == 1) {
			selected = CommUtil.null2String(Integer.valueOf(allDate));
		} else if (payoff_count == 2) {
			if (allDate == 31) {
				selected = "15,31";
			}
			if (allDate == 30) {
				selected = "15,30";
			}
			if (allDate == 29) {
				selected = "14,29";
			}
			if (allDate == 28) {
				selected = "14,28";
			}
		} else if (payoff_count == 3) {
			if (allDate == 31) {
				selected = "10,20,31";
			}
			if (allDate == 30) {
				selected = "10,20,30";
			}
			if (allDate == 29) {
				selected = "10,20,29";
			}
			if (allDate == 28) {
				selected = "10,20,28";
			}
		} else if (payoff_count == 4) {
			if (allDate == 31) {
				selected = "7,14,21,31";
			}
			if (allDate == 30) {
				selected = "7,14,21,30";
			}
			if (allDate == 29) {
				selected = "7,14,21,29";
			}
			if (allDate == 28) {
				selected = "7,14,21,28";
			}
		}
		return selected;
	}

	/**
	 * 结算设置保存
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "结算设置保存", value = "/payofflog_set_save*", rtype = "admin", rname = "结算设置", rcode = "admin_payoff_set", rgroup = "结算")
	@RequestMapping({ "/payofflog_set_save" })
	public ModelAndView payofflog_set_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		SysConfig obj = this.configService.getSysConfig();
		
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = (SysConfig) WebForm.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) WebForm.toPo(request, obj);
		}
		Date now = new Date();
		int now_date = now.getDate();
		String select = getSelectedDate(CommUtil.null2Int(Integer
				.valueOf(sysConfig.getPayoff_count())));
		String[] str = select.split(",");

		for (String payoff_date : str) {

			if (CommUtil.null2Int(payoff_date) >= now_date) {
				System.out.println(payoff_date);
				now.setDate(CommUtil.null2Int(payoff_date));
				now.setHours(0);
				now.setMinutes(0);
				now.setSeconds(1);
				break;
			}
		}
		sysConfig.setPayoff_date(now);
		if (id.equals("")) {
			this.configService.saveEntity(sysConfig);
		} else {
			this.configService.updateById(sysConfig);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "结算周期设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/payofflog_set");
		return mv;
	}
	
	/**
	 * 账单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type
	 * @param type_data
	 * @param apply_beginTime
	 * @param apply_endTime
	 * @param addTime_beginTime
	 * @param addTime_endTime
	 * @param begin_price
	 * @param end_price
	 * @param status
	 * @param complete_beginTime
	 * @param complete_endTime
	 * @return
	 */
	@SecurityMapping(title = "账单列表", value = "/payofflog_list*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping({ "/payofflog_list" })
	public ModelAndView payofflog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String type, String type_data,
			String apply_beginTime, String apply_endTime,
			String addTime_beginTime, String addTime_endTime,
			String begin_price, String end_price, String status,
			String complete_beginTime, String complete_endTime) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/payofflog_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		String status2 = "0";
		if ((status != null) && (!status.equals(""))) {
			status2 = CommUtil.null2String(status);
		}
		
		params.put("status", CommUtil.null2Int(status2));
		if ((type != null) && (!type.equals(""))) {
			if ((type_data != null) && (!type_data.equals(""))) {
				if (type.equals("payoff")) {
					
					params.put("pl_sn", type_data);
				}
				if (type.equals("out_order")) {
					
					params.put("out_order_id", type_data);
				}
				if (type.equals("seller")) {
					
					params.put("seller_userName", type_data);
				}
				if (type.equals("order")) {
					
					params.put("order_id", type_data);
				}
			}
			mv.addObject("type", type);
			mv.addObject("type_data", type_data);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(2, 0);
		c.set(5, 1);
		String first = format.format(c.getTime());
		Calendar ca = Calendar.getInstance();
		ca.set(5, ca.getActualMaximum(5));
		String last = format.format(ca.getTime());
		if ((addTime_beginTime == null) || (addTime_beginTime.equals(""))) {
			addTime_beginTime = first;
		}
		if ((addTime_endTime == null) || (addTime_endTime.equals(""))) {
			addTime_endTime = last;
		}
		if ((apply_beginTime == null) || (apply_beginTime.equals(""))) {
			apply_beginTime = first;
		}
		if ((apply_endTime == null) || (apply_endTime.equals(""))) {
			apply_endTime = last;
		}
		if ((complete_beginTime == null) || (complete_beginTime.equals(""))) {
			complete_beginTime = first;
		}
		if ((complete_endTime == null) || (complete_endTime.equals(""))) {
			complete_endTime = last;
		}
		if ((begin_price != null) && (!begin_price.equals(""))) {
			
			params.put("total_amount_more_than_equal", CommUtil.null2Double(begin_price));
		}
		if ((end_price != null) && (!end_price.equals(""))) {
			
			params.put("total_amount_less_than_equal", CommUtil.null2Double(end_price));
			
		}
		if ((status2 != null) && (!status2.equals(""))) {
			if (status2.equals("0")) {
				params.put("add_Time_more_than_equal", CommUtil.formatDate(addTime_beginTime));
				params.put("add_Time_less_than_equal", CommUtil.formatDate(addTime_endTime));
				params.put("orderBy", "addTime");
			}
			
			if (status2.equals("3")) {
				params.put("apply_time_more_than_equal", CommUtil.formatDate(apply_beginTime));
				params.put("apply_time_less_than_equal", CommUtil.formatDate(apply_endTime));
				
				params.put("orderBy", "apply_time");
				
			}
			
			if (status2.equals("6")) {
				params.put("complete_time_more_than_equal", CommUtil.formatDate(complete_beginTime));
				params.put("complete_time_less_than_equal", CommUtil.formatDate(complete_endTime));
				params.put("orderBy", "complete_time");
				
			}
		}
		
		params.put("orderType", "desc");
		IPageList pList = this.payofflogService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		mv.addObject("addTime_beginTime", addTime_beginTime);
		mv.addObject("addTime_endTime", addTime_endTime);
		mv.addObject("apply_beginTime", apply_beginTime);
		mv.addObject("apply_endTime", apply_endTime);
		mv.addObject("complete_beginTime", complete_beginTime);
		mv.addObject("complete_endTime", complete_endTime);
		mv.addObject("status", status2);
		return mv;
	}
	
	/**
	 * 账单详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "账单详情", value = "/payofflog_view*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping({ "/payofflog_view" })
	public ModelAndView payofflog_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/payofflog_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PayoffLog obj = this.payofflogService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("payofflogTools", this.payofflogTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 账单结算
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "账单结算", value = "/payofflog_edit*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping({ "/payofflog_edit" })
	public ModelAndView payofflog_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/payofflog_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		PayoffLog obj = this.payofflogService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 账单结算保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "账单结算保存", value = "/payofflog_save*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping({ "/payofflog_save" })
	public String payofflog_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		User user = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(SecurityUserHolder.getCurrentUser().getId()));
		PayoffLog obj = this.payofflogService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		
		obj = (PayoffLog) WebForm.toPo(request, obj);
		obj.setStatus(6);
		obj.setComplete_time(new Date());
		obj.setAdmin(user);
		this.payofflogService.updateById(obj);
		Store store = obj.getSeller().getStore();
		store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.subtract(
				store.getStore_sale_amount(), obj.getOrder_total_price())));
		store.setStore_commission_amount(BigDecimal.valueOf(CommUtil.subtract(
				store.getStore_commission_amount(), obj.getCommission_amount())));
		store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.subtract(
				store.getStore_payoff_amount(), obj.getTotal_amount())));
		this.storeService.updateById(store);
		SysConfig sc = this.configService.getSysConfig();
		sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil.add(
				obj.getTotal_amount(), sc.getPayoff_all_amount())));
		sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil.add(
				obj.getReality_amount(), sc.getPayoff_all_amount_reality())));
		this.configService.updateById(sc);
		return "redirect:payofflog_list?currentPage=" + currentPage;
	}
	
	/**
	 * 账单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type
	 * @param type_data
	 * @param apply_beginTime
	 * @param apply_endTime
	 * @param addTime_beginTime
	 * @param addTime_endTime
	 * @param begin_price
	 * @param end_price
	 * @param status
	 * @param complete_beginTime
	 * @param complete_endTime
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "账单列表", value = "/payofflog_stat*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping({ "/payofflog_stat" })
	public void payofflog_stat(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String type, String type_data,
			String apply_beginTime, String apply_endTime,
			String addTime_beginTime, String addTime_endTime,
			String begin_price, String end_price, String status,
			String complete_beginTime, String complete_endTime) {
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		String status2 = "0";
		if ((status != null) && (!status.equals(""))) {
			status2 = CommUtil.null2String(status);
		}
		
		params.put("status", CommUtil.null2Int(status2));
		
		if ((type != null) && (!type.equals("")) && (type_data != null)
				&& (!type_data.equals(""))) {
			if (type.equals("payoff")) {
				
				params.put("pl_sn", type_data);
			}
			if (type.equals("seller")) {
				
				params.put("seller_userName", type_data);
			}
			if (type.equals("order")) {
				
				params.put("order_id", type_data);
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(2, 0);
		c.set(5, 1);
		String first = format.format(c.getTime());
		Calendar ca = Calendar.getInstance();
		ca.set(5, ca.getActualMaximum(5));
		String last = format.format(ca.getTime());
		if ((addTime_beginTime == null) || (addTime_beginTime.equals(""))) {
			addTime_beginTime = first;
		}
		if ((addTime_endTime == null) || (addTime_endTime.equals(""))) {
			addTime_endTime = last;
		}
		if ((apply_beginTime == null) || (apply_beginTime.equals(""))) {
			apply_beginTime = first;
		}
		if ((apply_endTime == null) || (apply_endTime.equals(""))) {
			apply_endTime = last;
		}
		if ((complete_beginTime == null) || (complete_beginTime.equals(""))) {
			complete_beginTime = first;
		}
		if ((complete_endTime == null) || (complete_endTime.equals(""))) {
			complete_endTime = last;
		}
		if ((begin_price != null) && (!begin_price.equals(""))) {
			params.put("total_amount_more_than_equal", CommUtil.null2Double(begin_price));
		}
		if ((end_price != null) && (!end_price.equals(""))) {
			params.put("total_amount_less_than_equal", CommUtil.null2Double(end_price));
		}
		if ((status2 != null) && (!status2.equals(""))) {
			if (status2.equals("0")) {
				params.put("add_Time_more_than_equal", CommUtil.formatDate(addTime_beginTime));
				params.put("add_Time_less_than_equal", CommUtil.formatDate(addTime_endTime));
				params.put("orderBy", "addTime");
			}
			if (status2.equals("3")) {
				params.put("apply_time_more_than_equal", CommUtil.formatDate(apply_beginTime));
				params.put("apply_time_less_than_equal", CommUtil.formatDate(apply_endTime));
				params.put("orderBy", "apply_time");
			}
			if (status2.equals("6")) {
				params.put("complete_time_more_than_equal", CommUtil.formatDate(complete_beginTime));
				params.put("complete_time_less_than_equal", CommUtil.formatDate(complete_endTime));
				params.put("orderBy", "complete_time");
			}
		}
		params.put("orderType", "desc");
		IPageList pList = this.payofflogService.list(params);
		List<PayoffLog> objs = pList.getResult();
		double all_order_price = 0.0D;
		double all_commission_price = 0.0D;
		double all_total_amount = 0.0D;
		boolean code = false;
		if (objs != null) {
			code = true;
			for (PayoffLog obj : objs) {
				all_order_price = CommUtil.add(Double.valueOf(all_order_price),
						obj.getOrder_total_price());
				all_commission_price = CommUtil.add(
						Double.valueOf(all_commission_price),
						obj.getCommission_amount());
				all_total_amount = CommUtil
						.add(Double.valueOf(all_total_amount),
								obj.getTotal_amount());
			}
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("all_order_price", Double.valueOf(all_order_price));
		map.put("all_commission_price", Double.valueOf(all_commission_price));
		map.put("all_total_amount", Double.valueOf(all_total_amount));
		if (objs != null) {
			map.put("data_size", Integer.valueOf(objs.size()));
		}
		map.put("code", Boolean.valueOf(code));
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
	 * 账单数据导出
	 * @param request
	 * @param response
	 * @param type
	 * @param type_data
	 * @param apply_beginTime
	 * @param apply_endTime
	 * @param addTime_beginTime
	 * @param addTime_endTime
	 * @param begin_price
	 * @param end_price
	 * @param status
	 * @param complete_beginTime
	 * @param complete_endTime
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@SecurityMapping(title = "账单数据导出", value = "/payofflog_excel*", rtype = "admin", rname = "结算管理", rcode = "admin_payoff", rgroup = "结算")
	@RequestMapping({ "/payofflog_excel" })
	public void payofflog_excel(HttpServletRequest request,
			HttpServletResponse response, String type, String type_data,
			String apply_beginTime, String apply_endTime,
			String addTime_beginTime, String addTime_endTime,
			String begin_price, String end_price, String status,
			String complete_beginTime, String complete_endTime) {
		Map<String,Object> params = this.redPigQueryTools.getParams(null,1000000000, "addTime", "desc");
		
		String status2 = "0";
		if ((status != null) && (!status.equals(""))) {
			status2 = CommUtil.null2String(status);
		}
		
		params.put("status", CommUtil.null2Int(status2));
		if ((type != null) && (!type.equals("")) && (type_data != null)
				&& (!type_data.equals(""))) {
			if (type.equals("payoff")) {
				
				params.put("pl_sn", type_data);
			}
			if (type.equals("seller")) {
				
				params.put("seller_userName", type_data);
			}
			if (type.equals("order")) {
				
				params.put("order_id", type_data);
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(2, 0);
		c.set(5, 1);
		String first = format.format(c.getTime());
		Calendar ca = Calendar.getInstance();
		ca.set(5, ca.getActualMaximum(5));
		String last = format.format(ca.getTime());
		if ((addTime_beginTime == null) || (addTime_beginTime.equals(""))) {
			addTime_beginTime = first;
		}
		if ((addTime_endTime == null) || (addTime_endTime.equals(""))) {
			addTime_endTime = last;
		}
		if ((apply_beginTime == null) || (apply_beginTime.equals(""))) {
			apply_beginTime = first;
		}
		if ((apply_endTime == null) || (apply_endTime.equals(""))) {
			apply_endTime = last;
		}
		if ((complete_beginTime == null) || (complete_beginTime.equals(""))) {
			complete_beginTime = first;
		}
		if ((complete_endTime == null) || (complete_endTime.equals(""))) {
			complete_endTime = last;
		}
		if ((begin_price != null) && (!begin_price.equals(""))) {
			params.put("total_amount_more_than_equal", CommUtil.null2Double(begin_price));
		}
		if ((end_price != null) && (!end_price.equals(""))) {
			params.put("total_amount_less_than_equal", CommUtil.null2Double(end_price));
		}
		if ((status2 != null) && (!status2.equals(""))) {
			if (status2.equals("0")) {
				params.put("add_Time_more_than_equal", CommUtil.formatDate(addTime_beginTime));
				params.put("add_Time_less_than_equal", CommUtil.formatDate(addTime_endTime));
				params.put("orderBy", "addTime");
				
			}
			if (status2.equals("3")) {
				params.put("apply_time_more_than_equal", CommUtil.formatDate(apply_beginTime));
				params.put("apply_time_less_than_equal", CommUtil.formatDate(apply_endTime));
				params.put("orderBy", "apply_time");
			}
			if (status2.equals("6")) {
				params.put("complete_time_more_than_equal", CommUtil.formatDate(complete_beginTime));
				params.put("complete_time_less_than_equal", CommUtil.formatDate(complete_endTime));
				params.put("orderBy", "complete_time");
			}
		}
		params.put("orderType", "desc");
		IPageList pList = this.payofflogService.list(params);
		if (pList.getResult() != null) {
			List<PayoffLog> datas = pList.getResult();

			HSSFWorkbook wb = new HSSFWorkbook();

			HSSFSheet sheet = wb.createSheet("结算账单");
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			List<HSSFClientAnchor> anchor = Lists.newArrayList();
			for (int i = 0; i < datas.size(); i++) {
				anchor.add(new HSSFClientAnchor(0, 0, 1000, 255, (short) 1,
						2 + i, (short) 1, 2 + i));
			}
			sheet.setColumnWidth(0, 6000);
			sheet.setColumnWidth(1, 6000);
			sheet.setColumnWidth(2, 6000);
			sheet.setColumnWidth(3, 4000);
			sheet.setColumnWidth(4, 4000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
			sheet.setColumnWidth(9, 6000);
			sheet.setColumnWidth(10, 6000);
			sheet.setColumnWidth(11, 6000);
			sheet.setColumnWidth(12, 6000);
			sheet.setColumnWidth(13, 8000);

			HSSFFont font = wb.createFont();
			font.setFontName("Verdana");
			font.setBoldweight((short) 100);
			font.setFontHeight((short) 300);
			font.setColor((short) 12);

			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment((short) 2);
			style.setVerticalAlignment((short) 1);
			style.setFillForegroundColor((short) 41);
			style.setFillPattern((short) 1);

			style.setBottomBorderColor((short) 10);
			style.setBorderBottom((short) 1);
			style.setBorderLeft((short) 1);
			style.setBorderRight((short) 1);
			style.setBorderTop((short) 1);
			style.setFont(font);

			HSSFRow row = sheet.createRow(0);
			row.setHeight((short) 500);

			HSSFCell cell = row.createCell(0);

			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 11));

			cell.setCellStyle(style);
			String title = "结算账单";
			Date time1 = null;
			Date time2 = null;
			if ((status2 != null) && (!status2.equals(""))) {
				if (status2.equals("0")) {
					title = "未结算账单";
					time1 = CommUtil.formatDate(addTime_beginTime);
					time2 = CommUtil.formatDate(addTime_endTime);
				}
				if (status2.equals("3")) {
					title = "可结算账单";
					time1 = CommUtil.formatDate(apply_beginTime);
					time2 = CommUtil.formatDate(apply_endTime);
				}
				if (status2.equals("6")) {
					title = "已结算账单";
					time1 = CommUtil.formatDate(complete_beginTime);
					time2 = CommUtil.formatDate(complete_endTime);
				}
			}
			String time = CommUtil.null2String(CommUtil.formatShortDate(time1)
					+ " - " + CommUtil.formatShortDate(time2));
			cell.setCellValue(this.configService.getSysConfig().getTitle()
					+ title + "（" + time + "）");

			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("yyyy-mm-dd"));
			style1.setWrapText(true);
			style1.setAlignment((short) 2);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setAlignment((short) 2);
			row = sheet.createRow(1);
			cell = row.createCell(0);
			cell.setCellStyle(style2);
			cell.setCellValue("账单流水号");
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("对应订单号");
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("外部流水号");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("商家名称");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("账单说明");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("账单入账时间");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("申请结算时间");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("完成结算时间");
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("账单总金额（元）");
			cell = row.createCell(9);
			cell.setCellStyle(style2);
			cell.setCellValue("账单总佣金（元）");
			cell = row.createCell(10);
			cell.setCellStyle(style2);
			cell.setCellValue("账单应结算（元）");
			cell = row.createCell(22);
			cell.setCellStyle(style2);
			cell.setCellValue("操作财务");
			cell = row.createCell(13);
			cell.setCellStyle(style2);
			cell.setCellValue("操作管理员");
			cell = row.createCell(14);
			cell.setCellStyle(style2);
			cell.setCellValue("结算备注");
			double all_order_price = 0.0D;
			double all_commission_amount = 0.0D;
			double all_total_amount = 0.0D;
			for (int j = 2; j <= datas.size() + 1; j++) {
				row = sheet.createRow(j);

				int i = 0;
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(((PayoffLog) datas.get(j - 2)).getPl_sn());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(((PayoffLog) datas.get(j - 2)).getOrder_id());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(((PayoffLog) datas.get(j - 2))
						.getOut_order_id());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(((PayoffLog) datas.get(j - 2)).getSeller()
						.getUserName());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(((PayoffLog) datas.get(j - 2)).getPl_info());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(((PayoffLog) datas
						.get(j - 2)).getAddTime()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(((PayoffLog) datas
						.get(j - 2)).getApply_time()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(((PayoffLog) datas
						.get(j - 2)).getComplete_time()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(((PayoffLog) datas
						.get(j - 2)).getOrder_total_price()));
				all_order_price = CommUtil.add(Double.valueOf(all_order_price),
						((PayoffLog) datas.get(j - 2)).getOrder_total_price());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(((PayoffLog) datas
						.get(j - 2)).getCommission_amount()));
				all_commission_amount = CommUtil.add(
						Double.valueOf(all_commission_amount),
						((PayoffLog) datas.get(j - 2)).getCommission_amount());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(((PayoffLog) datas
						.get(j - 2)).getTotal_amount()));
				all_total_amount = CommUtil.add(
						Double.valueOf(all_total_amount),
						((PayoffLog) datas.get(j - 2)).getTotal_amount());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(((PayoffLog) datas
						.get(j - 2)).getFinance_userName()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				if (((PayoffLog) datas.get(j - 2)).getAdmin() != null) {
					cell.setCellValue(CommUtil.null2String(((PayoffLog) datas
							.get(j - 2)).getAdmin().getUserName()));
				}
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(((PayoffLog) datas
						.get(j - 2)).getPayoff_remark()));
			}
			int m = datas.size() + 2;
			row = sheet.createRow(m);

			int i = 0;
			cell = row.createCell(i);
			cell.setCellStyle(style2);
			cell.setCellValue("总计");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次总销售金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_order_price);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次总销售佣金：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_commission_amount);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次总结算金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_total_amount);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String excel_name = sdf.format(new Date());
			try {
				String path = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator + "excel";
				response.setContentType("application/x-download");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + excel_name + ".xls");
				OutputStream os = response.getOutputStream();
				wb.write(os);
				os.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
