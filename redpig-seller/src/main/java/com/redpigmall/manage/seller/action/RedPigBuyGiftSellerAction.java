package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.SalesLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigBuyGiftSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台满就送管理控制器
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
 * @date 2014-9-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigBuyGiftSellerAction extends BaseAction{
	/**
	 * 商家中心
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gift_status
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/buygift_list*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_list" })
	public ModelAndView buygift_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gift_status, String beginTime,
			String endTime) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/buygift_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		
		Store store = SecurityUserHolder.getCurrentUser().getStore();
		
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if ((gift_status != null) && (!gift_status.equals(""))) {
			maps.put("gift_status", gift_status);
		}
		if ((beginTime != null) && (!beginTime.equals(""))) {
			
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
		}
		if ((endTime != null) && (!endTime.equals(""))) {
			
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
		}
		
		maps.put("gift_type", 1);
		
		maps.put("store_id", store.getId());
		
		IPageList pList = this.buygiftService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/buygift_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 满就送添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就送添加", value = "/buygift_add*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_add" })
	public ModelAndView buygift_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/buygift_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		if (store.getBuygift_meal_endTime() == null) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未购买满就送套餐");
			mv.addObject("url", CommUtil.getURL(request) + "/buygift_meal");
			return mv;
		}
		if (store.getBuygift_meal_endTime().before(new Date())) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您的满就送套餐已到期");
			mv.addObject("url", CommUtil.getURL(request) + "/buygift_meal");
			return mv;
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", user.getStore().getId());
		params.put("gift_status1", 10);
		params.put("gift_status2", 0);
		
		List<BuyGift> bgs = this.buygiftService.queryPageList(params);
		
		if (bgs.size() > 0) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您当前有正在审核或进行的满就送");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buygift_list");
			return mv;
		}
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 满就送重新申请
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就送重新申请", value = "/buygift_edit*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_edit" })
	public ModelAndView buygift_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/buygift_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			BuyGift buygift = this.buygiftService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (!buygift.getStore_id().equals(store.getId())) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您没有此买就送活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buygift_list");
				return mv;
			}
			List<Map> goodses = JSON.parseArray(buygift.getGoods_info(),Map.class);
			Goods goods;
			for (Map map : goodses) {
				goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
				int inventory = 0;
				if (goods != null) {
					inventory = goods.getGoods_inventory();
				}
				map.put("store_inventory", Integer.valueOf(inventory));
			}
			List<Map> gifts = JSON
					.parseArray(buygift.getGift_info(), Map.class);
			for (Map map : gifts) {
				goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(map
						.get("goods_id")));
				int inventory = 0;
				if (goods != null) {
					inventory = goods.getGoods_inventory();
				}
				map.put("store_inventory", Integer.valueOf(inventory));
			}
			mv.addObject("gifts", gifts);
			mv.addObject("goodses", goodses);
			mv.addObject("obj", buygift);
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "详情", value = "/buygift_info*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_info" })
	public ModelAndView buygift_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/buygift_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			BuyGift buygift = this.buygiftService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			Store store = user.getStore();
			if (!buygift.getStore_id().equals(store.getId())) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "您没有此买就送活动");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buygift_list");
				return mv;
			}
			List<Map> goodses = JSON.parseArray(buygift.getGoods_info(),
					Map.class);
			Goods goods;
			for (Map map : goodses) {
				goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(map
						.get("goods_id")));
				int inventory = 0;
				if (goods != null) {
					inventory = goods.getGoods_inventory();
				}
				map.put("store_inventory", Integer.valueOf(inventory));
			}
			List<Map> gifts = JSON
					.parseArray(buygift.getGift_info(), Map.class);
			for (Map map : gifts) {
				goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
				int inventory = 0;
				if (goods != null) {
					inventory = goods.getGoods_inventory();
				}
				map.put("store_inventory", Integer.valueOf(inventory));
			}
			mv.addObject("gifts", gifts);
			mv.addObject("goodses", goodses);
			mv.addObject("obj", buygift);
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 满就送保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param goods_ids
	 * @param gift_ids
	 */
	@SecurityMapping(title = "满就送保存", value = "/buygift_save*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_save" })
	public void buygift_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_ids, String gift_ids) {
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(false));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (user.getStore().getBuygift_meal_endTime().before(new Date())) {
			json.put("op_title", "您的满就送套餐已到期");
			json.put("url", CommUtil.getURL(request) + "/buygift_meal");
			return_json(JSON.toJSONString(json), response);
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_id", user.getStore().getId());
			params.put("gift_status1", 10);
			params.put("gift_status2", 0);
			
			boolean ret = true;
			List<BuyGift> bgs = this.buygiftService.queryPageList(params);
			
			if (bgs.size() > 0) {
				json.put("op_title", "您当前有正在进行的满就送");
				json.put("url", CommUtil.getURL(request)
						+ "/buygift_list");
				return_json(JSON.toJSONString(json), response);
			} else {
				String[] ids = goods_ids.split(",");
				String[] gids = gift_ids.split(",");
				Set<String> ids_set = Sets.newTreeSet();
				ids_set.addAll(Arrays.asList(ids));
				Set<String> gids_set = Sets.newTreeSet();
				gids_set.addAll(Arrays.asList(gids));
				for (String goods_id : ids_set) {

					for (String gift_id : gids_set) {
						if (goods_id.equals(gift_id)) {
							ret = false;
						}
					}
				}
				Set<String> validate_ids = Sets.newTreeSet();
				validate_ids.addAll(Arrays.asList(ids));
				validate_ids.addAll(Arrays.asList(gids));
				String goods_store_id;
				for (String vid : validate_ids) {
					Goods v_goods = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(vid));
					goods_store_id = v_goods.getGoods_store().getId()
							.toString();
					String user_store_id = user.getStore().getId().toString();
					if ((v_goods.getGoods_status() == 0)
							&& (v_goods.getGroup_buy() == 0)
							&& (v_goods.getOrder_enough_give_status() == 0)
							&& (v_goods.getOrder_enough_if_give() == 0)
							&& (v_goods.getEnough_reduce() == 0)
							&& (!goods_store_id.equals(user_store_id))) {
						ret = false;
					}
				}
				if (ret) {
					BuyGift buygift = null;
					if (id.equals("")) {
						buygift = (BuyGift) WebForm
								.toPo(request, BuyGift.class);
						buygift.setAddTime(new Date());
					} else {
						BuyGift obj = this.buygiftService.selectByPrimaryKey(Long
								.valueOf(Long.parseLong(id)));
						buygift = (BuyGift) WebForm.toPo(request, obj);
					}
					List<Map> goodses = Lists.newArrayList();
					Goods goods;
					for (Object goods_id : ids_set) {
						goods = this.goodsService.selectByPrimaryKey(CommUtil
								.null2Long(goods_id));
						goods.setOrder_enough_give_status(1);
						goods.setBuyGift_amount(BigDecimal.valueOf(CommUtil
								.null2Double(request
										.getParameter("condition_amount"))));
						this.goodsService.updateById(goods);
						Map<String, Object> map = Maps.newHashMap();
						map.put("goods_id", goods.getId());
						map.put("goods_name", goods.getGoods_name());
						map.put("goods_main_photo", goods.getGoods_main_photo()
								.getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName()
								+ "_small."
								+ goods.getGoods_main_photo().getExt());
						
						map.put("big_goods_main_photo", goods
								.getGoods_main_photo().getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName());
						
						map.put("goods_price", goods.getGoods_current_price());
						String goods_domainPath = CommUtil.getURL(request)
								+ "/items_" + goods.getId() + "";
						
						String store_domainPath = CommUtil.getURL(request)
								+ "/store_" + goods.getGoods_store().getId()
								+ "";
						
						if ((this.configService.getSysConfig()
								.getSecond_domain_open())
								&& (goods.getGoods_store()
										.getStore_second_domain() != "")) {
							if (goods.getGoods_type() == 1) {
								String store_second_domain = "http://"
										+ goods.getGoods_store()
												.getStore_second_domain() + "."
										+ CommUtil.generic_domain(request);
								goods_domainPath = store_second_domain
										+ "/items_" + goods.getId() + "";
								store_domainPath = store_second_domain;
							}
						}
						map.put("goods_domainPath", goods_domainPath);
						map.put("store_domainPath", store_domainPath);
						goodses.add(map);
					}
					buygift.setGoods_info(JSON.toJSONString(goodses));
					
					List<Map<String, Object>> gifts = Lists.newArrayList();
					for (String gift_id : gids_set) {
						goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gift_id));
						int count = CommUtil.null2Int(request.getParameter("gift_" + goods.getId()));
						goods.setOrder_enough_if_give(1);
						Map<String, Object> map = Maps.newHashMap();
						if (count >= goods.getGoods_inventory()) {
							map.put("storegoods_count", Integer.valueOf(1));
						} else {
							map.put("storegoods_count", Integer.valueOf(0));
							map.put("goods_count", Integer.valueOf(count));
						}
						map.put("goods_id", goods.getId());
						map.put("goods_name", goods.getGoods_name());
						map.put("goods_main_photo", goods.getGoods_main_photo()
								.getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName()
								+ "_small."
								+ goods.getGoods_main_photo().getExt());
						map.put("big_goods_main_photo", goods
								.getGoods_main_photo().getPath()
								+ "/"
								+ goods.getGoods_main_photo().getName());
						map.put("goods_price", goods.getGoods_current_price());
						String goods_domainPath = CommUtil.getURL(request)
								+ "/items_" + goods.getId() + "";
						String store_domainPath = CommUtil.getURL(request)
								+ "/store_" + goods.getGoods_store().getId()
								+ "";
						if ((this.configService.getSysConfig()
								.getSecond_domain_open())
								&& (goods.getGoods_store()
										.getStore_second_domain() != "")) {
							if (goods.getGoods_type() == 1) {
								String store_second_domain = "http://"
										+ goods.getGoods_store()
												.getStore_second_domain() + "."
										+ CommUtil.generic_domain(request);
								goods_domainPath = store_second_domain
										+ "/items_" + goods.getId() + "";
								store_domainPath = store_second_domain;
							}
						}
						map.put("goods_domainPath", goods_domainPath);
						map.put("store_domainPath", store_domainPath);
						goods.setBuyGift_amount(BigDecimal.valueOf(CommUtil
								.null2Double(request
										.getParameter("condition_amount"))));
						this.goodsService.updateById(goods);
						gifts.add(map);
					}
					buygift.setGift_info(JSON.toJSONString(gifts));
					buygift.setGift_status(0);
					buygift.setGift_type(1);
					buygift.setStore_id(user.getStore().getId());
					buygift.setStore_name(user.getStore().getStore_name());
					if (id.equals("")) {
						this.buygiftService.saveEntity(buygift);
					} else {
						this.buygiftService.updateById(buygift);
					}
					json.put("ret", Boolean.valueOf(true));
					json.put("op_title", "申请满就送成功");
					json.put("url", CommUtil.getURL(request)
							+ "/buygift_list");
					return_json(JSON.toJSONString(json), response);
				} else {
					json.put("op_title", "一个商品只能参加一个促销活动，申请满就送失败");
					json.put("url", CommUtil.getURL(request)
							+ "/buygift_list");
					return_json(JSON.toJSONString(json), response);
				}
			}
		}
	}
	
	/**
	 * 满就送添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "满就送添加", value = "/buy_goods_seller*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buy_goods_seller" })
	public ModelAndView buy_goods_seller(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/buy_goods_seller.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		List<GoodsClass> gcs = this.StoreTools.query_store_gc(user.getStore());
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 满就送添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "满就送添加", value = "/buy_gift_seller*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buy_gift_seller" })
	public ModelAndView buy_gift_seller(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/buy_gift_seller.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		List<GoodsClass> gcs = this.StoreTools.query_store_gc(user.getStore());
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 满就送商品加载
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param gc_id
	 * @param goods_ids
	 */
	@SecurityMapping(title = "满就送商品加载", value = "/buy_goods_seller_load*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buy_goods_seller_load" })
	public void buy_goods_seller_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id,
			String goods_ids) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_store_id", user.getStore().getId());
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_type", 1);
		
		Map<String,Object> maps= Maps.newHashMap();
		
		this.queryTools.shildGoodsStatusParams(maps);
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			Set<Long> ids = genericGcIds(gc);
			params.put("gc_ids", ids);
		}
		
		List<Goods> goods = this.goodsService.queryPageList(params);
		
		String[] ids = goods_ids.split(",");
		List ids_list = Arrays.asList(ids);
		List<Map> list = Lists.newArrayList();
		
		for (Goods obj : goods) {
			if (!ids_list.contains(obj.getId().toString())) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", obj.getId());
				map.put("store_price", obj.getStore_price());
				map.put("goods_name", obj.getGoods_name());
				map.put("store_inventory",Integer.valueOf(obj.getGoods_inventory()));
				if (obj.getGoods_main_photo() != null) {
					map.put("img", obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName() + "_small."
							+ obj.getGoods_main_photo().getExt());
				} else {
					map.put("img", this.configService.getSysConfig()
							.getGoodsImage().getPath()
							+ "/"
							+ this.configService.getSysConfig().getGoodsImage()
									.getName());
				}
				list.add(map);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商家中心
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商家中心", value = "/buygift_meal*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_meal" })
	public ModelAndView buygift_meal(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/buygift_meal.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}
	
	/**
	 * 商家中心
	 * @param request
	 * @param response
	 * @param meal_day
	 * @throws ParseException
	 */
	@SecurityMapping(title = "商家中心", value = "/buygift_meal_save*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_meal_save" })
	public void buygift_meal_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String meal_day)
			throws ParseException {
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(false));
		if (this.configService.getSysConfig().getBuygift_status() == 1) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int cost = this.configService.getSysConfig().getBuygift_meal_gold();
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
				Date day = user.getStore().getBuygift_meal_endTime();
				if (day != null) {
					if (day.after(new Date())) {
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买满就送套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.saveEntity(log);
						
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore()
								.getBuygift_meal_endTime());
						c_log.setEnd_time(addDate(user.getStore()
								.getBuygift_meal_endTime(),
								CommUtil.null2Long(Integer.valueOf(days))
										.longValue()));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(2);
						this.salesLogService.saveEntity(c_log);
						user.getStore().setBuygift_meal_endTime(
								addDate(user.getStore()
										.getBuygift_meal_endTime(), CommUtil
										.null2Long(Integer.valueOf(days))
										.longValue()));
						this.storeService.updateById(user.getStore());
					} else {
						Calendar ca = Calendar.getInstance();
						ca.add(5, days);
						SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						user.getStore().setBuygift_meal_endTime(
								CommUtil.formatDate(latertime,"yyyy-MM-dd HH:mm:ss"));
						this.storeService.updateById(user.getStore());
						
						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买满就送套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.saveEntity(log);
						
						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(new Date());
						c_log.setEnd_time(CommUtil.formatDate(latertime,"yyyy-MM-dd HH:mm:ss"));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(2);
						this.salesLogService.saveEntity(c_log);
					}
				} else {
					Calendar ca = Calendar.getInstance();
					ca.add(5, days);
					SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String latertime = bartDateFormat.format(ca.getTime());
					user.getStore().setBuygift_meal_endTime(
							CommUtil.formatDate(latertime,
									"yyyy-MM-dd HH:mm:ss"));
					this.storeService.updateById(user.getStore());
					
					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_content("购买满就送套餐");
					log.setGl_count(costday * cost);
					log.setGl_user(user);
					log.setGl_type(-1);
					this.goldLogService.saveEntity(log);
					
					SalesLog c_log = new SalesLog();
					c_log.setAddTime(new Date());
					c_log.setBegin_time(new Date());
					c_log.setEnd_time(CommUtil.formatDate(latertime,"yyyy-MM-dd HH:mm:ss"));
					c_log.setGold(costday * cost);
					c_log.setSales_info("套餐总时间增加" + days + "天");
					c_log.setStore_id(user.getStore().getId());
					c_log.setSales_type(2);
					this.salesLogService.saveEntity(c_log);
				}
				json.put("ret", Boolean.valueOf(true));
				json.put("op_title", "购买成功");
				json.put("url", CommUtil.getURL(request)
						+ "/buygift_list");
				return_json(JSON.toJSONString(json), response);
			} else {
				json.put("op_title", "您的金币不足，无法购买满就送套餐");
				json.put("url", CommUtil.getURL(request)
						+ "/buygift_list");
				return_json(JSON.toJSONString(json), response);
			}
		} else {
			json.put("op_title", "购买失败");
			json.put("url", CommUtil.getURL(request) + "/buygift_list");
			return_json(JSON.toJSONString(json), response);
		}
	}

	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24L * 60L * 60L * 1000L;
		time += day;
		return new Date(time);
	}
	
	/**
	 * 满就送销售套餐日志
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就送销售套餐日志", value = "/buygift_meal_log*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_meal_log" })
	public ModelAndView buygift_meal_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/buygift_meal_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		maps.put("store_id", store.getId());
		maps.put("sales_type", 2);
		
		IPageList pList = this.salesLogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gid
	 * @param count
	 */
	@RequestMapping({ "/gift_count_adjust" })
	public void gift_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String gid, String count) {
		String code = "100";
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
		if ((goods != null) && (CommUtil.null2Int(count) > goods.getGoods_inventory())) {
			count = goods.getGoods_inventory() + "";
			code = "200";
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("count", count);
		map.put("code", code);
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
	 * 满就送商品删除
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就送商品删除", value = "/buygift_del*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_del" })
	public String buygift_del(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		BuyGift bg = this.buygiftService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((bg != null)
				&& (bg.getStore_id().equals(user.getStore().getId()))
				&& ((bg.getGift_status() == -10) || (bg.getGift_status() == 20))) {
			List<Map> maps = JSON.parseArray(bg.getGift_info(), Map.class);
			maps.addAll(JSON.parseArray(bg.getGoods_info(), Map.class));
			for (Map map : maps) {
				Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(map.get("goods_id")));
				if (goods != null) {
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					this.goodsService.updateById(goods);
				}
			}
			this.buygiftService.deleteById(bg.getId());
		}
		return "redirect:buygift_list";
	}
	
	/**
	 * 满就送商品停止
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就送商品停止", value = "/buygift_stop*", rtype = "seller", rname = "满就送", rcode = "buygift_seller", rgroup = "促销推广")
	@RequestMapping({ "/buygift_stop" })
	public String buygift_stop(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		BuyGift bg = this.buygiftService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((bg != null) && (bg.getStore_id().equals(user.getStore().getId()))
				&& (bg.getGift_status() == 10)) {
			bg.setGift_status(20);
			this.buygiftService.updateById(bg);
			List<Map> maps = JSON.parseArray(bg.getGift_info(), Map.class);
			maps.addAll(JSON.parseArray(bg.getGoods_info(), Map.class));
			for (Map map : maps) {
				Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
						.null2Long(map.get("goods_id")));
				if (goods != null) {
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					goods.setBuyGift_amount(new BigDecimal(0.0D));
					this.goodsService.updateById(goods);
				}
			}
		}
		return "redirect:buygift_list";
	}

	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	public void return_json(String json, HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
