package com.redpigmall.manage.admin.action.operative.combin;

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
import com.redpigmall.domain.CombinPlan;

/**
 * 
 * <p>
 * Title: RedPigCombinManageAction.java
 * </p>
 * 
 * <p>
 * Description: 组合销售平台控制器
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
 * @date 2014-9-19
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCombinManageAction extends BaseAction{

	/**
	 * 组合销售商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param combin_status
	 * @param beginTime
	 * @param endTime
	 * @param goods_name
	 * @param type
	 * @param combin_form
	 * @return
	 */
	@SecurityMapping(title = "组合销售商品列表", value = "/combin*", rtype = "admin", rname = "组合销售", rcode = "combin_manage", rgroup = "运营")
	@RequestMapping({ "/combin" })
	public ModelAndView combin(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage,
			String combin_status, 
			String beginTime, 
			String endTime,
			String goods_name, 
			String type, 
			String combin_form) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/combin_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, 10,"addTime", "desc");
		
		if ((combin_status != null) && (!combin_status.equals(""))) {
			params.put("combin_status", CommUtil.null2Int(combin_status));
			
			mv.addObject("combin_status", combin_status);
		}
		
		if ((combin_form != null) && (!combin_form.equals(""))) {
			params.put("combin_form", CommUtil.null2Int(combin_form));
			mv.addObject("combin_form", combin_form);
		}
		
		if ((goods_name != null) && (!goods_name.equals(""))) {
			params.put("main_goods_name_like", CommUtil.null2String(goods_name));
			
			mv.addObject("goods_name", goods_name);
		}
		
		if ((beginTime != null) && (!beginTime.equals(""))) {
			params.put("beginTime_more_than_equal", CommUtil.formatDate(beginTime));
			
			mv.addObject("beginTime", beginTime);
		}
		if ((endTime != null) && (!endTime.equals(""))) {
			params.put("endTime_less_than_equal", CommUtil.formatDate(endTime));
			
			mv.addObject("endTime", endTime);
		}
		if ((type != null) && (!type.equals(""))) {
			params.put("combin_type", CommUtil.null2Int(type));
			
			mv.addObject("type", type);
		} else {
			params.put("combin_type", 0);
		}
		
		IPageList pList = this.redPigCombinplanService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("combinTools", this.redPigCombinTools);
		return mv;
	}
	
	/**
	 * 组合销售审核通过
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "组合销售审核通过", value = "/combin_goods_audit*", rtype = "admin", rname = "组合销售", rcode = "combin_manage", rgroup = "运营")
	@RequestMapping({ "/combin_goods_audit" })
	public String combin_goods_audit(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage, String type) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				CombinPlan obj = this.redPigCombinplanService.selectByPrimaryKey(CommUtil.null2Long(id));
				obj.setCombin_status(1);
				this.redPigCombinplanService.updateById(obj);
			}
		}
		return "redirect:/combin?currentPage=" + currentPage + "&type=" + type;
	}
	
	/**
	 * 组合销售审核拒绝
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param refuse_msg
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "组合销售审核拒绝", value = "/combin_goods_refuse*", rtype = "admin", rname = "组合销售", rcode = "combin_manage", rgroup = "运营")
	@RequestMapping({ "/combin_goods_refuse" })
	public String combin_goods_refuse(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage, String refuse_msg, String type) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				CombinPlan obj = this.redPigCombinplanService.selectByPrimaryKey(CommUtil.null2Long(id));
				obj.setCombin_status(-1);
				if ((refuse_msg != null) && (!refuse_msg.equals(""))) {
					obj.setCombin_refuse_msg(refuse_msg);
				}
				this.redPigCombinplanService.updateById(obj);
			}
		}
		return "redirect:/combin?currentPage=" + currentPage + "&type=" + type;
	}
}
