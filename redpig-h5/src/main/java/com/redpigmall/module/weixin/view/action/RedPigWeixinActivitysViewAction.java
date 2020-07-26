package com.redpigmall.module.weixin.view.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.module.weixin.view.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title: RedPigWeixinActivitysViewAction.java
 * </p>
 * 
 * <p>
 * Description:移动端活动列表以及参加活动商品列表
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
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinActivitysViewAction extends BaseAction{
	
	/**
	 * 活动列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/activitys" })
	public ModelAndView activitys(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/activitys.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = Maps.newHashMap();
		params.put("ac_begin_time_less_than_equal", new Date());
		params.put("ac_end_time_more_than_equal", new Date());
		params.put("ac_status", Integer.valueOf(1));
		
		List<Activity> activitys = this.activityService.queryPageList(params, 0, 12);
		
		mv.addObject("activitys", activitys);
		return mv;
	}
	/**
	 * 活动列表ajax加载数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/activitys_ajax" })
	public ModelAndView activitys_ajax(HttpServletRequest request,
			HttpServletResponse response, String begin_count) {
		ModelAndView mv = new RedPigJModelAndView("weixin/activitys_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = Maps.newHashMap();
		params.put("ac_begin_time_less_than_equal", new Date());
		params.put("ac_end_time_more_than_equal", new Date());
		params.put("ac_status", Integer.valueOf(1));
		
		List<Activity> activitys = this.activityService.queryPageList(params, CommUtil.null2Int(begin_count), 6);
		
		mv.addObject("activitys", activitys);
		return mv;
	}
	
	/**
	 * 活动商品
	 * @param request
	 * @param response
	 * @param act_id
	 * @return
	 */
	@RequestMapping({ "/activitys_goods" })
	public ModelAndView activitys_goods(HttpServletRequest request,
			HttpServletResponse response, String act_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/activitys_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = Maps.newHashMap();
		params.clear();
		params.put("ag_status", Integer.valueOf(1));
		params.put("ag_goods_goods_status", Integer.valueOf(0));
		params.put("act_id", CommUtil.null2Long(act_id));
		
		List<ActivityGoods> activitygoods = this.activityGoodsService.queryPageList(params, 0, 12);
		
		mv.addObject("activitygoods", activitygoods);
		mv.addObject("act_id", act_id);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	/**
	 * 活动商品
	 * @param request
	 * @param response
	 * @param act_id
	 * @return
	 */
	@RequestMapping({ "/activitys_items" })
	public ModelAndView activitys_items(HttpServletRequest request,
			HttpServletResponse response, String act_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/activitys_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = Maps.newHashMap();
		params.clear();
		params.put("ag_status", Integer.valueOf(1));
		params.put("ag_goods_goods_status", Integer.valueOf(0));
		
		params.put("act_id", CommUtil.null2Long(act_id));
		
		List<ActivityGoods> activitygoods = this.activityGoodsService.queryPageList(params, 0, 12);
		
		mv.addObject("activitygoods", activitygoods);
		mv.addObject("act_id", act_id);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	/**
	 * ajax活动商品
	 * @param request
	 * @param response
	 * @param act_id
	 * @param begin_count
	 * @return
	 */
	@RequestMapping({ "/activitys_goods_ajax" })
	public ModelAndView activitys_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String act_id, String begin_count) {
		ModelAndView mv = new RedPigJModelAndView("weixin/activitys_goods_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map params = Maps.newHashMap();
		params.clear();
		params.put("ag_status", Integer.valueOf(1));
		params.put("ag_goods_goods_status", Integer.valueOf(0));
		params.put("act_id", CommUtil.null2Long(act_id));
		
		List<ActivityGoods> activitygoods = this.activityGoodsService.queryPageList(params, CommUtil.null2Int(begin_count), 6);
		
		mv.addObject("activitygoods", activitygoods);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/buygift/index" })
	public ModelAndView detail_activity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/detail_activity.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		BuyGift bg = this.buyGiftService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (bg != null) {
			if (bg.getGift_status() == 10) {
				mv.addObject("obj", bg);
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，活动查看失败");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，活动查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		return mv;
	}

	@RequestMapping({ "/enoughreduce/index" })
	public ModelAndView enoughreduce(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/detail_activity.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((er == null) || (er.getErstatus() != 10)) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，活动查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		mv.addObject("user", SecurityUserHolder.getCurrentUser());
		mv.addObject("er", er);
		return mv;
	}
}
