package com.redpigmall.manage.admin.action.operative.sms;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.SysConfig;

/**
 * 
 * <p>
 * Title: RedPigSmsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 短消息管理类，平台可设置系统短信邮件是否免费为商家所使用，或者由商家购买使用
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
 * @date 2014-10-31
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSmsManageAction extends BaseAction{
	
	/**
	 * 短消息设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "短消息设置", value = "/sms_set*", rtype = "admin", rname = "短消息管理", rcode = "sms_set", rgroup = "运营")
	@RequestMapping({ "/sms_set" })
	public ModelAndView sms_set(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/sms_set.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 短消息设置保存
	 * @param request
	 * @param response
	 * @param id
	 * @param ztc_status
	 * @param ztc_price
	 * @param ztc_goods_view
	 * @return
	 */
	@SecurityMapping(title = "短消息设置保存", value = "/sms_set_save*", rtype = "admin", rname = "短消息管理", rcode = "sms_set", rgroup = "运营")
	@RequestMapping({ "/sms_set_save" })
	public ModelAndView sms_set_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String ztc_status,
			String ztc_price, 
			String ztc_goods_view) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		SysConfig obj = this.redPigSysConfigService.getSysConfig();
		SysConfig sysConfig = null;
		sysConfig = (SysConfig) WebForm.toPo(request, obj);
		this.redPigSysConfigService.update(sysConfig);
		mv.addObject("op_title", "短消息设置成功");
		mv.addObject("list_url", CommUtil.getURL(request) + "/sms_set");
		return mv;
	}

	/**
	 * 短消息购买记录
	 * @param request
	 * @param response
	 * @param log_type
	 * @param currentPage
	 * @param store_name
	 * @param log_status
	 * @return
	 */
	@SecurityMapping(title = "短消息购买记录", value = "/sms_gold_log*", rtype = "admin", rname = "短消息管理", rcode = "sms_set", rgroup = "运营")
	@RequestMapping({ "/sms_gold_log" })
	public ModelAndView sms_gold_log(
			HttpServletRequest request,
			HttpServletResponse response, 
			String log_type, 
			String currentPage,
			String store_name, 
			String log_status) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/sms_gold_log.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		if ((store_name != null) && (!store_name.equals(""))) {
			mv.addObject("store_name", store_name);
			
			params.put("store_name_like", CommUtil.null2String(store_name));
			
		}
		
		if ((log_status != null) && (!log_status.equals(""))) {
			params.put("log_status", CommUtil.null2Int(log_status));
			
			mv.addObject("log_status", log_status);
		}
		if ((log_type != null) && (!log_type.equals(""))) {
			params.put("log_type", log_type);
			
			mv.addObject("log_type", log_type);
		} else {
			params.put("log_type", "sms");
		}
		
		IPageList pList = this.redPigSmsGoldLogService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
