package com.redpigmall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.redpigmall.domain.*;
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
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigGoodsView2Action extends BaseAction{
	
	/**
	 * 加载商品颜色规格
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param color_id
	 * @return
	 */
	@RequestMapping({ "/load_goods_color_gsp" })
	public ModelAndView load_goods_color_gsp(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String color_id) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_color_image.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		
		List<Map> img_list = Lists.newArrayList();
		if ((obj != null) 
				&& (obj.getGoods_color_json() != null)
				&& (!obj.getGoods_color_json().equals(""))) {
			
			String url2 = CommUtil.getURL(request);
			
			if (!"".equals(CommUtil.null2String(this.configService.getSysConfig().getImageWebServer()))) {
				url2 = this.configService.getSysConfig().getImageWebServer();
			}
			
			List<Map> color_arr = (List) JSON.parseObject(obj.getGoods_color_json()).get("data");
			
			//[{"color_id":"516","img_ids":"-12227-12231-12235"},{"color_id":"517","img_ids":"-m_12215-12219-12223"}
			for (Map color : color_arr) {
				String img_ids = CommUtil.null2String(color.get("img_ids"));
				if (CommUtil.null2String(color.get("color_id")).equals(color_id)) {
//					img_ids = img_ids.replace("-m_", "-");
					
					String[] ids = img_ids.split("-");
					
					for (String img_id : ids) {
						if(img_id.indexOf("m") >=0){
							String id = img_id.split("_")[1];
							Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(id));
							
							if (img != null) {
								Map temp_map = Maps.newHashMap();
								temp_map.put("id", img_id);
								temp_map.put(
										"bigImg",
											url2 + "/" 
											+ img.getPath() + "/"
											+ img.getName());
								temp_map.put(
										"smallImg",
											url2 + "/"
											+ img.getPath() + "/"
											+ img.getName() + "_small."
											+ img.getExt());
								
								img_list.add(0,temp_map);
							}
						}else{
							
							Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(img_id));
							
							if (img != null) {
								Map temp_map = Maps.newHashMap();
								temp_map.put("id", img_id);
								temp_map.put(
										"bigImg",
											url2 + "/" 
											+ img.getPath() + "/"
											+ img.getName());
								temp_map.put(
										"smallImg",
											url2 + "/"
											+ img.getPath() + "/"
											+ img.getName() + "_small."
											+ img.getExt());
								
								img_list.add(temp_map);
							}
						}
					}
					
					
				}
				
			}
		}
		if (img_list.size() > 0) {
			mv.addObject("img_list", img_list);
		}
		mv.addObject("obj", obj);
		mv.addObject("color_id", color_id);
		return mv;
	}
	
	/**
	 * 加载商品规格
	 * @param request
	 * @param response
	 * @param gsp
	 * @param id
	 */
	@RequestMapping({ "/load_goods_gsp" })
	public void load_goods_gsp(HttpServletRequest request,
			HttpServletResponse response, String gsp, String id) {
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		Map<String, Object> map = Maps.newHashMap();
		double price = 0.0D;
		if (goods != null) {
			if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						price = CommUtil.null2Double(gg.getGg_price());
					}
				}
			} else{
				price = CommUtil.null2Double(goods.getStore_price());
				if (goods.getInventory_type().equals("spec")) {
					List<HashMap> list = JSON.parseArray(
							goods.getGoods_inventory_detail(), HashMap.class);
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						String[] temp_ids = CommUtil
								.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							price = CommUtil.null2Double(temp.get("price"));
						}
					}
				}
			}
			// 如果是秒杀，修改显示价格为秒杀价
			if(goods.getNuke_buy()==2&&goods.getNuke()!=null) {
				Map<String, Object> para = new HashMap<>();
				para.put("ng_goods_id", goods.getId());
				para.put("nuke_id", goods.getNuke().getId());
				String[] spcArray = gsp.split(",");
				String goods_spec_id = "";
				for (String str : spcArray) {
					goods_spec_id += (str + "_");
				}
				para.put("goods_spec_id", goods_spec_id);
				List<NukeGoods> nukeGoodsList = this.nukeGoodsService.selectObjByProperty(para);
				if (nukeGoodsList != null && nukeGoodsList.size() > 0) {
					NukeGoods nukeGoods = nukeGoodsList.get(0);
					price = CommUtil.null2Double(nukeGoods.getNg_price());
				}
			}
			User user = SecurityUserHolder.getCurrentUser();
			if ((goods.getActivity_status() == 2) && (user != null)) {
				BigDecimal rate = this.activityviewTools.getActivityGoodsRebate(id,CommUtil.null2String(user.getId()));
				price = BigDecimal.valueOf(price).multiply(rate).doubleValue();
			}
		}
		map.put("price", CommUtil.formatMoney(Double.valueOf(price)));
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
	
	private Set<Long> genericIds(Long id) {
		Set<Long> ids = new HashSet();
		if (id != null) {
			ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", id);
			List<GoodsClass> id_list = this.goodsClassService.queryPageList(params);
			for (GoodsClass goodsClass : id_list) {
				ids.add(goodsClass.getId());
			}
			
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i).getId());
				Set<Long> cids = genericIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
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
	 * 根据商城分类查看商品列表
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param brand_ids
	 * @param gs_ids
	 * @param properties
	 * @param all_property_status
	 * @param detail_property_status
	 * @param goods_type
	 * @param goods_inventory
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
	 * @param goods_cod
	 * @param goods_transfee
	 * @return
	 */
	@RequestMapping({ "/store_goods_list" })
	public ModelAndView store_goods_list(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String brand_ids, String gs_ids,
			String properties, String all_property_status,
			String detail_property_status, String goods_type,
			String goods_inventory, String goods_current_price_floor,
			String goods_current_price_ceiling, String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type, String area_id,
			String sc_id, String goods_cod, String goods_transfee) {
		
		ModelAndView mv = new RedPigJModelAndView("store_goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map pa = Maps.newHashMap();
		pa.put("id", CommUtil.null2Long(gc_id));
		pa.put("display", true);
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(pa);
		
		GoodsClass gc = null;
		if (gcs.size() > 0) {
			gc =  gcs.get(0);
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
						ma.put("display",true);
						
						List<ShowClass> sc_list = this.showClassService.queryPageList(ma);
						
						mv.addObject("sc", sc);
						mv.addObject("sc_list", sc_list);
					}
				} else if (sc.getDisplay()) {
					mv.addObject("psc", sc);
				}
			}
			mv.addObject("sc_id", sc_id);
		} else if (gc != null) {
			mv.addObject("gc0", gc);
			Map map1 = Maps.newHashMap();
			map1.put("showClass_id", gc.getShowClass_id());
			map1.put("display",true);
			
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
					psc_map.put("display",true);
					List<ShowClass> scs = this.showClassService.queryPageList(psc_map);
					
					if (scs.size() > 0) {
						List<GoodsClass> sc_ids;
						for (ShowClass set_sc : scs) {
							Map sc_map = Maps.newHashMap();
							sc_map.put("showClass_id", set_sc.getId());
							psc_map.put("display",true);
							
							sc_ids = this.goodsClassService.queryPageList(psc_map);
							
							Set<Long> gc_ids = null;
							for (GoodsClass goodsClass : sc_ids) {
									gc_ids = genericIds(CommUtil.null2Long(goodsClass.getId()));
									all_id.addAll(gc_ids);
							}
							
						}
					}
				} else if (sc.getParent().getDisplay()) {
					Map sc_map = Maps.newHashMap();
					sc_map.put("showClass_id", CommUtil.null2Long(sc_id));
					sc_map.put("display",true);
					List<GoodsClass> sc_ids = this.goodsClassService.queryPageList(sc_map);
					
					Set<Long> gc_ids = null;
					for (GoodsClass goodsClass : sc_ids) {
							gc_ids = genericIds(CommUtil.null2Long(goodsClass.getId()));
							all_id.addAll(gc_ids);
					}
				}
			}
			if ((all_id != null) && (all_id.size() > 0)) {
				maps.put("gc_ids", all_id);
			} 
			
			mv.addObject("sc_id", sc_id);
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
				
				for (int i1 = 0; i1 < brand_id_list.length; i1++) {
					String brand_id = brand_id_list[i1];
					goods_brand_ids.add(CommUtil.null2Long(brand_id));
					if (i1 == 0) {
						
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService.selectByPrimaryKey(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else if (i1 == brand_id_list.length - 1) {
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService.selectByPrimaryKey(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else {
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService.selectByPrimaryKey(CommUtil.null2Long(brand_id));
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
					GoodsSpecProperty gsp = gsp_list.get(0);
					goodsSpecPropertyIds.add(gsp.getId());
					
					Map<String,Object> map = Maps.newHashMap();
					map.put("name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					for (int i1 = 0; i1 < gsp_list.size(); i1++) {
						GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i1);
						goodsSpecPropertyIds.add(gsp.getId());
						
						if (i1 == 0) {
							Map<String, Object> map = Maps.newHashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i1 == gsp_list.size() - 1) {
							Map<String, Object> map = Maps.newHashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else {
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
		
		if (gc != null) {
			if (!CommUtil.null2String(properties).equals("")) {
				String[] properties_list = properties.substring(1).split("\\|");
				List<String> gtp_names = Lists.newArrayList();
				List<String> gtp_values = Lists.newArrayList();
				
				for (int i11 = 0; i11 < properties_list.length; i11++) {
					
					String property_info1 = CommUtil.null2String(properties_list[i11]);
					String[] property_info_list1 = property_info1.split(",");
					GoodsTypeProperty gtp = this.goodsTypePropertyService.selectByPrimaryKey(CommUtil.null2Long(property_info_list1[0]));
					gtp_names.add(gtp.getName().trim());
					gtp_values.add(property_info_list1[1].trim());
					
					Map<String, Object> map = Maps.newHashMap();
					map.put("name", gtp.getName());
					map.put("value", property_info_list1[1]);
					map.put("type", "properties");
					map.put("id", gtp.getId());
					goods_property.add(map);
				}
				
				maps.put("gtp_names", gtp_names);
				maps.put("gtp_values", gtp_values);
				
				mv.addObject("properties", properties);
				
				List<GoodsTypeProperty> filter_properties = Lists.newArrayList();
				List<String> hc_property_list = Lists.newArrayList();
				if (gc.getGoodsType() != null) {
					
					for (GoodsTypeProperty gtp11 : gc.getGoodsType().getProperties()) {
						
						boolean flag = true;
						GoodsTypeProperty gtp1 = new GoodsTypeProperty();
						gtp1.setDisplay(gtp11.getDisplay());
						gtp1.setGoodsType(gtp11.getGoodsType());
						gtp1.setHc_value(gtp11.getHc_value());
						gtp1.setId(gtp11.getId());
						gtp1.setName(gtp11.getName());
						gtp1.setSequence(gtp11.getSequence());
						gtp1.setValue(gtp11.getValue());
						for (String hc_property : hc_property_list) {
							String[] hc_list = hc_property.split(":");
							if (hc_list[0].equals(gtp11.getName())) {
								String[] hc_temp_list = hc_list[1].split(",");
								String[] defalut_list_value = gtp1.getValue().split(",");
								ArrayList<String> defalut_list = new ArrayList(Arrays.asList(defalut_list_value));
								
								for (String hc_temp : hc_temp_list) {
									defalut_list.remove(hc_temp);
								}
								
								String value = "";
								for (int i11 = defalut_list.size() - 1; i11 >= 0; i11--) {
									value = (String) defalut_list.get(i11) + "," + value;
								}
								gtp1.setValue(value.substring(0,value.length() - 1));
								flag = false;
								break;
							}
						}
						
						if (flag) {
							if (!CommUtil.null2String(gtp11.getHc_value()).equals("")) {
								String[] list1 = gtp11.getHc_value().split("#");
								for (int i1 = 0; i1 < properties_list.length; i1++) {
									String property_info = CommUtil.null2String(properties_list[i1]);
									String[] property_info_list = property_info.split(",");
									if (property_info_list[1].equals(list1[0])) {
										hc_property_list.add(list1[1]);
									}
								}
							}
							filter_properties.add(gtp11);
						} else {
							filter_properties.add(gtp1);
						}
					}
					mv.addObject("filter_properties", filter_properties);
				}
			} else {
				mv.addObject("filter_properties",gc.getGoodsType() != null ? gc.getGoodsType().getProperties() : "");
			}
		}
		if ((area_id != null) && (!area_id.equals(""))) {
			this.areaViewTools.setDefaultArea(request, area_id);
			mv.addObject("area_id", area_id);
		}
		this.areaViewTools.getUserAreaInfo(request, mv);
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
			this.queryTools.queryGoodInventory(area_id, maps, goods_type);
			mv.addObject("goods_inventory", goods_inventory);
		}
		if ((goods_type != null) && (!goods_type.equals(""))) {
			maps.put("goods_type", CommUtil.null2Int(goods_type));
			mv.addObject("goods_type", goods_type);
		}
		
		IPageList pList = this.goodsService.list(maps);
		
		String url = CommUtil.getURL(request) + "/store_goods_list_ajax";
		mv.addObject("objs", pList.getResult());
		mv.addObject("store_goods_list_gotoPageAjaxHTML", CommUtil
				.showPageAjaxHtml(url, "", pList.getCurrentPage(),
						pList.getPages(), pList.getPageSize()));
		mv.addObject("gc", gc);
		mv.addObject("orderBy", orderBy);
		mv.addObject("goods_property", goods_property);
		mv.addObject("allCount", Integer.valueOf(pList.getRowCount()));
		if (this.configService.getSysConfig().getZtc_status()) {
			Object left_ztc_goods = null;
			Map ztc_map = Maps.newHashMap();
			ztc_map.put("ztc_status", Integer.valueOf(2));
			ztc_map.put("orderBy", "ztc_dredge_price");
			ztc_map.put("orderType", "desc");
			
			ztc_map.put("ztc_gold_more_than", Integer.valueOf(0));
			
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				left_ztc_goods = this.goodsService.queryPageList(ztc_map, 0, 10);
			}
			
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map.put("gc_ids", ids);
				ztc_map.put("ztc_begin_time_less_than_equal", new Date());
				
				left_ztc_goods = this.goodsService.queryPageList(ztc_map, 0, 10);
			}
			mv.addObject("left_ztc_goods", left_ztc_goods);

			List<Goods> top_ztc_goods = null;
			Map ztc_map2 = Maps.newHashMap();
			ztc_map2.put("ztc_status", Integer.valueOf(2));
			
			ztc_map2.put("ztc_gold_more_than", Integer.valueOf(0));
			
			ztc_map2.put("orderBy", "ztc_dredge_price");
			ztc_map2.put("orderType", "desc");
			
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				top_ztc_goods = this.goodsService.queryPageList(ztc_map2, 0, 4);
			}
			
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map2.put("gc_ids", ids);
				top_ztc_goods = this.goodsService.queryPageList(ztc_map2, 0, 4);
			}
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map<String,Object> params = Maps.newHashMap();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List<Goods> top_ztc_goods = this.goodsService.queryPageList(params, 0, 4);
			
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List all_goods = this.goodsService.queryPageList(params);
			
			List<Goods> left_ztc_goods = this.goodsService.queryPageList(params, 4, all_goods.size());
			
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		
		if ((detail_property_status != null) && (!detail_property_status.equals(""))) {
			
			mv.addObject("detail_property_status", detail_property_status);
			String[] temp_str = detail_property_status.split(",");
			Map pro_map = Maps.newHashMap();
			List pro_list = Lists.newArrayList();
			
			for (String property_status : temp_str) {
				if ((property_status != null) && (!property_status.equals(""))) {
					String[] mark = property_status.split("_");
					pro_map.put(mark[0], mark[1]);
					pro_list.add(mark[0]);
				}
			}
			mv.addObject("pro_list", pro_list);
			mv.addObject("pro_map", pro_map);
		}
		mv.addObject("all_property_status", all_property_status);
		mv.addObject("userTools", this.userTools);
		
		Object goods_compare_list = (List) request.getSession(false).getAttribute("goods_compare_cart");
		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		mv.addObject("goods_compare_list", goods_compare_list);
		mv.addObject("goodsViewTools", this.goodsViewTools);

		return mv;
	}
	
}
