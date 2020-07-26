package com.redpigmall.module.weixin.view.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsTypeProperty;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.module.weixin.view.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title: RedPigWeixinClassViewAction.java
 * </p>
 * 
 * <p>
 * Description:移动端商城分类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinClassViewAction extends BaseAction {
	
	/**
	 * 手机端商城分类请求
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping({ "/goodsclass" })
	public ModelAndView goodsclass(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = null;
		if ((id == null) || (id.equals(""))) {
			mv = new RedPigJModelAndView("weixin/goods_class.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			if ("index".equals(type)) {
				mv = new RedPigJModelAndView("weixin/index_goods_class.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("display", Boolean.valueOf(true));
			params.put("parent", -1);
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			
			List<ShowClass> gcs = this.showClassService.queryPageList(params);
			
			mv.addObject("gcs", gcs);
			if (gcs.size() > 0) {
				ShowClass sc = (ShowClass) gcs.get(0);
				mv.addObject("sc", sc);
			}
		} else {
			mv = new RedPigJModelAndView("weixin/index_goods_class_child.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			ShowClass sc = this.showClassService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			mv.addObject("second_gcs", sc.getChilds());
			mv.addObject("sc", sc);
		}
		mv.addObject("showClassTools", this.showClassTools);
		mv.addObject("imageTools", this.imageTools);
		return mv;
	}
	/**
	 * 获取二三级类目
	 * @param id 一级类目id
	 * @return
	 * @author wangshen
	 */
//	@RequestMapping(value="/goodsclass",method=RequestMethod.POST)
//	@ResponseBody
//	public BaseResponseDto<Object> goodsclass(@RequestParam("id")String id) {
//		BaseResponseDto<Object> br = new BaseResponseDto<Object>();
//		Map<String, Object> map = Maps.newHashMap();
//		List<ShowClass> second_scs = Lists.newArrayList();
//		try {
//				//List<ShowClass> second_gcs = this.showClassService.queryPageListWithNoRelations(params);//这种方式不查询关联表,根据addTime降序排列
//				second_scs = this.showClassService.queryListWithNoRelations(CommUtil.null2Long(id));
//				map.put("second_scs", second_scs);
//			List third_gcs = Lists.newArrayList();
//			Map<String, Object> params = Maps.newHashMap();
//			for (ShowClass sc : second_scs) {
//				params.put("showClass_id", sc.getId());
//				params.put("deleteStatus", "0");
//				params.put("orderBy", "sequence");
//				params.put("orderType", "asc");
//				List<Map<String, Object>> third_gc = this.goodsClassService.queryListWithNoRelations(params);
//				third_gcs.add(third_gc);
//			}
//			map.put("third_gcs", third_gcs);
//			br.setContent(map);
//		} catch (Exception e) {
//			br.setError(ErrorEnum.sys_error);
//			logger.error(e);
//		}
//		return br;
//	}
	
	/**
	 * 根据商城分类查看商品列表
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@RequestMapping({ "/sec_goodsclass" })
	public ModelAndView sec_goodsclass(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = null;
		if ("second".equals(CommUtil.null2String(type))) {
			mv = new RedPigJModelAndView("weixin/second_goods_class_child.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("showClassTools", this.showClassTools);
			mv.addObject("imageTools", this.imageTools);
			mv.addObject("id", id);
			return mv;
		}
		mv = new RedPigJModelAndView("weixin/second_goods_class.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (StringUtils.isNotBlank(id)) {
			ShowClass sc = this.showClassService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			List<ShowClass> list = new ArrayList(sc.getChilds());
			mv.addObject("second_gcs", list);
			if (sc.getChilds().size() > 0) {
				ShowClass sc2 = (ShowClass) list.get(0);
				mv.addObject("sc2", sc2);
			}
		}
		mv.addObject("showClassTools", this.showClassTools);
		mv.addObject("imageTools", this.imageTools);
		return mv;
	}
	
	
	@RequestMapping({ "/class_items" })
	public ModelAndView class_items(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String properties,
			String big_map, String brand_ids, String gs_ids,
			String all_property_status, String detail_property_status,
			String goods_type, String goods_inventory, String goods_transfee,
			String goods_cod, String goods_current_price_floor,
			String goods_current_price_ceiling, String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type, String area_id,
			String sc_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/class_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map pa = Maps.newHashMap();
		pa.put("id", CommUtil.null2Long(gc_id));
		pa.put("display", 1);
		
		List gcs = this.goodsClassService.queryPageList(pa); // 获取当前三级目录
		
		GoodsClass gc = null;
		if (gcs.size() > 0) {
			gc = (GoodsClass) gcs.get(0);
		}
		if ((sc_id != null) && (!"".equals(sc_id))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", CommUtil.null2Long(sc_id));
			map.put("display", 1);
			
			List<ShowClass> scs = this.showClassService.queryPageList(map);
			
			if (scs.size() > 0) {
				ShowClass sc = (ShowClass) scs.get(0);
				if (sc.getParent() != null) {
					if (sc.getParent().getDisplay()) {
						
						Map ma = Maps.newHashMap();
						ma.put("parent", sc.getParent().getId());
						ma.put("display", 1);
						
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
			map1.put("display", 1);
			
			List list = this.goodsClassService.queryPageList(map1);// 取当前二级目录下的三级目录元素
			
			mv.addObject("gc_list0", list);
			ShowClass sc = this.showClassService.selectByPrimaryKey(gc
					.getShowClass_id());// 取当前二级目录
			if ((sc != null) && (sc.getParent() != null)) {// 判断二级目录对象与对应的一级目录不为null
				Set<ShowClass> sc_list = sc.getParent().getChilds();
				mv.addObject("sc", sc);
				mv.addObject("sc_list", sc_list);
			}
		}
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "goods_salenum";
		}
		if ((orderType == null) || (orderType.equals(""))) {
			orderType = "desc";
		}// 默认销量从高到低排序
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,24, orderBy, orderType);
		Set<Long> ids = null;
		if (gc != null) {
			ids = genericIds(gc.getId());
		}
		if ((ids != null) && (ids.size() > 0)) {
			maps.put("gc_ids", ids);
		}
		int i = 0;
		if ((sc_id != null) && (!"".equals(sc_id))) {
			Set<Long> all_id = new HashSet();
			
			ShowClass sc = this.showClassService.selectByPrimaryKey(CommUtil.null2Long(sc_id));
			
			if ((sc != null) && (sc.getDisplay())) {
				if (sc.getParent() == null) {
					Map psc_map = Maps.newHashMap();
					psc_map.put("parent", sc.getId());
					psc_map.put("display", 1);
					
					List<ShowClass> scs = this.showClassService.queryPageList(psc_map);
					
					if (scs.size() > 0) {
						List sc_ids;
						for (ShowClass set_sc : scs) {
							Map sc_map = Maps.newHashMap();
							sc_map.put("showClass_id", set_sc.getId());
							sc_map.put("display", 1);
							
							sc_ids = this.goodsClassService.queryPageList(sc_map);
							
							Set<Long> gc_ids = null;
							i = 0;
							if ((sc_ids.get(i) != null)
									&& (!"".equals(sc_ids.get(0)))) {
								gc_ids = genericIds(CommUtil.null2Long(sc_ids
										.get(i)));
								all_id.addAll(gc_ids);
							}
							i++;
						}
					}
				} else if (sc.getParent().getDisplay()) {
					Map sc_map = Maps.newHashMap();
					sc_map.put("showClass_id", CommUtil.null2Long(sc_id));
					sc_map.put("display", 1);
					
					List sc_ids = this.goodsClassService.queryPageList(sc_map);
					
					Set<Long> gc_ids = null;
					for (Object id : sc_ids) {
						if ((id != null) && (!"".equals(id.toString()))) {
							gc_ids = genericIds(CommUtil.null2Long(id));
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
			
			maps.put("goods_transfee",1);
			mv.addObject("goods_transfee", goods_transfee);
		}
		maps.put("goods_status",0);//设置商品为已审核
		
		List<Map> goods_property = Lists.newArrayList();
		if (!CommUtil.null2String(brand_ids).equals("")) {
			if (brand_ids.indexOf(",") < 0) {
				brand_ids = brand_ids + ",";
			}
			String[] brand_id_list = CommUtil.null2String(brand_ids).split(",");
			if (brand_id_list.length == 1) {
				String brand_id = brand_id_list[0];
				
				maps.put("goods_brand_id",CommUtil.null2Long(brand_id));
				
				Map<String, Object> map = Maps.newHashMap();
				GoodsBrand brand = this.brandService.selectByPrimaryKey(CommUtil
						.null2Long(brand_id));
				if (brand != null) {
					map.put("name", "品牌");
					map.put("value", brand.getName());
					map.put("type", "brand");
					map.put("id", brand.getId());
					goods_property.add(map);
				}
			} else {
				List<Long> goods_brand_ids = Lists.newArrayList();
				for (String brand_id : brand_id_list) {
					
					if (i == 0) {
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService
								.selectByPrimaryKey(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else if (i == brand_id_list.length - 1) {
						
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService
								.selectByPrimaryKey(CommUtil.null2Long(brand_id));
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
				maps.put("goods_brand_ids",goods_brand_ids);
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
					
					Map<String,Object> map = Maps.newHashMap();
					map.put("name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					
					for (i = 0; i < gsp_list.size(); i++) {
						GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
						goodsSpecPropertyIds.add(gsp.getId());
						
						if (i == 0) {
							
							Map<String, Object> map = Maps.newHashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i == gsp_list.size() - 1) {
							
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
		String[] list1;
		String property_info;
		if (gc != null) {
			if (!CommUtil.null2String(properties).equals("")) {
				String[] properties_list = properties.substring(1).split("\\|");
				List<String> gtp_names = Lists.newArrayList();
				
				for (i = 0; i < properties_list.length; i++) {
					property_info = CommUtil.null2String(properties_list[i]);
					String[] property_info_list = property_info.split(",");
					GoodsTypeProperty gtp = this.goodsTypePropertyService
							.selectByPrimaryKey(CommUtil.null2Long(property_info_list[0]));
					
					
					gtp_names.add(gtp.getName().trim());
					gtp_names.add(property_info_list[1].trim());
					
					Map<String, Object> map = Maps.newHashMap();
					map.put("name", ((GoodsTypeProperty) gtp).getName());
					map.put("value", property_info_list[1]);
					map.put("type", "properties");
					map.put("id", ((GoodsTypeProperty) gtp).getId());
					goods_property.add(map);
				}
				maps.put("gtp_names", gtp_names);
				
				mv.addObject("properties", properties);

				List<GoodsTypeProperty> filter_properties = Lists
						.newArrayList();
				List<String> hc_property_list = Lists.newArrayList();
				if (gc.getGoodsType() != null) {
					for (GoodsTypeProperty gtp : gc.getGoodsType()
							.getProperties()) {

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
								for (String s : defalut_list) {
									value = s + "," + value;
								}
								gtp1.setValue(value.substring(0,
										value.length() - 1));
								flag = false;
								break;
							}
						}
						if (flag) {
							if (!CommUtil.null2String(gtp.getHc_value())
									.equals("")) {
								list1 = gtp.getHc_value().split("#");
								for (i = 0; i < properties_list.length; i++) {
									property_info = CommUtil
											.null2String(properties_list[i]);
									String[] property_info_list = property_info
											.split(",");
									if (property_info_list[1].equals(list1[0])) {
										hc_property_list.add(list1[1]);
									}
								}
							}
							filter_properties.add(gtp);
						} else {
							filter_properties.add(gtp1);
						}
					}
					mv.addObject("filter_properties", filter_properties);
				}
			} else {
				mv.addObject("filter_properties",
						gc.getGoodsType() != null ? gc.getGoodsType()
								.getProperties() : "");
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
			maps.put("group_buy", Integer.valueOf(2));
			
			mv.addObject("group_buy", group_buy);
		}
		if (!CommUtil.null2String(enough_reduce).equals("")) {
			maps.put("enough_reduce", Integer.valueOf(1));
			
			mv.addObject("enough_reduce", enough_reduce);
		}
		if (!CommUtil.null2String(order_enough_give_status).equals("")) {
			maps.put("order_enough_give_status", Integer.valueOf(1));
			
			mv.addObject("order_enough_give_status", order_enough_give_status);
		}
		if (!CommUtil.null2String(activity_status).equals("")) {
			maps.put("activity_status", Integer.valueOf(2));
			
			mv.addObject("activity_status", activity_status);
		}
		if (!CommUtil.null2String(combin_status).equals("")) {
			maps.put("combin_status", Integer.valueOf(1));
			
			mv.addObject("combin_status", combin_status);
		}
		if (!CommUtil.null2String(advance_sale_type).equals("")) {
			maps.put("advance_sale_type", Integer.valueOf(1));
			
			mv.addObject("advance_sale_type", advance_sale_type);
		}
		if (!CommUtil.null2String(f_sale_type).equals("")) {
			maps.put("f_sale_type", Integer.valueOf(1));
			
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
		mv.addObject("totalPage", Integer.valueOf(pList.getPages()));

		mv.addObject("objs", pList.getResult());

		mv.addObject("gc", gc);
		mv.addObject("orderBy", orderBy);
		mv.addObject("goods_property", goods_property);
		mv.addObject("allCount", Integer.valueOf(pList.getRowCount()));
		if (this.configService.getSysConfig().getZtc_status()) {
			
			List left_ztc_goods = null;
			Map<String,Object> ztc_map = Maps.newHashMap();
			ztc_map.put("ztc_status", Integer.valueOf(3));
			ztc_map.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map.put("ztc_gold", Integer.valueOf(0));
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				ztc_map.put("orderBy", "ztc_dredge_price");
				ztc_map.put("orderType", "desc");
				
				List all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
				
				left_ztc_goods = this.goodsService.queryPageList(ztc_map,4, all_left_ztc_goods.size());
				
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map.put("gc_ids", ids);
				ztc_map.put("orderBy", "ztc_dredge_price");
				ztc_map.put("orderType", "desc");
				
				List all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
				
				left_ztc_goods = this.goodsService.queryPageList(ztc_map, 4, all_left_ztc_goods.size());
				
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			}
			mv.addObject("left_ztc_goods", left_ztc_goods);

			List top_ztc_goods = null;
			Map ztc_map2 = Maps.newHashMap();
			ztc_map2.put("ztc_status", Integer.valueOf(3));
			ztc_map2.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map2.put("ztc_gold", Integer.valueOf(0));
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				ztc_map2.put("orderBy", "ztc_dredge_price");
				ztc_map2.put("orderType", "desc");
				top_ztc_goods = this.goodsService.queryPageList(ztc_map2,0,4);
				
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map2.put("gc_ids", ids);
				ztc_map2.put("orderBy", "ztc_dredge_price");
				ztc_map2.put("orderType", "desc");
				
				top_ztc_goods = this.goodsService.queryPageList(ztc_map2,0,4);
				
			}
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map<String,Object> params = Maps.newHashMap();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List top_ztc_goods = this.goodsService.queryPageList(params,0,4);//前4个推荐商品（销量从高到低）
			
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List all_goods = this.goodsService.queryPageList(params);//所有推荐商品（销量从高到低）
			
			List left_ztc_goods = this.goodsService.queryPageList(params, 4, all_goods.size());//第4个开始推荐商品（销量从高到低）
			
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		if ((detail_property_status != null)
				&& (!detail_property_status.equals(""))) {
			mv.addObject("detail_property_status", detail_property_status);
			String[] temp_str = detail_property_status.split(",");
			Object pro_map = Maps.newHashMap();
			List pro_list = Lists.newArrayList();
			for (String property_status : temp_str) {
				if ((property_status != null) && (!property_status.equals(""))) {
					String[] mark = property_status.split("_");
					((Map) pro_map).put(mark[0], mark[1]);
					pro_list.add(mark[0]);
				}
			}
			mv.addObject("pro_list", pro_list);
			mv.addObject("pro_map", pro_map);
		}
		mv.addObject("all_property_status", all_property_status);
		mv.addObject("userTools", this.userTools);

		Object goods_compare_list = (List) request.getSession(false)
				.getAttribute("goods_compare_cart");
		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		mv.addObject("goods_compare_list", goods_compare_list);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("gc_id", gc_id);
		mv.addObject("big_map", big_map);
		return mv;
	}
	
	@RequestMapping({ "/class_goods_ajax" })
	public ModelAndView class_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String properties,
			String big_map, String brand_ids, String gs_ids,
			String all_property_status, String detail_property_status,
			String goods_type, String goods_inventory, String goods_transfee,
			String goods_cod, String goods_current_price_floor,
			String goods_current_price_ceiling, String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type, String area_id,
			String sc_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/class_goods_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map pa = Maps.newHashMap();
		pa.put("id", CommUtil.null2Long(gc_id));
		pa.put("display", 1);
		
		List gcs = this.goodsClassService.queryPageList(pa);
		
		GoodsClass gc = null;
		if (gcs.size() > 0) {
			gc = (GoodsClass) gcs.get(0);
		}
		
		if ((sc_id != null) && (!"".equals(sc_id))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", CommUtil.null2Long(sc_id));
			map.put("display", 1);
			
			List<ShowClass> scs = this.showClassService.queryPageList(map);
			
			if (scs.size() > 0) {
				ShowClass sc = (ShowClass) scs.get(0);
				if (sc.getParent() != null) {
					if (sc.getParent().getDisplay()) {
						
						Map ma = Maps.newHashMap();
						ma.put("parent", sc.getParent().getId());
						ma.put("display", 1);
						
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
			map1.put("display", 1);
			
			List list = this.goodsClassService.queryPageList(map1);
			
			mv.addObject("gc_list0", list);
			ShowClass sc = this.showClassService.selectByPrimaryKey(gc
					.getShowClass_id());
			if ((sc != null) && (sc.getParent() != null)) {
				Set<ShowClass> sc_list = sc.getParent().getChilds();
				mv.addObject("sc", sc);
				mv.addObject("sc_list", sc_list);
			}
		}
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "goods_salenum";
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
		int i;
		if ((sc_id != null) && (!"".equals(sc_id))) {
			Set<Long> all_id = new HashSet();
			ShowClass sc = this.showClassService.selectByPrimaryKey(CommUtil.null2Long(sc_id));
			if ((sc != null) && (sc.getDisplay())) {
				if (sc.getParent() == null) {
					Map psc_map = Maps.newHashMap();
					psc_map.put("parent", sc.getId());
					psc_map.put("display", 1);
					
					List<ShowClass> scs = this.showClassService.queryPageList(psc_map);
					
					if (scs.size() > 0) {
						List sc_ids;
						for (ShowClass set_sc : scs) {
							Map sc_map = Maps.newHashMap();
							sc_map.put("showClass_id", set_sc.getId());
							sc_map.put("display", 1);
							
							sc_ids = this.goodsClassService.queryPageList(sc_map);
							
							Set<Long> gc_ids = null;
							i = 0;
							if ((sc_ids.get(i) != null)
									&& (!"".equals(sc_ids.get(0)))) {
								gc_ids = genericIds(CommUtil.null2Long(sc_ids
										.get(i)));
								all_id.addAll(gc_ids);
							}
							i++;
						}
					}
				} else if (sc.getParent().getDisplay()) {
					Map sc_map = Maps.newHashMap();
					sc_map.put("showClass_id", CommUtil.null2Long(sc_id));
					sc_map.put("display", 1);
					
					List sc_ids = this.goodsClassService.queryPageList(sc_map);
					
					Set<Long> gc_ids = null;
					for (i = 0; i < sc_ids.size(); i++) {
						if ((sc_ids.get(i) != null)
								&& (!"".equals(sc_ids.get(0)))) {
							gc_ids = genericIds(CommUtil.null2Long(sc_ids
									.get(i)));
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
				
				for (i = 0; i < brand_id_list.length; i++) {
					String brand_id = brand_id_list[i];
					goods_brand_ids.add(CommUtil.null2Long(brand_id));
					if (i == 0) {
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService
								.selectByPrimaryKey(CommUtil.null2Long(brand_id));
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
					GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(0);
					
					goodsSpecPropertyIds.add(gsp.getId());
					Map<String,Object> map = Maps.newHashMap();
					map.put("name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					for (i = 0; i < gsp_list.size(); i++) {
						GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
						goodsSpecPropertyIds.add(gsp.getId());
						if (i == 0) {
							
							Map<String, Object> map = Maps.newHashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i == gsp_list.size() - 1) {
							
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
		String[] list1;
		String property_info;
		if (gc != null) {
			if (!CommUtil.null2String(properties).equals("")) {
				String[] properties_list = properties.substring(1).split("\\|");
				List<String> gtp_names = Lists.newArrayList();
				
				for (i = 0; i < properties_list.length; i++) {
					property_info = CommUtil.null2String(properties_list[i]);
					String[] property_info_list = property_info.split(",");
					GoodsTypeProperty gtp = this.goodsTypePropertyService
							.selectByPrimaryKey(CommUtil.null2Long(property_info_list[0]));
					
					gtp_names.add(gtp.getName());
					gtp_names.add(property_info_list[1].trim());
					
					Map<String, Object> map = Maps.newHashMap();
					map.put("name", ((GoodsTypeProperty) gtp).getName());
					map.put("value", property_info_list[1]);
					map.put("type", "properties");
					map.put("id", ((GoodsTypeProperty) gtp).getId());
					goods_property.add(map);
				}
				
				maps.put("gtp_names", gtp_names);
				
				mv.addObject("properties", properties);

				List<GoodsTypeProperty> filter_properties = Lists
						.newArrayList();
				List<String> hc_property_list = Lists.newArrayList();
				if (gc.getGoodsType() != null) {
					for (GoodsTypeProperty gtp : gc.getGoodsType()
							.getProperties()) {
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
								for (i = defalut_list.size() - 1; i >= 0; i--) {
									value = (String) defalut_list.get(i) + ","
											+ value;
								}
								gtp1.setValue(value.substring(0,
										value.length() - 1));
								flag = false;
								break;
							}
						}
						if (flag) {
							if (!CommUtil.null2String(gtp.getHc_value())
									.equals("")) {
								list1 = gtp.getHc_value().split("#");
								for (i = 0; i < properties_list.length; i++) {
									property_info = CommUtil
											.null2String(properties_list[i]);
									String[] property_info_list = property_info
											.split(",");
									if (property_info_list[1].equals(list1[0])) {
										hc_property_list.add(list1[1]);
									}
								}
							}
							filter_properties.add(gtp);
						} else {
							filter_properties.add(gtp1);
						}
					}
					mv.addObject("filter_properties", filter_properties);
				}
			} else {
				mv.addObject("filter_properties",
						gc.getGoodsType() != null ? gc.getGoodsType()
								.getProperties() : "");
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
			maps.put("group_buy", Integer.valueOf(2));
			mv.addObject("group_buy", group_buy);
		}
		if (!CommUtil.null2String(enough_reduce).equals("")) {
			maps.put("enough_reduce", Integer.valueOf(1));
			mv.addObject("enough_reduce", enough_reduce);
		}
		if (!CommUtil.null2String(order_enough_give_status).equals("")) {
			maps.put("order_enough_give_status", Integer.valueOf(1));
			mv.addObject("order_enough_give_status", order_enough_give_status);
		}
		if (!CommUtil.null2String(activity_status).equals("")) {
			maps.put("activity_status", Integer.valueOf(2));
			mv.addObject("activity_status", activity_status);
		}
		if (!CommUtil.null2String(combin_status).equals("")) {
			maps.put("combin_status", Integer.valueOf(1));
			mv.addObject("combin_status", combin_status);
		}
		if (!CommUtil.null2String(advance_sale_type).equals("")) {
			maps.put("advance_sale_type", Integer.valueOf(1));
			mv.addObject("advance_sale_type", advance_sale_type);
		}
		if (!CommUtil.null2String(f_sale_type).equals("")) {
			maps.put("f_sale_type", Integer.valueOf(1));
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
		System.out.println(pList.getPages());
		mv.addObject("totalPage", Integer.valueOf(pList.getPages()));

		mv.addObject("objs", pList.getResult());

		mv.addObject("gc", gc);
		mv.addObject("orderBy", orderBy);
		mv.addObject("goods_property", goods_property);
		mv.addObject("allCount", Integer.valueOf(pList.getRowCount()));
		
		if (this.configService.getSysConfig().getZtc_status()) {
			List left_ztc_goods = null;
			Map<String,Object> ztc_map = Maps.newHashMap();
			ztc_map.put("ztc_status", Integer.valueOf(3));
			ztc_map.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map.put("ztc_gold_more_than", Integer.valueOf(0));
			ztc_map.put("orderBy", "ztc_dredge_price");
			ztc_map.put("orderType", "desc");
			
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				
				List all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
				
				left_ztc_goods = this.goodsService.queryPageList(ztc_map, 4, all_left_ztc_goods.size());
				
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map.put("gc_ids", ids);
				
				List all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
				
				left_ztc_goods = this.goodsService.queryPageList(ztc_map, 4, all_left_ztc_goods.size());
				
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			}
			mv.addObject("left_ztc_goods", left_ztc_goods);

			List top_ztc_goods = null;
			Map ztc_map2 = Maps.newHashMap();
			ztc_map2.put("ztc_status", Integer.valueOf(3));
			ztc_map2.put("ztc_begin_time_less_than_equal", new Date());
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
			
			List top_ztc_goods = this.goodsService.queryPageList(params, 0, 4);
			
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List all_goods = this.goodsService.queryPageList(params);
			
			List left_ztc_goods = this.goodsService.queryPageList(params, 4, all_goods.size());
			
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		if ((detail_property_status != null)
				&& (!detail_property_status.equals(""))) {
			mv.addObject("detail_property_status", detail_property_status);
			String[] temp_str = detail_property_status.split(",");
			Map<String,Object> pro_map = Maps.newHashMap();
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

		Object goods_compare_list = (List) request.getSession(false)
				.getAttribute("goods_compare_cart");
		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		mv.addObject("goods_compare_list", goods_compare_list);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("gc_id", gc_id);
		mv.addObject("big_map", big_map);
		return mv;
	}


	
	@RequestMapping({ "/class_goods" })
	public ModelAndView class_goods(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String currentPage,
			String orderBy, String orderType, String properties,
			String big_map, String brand_ids, String gs_ids,
			String all_property_status, String detail_property_status,
			String goods_type, String goods_inventory, String goods_transfee,
			String goods_cod, String goods_current_price_floor,
			String goods_current_price_ceiling, String group_buy,
			String enough_reduce, String order_enough_give_status,
			String activity_status, String combin_status,
			String advance_sale_type, String f_sale_type, String area_id,
			String sc_id) {
		ModelAndView mv = new RedPigJModelAndView("weixin/class_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map pa = Maps.newHashMap();
		pa.put("id", CommUtil.null2Long(gc_id));
		pa.put("display", 1);
		
		List gcs = this.goodsClassService.queryPageList(pa);
		
		GoodsClass gc = null;
		if (gcs.size() > 0) {
			gc = (GoodsClass) gcs.get(0);
		}
		if ((sc_id != null) && (!"".equals(sc_id))) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", CommUtil.null2Long(sc_id));
			map.put("display", 1);
			
			List<ShowClass> scs = this.showClassService.queryPageList(map);
			
			if (scs.size() > 0) {
				ShowClass sc = (ShowClass) scs.get(0);
				if (sc.getParent() != null) {
					if (sc.getParent().getDisplay()) {
						
						Map ma = Maps.newHashMap();
						ma.put("parent", sc.getParent().getId());
						ma.put("display", 1);
						
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
			map1.put("display", 1);
			
			List list = this.goodsClassService.queryPageList(map1);
			
			mv.addObject("gc_list0", list);
			ShowClass sc = this.showClassService.selectByPrimaryKey(gc
					.getShowClass_id());
			if ((sc != null) && (sc.getParent() != null)) {
				Set<ShowClass> sc_list = sc.getParent().getChilds();
				mv.addObject("sc", sc);
				mv.addObject("sc_list", sc_list);
			}
		}
		if ((orderBy == null) || (orderBy.equals(""))) {
			orderBy = "goods_salenum";
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
		int i = 0;
		if ((sc_id != null) && (!"".equals(sc_id))) {
			Set<Long> all_id = new HashSet();
			
			ShowClass sc = this.showClassService.selectByPrimaryKey(CommUtil.null2Long(sc_id));
			
			if ((sc != null) && (sc.getDisplay())) {
				if (sc.getParent() == null) {
					Map psc_map = Maps.newHashMap();
					psc_map.put("parent", sc.getId());
					psc_map.put("display", 1);
					
					List<ShowClass> scs = this.showClassService.queryPageList(psc_map);
					
					if (scs.size() > 0) {
						List sc_ids;
						for (ShowClass set_sc : scs) {
							Map sc_map = Maps.newHashMap();
							sc_map.put("showClass_id", set_sc.getId());
							sc_map.put("display", 1);
							
							sc_ids = this.goodsClassService.queryPageList(sc_map);
							
							Set<Long> gc_ids = null;
							i = 0;
							if ((sc_ids.get(i) != null)
									&& (!"".equals(sc_ids.get(0)))) {
								gc_ids = genericIds(CommUtil.null2Long(sc_ids
										.get(i)));
								all_id.addAll(gc_ids);
							}
							i++;
						}
					}
				} else if (sc.getParent().getDisplay()) {
					Map sc_map = Maps.newHashMap();
					sc_map.put("showClass_id", CommUtil.null2Long(sc_id));
					sc_map.put("display", 1);
					
					List sc_ids = this.goodsClassService.queryPageList(sc_map);
					
					Set<Long> gc_ids = null;
					for (Object id : sc_ids) {
						if ((id != null) && (!"".equals(id.toString()))) {
							gc_ids = genericIds(CommUtil.null2Long(id));
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
			
			maps.put("goods_transfee",1);
			mv.addObject("goods_transfee", goods_transfee);
		}
		maps.put("goods_status",0);
		
		List<Map> goods_property = Lists.newArrayList();
		if (!CommUtil.null2String(brand_ids).equals("")) {
			if (brand_ids.indexOf(",") < 0) {
				brand_ids = brand_ids + ",";
			}
			String[] brand_id_list = CommUtil.null2String(brand_ids).split(",");
			if (brand_id_list.length == 1) {
				String brand_id = brand_id_list[0];
				
				maps.put("goods_brand_id",CommUtil.null2Long(brand_id));
				
				Map<String, Object> map = Maps.newHashMap();
				GoodsBrand brand = this.brandService.selectByPrimaryKey(CommUtil
						.null2Long(brand_id));
				if (brand != null) {
					map.put("name", "品牌");
					map.put("value", brand.getName());
					map.put("type", "brand");
					map.put("id", brand.getId());
					goods_property.add(map);
				}
			} else {
				List<Long> goods_brand_ids = Lists.newArrayList();
				for (String brand_id : brand_id_list) {
					
					if (i == 0) {
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService
								.selectByPrimaryKey(CommUtil.null2Long(brand_id));
						map.put("name", "品牌");
						map.put("value", brand.getName());
						map.put("type", "brand");
						map.put("id", brand.getId());
						goods_property.add(map);
					} else if (i == brand_id_list.length - 1) {
						
						Map<String, Object> map = Maps.newHashMap();
						GoodsBrand brand = this.brandService
								.selectByPrimaryKey(CommUtil.null2Long(brand_id));
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
				maps.put("goods_brand_ids",goods_brand_ids);
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
					
					Map<String,Object> map = Maps.newHashMap();
					map.put("name", gsp.getSpec().getName());
					map.put("value", gsp.getValue());
					map.put("type", "gs");
					map.put("id", gsp.getId());
					goods_property.add(map);
				} else {
					
					for (i = 0; i < gsp_list.size(); i++) {
						GoodsSpecProperty gsp = (GoodsSpecProperty) gsp_list.get(i);
						goodsSpecPropertyIds.add(gsp.getId());
						
						if (i == 0) {
							
							Map<String, Object> map = Maps.newHashMap();
							map.put("name", gsp.getSpec().getName());
							map.put("value", gsp.getValue());
							map.put("type", "gs");
							map.put("id", gsp.getId());
							goods_property.add(map);
						} else if (i == gsp_list.size() - 1) {
							
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
		String[] list1;
		String property_info;
		if (gc != null) {
			if (!CommUtil.null2String(properties).equals("")) {
				String[] properties_list = properties.substring(1).split("\\|");
				List<String> gtp_names = Lists.newArrayList();
				
				for (i = 0; i < properties_list.length; i++) {
					property_info = CommUtil.null2String(properties_list[i]);
					String[] property_info_list = property_info.split(",");
					GoodsTypeProperty gtp = this.goodsTypePropertyService
							.selectByPrimaryKey(CommUtil.null2Long(property_info_list[0]));
					
					
					gtp_names.add(gtp.getName().trim());
					gtp_names.add(property_info_list[1].trim());
					
					Map<String, Object> map = Maps.newHashMap();
					map.put("name", ((GoodsTypeProperty) gtp).getName());
					map.put("value", property_info_list[1]);
					map.put("type", "properties");
					map.put("id", ((GoodsTypeProperty) gtp).getId());
					goods_property.add(map);
				}
				maps.put("gtp_names", gtp_names);
				
				mv.addObject("properties", properties);

				List<GoodsTypeProperty> filter_properties = Lists
						.newArrayList();
				List<String> hc_property_list = Lists.newArrayList();
				if (gc.getGoodsType() != null) {
					for (GoodsTypeProperty gtp : gc.getGoodsType()
							.getProperties()) {

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
								for (String s : defalut_list) {
									value = s + "," + value;
								}
								gtp1.setValue(value.substring(0,
										value.length() - 1));
								flag = false;
								break;
							}
						}
						if (flag) {
							if (!CommUtil.null2String(gtp.getHc_value())
									.equals("")) {
								list1 = gtp.getHc_value().split("#");
								for (i = 0; i < properties_list.length; i++) {
									property_info = CommUtil
											.null2String(properties_list[i]);
									String[] property_info_list = property_info
											.split(",");
									if (property_info_list[1].equals(list1[0])) {
										hc_property_list.add(list1[1]);
									}
								}
							}
							filter_properties.add(gtp);
						} else {
							filter_properties.add(gtp1);
						}
					}
					mv.addObject("filter_properties", filter_properties);
				}
			} else {
				mv.addObject("filter_properties",
						gc.getGoodsType() != null ? gc.getGoodsType()
								.getProperties() : "");
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
			maps.put("group_buy", Integer.valueOf(2));
			
			mv.addObject("group_buy", group_buy);
		}
		if (!CommUtil.null2String(enough_reduce).equals("")) {
			maps.put("enough_reduce", Integer.valueOf(1));
			
			mv.addObject("enough_reduce", enough_reduce);
		}
		if (!CommUtil.null2String(order_enough_give_status).equals("")) {
			maps.put("order_enough_give_status", Integer.valueOf(1));
			
			mv.addObject("order_enough_give_status", order_enough_give_status);
		}
		if (!CommUtil.null2String(activity_status).equals("")) {
			maps.put("activity_status", Integer.valueOf(2));
			
			mv.addObject("activity_status", activity_status);
		}
		if (!CommUtil.null2String(combin_status).equals("")) {
			maps.put("combin_status", Integer.valueOf(1));
			
			mv.addObject("combin_status", combin_status);
		}
		if (!CommUtil.null2String(advance_sale_type).equals("")) {
			maps.put("advance_sale_type", Integer.valueOf(1));
			
			mv.addObject("advance_sale_type", advance_sale_type);
		}
		if (!CommUtil.null2String(f_sale_type).equals("")) {
			maps.put("f_sale_type", Integer.valueOf(1));
			
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
		mv.addObject("totalPage", Integer.valueOf(pList.getPages()));
		
		mv.addObject("objs", pList.getResult());
		
		mv.addObject("gc", gc);
		mv.addObject("orderBy", orderBy);
		mv.addObject("goods_property", goods_property);
		mv.addObject("allCount", Integer.valueOf(pList.getRowCount()));
		if (this.configService.getSysConfig().getZtc_status()) {
			
			List left_ztc_goods = null;
			Map<String,Object> ztc_map = Maps.newHashMap();
			ztc_map.put("ztc_status", Integer.valueOf(3));
			ztc_map.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map.put("ztc_gold", Integer.valueOf(0));
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				ztc_map.put("orderBy", "ztc_dredge_price");
				ztc_map.put("orderType", "desc");
				
				List all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
				
				left_ztc_goods = this.goodsService.queryPageList(ztc_map,4, all_left_ztc_goods.size());
				
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map.put("gc_ids", ids);
				ztc_map.put("orderBy", "ztc_dredge_price");
				ztc_map.put("orderType", "desc");
				
				List all_left_ztc_goods = this.goodsService.queryPageList(ztc_map);
				
				left_ztc_goods = this.goodsService.queryPageList(ztc_map, 4, all_left_ztc_goods.size());
				
				left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			}
			mv.addObject("left_ztc_goods", left_ztc_goods);

			List top_ztc_goods = null;
			Map ztc_map2 = Maps.newHashMap();
			ztc_map2.put("ztc_status", Integer.valueOf(3));
			ztc_map2.put("ztc_begin_time_less_than_equal", new Date());
			ztc_map2.put("ztc_gold", Integer.valueOf(0));
			if (this.configService.getSysConfig().getZtc_goods_view() == 0) {
				ztc_map2.put("orderBy", "ztc_dredge_price");
				ztc_map2.put("orderType", "desc");
				top_ztc_goods = this.goodsService.queryPageList(ztc_map2,0,4);
				
			}
			if (this.configService.getSysConfig().getZtc_goods_view() == 1) {
				ztc_map2.put("gc_ids", ids);
				ztc_map2.put("orderBy", "ztc_dredge_price");
				ztc_map2.put("orderType", "desc");
				
				top_ztc_goods = this.goodsService.queryPageList(ztc_map2,0,4);
				
			}
			mv.addObject("top_ztc_goods", top_ztc_goods);
		} else {
			Map<String,Object> params = Maps.newHashMap();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List top_ztc_goods = this.goodsService.queryPageList(params,0,4);
			
			mv.addObject("top_ztc_goods", top_ztc_goods);
			params.clear();
			params.put("store_recommend", Boolean.valueOf(true));
			params.put("goods_status", Integer.valueOf(0));
			params.put("orderBy", "goods_salenum");
			params.put("orderType", "desc");
			
			List all_goods = this.goodsService.queryPageList(params);
			
			List left_ztc_goods = this.goodsService.queryPageList(params, 4, all_goods.size());
			
			left_ztc_goods = this.goodsViewTools.randomZtcGoods2(left_ztc_goods, 10);
			mv.addObject("left_ztc_goods", left_ztc_goods);
		}
		if ((detail_property_status != null)
				&& (!detail_property_status.equals(""))) {
			mv.addObject("detail_property_status", detail_property_status);
			String[] temp_str = detail_property_status.split(",");
			Object pro_map = Maps.newHashMap();
			List pro_list = Lists.newArrayList();
			for (String property_status : temp_str) {
				if ((property_status != null) && (!property_status.equals(""))) {
					String[] mark = property_status.split("_");
					((Map) pro_map).put(mark[0], mark[1]);
					pro_list.add(mark[0]);
				}
			}
			mv.addObject("pro_list", pro_list);
			mv.addObject("pro_map", pro_map);
		}
		mv.addObject("all_property_status", all_property_status);
		mv.addObject("userTools", this.userTools);

		Object goods_compare_list = (List) request.getSession(false)
				.getAttribute("goods_compare_cart");
		if (goods_compare_list == null) {
			goods_compare_list = Lists.newArrayList();
		}
		mv.addObject("goods_compare_list", goods_compare_list);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		mv.addObject("gc_id", gc_id);
		mv.addObject("big_map", big_map);
		return mv;
	}
	
	private Set<Long> genericIds(Long id) {
		Set<Long> ids = new HashSet();
		if (id != null) {
			ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", id);
			List<GoodsClass> id_list = this.goodsClassService.queryPageList(params);
			
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i).getId());
				Set cids = genericIds(cid);
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
			GoodsSpecProperty gsp = this.goodsSpecPropertyService
					.selectByPrimaryKey(CommUtil.null2Long(gs_info_list[0]));
			boolean create = true;
			for (List<GoodsSpecProperty> gsp_list : list) {
				for (GoodsSpecProperty gsp_temp : gsp_list) {
					if (gsp_temp.getSpec().getId()
							.equals(gsp.getSpec().getId())) {
						gsp_list.add(gsp);
						create = false;
						break;
					}
				}
			}
			if (create) {
				List gsps = Lists.newArrayList();
				gsps.add(gsp);
				list.add(gsps);
			}
		}
		return list;
	}
}
