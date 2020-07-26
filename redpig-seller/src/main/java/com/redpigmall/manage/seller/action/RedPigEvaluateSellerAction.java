package com.redpigmall.manage.seller.action;

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
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigEvaluateSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家评价管理控制器，
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
public class RedPigEvaluateSellerAction extends BaseAction {
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
	@SecurityMapping(title = "商品评价列表", value = "/evaluate_list*", rtype = "seller", rname = "评价管理", rcode = "evaluate_seller", rgroup = "客户服务")
	@RequestMapping({ "/evaluate_list" })
	public ModelAndView evaluate_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/evaluate_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		maps.put("evaluate_goods_goods_store_id", store.getId());
		maps.put("evaluate_status", 0);
		maps.put("evaluate_type", "goods");
		
		if ("yes".equals(status)) {
			maps.put("reply_status", 1);
			mv.addObject("status", status);
		}
		
		if ("no".equals(status)) {
			maps.put("reply_status", 0);
			mv.addObject("status", status);
		}
		
		IPageList pList = this.evaluateService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 商品评价内容
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品评价内容", value = "/evaluate_info*", rtype = "seller", rname = "评价管理", rcode = "evaluate_seller", rgroup = "客户服务")
	@RequestMapping({ "/evaluate_info" })
	public ModelAndView evaluate_info(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/evaluate_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Evaluate evl = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((evl != null)
				&& (evl.getOf().getStore_id().equals(user.getStore().getId()
						.toString()))) {
			mv.addObject("evl", evl);
			mv.addObject("imageTools", this.imageTools);
		} else {
			mv.addObject("ret", Integer.valueOf(0));
		}
		return mv;
	}
	
	/**
	 * 商品评价内容
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品评价内容", value = "/evaluate_reply*", rtype = "seller", rname = "评价管理", rcode = "evaluate_seller", rgroup = "客户服务")
	@RequestMapping({ "/evaluate_reply" })
	public ModelAndView evaluate_reply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/evaluate_reply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Evaluate evl = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((evl != null)
				&& (evl.getOf().getStore_id().equals(user.getStore().getId()
						.toString()))) {
			mv.addObject("evl", evl);
		} else {
			mv.addObject("ret", Integer.valueOf(0));
		}
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
	@SecurityMapping(title = "商品评价内容", value = "/evaluate_reply_save*", rtype = "seller", rname = "评价管理", rcode = "evaluate_seller", rgroup = "客户服务")
	@RequestMapping({ "/evaluate_reply_save" })
	public void evaluate_reply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String reply) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Evaluate evl = this.evaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((evl != null)
				&& (evl.getOf().getStore_id().equals(user.getStore().getId()
						.toString()))) {
			evl.setReply(CommUtil.filterHTML(reply));
			evl.setReply_status(1);
			this.evaluateService.updateById(evl);
		}
	}
}
