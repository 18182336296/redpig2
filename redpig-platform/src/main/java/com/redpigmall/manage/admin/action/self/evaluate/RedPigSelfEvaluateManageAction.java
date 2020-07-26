package com.redpigmall.manage.admin.action.self.evaluate;

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
import com.redpigmall.domain.Evaluate;

/**
 * 
 * <p>
 * Title: RedPigSelfEvaluateManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台评价回复控制器。
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
 * @date 2014-12-3
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSelfEvaluateManageAction extends BaseAction {
	/**
	 * 商品评价列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "商品评价列表", value = "/self_evaluate_list*", rtype = "admin", rname = "评价管理", rcode = "self_evaluate_admin", rgroup = "自营")
	@RequestMapping({ "/self_evaluate_list" })
	public ModelAndView self_evaluate_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_evaluate_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		params.put("evaluate_goods_goods_type", 0);
		params.put("evaluate_status", 0);
		params.put("evaluate_type", "goods");
		
		if ("yes".equals(status)) {
			
			params.put("reply_status", 1);
			mv.addObject("status", status);
		}
		if ("no".equals(status)) {
			
			params.put("reply_status", 0);
			mv.addObject("status", status);
		}
		
		IPageList pList = this.evaluateService.list(params);
		String url = this.redPigSysConfigService.getSysConfig().getAdminUrl();
		CommUtil.saveIPageList2ModelAndView(url+"self_evaluate_list.html", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 商品评价内容
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品评价内容", value = "/self_evaluate_info*", rtype = "admin", rname = "评价管理", rcode = "self_evaluate_admin", rgroup = "自营")
	@RequestMapping({ "/self_evaluate_info" })
	public ModelAndView self_evaluate_info(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_evaluate_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate evl = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("evl", evl);
		mv.addObject("imageTools", this.imageTools);
		return mv;
	}
	
	/**
	 * 商品评价内容
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品评价内容", value = "/self_evaluate_reply*", rtype = "admin", rname = "评价管理", rcode = "self_evaluate_admin", rgroup = "自营")
	@RequestMapping({ "/self_evaluate_reply" })
	public ModelAndView evaluate_reply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_evaluate_reply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Evaluate evl = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("evl", evl);
		mv.addObject("id", id);
		return mv;
	}
	
	/**
	 * 商品评价内容
	 * @param request
	 * @param response
	 * @param id
	 * @param reply
	 */
	@SecurityMapping(title = "商品评价内容", value = "/self_evaluate_reply_save*", rtype = "admin", rname = "评价管理", rcode = "self_evaluate_admin", rgroup = "自营")
	@RequestMapping({ "/self_evaluate_reply_save" })
	public void evaluate_reply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String reply) {
		Evaluate evl = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (evl != null) {
			evl.setReply(CommUtil.filterHTML(reply));
			evl.setReply_status(1);
			this.evaluateService.updateById(evl);
		}
	}
}
