package com.redpigmall.manage.admin.action.self.activity;

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
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.Goods;

/**
 * 
 * <p>
 * Title: RedPigSelfActivityManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营活动管理类
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
 * @date 2014年5月21日
 * 
 * @version redpigmall_b2b2c 8.0
 */

@Controller
public class RedPigSelfActivityManageAction extends BaseAction{
	/**
	 * 自营活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "自营活动列表", value = "/group_self*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping({ "/activity_self" })
	public ModelAndView activity_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/activity_self.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		IPageList pList = this.activityService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 自营活动申请
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "自营活动申请", value = "/activity_self_apply*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping({ "/activity_self_apply" })
	public ModelAndView activity_apply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/activity_self_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Activity act = this.activityService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("act", act);
		return mv;
	}
	
	/**
	 * 自营活动商品加载
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营活动商品加载", value = "/activity_self_goods_load*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping({ "/activity_self_goods_load" })
	public ModelAndView activity_self_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/activity_self_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage,15, null, null);
		
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			params.put("goods_name_like", CommUtil.null2String(goods_name));
			mv.addObject("goods_name", goods_name);
		}
		
		params.put("goods_type", 0);
		this.queryTools.shieldGoodsStatus(params, null);
		
		
		IPageList pList = this.goodService.list(params);
		String url = CommUtil.getURL(request)
				+ "/activity_self_goods_load";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		return mv;
	}
	
	/**
	 * 自营活动商品保存
	 * @param request
	 * @param response
	 * @param goods_ids
	 * @param act_id
	 * @return
	 */
	@SecurityMapping(title = "自营活动商品保存", value = "/activity_self_apply_save*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping({ "/activity_self_apply_save" })
	public ModelAndView activity_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String goods_ids, String act_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Activity act = this.activityService.selectByPrimaryKey(CommUtil
				.null2Long(act_id));
		String[] ids = goods_ids.split(",");
		for (String id : ids) {

			if (!id.equals("")) {
				Map<String,Object> params = Maps.newHashMap();
				params.put("ag_goods_id", CommUtil.null2Long(id));
				List<ActivityGoods> ags = this.activityGoodsService.queryPageList(params);
				for (ActivityGoods ag : ags) {
					this.activityGoodsService.deleteById(ag.getId());
				}
				ActivityGoods ag = new ActivityGoods();
				ag.setAddTime(new Date());
				Goods goods = this.goodService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				ag.setAg_goods(goods);
				ag.setAg_status(1);
				ag.setAct(act);
				ag.setAg_type(1);
				this.activityGoodsService.saveEntity(ag);
				goods.setActivity_status(2);
				goods.setActivity_goods_id(ag.getId());
				this.goodService.updateById(goods);
			}
		}
		mv.addObject("op_title", "参加活动成功");
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/activity_self");
		return mv;
	}
	
	/**
	 * 活动商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param act_id
	 * @return
	 */
	@SecurityMapping(title = "活动商品列表", value = "/activity_self_goods_list*", rtype = "admin", rname = "活动管理", rcode = "activity_self", rgroup = "自营")
	@RequestMapping({ "/activity_self_goods_list" })
	public ModelAndView activity_goods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String act_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/activity_self_goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		params.put("ag_type", 1);
		if ((act_id != null) && (!act_id.equals(""))) {
			params.put("act_id", CommUtil.null2Long(act_id));
		}
		
		IPageList pList = this.activityGoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("act_id", act_id);
		return mv;
	}
}
