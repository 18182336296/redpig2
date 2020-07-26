package com.redpigmall.manage.admin.action.self.goodsFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.GoodsFormat;

/**
 * 
 * <p>
 * Title: RedPigSelfGoodsFormatManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商品版式管理控制器，用来管理自营商品的版式
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
 * @date 2014-10-29
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigSelfGoodsFormatManageAction extends BaseAction{
	/**
	 * 商品版式列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "商品版式列表", value = "/goods_format_list*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping({ "/goods_format_list" })
	public ModelAndView goods_format_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_format_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("gf_cat", 1);
		
		IPageList pList = this.goodsFormatService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 商品版式添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "商品版式添加", value = "/goods_format_add*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping({ "/goods_format_add" })
	public ModelAndView goods_format_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_format_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
	
	/**
	 * 商品版式编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "商品版式编辑", value = "/goods_format_edit*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping({ "/goods_format_edit" })
	public ModelAndView goods_format_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_format_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsFormat obj = this.goodsFormatService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}
	
	/**
	 * 商品版式保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品版式保存", value = "/goods_format_save*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping({ "/goods_format_save" })
	public ModelAndView goods_format_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		
		if (CommUtil.null2String(id).equals("")) {
			GoodsFormat obj = (GoodsFormat) WebForm.toPo(request,GoodsFormat.class);
			obj.setAddTime(new Date());
			obj.setGf_cat(1);
			this.goodsFormatService.saveEntity(obj);
		} else {
			GoodsFormat obj = this.goodsFormatService.selectByPrimaryKey(CommUtil.null2Long(id));
			GoodsFormat gf = (GoodsFormat) WebForm.toPo(request, obj);
			this.goodsFormatService.updateById(gf);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/goods_format_list?currentPage=" + currentPage);
		mv.addObject("op_title", "商品版式保存成功");
		return mv;
	}
	
	/**
	 * 商品版式删除
	 * @param request
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "商品版式删除", value = "/goods_format_delete*", rtype = "admin", rname = "商品版式", rcode = "goods_format_self", rgroup = "自营")
	@RequestMapping({ "/goods_format_delete" })
	public String goods_format_delete(HttpServletRequest request,
			String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		List<Long> gfIds = Lists.newArrayList();
		for (String id : ids) {

			if (!id.equals("")) {
				gfIds.add(Long.valueOf(id.trim()));
			}
		}
		
		this.goodsFormatService.batchDeleteByIds(gfIds);
		
		return "redirect:goods_format_list?currentPage=" + currentPage;
	}
}
