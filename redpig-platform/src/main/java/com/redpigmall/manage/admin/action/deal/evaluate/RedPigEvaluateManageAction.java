package com.redpigmall.manage.admin.action.deal.evaluate;

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
 * Title: RedPigEvaluateManageAction.java
 * </p>
 * 
 * <p>
 * Description: 系统商品评价管理类
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
 * @date 2014年5月27日
 * 
 * @version 1.0
 */
@Controller
public class RedPigEvaluateManageAction extends BaseAction{

	/**
	 * 商品评价列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_name
	 * @param userName
	 * @return
	 */
	@SecurityMapping(title = "商品评价列表", value = "/evaluate_list*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping({ "/evaluate_list" })
	public ModelAndView evaluate_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String goods_name, String userName) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/evaluate_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			params.put("evaluate_goods_goods_name_like", goods_name);
			
		}
		if (!CommUtil.null2String(userName).equals("")) {
			params.put("evaluate_user_userName", userName);
			
		}
		mv.addObject("goods_name", goods_name);
		mv.addObject("userName", userName);
		IPageList pList = this.redPigEvaluateService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 商品评价编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品评价编辑", value = "/evaluate_edit*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping({ "/evaluate_edit" })
	public ModelAndView evaluate_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/evaluate_edit.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Evaluate obj = this.redPigEvaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		mv.addObject("evaluateViewTools", this.redPigEvaluateViewTools);
		return mv;
	}
	
	/**
	 * 商品评价编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param evaluate_status
	 * @param evaluate_admin_info
	 * @param list_url
	 * @param edit
	 * @return
	 */
	@SecurityMapping(title = "商品评价编辑", value = "/evaluate_save*", rtype = "admin", rname = "商品评价", rcode = "evaluate_admin", rgroup = "交易")
	@RequestMapping({ "/evaluate_save" })
	public ModelAndView evaluate_save(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String evaluate_status, String evaluate_admin_info,
			String list_url, String edit) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Evaluate obj = this.redPigEvaluateService.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setEvaluate_admin_info(evaluate_admin_info);
		obj.setEvaluate_status(CommUtil.null2Int(evaluate_status));
		this.redPigEvaluateService.updateById(obj);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "商品评价编辑成功");
		return mv;
	}
}
