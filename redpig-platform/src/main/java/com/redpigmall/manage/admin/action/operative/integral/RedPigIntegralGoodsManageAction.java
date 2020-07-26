package com.redpigmall.manage.admin.action.operative.integral;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.domain.IntegralGoods;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.User;
import com.redpigmall.domain.vo.ExpressInfoVO;

/**
 * 
 * <p>
 * Title: IntegralGoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 积分商品管理控制器，积分商品由平台运营商发布
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
 * @date 2014-5-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("rawtypes")
@Controller
public class RedPigIntegralGoodsManageAction extends BaseAction{

	
	/**
	 * 积分礼品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ig_goods_name
	 * @param ig_goods_sn
	 * @param ig_show
	 * @return
	 */
	@SecurityMapping(title = "积分礼品列表", value = "/integral_items*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_items" })
	public ModelAndView integral_items(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String ig_goods_name, 
			String ig_goods_sn,
			String ig_show) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/integral_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			
			Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
			
			if (!CommUtil.null2String(ig_goods_name).equals("")) {
				params.put("ig_goods_name_like", ig_goods_name.trim());
				
			}
			
			if (!CommUtil.null2String(ig_show).equals("")) {
				params.put("ig_show", CommUtil.null2Boolean(ig_show));
			}
			
			if (!CommUtil.null2String(ig_goods_sn).equals("")) {
				params.put("ig_goods_sn", CommUtil.null2String(ig_goods_sn));
				
			}
			
			IPageList pList = this.redPigIntegralgoodsService.list(params);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("ig_goods_name", ig_goods_name);
			mv.addObject("ig_goods_sn", ig_goods_sn);
			mv.addObject("ig_show", ig_show);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 积分礼品添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "积分礼品添加", value = "/integral_goods_add*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_goods_add" })
	public ModelAndView integral_goods_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/integral_goods_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			mv.addObject("currentPage", currentPage);
			Calendar cal = Calendar.getInstance();
			cal.add(5, 1);
			mv.addObject("default_begin_time",CommUtil.formatShortDate(cal.getTime()));
			cal.add(5, 1);
			mv.addObject("default_end_time",CommUtil.formatShortDate(cal.getTime()));
			
			List<Map> levels = this.redPigIntegralViewTools.query_all_level();
			
			mv.addObject("levels", levels);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 积分礼品编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "积分礼品编辑", value = "/integral_goods_edit*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_goods_edit" })
	public ModelAndView integral_goods_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/integral_goods_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			if ((id != null) && (!id.equals(""))) {
				IntegralGoods integralgoods = this.redPigIntegralgoodsService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				mv.addObject("obj", integralgoods);
				mv.addObject("currentPage", currentPage);
				mv.addObject("edit", Boolean.valueOf(true));
				if (integralgoods.getIg_time_type()) {
					mv.addObject("default_begin_time", CommUtil
							.formatShortDate(integralgoods.getIg_begin_time()));
					mv.addObject("default_end_time", CommUtil
							.formatShortDate(integralgoods.getIg_end_time()));
				} else {
					Calendar cal = Calendar.getInstance();
					cal.add(5, 1);
					mv.addObject("default_begin_time",
							CommUtil.formatShortDate(cal.getTime()));
					cal.add(5, 1);
					mv.addObject("default_end_time",
							CommUtil.formatShortDate(cal.getTime()));
				}
				List<Map> levels = this.redPigIntegralViewTools.query_all_level();
				mv.addObject("levels", levels);
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 积分礼品保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param begin_hour
	 * @param end_hour
	 * @param list_url
	 * @param add_url
	 * @param user_level
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "积分礼品保存", value = "/integral_goods_save*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_goods_save" })
	public ModelAndView integral_goods_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String begin_hour, String end_hour, String list_url,
			String add_url, String user_level) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			IntegralGoods goods = null;
			if (id.equals("")) {
				goods = (IntegralGoods) WebForm.toPo(request,
						IntegralGoods.class);
				goods.setAddTime(new Date());
				goods.setIg_goods_sn("gift"
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
						+ SecurityUserHolder.getCurrentUser().getId());
			} else {
				IntegralGoods obj = this.redPigIntegralgoodsService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				goods = (IntegralGoods) WebForm.toPo(request, obj);
			}
			String uploadFilePath = this.redPigSysConfigService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "integral_goods";
			Map<String, Object> map = Maps.newHashMap();
			String fileName = "";
			
//			if (!id.equals("") && goods.getIg_goods_img() != null) {
//					fileName = goods.getIg_goods_img().getName();
//			}
//			
			
			try {
				map = CommUtil.saveFileToServer(request, "img1",
						saveFilePathName, fileName, null);
				Accessory acc = null;
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						acc = new Accessory();
						acc.setName(CommUtil.null2String(map.get("fileName")));
						acc.setExt(CommUtil.null2String(map.get("mime")));
						acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
								.get("fileSize"))));
						acc.setPath(uploadFilePath + "/integral_goods");
						acc.setWidth(CommUtil.null2Int(map.get("width")));
						acc.setHeight(CommUtil.null2Int(map.get("height")));
						acc.setAddTime(new Date());
						this.redPigAccessoryService.save(acc);
						
						System.out.println(acc);
						System.out.println("accId:"+acc.getId());
						
						goods.setIg_goods_img(acc);
						System.out.println("goods.ig_goods_img_id set after="+goods.getIg_goods_img().getId());
					}
				} else if (map.get("fileName") != "") {
					acc = goods.getIg_goods_img();
					acc.setName(CommUtil.null2String(map.get("fileName")));
					acc.setExt(CommUtil.null2String(map.get("mime")));
					acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					acc.setPath(uploadFilePath + "/integral_goods");
					acc.setWidth(CommUtil.null2Int(map.get("width")));
					acc.setHeight(CommUtil.null2Int(map.get("height")));
					acc.setAddTime(new Date());
					this.redPigAccessoryService.update(acc);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			String ext = goods.getIg_goods_img().getExt().indexOf(".") < 0 ? "."
					+ goods.getIg_goods_img().getExt()
					: goods.getIg_goods_img().getExt();
			String source = //request.getSession().getServletContext().getRealPath("/")
					this.redPigSysConfigService.getSysConfig().getImageWebServer()
					+ goods.getIg_goods_img().getPath()
					+ File.separator
					+ goods.getIg_goods_img().getName();
			String target = source + "_small" + ext;
			CommUtil.createSmall(source, target, this.redPigSysConfigService
					.getSysConfig().getSmallWidth(), this.redPigSysConfigService
					.getSysConfig().getSmallHeight());
			Calendar cal = Calendar.getInstance();
			if (goods.getIg_begin_time() != null) {
				cal.setTime(goods.getIg_begin_time());
				cal.add(10, CommUtil.null2Int(begin_hour));
				goods.setIg_begin_time(cal.getTime());
			}
			if (goods.getIg_end_time() != null) {
				cal.setTime(goods.getIg_end_time());
				cal.add(10, CommUtil.null2Int(end_hour));
				goods.setIg_end_time(cal.getTime());
			}
			goods.setIg_user_Level(CommUtil.null2Int(user_level));
			if (id.equals("")) {
				this.redPigIntegralgoodsService.saveEntity(goods);
			} else {
				this.redPigIntegralgoodsService.updateById(goods);
			}
			mv.addObject("list_url", list_url);
			mv.addObject("op_title", "保存积分商品成功");
			if (add_url != null) {
				mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 积分礼品删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "积分礼品删除", value = "/integral_goods_del*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_goods_del" })
	public String integral_goods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId != null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.redPigIntegralgoodsService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:integral_items?currentPage=" + currentPage;
	}
	
	/**
	 * 积分礼品Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "积分礼品Ajax更新", value = "/integral_goods_ajax*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_goods_ajax" })
	public void integral_goods_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String fieldName,
			String value) throws ClassNotFoundException {
		
		IntegralGoods ig = this.redPigIntegralgoodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		
		Field[] fields = IntegralGoods.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, ig, fields);
		
		this.redPigIntegralgoodsService.updateById(ig);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 积分礼品兑换列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param igo_order_sn
	 * @param userName
	 * @param igo_payment
	 * @param igo_status
	 * @return
	 */
	@SecurityMapping(title = "积分礼品兑换列表", value = "/integral_order*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order" })
	public ModelAndView integral_order(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String igo_order_sn, 
			String userName,
			String igo_payment, 
			String igo_status) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/integral_order.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			
			Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
			
			if (StringUtils.isNotBlank(igo_order_sn)) {
				params.put("igo_order_sn_like", igo_order_sn.trim());
			}
			
			if (StringUtils.isNotBlank(userName)) {
				params.put("igo_user_userName_like", userName);
			}
			
			if (CommUtil.null2String(igo_payment).equals("alipay")) {
				params.put("igo_payment_alipay", "alipay");
				
				params.put("igo_payment_alipay_wap", "alipay_wap");
				
				params.put("igo_payment_alipay_app", "alipay_app");
				
			} else if (CommUtil.null2String(igo_payment).equals("wx")) {
				params.put("igo_payment_wx_app", "wx_app");
				
				params.put("igo_payment_wx_pay", "wx_pay");
				
			} else if (!CommUtil.null2String(igo_payment).equals("")) {
				params.put("igo_payment", igo_payment.trim());
				
			}
			if (!CommUtil.null2String(igo_status).equals("")) {
				params.put("igo_status", CommUtil.null2Int(igo_status));
			}
			
			IPageList pList = this.redPigIntegralGoodsOrderService.list(params);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("igo_order_sn", igo_order_sn);
			mv.addObject("userName", userName);
			mv.addObject("igo_payment", igo_payment);
			mv.addObject("igo_status", igo_status);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 积分礼品兑换详情
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "积分礼品兑换详情", value = "/integral_order_view*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order_view" })
	public ModelAndView integral_order_view(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String id) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/integral_order_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			IntegralGoodsOrder obj = this.redPigIntegralGoodsOrderService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("obj", obj);
			mv.addObject("express_company_name", this.redPigOrderFormTools
								.queryExInfo(obj.getIgo_express_info(),
										"express_company_name"));
			
			mv.addObject("orderFormTools", this.redPigOrderFormTools);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 取消积分订单
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "取消积分订单", value = "/integral_order_cancel*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order_cancel" })
	public ModelAndView integral_order_cancel(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		IntegralGoodsOrder obj = this.redPigIntegralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			obj.setIgo_status(-1);
			this.redPigIntegralGoodsOrderService.updateById(obj);

			List<IntegralGoods> igs = this.redPigIntegralGoodsOrderService.queryIntegralGoods(CommUtil.null2String(obj.getId()));
			for (IntegralGoods ig : igs) {
				int count = this.redPigIntegralGoodsOrderService.queryIntegralOneGoodsCount(obj,CommUtil.null2String(obj.getId()));
				ig.setIg_goods_count(ig.getIg_goods_count() - count);
				this.redPigIntegralgoodsService.updateById(ig);
			}
			
			User user = obj.getIgo_user();
			user.setIntegral(user.getIntegral() + obj.getIgo_total_integral());
			this.redPigUserService.updateById(user);
			IntegralLog log = new IntegralLog();
			log.setAddTime(new Date());
			log.setContent("取消" + obj.getIgo_order_sn() + "积分兑换，返还积分");
			log.setIntegral(obj.getIgo_total_integral());
			log.setIntegral_user(obj.getIgo_user());
			log.setOperate_user(SecurityUserHolder.getCurrentUser());
			log.setType("integral_order");
			this.redPigIntegralLogService.saveEntity(log);
			mv.addObject("op_title", "积分兑换取消成功");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/integral_order");
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 订单确认付款
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "订单确认付款", value = "/integral_order_payok*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order_payok" })
	public ModelAndView integral_order_payok(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		IntegralGoodsOrder obj = this.redPigIntegralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			obj.setIgo_status(20);
			this.redPigIntegralGoodsOrderService.updateById(obj);
			mv.addObject("op_title", "确认收款成功");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/integral_order");
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 订单删除
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "订单删除", value = "/integral_order_del*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order_del" })
	public ModelAndView integral_order_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		IntegralGoodsOrder obj = this.redPigIntegralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			if (obj.getIgo_status() == -1) {
				this.redPigIntegralGoodsOrderService.deleteById(obj.getId());
			}
			mv.addObject("op_title", "删除兑换订单成功");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/integral_order");
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 订单费用调整
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "订单费用调整", value = "/integral_order_fee*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order_fee" })
	public ModelAndView integral_order_fee(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/integral_order_fee.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		IntegralGoodsOrder obj = this.redPigIntegralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 订单费用调整保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param igo_trans_fee
	 * @return
	 */
	@SecurityMapping(title = "订单费用调整保存", value = "/integral_order_fee_save*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order_fee_save" })
	public String integral_order_fee_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String igo_trans_fee) {
		
		IntegralGoodsOrder obj = this.redPigIntegralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			obj.setIgo_trans_fee(BigDecimal.valueOf(CommUtil
					.null2Double(igo_trans_fee)));
			if (CommUtil.null2Double(obj.getIgo_trans_fee()) == 0.0D) {
				obj.setIgo_pay_time(new Date());
				obj.setIgo_status(20);
			}
			this.redPigIntegralGoodsOrderService.updateById(obj);
		}
		return "redirect:integral_order?currentPage=" + currentPage;
	}
	
	/**
	 * 确认发货
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "确认发货", value = "/integral_order_ship*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order_ship" })
	public ModelAndView integral_order_ship(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/integral_order_ship.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		IntegralGoodsOrder obj = this.redPigIntegralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			mv.addObject("obj", obj);
			mv.addObject("currentPage", currentPage);
			
			ExpressInfoVO ec = (ExpressInfoVO) JSON.toJavaObject(JSON.parseObject(obj.getIgo_express_info()), ExpressInfoVO.class);
			
			mv.addObject("ec", ec);
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("status", Integer.valueOf(0));
			List<ExpressCompany> expressCompanys = this.redPigExpressCompanyService.queryPageList(params);
			
			mv.addObject("expressCompanys", expressCompanys);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.redPigSysConfigService.getSysConfig(),
					this.redPigUserConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启积分商城");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
	
	/**
	 * 确认发货保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param igo_ship_code
	 * @param igo_ship_content
	 * @param ec_id
	 * @return
	 */
	@SecurityMapping(title = "确认发货保存", value = "/integral_order_ship_save*", rtype = "admin", rname = "积分商城", rcode = "integral_goods_admin", rgroup = "运营")
	@RequestMapping({ "/integral_order_ship_save" })
	public String integral_order_ship_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String igo_ship_code, 
			String igo_ship_content, 
			String ec_id) {
		
		IntegralGoodsOrder obj = this.redPigIntegralGoodsOrderService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		ExpressCompany ec = this.redPigExpressCompanyService.selectByPrimaryKey(CommUtil.null2Long(ec_id));
		
		if (this.redPigSysConfigService.getSysConfig().getIntegralStore()) {
			obj.setIgo_status(30);
			obj.setIgo_ship_code(igo_ship_code);
			if (obj.getIgo_ship_time() == null) {
				obj.setIgo_ship_time(new Date());
				String msg_content = "您积分订单号为：" + obj.getIgo_order_sn()
						+ "的商品已发货，物流单号为：" + igo_ship_code + "，请注意查收！";

				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(obj.getIgo_user());
				this.redPigMessageService.saveEntity(msg);
			} else {
				String msg_content = "您积分订单号为：" + obj.getIgo_order_sn()
						+ "的商品已经更改物流信息，新物流单号为：" + igo_ship_code + "，请注意查收！";

				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(obj.getIgo_user());
				this.redPigMessageService.saveEntity(msg);
			}
			obj.setIgo_ship_content(igo_ship_content);
			Map<String,Object> json_map = Maps.newHashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			String express_json = JSON.toJSONString(json_map);
			obj.setIgo_express_info(express_json);
			this.redPigIntegralGoodsOrderService.updateById(obj);
		}
		return "redirect:integral_order?currentPage=" + currentPage;
	}
}
