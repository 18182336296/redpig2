package com.redpigmall.manage.admin.action.operative.freeGoods;

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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.FreeClass;
import com.redpigmall.domain.FreeGoods;
import com.redpigmall.domain.Goods;

/**
 * 
 * <p>
 * Title: RedPigFreeGoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 第三方申请的0元试用商品审核
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
 * @date 2014年11月12日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigFreeGoodsManageAction extends BaseAction{

	/**
	 * 0元试用商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param free_name
	 * @param beginTime
	 * @param endTime
	 * @param cls
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品列表", value = "/freegoods_list*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping({ "/freegoods_list" })
	public ModelAndView freegoods_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String free_name, 
			String beginTime,
			String endTime, 
			String cls, 
			String status) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/freegoods_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		if ((free_name != null) && (!free_name.equals(""))) {
			params.put("free_name_like", free_name);
			
			mv.addObject("free_name", free_name);
		}
		
		if ((cls != null) && (!cls.equals(""))) {
			params.put("class_id", CommUtil.null2Long(cls));
			
			mv.addObject("cls_id", cls);
		}
		
		if ((status != null) && (status.equals("going"))) {
			params.put("freeStatus", 5);
			
			mv.addObject("status", status);
		}
		
		if ((status != null) && (status.equals("finish"))) {
			params.put("freeStatus", 10);
			
			mv.addObject("status", status);
		}
		
		if ((status != null) && (status.equals("failed"))) {
			params.put("freeStatus", -5);
			
			mv.addObject("status", status);
		}
		
		if ((status != null) && (status.equals("waiting"))) {
			params.put("freeStatus", 0);
			
			mv.addObject("status", status);
		}
		
		if ((beginTime != null) && (!beginTime.equals(""))) {
			params.put("beginTime_more_than_equal", CommUtil.formatDate(beginTime));
			
			mv.addObject("beginTime", beginTime);
		}
		
		if ((endTime != null) && (!endTime.equals(""))) {
			params.put("endTime_less_than_equal", CommUtil.formatDate(endTime));
			
			mv.addObject("endTime", endTime);
		}
		
		
		IPageList pList = this.redPigFreegoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		params.clear();
		List<FreeClass> fcls = this.redPigFreeClassService.queryPageList(params);
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", this.redPigFreeTools);
		return mv;
	}
	
	/**
	 * 0元试用商品审核
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品审核", value = "/freegoods_add*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping({ "/freegoods_edit" })
	public ModelAndView freegoods_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/freegoods_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			FreeGoods freegoods = this.redPigFreegoodsService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			Goods goods = this.redPigGoodsService.selectByPrimaryKey(freegoods.getGoods_id());
			Map<String,Object> params = Maps.newHashMap();
			List<FreeClass> fcls = this.redPigFreeClassService.queryPageList(params);
			mv.addObject("goods", goods);
			mv.addObject("fcls", fcls);
			mv.addObject("obj", freegoods);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 0元试用商品添加
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_ur
	 * @param status
	 * @param failed_reason
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品添加", value = "/freegoods_add*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping({ "/freegoods_save" })
	public ModelAndView freegoods_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String cmd, 
			String list_url, 
			String add_ur, 
			String status,
			String failed_reason) {
		
		FreeGoods freegoods = null;
		if (id.equals("")) {
			freegoods = (FreeGoods) WebForm.toPo(request, FreeGoods.class);
			freegoods.setAddTime(new Date());
		} else {
			FreeGoods obj = this.redPigFreegoodsService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			freegoods = (FreeGoods) WebForm.toPo(request, obj);
		}
		
		freegoods.setFreeStatus(CommUtil.null2Int(status));
		freegoods.setFailed_reason(failed_reason);
		if (id.equals("")) {
			this.redPigFreegoodsService.saveEntity(freegoods);
		} else {
			this.redPigFreegoodsService.updateById(freegoods);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/freegoods_list");
		mv.addObject("op_title", "审核0元试用成功");
		return mv;
	}
	
	/**
	 * 0元试用商品申请列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param id
	 * @param userName
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品申请列表", value = "/freegoods_add*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping({ "/freeapply_logs" })
	public ModelAndView freeapply_logs(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String id, 
			String userName, 
			String status) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/freeapply_logs.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("freegoods_id", CommUtil.null2Long(id));
		
		if ((userName != null) && (!userName.equals(""))) {
			maps.put("user_name_like", userName);
			
			mv.addObject("userName", userName);
		}
		
		if ((status != null) && (status.equals("yes"))) {
			maps.put("apply_status", 5);
			
			mv.addObject("status", status);
		}
		
		if ((status != null) && (status.equals("waiting"))) {
			maps.put("apply_status", 0);
			
			mv.addObject("status", status);
		}
		
		if ((status != null) && (status.equals("no"))) {
			maps.put("apply_status", -5);
			
			mv.addObject("status", status);
		}
		
		IPageList pList = this.redPigFreeapplylogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/freeapply_logs.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 0元试用商品申请详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品申请详情", value = "/apply_log_info*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping({ "/apply_log_info" })
	public ModelAndView apply_log_info(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/apply_log_info.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			FreeApplyLog freeapplylog = this.redPigFreeapplylogService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", freeapplylog);
		}
		return mv;
	}
	
	/**
	 * 0元试用活动关闭
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用活动关闭", value = "/free_close*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping({ "/free_close" })
	public String free_close(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		FreeGoods fg = this.redPigFreegoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		fg.setFreeStatus(10);
		this.redPigFreegoodsService.updateById(fg);
		return "redirect:freegoods_list?currentPage=" + currentPage;
	}
	
	/**
	 * 0元试用活动删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用活动删除", value = "/freegoods_del*", rtype = "admin", rname = "0元试用管理", rcode = "freegoods_admin", rgroup = "运营")
	@RequestMapping({ "/freegoods_del" })
	public String freegoods_del(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mulitId, 
			String currentPage) {
		
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				FreeGoods fg = this.redPigFreegoodsService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				if (fg != null) {
					this.redPigFreegoodsService.deleteById(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:freegoods_list?currentPage=" + currentPage;
	}
}
