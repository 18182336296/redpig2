package com.redpigmall.view.web.action.index;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Channel;
import com.redpigmall.view.web.action.base.BaseAction;

/**
*
* <p>
* Title: RedPigHeadViewAction.java
* </p>
*
* <p>
* Description:商城首页控制器
* </p>
*
* <p>
* Copyright: Copyright (c) 2018
* </p>
*
* <p>
* Company: www.redpigmall.net
* </p>
*
* @author redpig
*
* @date 2018-7-25
*
* @version redpigmall_b2b2c v8.0 2018版
*/
@Controller
public class RedPigHeadViewAction extends BaseAction{

	/**
	 * head头部分
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/head2" })
	public ModelAndView head2(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("head2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		mv.addObject("type", type.equals("") ? "goods" : type);
		if ((id != null) && (!"".equals(id))) {
			Channel channel = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));

			mv.addObject("channel", channel);
		}
		mv.addObject("navTools", this.navTools);
		return mv;
	}

}
