package com.redpigmall.manage.admin.action.self.goods;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsFormat;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.Inventory;
import com.redpigmall.domain.InventoryOperation;
import com.redpigmall.domain.StoreHouse;
import com.redpigmall.domain.Transport;

/**
 * 
 * <p>
 * Title: RedPigGoodsSelfManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营商品管理控制器，平台可发布商品并进行管理
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
 * @date 2014年4月25日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigSelfGoodsManage2Action extends BaseAction{
	
	/**
	 * 产品规格显示
	 * @param request
	 * @param response
	 * @param goods_spec_ids
	 * @param supplement
	 * @return
	 */
	@SecurityMapping(title = "产品规格显示", value = "/goods_inventory*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_inventory" })
	public ModelAndView goods_inventory(HttpServletRequest request,
			HttpServletResponse response, String goods_spec_ids,
			String supplement) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/goods_inventory.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String[] spec_ids = goods_spec_ids.split(",");
		List<GoodsSpecProperty> gsps = Lists.newArrayList();
		
		for (String spec_id : spec_ids) {
			if (!spec_id.equals("")) {
				GoodsSpecProperty gsp = this.specPropertyService.selectByPrimaryKey(Long.valueOf(Long.parseLong(spec_id)));
				gsps.add(gsp);
			}
		}
		
		Set<GoodsSpecification> specs = new HashSet();
		for (GoodsSpecProperty gspr : gsps) {
			specs.add(gspr.getSpec());
		}
		
		for (GoodsSpecification spec : specs) {
			spec.getProperties().clear();
			for (GoodsSpecProperty gspro : gsps) {
				if (gspro.getSpec().getId().equals(spec.getId())) {
					spec.getProperties().add(gspro);
				}
			}
		}
		
		GoodsSpecification[] spec_list = (GoodsSpecification[]) specs.toArray(new GoodsSpecification[specs.size()]);
		Arrays.sort(spec_list, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				GoodsSpecification a = (GoodsSpecification) obj1;
				GoodsSpecification b = (GoodsSpecification) obj2;
				if (a.getSequence() == b.getSequence()) {
					return 0;
				}
				return a.getSequence() > b.getSequence() ? 1 : -1;
			}
		});
		
		Object gsp_list = generic_spec_property(specs);
		mv.addObject("specs", Arrays.asList(spec_list));
		mv.addObject("gsps", gsp_list);
		return mv;
	}
	
	public static GoodsSpecProperty[][] list2group(List<List<GoodsSpecProperty>> list) {
		GoodsSpecProperty[][] gps = new GoodsSpecProperty[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			gps[i] = ((GoodsSpecProperty[]) ((List) list.get(i))
					.toArray(new GoodsSpecProperty[((List) list.get(i)).size()]));
		}
		return gps;
	}

	private List<List<GoodsSpecProperty>> generic_spec_property(
			Set<GoodsSpecification> specs) {
		List<List<GoodsSpecProperty>> result_list = Lists.newArrayList();
		List<List<GoodsSpecProperty>> list = Lists.newArrayList();
		int max = 1;
		for (GoodsSpecification spec : specs) {
			list.add(spec.getProperties());
		}
		GoodsSpecProperty[][] gsps = list2group(list);
		for (int i = 0; i < gsps.length; i++) {
			max *= gsps[i].length;
		}
		for (int i = 0; i < max; i++) {
			List<GoodsSpecProperty> temp_list = Lists.newArrayList();
			int temp = 1;
			for (int j = 0; j < gsps.length; j++) {
				temp *= gsps[j].length;
				temp_list.add(j, gsps[j][(i / (max / temp) % gsps[j].length)]);
			}
			GoodsSpecProperty[] temp_gsps = (GoodsSpecProperty[]) temp_list
					.toArray(new GoodsSpecProperty[temp_list.size()]);
			Arrays.sort(temp_gsps, new Comparator() {
				public int compare(Object obj1, Object obj2) {
					GoodsSpecProperty a = (GoodsSpecProperty) obj1;
					GoodsSpecProperty b = (GoodsSpecProperty) obj2;
					if (a.getSpec().getSequence() == b.getSpec().getSequence()) {
						return 0;
					}
					return a.getSpec().getSequence() > b.getSpec()
							.getSequence() ? 1 : -1;
				}
			});
			result_list.add(Arrays.asList(temp_gsps));
		}
		return result_list;
	}
	
	/**
	 * 运费模板显示
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ajax
	 * @return
	 */
	@SecurityMapping(title = "运费模板显示", value = "/goods_transport*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/goods_transport" })
	public ModelAndView goods_transport(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ajax) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/goods_transport.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Boolean(ajax)) {
			mv = new RedPigJModelAndView("admin/blue/goods_transport_list.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,1, orderBy, orderType);
        maps.put("trans_user", 0);
        
		IPageList pList = this.transportService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/goods_transport", "",
				params, pList, mv);
		mv.addObject("transportTools", this.transportTools);
		return mv;
	}
	
	/**
	 * 商品发布第三步
	 * @param request
	 * @param response
	 * @param id
	 * @param goods_class_id
	 * @param image_ids
	 * @param goods_main_img_id
	 * @param goods_brand_id
	 * @param goods_spec_ids
	 * @param goods_properties
	 * @param intentory_details
	 * @param goods_session
	 * @param transport_type
	 * @param transport_id
	 * @param goods_status
	 * @param f_code_count
	 * @param f_code_profix
	 * @param advance_date
	 * @param goods_top_format_id
	 * @param goods_bottom_format_id
	 * @param delivery_area_id
	 * @param goods_spec_id_value
	 * @param add_type
	 * @param goodstag_ids
	 * @param color_info
	 * @param update_inventory
	 * @param storehouse_id
	 * @param inventory_type
	 * @param serve_ids 商家承诺
	 * @param earnest
	 * @param rest_start_date
	 * @param rest_end_date
	 * @param edit_advance_info
	 * @param special_goods_type
	 * @return
	 */
	@SecurityMapping(title = "商品发布第三步", value = "/add_goods_finish*", rtype = "admin", rname = "商品发布", rcode = "goods_self_add", rgroup = "自营")
	@RequestMapping({ "/add_goods_finish" })
	public ModelAndView add_goods_finish(HttpServletRequest request,
			HttpServletResponse response, String id, String goods_class_id,
			String image_ids, String goods_main_img_id, String goods_brand_id,
			String goods_spec_ids, String goods_properties,
			String intentory_details, String goods_session,
			String transport_type, String transport_id, String goods_status,
			String f_code_count, String f_code_profix, String advance_date,
			String goods_top_format_id, String goods_bottom_format_id,
			String delivery_area_id, String goods_spec_id_value,
			String add_type, String goodstag_ids, String color_info,
			String update_inventory, String storehouse_id,
			String inventory_type, String serve_ids, String earnest,
			String rest_start_date, String rest_end_date,
			String edit_advance_info, String special_goods_type) {
		ModelAndView mv = null;
		String goods_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("goods_session"));
		if (goods_session1.equals("")) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "禁止重复提交表单");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/add_goods_first");
		} else {
			if (CommUtil.null2String(id).equals("")) {
				mv = new RedPigJModelAndView("admin/blue/success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "商品发布成功");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/add_goods_first");
				if ((add_type != null)
						&& (add_type.equals("add_goods_by_goods"))) {
					mv.addObject("list_url", CommUtil.getURL(request)
							+ "/goods_self_list");
				}
			} else {
				mv = new RedPigJModelAndView("admin/blue/success.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "商品编辑成功");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/goods_self_list");
			}
			WebForm wf = new WebForm();
			
			Goods goods = null;
			String obj_status = null;
			Map temp_params = Maps.newHashMap();
			Set<Long> temp_ids = new HashSet();
			if (id.equals("")) {
				goods = (Goods) WebForm.toPo(request, Goods.class);
				goods.setAddTime(new Date());
				goods.setUser_admin(SecurityUserHolder.getCurrentUser());
				goods.setGoods_type(0);
			} else {
				Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				System.out.println("obj_goods_main_id="+obj.getGoods_main_photo().getId());
				//编辑时候将所有关联关系的数据都清空
				this.goodsService.batchDeleteGoodsPhotos(obj.getId(), obj.getGoods_photos());
				obj.getGoods_photos().clear();
				this.goodsService.batchDeleteGoodsSpecProperty(obj.getId(), obj.getGoods_specs());
				obj.getGoods_specs().clear();
				this.goodsService.batchDeleteUserGoodsClass(obj.getId(), obj.getGoods_ugcs());
				obj.getGoods_ugcs().clear();
				
				BigDecimal old_price = obj.getGoods_current_price();
				obj_status = CommUtil.null2String(Integer.valueOf(obj.getGoods_status()));
				goods = (Goods) WebForm.toPo(request, obj);
				
				System.out.println("goods_main_id="+goods.getGoods_main_photo().getId());
				
				goods.setPrice_history(old_price);
			}
			//商家承诺
			if ((serve_ids != null) && (!"".equals(serve_ids))) {
				List list = Lists.newArrayList();
				String[] arr = serve_ids.split(",");
				for (int i = 0; i < arr.length; i++) {
					String s_id = arr[i];
					list.add(i, s_id);
				}
				goods.setMerchantService_info(JSON.toJSONString(list));
			}
			if (goods.getGroup_buy() != 2) {
				goods.setGoods_current_price(goods.getStore_price());
			}
			goods.setGoods_name(Jsoup.clean(goods.getGoods_name(),Whitelist.none()));
			
			goods.setGoods_details(goods.getGoods_details());
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(Long.valueOf(Long.parseLong(goods_class_id)));
			goods.setGc(gc);
			Accessory main_img = null;
			if ((goods_main_img_id != null) && (!goods_main_img_id.equals(""))) {
				main_img = this.accessoryService.selectByPrimaryKey(Long.valueOf(Long.parseLong(goods_main_img_id)));
			}
			
			goods.setGoods_main_photo(main_img);
			String[] img_ids = image_ids.split(",");
			goods.getGoods_photos().clear();
			
			temp_ids.clear();
			
			for (String img_id : img_ids) {
				if (!img_id.equals("")) {
					temp_ids.add(CommUtil.null2Long(img_id));
				}
			}
			if (!temp_ids.isEmpty()) {
				temp_params.clear();
				temp_params.put("ids", temp_ids);
				List<Accessory> temp_list = this.accessoryService.queryPageList(temp_params);
				goods.getGoods_photos().addAll(temp_list);
			}
			if ((goods_brand_id != null) && (!goods_brand_id.equals(""))) {
				GoodsBrand goods_brand = this.goodsBrandService.selectByPrimaryKey(Long.valueOf(Long.parseLong(goods_brand_id)));
				goods.setGoods_brand(goods_brand);
			}
			goods.getGoods_specs().clear();
			
			String[] spec_ids = goods_spec_ids.split(",");
			temp_ids.clear();
			for (String spec_id : spec_ids) {

				if (!spec_id.equals("")) {
					temp_ids.add(CommUtil.null2Long(spec_id));
				}
			}
			if (!temp_ids.isEmpty()) {
				temp_params.clear();
				temp_params.put("ids", temp_ids);
				
				List<GoodsSpecProperty> gsps = Lists.newArrayList();
				for (Long gspId : temp_ids) {
					GoodsSpecProperty gsp = new GoodsSpecProperty();
					gsp.setId(gspId);
					
					gsps.add(gsp);
				}
				
				this.goodsService.batchDeleteGoodsSpecProperty(goods.getId(), gsps);
				
				goods.getGoods_specs().addAll(gsps);
				
			}
			
			if (!goods_spec_id_value.isEmpty()) {
				String[] id_values = goods_spec_id_value.split(",");
				List<Map> list = Lists.newArrayList();
				for (String id_value : id_values) {
					String[] single_id_value = id_value.split(":");
					Map map = Maps.newHashMap();
					map.put("id", single_id_value[0]);
					map.put("name", single_id_value[1]);
					list.add(map);
				}
				goods.setGoods_specs_info(JSON.toJSONString(list));
			}
			
			List maps = Lists.newArrayList();
			String[] properties = goods_properties.split(";");
			for (String property : properties) {

				if (!property.equals("")) {
					String[] list = property.split(",");

					Map map = Maps.newHashMap();
					map.put("id", list[0]);
					map.put("val", list[1]);
					map.put("name",this.goodsTypePropertyService.selectByPrimaryKey(Long.valueOf(Long.parseLong(list[0]))).getName());
					maps.add(map);
				}
			}
			
			goods.setGoods_property(JSON.toJSONString(maps));
			String[] inventory_list = intentory_details.split(";");
			
			if (inventory_type.equals("spec")) {
				((List) maps).clear();
				for (String inventory : inventory_list) {
					if (!inventory.equals("")) {
						String[] list = inventory.split(",");
						Map map = Maps.newHashMap();
						map.put("id", list[0]);
						map.put("count", list[1]);
						map.put("supp", list[2]);
						map.put("price", list[3]);
						map.put("code", list[4]);
						maps.add(map);
					}
				}
				goods.setGoods_inventory_detail(JSON.toJSONString(maps));
			} else {
				goods.setGoods_inventory_detail(null);
			}
			String color_id;
			if ((color_info != null) && (!"".equals(color_info))) {
				Map color_map = Maps.newHashMap();
				List list1 = Lists.newArrayList();
				String[] colors = color_info.split("\\|");
				for (String color : colors) {
					if (!color.equals("")) {
						String[] all_img_ids = color.split("#");
						color_id = all_img_ids[1];
						String other_img_ids = all_img_ids[0];
						Map map = Maps.newHashMap();
						map.put("color_id", color_id);
						map.put("img_ids", other_img_ids);
						list1.add(map);
					}
				}
				color_map.put("data", list1);
				goods.setGoods_color_json(JSON.toJSONString(color_map));
			} else {
				goods.setGoods_color_json(null);
			}
			if (CommUtil.null2Int(transport_type) == 0) {
				Transport trans = this.transportService.selectByPrimaryKey(CommUtil.null2Long(transport_id));
				goods.setTransport(trans);
			}
			if (CommUtil.null2Int(transport_type) == 1) {
				goods.setTransport(null);
			}
			if (CommUtil.null2String(special_goods_type).equals("1")) {
				goods.setF_sale_type(1);
				goods.setGoods_limit(0);
				goods.setAdvance_sale_type(0);
				if (CommUtil.null2Int(f_code_count) > 0) {
					Set<String> set = new HashSet();
					while (set.size() != CommUtil.null2Int(f_code_count)) {
						set.add((f_code_profix + CommUtil.randomString(12)).toUpperCase());
					}
					List f_code_maps = Lists.newArrayList();
					if (!CommUtil.null2String(goods.getGoods_f_code()).equals("")) {
						f_code_maps = JSON.parseArray(goods.getGoods_f_code(),Map.class);
					}
					for (String code : set) {
						Map f_code_map = Maps.newHashMap();
						f_code_map.put("code", code);
						f_code_map.put("status", Integer.valueOf(0));
						f_code_maps.add(f_code_map);
					}
					if (f_code_maps.size() > 0) {
						goods.setGoods_f_code(JSON.toJSONString(f_code_maps));
					}
				}
			} else if (CommUtil.null2String(special_goods_type).equals("2")) {
				if (edit_advance_info.equals("add")) {
					goods.setAdvance_sale_type(1);
					goods.setF_sale_type(0);
					goods.setGoods_limit(0);
					goods.setGoods_cod(-1);
					goods.setAdvance_date(CommUtil.formatDate(advance_date));
					List list1 = Lists.newArrayList();
					Map map1 = Maps.newHashMap();
					map1.put("advance_price", goods.getStore_price());
					map1.put("earnest", new BigDecimal(earnest));
					map1.put("final_earnest", goods.getStore_price().subtract(new BigDecimal(earnest)));
					map1.put("rest_start_date", rest_start_date);
					map1.put("rest_end_date", rest_end_date);
					map1.put("advance_date", advance_date);
					list1.add(map1);
					String temp = JSON.toJSONString(list1);
					goods.setAdvance_sale_info(temp);
				}
			} else if (CommUtil.null2String(special_goods_type).equals("3")) {
				goods.setGoods_limit(1);
				goods.setF_sale_type(0);
				goods.setAdvance_sale_type(0);
			} else {
				goods.setGoods_limit(0);
				goods.setF_sale_type(0);
				goods.setAdvance_sale_type(0);
			}
			goods.setGoods_top_format_id(CommUtil.null2Long(goods_top_format_id));
			GoodsFormat gf = this.goodsFormatService.selectByPrimaryKey(CommUtil.null2Long(goods_top_format_id));
			if (gf != null) {
				goods.setGoods_top_format_content(gf.getGf_content());
			} else {
				goods.setGoods_top_format_content(null);
			}
			goods.setGoods_bottom_format_id(CommUtil.null2Long(goods_bottom_format_id));
			gf = this.goodsFormatService.selectByPrimaryKey(CommUtil.null2Long(goods_bottom_format_id));
			if (gf != null) {
				goods.setGoods_bottom_format_content(gf.getGf_content());
			} else {
				goods.setGoods_bottom_format_content(null);
			}
			goods.setDelivery_area_id(CommUtil.null2Long(delivery_area_id));
			Area de_area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(delivery_area_id));
			if (de_area != null) {
				String delivery_area = de_area.getParent().getParent()
						.getAreaName()
						+ de_area.getParent().getAreaName()
						+ de_area.getAreaName();
				goods.setDelivery_area(delivery_area);
			}
			if (id.equals("")) {
				
				this.goodsService.saveEntity(goods);
				
				String qr_img_path = this.goodsTools.createGoodsQR(request,goods);
				goods.setQr_img_path(qr_img_path);
				
				this.goodsService.update(goods);
				
				this.goodsService.saveGoodsPhotos(goods.getId(), goods.getGoods_photos());
				this.goodsService.saveGoodsSpecProperty2(goods.getId(), goods.getGoods_specs());
				
				this.goodsService.saveGoodsUserGoodsClass(goods.getId(),goods.getGoods_ugcs());
				
				
				if ("0".equals(goods_status)) {
					this.goodsTools.addGoodsLucene(goods);
				}
			} else {
				String goods_lucene_path = ClusterSyncTools.getClusterRoot()
						+ File.separator + "luence" + File.separator + "goods";
				
				if (("0".equals(obj_status)) && ("0".equals(goods_status))) {
					this.goodsTools.updateGoodsLucene(goods);
				}
				if (("0".equals(obj_status))
						&& (("1".equals(goods_status)) || ("2"
								.equals(goods_status)))) {
					this.goodsTools.deleteGoodsLucene(goods);
				}
				if ((("1".equals(obj_status)) || ("2".equals(obj_status)))
						&& ("0".equals(goods_status))) {
					this.goodsTools.addGoodsLucene(goods);
				}
				
				String qr_img_path = this.goodsTools.createGoodsQR(request,goods);
				goods.setQr_img_path(qr_img_path);
				//编辑时候,如果有多对多、一对多的关联数据,先需要删除所有的关联关系,然后在新增
				
				this.goodsService.saveGoodsPhotos(goods.getId(), goods.getGoods_photos());
				
				this.goodsService.saveGoodsSpecProperty2(goods.getId(), goods.getGoods_specs());
				
				this.goodsService.saveGoodsUserGoodsClass(goods.getId(),goods.getGoods_ugcs());
				
				this.goodsService.updateById(goods);
				
				System.out.println("goods_main_id="+goods.getGoods_main_photo().getId());
				
			}
			mv.addObject("obj", goods);
			if ((id != null) && (!id.equals(""))) {
				this.InventoryTools.async_updateInventoryByUpdateGoods(
						update_inventory, storehouse_id, id, inventory_type,
						intentory_details);
			} else if ((update_inventory != null)
					&& (!update_inventory.equals(""))
					&& (storehouse_id != null) && (!storehouse_id.equals(""))) {
				StoreHouse storeHouse = this.storeHouseService
						.selectByPrimaryKey(CommUtil.null2Long(storehouse_id));
				Map params = Maps.newHashMap();
				params.put("goods_id", goods.getId());
				params.put("storehouse_id", CommUtil.null2Long(storehouse_id));
				
				List<Inventory> list1 = this.inventoryService.queryPageList(params);
				
				Inventory inventory;
				InventoryOperation inventoryOperation;
				if (inventory_type.equals("all")) {
					goods.setGoods_inventory_detail(null);
					List<Map> inventory_detail = Lists.newArrayList();
					int count = goods.getGoods_inventory();
					if (CommUtil.null2Int(Integer.valueOf(count)) > 0) {
						if (list1.size() == 0) {
							inventory = new Inventory();
							inventory.setAddTime(new Date());
							inventory.setStorehouse_id(CommUtil.null2Long(storehouse_id));
							inventory.setStorehouse_name(storeHouse.getSh_name());
							inventory.setGoods_id(goods.getId());
							inventory.setGoods_name(goods.getGoods_name());
							inventory.setSpec_id("");
							inventory.setSpec_name("无");
							inventory.setPrice(goods.getGoods_current_price());
							inventory.setCount(CommUtil.null2Int(Integer.valueOf(count)));
							this.inventoryService.saveEntity(inventory);
							inventoryOperation = new InventoryOperation();
							inventoryOperation.setAddTime(new Date());
							inventoryOperation.setCount(CommUtil.null2Int(Integer.valueOf(count)));
							inventoryOperation.setGoods_name(inventory.getGoods_name());
							inventoryOperation.setStorehouse_name(inventory.getStorehouse_name());
							inventoryOperation.setInventory_id(inventory.getId());
							inventoryOperation.setSpec_name(inventory.getSpec_name());
							inventoryOperation.setType(1);
							inventoryOperation.setOperation_info("手动更新库存");
							this.inventoryOperationService.saveEntity(inventoryOperation);
						}
					}
				} else if ((intentory_details != null)
						&& (!intentory_details.equals(""))) {
					for (String inventory_str : inventory_list) {
						if (!inventory_str.equals("")) {
							String[] arr = inventory_str.split(",");
							Map map1 = Maps.newHashMap();
							map1.put("id", arr[0]);
							map1.put("count", arr[1]);
							map1.put("price", arr[2]);
							int count = CommUtil.null2Int(arr[1]);
							String spec = arr[0];
							String[] sp = spec.split("_");
							Arrays.sort(sp);
							spec = "";
							for (String s : sp) {
								spec = spec + s + ",";
							}
							if (spec.length() > 0) {
								spec = spec.substring(0, spec.length() - 1);
							}
							params.put("spec_id", spec);
							params.put("storehouse_id",CommUtil.null2Long(storehouse_id));
							
							list1 = this.inventoryService.queryPageList(params);
							
							if (list1.size() == 0) {
								inventory = new Inventory();
								inventory.setAddTime(new Date());
								inventory.setStorehouse_id(CommUtil.null2Long(storehouse_id));
								inventory.setStorehouse_name(storeHouse.getSh_name());
								inventory.setGoods_id(goods.getId());
								inventory.setGoods_name(goods.getGoods_name());
								spec = map1.get("id").toString();
								sp = spec.split("_");
								Arrays.sort(sp);
								spec = "";
								String specname = "";
								for (String s : sp) {
									spec = spec + s + ",";
									specname = specname
											+ " "
											+ this.specPropertyService.selectByPrimaryKey(CommUtil.null2Long(s)).getValue();
								}
								if (spec.length() > 0) {
									spec = spec.substring(0, spec.length() - 1);
								}
								inventory.setSpec_id(spec);
								inventory.setSpec_name(specname);
								inventory.setPrice(goods.getGoods_current_price());
								inventory.setCount(CommUtil.null2Int(Integer.valueOf(count)));
								this.inventoryService.saveEntity(inventory);
								inventoryOperation = new InventoryOperation();
								inventoryOperation.setAddTime(new Date());
								inventoryOperation.setCount(CommUtil.null2Int(Integer.valueOf(count)));
								inventoryOperation.setGoods_name(inventory.getGoods_name());
								inventoryOperation.setStorehouse_name(inventory.getStorehouse_name());
								inventoryOperation.setInventory_id(inventory.getId());
								inventoryOperation.setSpec_name(inventory.getSpec_name());
								inventoryOperation.setType(1);
								inventoryOperation.setOperation_info("手动更新库存");
								this.inventoryOperationService.saveEntity(inventoryOperation);
							}
						}
					}
				}
			}
			request.getSession(false).removeAttribute("goods_session");
		}
		return mv;
	}
	
}
