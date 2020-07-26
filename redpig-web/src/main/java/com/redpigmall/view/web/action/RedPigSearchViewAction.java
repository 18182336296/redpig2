package com.redpigmall.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.google.common.collect.Sets;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsTypeProperty;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.domain.Store;
import com.redpigmall.lucene.LuceneResult;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigSearchViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商品搜索控制器，商城搜索支持关键字全文搜索
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
 * @author redpig,jy
 * 
 * @date 2014-6-5
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigSearchViewAction extends BaseAction{
	
	/**
	 * 搜搜
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
	 * @param goods_pro
	 * @param goods_current_price_floor
	 * @param goods_current_price_ceiling
	 * @param group_buy
	 * @param enough_reduce
	 * @param order_enough_give_status
	 * @param activity_status
	 * @param combin_status
	 * @param advance_sale_type
	 * @param f_sale_type
	 * @param area_id
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "/search" })
	public ModelAndView search(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String goods_type,
			String goods_inventory, String keyword, String goods_transfee,
			String goods_cod, String goods_pro,
			String goods_current_price_floor,
			String goods_current_price_ceiling, String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type, String area_id)
			throws UnsupportedEncodingException {
		ModelAndView mv = new RedPigJModelAndView("search_goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean order_type;
		if ((keyword != null) && (!keyword.equals(""))) {
			response.addCookie(search_history_cookie(request, keyword));
			if ((keyword != null) && (!keyword.equals(""))
					&& (keyword.length() > 1)) {
				mv.addObject("stores", search_stores_seo(keyword));
			}
			String path = ClusterSyncTools.getClusterRoot() + File.separator
					+ "luence" + File.separator + "goods";
			RedPigLuceneUtil.setIndex_path(path);
			Map<String,Object> params = Maps.newHashMap();
			List temp_list = this.goodsClassService.queryPageList(params);
			
			RedPigLuceneUtil.setGc_size(temp_list.size());
			order_type = true;
			String order_by = "";
			Sort sort = null;
			String query_gc = "";
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
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
				order_by = "store_price";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if ((area_id != null) && (!area_id.equals(""))) {
				this.areaViewTools.setDefaultArea(request, area_id);
				mv.addObject("area_id", area_id);
			}
			this.areaViewTools.getUserAreaInfo(request, mv);
			if ((gc_id != null) && (!gc_id.equals(""))) {
				GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
						.null2Long(gc_id));
				keyword = gc.getClassName();
				query_gc = CommUtil.null2String(gc.getParent().getId()) + "_"
						+ gc_id;
				ShowClass sc = this.showClassService.selectByPrimaryKey(gc
						.getShowClass_id());
				Map temp_map = Maps.newHashMap();
				temp_map.put("showClass_id", gc.getShowClass_id());
				
				List<GoodsClass> gcs = this.goodsClassService.queryPageList(temp_map);
				
				mv.addObject("gcs_3", gcs);
				mv.addObject("sc", sc);
				mv.addObject("gc", gc);
				mv.addObject("gc_id", gc_id);
			}
			String salesStr = getSalesWayLuceneString(group_buy, enough_reduce,
					order_enough_give_status, activity_status, combin_status,
					advance_sale_type, f_sale_type);
			LuceneResult pList = lucene.search(keyword,
					CommUtil.null2Int(currentPage), goods_inventory,
					goods_type, query_gc, goods_transfee, goods_cod, sort,
					null, goods_pro, salesStr, goods_current_price_floor,
					goods_current_price_ceiling, area_id);
			
			String url = CommUtil.getURL(request) + "/search_ajax";
			mv.addObject("objs", pList.getVo_list());
			mv.addObject("search_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
					url, "", pList.getCurrentPage(), pList.getPages(),
					pList.getPageSize()));

			Set<String> list_gcs = lucene.LoadData_goods_class(keyword);

			List<GoodsClass> gcs = query_GC_second(list_gcs);
			GoodsClass tempGc = null;
			if ((gc_id != null) && (!gc_id.equals(""))) {
				tempGc = this.goodsClassService.selectByPrimaryKey(CommUtil
						.null2Long(gc_id));
			} else if (gcs.size() > 0) {
				tempGc = this.goodsClassService.selectByPrimaryKey(((GoodsClass) gcs
						.get(0)).getId());
			}
			mv.addObject("filter_properties",
					getSelectableGoodsProperties(goods_pro, tempGc));
			mv.addObject("goods_property",
					formatCurrentSelectedGoodsProperties(goods_pro));
			mv.addObject("list_gc", list_gcs);
			mv.addObject("gcs", gcs);
			mv.addObject("allCount", Integer.valueOf(pList.getRows()));
		}
		mv.addObject("keyword", keyword);
		mv.addObject("orderBy", orderBy);
		mv.addObject("orderType", orderType);
		if (!CommUtil.null2String(goods_type).equals("")) {
			mv.addObject("goods_type", goods_type);
		}
		if (!CommUtil.null2String(goods_inventory).equals("")) {
			mv.addObject("goods_inventory", goods_inventory);
		}
		if (!CommUtil.null2String(goods_transfee).equals("")) {
			mv.addObject("goods_transfee", goods_transfee);
		}
		if (!CommUtil.null2String(goods_cod).equals("")) {
			mv.addObject("goods_cod", goods_cod);
		}
		if (!CommUtil.null2String(group_buy).equals("")) {
			mv.addObject("group_buy", group_buy);
		}
		if (!CommUtil.null2String(enough_reduce).equals("")) {
			mv.addObject("enough_reduce", enough_reduce);
		}
		if (!CommUtil.null2String(order_enough_give_status).equals("")) {
			mv.addObject("order_enough_give_status", order_enough_give_status);
		}
		if (!CommUtil.null2String(activity_status).equals("")) {
			mv.addObject("activity_status", activity_status);
		}
		if (!CommUtil.null2String(combin_status).equals("")) {
			mv.addObject("combin_status", combin_status);
		}
		if (!CommUtil.null2String(advance_sale_type).equals("")) {
			mv.addObject("advance_sale_type", advance_sale_type);
		}
		if (!CommUtil.null2String(f_sale_type).equals("")) {
			mv.addObject("f_sale_type", f_sale_type);
		}
		if (!CommUtil.null2String(goods_current_price_floor).equals("")) {
			if (!CommUtil.null2String(goods_current_price_ceiling).equals("")) {
				mv.addObject("goods_current_price_floor",
						goods_current_price_floor);
				mv.addObject("goods_current_price_ceiling",
						goods_current_price_ceiling);
			}
		}
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("goods_pro", goods_pro);
		mv.addObject("userTools", this.userTools);

		search_other_goods(mv);

		List<Goods> goods_compare_list = (List) request.getSession(false)
				.getAttribute("goods_compare_cart");
		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		int compare_goods_flag = 0;
		for (Goods compare_goods : goods_compare_list) {
			if (compare_goods != null) {
				compare_goods = this.goodsService.selectByPrimaryKey(compare_goods
						.getId());
				if (!compare_goods.getGc().getParent().getParent().getId()
						.equals(CommUtil.null2Long(gc_id))) {
					compare_goods_flag = 1;
				}
			}
		}
		mv.addObject("compare_goods_flag", Integer.valueOf(compare_goods_flag));
		mv.addObject("goods_compare_list", goods_compare_list);
		return mv;
	}
	
	/**
	 * 异步搜索
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
	 * @param goods_current_price_floor
	 * @param goods_current_price_ceiling
	 * @param goods_cod
	 * @param goods_pro
	 * @param group_buy
	 * @param enough_reduce
	 * @param order_enough_give_status
	 * @param activity_status
	 * @param combin_status
	 * @param advance_sale_type
	 * @param f_sale_type
	 * @param area_id
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "/search_ajax" })
	public ModelAndView search_ajax(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String goods_type,
			String goods_inventory, String keyword, String goods_transfee,
			String goods_current_price_floor,
			String goods_current_price_ceiling, String goods_cod,
			String goods_pro, String group_buy, String enough_reduce,
			String order_enough_give_status, String activity_status,
			String combin_status, String advance_sale_type, String f_sale_type,
			String area_id) throws UnsupportedEncodingException {
		ModelAndView mv = new RedPigJModelAndView("search_goods_list_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((keyword != null) && (!keyword.equals(""))) {
			response.addCookie(search_history_cookie(request, keyword));
			if ((keyword != null) && (!keyword.equals(""))
					&& (keyword.length() > 1)) {
				mv.addObject("stores", search_stores_seo(keyword));
			}
			String path = ClusterSyncTools.getClusterRoot() + File.separator
					+ "luence" + File.separator + "goods";
			RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
			RedPigLuceneUtil.setIndex_path(path);
			Map<String,Object> params = Maps.newHashMap();
			
			List temp_list = this.goodsClassService.queryPageList(params);
			
			RedPigLuceneUtil.setGc_size(temp_list.size());
			boolean order_type = true;
			String order_by = "";
			Sort sort = null;
			String query_gc = "";
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
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if (CommUtil.null2String(orderType).equals("asc")) {
				order_type = false;
			}
			if (CommUtil.null2String(orderType).equals("")) {
				orderType = "desc";
			}
			if (CommUtil.null2String(orderBy).equals("goods_current_price")) {
				order_by = "store_price";
				sort = new Sort(new SortField(order_by, SortField.Type.DOUBLE,
						order_type));
			}
			if ((gc_id != null) && (!gc_id.equals(""))) {
				GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
						.null2Long(gc_id));
				query_gc = CommUtil.null2String(gc.getParent().getId()) + "_"
						+ gc_id;
				mv.addObject("gc_id", gc_id);
			}
			String salesStr = getSalesWayLuceneString(group_buy, enough_reduce,
					order_enough_give_status, activity_status, combin_status,
					advance_sale_type, f_sale_type);
			LuceneResult pList = lucene.search(keyword,
					CommUtil.null2Int(currentPage), goods_inventory,
					goods_type, query_gc, goods_transfee, goods_cod, sort,
					null, goods_pro, salesStr, goods_current_price_floor,
					goods_current_price_ceiling, area_id);
			String url = CommUtil.getURL(request) + "/search_ajax";
			mv.addObject("objs", pList.getVo_list());
			mv.addObject("search_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
					url, "", pList.getCurrentPage(), pList.getPages(),
					pList.getPageSize()));
		}
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("userTools", this.userTools);

		List<Goods> goods_compare_list = (List) request.getSession(false)
				.getAttribute("goods_compare_cart");
		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		mv.addObject("goods_compare_list", goods_compare_list);
		return mv;
	}
	
	/**
	 * 搜索分类
	 * @param request
	 * @param response
	 * @param keyword
	 */
	@RequestMapping({ "/search_goodsclass" })
	public void search_goodsclass(HttpServletRequest request,
			HttpServletResponse response, String keyword) {
		if ((keyword == null) || (keyword.equals(""))) {
			return;
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("level_less_than", Integer.valueOf(2));
		params.put("className_like", keyword);
		
		List<GoodsClass> objs = this.goodsClassService.queryPageList(params);
		
		Map<String, Object> map = null;
		if (objs.size() > 0) {
			map = Maps.newHashMap();
			List<Map> list_parent = Lists.newArrayList();
			List list_child = Lists.newArrayList();
			for (GoodsClass obj : objs) {
				Map<String, String> parent_gc = Maps.newHashMap();
				parent_gc.put("id", obj.getId().toString());
				parent_gc.put("name", obj.getClassName());
				list_parent.add(parent_gc);
				List<Map<String, String>> list_temp = Lists.newArrayList();
				for (GoodsClass Child : obj.getChilds()) {
					Map<String, String> map_temp = Maps.newHashMap();
					map_temp.put("id", Child.getId().toString());
					map_temp.put("name", Child.getClassName());
					list_temp.add(map_temp);
				}
				list_child.add(list_temp);
			}
			map.put("parent_gc", list_parent);
			map.put("list_child", list_child);
		}
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
	 * 加载页面上其它的商品信息，最近浏览，猜你喜欢，推广热卖，直通车。
	 * @param mv
	 */
	public void search_other_goods(ModelAndView mv) {
		if (this.configService.getSysConfig().getZtc_status()) {
			List<Goods> left_ztc_goods = null;
			Map<String,Object> ztc_map = Maps.newHashMap();
			ztc_map.put("ztc_status", Integer.valueOf(3));
			ztc_map.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map.put("ztc_gold_more_than", Integer.valueOf(0));
			ztc_map.put("orderBy","ztc_dredge_price");
			ztc_map.put("orderType","desc");
			
			List<Goods> all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
			
//			left_ztc_goods = this.goodsService.queryPageList(ztc_map,4, all_left_ztc_goods.size());
//			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 8);
			
			left_ztc_goods = all_left_ztc_goods;
			
			mv.addObject("left_ztc_goods", left_ztc_goods);
			
			List<Goods> top_ztc_goods = null;
			Map<String,Object> ztc_map2 = Maps.newHashMap();
			ztc_map2.put("ztc_status", Integer.valueOf(3));
			ztc_map2.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map2.put("ztc_gold_more_than", Integer.valueOf(0));
			ztc_map2.put("orderBy", "ztc_dredge_price");
			ztc_map2.put("orderType", "desc");
			top_ztc_goods = this.goodsService.queryPageList(ztc_map2, 0,4);
			
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List<Goods> top_ztc_goods = this.goodsService.queryPageList(params, 0,4);
			
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List<Goods> all_goods = this.goodsService.queryPageList(params);
			
			List<Goods> left_ztc_goods = this.goodsService.queryPageList(params,4, all_goods.size());
			
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 8);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
	}

	public List<GoodsClass> query_GC_second(Set<String> list_gcs) {
		String sid = new String();
		Map<String, Object> params = Maps.newHashMap();
		List<GoodsClass> gcs = Lists.newArrayList();
		Set<Long> ids = Sets.newHashSet();
		if (list_gcs != null)
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

	/**
	 * 根据店铺SEO关键字，查出关键字命中的店铺
	 * @param keyword
	 * @return
	 */
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
	 * 得到一个存有搜索数据的Cookie
	 * @param request
	 * @param keyword
	 * @return
	 * @throws UnsupportedEncodingException
	 */
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

	public List<GoodsTypeProperty> getSelectableGoodsProperties(
			String properties, GoodsClass gc) {
		if (gc == null) {
			return null;
		}
		if (!CommUtil.null2String(properties).equals("")) {
			String[] properties_list = properties.substring(1).split("\\|");

			List<GoodsTypeProperty> filter_properties = Lists.newArrayList();
			List<String> hc_property_list = Lists.newArrayList();
			if (gc.getGoodsType() != null) {
				for (GoodsTypeProperty gtp : gc.getGoodsType().getProperties()) {
					boolean flag = true;
					GoodsTypeProperty gtp1 = new GoodsTypeProperty();
					gtp1.setDisplay(gtp.getDisplay());
					gtp1.setGoodsType(gtp.getGoodsType());
					gtp1.setHc_value(gtp.getHc_value());
					gtp1.setId(gtp.getId());
					gtp1.setName(gtp.getName());
					gtp1.setSequence(gtp.getSequence());
					gtp1.setValue(gtp.getValue());
					for (String hc_property : hc_property_list) {
						String[] hc_list = hc_property.split(":");
						if (hc_list[0].equals(gtp.getName())) {
							String[] hc_temp_list = hc_list[1].split(",");
							String[] defalut_list_value = gtp1.getValue()
									.split(",");
							ArrayList<String> defalut_list = new ArrayList(
									Arrays.asList(defalut_list_value));

							for (String hc_temp : hc_temp_list) {

								defalut_list.remove(hc_temp);
							}
							String value = "";
							for (int i = defalut_list.size() - 1; i >= 0; i--) {
								value = (String) defalut_list.get(i) + ","
										+ value;
							}
							gtp1.setValue(value.substring(0, value.length() - 1));
							flag = false;
							break;
						}
					}
					if (flag) {
						if (!CommUtil.null2String(gtp.getHc_value()).equals("")) {
							String[] list1 = gtp.getHc_value().split("#");
							for (int i = 0; i < properties_list.length; i++) {
								String property_info = CommUtil
										.null2String(properties_list[i]);
								String[] property_info_list = property_info
										.split(",");
								if (property_info_list[0].equals(list1[0])) {
									hc_property_list.add(list1[1]);
								}
							}
						}
						filter_properties.add(gtp);
					} else {
						filter_properties.add(gtp1);
					}
				}
			}
			return filter_properties;
		}
		if (gc.getGoodsType() != null) {
			return gc.getGoodsType().getProperties();
		}
		return null;
	}

	public List<Map> formatCurrentSelectedGoodsProperties(String properties) {
		if (CommUtil.null2String(properties).equals("")) {
			return null;
		}
		List<Map> goods_property = Lists.newArrayList();
		String[] properties_list = properties.substring(1).split("\\|");
		for (int i = 0; i < properties_list.length; i++) {
			String property_info = CommUtil.null2String(properties_list[i]);
			String[] property_info_list = property_info.split(",");
			Map<String, Object> map = Maps.newHashMap();
			map.put("name", property_info_list[2]);
			map.put("value", property_info_list[0]);
			map.put("id", property_info_list[1]);
			goods_property.add(map);
		}
		return goods_property;
	}

	private String getSalesWayLuceneString(String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type) {
		StringBuilder sbSql = new StringBuilder("(");
		if (!CommUtil.null2String(group_buy).equals("")) {
			sbSql.append("whether_active:1 OR ");
		}
		if (!CommUtil.null2String(enough_reduce).equals("")) {
			sbSql.append("whether_active:4 OR ");
		}
		if (!CommUtil.null2String(order_enough_give_status).equals("")) {
			sbSql.append("whether_active:3 OR ");
		}
		if (!CommUtil.null2String(activity_status).equals("")) {
			sbSql.append("whether_active:2 OR ");
		}
		if (!CommUtil.null2String(combin_status).equals("")) {
			sbSql.append("whether_active:5 OR ");
		}
		if (!CommUtil.null2String(advance_sale_type).equals("")) {
			sbSql.append("whether_active:7 OR ");
		}
		if (!CommUtil.null2String(f_sale_type).equals("")) {
			sbSql.append("whether_active:6 OR ");
		}
		if (sbSql.length() > 1) {
			sbSql.setLength(sbSql.length() - 3);
			sbSql.append(")");
			return sbSql.toString();
		}
		return null;
	}
}
