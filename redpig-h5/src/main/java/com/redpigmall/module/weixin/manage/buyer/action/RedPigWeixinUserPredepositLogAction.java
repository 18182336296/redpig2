package com.redpigmall.module.weixin.manage.buyer.action;

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
import com.redpigmall.module.weixin.view.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigWeixinUserPredepositLogAction.java
 * </p>
 * 
 * <p>
 * Description: wap端退货退款管理
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
 * @date 2015年1月23日
 * 
 * @version b2b2c_2015
 */
@Controller
public class RedPigWeixinUserPredepositLogAction extends BaseAction{
	
	
	public static final String PREDEPOSIT_TYPE_0 = "全部";
	public static final String PREDEPOSIT_TYPE_1 = "充值";
	public static final String PREDEPOSIT_TYPE_2 = "消费";
	public static final String PREDEPOSIT_TYPE_3 = "人工退款";
	public static final String PREDEPOSIT_TYPE_4 = "手动修改";
	
	/**
	 * 会员收支明细
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "会员收支明细", value = "/buyer/predeposit_log*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/predeposit_log" })
	public ModelAndView predeposit_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type) {
		if (SecurityUserHolder.getCurrentUser() != null) {
			ModelAndView mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/predeposit_log.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
	        
	        
			if (this.configService.getSysConfig().getDeposit()) {
				if ((type != null) && (!"".equals(type)) && (!"0".equals(type))) {
					maps.put("pd_log_user_id",SecurityUserHolder.getCurrentUser().getId());
					maps.put("pd_op_type",getPredepositOpType(type));
				} else {
					maps.put("pd_log_user_id",SecurityUserHolder.getCurrentUser().getId());
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "系统未开启预存款");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
			}
			IPageList pList = this.predepositLogService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			if ((type != null) && (!"".equals(type))) {
				mv.addObject("type", type);
			}
			mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId()));
			mv.addObject("type", type);
			return mv;
		}
		ModelAndView mv = new RedPigJModelAndView("weixin/404.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("url", CommUtil.getURL(request) + "/index");
		return mv;
	}
	
	/**
	 * 会员收支明细追加数据
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "会员收支明细追加数据", value = "/buyer/predeposit_log_data*", rtype = "buyer", rname = "预存款管理", rcode = "predeposit_set", rgroup = "用户中心")
	@RequestMapping({ "/buyer/predeposit_log_data" })
	public ModelAndView predeposit_log_data(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/predeposit_log_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
       

		if (this.configService.getSysConfig().getDeposit()) {
			if ((type != null) && (!"".equals(type)) && (!"0".equals(type))) {
				maps.put("pd_log_user_id",SecurityUserHolder.getCurrentUser().getId());
				maps.put("pd_op_type",getPredepositOpType(type));
			} else {
				maps.put("pd_log_user_id",SecurityUserHolder.getCurrentUser().getId());
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
		}
		IPageList pList = this.predepositLogService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		if ((type != null) && (!"".equals(type))) {
			mv.addObject("type", type);
		}
		mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId()));
		return mv;
	}

	public String getPredepositOpType(String type) {
		if (type.equals("0")) {
			return PREDEPOSIT_TYPE_0;
		}
		if (type.equals("1")) {
			return PREDEPOSIT_TYPE_1;
		}
		if (type.equals("2")) {
			return PREDEPOSIT_TYPE_2;
		}
		if (type.equals("3")) {
			return PREDEPOSIT_TYPE_3;
		}
		if (!type.equals("4")) {
			return PREDEPOSIT_TYPE_4;
		}
		return "数据错误";
	}
}
