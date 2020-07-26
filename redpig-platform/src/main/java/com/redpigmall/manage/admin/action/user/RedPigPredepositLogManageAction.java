package com.redpigmall.manage.admin.action.user;

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
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: PredepositLogManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城预存款管理控制器，用来显示系统预存款明细数据
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
 * @date 2014-5-30
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigPredepositLogManageAction extends BaseAction{
	
	
	/**
	 * 预存款明细列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param userName
	 * @return
	 */
	@SecurityMapping(title = "预存款明细列表", value = "/predepositlog_list*", rtype = "admin", rname = "预存款明细", rcode = "predeposit_log", rgroup = "会员")
	@RequestMapping({ "/predepositlog_list" })
	public ModelAndView predepositlog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String userName) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/predepositlog_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			String url = this.configService.getSysConfig().getAddress();
			if ((url == null) || (url.equals(""))) {
				url = CommUtil.getURL(request);
			}
			String params = "";
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
			if (!CommUtil.null2String(userName).equals("")) {
				maps.put("pd_log_user_userName", userName);
			}
			
			IPageList pList = this.predepositlogService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView(url
					+ "/predepositlog_list.html", "", params, pList, mv);
			mv.addObject("userName", userName);
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
}
