package com.redpigmall.view.web.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.domain.Article;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.domain.Document;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigDocumentViewAction.java
 * </p>
 * 
 * <p>
 * Description:系统文章前台显示控制器
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-5-6
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigDocumentViewAction extends BaseAction{
	
	/**
	 * 文章
	 * @param request
	 * @param response
	 * @param mark
	 * @return
	 */
	@RequestMapping({ "/doc" })
	public ModelAndView doc(HttpServletRequest request,
			HttpServletResponse response, String mark) {
		ModelAndView mv = new RedPigJModelAndView("article.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("doc", "doc");
		
		Document obj = this.documentService.getObjByProperty("mark","=",  mark);
		mv.addObject("obj", obj);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<ArticleClass> acs = this.articleClassService.queryPageList(params);
		params.clear();
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Article> articles = this.articleService.queryPageList(params,0,6);
		
		mv.addObject("articles", articles);
		mv.addObject("acs", acs);
		return mv;
	}
}
