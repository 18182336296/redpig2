package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.Inventory;
import com.redpigmall.domain.StoreHouse;
import com.redpigmall.service.RedPigAreaService;
import com.redpigmall.service.RedPigInventoryService;
import com.redpigmall.service.RedPigStoreHouseService;


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
public class RedPigQueryTools {
	
	@Autowired
	private RedPigInventoryService inventoryService;
	@Autowired
	private RedPigStoreHouseService storehouseService;
	@Autowired
	private RedPigAreaService areaService;
	
	public void queryGoodInventory(String area_id, Map<String,Object> maps,String goods_type) {
		if ((goods_type != null) && (!goods_type.equals(""))) {
			if (goods_type.equals("0")) {
				Map<String, Object> params = Maps.newHashMap();
				Set<Long> ids = Sets.newHashSet();
				
				if ((area_id != null) && (!area_id.equals(""))) {
					Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
					params.put("id", area.getParent().getId());
					params.put("area_name", area.getParent().getAreaName());
				} else {
					params.put("id", CommUtil.null2Long(Integer.valueOf(0)));
					params.put("area_name", "局域网");
				}
				String json = JSON.toJSONString(params);
				ids.clear();
				params.clear();
				params.put("sh_area_like", json);
				
				List<StoreHouse> sh_ids = this.storehouseService.queryPageList(params);
				
				ids.clear();
				if (sh_ids.size() > 0) {
					for (int i = 0; i < sh_ids.size(); i++) {
						ids.add(CommUtil.null2Long(sh_ids.get(i)));
					}
				} else {
					ids.add(CommUtil.null2Long(Integer.valueOf(0)));
				}
				
				if (ids.size() > 0) {
					params.clear();
					
					params.put("count_more_than", Integer.valueOf(0));
					
					List<Inventory> goods_ids = this.inventoryService.queryPageList(params);
					
					ids.clear();
					for (int i = 0; i < goods_ids.size(); i++) {
						ids.add(CommUtil.null2Long(goods_ids.get(i).getId()));
					}
					
					if (ids.size() == 0) {
						ids.add(CommUtil.null2Long(Integer.valueOf(0)));
					}
					
					params.clear();
					
					maps.put("goods_type", Integer.valueOf(0));
					maps.put("ids", ids);
					
					
				}
			}
			if (goods_type.equals("1")) {
				maps.put("goods_type", CommUtil.null2Int(goods_type));
				maps.put("goods_inventory_more_than", Integer.valueOf(0));
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			Set<Long> ids = Sets.newHashSet();
			
			if ((area_id != null) && (!area_id.equals(""))) {
				Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
				
				params.put("id", area.getParent().getId());
				params.put("area_name", area.getParent().getAreaName());
				
			} else {
				params.put("id", CommUtil.null2Long(Integer.valueOf(0)));
				params.put("area_name", "局域网");
			}
			
			String json = JSON.toJSONString(params);
			ids.clear();
			params.clear();
			params.put("sh_area_like", json);
			List<StoreHouse> sh_ids = this.storehouseService.queryPageList(params);
			
			ids.clear();
			if (sh_ids.size() > 0) {
				for (int i = 0; i < sh_ids.size(); i++) {
					ids.add(CommUtil.null2Long(sh_ids.get(i)));
				}
			} else {
				ids.add(CommUtil.null2Long(Integer.valueOf(0)));
			}
			if (ids.size() > 0) {
				params.clear();
				
				params.put("count_more_than", Integer.valueOf(0));
				
				List<Inventory> goods_ids = this.inventoryService.queryPageList(params);
				
				ids.clear();
				for (int i = 0; i < goods_ids.size(); i++) {
					ids.add(CommUtil.null2Long(goods_ids.get(i)));
				}
				if (ids.size() == 0) {
					ids.add(CommUtil.null2Long(Integer.valueOf(0)));
				}
				
				params.clear();
				
				params.put("iventory_ids", ids);
				params.put("goods_type1", Integer.valueOf(0));
				params.put("goods_type2", Integer.valueOf(1));
				params.put("goods_inventory", Integer.valueOf(0));
				maps.put("goods_type_id_goods_inventory", "goods_type_id_goods_inventory");
				
				maps.put("rids", ids);
				maps.put("rgoods_type1", Integer.valueOf(0));
				maps.put("rgoods_type2", Integer.valueOf(1));
				maps.put("rgoods_inventory_more_than", Integer.valueOf(0));
				
			}
		}
	}
	
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
			params.put("orderBy", "addTime");
			params.put("orderType", "asc");
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
	
	/**
	 * 
	 * queryGoodsInventoryByLucene:(这里用一句话描述这个方法的作用). <br/>
	 *
	 * @author redpig
	 * @param luceneParams
	 * @param area_id
	 * @param goods_type
	 * @return
	 * @since JDK 1.8
	 */
	public String queryGoodsInventoryByLucene(String luceneParams,String area_id, String goods_type) {
		if ((goods_type != null) && (!goods_type.equals(""))) {
			if (goods_type.equals("0")) {
				Map<String, Object> params = Maps.newHashMap();
				Set<Long> ids = Sets.newHashSet();
				if ((area_id != null) && (!area_id.equals(""))) {
					Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
					params.put("id", area.getParent().getId());
					params.put("area_name", area.getParent().getAreaName());
				} else {
					params.put("id", CommUtil.null2Long(Integer.valueOf(0)));
					params.put("area_name", "局域网");
				}
				
				String json = JSON.toJSONString(params);
				ids.clear();
				params.clear();
				params.put("sh_area_like", json);
				
				List<StoreHouse> sh_ids = this.storehouseService.queryPageList(params);
				params.clear();
				
				ids.clear();
				if (sh_ids.size() > 0) {
					for (int i = 0; i < sh_ids.size(); i++) {
						ids.add(CommUtil.null2Long(sh_ids.get(i)));
					}
				} else {
					ids.add(CommUtil.null2Long(Integer.valueOf(0)));
				}
				if (ids.size() > 0) {
					params.clear();
					params.put("ids", ids);
					params.put("count_more_than", Integer.valueOf(0));
					
					List<Map<String, Object>> goods_ids = this.inventoryService.getGoodsId(params);
					
					List<Long> ids_list = Lists.newArrayList();
					
					for (int i = 0; i < goods_ids.size(); i++) {
						ids_list.add(CommUtil.null2Long(goods_ids.get(i).get("goods_id")));
					}
					
					if (goods_ids.size() == 0) {
						ids_list.add(CommUtil.null2Long(Integer.valueOf(0)));
					}
					
					String start_str = "(";
					for (int j = 0; j < ids_list.size(); j++) {
						if (ids_list.size() == j + 1) {
							start_str = start_str + "(id:" + ids_list.get(j)
									+ "))";
						} else {
							start_str = start_str + "(id:" + ids_list.get(j)
									+ ") " + "OR ";
						}
					}
					
					luceneParams = luceneParams + "AND goods_type:0 AND " + start_str;
				}
			}
			if (goods_type.equals("1")) {
				luceneParams = luceneParams + " AND goods_inventory:[1 TO "
						+ Integer.MAX_VALUE + "] AND goods_type:1";
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			Set<Long> ids = Sets.newHashSet();
			if ((area_id != null) && (!area_id.equals(""))) {
				Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
				
				params.put("id", area.getParent().getId());
				params.put("area_name", area.getParent().getAreaName());
			} else {
				params.put("id", CommUtil.null2Long(Integer.valueOf(0)));
				params.put("area_name", "局域网");
			}
			String json = JSON.toJSONString(params);
			ids.clear();
			params.clear();
			params.put("sh_area_like", json);
			
			List<StoreHouse> sh_ids = this.storehouseService.queryPageList(params);

			ids.clear();
			if (sh_ids.size() > 0) {
				for (int i = 0; i < sh_ids.size(); i++) {
					ids.add(CommUtil.null2Long(sh_ids.get(i)));
				}
			} else {
				ids.add(CommUtil.null2Long(Integer.valueOf(0)));
			}
			if (ids.size() > 0) {
				params.clear();
				params.put("ids", ids);
				params.put("count_more_than", Integer.valueOf(0));
				
				List<Map<String, Object>> goods_ids = this.inventoryService.getGoodsId(params);
				
				List<Long> ids_list = Lists.newArrayList();
				for (int i = 0; i < goods_ids.size(); i++) {
					ids_list.add(CommUtil.null2Long(goods_ids.get(i).get("goods_id")));
				}
				
				if (goods_ids.size() == 0) {
					ids_list.add(CommUtil.null2Long(Integer.valueOf(0)));
				}
				
				String start_str = "(";
				for (int j = 0; j < ids_list.size(); j++) {
					if (ids_list.size() == j + 1) {
						start_str = start_str + "(id:" + ids_list.get(j) + "))";
					} else {
						start_str = start_str + "(id:" + ids_list.get(j) + ") "
								+ "OR ";
					}
				}
				luceneParams =

				luceneParams + "AND ((goods_type:1 AND　goods_inventory:[1 TO "
						+ Integer.MAX_VALUE + "]) OR (goods_type:0 AND "
						+ start_str + "))";
			}
		}
		return luceneParams;
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
