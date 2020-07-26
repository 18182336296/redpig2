package com.redpigmall.manage.seller.action;

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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: ActivitySellerAction.java
 * </p>
 * 
 * <p>
 * Description:商家活动管理控制器，商家可以申请活动套餐，申请完成后可以发布活动唱片，活动会在商家店铺页面显示
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
 * @date 2014-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigActivitySellerAction extends BaseAction{
	/**
	 * 活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "活动列表", value = "/activity*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping({ "/activity" })
	public ModelAndView activity(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/activity.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 1,"ac_sequence", "asc");
		
		maps.put("ac_status", 1);
//		maps.put("ac_begin_time_less_than_equal", new Date());
//		maps.put("ac_end_time_more_than_equal", new Date());
		
		IPageList pList = this.activityService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("activityViewTools", this.activityViewTools);
		return mv;
	}
	
	/**
	 * 申请参加活动
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "申请参加活动", value = "/activity_apply*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping({ "/activity_apply" })
	public ModelAndView activity_apply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/activity_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean ret = false;
		if ((id != null) && (!id.equals(""))) {
			Activity act = this.activityService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (act != null) {
				if (act.getAc_status() == 1) {
					ret = true;
				}
				if (act.getAc_begin_time().before(new Date())) {
					ret = true;
				}
				if (act.getAc_end_time().after(new Date())) {
					ret = true;
				}
				if (ret) {
					mv.addObject("act", act);
				}
			}
		}
		if (!ret) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "活动参数不正确");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/activity?id=" + id);
		}
		return mv;
	}
	
	/**
	 * 活动商品加载
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "活动商品加载", value = "/activity_goods*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping({ "/activity_goods" })
	public ModelAndView activity_goods(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/activity_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 15,null, null);
		
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("goods_name_like", CommUtil.null2String(goods_name));
			
			mv.addObject("goods_name", goods_name);
		}
		
		maps.put("store_id", store.getId());
		
		this.queryTools.shieldGoodsStatus(maps, null);
		
		IPageList pList = this.goodsService.list(maps);
		
		String url = CommUtil.getURL(request) + "/activity_goods";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url,
				"&goods_name=" + goods_name, pList.getCurrentPage(),
				pList.getPages(), pList.getPageSize()));
		return mv;
	}
	
	/**
	 * 申请参加活动
	 * @param request
	 * @param response
	 * @param goods_ids
	 * @param act_id
	 * @return
	 */
	@SecurityMapping(title = "申请参加活动", value = "/activity_apply_save*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping({ "/activity_apply_save" })
	public String activity_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String goods_ids, String act_id) {
		String url = "redirect:/success";
		System.out.println(goods_ids);
		if ((goods_ids != null) && (!goods_ids.equals(""))) {
			Activity act = this.activityService.selectByPrimaryKey(CommUtil.null2Long(act_id));
			String[] ids = goods_ids.split(",");
			for (String id : ids) {

				if (!id.equals("")) {
					ActivityGoods ag = new ActivityGoods();
					Map<String,Object> params = Maps.newHashMap();
					params.put("ag_goods_id", CommUtil.null2Long(id));
					params.put("act_id", CommUtil.null2Long(act_id));
					
					List<ActivityGoods> ags = this.activityGoodsService.queryPageList(params, 0, 1);
					
					if (ags.size() > 0) {
						ag = (ActivityGoods) ags.get(0);
					}
					
					ag.setAddTime(new Date());
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
					
					ag.setAg_goods(goods);
					ag.setAct(act);
					ag.setAg_status(0);
					
					if (ags.size() > 0) {
						this.activityGoodsService.updateById(ag);
					} else {
						this.activityGoodsService.saveEntity(ag);
					}
					
					goods.setActivity_status(1);
					goods.setActivity_goods_id(ag.getId());
					this.goodsService.updateById(goods);
				}
			}
			request.getSession(false).setAttribute("url",CommUtil.getURL(request) + "/activity");
			
			request.getSession(false).setAttribute("op_title", "申请参加活动成功");
		} else {
			url = "redirect:/error";
			request.getSession(false).setAttribute("url",CommUtil.getURL(request) + "/activity");
			request.getSession(false).setAttribute("op_title", "至少选择一件商品");
		}
		return url;
	}
	
	/**
	 * 活动商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param act_id
	 * @return
	 */
	@SecurityMapping(title = "活动商品列表", value = "/activity_goods_list*", rtype = "seller", rname = "商城活动", rcode = "activity_seller", rgroup = "促销推广")
	@RequestMapping({ "/activity_goods_list" })
	public ModelAndView activity_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String act_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/activity_goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Activity act = this.activityService.selectByPrimaryKey(CommUtil.null2Long(act_id));
		
		if (act != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 30, "addTime", "desc");
			maps.put("ag_goods_goods_store_user_id", user.getId());
			
			maps.put("act_id", CommUtil.null2Long(act_id));
			
			
			IPageList pList = this.activityGoodsService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		mv.addObject("act_id", act_id);
		return mv;
	}
}
