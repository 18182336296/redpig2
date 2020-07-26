package com.redpigmall.view.web.action;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.User;
import com.redpigmall.manage.buyer.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigActivityViewAction.java
 * </p>
 * 
 * <p>
 * Description:商城活动前台管理控制控制器
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
 * @date 2015-9-24
 * 
 * @version redpig 2016
 */
@Controller
public class RedPigActivityViewAction extends BaseAction{
	
	/**
	 * 活动
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping({ "/activity/index" })
	public ModelAndView activity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("activity.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<Map> level_list = this.IntegralViewTools.query_all_level();
		mv.addObject("level_list", level_list);
		Map<String, Object> params = Maps.newHashMap();
		params.put("orderBy", "ac_sequence");
		params.put("orderType", "asc");
		params.put("ac_status", Integer.valueOf(1));
		
		List<Activity> acts = this.activityService.queryPageList(params);
		
		if (acts.size() > 0) {
			List list = JSON.parseArray(acts.get(0).getAc_rebate_json());
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					if (map != null) {
						mv.addObject("list", list);
					}
				}
			}
			if (id == null) {
				id = CommUtil.null2String(acts.get(0).getId());
				mv.addObject("op", "true");
			}
			Activity act = this.activityService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (act != null) {
				if (act.getAc_status() == 1) {
					Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage,20, "addTime", "desc");
					maps.put("ag_status", 1);
					maps.put("act_id", act.getId());
					maps.put("act_id", act.getId());
					maps.put("ag_goods_goods_status", 0);
					
					IPageList pList = this.activityGoodsService.list(maps);
					
					CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
					mv.addObject("act", act);
					mv.addObject("activityViewTools", this.activityViewTools);
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "活动尚未开启");
					mv.addObject("url", CommUtil.getURL(request) + "/index");
					return mv;
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，活动查看失败");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
			mv.addObject("acts", acts);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统未开启任何商城活动");
			mv.addObject("url", CommUtil.getURL(request) + "/index");

			return mv;
		}
		return mv;
	}
	
	/**
	 * 满就送
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/buygift/index" })
	public ModelAndView buygift(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("buygift.html", 
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		BuyGift bg = this.buyGiftService.selectByPrimaryKey(CommUtil.null2Long(id));//根据ID查询满就送
		if (bg != null) {
			if (bg.getGift_status() == 10) {//审核通过
				mv.addObject("obj", bg);
			} else {//未审核通过或者待审核等
				mv = new RedPigJModelAndView("error.html", this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request, response);
				mv.addObject("op_title", "参数错误，活动查看失败");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
		} else {//没有查到满就送
			mv = new RedPigJModelAndView("error.html", this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title", "参数错误，活动查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		return mv;
	}
	
	/**
	 * 满就减
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/enoughreduce/index" })
	public ModelAndView enoughreduce(HttpServletRequest request, HttpServletResponse response, String id,String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("enoughreduceview.html", 
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((er == null) || (er.getErstatus() != 10)) {
			
			mv = new RedPigJModelAndView("error.html",
					configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request, response);
			mv.addObject("op_title","未发现其他满就减活动");
			return mv;
		}
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, 20, null, null);
		maps.put("order_enough_reduce_id", id);
		
		IPageList pList = this.goodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("currentPage", currentPage);
		mv.addObject("user", user);
		mv.addObject("er", er);
		return mv;
	}
	
	/**
	 * 直通车
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @return
	 */
	@RequestMapping({ "/ztc_goods_list" })
	public ModelAndView ztc_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id) {
		ModelAndView mv = null;
		if (this.configService.getSysConfig().getZtc_status()) {
			mv = new RedPigJModelAndView("ztc_goods_list.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,24, "ztc_dredge_price", "desc");
	        maps.put("ztc_status", 3);
	        maps.put("ztc_begin_time_less_than_equal", new Date());
	        maps.put("ztc_gold_more_than", 0);
	        
			if ((gc_id != null)
					&& (!"".equals(gc_id))
					&& (this.configService.getSysConfig().getZtc_goods_view() == 1)) {
				mv.addObject("gc_id", gc_id);
				GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
				Set<Long> ids = null;
				if (gc != null) {
					ids = genericIds(gc.getId());
				}
				if ((ids != null) && (ids.size() > 0)) {
					maps.put("gc_ids", ids);
				}
			}
			IPageList pList = this.goodsService.queryPagesWithNoRelations(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			mv.addObject("op_title", "商城直通车未开通");
		}
		return mv;
	}
	
	/**
	 * 迭代ID
	 * @param id
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Set<Long> genericIds(Long id) {
		Set<Long> ids = new HashSet<Long>();
		if (id != null) {
			ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", id);

			List id_list = this.goodsClassService.queryPageList(params);

			ids.addAll(id_list);
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i));
				Set<Long> cids = genericIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
	}
}
