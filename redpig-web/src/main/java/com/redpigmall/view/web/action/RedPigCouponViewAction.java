package com.redpigmall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Coupon;
import com.redpigmall.view.web.action.base.BaseAction;
/**
 * 
 * <p>
 * Title: RedPigCouponViewAction.java
 * </p>
 * 
 * <p>
 * Description:优惠券
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
 * @date 2016-5-14
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigCouponViewAction extends BaseAction{


	
	/**
	 * 买家可领取优惠券
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@SecurityMapping(title = "买家可领取优惠券", value = "/goods_coupon*", rtype = "buyer", rname = "用户中心", rcode = "user_coupon", rgroup = "优惠券领取")
	@RequestMapping({ "/goods_coupon" })
	public ModelAndView goods_coupon(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView("goods_coupon.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Coupon> coupons = this.couponViewTools.getUsableCoupon(CommUtil
				.null2Long(goods_id), SecurityUserHolder.getCurrentUser()
				.getId());
		mv.addObject("coupons", coupons);
		mv.addObject("couponViewTools", this.couponViewTools);
		return mv;
	}
	
	/**
	 * 买家可领取店铺优惠券
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@SecurityMapping(title = "买家可领取店铺优惠券", value = "/store_coupon*", rtype = "buyer", rname = "用户中心", rcode = "user_coupon", rgroup = "优惠券领取")
	@RequestMapping({ "/store_coupon" })
	public ModelAndView store_coupon(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = new RedPigJModelAndView("goods_coupon.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Coupon> coupons = this.couponViewTools.getStoreUsableCoupon(CommUtil.null2Long(store_id), SecurityUserHolder.getCurrentUser().getId());
		mv.addObject("coupons", coupons);
		mv.addObject("couponViewTools", this.couponViewTools);
		return mv;
	}
	
	/**
	 * 买家可领取优惠券
	 * @param request
	 * @param response
	 * @param coupon_id
	 */
	@SecurityMapping(title = "买家可领取优惠券", value = "/capture_goods_coupon*", rtype = "buyer", rname = "用户中心", rcode = "user_coupon", rgroup = "优惠券领取")
	@RequestMapping({ "/capture_goods_coupon" })
	public void capture_goods_coupon(HttpServletRequest request,
			HttpServletResponse response, String coupon_id) {
		boolean ret = true;
		if (isUsableCoupon(SecurityUserHolder.getCurrentUser().getId(),
				coupon_id)) {
			this.couponInfoService.getCouponInfo(coupon_id, null);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	@SuppressWarnings("rawtypes")
	private boolean isUsableCoupon(Long user_id, String coupon_id) {
		Map Level_map = this.integralViewTools.query_user_level(CommUtil.null2String(user_id));
		Coupon coupon = this.couponService.selectByPrimaryKey(CommUtil
				.null2Long(coupon_id));
		if ((coupon.getCoupon_limit() > CommUtil.null2Int(Level_map
				.get("level"))) || (coupon.getStatus() == -1)) {
			return false;
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("coupon_id", CommUtil.null2Long(coupon_id));
		map.put("user_id", user_id);
		
		List cis = this.couponInfoService.queryPageList(map);
		
		if (cis.size() > 0) {
			return false;
		}
		return true;
	}
}
