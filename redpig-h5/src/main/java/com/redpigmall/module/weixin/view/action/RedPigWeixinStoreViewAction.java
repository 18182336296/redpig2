package com.redpigmall.module.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserGoodsClass;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
/**
 * <p>
 * Title: RedPigWeixinStoreViewAction.java
 * </p>
 * 
 * <p>
 * Description: 微信店铺
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
 * @date 2017-4-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinStoreViewAction extends BaseAction{

	/**
	 * 手机店铺
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store" })
	public ModelAndView store(HttpServletRequest request,
			HttpServletResponse response, String id) {
		String serverName = request.getServerName().toLowerCase();
		String secondDomain = "";
		if (this.configService.getSysConfig().getSecond_domain_open()) {
			secondDomain = serverName.substring(0, serverName.indexOf("."));
		}
		Store store = null;
		if ((this.configService.getSysConfig().getSecond_domain_open())
				&& (serverName.indexOf(".") != serverName.lastIndexOf("."))
				&& (!secondDomain.equals("www"))) {
			store = this.storeService.getObjByProperty("store_second_domain","=", secondDomain);
		} else {
			store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		}
		if (store == null) {
			ModelAndView mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "不存在该店铺信息");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		ModelAndView mv = null;
		mv = new RedPigJModelAndView("/weixin/store_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("store", store);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("store_id", CommUtil.null2Long(id));
			
			List<Favorite> list = this.favoriteService.queryPageList(params);
			
			if (list.size() > 0) {
				mv.addObject("all_col", Integer.valueOf(1));
			}
		}
		if ((store.getStore_wap_decorate_info() != null)
				&& (!"".equals(store.getStore_wap_decorate_info()))) {
			String swd_info = store.getStore_wap_decorate_info();
			if ((swd_info != null) && (!"".equals(swd_info))) {
				List<Map> json_list = JSON.parseArray(swd_info, Map.class);
				mv.addObject("json_list", json_list);
				mv.addObject("weixinstoreViewTools", this.weixinstoreViewTools);
			}
		}
		if ((CommUtil.null2Int(store.getStore_status()) == 25)
				|| (CommUtil.null2Int(store.getStore_status()) == 26)) {
			mv = new RedPigJModelAndView("/weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺因为合同到期现已关闭，如有疑问请联系商城客服");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else if (store.getStore_status() < 15) {
			mv = new RedPigJModelAndView("/weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺未正常营业");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else if (store.getStore_status() == 20) {
			mv = new RedPigJModelAndView("/weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺因为违反商城相关规定现已关闭，如有疑问请联系商城客服");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 手机店铺中商品
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_goods_new" })
	public ModelAndView store_goods_new(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("/weixin/store_goods_new.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		List list = Lists.newArrayList();
		if (store != null) {
			mv.addObject("store", store);
			Map params = Maps.newHashMap();
			params.put("goods_store_id", store.getId());
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(2, -1);
			params.put("add_Time_more_than_equal", cal.getTime());
			params.put("goods_status", 0);
			
			List<Goods> date_list = this.goodsService.queryPageList(params);
			
			for (int i = 0; i < date_list.size(); i++) {
				Map map = Maps.newHashMap();
				params.clear();
				params.put("goods_store_id", store.getId());
				params.put("date_format_addTime", date_list.get(i).getAddTime());
				params.put("goods_status", 0);
				
				List goods_list = this.goodsService.queryPageList(params);
				
				map.put("date", date_list.get(i).getAddTime());
				map.put("goods_list", goods_list);
				list.add(map);
			}
		}
		mv.addObject("list", list);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_user_goods_class" })
	public ModelAndView store_user_goods_class(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"/weixin/store_user_goods_class.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (store != null) {
			Map params = Maps.newHashMap();
			params.put("user_id", store.getUser().getId());
			params.put("display", Boolean.valueOf(true));
			params.put("parent", -1);
			params.put("orderBy", "sequence");
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<UserGoodsClass> ugcs = this.userGoodsClassService.queryPageList(params);
			
			mv.addObject("ugcs", ugcs);
		}
		return mv;
	}
	
	/**
	 * 店铺活动
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_activity" })
	public ModelAndView store_activity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("/weixin/store_activity.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((id != null) && (!"".equals(id))) {
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (store != null) {
				mv.addObject("store", store);
				Map map = Maps.newHashMap();
				map.put("ag_goods_goods_store_id", store.getId());
				map.put("ag_status", 1);
				
				
				List<ActivityGoods> ags = this.activityGoodsService.queryPageList(map);
				
				Set ids = new HashSet();
				for (ActivityGoods ag : ags) {
					if (ag.getAct() != null) {
						ids.add(ag.getAct().getId());
					}
				}
				if (ids.size() > 0) {
					Map paras = Maps.newHashMap();
					paras.put("ids", ids);
					
					List list = this.activityService.queryPageList(paras);
					
					mv.addObject("objs", list);
				}
			}
		}
		return mv;
	}
	
	/**
	 * 活动商品列表
	 * @param request
	 * @param response
	 * @param act_id
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/activity_goods_list" })
	public ModelAndView activity_goods_list(HttpServletRequest request,
			HttpServletResponse response, String act_id, String id) {
		ModelAndView mv = new RedPigJModelAndView("/weixin/activity_goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Activity act = this.activityService.selectByPrimaryKey(CommUtil
				.null2Long(act_id));
		if ((id != null) && (!"".equals(id)) && (act != null)) {
			Map<String,Object> maps= this.redPigQueryTools.getParams(null,"addTime", "desc");
			maps.put("ag_goods_goods_store_id", CommUtil.null2Long(id));
			maps.put("act_id", CommUtil.null2Long(act_id));
			maps.put("ag_status", 1);
			
			IPageList pList = this.activityGoodsService.list(maps);
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		mv.addObject("act_id", act_id);
		if ((id != null) && (!"".equals(id))) {
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (store != null) {
				mv.addObject("store", store);
			}
		}
		return mv;
	}
	
	/**
	 * 添加店铺收藏
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/add_store_favorite" })
	public void add_store_favorite(HttpServletResponse response, String id) {
		Map map = Maps.newHashMap();
		int ret = 0;
		if (SecurityUserHolder.getCurrentUser() == null) {
			ret = 2;
		} else if (SecurityUserHolder.getCurrentUser().getStore() == this.storeService
				.selectByPrimaryKey(CommUtil.null2Long(id))) {
			ret = 3;
		} else {
			Map params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("store_id", CommUtil.null2Long(id));
			
			List<Favorite> list = this.favoriteService.queryPageList(params);
			
			if (list.size() == 0) {
				Favorite obj = new Favorite();
				obj.setAddTime(new Date());
				obj.setType(1);
				User user = SecurityUserHolder.getCurrentUser();
				Store store = this.storeService.selectByPrimaryKey(CommUtil
						.null2Long(id));
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
						store_addr =

						store.getArea().getParent().getAreaName() + store_addr;
						if (store.getArea().getParent().getParent() != null) {
							store_addr =

							store.getArea().getParent().getParent()
									.getAreaName()
									+ store_addr;
						}
					}
				}
				obj.setStore_ower(store.getUser().getUserName());
				obj.setStore_addr(store_addr);
				this.favoriteService.saveEntity(obj);
				store.setFavorite_count(store.getFavorite_count() + 1);
				map.put("count", Integer.valueOf(store.getFavorite_count()));
				this.storeService.updateById(store);
			} else {
				this.favoriteService
						.deleteById(((Favorite) list.get(0)).getId());
				boolean r = true;
				
				if (r) {
					Store store = this.storeService.selectByPrimaryKey(CommUtil
							.null2Long(id));
					if (store != null) {
						store.setFavorite_count(store.getFavorite_count() - 1);
						map.put("count",
								Integer.valueOf(store.getFavorite_count()));
						this.storeService.updateById(store);
						ret = 1;
					}
				}
			}
		}
		map.put("ret", Integer.valueOf(ret));
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 店铺分类列表
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_class_list" })
	public ModelAndView store_class_list(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("/weixin/store_class_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (store != null) {
			Map params = Maps.newHashMap();
			params.put("user_id", store.getUser().getId());
			params.put("display", Boolean.valueOf(true));
			params.put("parent", Boolean.valueOf(true));
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<UserGoodsClass> ugcs = this.userGoodsClassService.queryPageList(params);
			
			mv.addObject("store", store);
			mv.addObject("ugcs", ugcs);
		}
		return mv;
	}
	
	/**
	 * 店铺商品
	 * @param request
	 * @param response
	 * @param ugc_id
	 * @param store_id
	 * @param keyword
	 * @param orderBy
	 * @param orderType
	 * @param currentPage
	 * @param all
	 * @return
	 */
	@RequestMapping({ "/store_goods" })
	public ModelAndView store_goods(HttpServletRequest request,
			HttpServletResponse response, String ugc_id, String store_id,
			String keyword, String orderBy, String orderType,
			String currentPage, String all) {
		ModelAndView mv = new RedPigJModelAndView("weixin/store_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,12, orderBy, orderType);
		if (!"all".equals(all)) {
			mv = new RedPigJModelAndView("weixin/store_goods_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "goods_salenum";
		}
		if ((orderType == null) || (orderType.equals(""))) {
			orderType = "desc";
		}
		if ((currentPage == null) || ("".equals(currentPage))) {
			currentPage = "1";
		}
		Store store = this.storeService
				.selectByPrimaryKey(CommUtil.null2Long(store_id));
		if (store != null) {
			mv.addObject("store", store);
			maps.put("goods_store_id", store.getId());
			
			maps.put("goods_status", 0);
			if ((keyword != null) && (!keyword.equals(""))) {
				maps.put("goods_name_like", keyword);
				mv.addObject("keyword", keyword);
			}
			
			UserGoodsClass ugc = this.userGoodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(ugc_id));
			if (ugc != null) {
				mv.addObject("ugc", ugc);
				Set<Long> ids = genericUserGcIds(ugc);
				List<UserGoodsClass> ugc_list = Lists.newArrayList();
				for (Long g_id : ids) {
					UserGoodsClass temp_ugc = this.userGoodsClassService
							.selectByPrimaryKey(g_id);
					ugc_list.add(temp_ugc);
				}
				
				
				List<Long> goods_ugcs_ugc_ids = Lists.newArrayList();
				
				for (int i = 0; i < ugc_list.size(); i++) {
					UserGoodsClass userGoodsClass = ugc_list.get(i);
					goods_ugcs_ugc_ids.add(userGoodsClass.getId());
				}
				maps.put("goods_ugcs_ugc_ids", goods_ugcs_ugc_ids);
			}
			
			if ((orderBy != null) && (!orderBy.equals(""))) {
				mv.addObject("orderBy", orderBy);
				mv.addObject("orderType", orderType);
			}
			
			
			IPageList pList = this.goodsService.list(maps);
			mv.addObject("objs", pList.getResult());
			mv.addObject("totalPage", Integer.valueOf(pList.getPages()));
			String url = this.configService.getSysConfig().getAddress();
			CommUtil.saveIPageList2ModelAndView(url + "/goods_list", "",
					"", pList, mv);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 店铺商品
	 * @param request
	 * @param response
	 * @param ugc_id
	 * @param store_id
	 * @param keyword
	 * @param orderBy
	 * @param orderType
	 * @param currentPage
	 * @param all
	 * @return
	 */
	@RequestMapping({ "/store_items" })
	public ModelAndView store_items(HttpServletRequest request,
			HttpServletResponse response, String ugc_id, String store_id,
			String keyword, String orderBy, String orderType,
			String currentPage, String all) {
		ModelAndView mv = new RedPigJModelAndView("weixin/store_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,12, orderBy, orderType);
		
		if (!"all".equals(all)) {
			mv = new RedPigJModelAndView("weixin/store_goods_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "goods_salenum";
		}
		if ((orderType == null) || (orderType.equals(""))) {
			orderType = "desc";
		}
		if ((currentPage == null) || ("".equals(currentPage))) {
			currentPage = "1";
		}
		Store store = this.storeService
				.selectByPrimaryKey(CommUtil.null2Long(store_id));
		if (store != null) {
			mv.addObject("store", store);
			
			maps.put("goods_store_id", store.getId());
			
			maps.put("goods_status", 0);
			
			if ((keyword != null) && (!keyword.equals(""))) {
				maps.put("goods_name_like", keyword);
				mv.addObject("keyword", keyword);
			}
			
			UserGoodsClass ugc = this.userGoodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(ugc_id));
			if (ugc != null) {
				mv.addObject("ugc", ugc);
				Set<Long> ids = genericUserGcIds(ugc);
				List<UserGoodsClass> ugc_list = Lists.newArrayList();
				for (Long g_id : ids) {
					UserGoodsClass temp_ugc = this.userGoodsClassService
							.selectByPrimaryKey(g_id);
					ugc_list.add(temp_ugc);
				}
				
				List<Long> goods_ugcs_ugc_ids = Lists.newArrayList();
				
				for (int i = 0; i < ugc_list.size(); i++) {
					goods_ugcs_ugc_ids.add(ugc_list.get(i).getId());
				}
				maps.put("goods_ugcs_ugc_ids", goods_ugcs_ugc_ids);
				
			}
			
			IPageList pList = this.goodsService.list(maps);
			mv.addObject("objs", pList.getResult());
			mv.addObject("totalPage", Integer.valueOf(pList.getPages()));
			String url = this.configService.getSysConfig().getAddress();
			CommUtil.saveIPageList2ModelAndView(url + "/goods_list", "",
					"", pList, mv);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "请求参数错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_head" })
	public ModelAndView store_head(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("/weixin/store_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("store", store);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_foot" })
	public ModelAndView store_foot(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("/weixin/store_foot.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("store", store);
		return mv;
	}
	
	/**
	 * 
	 * @param ugc
	 * @return
	 */
	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set ids = new HashSet();
		ids.add(ugc.getId());
		for (UserGoodsClass child : ugc.getChilds()) {
			Set<Long> cids = genericUserGcIds(child);
			for (Long cid : cids) {
				ids.add(cid);
			}
			ids.add(child.getId());
		}
		return ids;
	}
}
