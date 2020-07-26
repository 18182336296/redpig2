package com.redpigmall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigRedPigFavoriteViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商城前台收藏控制器，用来添加商品、店铺收藏
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
 * @date 2014-4-30
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Controller
public class RedPigFavoriteViewAction extends BaseAction{
	
	/**
	 * 添加商品收藏
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/add_goods_favorite" })
	public void add_goods_favorite(HttpServletResponse response, String id) {
		Long goods_id = CommUtil.null2Long(id);
		User user = SecurityUserHolder.getCurrentUser();
		int ret = 0;
		if (user != null) {
			Map<String,Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			params.put("goods_id", goods_id);
			
			List<Favorite> list = this.favoriteService.queryPageList(params);
			
			if (list.size() == 0) {
				Goods goods = this.goodsService.selectByPrimaryKey(goods_id);
				Favorite obj = new Favorite();
				obj.setAddTime(new Date());
				obj.setType(0);
				obj.setUser_name(user.getUserName());
				obj.setUser_id(user.getId());
				obj.setGoods_id(goods.getId());
				obj.setGoods_name(goods.getGoods_name());
				obj.setGoods_photo(goods.getGoods_main_photo().getPath() + "/"
						+ goods.getGoods_main_photo().getName());
				obj.setGoods_photo_ext(goods.getGoods_main_photo().getExt());
				obj.setGoods_store_id(goods.getGoods_store() == null ? null
						: goods.getGoods_store().getId());
				obj.setGoods_type(goods.getGoods_type());
				obj.setGoods_current_price(goods.getGoods_current_price());
				if (this.configService.getSysConfig().getSecond_domain_open()) {
					Store store = this.storeService.selectByPrimaryKey(obj
							.getStore_id());
					obj.setGoods_store_second_domain(store
							.getStore_second_domain());
				}
				this.favoriteService.saveEntity(obj);
				goods.setGoods_collect(goods.getGoods_collect() + 1);
				this.goodsService.updateById(goods);
				GoodsLog todayGoodsLog = this.goodsTools.getTodayGoodsLog(obj
						.getId().longValue());
				todayGoodsLog
						.setGoods_collect(todayGoodsLog.getGoods_collect() + 1);
				this.goodsLogService.updateById(todayGoodsLog);
				this.goodsTools.updateGoodsLucene(goods);
			} else {
				ret = 1;
			}
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
	 * 添加店铺收藏
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/add_store_favorite" })
	public void add_store_favorite(HttpServletResponse response, String id) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("store_id", CommUtil.null2Long(id));
		List<Favorite> list = this.favoriteService.queryPageList(params);
		
		int ret = 0;
		if (list.size() == 0) {
			Favorite obj = new Favorite();
			obj.setAddTime(new Date());
			obj.setType(1);
			User user = SecurityUserHolder.getCurrentUser();
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
			obj.setUser_id(user.getId());
			obj.setStore_id(store.getId());
			obj.setStore_name(store.getStore_name());
			obj.setStore_photo(store.getStore_logo() != null ? store
					.getStore_logo().getPath()
					+ "/"
					+ store.getStore_logo().getName() : null);
			if (this.configService.getSysConfig().getSecond_domain_open()) {
				obj.setStore_second_domain(store.getStore_second_domain());
			}
			String store_addr = "";
			if (store.getArea() != null) {
				store_addr = store.getArea().getAreaName()
						+ store.getStore_address();
				if (store.getArea().getParent() != null) {
					store_addr = store.getArea().getParent().getAreaName()
							+ store_addr;
					if (store.getArea().getParent().getParent() != null) {
						store_addr =

						store.getArea().getParent().getParent().getAreaName()
								+ store_addr;
					}
				}
			}
			obj.setStore_ower(store.getUser().getUserName());
			obj.setStore_addr(store_addr);
			this.favoriteService.saveEntity(obj);
			store.setFavorite_count(store.getFavorite_count() + 1);
			this.storeService.updateById(store);
		} else {
			ret = 1;
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
