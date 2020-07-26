package com.redpigmall.manage.admin.action.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;

/**
 * 
 * Title: ErrorController.java
 * 
 * Description:异常处理页面
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月5日 上午11:48:44
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@Controller
public class RedPigErrorController extends BaseAction implements ErrorController {

	@Override
	public String getErrorPath() {

		return null;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/RedPigError" })
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((SecurityUserHolder.getCurrentUser() != null) && (SecurityUserHolder.getCurrentUser().getUserRole().equalsIgnoreCase("ADMIN"))) {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("list_url", request.getSession(false).getAttribute("url"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

}
