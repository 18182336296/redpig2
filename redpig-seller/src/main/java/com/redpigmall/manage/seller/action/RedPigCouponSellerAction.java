package com.redpigmall.manage.seller.action;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title:RedPigCouponSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心优惠劵控制器
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
 * @date 2014年5月5日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCouponSellerAction extends BaseAction{
	/**
	 * 优惠券列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param coupon_name
	 * @param coupon_begin_time
	 * @param coupon_end_time
	 * @return
	 */
	@SecurityMapping(title = "优惠券列表", value = "/coupon*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping({ "/coupon" })
	public ModelAndView coupon(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_name, String coupon_begin_time,
			String coupon_end_time) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/coupon_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("store_id", store.getId());
		
		if (!CommUtil.null2String(coupon_name).equals("")) {
			
			maps.put("coupon_name_like", coupon_name);
		}
		if (!CommUtil.null2String(coupon_begin_time).equals("")) {
			
			maps.put("coupon_begin_time_more_than_equal", CommUtil.formatDate(coupon_begin_time));
		}
		if (!CommUtil.null2String(coupon_end_time).equals("")) {
			
			maps.put("coupon_begin_time_less_than_equal", CommUtil.formatDate(coupon_end_time));
		}
		IPageList pList = this.couponService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
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
	@SecurityMapping(title = "优惠券添加", value = "/coupon_add*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping({ "/coupon_add" })
	public ModelAndView coupon_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/coupon_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		List<Map> list = JSON.parseArray(this.configService.getSysConfig().getUser_level(), Map.class);
		mv.addObject("userLevelList", list);
		return mv;
	}
	
	/**
	 * 优惠券编辑
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "优惠券编辑", value = "/coupon_edit*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping({ "/coupon_edit" })
	public ModelAndView coupon_edit(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/coupon_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		Coupon coupon = this.couponService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", coupon);
		mv.addObject("edit", Boolean.valueOf(true));
		List<Map> list = JSON.parseArray(this.configService.getSysConfig().getUser_level(), Map.class);
		mv.addObject("userLevelList", list);
		return mv;
	}
	
	/**
	 * 优惠券保存
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param coupon_count
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "优惠券保存", value = "/coupon_save*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping({ "/coupon_save" })
	public void coupon_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String coupon_count) {
		Coupon coupon = null;
		if (id.equals("")) {
			coupon = (Coupon) WebForm.toPo(request, Coupon.class);
			coupon.setAddTime(new Date());
			coupon.setCoupon_surplus_amount(CommUtil.null2Int(coupon_count));
		} else {
			coupon = this.couponService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			coupon = (Coupon) WebForm.toPo(request, coupon);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = ClusterSyncTools.getClusterRoot() + File.separator + uploadFilePath + File.separator + "coupon";
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
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
				String targetImgName = UUID.randomUUID().toString() + ".jpg";
				String targetImg = saveFilePathName + File.separator
						+ targetImgName;
				if (!CommUtil.fileExist(saveFilePathName)) {
					CommUtil.createFolder(saveFilePathName);
				}
				try {
					Font font = new Font("微软雅黑", 0, 40);
					waterMarkWithText(pressImg, targetImg,
							"订单满" + coupon.getCoupon_order_amount() + "元可用",
							"#726960", font, 50, 170, 1.0F, null);
					font = new Font("Garamond", 1, 90);
					waterMarkWithText(targetImg, targetImg, coupon
							.getCoupon_amount().toString(), "#f2d62d", font,
							150, 100, 1.0F, Boolean.valueOf(true));
				} catch (Exception localException) {
				}
				coupon_acc.setName(targetImgName);
				File file = new File(pressImg);
				file.delete();
				this.accessoryService.updateById(coupon_acc);
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
				try {
					Font font = new Font("Garamond", 1, 90);
					waterMarkWithText(pressImg, targetImgPath + targetImgName,
							coupon.getCoupon_amount().toString(), "#f2d62d",
							font, 150, 100, 1.0F, Boolean.valueOf(true));
					font = new Font("微软雅黑", 0, 40);
					waterMarkWithText(targetImgPath + targetImgName,
							targetImgPath + targetImgName,
							"订单满" + coupon.getCoupon_order_amount() + "元可用",
							"#726960", font, 50, 170, 1.0F, null);
				} catch (Exception e) {}
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
		if (id.equals("")) {
			coupon.setCoupon_type(1);
			coupon.setStore(store);
			this.couponService.saveEntity(coupon);
		} else {
			this.couponService.updateById(coupon);
		}
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(true));
		json.put("op_title", "优惠券保存成功");
		json.put("url", CommUtil.getURL(request)
				+ "/coupon?currentPage=" + currentPage);
		return_json(JSON.toJSONString(json), response);
	}
	
	/**
	 * 优惠券保存成功
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "优惠券保存成功", value = "/coupon_success*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping({ "/coupon_success" })
	public ModelAndView coupon_success(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", CommUtil.getURL(request)
				+ "/coupon?currentPage=" + currentPage);
		mv.addObject("op_title", "优惠券保存成功");
		return mv;
	}
	
	/**
	 * 优惠券删除
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "优惠券删除", value = "/coupon_del*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping({ "/coupon_del" })
	public String coupon_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Coupon coupon = this.couponService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((coupon != null)
				&& (coupon.getStore().getId().equals(user.getStore().getId()))) {
			Date nowDate = new Date();
			if ((coupon.getCoupon_surplus_amount() == coupon.getCoupon_count())
					&& (nowDate.getTime() <= coupon.getCoupon_end_time()
							.getTime())) {
				Accessory acc = coupon.getCoupon_acc();
				this.couponService.deleteById(CommUtil.null2Long(id));
				this.accessoryService.deleteById(acc.getId());
				boolean ret = true;
				if (ret) {
					RedPigCommonUtil.del_acc(request, acc);
				}
			}
		}
		return "redirect:coupon?currentPage=" + currentPage;
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
	@SecurityMapping(title = "优惠券详细信息", value = "/coupon_ajax*", rtype = "seller", rname = "优惠券管理", rcode = "coupon_seller", rgroup = "促销推广")
	@RequestMapping({ "/coupon_info_list" })
	public ModelAndView coupon_info_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String coupon_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/coupon_info_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("coupon_id", CommUtil.null2Long(coupon_id));
		
		IPageList pList = this.couponinfoService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/coupon_info_list.html", "", params, pList, mv);
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
			g.drawString(this.configService.getSysConfig().getCurrency_code(),
					100, 90);
		}
		g.dispose();
		try {
			FileOutputStream out = new FileOutputStream(outPath);
			ImageIO.write(bimage, filePath.substring(filePath.lastIndexOf(".") + 1), out);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
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
