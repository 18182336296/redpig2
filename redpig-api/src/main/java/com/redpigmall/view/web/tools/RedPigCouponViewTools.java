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
 * <p>
 * Title: RedPigCouponViewTools.java
 * </p>
 * 
 * <p>
 * Description:优惠券处理工具
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
 * @author redpig
 * 
 * @date 2017-3-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "rawtypes"})
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
	/**
	 * 判断是否是可用优惠券
	 * @param goods_id
	 * @param user_id
	 * @return
	 */
	public Boolean isUsableCoupon(Long goods_id, Long user_id) {
		Map level_map = this.tools.query_user_level(CommUtil.null2String(user_id));
		Goods goods = this.goodsService.selectByPrimaryKey(goods_id);
		if ((level_map != null) && (goods != null)) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("coupon_limit_less_than_equal",Integer.valueOf(CommUtil.null2Int(level_map.get("level"))));
			map.put("status", 0);
			List<Coupon> coupons = null;
			
			if (goods.getGoods_type() == 0) {
				map.put("coupon_type", 0);
				coupons = this.couponService.queryPageList(map);
			} else {
				map.put("store_id", goods.getGoods_store().getId());
				
				coupons = this.couponService.queryPageList(map);
			}
			if (coupons.size() > 0) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 查询可用优惠券
	 * @param goods_id
	 * @param user_id
	 * @return
	 */
	public List<Coupon> getUsableCoupon(Long goods_id, Long user_id) {
		Map level_map = this.tools.query_user_level(String.valueOf(user_id));
		List<Coupon> coupons = null;
		if (level_map != null) {
			Goods goods = this.goodsService.selectByPrimaryKey(goods_id);
			Map<String, Object> map = Maps.newHashMap();
			map.put("coupon_limit_less_than_equal",CommUtil.null2Int(level_map.get("level")));
			map.put("coupon_end_time_able", "coupon_end_time_more_than");
			map.put("status", 0);
			if (goods.getGoods_type() == 0) {
				map.put("coupon_type", 0);
				
				coupons = this.couponService.queryPageList(map);
				
			} else {
				map.put("store_id", goods.getGoods_store().getId());
				coupons = this.couponService.queryPageList(map);
				
			}
		}
		return coupons;
	}
	
	/**
	 * 判断店铺可用优惠券
	 * @param store_id
	 * @param user_id
	 * @return
	 */
	public boolean isStoreUsableCoupon(Long store_id, Long user_id) {
		Map level_map = this.tools.query_user_level(String.valueOf(user_id));
		List coupons = Lists.newArrayList();
		if (level_map != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("coupon_limit_less_than_equal",Integer.valueOf(CommUtil.null2Int(level_map.get("level"))));
			map.put("status", 0);
			map.put("store_id", store_id);
			
			coupons = this.couponService.queryPageList(map);
		}
		return coupons.size() > 0;
	}
	
	/**
	 * 查询店铺可用优惠券
	 * @param store_id
	 * @param user_id
	 * @return
	 */
	public List<Coupon> getStoreUsableCoupon(Long store_id, Long user_id) {
		Map level_map = this.tools.query_user_level(String.valueOf(user_id));
		if (level_map != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("coupon_limit_less_than_equal",Integer.valueOf(CommUtil.null2Int(level_map.get("level"))));
			map.put("status", 0);
			map.put("store_id", store_id);
			
			List<Coupon> coupons = this.couponService.queryPageList(map);
			
			return coupons;
		}
		return Lists.newArrayList();
	}
	
	/**
	 * 判断
	 * @param coupon_id
	 * @param user_id
	 * @return
	 */
	public boolean isPossessCoupon(Long coupon_id, Long user_id) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("coupon_id", CommUtil.null2Long(coupon_id));
		map.put("user_id", user_id);
		
		List cis = this.couponInfoService.queryPageList(map);
		
		return cis.size() > 0;
	}
}
