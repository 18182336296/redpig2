package com.redpigmall.view.web.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigAdvertViewAction.java
 * </p>
 * 
 * <p>
 * Description:广告调用控制器,系统采用广告位形式管理广告信息，前端使用js完成调用，js调用的是该控制器中的invoke方法，
 * redirect方法用来控制并记录广告点击信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-9-16
 * 
 * @version redpigmall_b2b2c 2015
 */
@Controller
public class RedPigAdvertViewAction extends BaseAction{
	
	/**
	 * 广告调用方法
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/advert_invoke" })
	public ModelAndView advert_invoke(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("advert_invoke.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (ap != null) {
				//新建一个对象,用于给前台传数据
				AdvertPosition obj = new AdvertPosition();
				obj.setAp_type(ap.getAp_type());
				obj.setAp_status(ap.getAp_status());
				obj.setAp_show_type(ap.getAp_show_type());
				obj.setAp_width(ap.getAp_width());
				obj.setAp_height(ap.getAp_height());
				obj.setAp_location(ap.getAp_location());
				List<Advert> advs = Lists.newArrayList();
				// 广告位对应的广告
				for (Advert temp_adv : ap.getAdvs()) {
					if ((temp_adv.getAd_status() == 1)//1为审核通过
							&& (temp_adv.getAd_begin_time().before(new Date()))//开始时间在当前时间之前
							&& (temp_adv.getAd_end_time().after(new Date()))) //结束时间在当前时间之后
						{
						
						advs.add(temp_adv);
					}
				}
				
				if (advs.size() > 0) {//如果广告位上有广告
					//数据库中查询出来的广告设置到广告位上
					setAdvertPosition(obj, advs);
					
				} else {//如果广告位上没有广告,就新建一个广告
					obj.setAp_acc(ap.getAp_acc());
					obj.setAp_text(ap.getAp_text());
					obj.setAp_acc_url(ap.getAp_acc_url());
					Advert adv = new Advert();
					adv.setAd_url(obj.getAp_acc_url());
					adv.setAd_acc(ap.getAp_acc());
					obj.getAdvs().add(adv);
					obj.setAp_location(ap.getAp_location());
				}
				
				if (obj.getAp_status() == 1) {// 1为启用
					mv.addObject("obj", obj);
				} else {// 0为关闭
					mv.addObject("obj", new AdvertPosition());
				}
			}
		}
		return mv;
	}
	
	/**
	 * 将广告设置到广告位上
	 * @param obj
	 * @param advs
	 */
	private void setAdvertPosition(AdvertPosition obj, List<Advert> advs) {
		if (obj.getAp_type().equals("text")) {// 文字广告
			if (obj.getAp_show_type() == 0) {// 固定广告
				obj.setAp_text(advs.get(0).getAd_text());// 广告默认文字，文字广告
				obj.setAp_acc_url(advs.get(0).getAd_url());// 广告默认链接
				obj.setAdv_id(CommUtil.null2String(advs.get(0).getId()));// 广告id，不存储数据库
			}
			if (obj.getAp_show_type() == 1) {// 随机广告
				Random random = new Random();
				int i = random.nextInt(advs.size());
				obj.setAp_text(advs.get(i).getAd_text());// 广告默认文字，文字广告
				obj.setAp_acc_url(advs.get(i).getAd_url());// 广告默认链接
				obj.setAdv_id(CommUtil.null2String(advs.get(i).getId()));// 广告id，不存储数据库
			}
		}
		
		if (obj.getAp_type().equals("img")) {// 图片广告
			if (obj.getAp_show_type() == 0) {// 固定广告
				obj.setAp_acc(advs.get(0).getAd_acc());// 广告默认图片，图片广告
				obj.setAp_acc_url(advs.get(0).getAd_url());// 广告默认链接
				obj.setAdv_id(CommUtil.null2String(advs.get(0).getId()));// 广告id，不存储数据库
			}
			if (obj.getAp_show_type() == 1) {// 随机广告
				Random random = new Random();
				int i = random.nextInt(advs.size());
				obj.setAp_acc(advs.get(i).getAd_acc());// 广告默认图片，图片广告
				obj.setAp_acc_url(advs.get(i).getAd_url());// 广告默认链接
				obj.setAdv_id(CommUtil.null2String(advs.get(i).getId()));// 广告id，不存储数据库
			}
		}
		
		if (obj.getAp_type().equals("slide")) {// 幻灯广告
			if (obj.getAp_show_type() == 0) {// 固定广告
				obj.setAdvs(advs);// 广告位对应的广告
			}
			if (obj.getAp_show_type() == 1) {// 随机广告
				Set<Integer> list = CommUtil.randomInt(advs.size(), 8);
				for (Integer i : list) {
					obj.getAdvs().add(advs.get(i));// 广告位对应的广告
				}
			}
		}
		
		if (obj.getAp_type().equals("scroll")) {// 滚动广告
			if (obj.getAp_show_type() == 0) {// 固定广告
				obj.setAdvs(advs);// 广告位对应的广告
			}
			if (obj.getAp_show_type() == 1) {// 随机广告
				Set<Integer> list = CommUtil.randomInt(advs.size(), 12);
				for (Integer i : list) {
					obj.getAdvs().add(advs.get(i));// 广告位对应的广告
				}
			}
		}
		
		if (obj.getAp_type().equals("bg_slide")) {//背景幻灯
			if (obj.getAp_show_type() == 0) {// 固定广告
				obj.setAdvs(advs);// 广告位对应的广告
			}
			if (obj.getAp_show_type() == 1) {// 随机广告
				
				Set<Integer> list = CommUtil.randomInt(advs.size(),advs.size());
				for (Integer i : list) {
					obj.getAdvs().add((Advert) advs.get(i));// 广告位对应的广告
				}
			}
		}
		
		if (obj.getAp_type().equals("pc_bg_slide")) {//PC背景幻灯
			if (obj.getAp_show_type() == 0) {// 固定广告
				obj.setAdvs(advs);// 广告位对应的广告
			}
			if (obj.getAp_show_type() == 1) {// 随机广告
				
				Set<Integer> list = CommUtil.randomInt(advs.size(),advs.size());
				for (Integer i : list) {
					obj.getAdvs().add((Advert) advs.get(i));// 广告位对应的广告
				}
			}
		}
		
		if (obj.getAp_type().equals("pc_bg_ver")) {// 图片广告
			if (obj.getAp_show_type() == 0) {// 固定广告
				obj.setAdvs(advs);// 广告位对应的广告
			}
			if (obj.getAp_show_type() == 1) {// 随机广告
				Set<Integer> list = CommUtil.randomInt(advs.size(),advs.size());
				for (Integer i : list) {
					obj.getAdvs().add((Advert) advs.get(i));// 广告位对应的广告
				}
			}
		}
	}
	
	/**
	 * 广告跳转
	 * @param request
	 * @param response
	 * @param id
	 * @param url
	 */
	@RequestMapping({ "/advert_redirect" })
	public void advert_redirect(HttpServletRequest request,
			HttpServletResponse response, String id, String url) {
		try {
			Advert adv = this.advertService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (adv != null) {
				adv.setAd_click_num(adv.getAd_click_num() + 1);
				this.advertService.updateById(adv);
			}
			if (adv != null) {
				url = adv.getAd_url();
				response.sendRedirect(url);
			} else {
				response.sendRedirect(CommUtil.getURL(request) + "/" + url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
