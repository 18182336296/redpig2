package com.redpigmall.manage.buyer.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.CouponInfo;

/**
 * 
 * <p>
 * Title: RedPigCouponBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:买家优惠券管理控制器，商场管理员赠送给买家优惠券后，买家可以在这里查看优惠券信息
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
 * @date 2015-3-16
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigCouponBuyerAction extends BaseAction{
	
	/**
	 * 买家优惠券列表
	 * @param request
	 * @param response
	 * @param reply
	 * @param currentPage
	 * @param usable
	 * @param disabled
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "买家优惠券列表", value = "/buyer/coupon*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/coupon" })
	public ModelAndView coupon(HttpServletRequest request,
			HttpServletResponse response, String reply, String currentPage,
			String usable, String disabled) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_coupon.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		if ((usable == null) && (disabled == null)) {
			usable = "usable";
		}
		
		if ((usable != null) && (!usable.equals(""))) {
			maps.put("status", 0);
			mv.addObject("usable", usable);
		}
		
		if ((disabled != null) && (!disabled.equals(""))) {
			maps.put("status_no", 0);
			mv.addObject("disabled", disabled);
		}
		
		IPageList pList = this.couponInfoService.list(maps);
		
		List<CouponInfo> infos = pList.getResult();
		for (CouponInfo c_info : infos) {
			Coupon c = c_info.getCoupon();
			if ((c.getCoupon_end_time().before(new Date()))
					|| (c_info.getEndDate().before(new Date()))) {
				c_info.setStatus(-1);
				this.couponInfoService.updateById(c_info);
			}
		}
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
}
