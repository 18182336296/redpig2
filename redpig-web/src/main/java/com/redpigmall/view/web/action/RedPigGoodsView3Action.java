package com.redpigmall.view.web.action;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsTypeProperty;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigGoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商品前台控制器,用来显示商品列表、商品详情、商品其他信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-4-28
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigGoodsView3Action extends BaseAction{

	/**
	 * 店铺商品列表异步加载
	 * @param request
	 * @param response
	 * @param ajax_gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param brand_ids
	 * @param gs_ids
	 * @param properties
	 * @param goods_type
	 * @param goods_inventory
	 * @param goods_transfee
	 * @param goods_cod
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
	 * @param sc_id
	 * @return
	 */
	@RequestMapping({ "/store_goods_list_ajax" })
	public ModelAndView store_goods_list_ajax(HttpServletRequest request,
			HttpServletResponse response, String ajax_gc_id,
			String currentPage, String orderBy, String orderType,
			String brand_ids, String gs_ids, String properties,
			String goods_type, String goods_inventory, String goods_transfee,
			String goods_cod, String goods_current_price_floor,
			String goods_current_price_ceiling, String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type, String area_id,
			String sc_id) {
		ModelAndView mv = new RedPigJModelAndView("store_goods_list_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map pa = Maps.newHashMap();
		pa.put("id", CommUtil.null2Long(ajax_gc_id));
		pa.put("display", true);
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(pa);
		
		GoodsClass gc = null;
		if (gcs.size() > 0) {
			gc = gcs.get(0);
		}
		
		if ((sc_id != null) && (!"".equals(sc_id))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", CommUtil.null2Long(sc_id));
			map.put("display", true);
			
			List<ShowClass> scs = this.showClassService.queryPageList(map);
			
			if (scs.size() > 0) {
				ShowClass sc = (ShowClass) scs.get(0);
				if (sc.getParent() != null) {
					
					if (sc.getParent().getDisplay()) {
						Map ma = Maps.newHashMap();
						ma.put("parent", sc.getParent().getId());
						ma.put("display", true);
						
						List<ShowClass> sc_list = this.showClassService.queryPageList(ma);
						
						mv.addObject("sc", sc);
						mv.addObject("sc_list", sc_list);
					}
				} else if (sc.getDisplay()) {
					mv.addObject("psc", sc);
				}
			}
		} else if (gc != null) {
			mv.addObject("gc0", gc);
			Map map1 = Maps.newHashMap();
			map1.put("showClass_id", gc.getShowClass_id());
			map1.put("display", true);
			
			List list = this.goodsClassService.queryPageList(map1);
			
			mv.addObject("gc_list0", list);
			ShowClass sc = this.showClassService.selectByPrimaryKey(gc.getShowClass_id());
			
			if ((sc != null) && (sc.getParent() != null)) {
				Set<ShowClass> sc_list = sc.getParent().getChilds();
				mv.addObject("sc", sc);
				mv.addObject("sc_list", sc_list);
			}
		}
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "addTime";
		}
		if ((orderType == null) || (orderType.equals(""))) {
			orderType = "desc";
		}
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,24, orderBy, orderType);
		
		Set<Long> ids = null;
		if (gc != null) {
			ids = genericIds(gc.getId());
		}
		
		if ((ids != null) && (ids.size() > 0)) {
			maps.put("gc_ids", ids);
		}
		
		if ((sc_id != null) && (!"".equals(sc_id))) {
			Set<Long> all_id = new HashSet();
			ShowClass sc = this.showClassService.selectByPrimaryKey(CommUtil.null2Long(sc_id));
			
			if ((sc != null) && (sc.getDisplay())) {
				if (sc.getParent() == null) {
					Map psc_map = Maps.newHashMap();
					psc_map.put("parent", sc.getId());
					psc_map.put("display", true);
					
					List<ShowClass> scs = this.showClassService.queryPageList(psc_map);
					
					if (scs.size() > 0) {
						
						int i = 0;
						for (ShowClass set_sc : scs) {
							Map sc_map = Maps.newHashMap();
							sc_map.put("showClass_id", set_sc.getId());
							sc_map.put("display", true);
							
							List<GoodsClass> sc_ids = this.goodsClassService.queryPageList(sc_map);
							
							for (GoodsClass object : sc_ids) {
								if ((object != null) && (!"".equals(sc_ids.get(0)))) {
									Set<Long> gc_ids = genericIds(CommUtil.null2Long(object.getId()));
									all_id.addAll(gc_ids);
								}
							}

						}
					}
				} else if (sc.getParent().getDisplay()) {
					Map sc_map = Maps.newHashMap();
					sc_map.put("showClass_id", CommUtil.null2Long(sc_id));
					sc_map.put("display", true);
					
					List sc_ids = this.goodsClassService.queryPageList(sc_map);
					
					Set<Long> gc_ids = null;
					for (int i = 0; i < sc_ids.size(); i++) {
						if ((sc_ids.get(i) != null) && (!"".equals(sc_ids.get(0)))) {
							gc_ids = genericIds(CommUtil.null2Long(sc_ids.get(i)));
							all_id.addAll(gc_ids);
						}
					}
				}
			}
			
			if ((all_id != null) && (all_id.size() > 0)) {
				maps.put("gc_ids", all_id);
			}
			
		}
		
		if (!CommUtil.null2String(goods_cod).equals("")) {
			maps.put("goods_cod", 0);
			mv.addObject("goods_cod", goods_cod);
		}
		
		if (!CommUtil.null2String(goods_transfee).equals("")) {
			maps.put("goods_transfee", 1);
			mv.addObject("goods_transfee", goods_transfee);
		}
		
		maps.put("goods_status", 0);
		
		List<Map> goods_property = Lists.newArrayList();
		if (!CommUtil.null2String(brand_ids).equals("")) {
			if (brand_ids.indexOf(",") < 0) {
				brand_ids = brand_ids + ",";
			}
			String[] brand_id_list = CommUtil.null2String(brand_ids).split(",");
			if (brand_id_list.length == 1) {
				String brand_id = brand_id_list[0];
				maps.put("goods_brand_id", CommUtil.null2Long(brand_id));
				
				Map<String, Object> map = Maps.newHashMap();
				GoodsBrand brand = this.brandService.selectByPrimaryKey(CommUtil.null2Long(brand_id));
				
				if (brand != null) {
					map.put("name", "品牌");
					map.put("value", brand.getName());
					map.put("type", "brand");
					map.put("id", brand.getId());
					goods_property.add(map);
				}
			} else {
				List<Long> goods_brand_ids = Lists.newArrayList();
				
				for (int i = 0; i < brand_id_list.length; i++) {
					String brand_id = brand_id_list[i];
					goods_brand_ids.add(CommUtil.null2Long(brand_id));
					if (i == 0) {
						
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService.selectByPrimaryKey(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else if (i == brand_id_list.length - 1) {
						
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService.selectByPrimaryKey(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else {
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService
								.selectByPrimaryKey(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					}
				}
				
				maps.put("goods_brand_ids", goods_brand_ids);
				
			}
			if ((brand_ids != null) && (!brand_ids.equals(""))) {
				mv.addObject("brand_ids", brand_ids);
			}
		}
		if (!CommUtil.null2String(gs_ids).equals("")) {
			List<List<GoodsSpecProperty>> gsp_lists = generic_gsp(gs_ids);
			List<Long> goodsSpecPropertyIds = Lists.newArrayList();
			for (int j = 0; j < gsp_lists.size(); j++) {
				List<GoodsSpecProperty> gsp_list = (List) gsp_lists.get(j);
				
				if (gsp_list.size() == 1) {
					GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(0);
					
					goodsSpecPropertyIds.add(gsp.getId());
					
					Map<String, Object> map = Maps.newHashMap();
					map.put("name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					for (int i = 0; i < gsp_list.size(); i++) {
						if (i == 0) {
							GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
							goodsSpecPropertyIds.add(gsp.getId());
							Map<String, Object> map = Maps.newHashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i == gsp_list.size() - 1) {
							GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
							goodsSpecPropertyIds.add(gsp.getId());
							Map<String, Object> map = Maps.newHashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else {
							GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
							goodsSpecPropertyIds.add(gsp.getId());
							Map<String, Object> map = Maps.newHashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						}
					}
				}
			}
			
			maps.put("goodsSpecPropertyIds", goodsSpecPropertyIds);
			
			mv.addObject("gs_ids", gs_ids);
		}
		if (!CommUtil.null2String(properties).equals("")) {
			String[] properties_list = properties.substring(1).split("\\|");
			List<String> gtp_names = Lists.newArrayList();
			List<String> gtp_values = Lists.newArrayList();
			
			for (int i = 0; i < properties_list.length; i++) {
				String property_info = CommUtil.null2String(properties_list[i]);
				String[] property_info_list = property_info.split(",");
				GoodsTypeProperty gtp = this.goodsTypePropertyService.selectByPrimaryKey(CommUtil.null2Long(property_info_list[0]));
				
				gtp_names.add(gtp.getName().trim());
				gtp_values.add(property_info_list[1].trim());
				
				Map<String, Object> map = Maps.newHashMap();
				map.put("name", gtp.getName());
				map.put("value", property_info_list[1]);
				map.put("type", "properties");
				map.put("id", gtp.getId());
				goods_property.add(map);
			}
			
			maps.put("gtp_names", gtp_names);
			maps.put("gtp_values", gtp_values);
			
			mv.addObject("properties", properties);
			
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
							for (String default_ : defalut_list) {
								value = default_ + "," + value;
							}
							gtp1.setValue(value.substring(0, value.length() - 1));
							flag = false;
							break;
						}
					}
					if ((!flag)|| (CommUtil.null2String(gtp.getHc_value()).equals(""))) {
						break ;
					}
					String[] list1 = gtp.getHc_value().split("#");
					
					for (String property_info : properties_list) {
						String[] property_info_list = property_info.split(",");
						if (property_info_list[1].equals(list1[0])) {
							hc_property_list.add(list1[1]);
						}
					}
					
					
				}
			}
		}
		if (!CommUtil.null2String(goods_current_price_floor).equals("")) {
			if (!CommUtil.null2String(goods_current_price_ceiling).equals("")) {
				maps.put("goods_current_price_more_than_equal", CommUtil.null2Double(goods_current_price_floor));
				maps.put("goods_current_price_less_than_equal", CommUtil.null2Double(goods_current_price_ceiling));
				mv.addObject("goods_current_price_floor",goods_current_price_floor);
				mv.addObject("goods_current_price_ceiling",goods_current_price_ceiling);
			}
		}
		
		if (!CommUtil.null2String(group_buy).equals("")) {
			maps.put("group_buy", 2);
			mv.addObject("group_buy", group_buy);
		}
		if (!CommUtil.null2String(enough_reduce).equals("")) {
			maps.put("enough_reduce", 1);
			mv.addObject("enough_reduce", enough_reduce);
		}
		if (!CommUtil.null2String(order_enough_give_status).equals("")) {
			maps.put("order_enough_give_status", 1);
			mv.addObject("order_enough_give_status", order_enough_give_status);
		}
		if (!CommUtil.null2String(activity_status).equals("")) {
			maps.put("activity_status", 2);
			mv.addObject("activity_status", activity_status);
		}
		if (!CommUtil.null2String(combin_status).equals("")) {
			maps.put("combin_status", 1);
			mv.addObject("combin_status", combin_status);
		}
		if (!CommUtil.null2String(advance_sale_type).equals("")) {
			maps.put("advance_sale_type", 1);
			mv.addObject("advance_sale_type", advance_sale_type);
		}
		if (!CommUtil.null2String(f_sale_type).equals("")) {
			maps.put("f_sale_type", 1);
			mv.addObject("f_sale_type", f_sale_type);
		}
		
		if ((goods_inventory != null) && (!goods_inventory.equals(""))) {
			this.QueryTools.queryGoodInventory(area_id, maps, goods_type);
			mv.addObject("goods_inventory", goods_inventory);
		}
		
		if ((goods_type != null) && (!goods_type.equals(""))) {
			maps.put("goods_type", CommUtil.null2Int(goods_type));
			mv.addObject("goods_type", goods_type);
		}
		
		IPageList pList = this.goodsService.list(maps);
		String url = CommUtil.getURL(request) + "/store_goods_list_ajax";
		mv.addObject("objs", pList.getResult());
		mv.addObject("store_goods_list_gotoPageAjaxHTML", 
				CommUtil.showPageAjaxHtml(url, "", pList.getCurrentPage(),pList.getPages(), pList.getPageSize()));
		mv.addObject("userTools", this.userTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	/**
	 * 商品直通车ajax加载
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param count
	 * @return
	 */
	@RequestMapping({ "/goods_list_ztc_ajax" })
	public ModelAndView goods_list_ztc_ajax(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String count) {
		ModelAndView mv = new RedPigJModelAndView("goods_list_ztc_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		int select_count = 10;
		if ((count != null) && (!CommUtil.null2String(count).equals(""))) {
			select_count = CommUtil.null2Int(count);
		}
		if (this.configService.getSysConfig().getZtc_status()) {
			List<Goods> left_ztc_goods = null;
			Map ztc_map = Maps.newHashMap();
			ztc_map.put("ztc_status", Integer.valueOf(3));
			ztc_map.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map.put("ztc_gold_more_than", Integer.valueOf(0));
			ztc_map.put("orderBy", "ztc_dredge_price");
			ztc_map.put("orderType", "desc");
			
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				List all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
				left_ztc_goods = this.goodsService.queryPageList(ztc_map, 3, all_left_ztc_goods.size());
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, select_count);
			}
			
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
				Set<Long> ids = null;
				if (gc != null) {
					ids = genericIds(gc.getId());
				}
				
				ztc_map.put("gc_ids", ids);
				
				List all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
				
				left_ztc_goods = this.goodsService.queryPageList(ztc_map, 3, all_left_ztc_goods.size());
				
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, select_count);
			}
			mv.addObject("left_ztc_goods", left_ztc_goods);
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List all_goods = this.goodsService.queryPageList(params);
			
			List<Goods> left_ztc_goods = this.goodsService.queryPageList(params, 3, all_goods.size());
			
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, select_count);
			
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		return mv;
	}
	
	/**
	 * 底部商品列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping({ "/goods_list_bottom" })
	public ModelAndView goods_list_bottom(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("goods_list_bottom.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<Goods> your_like_goods = Lists.newArrayList();
		
		Cookie[] cookies = request.getCookies();
		
		if (cookies != null) {
			
			
			for (Cookie cookie : cookies) {
				
				if (cookie.getName().equals("goodscookie")) {
					String[] like_gcid = URLDecoder.decode(cookie.getValue(), "UTF-8").split(",", 2);
					Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(like_gcid[0]));
					
					if (goods == null) {
						break;
					}
					Long your_like_GoodsClass = goods.getGc().getId();
					
					Map<String,Object> params = Maps.newHashMap();
					params.put("goods_status", 0);
					params.put("gc_id", your_like_GoodsClass);
					params.put("id_no", goods.getId());
					params.put("orderBy", "goods_salenum");
					params.put("orderType", "desc");
					
					your_like_goods = this.goodsService.queryPageList(params, 0, 20);
					
					int gcs_size = your_like_goods.size();
					if (gcs_size >= 20) {
						break;
					}
					params.clear();
					params.put("goods_status", 0);
					params.put("id_no", goods.getId());
					params.put("orderBy", "goods_salenum");
					params.put("orderType", "desc");
					
					List<Goods> like_goods = this.goodsService.queryPageList(params, 0, 20 - gcs_size);
					
					for (int i = 0; i < like_goods.size(); i++) {
						int k = 0;
						for (int j = 0; j < your_like_goods.size(); j++) {
							if ((like_goods.get(i)).getId().equals(your_like_goods.get(j).getId())) {
								k++;
							}
						}
						if (k == 0) {
							your_like_goods.add(like_goods.get(i));
						}
					}
					break;
				}
				Map<String,Object> params = Maps.newHashMap();
				params.put("goods_status", 0);
				params.put("orderBy", "goods_salenum");
				params.put("orderType", "desc");
				
				your_like_goods = this.goodsService.queryPageList(params, 0, 20);
			}
		} else {
			Map<String,Object> params = Maps.newHashMap();
			params.put("goods_status", 0);
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			your_like_goods = this.goodsService.queryPageList(params, 0, 20);
		}
		mv.addObject("your_like_goods", your_like_goods);
		List<Goods> goods_last = Lists.newArrayList();
		Cookie[] cookies_last = request.getCookies();
		Map<String,Object> params = Maps.newHashMap();
		Set<Long> ids = Sets.newHashSet();
		
		if (cookies_last != null) {
			for (Cookie co : cookies_last) {
				if (co.getName().equals("goodscookie")) {
					String[] goods_id = URLDecoder.decode(co.getValue(), "UTF-8").split(",");
					int j = 4;
					if (j > goods_id.length) {
						j = goods_id.length;
					}
					for (int i = 0; i < j; i++) {
						((Set) ids).add(CommUtil.null2Long(goods_id[i]));
					}
				}
			}
		}
		
		if (!ids.isEmpty()) {
			params.put("ids", ids);
			
			goods_last = this.goodsService.queryPageList(params);
			
		}
		mv.addObject("goods_last", goods_last);
		return mv;
	}

	private List<List<GoodsSpecProperty>> generic_gsp(String gs_ids) {
		List<List<GoodsSpecProperty>> list = Lists.newArrayList();
		String[] gs_id_list = gs_ids.substring(1).split("\\|");
		
		for (String gd_id_info : gs_id_list) {
			
			String[] gs_info_list = gd_id_info.split(",");
			GoodsSpecProperty gsp = this.goodsSpecPropertyService.selectByPrimaryKey(CommUtil.null2Long(gs_info_list[0]));
			boolean create = true;
			
			for (List<GoodsSpecProperty> gsp_list : list) {
				for (GoodsSpecProperty gsp_temp : gsp_list) {
					if (gsp_temp.getSpec().getId().equals(gsp.getSpec().getId())) {
						gsp_list.add(gsp);
						create = false;
						break;
					}
				}
			}
			
			if (create) {
				List<GoodsSpecProperty> gsps = Lists.newArrayList();
				gsps.add(gsp);
				list.add(gsps);
			}
		}
		return list;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param currentPage
	 * @param goods_eva
	 * @return
	 */
	@RequestMapping({ "/goods_evaluation" })
	public ModelAndView goods_evaluation(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage,
			String goods_eva) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_evaluation.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10,"addTime", "desc");
		maps.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		maps.put("evaluate_type", "goods");
		maps.put("evaluate_status", 0);
		
		if (!CommUtil.null2String(goods_eva).equals("")) {
			if (goods_eva.equals("100")) {
				maps.put("evaluate_photos_no", "");
			} else {
				maps.put("evaluate_buyer_val", CommUtil.null2Int(goods_eva));
			}
		}
		
		IPageList pList = this.evaluateService.list(maps);
		String url = CommUtil.getURL(request) + "/goods_evaluation";
		mv.addObject("eva_objs", pList.getResult());
		mv.addObject("eva_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}

	private Set<Long> genericIds(Long id) {
		Set<Long> ids = new HashSet();
		if (id != null) {
			ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", id);
			
			List id_list = this.goodsClassService.queryPageList(params);
			
			ids.addAll(id_list);
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i));
				Set<Long> cids = genericIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
	}
	
}
