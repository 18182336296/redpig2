package com.redpigmall.manage.seller.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
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
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GoodsLog;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigStoreStatSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家中心店铺统计控制器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * 
 * @date 2014-12-30
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigStoreStatSellerAction extends BaseAction{
	/**
	 * 店铺统计
	 * @param request
	 * @param response
	 * @return
	 */
	
	@SecurityMapping(title = "店铺统计", value = "/stat_store*", rtype = "seller", rname = "店铺统计", rcode = "seller_stat_store", rgroup = "店铺统计")
	@RequestMapping({ "/stat_store" })
	public ModelAndView stat_store(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/stat_store.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Date end = CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime()));
		cal.add(5, -30);
		Date begin = CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime()));
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", user.getStore().getId());
		
		params.put("addTime_more_than_equal", CommUtil.formatShortDate(begin));
		params.put("addTime_less_than_equal", CommUtil.formatShortDate(end));
		
		List<GoodsLog> logs = this.goodsLogService.queryPageList(params);
		
		int goods_collectAll = 0;
		int goods_salenumAll = 0;
		int orderFormAllCount = 0;
		Iterator<String> it = null;

		Map<String, Integer> orderTypeMap = Maps.newHashMap();
		Map<String, Integer> goods_clickfrom = Maps.newHashMap();
		String from;

		for (GoodsLog gl : logs) {
			goods_collectAll += gl.getGoods_collect();
			goods_salenumAll += gl.getGoods_salenum();

			String jsonStr = gl.getGoods_order_type();
			if ((jsonStr != null) && (!jsonStr.equals(""))) {
				Map<String, Integer> fromMap = (Map) JSON.parseObject(jsonStr,
						Map.class);
				it = fromMap.keySet().iterator();
				while (it.hasNext()) {
					String key = ((String) it.next()).toString();
					from = "PC网页";
					if (key.equals("weixin")) {
						from = "手机网页";
					}
					if (key.equals("android")) {
						from = "Android客户端";
					}
					if (key.equals("ios")) {
						from = "iOS客户端";
					}
					if (orderTypeMap.containsKey(from)) {
						orderTypeMap.put(from, Integer
								.valueOf(((Integer) orderTypeMap.get(from))
										.intValue()
										+ ((Integer) fromMap.get(key))
												.intValue()));
					} else {
						orderTypeMap.put(from, (Integer) fromMap.get(key));
					}
				}
			}
			jsonStr = gl.getGoods_click_from();
			if ((jsonStr == null) || (jsonStr.equals(""))) {
				break;
			}
			Map<String, Integer> clickmap = (Map) JSON.parseObject(jsonStr,
					Map.class);
			it = clickmap.keySet().iterator();
			String key = ((String) it.next()).toString();
			if (goods_clickfrom.containsKey(key)) {
				goods_clickfrom.put(key, Integer
						.valueOf(((Integer) goods_clickfrom.get(key))
								.intValue()
								+ ((Integer) clickmap.get(key)).intValue()));
			} else {
				goods_clickfrom.put(key, (Integer) clickmap.get(key));
			}
		}
		mv.addObject("goods_collectAll", Integer.valueOf(goods_collectAll));
		mv.addObject("goods_salenumAll", Integer.valueOf(goods_salenumAll));

		it = orderTypeMap.keySet().iterator();
		StringBuilder orderTypesb = new StringBuilder();
		while (it.hasNext()) {
			String str = (String) it.next();
			orderTypesb.append("['").append(str).append("',")
					.append(orderTypeMap.get(str)).append("],");
		}
		if (orderTypesb.length() == 0) {
			orderTypesb = orderTypesb.append("['暂无数据',1]");
		}
		mv.addObject("orderType", orderTypesb);

		it = goods_clickfrom.keySet().iterator();
		StringBuilder clickFromsb = new StringBuilder();
		while (it.hasNext()) {
			String string = (String) it.next();
			clickFromsb.append("['").append(string).append("',")
					.append(goods_clickfrom.get(string)).append("],");
		}
		if (clickFromsb.length() == 0) {
			clickFromsb = clickFromsb.append("['暂无数据',1]");
		}
		
		mv.addObject("clickFrom", clickFromsb);
		params.put("group_by", "goods_name");
		
		params.put("orderBy", "goods_click");
		params.put("orderType", "desc");
		params.put("goods_click", "goods_click");
		
		List<Map<String,Object>> goods_clickRanks = this.goodsLogService.queryByGroup(params,0, 10);
		
		List click_desc_List = Lists.newArrayList();
		for (Map<String,Object> obj : goods_clickRanks) {
			Map goods_clickMap = Maps.newHashMap();
			goods_clickMap.put("goods_name", obj.get("goods_name"));
			goods_clickMap.put("click_count", obj.get("goods_click"));
			click_desc_List.add(goods_clickMap);
		}
		mv.addObject("click_desc_List", click_desc_List);
		List click_asc_List = new ArrayList(click_desc_List);
		Collections.reverse(click_asc_List);
		mv.addObject("click_asc_List", click_asc_List);
		
		List<Map<String,Object>> goods_collectRanks = this.goodsLogService.queryByGroup(params,0, 10);
		
		List collect_desc_List = Lists.newArrayList();
		for (Map<String,Object> obj : goods_clickRanks) {
			Map goods_collectMap = Maps.newHashMap();
			goods_collectMap.put("goods_name", obj.get("goods_name"));
			goods_collectMap.put("collect_count", obj.get("goods_click"));
			collect_desc_List.add(goods_collectMap);
		}
		mv.addObject("collect_desc_List", collect_desc_List);
		List collect_asc_List = new ArrayList(collect_desc_List);
		Collections.reverse(collect_asc_List);
		mv.addObject("collect_asc_List", collect_asc_List);
		
		params.put("orderBy", "goods_salenum");
		params.put("goods_salenum", "goods_salenum");
		params.remove("goods_click");
		
		List<Map<String,Object>> goods_salenumRanks = this.goodsLogService.queryByGroup(params,0, 10);
		
		List salenum_desc_List = Lists.newArrayList();
		for (Map<String,Object> obj : goods_salenumRanks) {
			Map goods_salenumMap = Maps.newHashMap();
			goods_salenumMap.put("goods_name", obj.get("goods_name"));
			goods_salenumMap.put("sale_num", obj.get("goods_salenum"));
			salenum_desc_List.add(goods_salenumMap);
		}
		mv.addObject("salenum_desc_List", salenum_desc_List);
		List salenum_asc_List = new ArrayList(salenum_desc_List);
		Collections.reverse(salenum_asc_List);
		mv.addObject("salenum_asc_List", salenum_asc_List);
		
		params.clear();
		params.put("order_form", Integer.valueOf(0));
		params.put("store_id", user.getStore().getId().toString());
		params.put("finishTime_more_than_equal", begin);
		params.put("finishTime_less_than_equal", end);
		params.put("order_status_more_than_equal", Integer.valueOf(40));
		params.put("totalSumPrice", "totalSumPrice");
		params.put("orderBy", "totalPrice");
		params.put("orderType", "desc");
		
		List<Map<String,Object>> of_totalPriceTemp = this.orderFormService.querySum(params,0, 3);
		
		mv.addObject("of_totalPrice", of_totalPriceTemp.get(0) == null ?0:of_totalPriceTemp.get(0).get("totalPrice"));
		
		params.clear();
		params.put("type", Integer.valueOf(1));
		params.put("store_id", user.getStore().getId());
		params.put("finishTime_more_than_equal", begin);
		params.put("finishTime_less_than_equal", end);
		
		int storeFavCount = this.favoriteService.selectCount(params);
		
		mv.addObject("storeFavCount", Integer.valueOf(storeFavCount));
		
		params.clear();
		params.put("order_form", Integer.valueOf(0));
		params.put("store_id", user.getStore().getId().toString());
		StringBuilder formTimeData = new StringBuilder();
		Object addFormCountTemp = null;
		List<Object> addFormCount = null;
		StringBuilder addFormCountData = new StringBuilder();
		Object payFormCountTemp = null;
		List<Object> payFormCount = null;
		StringBuilder payFormCountData = new StringBuilder();
		Object finishFormCountTemp = null;
		List<Object> finishFormCount = null;
		StringBuilder finishFormCountData = new StringBuilder();
		while (begin.before(end)) {
			formTimeData.append("'").append(CommUtil.formatTime("MM-dd", begin)).append("',");
			params.put("add_Time_queryTime", CommUtil.formatTime("yyyy-MM-dd", begin));
			
			addFormCountTemp = this.orderFormService.selectCount(params);
			
			addFormCountData.append(addFormCountTemp).append(",");
			
			payFormCountTemp = this.orderFormService.selectCount(params);
			
			payFormCountData.append(payFormCountTemp).append(",");
			
			finishFormCountTemp = this.orderFormService.selectCount(params);
			
			finishFormCountData.append(finishFormCountTemp).append(",");
			orderFormAllCount += CommUtil.null2Int(finishFormCountTemp);
			cal.setTime(begin);
			cal.add(5, 1);
			begin = cal.getTime();
		}
		mv.addObject("addFormCountData", addFormCountData);
		mv.addObject("payFormCountData", payFormCountData);
		mv.addObject("finishFormCountData", finishFormCountData);
		mv.addObject("formTimeData", formTimeData);
		mv.addObject("orderFormAllCount", Integer.valueOf(orderFormAllCount));
		return mv;
	}
	
	/**
	 * 地域分布
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "地域分布", value = "/stat_formArea*", rtype = "seller", rname = "店铺统计", rcode = "seller_stat_store", rgroup = "店铺统计")
	@RequestMapping({ "/stat_formArea" })
	public ModelAndView stat_formArea(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/stat_formArea.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Date end = CommUtil.formatDate(CommUtil.formatShortDate(cal.getTime()));
		cal.add(5, -30);
		Date begin = CommUtil
				.formatDate(CommUtil.formatShortDate(cal.getTime()));
		
		StringBuilder lineDistrictData = new StringBuilder();
		StringBuilder lineCountData = new StringBuilder();
		StringBuilder mapCityData = new StringBuilder("{");
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("order_form", Integer.valueOf(0));
		params.put("store_id", user.getStore().getId().toString());
		params.put("beginDate", begin);
		params.put("endDate", end);
		params.put("order_status", Integer.valueOf(40));
		
		List<Map<String,Object>> formAreaDatas = this.orderFormService.countByArea(params);
		
//				.query("select substring(receiver_area,1,2),count(*) from OrderForm obj where obj.order_form =:order_form and obj.store_id=:store_id and obj.order_status>=:order_status and obj.finishTime>=:beginDate and obj.finishTime<:endDate group by substring(receiver_area,1,2) order by count(*) desc",
//		
//				params, -1, -1);
		
		List<Map> addAreaData = Lists.newArrayList();
		for (int i = 0; i < formAreaDatas.size(); i++) {
			if (i < 6) {
				Map<String,Object> formAreaData = formAreaDatas.get(i);
				
				Set<String> keys = formAreaData.keySet();
				
				List<String> ks = Lists.newArrayList(keys);
				
				for (int j = 0;j<ks.size();j++) {
					if (j == 0) {
						if (formAreaData.get(ks.get(j)).equals("黑龙")) {
							lineDistrictData.append("'").append("黑龙江").append("',");
						} else {
							lineDistrictData.append("'").append(formAreaData.get(ks.get(j))).append("',");
						}
						mapCityData.append("'").append(formAreaData.get(ks.get(j)))
								.append("':'").append(getDistrictColor(i))
								.append("',");
					} else {
						lineCountData.append(formAreaData.get(ks.get(j))).append(",");
					}
				}
				
			}
			Map<String, String> map = Maps.newHashMap();
			Map<String,Object> formAreaData = formAreaDatas.get(i);
			String str = CommUtil.null2String(formAreaData.get("receiver_area"));
			str = str.equals("黑龙") ? "黑龙江" : str;
			map.put("name", str);
			map.put("count", CommUtil.null2String(formAreaData.get("count")));
			addAreaData.add(map);
		}
		mv.addObject("addAreaData", addAreaData);
		mv.addObject("lineCityData", lineDistrictData);
		mv.addObject("lineCountData", lineCountData);
		mv.addObject("mapCityData", mapCityData.append("}"));
		return mv;
	}

	public String getDistrictColor(int i) {
		switch (i) {
		case 0:
		case 1:
			return "#f00";
		case 2:
		case 3:
			return "#9aff04";
		case 4:
		case 5:
			return "#fbb688";
		}
		return "#BBB";
	}
}
