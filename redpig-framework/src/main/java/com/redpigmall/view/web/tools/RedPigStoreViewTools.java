package com.redpigmall.view.web.tools;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.service.RedPigCouponService;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigStoreService;
/**
 * 
 * <p>
 * Title: RedPigStoreViewTools.java
 * </p>
 * 
 * <p>
 * Description: 店铺工具类
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
 * @date 2016-4-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigStoreViewTools {
	@Autowired
	private RedPigStoreService storeService;

	@Autowired
	private RedPigEvaluateService evaluateService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigGoodsClassService goodsClassService;

	@Autowired
	private RedPigCouponService couponService;

	public String genericFunction(StoreGrade grade) {
		String fun = "";
		if (grade.getAdd_funciton().equals("")) {
			fun = "无";
		}
		String[] list = grade.getAdd_funciton().split(",");
		for (String s : list) {
			if (s.equals("editor_multimedia")) {
				fun = "富文本编辑器" + fun;
			}
		}
		return fun;
	}

	public String genericImageSuffix(String imageSuffix) {
		String suffix = "";
		String[] list = imageSuffix.split("\\|");
		for (String l : list) {
			suffix = "*." + l + ";" + suffix;
		}
		return suffix.substring(0, suffix.length() - 1);
	}

	public List<Store> query_recommend_store(int count) {
		List<Store> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("recommend", Boolean.valueOf(true));
		list = this.storeService.queryPageList(params,0,count);
		return list;
	}

	public List<Goods> query_recommend_store_goods(Store store, int begin,
			int max) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("recommend", Boolean.valueOf(true));
		params.put("goods_store_id", store.getId());
		params.put("goods_status", Integer.valueOf(0));
		List<Goods> goods = this.goodsService.queryPageList(params,begin,max);
		if (goods.size() < 5) {
			int count = 5 - goods.size();
			for (int i = 0; i < count; i++) {
				goods.add(null);
			}
		}
		return goods;
	}

	@SuppressWarnings({ "unused"})
	public int query_evaluate(String store_id, int evaluate_val, String type,
			String date_symbol, int date_count) {
		Calendar cal = Calendar.getInstance();
		if (type.equals("date")) {
			cal.add(6, date_count);
		}
		if (type.equals("week")) {
			cal.add(3, date_count);
		}
		if (type.equals("month")) {
			cal.add(2, date_count);
		}
		String symbol = ">=";
		if (date_symbol.equals("before")) {
			symbol = "<=";
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", CommUtil.null2Long(store_id));
		params.put("addTime_equal_greater_than", cal.getTime());
		params.put("evaluate_buyer_val", evaluate_val);
		
		List<Evaluate> evas = this.evaluateService.queryPageList(params);
				
		return evas.size();
	}

	public String queryStoreNameById(String store_id) {
		String store_name = "";
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(store_id));
		if (store != null) {
			store_name = store.getStore_name();
		}
		return store_name;
	}

	public String queryStoreQQById(String store_id) {
		String store_qq = "";
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(store_id));
		if (store != null) {
			store_qq = store.getStore_qq();
		}
		return store_qq;
	}

	public BigDecimal queryStore_evaluate(String store_id) {
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(store_id));
		if (store != null) {
			return store.getPoint().getStore_evaluate();
		}
		return new BigDecimal("0");
	}

	@SuppressWarnings({ "rawtypes" })
	public List<GoodsClass> main_class(String store_id) {
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(store_id));
		String gc_detail = store.getGc_detail_info();
		List<GoodsClass> gc_list = Lists.newArrayList();
		gc_list = Lists.newArrayList();
		if (gc_detail != null) {
			List<Map> list = JSON.parseArray(gc_detail, Map.class);
			if ((list.size() > 0) && (list != null) && (list.size() > 0)) {
				for (Map m : list) {
					List m_info = (List) m.get("gc_list");
					if (m_info != null) {
						for (int i = 0; i < m_info.size(); i++) {
							GoodsClass gc = this.goodsClassService
									.selectByPrimaryKey(CommUtil.null2Long(m_info.get(i)));
							gc_list.add(gc);
						}
					}
				}
			}
		} else {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(store
					.getGc_main_id());
			gc_list.add(gc);
		}
		return gc_list;
	}

	public Map<String, Object>  point(Store store) {
		Map<String, Object> map = Maps.newHashMap();
		double description_result = 0.0D;
		double service_result = 0.0D;
		double ship_result = 0.0D;
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("goods_status", 0);
		params.put("goods_store_id", store.getId());
		
		int g_count = this.goodsService.selectCount(params);
		
		map.put("count", g_count);
		
		GoodsClass gc = this.goodsClassService.selectByPrimaryKey(store.getGc_main_id());
		if ((store != null) && (gc != null) && (store.getPoint() != null)) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());

			description_result = CommUtil.div(
					Float.valueOf(store_description_evaluate
							- description_evaluate),
					Float.valueOf(description_evaluate));
			service_result = CommUtil.div(
					Float.valueOf(store_service_evaluate - service_evaluate),
					Float.valueOf(service_evaluate));
			ship_result = CommUtil.div(
					Float.valueOf(store_ship_evaluate - ship_evaluate),
					Float.valueOf(ship_evaluate));
		}
		if (description_result > 0.0D) {
			map.put("description_result",

					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(description_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(description_result),
									Integer.valueOf(100))))
							+ "%");
		}
		if (description_result == 0.0D) {
			map.put("description_result", "-----");
		}
		if (description_result < 0.0D) {
			map.put("description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-description_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result > 0.0D) {
			map.put("service_result", CommUtil.null2String(Double
					.valueOf(CommUtil.mul(Double.valueOf(service_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(service_result),
									Integer.valueOf(100)))));
		}
		if (service_result == 0.0D) {
			map.put("service_result", "-----");
		}
		if (service_result < 0.0D) {
			map.put("service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result > 0.0D) {
			map.put("ship_result",
					CommUtil.null2String(Double
							.valueOf(CommUtil.mul(Double.valueOf(ship_result),
									Integer.valueOf(100)) > 100.0D ? 100.0D
									: CommUtil.mul(Double.valueOf(ship_result),
											Integer.valueOf(100)))));
		}
		if (ship_result == 0.0D) {
			map.put("ship_result", "-----");
		}
		if (ship_result < 0.0D) {
			map.put("ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-ship_result), Integer.valueOf(100))))
							+ "%");
		}
		
		params.clear();
		params.put("goods_status", 0);
		params.put("goods_store_id", store.getId());
		
		List<Goods> s_goods = this.goodsService.queryPageList(params,0,5);
		
		map.put("s_goods", s_goods);
		return map;
	}

	public boolean getStoreCouponStatus(Long store_id) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("status", Integer.valueOf(0));
		map.put("store_id", store_id);
		return  this.couponService.selectCount(map) > 0;
	}
}
