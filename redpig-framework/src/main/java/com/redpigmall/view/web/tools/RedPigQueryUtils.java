package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.StringUtils;

/**
 * 
 * <p>
 * Title: RedPigQueryTools.java
 * </p>
 * 
 * <p>
 * Description: 商品查询语句工具类
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
 * @date 2016-10-29
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigQueryUtils {

	public void queryActivityGoods(Map<String, Object> maps,
			List<String> str_list) {
		String temp_str = "";
		String[] status_list = { "goods_status", "activity_status",
				"group_buy", "combin_status", "order_enough_give_status",
				"enough_reduce", "f_sale_type", "advance_sale_type",
				"order_enough_if_give", "goods_limit" };
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_status", Integer.valueOf(0));
		params.put("activity_status", Integer.valueOf(2));
		params.put("group_buy", Integer.valueOf(2));
		params.put("combin_status", Integer.valueOf(1));
		params.put("order_enough_give_status", Integer.valueOf(1));
		params.put("enough_reduce", Integer.valueOf(1));
		params.put("f_sale_type", Integer.valueOf(1));
		params.put("advance_sale_type", Integer.valueOf(1));
		params.put("order_enough_if_give", Integer.valueOf(1));
		params.put("goods_limit", Integer.valueOf(1));
		if (str_list != null) {
			for (String str : str_list) {
				if (!"".equals(str)) {
					temp_str = temp_str + "," + str;
				}
			}
		}

		for (String status : status_list) {
			if (temp_str.indexOf("," + status) >= 0) {
				maps.put(status, CommUtil.null2Int(params.get(status)));

			}
		}
	}

	/**
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	public Map<String, Object> getParams(String currentPage, String orderBy,
			String orderType) {
		int cp = CommUtil.null2Int(currentPage);
		
		Map<String, Object> params = Maps.newHashMap();
		// 这个src_currentPage需要返回到前台,让前台显示页码用的
		int src_currentPage = cp;
		
		if (src_currentPage == 0) {
			src_currentPage = 1;
		}
		
		params.put("src_currentPage", src_currentPage);
		params.put("currentPage", (src_currentPage - 1) * 12);
		params.put("pageSize", 12);
		
		params.put("orderBy", orderBy);
		params.put("orderType", orderType);
		
		if (StringUtils.isBlank(orderBy) || StringUtils.isBlank(orderType)) {
			params.put("orderBy", "obj.addTime");
			params.put("orderType", "desc");
		}
		
		return params;
	}

	/**
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	public Map<String, Object> getParams(String currentPage, Integer pageSize,
			String orderBy, String orderType) {
		int cp = CommUtil.null2Int(currentPage);

		Map<String, Object> params = Maps.newHashMap();
		// 这个src_currentPage需要返回到前台,让前台显示页码用的
		int src_currentPage = cp;

		if (src_currentPage == 0) {
			src_currentPage = 1;
		}

		if (pageSize == null) {
			pageSize = 12;
		}

		params.put("src_currentPage", src_currentPage);
		params.put("currentPage", (src_currentPage - 1) * pageSize);
		params.put("pageSize", pageSize);

		params.put("orderBy", orderBy);
		params.put("orderType", orderType);

		if (StringUtils.isBlank(orderBy) || StringUtils.isBlank(orderType)) {
			params.put("orderBy", "addTime");
			params.put("orderType", "asc");
		}

		return params;
	}



	/**
	 * 屏蔽商品多种活动状态，并且查询出的商品为正常上架商品(满就赠中的赠品状态目前没有屏蔽)
	 * str_list:无需屏蔽的商品状态，当没有需要屏蔽的状态时传入null即可,买就送赠品不可参加活动
	 * 
	 * @return
	 */
	public void shieldGoodsStatus(Map<String, Object> params,
			List<String> str_list) {
		String temp_str = "";
		String[] status_list = { "goods_status", "activity_status",
				"group_buy", "combin_status", "order_enough_give_status",
				"enough_reduce", "f_sale_type", "advance_sale_type",
				"order_enough_if_give", "whether_free", "goods_limit" };
		if (str_list != null) {
			for (String str : str_list) {
				if (!"".equals(str)) {
					temp_str = temp_str + "," + str;
				}
			}
		}
		for (String status : status_list) {
			if (temp_str.indexOf("," + status) < 0) {
				params.put(status, 0);
			}
		}
	}

	public void shildGoodsStatusParams(Map<String, Object> params) {
		params.put("goods_status", 0);
		params.put("activity_status", 0);
		params.put("group_buy", 0);
		params.put("combin_status", 0);
		params.put("order_enough_give_status", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("order_enough_if_give", 0);
		params.put("whether_free", 0);
		params.put("goods_limit", 0);
	}

}
