package com.redpigmall.module.weixin.manage.buyer.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.weixin.view.action.base.BaseAction;

/**
 * <p>
 * Title: wap买家咨询
 * </p>
 * 
 * <p>
 * Description: WapUserComplaintAction
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2015年1月26日
 * 
 * @version b2b2c_2015
 */
@Controller
public class RedPigWeixinUserConsultAction extends BaseAction{
	
	
	/**
	 * wap端买家咨询列表
	 * @param request
	 * @param response
	 * @param reply
	 * @param currentPage
	 * @param tab
	 * @return
	 */
	@SecurityMapping(title = "wap端买家咨询列表", value = "/buyer/consult*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/consult" })
	public ModelAndView consult(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage,
			String tab) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/service_center_1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((CommUtil.null2Int(currentPage) > 1)
				|| (CommUtil.null2String(reply).equals("true"))) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/service_center_1_data.html",
					this.configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request, response);
		}
		if ((StringUtils.isNotBlank(tab)) && (tab.equals("2"))) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/service_center_1_data2.html",
					this.configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request, response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10,"addTime", "desc");
		
		if (!CommUtil.null2String(reply).equals("")) {
			maps.put("reply", CommUtil.null2Boolean(reply));
		} else {
			maps.put("reply", CommUtil.null2Boolean("false"));
		}
		maps.put("consult_user_id", SecurityUserHolder.getCurrentUser().getId());
		IPageList pList = this.consultService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("reply", CommUtil.null2String(reply));
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
}
