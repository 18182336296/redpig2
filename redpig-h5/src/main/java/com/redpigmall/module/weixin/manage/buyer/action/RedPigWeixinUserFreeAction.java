package com.redpigmall.module.weixin.manage.buyer.action;

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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.weixin.manage.base.BaseAction;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.FreeGoods;
import com.redpigmall.domain.User;

/**

 * <p>
 * Title: RedPigWapUserGroupInfoAction.java
 * </p>
 * 
 * <p>
 * Description: wap端用户中心0元试用
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
 * @date 2015年1月6日
 * 
 * @version b2b2c_2015
 */
@Controller
public class RedPigWeixinUserFreeAction extends BaseAction{
	
	
	/**
	 * 移动端户中心团购列表
	 * @param request
	 * @param response
	 * @param status
	 * @param begin_num
	 * @param count_num
	 * @return
	 */
	@SecurityMapping(title = "移动端用户中心0元试用列表", value = "/buyer/free_list*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端户中心0元试用")
	@RequestMapping({ "/buyer/free_list" })
	public ModelAndView free_list(HttpServletRequest request,
			HttpServletResponse response, String apply_status,
			String evaluate_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/freegoods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", user.getId());
		
		if (CommUtil.null2String(apply_status).equals("0")) {
			map.put("apply_status", Integer.valueOf(0));
		}
		
		if (CommUtil.null2String(apply_status).equals("5")) {
			map.put("apply_status", Integer.valueOf(5));
		}
		
		if (CommUtil.null2String(apply_status).equals("-5")) {
			map.put("apply_status", Integer.valueOf(-5));
		}
		
		if (CommUtil.null2String(evaluate_status).equals("0")) {
			map.put("evaluate_status", Integer.valueOf(0));
		}
		
		if (CommUtil.null2String(evaluate_status).equals("1")) {
			map.put("evaluate_status", Integer.valueOf(1));
		}
		
		if (CommUtil.null2String(evaluate_status).equals("2")) {
			map.put("evaluate_status", Integer.valueOf(2));
		}
		
		List<FreeApplyLog> applyLogs = this.applyLogService.queryPageList(map, 0,12);
		mv.addObject("applyLogs", applyLogs);
		mv.addObject("this", this);
		mv.addObject("freeTools", this.freeTools);
		mv.addObject("apply_status", apply_status);

		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param apply_status
	 * @param evaluate_status
	 * @param begin_count
	 * @return
	 */
	@RequestMapping({ "/buyer/freeapply_data" })
	public ModelAndView freeapply_data(HttpServletRequest request,
			HttpServletResponse response, String apply_status,
			String evaluate_status, String begin_count) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/freegoods_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> map = Maps.newHashMap();
		map.put("user_id", user.getId());
		
		if (!CommUtil.null2String(apply_status).equals("0")) {
			map.put("apply_status", Integer.valueOf(0));
		}
		if (!CommUtil.null2String(apply_status).equals("5")) {
			map.put("apply_status", Integer.valueOf(5));
		}
		if (!CommUtil.null2String(apply_status).equals("-5")) {
			map.put("apply_status", Integer.valueOf(-5));
		}
		if (!CommUtil.null2String(evaluate_status).equals("0")) {
			map.put("evaluate_status", Integer.valueOf(0));
		}
		if (!CommUtil.null2String(evaluate_status).equals("1")) {
			map.put("evaluate_status", Integer.valueOf(1));
		}
		if (!CommUtil.null2String(evaluate_status).equals("2")) {
			map.put("evaluate_status", Integer.valueOf(2));
		}
		
		List<FreeApplyLog> applyLogs = this.applyLogService.queryPageList(map, CommUtil.null2Int(begin_count), 12);
		
		mv.addObject("applyLogs", applyLogs);
		mv.addObject("this", this);
		mv.addObject("apply_status", apply_status);
		mv.addObject("evaluate_status", evaluate_status);
		return mv;
	}
	
	/**
	 * 移动端户中心0元试用详细
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "移动端户中心0元试用详细", value = "/buyer/free_detail*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端户中心0元试用")
	@RequestMapping({ "/buyer/free_detail" })
	public ModelAndView free_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/freegoods_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (!CommUtil.null2String(id).equals("")) {
			FreeApplyLog applyLog = this.applyLogService.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("applyLog", applyLog);
			mv.addObject("this", this);
			if (applyLog != null) {
				FreeGoods freeGoods = this.freeGoodsService.selectByPrimaryKey(applyLog
						.getFreegoods_id());
				mv.addObject("freeGoods", freeGoods);
			}
		}
		return mv;
	}
	
	/**
	 * 移动端户中心0元试用评论保存
	 * @param request
	 * @param response
	 * @param id
	 * @param use_experience
	 * @return
	 */
	@SecurityMapping(title = "移动端户中心0元试用评论保存", value = "/buyer/free_save*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端户中心0元试用")
	@RequestMapping({ "/buyer/free_save" })
	public ModelAndView free_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String use_experience) {
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (!CommUtil.null2String(id).equals("")) {
			FreeApplyLog applyLog = this.applyLogService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (applyLog.getEvaluate_status() == 0) {
				applyLog.setUse_experience(use_experience);
				applyLog.setEvaluate_time(new Date());
				applyLog.setEvaluate_status(1);
				this.applyLogService.updateById(applyLog);
				mv.addObject("op_title", "0元试用评论成功！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/free_list");
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/free_list");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/free_list");
		}
		return mv;
	}

	public FreeGoods queryFreeGoods(String id) {
		FreeGoods freeGoods = this.freeGoodsService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		return freeGoods;
	}
}
