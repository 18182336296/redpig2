package com.redpigmall.manage.admin.action.self.group;

import java.util.Date;
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
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.GroupPriceRange;

/**
 * 
 * <p>
 * Title: GroupPriceRangeManageAction.java
 * </p>
 * 
 * <p>
 * Description:团购价格区间管理类
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
 * @date 2014年5月27日
 * 
 * @version 1.0
 */
@Controller
public class RedPigGroupPriceRangeManageAction extends BaseAction{
	/**
	 * 团购价格区间列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "团购价格区间列表", value = "/group_price_list*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_price_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_price_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.grouppricerangeService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView(url
				+ "/group_range_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 团购价格区间列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购价格区间列表", value = "/group_price_add*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_price_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_price_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 团购价格区间列表
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购价格区间列表", value = "/group_price_edit*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_price_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_price_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			GroupPriceRange grouppricerange = this.grouppricerangeService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", grouppricerange);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 团购价格区间保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @return
	 */
	@SecurityMapping(title = "团购价格区间保存", value = "/group_price_save*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_price_save" })
	public ModelAndView saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd) {
		
		GroupPriceRange grouppricerange = null;
		if (id.equals("")) {
			grouppricerange = (GroupPriceRange) WebForm.toPo(request,
					GroupPriceRange.class);
			grouppricerange.setAddTime(new Date());
		} else {
			GroupPriceRange obj = this.grouppricerangeService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			grouppricerange = (GroupPriceRange) WebForm.toPo(request, obj);
		}
		if (id.equals("")) {
			this.grouppricerangeService.saveEntity(grouppricerange);
		} else {
			this.grouppricerangeService.updateById(grouppricerange);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/group_price_list");
		mv.addObject("op_title", "保存价格区间成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/group_price_add" + "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 团购价格区间删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购价格区间删除", value = "/group_price_del*", rtype = "admin", rname = "团购管理", rcode = "group_admin", rgroup = "运营")
	@RequestMapping({ "/group_price_del" })
	public String deleteById(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		
		this.grouppricerangeService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		
		return "redirect:group_price_list?currentPage=" + currentPage;
	}
}
