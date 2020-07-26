package com.redpigmall.module.weixin.view.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.api.tools.XMLUtil;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.CloudPurchaseOrder;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.DeliveryAddress;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreHouse;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VatInvoice;
import com.redpigmall.domain.VerifyCode;
import com.redpigmall.module.weixin.manage.base.BaseAction;

/**
 * <p>
 * Title: RedPigWeixinCartViewAction.java
 * </p>
 * 
 * <p>
 * Description:移动端用户中心购物车控制器
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
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigWeixinCartViewAction extends BaseAction{
	
	/**
	 * 删除购物车
	 * @param request
	 * @param response
	 * @param ids
	 */
	@RequestMapping({ "/remove_goods_cart" })
	public void remove_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String ids) {
		List<String> list_ids = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		Double total_price = Double.valueOf(0.0D);
		String code = "100";
		List<GoodsCart> carts = Lists.newArrayList();
		
		if ((ids != null) && (!ids.equals(""))) {
			String[] cart_ids = ids.split(",");
			
			for (String id : cart_ids) {
				
				if ((id != null) && (!id.equals(""))) {
					list_ids.add(id);
					String[] suit_cart_ids;
					if (id.indexOf("combin") < 0) {
						GoodsCart gc = this.goodsCartService.selectByPrimaryKey(CommUtil.null2Long(id));
						if (gc != null) {
							if ((gc.getCart_type() != null)
									&& (gc.getCart_type().equals("combin"))) {
								params.clear();
								params.put("combin_mark", gc.getCombin_mark());
								params.put("combin_main", 1);
								List<GoodsCart> suit_main_carts = this.goodsCartService.queryPageList(params);
								
								if (suit_main_carts.size() > 0) {
									suit_cart_ids = suit_main_carts.get(0).getCombin_suit_ids().split(",");
									String[] gsp_ids;
									String spec_info = null;
									for (String suit_cart_id : suit_cart_ids) {
										
										if (!suit_cart_id.equals("")) {
											GoodsCart suit_cart = this.goodsCartService.selectByPrimaryKey(CommUtil.null2Long(suit_cart_id));
											
											if (suit_cart != null) {
												suit_cart.setCart_type(null);
												suit_cart.setCombin_mark(null);
												suit_cart.setCombin_main(0);
												suit_cart.setCombin_suit_ids(null);
												
												String default_gsp = this.cartTools.getGoodsDefaultGsp(suit_cart.getGoods());
												
												double default_price = 
														CommUtil.null2Double(
																this.cartTools.getGoodsDefaultInfo(
																		request,suit_cart.getGoods(),default_gsp).get("price"));
												
												suit_cart.setPrice(BigDecimal.valueOf(default_price));
												suit_cart.setCart_gsp(default_gsp);
												
												gsp_ids = CommUtil.null2String(default_gsp).split(",");
												
												spec_info = "";
												for (String gsp_id : gsp_ids) {
													
													GoodsSpecProperty spec_property = this.goodsSpecPropertyService.selectByPrimaryKey(CommUtil.null2Long(gsp_id));
													
													if (spec_property != null) {
														suit_cart.getGsps().add(spec_property);
														
														spec_info = spec_property.getSpec().getName() + "：" + spec_property.getValue() + "<br>" + spec_info;
													}
												}
												suit_cart.setSpec_info(spec_info);
												this.goodsCartService.updateById(suit_cart);
											}
										}
									}
									for (GoodsCart main_suit_gc : suit_main_carts) {
										main_suit_gc.setCart_type(null);
										main_suit_gc.setCombin_mark(null);
										main_suit_gc.setCombin_main(0);
										main_suit_gc.setCombin_suit_ids(null);
										main_suit_gc.setCombin_version(null);
										
										String default_gsp = this.cartTools.getGoodsDefaultGsp(main_suit_gc.getGoods());
										
										double default_price = CommUtil.null2Double(
												this.cartTools.getGoodsDefaultInfo(
														request,main_suit_gc.getGoods(),default_gsp).get("price"));
										
										main_suit_gc.setPrice(BigDecimal.valueOf(default_price));
										main_suit_gc.setCart_gsp(default_gsp);
										gsp_ids = CommUtil.null2String(default_gsp).split(",");
										
										for (String gsp_id1 : gsp_ids) {
											GoodsSpecProperty spec_property = this.goodsSpecPropertyService.selectByPrimaryKey(CommUtil.null2Long(gsp_id1));
											if (spec_property != null) {
												main_suit_gc.getGsps().add(spec_property);
												spec_info = spec_property.getSpec().getName() + "：" + spec_property.getValue() + "<br>" + spec_info;
											}
										}
										main_suit_gc.setSpec_info(spec_info);
										this.goodsCartService.updateById(main_suit_gc);
									}
								}
							}
							gc.getGsps().clear();
							this.goodsCartService.deleteById(CommUtil.null2Long(id));
						}
					} else {
						params.clear();
						params.put("combin_mark", id);
						List<GoodsCart> suit_carts = this.goodsCartService.queryPageList(params);
						
						for (GoodsCart suit_gc : suit_carts) {
							this.goodsCartService.deleteById(suit_gc.getId());
						}
					}
				}
			}
		} else {
			code = "200";
		}
		
		carts = cart_calc(request);
		total_price = this.cartTools.getPriceByCarts(carts, "");
		Map<String, Object> map = Maps.newHashMap();
		map.put("total_price", total_price);
		map.put("code", code);
		map.put("count", carts.size());
		map.put("ids", list_ids);
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
	 * 设置购物车的规格文字说明，将原有规格的名字替换成自定义的
	 * 
	 * @param goods
	 * @param obj
	 * @param gsp_ids
	 */
	void setGoodsCartSpec(Goods goods, GoodsCart obj, String[] gsp_ids) {
		String spec_info = "";
		List<Map> goods_specs_info = goods.getGoods_specs_info() == null ? new ArrayList()
				: JSON.parseArray(goods.getGoods_specs_info(), Map.class);
		for (String gsp_id : gsp_ids) {
			GoodsSpecProperty spec_property = this.goodsSpecPropertyService
					.selectByPrimaryKey(CommUtil.null2Long(gsp_id));
			if (spec_property != null) {
				obj.getGsps().add(spec_property);
				spec_info = spec_info + spec_property.getSpec().getName() + "：";
				if (goods_specs_info.size() > 0) {
					for (Map map : goods_specs_info) {
						if (CommUtil.null2Long(map.get("id")).equals(
								spec_property.getId())) {
							spec_info = spec_info + map.get("name").toString();
						}
					}
				} else {
					spec_info = spec_info + spec_property.getValue();
				}
				spec_info = spec_info + "<br>";
			}
		}
		obj.setSpec_info(spec_info);
	}
	/**
	 * 添加商品到购物车
	 * 
	 * @param request
	 * @param response
	 * @param id
	 *            添加到购物车的商品id
	 * @param count
	 *            添加到购物车的商品数量
	 * @param price
	 *            添加到购物车的商品的价格,该逻辑会更加gsp再次计算实际价格，避免用户在前端篡改
	 * @param gsp
	 *            商品的属性值，这里传递id值，如12,1,21
	 * @param buy_type
	 *            购买的商品类型，组合销售时用于判断是套装购买还是配件购买,普通商品：不传值，配件组合:parts,组合套装：suit
	 * @param combin_ids
	 *            组合搭配中配件id
	 */
	@RequestMapping({ "/add_goods_cart" })
	public void add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count, String gsp,
			String buy_type, String combin_ids, String combin_version,
			String area_id) {
		Map json_map = this.goodsCartService.addGoodsCart(request, response,
				id, count, gsp, buy_type, combin_ids, combin_version, null,
				null, null, null);

		List<GoodsCart> carts = cart_calc(request);
		json_map.put("count", Integer.valueOf(carts.size()));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 移动端用户查看购物车
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	// @SecurityMapping(title = "查看购物车", value = "/goods_cart1.htm*",
	// rtype = "buyer", rname = "购物流程0", rcode = "wap_goods_cart", rgroup =
	// "移动端购物")
	@RequestMapping({ "/goods_cart1" })
	public ModelAndView goods_cart1(HttpServletRequest request,
			HttpServletResponse response, String area_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_cart1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = cart_calc(request);
		List<GoodsCart> removeList = Lists.newArrayList();
		for (GoodsCart cart : carts) {
			if (cart.getGoods().getAdvance_sale_type() == 1) {
				cart.getGsps().clear();
				cart.setUser(null);
				this.goodsCartService.updateById(cart);
				removeList.add(cart);
			}
		}
		carts.removeAll(removeList);
		Date date = new Date();
		int count;
		if (carts.size() > 0) {
			Object set = Sets.newHashSet();
			List<GoodsCart> native_goods = Lists.newArrayList();
			Map<Long, List<GoodsCart>> ermap = Maps.newHashMap();
			Map<Long, String> erString = Maps.newHashMap();
			User user = SecurityUserHolder.getCurrentUser();
			count = -1;
			for (GoodsCart cart : carts) {
				if ((cart.getGoods().getGoods_limit() == 1)
						&& (cart.getGoods().getGoods_limit_count() > 0)) {
					count = cart.getGoods().getGoods_limit_count();
					if (user != null) {
						String info = CommUtil.null2String(this.userService
								.selectByPrimaryKey(user.getId())
								.getBuy_goods_limit_info());
						if (info.equals("")) {
							count = cart.getGoods().getGoods_limit_count();
						} else {
							Map maps = JSON.parseObject(CommUtil
									.null2String(info));
							List<Map> list = (List) maps.get("data");
							for (Map map : list) {
								String gid = CommUtil.null2String(map
										.get("gid"));
								if (CommUtil.null2Int(gid) == cart.getGoods()
										.getId().longValue()) {
									count = cart.getGoods()
											.getGoods_limit_count()
											- CommUtil.null2Int(map
													.get("count"));
									if (count < 1) {
										count = 0;
									}
								}
							}
						}
					}
				} else {
					count = -1;
				}
				cart.setRemain_count(count);
				this.goodsCartService.updateById(cart);
			}
		}
		if (carts.size() > 0) {
			Set set = new HashSet();
			List<GoodsCart> native_goods = Lists.newArrayList();
			Map<Long, List<GoodsCart>> ermap = Maps.newHashMap();
			Map<Long, String> erString = Maps.newHashMap();
//			Object er;
			for (GoodsCart cart : carts) {
				if ((cart.getGoods().getOrder_enough_give_status() == 1)
						&& (cart.getGoods().getBuyGift_id() != null)) {
					BuyGift bg = this.buyGiftService.selectByPrimaryKey(cart.getGoods()
							.getBuyGift_id());
					if (bg.getBeginTime().before(date)) {
						((Set) set).add(cart.getGoods().getBuyGift_id());
					} else {
						native_goods.add(cart);
					}
				} else if (cart.getGoods().getEnough_reduce() == 1) {
					String er_id = cart.getGoods().getOrder_enough_reduce_id();
					EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil
							.null2Long(er_id));
					if (er.getErbegin_time().before(date)) {
						if (ermap.containsKey(er.getId())) {
							ermap.get(er.getId()).add(cart);
						} else {
							List<GoodsCart> list = Lists.newArrayList();
							list.add(cart);
							ermap.put(er.getId(), list);
							Map<String, String> map = (Map) JSON.parseObject(er.getEr_json(),Map.class);
							double k = 0.0D;
							String str = "";
							for (Object key : map.keySet()) {
								if (k == 0.0D) {
									k = Double.parseDouble(key.toString());
									str = "活动商品购满" + k + "元，即可享受满减";
								}
								if (Double.parseDouble(key.toString()) < k) {
									k = Double.parseDouble(key.toString());
									str = "活动商品购满" + k + "元，即可享受满减";
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
			mv.addObject("cart", (List) separate_carts.get("normal"));
			mv.addObject("combin_carts", (List) separate_carts.get("combin"));
			if (((Set) set).size() > 0) {
				Map<Long, List<GoodsCart>> map = Maps.newHashMap();
				for (Iterator er = set.iterator();er.hasNext();) {
					Long id = (Long) ((Iterator) er).next();
					map.put(id, new ArrayList());
				}
				for (Iterator er = carts.iterator(); ((Iterator) er).hasNext();) {
					GoodsCart cart = (GoodsCart) ((Iterator) er).next();
					if ((cart.getGoods().getOrder_enough_give_status() == 1)
							&& (cart.getGoods().getBuyGift_id() != null)
							&& (map.containsKey(cart.getGoods().getBuyGift_id()))) {
						((List) map.get(cart.getGoods().getBuyGift_id()))
								.add(cart);
					}
				}
				mv.addObject("ac_goods", map);
			}
		} else {
			mv = new RedPigJModelAndView("weixin/goods_cart1_none.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if ((area_id != null) && (!area_id.equals(""))) {
			this.areaViewTools.setDefaultArea(request, area_id);
			mv.addObject("area_id", area_id);
		}
		this.areaViewTools.getUserAreaInfo(request, mv);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("weixinCartTools", this.weixinCartTools);
		mv.addObject("combinTools", this.combinTools);
		return mv;
	}

	@RequestMapping({ "/get_area_info" })
	public void get_area_info(HttpServletRequest request,
			HttpServletResponse response, String area_id) {
		if ((area_id != null) && (!area_id.equals(""))) {
			this.areaViewTools.setDefaultArea(request, area_id);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/cart_coupon_load" })
	public ModelAndView cart_coupon_load(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/cart_coupon_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List coupons = Lists.newArrayList();
		if ((id != null) && (!"".equals(id))) {
			coupons = this.couponViewTools
					.getStoreUsableCoupon(CommUtil.null2Long(id),
							SecurityUserHolder.getCurrentUser().getId());
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (store != null) {
				mv.addObject("store", store);
			}
		} else {
			coupons = this.couponViewTools.getUsableCoupon(CommUtil
					.null2Long(goods_id), SecurityUserHolder.getCurrentUser()
					.getId());
			mv.addObject("goods_id", goods_id);
		}
		mv.addObject("coupons", coupons);
		mv.addObject("couponViewTools", this.couponViewTools);
		return mv;
	}

	@RequestMapping({ "/capture_goods_coupon" })
	public void capture_goods_coupon(HttpServletRequest request,
			HttpServletResponse response, String store_id, String co_id,
			String goods_id) {
		List<String> coupon_ids = Lists.newArrayList();
		boolean ret = false;
		if ("all".equals(co_id)) {
			List<Coupon> coupons = Lists.newArrayList();
			if ((store_id != null) && (!"".equals(store_id))) {
				coupons = this.couponViewTools.getStoreUsableCoupon(CommUtil
						.null2Long(store_id), SecurityUserHolder
						.getCurrentUser().getId());
			} else {
				coupons = this.couponViewTools.getUsableCoupon(CommUtil
						.null2Long(goods_id), SecurityUserHolder
						.getCurrentUser().getId());
			}
			for (Coupon cp : coupons) {
				coupon_ids.add(CommUtil.null2String(cp.getId()));
			}
		} else if ((co_id != null) && (!"".equals(co_id))) {
			coupon_ids.add(co_id);
		}
		List list = Lists.newArrayList();
		for (String coupon_id : coupon_ids) {
			if (isUsableCoupon(SecurityUserHolder.getCurrentUser().getId(),
					coupon_id)) {
				Coupon coupon = this.couponService.selectByPrimaryKey(CommUtil
						.null2Long(coupon_id));
				if (coupon.getCoupon_surplus_amount() > 0) {
					User user = this.userService.selectByPrimaryKey(SecurityUserHolder
							.getCurrentUser().getId());
					CouponInfo info = new CouponInfo();
					info.setAddTime(new Date());
					info.setCoupon(coupon);
					info.setCoupon_sn(UUID.randomUUID().toString());
					info.setUser(user);
					info.setCoupon_amount(coupon.getCoupon_amount());
					info.setCoupon_order_amount(coupon.getCoupon_order_amount());
					info.setEndDate(coupon.getCoupon_end_time());
					if (coupon.getCoupon_type() == 0) {
						info.setStore_id(CommUtil.null2Long(Integer.valueOf(0)));
						info.setStore_name("平台自营");
					} else if (coupon.getStore() != null) {
						info.setStore_id(coupon.getStore().getId());
						info.setStore_name(coupon.getStore().getStore_name());
					} else {
						info.setStore_id(CommUtil.null2Long(Integer.valueOf(-1)));
					}
					this.couponInfoService.saveEntity(info);
					ret = true;
					if (ret) {
						list.add(coupon.getId());
					}
					coupon.setCoupon_surplus_amount(coupon
							.getCoupon_surplus_amount() - 1);
					if (coupon.getCoupon_surplus_amount() <= 0) {
						coupon.setStatus(-1);
					}
					this.couponService.updateById(coupon);
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			if ("all".equals(co_id)) {
				writer.print(JSON.toJSONString(list));
			} else {
				writer.print(ret);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/combin_carts_detail" })
	public void combin_carts_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		int code = -100;
		Map json_map = Maps.newHashMap();
		List map_list = Lists.newArrayList();
		GoodsCart cart = this.goodsCartService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((cart != null) && (cart.getCart_type() != null)
				&& (cart.getCart_type().equals("combin"))
				&& (cart.getCombin_main() == 1)) {
			String[] cart_ids = cart.getCombin_suit_ids().split(",");
			for (String cart_id : cart_ids) {
				if ((!cart_id.equals("")) && (!cart_id.equals(id))) {
					GoodsCart other = this.goodsCartService.selectByPrimaryKey(CommUtil
							.null2Long(cart_id));
					if (other != null) {
						Map temp_map = Maps.newHashMap();
						temp_map.put("id", other.getId());
						temp_map.put("name", other.getGoods().getGoods_name());
						temp_map.put("price", other.getGoods()
								.getGoods_current_price());
						temp_map.put("count", Integer.valueOf(other.getCount()));
						temp_map.put("all_price", other.getPrice());
						temp_map.put("spec_info", other.getSpec_info());
						String goods_url = CommUtil.getURL(request)
								+ "/items?id=" + other.getGoods().getId();
						if ((this.configService.getSysConfig()
								.getSecond_domain_open())
								&& (other.getGoods().getGoods_store()
										.getStore_second_domain() != "")) {
							if (other.getGoods().getGoods_type() == 1) {
								String store_second_domain = "http://"
										+ other.getGoods().getGoods_store()
												.getStore_second_domain() + "."
										+ CommUtil.generic_domain(request);
								goods_url = store_second_domain
										+ "/items?id="
										+ other.getGoods().getId();
							}
						}
						temp_map.put("url", goods_url);
						String img2 = CommUtil.getURL(request)
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getName();
						if (other.getGoods().getGoods_main_photo() != null) {
							img2 =

							CommUtil.getURL(request)
									+ "/"
									+ other.getGoods().getGoods_main_photo()
											.getPath()
									+ "/"
									+ other.getGoods().getGoods_main_photo()
											.getName()
									+ "_small."
									+ other.getGoods().getGoods_main_photo()
											.getExt();
						}
						temp_map.put("img", img2);
						map_list.add(temp_map);
					}
					code = 100;
				}
			}
		}
		json_map.put("map_list", map_list);
		json_map.put("code", Integer.valueOf(code));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isUsableCoupon(Long user_id, String coupon_id) {
		Map Level_map = this.integralViewTools.query_user_level(CommUtil
				.null2String(user_id));
		Coupon coupon = this.couponService.selectByPrimaryKey(CommUtil
				.null2Long(coupon_id));
		if ((coupon.getCoupon_limit() > CommUtil.null2Int(Level_map
				.get("level"))) || (coupon.getStatus() == -1)) {
			return false;
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("coupon_id", CommUtil.null2Long(coupon_id));
		map.put("user_id", user_id);
		
		List cis = this.couponInfoService.queryPageList(map);
		
		if (cis.size() > 0) {
			return false;
		}
		return true;
	}

	@RequestMapping({ "/goods_inventory_load" })
	public void goods_inventory_load(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id) {
		Map json_map = Maps.newHashMap();
		GoodsCart obj = this.goodsCartService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			json_map = this.cartTools.getGoodsDefaultInfo(request,
					obj.getGoods(), gsp);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/goods_spec_load" })
	public ModelAndView goods_spec_load(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_spec_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsCart cart = this.goodsCartService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((cart != null) && (cart.getGoods() != null)) {
			mv.addObject("cart", cart);
			mv.addObject("goodsViewTools", this.goodsViewTools);
		}
		return mv;
	}

	@RequestMapping({ "/goods_cart1_spec_save" })
	public void goods_cart1_spec_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id) {
		Map json_map = Maps.newHashMap();
		int code = 100;
		GoodsCart obj = this.goodsCartService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			Map goods_map = this.cartTools.getGoodsDefaultInfo(request,
					obj.getGoods(), gsp);
			int goods_inventory = CommUtil.null2Int(goods_map.get("count"));
			double price = CommUtil.null2Double(goods_map.get("price"));
			if (goods_inventory == 0) {
				code = -100;
			} else {
				boolean add = true;
				GoodsCart obj_gc = new GoodsCart();
				List<GoodsCart> carts_list = cart_calc(request);
				String[] gsp_ids1;
				for (GoodsCart gc : carts_list) {
					if (!CommUtil.null2String(gc.getId()).equals(id)) {
						String[] gsp_ids = CommUtil.null2String(gsp).split(",");
						Arrays.sort(gsp_ids);
						if ((gsp_ids != null) && (gsp_ids.length > 0)
								&& (gc.getGsps().size() > 0)) {
							gsp_ids1 = new String[gc.getGsps().size()];
							for (int i = 0; i < gc.getGsps().size(); i++) {
								gsp_ids1[i] = (gc.getGsps().get(i) != null ? ((GoodsSpecProperty) gc
										.getGsps().get(i)).getId().toString()
										: "");
							}
							Arrays.sort(gsp_ids1);
							if ((gc.getGoods().getId().toString().equals(obj
									.getGoods().getId().toString()))
									&& (Arrays.equals(gsp_ids, gsp_ids1))) {
								obj_gc = gc;
								add = false;
								break;
							}
						} else if (gc.getGoods().getId().toString().equals(id)) {
							add = false;
							break;
						}
					}
				}
				if (add) {
					String[] gsp_ids = CommUtil.null2String(gsp).split(",");
					String spec_info = "";
					obj.getGsps().removeAll(obj.getGsps());
					List<Map> goods_specs_info = obj.getGoods()
							.getGoods_specs_info() == null ? new ArrayList()
							: JSON.parseArray(obj.getGoods()
									.getGoods_specs_info(), Map.class);
					
					for (String gsp_id : gsp_ids) {
						
						GoodsSpecProperty spec_property = this.goodsSpecPropertyService
								.selectByPrimaryKey(CommUtil.null2Long(gsp_id));
						if (spec_property != null) {
							obj.getGsps().add(spec_property);
							spec_info = spec_info
									+ spec_property.getSpec().getName() + "：";
							if (goods_specs_info.size() > 0) {
								for (Map map : goods_specs_info) {
									if (CommUtil.null2Long(map.get("id"))
											.equals(spec_property.getId())) {
										spec_info = spec_info
												+ map.get("name").toString();
									}
								}
							} else {
								spec_info = spec_info
										+ spec_property.getValue();
							}
							spec_info = spec_info + "<br>";
						}
					}
					obj.setCart_gsp(gsp);
					obj.setSpec_info(spec_info);
					obj.setPrice(BigDecimal.valueOf(price));
					this.goodsCartService.updateById(obj);
				} else {
					obj_gc.setCount(obj_gc.getCount() + obj.getCount());
					if ((obj_gc.getGoods() != null)
							&& (obj_gc.getGoods().getGoods_limit() == 1)
							&& (obj_gc.getGoods().getGoods_limit_count() > 0)) {
						int count = obj_gc.getGoods().getGoods_limit_count();
						if (count < obj_gc.getCount()) {
							obj_gc.setCount(count);
						}
					}
					this.goodsCartService.updateById(obj_gc);
					this.goodsCartService.deleteById(CommUtil.null2Long(id));
				}
			}
			json_map.put("price", obj.getPrice());
			json_map.put("spec_info", obj.getSpec_info());
			GoodsCart gc = this.goodsCartService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (gc == null) {
				code = 200;
			}
		} else {
			code = -100;
		}
		json_map.put("code", Integer.valueOf(code));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/goods_cart_del" })
	public String goods_cart_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if ((id != null) && (!id.equals(""))) {
				GoodsCart gc = this.goodsCartService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				gc.getGsps().clear();
				this.goodsCartService.deleteById(CommUtil.null2Long(id));
			}
		}
		return "redirect:wap/goods_cart0";
	}
	/**
	 * 购物车列表相关调整
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param store_id
	 */
	@RequestMapping({ "/goods_count_adjust" })
	public void goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String count,
			String gcs, String gift_id) {
		Map result_map = this.goodsCartService.adjustGoodscartCount(request,
				response, gc_id, count, gcs, gift_id, null, null, null);

		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(result_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 移动端用户对选定购物车进行结算
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "确认购物车第二步", value = "/goods_cart2*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/goods_cart2" })
	public ModelAndView goods_cart2(HttpServletRequest request,
			HttpServletResponse response, String gcs, String giftids) {

		ModelAndView mv = new RedPigJModelAndView("weixin/goods_cart2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		if ((gcs == null) || (gcs.equals(""))) {

			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);

			mv.addObject("op_title", "参数错误，请重新进入购物车");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}

		List<GoodsCart> carts = this.goodsCartService.cart_list(request, null,
				null, null, true);

		boolean flag = true;

		if (carts.size() > 0) {
			for (GoodsCart gc : carts) {
				if (!gc.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					flag = false;
					break;
				}
			}
		}

		boolean goods_cod = true;
		int tax_invoice = 1;

		if (flag && carts.size() > 0) {

			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("orderBy", "default_val desc,addTime");
			params.put("orderType", "desc");
			
			List<Address> addrs = this.addressService.queryPageList(params, 0, 1);
			
			mv.addObject("addrs", addrs);
			
			if ((addrs.size() > 0) && (addrs.get(0).getArea() != null)) {
				
				this.areaViewTools.setDefaultArea(request,
						CommUtil.null2String(addrs.get(0).getArea().getId()));
			}

			String cart_session = CommUtil.randomString(32);
			request.getSession(false)
					.setAttribute("cart_session", cart_session);

			Date date = new Date();
			Map erpMap = this.cartTools.getEnoughReducePriceByCarts(carts, gcs);

			mv.addObject("cart_session", cart_session);
			mv.addObject("transportTools", this.transportTools);
			mv.addObject("goodsViewTools", this.goodsViewTools);
			mv.addObject("order_goods_price", erpMap.get("all"));
			mv.addObject("order_er_price", erpMap.get("reduce"));

			List<Map<String, Object>> map_list = Lists.newArrayList();
			List<Object> store_list = Lists.newArrayList();

			for (GoodsCart gc : carts) {
				if (gc.getGoods().getGoods_type() == 1) {
					store_list.add(gc.getGoods().getGoods_store().getId());
				} else {
					store_list.add("self");
				}
			}

			HashSet hs = new HashSet(store_list);

			store_list.removeAll(store_list);
			store_list.addAll(hs);

			String[] gc_ids = CommUtil.null2String(gcs).split(",");
			List<Goods> ac_goodses = Lists.newArrayList();

			if ((giftids != null) && (!giftids.equals(""))) {

				String[] gift_ids = giftids.split(",");
				for (String gift_id : gift_ids) {
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(gift_id));

					if (goods != null) {
						ac_goodses.add(goods);
					}
				}
			}

			boolean ret = false;
			if (ac_goodses.size() > 0) {
				ret = true;
			}

			for (Object sl : store_list) {

				if (!"self".equals(sl)) {

					List<GoodsCart> gc_list = Lists.newArrayList();
					List<GoodsCart> amount_gc_list = Lists.newArrayList();
					Map<Goods, List<GoodsCart>> gift_map = Maps.newHashMap();
					Map<Long, List<GoodsCart>> ermap = Maps.newHashMap();
					Map<Long, String> erString = Maps.newHashMap();

					for (Goods g : ac_goodses) {
						if (g.getGoods_type() == 1) {
							if (g.getGoods_store().getId().toString()
									.equals(sl.toString())) {
								gift_map.put(g, new ArrayList<GoodsCart>());
							}
						}
					}

					for (GoodsCart gc : carts) {
						for (String gc_id : gc_ids) {

							if (!"".equals(gc_id)) {
								if (CommUtil.null2Long(gc_id)
										.equals(gc.getId())
										&& (gc.getGoods().getGoods_store() != null)) {

									if (gc.getGoods().getGoods_store().getId()
											.equals(sl)) {

										if (ret
												&& gift_map.size() > 0
												&& gc.getGoods()
														.getOrder_enough_give_status() == 1) {

											if (gc.getGoods().getBuyGift_id() != null) {

												BuyGift bg = this.buyGiftService
														.selectByPrimaryKey(gc
																.getGoods()
																.getBuyGift_id());

												if (bg.getBeginTime().before(
														date)) {

													for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map
															.entrySet()) {
														if (entry
																.getKey()
																.getBuyGift_id()
																.equals(gc
																		.getGoods()
																		.getBuyGift_id())) {
															entry.getValue()
																	.add(gc);
														} else {
															gc_list.add(gc);
														}
													}
												} else {
													gc_list.add(gc);
												}
											}
										}

										if (gc.getGoods().getEnough_reduce() == 1) {
											String er_id = gc
													.getGoods()
													.getOrder_enough_reduce_id();
											EnoughReduce er = this.enoughReduceService
													.selectByPrimaryKey(CommUtil
															.null2Long(er_id));

											if (er.getErbegin_time().before(
													date)) {

												if (ermap.containsKey(er
														.getId())) {

													ermap.get(er.getId()).add(
															gc);

												} else {

													List<GoodsCart> list = Lists
															.newArrayList();

													list.add(gc);
													ermap.put(er.getId(), list);

													Map<String, Object> map = (Map) JSON
															.parseObject(
																	er.getEr_json(),
																	Map.class);

													double k1 = 0.0D;
													String str = "";

													for (Object key : map
															.keySet()) {
														if (k1 == 0.0D) {

															k1 = Double
																	.parseDouble(key
																			.toString());
															str = "活动商品购满"
																	+ k1
																	+ "元，即可享受满减";
														}
														if (Double
																.parseDouble(key
																		.toString()) < k1) {
															k1 = Double
																	.parseDouble(key
																			.toString());
															str = "活动商品购满"
																	+ k1
																	+ "元，即可享受满减";
														}
													}
													erString.put(er.getId(),
															str);
												}
											} else {
												gc_list.add(gc);
											}
										} else {
											gc_list.add(gc);
										}
										amount_gc_list.add(gc);
									}
								}
							}
						}
					}

					if ((gc_list != null && gc_list.size() > 0)
							|| (gift_map != null && gift_map.size() > 0)
							|| (ermap != null && ermap.size() > 0)) {

						Map<String, Object> map = Maps.newHashMap();
						Map ergcMap = this.cartTools
								.getEnoughReducePriceByCarts(amount_gc_list,
										gcs);
						if (gift_map.size() > 0) {
							map.put("ac_goods", gift_map);
						}
						if (ermap.size() > 0) {
							map.put("er_goods", ermap);
							map.put("erString", ergcMap.get("erString"));
						}
						map.put("store_id", sl);
						map.put("store", this.storeService.selectByPrimaryKey(CommUtil
								.null2Long(sl)));
						map.put("store_goods_price", Double
								.valueOf(this.cartTools.getPriceByCarts(
										(List) amount_gc_list, gcs)));
						map.put("store_enough_reduce",
								((Map) ergcMap).get("reduce"));
						map.put("gc_list", gc_list);
						map_list.add(map);
					}

					for (GoodsCart gc : gc_list) {
						if ((gc.getGoods().getGoods_cod() == -1)
								|| (gc.getGoods().getGoods_choice_type() == 1)) {
							goods_cod = false;
						}
						if (gc.getGoods().getTax_invoice() == 0) {
							tax_invoice = 0;
						}
					}
				} else {
					List<GoodsCart> gc_list = Lists.newArrayList();
					List<GoodsCart> amount_gc_list = Lists.newArrayList();
					Map<Goods, List<GoodsCart>> gift_map = Maps.newHashMap();
					Map<Long, List<GoodsCart>> ermap = Maps.newHashMap();
					Map<Long, String> erString = Maps.newHashMap();

					for (Goods g : ac_goodses) {
						if (g.getGoods_type() == 0) {
							gift_map.put(g, new ArrayList<GoodsCart>());
						}
					}

					for (GoodsCart gc : carts) {

						for (String gc_id : gc_ids) {

							if (!CommUtil.null2String(gc_id).equals("")) {
								if (CommUtil.null2Long(gc_id)
										.equals(gc.getId())
										&& gc.getGoods().getGoods_store() == null) {

									if (ret
											&& gift_map.size() > 0
											&& gc.getGoods()
													.getOrder_enough_give_status() == 1) {

										if (gc.getGoods().getBuyGift_id() != null) {

											BuyGift bg = this.buyGiftService
													.selectByPrimaryKey(gc.getGoods()
															.getBuyGift_id());

											if (bg.getBeginTime().before(date)) {

												for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map
														.entrySet()) {
													if (entry
															.getKey()
															.getBuyGift_id()
															.equals(gc
																	.getGoods()
																	.getBuyGift_id())) {
														entry.getValue()
																.add(gc);
													} else {
														gc_list.add(gc);
													}
												}
											} else {
												gc_list.add(gc);
											}
										}
									}
									if (gc.getGoods().getEnough_reduce() == 1) {

										String er_id = gc.getGoods()
												.getOrder_enough_reduce_id();
										EnoughReduce er = this.enoughReduceService
												.selectByPrimaryKey(CommUtil
														.null2Long(er_id));

										if (er.getErbegin_time().before(date)) {

											if (ermap.containsKey(er.getId())) {
												ermap.get(er.getId()).add(gc);
											} else {

												List<GoodsCart> list = Lists
														.newArrayList();
												list.add(gc);
												ermap.put(er.getId(),
														(List<GoodsCart>) list);
												Map<String, Object> map = (Map) JSON
														.parseObject(
																er.getEr_json(),
																Map.class);

												double k1 = 0.0D;

												String str = "";
												for (Object key : map.keySet()) {

													if (k1 == 0.0D) {
														k1 = Double
																.parseDouble(key
																		.toString());
														str = "活动商品购满" + k1
																+ "元，即可享受满减";
													}

													if (Double.parseDouble(key
															.toString()) < k1) {
														k1 = Double
																.parseDouble(key
																		.toString());
														str = "活动商品购满" + k1
																+ "元，即可享受满减";
													}
												}

												erString.put(er.getId(), str);
											}

										} else {
											gc_list.add(gc);
										}
									} else {
										gc_list.add(gc);
									}
									amount_gc_list.add(gc);
								}
							}
						}
					}
					if ((gc_list != null && gc_list.size() > 0)
							|| (gift_map != null && gift_map.size() > 0)
							|| (ermap != null) && (ermap.size() > 0)) {

						Map<String, Object> map = Maps.newHashMap();
						Map ergcMap = this.cartTools
								.getEnoughReducePriceByCarts(amount_gc_list,
										gcs);

						if (gift_map.size() > 0) {
							map.put("ac_goods", gift_map);
						}

						if (ermap.size() > 0) {

							map.put("er_goods", ermap);
							map.put("erString", ergcMap.get("erString"));
						}

						map.put("store_id", sl);
						map.put("store_goods_price", Double
								.valueOf(this.cartTools.getPriceByCarts(
										(List) amount_gc_list, gcs)));
						map.put("store_enough_reduce",
								((Map) ergcMap).get("reduce"));
						map.put("gc_list", gc_list);
						map_list.add(map);

					}
					for (GoodsCart gc : gc_list) {
						if ((gc.getGoods().getGoods_cod() == -1)
								|| (gc.getGoods().getGoods_choice_type() == 1)) {
							goods_cod = false;
						}
						if (gc.getGoods().getTax_invoice() == 0) {
							tax_invoice = 0;
						}
					}
				}
			}

			List<String> days = Lists.newArrayList();
			List<String> day_list = Lists.newArrayList();
			for (int i = 0; i < 7; i++) {

				Calendar cal = Calendar.getInstance();
				cal.add(6, i);
				days.add(CommUtil.formatTime("MM-dd", cal.getTime()) + "<br />"
						+ generic_day(cal.get(7)));
				day_list.add(CommUtil.formatTime("MM-dd", cal.getTime())
						+ generic_day(cal.get(7)));
			}

			Calendar cal = Calendar.getInstance();
			mv.addObject("before_time1", Boolean.valueOf(cal.getTime().before(
					CommUtil.formatDate(CommUtil.formatTime(
							"yyyy-MM-dd 15:00:00", new Date()),
							"yyyy-MM-dd HH:mm:ss"))));

			mv.addObject("before_time2", Boolean.valueOf(cal.getTime().before(
					CommUtil.formatDate(CommUtil.formatTime(
							"yyyy-MM-dd 19:00:00", new Date()),
							"yyyy-MM-dd HH:mm:ss"))));

			mv.addObject("before_time3", Boolean.valueOf(cal.getTime().before(
					CommUtil.formatDate(CommUtil.formatTime(
							"yyyy-MM-dd 22:00:00", new Date()),
							"yyyy-MM-dd HH:mm:ss"))));

			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());

			if (user != null) {

				Map<String, Object> vam = Maps.newHashMap();
				vam.put("user_id", user.getId());
				vam.put("status", 1);
				
				List<VatInvoice> va = this.vatinvoiceService.queryPageList(vam);
				
				if (va.size() > 0) {
					mv.addObject("va", va.get(0));
				}
			}
			mv.addObject("user", user);
			mv.addObject("days", days);
			mv.addObject("day_list", day_list);
			mv.addObject("storeViewTools", this.storeViewTools);
			mv.addObject("cartTools", this.cartTools);
			mv.addObject("transportTools", this.transportTools);
			mv.addObject("userTools", this.userTools);
			mv.addObject("map_list", map_list);
			mv.addObject("gcs", gcs);
			mv.addObject("goods_cod", Boolean.valueOf(goods_cod));
			mv.addObject("tax_invoice", Integer.valueOf(tax_invoice));
			mv.addObject("giftids", giftids);
			mv.addObject("goodsTools", this.goodsTools);

		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 选择订单地址
	 * @param request
	 * @param response
	 * @param gcs
	 * @param addr_id
	 * @param type
	 * @param cloudPurchaseLottery_id
	 * @param giftids
	 * @return
	 */
	@SecurityMapping(title = "选择订单地址", value = "/choose_address*", rtype = "buyer", rname = "移动端购物订单地址", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping({ "/choose_address" })
	public ModelAndView choose_address(HttpServletRequest request,
			HttpServletResponse response, String gcs, String addr_id,
			String type, String cloudPurchaseLottery_id, String giftids) {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_cart2_address.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		List<Address> addrs = this.addressService.queryPageList(params);
		
		mv.addObject("addrs", addrs);
		mv.addObject("gcs", gcs);
		mv.addObject("areaViewTools", this.areaViewTools);
		String cart_session = (String) request.getSession(false).getAttribute(
				"cart_session");
		mv.addObject("cart_session", cart_session);
		mv.addObject("addr_id", addr_id);
		mv.addObject("type", type);
		mv.addObject("giftids", giftids);
		mv.addObject("cloudPurchaseLottery_id", cloudPurchaseLottery_id);
		return mv;
	}
	
	/**
	 * 设为默认地址
	 * @param request
	 * @param response
	 * @param type
	 * @param gcs
	 * @param gifts
	 * @param day_value
	 * @param addr_id
	 * @param cart_session
	 * @param cloudPurchaseLottery_id
	 * @return
	 */
	@SecurityMapping(title = "设为默认地址", value = "/cart_address_default*", rtype = "buyer", rname = "移动端购物订单地址", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping({ "/cart_address_default" })
	public String cart_address_default(HttpServletRequest request,
			HttpServletResponse response, String type, String gcs,
			String gifts, String day_value, String addr_id,
			String cart_session, String cloudPurchaseLottery_id) {
		String[] ids = addr_id.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Address address = this.addressService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				if (address.getUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("user_id", SecurityUserHolder.getCurrentUser()
							.getId());
					params.put("id_no", CommUtil.null2Long(id));
					params.put("default_val", Integer.valueOf(1));
					
					List<Address> addrs = this.addressService.queryPageList(params);
					
					for (Address addr1 : addrs) {
						addr1.setDefault_val(0);
						this.addressService.updateById(addr1);
					}
					address.setDefault_val(1);
					this.addressService.updateById(address);
					if (address.getArea() != null) {
						this.areaViewTools
								.setDefaultArea(request, CommUtil
										.null2String(address.getArea().getId()));
					}
				}
			}
		}
		if ((type != null) && (type.indexOf("cloud_order_list") > -1)) {
			return "redirect:" + type + "&cloudPurchaseLottery_id="
					+ cloudPurchaseLottery_id;
		}
		return

		"redirect:" + type + "?gcs=" + gcs + "&giftids=" + gifts
				+ "&day_value=" + day_value + "&addr_id=" + addr_id
				+ "&cart_session=" + cart_session;
	}
	
	/**
	 * 提交订单编辑地址
	 * @param request
	 * @param response
	 * @param addr_id
	 * @return
	 */
	@SecurityMapping(title = "提交订单编辑地址", value = "/goods_cart2_address_edit*", rtype = "buyer", rname = "移动端购物订单地址", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping({ "/goods_cart2_address_edit" })
	public ModelAndView goods_cart2_address_edit(HttpServletRequest request,
			HttpServletResponse response, String addr_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"weixin/goods_cart2_address_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		
		mv.addObject("areas", areas);
		Address obj = this.addressService.selectByPrimaryKey(CommUtil
				.null2Long(addr_id));
		mv.addObject("obj", obj);
		mv.addObject("areaViewTools", this.areaViewTools);
		return mv;
	}
	
	/**
	 * 提交订单地址保存
	 * @param request
	 * @param response
	 * @param id
	 * @param area_id
	 * @param type
	 * @param gcs
	 * @param gifts
	 * @param day_value
	 * @param cart_session
	 * @return
	 */
	@SecurityMapping(title = "提交订单地址保存", value = "/goods_cart2_address_save*", rtype = "buyer", rname = "移动端购物订单地址", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping({ "/goods_cart2_address_save" })
	public String goods_cart2_address_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String type, String gcs, String gifts, String day_value,
			String cart_session) {
		WebForm wf = new WebForm();
		Address address = null;
		if ((id == null) || (id.equals(""))) {
			address = (Address) WebForm.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			address = (Address) WebForm.toPo(request, obj);
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		address.setArea(area);
		boolean ret = true;
		if ((id == null) || (id.equals(""))) {
			this.addressService.saveEntity(address);
		} else {
			this.addressService.updateById(address);
		}
		if (ret) {
			if (address.getDefault_val() == 1) {
				if (address.getArea() != null) {
					this.areaViewTools.setDefaultArea(request,
							CommUtil.null2String(address.getArea().getId()));
				}
				return

				"redirect:" + type + "?gcs=" + gcs + "&gifts=" + gifts
						+ "&day_value=" + day_value + "&addr_id=" + id
						+ "&cart_session=" + cart_session;
			}
			return "redirect:/choose_address?addr_id=" + id + "&type="
					+ type + "&gcs=" + gcs + "&giftids=" + gifts;
		}
		return "redirect:/404";
	}
	
	/**
	 * 提交订单地址删除
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "提交订单地址删除", value = "/goods_cart2_address_del*", rtype = "buyer", rname = "移动端购物订单地址", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping({ "/goods_cart2_address_del" })
	public void goods_cart2_address_del(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Address address = this.addressService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		boolean ret = true;
		if (address != null) {
			if (address.getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				this.addressService.deleteById(Long.valueOf(Long
						.parseLong(id)));
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
	 * 添加购物车成功页
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/goods_cart0" })
	public ModelAndView goods_cart0(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_cart0.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("id", id);
		return mv;
	}
	
	/**
	 * 确认购物车第3步
	 * @param request
	 * @param response
	 * @param cart_session
	 * @param store_id
	 * @param addr_id
	 * @param gcs
	 * @param delivery_time
	 * @param delivery_type
	 * @param delivery_id
	 * @param payType
	 * @param gifts
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "确认购物车第3步", value = "/goods_cart3*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/goods_cart3" })
	public ModelAndView goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String gcs, String delivery_time,
			String delivery_type, String delivery_id, String payType,
			String gifts) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("weixin/goods_cart3.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String cart_session1 = (String) request.getSession(false).getAttribute(
				"cart_session");
		if ((!CommUtil.null2String(cart_session1).equals(cart_session))
				|| (CommUtil.null2String(store_id).equals(""))
				|| (CommUtil.null2String(gcs).equals(""))) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经失效");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		List<GoodsCart> order_carts = Lists.newArrayList();
		String[] gc_ids = gcs.split(",");
		for (String gc_id : gc_ids) {
			if (!gc_id.equals("")) {
				GoodsCart car = this.goodsCartService.selectByPrimaryKey(CommUtil
						.null2Long(gc_id));
				order_carts.add(car);
			}
		}
		if (order_carts.size() == 0) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		for (GoodsCart gc : order_carts) {
			if ((gc != null) && (gc.getGoods().getGoods_cod() == -1)
					&& (!payType.equals("online"))) {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您恶意篡改支付方式，订单已经失效");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
			if (!gc.getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您恶意篡购物车信息，订单已经失效");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
			if ("limit".equals(gc.getCart_type())) {
				Map limit_map = this.cartTools.handle_limit_cart(gc.getGoods(),
						null);
				int limit_code = CommUtil.null2Int(limit_map.get("limit_code"));
				if (limit_code != 0) {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单中商品已经超出限购数量，请重新下单");
					mv.addObject("url", CommUtil.getURL(request) + "/index");
					return mv;
				}
			}
			// 判断秒杀是否限购
			if (gc.getGoods().getNuke_buy()==2&&gc.getGoods().getNuke()!=null) {
				/*Map limit_map = this.cartTools.handle_limit_cart(gc.getGoods(),
						null);
				int limit_code = CommUtil.null2Int(limit_map.get("limit_code"));
				if (limit_code != 0) {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单中商品已经超出限购数量，请重新下单");
					mv.addObject("url", CommUtil.getURL(request) + "/index");
					return mv;
				}*/
			}
			int goods_inventory = CommUtil.null2Int(this.cartTools
					.getGoodsDefaultInfo(request, gc.getGoods(),
							gc.getCart_gsp()).get("count"));
			if ((goods_inventory == 0) || (goods_inventory < gc.getCount())) {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品库存不足，请重新下单");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
		}
		boolean verify_goods = this.cartTools
				.verify_goods_available(order_carts);
		if (!verify_goods) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "部分商品已失效，请删除失效商品");
			mv.addObject("url", CommUtil.getURL(request) + "/goods_cart1");
			return mv;
		}
		Address addr = this.addressService.selectByPrimaryKey(CommUtil
				.null2Long(addr_id));
		if (addr == null) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单收货地址信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		this.areaViewTools.setDefaultArea(request,
				CommUtil.null2String(addr.getArea().getId()));
		User buyer = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(SecurityUserHolder.getCurrentUser().getId()));
		if (payType.equals("payafter")) {
			mv = new RedPigJModelAndView("payafter_pay.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			String pay_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("pay_session", pay_session);
			mv.addObject("paymentTools", this.paymentTools);
			mv.addObject("pay_session", pay_session);
		}
		if ((gifts != null) && (!gifts.equals(""))) {
			String[] gift_ids = gifts.split(",");
			for (String gift_id : gift_ids) {
				if ((gift_id != null) && (!gift_id.equals(""))) {
					String url = CommUtil.getURL(request);
					if (!"".equals(CommUtil.null2String(this.configService
							.getSysConfig().getImageWebServer()))) {
						url = this.configService.getSysConfig()
								.getImageWebServer();
					}
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(gift_id));
					Map<String, Object> map = Maps.newHashMap();
					map.put("goods_id", goods.getId().toString());
					map.put("goods_name", goods.getGoods_name());
					String goods_main_photo = url
							+ "/"
							+ this.configService.getSysConfig().getGoodsImage()
									.getPath()
							+ "/"
							+ this.configService.getSysConfig().getGoodsImage()
									.getName();
					if (goods.getGoods_main_photo() != null) {
						goods_main_photo =

						url + "/" + goods.getGoods_main_photo().getPath() + "/"
								+ goods.getGoods_main_photo().getName()
								+ "_small."
								+ goods.getGoods_main_photo().getExt();
					}
					map.put("goods_main_photo", goods_main_photo);
					String json = JSON.toJSONString(map);
					GoodsCart gift_cart = null;
					for (GoodsCart gc : order_carts) {
						if (gc.getGoods().getOrder_enough_give_status() == 1) {
							BuyGift bg = this.buyGiftService.selectByPrimaryKey(gc
									.getGoods().getBuyGift_id());
							List<Map> temp_gifts = JSON.parseArray(
									bg.getGift_info(), Map.class);
							for (Map temp : temp_gifts) {
								if (CommUtil.null2String(temp.get("goods_id"))
										.equals(gift_id)) {
									gift_cart = gc;
									break;
								}
							}
						}
					}
					gift_cart.setWhether_choose_gift(1);
					gift_cart.setGift_info(json);
					this.goodsCartService.updateById(gift_cart);
				}
			}
		}
		request.getSession(false).removeAttribute("cart_session");

		OrderForm main_order = this.HandleOrderFormService.submitOrderForm(
				request, store_id, gcs, delivery_time, delivery_type,
				delivery_id, payType, order_carts, buyer, addr, "wap");

		mv.addObject("all_of_price", CommUtil.formatMoney(Double
				.valueOf(this.orderFormTools.query_order_pay_price(CommUtil
						.null2String(main_order.getId())))));
		mv.addObject("paymentTools", this.paymentTools);
		mv.addObject("user", buyer);
		mv.addObject("from", "goodsCart");
		mv.addObject("order", main_order);

		List<Payment> payments = Lists.newArrayList();
		Map<String,Object> params = Maps.newHashMap();
		params.put("mark", "wx_pay");
		
		payments = this.paymentService.queryPageList(params);
		
		Payment payment = null;
		if (payments.size() > 0) {
			payment = (Payment) payments.get(0);
			mv.addObject("appid", payment.getWx_appid());
		}
		return mv;
	}
	
	/**
	 * 订单加载自提点
	 * @param request
	 * @param response
	 * @param addr_id
	 * @param currentPage
	 * @param deliver_area_id
	 * @return
	 */
	@SecurityMapping(title = "订单加载自提点", value = "/cart_delivery*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/cart_delivery" })
	public ModelAndView cart_delivery(HttpServletRequest request,
			HttpServletResponse response, String addr_id, String currentPage,
			String deliver_area_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/cart_delivery.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map paras = Maps.newHashMap();
		
		if ((deliver_area_id != null) && (!deliver_area_id.equals(""))) {
			Area deliver_area = this.areaService.selectByPrimaryKey(CommUtil
					.null2Long(deliver_area_id));
			Set<Long> ids = genericIds(deliver_area);
			paras.put("da_area_ids", ids);
			
			mv.addObject("deliver_area_id", deliver_area_id);
		} else if ((addr_id != null) && (!addr_id.equals(""))) {
			Address addr = this.addressService.selectByPrimaryKey(CommUtil
					.null2Long(addr_id));
			paras.put("da_area_id", addr.getArea().getParent().getId());
			
			mv.addObject("area", addr.getArea().getParent());
		}
		paras.put("da_status", Integer.valueOf(10));
		
		List<DeliveryAddress> objs = this.deliveryaddrService.queryPageList(paras);
		
		mv.addObject("objs", objs);
		return mv;
	}
	
	private String generic_day(int day) {
		String[] list = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return list[(day - 1)];
	}
	
	/**
	 * 订单支付详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "订单支付详情", value = "/order_pay_view*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay_view" })
	public ModelAndView order_pay_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/order_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());

		Map<Boolean, ModelAndView> map_verify = this.orderFormTools
				.orderForm_verify(request, response, mv, order, user);
		Iterator iterator = map_verify.keySet().iterator();
		while (iterator.hasNext()) {
			Boolean verify_result = (Boolean) iterator.next();
			if (!verify_result.booleanValue()) {
				mv = (ModelAndView) map_verify.get(verify_result);
				return mv;
			}
		}
		int order_status = this.orderFormTools.query_order_status(id);
		if ((order_status == 10) || (order_status == 11)) {
			List<Payment> payments = Lists.newArrayList();
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", "wx_pay");
			
			payments = this.paymentService.queryPageList(params);
			
			Payment payment = null;
			if (payments.size() > 0) {
				payment = (Payment) payments.get(0);
				mv.addObject("appid", payment.getWx_appid());
			}
			if (order.getOrder_cat() == 1) {
				mv = new RedPigJModelAndView("weixin/recharge_order.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				String ofcard_userid = this.configService.getSysConfig()
						.getOfcard_userid();
				String ofcard_userpws = Md5Encrypt.md5(this.configService
						.getSysConfig().getOfcard_userpws());
				String rc_amount = CommUtil.null2String(Integer.valueOf(order
						.getRc_amount()));
				String mobile = order.getRc_mobile();
				String query_url = "http://api2.ofpay.com/telquery.do?userid="
						+ ofcard_userid + "&userpws=" + ofcard_userpws
						+ "&phoneno=" + mobile + "&pervalue=" + rc_amount
						+ "&version=6.0";
				String return_xml = getHttpContent(query_url, "gb2312", "POST");
				Map<String, Object> map = XMLUtil.parseXML(return_xml, true);
				double inprice = CommUtil.null2Double(map.get("inprice"));
				if (CommUtil.null2Double(map.get("inprice")) <= CommUtil
						.null2Double(rc_amount)) {
					inprice = CommUtil.add(map.get("inprice"),
							this.configService.getSysConfig()
									.getOfcard_mobile_profit());
					if (inprice > CommUtil.null2Double(rc_amount)) {
						inprice = CommUtil.null2Double(rc_amount);
					}
				}
				map.put("inprice", Double.valueOf(inprice));
				String recharge_pay_session = CommUtil.randomString(64);
				request.getSession(false).setAttribute("recharge_pay_session",
						recharge_pay_session);
				mv.addObject("recharge_pay_session", recharge_pay_session);
				mv.addObject("map", map);
				mv.addObject("rc_amount", rc_amount);
				mv.addObject("mobile", mobile);
				mv.addObject("order", order);
				mv.addObject("user", user);
				mv.addObject("from", "order");
				return mv;
			}
			if (order.getOrder_cat() != 1) {
				mv.addObject("order", order);
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("orderFormTools", this.orderFormTools);
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("user", user);
				mv.addObject("from", "order");
				return mv;
			}
			if ((order.getOrder_special().equals("advance"))
					&& (order_status == 11)) {
				mv.addObject("order", order);
				mv.addObject("paymentTools", this.paymentTools);
				mv.addObject("orderFormTools", this.orderFormTools);
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("user", user);
				mv.addObject("from", "order");
				return mv;
			}
		}
		return mv;
	}
	
	/**
	 * 移动端订单支付详情
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @param order_type
	 * @param pay_password
	 * @return
	 */
	@SecurityMapping(title = "移动端订单支付详情", value = "/order_pay*", rtype = "buyer", rname = "移动端订单支付详情", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping({ "/order_pay" })
	public ModelAndView order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String order_type, String pay_password) {
		ModelAndView mv = null;
		if ("cloudpurchase".equals(order_type)) {
			CloudPurchaseOrder order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(order_id));
			if (order != null) {
				if ((order.getUser_id().equals(SecurityUserHolder
						.getCurrentUser().getId())) && (order.getStatus() == 0)) {
					if ("wx_pay".equals(payType)) {
						try {
							response.sendRedirect("/weixin/pay/wx_pay?id="
									+ order_id + "&showwxpaytitle=1&type="
									+ order_type);
						} catch (IOException e) {
							e.printStackTrace();
							mv = new RedPigJModelAndView("weixin/error.html",
									this.configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "支付方式错误！");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/index");
						}
					}
					if (payType.equals("balance")) {
						mv = new RedPigJModelAndView("weixin/balance_pay.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						double order_total_price = CommUtil.null2Double(Integer
								.valueOf(order.getPrice()));
						mv.addObject("order_total_price",
								Double.valueOf(order_total_price));
						mv.addObject("type", order_type);
						mv.addObject("order_id", order_id);
						return mv;
					}
					mv = new RedPigJModelAndView("weixin/line_pay.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("type", "cloudpurchase");
					mv.addObject("order_id", order.getId());
					mv.addObject("url", CommUtil.getURL(request));
					List<Payment> payments = Lists.newArrayList();
					Map<String, Object> params = Maps.newHashMap();
					params.put("mark", payType);
					payments = this.paymentService.queryPageList(params);
					
					if (payments.size() > 0) {
						Payment payment = (Payment) payments.get(0);
						mv.addObject("payment_id", payment.getId());
					}
					return mv;
				}
			}
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单中商品已被删除，请重新下单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/cloudpurchase_index");
			return mv;
		}
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());

		Map<Boolean, ModelAndView> map_verify = this.orderFormTools
				.orderForm_verify(request, response, mv, order, user);
		Iterator iterator = map_verify.keySet().iterator();
		while (iterator.hasNext()) {
			Boolean verify_result = (Boolean) iterator.next();
			if (!verify_result.booleanValue()) {
				mv = (ModelAndView) map_verify.get(verify_result);
				return mv;
			}
		}
		int order_status = this.orderFormTools.query_order_status(order_id);
		if (order_status == 10) {
			if ((payType == null) || ("".equals(CommUtil.null2String(payType)))) {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "支付方式错误");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
			List<Payment> payments = Lists.newArrayList();
			Map<String, Object> params = Maps.newHashMap();
			params.put("mark", payType);
			
			payments = this.paymentService.queryPageList(params);
			
			if (payments.size() > 0) {
				Payment payment = (Payment) payments.get(0);
				order.setPayment_id(payment.getId());
				order.setPayment_mark(payment.getMark());
				order.setPayment_name(payment.getName());
			}
			order.setPayType("online");
			this.orderFormService.updateById(order);
			if (payType.equals("balance")) {
				mv = new RedPigJModelAndView("weixin/balance_pay.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				double order_total_price = this.orderFormTools
						.query_order_pay_price(order_id);
				mv.addObject("order_total_price",
						Double.valueOf(order_total_price));
				mv.addObject("user", user);
			} else {
				mv = new RedPigJModelAndView("weixin/line_pay.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("payType", payType);
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", this.payTools);
				String type = "goods";
				if (order.getOrder_cat() == 2) {
					type = "group";
				}
				mv.addObject("type", type);
				mv.addObject("payment_id", order.getPayment_id());
				if ("wx_pay".equals(payType)) {
					try {
						response.sendRedirect("/weixin/pay/wx_pay?id="
								+ order_id + "&showwxpaytitle=1&type=" + type);
					} catch (IOException e) {
						e.printStackTrace();
						mv = new RedPigJModelAndView("weixin/error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "支付方式错误！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/index");
					}
				}
			}
			mv.addObject("order", order);
			mv.addObject("order_id", order.getId());
			mv.addObject("user", user);
		}
		if (order_status == 11) {
			if (payType.equals("balance")) {
				mv = new RedPigJModelAndView("weixin/balance_pay.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				double order_total_price = this.orderFormTools
						.query_order_pay_price(order_id);
				mv.addObject("order_total_price",
						Double.valueOf(order_total_price));
				mv.addObject("user", user);
			} else {
				mv = new RedPigJModelAndView("weixin/line_pay.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("payType", payType);
				mv.addObject("url", CommUtil.getURL(request));
				mv.addObject("payTools", this.payTools);
				String type = "goods";
				mv.addObject("type", type);
				mv.addObject("payment_id", order.getPayment_id());
				if ("wx_pay".equals(payType)) {
					try {
						response.sendRedirect("/weixin/pay/wx_pay?id="
								+ order_id + "&showwxpaytitle=1&type=" + type);
					} catch (IOException e) {
						e.printStackTrace();
						mv = new RedPigJModelAndView("weixin/error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "支付方式错误！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/index");
					}
				}
			}
			mv.addObject("order", order);
			mv.addObject("order_id", order.getId());
			mv.addObject("user", user);
		}
		return mv;
	}
	
	/**
	 * 验证密码
	 * @param request
	 * @param response
	 * @param pw
	 */
	@RequestMapping({ "/va_pw" })
	public void showclass_brand(HttpServletRequest request,
			HttpServletResponse response, String pw) {
		String temp = "";
		User user = SecurityUserHolder.getCurrentUser();
		user = this.userService.selectByPrimaryKey(user.getId());
		String password = user.getPay_password();
		String p = Md5Encrypt.md5(pw);
		if ((!"".equals(p)) && (p.equals(password))) {
			temp = "密码输入成功";
		} else {
			temp = "请重新输入密码";
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改密码
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/account_pay_password" })
	public ModelAndView account_pay_password(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/set_password.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	
	/**
	 * 手机号
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/account_mobile" })
	public ModelAndView account_mobile(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/binding_password.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String ran = CommUtil.randomInt(8)
				+ SecurityUserHolder.getCurrentUser().getId();
		request.getSession(false).setAttribute("SessionCode", ran);
		mv.addObject("SessionCode", ran);
		return mv;
	}
	
	/**
	 * 卖家手机微信信息
	 * @param request
	 * @param response
	 * @param type
	 * @param mobile
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "wap/buyer/account_mobile_sms_weixin" })
	public void account_mobile_sms_weixin(HttpServletRequest request,
			HttpServletResponse response, String type, String mobile)
			throws UnsupportedEncodingException {
		String ret = "100";
		if (type.equals("mobile_vetify_code")) {
			String code = CommUtil.randomInt(4);
			String content = "";
			content = "尊敬的" + SecurityUserHolder.getCurrentUser().getUserName()
					+ "您好，您在试图修改"
					+ this.configService.getSysConfig().getWebsiteName()
					+ "的支付密码，" + "手机验证码为：" + code + "。["
					+ this.configService.getSysConfig().getTitle() + "]";
			
			if (this.configService.getSysConfig().getSmsEnbale()) {
				boolean ret_op = this.msgTools.sendSMS(mobile, content);
				if (ret_op) {
					VerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  mobile);
					if (mvc == null) {
						mvc = new VerifyCode();
					}
					mvc.setAddTime(new Date());
					mvc.setCode(code);
					mvc.setMobile(mobile);
					this.mobileverifycodeService.updateById(mvc);
				} else {
					ret = "200";
				}
			} else {
				ret = "300";
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
	}
	
	/**
	 * 手机短信
	 * @param request
	 * @param response
	 * @param mobile
	 */
	@RequestMapping({ "/account_mobile_sms" })
	public void account_mobile_sms(HttpServletRequest request,
			HttpServletResponse response, String mobile) {
		String mobilePhone = null;
		List<User> list = this.userService.queryPageList(RedPigMaps.newMap());
		
		String temp = "";
		for (User u : list) {
			mobilePhone = u.getMobile();
			if ((!"".equals(mobile)) && (mobile.equals(mobilePhone))) {
				temp = "true";
				break;
			}
		}
		if (temp == "") {
			temp = "false";
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 手机验证码
	 * @param request
	 * @param response
	 * @param mobile_code
	 * @param mobile
	 */
	@RequestMapping({ "/verification_phone_code" })
	public void verification_phone_code(HttpServletRequest request,
			HttpServletResponse response, String mobile_code, String mobile) {
		String temp = "";
		Map<String, Object> params = Maps.newHashMap();
		params.put("mobile", mobile);
		List<VerifyCode> codes = this.verifyCodeService.queryPageList(params);
		
		if ((!mobile.equals("")) && (mobile != null)) {
			if (codes.size() > 0) {
				if ((((VerifyCode) codes.get(0)).getCode().equals(mobile_code))
						&& (!mobile_code.equals(""))) {
					User user = SecurityUserHolder.getCurrentUser();
					user = this.userService.selectByPrimaryKey(user.getId());
					user.setMobile(mobile);
					this.userService.updateById(user);
					temp = "true";
				} else {
					temp = "false";
				}
			} else {
				temp = "false";
			}
		} else {
			temp = "false";
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 绑定成功
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/success_binding" })
	public ModelAndView success_binding(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/success_binding.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("url", CommUtil.getURL(request) + "/register_page");
		return mv;
	}
	
	/**
	 * 注册页
	 */
	@RequestMapping({ "/register_page" })
	public ModelAndView register_page(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/register_page.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		user = this.userService.selectByPrimaryKey(user.getId());
		String mobile = user.getMobile();

		mv.addObject("star_mobile", CommUtil.generic_star(mobile, 3, 7));
		mv.addObject("mobile", mobile);
		return mv;
	}
	
	/**
	 * 设置密码
	 * @param request
	 * @param response
	 * @param mobile
	 * @param code
	 * @param password
	 */
	@RequestMapping({ "/set_password" })
	public void set_password(HttpServletRequest request,
			HttpServletResponse response, String mobile, String code,
			String password) {
		String temp = "";
		Map<String, Object> params = Maps.newHashMap();
		params.put("mobile", mobile);
		List<VerifyCode> codes = this.verifyCodeService.queryPageList(params);
		
		if ((codes != null) && (!codes.equals(""))) {
			if (codes.size() > 0) {
				if ((((VerifyCode) codes.get(0)).getCode().equals(code))
						&& (!code.equals(""))) {
					User user = SecurityUserHolder.getCurrentUser();
					user = this.userService.selectByPrimaryKey(user.getId());
					user.setPay_password(Md5Encrypt.md5(password));
					this.userService.updateById(user);
					temp = "true";
				} else {
					temp = "false";
				}
			} else {
				temp = "false";
			}
		} else {
			temp = "false";
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 设置密码
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/set_password_success" })
	public ModelAndView set_password_success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/set_password_success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("url", CommUtil.getURL(request) + "/index");
		return mv;
	}
	
	/**
	 * 绑定密码
	 * @param request
	 * @param response
	 */
	@RequestMapping({ "/binding_password" })
	public void binding_password(HttpServletRequest request,
			HttpServletResponse response) {
		String temp = "";
		User user = SecurityUserHolder.getCurrentUser();
		user = this.userService.selectByPrimaryKey(user.getId());
		String mobile = user.getMobile();
		if ((mobile == null) || (mobile.equals(""))) {
			temp = "true";
		} else {
			temp = "false";
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(temp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 移动端订单预付款支付
	 * @param request
	 * @param response
	 * @param order_id
	 * @param pay_msg
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "移动端订单预付款支付", value = "/order_pay_balance*", rtype = "buyer", rname = "移动端预存款支付", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping({ "/order_pay_balance" })
	public ModelAndView order_pay_balance(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_msg,
			String type) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (type.equals("cloudpurchase")) {
			CloudPurchaseOrder order = this.cloudPurchaseOrderService
					.selectByPrimaryKey(CommUtil.null2Long(CommUtil.null2Long(order_id)));
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "预存款余额不足支付失败");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/cloudpurchase_index");
			if ((order != null)
					&& (order.getStatus() == 0)
					&& (user.getAvailableBalance().compareTo(
							BigDecimal.valueOf(CommUtil.null2Double(Integer
									.valueOf(order.getPrice())))) != -1)) {
				order.setStatus(5);
				order.setPayment("balance");
				order.setPayTime(new Date());
				this.cloudPurchaseOrderService.updateById(order);
				boolean ret = true;
				if (ret) {
					this.cloudPurchaseOrderService.reduce_inventory(order,
							request);
					user.setAvailableBalance(user.getAvailableBalance()
							.subtract(
									BigDecimal.valueOf(CommUtil
											.null2Double(Integer.valueOf(order
													.getPrice())))));
					this.userService.updateById(user);
				}
				mv = new RedPigJModelAndView("weixin/cloud_order_finish.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", order);
			}
			return mv;
		}
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));

		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Map<Boolean, ModelAndView> map_verify = this.orderFormTools
				.orderForm_verify(request, response, mv, order, user);
		Iterator iterator = map_verify.keySet().iterator();
		while (iterator.hasNext()) {
			Boolean verify_result = (Boolean) iterator.next();
			if (!verify_result.booleanValue()) {
				mv = (ModelAndView) map_verify.get(verify_result);
				return mv;
			}
		}
		double order_total_price = this.orderFormTools
				.query_order_pay_price(order_id);
		if (CommUtil.null2Double(user.getAvailableBalance()) >= order_total_price) {
			boolean ret = this.HandleOrderFormService.payByBalance(order,
					CommUtil.getURL(request), pay_msg);

			mv.addObject("op_title", "预付款支付成功");
			if (order.getOrder_cat() == 2) {
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/group_list");
			} else {
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order_list");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "可用余额不足，支付失败");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_list");
		}
		return mv;
	}
	
	/**
	 * 手机订单货到付款
	 * @param request
	 * @param response
	 * @param order_id
	 * @param pay_msg
	 * @param pay_session
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "手机订单货到付款", value = "/order_pay_payafter*", rtype = "buyer", rname = "移动端货到付款支付", rcode = "wap_goods_cart", rgroup = "移动端购物")
	@RequestMapping({ "/order_pay_payafter" })
	public ModelAndView order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_msg,
			String pay_session) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
			OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil
					.null2Long(order_id));
			boolean exist = this.orderFormTools.verify_goods_exist(order);
			if (!exist) {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品已被删除，请重新下单");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order_list");
				return mv;
			}
			boolean inventory_very = this.orderFormTools
					.verify_goods_Inventory(order);
			if (!inventory_very) {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "订单中商品库存不足，请重新下单");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order_list");
				return mv;
			}
			boolean ret = this.HandleOrderFormService.payByPayafter(order,
					CommUtil.getURL(request), pay_msg);
			if (ret) {
				this.orderFormTools.updateGoodsInventory(order);
			}
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待发货");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_list");
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_list");
		}
		return mv;
	}

	private List<GoodsCart> cart_calc(HttpServletRequest request) {
		List<GoodsCart> carts_list = this.goodsCartService.cart_list(request,null, null, null, false);
		return carts_list;
	}

	private Map<String, List<GoodsCart>> separateCombin(List<GoodsCart> carts) {
		Map<String, List<GoodsCart>> map = Maps.newHashMap();
		List<GoodsCart> normal_carts = Lists.newArrayList();
		List<GoodsCart> combin_carts = Lists.newArrayList();
		for (GoodsCart cart : carts) {
			if ((cart.getCart_type() != null)
					&& (cart.getCart_type().equals("combin"))) {
				if (cart.getCombin_main() == 1) {
					combin_carts.add(cart);
				}
			} else {
				normal_carts.add(cart);
			}
		}
		map.put("combin", combin_carts);
		map.put("normal", normal_carts);
		return map;
	}

	private Set<Long> genericIds(Area area) {
		Set<Long> ids = new HashSet();
		ids.add(area.getId());
		for (Area child : area.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}

	private StoreHouse getStoreHouse(String addr_id) {
		StoreHouse sh = new StoreHouse();
		Address addr = this.addressService.selectByPrimaryKey(CommUtil
				.null2Long(addr_id));
		Map<String, Object> params = Maps.newHashMap();
		params.put("sh_area_like", "\"id\":" + addr.getArea().getParent().getId());
		
		List<StoreHouse> storeHouses = this.storeHouseService.queryPageList(params, 0, 1);
		
		sh = (StoreHouse) storeHouses.get(0);
		return sh;
	}
	
	/**
	 * wap端F码购物第一步
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param gsp
	 * @return
	 */
	@RequestMapping({ "/f_code_cart" })
	@SecurityMapping(title = "wap端F码购物第一步", value = "/f_code_cart*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	public ModelAndView f_code_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String gsp) {
		ModelAndView mv = new RedPigJModelAndView("weixin/f_code_cart.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		if (goods != null) {
			if (goods.getF_sale_type() == 0) {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该商品不需要F码购买");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			} else {
				if (CommUtil.null2String(gsp).equals("")) {
					gsp = this.cartTools.getGoodsDefaultGsp(goods);
				}
				int goods_inventory = CommUtil.null2Int(this.cartTools.getGoodsDefaultInfo(request,goods, gsp).get("count"));
				if (goods_inventory > 0) {
					String[] gsp_ids = CommUtil.null2String(gsp).split(",");
					String spec_info = "";
					List<GoodsSpecProperty> specs = Lists.newArrayList();
					for (String gsp_id : gsp_ids) {
						GoodsSpecProperty spec_property = this.goodsSpecPropertyService
								.selectByPrimaryKey(CommUtil.null2Long(gsp_id));

						boolean add = false;

						for (GoodsSpecProperty temp_gsp : goods
								.getGoods_specs()) {
							if (temp_gsp.getId().equals(spec_property.getId())) {
								add = true;
							}
						}
						for (GoodsSpecProperty temp_gsp : specs) {
							if (temp_gsp.getSpec().getId()
									.equals(spec_property.getSpec().getId())) {
								add = false;
							}
						}
						if (add) {
							specs.add(spec_property);
						}
					}
					if (this.goodsViewTools.generic_spec(goods_id).size() == specs
							.size()) {
						for (GoodsSpecProperty spec : specs) {
							spec_info = spec.getSpec().getName() + ":"
									+ spec.getValue() + " " + spec_info;
						}
						String price = this.cartTools.getGoodsPriceByGsp(gsp,
								goods_id);
						mv.addObject("spec_info", spec_info);
						mv.addObject("price", price);
						mv.addObject("obj", goods);
						mv.addObject("gsp", gsp);
						mv.addObject("goodsViewTools", this.goodsViewTools);
					} else {
						mv = new RedPigJModelAndView("weixin/error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "规格参数错误");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/index");
					}
				} else {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "商品默认规格无库存，请选择其他规格购买");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/items?id=" + goods.getId());
				}
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * wap端F码验证
	 * @param request
	 * @param response
	 * @param f_code
	 * @param goods_id
	 */
	@SecurityMapping(title = "wap端F码验证", value = "/f_code_validate*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/f_code_validate" })
	public void f_code_validate(HttpServletRequest request,
			HttpServletResponse response, String f_code, String goods_id) {
		int code = -100;
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		List<Map> list = JSON.parseArray(obj.getGoods_f_code(), Map.class);
		for (Map map : list) {
			if (CommUtil.null2String(map.get("code")).equals(f_code)) {
				if (CommUtil.null2Int(map.get("status")) == 0) {
					code = 100;
					break;
				}
				code = 65336;

				break;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * wap端F码完成验证进入订单提交
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param f_code
	 * @param gsp
	 */
	@SecurityMapping(title = "wap端F码完成验证进入订单提交", value = "/f_code_validate*", rtype = "buyer", rname = "购物流程3", rcode = "wap_goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/add_f_code_goods_cart" })
	public void add_f_code_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String f_code,
			String gsp) {
		boolean ret = false;
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		List<Map> f_code_list = JSON.parseArray(goods.getGoods_f_code(),
				Map.class);
		for (Map map : f_code_list) {
			if ((CommUtil.null2String(map.get("code")).equals(f_code))
					&& (CommUtil.null2Int(map.get("status")) == 0)) {
				ret = true;
			}
		}
		if (ret) {
			List<GoodsCart> carts_list = Lists.newArrayList();
			List<GoodsCart> carts_cookie = Lists.newArrayList();
			List<GoodsCart> carts_user = Lists.newArrayList();
			Map cart_map = Maps.newHashMap();
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			cart_map.clear();
			cart_map.put("user_id", user.getId());
			cart_map.put("cart_status", Integer.valueOf(0));
			
			carts_user = this.goodsCartService.queryPageList(cart_map);
			
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
				for (GoodsCart gc2 : carts_user) {
					if ((cookie.getGoods().getId().equals(gc2.getGoods()
							.getId()))
							&& (cookie.getSpec_info()
									.equals(gc2.getSpec_info()))) {
						add = false;
						this.goodsCartService.deleteById(cookie.getId());
					}
				}
				if (add) {
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.updateById(cookie);
					carts_list.add(cookie);
				}
			}
			GoodsCart obj = new GoodsCart();
			boolean add = true;
			String[] gsp_ids = CommUtil.null2String(gsp).split(",");
			Arrays.sort(gsp_ids);
			int i;
			for (GoodsCart gc : carts_list) {
				if ((gsp_ids != null) && (gsp_ids.length > 0)
						&& (gc.getGsps() != null)) {
					String[] gsp_ids1 = new String[gc.getGsps().size()];
					for (i = 0; i < gc.getGsps().size(); i++) {
						gsp_ids1[i] = (gc.getGsps().get(i) != null ? ((GoodsSpecProperty) gc
								.getGsps().get(i)).getId().toString() : "");
					}
					Arrays.sort(gsp_ids1);
					if ((gc.getGoods().getId().toString().equals(goods_id))
							&& (Arrays.equals(gsp_ids, gsp_ids1))) {
						add = false;
					}
				} else if (gc.getGoods().getId().toString().equals(goods_id)) {
					add = false;
				}
			}
			if (add) {
				obj.setAddTime(new Date());
				obj.setCount(1);
				String price = this.cartTools.getGoodsPriceByGsp(gsp, goods_id);
				obj.setPrice(BigDecimal.valueOf(CommUtil.null2Double(price)));
				obj.setGoods(goods);
				String spec_info = "";
				for (String gsp_id : gsp_ids) {
					GoodsSpecProperty spec_property = this.goodsSpecPropertyService
							.selectByPrimaryKey(CommUtil.null2Long(gsp_id));
					obj.getGsps().add(spec_property);
					if (spec_property != null) {
						spec_info = spec_property.getSpec().getName() + ":"
								+ spec_property.getValue() + " " + spec_info;
					}
				}
				obj.setUser(user);
				obj.setCart_gsp(gsp);
				obj.setSpec_info(spec_info);
				this.goodsCartService.saveEntity(obj);
				ret = true;
				if (ret) {
					for (Map map : f_code_list) {
						if ((CommUtil.null2String(map.get("code"))
								.equals(f_code))
								&& (CommUtil.null2Int(map.get("status")) == 0)) {
							map.put("status", Integer.valueOf(1));
							break;
						}
					}
					goods.setGoods_f_code(JSON.toJSONString(f_code_list));
					this.goodsService.updateById(goods);
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

	private Map handle_limit_cart(Goods goods, int next) {
		User user = SecurityUserHolder.getCurrentUser();
		Map<String, Object> params = Maps.newHashMap();
		int limit_count = -1;
		if (user != null) {
			user = this.userService.selectByPrimaryKey(user.getId());
			Map cart_map = Maps.newHashMap();
			cart_map.put("user_id", user.getId());
			cart_map.put("goods_id", goods.getId());
			if ((goods.getGoods_limit() == 1)
					&& (goods.getGoods_limit_count() > 0)) {
				String info = CommUtil.null2String(this.userService.selectByPrimaryKey(
						user.getId()).getBuy_goods_limit_info());
				if (!CommUtil.null2String(info).equals("")) {
					Map maps = JSON.parseObject(CommUtil.null2String(info));
					List<Map> list = (List) maps.get("data");
					for (Map map : list) {
						String gid = CommUtil.null2String(map.get("gid"));
						if (CommUtil.null2Long(gid).equals(goods.getId())) {
							limit_count = goods.getGoods_limit_count()
									- CommUtil.null2Int(map.get("count"));
							if (limit_count >= 1) {
								break;
							}
							limit_count = 0;
							next = -4;

							break;
						}
					}
				}
				if (limit_count == -1) {
					limit_count = goods.getGoods_limit_count();
				}
			}
		} else if ((goods.getGoods_limit() == 1)
				&& (goods.getGoods_limit_count() > 0)) {
			limit_count = 0;
			next = -5;
		}
		params.put("limit_count", Integer.valueOf(limit_count));
		params.put("next", Integer.valueOf(next));
		return params;
	}

	private static String getHttpContent(String url, String charSet,
			String method) {
		HttpURLConnection connection = null;
		String content = "";
		try {
			URL address_url = new URL(url);
			connection = (HttpURLConnection) address_url.openConnection();
			connection.setRequestMethod("GET");

			connection.setConnectTimeout(1000000);
			connection.setReadTimeout(1000000);

			int response_code = connection.getResponseCode();
			if (response_code == 200) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in, charSet));
				String line = null;
				while ((line = reader.readLine()) != null) {
					content = content + line;
				}
				return content;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		if (connection != null) {
			connection.disconnect();
		}
		return "";
	}
}
