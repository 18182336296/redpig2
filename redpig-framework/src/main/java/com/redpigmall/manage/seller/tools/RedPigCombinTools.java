package com.redpigmall.manage.seller.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.CombinPlan;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.Store;
import com.redpigmall.service.RedPigCombinPlanService;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * <p>
 * Title: RedPigCombinTools.java
 * </p>
 * 
 * <p>
 * Description: 组合销售商品信息解析工具类
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
 * @date 2014-10-8
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class RedPigCombinTools {
	@Autowired
	private RedPigCombinPlanService combinplanService;
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigStoreService storeService;
	@Autowired
	private RedPigGoodsCartService goodsCartService;

	/**
	 * 解析组合商品中主商品信息
	 * 
	 * @param plan_id
	 * @return
	 */
	public Map getMainGoodsMap(String plan_id) {
		Map map_temp = Maps.newHashMap();
		CombinPlan obj = this.combinplanService.selectByPrimaryKey(CommUtil
				.null2Long(plan_id));
		map_temp = JSON.parseObject(obj.getMain_goods_info());
		String url = (String) map_temp.get("url");
		
		if(url!=null && url.contains("/items")){
			url = configService.getSysConfig().getIndexUrl() + url.substring(url.indexOf("/items"));
		}
		
		map_temp.put("url", url);
		
		return map_temp;
	}

	/**
	 * 解析组合商品中所有方案信息
	 * 
	 * @param plan_id
	 * @return
	 */
	public List<Map> getCombinGoodsMaps(String plan_id) {
		List<Map> map_temps = Lists.newArrayList();
		CombinPlan obj = this.combinplanService.selectByPrimaryKey(CommUtil
				.null2Long(plan_id));
		map_temps = JSON.parseArray(obj.getCombin_plan_info(), Map.class);
		
		return map_temps;
	}
	
	/**
	 * 替换管理平台URL为PC URL
	 * @param url
	 * @return
	 */
	public String replaceCombinGoodsInfoUrl(String url){
		
		if(url !=null && url.contains("/goods_")){
			url = url.replace("goods_", "items_");
		}
		
		if(url!=null && url.contains("/items")){
			url = configService.getSysConfig().getIndexUrl() + url.substring(url.indexOf("/items"));
		}
		
		
		return url;
	}
	
	/**
	 * 解析方案中商品信息,返回List<Map>
	 * 
	 * @param plan_id
	 * @return
	 */
	public List<Map> getCombinGoodsInfo(Map map) {
		List<Map> map_temps = Lists.newArrayList();
		map_temps = (List) map.get("goods_list");
		return map_temps;
	}

	/**
	 * 解析方案中商品信息,返回List<Map>,返回list长度为5，如果长度不够5，使用null替代
	 * 
	 * @param plan_id
	 * @return
	 */
	public List<Map> getCombinGoodsInfo_list(Map map) {
		List<Map> map_list = Lists.newArrayList();
		List<Map> map_temps = (List) map.get("goods_list");
		int max = this.configService.getSysConfig().getCombin_count();
		if (max < map_temps.size()) {
			max = map_temps.size();
		}
		for (int i = 0; i < max; i++) {
			map_list.add(null);
		}
		for (int i = 0; i < map_temps.size(); i++) {
			map_list.set(i, (Map) map_temps.get(i));
		}
		return map_list;
	}

	/**
	 * 解析方案中商品信息id,返回多个id，以逗号间隔
	 * 
	 * @param plan_id
	 * @return
	 */
	public String getCombinGoodsIds(Map map) {
		String ids = "";
		List<Map> map_temps = (List) map.get("goods_list");
		for (Map map2 : map_temps) {
			ids = ids + "," + CommUtil.null2String(map2.get("id"));
		}
		return ids;
	}

	/**
	 * 解析组合销售所属店铺
	 * 
	 * @param plan_id
	 * @return
	 */
	public String getStoreName(String plan_id) {
		String store_name = "平台自营";
		CombinPlan obj = this.combinplanService.selectByPrimaryKey(CommUtil
				.null2Long(plan_id));
		if (obj.getCombin_form() == 1) {
			Store store = this.storeService.selectByPrimaryKey(obj
					.getStore_id());
			if (store != null) {
				store_name = store.getStore_name();
			}
		}
		return store_name;
	}

	public List<GoodsCart> combin_carts_detail(String id) {
		GoodsCart cart = this.goodsCartService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		List<GoodsCart> gcs = Lists.newArrayList();
		if ((cart != null) && (cart.getCart_type() != null)
				&& (cart.getCart_type().equals("combin"))
				&& (cart.getCombin_main() == 1)) {
			String[] cart_ids = cart.getCombin_suit_ids().split(",");
			for (String cart_id : cart_ids) {
				if ((!cart_id.equals("")) && (!cart_id.equals(id))) {
					GoodsCart other = this.goodsCartService
							.selectByPrimaryKey(CommUtil.null2Long(cart_id));
					if (other != null) {
						gcs.add(other);
					}
				}
			}
		}
		return gcs;
	}
}
