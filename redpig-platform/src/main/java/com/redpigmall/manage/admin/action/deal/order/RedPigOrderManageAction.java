package com.redpigmall.manage.admin.action.deal.order;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;

/**
 * 
 * <p>
 * Title: RedPigOrderManageAction.java
 * </p>
 * 
 * <p>
 * Description:商城后台订单管理器
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
@SuppressWarnings("deprecation")
@Controller
public class RedPigOrderManageAction extends BaseAction {

	
	private static final BigDecimal WHETHER_ENOUGH = new BigDecimal(0.0D);
	private static final Map<Integer, String> STATUS_MAP = Maps.newHashMap();
	private static final Map<String, String> PAYMENT_MAP = Maps.newHashMap();
	private static final Map<String, String> TYPE_MAP = Maps.newHashMap();
	
	/**
	 * 订单设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "订单设置", value = "/set_order_confirm*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "交易")
	@RequestMapping({ "/set_order_confirm" })
	public ModelAndView set_order_confirm(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_order_confirm.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 订单设置保存
	 * @param request
	 * @param response
	 * @param id
	 * @param auto_order_confirm
	 * @param auto_order_notice
	 * @param auto_order_return
	 * @param auto_order_evaluate
	 * @param grouplife_order_return
	 * @param evaluate_edit_deadline
	 * @param evaluate_add_deadline
	 * @param auto_order_cancel
	 * @return
	 */
	@SecurityMapping(title = "订单设置保存", value = "/set_order_confirm_save*", rtype = "admin", rname = "订单设置", rcode = "set_order_confirm", rgroup = "交易")
	@RequestMapping({ "/set_order_confirm_save" })
	public ModelAndView set_order_confirm_save(HttpServletRequest request,
			HttpServletResponse response, String id, String auto_order_confirm,
			String auto_order_notice, String auto_order_return,
			String auto_order_evaluate, String grouplife_order_return,
			String evaluate_edit_deadline, String evaluate_add_deadline,
			String auto_order_cancel) {
		SysConfig obj = this.redPigSysConfigService.getSysConfig();
		
		SysConfig config = null;
		if (id.equals("")) {
			config = (SysConfig) WebForm.toPo(request, SysConfig.class);
			config.setAddTime(new Date());
		} else {
			config = (SysConfig) WebForm.toPo(request, obj);
		}
		config.setAuto_order_cancel(CommUtil.null2Int(auto_order_cancel));
		config.setAuto_order_confirm(CommUtil.null2Int(auto_order_confirm));
		config.setAuto_order_notice(CommUtil.null2Int(auto_order_notice));
		config.setAuto_order_return(CommUtil.null2Int(auto_order_return));
		config.setAuto_order_evaluate(CommUtil.null2Int(auto_order_evaluate));
		config.setGrouplife_order_return(CommUtil
				.null2Int(grouplife_order_return));
		config.setEvaluate_edit_deadline(CommUtil
				.null2Int(evaluate_edit_deadline));
		config.setEvaluate_add_deadline(CommUtil
				.null2Int(evaluate_add_deadline));
		if (id.equals("")) {
			this.redPigSysConfigService.save(config);
		} else {
			this.redPigSysConfigService.update(config);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "订单设置成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/set_order_confirm");
		return mv;
	}
	
	/**
	 * 订单列表
	 * @param request
	 * @param response
	 * @param order_status
	 * @param type
	 * @param type_data
	 * @param payment
	 * @param beginTime
	 * @param endTime
	 * @param begin_price
	 * @param end_price
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "订单列表", value = "/order_list*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping({ "/order_list" })
	public ModelAndView order_list(HttpServletRequest request,
			HttpServletResponse response, String order_status, String type,
			String type_data, String payment, String beginTime, String endTime,
			String begin_price, String end_price, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/order_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		params.put("order_cat", 0);
		
		if (!CommUtil.null2String(order_status).equals("")) {
			if ("20".equals(order_status)) {
				Set<Integer> statuss = Sets.newTreeSet();
				statuss.add(Integer.valueOf(16));
				statuss.add(Integer.valueOf(20));
				
				params.put("order_statuss", statuss);
			} else {
				params.put("order_status", CommUtil.null2Int(order_status));
			}
		}
		if (!CommUtil.null2String(type_data).equals("")) {
			if (type.equals("store")) {
				params.put("store_name", type_data);
			}
			
			if (type.equals("buyer")) {
				params.put("user_name", type_data);
			}
			
			if (type.equals("order")) {
				params.put("order_id", type_data);
			}
		}
		if (CommUtil.null2String(payment).equals("alipay")) {
			params.put("payment_mark", payment);
			params.put("payment_mark2", "alipay_app");
			params.put("payment_mark3", "alipay_wap");
			
		} else if (CommUtil.null2String(payment).equals("apyafter")) {
			params.put("payType", "payafter");
		} else if (CommUtil.null2String(payment).equals("wx_app")) {
			params.put("payment_mark", "wx_app");
			params.put("payment_mark2", "wx_pay");
			
		} else if (CommUtil.null2String(payment).equals("unionpay")) {
			params.put("payment_mark", "unionpay");
		} else if (!CommUtil.null2String(payment).equals("")) {
			params.put("payment_mark", payment);
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("addTime_more_than_equal", CommUtil.formatDate(beginTime));
		}
		if (!CommUtil.null2String(endTime).equals("")) {
			
			params.put("addTime_less_than_equal", CommUtil.formatDate(endTime));
		}
		if (!CommUtil.null2String(begin_price).equals("")) {
			params.put("totalPrice_more_than_equal", CommUtil.null2Double(begin_price));
			
		}
		if (!CommUtil.null2String(end_price).equals("")) {
			params.put("totalPrice_less_than_equal", CommUtil.null2Double(end_price));
			
		}
		
		IPageList pList = this.redPigOrderFormService.list(params);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		mv.addObject("type_data", type_data);
		mv.addObject("payment", payment);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		return mv;
	}
	
	/**
	 * 手机充值订单列表
	 * @param request
	 * @param response
	 * @param order_status
	 * @param beginTime
	 * @param endTime
	 * @param begin_price
	 * @param end_price
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "手机充值订单列表", value = "/order_recharge*", rtype = "admin", rname = "充值列表", rcode = "ofcard_list", rgroup = "交易")
	@RequestMapping({ "/order_recharge" })
	public ModelAndView order_recharge(HttpServletRequest request,
			HttpServletResponse response, String order_status,
			String beginTime, String endTime, String begin_price,
			String end_price, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/order_recharge.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		params.put("order_cat", 1);
		
		if (!CommUtil.null2String(order_status).equals("")) {
			params.put("order_status", CommUtil.null2Int(order_status));
		}
		
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("addTime_more_than_equal", CommUtil.formatDate(beginTime));
			
		}
		
		if (!CommUtil.null2String(endTime).equals("")) {
			params.put("addTime_less_than_equal", CommUtil.formatDate(endTime));
			
		}
		
		if (!CommUtil.null2String(begin_price).equals("")) {
			params.put("totalPrice_more_than_equal", CommUtil.null2Double(begin_price));
			
		}
		if (!CommUtil.null2String(end_price).equals("")) {
			params.put("totalPrice_less_than_equal", CommUtil.null2Double(end_price));
		}
		
		IPageList pList = this.redPigOrderFormService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_status", order_status);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param view_type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "订单详情", value = "/order_view*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping({ "/order_view" })
	public ModelAndView order_view(HttpServletRequest request,
			HttpServletResponse response, String id, String view_type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/order_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.redPigOrderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getOrder_cat() == 1) {
			mv = new RedPigJModelAndView("admin/blue/order_recharge_view.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
		} else {
			Store store = this.redPigStoreService.selectByPrimaryKey(CommUtil.null2Long(obj
					.getStore_id()));
			mv.addObject("store", store);
			mv.addObject("obj", obj);
		}
		if (obj != null) {
			String temp = obj.getSpecial_invoice();
			if ((temp != null) && (!"".equals(temp))) {
				Map of_va = JSON.parseObject(temp);
				mv.addObject("of_va", of_va);
			}
		}
		mv.addObject("express_company_name", this.redPigOrderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
		mv.addObject("orderFormTools", this.redPigOrderFormTools);
		mv.addObject("obj", obj);
		mv.addObject("view_type", view_type);
		return mv;
	}
	
	/**
	 * 订单导出excel
	 * @param request
	 * @param response
	 * @param order_status
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @param type
	 * @param type_date
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@SecurityMapping(title = "订单导出excel", value = "/order_manage_excel*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping({ "/order_manage_excel" })
	public void order_manage_excel(HttpServletRequest request,
			HttpServletResponse response, String order_status, String order_id,
			String beginTime, String endTime, String type, String type_date) {
		String buyer_userName = "";
		String store_name = "";
		if ((type != null) && (type.equals("buyer"))) {
			buyer_userName = type_date;
		}
		if ((type != null) && (type.equals("store"))) {
			store_name = type_date;
		}
		if ((type != null) && (type.equals("order"))) {
			order_id = type_date;
		}
		
		
		Map<String,Object> params = this.redPigQueryTools.getParams(null, "addTime", "desc");
		params.put("pageSize", 1000000000);
		
		if (!CommUtil.null2String(order_status).equals("")) {
			if (order_status.equals("order_submit")) {
				params.put("order_status1", 10);
				params.put("order_status1", 16);
			}
			
			if (order_status.equals("order_pay")) {
				params.put("order_status_more_than_equal", 16);
				params.put("order_status_less_than_equal", 20);
			}
			
			if (order_status.equals("order_shipping")) {
				params.put("order_status", 30);
			}
			
			if (order_status.equals("order_evaluate")) {
				params.put("order_status", 40);
			}
			
			if (order_status.equals("order_finish")) {
				params.put("order_status", 50);
			}
			
			if (order_status.equals("order_cancel")) {
				params.put("order_status", 0);
			}
		}
		
		if (!CommUtil.null2String(order_id).equals("")) {
			params.put("order_id_like", order_id);
		}
		
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("addTime_more_than_equal", CommUtil.formatDate(beginTime));
		}
		
		if (!CommUtil.null2String(endTime).equals("")) {
			String ends = endTime + " 23:59:59";
			params.put("addTime_less_than_equal", CommUtil.formatDate(ends,"yyyy-MM-dd hh:mm:ss"));
		}
		
		if (!CommUtil.null2String(buyer_userName).equals("")) {
			params.put("user_name", buyer_userName);
		}
		
		if (!CommUtil.null2String(store_name).equals("")) {
			params.put("store_name", store_name);
		}
		
		Calendar c = Calendar.getInstance();
		c.add(2, 0);
		c.set(5, 1);
		Calendar ca = Calendar.getInstance();
		ca.set(5, ca.getActualMaximum(5));
		
		IPageList pList = this.redPigOrderFormService.list(params);
		if (pList.getResult() != null) {
			List<OrderForm> datas = pList.getResult();

			HSSFWorkbook wb = new HSSFWorkbook();

			HSSFSheet sheet = wb.createSheet("订单列表");
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
			sheet.setColumnWidth(4, 12000);
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
			String title = "订单列表";
			Date time1 = CommUtil.formatDate(beginTime);
			Date time2 = CommUtil.formatDate(endTime);
			String time = CommUtil.null2String(CommUtil.formatShortDate(time1)
					+ " - " + CommUtil.formatShortDate(time2));
			cell.setCellValue(this.redPigSysConfigService.getSysConfig().getTitle()
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
			cell.setCellValue("订单号");
			cell = row.createCell(1);
			cell.setCellStyle(style2);
			cell.setCellValue("下单时间");
			cell = row.createCell(2);
			cell.setCellStyle(style2);
			cell.setCellValue("支付方式");
			cell = row.createCell(3);
			cell.setCellStyle(style2);
			cell.setCellValue("订单类型");
			cell = row.createCell(4);
			cell.setCellStyle(style2);
			cell.setCellValue("商品");
			cell = row.createCell(5);
			cell.setCellStyle(style2);
			cell.setCellValue("物流单号");
			cell = row.createCell(6);
			cell.setCellStyle(style2);
			cell.setCellValue("运费");
			cell = row.createCell(7);
			cell.setCellStyle(style2);
			cell.setCellValue("商品总价");
			cell = row.createCell(8);
			cell.setCellStyle(style2);
			cell.setCellValue("订单总额");
			cell = row.createCell(9);
			cell.setCellStyle(style2);
			cell.setCellValue("订单状态");
			cell = row.createCell(10);
			cell.setCellStyle(style2);
			cell.setCellValue("发货时间");
			cell = row.createCell(11);
			cell.setCellStyle(style2);
			cell.setCellValue("活动信息");
			double all_order_price = 0.0D;
			double all_total_amount = 0.0D;
			for (int j = 2; j <= datas.size() + 1; j++) {
				row = sheet.createRow(j);

				int i = 0;
				cell = row.createCell(i);
				cell.setCellStyle(style2);
				cell.setCellValue(((OrderForm) datas.get(j - 2)).getOrder_id());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(((OrderForm) datas
						.get(j - 2)).getAddTime()));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				if (((OrderForm) datas.get(j - 2)).getPayment_name() != null) {
					cell.setCellValue((String) PAYMENT_MAP
							.get(((OrderForm) datas.get(j - 2))
									.getPayment_name()));
				} else {
					cell.setCellValue("未支付");
				}
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue((String) TYPE_MAP.get(((OrderForm) datas
						.get(j - 2)).getOrder_type()));
				List<Map> goods_json = Lists.newArrayList();
				if ((datas.size() >= j - 2) && (datas.get(j - 2) != null)) {
					goods_json = JSON.parseArray(CommUtil
							.null2String(((OrderForm) datas.get(j - 2))
									.getGoods_info()), Map.class);
				}
				StringBuilder sb = new StringBuilder();
				boolean whether_combin = false;
				if (goods_json != null) {
					for (Map map : goods_json) {
						sb.append(map.get("goods_name") + "*"
								+ map.get("goods_count") + ",");
						if ((map.get("goods_type") != null)
								&& (!"".equals(map.get("goods_type")))) {
							if (map.get("goods_type").toString()
									.equals("combin")) {
								whether_combin = true;
							}
						}
					}
				}
				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(sb.toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(((OrderForm) datas.get(j - 2)).getShipCode());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(((OrderForm) datas.get(j - 2))
						.getShip_price().toString());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(((OrderForm) datas.get(j - 2))
						.getGoods_amount().toString());
				all_total_amount = CommUtil.add(
						Double.valueOf(all_total_amount),
						((OrderForm) datas.get(j - 2)).getGoods_amount());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.null2String(((OrderForm) datas
						.get(j - 2)).getTotalPrice()));
				all_order_price = CommUtil.add(Double.valueOf(all_order_price),
						((OrderForm) datas.get(j - 2)).getTotalPrice());

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue((String) STATUS_MAP.get(Integer
						.valueOf(((OrderForm) datas.get(j - 2))
								.getOrder_status())));

				cell = row.createCell(++i);
				cell.setCellStyle(style2);
				cell.setCellValue(CommUtil.formatLongDate(((OrderForm) datas
						.get(j - 2)).getShipTime()));
				if ((((OrderForm) datas.get(j - 2)).getWhether_gift() !=null ) 
						&& (((OrderForm) datas.get(j - 2)).getWhether_gift()== 1)) {
					List<Map> gifts_json = JSON.parseArray(
							((OrderForm) datas.get(j - 2)).getGift_infos(),
							Map.class);
					StringBuilder gsb = new StringBuilder();
					for (Map map : gifts_json) {
						gsb.append(map.get("goods_name") + ",");
					}
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue(gsb.toString());
				}
				if ((((OrderForm) datas.get(j - 2)).getEnough_reduce_amount() != null)
						&& (((OrderForm) datas.get(j - 2))
								.getEnough_reduce_amount().compareTo(
										WHETHER_ENOUGH) == 1)) {
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue("满减");
				}
				if (whether_combin) {
					cell = row.createCell(++i);
					cell.setCellStyle(style2);
					cell.setCellValue("组合销售");
				}
			}
			int m = datas.size() + 2;
			row = sheet.createRow(m);

			int i = 0;
			cell = row.createCell(i);
			cell.setCellStyle(style2);
			cell.setCellValue("总计");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次订单金额：");

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue(all_order_price);

			cell = row.createCell(++i);
			cell.setCellStyle(style2);
			cell.setCellValue("本次商品总金额：");

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
