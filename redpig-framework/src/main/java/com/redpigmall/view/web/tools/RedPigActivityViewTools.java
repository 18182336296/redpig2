package com.redpigmall.view.web.tools;

import java.math.BigDecimal;
import java.util.*;

import com.redpigmall.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.tools.RedPigCartTools;
import com.redpigmall.service.RedPigActivityGoodsService;
import com.redpigmall.service.RedPigActivityService;
import com.redpigmall.service.RedPigGoodsService;

/**
 * 
 * <p>
 * Title: RedPigActivityViewTools.java
 * </p>
 * 
 * <p>
 * Description:活动工具
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
 * @date 2014-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Component
public class RedPigActivityViewTools {
	@Autowired
	private RedPigGoodsService goodspService;
	@Autowired
	private RedPigActivityGoodsService actgoodsService;
	@Autowired
	private RedPigIntegralViewTools IntegralViewTools;
	@Autowired
	private RedPigActivityService activityService;
	@Autowired
	private RedPigCartTools cartTools;
	
	
	
	/**
	 * 商品详情页，显示商品的所有活动信息，包括活动商品价格、活动折扣，当前登录用户的用户等级
	 * 
	 * @param goods_id
	 * @param user_id
	 * @return
	 */
	public Map getActivityGoodsInfo(String goods_id, String app_user_id) {
		Goods obj = this.goodspService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		Map<String, Object> map = Maps.newHashMap();
		Double price = Double.valueOf(CommUtil.null2Double(obj.getGoods_current_price()));
		if ((obj != null) && (obj.getActivity_status() == 2)) {
			ActivityGoods actGoods = this.actgoodsService.selectByPrimaryKey(obj.getActivity_goods_id());
			if (actGoods != null) {
				Activity act = actGoods.getAct();
				BigDecimal rate = null;
				String level_name = null;
				List<Map> rate_maps = JSON.parseArray(act.getAc_rebate_json(),Map.class);
				int[] temp_level = new int[rate_maps.size()];
				Map level_map = this.IntegralViewTools.query_user_level(String
						.valueOf(app_user_id));
				level_name = CommUtil.null2String(level_map.get("name"));
				int user_level = CommUtil.null2Int(level_map.get("level"));
				for (int i = 0; i < rate_maps.size(); i++) {
					Map m_rebate = (Map) rate_maps.get(i);
					if (CommUtil.null2Int(m_rebate.get("level")) == user_level) {
						rate = BigDecimal.valueOf(CommUtil.null2Double(m_rebate
								.get("rebate")));
						break;
					}
				}
				if (rate == null) {
					Map target_map = null;
					int heigh_level = 0;
					for (Map temp : rate_maps) {
						if (CommUtil.null2Int(temp.get("level")) >= heigh_level) {
							heigh_level = CommUtil.null2Int(temp.get("level"));
							target_map = temp;
						}
					}
					rate = BigDecimal.valueOf(CommUtil.null2Double(target_map
							.get("rebate")));
				}
				String fina_gsp = "";
				if (obj != null) {
					List<GoodsSpecification> specs = Lists.newArrayList();
					if ("spec".equals(obj.getInventory_type())) {
						for (GoodsSpecProperty gsp : obj.getGoods_specs()) {
							GoodsSpecification spec = gsp.getSpec();
							if (!specs.contains(spec)) {
								specs.add(spec);
							}
						}
						Collections.sort(specs,
								new Comparator<GoodsSpecification>() {
									public int compare(GoodsSpecification gs1,
											GoodsSpecification gs2) {
										return gs1.getSequence()
												- gs2.getSequence();
									}
								});
					}
					for (GoodsSpecification spec : specs) {
						for (GoodsSpecProperty prop : obj.getGoods_specs()) {
							if (prop.getSpec().getId().equals(spec.getId())) {
								fina_gsp = prop.getId() + "," + fina_gsp;
								break;
							}
						}
					}
				}
				if ((app_user_id != null) && (!app_user_id.equals(""))) {
					price = Double.valueOf(CommUtil.null2Double(this.cartTools
							.generic_goods_default_Info(obj, fina_gsp, null,
									app_user_id).get("price")));
				} else {
					price = Double.valueOf(CommUtil.null2Double(this.cartTools
							.getGoodsDefaultInfo(null, obj, fina_gsp).get(
									"price")));
				}
				map.put("rate", CommUtil.formatMoney(Double.valueOf(CommUtil
						.null2Double(rate) * 10.0D)));
				map.put("level_name", level_name);
				map.put("rate_price", CommUtil.formatMoney(price));
			} else {
				map.put("rate", CommUtil.formatMoney(Double.valueOf(CommUtil
						.null2Double(Integer.valueOf(1)) * 10.0D)));
				map.put("rate_price", CommUtil.formatMoney(price));
			}
		}
		return map;
	}

	
	public String getA_Prices(String goods_id) {
		Goods obj = this.goodspService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		List list = Lists.newArrayList();
		if ((obj != null) && (obj.getActivity_status() == 2)) {
			ActivityGoods actGoods = this.actgoodsService.selectByPrimaryKey(obj
					.getActivity_goods_id());
			if (actGoods != null) {
				Activity act = actGoods.getAct();

				List rebate = JSON.parseArray(act.getAc_rebate_json());
				List<Map> level_list = this.IntegralViewTools.query_all_level();
				if (rebate.size() > 0) {
					for (int i = 0; i < rebate.size(); i++) {
						Map m = (Map) rebate.get(i);
						String price = null;
						if (m != null) {
							price = CommUtil.formatMoney(Double
									.valueOf(CommUtil.mul(m.get("rebate"),
											obj.getGoods_current_price())));
						}
						list.add(i, price);
						Collections.sort(list);
					}
				}
			}
		}
		String price = "";
		if (list.size() > 0) {
			price = CommUtil.null2String(list.get(0));
		} else {
			price = CommUtil.null2String(obj.getGoods_current_price());
		}
		return price;
	}

	public List getRebate(String id) {
		List list = Lists.newArrayList();
		if ((id != null) && (!"".equals(id))) {
			Activity act = this.activityService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			List rebate = JSON.parseArray(act.getAc_rebate_json());
			List<Map> level_list = this.IntegralViewTools.query_all_level();
			if (rebate.size() > 0) {
				for (int i = 0; i < rebate.size(); i++) {
					Map m = (Map) rebate.get(i);
					Map<String, Object> map = Maps.newHashMap();
					if (m != null) {
						map.put("rate", m.get("rebate"));
						map.put("size", Integer.valueOf(rebate.size()));
						Map level_map = (Map) level_list.get(0);
						map.put("level", m.get("level"));
						map.put("icon", level_map.get("icon"));
					}
					list.add(i, map);
				}
			}
		}
		return list;
	}

	public BigDecimal getActivityGoodsRebate(String goods_id, String user_id) {
		Goods obj = this.goodspService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		BigDecimal rate = new BigDecimal(1.0D);
		if ((obj != null) && (obj.getActivity_status() == 2)) {
			ActivityGoods actGoods = this.actgoodsService.selectByPrimaryKey(obj.getActivity_goods_id());
			if (actGoods != null) {
				Activity act = actGoods.getAct();
				String level_name = null;
				List<Map> rate_maps = JSON.parseArray(act.getAc_rebate_json(),
						Map.class);
				int[] temp_level = new int[rate_maps.size()];
				Map level_map = this.IntegralViewTools.query_user_level(String
						.valueOf(user_id));
				level_name = CommUtil.null2String(level_map.get("name"));
				int user_level = CommUtil.null2Int(level_map.get("level"));
				for (int i = 0; i < rate_maps.size(); i++) {
					Map m_rebate = (Map) rate_maps.get(i);
					if (CommUtil.null2Int(m_rebate.get("level")) == user_level) {
						rate = BigDecimal.valueOf(CommUtil.null2Double(m_rebate
								.get("rebate")));
						break;
					}
				}
				if (rate == null) {
					Map target_map = null;
					int heigh_level = 0;
					for (Map temp : rate_maps) {
						if (CommUtil.null2Int(temp.get("level")) >= heigh_level) {
							heigh_level = CommUtil.null2Int(temp.get("level"));
							target_map = temp;
						}
					}
					rate = BigDecimal.valueOf(CommUtil.null2Double(target_map
							.get("rebate")));
				}
			}
		}
		return rate;
	}

	//判断秒杀的商品否介于开始日期和结束日期之间
	public boolean isBetweenStartAndEnd(Goods goods){
		Nuke nuke = goods.getNuke();
		if (nuke!=null){
			Date currentDate = new Date();
			if (nuke.getBeginTime().getTime()<=currentDate.getTime()&&currentDate.getTime()<=nuke.getEndTime().getTime()){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

}
