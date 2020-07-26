package com.redpigmall.view.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigDynamicUrlViewAction.java
 * </p>
 * 
 * <p>
 * Description:动态url处理控制器，该控制器用来处理相关动态url，比如商品url，前端页面中仅仅有商品id，
 * 无法判断商品url是通过二级域名访问还是顶级域名访问，此时就需要使用该控制的一个方法完成url跳转
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
 * @date 2015-2-4
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigDynamicUrlViewAction extends BaseAction{
	
	/**
	 * 商品url定向，前端只有商品id，不知道是否开启二级域名，需要通过该url来定向商品的url
	 * 
	 * @param request
	 *            输入请求
	 * @param response
	 *            输出信息
	 * @param id
	 *            商品id
	 */
	@RequestMapping({ "/goods_dynamic" })
	public void goods_dynamic(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		String goods_url = CommUtil.getURL(request) + "/items_" + id + "";
		if (this.configService.getSysConfig().getSecond_domain_open()) {
			if (!CommUtil.null2String(
					obj.getGoods_store().getStore_second_domain()).equals("")) {
				goods_url =

				"http://" + obj.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request) + "/items_" + id
						+ "";
			}
		}
		try {
			response.sendRedirect(goods_url);
		} catch (IOException e) {
			try {
				response.sendRedirect(CommUtil.getURL(request) + "/404");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
}
