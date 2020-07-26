package com.redpigmall.module.weixin.view.tools;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.view.web.tools.RedPigCouponViewTools;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class RedPigWeixinCartTools {
	
	@Autowired
	private RedPigGoodsCartService goodsCartService;
	
	@Autowired
	private RedPigCouponViewTools couponViewTools;

	public List getothergc(String id) {
		List list = Lists.newArrayList();
		GoodsCart cart = this.goodsCartService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if ((cart != null) && (cart.getCart_type() != null)
				&& (cart.getCart_type().equals("combin"))
				&& (cart.getCombin_main() == 1)) {
			String[] cart_ids = cart.getCombin_suit_ids().split(",");
			for (String cart_id : cart_ids) {
				if ((!cart_id.equals("")) && (!cart_id.equals(id))) {
					GoodsCart other = this.goodsCartService.selectByPrimaryKey(CommUtil
							.null2Long(cart_id));
					if (other != null) {
						list.add(other);
					}
				}
			}
		}
		return list;
	}

	public boolean coupon_count(String id) {
		List coupons = this.couponViewTools.getUsableCoupon(CommUtil
				.null2Long(id), SecurityUserHolder.getCurrentUser().getId());
		boolean ret = false;
		if (coupons.size() > 0) {
			ret = true;
		}
		return ret;
	}
}
