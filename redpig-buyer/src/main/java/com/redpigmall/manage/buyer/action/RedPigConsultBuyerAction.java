package com.redpigmall.manage.buyer.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.buyer.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigConsultBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家咨询管理器,显示所有买家发布的商品咨询信息及回复信息
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
 * @date 2014-9-29
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigConsultBuyerAction extends BaseAction{
	
	/**
	 * 买家咨询列表
	 * @param request
	 * @param response
	 * @param reply
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家咨询列表", value = "/buyer/consult*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/consult" })
	public ModelAndView consult(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_consult.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,3,"addTime", "desc");
		
		if (!CommUtil.null2String(reply).equals("")) {
			maps.put("reply", CommUtil.null2Boolean(reply));
		}
		
		
		maps.put("consult_user_id", SecurityUserHolder.getCurrentUser().getId());
		
		IPageList pList = this.consultService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("reply", CommUtil.null2String(reply));
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
}
