package com.redpigmall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.FootPoint;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.view.web.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigRedPigGlobalToolbarViewAction.java
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
 * @date 2015-2-4
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigGlobalToolbarViewAction extends BaseAction{
	
	/**
	 * 导航工具栏
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/toolbar" })
	public ModelAndView toolbar(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("toolbar.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<GoodsCart> carts = this.goodsCartService.cart_list(request, null,null, null, false);
		
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			List<Coupon> cp_list = Lists.newArrayList();
			List<Coupon> coupons = Lists.newArrayList();
			if ((request.getParameter("goods_id") != null)
					&& (!"".equals(request.getParameter("goods_id")))) {
				coupons = this.couponViewTools.getUsableCoupon(CommUtil.null2Long(request.getParameter("goods_id")),user.getId());
			}
			if ((request.getParameter("store_id") != null)
					&& (!"".equals(request.getParameter("store_id")))) {
				coupons = this.couponViewTools.getStoreUsableCoupon(CommUtil.null2Long(request.getParameter("store_id")),user.getId());
				mv.addObject("store_id", request.getParameter("store_id"));
			}
			if ((coupons != null) && (coupons.size() > 0)) {
				for (Coupon cp : coupons) {
					boolean flag = this.couponViewTools.isPossessCoupon(
							cp.getId(), user.getId());
					if (!flag) {
						cp_list.add(cp);
					}
				}
			}
			mv.addObject("size", Integer.valueOf(cp_list.size()));
		}
		
		mv.addObject("goods_compare_list", request.getSession(false).getAttribute("goods_compare_cart"));
		mv.addObject("carts", carts);
		mv.addObject("goods_id", request.getParameter("goods_id"));
		mv.addObject("user", user);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/redpig_toolbar" })
	public ModelAndView redpig_toolbar(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("toolbar.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 1, request,
				response);

		List<GoodsCart> carts = this.goodsCartService.cart_list(request, null,
				null, null, false);

		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			List<Coupon> cp_list = Lists.newArrayList();
			List<Coupon> coupons = Lists.newArrayList();
			if ((request.getParameter("goods_id") != null)
					&& (!"".equals(request.getParameter("goods_id")))) {
				coupons = this.couponViewTools.getUsableCoupon(
						CommUtil.null2Long(request.getParameter("goods_id")),
						user.getId());
			}

			if ((request.getParameter("store_id") != null)
					&& (!"".equals(request.getParameter("store_id")))) {
				coupons = this.couponViewTools.getStoreUsableCoupon(
						CommUtil.null2Long(request.getParameter("store_id")),
						user.getId());
				mv.addObject("store_id", request.getParameter("store_id"));
			}
			if ((coupons != null) && (coupons.size() > 0)) {
				for (Coupon cp : coupons) {
					boolean flag = this.couponViewTools.isPossessCoupon(
							cp.getId(), user.getId());
					if (!flag) {
						cp_list.add(cp);
					}
				}
			}
			mv.addObject("size", Integer.valueOf(cp_list.size()));
		}
		List<Goods> goods_compare_list = (List<Goods>) request.getSession(false).getAttribute("goods_compare_cart");

		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		mv.addObject("goods_compare_list", goods_compare_list);
		mv.addObject("carts", carts);
		mv.addObject("goods_id", request.getParameter("goods_id"));
		mv.addObject("user", user);
		return mv;
	}
	
	/**
	 * 用户足迹删除
	 * @param request
	 * @param response
	 * @param date
	 * @param goods_id
	 */
	@RequestMapping({ "/fp_remove" })
	public void fp_remove(HttpServletRequest request,
			HttpServletResponse response, String date, String goods_id) {
		boolean ret = true;
		if ((!CommUtil.null2String(date).equals(""))
				&& (CommUtil.null2String(goods_id).equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("fp_date", CommUtil.formatDate(date));
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			
			List<FootPoint> fps = this.footPointService.queryPageList(params);
			
			for (FootPoint fp : fps) {
				this.footPointService.deleteById(fp.getId());
			}
		}
		if ((!CommUtil.null2String(date).equals(""))
				&& (!CommUtil.null2String(goods_id).equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("fp_date", CommUtil.formatDate(date));
			params.put("fp_user_id", SecurityUserHolder.getCurrentUser()
					.getId());
			
			List<FootPoint> fps = this.footPointService.queryPageList(params);
			
			for (FootPoint fp : fps) {
				List<Map> list = JSON.parseArray(fp.getFp_goods_content(),
						Map.class);
				for (Map map : list) {
					if (CommUtil.null2String(map.get("goods_id")).equals(
							goods_id)) {
						list.remove(map);
						break;
					}
				}
				if (list.size() > 0) {
					fp.setFp_goods_content(JSON.toJSONString(list));
					fp.setFp_goods_count(list.size());
					this.footPointService.updateById(fp);
				} else {
					this.footPointService.deleteById(fp.getId());
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/collect" })
	public ModelAndView collect(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("toolbar_collect.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(null,30, null, null);
		maps.put("type", 0);
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		IPageList pList = this.favoriteService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/goods_com" })
	public ModelAndView goods_com(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("toolbar_goods_com.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		mv.addObject("goods_compare_list", request.getSession(false).getAttribute("goods_compare_cart"));
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/personal" })
	public ModelAndView personal(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("toolbar_personal.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		mv.addObject("user", user);
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("status", Integer.valueOf(0));
		params.put("toUser_id", user.getId());
		params.put("parent", -1);
		params.put("orderBy","addTime");
		params.put("orderType","desc");
		
		int m  = this.messageService.selectCount(params);
		
		mv.addObject("msg_size", m);
		
		params.clear();
		params.put("user_id", user.getId());
		params.put("status", Integer.valueOf(0));
		
		List<CouponInfo> couponInfos = this.couponInfoService.queryPageList(params);
		
		if (couponInfos != null) {
			mv.addObject("couponInfos", couponInfos);
		}
		return mv;
	}
	
	/**
	 * 优惠券
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param store_id
	 * @return
	 */
	@RequestMapping({ "/coupon" })
	public ModelAndView coupon(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String store_id) {
		ModelAndView mv = new RedPigJModelAndView("toolbar_coupon.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		Map<String,Object> maps= this.redPigQueryTools.getParams(null, null, null);
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		maps.put("status", 0);
		
		List<Coupon> coupons = Lists.newArrayList();
		
		if ((goods_id != null) && (!"".equals(goods_id))) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			if (goods != null) {
				coupons = this.couponViewTools.getUsableCoupon(CommUtil.null2Long(goods_id), user.getId());
			}
			mv.addObject("goods_id", goods_id);
		}
		if ((store_id != null) && (!"".equals(store_id))) {
			coupons = this.couponViewTools.getStoreUsableCoupon(
					CommUtil.null2Long(store_id), user.getId());
			mv.addObject("store_id", store_id);
		}
		
		if (((store_id == null) || (store_id == ""))
				&& ((goods_id == null) || (goods_id == ""))) {
			mv.addObject("all", true);
		}
		
		mv.addObject("coupons", coupons);
		mv.addObject("couponViewTools", this.couponViewTools);
		mv.addObject("user", user);
		IPageList pList = this.couponInfoService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/user_footer" })
	public ModelAndView user_footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("toolbar_footers.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(null,5, "addTime", "desc");
		maps.put("fp_user_id", user.getId());
		
		IPageList pList = this.footPointService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("footPointTools", this.footPointTools);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/del_fav" })
	public void del_fav(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Favorite favorite = this.favoriteService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		
		if (favorite.getGoods_id() != null) {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(favorite.getGoods_id()));
			this.goodsTools.updateGoodsLucene(obj);
		}
		
		if (favorite.getType() == 0) {
			Goods goods = this.goodsService.selectByPrimaryKey(favorite.getGoods_id());
			goods.setGoods_collect(goods.getGoods_collect() - 1);
			this.goodsService.updateById(goods);
		}
		
		if (favorite.getType() == 1) {
			Store store = this.storeService.selectByPrimaryKey(favorite.getStore_id());
			store.setFavorite_count(store.getFavorite_count() - 1);
			this.storeService.updateById(store);
		}
		this.favoriteService.deleteById(Long.valueOf(Long.parseLong(id)));
	}
	
	/**
	 * 加载购物车
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/cart_load" })
	public ModelAndView cart_load(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("toolbar_goodscart.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<GoodsCart> carts = this.goodsCartService.cart_list(request, null,null, null, false);
		Date date = new Date();
		
		if (carts.size() > 0) {
			Set<Long> set = Sets.newHashSet();
			List<GoodsCart> native_goods = Lists.newArrayList();
			Map<Long, List<GoodsCart>> ermap = Maps.newHashMap();
			Map<Long, String> erString = Maps.newHashMap();
			EnoughReduce er;
			for (GoodsCart cart : carts) {
				if ((cart.getGoods().getOrder_enough_give_status() == 1) // 满就送状态 1为满就送商品
						&& (cart.getGoods().getBuyGift_id() != null)) {// 对应的满就送id
					BuyGift bg = this.buyGiftService.selectByPrimaryKey(cart.getGoods().getBuyGift_id());
					
					if (bg.getBeginTime().before(date)) {
						set.add(cart.getGoods().getBuyGift_id());
					} else {
						native_goods.add(cart);
					}
					
				} else if (cart.getGoods().getEnough_reduce() == 1) {
					String er_id = cart.getGoods().getOrder_enough_reduce_id();
					er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
					
					if (er.getErbegin_time().before(date)) {
						if (ermap.containsKey(er.getId())) {
							ermap.get(er.getId()).add(cart);
						} else {
							List<GoodsCart> list = Lists.newArrayList();
							list.add(cart);
							ermap.put(er.getId(), list);
							Map<String, Object> map = JSON.parseObject(er.getEr_json());
							double k = 0.0D;
							String str = "";
							if (map != null) {
								for (String key : map.keySet()) {
									if (k == 0.0D) {
										k = Double.parseDouble(key);
										str = "活动商品购满" + k + "元，即可享受满减";
									}
									if (Double.parseDouble(key) < k) {
										k = Double.parseDouble(key.toString());
										str = "活动商品购满" + k + "元，即可享受满减";
									}
								}
							}
							erString.put(er.getId(), str);
						}
					} else {
						native_goods.add(cart);
					}
				} else {
					native_goods.add(cart);
				}
			}
			
			mv.addObject("erString", erString);
			mv.addObject("er_goods", ermap);
			Map<String, List<GoodsCart>> separate_carts = separateCombin(native_goods);
			mv.addObject("cart", separate_carts.get("normal"));
			mv.addObject("combin_carts", separate_carts.get("combin"));
			
			if (set.size() > 0) {
				Map<Long,ArrayList> map = Maps.newHashMap();
				for (Long id : set) {
					map.put(id, new ArrayList());
				}
				for (GoodsCart cart : carts) {
					if ((cart.getGoods().getOrder_enough_give_status() == 1)//1为满就送商品
							&& (cart.getGoods().getBuyGift_id() != null)//// 对应的满就送id
							&& (map.containsKey(cart.getGoods().getBuyGift_id())) //如果包含满就送ID
							) {
						map.get(cart.getGoods().getBuyGift_id()).add(cart);
					}
				}
				mv.addObject("ac_goods", map);
			}
			mv.addObject("total_price", calCartPrice(carts, ""));
			mv.addObject("carts", carts);
		}
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	/**
	 * 组合销售和非组合销售分开
	 * @param carts
	 * @return
	 */
	private Map<String, List<GoodsCart>> separateCombin(List<GoodsCart> carts) {
		Map<String, List<GoodsCart>> map = Maps.newHashMap();
		List<GoodsCart> normal_carts = Lists.newArrayList();
		List<GoodsCart> combin_carts = Lists.newArrayList();
		for (GoodsCart cart : carts) {
			if ((cart.getCart_type() != null)
					&& (cart.getCart_type().equals("combin"))) {//如果是组合销售
				if (cart.getCombin_main() == 1) {//为套装主购物车
					combin_carts.add(cart);
				}
			} else {//不是组合销售
				normal_carts.add(cart);
			}
		}
		map.put("combin", combin_carts);
		map.put("normal", normal_carts);
		return map;
	}
	
	/**
	 * 计算购物车总价
	 * @param carts
	 * @param gcs
	 * @return
	 */
	private double calCartPrice(List<GoodsCart> carts, String gcs) {
		double all_price = 0.0D;
		Map<String, Double> ermap = Maps.newHashMap();
		
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				
				if ((gc.getCart_type() == null) || (gc.getCart_type().equals(""))) {//如果没有组合销售的情况下
					//总价 = 总价 + (每个购物车数量 X 购物车价格)
					all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(),gc.getPrice()));
					
				} else if ((gc.getCart_type().equals("combin")) && (gc.getCombin_main() == 1)) {//如果是组合销售情况并且为套装主购物车
					//首先获取购物车↓
					/**
					 * 套装购物车详情，使用json管理，
					 * 	{
					 * 	"suit_count":2,
					 * 	"plan_goods_price":"429",
					 * 	"all_goods_price":"236.00",
					 * 	"goods_list":
					 * 		[{
					 * 			"id":92,"price":25.0,
					 * 			"inventory":465765,
					 * 			"store_price":25.0,
					 * 			"name":"RAYLI 韩版时尚潮帽 百变女帽子围脖两用 可爱球球保暖毛线 HA048",
					 * 			"img":"upload/system/self_goods/a7c137ef-0933-4c72-8be6-e7eb7fdfb3c7.jpg_small.jpg",
					 * 			"url":"http://localhost/goods_92.htm"
					 * 		}],
					 * 	"suit_all_price":"429.00"
					 * }
					 */
					
					Map<String, Object> map = JSON.parseObject(gc.getCombin_suit_info());
					//获取里面的suit_all_price字段即可获取总价
					all_price = CommUtil.add(all_price,map.get("suit_all_price"));
				}
				
				if (gc.getGoods().getEnough_reduce() == 1) {// 如果已参加满就减
					String er_id = gc.getGoods().getOrder_enough_reduce_id();// 获得每个商品对应的满就减id
					if (ermap.containsKey(er_id)) {//如果已经包含该满就减活动ID
						double last_price = ermap.get(er_id);
						//满就减价格=满就减+(购物车数量 X 购物车价格)
						ermap.put(er_id, CommUtil.add(last_price,CommUtil.mul(gc.getCount(),gc.getPrice())));
					} else {//如果是第一次添加
						//满就减价格 = 购物车数量 X 购物车价格
						ermap.put(er_id,CommUtil.mul(gc.getCount(),gc.getPrice()));
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						//如果是组合销售并且为套装主购物车
						if ((gc.getCart_type() != null) && (gc.getCart_type().equals("combin")) && (gc.getCombin_main() == 1)) {
							/**
							 * 套装购物车详情，使用json管理，
							 * 	{
							 * 	"suit_count":2,
							 * 	"plan_goods_price":"429",
							 * 	"all_goods_price":"236.00",
							 * 	"goods_list":
							 * 		[{
							 * 			"id":92,"price":25.0,
							 * 			"inventory":465765,
							 * 			"store_price":25.0,
							 * 			"name":"RAYLI 韩版时尚潮帽 百变女帽子围脖两用 可爱球球保暖毛线 HA048",
							 * 			"img":"upload/system/self_goods/a7c137ef-0933-4c72-8be6-e7eb7fdfb3c7.jpg_small.jpg",
							 * 			"url":"http://localhost/goods_92.htm"
							 * 		}],
							 * 	"suit_all_price":"429.00"
							 * }
							 */
							Map<String, Object> map = JSON.parseObject(gc.getCombin_suit_info());
							//总价=suit_all_price价格
							all_price = CommUtil.add(all_price,map.get("suit_all_price"));
						} else {//如果不是组合销售
							//总价 = 总价 + (购物车数量 X 购物车价格)
							all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(),gc.getPrice()));
						}
						
						if (gc.getGoods().getEnough_reduce() == 1) {//如果已参加满就减活动
							String er_id1 = gc.getGoods().getOrder_enough_reduce_id();// 获得每个商品对应的满就减id
							if (ermap.containsKey(er_id1)) {//如果已经包含该满就减活动ID
								double last_price = ermap.get(er_id1);
								//满就减价格=满就减+(购物车数量 X 购物车价格)
								ermap.put(er_id1, CommUtil.add(last_price, CommUtil.mul(gc.getCount(),gc.getPrice())));
							} else {//如果是第一次添加
								//满就减价格 = 购物车数量 X 购物车价格
								ermap.put(er_id1, CommUtil.mul(gc.getCount(),gc.getPrice()));
							}
						}
					}
				}
			}
		}
		
		double all_enough_reduce = 0.0D;//所有满就减费用
		for (String er_id1 : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id1));
			if ((er.getErstatus() == 10)//10为 审核通过 
					&& (er.getErbegin_time().before(new Date()))//满就减开始时间大于当前时间:意思已经开启了满减活动
					) {
				String erjson = er.getEr_json();//[200:10,300:20,400:50]满200减10、满300减20。。。。json格式
				double er_money = ermap.get(er_id1);
				Map fromJson = JSON.parseObject(erjson);
				double reduce = 0.0D;
				if (fromJson != null) {
					//key:满足的费用
					//value:满租之后要减去的费用
					for (Object enough : fromJson.keySet()) {
						/**
						 *这里的意思是:
						 *	如果这次的满就减费用原价er_money满足(大于)满就减活动时候
						 *	就把满就减需要减去的费用记录下单,但是整个满就减只能减一次
						 *	也就是循环到追后一个满足就行
						 *例如:商品价格是500
						 *	满就减的活动有:[100:10,200:20,300:50:400:60:800:200]
						 *这时候按照以下循环就可以正确的走到:满400减60这里 
						 */
						if (er_money >= CommUtil.null2Double(enough)) {
							reduce = CommUtil.null2Double(fromJson.get(enough));
						}
					}
				}
				all_enough_reduce = CommUtil.add(all_enough_reduce,reduce);
				
			}
		}
		
		double d2 = Math.round((all_price - all_enough_reduce) * 100.0D) / 100.0D;
		return CommUtil.null2Double(CommUtil.formatMoney(d2));
	}
}
