package com.redpigmall.manage.admin.action.website.articleClass;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
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
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.ArticleClass;

/**
 * 
 * <p>
 * Title: RedPigArticleClassManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商城会员管理
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
 * @date 2016年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigArticleClassManageAction extends BaseAction{
	
	/**
	 * 文章分类列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "文章分类列表", value = "/articleclass_list*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
	@RequestMapping({ "/articleclass_list" })
	public ModelAndView list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/articleclass_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("one_type", 1);
		List<ArticleClass> ac_list = this.articleClassService.queryPageList(params);
		
		if (ac_list.size() == 0) {
			this.articleTools.change();
		}
		
		params.clear();
		params = this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		params.put("parent", -1);
		
		IPageList pList = this.articleClassService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 文章分类添加
	 * @param request
	 * @param response
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "文章分类添加", value = "/articleclass_add*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
	@RequestMapping({ "/articleclass_add" })
	public ModelAndView add(
			HttpServletRequest request,
			HttpServletResponse response, 
			String pid) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/articleclass_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		
		List<ArticleClass> acs = this.articleClassService.queryPageList(params);
		
		if ((pid != null) && (!pid.equals(""))) {
			ArticleClass obj = new ArticleClass();
			obj.setParent(this.articleClassService.selectByPrimaryKey(Long.parseLong(pid)));
			
			mv.addObject("obj", obj);
			ArticleClass ac = this.articleClassService.selectByPrimaryKey(CommUtil.null2Long(pid));
			if (ac != null) {
				mv.addObject("ac", ac);
			}
		}
		mv.addObject("acs", acs);
		return mv;
	}
	
	/**
	 * 文章分类编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "文章分类编辑", value = "/articleclass_edit*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
	@RequestMapping({ "/articleclass_edit" })
	public ModelAndView edit(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/articleclass_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!id.equals(""))) {
			ArticleClass articleClass = this.articleClassService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			Map<String,Object> params = Maps.newHashMap();
			params.put("parent", -1);
			List<ArticleClass> acs = this.articleClassService.queryPageList(params);
			mv.addObject("acs", acs);
			mv.addObject("obj", articleClass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 文章分类保存
	 * @param request
	 * @param response
	 * @param id
	 * @param pid
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @return
	 */
	@SecurityMapping(title = "文章分类保存", value = "/articleclass_save*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
	@RequestMapping({ "/articleclass_save" })
	public ModelAndView saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String id, 
			String pid,
			String currentPage, 
			String cmd, 
			String list_url, 
			String add_url) {
		
		ArticleClass articleClass = null;
		if (id.equals("")) {
			articleClass = (ArticleClass) WebForm.toPo(request,
					ArticleClass.class);
			articleClass.setAddTime(new Date());
		} else {
			ArticleClass obj = this.articleClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			articleClass = (ArticleClass) WebForm.toPo(request, obj);
		}
		
		if ((pid != null) && (!pid.equals(""))) {
			ArticleClass parent = this.articleClassService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(pid)));
			articleClass.setParent(parent);
		}
		if (id.equals("")) {
			this.articleClassService.saveEntity(articleClass);
		} else {
			this.articleClassService.update(articleClass);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url + "?currentPage=" + currentPage);
		mv.addObject("op_title", "保存文章分类成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?pid=" + pid);
		}
		return mv;
	}

	private Set<Long> genericIds(ArticleClass ac) {
		Set<Long> ids = Sets.newHashSet();
		ids.add(ac.getId());
		for (ArticleClass child : ac.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
	
	/**
	 * 文章分类删除
	 * @param request
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "文章分类删除", value = "/articleclass_del*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
	@RequestMapping({ "/articleclass_del" })
	public String deleteById(HttpServletRequest request, String mulitId) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Set<Long> list = genericIds(this.articleClassService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id))));
				Map<String, Object> params = Maps.newHashMap();
				params.put("ids", list);
				List<ArticleClass> acs = this.articleClassService.queryPageList(params);
				
				for (ArticleClass ac : acs) {
					ac.setParent(null);
					this.articleClassService.deleteById(ac.getId());
				}
			}
		}
		return "redirect:articleclass_list";
	}
	
	/**
	 * 文章下级分类
	 * @param request
	 * @param response
	 * @param pid
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "文章下级分类", value = "/articleclass_data*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
	@RequestMapping({ "/articleclass_data" })
	public ModelAndView articleclass_data(
			HttpServletRequest request,
			HttpServletResponse response, 
			String pid, 
			String currentPage) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/articleclass_data.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("parent", Long.valueOf(Long.parseLong(pid)));
		
		List<ArticleClass> acs = this.articleClassService.queryPageList(map);
		
		mv.addObject("acs", acs);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 文章分类AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "文章分类AJAX更新", value = "/articleclass_ajax*", rtype = "admin", rname = "文章分类", rcode = "article_class", rgroup = "网站")
	@RequestMapping({ "/articleclass_ajax" })
	public void ajax(
			HttpServletRequest request, 
			HttpServletResponse response,
			String id, 
			String fieldName, 
			String value) throws ClassNotFoundException {
		
		ArticleClass ac = this.articleClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = ArticleClass.class.getDeclaredFields();
		Object val = ObjectUtils.setValue(fieldName, value, ac, fields);
		
		this.articleClassService.update(ac);
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
	 * 文章验证
	 * @param request
	 * @param response
	 * @param className
	 * @param id
	 */
	@RequestMapping({ "/articleclass_verify" })
	public void articleclass_verify(
			HttpServletRequest request,
			HttpServletResponse response, 
			String className, 
			String id) {
		
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("className", className);
		params.put("id_no", CommUtil.null2Long(id));
		List<ArticleClass> gcs = this.articleClassService.queryPageList(params);
		
		if ((gcs != null) && (gcs.size() > 0)) {
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
}
