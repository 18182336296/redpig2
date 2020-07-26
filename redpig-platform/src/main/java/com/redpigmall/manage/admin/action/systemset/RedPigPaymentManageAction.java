package com.redpigmall.manage.admin.action.systemset;

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
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Payment;

/**
 * 
 * <p>
 * Title: RedPigPaymentManageAction.java
 * </p>
 * 
 * <p>
 * Description:支付方式控制器,配置系统接受支付的所有支付方式，B2B2C由平台统一收款，只需要运营商配置收款方式，商家无需关心收款方式
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
 * @date 2016-5-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigPaymentManageAction extends BaseAction{
	/**
	 * 支付方式保存
	 * @param request
	 * @param response
	 * @param mark
	 * @param list_url
	 * @return
	 */
	@SecurityMapping(title = "支付方式保存", value = "/payment_save*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping({ "/payment_save" })
	public ModelAndView payment_save(HttpServletRequest request,
			HttpServletResponse response, String mark, String list_url) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);

		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark);
		List<Payment> objs = this.redPigPaymentService.queryPageList(params);

		Payment obj = null;
		if (objs.size() > 0) {
			Payment temp = (Payment) objs.get(0);
			obj = (Payment) WebForm.toPo(request, temp);
		} else {
			obj = (Payment) WebForm.toPo(request, Payment.class);
			obj.setAddTime(new Date());
		}
		if (objs.size() > 0) {
			this.redPigPaymentService.update(obj);
		} else {
			this.redPigPaymentService.save(obj);
		}
		mv.addObject("op_title", "保存支付方式成功");
		mv.addObject("list_url", list_url);
		return mv;
	}
	
	/**
	 * 支付方式编辑
	 * @param request
	 * @param response
	 * @param mark
	 * @return
	 */
	@SecurityMapping(title = "支付方式编辑", value = "/payment_edit*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping({ "/payment_edit" })
	public ModelAndView payment_edit(HttpServletRequest request,
			HttpServletResponse response, String mark) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/payment_info.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark);
		List<Payment> objs = this.redPigPaymentService.queryPageList(params);

		Payment obj = null;
		if (objs.size() > 0) {
			obj = (Payment) objs.get(0);
		} else {
			obj = new Payment();
			obj.setMark(mark);
		}
		mv.addObject("obj", obj);
		mv.addObject("edit", Boolean.valueOf(true));
		return mv;
	}
	
	/**
	 * 支付方式设置
	 * @param request
	 * @param response
	 * @param mark
	 * @param type
	 * @param pay
	 * @param config_id
	 * @return
	 */
	@SecurityMapping(title = "支付方式设置", value = "/payment_set*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping({ "/payment_set" })
	public ModelAndView payment_set(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mark, String type, String pay,String config_id) {

		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		
		if (CommUtil.null2String(type).equals("admin")) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", mark);
			List<Payment> objs = this.redPigPaymentService.queryPageList(params);
			Payment obj = null;
			if (objs.size() > 0) {
				obj = (Payment) objs.get(0);
			} else {
				obj = new Payment();
			}
			
			obj.setAddTime(new Date());
			obj.setMark(mark);
			obj.setInstall(!CommUtil.null2Boolean(pay));
			if (CommUtil.null2String(obj.getName()).equals("")) {
				if (mark.trim().equals("alipay")) {
					obj.setName("支付宝");
				}
				if (mark.trim().equals("balance")) {
					obj.setName("预存款支付");
				}
				if (mark.trim().equals("outline")) {
					obj.setName("线下支付");
				}
				if (mark.trim().equals("tenpay")) {
					obj.setName("财付通");
				}
				if (mark.trim().equals("bill")) {
					obj.setName("快钱支付");
				}
				if (mark.trim().equals("chinabank")) {
					obj.setName("网银在线");
				}
				if (mark.trim().equals("alipay_wap")) {
					obj.setName("支付宝手机网页支付");
				}
				if (mark.trim().equals("wx_pay")) {
					obj.setName("微信支付");
				}
				if (mark.trim().equals("wx_app")) {
					obj.setName("app微信支付");
				}
				if (mark.trim().equals("unionpay")) {
					obj.setName("银联支付");
				}
			}
			if (objs.size() > 0) {
				this.redPigPaymentService.update(obj);
			} else {
				this.redPigPaymentService.save(obj);
			}
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/payment_list?type=" + type);
		mv.addObject("op_title", "设置支付方式成功");
		mv.addObject("paymentTools", this.redPigPaymentTools);
		return mv;
	}
	  
	/**
	 * 支付方式列表
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "支付方式列表", value = "/payment_list*", rtype = "admin", rname = "支付方式", rcode = "payment_set", rgroup = "设置")
	@RequestMapping({ "/payment_list" })
	public ModelAndView payment_list(HttpServletRequest request,
			HttpServletResponse response) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/payment_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request,
				response);
		mv.addObject("paymentTools", this.redPigPaymentTools);
		return mv;
	}
	
	
	
}
