package com.redpigmall.manage.buyer.action.base;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.FootPoint;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.User;
import com.redpigmall.domain.virtual.FootPointView;
import com.redpigmall.domain.virtual.IntegralGoodsOrderView;


/**
 * 
 * <p>
 * Title: BaseBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家中心基础管理控制器
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
 * @date 2014-5-19
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class BaseBuyerAction extends BaseAction{
	
	/**
	 * * 买家首页并分页查询所有动态,可以根据type参数不同进行不同的条件查询，
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type
	 *            ：条件查询参数，type=1为查询自己，type=2为查询好友，type=3为查询相互关注
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "买家中心", value = "/buyer/index*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/index" })
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("orderViewTools", this.orderViewTools);
		Map<String, Object> params = Maps.newHashMap();
		if (user != null) {
			params.put("user_id", user.getId());
			params.put("status", Integer.valueOf(0));
			mv.addObject("couponInfos",Integer.valueOf(this.couponInfoService.selectCount(params)));
		}
		int[] status = { 10, 30, 50 };
		String[] string_status = { "order_submit", "order_shipping","order_finish" };
		Map<String,Object> orders_status = Maps.newLinkedHashMap();
		for (int i = 0; i < status.length; i++) {
			params.clear();
			params.put("order_cat_no", 2);
			params.put("user_id", user.getId().toString());
			params.put("order_status", status[i]);
			params.put("orderBy", "addTime");
			params.put("orderType", "desc");
			
			int size = this.orderFormService.selectCount(params);
			
			mv.addObject("order_size_" + status[i], Integer.valueOf(size));
			orders_status.put(string_status[i], Integer.valueOf(size));
		}
		params.clear();
		params.put("user_id", user.getId().toString());
		params.put("order_main", 1);
		params.put("order_status", 10);
		params.put("order_cat_no", 2);
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<OrderForm> orderForms = this.orderFormService.queryPageList(params, 0, 9);
		
		mv.addObject("orderForms", orderForms);

		params.clear();
		
		params.put("type", Integer.valueOf(0));
		params.put("user_id", user.getId());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Favorite> favorite_goods = this.favService.queryPageList(params, 0, 9);
		
		mv.addObject("favorite_goods", favorite_goods);
		
		params.clear();
		params.put("type", Integer.valueOf(1));
		params.put("user_id", user.getId());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Favorite> favorite_stores = this.favService.queryPageList(params, 0, 9);
		
		mv.addObject("favorite_stores", favorite_stores);
		mv.addObject("orders_status", orders_status);
		mv.addObject("user", user);
		mv.addObject("type", type);
		
		params.clear();
		params.put("status", 0);
		params.put("toUser_id", user.getId());
		params.put("parent", -1);
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		mv.addObject("msg_size", this.messageService.selectCount(params));
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("integralViewTools", this.integralViewTools);

		params.clear();
		params.put("igo_user", user.getId());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<IntegralGoodsOrder> igos = this.integralGoodsOrderService.queryPageList(params,0,9);
		
		List<IntegralGoodsOrderView> igois = Lists.newArrayList();
		
		for (IntegralGoodsOrder igo : igos) {
			IntegralGoodsOrderView igoi = new IntegralGoodsOrderView();
			igoi.setIgo_order_id(igo.getId());
			igoi.setIgo_total_integral(igo.getIgo_total_integral());
			List<Map> maps = this.orderFormTools.query_integral_goodsinfo(igo.getGoods_info());
			
			for (Map map : maps) {
				igoi.setIgo_goods_name(CommUtil.null2String(map
						.get("ig_goods_name")));
				igoi.setIgo_goods_id(CommUtil.null2Long(map.get("id")));
				igoi.setIgo_goods_img(CommUtil.null2String(map
						.get("ig_goods_img")));
			}
			igois.add(igoi);
		}

		mv.addObject("igois", igois);

		params.clear();
		params.put("fp_user_id", user.getId());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<FootPoint> fps = this.footPointService.queryPageList(params, 0,6);
		
		List<FootPointView> fpvs = Lists.newArrayList();
		for (FootPoint fp : fps) {
			List<FootPointView> list = this.footPointTools.generic_fpv(fp.getFp_goods_content());
			fpvs.addAll(list);
		}
		mv.addObject("fpvs", fpvs);
		
		int pws_safe = 20;
		int num = CommUtil.checkInput(user.getPassword());
		if (num > 1) {
			pws_safe += 10;
		}
		if (num > 2) {
			pws_safe += 10;
		}
		if (!CommUtil.null2String(user.getEmail()).equals("")) {
			pws_safe += 30;
		}
		if (!CommUtil.null2String(user.getMobile()).equals("")) {
			pws_safe += 30;
		}
		mv.addObject("pws_safe", Integer.valueOf(pws_safe));

		int safe = 0;
		if ((user.getEmail() != null) && (!"".equals(user.getEmail()))) {
			safe += 10;
		}
		if ((user.getMobile() != null) && (!"".equals(user.getMobile()))) {
			safe += 10;
		}
		if ((user.getPay_password() != null)
				&& (!"".equals(user.getPassword()))) {
			safe += 10;
		}
		mv.addObject("safe", Integer.valueOf(safe));
		return mv;
	}
	
	/**
	 * 买家中心导航
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家中心导航", value = "/buyer/nav*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/nav" })
	public ModelAndView nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		Map<String, Object> map = Maps.newHashMap();
		
		map.put("status", Integer.valueOf(0));
		map.put("toUser_id", SecurityUserHolder.getCurrentUser().getId());
		map.put("parent", -1);
        map.put("orderBy", "addTime");
        map.put("orderType", "desc");
        
		mv.addObject("msg_size", this.messageService.selectCount(map));
		return mv;
	}
	
	/**
	 * 买家中心导航
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家中心导航", value = "/buyer/head*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/head" })
	public ModelAndView head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 您登录的用户角色不正确
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/buyer/authority" })
	public ModelAndView authority(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "您登录的用户角色不正确");
		mv.addObject("url", CommUtil.getURL(request) + "/index");
		return mv;
	}
	
	/**
	 * 会员等级列表
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "会员等级列表", value = "/buyer/user_level*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/user_level" })
	public ModelAndView user_level(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/user_level.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", SecurityUserHolder.getCurrentUser());
			mv.addObject("integralViewTools", this.integralViewTools);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "操作失败，请重新登录");
			mv.addObject("url", CommUtil.getURL(request) + "/user/login");
		}
		return mv;
	}
}
