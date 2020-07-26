package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.SalesLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigEnoughReduceSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家满就减控制器
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
 * @date 2014-9-22
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("rawtypes")
@Controller
public class RedPigEnoughReduceSellerAction extends BaseAction{
	/**
	 * 商家满就减活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param ertitle
	 * @param erstatus
	 * @param erbegin_time
	 * @param erend_time
	 * @return
	 */
	@SecurityMapping(title = "商家满就减活动列表", value = "/enoughreduce_list*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_list" })
	public ModelAndView enoughreduce_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String ertitle,
			String erstatus, String erbegin_time, String erend_time) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/enoughreduce_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		Store store = SecurityUserHolder.getCurrentUser().getStore();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		
		maps.put("store_id", store.getId());
		
		if ((erbegin_time != null) && (!erbegin_time.equals(""))
				&& (erend_time != null) && (!erend_time.equals(""))) {
			maps.put("erend_time_more_than_equal", CommUtil.formatDate(erbegin_time));
			maps.put("erend_time_less_than_equal", CommUtil.formatDate(erbegin_time));
		}
		mv.addObject("erbegin_time", erbegin_time);
		mv.addObject("erend_time", erend_time);
		
		if ((ertitle != null) && (!"".equals(ertitle))) {
			maps.put("ertitle_like", ertitle);
			mv.addObject("ertitle", ertitle);
		}
		
		if ((erstatus != null) && (!"".equals(erstatus))) {
			maps.put("erstatus", CommUtil.null2Int(erstatus));
			mv.addObject("erstatus", erstatus);
		}
		
		IPageList pList = this.enoughreduceService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url
				+ "/enoughreduce_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 商家满就减活动添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商家满就减活动添加", value = "/enoughreduce_add*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_add" })
	public ModelAndView enoughreduce_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/enoughreduce_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store.getEnoughreduce_meal_endTime() == null) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未购买满就减套餐");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/enoughreduce_meal");
			return mv;
		}
		if (store.getEnoughreduce_meal_endTime().before(new Date())) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的满就减套餐已到期");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/enoughreduce_meal");
			return mv;
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", store.getId().toString());
		params.put("erstatus1", 10);
		params.put("erstatus2", 5);
		
		List<EnoughReduce> er = this.enoughreduceService.queryPageList(params);
		
		for (EnoughReduce enoughReduce : er) {
			if (enoughReduce.getErend_time().before(new Date())) {
				enoughReduce.setErstatus(20);
			}
			this.enoughreduceService.updateById(enoughReduce);
		}
		
		er = this.enoughreduceService.queryPageList(params);
		
		if (er.size() >= this.configService.getSysConfig().getEnoughreduce_max_count()) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您当前正在审核或进行的满就减超过了规定的最大值");
			mv.addObject("url", CommUtil.getURL(request) + "/enoughreduce_list");
			return mv;
		}
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 满就减活动修改
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就减活动修改", value = "/enoughreduce_edit*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_edit" })
	public ModelAndView enoughreduce_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/enoughreduce_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			EnoughReduce enoughreduce = this.enoughreduceService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			if (enoughreduce.getErstatus() > 0) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该活动不可编辑");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/enoughreduce_list");
				return mv;
			}
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (enoughreduce.getStore_id().equals(store.getId().toString())) {
				mv.addObject("obj", enoughreduce);
				mv.addObject("currentPage", currentPage);
				mv.addObject("edit", Boolean.valueOf(true));
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您的店铺中没有该活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/enoughreduce_list");
				return mv;
			}
		}
		return mv;
	}
	
	/**
	 * 商家满就减活动保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param url
	 * @param add_url
	 * @param count
	 */
	@SecurityMapping(title = "商家满就减活动保存", value = "/enoughreduce_save*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_save" })
	public void enoughreduce_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String url, String add_url, String count) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("ret", Boolean.valueOf(false));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store.getEnoughreduce_meal_endTime().before(new Date())) {
			map.put("op_title", "您的满就减套餐已到期");
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", store.getId().toString());
		params.put("erstatus1", 10);
		params.put("erstatus2", 5);
		
		List<EnoughReduce> er = this.enoughreduceService.queryPageList(params);
		
		for (EnoughReduce enoughReduce : er) {
			if (enoughReduce.getErend_time().before(new Date())) {
				enoughReduce.setErstatus(20);
			}
			this.enoughreduceService.updateById(enoughReduce);
		}
		
		er = this.enoughreduceService.queryPageList(params);
		
		if (er.size() >= this.configService.getSysConfig()
				.getEnoughreduce_max_count()) {
			map.put("op_title", "您当前正在审核或进行的满就减超过了规定的最大值");
		}
		
		EnoughReduce enoughreduce = null;
		if (CommUtil.null2String(id).equals("")) {
			enoughreduce = (EnoughReduce) WebForm.toPo(request,EnoughReduce.class);
			enoughreduce.setAddTime(new Date());
			enoughreduce.setEr_type(1);
		} else {
			EnoughReduce obj = this.enoughreduceService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			if (obj.getErstatus() > 0) {
				map.put("op_title", "该活动不可编辑");
			}
			enoughreduce = (EnoughReduce) WebForm.toPo(request, obj);
		}
		TreeMap<Double, Double> jsonmap = Maps.newTreeMap();
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			String enoughMoney = CommUtil.null2String(request
					.getParameter("enoughMoney_" + i));
			String reduceMoney = CommUtil.null2String(request
					.getParameter("reduceMoney_" + i));
			if ((enoughMoney != null) && (!"".equals(enoughMoney))
					&& (reduceMoney != null) && (!"".equals(reduceMoney))) {
				jsonmap.put(Double.valueOf(CommUtil.null2Double(new BigDecimal(
						enoughMoney))), Double.valueOf(CommUtil
						.null2Double(new BigDecimal(reduceMoney))));
			}
		}
		enoughreduce.setEr_json(JSON.toJSONString(jsonmap));
		String ertag = "";
		Iterator<Double> it = jsonmap.keySet().iterator();
		while (it.hasNext()) {
			double key = ((Double) it.next()).doubleValue();
			double value = ((Double) jsonmap.get(Double.valueOf(key)))
					.doubleValue();
			ertag = ertag + "满" + key + "减" + value + ",";
		}
		ertag = ertag.substring(0, ertag.length() - 1);
		enoughreduce.setErtag(ertag);
		enoughreduce.setErstatus(0);
		enoughreduce.setEr_type(1);
		enoughreduce.setStore_id(store.getId().toString());
		enoughreduce.setStore_name(store.getStore_name());
		enoughreduce.setErgoods_ids_json("[]");
		if (id.equals("")) {
			this.enoughreduceService.saveEntity(enoughreduce);
			map.put("op_title", "保存满就减活动成功");
			map.put("ret", Boolean.valueOf(true));
			map.put("id", enoughreduce.getId());
		} else {
			this.enoughreduceService.updateById(enoughreduce);
			map.put("op_title", "保存满就减活动成功");
			map.put("ret", Boolean.valueOf(true));
			map.put("id", enoughreduce.getId());
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 满就减活动删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就减活动删除", value = "/enoughreduce_del*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_del" })
	public String enoughreduce_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {

		for (String id : mulitId.split(",")) {

			if (!id.equals("")) {
				EnoughReduce enoughreduce = this.enoughreduceService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				Store store = user.getStore();
				if (enoughreduce.getStore_id().equals(store.getId().toString())) {
					String goods_json = enoughreduce.getErgoods_ids_json();
					if ((goods_json != null) && (!goods_json.equals(""))) {
						List<String> goods_id_list = JSON.parseArray(
								goods_json, String.class);
						for (String goods_id : goods_id_list) {
							Goods ergood = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
							if (ergood != null) {
								ergood.setEnough_reduce(0);
								ergood.setOrder_enough_reduce_id("");
								this.goodsService.updateById(ergood);
							}
						}
					}
					this.enoughreduceService.deleteById(Long.valueOf(Long
							.parseLong(id)));
				}
			}
		}
		return "redirect:enoughreduce_list?currentPage=" + currentPage;
	}
	
	/**
	 * 满就减活动申请审核
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "满就减活动申请审核", value = "/enoughreduce_apply*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_apply" })
	public ModelAndView enoughreduce_apply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		if ((id != null) && (!id.equals(""))) {
			EnoughReduce enoughreduce = this.enoughreduceService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (!enoughreduce.getStore_id().equals(store.getId().toString())) {
				RedPigJModelAndView mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您的店铺中没有该活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/enoughreduce_list");
				return mv;
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_id", store.getId().toString());
			params.put("erstatus1", 10);
			params.put("erstatus2", 5);
			
			List<EnoughReduce> er = this.enoughreduceService.queryPageList(params);
			
			if (er.size() >= this.configService.getSysConfig()
					.getEnoughreduce_max_count()) {
				RedPigJModelAndView mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您当前正在审核或进行的满就减超过了规定的最大值");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/enoughreduce_list");
				return mv;
			}
			if ((enoughreduce.getErstatus() == 0)
					|| (enoughreduce.getErstatus() == -10)) {
				enoughreduce.setErstatus(5);
				enoughreduce.setFailed_reason("");
				this.enoughreduceService.updateById(enoughreduce);
				ModelAndView mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("url", CommUtil.getURL(request)
						+ "/enoughreduce_list");
				mv.addObject("op_title", "提交申请成功");
				return mv;
			}
		}
		RedPigJModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "提交申请失败");
		mv.addObject("url", CommUtil.getURL(request)
				+ "/enoughreduce_list");
		return mv;
	}
	
	/**
	 * 满就减活动商品列表
	 * @param request
	 * @param response
	 * @param er_id
	 * @param currentPage
	 * @param brand_id
	 * @param searchstr
	 * @return
	 */
	@SecurityMapping(title = "满就减活动商品列表", value = "/enoughreduce_items*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_items" })
	public ModelAndView enoughreduce_goods(HttpServletRequest request,
			HttpServletResponse response, String er_id, String currentPage,
			String brand_id, String searchstr) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/enoughreduce_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		EnoughReduce er = this.enoughreduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (!er.getStore_id().equals(store.getId().toString())) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的店铺中没有该活动");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/enoughreduce_list");
			return mv;
		}
		if (er.getErstatus() > 0) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "该活动不可编辑");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/enoughreduce_list");
			return mv;
		}
		String store_id = store.getId().toString();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, null, null);
		
		this.queryTools.shieldGoodsStatus(maps, null);
		
		
		maps.put("goods_store_id", CommUtil.null2Long(store_id));
		
		maps.put("ids", genericIds(er.getErgoods_ids_json()));
		
		
		if ((searchstr != null) && (!searchstr.equals(""))) {
			
			maps.put("goods_name_like", searchstr);
			
			mv.addObject("searchstr", searchstr);
		}
		if ((brand_id != null) && (!brand_id.equals(""))) {
			maps.put("goods_brand_id", CommUtil.null2Long(brand_id));
			
			mv.addObject("brand_id", brand_id);
		}
		
		IPageList pList = this.goodsService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);

		mv.addObject("storeTools", this.storeTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("er_title", er.getErtitle());
		mv.addObject("ercontent", er.getErcontent());
		mv.addObject("er_id", er_id);
		return mv;
	}
	
	/**
	 * 满就减商品AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param er_id
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "unchecked"})
	@SecurityMapping(title = "满就减商品AJAX更新", value = "/enoughreduce_goods_ajax*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_goods_ajax" })
	public void enoughreduce_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String er_id)
			throws ClassNotFoundException {
		Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		EnoughReduce er = this.enoughreduceService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(er_id)));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (obj.getGoods_store().getId().equals(store.getId())) {
			boolean data = false;
			int flag = obj.getEnough_reduce();
			String json = er.getErgoods_ids_json();
			List jsonlist = Lists.newArrayList();
			if ((json != null) && (!"".equals(json))) {
				jsonlist = JSON.parseArray(json);
			}
			if (flag == 0) {
				data = true;
				if ((obj.getCombin_status() == 0) && (obj.getGroup_buy() == 0)
						&& (obj.getGoods_type() == 1)
						&& (obj.getActivity_status() == 0)
						&& (obj.getF_sale_type() == 0)
						&& (obj.getAdvance_sale_type() == 0)
						&& (obj.getOrder_enough_give_status() == 0)) {
					obj.setEnough_reduce(1);
					obj.setOrder_enough_reduce_id(er_id);
					jsonlist.add(id);
					er.setErgoods_ids_json(JSON.toJSONString(jsonlist));
				}
			} else {
				data = false;
				obj.setEnough_reduce(0);
				obj.setOrder_enough_reduce_id("");
				if (jsonlist.contains(id)) {
					jsonlist.remove(id);
				}
				er.setErgoods_ids_json(JSON.toJSONString(jsonlist));
			}
			this.enoughreduceService.updateById(er);
			this.goodsService.updateById(obj);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = response.getWriter();
				writer.print(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 满就减活动商品批量管理
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param er_id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "满就减活动商品批量管理", value = "/enoughreduce_goods_admin*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_goods_admin" })
	public String enoughreduce_goods_admin(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String er_id, String type) {
		EnoughReduce enoughreduce = this.enoughreduceService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(er_id)));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		List<String> goods_id_list = Lists.newArrayList();
		String goods_json = enoughreduce.getErgoods_ids_json();
		if ((goods_json != null) && (!goods_json.equals(""))
				&& (goods_json.length() > 2)) {
			goods_id_list = JSON.parseArray(goods_json, String.class);
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				Goods ergood = this.goodsService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				if (ergood.getGoods_store().getId().equals(store.getId())) {
					if ((ergood.getEnough_reduce() == 0)
							|| (ergood.getOrder_enough_reduce_id()
									.equals(er_id))) {
						if (type.equals("add")) {
							if ((ergood.getCombin_status() == 0)
									&& (ergood.getGroup_buy() == 0)
									&& (ergood.getGoods_type() == 1)
									&& (ergood.getActivity_status() == 0)
									&& (ergood.getF_sale_type() == 0)
									&& (ergood.getAdvance_sale_type() == 0)
									&& (ergood.getOrder_enough_give_status() == 0)) {
								goods_id_list.add(id);
								ergood.setEnough_reduce(1);
								ergood.setOrder_enough_reduce_id(er_id);
							}
						} else {
							if (goods_id_list.contains(id)) {
								goods_id_list.remove(id);
							}
							ergood.setEnough_reduce(0);
							ergood.setOrder_enough_reduce_id("");
						}
					}
					this.goodsService.updateById(ergood);
				}
			}
		}
		enoughreduce.setErgoods_ids_json(JSON.toJSONString(goods_id_list));
		this.enoughreduceService.updateById(enoughreduce);

		return "redirect:enoughreduce_items?currentPage=" + currentPage
				+ "&er_id=" + er_id;
	}

	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24L * 60L * 60L * 1000L;
		time += day;
		return new Date(time);
	}
	
	/**
	 * 满就减活动添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "满就减活动添加", value = "/enoughreduce_meal*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_meal" })
	public ModelAndView enoughreduce_meal(HttpServletRequest request,
			HttpServletResponse response) {
		if (this.configService.getSysConfig().getEnoughreduce_status() == 0) {
			ModelAndView mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "商城没有开启满就减活动");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/enoughreduce_list");
			return mv;
		}
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/enoughreduce_meal.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}
	
	/**
	 * 满就减活动保存
	 * @param request
	 * @param response
	 * @param meal_day
	 * @throws ParseException
	 */
	@SecurityMapping(title = "满就减活动保存", value = "/enoughreduce_male_save*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_male_save" })
	public void enoughreduce_male_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String meal_day)
			throws ParseException {
		Map<String, Object> map = Maps.newHashMap();
		if (this.configService.getSysConfig().getEnoughreduce_status() == 1) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int cost = this.configService.getSysConfig()
					.getEnoughreduce_meal_gold();
			int days = 30;
			if ((meal_day != null) && (meal_day.equals("30"))) {
				days = 30;
			}
			if ((meal_day != null) && (meal_day.equals("90"))) {
				days = 90;
			}
			if ((meal_day != null) && (meal_day.equals("180"))) {
				days = 180;
			}
			if ((meal_day != null) && (meal_day.equals("360"))) {
				days = 360;
			}
			int costday = days / 30;
			if (user.getGold() >= costday * cost) {
				user.setGold(user.getGold() - costday * cost);
				this.userService.updateById(user);
				Date day = user.getStore().getEnoughreduce_meal_endTime();
				if (day != null) {
					if (day.after(new Date())) {
						user.getStore().setEnoughreduce_meal_endTime(
								addDate(user.getStore()
										.getEnoughreduce_meal_endTime(),
										CommUtil.null2Long(
												Integer.valueOf(days))
												.longValue()));
						this.storeService.updateById(user.getStore());

						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买满就减套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.saveEntity(log);

						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore().getEnoughreduce_meal_endTime());
						
						c_log.setEnd_time(addDate(user.getStore()
								.getEnoughreduce_meal_endTime(), CommUtil
								.null2Long(Integer.valueOf(days)).longValue()));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(3);
						this.salesLogService.saveEntity(c_log);
					} else {
						Calendar ca = Calendar.getInstance();
						ca.add(5, days);
						SimpleDateFormat bartDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						user.getStore().setEnoughreduce_meal_endTime(
								CommUtil.formatDate(latertime,
										"yyyy-MM-dd HH:mm:ss"));
						this.storeService.updateById(user.getStore());

						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买满就减套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.saveEntity(log);

						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(new Date());
						c_log.setEnd_time(CommUtil.formatDate(latertime,
								"yyyy-MM-dd HH:mm:ss"));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(3);
						this.salesLogService.saveEntity(c_log);
					}
				} else {
					Calendar ca = Calendar.getInstance();
					ca.add(5, days);
					SimpleDateFormat bartDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String latertime = bartDateFormat.format(ca.getTime());
					user.getStore().setEnoughreduce_meal_endTime(
							CommUtil.formatDate(latertime,
									"yyyy-MM-dd HH:mm:ss"));
					this.storeService.updateById(user.getStore());

					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_content("购买满就减套餐");
					log.setGl_count(costday * cost);
					log.setGl_user(user);
					log.setGl_type(-1);
					this.goldLogService.saveEntity(log);

					SalesLog c_log = new SalesLog();
					c_log.setAddTime(new Date());
					c_log.setBegin_time(new Date());
					c_log.setEnd_time(CommUtil.formatDate(latertime,
							"yyyy-MM-dd HH:mm:ss"));
					c_log.setGold(costday * cost);
					c_log.setSales_info("套餐总时间增加" + days + "天");
					c_log.setStore_id(user.getStore().getId());
					c_log.setSales_type(3);
					this.salesLogService.saveEntity(c_log);
				}
				map.put("ret", Boolean.valueOf(true));
				map.put("msg", "购买成功");
			} else {
				map.put("ret", Boolean.valueOf(false));
				map.put("msg", "您的金币不足，无法购买满就减套餐");
			}
		} else {
			map.put("ret", Boolean.valueOf(false));
			map.put("msg", "购买失败,商城未开启满就减活动");
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 满就减购买记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就减购买记录", value = "/enoughreduce_meal_log*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_meal_log" })
	public ModelAndView enoughreduce_meal_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/enoughreduce_meal_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		maps.put("store_id", store.getId());
		maps.put("sales_type", 3);
		
		IPageList pList = this.salesLogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 满就减详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "满就减详情", value = "/enoughreduce_info*", rtype = "seller", rname = "满就减", rcode = "enoughreduce_seller", rgroup = "促销推广")
	@RequestMapping({ "/enoughreduce_info" })
	public ModelAndView enoughreduce_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/enoughreduce_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Store store = user.getStore();
		if ((id != null) && (!id.equals(""))) {
			EnoughReduce er = this.enoughreduceService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (er.getStore_id().equals(store.getId().toString())) {
				mv.addObject("er", er);
				Set<Long> ids = Sets.newHashSet();
				
				Map<String,Object> para = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
				if (er.getErgoods_ids_json().length() > 2) {
					ids.addAll(genericIds(er.getErgoods_ids_json()));
				}else{
					ids.add(Long.MAX_VALUE);
				}
				para.put("ids", ids);
				para.put("goods_store_id", store.getId());
				
				IPageList pList = this.goodsService.list(para);
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您的店铺中没有该活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/enoughreduce_list");
				return mv;
			}
		}
		return mv;
	}

	
	private Set<Long> genericIds(String str) {
		Set<Long> ids = Sets.newHashSet();
		List list = JSON.parseArray(str);
		for (Object object : list) {
			ids.add(CommUtil.null2Long(object));
		}
		return ids;
	}
}
