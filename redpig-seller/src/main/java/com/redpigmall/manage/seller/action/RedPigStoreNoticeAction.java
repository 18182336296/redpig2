package com.redpigmall.manage.seller.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Article;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigStoreNoticeAction.java
 * </p>
 * 
 * <p>
 * Description: 商家后台店铺公告管理控制器
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
 * @date 2014-4-2
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigStoreNoticeAction extends BaseAction{
	/**
	 * 商家店铺公告列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "商家店铺公告列表", value = "/store_notice*", rtype = "seller", rname = "店内公告", rcode = "store_notice", rgroup = "我的店铺")
	@RequestMapping({ "/store_notice" })
	public ModelAndView store_notice(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_notice.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10, "addTime", "desc");
		
		maps.put("type", "store");
		
		IPageList pList = this.articleService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}
	
	/**
	 * 商家店铺公告详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商家店铺公告详情", value = "/store_notice_detail*", rtype = "seller", rname = "店内公告", rcode = "store_notice", rgroup = "我的店铺")
	@RequestMapping({ "/store_notice_detail" })
	public ModelAndView store_notice_detail(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/store_notice_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Article article = this.articleService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", article);
		return mv;
	}
}
