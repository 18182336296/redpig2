package com.redpigmall.view.web.action.index;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.view.web.action.base.BaseAction;

/**
*
* <p>
* Title: RedPigNavViewAction.java
* </p>
*
* <p>
* Description:商城首页控制器
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
* @date 2014-4-25
*
* @version redpigmall_b2b2c v8.0 2016版
*/
@Controller
public class RedPigNavViewAction extends BaseAction{

	/**
	 * 页面导航
	 * @param request
	 * @param response
	 * @param pid
	 * @return
	 */
	@RequestMapping({ "/nav2" })
	public ModelAndView nav2(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new RedPigJModelAndView("nav2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> params = Maps.newHashMap();
		//***********************获取团购促销、公告 begin*********************
	    
	    params.put("gg_status", 1);
	    List<GroupGoods>  group_goods_list = this.groupGoodsService.queryPageList(params,0,3);
	    
	    mv.addObject("group_goods_list", group_goods_list);
		
	    
	    params.put("one_type", 1);
	    params.put("two_type", "right");
	    params.put("parent", -1);
	    List<ArticleClass> acs_right = this.articleClassService.queryPageList(params,0,1);
	    mv.addObject("acs_right", acs_right);
	    mv.addObject("article_Tools", this.article_Tools);
	    //***********************获取团购促销、公告 end***********************
	    
	    params.clear();
		params.put("parent",-1);
		params.put("orderBy","sequence");
		params.put("orderType", "asc");
		
		List<ShowClass> sc_list = this.showclassService.queryPageList(params,0,14);
		
		mv.addObject("sc_list", sc_list);
		mv.addObject("navTools", this.navTools);
		mv.addObject("gcViewTools", this.gcViewTools);
		mv.addObject("showClassTools", this.showClassTools);
		return mv;
	}
}
