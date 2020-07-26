package com.redpigmall.module.weixin.view.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.User;
import com.redpigmall.module.weixin.view.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigWeixinCouponViewAction.java
 * </p>
 * 
 * <p>
 * Description: 微信优惠券
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
@SuppressWarnings({ "rawtypes" })
@Controller
public class RedPigWeixinCouponViewAction extends BaseAction{
	
	/**
	 * 优惠券列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "优惠券列表", value = "/coupon/goods_coupon*", rtype = "buyer", rname = "移动端优惠券列表", rcode = "wap_coupon_list", rgroup = "移动端优惠券")
	@RequestMapping({ "/coupon/goods_coupon" })
	public ModelAndView goods_coupon(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("weixin/coupon_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView("weixin/coupon_list_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		User user = SecurityUserHolder.getCurrentUser();
		
		Map user_level = Maps.newHashMap();
		if (user != null) {
			user = this.userService.selectByPrimaryKey(user.getId());
			user_level = this.integralViewTools.query_user_level(user.getId().toString());
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10,"addTime ", "asc");
		if ((user_level != null) && (user_level.size() > 0)) {
			int level = ((Integer) user_level.get("level")).intValue();
			if ((level > 0) || (level == -1)) {
				
				IPageList coupon_pList = this.couponService.list(maps);
				List result = coupon_pList.getResult();
				mv.addObject("coupons", result);
				mv.addObject("couponViewTools", this.couponViewTools);
				CommUtil.saveIPageList2ModelAndView("", "", "", coupon_pList,
						mv);
			} else {
				maps.put("coupon_limit", Integer.valueOf(level));
				
				IPageList coupon_pList = this.couponService.list(maps);
				mv.addObject("coupons", coupon_pList.getResult());
				mv.addObject("couponViewTools", this.couponViewTools);
				CommUtil.saveIPageList2ModelAndView("", "", "", coupon_pList,
						mv);
			}
		}
		return mv;
	}
	
	/**
	 * 去领取优惠券
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "去领取优惠券", value = "/coupon/to_capture_coupon*", rtype = "buyer", rname = "移动端去领取优惠券", rcode = "wap_to_capture_coupon", rgroup = "移动端优惠券")
	@RequestMapping({ "/coupon/to_capture_coupon" })
	public ModelAndView to_capture_coupon(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/capture_goods_coupon.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Coupon coupon = this.couponService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("coupon", coupon);
		mv.addObject("couponViewTools", this.couponViewTools);
		return mv;
	}
	
	/**
	 * 领取优惠券
	 * @param request
	 * @param response
	 * @param id
	 * @param code
	 * @return
	 */
	@SecurityMapping(title = "领取优惠券", value = "/coupon/apture_goods_coupon*", rtype = "buyer", rname = "移动端领取优惠券", rcode = "wap_capture_coupon", rgroup = "移动端优惠券")
	@RequestMapping({ "/coupon/apture_goods_coupon" })
	public ModelAndView capture_goods_coupon(HttpServletRequest request,
			HttpServletResponse response, String id, String code) {
		ModelAndView mv = new RedPigJModelAndView(
				"weixin/capture_goods_coupon_succ.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();

		String verify_code = CommUtil.null2String(request.getSession(false)
				.getAttribute("coupon_verify_code"));
		Coupon coupon = this.couponService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (!verify_code.equals(code)) {
			mv = new RedPigJModelAndView("weixin/capture_goods_coupon.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("error", "验证码错误");
			mv.addObject("couponViewTools", this.couponViewTools);
			mv.addObject("coupon", coupon);
			return mv;
		}
		request.getSession(false).removeAttribute("coupon_verify_code");
		if (coupon == null) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该优惠券不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		if (coupon.getCoupon_surplus_amount() < 1) {
			mv = new RedPigJModelAndView("weixin/capture_goods_coupon.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("error", "优惠券不足");
			mv.addObject("couponViewTools", this.couponViewTools);
			mv.addObject("coupon", coupon);
			return mv;
		}
		if (this.couponViewTools.isPossessCoupon(coupon.getId(), user.getId())) {
			mv = new RedPigJModelAndView("weixin/capture_goods_coupon.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("error", "你已领取该优惠券");
			mv.addObject("couponViewTools", this.couponViewTools);
			mv.addObject("coupon", coupon);
			return mv;
		}
		this.couponInfoService.getCouponInfo(id, null);

		mv.addObject("id", coupon.getId());
		return mv;
	}
	
	/**
	 * 优惠券认证
	 * @param request
	 * @param response
	 * @param name
	 * @param w
	 * @param h
	 * @throws IOException
	 */
	@RequestMapping({ "/coupon/verify" })
	public void verify(HttpServletRequest request,
			HttpServletResponse response, String name, String w, String h)
			throws IOException {
		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		HttpSession session = request.getSession(false);

		int width = 73;
		int height = 27;
		if (!CommUtil.null2String(w).equals("")) {
			width = CommUtil.null2Int(w);
		}
		if (!CommUtil.null2String(h).equals("")) {
			height = CommUtil.null2Int(h);
		}
		BufferedImage image = new BufferedImage(width, height, 1);

		Graphics g = image.getGraphics();

		Random random = new Random();

		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);

		g.setFont(new Font("Times New Roman", 0, 24));

		g.setColor(new Color(80, 80, 80));
		g.drawRect(0, 0, width - 1, height - 1);

		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 255; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		String sRand = "";
		for (int i = 0; i < 4; i++) {
			String rand = CommUtil.randomInt(1).toUpperCase();
			sRand = sRand + rand;

			g.setColor(new Color(20 + random.nextInt(110), 20 + random
					.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(rand, 14 * i + 6, 24);
		}
		if (CommUtil.null2String(name).equals("")) {
			session.setAttribute("coupon_verify_code", sRand);
		} else {
			session.setAttribute(name, sRand);
		}
		g.dispose();
		ServletOutputStream responseOutputStream = response.getOutputStream();

		ImageIO.write(image, "JPEG", responseOutputStream);

		responseOutputStream.flush();
		responseOutputStream.close();
	}

	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}
