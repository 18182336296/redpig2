package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WaterMark;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigWaterMarkSellerAction.java
 * </p>
 * 
 * <p>
 * Description:水印管理控制器，卖家可以管理图片的文字水印、图片水印
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
 * @date 2014-7-17
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigWaterMarkSellerAction extends BaseAction{
	/**
	 * 图片水印
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "图片水印", value = "/watermark*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/watermark" })
	public ModelAndView watermark(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/watermark.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (store != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_id", store.getId());
			List<WaterMark> wms = this.watermarkService.queryPageList(params);
			
			if (wms.size() > 0) {
				mv.addObject("obj", wms.get(0));
			}
		}
		return mv;
	}
	
	/**
	 * 图片水印保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "图片水印保存", value = "/watermark_save*", rtype = "seller", rname = "图片空间", rcode = "album_seller", rgroup = "其他管理")
	@RequestMapping({ "/watermark_save" })
	public ModelAndView watermark_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd) {
		ModelAndView mv = null;
		if (SecurityUserHolder.getCurrentUser().getStore() != null) {
			
			WaterMark watermark = null;
			if (id.equals("")) {
				watermark = (WaterMark) WebForm.toPo(request, WaterMark.class);
				watermark.setAddTime(new Date());
			} else {
				WaterMark obj = this.watermarkService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				watermark = (WaterMark) WebForm.toPo(request, obj);
			}
			
			watermark.setStore(SecurityUserHolder.getCurrentUser().getStore());
			String path = request.getSession().getServletContext()
					.getRealPath("/")
					+ "upload/wm";
			try {
				Map<String, Object> map = CommUtil.saveFileToServer(request,
						"wm_img", path, null, null);
				if (!map.get("fileName").equals("")) {
					Accessory wm_image = new Accessory();
					wm_image.setAddTime(new Date());
					wm_image.setHeight(CommUtil.null2Int(map.get("height")));
					wm_image.setName(CommUtil.null2String(map.get("fileName")));
					wm_image.setPath("upload/wm");
					wm_image.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					wm_image.setUser(SecurityUserHolder.getCurrentUser());
					wm_image.setWidth(CommUtil.null2Int("width"));
					this.accessoryService.saveEntity(wm_image);
					watermark.setWm_image(wm_image);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (id.equals("")) {
				this.watermarkService.saveEntity(watermark);
			} else {
				this.watermarkService.updateById(watermark);
			}
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "水印设置成功");
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您尚未开店");
		}
		mv.addObject("url", CommUtil.getURL(request) + "/watermark");
		return mv;
	}

	public void return_json(String json, HttpServletResponse response) {
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
