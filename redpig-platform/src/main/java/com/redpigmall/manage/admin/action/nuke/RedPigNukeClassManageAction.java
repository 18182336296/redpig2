package com.redpigmall.manage.admin.action.nuke;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.NukeClass;
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * Title: RedPigNukeClassManageAction.java
 * 
 * Description:秒杀分类
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月31日 上午11:52:00
 * 
 * @version redpigmall_b2b2c v8.0 2018版  
 */
@Controller
public class RedPigNukeClassManageAction extends BaseAction {
	/**
	 * 秒杀分类列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param type
	 * @return
	 */
	@RequestMapping({ "/nuke_class_list" })
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType, String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_class_list.html", 
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, "nk_sequence", "asc");
		
		maps.put("parent", -1);
		
		IPageList pList = this.nukeClassService.queryPagesWithNoRelations(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/nuke/nuke_class_list.html", "", params, pList, mv);
		
		return mv;
	}
	
	
	/**
	 * 秒杀分类增加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param pid
	 * @param type
	 * @return
	 */
	@RequestMapping({ "/nuke_class_add" })
	public ModelAndView nuke_class_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String pid,
			String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_class_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps = Maps.newHashMap();
		maps.put("parent", -1);
		
		List<NukeClass> ncs = this.nukeClassService.queryPageList(maps);
		
		NukeClass obj = new NukeClass();
		
		mv.addObject("obj", obj);
		mv.addObject("ncs", ncs);
		mv.addObject("currentPage", currentPage);
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 
	 * nuke_class_save:秒杀分类保存. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param pid
	 * @param nk_recommend
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/nuke_class_save" })
	public ModelAndView nuke_class_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String pid,  String nk_recommend) {
		
		NukeClass nukeClass = null;
		if (id.equals("")) {
			nukeClass = (NukeClass) WebForm.toPo(request, NukeClass.class);
			nukeClass.setAddTime(new Date());
		} else {
			NukeClass obj = this.nukeClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			nukeClass = (NukeClass) WebForm.toPo(request, obj);
		}
		
		NukeClass parent = this.nukeClassService.selectByPrimaryKey(CommUtil.null2Long(pid));
		if (parent != null) {
			
			nukeClass.setNk_level(parent.getNk_level() + 1);
		}
		
		if (id.equals("")) {
			this.nukeClassService.saveEntity(nukeClass);
		} else {
			this.nukeClassService.updateById(nukeClass);
		}
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("list_url", CommUtil.getURL(request) + "/nuke_class_list");
		
		mv.addObject("op_title", "保存秒杀分类成功");
		mv.addObject("add_url", CommUtil.getURL(request) + "/nuke_class_add" + "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 
	 * edit:秒杀编辑. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping({ "/nuke_class_edit" })
	public ModelAndView nuke_class_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/nuke/nuke_class_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			NukeClass nukeClass = this.nukeClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
	         
			mv.addObject("obj", nukeClass);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	
}

