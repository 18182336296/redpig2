package com.redpigmall.manage.buyer.action;

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
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Store;

/**
 * 
 * <p>
 * Title: RedPigFavoriteBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 收藏管理控制器，用来显示买家收藏的商品信息、店铺信息
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
 * @date 2014-8-8
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigFavoriteBuyerAction extends BaseAction{
	
	/**
	 * Favorite列表页
	 * 
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param request
	 * @return
	 */
	@SecurityMapping(title = "用户商品收藏", value = "/buyer/favorite_goods*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/favorite_goods" })
	public ModelAndView favorite_goods(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/favorite_goods.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("type", 0);
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());

		IPageList pList = this.favoriteService.list(maps);

		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_goods", "", params, pList, mv);
		mv.addObject("userTools", this.userTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}

	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "用户店铺收藏", value = "/buyer/favorite_store*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/favorite_store" })
	public ModelAndView favorite_store(HttpServletRequest request, HttpServletResponse response, String currentPage,
			String orderBy, String orderType) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/favorite_store.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);

		maps.put("type", 1);
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());

		IPageList pList = this.favoriteService.queryPagesWithNoRelations(maps);

		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_store", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 用户收藏删除
	 * 
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "用户收藏删除", value = "/buyer/favorite_del*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/favorite_del" })
	public String favorite_del(HttpServletRequest request, HttpServletResponse response, String mulitId,
			String currentPage, int type) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Favorite favorite = this.favoriteService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if ((favorite.getType() == 0) && (favorite.getGoods_id() != null)) {
					Goods goods = this.goodsService.selectByPrimaryKey(favorite.getGoods_id());
					goods.setGoods_collect(goods.getGoods_collect() - 1);
					this.goodsService.updateById(goods);
					
					this.goodsTools.updateGoodsLucene(goods);
				}
				if (favorite.getType() == 1) {
					Store store = this.storeService.selectByPrimaryKey(favorite.getStore_id());
					store.setFavorite_count(store.getFavorite_count() - 1);
					this.storeService.updateById(store);
				}
				this.favoriteService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		if (type == 0) {
			return "redirect:favorite_goods?currentPage=" + currentPage;
		}
		return "redirect:favorite_store?currentPage=" + currentPage;
	}
}
