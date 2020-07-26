package com.redpigmall.module.weixin.view.action;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.Document;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.WeixinFloor;
import com.redpigmall.lucene.LuceneResult;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.module.weixin.view.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigWapIndexViewAction.java
 * </p>
 * 
 * <p>
 * Description:wap以及微信商城使用的前台首页控制器
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinIndexViewAction extends BaseAction{
	/**
	 * 手机客户端商城首页
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/index" })
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("orderBy", "sequence");
        maps.put("orderType", "asc");
        
		List<WeixinFloor> list = this.weixinfloorService.queryPageList(maps);
		
		mv.addObject("floor_list", list);
		mv.addObject("weixinIndexTools", this.weixinIndexTools);
		String weixin_navigator = this.configService.getSysConfig().getWeixin_navigator();
		
		Map index_nav_map = Maps.newHashMap();
		Map weixin_nav_map;
		if ((weixin_navigator != null) && (!weixin_navigator.equals(""))) {
			weixin_nav_map = JSON.parseObject(weixin_navigator);
			for (Object obj : weixin_nav_map.keySet()) {
				if (obj.toString().indexOf("index") >= 0) {
					index_nav_map.put(obj, weixin_nav_map.get(obj));
				}
			}
		} else {
			weixin_nav_map = Maps.newHashMap();
		}
		mv.addObject("index_nav_map", index_nav_map);
		return mv;
	}
	
	/**
	 * 手机端商品搜索
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param goods_type
	 * @param goods_inventory
	 * @param keyword
	 * @param goods_transfee
	 * @param goods_cod
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "/search" })
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String goods_type,
			String goods_inventory, String keyword, String goods_transfee,
			String goods_cod) throws UnsupportedEncodingException {
		ModelAndView mv = new RedPigJModelAndView("weixin/class_goods_lucene.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView("weixin/class_goods_lucene_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		if ((keyword != null) && (!keyword.equals(""))) {
			if (CommUtil.null2Int(currentPage) <= 1) {
				response.addCookie(search_history_cookie(request, keyword));
				if ((keyword != null) && (!keyword.equals(""))
						&& (keyword.length() > 1)) {
					mv.addObject("stores", search_stores_seo(keyword));
				}
			}
			String path = ClusterSyncTools.getClusterRoot() + File.separator
					+ "luence" + File.separator + "goods";
			RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
			RedPigLuceneUtil.setIndex_path(path);
			Map<String,Object> maps = Maps.newHashMap();
			List temp_list = this.goodsClassService.queryPageList(maps);
			
			RedPigLuceneUtil.setGc_size(temp_list.size());
			boolean order_type = true;
			String order_by = "";
			Sort sort = null;
			String query_gc = "";
			
			if ((orderBy == null) || ("".equals(orderBy))) {
				orderBy = "goods_salenum";
			}
			if (CommUtil.null2String(orderBy).equals("goods_salenum")) {
				order_by = "goods_salenum";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,
						order_type));
			}
			if (CommUtil.null2String(orderBy).equals("goods_collect")) {
				order_by = "goods_collect";
				sort = new Sort(new SortField(order_by, SortField.Type.INT,
						order_type));
			}
			if (CommUtil.null2String(orderBy).equals("well_evaluate")) {
				order_by = "well_evaluate";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,order_type));
			}
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("store_price")) {
				order_by = "store_price";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if ((gc_id != null) && (!gc_id.equals(""))) {
				GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
				if (gc.getParent() != null) {
					query_gc = CommUtil.null2String(gc.getParent().getId()) + "_" + gc_id;
				} else {
					query_gc = CommUtil.null2String(gc.getId()) + "_" + gc_id;
				}
				mv.addObject("gc_id", gc_id);
				mv.addObject("gc", gc);
			}
			LuceneResult pList = lucene.search(keyword,
					CommUtil.null2Int(currentPage), goods_inventory,
					goods_type, query_gc, goods_transfee, goods_cod, sort,
					null, null, null, null, null, null);
			pList.setPageSize(20);
			mv.addObject("objs", pList.getVo_list());
			CommUtil.saveLucene2ModelAndView(pList, mv);

			Set<String> list_gcs = lucene.LoadData_goods_class(keyword);

			List<GoodsClass> gcs = query_GC_second(list_gcs);

			mv.addObject("list_gc", list_gcs);
			mv.addObject("gcs", gcs);
			mv.addObject("allCount", Integer.valueOf(pList.getRows()));
		}
		mv.addObject("keyword", keyword);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		mv.addObject("goods_type", goods_type);
		mv.addObject("goods_inventory", goods_inventory);
		mv.addObject("goods_transfee", goods_transfee);
		mv.addObject("goods_cod", goods_cod);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("userTools", this.userTools);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 手机端页脚
	 * @param request
	 * @param response
	 * @param op
	 * @return
	 */
	@RequestMapping({ "/footer" })
	public ModelAndView footer(HttpServletRequest request,
			HttpServletResponse response, String op) {
		ModelAndView mv = new RedPigJModelAndView("weixin/footer.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("name", request.getServerName());
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/layer" })
	public ModelAndView layer(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/layer.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String weixin_navigator = this.configService.getSysConfig()
				.getWeixin_navigator();

		Map layer_nav_map = Maps.newHashMap();
		Map weixin_nav_map;
		if ((weixin_navigator != null) && (!weixin_navigator.equals(""))) {
			weixin_nav_map = JSON.parseObject(weixin_navigator);
			for (Object obj : weixin_nav_map.keySet()) {
				if (obj.toString().indexOf("nav_") >= 0) {
					layer_nav_map.put(obj, weixin_nav_map.get(obj));
				}
			}
			String ret = CommUtil.null2String(weixin_nav_map
					.get("navigator_bar"));
			layer_nav_map.put("navigator_bar", ret);
		} else {
			weixin_nav_map = Maps.newHashMap();
		}
		mv.addObject("layer_nav_map", layer_nav_map);
		mv.addObject("op", CommUtil.null2String(request.getAttribute("op")));
		return mv;
	}
	
	/**
	 * APP下载
	 * @param request
	 * @param response
	 */
	@RequestMapping({ "/app/download" })
	public void app_download(HttpServletRequest request,
			HttpServletResponse response) {
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		String url = CommUtil.getURL(request);
		if (user_agent.indexOf("iphone") > 0) {
			url = this.configService.getSysConfig().getIos_download();
		}
		if (user_agent.indexOf("android") > 0) {
			url = this.configService.getSysConfig().getAndroid_download();
		}
		try {
			response.sendRedirect(CommUtil.getURL(request) + "/" + url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private List<GoodsCart> cart_calc(HttpServletRequest request) {
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			
			
			for (Cookie cookie : cookies) {
				
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(cookie.getValue());
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
						gc.getGoods().getGoods_type();
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
		if (user != null) {
			for (GoodsCart ugc : carts_user) {
				carts_list.add(ugc);
			}
			for (GoodsCart cookie : carts_cookie) {
				boolean add = true;
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
			for (GoodsCart cookie : carts_cookie) {
				carts_list.add(cookie);
			}
		}
		return carts_list;
	}

	public List<GoodsClass> query_GC_second(Set<String> list_gcs) {
		String sid = new String();
		Map<String, Object> params = Maps.newHashMap();
		List<GoodsClass> gcs = Lists.newArrayList();
		Set<Long> ids = new HashSet();
		for (String str : list_gcs) {
			sid = str.split("_")[0];
			ids.add(CommUtil.null2Long(sid));
		}
		if (!ids.isEmpty()) {
			params.put("ids", ids);
			
			gcs = this.goodsClassService.queryPageList(params);
			
		}
		return gcs;
	}

	public List<Store> search_stores_seo(String keyword) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_seo_keywords1", keyword);
		
		
		List<Store> stores = this.storeService.queryPageList(params, 0, 3);
		
		Collections.sort(stores, new Comparator() {
			public int compare(Object o1, Object o2) {
				Store store1 = (Store) o1;
				Store store2 = (Store) o2;
				int l1 = store1.getStore_seo_keywords().split(",").length;
				int l2 = store2.getStore_seo_keywords().split(",").length;
				if (l1 > l2) {
					return 1;
				}
				if (l1 == l2) {
					if (store1.getPoint().getStore_evaluate()
							.compareTo(store2.getPoint().getStore_evaluate()) == 1) {
						return -1;
					}
					if (store1.getPoint().getStore_evaluate()
							.compareTo(store2.getPoint().getStore_evaluate()) == -1) {
						return 1;
					}
					return 0;
				}
				return -1;
			}
		});
		return stores;
	}
	
	/**
	 * 手机端文章
	 * @param request
	 * @param response
	 * @param mark
	 * @return
	 */
	@RequestMapping({ "/doc" })
	public ModelAndView doc(HttpServletRequest request,
			HttpServletResponse response, String mark) {
		ModelAndView mv = new RedPigJModelAndView("weixin/article.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("doc", "doc");
		
		Document obj = this.documentService.getObjByProperty("mark","=", mark);
		
		mv.addObject("obj", obj);
		return mv;
	}
	
	/**
	 * 手机端广告
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/advert_invoke" })
	public ModelAndView advert_invoke(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/advert_invoke.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((id != null) && (!id.equals(""))) {
			AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (ap != null) {
				AdvertPosition obj = new AdvertPosition();
				obj.setAp_type(ap.getAp_type());
				obj.setAp_status(ap.getAp_status());
				obj.setAp_show_type(ap.getAp_show_type());
				obj.setAp_width(ap.getAp_width());
				obj.setAp_height(ap.getAp_height());
				obj.setAp_location(ap.getAp_location());
				List<Advert> advs = Lists.newArrayList();
				for (Advert temp_adv : ap.getAdvs()) {
					if ((temp_adv.getAd_status() == 1)
							&& (temp_adv.getAd_begin_time().before(new Date()))
							&& (temp_adv.getAd_end_time().after(new Date()))) {
						advs.add(temp_adv);
					}
				}
				if (advs.size() > 0) {
					if (obj.getAp_type().equals("text")) {
						if (obj.getAp_show_type() == 0) {
							obj.setAp_text(((Advert) advs.get(0)).getAd_text());
							obj.setAp_acc_url(((Advert) advs.get(0))
									.getAd_url());
							obj.setAdv_id(CommUtil.null2String(((Advert) advs
									.get(0)).getId()));
						}
						if (obj.getAp_show_type() == 1) {
							Random random = new Random();
							int i = random.nextInt(advs.size());
							obj.setAp_text(((Advert) advs.get(i)).getAd_text());
							obj.setAp_acc_url(((Advert) advs.get(i))
									.getAd_url());
							obj.setAdv_id(CommUtil.null2String(((Advert) advs
									.get(i)).getId()));
						}
					}
					if (obj.getAp_type().equals("img")) {
						if (obj.getAp_show_type() == 0) {
							obj.setAp_acc(((Advert) advs.get(0)).getAd_acc());
							obj.setAp_acc_url(((Advert) advs.get(0))
									.getAd_url());
							obj.setAdv_id(CommUtil.null2String(((Advert) advs
									.get(0)).getId()));
						}
						if (obj.getAp_show_type() == 1) {
							Random random = new Random();
							int i = random.nextInt(advs.size());
							obj.setAp_acc(((Advert) advs.get(i)).getAd_acc());
							obj.setAp_acc_url(((Advert) advs.get(i))
									.getAd_url());
							obj.setAdv_id(CommUtil.null2String(((Advert) advs
									.get(i)).getId()));
						}
					}
					if (obj.getAp_type().equals("slide")) {
						if (obj.getAp_show_type() == 0) {
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									8);
							for (int i : list) {
								obj.getAdvs().add((Advert) advs.get(i));
							}
						}
					}
					if (obj.getAp_type().equals("scroll")) {
						if (obj.getAp_show_type() == 0) {
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {
							
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									12);
							for (int i : list) {
								obj.getAdvs().add((Advert) advs.get(i));
							}
						}
					}
					if (obj.getAp_type().equals("bg_slide")) {
						if (obj.getAp_show_type() == 0) {
							obj.setAdvs(advs);
						}
						if (obj.getAp_show_type() == 1) {
							Set<Integer> list = CommUtil.randomInt(advs.size(),
									advs.size());
							for (int i : list) {
								obj.getAdvs().add((Advert) advs.get(i));
							}
						}
					}
				} else {
					obj.setAp_acc(ap.getAp_acc());
					obj.setAp_text(ap.getAp_text());
					obj.setAp_acc_url(ap.getAp_acc_url());
					Advert adv = new Advert();
					adv.setAd_url(obj.getAp_acc_url());
					adv.setAd_acc(ap.getAp_acc());
					obj.getAdvs().add(adv);
					obj.setAp_location(ap.getAp_location());
				}
				if (obj.getAp_status() == 1) {
					mv.addObject("obj", obj);
				} else {
					mv.addObject("obj", new AdvertPosition());
				}
			}
		}
		return mv;
	}

	public Cookie search_history_cookie(HttpServletRequest request,
			String keyword) throws UnsupportedEncodingException {
		String str = "";
		Cookie[] cookies = request.getCookies();
		Cookie search_cookie = null;
		
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("search_history")) {
				String str_temp = URLDecoder.decode(cookie.getValue(), "UTF-8");
				for (String s : str_temp.split(",")) {
					if ((!s.equals(keyword)) && (!str.equals(""))) {
						str = str + "," + s;
					} else if (!s.equals(keyword)) {
						str = s;
					}
				}
				break;
			}
		}
		if (str.equals("")) {
			str = keyword;
			str = URLEncoder.encode(str, "UTF-8");
			search_cookie = new Cookie("search_history", str);
		} else {
			str = keyword + "," + str;
			str = URLEncoder.encode(str, "UTF-8");
			search_cookie = new Cookie("search_history", str);
		}
		return search_cookie;
	}
}