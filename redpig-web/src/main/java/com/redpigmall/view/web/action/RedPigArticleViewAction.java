package com.redpigmall.view.web.action;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Article;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.domain.User;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigArticleViewAction.java
 * </p>
 * 
 * <p>
 * Description: 前台文章控制器，主要功能: 1、分类列表文章 2、显示文章
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
 * @date 2016-4-30
 * 
 * @version redpig_b2b2c v8.0 2016版
 */
@Controller
public class RedPigArticleViewAction extends BaseAction{
	
	/**
	 * 文章帮助类
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/articlelist_help" })
	public ModelAndView article_list(HttpServletRequest request,
			HttpServletResponse response, String param, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("article_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		//查询条件
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		ArticleClass ac = null;
		
		Long id = CommUtil.null2Long(param);
		String mark = "";
		if (id  ==  -1L) {
			mark = param;
		}
		//根据属性名字、属性值来查询
		if ((mark != null) && (!"".equals(mark))) {
			ac = this.articleClassService.getObjByProperty("mark","=", mark);
		}
		//根据主键ID查询
		if (id != -1L) {
			ac = this.articleClassService.selectByPrimaryKey(id);
		}
		
		//获得所有ArticleClass子类ID
		if (ac != null) {
			Set<Long> ids = genericIds(ac);
			maps.put("articleClass_ids", ids);
		}
		
		maps.put("display", true);
		maps.put("articleClass_one_type", 1);
        maps.put("orderBy", "addTime");
        maps.put("orderType", "desc");
        
        //分页查询
		IPageList pList = this.articleService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		maps.clear();
		
		maps.put("one_type", 1);
		maps.put("parent", -1);
		maps.put("orderBy", "sequence");
        maps.put("orderType", "asc");
        
        //查询文章
		List<ArticleClass> acs = this.articleClassService.queryPageList(maps);
		
		Map<String, Object> map = Maps.newHashMap();
		maps.clear();
		maps.put("articleClass_one_type", 1);
		map.put("display", true);
		maps.put("orderBy", "addTime");
        maps.put("orderType", "desc");
        
        //查询文章
		List<Article> articles = this.articleService.queryPageList(maps, 0,6);
		
		mv.addObject("articles", articles);
		mv.addObject("acs", acs);
		mv.addObject("article_Tools", this.articleTools);
		return mv;
	}

	/**
	 * 获得ArticleClass所有的子类ID
	 * @param ac
	 * @return
	 */
	private Set<Long> genericIds(ArticleClass ac) {
		Set<Long> ids = Sets.newHashSet();
		if (ac != null) {
			ids.add(ac.getId());
			for (ArticleClass child : ac.getChilds()) {
				Set<Long> cids = genericIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}

	/**
	 * 文章查看类
	 * 
	 * @param request
	 * @param response
	 * @param param
	 * @return
	 */
	@RequestMapping({ "/article" })
	public ModelAndView article(HttpServletRequest request,
			HttpServletResponse response, String param) {
		ModelAndView mv = new RedPigJModelAndView("article.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Article obj = null;
		Long id = CommUtil.null2Long(param);
		String mark = "";
		if (id == -1L) {
			mark = param;
		}

		if (id != -1L) {
			obj = this.articleService.selectByPrimaryKey(id);
		}

		if (!mark.equals("")) {
			obj = this.articleService.getObjByProperty("mark","=",  mark);
		}

		User user = SecurityUserHolder.getCurrentUser();
		
		if (user != null) {

			if (user.getUserRole().equals("BUYER")) {
				if (obj != null) {
					if ("1".equals(CommUtil.null2String(Integer.valueOf(obj.getArticleClass().getOne_type())))) {
						Map<String,Object> maps = Maps.newHashMap();
				        maps.put("one_type", 1);
				        maps.put("parent", -1);
				        maps.put("orderBy", "sequence");
				        maps.put("orderType", "asc");
				        
						List<ArticleClass> acs = this.articleClassService.queryPageList(maps);
						maps.clear();
						maps.put("articleClass_one_type", 1);
				        maps.put("orderBy", "addTime");
				        maps.put("orderType", "desc");
				        
						List<Article> articles = this.articleService.queryPageList(maps, 0, 6);
						
						mv.addObject("articles", articles);
						mv.addObject("acs", acs);
						mv.addObject("obj", obj);
						mv.addObject("articleTools", this.articleTools);
					} else {
						mv = new RedPigJModelAndView("error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "商家公告，您无权查看！");
					}
				}
			}
			if ((user.getUserRole().equals("SELLER"))
					|| (user.getUserRole().equals("ADMIN"))) {
				if (obj != null) {
					Map<String,Object> maps = Maps.newHashMap();
			        maps.put("parent", -1);
			        maps.put("orderBy", "sequence");
			        maps.put("orderType", "asc");
			        
					List<ArticleClass> acs = this.articleClassService.queryPageList(maps);
					
					maps.clear();
			        maps.put("orderBy", "addTime");
			        maps.put("orderType", "desc");
			        
					List<Article> articles = this.articleService.queryPageList(maps, 0, 6);
					
					mv.addObject("articles", articles);
					mv.addObject("acs", acs);
					mv.addObject("obj", obj);
					mv.addObject("articleTools", this.articleTools);
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "该公告已不存在！");
				}
			}
		} else {
			if (obj != null) {
				if ("1".equals(CommUtil.null2String(Integer.valueOf(obj.getArticleClass().getOne_type())))) {
					
					Map<String,Object> maps = Maps.newHashMap();
			        maps.put("one_type", 1);
			        maps.put("parent", -1);
			        maps.put("orderBy", "addTime");
			        maps.put("orderType", "desc");
					
					List<ArticleClass> acs = this.articleClassService.queryPageList(maps);
					maps.clear();
					maps.put("articleClass_one_type", 1);
					
			        maps.put("orderBy", "addTime");
			        maps.put("orderType", "desc");
					List<Article> articles = this.articleService.queryPageList(maps, 0, 6);
					
					mv.addObject("articles", articles);
					mv.addObject("acs", acs);
					mv.addObject("obj", obj);
					mv.addObject("articleTools", this.articleTools);
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "商家公告，您无权查看！");
				}
			}

		}
		
		mv.addObject("articleTools", this.articleTools);
		return mv;
	}
}
