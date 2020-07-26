package com.redpigmall.manage.admin.action.deal.goodsCase;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;

import com.redpigmall.api.domain.virtual.SysMap;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCase;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.service.RedPigGoodsCaseService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsService;

/**
 * 
 * <p>
 * Title: RedPigGoodsCaseManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台橱窗管理控制器，用来管理首页等页面橱窗展示，首页橱窗展示位置在推荐商品通栏的tab页
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
 * @date 2014-9-16
 * 
 * @version redpigmall_b2b2c 2016
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigGoodsCaseManageAction extends BaseAction{
	
	
	/**
	 * 橱窗列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "橱窗列表", value = "/goods_case_list*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_list" })
	public ModelAndView goods_case_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_case_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String, Object> map = this.redPigQueryTools.getParams(currentPage, "sequence", "asc");
		
		IPageList pList = this.goodscaseService.list(map);
		CommUtil.saveIPageList2ModelAndView(url + "/goodscase_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 橱窗添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "橱窗添加", value = "/goods_case_add*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_add" })
	public ModelAndView goods_case_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_case_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 橱窗编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "橱窗编辑", value = "/goods_case_edit*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_edit" })
	public ModelAndView goods_case_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_case_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			GoodsCase goodscase = this.goodscaseService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			List list = JSON.parseArray(goodscase.getCase_content());
			mv.addObject("count", Integer.valueOf(list.size()));
			mv.addObject("obj", goodscase);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 橱窗保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param case_name
	 * @param display
	 * @param sequence
	 * @param case_id
	 * @param case_content
	 * @return
	 */
	@SecurityMapping(title = "橱窗保存", value = "/goods_case_save*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_save" })
	public ModelAndView goods_case_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String case_name,
			String display, String sequence, String case_id, String case_content) {
		GoodsCase goodscase = null;
		if (id.equals("")) {
			goodscase = new GoodsCase();
			goodscase.setAddTime(new Date());
		} else {
			goodscase = this.goodscaseService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		}
		goodscase.setDisplay(CommUtil.null2Boolean(display));
		
		goodscase.setCase_name(case_name);
		goodscase.setSequence(CommUtil.null2Int(sequence));
		goodscase.setCase_id(case_id);
		if ((case_content != null) && (!case_content.equals(""))) {
			List list = Lists.newArrayList();

			for (String str : case_content.split(",")) {

				if ((str != null) && (!str.equals(""))) {
					list.add(CommUtil.null2Long(str));
				}
			}
			Map<String, Object> map = Maps.newHashMap();
			map.put("ids", list);
			goodscase.setCase_content(JSON.toJSONString(list));
		}
		if (id.equals("")) {
			this.goodscaseService.saveEntity(goodscase);
		} else {
			this.goodscaseService.updateById(goodscase);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存橱窗成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 橱窗删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "橱窗删除", value = "/goods_case_del*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_del" })
	public String goods_case_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		
		this.goodscaseService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		
		return "redirect:goods_case_list?currentPage=" + currentPage;
	}
	
	/**
	 * 橱窗Ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "橱窗Ajax更新", value = "/goods_case_del*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_ajax" })
	public void goods_case_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		
		GoodsCase obj = this.goodscaseService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = GoodsCase.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.goodscaseService.updateById(obj);
		
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
	 * 橱窗商品添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param goods_ids
	 * @return
	 */
	@SecurityMapping(title = "橱窗商品添加", value = "/goods_case_items*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_items" })
	public ModelAndView goods_case_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String goods_ids) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_case_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((goods_ids != null) && (!goods_ids.equals(""))) {
			List<Goods> goods_list = Lists.newArrayList();
			String[] ids = goods_ids.split(",");

			for (String gid : ids) {

				if (!gid.equals("")) {
					Goods obj = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(gid));
					goods_list.add(obj);
				}
			}
			mv.addObject("goods_list", goods_list);
		} else if ((id != null) && (!id.equals(""))) {
			List<Goods> goods_list = Lists.newArrayList();
			GoodsCase goodscase = this.goodscaseService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			List list = JSON.parseArray(goodscase.getCase_content());
			for (Object obj : list) {
				Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
						.null2Long(obj));
				goods_list.add(goods);
			}
			mv.addObject("goods_list", goods_list);
		}
		mv.addObject("goods_ids", goods_ids);
		mv.addObject("id", id);
		return mv;
	}
	
	/**
	 * 商品分类异步加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品分类异步加载", value = "/goods_case_gc*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_gc" })
	public ModelAndView goods_case_gc(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_case_gc.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps = Maps.newHashMap();
        
        maps.put("parent", -1);
        maps.put("orderBy", "sequence");
        maps.put("orderType", "asc");
        
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(maps);
		
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 商品加载
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gc_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "商品加载", value = "/goods_case_goods_load*", rtype = "admin", rname = "橱窗管理", rcode = "goods_case", rgroup = "装修")
	@RequestMapping({ "/goods_case_goods_load" })
	public ModelAndView goods_case_goods_load(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gc_id,
			String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_case_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, 7,"addTime", "desc");
        maps.put("gc_type", 1);
        maps.put("parent", -1);
        maps.put("orderBy", "addTime");
        maps.put("orderType", "asc");

		
		if (!CommUtil.null2String(gc_id).equals("")) {
			Set<Long> ids = genericIds(this.goodsClassService
					.selectByPrimaryKey(CommUtil.null2Long(gc_id)));
			
			maps.put("gc_ids", ids);
			
		}
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("goods_name_like",goods_name);
		}
		
		maps.put("goods_status",0);
		IPageList pList = this.goodsService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request)
				+ "/goods_case_goods_load.html", "", "&gc_id=" + gc_id
				+ "&goods_name=" + goods_name, pList, mv);
		return mv;
	}

	private Set<Long> genericIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		
		ids.add(gc.getId());
		for (GoodsClass child : gc.getChilds()) {
			Set<Long> cids = genericIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
}
