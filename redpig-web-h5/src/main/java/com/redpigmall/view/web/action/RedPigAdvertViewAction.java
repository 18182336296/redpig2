package com.redpigmall.view.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Advert;
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
