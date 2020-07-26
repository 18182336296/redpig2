package com.redpigmall.manage.admin.action.website.navigation;

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
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Navigation;
import com.redpigmall.domain.Subject;

/**
 * 
 * <p>
 * Title: RedPigNavigationManageAction.java
 * </p>
 * 
 * <p>
 * Description:系统网站导航管理类
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigNavigationManageAction extends  BaseAction{

	/**
	 * 页面导航列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param title
	 * @return
	 */
	@SecurityMapping(title = "页面导航列表", value = "/navigation_list*", rtype = "admin", rname = "页面导航", rcode = "nav_manage", rgroup = "网站")
	@RequestMapping({ "/navigation_list" })
	public ModelAndView navigation_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String title) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/navigation_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		String url = this.redPigSysConfigService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		
		if (!CommUtil.null2String(title).equals("")) {
			maps.put("title_like", title);
		}
		
		IPageList pList = this.redPigNavigationService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/navigation_list", "",
				params, pList, mv);
		mv.addObject("title", title);
		return mv;
	}
	
	/**
	 * 页面导航添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "页面导航添加", value = "/navigation_add*", rtype = "admin", rname = "页面导航", rcode = "nav_manage", rgroup = "网站")
	@RequestMapping({ "/navigation_add" })
	public ModelAndView navigation_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/navigation_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = Maps.newHashMap();
		params.put("parent", -1);
		List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
		
		params.clear();
		params.put("parent", -1);
		List<ArticleClass> acs = this.articleClassService.queryPageList(params);
		
		
		params.clear();
		List<Activity> activitys = this.redPigActivityService.queryPageList(params);
		
		params.clear();
		params.put("display", Integer.valueOf(1));
		List<Subject> subjects = this.redPigSubjectService.queryPageList(params);
		
		Navigation obj = new Navigation();
		obj.setDisplay(true);
		obj.setType("diy");
		obj.setNew_win(1);
		obj.setLocation(0);
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		mv.addObject("gcs", gcs);
		mv.addObject("acs", acs);
		mv.addObject("activitys", activitys);
		mv.addObject("subjects", subjects);
		return mv;
	}
	
	/**
	 * 页面导航编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "页面导航编辑", value = "/navigation_edit*", rtype = "admin", rname = "页面导航", rcode = "nav_manage", rgroup = "网站")
	@RequestMapping({ "/navigation_edit" })
	public ModelAndView navigation_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/navigation_add.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Navigation navigation = this.redPigNavigationService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			Map<String,Object> params = Maps.newHashMap();
			
			params.put("parent", -1);
			
			List<GoodsClass> gcs = this.redPigGoodsClassService.queryPageList(params);
			
			params.clear();
			params.put("parent", -1);
			List<ArticleClass> acs = this.articleClassService.queryPageList(params);
			
			
			params.clear();
			List<Activity> activitys = this.redPigActivityService.queryPageList(params);
			
			params.clear();
			params.put("display", Integer.valueOf(1));
			List<Subject> subjects = this.redPigSubjectService.queryPageList(params);
			
			mv.addObject("subjects", subjects);
			mv.addObject("gcs", gcs);
			mv.addObject("acs", acs);
			mv.addObject("activitys", activitys);
			mv.addObject("obj", navigation);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 页面导航保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param goodsclass_id
	 * @param articleclass_id
	 * @param activity_id
	 * @param subject_id
	 * @return
	 */
	@SecurityMapping(title = "页面导航保存", value = "/navigation_save*", rtype = "admin", rname = "页面导航", rcode = "nav_manage", rgroup = "网站")
	@RequestMapping({ "/navigation_save" })
	public ModelAndView navigation_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String goodsclass_id,
			String articleclass_id, String activity_id, String subject_id) {
		
		Navigation nav = null;
		if (id.equals("")) {
			nav = (Navigation) WebForm.toPo(request, Navigation.class);
			nav.setAddTime(new Date());
		} else {
			Navigation obj = this.redPigNavigationService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			nav = (Navigation) WebForm.toPo(request, obj);
		}
		nav.setOriginal_url(nav.getUrl());
		if (nav.getType().equals("goodsclass")) {
			nav.setType_id(CommUtil.null2Long(goodsclass_id));
			nav.setUrl("store_goods_list_" + goodsclass_id + "");
			nav.setOriginal_url("store_goods_list?gc_id=" + goodsclass_id);
		}
		if (nav.getType().equals("articleclass")) {
			nav.setType_id(CommUtil.null2Long(articleclass_id));
			nav.setUrl("articlelist_help_" + articleclass_id + "");
			nav.setOriginal_url("articlelist?param=" + articleclass_id);
		}
		if (nav.getType().equals("activity")) {
			nav.setType_id(CommUtil.null2Long(activity_id));
			nav.setUrl("activity/index_" + activity_id + "");
			nav.setOriginal_url("activity?id=" + activity_id);
		}
		if (nav.getType().equals("subject")) {
			nav.setType_id(CommUtil.null2Long(subject_id));
			nav.setUrl("subject/view_" + subject_id + "");
			nav.setOriginal_url("subject/view?id=" + subject_id);
		}
		if (id.equals("")) {
			this.redPigNavigationService.saveEntity(nav);
		} else {
			this.redPigNavigationService.updateById(nav);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存页面导航成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 页面导航删除
	 * @param request
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "页面导航删除", value = "/navigation_del*", rtype = "admin", rname = "页面导航", rcode = "nav_manage", rgroup = "网站")
	@RequestMapping({ "/navigation_del" })
	public String navigation_del(HttpServletRequest request, String mulitId) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				this.redPigNavigationService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:navigation_list";
	}
	
	/**
	 * 页面导航AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "页面导航AJAX更新", value = "/navigation_ajax*", rtype = "admin", rname = "页面导航", rcode = "nav_manage", rgroup = "网站")
	@RequestMapping({ "/navigation_ajax" })
	public void navigation_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		Navigation obj = this.redPigNavigationService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = Navigation.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.redPigNavigationService.updateById(obj);
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
}
