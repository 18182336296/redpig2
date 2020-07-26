package com.redpigmall.manage.admin.tools;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.redpigmall.domain.*;
import com.redpigmall.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.view.web.tools.RedPigArticleViewTools;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;
import com.redpigmall.view.web.tools.RedPigIntegralViewTools;

/**
 * 
 * 
 * <p>
 * Title:CartTools.java
 * </p>
 * 
 * <p>
 * Description:订单工具类，用以处理各个店铺的优惠劵信息
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
 * @date 2014年5月12日
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Component
public class RedPigCartTools {
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigCouponInfoService couponInfoService;
	@Autowired
	private RedPigStoreService storeService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigGoodsClassService goodsClassService;
	@Autowired
	private RedPigEnoughReduceService enoughReduceService;
	@Autowired
	private RedPigArticleViewTools activityTools;
	@Autowired
	private RedPigIntegralViewTools integralViewTools;
	@Autowired
	private RedPigActivityGoodsService actgoodsService;
	@Autowired
	private RedPigGoodsViewTools goodsViewTools;
	@Autowired
	private RedPigGoodsCartService goodsCartService;

	@Autowired
	private RedPigStoreHouseService storeHouseService;
	@Autowired
	private RedPigInventoryService inventoryService;
	@Autowired
	private RedPigStoreHouseService storehouseService;
	@Autowired
	private RedPigAreaService areaService;
	@Autowired
	private RedPigAddressService addrService;
	@Autowired
	private RedPigNukeGoodsService nukeGoodsService;

	/**
	 * 查出该用户在店铺（包括自营）所可以使用的优惠券
	 * 
	 * @param store_id
	 * @param total_price
	 * @return
	 */
	public List<CouponInfo> query_coupon(String store_id, String total_price) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		Map<String, Object> params = Maps.newHashMap();
		List<CouponInfo> couponinfos = Lists.newArrayList();
		params.put("coupon_coupon_order_amount_less_than_equal",
				BigDecimal.valueOf(CommUtil.null2Double(total_price)));
		params.put("user_id", user.getId());
		params.put("coupon_coupon_begin_time", new Date());
		params.put("coupon_coupon_end_time", new Date());

		params.put("status", Integer.valueOf(0));
		if (store_id.equals("self")) {
			params.put("coupon_coupon_type", Integer.valueOf(0));
			couponinfos = this.couponInfoService.queryPageList(params);
		} else {
			params.put("coupon_store_id", CommUtil.null2Long(store_id));
			couponinfos = this.couponInfoService.queryPageList(params);
		}
		return couponinfos;
	}

	/**
	 * 手机端优惠券查询
	 * 
	 * @param store_id
	 * @param total_price
	 * @param user_id
	 * @return
	 */
	public List<CouponInfo> mobile_query_coupon(String store_id,
			String total_price, String user_id) {
		User user = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(user_id));
		Map<String, Object> params = Maps.newHashMap();
		List<CouponInfo> couponinfos = Lists.newArrayList();
		params.put("coupon_order_amount",
				BigDecimal.valueOf(CommUtil.null2Double(total_price)));
		params.put("user_id", user.getId());
		params.put("coupon_coupon_begin_time", new Date());
		params.put("coupon_coupon_end_time", new Date());
		params.put("status", Integer.valueOf(0));
		if (store_id.equals("self")) {
			params.put("coupon_coupon_type", Integer.valueOf(0));
			couponinfos = this.couponInfoService.queryPageList(params);
		} else {
			params.put("coupon_store_id", CommUtil.null2Long(store_id));
			couponinfos = this.couponInfoService.queryPageList(params);
		}
		return couponinfos;
	}

	public double getCommissionByCarts(List<GoodsCart> gcs) {
		double commission_price = 0.0D;
		for (GoodsCart gc : gcs) {
			commission_price += getCommissionByCart(gc);
		}
		return commission_price;
	}

	public double getCommissionByCart(GoodsCart gc) {
		double commission_price = 0.0D;
		String commission = "";
		String gc_id = gc.getGoods().getGc().getId().toString();

		GoodsClass gc1 = gc.getGoods().getGc();
		if (gc1.getLevel() == 2) {
			gc1 = gc1.getParent();
			gc_id = gc1.getId().toString();
		}
		Store store = gc.getGoods().getGoods_store();
		if (store != null) {
			String gc_detail_info = store.getGc_detail_info();
			if ((store.getGc_detail_info() != null)
					&& (store.getGc_detail_info() != "")
					&& (!"[]".equals(store.getGc_detail_info()))) {
				List<Map> gc_detail_list = JSON.parseArray(gc_detail_info,Map.class);
				List gc_id_array = Lists.newArrayList();
				List main_id_array = Lists.newArrayList();
				for (int i = 0; i < gc_detail_list.size(); i++) {
					List gc_id_list = (List) ((Map) gc_detail_list.get(i))
							.get("gc_list");
					String main_id = CommUtil.null2String(((Map) gc_detail_list
							.get(i)).get("m_id"));
					for (int j = 0; j < gc_id_list.size(); j++) {
						gc_id_array.add(gc_id_list.get(j).toString());
					}
					main_id_array.add(main_id.toString());
				}
				if (gc_id_array.contains(gc_id.toString())) {
					GoodsClass gc2 = this.goodsClassService
							.selectByPrimaryKey(CommUtil.null2Long(gc_id));
					String m_id = gc2.getParent().getId().toString();
					for (int m = 0; m < gc_detail_list.size(); m++) {
						if (((Map) gc_detail_list.get(m)).get("m_id")
								.toString().equals(m_id)) {
							Map gc_map = (Map) ((Map) gc_detail_list.get(m))
									.get("gc_commission");
							if (gc_map != null) {
								if (gc_map.size() > 0) {
									if (gc_map.get("gc_" + gc_id) != null) {
										commission = gc_map.get("gc_" + gc_id)
												.toString();
									}
								} else {
									commission = "";
								}
								if ("".equals(commission)) {
									commission_price = CommUtil.mul(gc
											.getGoods().getGc()
											.getCommission_rate(), Double
											.valueOf(CommUtil.mul(
													gc.getPrice(),
													Integer.valueOf(gc
															.getCount()))));
									break;
								}
								BigDecimal com = BigDecimal.valueOf(CommUtil
										.null2Double(commission));
								commission_price = CommUtil
										.mul(com,
												Double.valueOf(CommUtil.mul(gc
														.getPrice(), Integer
														.valueOf(gc.getCount()))));

								break;
							}
							commission_price = CommUtil.mul(gc.getGoods()
									.getGc().getCommission_rate(), Double
									.valueOf(CommUtil.mul(gc.getPrice(),
											Integer.valueOf(gc.getCount()))));

							break;
						}
					}
				} else if (gc1.getLevel() == 1) {
					if (gc1.getParent().getId().toString()
							.equals(store.getGc_main_id().toString())) {
						commission_price = CommUtil.mul(store
								.getCommission_rate(), Double.valueOf(CommUtil
								.mul(gc.getPrice(),
										Integer.valueOf(gc.getCount()))));
					} else {
						commission_price = CommUtil.mul(gc.getGoods().getGc()
								.getCommission_rate(), Double.valueOf(CommUtil
								.mul(gc.getPrice(),
										Integer.valueOf(gc.getCount()))));
					}
				} else if (gc1.getId().toString()
						.equals(store.getGc_main_id().toString())) {
					commission_price = CommUtil.mul(
							store.getCommission_rate(),
							Double.valueOf(CommUtil.mul(gc.getPrice(),
									Integer.valueOf(gc.getCount()))));
				} else {
					commission_price = CommUtil.mul(
							gc.getGoods().getGc().getCommission_rate(),
							Double.valueOf(CommUtil.mul(gc.getPrice(),
									Integer.valueOf(gc.getCount()))));
				}
			} else if (gc1.getParent().getId().toString()
					.equals(store.getGc_main_id().toString())) {
				if ((store.getCommission_rate() != null)
						&& (!"".equals(store.getCommission_rate()))) {
					commission_price = CommUtil.mul(
							store.getCommission_rate(),
							Double.valueOf(CommUtil.mul(gc.getPrice(),
									Integer.valueOf(gc.getCount()))));
				} else {
					commission_price = CommUtil.mul(
							gc.getGoods().getGc().getCommission_rate(),
							Double.valueOf(CommUtil.mul(gc.getPrice(),
									Integer.valueOf(gc.getCount()))));
				}
			} else {
				commission_price = CommUtil.mul(
						gc.getGoods().getGc().getCommission_rate(),
						Double.valueOf(CommUtil.mul(gc.getPrice(),
								Integer.valueOf(gc.getCount()))));
			}
		} else {
			commission_price = CommUtil.mul(
					gc.getGoods().getGc().getCommission_rate(),
					Double.valueOf(CommUtil.mul(gc.getPrice(),
							Integer.valueOf(gc.getCount()))));
		}
		return commission_price;
	}

	public BigDecimal getCommissionRateByCart(GoodsCart gc) {
		BigDecimal com = null;
		String commission = "";
		String gc_id = gc.getGoods().getGc().getId().toString();
		GoodsClass gc1 = this.goodsClassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		if (gc1.getLevel() == 2) {
			gc1 = gc1.getParent();
			gc_id = gc1.getId().toString();
		}
		Store store = gc.getGoods().getGoods_store();
		String gc_detail_info = store.getGc_detail_info();
		if (store != null) {
			if ((store.getGc_detail_info() != null)
					&& (store.getGc_detail_info() != "")
					&& (!"[]".equals(store.getGc_detail_info()))) {
				List<Map> gc_detail_list = JSON.parseArray(gc_detail_info,
						Map.class);
				List gc_id_array = Lists.newArrayList();
				List main_id_array = Lists.newArrayList();
				for (int i = 0; i < gc_detail_list.size(); i++) {
					List gc_id_list = (List) ((Map) gc_detail_list.get(i))
							.get("gc_list");
					String main_id = CommUtil.null2String(((Map) gc_detail_list
							.get(i)).get("m_id"));
					for (int j = 0; j < gc_id_list.size(); j++) {
						gc_id_array.add(gc_id_list.get(j).toString());
					}
					main_id_array.add(main_id.toString());
				}
				if (gc_id_array.contains(gc_id.toString())) {
					GoodsClass gc2 = this.goodsClassService
							.selectByPrimaryKey(CommUtil.null2Long(gc_id));
					String m_id = gc2.getParent().getId().toString();
					for (int m = 0; m < gc_detail_list.size(); m++) {
						if (((Map) gc_detail_list.get(m)).get("m_id")
								.toString().equals(m_id)) {
							Map gc_map = (Map) ((Map) gc_detail_list.get(m))
									.get("gc_commission");
							if (gc_map != null) {
								if (gc_map.size() > 0) {
									if (gc_map.get("gc_" + gc_id) != null) {
										commission = gc_map.get("gc_" + gc_id)
												.toString();
									}
								} else {
									commission = "";
								}
								if ("".equals(commission)) {
									com = gc.getGoods().getGc()
											.getCommission_rate();
									break;
								}
								com = BigDecimal.valueOf(CommUtil
										.null2Double(commission));

								break;
							}
							com = gc.getGoods().getGc().getCommission_rate();

							break;
						}
					}
				} else if (gc1.getLevel() == 1) {
					if (gc1.getId().toString()
							.equals(store.getGc_main_id().toString())) {
						com = store.getCommission_rate();
					} else {
						com = gc.getGoods().getGc().getCommission_rate();
					}
				} else if (gc1.getId().toString()
						.equals(store.getGc_main_id().toString())) {
					com = store.getCommission_rate();
				} else {
					com = gc.getGoods().getGc().getCommission_rate();
				}
			} else if (gc1.getParent().getId().toString()
					.equals(store.getGc_main_id().toString())) {
				com = store.getCommission_rate();
			} else {
				com = gc.getGoods().getGc().getCommission_rate();
			}
		} else {
			com = gc.getGoods().getGc().getCommission_rate();
		}
		return com;
	}
	
	/**
	 * 获得购物车中用户勾选需要购买的商品总价格
	 * @param carts
	 * @param gcs
	 * @return
	 */
	public double getPriceByCarts(List<GoodsCart> carts, String gcs) {
		double all_price = 0.0D;
		Map<String, Double> ermap = Maps.newHashMap();
		
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				if ((gc.getCart_type() == null)
						|| (gc.getCart_type().equals(""))
						|| (gc.getCart_type().equals("limit"))) {// 普通商品处理
					
					all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(),gc.getPrice()));
					
				} else if ((gc.getCart_type().equals("combin"))
						&& (gc.getCombin_main() == 1)) {// 组合套装商品处理
					Map<String, Object> map = JSON.parseObject(gc.getCombin_suit_info());
					all_price = CommUtil.add(all_price,map.get("suit_all_price"));
				}
				
				if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
					String er_id = gc.getGoods().getOrder_enough_reduce_id();
					if (ermap.containsKey(er_id)) {
						double last_price = ermap.get(er_id).doubleValue();
						ermap.put(er_id, CommUtil.add(last_price,CommUtil.mul(gc.getCount(),gc.getPrice())));
					} else {
						ermap.put(er_id,CommUtil.mul(gc.getCount(),gc.getPrice()));
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			
			for (GoodsCart gc : carts) {
				
				for (String gc_id : gc_ids) {
					if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
						if ((gc.getCart_type() != null)
								&& (gc.getCart_type().equals("combin"))
								&& (gc.getCombin_main() == 1)) {
							Map<String, Object> map = JSON.parseObject(gc.getCombin_suit_info());
							all_price = CommUtil.add(all_price,map.get("suit_all_price"));
						} else {
							all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(),gc.getPrice()));
						}
						if (gc.getGoods().getEnough_reduce() == 1) {// 是满就减商品，记录金额
							String er_id = gc.getGoods().getOrder_enough_reduce_id();
							if (ermap.containsKey(er_id)) {
								double last_price = ermap.get(er_id);
								ermap.put(er_id, CommUtil.add(last_price,CommUtil.mul(gc.getCount(),gc.getPrice())));
							} else {
								ermap.put(er_id,CommUtil.mul(gc.getCount(),gc.getPrice()));
							}
						}
					}
				}
				
			}
		}
		
		double all_enough_reduce = 0.0D;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService
					.selectByPrimaryKey(CommUtil.null2Long(er_id));
			if ((er.getErstatus() == 10)
					&& (er.getErbegin_time().before(new Date()))) {// 活动通过审核且正在进行
				String erjson = er.getEr_json();
				double er_money = ermap.get(er_id);// 购物车中的此类满减的金额
				Map fromJson = JSON.parseObject(erjson);
				double reduce = 0.0D;
				if (fromJson != null) {
					for (Object enough : fromJson.keySet()) {
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
	
	/**
	 * 查询满就减价格
	 * @param carts
	 * @param gcs
	 * @return
	 */
	public Map getEnoughReducePriceByCarts(List<GoodsCart> carts, String gcs) {
		Map<Long, String> erString = Maps.newHashMap();
		double all_price = 0.0D;
		Map<String, Double> ermap = Maps.newHashMap();
		Map erid_goodsids = Maps.newHashMap();
		Date date = new Date();
		
		List list;
		String gc_id;
		if (CommUtil.null2String(gcs).equals("")) {
			for (GoodsCart gc : carts) {
				all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(),gc.getPrice()));
				if (gc.getGoods().getEnough_reduce() == 1) {
					String er_id = gc.getGoods().getOrder_enough_reduce_id();
					EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
					
					if ((er.getErstatus() == 10)
							&& (er.getErbegin_time().before(date))) {
						if (ermap.containsKey(er_id)) {
							double last_price = ermap.get(er_id);
							ermap.put(er_id, CommUtil.add(last_price,CommUtil.mul(gc.getCount(),gc.getPrice())));
							
							((List) erid_goodsids.get(er_id)).add(gc.getGoods().getId());
						} else {
							ermap.put(er_id,CommUtil.mul(gc.getCount(),gc.getPrice()));
							list = Lists.newArrayList();
							list.add(gc.getGoods().getId());
							erid_goodsids.put(er_id, list);
						}
					}
				}
			}
		} else {
			String[] gc_ids = gcs.split(",");
			int localList1;
			int listCnt;
			for (GoodsCart gc : carts) {
				localList1 = gc_ids.length;
				listCnt = 0;

				gc_id = gc_ids[listCnt];
				if (gc.getId().equals(CommUtil.null2Long(gc_id))) {
					all_price = CommUtil.add(all_price,CommUtil.mul(gc.getCount(),gc.getPrice()));
					if (gc.getGoods().getEnough_reduce() == 1) {
						String er_id = gc.getGoods().getOrder_enough_reduce_id();
						EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
						if ((er.getErstatus() == 10)
								&& (er.getErbegin_time().before(date))) {
							if (ermap.containsKey(er_id)) {
								double last_price = ermap.get(er_id);
								ermap.put(er_id, CommUtil.add(
										last_price,CommUtil.mul(
												gc.getCount(),
												gc.getPrice())));
								((List) erid_goodsids.get(er_id)).add(gc.getGoods().getId());
							} else {
								ermap.put(er_id, CommUtil.mul(gc.getCount(),gc.getPrice()));
								list = Lists.newArrayList();
								list.add(gc.getGoods().getId());
								erid_goodsids.put(er_id, list);
							}
						}
					}
				}
				listCnt++;
			}
		}
		double all_enough_reduce = 0.0D;
		for (String er_id : ermap.keySet()) {
			EnoughReduce er = this.enoughReduceService
					.selectByPrimaryKey(CommUtil.null2Long(er_id));
			String erjson = er.getEr_json();
			double er_money = ermap.get(er_id);
			Map fromJson = JSON.parseObject(erjson);
			double reduce = 0.0D;
			String erstr = "";
			if (fromJson != null) {
				for (Object enough : fromJson.keySet()) {
					if (er_money >= CommUtil.null2Double(enough)) {
						reduce = CommUtil.null2Double(fromJson.get(enough));
						erstr = "活动商品已购满" + enough + "元,已减" + reduce + "元";
						erid_goodsids.put("enouhg_" + er_id, enough);
					}
				}
			}
			erString.put(er.getId(), erstr);
			erid_goodsids.put("all_" + er_id, er_money);
			erid_goodsids.put("reduce_" + er_id, reduce);
			
			all_enough_reduce = CommUtil.add(all_enough_reduce,reduce);
		}
		Map prices = Maps.newHashMap();
		prices.put("er_json", JSON.toJSONString(erid_goodsids));
		prices.put("erString", erString);
		
		double d2 = Math.round(all_price * 100.0D) / 100.0D;
		BigDecimal bd = new BigDecimal(d2);
		BigDecimal bd2 = bd.setScale(2, 4);
		prices.put("all", Double.valueOf(CommUtil.null2Double(bd2)));
		
		double er = Math.round(all_enough_reduce * 100.0D) / 100.0D;
		BigDecimal erbd = new BigDecimal(er);
		BigDecimal erbd2 = erbd.setScale(2, 4);
		prices.put("reduce", Double.valueOf(CommUtil.null2Double(erbd2)));
		
		double af = Math.round((all_price - all_enough_reduce) * 100.0D) / 100.0D;
		BigDecimal afbd = new BigDecimal(af);
		BigDecimal afbd2 = afbd.setScale(2, 4);
		prices.put("after", Double.valueOf(CommUtil.null2Double(afbd2)));
		
		return prices;
	}

	public String getGoodsPriceByGsp(String gsp, String goods_id) {
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
				.null2Long(goods_id));
		double price = CommUtil.null2Double(goods.getGoods_current_price());
		User user = SecurityUserHolder.getCurrentUser();
		String[] gsp_ids;
		if ("spec".equals(goods.getInventory_type())) {
			List<HashMap> list = JSON.parseArray(
					goods.getGoods_inventory_detail(), HashMap.class);
			gsp_ids = gsp.split(",");
			for (Map temp : list) {
				String[] temp_ids = CommUtil.null2String(temp.get("id")).split(
						"_");
				Arrays.sort(gsp_ids);
				Arrays.sort(temp_ids);
				if (Arrays.equals(gsp_ids, temp_ids)) {
					price = CommUtil.null2Double(temp.get("price"));
				}
			}
		}
		if ((user != null) && (goods.getActivity_status() == 2)) {
			BigDecimal rate = this.activityTools.getActivityGoodsRebate(
					CommUtil.null2String(goods.getId()),
					CommUtil.null2String(user.getId()));
			
			price = BigDecimal.valueOf(price).multiply(rate).doubleValue();
		}
		if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					price = CommUtil.null2Double(gg.getGg_price());
					break;
				}
			}
		}
		// 如果是秒杀，价格等于秒杀价格
		if((goods.getNuke() != null) && (goods.getNuke_buy() == 2)){
			Map<String,Object> map = new HashMap<>();
			map.put("ng_goods_id",goods.getId());
			map.put("nuke_id",goods.getNuke().getId());
			String []spcArray = gsp.split(",");
			String goods_spec_id = "";
			for (String str:spcArray){
				goods_spec_id+=(str+"_");
			}
			map.put("goods_spec_id",goods_spec_id);
			List<NukeGoods>nukeGoodsList = this.nukeGoodsService.selectObjByProperty(map);
			if (nukeGoodsList!=null&&nukeGoodsList.size()>0){
				NukeGoods nukeGoods = nukeGoodsList.get(0);
				price = CommUtil.null2Double(nukeGoods.getNg_price());
			}
		}

		// 如果是团购,则保持原价,因为价格已经修改过了
		/*if (goods.getCollage_buy() == 2) {
			price = CommUtil.null2Double(goods.getGoods_current_price());
		}*/
		return CommUtil.null2String(Double.valueOf(price));
	}

	public String getGoodsPriceByGsp(String gsp, String goods_id, String user_id) {
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
				.null2Long(goods_id));
		double price = CommUtil.null2Double(goods.getGoods_current_price());
		User user = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(user_id));
		String[] gsp_ids;
		if ("spec".equals(goods.getInventory_type())) {
			List<HashMap> list = JSON.parseArray(
					goods.getGoods_inventory_detail(), HashMap.class);
			gsp_ids = gsp.split(",");
			for (Map temp : list) {
				String[] temp_ids = CommUtil.null2String(temp.get("id")).split(
						"_");
				Arrays.sort(gsp_ids);
				Arrays.sort(temp_ids);
				if (Arrays.equals(gsp_ids, temp_ids)) {
					price = CommUtil.null2Double(temp.get("price"));
				}
			}
		}
		if ((user != null) && (goods.getActivity_status() == 2)) {
			BigDecimal rate = this.activityTools.getActivityGoodsRebate(
					CommUtil.null2String(goods.getId()),
					CommUtil.null2String(user.getId()));
			price = BigDecimal.valueOf(price).multiply(rate).doubleValue();
		}
		if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					price = CommUtil.null2Double(gg.getGg_price());
					break;
				}
			}
		}
		return CommUtil.null2String(Double.valueOf(price));
	}

	public Map getGoodsDefaultInfo(HttpServletRequest request, Goods goods,
			String gsp) {
		String default_area_id = null;
		if (request != null) {
			default_area_id = CommUtil.null2String(request.getSession(false).getAttribute("default_area_id"));
		}
		Map<String, Object> map = generic_goods_default_Info(goods, gsp,default_area_id, null);
		return map;
	}
	
	/**
	 * 
	 * @param goods
	 * @param gsp
	 * @param area_id
	 * @param app_user_id
	 * @return
	 */
	public Map generic_goods_default_Info(Goods goods, String gsp,String area_id, String app_user_id) {
		double price = 0.0D;
		Map<String, Object> map = Maps.newHashMap();
		int count = 0;
		if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
			for (GroupGoods gg : goods.getGroup_goods_list()) {
				if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
					count = gg.getGg_count();
					price = CommUtil.null2Double(gg.getGg_price());
				}
			}
		} else {
			count = goods.getGoods_inventory();
			price = CommUtil.null2Double(goods.getStore_price());
			if (("spec".equals(goods.getInventory_type())) && (gsp != null) && (!gsp.equals(""))) {
				List<HashMap> list = JSON.parseArray(goods.getGoods_inventory_detail(), HashMap.class);
				String[] gsp_ids = gsp.split(",");
				for (Map temp : list) {
					String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
					Arrays.sort(gsp_ids);
					Arrays.sort(temp_ids);
					if (Arrays.equals(gsp_ids, temp_ids)) {
						count = CommUtil.null2Int(temp.get("count"));
						price = CommUtil.null2Double(temp.get("price"));
						// 如果是秒杀，需要单独设置库存
						if(goods.getNuke_buy()==2&&goods.getNuke()!=null){
							Map<String,Object> nukeGoodsMap = new HashMap<>();
							nukeGoodsMap.put("ng_goods_id",goods.getId());
							nukeGoodsMap.put("nuke_id",goods.getNuke().getId());
							String []spcArray = gsp.split(",");
							String goods_spec_id = "";
							for (String str:spcArray){
								goods_spec_id+=(str+"_");
							}
							nukeGoodsMap.put("goods_spec_id",goods_spec_id);
							List<NukeGoods>nukeGoodsList = this.nukeGoodsService.selectObjByProperty(map);
							// 如果秒杀活动里面有，则设置当前库存为秒杀库存
							if (nukeGoodsList!=null&&nukeGoodsList.size()>0){
								NukeGoods nukeGoods = nukeGoodsList.get(0);
								count = nukeGoods.getNg_count();
							}
						}
					}
				}
			}
		}
		User user = SecurityUserHolder.getCurrentUser();
		if (user == null) {
			user = this.userService.selectByPrimaryKey(CommUtil.null2Long(app_user_id));
		}
		int user_level;
		Activity act;
		List<Map> rate_maps;
		if ((goods.getActivity_status() == 2) && (user != null)) {
			ActivityGoods actGoods = this.actgoodsService.selectByPrimaryKey(goods.getActivity_goods_id());
			BigDecimal rebate = null;
			Map level_map = this.integralViewTools.query_user_level(CommUtil.null2String(user.getId()));
			user_level = CommUtil.null2Int(level_map.get("level"));
			act = actGoods.getAct();
			rate_maps = JSON.parseArray(act.getAc_rebate_json(), Map.class);
			int[] temp_level = new int[rate_maps.size()];
			for (int i = 0; i < rate_maps.size(); i++) {
				Map m_rebate = (Map) rate_maps.get(i);
				if (CommUtil.null2Int(m_rebate.get("level")) == user_level) {
					rebate = BigDecimal.valueOf(CommUtil.null2Double(m_rebate.get("rebate")));
					break;
				}
				temp_level[i] = CommUtil.null2Int(m_rebate.get("level"));
			}
			if (rebate == null) {
				Arrays.sort(temp_level);
				int temp_rate_up = temp_level[(rate_maps.size() - 1)];
				int temp_rate_down = temp_level[0];
				int last_level = 0;
				if (user_level < temp_rate_down) {
					last_level = temp_rate_down;
				}
				if (user_level > temp_rate_up) {
					last_level = temp_rate_up;
				}
				for (Map temp_map : rate_maps) {
					if (CommUtil.null2Int(temp_map.get("level")) == last_level) {
						rebate = BigDecimal.valueOf(CommUtil
								.null2Double(temp_map.get("rebate")));
						break;
					}
				}
			}
			price = CommUtil.mul(rebate, price);
		}
		
		map.put("price", price);
		map.put("count", count);
		return map;
	}
	
	public String getGoodsDefaultGsp(Goods goods) {
		String gsp = "";
		if (goods != null) {
			List<GoodsSpecification> specs = this.goodsViewTools.generic_spec(CommUtil.null2String(goods.getId()));
			for (GoodsSpecification spec : specs) {
				for (GoodsSpecProperty prop : goods.getGoods_specs()) {
					if (prop.getSpec().getId().equals(spec.getId())) {
						gsp = prop.getId() + "," + gsp;
						break;
					}
				}
			}
		}
		return gsp;
	}
	
	public List<GoodsCart> getGoodscartByIds(String cart_ids) {
		List<GoodsCart> carts = Lists.newArrayList();
		if (cart_ids != null) {
			String[] ids = cart_ids.split(",");
			for (String cart_id : ids) {
				if (!cart_id.equals("")) {
					GoodsCart gc = this.goodsCartService
							.selectByPrimaryKey(CommUtil.null2Long(cart_id));
					if (gc != null) {
						carts.add(gc);
					}
				}
			}
		}
		return carts;
	}

	public boolean verify_gcart_Inventory(String cart_id, String area_id) {
		GoodsCart gcart = this.goodsCartService.selectByPrimaryKey(CommUtil
				.null2Long(cart_id));
		Goods obj = gcart.getGoods();
		boolean verify = true;
		if ((obj.getGoods_type() == 0) && (area_id != null)
				&& (!area_id.equals(""))) {
			String order_goods_gsp_ids = gcart.getCart_gsp();

			Area area = this.areaService.selectByPrimaryKey(CommUtil
					.null2Long(area_id));
			if (area != null) {
				Map<String, Object> map = generic_goods_default_Info(obj,
						order_goods_gsp_ids,
						CommUtil.null2String(area.getId()), "");
				int real_goods_count = CommUtil.null2Int(map.get("count"));
				if (real_goods_count <= 0) {
					verify = false;
				}
			}
		}
		return verify;
	}

	public boolean verify_goods_available(List<GoodsCart> gcs) {
		boolean flag = true;
		List<GoodsCart> wrong_gcs = Lists.newArrayList();
		for (GoodsCart gc : gcs) {
			Goods goods = gc.getGoods();
			if (goods.getGoods_type() == 0) {
				if (goods.getGoods_status() != 0) {
					wrong_gcs.add(gc);
				}
			} else if ((goods.getGoods_status() != 0)
					|| (CommUtil.null2Int(goods.getGoods_store()
							.getStore_status()) != 15)) {
				wrong_gcs.add(gc);
			}
		}
		if (wrong_gcs.size() > 0) {
			for (GoodsCart gc : wrong_gcs) {
				gc.getGsps().clear();
				this.goodsCartService.deleteById(gc.getId());
			}
			flag = false;
		}
		return flag;
	}

	public Map handle_limit_cart(Goods goods, String app_user_id) {
		User user = null;
		if (app_user_id != null) {
			user = this.userService.selectByPrimaryKey(CommUtil
					.null2Long(app_user_id));
		} else {
			user = SecurityUserHolder.getCurrentUser();
		}
		Map<String, Object> params = Maps.newHashMap();
		int limit_code = 0;
		int limit_count = goods.getGoods_limit_count();
		GoodsCart limit_goodsCart = null;
		if (user != null) {
			Map cart_map = Maps.newHashMap();
			cart_map.put("user_id", user.getId());
			cart_map.put("goods_id", goods.getId());
			cart_map.put("cart_status", 0);

			List<GoodsCart> gc_list = this.goodsCartService
					.queryPageList(params);

			String info = CommUtil
					.null2String(this.userService.selectByPrimaryKey(
							user.getId()).getBuy_goods_limit_info());
			for (GoodsCart cart : gc_list) {
				if (cart.getGoods().getId().equals(goods.getId())) {
					limit_goodsCart = cart;
					break;
				}
			}
			if (!CommUtil.null2String(info).equals("")) {
				Map maps = JSON.parseObject(CommUtil.null2String(info));
				List<Map> list = (List) maps.get("data");
				for (Map map : list) {
					String gid = CommUtil.null2String(map.get("gid"));
					if (CommUtil.null2Long(gid).equals(goods.getId())) {
						boolean flag = true;

						limit_count = goods.getGoods_limit_count()
								- CommUtil.null2Int(map.get("count"));
						if (limit_count <= 0) {
							limit_code = -1;
							flag = false;
						}
						if ((!flag)
								|| (limit_goodsCart == null)
								|| (limit_count - limit_goodsCart.getCount() >= 0)) {
							break;
						}
						limit_code = -2;
						flag = false;

						break;
					}
				}
			} else if (gc_list.size() > 0) {
				if (limit_goodsCart != null) {
					limit_count = goods.getGoods_limit_count()
							- limit_goodsCart.getCount();
					if (limit_count < 0) {
						limit_code = -4;
					}
				}
			}
		} else {
			limit_code = -3;
		}
		params.put("limit_code", Integer.valueOf(limit_code));
		if (limit_code == 0) {
			params.put("limit_count", Integer.valueOf(limit_count));
		}
		System.out.println("=========params:" + params);
		return params;
	}

	public boolean calcul_limit_count(GoodsCart gc, int goods_count,
			int last_limit_count, String app_user_id) {
		boolean ret = true;
		boolean buyed = false;
		int all_limit_count = gc.getGoods().getGoods_limit_count();
		int buy_count_ready = 0;
		User user = SecurityUserHolder.getCurrentUser();
		if (user == null) {
			user = this.userService.selectByPrimaryKey(CommUtil
					.null2Long(app_user_id));
		}
		if (user.getBuy_goods_limit_info() != null) {
			Map maps = JSON.parseObject(CommUtil.null2String(user
					.getBuy_goods_limit_info()));
			List<Map> list = (List) maps.get("data");
			for (Map map : list) {
				String gid = CommUtil.null2String(map.get("gid"));
				if (CommUtil.null2Long(gid).equals(gc.getGoods().getId())) {
					buy_count_ready = CommUtil.null2Int(map.get("count"));
					buyed = true;
					break;
				}
			}
		}
		if (!buyed) {
			if (gc.getCount() != goods_count) {
				if (goods_count > all_limit_count) {
					gc.setCount(all_limit_count);
					gc.setRemain_count(0);
					ret = false;
				} else {
					gc.setCount(goods_count);
					gc.setRemain_count(all_limit_count - goods_count);
				}
			}
		} else {
			int temp_all_limit_count = all_limit_count - buy_count_ready;
			if (goods_count >= temp_all_limit_count) {
				gc.setCount(temp_all_limit_count);
				gc.setRemain_count(0);
				ret = false;
			} else {
				gc.setCount(goods_count);
				gc.setRemain_count(temp_all_limit_count - goods_count);
			}
		}
		return ret;
	}
}
