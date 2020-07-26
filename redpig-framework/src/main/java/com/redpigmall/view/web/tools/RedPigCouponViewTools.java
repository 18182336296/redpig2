package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.Goods;
import com.redpigmall.service.RedPigCouponInfoService;
import com.redpigmall.service.RedPigCouponService;
import com.redpigmall.service.RedPigGoodsService;

/**
 * 
 * <p>
 * Title: RedPigCouponViewTools.java
 * </p>
 * 
 * <p>
 * Description: 优惠券工具类
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
 * @date 2014-11-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({"rawtypes"})
@Component
public class RedPigCouponViewTools {
	@Autowired
	private RedPigIntegralViewTools tools;
	@Autowired
	private RedPigCouponService couponService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigCouponInfoService couponInfoService;


	public Boolean isUsableCoupon(Long goods_id, Long user_id) {
		Map level_map = this.tools.query_user_level(CommUtil
				.null2String(user_id));
		Goods goods = this.goodsService.selectByPrimaryKey(goods_id);
		if ((level_map != null) && (goods != null)) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("coupon_limit_less_than_equal",Integer.valueOf(CommUtil.null2Int(level_map.get("level"))));
			map.put("status", Integer.valueOf(0));
			List coupons = null;
			if (goods.getGoods_type() == 0) {
				map.put("coupon_type", Integer.valueOf(0));
				
				coupons = this.couponService.queryPageList(map);
				
			} else {
				map.put("store_id", goods.getGoods_store().getId());
				coupons = this.couponService.queryPageList(map);
				
			}
			if (coupons.size() > 0) {
				return Boolean.valueOf(true);
			}
			return Boolean.valueOf(false);
		}
		return Boolean.valueOf(false);
	}

	public List<Coupon> getUsableCoupon(Long goods_id, Long user_id) {
		Map level_map = this.tools.query_user_level(String.valueOf(user_id));
		List<Coupon> coupons = null;
		if (level_map != null) {
			Goods goods = this.goodsService.selectByPrimaryKey(goods_id);
			Map<String, Object> map = Maps.newHashMap();
			map.put("coupon_limit_less_than_equal",Integer.valueOf(CommUtil.null2Int(level_map.get("level"))));
			map.put("status", Integer.valueOf(0));
			if (goods.getGoods_type() == 0) {
				map.put("coupon_type", Integer.valueOf(0));
				coupons = this.couponService.queryPageList(map);
				
			} else {
				map.put("store_id", goods.getGoods_store().getId());
				coupons = this.couponService.queryPageList(map);
				
			}
		}
		return coupons;
	}

	public boolean isStoreUsableCoupon(Long store_id, Long user_id) {
		Map level_map = this.tools.query_user_level(String.valueOf(user_id));
		List coupons = Lists.newArrayList();
		if (level_map != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("coupon_limit_less_than_equal",Integer.valueOf(CommUtil.null2Int(level_map.get("level"))));
			map.put("status", Integer.valueOf(0));
			map.put("store_id", store_id);
			
			coupons = this.couponService.queryPageList(map);
			
		}
		return coupons.size() > 0;
	}

	public List<Coupon> getStoreUsableCoupon(Long store_id, Long user_id) {
		Map level_map = this.tools.query_user_level(String.valueOf(user_id));
		if (level_map != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("coupon_limit_less_than_equal",
					Integer.valueOf(CommUtil.null2Int(level_map.get("level"))));
			map.put("status", Integer.valueOf(0));
			map.put("store_id", store_id);
			List<Coupon> coupons = this.couponService.queryPageList(map);
			
			return coupons;
		}
		return Lists.newArrayList();
	}

	public boolean isPossessCoupon(Long coupon_id, Long user_id) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("coupon_id", CommUtil.null2Long(coupon_id));
		map.put("user_id", user_id);
		return this.couponInfoService.selectCount(map) > 0;
	}
}
