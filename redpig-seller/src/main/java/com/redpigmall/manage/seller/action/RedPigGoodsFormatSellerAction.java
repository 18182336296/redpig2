package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.GoodsFormat;
import com.redpigmall.domain.Store;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsFormatSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商品顶部、底部版式管理控制器
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
 * @date 2014-10-19
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigGoodsFormatSellerAction extends BaseAction{
	/**
	 * 卖家商品版式列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "卖家商品版式列表", value = "/goods_format_list*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_format_list" })
	public ModelAndView goods_format_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_format_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		Store store = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()).getStore();
		maps.put("gf_store_id", store.getId());
		IPageList pList = this.goodsFormatService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 卖家商品版式添加
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "卖家商品版式添加", value = "/goods_format_add*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_format_add" })
	public ModelAndView goods_format_add(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_format_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 卖家商品版式编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家商品版式编辑", value = "/goods_format_edit*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_format_edit" })
	public ModelAndView goods_format_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/goods_format_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GoodsFormat obj = this.goodsFormatService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("obj", obj);
		return mv;
	}
	
	/**
	 * 卖家商品版式保存
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "卖家商品版式保存", value = "/goods_format_save*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_format_save" })
	public void goods_format_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		boolean ret = true;
		if (CommUtil.null2String(id).equals("")) {
			GoodsFormat obj = (GoodsFormat) WebForm.toPo(request,GoodsFormat.class);
			obj.setAddTime(new Date());
			Store store = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()).getStore();
			obj.setGf_store_id(store.getId());
			this.goodsFormatService.saveEntity(obj);
		} else {
			GoodsFormat obj = this.goodsFormatService.selectByPrimaryKey(CommUtil.null2Long(id));
			GoodsFormat gf = (GoodsFormat) WebForm.toPo(request, obj);
			this.goodsFormatService.updateById(gf);
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
	
	/**
	 * 卖家商品版式删除
	 * @param request
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家商品版式删除", value = "/goods_format_delete*", rtype = "seller", rname = "版式管理", rcode = "goods_format_seller", rgroup = "商品管理")
	@RequestMapping({ "/goods_format_delete" })
	public String goods_format_deleteById(HttpServletRequest request, String id,
			String currentPage) {
		if (!id.equals("")) {
			GoodsFormat obj = this.goodsFormatService.selectByPrimaryKey(CommUtil.null2Long(id));
			Store store = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()).getStore();
			if (obj.getGf_store_id().equals(store.getId())) {
				this.goodsFormatService.deleteById(CommUtil.null2Long(id));
			}
		}
		return "redirect:goods_format_list?currentPage=" + currentPage;
	}
}
