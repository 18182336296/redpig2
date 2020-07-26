package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.CombinPlan;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.SalesLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigCombinSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家组合销售管理控制器
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
public class RedPigCombinSellerAction extends BaseAction{
	/**
	 * 组合销售商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param type
	 * @param combin_status
	 * @param goods_name
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "组合销售商品列表", value = "/combin*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin" })
	public ModelAndView combin(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String combin_status, String goods_name, String beginTime,
			String endTime) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/combin_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (config.getCombin_status() == 1) {
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10, "addTime", "desc");
			
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			
			maps.put("store_id", store.getId());
			
			if ((type != null) && (!type.equals(""))) {
				
				maps.put("combin_type", type);
				mv.addObject("type", type);
			} else {
				
				maps.put("combin_type", 0);
			}
			
			if ((combin_status != null) && (!combin_status.equals(""))) {
				maps.put("combin_status", CommUtil.null2Int(combin_status));
				mv.addObject("combin_status", combin_status);
			}
			
			if ((goods_name != null) && (!goods_name.equals(""))) {
				
				maps.put("main_goods_name_like", CommUtil.null2String(goods_name));
				
				mv.addObject("goods_name", goods_name);
			}
			if ((beginTime != null) && (!beginTime.equals(""))) {
				maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
				mv.addObject("beginTime", beginTime);
			}
			if ((endTime != null) && (!endTime.equals(""))) {
				maps.put("add_Time_less_than_equal", CommUtil.formatDate(beginTime));
				mv.addObject("endTime", endTime);
			}
			
			IPageList pList = this.combinplanService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("combinTools", this.combinTools);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启组合销售促销方式");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 组合销售商品添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "组合销售商品添加", value = "/combin_add*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_add" })
	public ModelAndView combin_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/combin_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean ret = true;
		if (store.getCombin_end_time() != null) {
			Calendar cal = Calendar.getInstance();
			Date now = new Date();
			if (cal.getTime().before(now)) {
				ret = false;
			}
			mv.addObject("now", CommUtil.formatShortDate(now));
		} else {
			ret = false;
		}
		if (!ret) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有购买组合套餐或者套餐时间已到期");
			mv.addObject("url", CommUtil.getURL(request) + "/combin");
		}
		return mv;
	}
	
	/**
	 * 验证商品两个组合类型是否存在
	 * @param request
	 * @param response
	 * @param gid
	 * @param combin_mark
	 * @param endTime
	 * @param id
	 */
	@SecurityMapping(title = " 验证商品两个组合类型是否存在", value = "/verify_combin*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/verify_combin" })
	public void verify_combin(HttpServletRequest request,
			HttpServletResponse response, String gid, String combin_mark,
			String endTime, String id) {
		boolean ret = true;
		String code = "参数错误";
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
		if (goods == null) {
			ret = false;
			code = "主体商品信息错误";
		}
		if ((combin_mark == null) || (combin_mark.equals(""))) {
			ret = false;
			code = "参数错误";
		} else if ((!combin_mark.equals("0")) && (!combin_mark.equals("1"))) {
			ret = false;
			code = "参数错误";
		}
		Date endTime2 = CommUtil.formatDate(endTime);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (endTime2.after(store.getCombin_end_time())) {
			ret = false;
			code = "结束时间不能超出当前商家的套餐结束时间";
		}
		if (ret) {
			if (combin_mark.equals("0")) {
				if ((goods.getCombin_suit_id() != null) && (id.equals(""))) {
					ret = false;
					code = "该主商品已经存在组合套餐，请先将存在的组合套餐删除掉再添加新的组合套餐";
				}
			} else if ((goods.getCombin_parts_id() != null) && (id.equals(""))) {
				ret = false;
				code = "该主商品已经存在组合配件，请先将存在的组合配件删除掉再添加新的组合配件";
			}
		}
		
		Map<String,Object> json_map = Maps.newHashMap();
		json_map.put("ret", Boolean.valueOf(ret));
		json_map.put("code", code);
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");

			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 组合销售添加时获得商品全部价格
	 * @param request
	 * @param response
	 * @param other_ids
	 * @param main_goods_id
	 */
	@SecurityMapping(title = "组合销售添加时获得商品全部价格", value = "/getPrice*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/getPrice" })
	public void combin_getPrice(HttpServletRequest request,
			HttpServletResponse response, String other_ids, String main_goods_id) {
		double all_price = 0.0D;
		if ((main_goods_id != null) && (!main_goods_id.equals(""))) {
			Goods main = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(main_goods_id));
			all_price = CommUtil.null2Double(main.getGoods_current_price());
		}
		if ((other_ids != null) && (!other_ids.equals(""))) {
			String[] ids = other_ids.split(",");
			
			for (String id : ids) {
				
				if (!id.equals("")) {
					Goods other = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
					
					all_price = all_price + CommUtil.null2Double(other.getGoods_current_price());
				}
			}
		}
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");

			PrintWriter writer = response.getWriter();
			writer.print(all_price);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 组合销售套餐
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "组合销售套餐", value = "/combin_meal*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_meal" })
	public ModelAndView combin_meal(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/combin_meal.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig config = this.configService.getSysConfig();
		if (config.getCombin_status() == 1) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = this.storeService.selectByPrimaryKey(user.getStore().getId());
			mv.addObject("store", store);
			mv.addObject("gold", Integer.valueOf(user.getGold()));
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启组合销售促销方式");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 组合销售套餐保存
	 * @param request
	 * @param response
	 * @param meal_day
	 */
	@SecurityMapping(title = "组合销售套餐保存", value = "/combin_meal_buy*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_meal_buy" })
	public void combin_meal_buy(HttpServletRequest request,
			HttpServletResponse response, String meal_day) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		int gold = user.getGold();
		int count = 0;
		boolean verify = true;
		int day = 30;
		if (meal_day != null) {
			if (meal_day.equals("a")) {
				count = 1;
			} else if (meal_day.equals("b")) {
				count = 2;
			} else if (meal_day.equals("c")) {
				count = 3;
			} else if (meal_day.equals("d")) {
				count = 6;
			} else if (meal_day.equals("e")) {
				count = 12;
			} else {
				verify = false;
			}
		} else {
			verify = false;
		}
		day *= count;
		int combin_gold = CommUtil.null2Int(Integer.valueOf(count))
				* this.configService.getSysConfig().getCombin_amount();
		if ((gold >= combin_gold) && (verify)) {
			user.setGold(gold - combin_gold);
			this.userService.updateById(user);
			
			GoldLog log = new GoldLog();
			log.setAddTime(new Date());
			log.setGl_content("购买组合销售套餐");
			log.setGl_count(combin_gold);
			log.setGl_user(user);
			log.setGl_type(-1);
			this.goldLogService.saveEntity(log);

			Store store = user.getStore();
			Date meal_begin_time = null;
			Calendar cal = Calendar.getInstance();
			if ((store.getCombin_end_time() != null)
					&& (store.getCombin_end_time().after(cal.getTime()))) {
				meal_begin_time = store.getCombin_end_time();
				cal.setTime(store.getCombin_end_time());
			}
			cal.add(5, day);
			store.setCombin_end_time(cal.getTime());
			this.storeService.updateById(store);

			SalesLog c_log = new SalesLog();
			c_log.setAddTime(new Date());
			if (meal_begin_time != null) {
				c_log.setBegin_time(meal_begin_time);
			} else {
				c_log.setBegin_time(new Date());
			}
			c_log.setEnd_time(cal.getTime());
			c_log.setGold(combin_gold);
			c_log.setSales_info("套餐总时间增加" + day + "天");
			c_log.setStore_id(store.getId());
			c_log.setSales_type(1);
			this.combinlogService.saveEntity(c_log);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = response.getWriter();
				writer.print("true");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = response.getWriter();
				writer.print("false");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 组合销售套餐
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "组合销售套餐", value = "/combin_meal_log*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_meal_log" })
	public ModelAndView combin_meal_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/combin_meal_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		maps.put("store_id", store.getId());
		maps.put("sales_type", 1);
		
		IPageList pList = this.saleslogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 组合套餐设置
	 * @param request
	 * @param response
	 * @param type
	 * @param plan_count
	 * @return
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/combin_set_items*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_set_items" })
	public ModelAndView combin_set_goods(HttpServletRequest request,
			HttpServletResponse response, String type, String plan_count) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/combin_set_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("type", type);
		mv.addObject("plan_count", plan_count);
		return mv;
	}
	
	/**
	 * 组合套餐设置
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param currentPage
	 * @param type
	 * @param plan_count
	 * @return
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/combin_set_goods_load*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_set_goods_load" })
	public ModelAndView combin_set_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name,
			String currentPage, String type, String plan_count) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/combin_set_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,14, null, null);
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			maps.put("goods_name_like", CommUtil.null2String(goods_name));
		}
		
		maps.put("combin_suit_id_or_combin_parts_id_is_null", -1);
		
		maps.put("goods_store_id", store.getId());
		
		List<String> params = Lists.newArrayList();
		
		params.add("combin_status");
		
		this.queryTools.shieldGoodsStatus(maps, params);
		
		
		
		IPageList pList = this.goodsService.list(maps);
		
		String url = CommUtil.getURL(request) + "/combin_set_goods_load";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		mv.addObject("type", type);
		if ((plan_count == null) || (plan_count.equals(""))) {
			plan_count = "1";
		}
		mv.addObject("plan_count", plan_count);
		return mv;
	}
	
	/**
	 * 组合套餐设置
	 * @param request
	 * @param response
	 * @param plan_num
	 * @param main_goods_id
	 * @param beginTime
	 * @param endTime
	 * @param id
	 * @param combin_mark
	 * @param old_main_goods_id
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/combin_plan_save*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_plan_save" })
	public void combin_plan_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String plan_num,
			String main_goods_id, String beginTime, String endTime, String id,
			String combin_mark, String old_main_goods_id) {
		boolean ret = true;
		if ((main_goods_id == null) || (main_goods_id.equals(""))) {
			ret = false;
		}
		if ((plan_num == null) || (plan_num.equals(""))) {
			ret = false;
		}
		if ((beginTime == null) || (beginTime.equals("")) || (endTime == null)
				|| (endTime.equals(""))) {
			ret = false;
		}
		if ((combin_mark == null) || (combin_mark.equals(""))) {
			ret = false;
		} else if ((!combin_mark.equals("0")) && (!combin_mark.equals("1"))) {
			ret = false;
		}
		if (ret) {
			if ((old_main_goods_id != null) && (!old_main_goods_id.equals(""))) {
				Goods old_main_goods = this.goodsService.selectByPrimaryKey(CommUtil
						.null2Long(old_main_goods_id));
				old_main_goods.setCombin_parts_id(null);
				old_main_goods.setCombin_suit_id(null);
				old_main_goods.setCombin_status(0);
				this.goodsService.updateById(old_main_goods);
			}
			double all_price = 0.0D;
			Goods main_goods = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(main_goods_id));
			CombinPlan combinplan = null;
			if ((id != null) && (!id.equals(""))) {
				combinplan = this.combinplanService.selectByPrimaryKey(CommUtil
						.null2Long(id));
			} else {
				combinplan = new CombinPlan();
				combinplan.setAddTime(new Date());
				combinplan.setCombin_type(CommUtil.null2Int(combin_mark));
			}
			
			Map<String,Object> main_map = Maps.newHashMap();
			main_map.put("id", main_goods.getId());
			main_map.put("name", main_goods.getGoods_name());
			main_map.put("price", main_goods.getGoods_current_price());
			main_map.put("store_price", main_goods.getStore_price());
			main_map.put("inventory",
					Integer.valueOf(main_goods.getGoods_inventory()));
			String goods_domainPath = CommUtil.getURL(request) + "/items_" + main_goods.getId() + "";
			
			if ((this.configService.getSysConfig().getSecond_domain_open())
					&& (main_goods.getGoods_store().getStore_second_domain() != "")
					&& (main_goods.getGoods_type() == 1)) {
				String store_second_domain = "http://"
						+ main_goods.getGoods_store().getStore_second_domain()
						+ "." + CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/items_"
						+ main_goods.getId() + "";
			}
			main_map.put("url", goods_domainPath);
			String img = this.configService.getSysConfig().getGoodsImage()
					.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (main_goods.getGoods_main_photo() != null) {
				img =

				main_goods.getGoods_main_photo().getPath() + "/"
						+ main_goods.getGoods_main_photo().getName()
						+ "_small." + main_goods.getGoods_main_photo().getExt();
			}
			main_map.put("img", img);
			combinplan.setMain_goods_info(JSON.toJSONString(main_map));
			combinplan.setMain_goods_id(main_goods.getId());
			combinplan.setMain_goods_name(main_goods.getGoods_name());
			List<Map<String,Object>> plan_list = Lists.newArrayList();
			
			String[] nums = plan_num.split(",");

			for (String count : nums) {

				all_price = CommUtil.null2Double(main_goods
						.getGoods_current_price());
				if (!count.equals("")) {
					String other_goods_ids = request
							.getParameter("other_goods_ids_" + count);
					String[] other_ids = other_goods_ids.split(",");
					List<Map<String,Object>> goods_list = Lists.newArrayList();
					for (String other_id : other_ids) {

						if (!other_id.equals("")) {
							Goods obj = this.goodsService.selectByPrimaryKey(CommUtil
									.null2Long(other_id));

							all_price = all_price
									+ CommUtil.null2Double(obj
											.getGoods_current_price());
							Map<String,Object> temp_map = Maps.newHashMap();
							temp_map.put("id", obj.getId());
							temp_map.put("name", obj.getGoods_name());
							temp_map.put("price", obj.getGoods_current_price());
							temp_map.put("store_price", obj.getStore_price());
							temp_map.put("inventory",
									Integer.valueOf(obj.getGoods_inventory()));
							String goods_url = CommUtil.getURL(request)
									+ "/items_" + obj.getId() + "";
							if ((this.configService.getSysConfig()
									.getSecond_domain_open())
									&& (obj.getGoods_store()
											.getStore_second_domain() != "")) {
								if (obj.getGoods_type() == 1) {
									String store_second_domain = "http://"
											+ obj.getGoods_store()
													.getStore_second_domain()
											+ "."
											+ CommUtil.generic_domain(request);
									goods_url = store_second_domain + "/items_"
											+ obj.getId() + "";
								}
							}
							temp_map.put("url", goods_url);
							String img2 = this.configService.getSysConfig()
									.getGoodsImage().getPath()
									+ "/"
									+ this.configService.getSysConfig()
											.getGoodsImage().getName();
							if (obj.getGoods_main_photo() != null) {
								img2 =

								obj.getGoods_main_photo().getPath() + "/"
										+ obj.getGoods_main_photo().getName()
										+ "_small."
										+ obj.getGoods_main_photo().getExt();
							}
							temp_map.put("img", img2);
							goods_list.add(temp_map);
						}
					}
					
					Map<String,Object> combin_goods_map = Maps.newHashMap();
					combin_goods_map.put("goods_list", goods_list);
					combin_goods_map.put("plan_goods_price",
							request.getParameter("combin_price_" + count));
					combin_goods_map.put("all_goods_price",
							CommUtil.formatMoney(Double.valueOf(all_price)));
					plan_list.add(combin_goods_map);
				}
			}
			String plan_list_json = JSON.toJSONString(plan_list);
			combinplan.setCombin_plan_info(plan_list_json);
			combinplan.setCombin_status(0);
			combinplan.setBeginTime(CommUtil.formatDate(beginTime));
			combinplan.setEndTime(CommUtil.formatDate(endTime));
			combinplan.setCombin_form(1);
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			combinplan.setStore_id(store.getId());
			if ((id != null) && (!id.equals(""))) {
				this.combinplanService.updateById(combinplan);
			} else {
				this.combinplanService.saveEntity(combinplan);
			}
			main_goods.setCombin_status(1);
			if (combinplan.getCombin_type() == 0) {
				main_goods.setCombin_suit_id(combinplan.getId());
			} else {
				main_goods.setCombin_parts_id(combinplan.getId());
			}
			this.goodsService.updateById(main_goods);
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = response.getWriter();
				writer.print("true");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");
			try {
				PrintWriter writer = response.getWriter();
				writer.print("false");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 组合销售商品编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "组合销售商品编辑", value = "/combin_plan_edit*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_plan_edit" })
	public ModelAndView combin_plan_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "您所访问的地址不存在");
		mv.addObject("url", CommUtil.getURL(request) + "/combin");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((id != null) && (!id.equals(""))) {
			CombinPlan obj = this.combinplanService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (obj != null) {
				if (CommUtil.null2String(store.getId()).equals(
						CommUtil.null2String(obj.getStore_id()))) {
					mv = new RedPigJModelAndView(
							"user/default/sellercenter/combin_add.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("edit", Boolean.valueOf(true));
					mv.addObject("obj", obj);
					mv.addObject("combinTools", this.combinTools);
				}
			}
		}
		return mv;
	}
	
	/**
	 * 组合销售删除
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "组合销售删除", value = "/combin_plan_delete*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_plan_delete" })
	public String combin_plan_deleteById(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String type) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((id != null) && (!id.equals(""))) {
			CombinPlan obj = this.combinplanService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (obj != null) {
				if (CommUtil.null2String(store.getId()).equals(
						CommUtil.null2String(obj.getStore_id()))) {
					Goods goods = this.goodsService.selectByPrimaryKey(obj
							.getMain_goods_id());
					goods.setCombin_status(0);
					if ((obj.getCombin_type() == 0)
							&& (goods.getCombin_suit_id() != null)) {
						if (goods.getCombin_suit_id().equals(obj.getId())) {
							goods.setCombin_suit_id(null);
						}
					} else if ((obj.getCombin_type() == 1)
							&& (goods.getCombin_parts_id() != null)
							&& (goods.getCombin_parts_id().equals(obj.getId()))) {
						goods.setCombin_parts_id(null);
					}
					this.goodsService.updateById(goods);
					boolean ret = true;
					this.goodsTools.updateGoodsLucene(goods);
					if (ret) {
						this.combinplanService.deleteById(CommUtil.null2Long(id));
					}
				}
			}
		}
		return "redirect:/combin?currentPage=" + currentPage + "&type="
				+ type;
	}
	
	/**
	 * 组合销售上架下架
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "组合销售上架下架", value = "/combin_plan_switch*", rtype = "seller", rname = "组合销售", rcode = "combin_seller", rgroup = "促销推广")
	@RequestMapping({ "/combin_plan_switch" })
	public String combin_plan_switch(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((id != null) && (!id.equals(""))) {
			CombinPlan obj = this.combinplanService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (obj != null) {
				if (CommUtil.null2String(store.getId()).equals(
						CommUtil.null2String(obj.getStore_id()))) {
					if (obj.getCombin_status() == -5) {
						obj.setCombin_status(1);
					} else if (obj.getCombin_status() == 1) {
						obj.setCombin_status(-5);
					}
					this.combinplanService.updateById(obj);
				}
			}
		}
		return "redirect:/combin?currentPage=" + currentPage;
	}
}
