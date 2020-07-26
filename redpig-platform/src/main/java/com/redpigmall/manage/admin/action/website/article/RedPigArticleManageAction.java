package com.redpigmall.manage.admin.action.website.article;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Article;
import com.redpigmall.domain.ArticleClass;

/**
 * 
 * <p>
 * Title: RedPigArticleManageAction.java
 * </p>
 * 
 * <p>
 * Description:系统文章管理控制器，用来发布、修改系统文章信息
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
 * @date 2014-5-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigArticleManageAction extends BaseAction{

	/**
	 * 文章列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "文章列表", value = "/article_list*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping({ "/article_list" })
	public ModelAndView article_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/article_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.redPigArticleService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/article_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 文章添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param class_id
	 * @return
	 */
	@SecurityMapping(title = "文章添加", value = "/article_add*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping({ "/article_add" })
	public ModelAndView article_add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String class_id) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/article_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<ArticleClass> acs = this.articleClassService.queryPageList(params);
		
		Article obj = new Article();
		obj.setDisplay(true);
		if ((class_id != null) && (!class_id.equals(""))) {
			obj.setArticleClass(this.articleClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(class_id))));
		}
		mv.addObject("obj", obj);
		mv.addObject("acs", acs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 文章编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "文章编辑", value = "/article_edit*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping({ "/article_edit" })
	public ModelAndView article_edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/article_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			Article article = this.redPigArticleService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			
			Map<String,Object> params = Maps.newHashMap();
			params.put("parent", -1);
			
			List<ArticleClass> acs = this.articleClassService.queryPageList(params);
			
			mv.addObject("acs", acs);
			mv.addObject("obj", article);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 文章保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param class_id
	 * @param content
	 * @return
	 */
	@SecurityMapping(title = "文章保存", value = "/article_save*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping({ "/article_save" })
	public ModelAndView article_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage,
			String cmd, 
			String list_url, 
			String add_url, 
			String class_id,
			String content) {
		
		Article article = null;
		if (id.equals("")) {
			article = (Article) WebForm.toPo(request, Article.class);
			article.setAddTime(new Date());
		} else {
			Article obj = this.redPigArticleService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			article = (Article) WebForm.toPo(request, obj);
		}
		ArticleClass ac = this.articleClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(class_id)));
		article.setDisplay(Boolean.valueOf(request.getParameter("display")));
		System.out.println(article.getArticleClass());
		
		article.setArticleClass(ac);
		
		System.out.println(content);
		if (id.equals("")) {
			this.redPigArticleService.saveEntity(article);
		} else {
			this.redPigArticleService.updateById(article);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存文章成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage
					+ "&class_id=" + class_id);
		}
		return mv;
	}
	
	/**
	 * 文章删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "文章删除", value = "/article_del*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping({ "/article_del" })
	public String article_del(
			HttpServletRequest request, 
			String mulitId,
			String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.redPigArticleService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:article_list?currentPage=" + currentPage;
	}
	
	/**
	 * 文章AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "文章AJAX更新", value = "/article_ajax*", rtype = "admin", rname = "文章管理", rcode = "article", rgroup = "网站")
	@RequestMapping({ "/article_ajax" })
	public void article_ajax(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String fieldName,
			String value) throws ClassNotFoundException {
		
		Article obj = this.redPigArticleService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = Article.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigArticleService.updateById(obj);
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
	 * 文章标识
	 * @param request
	 * @param response
	 * @param mark
	 * @param id
	 */
	@RequestMapping({ "/article_mark" })
	public void article_mark(HttpServletRequest request,
			HttpServletResponse response, String mark, String id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark.trim());
		params.put("id_no", CommUtil.null2Long(id));
		
		List<Article> arts = this.redPigArticleService.queryPageList(params);
		boolean ret = true;
		if (arts.size() > 0) {
			ret = false;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/article_show" })
	public void article_show(HttpServletResponse response,
			HttpServletRequest request, String id) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		String ret = "";
		Article article = this.redPigArticleService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (article != null) {
			if (article.getDisplay()) {
				article.setDisplay(false);
				ret = "off";
			} else {
				article.setDisplay(true);
				ret = "on";
			}
			this.redPigArticleService.updateById(article);
		} else {
			ret = "no";
		}
		try {
			response.getWriter().print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
