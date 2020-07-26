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
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Channel;
import com.redpigmall.domain.ChannelFloor;
import com.redpigmall.view.web.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigChannelViewAction.java
 * </p>
 * 
 * <p>
 * Description:频道
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
public class RedPigChannelViewAction extends BaseAction{
	
	/**
	 * 频道导航
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/channel_nav" })
	public ModelAndView channel_nav(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = null;
		String style = "";
		if ((id != null) && (!"".equals(id))) {
			Channel ch = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ch != null) {
				if ("blue".equals(ch.getCh_nav_style())) {
					style = "blue";
				} else if ("brown".equals(ch.getCh_nav_style())) {
					style = "brown";
				} else {
					style = "green";
				}
				mv = new RedPigJModelAndView("channel_nav.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("obj", ch);
				mv.addObject("style", style);
				mv.addObject("ch_tools", this.channelTools);
			}
		}
		return mv;
	}
	
	/**
	 * 频道楼层
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/channel_floor" })
	public ModelAndView channel_floor(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("channel_floor.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> map = Maps.newHashMap();
		map.put("cf_ch_id", CommUtil.null2Long(id));
		map.put("orderBy", "cf_sequence");
		map.put("orderType", "asc");
		
		List<ChannelFloor> channelfloor_list = this.channelfloorService.queryPageList(map);
		
		mv.addObject("channelfloor_list", channelfloor_list);
		mv.addObject("channel_id", id);
		mv.addObject("channelFloorTools", this.channelTools);
		return mv;
	}
	
	/**
	 * 频道首页
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/channel/index" })
	public ModelAndView channel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("channel_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Channel ch = this.channelService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (ch != null) {
			mv.addObject("id", id);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该频道不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 楼层加载
	 * @param request
	 * @param response
	 * @param cf_id
	 * @throws ClassNotFoundException
	 */
	@RequestMapping({ "/channelfloor_load" })
	public void channelfloor_load(HttpServletRequest request,
			HttpServletResponse response, String cf_id)
			throws ClassNotFoundException {
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(cf_id)));
		String json = "";
		if (obj != null) {
			json = obj.getCf_centent_list();
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
