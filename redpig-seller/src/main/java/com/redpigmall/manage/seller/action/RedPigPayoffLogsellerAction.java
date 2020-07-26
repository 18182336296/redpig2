package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.PayoffLog;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigPayoffLogsellerAction.java
 * </p>
 * 
 * <p>
 * Description: 系统结算管理类,商家可以和平台进行结算
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
public class RedPigPayoffLogsellerAction extends BaseAction{
	/**
	 * 结算列表页
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param beginTime
	 * @param endTime
	 * @param pl_sn
	 * @param order_id
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "结算列表页", value = "/payofflog_list*", rtype = "seller", display = false, rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping({ "/payofflog_list" })
	public ModelAndView payofflog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String beginTime, String endTime, String pl_sn,
			String order_id, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/payofflog_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20, orderBy, orderType);
		if ((pl_sn != null) && (!pl_sn.equals(""))) {
			maps.put("pl_sn", pl_sn);
			mv.addObject("pl_sn", pl_sn);
		}
		
		if ((order_id != null) && (!order_id.equals(""))) {
			maps.put("order_id", order_id);
			mv.addObject("order_id", order_id);
		}
		
		if ((beginTime != null) && (!beginTime.equals(""))) {
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		
		if ((endTime != null) && (!endTime.equals(""))) {
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("endTime", endTime);
		}
		
		int st = 0;
		if ((status != null) && (!status.equals(""))) {
			if (status.equals("not")) {
				st = 0;
			}
			if (status.equals("underway")) {
				st = 3;
			}
			if (status.equals("already")) {
				st = 6;
			}
		} else {
			status = "not";
		}
		
		maps.put("status", Integer.valueOf(st));
		mv.addObject("status", status);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		maps.put("seller_id", user.getId());
		IPageList pList = this.payoffLogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);

		Calendar a = Calendar.getInstance();
		a.set(5, 1);
		a.roll(5, -1);
		int maxDate = a.get(5);
		List<Integer> list = Lists.newArrayList();
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
		mv.addObject("payoff_mag_default", "今天是"
				+ DateFormat.getDateInstance(0).format(new Date())
				+ "，本月的结算日期为" + ms + "，请于结算日申请结算。");
		return mv;
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

	private boolean validatePayoffDate() {
		return true;
	}

	@SuppressWarnings({ "unused" })
	private void check_payoff_list() {
		Map<String, Object> params = Maps.newHashMap();
		SysConfig sysConfig = this.configService.getSysConfig();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		params.clear();
		params.put("seller_id", user.getId());
		params.put("status", Integer.valueOf(0));
		params.put("add_Time_less_than_equal", sysConfig.getPayoff_date());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		List<PayoffLog> payofflogs = this.payoffLogService.queryPageList(params);
		
		for (PayoffLog obj : payofflogs) {
			OrderForm of = this.orderformService.selectByPrimaryKey(CommUtil
					.null2Long(obj.getO_id()));
			Date Payoff_date = this.configService.getSysConfig()
					.getPayoff_date();
			Date now = new Date();
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			Date next = new Date();
			next.setDate(next.getDate() + 1);
			next.setHours(0);
			next.setMinutes(0);
			next.setSeconds(0);
			if ((of.getOrder_cat() == 2) && (of.getOrder_status() == 20)) {
				obj.setStatus(1);
			}
			if (of.getOrder_cat() == 0) {
				if ((of.getOrder_status() == 50)
						|| (of.getOrder_status() == 65)) {
					obj.setStatus(1);
				}
				if ((obj.getPayoff_type() == -1)
						&& ((of.getOrder_status() == 50) || (of
								.getOrder_status() == 65))) {
					obj.setStatus(3);
					obj.setApply_time(new Date());
				}
			}
			this.payoffLogService.updateById(obj);
		}
	}
	
	/**
	 * 可结算列表页
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param beginTime
	 * @param endTime
	 * @param pl_sn
	 * @param order_id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "可结算列表页", value = "/payofflog_ok_list*", rtype = "seller", display = false, rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping({ "/payofflog_ok_list" })
	public ModelAndView payofflog_ok_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String beginTime, String endTime, String pl_sn,
			String order_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/payofflog_ok_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean verify = validatePayoffDate();
		if (verify) {
			check_payoff_list();
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 20,orderBy, orderType);
		if ((pl_sn != null) && (!pl_sn.equals(""))) {
			maps.put("pl_sn", pl_sn);
			mv.addObject("pl_sn", pl_sn);
		}
		if ((order_id != null) && (!order_id.equals(""))) {
			maps.put("order_id", order_id);
			mv.addObject("order_id", order_id);
		}
		
		if ((beginTime != null) && (!beginTime.equals(""))) {
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		
		if ((endTime != null) && (!endTime.equals(""))) {
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
			mv.addObject("endTime", endTime);
		}
		
		maps.put("status_1" , 0);
		maps.put("status_2" , 1);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		maps.put("seller_id", user.getId());
		IPageList pList = this.payoffLogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		boolean payoff = validatePayoffDate();
		mv.addObject("payoff", Boolean.valueOf(payoff));
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
		mv.addObject("payoff_mag_default", "今天是"
				+ DateFormat.getDateInstance(0).format(new Date())
				+ "，本月的结算日期为" + ms + "，请于结算日申请结算。");
		return mv;
	}
	
	/**
	 * 账单详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param op
	 * @return
	 */
	@SecurityMapping(title = "账单详情", value = "/payofflog_info*", rtype = "seller", display = false, rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping({ "/payofflog_info" })
	public ModelAndView payofflog_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String op) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/payofflog_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			PayoffLog payofflog = this.payoffLogService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (user.getId().equals(payofflog.getSeller().getId())) {
				mv.addObject("payofflogTools", this.payofflogTools);
				mv.addObject("obj", payofflog);
				mv.addObject("currentPage", currentPage);
				mv.addObject("op", op);
			} else {
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/payofflog_list");
				mv.addObject("op_title", "您没有该账单");
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
		} else {
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/payofflog_list");
			mv.addObject("op_title", "账单不存在");
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		return mv;
	}
	
	/**
	 * 账单结算
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "账单结算", value = "/payofflog_edit*", rtype = "seller", display = false, rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping({ "/payofflog_edit" })
	public String payofflog_edit(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {

		for (String id : mulitId.split(",")) {

			if ((id != null) && (!id.equals(""))) {
				PayoffLog obj = this.payoffLogService.selectByPrimaryKey(CommUtil.null2Long(id));
				if (obj != null) {
					User user = this.userService.selectByPrimaryKey(SecurityUserHolder
							.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					if ((user.getId().equals(obj.getSeller().getId()))
							&& (obj.getStatus() == 1 || obj.getStatus() == 0)) {
						OrderForm of = this.orderFormServer.selectByPrimaryKey(CommUtil
								.null2Long(obj.getO_id()));
						if (of != null) {
							boolean payoff = validatePayoffDate();
							boolean goods = false;//商品购买
							boolean group = false;//团购也就是生活类订单
							//of.getOrder_status() == 50 为买家评价完
							//of.getOrder_status() == 65 为订单不可评价
							if (((of.getOrder_status() == 50) && (payoff))
									|| ((of.getOrder_status() == 65) && (payoff))) {
								goods = true;
							}
							//of.getOrder_cat() == 2 为生活类订单
							//of.getOrder_status() == 20为为已付款待发货
							if ((of.getOrder_cat() == 2)
									&& (of.getOrder_status() == 20) && (payoff)) {
								group = true;
							}
							if ((goods) || (group)) {
								obj.setStatus(3);
								obj.setApply_time(new Date());
								this.payoffLogService.updateById(obj);
							}
						}
					}
				}
			}
		}
		return "redirect:payofflog_ok_list?currentPage" + currentPage;
	}
	
	/**
	 * 批量统计
	 * @param request
	 * @param response
	 * @param mulitId
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "批量统计", value = "/payofflog_ajax*", rtype = "seller", display = false, rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping({ "/payofflog_ajax" })
	public void payofflog_ajax(HttpServletRequest request,
			HttpServletResponse response, String mulitId)
			throws ClassNotFoundException {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		double order_total_price = 0.0D;
		double commission_amount = 0.0D;
		double total_amount = 0.0D;
		boolean error = true;

		for (String id : ids) {

			if (!id.equals("")) {
				PayoffLog obj = this.payoffLogService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				if (obj != null) {
					User user = this.userService.selectByPrimaryKey(SecurityUserHolder
							.getCurrentUser().getId());
					user = user.getParent() == null ? user : user.getParent();
					if (user.getId().equals(obj.getSeller().getId())) {
						total_amount = CommUtil.add(
								Double.valueOf(total_amount),
								obj.getTotal_amount());
						commission_amount = CommUtil.add(
								Double.valueOf(commission_amount),
								obj.getCommission_amount());
						order_total_price = CommUtil.add(
								Double.valueOf(order_total_price),
								obj.getOrder_total_price());
					} else {
						error = false;
						break;
					}
				} else {
					error = false;
					break;
				}
			}
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("order_total_price", Double.valueOf(order_total_price));
		map.put("commission_amount", Double.valueOf(commission_amount));
		map.put("total_amount", Double.valueOf(total_amount));
		map.put("error", Boolean.valueOf(error));
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
	 * @param beginTime
	 * @param endTime
	 * @param pl_sn
	 * @param order_id
	 * @param status
	 */
	@SuppressWarnings({ "unused", "unchecked" })
	@SecurityMapping(title = "账单数据导出", value = "/payofflog_excel*", rtype = "seller", rname = "我的账单", rcode = "payoff_seller", rgroup = "结算管理")
	@RequestMapping({ "/payofflog_excel" })
	public void payofflog_excel(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String endTime,
			String pl_sn, String order_id, String status) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(null,1000000000, "addTime", "desc");
		maps.put("seller_id", user.getId());
		
		String status2 = "0";
		if ((status != null) && (!status.equals(""))) {
			status2 = CommUtil.null2String(status);
		}
		
		if ((pl_sn != null) && (!pl_sn.equals(""))) {
			maps.put("pl_sn", pl_sn);
		}
		
		if ((order_id != null) && (!order_id.equals(""))) {
			maps.put("order_id", order_id);
		}
		
		if ((beginTime != null) && (!beginTime.equals(""))) {
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
		}
		
		if ((endTime != null) && (!endTime.equals(""))) {
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
		}
		
		int st = 0;
		if ((status != null) && (!status.equals(""))) {
			if (status.equals("not")) {
				st = 0;
			}
			if (status.equals("underway")) {
				st = 3;
			}
			if (status.equals("already")) {
				st = 6;
			}
		} else {
			status = "not";
		}
		maps.put("status", st);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.add(2, 0);
		c.set(5, 1);
		String first = format.format(c.getTime());
		Calendar ca = Calendar.getInstance();
		ca.set(5, ca.getActualMaximum(5));
		String last = format.format(ca.getTime());
		
		IPageList pList = this.payoffLogService.list(maps);
		
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
			sheet.setColumnWidth(1, 4000);
			sheet.setColumnWidth(2, 4000);
			sheet.setColumnWidth(3, 6000);
			sheet.setColumnWidth(4, 6000);
			sheet.setColumnWidth(5, 6000);
			sheet.setColumnWidth(6, 6000);
			sheet.setColumnWidth(7, 6000);
			sheet.setColumnWidth(8, 6000);
			sheet.setColumnWidth(9, 6000);
			sheet.setColumnWidth(10, 6000);
			sheet.setColumnWidth(11, 8000);

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
			String time = CommUtil.null2String(CommUtil.formatDate(beginTime)
					+ " - " + CommUtil.formatDate(endTime));
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
			cell.setCellValue("商家名称");
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("账单说明");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("账单入账时间");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("申请结算时间");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("完成结算时间");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("账单总金额（元）");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("账单总佣金（元）");
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("账单应结算（元）");
			cell = row.createCell(9);
			cell.setCellStyle(style2);
			cell.setCellValue("操作财务");
			cell = row.createCell(10);
			cell.setCellStyle(style2);
			cell.setCellValue("操作管理员");
			cell = row.createCell(11);
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
