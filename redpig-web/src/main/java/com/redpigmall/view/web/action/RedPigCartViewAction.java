package com.redpigmall.view.web.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
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
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.api.tools.XMLUtil;
import com.redpigmall.domain.Address;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.CombinPlan;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.Inventory;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Payment;
import com.redpigmall.domain.StoreHouse;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VatInvoice;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigRedPigCartViewAction.java
 * </p>
 * 
 * <p>
 * Description:购物控制器,包括购物车所有操作及订单相关操作。主要包含：购物三个主要流程、F码购物、添加商品到购物车、从购物车移除商品、
 * 购物地址处理、各种付款方式付款等等
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
 * @date 2016-5-14
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigCartViewAction extends BaseAction{
	

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
			
			GoodsSpecProperty spec_property = this.goodsSpecPropertyService.selectByPrimaryKey(CommUtil.null2Long(gsp_id));
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
	 * 用户登陆后清除用户购物车中自己店铺的商品，将cookie购物车与用户user购物车合并，去重复商品（相同商品不同规格不去掉）
	 * 
	 * @param request
	 * @return
	 */
	private List<GoodsCart> cart_calc(HttpServletRequest request) {
		List<GoodsCart> carts_list = this.goodsCartService.cart_list(request,null, null, null, false);
		return carts_list;
	}
	
	/**
	 * 购物车菜单详情
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/cart_menu_detail" })
	public ModelAndView cart_menu_detail(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("cart_menu_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = cart_calc(request);
		if (carts.size() > 0) {
			mv.addObject("total_price",Double.valueOf(this.cartTools.getPriceByCarts(carts, "")));
			mv.addObject("carts", carts);
		}
		return mv;
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
	 * @param combin_version
	 *            组合套装中套装版本
	 */
	@RequestMapping({ "/add_goods_cart" })
	public void add_goods_cart(HttpServletRequest request,
			HttpServletResponse response, String id, String count, String gsp,
			String buy_type, String combin_ids, String combin_version) {
		
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
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param count
	 * @param gcs
	 * @param gift_id
	 */
	@RequestMapping({ "/goods_count_adjust" })
	public void goods_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String count,
			String gcs, String gift_id) {

		List<GoodsCart> carts = this.cart_calc(request);
		Map map = new HashMap();
		String code = "100";// 100表示修改成功，200表示库存不足,300表示团购库存不足
		double gc_price = 0.00;// 单位GoodsCart总价钱
		double total_price = 0.00;// 购物车总价钱
		String cart_type = "";// 判断是否为组合销售
		Goods goods = null;
		int temp_count = CommUtil.null2Int(count);
		GoodsCart gc = this.goodsCartService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		if (gc != null) {
			if (CommUtil.null2String(count).length() <= 9) {
				if (gc.getId().toString().equals(gc_id)) {
					cart_type = CommUtil.null2String(gc.getCart_type());
					goods = gc.getGoods();
					if (cart_type.equals("")) {// 普通商品的处理
						if (goods.getGroup_buy() == 2) {// 团购商品处理
							GroupGoods gg = new GroupGoods();
							for (GroupGoods gg1 : goods.getGroup_goods_list()) {
								if (gg1.getGg_goods().getId()
										.equals(goods.getId())) {
									gg = gg1;
									break;
								}
							}
							if (gg.getGg_count() >= CommUtil.null2Int(count)) {
								gc.setPrice(BigDecimal.valueOf(CommUtil
										.null2Double(gg.getGg_price())));
								gc_price = CommUtil
										.mul(gg.getGg_price(), count);
								gc.setCount(CommUtil.null2Int(count));
								this.goodsCartService.updateById(gc);
							} else {
								if (gg.getGg_count() == 0) {
									gc.setCount(0);
									this.goodsCartService.updateById(gc);
								}
								code = "300";
							}
						} else {
							String gsp = "";
							for (GoodsSpecProperty gs : gc.getGsps()) {
								gsp = gs.getId() + "," + gsp;
							}
							int inventory = goods.getGoods_inventory();
							if (("spec").equals(goods.getInventory_type())) {
								List<HashMap> list = Json.fromJson(
										ArrayList.class,
										goods.getGoods_inventory_detail());
								String[] gsp_ids = gsp.split(",");
								for (Map temp : list) {
									String[] temp_ids = CommUtil.null2String(
											temp.get("id")).split("_");
									Arrays.sort(gsp_ids);
									Arrays.sort(temp_ids);
									if (Arrays.equals(gsp_ids, temp_ids)) {
										inventory = CommUtil.null2Int(temp
												.get("count"));
									}
								}
							}
							if (inventory >= CommUtil.null2Int(count)
									&& CommUtil.null2String(count).length() <= 9
									&& gc.getGoods().getGroup_buy() != 2) {
								if (gc.getId().toString().equals(gc_id)) {
									gc.setCount(CommUtil.null2Int(count));
									this.goodsCartService.updateById(gc);
									gc_price = CommUtil.mul(gc.getPrice(),
											count);
								}
							} else {
								if (inventory == 0) {
									gc.setCount(0);
									this.goodsCartService.updateById(gc);
								}
								code = "200";
							}
						}
					}
					if (cart_type.equals("combin") && gc.getCombin_main() == 1) {// 组合销售的处理
						if (goods.getGoods_inventory() >= CommUtil
								.null2Int(count)) {
							gc.setCount(CommUtil.null2Int(count));
							this.goodsCartService.updateById(gc);
							String suit_all_price = "0.00";
							GoodsCart suit = gc;
							Map suit_map = (Map) Json.fromJson(suit
									.getCombin_suit_info());
							suit_map.put("suit_count", CommUtil.null2Int(count));
							suit_all_price = CommUtil.formatMoney(CommUtil.mul(
									CommUtil.null2Int(count), CommUtil
											.null2Double(suit_map
													.get("plan_goods_price"))));
							suit_map.put("suit_all_price", suit_all_price);// 套装整体价格=套装单价*数量
							String new_json = Json.toJson(suit_map,
									JsonFormat.compact());
							suit.setCombin_suit_info(new_json);
							suit.setCount(CommUtil.null2Int(count));
							this.goodsCartService.updateById(suit);
							gc_price = CommUtil.null2Double(suit_all_price);
						} else {
							if (goods.getGoods_inventory() == 0) {
								gc.setCount(0);
								this.goodsCartService.updateById(gc);
							}
							code = "200";
						}
					}
					// 判断出是否满足满就送条件
					if (gift_id != null) {
						BuyGift bg = this.buyGiftService.selectByPrimaryKey(CommUtil
								.null2Long(gift_id));
						Set<Long> bg_ids = new HashSet<Long>();
						if (bg != null) {
							bg_ids.add(bg.getId());
						}
						List<GoodsCart> g_carts = new ArrayList<GoodsCart>();
						if (CommUtil.null2String(gcs).equals("")) {
							for (GoodsCart gCart : carts) {
								if (gCart.getGoods()
										.getOrder_enough_give_status() == 1
										&& gCart.getGoods().getBuyGift_id() != null) {
									bg_ids.add(gCart.getGoods().getBuyGift_id());
								}
							}
							g_carts = carts;
						} else {
							String[] gc_ids = gcs.split(",");
							for (String g_id : gc_ids) {
								GoodsCart goodsCart = this.goodsCartService
										.selectByPrimaryKey(CommUtil.null2Long(g_id));
								if (goodsCart != null
										&& goodsCart.getGoods()
												.getOrder_enough_give_status() == 1
										&& goodsCart.getGoods().getBuyGift_id() != null) {
									bg_ids.add(goodsCart.getGoods()
											.getBuyGift_id());
									g_carts.add(goodsCart);
								}
							}
						}
						Map<Long, List<GoodsCart>> gc_map = new HashMap<Long, List<GoodsCart>>();
						for (Long id : bg_ids) {
							gc_map.put(id, new ArrayList<GoodsCart>());
						}
						for (GoodsCart cart : g_carts) {
							if (cart.getGoods().getOrder_enough_give_status() == 1
									&& cart.getGoods().getBuyGift_id() != null) {
								for (Map.Entry<Long, List<GoodsCart>> entry : gc_map
										.entrySet()) {
									if (cart.getGoods().getBuyGift_id()
											.equals(entry.getKey())) {
										entry.getValue().add(cart);
									}
								}
							}
						}
						
						List<String> enough_bg_ids = new ArrayList<String>();
						for (Map.Entry<Long, List<GoodsCart>> entry : gc_map.entrySet()) {
							BuyGift buyGift = this.buyGiftService.selectByPrimaryKey(entry.getKey());
							// 计算出购物车价钱是否满足对应满就送
							List<GoodsCart> arrs = entry.getValue();
							BigDecimal bd = new BigDecimal("0.00");
							for (GoodsCart arr : arrs) {
								bd = bd.add(BigDecimal.valueOf(CommUtil.mul(arr.getPrice(), arr.getCount())));
							}
							if (bd.compareTo(buyGift.getCondition_amount()) >= 0) {
								enough_bg_ids.add(buyGift.getId().toString());
							}
						}
						map.put("bg_ids", enough_bg_ids);
					}
				}

			} else {
				code = "200";
			}
			map.put("count", gc.getCount());
		}
		total_price = this.calCartPrice(carts, gcs);
		Map price_map = calEnoughReducePrice(carts, gcs);
		Map<Long, String> erMap = (Map<Long, String>) price_map.get("erString");
		map.put("gc_price", CommUtil.formatMoney(gc_price));
		map.put("total_price", CommUtil.formatMoney(total_price));
		map.put("code", code);
		map.put("enough_reduce_price",CommUtil.formatMoney(price_map.get("reduce")));
		map.put("before", CommUtil.formatMoney(price_map.get("all")));
		for (long k : erMap.keySet()) {
			map.put("erString" + k, erMap.get(k));
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer;
		try {
			writer = response.getWriter();
			writer.print(Json.toJson(map, JsonFormat.compact()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Map calEnoughReducePrice(List<GoodsCart> carts, String gcs) {
		Map<Long, String> erString = Maps.newHashMap();
		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		Map erid_goodsids = new HashMap();
		Date date = new Date();
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(), gc.getPrice()));
				if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
					String er_id = gc.getGoods().getOrder_enough_reduce_id();
					EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
					if (er.getErstatus() == 10
							&& er.getErbegin_time().before(date)) {
						if (ermap.containsKey(er_id)) {
							double last_price = ermap.get(er_id);
							ermap.put(er_id,CommUtil.add(last_price,CommUtil.mul(gc.getCount(),gc.getPrice())));
							((List) erid_goodsids.get(er_id)).add(gc.getGoods().getId());
						} else {
							ermap.put(er_id,CommUtil.mul(gc.getCount(), gc.getPrice()));
							List list = new ArrayList();
							list.add(gc.getGoods().getId());
							erid_goodsids.put(er_id, list);
						}
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(), gc.getPrice()));
						if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
							String er_id = gc.getGoods().getOrder_enough_reduce_id();
							EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
							if (er.getErstatus() == 10 && er.getErbegin_time().before(date)) {
								if (ermap.containsKey(er_id)) {
									double last_price = ermap.get(er_id);
									ermap.put(er_id, CommUtil.add(last_price,CommUtil.mul(gc.getCount(),gc.getPrice())));
									((List) erid_goodsids.get(er_id)).add(gc.getGoods().getId());
								} else {
									ermap.put(er_id,CommUtil.mul(gc.getCount(),gc.getPrice()));
									List list = new ArrayList();
									list.add(gc.getGoods().getId());
									erid_goodsids.put(er_id, list);
								}
							}
						}
					}
				}
			}
		}
		double all_enough_reduce = 0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil
					.null2Long(er_id));
			String erjson = er.getEr_json();
			double er_money = ermap.get(er_id);// 购物车中的此类满减的金额
			Map fromJson = (Map) Json.fromJson(erjson);
			double reduce = 0;
			String erstr = "";
			for (Object enough : fromJson.keySet()) {
				if (er_money >= CommUtil.null2Double(enough)) {
					reduce = CommUtil.null2Double(fromJson.get(enough));
					erstr = "活动商品已购满" + enough + "元,已减" + reduce + "元";
					erid_goodsids.put("enouhg_" + er_id, enough);
				}
			}
			erString.put(er.getId(), erstr);
			erid_goodsids.put("all_" + er_id, er_money);
			erid_goodsids.put("reduce_" + er_id, reduce);

			all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
		}
		Map prices = new HashMap();
		prices.put("er_json", Json.toJson(erid_goodsids, JsonFormat.compact()));
		prices.put("erString", erString);

		double d2 = Math.round(all_price * 100) / 100.0;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("all", CommUtil.null2Double(bd2));// 商品总价

		double er = Math.round(all_enough_reduce * 100) / 100.0;
		BigDecimal erbd = new BigDecimal(er);
		BigDecimal erbd2 = erbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("reduce", CommUtil.null2Double(erbd2));// 满减价格

		double af = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		BigDecimal afbd = new BigDecimal(af);
		BigDecimal afbd2 = afbd.setScale(2, BigDecimal.ROUND_HALF_UP);
		prices.put("after", CommUtil.null2Double(afbd2));// 减后价格

		return prices;
	}

	/**
	 * 获得购物车中用户勾选需要购买的商品总价格
	 * 
	 * @param request
	 * @param response
	 */
	private double calCartPrice(List<GoodsCart> carts, String gcs) {
		double all_price = 0.0;
		Map<String, Double> ermap = new HashMap<String, Double>();
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				if (gc.getCart_type() == null 
						|| gc.getCart_type().equals("")) {// 普通商品处理
					
					all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(), gc.getPrice()));
				} else if (gc.getCart_type().equals("combin")) {// 组合套装商品处理
					if (gc.getCombin_main() == 1) {
						Map map = (Map) Json.fromJson(gc.getCombin_suit_info());
						all_price = CommUtil.add(all_price,
								map.get("suit_all_price"));
					}
				}
				
				if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
					String er_id = gc.getGoods().getOrder_enough_reduce_id();
					if (ermap.containsKey(er_id)) {
						double last_price = ermap.get(er_id);
						ermap.put(er_id,CommUtil.add(last_price,CommUtil.mul(gc.getCount(),gc.getPrice())));
					} else {
						ermap.put(er_id,CommUtil.mul(gc.getCount(), gc.getPrice()));
					}
				}
			}
		} else {
			
			String[] gc_ids = gcs.split(",");
			for (GoodsCart gc : carts) {
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						if (gc.getCart_type() != null && gc.getCart_type().equals("combin") && gc.getCombin_main() == 1) {
							Map map = (Map) Json.fromJson(gc.getCombin_suit_info());
							all_price = CommUtil.add(all_price,map.get("suit_all_price"));
						} else {
							all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(), gc.getPrice()));
						}
						if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
							String er_id = gc.getGoods().getOrder_enough_reduce_id();
							if (ermap.containsKey(er_id)) {
								double last_price =  ermap.get(er_id);
								ermap.put(er_id,CommUtil.add(last_price,CommUtil.mul(gc.getCount(),gc.getPrice())));
							} else {
								ermap.put(er_id,CommUtil.mul(gc.getCount(),gc.getPrice()));
							}
						}
					}
				}
			}
		}
		
		double all_enough_reduce = 0;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
			if (er.getErstatus() == 10 && er.getErbegin_time().before(new Date())) {// 活动通过审核且正在进行
				String erjson = er.getEr_json();
				double er_money = ermap.get(er_id);// 购物车中的此类满减的金额
				Map fromJson = (Map) Json.fromJson(erjson);
				double reduce = 0;
				for (Object enough : fromJson.keySet()) {
					if (er_money >= CommUtil.null2Double(enough)) {
						reduce = CommUtil.null2Double(fromJson.get(enough));
					}
				}
				all_enough_reduce = CommUtil.add(all_enough_reduce, reduce);
			}
		}
		double d2 = Math.round((all_price - all_enough_reduce) * 100) / 100.0;
		return CommUtil.null2Double(CommUtil.formatMoney(d2));
	}
	
	/**
	 * 组合购物车详细
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/combin_carts_detail" })
	public void combin_carts_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		int code = -100;
		Map json_map = Maps.newHashMap();
		List<Map> map_list = Lists.newArrayList();
		GoodsCart cart = this.goodsCartService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((cart != null) && (cart.getCart_type() != null)
				&& (cart.getCart_type().equals("combin"))
				&& (cart.getCombin_main() == 1)) {
			String[] cart_ids = cart.getCombin_suit_ids().split(",");
			
			for (String cart_id : cart_ids) {
				
				if ((!cart_id.equals("")) && (!cart_id.equals(id))) {
					GoodsCart other = this.goodsCartService.selectByPrimaryKey(CommUtil.null2Long(cart_id));
					if (other != null) {
						Map temp_map = Maps.newHashMap();
						temp_map.put("id", other.getId());
						temp_map.put("name", other.getGoods().getGoods_name());
						temp_map.put("price", other.getGoods().getGoods_current_price());
						temp_map.put("count", other.getCount());
						temp_map.put("all_price", other.getPrice());
						temp_map.put("spec_info", other.getSpec_info());
						String goods_url = CommUtil.getURL(request) + "/items_" + other.getGoods().getId() + "";
						
						if ((this.configService.getSysConfig().getSecond_domain_open())
								&& (other.getGoods().getGoods_store()
										.getStore_second_domain() != "")) {
							
							if (other.getGoods().getGoods_type() == 1) {
								String store_second_domain = "http://" + other.getGoods().getGoods_store().getStore_second_domain() + "." + CommUtil.generic_domain(request);
								goods_url = store_second_domain + "/items_" + other.getGoods().getId() + "";
							}
						}
						temp_map.put("url", goods_url);
						String img2 = this.redPigSysConfigService.getSysConfig().getImageWebServer()
								+ "/"
								+ this.configService.getSysConfig().getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig().getGoodsImage().getName();
						if (other.getGoods().getGoods_main_photo() != null) {
							img2 = this.redPigSysConfigService.getSysConfig().getImageWebServer()
									+ "/"
									+ other.getGoods().getGoods_main_photo().getPath()
									+ "/"
									+ other.getGoods().getGoods_main_photo().getName()
									+ "_small."
									+ other.getGoods().getGoods_main_photo().getExt();
						}
						temp_map.put("img", img2);
						map_list.add(temp_map);
					}
					code = 100;
				}
			}
		}
		json_map.put("map_list", map_list);
		json_map.put("code", code);
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
	 * 组合销售的购物车
	 * @param request
	 * @param response
	 * @param id
	 * @param combin_ids
	 * @param type
	 * @param combin_version
	 * @return
	 */
	@RequestMapping({ "/goods_cart_combin" })
	public ModelAndView goods_cart_combin(HttpServletRequest request,
			HttpServletResponse response, String id, String combin_ids,
			String type, String combin_version) {
		ModelAndView mv = new RedPigJModelAndView("goods_cart_suit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean ret = false;
		Map suit_map = null;
		User user = SecurityUserHolder.getCurrentUser();
		if ((type != null) && (!type.equals(""))) {
			if (type.equals("suit")) {
				String main_goods_info = null;
				if ((combin_ids != null) && (!combin_ids.equals(""))) {
					ret = true;
				}
				if (ret) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("main_goods_id", CommUtil.null2Long(id));
					params.put("combin_type", Integer.valueOf(0));
					params.put("combin_status", Integer.valueOf(1));
					List<CombinPlan> suits = this.combinplanService.queryPageList(params);
					
					for (CombinPlan obj : suits) {
						List<Map> map_list = JSON.parseArray(
								obj.getCombin_plan_info(), Map.class);
						for (Map temp_map : map_list) {
							String ids = this.goodsViewTools
									.getCombinPlanGoodsIds(temp_map);
							if (ids.equals(combin_ids)) {
								suit_map = temp_map;
								main_goods_info = obj.getMain_goods_info();
								break;
							}
						}
					}
				}
				if (suit_map != null) {
					List<Map> map_list_temp = Lists.newArrayList();
					if (main_goods_info != null) {
						Map main_map = JSON.parseObject(main_goods_info);
						map_list_temp.add(main_map);
					}
					List<Map> other_goods_maps = this.goodsViewTools.getCombinPlanGoods(suit_map);
					for (Map other : other_goods_maps) {
						map_list_temp.add(other);
					}
					for (Map temp : map_list_temp) {
						Goods goods_temp = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(temp.get("id")));
						int goods_inventory = 0;
						if (goods_temp != null) {
							goods_inventory = goods_temp.getGoods_inventory();
						}
						temp.put("inventory", goods_inventory);
						Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(temp.get("id")));
						int count;
						if ((goods.getGoods_limit() == 1)
								&& (goods.getGoods_limit_count() > 0)) {
							count = goods.getGoods_limit_count();
							if (user != null) {
								String info = CommUtil.null2String(user.getBuy_goods_limit_info());
								if (!info.equals("")) {
									Map maps = JSON.parseObject(CommUtil.null2String(info));
									List<Map> list = (List) maps.get("data");
									for (Map map : list) {
										String gid = CommUtil.null2String(map.get("gid"));
										if (CommUtil.null2Int(gid) == goods.getId()) {
											count = goods.getGoods_limit_count() - CommUtil.null2Int(map.get("count"));
										}
									}
								}
							}
						} else {
							count = 2;
						}
						temp.put("count", count);
					}
					mv.addObject("maps", map_list_temp);
					mv.addObject("plan_map", suit_map);
					mv.addObject("combin_version", combin_version);
					mv.addObject("goodsViewTools", this.goodsViewTools);
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您所访问的地址不存在");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
			mv.addObject("id", id);
			mv.addObject("goodsViewTools", this.goodsViewTools);
			mv.addObject("combin_ids", combin_ids);
			mv.addObject("type", type);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 购物车头部
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/goods_cart_head" })
	public ModelAndView goods_cart_head(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("goods_cart_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		return mv;
	}
	
	/**
	 * 购物车第一步
	 * @param request
	 * @param response
	 * @param gid
	 * @return
	 */
	@RequestMapping({ "/goods_cart0" })
	public ModelAndView goods_cart0(HttpServletRequest request,
			HttpServletResponse response, String gid) {
		ModelAndView mv = new RedPigJModelAndView("goods_cart0.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
		if (goods != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("goods_status", Integer.valueOf(0));
			map.put("gc_id", goods.getGc().getId());
			map.put("id_no", CommUtil.null2Long(gid));
			map.put("orderBy", "goods_salenum");
			map.put("orderType", "desc");
			
			List<Goods> class_goods = this.goodsService.queryPageList(map,0,9);
			
			mv.addObject("class_goods", class_goods);
		}
		List<Goods> ztc_goods = this.goodsViewTools.query_Ztc_Goods(6);
		mv.addObject("ztc_goods", ztc_goods);
		String return_url = CommUtil.getURL(request) + "/items_" + gid + "";
		if ((goods != null) && (goods.getGoods_type() == 1)
				&& (this.configService.getSysConfig().getSecond_domain_open())
				&& (goods.getGoods_store() != null)
				&& (goods.getGoods_store().getStore_second_domain() != "")) {
			String store_second_domain = "http://"
					+ goods.getGoods_store().getStore_second_domain() + "."
					+ CommUtil.generic_domain(request);
			return_url = store_second_domain + "/items_" + gid + "";
		}
		mv.addObject("return_url", return_url);
		return mv;
	}
	
	/**
	 * 购物车
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/goods_cart1" })
	public ModelAndView goods_cart1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("goods_cart1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}	
	
	/**
	 * 购物车加载
	 * @param request
	 * @param response
	 * @param load_class
	 * @param area_id
	 * @return
	 */
	@RequestMapping({ "/goods_cart1_load" })
	public ModelAndView goods_cart1_load(HttpServletRequest request,
			HttpServletResponse response, String load_class, String area_id) {
		ModelAndView mv = new RedPigJModelAndView("goods_cart1_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<GoodsCart> carts = cart_calc(request);
		Date date = new Date();
		if (carts.size() > 0) {
			Set<Long> set = Sets.newHashSet();
			List<GoodsCart> native_goods = Lists.newArrayList();
			Map<Long, List<GoodsCart>> ermap = Maps.newHashMap();
			Map<Long, String> erString = Maps.newHashMap();
			User user = SecurityUserHolder.getCurrentUser();
			EnoughReduce er;
			for (GoodsCart cart : carts) {
				if ((cart.getGoods().getGoods_limit() == 1)
						&& (cart.getGoods().getGoods_limit_count() > 0)) {
					if (user != null) {
						Map limit_map = this.cartTools.handle_limit_cart(cart.getGoods(), null);
						
						int limit_code = CommUtil.null2Int(limit_map.get("limit_code"));
						if (limit_code == 0) {
							int limit_count = CommUtil.null2Int(limit_map.get("limit_count"));
							cart.setRemain_count(limit_count);
							this.goodsCartService.updateById(cart);
						}
					}
				}
				if ((cart.getGoods().getOrder_enough_give_status() == 1)
						&& (cart.getGoods().getBuyGift_id() != null)) {
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
			if (set.size() > 0) {
				Map<Long,List> map = Maps.newHashMap();
				for (Long id : set) {
					map.put(id, new ArrayList());
				}
				for (GoodsCart cart : carts) {
					if ((cart.getGoods().getOrder_enough_give_status() == 1)
							&& (cart.getGoods().getBuyGift_id() != null)
							&&  map.containsKey(cart.getGoods().getBuyGift_id())) {
						
						map.get(cart.getGoods().getBuyGift_id()).add(cart);
						
					}
				}
				mv.addObject("ac_goods", map);
			}
		}
		if (load_class != null) {
			mv.addObject("load_class", load_class);
		}
		if ((area_id == null) || (area_id.equals(""))) {
			area_id = CommUtil.null2String(request.getSession(false)
					.getAttribute("default_area_id"));
		}
		if ((area_id != null) && (!area_id.equals(""))) {
			this.areaViewTools.setDefaultArea(request, area_id);
			mv.addObject("area_id", area_id);
		}
		this.areaViewTools.getUserAreaInfo(request, mv);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("cartTools", this.cartTools);
		return mv;
	}
	
	/**
	 * 购物车规格
	 * @param request
	 * @param response
	 * @param cart_id
	 * @return
	 */
	@RequestMapping({ "/goods_cart1_spec" })
	public ModelAndView goods_cart1_spec(HttpServletRequest request,
			HttpServletResponse response, String cart_id) {
		ModelAndView mv = new RedPigJModelAndView("goods_cart1_spec.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		GoodsCart cart = this.goodsCartService.selectByPrimaryKey(CommUtil
				.null2Long(cart_id));
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("cart", cart);
		return mv;
	}
	
	/**
	 * 购物车商品规格新增
	 * @param request
	 * @param response
	 * @param gsp
	 * @param id
	 */
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
				int i;
				for (GoodsCart gc : carts_list) {
					if (!CommUtil.null2String(gc.getId()).equals(id)) {
						String[] gsp_ids = CommUtil.null2String(gsp).split(",");
						Arrays.sort(gsp_ids);
						if ((gsp_ids != null) && (gsp_ids.length > 0)
								&& (gc.getGsps().size() > 0)) {
							String[] gsp_ids1 = new String[gc.getGsps().size()];
							for (i = 0; i < gc.getGsps().size(); i++) {
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
					if ((obj_gc.getGoods().getGoods_limit() == 1)
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
	
	/**
	 * 确认购物车第二步
	 * @param request
	 * @param response
	 * @param gcs 购物车ID
	 * @param giftids 满就送
	 * @return
	 */
	@SecurityMapping(title = "确认购物车第二步", value = "/goods_cart2*", rtype = "buyer", rname = "购物流程2", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/goods_cart2" })
	public ModelAndView goods_cart2(HttpServletRequest request,
			HttpServletResponse response, String gcs, String giftids) {
		ModelAndView mv = new RedPigJModelAndView("goods_cart2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((gcs == null) || (gcs.equals(""))) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，请重新进入购物车");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		/**
		 * 查询当前用户所有的购物车
		 */
		List<GoodsCart> carts = this.goodsCartService.cart_list(request, null,null, null, true);
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
		if ((flag) && (carts.size() > 0)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("orderBy", "default_val desc,");
			params.put("orderType", "addTime desc");
			
			List<Address> addrs = this.addressService.queryPageList(params);
			
			mv.addObject("addrs", addrs);
			String cart_session = CommUtil.randomString(32);
			request.getSession(false).setAttribute("cart_session", cart_session);
			Date date = new Date();
			/**
			 * 满就减价格
			 */
			Map erpMap = this.cartTools.getEnoughReducePriceByCarts(carts, gcs);
			mv.addObject("cart_session", cart_session);
			mv.addObject("transportTools", this.transportTools);
			mv.addObject("goodsViewTools", this.goodsViewTools);
			mv.addObject("order_goods_price", erpMap.get("all"));
			mv.addObject("order_er_price", erpMap.get("reduce"));
			List map_list = Lists.newArrayList();
			//购物车里面的商品所属店铺ID或者自营self标识
			List<Object> store_list = Lists.newArrayList();
			for (GoodsCart gc : carts) {
				if (gc.getGoods().getGoods_type() == 1) {//如果是第三方商品
					store_list.add(gc.getGoods().getGoods_store().getId());
				} else {
					store_list.add("self");
				}
			}
			HashSet hs = new HashSet(store_list);
			store_list.removeAll(store_list);
			store_list.addAll(hs);
			String[] gc_ids = CommUtil.null2String(gcs).split(",");
			//满就送商品
			List<Goods> ac_goodses = Lists.newArrayList();
			if ((giftids != null) && (!giftids.equals(""))) {
				String[] gift_ids = giftids.split(",");
				
				for (String gift_id : gift_ids) {
					
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gift_id));
					
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
				
				Object ergcMap;
				//如果不是自营商品
				if ((sl != "self") && (!sl.equals("self"))) {
					
					List<GoodsCart> gc_list = Lists.newArrayList();
					List<GoodsCart> amount_gc_list = Lists.newArrayList();
					Map<Goods, List<GoodsCart>> gift_map = Maps.newHashMap();
					Map<Long, List<GoodsCart>> ermap = Maps.newHashMap();
					Map<Long, String> erString = Maps.newHashMap();
					//满就送
					for (Goods g : ac_goodses) {
						//如果是第三方商品
						if (g.getGoods_type() == 1) {
							if (g.getGoods_store().getId().toString().equals(sl.toString())) {
								gift_map.put(g, new ArrayList<GoodsCart>());
							}
						}
					}
					
					//循环查询出来的购物车
					for (GoodsCart gc : carts) {
						//循环前台传过来的购物车id
						for (String gc_id : gc_ids) {
							if (!CommUtil.null2String(gc_id).equals("")) {//如果购物车id不为空
								if ((CommUtil.null2Long(gc_id).equals(gc.getId()))//如果规格id==购物车的id
										&& (gc.getGoods().getGoods_store() != null)) {//商品店铺不为空
									
									if (gc.getGoods().getGoods_store().getId().equals(sl)) {
										if ((ret)
												&& (gift_map.size() > 0)
												&& (gc.getGoods().getOrder_enough_give_status() == 1)) {
											
											if (gc.getGoods().getBuyGift_id() != null) {
												BuyGift bg = this.buyGiftService.selectByPrimaryKey(gc.getGoods().getBuyGift_id());
												
												if (bg.getBeginTime().before(date)) {
													
													for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map.entrySet()) {
														if (entry.getKey().getBuyGift_id().equals(gc.getGoods().getBuyGift_id())) {
															(entry.getValue()).add(gc);
														} else {
															gc_list.add(gc);
														}
													}
												}
												gc_list.add(gc);
											}
										}
										if (gc.getGoods().getEnough_reduce() == 1) {
											String er_id = gc.getGoods().getOrder_enough_reduce_id();
											
											EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
											
											if (er.getErbegin_time().before(date)) {
												if (ermap.containsKey(er.getId())) {
													ermap.get(er.getId()).add(gc);
												} else {
													List<GoodsCart> list = Lists.newArrayList();
													list.add(gc);
													ermap.put(er.getId(),list);
													Map<String, Object> map = JSON.parseObject(er.getEr_json());
													double k1 = 0.0D;
													String str = "";
													if (map != null) {
														for (String key : map.keySet()) {
															if (k1 == 0.0D) {
																k1 = Double.parseDouble(key);
																str = "活动商品购满" + k1 + "元，即可享受满减";
															}
															if (Double.parseDouble(key.toString()) < k1) {
																k1 = Double.parseDouble(key.toString());
																str = "活动商品购满" + k1+ "元，即可享受满减";
															}
														}
													}
													erString.put(er.getId(),str);
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
					
					if (((gc_list != null) 
							&& (gc_list.size() > 0))
							|| ((gift_map != null) && (gift_map.size() > 0))
							|| ((ermap != null) && (ermap.size() > 0))) {
						
						Map<String, Object> map = Maps.newHashMap();
						ergcMap = this.cartTools.getEnoughReducePriceByCarts(amount_gc_list, gcs);
						if (gift_map.size() > 0) {
							map.put("ac_goods", gift_map);
						}
						
						if (ermap.size() > 0) {
							map.put("er_goods", ermap);
							map.put("erString", ((Map) ergcMap).get("erString"));
						}
						
						map.put("store_id", sl);
						map.put("store", this.storeService.selectByPrimaryKey(CommUtil.null2Long(sl)));
						map.put("store_goods_price", this.cartTools.getPriceByCarts(amount_gc_list, gcs));
						map.put("store_enough_reduce",((Map) ergcMap).get("reduce"));
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
							gift_map.put(g, new ArrayList());
						}
					}
					
					for (GoodsCart gc : carts) {

						for (String gc_id : gc_ids) {
							if (!CommUtil.null2String(gc_id).equals("")) {
								if ((CommUtil.null2Long(gc_id).equals(gc.getId()))
										&& (gc.getGoods().getGoods_store() == null)) {
									if ((ret)
											&& (gift_map.size() > 0)
											&& (gc.getGoods()
													.getOrder_enough_give_status() == 1)) {
										
										if (gc.getGoods().getBuyGift_id() != null) {
											BuyGift bg = this.buyGiftService.selectByPrimaryKey(gc.getGoods().getBuyGift_id());
											
											if (bg.getBeginTime().before(date)) {
												for (Map.Entry<Goods, List<GoodsCart>> entry : gift_map.entrySet()) {
													if (entry.getKey().getBuyGift_id().equals(gc.getGoods().getBuyGift_id())) {
														entry.getValue().add(gc);
													} else {
														gc_list.add(gc);
													}
												}
											}
											gc_list.add(gc);
										}
									}
									if (gc.getGoods().getEnough_reduce() == 1) {
										String er_id = gc.getGoods().getOrder_enough_reduce_id();
										EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
										if (er.getErbegin_time().before(date)) {
											if (ermap.containsKey(er.getId())) {
												(ermap.get(er.getId())).add(gc);
											} else {
												List<GoodsCart> list1 = Lists.newArrayList();
												list1.add(gc);
												ermap.put(er.getId(),list1);
												Map<String, Object> map = JSON.parseObject(er.getEr_json());
												double k1 = 0.0D;
												String str = "";
												if (map != null) {
													for (Object key : map
															.keySet()) {
														if (k1 == 0.0D) {
															k1 = Double.parseDouble(key.toString());
															str = "活动商品购满" + k1 + "元，即可享受满减";
														}
														if (Double.parseDouble(key.toString()) < k1) {
															k1 = Double.parseDouble(key.toString());
															str = "活动商品购满" + k1 + "元，即可享受满减";
														}
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
					
					if (((gc_list != null) 
							&& gc_list.size() > 0)
							|| ((gift_map != null) && (gift_map.size() > 0))
							|| ((ermap != null) && (ermap.size() > 0))) {
						
						Map<String, Object> map = Maps.newHashMap();
						ergcMap = this.cartTools.getEnoughReducePriceByCarts(amount_gc_list, gcs);
						if (gift_map.size() > 0) {
							map.put("ac_goods", gift_map);
						}
						if (ermap.size() > 0) {
							map.put("er_goods", ermap);
							map.put("erString", ((Map) ergcMap).get("erString"));
						}
						map.put("store_id", sl);
						map.put("store_goods_price", Double.valueOf(this.cartTools.getPriceByCarts( amount_gc_list, gcs)));
						map.put("store_enough_reduce",((Map) ergcMap).get("reduce"));
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
			
			List days = Lists.newArrayList();
			List day_list = Lists.newArrayList();
			for (int i = 0; i < 7; i++) {
				Calendar cal = Calendar.getInstance();
				cal.add(6, i);
				days.add(CommUtil.formatTime("MM-dd", cal.getTime()) + "<br />" + generic_day(cal.get(7)));
				day_list.add(CommUtil.formatTime("MM-dd", cal.getTime()) + generic_day(cal.get(7)));
			}
			
			Calendar cal = Calendar.getInstance();
			mv.addObject("before_time1", cal.getTime().before(CommUtil.formatDate(CommUtil.formatTime("yyyy-MM-dd 15:00:00", new Date()),"yyyy-MM-dd HH:mm:ss")));
			
			mv.addObject("before_time2", cal.getTime().before(CommUtil.formatDate(CommUtil.formatTime("yyyy-MM-dd 19:00:00", new Date()),"yyyy-MM-dd HH:mm:ss")));
			mv.addObject("before_time3", cal.getTime().before(CommUtil.formatDate(CommUtil.formatTime("yyyy-MM-dd 22:00:00", new Date()),"yyyy-MM-dd HH:mm:ss")));
			
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			
			if (user != null) {
				Map vam = Maps.newHashMap();
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
			mv.addObject("goods_cod", goods_cod);
			mv.addObject("tax_invoice", tax_invoice);
			mv.addObject("giftids", giftids);
			mv.addObject("goodsTools", this.goodsTools);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "购物车信息为空");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}

	private String generic_day(int day) {
		String[] list = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		return list[(day - 1)];
	}
	
	/**
	 * 完成订单提交进入支付
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
	@SecurityMapping(title = "完成订单提交进入支付", value = "/goods_cart3*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/goods_cart3" })
	public ModelAndView goods_cart3(HttpServletRequest request,
			HttpServletResponse response, String cart_session, String store_id,
			String addr_id, String gcs, String delivery_time,
			String delivery_type, String delivery_id, String payType,
			String gifts) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("order_pay.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		String cart_session1 = (String) request.getSession(false).getAttribute(
				"cart_session");
		if ((!CommUtil.null2String(cart_session1).equals(cart_session))
				|| (CommUtil.null2String(store_id).equals(""))
				|| (CommUtil.null2String(gcs).equals(""))) {
			mv = new RedPigJModelAndView("error.html",
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
			mv = new RedPigJModelAndView("error.html",
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
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "您恶意篡改支付方式，订单已经失效");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
				return mv;
			}
			if (!gc.getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				mv = new RedPigJModelAndView("error.html",
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
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "订单中商品已经超出限购数量，请重新下单");
					mv.addObject("url", CommUtil.getURL(request) + "/index");
					return mv;
				}
			}
			int goods_inventory = CommUtil.null2Int(this.cartTools
					.getGoodsDefaultInfo(request, gc.getGoods(),
							gc.getCart_gsp()).get("count"));
			if ((goods_inventory == 0) || (goods_inventory < gc.getCount())) {
				mv = new RedPigJModelAndView("error.html",
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
			mv = new RedPigJModelAndView("error.html",
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
			mv = new RedPigJModelAndView("error.html",
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
				delivery_id, payType, order_carts, buyer, addr, "web");

		mv.addObject("all_of_price", CommUtil.formatMoney(Double.valueOf(this.orderFormTools.query_order_pay_price(CommUtil.null2String(main_order.getId())))));
		mv.addObject("paymentTools", this.paymentTools);
		mv.addObject("user", buyer);
		mv.addObject("from", "goodsCart");
		mv.addObject("order", main_order);
		return mv;
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
		ModelAndView mv = new RedPigJModelAndView("order_pay.html",
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
			if (order.getOrder_cat() == 1) {
				mv = new RedPigJModelAndView("recharge_order.html",
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
				mv.addObject("all_of_price", CommUtil.formatMoney(Double
						.valueOf(this.orderFormTools
								.query_order_pay_price(CommUtil
										.null2String(order.getId())))));
				return mv;
			}
		}
		return mv;
	}
	
	/**
	 * 订单支付
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @param pay_password
	 * @return
	 */
	@SecurityMapping(title = "订单支付", value = "/order_pay*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay" })
	public ModelAndView order_pay(HttpServletRequest request,
			HttpServletResponse response, String payType, String order_id,
			String pay_password) {
		ModelAndView mv = null;
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
		if (("balance".equals(payType)) && (user.getPay_password() != null)
				&& (!user.getPay_password().equals(""))) {
			if ((pay_password != null) && (!"".equals(pay_password))) {
				if (!user.getPay_password()
						.equals(Md5Encrypt.md5(pay_password))) {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "请不要违规操作");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/order");
					return mv;
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "请输入您的支付密码");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
				return mv;
			}
		}
		int order_status = this.orderFormTools.query_order_status(order_id);
		if (order_status == 10) {
			if (CommUtil.null2String(payType).equals("")) {
				mv = new RedPigJModelAndView("error.html",
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
				mv = new RedPigJModelAndView("balance_pay.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				double order_total_price = this.orderFormTools
						.query_order_pay_price(order_id);
				mv.addObject("order_total_price",
						Double.valueOf(order_total_price));
				mv.addObject("user", user);
			} else {
				mv = new RedPigJModelAndView("line_pay.html",
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
						response.sendRedirect("/pay/wx_pay?id=" + order_id
								+ "&showwxpaytitle=1&type=" + type);
					} catch (IOException e) {
						e.printStackTrace();
						mv = new RedPigJModelAndView("error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "支付方式错误！");
						mv.addObject("url", CommUtil.getURL(request) + "/index");
					}
				}
			}
			mv.addObject("order", order);
			mv.addObject("order_id", order.getId());
			mv.addObject("user", user);
		}
		if (order_status == 11) {
			if (payType.equals("balance")) {
				mv = new RedPigJModelAndView("balance_pay.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				double order_total_price = this.orderFormTools
						.query_order_pay_price(order_id);
				mv.addObject("order_total_price",
						Double.valueOf(order_total_price));
				mv.addObject("user", user);
			} else {
				mv = new RedPigJModelAndView("line_pay.html",
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
						response.sendRedirect("/pay/wx_pay?id=" + order_id
								+ "&showwxpaytitle=1&type=" + type);
					} catch (IOException e) {
						e.printStackTrace();
						mv = new RedPigJModelAndView("error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "支付方式错误！");
						mv.addObject("url", CommUtil.getURL(request) + "/index");
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
	 * 订单货到付款
	 * @param request
	 * @param response
	 * @param order_id
	 * @param pay_msg
	 * @param pay_session
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "订单货到付款", value = "/order_pay_payafter*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay_payafter" })
	public ModelAndView order_pay_payafter(HttpServletRequest request,
			HttpServletResponse response, String order_id, String pay_msg,
			String pay_session) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String pay_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("pay_session"));
		if (pay_session1.equals(pay_session)) {
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
			boolean ret = this.HandleOrderFormService.payByPayafter(order,
					CommUtil.getURL(request), pay_msg);
			if (ret) {
				this.orderFormTools.updateGoodsInventory(order);
			}
			request.getSession(false).removeAttribute("pay_session");
			mv.addObject("op_title", "货到付款提交成功，等待发货");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单已经支付，禁止重复支付");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}
		return mv;
	}
	
	/**
	 * 订单预付款支付
	 * @param request
	 * @param response
	 * @param payType
	 * @param order_id
	 * @param pay_msg
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "订单预付款支付", value = "/order_pay_balance*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_pay_balance" })
	public ModelAndView order_pay_balance(
			HttpServletRequest request,
			HttpServletResponse response, 
			String payType, 
			String order_id,
			String pay_msg)  {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		OrderForm order = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_id));
		int order_status = this.orderFormTools.query_order_status(order_id);

		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
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
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", "balance");
		List<Payment> payments = this.paymentService.queryPageList(params);
		
		double order_total_price = this.orderFormTools.query_order_pay_price(order_id);
		if (CommUtil.null2Double(user.getAvailableBalance()) >= order_total_price) {
			boolean ret = this.HandleOrderFormService.payByBalance(order,CommUtil.getURL(request), pay_msg);
			if (ret) {
				this.orderFormTools.updateGoodsInventory(order);
			}
			mv.addObject("op_title", "预付款支付成功");
			if (order.getOrder_cat() == 2) {
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
			} else {
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "可用余额不足，支付失败");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}
		return mv;
	}
	
	/**
	 * 订单支付结果
	 * @param request
	 * @param response
	 * @param order_id
	 * @return
	 */
	@SecurityMapping(title = "订单支付结果", value = "/order_finish*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_finish" })
	public ModelAndView order_finish(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new RedPigJModelAndView("order_finish.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		mv.addObject("obj", obj);
		mv.addObject("all_price", Double.valueOf(this.orderFormTools
				.query_order_pay_price(obj.getId().toString())));
		return mv;
	}
	
	/**
	 * 地址修改
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "地址修改", value = "/cart_address*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/cart_address" })
	public ModelAndView cart_address(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("cart_address.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> params = RedPigMaps.newParent(-1);
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		params.clear();
		
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Address> addrs = this.addressService.queryPageList(params);
		
		if ((id != null) && (!id.equals(""))) {
			Address obj = this.addressService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (obj != null) {
				if (SecurityUserHolder.getCurrentUser().getId().equals(obj.getUser().getId())) {
					mv.addObject("obj", obj);
				} else {
					mv.addObject("error", Boolean.valueOf(true));
				}
			} else {
				mv.addObject("error", Boolean.valueOf(true));
			}
		}
		mv.addObject("addrs_size", Integer.valueOf(addrs.size()));
		mv.addObject("areas", areas);
		return mv;
	}
	
	/**
	 * 地址保存
	 * @param request
	 * @param response
	 * @param id
	 * @param area_id
	 * @param op_type
	 * @param gcs
	 * @return
	 */
	@SecurityMapping(title = "地址保存", value = "/cart_address_save*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/cart_address_save" })
	public ModelAndView cart_address_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String area_id,
			String op_type, String gcs) {
		WebForm wf = new WebForm();
		Address address = null;
		if (CommUtil.null2String(id).equals("")) {
			address = (Address) WebForm.toPo(request, Address.class);
			address.setAddTime(new Date());
		} else {
			Address obj = this.addressService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			if (obj.getUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				address = (Address) WebForm.toPo(request, obj);
			}
		}
		address.setUser(SecurityUserHolder.getCurrentUser());
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
		address.setArea(area);
		if (CommUtil.null2String(id).equals("")) {
			this.addressService.saveEntity(address);
		} else {
			this.addressService.updateById(address);
		}
		ModelAndView mv = null;
		if (CommUtil.null2String(op_type).equals("address_create")) {
			mv = new RedPigJModelAndView("cart_address_create_result.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("orderBy", "default_val desc,");
			params.put("orderType", "addTime desc");
			
			List<Address> addrs = this.addressService.queryPageList(params);
			
			mv.addObject("addrs", addrs);
		} else {
			mv = new RedPigJModelAndView("cart_address_result.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("addr", address);
		}
		return mv;
	}
	
	/**
	 * 地址新增
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "地址新增", value = "/cart_address_create*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/cart_address_create" })
	public ModelAndView cart_address_create(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("cart_address_create.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> params = RedPigMaps.newParent(-1);
		
		List<Area> areas = this.areaService.queryPageList(params);
		
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Address> addrs = this.addressService.queryPageList(params);
		
		mv.addObject("addrs", addrs);
		mv.addObject("areas", areas);
		return mv;
	}
	
	/**
	 * 设置默认地址
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "设置默认地址", value = "/cart_addr_default*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/cart_addr_default" })
	public void cart_addr_default(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = false;
		if ((id != null) && (!id.equals(""))) {
			Address addr = this.addressService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("id", CommUtil.null2Long(id));
			params.put("default_val", Integer.valueOf(1));
			
			List<Address> addrs = this.addressService.queryPageList(params);
			
			for (Address addr1 : addrs) {
				addr1.setDefault_val(0);
				this.addressService.updateById(addr1);
			}
			if (addr != null) {
				addr.setDefault_val(1);
				this.addressService.updateById(addr);
				ret = true;
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
		ModelAndView mv = new RedPigJModelAndView("cart_delivery.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,5, "addTime", "desc");
		
		if ((deliver_area_id != null) && (!deliver_area_id.equals(""))) {
			Area deliver_area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(deliver_area_id));
			Set<Long> ids = genericIds(deliver_area);
			Map paras = Maps.newHashMap();
			paras.put("ids", ids);
			maps.put("da_area_ids", ids);
			
			mv.addObject("deliver_area_id", deliver_area_id);
		} else if ((addr_id != null) && (!addr_id.equals(""))) {
			Address addr = this.addressService.selectByPrimaryKey(CommUtil.null2Long(addr_id));
			maps.put("da_area_parent_id", addr.getArea().getParent().getId());
			
			mv.addObject("area", addr.getArea().getParent());
		}
		
		maps.put("da_status", 10);
		
		IPageList pList = this.deliveryaddrService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		String url = CommUtil.getURL(request) + "/cart_delivery";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		return mv;
	}
	
	/**
	 * 保存用户发票信息
	 * @param request
	 * @param response
	 * @param invoice
	 * @param invoiceType
	 */
	@SecurityMapping(title = "保存用户发票信息", value = "/invoice_save*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/invoice_save" })
	public void invoice_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String invoice, String invoiceType) {
		boolean ret = false;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user.setInvoice(invoice);
		user.setInvoiceType(CommUtil.null2Int(invoiceType));
		this.userService.updateById(user);
		ret = true;
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
	 * 地址切换
	 * @param request
	 * @param response
	 * @param addr_id
	 * @param store_id
	 * @param gcs
	 */
	@SecurityMapping(title = "地址切换", value = "/order_address*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_address" })
	public void order_address(HttpServletRequest request,
			HttpServletResponse response, String addr_id, String store_id,
			String gcs) {
		String[] gc_cart_ids = gcs.split(",");
		List<GoodsCart> carts = cart_calc(request);
		List<GoodsCart> gc_list = Lists.newArrayList();
		for (GoodsCart gc : carts) {
			if ((store_id != "self") && (!store_id.equals("self"))) {
				if (gc.getGoods().getGoods_type() == 1) {
					if ((gc.getGoods().getGoods_store().getId().equals(CommUtil
							.null2Long(store_id))) && (gc_cart_ids.length >= 1)) {
						for (int i = 0; i < gc_cart_ids.length; i++) {
							if (CommUtil.null2String(gc.getId()).equals(
									gc_cart_ids[i])) {
								gc_list.add(gc);
								break;
							}
						}
					}
				}
			} else if (gc.getGoods().getGoods_type() == 0) {
				gc_list.add(gc);
			}
		}
		Address addr = this.addressService.selectByPrimaryKey(CommUtil
				.null2Long(addr_id));
		Object sms = this.transportTools.query_cart_trans(gc_list, null, null,
				CommUtil.null2String(addr.getArea().getId()));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(sms));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 地址切换，检查库存
	 * @param request
	 * @param response
	 * @param addr_id
	 * @param gcs
	 */
	@SecurityMapping(title = "地址切换，检查库存", value = "/order_inventory*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/order_inventory" })
	public void order_inventory(HttpServletRequest request,
			HttpServletResponse response, String addr_id, String gcs) {
		List jsonlist = Lists.newArrayList();
		if ((gcs != null) && (!gcs.equals(""))) {
			Area area = null;
			if ((addr_id != null) && (!addr_id.equals(""))) {
				Address addr = this.addressService.selectByPrimaryKey(CommUtil
						.null2Long(addr_id));
				area = addr.getArea().getParent();
			} else {
				Long area_id = CommUtil.null2Long(request.getSession(false)
						.getAttribute("default_area_id"));
				if (area_id == null || area_id == -1) {
					area_id = 4521986L;// 默认给个北京市的
				}
				area = this.areaService.selectByPrimaryKey(area_id);

				area = area.getParent();
			}
			Map<String, Object> params = Maps.newHashMap();
			
			params.put("sh_area_like", "\"id\":" + area.getId());
			
			for (String id : CommUtil.null2String(gcs).split(",")) {
				
				GoodsCart gc = this.goodsCartService.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((CommUtil.null2Int(gc.getGoods().getGoods_type()) == 0)
						&& (gc.getGoods().getGoods_choice_type() == 0)) {
					
						params.clear();
						
						params.put("goods_id", gc.getGoods().getId());
						String spec = "";
						if (gc.getCart_gsp() != null) {
							String[] sp = gc.getCart_gsp().split(",");
							Arrays.sort(sp);
							for (String s : sp) {

								spec = spec + s + ",";
							}
						}
						if (spec.length() > 0) {
							spec = spec.substring(0, spec.length() - 1);
						}
						List<Inventory> list = Lists.newArrayList();
						if (gc.getGoods().getInventory_type().equals("all")) {
							
							list = this.inventoryService.queryPageList(params);
							
						} else {
							params.put("spec_id", spec);
							list = this.inventoryService.queryPageList(params);
						}
						Map jsonmap = Maps.newHashMap();
						jsonmap.put("goods_id", gc.getId());
						if (list.size() > 0) {
							if (CommUtil.null2Int(list.get(0)) >= gc.getCount()) {
								jsonmap.put("count", list.get(0));
							}
						}
						jsonmap.put("count", gc.getGoods().getGoods_inventory());
						jsonlist.add(jsonmap);
				} else {
					Map jsonmap = Maps.newHashMap();
					jsonmap.put("goods_id", gc.getId());
					Goods goods = gc.getGoods();
					int inventory = 0;
					if ((goods.getGroup() != null)
							&& (goods.getGroup_buy() == 2)) {
						for (GroupGoods gg : goods.getGroup_goods_list()) {
							if (gg.getGroup().getId()
									.equals(goods.getGroup().getId())) {
								inventory = gg.getGg_count();
							}
						}
					} else {
						inventory = goods.getGoods_inventory();
						if (gc.getGoods().getInventory_type().equals("all")) {
							inventory = gc.getGoods().getGoods_inventory();
						} else {
							inventory = CommUtil.null2Int(this.cartTools
									.getGoodsDefaultInfo(request,
											gc.getGoods(), gc.getCart_gsp())
									.get("count"));
						}
					}
					if (inventory >= gc.getCount()) {
						jsonmap.put("count", Integer.valueOf(inventory));
					} else {
						jsonmap.put("count", Integer.valueOf(0));
					}
					jsonlist.add(jsonmap);
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(jsonlist));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * F码购物第一步
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param gsp
	 * @return
	 */
	@SecurityMapping(title = "F码购物第一步", value = "/f_code_cart*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
	@RequestMapping({ "/f_code_cart" })
	public ModelAndView f_code_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String gsp) {
		ModelAndView mv = new RedPigJModelAndView("f_code_cart.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		if (goods != null) {
			if (goods.getF_sale_type() == 0) {
				mv = new RedPigJModelAndView("error.html",
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
						GoodsSpecProperty spec_property = this.goodsSpecPropertyService.selectByPrimaryKey(CommUtil.null2Long(gsp_id));
						boolean add = false;
						for (GoodsSpecProperty temp_gsp : goods.getGoods_specs()) {
							if ((temp_gsp != null) && (spec_property != null)) {
								if (temp_gsp.getId().equals(spec_property.getId())) {
									add = true;
								}
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
						mv = new RedPigJModelAndView("error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "规格参数错误");
						mv.addObject("url", CommUtil.getURL(request) + "/index");
					}
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "商品默认规格无库存，请选择其他规格购买");
					mv.addObject("url", CommUtil.getURL(request) + "/items_"
							+ goods.getId() + "");
				}
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "商品参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * F码验证
	 * @param request
	 * @param response
	 * @param f_code
	 * @param goods_id
	 */
	@SecurityMapping(title = "F码验证", value = "/f_code_validate*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
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
	 * F码完成验证进入订单提交
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param f_code
	 * @param gsp
	 */
	@SecurityMapping(title = "F码完成验证进入订单提交", value = "/f_code_validate*", rtype = "buyer", rname = "购物流程3", rcode = "goods_cart", rgroup = "在线购物")
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
				obj.setSpec_info(spec_info);
				obj.setCart_gsp(gsp);
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

	@RequestMapping({ "/buyer/verify_pay_password" })
	public void verify_pay_password(HttpServletResponse response,
			HttpServletRequest request, String pay_password) {
		String ret = "ok";
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (!user.getPay_password().equals(Md5Encrypt.md5(pay_password))) {
			ret = "false";
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

	private StoreHouse getStoreHouse(String addr_id) {
		StoreHouse sh = new StoreHouse();
		Address addr = this.addressService.selectByPrimaryKey(CommUtil
				.null2Long(addr_id));
		Map<String, Object> params = Maps.newHashMap();
		params.put("sh_area", "\"id\":" + addr.getArea().getParent().getId());
		
		List<StoreHouse> storeHouses = this.storeHouseService.queryPageList(params, 0, 1);
		
		sh = (StoreHouse) storeHouses.get(0);
		return sh;
	}
}
