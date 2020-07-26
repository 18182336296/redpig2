package com.redpigmall.manage.admin.action.self.coupon;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.CouponInfo;

/**
 * <p>
 * Title: RedPigCouponManageAction.java
 * </p>
 * 
 * <p>
 * Description: 优惠券控制管理器，管理商城系统优惠券信息 优惠券使用方法：
 * 1、管理员添加优惠券，包括优惠券面额、使用条件（订单满多少可以可以用），优惠券数量、使用时间区间 2、优惠券只有平台管理员赠送用户才能获取
 * 3、用户购物订单金额满足优惠券使用条件后可以使用优惠券
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCouponManageAction extends BaseAction {
	/**
	 * 优惠券列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param coupon_search_name
	 * @param coupon_begin_time
	 * @param coupon_end_time
	 * @return
	 */
	@SecurityMapping(title = "优惠券列表", value = "/coupon_list*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping({ "/coupon_list" })
	public ModelAndView coupon_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_search_name,
			String coupon_begin_time, String coupon_end_time) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/coupon_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("coupon_type", 0);
		if (!CommUtil.null2String(coupon_search_name).equals("")) {
			
			maps.put("coupon_name_like", coupon_search_name);
			mv.addObject("coupon_search_name", coupon_search_name);
		}
		if (!CommUtil.null2String(coupon_begin_time).equals("")) {
			
			
			maps.put("coupon_begin_time_more_than_equal", CommUtil.formatDate(coupon_begin_time));
			mv.addObject("coupon_begin_time", coupon_begin_time);
		}
		if (!CommUtil.null2String(coupon_end_time).equals("")) {
			
			maps.put("coupon_begin_time_less_than_equal", CommUtil.formatDate(coupon_end_time));
			mv.addObject("coupon_end_time", coupon_end_time);
		}
		IPageList pList = this.couponService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/coupon_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 优惠券添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "优惠券添加", value = "/coupon_add*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping({ "/coupon_add" })
	public ModelAndView coupon_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/coupon_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		List<Map> list = JSON.parseArray(this.configService.getSysConfig()
				.getUser_level(), Map.class);
		mv.addObject("userLevelList", list);
		return mv;
	}
	
	/**
	 * 优惠券保存
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param coupon_count
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "优惠券保存", value = "/coupon_save*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping({ "/coupon_save" })
	public String coupon_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String currentPage,
			String coupon_count) {
		
		Coupon coupon = (Coupon) WebForm.toPo(request, Coupon.class);
		coupon.setAddTime(new Date());
		coupon.setCoupon_surplus_amount(CommUtil.null2Int(coupon_count));
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "coupon";
		Map<String, Object> map = Maps.newHashMap();
		try {
			map = CommUtil.saveFileToServer(request, "coupon_img",
					saveFilePathName, null, null);
			if (map.get("fileName") != "") {
				Accessory coupon_acc = new Accessory();
				coupon_acc.setName(CommUtil.null2String(map.get("fileName")));
				coupon_acc.setExt((String) map.get("mime"));
				coupon_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				coupon_acc.setPath(uploadFilePath + "/coupon");
				coupon_acc.setWidth(CommUtil.null2Int(map.get("width")));
				coupon_acc.setHeight(CommUtil.null2Int(map.get("height")));
				coupon_acc.setAddTime(new Date());
				this.accessoryService.saveEntity(coupon_acc);
				String pressImg = saveFilePathName + File.separator
						+ coupon_acc.getName();
				String targetImg = saveFilePathName + File.separator
						+ coupon_acc.getName() + "." + coupon_acc.getExt();
				if (!CommUtil.fileExist(saveFilePathName)) {
					CommUtil.createFolder(saveFilePathName);
				}
				try {
					Font font = new Font("Garamond", 1, 90);
					waterMarkWithText(pressImg, targetImg, coupon
							.getCoupon_amount().toString(), "#f2d62d", font,
							150, 100, 1.0F, Boolean.valueOf(true));
					font = new Font("微软雅黑", 0, 40);
					waterMarkWithText(targetImg, targetImg,
							"订单满" + coupon.getCoupon_order_amount() + "元可用",
							"#726960", font, 50, 170, 1.0F, null);
				} catch (Exception localException) {
				}
				coupon.setCoupon_acc(coupon_acc);
			} else {
				String pressImg = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator
						+ "resources"
						+ File.separator
						+ "style"
						+ File.separator
						+ "common"
						+ File.separator
						+ "template" + File.separator + "coupon_template.jpg";
				String targetImgPath = request.getSession().getServletContext()
						.getRealPath("")
						+ File.separator
						+ uploadFilePath
						+ File.separator
						+ "coupon" + File.separator;
				if (!CommUtil.fileExist(targetImgPath)) {
					CommUtil.createFolder(targetImgPath);
				}
				String targetImgName = UUID.randomUUID().toString() + ".jpg";
				Font font = new Font("Garamond", 1, 90);
				waterMarkWithText(pressImg, targetImgPath + targetImgName,
						coupon.getCoupon_amount().toString(), "#f2d62d", font,
						150, 100, 1.0F, Boolean.valueOf(true));
				font = new Font("微软雅黑", 0, 40);
				waterMarkWithText(targetImgPath + targetImgName, targetImgPath
						+ targetImgName,
						"订单满" + coupon.getCoupon_order_amount() + "元可用",
						"#726960", font, 50, 170, 1.0F, null);
				Accessory coupon_acc = new Accessory();
				coupon_acc.setName(targetImgName);
				coupon_acc.setExt("jpg");
				coupon_acc.setPath(uploadFilePath + "/coupon");
				coupon_acc.setAddTime(new Date());
				coupon_acc.setSize(BigDecimal.valueOf(CommUtil
						.null2Double(Double.valueOf(28.4D))));
				this.accessoryService.saveEntity(coupon_acc);
				coupon.setCoupon_acc(coupon_acc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.couponService.saveEntity(coupon);
		return "redirect:coupon_success?currentPage=" + currentPage;
	}
	
	/**
	 * 优惠券保存成功
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "优惠券保存成功", value = "/coupon_success*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping({ "/coupon_success" })
	public ModelAndView coupon_success(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/coupon_list");
		mv.addObject("op_title", "优惠券保存成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/coupon_add"
				+ "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 优惠券AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "优惠券AJAX更新", value = "/coupon_ajax*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping({ "/coupon_ajax" })
	public void coupon_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Coupon obj = this.couponService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = Coupon.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.couponService.updateById(obj);
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
	
	/**
	 * 优惠券详细信息
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param coupon_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "优惠券详细信息", value = "/coupon_info_list*", rtype = "admin", rname = "优惠券管理", rcode = "coupon_admin", rgroup = "自营")
	@RequestMapping({ "/coupon_info_list" })
	public ModelAndView coupon_info_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/coupon_info_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps .put("coupon_id", CommUtil.null2Long(coupon_id));
		IPageList pList = this.couponinfoService.list(maps);
		List<CouponInfo> infos = pList.getResult();
		for (CouponInfo c_info : infos) {
			Coupon c = c_info.getCoupon();
			if ((c.getCoupon_end_time().before(new Date()))
					|| (c_info.getEndDate().before(new Date()))) {
				c_info.setStatus(-1);
				this.couponinfoService.updateById(c_info);
			}
		}
		CommUtil.saveIPageList2ModelAndView("", "", params, pList, mv);
		mv.addObject("coupon_id", coupon_id);
		return mv;
	}

	private boolean waterMarkWithText(String filePath, String outPath,
			String text, String markContentColor, Font font, int left, int top,
			float qualNum, Boolean mark) {
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image theImg = imgIcon.getImage();
		int width = theImg.getWidth(null);
		int height = theImg.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height, 1);
		Graphics2D g = bimage.createGraphics();
		if (font == null) {
			font = new Font("宋体", 1, 20);
			g.setFont(font);
		} else {
			g.setFont(font);
		}
		g.setColor(CommUtil.getColor(markContentColor));
		g.setComposite(AlphaComposite.getInstance(10, 1.0F));
		g.drawImage(theImg, 0, 0, null);
		g.drawString(text, left, top);
		if (mark != null) {
			g.setFont(new Font("Garamond", 1, 55));
			String currency_code = this.configService.getSysConfig()
					.getCurrency_code();
			if ((currency_code == null) || (currency_code.equals(""))) {
				currency_code = "¥";
			}
			g.drawString(this.configService.getSysConfig().getCurrency_code(),
					100, 90);
		}
		g.dispose();
		try {
			FileOutputStream out = new FileOutputStream(outPath);
			ImageIO.write(bimage,
					filePath.substring(filePath.lastIndexOf(".") + 1), out);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
