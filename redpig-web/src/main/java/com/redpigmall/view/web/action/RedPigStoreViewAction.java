package com.redpigmall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.ip.IPSeeker;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Information;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.domain.StoreNavigation;
import com.redpigmall.domain.StoreSlide;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserGoodsClass;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title: RedPigRedPigStoreViewAction.java
 * </p>
 * 
 * <p>
 * Description: 前端店铺控制器
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
 * @date 2014年4月24日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigStoreViewAction extends BaseAction{
	
	/**
	 * 店铺首页
	 * @param request
	 * @param response
	 * @param a_id
	 * @param gc_id
	 * @param store_name
	 * @param currentPage
	 * @param grade_id
	 * @param t
	 * @param favorite
	 * @param service
	 * @param gb_id
	 * @return
	 */
	@RequestMapping({ "/store/index" })
	public ModelAndView store_list(HttpServletRequest request,
			HttpServletResponse response, String a_id, String gc_id,
			String store_name, String currentPage, String grade_id, String t,
			String favorite, String service, String gb_id) {
		ModelAndView mv = new RedPigJModelAndView("store_lists.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, null, null);
		maps.put("store_status", 15);
		
		Map<String,Object> params = Maps.newHashMap();
		
		Map map_service = Maps.newHashMap();
		int start = 0;
		int page_size = 12;
		int end = start + page_size;
		
		if (currentPage != null) {
			start += (CommUtil.null2Int(currentPage) - 1) * page_size;
			end += (CommUtil.null2Int(currentPage) - 1) * page_size;
		}
		
		if ((!"".equals(grade_id)) && (grade_id != null)) {
			maps.put("grade_id", CommUtil.null2Long(grade_id));
			mv.addObject("grade_id", grade_id);
			params.put("grade_id", CommUtil.null2Long(grade_id));
			map_service.put("grade_id", CommUtil.null2Long(grade_id));
		}
		
		if (((store_name != null ? 1 : 0) & ("".equals(store_name) ? 0 : 1)) != 0) {
			maps.put("store_name_like", store_name);
			mv.addObject("store_name", store_name);
			params.put("store_name_like", store_name);
			map_service.put("store_name_like", store_name);
			
		}
		if ((!"".equals(gc_id)) && (gc_id != null)) {
			maps.put("gc_main_id", CommUtil.null2Long(gc_id));
			mv.addObject("gc_id", gc_id);
			params.put("gc_main_id", CommUtil.null2Long(gc_id));
			map_service.put("gc_main_id", CommUtil.null2Long(gc_id));
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			mv.addObject("gc", gc);
		}
		
		if ((gb_id != null) && (!"".equals(gb_id))) {
			GoodsBrand gb = this.goodsBrandService.selectByPrimaryKey(CommUtil.null2Long(gb_id));
			GoodsClass gc = gb.getGc();
			if (gc != null) {
				maps.put("gc_main_id", CommUtil.null2Long(gc_id));
				mv.addObject("gb_id", gb_id);
				mv.addObject("gb", gb);
				params.put("gc_main_id", CommUtil.null2Long(gc_id));
				map_service.put("gc_main_id", gc.getId());
			}
		}
		
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(a_id));
		if ((!"".equals(a_id)) && (a_id != null)) {
			if (area.getLevel() == 0) {
				maps.put("area_parent_parent_id", CommUtil.null2Long(gc_id));
				mv.addObject("area", area);
				params.put("area_parent_parent_id", CommUtil.null2Long(gc_id));
				map_service.put("area_parent_parent_id", CommUtil.null2Long(a_id));
			}
			
			if (area.getLevel() == 1) {
				maps.put("area_parent_parent_id", Long.parseLong(a_id));
				mv.addObject("area", area);
				params.put("area_parent_parent_id", Long.parseLong(a_id));
				map_service.put("area_parent_parent_id", CommUtil.null2Long(a_id));
			}
			
			if (area.getLevel() == 2) {
				maps.put("area_id", Long.parseLong(a_id));
				mv.addObject("area", area);
				params.put("area_id", Long.parseLong(a_id));
				map_service.put("area_id", CommUtil.null2Long(a_id));
			}
		} else {
			List<Area> area_list = null;
			String current_ip = CommUtil.getIpAddr(request);
			if (CommUtil.isIp(current_ip)) {
				IPSeeker ip = new IPSeeker(null, null);
				String current_city = ip.getIPLocation(current_ip).getCountry();
				Map<String, Object> map = Maps.newHashMap();
				map.put("areaName_like", current_city);
				
				area_list = this.areaService.queryPageList(map);
				
				if (area_list.size() > 0) {
					Area curreent_city = (Area) area_list.get(0);
					maps.put("area_parent_id", curreent_city.getId());
					
					mv.addObject("area", curreent_city);
					
					params.put("area_parent_id", curreent_city.getId());
					
					map_service.put("area_id",CommUtil.null2Long(curreent_city.getId()));
				}
			}
		}
		
		if ("point".equals(t)) {
			maps.put("orderByPoint", "point.store_evaluate");
			maps.put("orderType", "desc");
			mv.addObject("t", t);
			params.put("orderByPoint", "point.store_evaluate");
			params.put("orderType", "desc");
		}
		
		if ("favorite".equals(t)) {
			maps.put("orderBy", "favorite_count");
			maps.put("orderType", "desc");
			mv.addObject("t", t);
			params.put("orderBy", "point.store_evaluate");
			params.put("orderType", "desc");
		}
		
		if ("store_credit".equals(t)) {
			maps.put("orderBy", "store_credit");
			maps.put("orderType", "desc");
			mv.addObject("t", t);
			params.put("orderBy", "store_credit");
			params.put("orderType", "desc");
		}
		
		List<Store> objs_all = Lists.newArrayList();
		List<Store> objs = Lists.newArrayList();
		if (("no".equals(service)) && (service != null)) {
			List<Store> store_all = this.storeService.queryPageList(map_service);
			
			for (Store store : store_all) {
				boolean ret = this.userTools.userOnLine(store.getUser().getUserName());
				if (ret) {
					objs_all.add(store);
				}
			}
			if (objs_all.size() >= page_size) {
				objs = objs_all.subList(start, end);
			} else {
				objs = objs_all;
			}
		}
		if ("no".equals(service)) {
			mv.addObject("objs", objs);
		}
		mv.addObject("service", service);
		List<Store> store_list = Lists.newArrayList();
		if ((user != null) && (area != null) && (area.getLevel() == 2)) {
			Map f_map = Maps.newHashMap();
			f_map.put("user_id", user.getId());
			f_map.put("type", 1);
			
			List<Favorite> f_list = this.favoriteService.queryPageList(f_map);
			
			int f_size = f_list.size();
			for (Favorite f : f_list) {
				Store store = this.storeService.selectByPrimaryKey(f.getStore_id());
				if (CommUtil.null2Int(store.getStore_status()) == 15) {
					store_list.add(store);
				}
				if ((store.getArea() != null)
						&& (store.getStore_status() == 15)
						&& (!store.getArea().getId().equals(area.getId()))) {
					store_list.remove(store);
					f_size--;
				}
			}
			mv.addObject("f_size", Integer.valueOf(f_size));
			if (store_list.size() > 0) {
				mv.addObject("show", "fa");
			}
		}
		mv.addObject("store_list", store_list);

		List<StoreGrade> sg_list = this.storeGradeService.queryPageList(RedPigMaps.newMap());
		
		mv.addObject("sg_list", sg_list);
		Map<String,Object> gbs = Maps.newHashMap();
		gbs.put("audit", 1);
		
		List<GoodsBrand> gb_list = this.goodsBrandService.queryPageList(gbs);
		
		mv.addObject("gb_list", gb_list);
		
		Map map = Maps.newHashMap();
		map.put("level", 0);
		map.put("display", Boolean.valueOf(true));
		
		List<GoodsClass> gc_list = this.goodsClassService.queryPageList(map);
		
		mv.addObject("gc_list", gc_list);
		
		Map ars = Maps.newHashMap();
		ars.put("level", 0);
		
		List<Area> area_list = this.areaService.queryPageList(map);
		
		mv.addObject("area_list", area_list);
		maps.put("pageSize", page_size);
		IPageList pList = this.storeService.list(maps);
		
		if ("no".equals(service)) {
			int pages = (objs_all.size() + page_size - 1) / page_size;
			mv.addObject("gotoPageFormHTML", CommUtil.showPageFormHtml(
					CommUtil.null2Int(currentPage), pages, page_size));
		} else {
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		}
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("userTools", this.userTools);
		return mv;
	}
	
	/**
	 * 店铺数据
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@RequestMapping({ "/store_data" })
	public ModelAndView store_data(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		ModelAndView mv = new RedPigJModelAndView("store_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String[] ids = store_id.split("_");
		Map<String, Object> map = Maps.newHashMap();
		map.put("goods_store_id", CommUtil.null2Long(ids[1]));
		map.put("goods_status", 0);
		map.put("orderBy", "goods_salenum");
		map.put("orderType", "desc");
		
		List<Goods> goods = this.goodsService.queryPageList(map, 0, 3);
		
		mv.addObject("goods", goods);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(ids[1]));
		mv.addObject("store", store);
		String gc_detail = store.getGc_detail_info();
		if (gc_detail != null) {
			List<Map> list = JSON.parseArray(gc_detail, Map.class);
			List<GoodsClass> gc_list = Lists.newArrayList();
			if ((list != null) && (list.size() > 0)) {
				for (Map m : list) {
					List m_info = (List) m.get("gc_list");
					if (m_info != null) {
						for (int i = 0; i < m_info.size(); i++) {
							GoodsClass gc = this.goodsClassService
									.selectByPrimaryKey(CommUtil.null2Long(m_info
											.get(i)));
							gc_list.add(gc);
						}
					}
				}
				mv.addObject("gc_list", gc_list);
			}
		}
		mv.addObject("store", store);
		map.clear();
		map.put("store_id", CommUtil.null2Long(ids[1]));
		map.put("status", 20);
		map.put("orderBy", "addTime");
		map.put("orderType", "desc");
		map.put("type", 1);
		
		List<Information> infor_list = this.iInformationService.queryPageList(map, 0, 3);
		
		if (infor_list.size() > 0) {
			mv.addObject("new_infor", infor_list.get(0));
			infor_list.remove(0);
			mv.addObject("news_list", infor_list);
		}
		return mv;
	}
	
	/**
	 * 店铺
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
			store = this.storeService.getObjByProperty("store_second_domain","=",  secondDomain);
		} else {
			store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		}
		if (store == null) {
			ModelAndView mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "不存在该店铺信息");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		ModelAndView mv = null;
		if ((store.getStore_decorate_old_info() != null)
				&& (!store.getStore_decorate_old_info().equals(""))) {
			mv = new RedPigJModelAndView("default/store_index.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		} else {
			mv = new RedPigJModelAndView("default/store_default.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		
		if (CommUtil.null2Int(store.getStore_status()) == 15) {
			//
			store_view(id, store, mv);
			
			
			
		} else if ((store.getStore_status() == 25)
				|| (store.getStore_status() == 26)) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺因为合同到期现已关闭，如有疑问请联系商城客服");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else if (store.getStore_status() < 15) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺未正常营业");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else if (store.getStore_status() == 20) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺因为违反商城相关规定现已关闭，如有疑问请联系商城客服");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}

	private void store_view(String id, Store store, ModelAndView mv) {
		if ((store.getStore_decorate_old_info() != null) && (!store.getStore_decorate_old_info().equals(""))) {
			generic_evaluate(store, mv);
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_id", store.getId());
			params.put("display", Boolean.valueOf(true));
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<StoreNavigation> navs = this.storenavigationService.queryPageList(params);
			
			mv.addObject("navs", navs);
			mv.addObject("store", store);
			mv.addObject("userTools", this.userTools);
			
			if (store.getStore_decorate_base_old_info() != null) {
				List<Map> fundations = JSON.parseArray(store.getStore_decorate_base_old_info(), Map.class);
				
				for (Map fun : fundations) {
					mv.addObject("fun_" + fun.get("key"), fun.get("val"));
				}
			}
			
			List<Map> old_maps = JSON.parseArray(store.getStore_decorate_old_info(), Map.class);
			
			mv.addObject("maps", old_maps);
			params.clear();
			params.put("store_id", CommUtil.null2Long(id));
			params.put("slide_type", Integer.valueOf(0));
			
			List<StoreSlide> slides = this.storeSlideService.queryPageList(params);
			
			mv.addObject("default_slides", slides);
		} else {
			add_store_common_info(mv, store);
			generic_evaluate(store, mv);
			mv.addObject("userTools", this.userTools);
		}
		
		String store_theme = "default";
		if (store.getStore_decorate_old_theme() != null) {
			store_theme = store.getStore_decorate_old_theme();
		}
		
		mv.addObject("store_theme", store_theme);
		if (store.getStore_decorate_background_old_info() != null) {
			Map bg = JSON.parseObject(store.getStore_decorate_background_old_info());
			mv.addObject("bg", bg);
		}
		mv.addObject("couponViewTools", this.couponViewTools);
	}
	
	/**
	 * 店铺头部
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping({ "/store_head" })
	public ModelAndView store_head(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("default/store_head.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String type = CommUtil.null2String(request.getAttribute("type"));
		String store_id = CommUtil
				.null2String(request.getAttribute("store_id"));
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			
			for (Cookie cookie : cookies) {
				
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(URLDecoder.decode(cookie.getValue(), "UTF-8"));
				}
			}
		}
		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
		}
		List<GoodsCart> carts_list = Lists.newArrayList();
		List<GoodsCart> carts_cookie = Lists.newArrayList();
		List<GoodsCart> carts_user = Lists.newArrayList();
		User user = SecurityUserHolder.getCurrentUser();
		Map cart_map = Maps.newHashMap();
		if (user != null) {
			user = this.userService.selectByPrimaryKey(user.getId());
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", Integer.valueOf(0));
				carts_cookie = this.goodsCartService.queryPageList(cart_map);
				
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods().getGoods_type() == 1) {
							if (gc.getGoods().getGoods_store().getId()
									.equals(user.getStore().getId())) {
								this.goodsCartService.deleteById(gc.getId());
							}
						}
					}
				}
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", Integer.valueOf(0));
				
				carts_user = this.goodsCartService.queryPageList(cart_map);
				
			} else {
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", Integer.valueOf(0));
				
				carts_user = this.goodsCartService.queryPageList(cart_map);
				
			}
		} else if (!cart_session_id.equals("")) {
			cart_map.clear();
			cart_map.put("cart_session_id", cart_session_id);
			cart_map.put("cart_status", Integer.valueOf(0));
			
			carts_cookie = this.goodsCartService.queryPageList(cart_map);
			
		}
		boolean add;
		if (user != null) {
			for (GoodsCart cookie : carts_cookie) {
				add = true;
				for (GoodsCart gc2 : carts_user) {
					if ((cookie.getGoods().getId().equals(gc2.getGoods()
							.getId()))
							&& (cookie.getSpec_info()
									.equals(gc2.getSpec_info()))) {
						add = false;
						this.goodsCartService.deleteById(cookie.getId());
					}
				}
				if (add) {
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.updateById(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart gc : carts_cookie) {
				carts_list.add(gc);
			}
		}
		for (GoodsCart gc : carts_user) {
			carts_list.add(gc);
		}
		List<GoodsCart> combin_carts_list = Lists.newArrayList();
		for (GoodsCart gc : carts_list) {
			if ((gc.getCart_type() != null)
					&& (gc.getCart_type().equals("combin"))
					&& (gc.getCombin_main() != 1)) {
				combin_carts_list.add(gc);
			}
		}
		if (combin_carts_list.size() > 0) {
			carts_list.removeAll(combin_carts_list);
		}
		mv.addObject("store_id", store_id);
		mv.addObject("carts", carts_list);
		mv.addObject("type", type.equals("") ? "goods" : type);
		return mv;
	}
	
	/**
	 * 店铺导航
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/store_nav" })
	public ModelAndView store_nav(HttpServletRequest request,
			HttpServletResponse response) {
		Long id = CommUtil.null2Long(request.getAttribute("id"));
		Store store = this.storeService.selectByPrimaryKey(id);
		ModelAndView mv = new RedPigJModelAndView("default/store_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(store.getStore_status()) == 15) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_id", store.getId());
			params.put("display", Boolean.valueOf(true));
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<StoreNavigation> navs = this.storenavigationService.queryPageList(params);
			
			mv.addObject("navs", navs);
			mv.addObject("store", store);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "店铺信息错误");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 店铺地址
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_url" })
	public ModelAndView store_url(HttpServletRequest request,
			HttpServletResponse response, String id) {
		StoreNavigation nav = this.storenavigationService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		ModelAndView mv = new RedPigJModelAndView("default/store_url.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("store", nav.getStore());
		mv.addObject("nav", nav);
		mv.addObject("nav_id", nav.getId());
		add_store_common_info(mv, nav.getStore());
		generic_evaluate(nav.getStore(), mv);
		mv.addObject("userTools", this.userTools);
		String store_theme = "default";
		if (nav.getStore().getStore_decorate_old_theme() != null) {
			store_theme = nav.getStore().getStore_decorate_old_theme();
		}
		mv.addObject("store_theme", store_theme);
		if (nav.getStore().getStore_decorate_background_old_info() != null) {
			Map bg = JSON.parseObject(nav.getStore()
					.getStore_decorate_background_old_info());
			mv.addObject("bg", bg);
		}
		return mv;
	}
	
	/**
	 * 店铺列表
	 * @param request
	 * @param response
	 * @param ugc_id
	 * @param store_id
	 * @param keyword
	 * @param orderBy
	 * @param orderType
	 * @param currentPage
	 * @param submit_type
	 * @return
	 */
	@RequestMapping({ "/goods_list" })
	public ModelAndView goods_list(HttpServletRequest request,
			HttpServletResponse response, String ugc_id, String store_id,
			String keyword, String orderBy, String orderType,
			String currentPage, String submit_type) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(store_id));
		if (store != null) {
			if ((submit_type != null) && (!submit_type.equals(""))) {
				if ((keyword != null) && (!keyword.equals(""))) {
					Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,18, orderBy, orderType);
					maps.put("goods_store_id", store.getId());
					maps.put("goods_status", 0);
					maps.put("goods_name_like", keyword);
					
					if ((orderBy != null) && (!orderBy.equals(""))) {
						mv.addObject("orderBy", orderBy);
						mv.addObject("orderType", orderType);
					}
					
					IPageList pList = this.goodsService.list(maps);
					
					String url = this.configService.getSysConfig().getAddress();
					CommUtil.saveIPageList2ModelAndView(url + "/goods_list.html", "", "", pList, mv);
				}
				mv.addObject("submit_type", submit_type);
			} else if ((ugc_id != null) && (!ugc_id.equals(""))) {
				UserGoodsClass ugc = this.userGoodsClassService.selectByPrimaryKey(CommUtil.null2Long(ugc_id));
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,18, orderBy, orderType);
				maps.put("goods_store_id", store.getId());
				maps.put("goods_status", 0);
				
				if (ugc != null) {
					Set<Long> ids = genericUserGcIds(ugc);
					maps.put("goods_ugcs_ugc_ids", ids);
					
				} else {
					ugc = new UserGoodsClass();
					ugc.setClassName("全部商品");
					mv.addObject("ugc", ugc);
				}
				if ((orderBy != null) && (!orderBy.equals(""))) {
					maps.put("orderBy", orderBy);
					mv.addObject("orderBy", orderBy);
					
					maps.put("orderType", orderType);
					mv.addObject("orderType", orderType);
				}
				
				IPageList pList = this.goodsService.list(maps);
				
				String url = this.configService.getSysConfig().getAddress();
				CommUtil.saveIPageList2ModelAndView(url + "/goods_list.html",
						"", "", pList, mv);
				mv.addObject("ugc", ugc);
			}
			mv.addObject("ugc_id", ugc_id);
			mv.addObject("keyword", keyword);
			mv.addObject("store", store);
			mv.addObject("goodsViewTools", this.goodsViewTools);
			mv.addObject("storeViewTools", this.storeViewTools);
			mv.addObject("areaViewTools", this.areaViewTools);
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", store.getUser().getId());
			params.put("display", Boolean.valueOf(true));
			params.put("parent", -1);
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<UserGoodsClass> ugcs = this.userGoodsClassService.queryPageList(params);
			
			mv.addObject("ugcs", ugcs);
			params.clear();
			params.put("store_id", store.getId());
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List<Goods> hotgoods = this.goodsService.queryPageList(params, 0, 5);
			
			mv.addObject("hotgoods", hotgoods);
			params.clear();
			params.put("store_id", store.getId());
			params.put("display", Boolean.valueOf(true));
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<StoreNavigation> navs = this.storenavigationService.queryPageList(params);
						
			mv.addObject("navs", navs);
			generic_evaluate(store, mv);
			mv.addObject("userTools", this.userTools);
			String store_theme = "default";
			if (store.getStore_decorate_old_theme() != null) {
				store_theme = store.getStore_decorate_old_theme();
			}
			mv.addObject("store_theme", store_theme);
			if (store.getStore_decorate_background_old_info() != null) {
				Object bg = JSON.parseObject(store.getStore_decorate_background_old_info());
				mv.addObject("bg", bg);
			}
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
	 * 店铺模块加载，通过url,加载不同的店铺模块
	 * @param request
	 * @param response
	 * @param url
	 * @param id
	 * @param mark
	 * @param decorate_view
	 * @param div
	 * @return
	 */
	@RequestMapping({ "/module_loading" })
	public String module_loading(HttpServletRequest request,
			HttpServletResponse response, String url, String id, String mark,
			String decorate_view, String div) {
		return "redirect:module_" + url + "?id=" + id + "&mark=" + mark + "&decorate_view=" + decorate_view + "&div=" + div;
	}
	
	/**
	 * 店铺导航模块加载
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @param decorate_view
	 * @return
	 */
	@RequestMapping({ "/module_nav" })
	public ModelAndView module_nav(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		ModelAndView mv = new RedPigJModelAndView("default/module_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);

		Map obj = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map map : maps) {
				if (map.get("mark").equals(mark)) {
					obj = map;
					break;
				}
			}
		}
		mv.addObject("obj", obj);

		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", store.getId());
		params.put("display", Boolean.valueOf(true));
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<StoreNavigation> navs = this.storenavigationService.queryPageList(params);
		
		mv.addObject("navs", navs);
		mv.addObject("store", store);
		mv.addObject("decorate_view", decorate_view);
		return mv;
	}
	
	/**
	 * 店铺自定义幻灯模块加载
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @param decorate_view
	 * @return
	 */
	@RequestMapping({ "/module_defined_slide" })
	public ModelAndView module_defined_slide(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		ModelAndView mv = new RedPigJModelAndView(
				"default/module_defined_slide.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		List<Map> objs = Lists.newArrayList();
		Map obj_map = null;
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					obj_map = temp;
					break;
				}
			}
		}
		if (obj_map != null) {
			String[] temp_str = CommUtil.null2String(obj_map.get("slide_info"))
					.split("\\|");

			for (String str : temp_str) {

				if (!str.equals("")) {
					String[] temp = str.split("==");
					Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
							.null2Long(temp[0]));
					Map obj = Maps.newHashMap();
					obj.put("src", img.getPath() + "/" + img.getName());
					if (temp.length > 1) {
						obj.put("url", temp[1]);
					}
					objs.add(obj);
				}
			}
			mv.addObject("obj", obj_map);
			mv.addObject("slides", objs);
		}
		mv.addObject("decorate_view", decorate_view);
		return mv;
	}
	
	/**
	 * 店铺分类模块加载
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @param decorate_view
	 * @return
	 */
	@RequestMapping({ "/module_class" })
	public ModelAndView module_class(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		ModelAndView mv = new RedPigJModelAndView("default/module_class.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", store.getUser().getId());
		params.put("display", Boolean.valueOf(true));
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<UserGoodsClass> ugcs = this.userGoodsClassService.queryPageList(params);
		
		mv.addObject("ugcs", ugcs);
		Map obj_map = null;
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					obj_map = temp;
					break;
				}
			}
		}
		mv.addObject("store", store);
		mv.addObject("decorate_view", decorate_view);
		mv.addObject("obj", obj_map);
		return mv;
	}
	
	/**
	 * 店铺热销商品列表模块加载
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @param decorate_view
	 * @return
	 */
	@RequestMapping({ "/module_goods_sale" })
	public ModelAndView module_goods_sale(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		ModelAndView mv = new RedPigJModelAndView("default/module_goods_sale.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		Map obj_map = null;
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					obj_map = temp;
					break;
				}
			}
		}
		int count = 5;
		if ((obj_map != null) && (obj_map.get("goods_count") != null)
				&& (!obj_map.get("goods_count").equals(""))) {
			count = CommUtil.null2Int(obj_map.get("goods_count"));
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_id", store.getId());
		params.put("goods_status", Integer.valueOf(0));
		params.put("orderBy", "goods_salenum");
		params.put("orderType", "desc");
		
		List<Goods> hotgoods = this.goodsService.queryPageList(params, 0,count);
		
		mv.addObject("hotgoods", hotgoods);
		mv.addObject("obj", obj_map);
		mv.addObject("decorate_view", decorate_view);
		return mv;
	}
	
	/**
	 * 店铺信息模块加载
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @param decorate_view
	 * @return
	 */
	@RequestMapping({ "/module_store_info" })
	public ModelAndView module_store_info(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		ModelAndView mv = new RedPigJModelAndView("default/module_store_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		generic_evaluate(store, mv);
		mv.addObject("store", store);
		Map obj_map = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					obj_map = temp;
					break;
				}
			}
		}
		mv.addObject("obj", obj_map);
		mv.addObject("decorate_view", decorate_view);
		return mv;
	}
	
	/**
	 * 店铺自定义商品列表块加载（2排3列）
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @param decorate_view
	 * @return
	 */
	@RequestMapping({ "/module_goods_top" })
	public ModelAndView module_goods_top(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		ModelAndView mv = new RedPigJModelAndView("default/module_goods_top.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		String goods_ids = null;
		List<Goods> objs = Lists.newArrayList();
		Map obj_map = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					goods_ids = CommUtil.null2String(temp.get("goods_ids"));
					obj_map = temp;
					break;
				}
			}
		}
		if (goods_ids != null) {
			String[] ids = goods_ids.split(",");
			for (String gid : ids) {
				if (!gid.equals("")) {
					Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
					objs.add(obj);
				}
			}
			mv.addObject("obj", obj_map);
			mv.addObject("objs", objs);
		}
		mv.addObject("decorate_view", decorate_view);
		return mv;
	}
	
	/**
	 * 店铺自定义商品列表块加载（4排3列）
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @param decorate_view
	 * @return
	 */
	@RequestMapping({ "/module_goods_right" })
	public ModelAndView module_goods_right(HttpServletRequest request,
			HttpServletResponse response, String id, String mark,
			String decorate_view) {
		ModelAndView mv = new RedPigJModelAndView("default/module_goods_right.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		String goods_ids = null;
		List<Goods> objs = Lists.newArrayList();
		Map obj = Maps.newHashMap();
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					goods_ids = CommUtil.null2String(temp.get("goods_ids"));
					obj = temp;
					break;
				}
			}
		}
		if (goods_ids != null) {
			String[] ids = goods_ids.split(",");
			for (String gid : ids) {
				if (!gid.equals("")) {
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
					objs.add(goods);
				}
			}
			mv.addObject("obj", obj);
			mv.addObject("objs", objs);
		}
		mv.addObject("decorate_view", decorate_view);
		return mv;
	}
	
	/**
	 * 店铺热点模块加载
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 * @param div
	 * @return
	 */
	@RequestMapping({ "/module_hotspot" })
	public ModelAndView module_hotspot(HttpServletRequest request,
			HttpServletResponse response, String id, String mark, String div) {
		ModelAndView mv = new RedPigJModelAndView("default/module_hotspot.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		Map temp_map = null;
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					temp_map = temp;
					break;
				}
			}
		}
		String coors_img_id_mark = "coors_img_id";
		String coors_list_mark = "coors_list";
		if ((div != null) && (!div.equals("undefined")) && (!div.equals(""))
				&& (!div.equals("null"))) {
			coors_img_id_mark = "coors_img_id_" + div;
			coors_list_mark = "coors_list_" + div;
		}
		Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
				.null2Long(temp_map.get(coors_img_id_mark)));
		List<Map> temp_coors_list = (List) temp_map.get(coors_list_mark);
		List<Map> coors_list = Lists.newArrayList();
		mv.addObject("coors_list", temp_coors_list);
		if (img != null) {
			Map obj = Maps.newHashMap();
			obj.put("src", this.configService.getSysConfig().getImageWebServer() + "/" + img.getPath() + "/" + img.getName());
			obj.put("id", img.getId());
			mv.addObject("obj", obj);
		}
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param coords
	 * @param img_id
	 * @param screen_width
	 */
	@RequestMapping({ "/generic_coords" })
	public void generic_coords(HttpServletRequest request,
			HttpServletResponse response, String coords, String img_id,
			String screen_width) {
		Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil
				.null2Long(img_id));
		int setWidth = 680;
		int imageWidth = img.getWidth();
		int screenWidth = CommUtil.null2Int(screen_width);
		Map json_map = Maps.newHashMap();
		String final_coords = coords;
		double rate = 1.0D;
		if ((img != null) && (img.getWidth() > setWidth)) {
			rate = CommUtil.div(Integer.valueOf(img.getWidth()),
					Integer.valueOf(setWidth));
		}
		String[] nums = coords.split(",");
		String temp_coords = "";
		for (String num : nums) {
			String coor = CommUtil.null2String(Long.valueOf(Math.round(CommUtil.mul(Double.valueOf(rate), num))));
			if (temp_coords.equals("")) {
				temp_coords = coor;
			} else {
				temp_coords = temp_coords + "," + coor;
			}
		}
		if (!temp_coords.equals("")) {
			final_coords = temp_coords;
		}
		int real_width = CommUtil.null2Int(screen_width);
		if (img.getWidth() > real_width) {
			double rate2 = CommUtil.div(Integer.valueOf(real_width),
					Integer.valueOf(img.getWidth()));
			String[] temp_real_coors = temp_coords.split(",");
			String real_coors = "";
			for (String real : temp_real_coors) {

				String coor = CommUtil.null2String(Long.valueOf(Math
						.round(CommUtil.mul(Double.valueOf(rate2), real))));
				if (real_coors.equals("")) {
					real_coors = coor;
				} else {
					real_coors = real_coors + "," + coor;
				}
			}
			if (!real_coors.equals("")) {
				final_coords = real_coors;
			}
		}
		json_map.put("coords", final_coords);
		String json = JSON.toJSONString(json_map);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/module_defined" })
	public ModelAndView module_defined(HttpServletRequest request,
			HttpServletResponse response, String id, String mark, String div,
			String decorate_view) {
		ModelAndView mv = new RedPigJModelAndView("default/module_defined.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		String content = "";
		if (store.getStore_decorate_info() != null) {
			List<Map> maps = JSON.parseArray(store.getStore_decorate_info(),
					Map.class);
			for (Map temp : maps) {
				if (temp.get("mark").equals(mark)) {
					if ((div != null) && (!div.equals("undefined"))
							&& (!div.equals("")) && (!div.equals("null"))) {
						content = CommUtil.null2String(temp.get(div
								+ "_content"));
						break;
					}
					content = CommUtil.null2String(temp.get("content"));

					break;
				}
			}
		}
		mv.addObject("content", content);
		mv.addObject("decorate_view", decorate_view);
		return mv;
	}

	private void add_store_common_info(ModelAndView mv, Store store) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", store.getUser().getId());
		params.put("display", Boolean.valueOf(true));
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType","asc");
		
		List<UserGoodsClass> ugcs = this.userGoodsClassService.queryPageList(params);
		
		mv.addObject("ugcs", ugcs);

		params.clear();
		params.put("goods_store_id", store.getId());
		params.put("goods_status", Integer.valueOf(0));
		params.put("orderBy", "goods_salenum");
		params.put("orderType", "desc");
		
		List<Goods> hotgoods = this.goodsService.queryPageList(params, 0, 5);
		
		mv.addObject("hotgoods", hotgoods);

		params.clear();
		params.put("recommend", Boolean.valueOf(true));
		params.put("goods_store_id", store.getId());
		params.put("goods_status", Integer.valueOf(0));
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Goods> goods_recommend = this.goodsService.queryPageList(params, 0,6);
		
		mv.addObject("goods_recommend", goods_recommend);

		params.clear();
		params.put("goods_store_id", store.getId());
		params.put("goods_status", Integer.valueOf(0));
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Goods> goods_collect = this.goodsService.queryPageList(params, 0, 6);
		
		mv.addObject("goods_collect", goods_collect);

		params.clear();
		params.put("goods_store_id", store.getId());
		params.put("goods_status", Integer.valueOf(0));
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Goods> goods_new = this.goodsService.queryPageList(params, 0, 6);
		
		mv.addObject("goods", goods_new);
		
		params.clear();
		params.put("store_id", store.getId());
		params.put("display", Boolean.valueOf(true));
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<StoreNavigation> navs = this.storenavigationService.queryPageList(params);
		
		mv.addObject("navs", navs);

		mv.addObject("store", store);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("areaViewTools", this.areaViewTools);
	}

	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0.0D;
		double service_result = 0.0D;
		double ship_result = 0.0D;
		GoodsClass gc = this.goodsClassService.selectByPrimaryKey(store.getGc_main_id());
		if ((store != null) && (gc != null) && (store.getPoint() != null)) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());

			description_result = CommUtil.div(
					Float.valueOf(store_description_evaluate
							- description_evaluate),
					Float.valueOf(description_evaluate));
			service_result = CommUtil.div(
					Float.valueOf(store_service_evaluate - service_evaluate),
					Float.valueOf(service_evaluate));
			ship_result = CommUtil.div(
					Float.valueOf(store_ship_evaluate - ship_evaluate),
					Float.valueOf(ship_evaluate));
		}
		if (description_result > 0.0D) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(description_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(description_result),
									Integer.valueOf(100))))
							+ "%");
			
		}
		if (description_result == 0.0D) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0.0D) {
			mv.addObject("description_css", "value_light");
			mv.addObject(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-description_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result > 0.0D) {
			mv.addObject("service_css", "value_strong");
			mv.addObject("service_result", CommUtil.null2String(Double
					.valueOf(CommUtil.mul(Double.valueOf(service_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(service_result),
									Integer.valueOf(100)))));
		}
		if (service_result == 0.0D) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0.0D) {
			mv.addObject("service_css", "value_light");
			mv.addObject(
					"service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result > 0.0D) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject("ship_result", CommUtil.null2String(Double
					.valueOf(CommUtil.mul(Double.valueOf(ship_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(ship_result),
									Integer.valueOf(100)))));
		}
		if (ship_result == 0.0D) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0.0D) {
			mv.addObject("ship_css", "value_light");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-ship_result), Integer.valueOf(100))))
							+ "%");
		}
	}

	private Set<Long> genericUserGcIds(UserGoodsClass ugc) {
		Set<Long> ids = new HashSet();
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

	@RequestMapping({ "/area_data" })
	public void area_data(HttpServletRequest request,
			HttpServletResponse response, String a_id) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(a_id));
		List<Map> next_area = Lists.newArrayList();
		if (area.getLevel() == 0) {
			for (int i = 0; i < area.getChilds().size(); i++) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("area_name",((Area) area.getChilds().get(i)).getAreaName());
				map.put("area_id", ((Area) area.getChilds().get(i)).getId());
				next_area.add(map);
			}
		}
		if (area.getLevel() == 1) {
			for (int i = 0; i < area.getChilds().size(); i++) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("area_name",
						((Area) area.getChilds().get(i)).getAreaName());
				map.put("area_id", ((Area) area.getChilds().get(i)).getId());
				next_area.add(map);
			}
		}
		String next_json = JSON.toJSONString(next_area);
		try {
			response.getWriter().print(next_json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/user_store" })
	public void user_store(HttpServletRequest request,
			HttpServletResponse response, String store_id) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		User user = SecurityUserHolder.getCurrentUser();
		String ret = "true";
		if (user != null) {
			if (user.getStore().getId().equals(CommUtil.null2Long(store_id))) {
				ret = "false";
			}
		}
		try {
			response.getWriter().print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
