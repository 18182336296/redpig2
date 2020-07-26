package com.redpigmall.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
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
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.WeixinChannel;
import com.redpigmall.domain.WeixinChannelFloor;

/**
 * 
 * <p>
 * Title: RedPigWeixinChannelManageAction.java
 * </p>
 * 
 * <p>
 * Description:后台微商城管理
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigWeixinChannelManageAction extends BaseAction {

	/**
	 * 微信二级频道列表页
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道列表页", value = "/weixin_channel_list*", rtype = "admin", rname = "微信二级频道管理", rcode = "admin_weixin_channel", rgroup = "运营")
	@RequestMapping({ "/weixin_channel_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_channel_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage,
				orderBy, orderType);

		IPageList pList = this.weixinchannelService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/weixin_channel_list.html", "", params, pList, mv);
		mv.addObject("weixinChannelFloorTools", this.weixinChannelFloorTools);
		return mv;
	}

	/**
	 * 微信二级频道添加管理
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道添加管理", value = "/weixin_channel_add*", rtype = "admin", rname = "微信二级频道管理", rcode = "admin_weixin_channel", rgroup = "运营")
	@RequestMapping({ "/weixin_channel_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_channel_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		Map<String, Object> params = Maps.newHashMap();
		params.put("ap_status", Integer.valueOf(1));
		params.put("ap_type", "slide");
		params.put("ap_location", "Mobile");
		List<AdvertPosition> aps = this.advertPositionService
				.queryPageList(params);

		mv.addObject("aps", aps);
		return mv;
	}

	/**
	 * 微信二级频道编辑管理
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道编辑管理", value = "/weixin_channel_edit*", rtype = "admin", rname = "微信二级频道管理", rcode = "admin_weixin_channel", rgroup = "运营")
	@RequestMapping({ "/weixin_channel_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {

		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/weixin_channel_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		if ((id != null) && (!id.equals(""))) {
			WeixinChannel weixinchannel = this.weixinchannelService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", weixinchannel);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
			Map<String, Object> params = Maps.newHashMap();
			params.put("ap_status", Integer.valueOf(1));
			params.put("ap_type", "slide");
			params.put("ap_location", "Mobile");

			List<AdvertPosition> aps = this.advertPositionService
					.queryPageList(params);

			mv.addObject("aps", aps);
		}
		return mv;
	}

	/**
	 * 微信二级频道保存管理
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道保存管理", value = "/weixin_channel_save*", rtype = "admin", rname = "微信二级频道管理", rcode = "admin_weixin_channel", rgroup = "运营")
	@RequestMapping({ "/weixin_channel_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String list_url, String add_url) {

		WeixinChannel weixinchannel = null;
		if (id.equals("")) {
			weixinchannel = (WeixinChannel) WebForm.toPo(request,
					WeixinChannel.class);
			weixinchannel.setAddTime(new Date());
		} else {
			WeixinChannel obj = this.weixinchannelService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			weixinchannel = (WeixinChannel) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.weixinchannelService.saveEntity(weixinchannel);
		} else {
			this.weixinchannelService.updateById(weixinchannel);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存微商城频道成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}

	/**
	 * 微信二级频道删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "微信二级频道删除", value = "/weixin_channel_del*", rtype = "admin", rname = "微信二级频道管理", rcode = "admin_weixin_channel", rgroup = "运营")
	@RequestMapping({ "/weixin_channel_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {

		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				WeixinChannel weixinchannel = this.weixinchannelService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				this.weixinchannelService.deleteById(Long.valueOf(Long
						.parseLong(id)));
				Map<String, Object> map = Maps.newHashMap();
				map.put("weixin_ch_id", weixinchannel.getId());
				List<WeixinChannelFloor> list = this.weixinChannelFloorService
						.queryPageList(map);
				if ((list != null) && (list.size() > 0)) {
					for (WeixinChannelFloor weixinChannelFloor : list) {
						this.weixinChannelFloorService
								.deleteById(weixinChannelFloor.getId());
					}
				}
			}
		}
		return "redirect:weixin_channel_list?currentPage=" + currentPage;
	}

	/**
	 * 微信二级频道ajax
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "微信二级频道ajax", value = "/weixin_channel_ajax*", rtype = "admin", rname = "微信二级频道管理", rcode = "admin_weixin_channel", rgroup = "运营")
	@RequestMapping({ "/weixin_channel_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		WeixinChannel obj = this.weixinchannelService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = WeixinChannel.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);

		this.weixinchannelService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
