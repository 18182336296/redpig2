package com.redpigmall.manage.admin.action.tools.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redpigmall.redis.RedisCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;

import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigCacheManageAction.java
 * </p>
 * 
 * <p>
 * Description:系统缓存管理控制器
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
 * @date 2016年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCacheManageAction extends BaseAction{
	/**
	 * 缓存列表
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "缓存列表", value = "/cache_list*", rtype = "admin", rname = "缓存管理", rcode = "cache_manage", rgroup = "工具")
	@RequestMapping({ "/cache_list" })
	public ModelAndView cache_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/cache_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		
		mv.addObject("data_cache_size",RedisCache.getSize());
		return mv;
	}
	
	/**
	 * 更新缓存
	 * @param request
	 * @param response
	 * @param data_cache
	 * @param page_cache
	 * @return
	 */
	@SecurityMapping(title = "更新缓存", value = "/update_cache*", rtype = "admin", rname = "缓存管理", rcode = "cache_manage", rgroup = "工具")
	@RequestMapping({ "/update_cache" })
	public ModelAndView update_cache(HttpServletRequest request,
			HttpServletResponse response, String data_cache, String page_cache) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		RedisCache.clear();
		
		mv.addObject("list_url", CommUtil.getURL(request) + "/cache_list");
		mv.addObject("op_title", "更新缓存成功");
		return mv;
	}
}
