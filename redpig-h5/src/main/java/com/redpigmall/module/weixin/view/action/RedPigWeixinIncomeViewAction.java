package com.redpigmall.module.weixin.view.action;

import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.*;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import com.redpigmall.service.RedPigDistributionCommissionService;
import com.redpigmall.service.RedPigDistributionGradeService;
import com.redpigmall.service.UserDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * 
 * <p>
 * Title: RedPigWapGroupViewAction.java
 * </p>
 * 
 * <p>
 * Description:我的收益管理控制器
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
 * @date 2018-9-11
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigWeixinIncomeViewAction extends BaseAction{

	@Autowired
	private RedPigDistributionCommissionService distributionCommissionService;

	@Autowired
	private UserDistributionService userDistributionService;
	/**
	 * 我的收益主页
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/income/index" })
	public ModelAndView income_index(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/income/index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		String params = "";

		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		User user = SecurityUserHolder.getCurrentUser();

		maps.put("user_id",user.getId());
		DistributionCommission distributionCommission = this.distributionCommissionService.selectByUserId(user.getId());
		mv.addObject("obj",distributionCommission);
		return mv;
	}

	/**
	 * 提现
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/income/withdraw" })
	public ModelAndView income_withdraw(HttpServletRequest request,
							 HttpServletResponse response, String currentPage, String orderBy,
							 String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/income/withdraw.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		String params = "";

		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		/*IPageList pList = this.nukeGoodsService.list(maps);*/
		/*CommUtil.saveIPageList2ModelAndView(url + "/nuke/nuke_list.html", "",params, pList, mv);*/
		return mv;
	}


	/**
	 * 累计客户列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/income/cumul_customer" })
	public ModelAndView cumul_customer(HttpServletRequest request,
										HttpServletResponse response, String currentPage, String orderBy,
										String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/income/cumul_customer.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		// 查出推广的客户信息
		params.put("parent_user_id",SecurityUserHolder.getCurrentUser().getId());
		List<UserDistribution> userDistributionList = this.userDistributionService.selectObjByProperty(params);
		mv.addObject("objs",userDistributionList);

		// 查询当月的客户数
		Calendar first = Calendar.getInstance();
		first.add(Calendar.MONTH, 0);
		first.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天

		Calendar last = Calendar.getInstance();
		last.set(Calendar.DAY_OF_MONTH, last.getActualMaximum(Calendar.DAY_OF_MONTH));

		params.put("add_Time_more_than_equal",first.getTime());
		params.put("add_Time_less_than_equal",last.getTime());

		String monthCount = this.userDistributionService.selectCount(params)+"";
		mv.addObject("monthCount",monthCount);
		return mv;
	}

	/**
	 * 累计订单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/income/cumul_orders" })
	public ModelAndView cumul_orders(HttpServletRequest request,
									   HttpServletResponse response, String currentPage, String orderBy,
									   String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/income/cumul_orders.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		String params = "";

		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		/*IPageList pList = this.nukeGoodsService.list(maps);*/
		/*CommUtil.saveIPageList2ModelAndView(url + "/nuke/nuke_list.html", "",params, pList, mv);*/
		return mv;
	}

	/**
	 * 线下累计订单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/income/cumul_orders_offline" })
	public ModelAndView cumul_orders_offline(HttpServletRequest request,
											 HttpServletResponse response, String currentPage, String orderBy,
											 String orderType) {
		ModelAndView mv = new RedPigJModelAndView("weixin/income/cumul_orders_offline.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String url = this.configService.getSysConfig().getAddress();

		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}

		String params = "";

		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		/*IPageList pList = this.nukeGoodsService.list(maps);*/
		/*CommUtil.saveIPageList2ModelAndView(url + "/nuke/nuke_list.html", "",params, pList, mv);*/
		return mv;
	}

	/**
	 * 商品推广列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@RequestMapping({ "/income/goods_extension_list" })
	public ModelAndView goods_extension_list(HttpServletRequest request,
											 HttpServletResponse response, String currentPage, String orderBy,
											 String orderType,String class_id, String brand_id,String goods_name) {
		ModelAndView mv = new RedPigJModelAndView("weixin/income/goods_extension_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		Map<String, Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		params.put("goods_type", 0);

		if ((goods_name != null) && (!goods_name.equals(""))) {

			params.put("goods_name_like", goods_name);
			mv.addObject("goods_name", goods_name);
		}
		// 分类查询
		if ((class_id != null) && (!class_id.equals(""))) {
			params.put("rgc_id", CommUtil.null2Long(class_id));
			params.put("rgc_parent_id", CommUtil.null2Long(class_id));
			params.put("rgc_parent_parent_id", CommUtil.null2Long(class_id));

			mv.addObject("class_id", class_id);
		}
		if ((brand_id != null) && (!brand_id.equals(""))) {

			params.put("goods_brand_id", CommUtil.null2Long(brand_id));
			mv.addObject("brand_id", brand_id);

		}

		IPageList pList = this.goodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		params.clear();
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");

		List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params);
		params.clear();

		params.put("parent", -1);

		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(params);

		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);

		return mv;
	}


	/**
	 * 通过ajax加载数据
	 * 注意：由于商品信息比较大，转成jso耗费较多时间（此方法暂时弃用）
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param class_id
	 * @param brand_id
	 * @param goods_name
	 * @return
	 */
	@RequestMapping(value = { "/income/goods_extension_list_ajax" },method=RequestMethod.GET)
	@ResponseBody
	public Map goods_extension_list_ajax(String currentPage, String orderBy,String orderType,
										 String class_id, String brand_id,String goods_name) {

		Map<String, Object> resultMap = new HashMap<>();
		if (SecurityUserHolder.getCurrentUser()==null){
			resultMap.put("code",530);
			resultMap.put("data",null);
			resultMap.put("msg","用户未登录！");
			return resultMap;
		}
		// 查询
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		params.put("goods_type", 0);

		if ((goods_name != null) && (!goods_name.equals(""))) {
			params.put("goods_name_like", goods_name);
		}
		// 分类查询
		if ((class_id != null) && (!class_id.equals(""))) {
			params.put("rgc_id", CommUtil.null2Long(class_id));
			params.put("rgc_parent_id", CommUtil.null2Long(class_id));
			params.put("rgc_parent_parent_id", CommUtil.null2Long(class_id));
		}
		if ((brand_id != null) && (!brand_id.equals(""))) {
			params.put("goods_brand_id", CommUtil.null2Long(brand_id));
		}

		List<Goods> goodsList = this.goodsService.list(params).getResult();
		if (goodsList!=null){
			resultMap.put("code",200);
			resultMap.put("data",goodsList);
			resultMap.put("msg","获取商品列表成功！");
		}
		params.clear();
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");

		List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params);
		params.clear();

		params.put("parent", -1);

		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(params);

		return resultMap;
	}

}
