package com.redpigmall.module.weixin.view.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.domain.WeixinChannel;
import com.redpigmall.domain.WeixinChannelFloor;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
/**
 * <p>
 * Title: RedPigWeixinChannelAction.java
 * </p>
 * 
 * <p>
 * Description: 微信频道
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
 * @date 2017-4-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinChannelAction extends BaseAction{
	
	/**
	 * 手机端频道
	 * @param request
	 * @param response
	 * @param id
	 * @param sc
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/channel" })
	public ModelAndView channel(HttpServletRequest request,
			HttpServletResponse response, String id, String sc,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("weixin/second_channel.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ShowClass showClass = this.showClassService.selectByPrimaryKey(CommUtil
				.null2Long(sc));
		if ((StringUtils.isNotBlank(id)) && (StringUtils.isNotBlank(sc))
				&& (showClass != null)) {
			WeixinChannel channel = this.weixinChannelService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			mv.addObject("channel", channel);
			Map<String, Object> params = Maps.newHashMap();
			params.put("weixin_ch_id", channel.getId());
			params.put("display", Integer.valueOf(1));
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<WeixinChannelFloor> list = this.weixinChannelFloorService.queryPageList(params);
			
			mv.addObject("channel_floors", list);
			Map paras = Maps.newHashMap();
			paras.put("ad_ap", this.advertPositionService.selectByPrimaryKey(channel
					.getAdv_pos_id()));
			paras.put("status", Integer.valueOf(1));
			List<Advert> ads = this.advertService.queryPageList(params);
			
			if ((ads != null) && (ads.size() > 0)) {
				mv.addObject("ads", ads);
			}
			mv.addObject("weixinChannelFloorTools",this.weixinChannelFloorTools);
			mv.addObject("sc", showClass.getId());
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
			maps.put("parent", showClass.getId());
			maps.put("display", Boolean.valueOf(true));
			
			mv.addObject("weixinChannelFloorTools",this.weixinChannelFloorTools);
			
			IPageList pList = this.showClassService.list(maps);
			if (pList.getPages() >= CommUtil.null2Int(currentPage)) {
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该频道不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param sc_id
	 * @return
	 */
	@RequestMapping({ "/second_channel_data" })
	public ModelAndView second_channel_data(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String sc_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/second_channel_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ShowClass showClass = this.showClassService.selectByPrimaryKey(CommUtil
				.null2Long(sc_id));
		if (showClass == null) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该展示类目不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "sequence", "desc");
		
		maps.put("parent", showClass.getId());
		maps.put("display", Boolean.valueOf(true));
		
		mv.addObject("weixinChannelFloorTools", this.weixinChannelFloorTools);
		
		IPageList pList = this.showClassService.list(maps);
		if (pList.getPages() >= CommUtil.null2Int(currentPage)) {
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		return mv;
	}
}
