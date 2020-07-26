package com.redpigmall.manage.admin.action.operative.enoughReduce;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.EnoughReduce;

/**
 * 
 * <p>
 * Title: RedPigEnoughReduceManageAction.java
 * </p>
 * 
 * <p>
 * Description: 满就减控制器，对整个平台的满就减活动进行管理
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
 * @author lixiaoyang
 * 
 * @date 2014-9-22
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigEnoughReduceManageAction extends BaseAction {

	/**
	 * 满就减活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ertitle
	 * @param erstatus
	 * @param erbegin_time
	 * @param erend_time
	 * @return
	 */
	@SecurityMapping(title = "满就减活动列表", value = "/enoughreduce_list*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_admin", rgroup = "运营")
	@RequestMapping({ "/enoughreduce_list" })
	public ModelAndView enoughreduce_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String orderBy,
			String orderType, 
			String ertitle, 
			String erstatus,
			String erbegin_time, 
			String erend_time) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/enoughreduce_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage,  "addTime", "desc");
		
		if ((ertitle != null) && (!"".equals(ertitle))) {
			params.put("ertitle", ertitle);
			mv.addObject("ertitle", ertitle);
		}
		
		if ((erstatus != null) && (!"".equals(erstatus))) {
			params.put("erstatus", CommUtil.null2Int(erstatus));
			
			mv.addObject("erstatus", erstatus);
		}
		
		if ((erbegin_time != null) && (!erbegin_time.equals(""))) {
			params.put("erbegin_time_more_than_equal", erbegin_time);
			
			mv.addObject("erbegin_time", erbegin_time);
		}
		
		if ((erend_time != null) && (!erend_time.equals(""))) {
			params.put("erend_time_less_than_equal", erend_time);
			
			mv.addObject("erend_time", erend_time);
		}
		IPageList pList = this.redPigEnoughreduceService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", null, pList, mv);
		return mv;
	}

	/**
	 * 满就减活动商品列表
	 * @param request
	 * @param response
	 * @param er_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "满就减活动商品列表", value = "/enoughreduce_goods_list*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_admin", rgroup = "运营")
	@RequestMapping({ "/enoughreduce_goods_list" })
	public ModelAndView enoughreduce_goods_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String er_id, 
			String currentPage,
			String orderBy, 
			String orderType) {
		
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/enoughreduce_goods.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		
		EnoughReduce er = this.redPigEnoughreduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if ((er_id != null) && (!"".equals(er_id))) {
			params.put("ids", genericIds(er.getErgoods_ids_json()));
		}
		
		IPageList pList = this.redPigGoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		
		mv.addObject("er", er);
		return mv;
	}
	
	/**
	 * 满就减活动保存
	 * @param request
	 * @param response
	 * @param er_id
	 * @param erstatus
	 * @param list_url
	 * @param failed_reason
	 * @return
	 */
	@SecurityMapping(title = "满就减活动保存", value = "/enoughreduce_verify*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_admin", rgroup = "运营")
	@RequestMapping({ "/enoughreduce_verify" })
	public ModelAndView enoughreduce_verify(
			HttpServletRequest request,
			HttpServletResponse response, 
			String er_id, 
			String erstatus,
			String list_url, 
			String failed_reason) {
		
		EnoughReduce enoughreduce = null;
		if ((er_id != null) && (!er_id.equals(""))) {
			enoughreduce = this.redPigEnoughreduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
			
			if (CommUtil.null2Int(erstatus) == -10) {
				enoughreduce.setErstatus(CommUtil.null2Int(erstatus));
				enoughreduce.setFailed_reason(failed_reason);
			}
			if (CommUtil.null2Int(erstatus) == 10) {
				enoughreduce.setErstatus(CommUtil.null2Int(erstatus));
				enoughreduce.setFailed_reason("");
			}
			this.redPigEnoughreduceService.updateById(enoughreduce);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "审核完成");
		return mv;
	}

	@SuppressWarnings("rawtypes")
	private Set<Long> genericIds(String str) {
		Set<Long> ids = Sets.newHashSet();
		List list = JSON.parseArray(str);
		for (Object object : list) {
			ids.add(CommUtil.null2Long(object));
		}
		return ids;
	}
}
