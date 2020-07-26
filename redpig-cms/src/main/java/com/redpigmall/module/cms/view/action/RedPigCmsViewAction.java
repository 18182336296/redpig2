package com.redpigmall.module.cms.view.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.cms.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.CmsIndexTemplate;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Information;
import com.redpigmall.domain.InformationClass;


/**
 * 
 * <p>
 * Title: RedPigInformationSellerManageAction.java
 * </p>
 * 
 * <p>
 * Description: 资讯前台控制器
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
 * @date 2014-12-4
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigCmsViewAction extends BaseAction{
	
	/**
	 * cms首页
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param classid
	 * @return
	 */
	@RequestMapping({ "/cms/index" })
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String classid) {
		ModelAndView mv = new RedPigJModelAndView("/cms/index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		Map<String, Object> map = Maps.newHashMap();
		map.put("status", Integer.valueOf(20));
		map.put("recommend", Integer.valueOf(1));
		
		List<Information> informations = this.informationService.queryPageList(map);
		
		if (informations.size() > 0) {
			mv.addObject("information", informations.get(0));
		}
		map.clear();
		map.put("whether_show", 1);
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		
		List<CmsIndexTemplate> templates = this.cmsIndexTemplateService.queryPageList(map);
		
		map.clear();
		map.put("status", Integer.valueOf(20));
		map.put("orderBy", "clicktimes");
		map.put("orderType", "desc");
		
		List<Information> hot_infors = this.informationService.queryPageList(map, 0,7);
		
		mv.addObject("hot_infors", hot_infors);
		mv.addObject("objs", templates);
		mv.addObject("cmsTools", this.cmsTools);
		mv.addObject("imageTools", this.imageTools);
		mv.addObject("gf_tools", this.gf_tools);
		mv.addObject("circleViewTools", this.circleViewTools);
		return mv;
	}
	
	/**
	 * cms列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param title
	 * @param author
	 * @return
	 */
	@RequestMapping({ "/cms/list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String title,
			String author) {
		ModelAndView mv = new RedPigJModelAndView("/cms/list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"sequence,addTime", "desc");
		maps.put("status", 20);
		
		if ((title != null) && (!title.equals(""))) {
			maps.put("title_like", title);
			mv.addObject("title", title);
		}
		
		if ((author != null) && (!author.equals(""))) {
			maps.put("author_like", author);
			mv.addObject("author", author);
		}
		
		String id = request.getParameter("id");
		mv.addObject("id", id);

		mv.addObject("imageTools", this.imageTools);
		if (!CommUtil.null2String(id).equals("")) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("ic_pid", CommUtil.null2Long(id));
			
			List<InformationClass> informationClasses = this.informationClassService.queryPageList(map);
			
			List<Long> ids = Lists.newArrayList();
			for (InformationClass ic : informationClasses) {
				ids.add(ic.getId());
			}
			ids.add(CommUtil.null2Long(id));
			map.clear();
			map.put("classIds", ids);
			map.put("orderBy", "clicktimes");
			map.put("orderType", "desc");
			
			List<Information> hot_infors = this.informationService.queryPageList(map,0,6);
			
			mv.addObject("hot_infors", hot_infors);
			
			maps.put("classIds", ids);
		} else {
			Map<String, Object> map = Maps.newHashMap();
			map.put("orderBy", "clicktimes");
			map.put("orderType", "desc");
			
			List<Information> hot_infors = this.informationService.queryPageList(map,0,6);
			
			mv.addObject("hot_infors", hot_infors);
		}
		
		IPageList pList = this.informationService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		if (this.configService.getSysConfig().getZtc_status()) {
			List<Goods> ztc_goods = null;
			Map<String,Object> ztc_map = Maps.newHashMap();
			ztc_map.put("ztc_status", Integer.valueOf(3));
			ztc_map.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map.put("ztc_gold_more_than", Integer.valueOf(0));
			
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				ztc_map.put("orderBy", "ztc_dredge_price");
				ztc_map.put("orderType", "desc");
		        
				ztc_goods = this.goodsService.queryPageList(ztc_map,0,6);
			}
			mv.addObject("ztc_goods", ztc_goods);
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List<Goods> ztc_goods = this.goodsService.queryPageList(params,0,6);
			
			mv.addObject("ztc_goods", ztc_goods);
		}
		mv.addObject("cmsTools", this.cmsTools);
		return mv;
	}
	
	/**
	 * cms顶部
	 * @param request
	 * @param response
	 * @param title
	 * @return
	 */
	@RequestMapping({ "/cms/top" })
	public ModelAndView top(HttpServletRequest request,
			HttpServletResponse response, String title) {
		ModelAndView mv = new RedPigJModelAndView("/cms/top.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("ic_pid", -1);
        maps.put("orderBy", "ic_sequence");
        maps.put("orderType", "asc");
        
		List<InformationClass> classes = this.informationClassService.queryPageList(maps, 0, 18);
		mv.addObject("classes", classes);
		String op = CommUtil.null2String(request.getAttribute("id"));
		InformationClass informationClass = this.informationClassService
				.selectByPrimaryKey(CommUtil.null2Long(op));
		if ((informationClass != null)
				&& (informationClass.getIc_pid() != null)) {
			op = CommUtil.null2String(informationClass.getIc_pid());
		}
		mv.addObject("title", title);
		mv.addObject("op", op);
		mv.addObject("cmsTools", this.cmsTools);
		return mv;
	}
	
	/**
	 * cms页脚
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/cms/footer" })
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("/cms/footer.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
}
