package com.redpigmall.module.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.weixin.manage.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title: RedPigWeixinAppDownloadViewAction.java
 * </p>
 * 
 * <p>
 * Description:下载APP
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
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class RedPigWeixinAppDownloadViewAction extends BaseAction{
	@RequestMapping({ "/app_download" })
	public ModelAndView app_download(HttpServletRequest request,
			HttpServletResponse response) {
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		String url = CommUtil.getURL(request);
		if (user_agent.indexOf("iphone") > 0) {
			url = this.configService.getSysConfig().getIos_download();
		}
		if (user_agent.indexOf("android") > 0) {
			ModelAndView mv = new RedPigJModelAndView("weixin/app_download.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			url = this.configService.getSysConfig().getAndroid_download();
			mv.addObject("url", url);
			if (StringUtils.isNotBlank(url)) {
				return mv;
			}
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "下载链接异常，稍后重试");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		Map<String, Object> map = Maps.newHashMap();
		map.put("url", url);
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
