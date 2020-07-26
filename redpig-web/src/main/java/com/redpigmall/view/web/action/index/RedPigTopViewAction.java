package com.redpigmall.view.web.action.index;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.User;
import com.redpigmall.view.web.action.base.BaseAction;

/**
*
* <p>
* Title: RedPigTopViewAction.java
* </p>
*
* <p>
* Description:商城首页控制器
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
* @version redpigmall_b2b2c v8.0 2016版
*/
@Controller
public class RedPigTopViewAction extends BaseAction{

	/**
	 * 页面top部分
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/top2" })
	public ModelAndView top2(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("top2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", this.navTools);

		Map<String,Object> params = Maps.newHashMap();
		if (SecurityUserHolder.getCurrentUser() != null) {

			params.clear();
			params.put("status", 0);
			params.put("toUser_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("parent", -1);

			int msgs = this.messageService.selectCount(params);

			mv.addObject("msg_size", msgs);
		}
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());

			mv.addObject("user", user);
		}
		List<GoodsCart> carts = this.goodsCartService.cart_list(request, null,
				null, null, false);
		mv.addObject("carts", carts);
		return mv;
	}
}
