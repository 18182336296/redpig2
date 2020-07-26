package com.redpigmall.module.weixin.view.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Store;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigStoreService;


/**
 * 
 * <p>
 * Title: RedPigWeixinStoreViewTools.java
 * </p>
 * 
 * <p>
 * Description:微信店铺工具
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
 * @date 2014-7-17
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class RedPigWeixinStoreViewTools {

	@Autowired
	private RedPigGoodsService goodsService;

	@Autowired
	private RedPigStoreService storeService;

	public List<Map> getfloor_info(String floor_info) {
		List list = Lists.newArrayList();
		if ((floor_info != null) && (!"".equals(floor_info))) {
			for (String str : floor_info.split(";")) {
				if (str != "") {
					String[] arr = str.split(",");
					Map line_map = Maps.newHashMap();
					line_map.put("click_type", arr[0]);
					if ("goods".equals(arr[0])) {
						line_map.put("goods_id", arr[1]);
					}
					if ("url".equals(arr[0])) {
						line_map.put("url", arr[1]);
					}
					line_map.put("img_src", arr[2]);
					line_map.put("po", arr[3]);
					list.add(line_map);
				}
			}
		}
		return list;
	}

	public List<Map> getgoods(String goods_info) {
		List list = Lists.newArrayList();
		if ((goods_info != null) && (!"".equals(goods_info))) {
			String[] arr = goods_info.split(",");
			for (String st : arr) {
				if ((st != null) && (!"".equals(st))) {
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(st));
					
					if (goods != null) {
						list.add(goods);
					}
				}
			}
		}
		return list;
	}

	public Store getstore(String store_id) {
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(store_id));
		return store;
	}

	public List<Map> getslide(String slide_info) {
		List list = Lists.newArrayList();
		if ((slide_info != null) && (!"".equals(slide_info))) {
			String[] slides = slide_info.split(";");
			for (String str : slides) {
				if (!"".equals(str)) {
					String[] arr = str.split(",");
					Map line_map = Maps.newHashMap();
					line_map.put("click_type", arr[0]);
					if ("goods".equals(arr[0])) {
						line_map.put("goods_id", arr[1]);
					}
					if ("url".equals(arr[0])) {
						line_map.put("url", arr[1]);
					}
					line_map.put("img_src", arr[2]);
					line_map.put("po", arr[3]);
					list.add(line_map);
				}
			}
		}
		return list;
	}
}
