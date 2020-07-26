package com.redpigmall.manage.admin.action.self.inventory;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Inventory;
import com.redpigmall.domain.InventoryOperation;
import com.redpigmall.domain.StoreHouse;

/**
 * 
 * <p>
 * Title: RedPigInventoryManageAction.java
 * </p>
 * 
 * <p>
 * Description:库存管理
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
 * @version redp
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigInventoryManageAction extends BaseAction {
	@SecurityMapping(title = "库存管理", value = "/inventory_list*", rtype = "admin", rname = "库存管理", rcode = "admin_store_inventory", rgroup = "自营")
	@RequestMapping({ "/inventory_list" })
	public ModelAndView list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String goods_name, String storehouse_name, String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/inventory_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			maps.put("goods_name_like", goods_name);
			mv.addObject("goods_name", goods_name);
		}
		if (!CommUtil.null2String(storehouse_name).equals("")) {
			
			maps.put("storehouse_name_like", storehouse_name);
			mv.addObject("storehouse_name", storehouse_name);
		}
		
		maps.put("orderBy", "addTime");
		maps.put("orderType", "desc");
		
		IPageList pList = this.inventoryService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/inventory_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 库存管理
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param goods_name
	 * @param orderType
	 * @param id
	 * @param storehouse_name
	 * @return
	 */
	@SecurityMapping(title = "库存管理", value = "/inventory_history*", rtype = "admin", rname = "库存管理", rcode = "admin_store_inventory", rgroup = "自营")
	@RequestMapping({ "/inventory_history" })
	public ModelAndView inventory_history(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String goods_name, String orderType, String id,
			String storehouse_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/inventory_history.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("goods_name_like", goods_name);
			
			mv.addObject("goods_name", goods_name);
		}
		if (!CommUtil.null2String(id).equals("")) {
			
			maps.put("inventory_id", CommUtil.null2Long(id));
			mv.addObject("id", id);
		}
		if (!CommUtil.null2String(storehouse_name).equals("")) {
			
			maps.put("storehouse_name_like", storehouse_name);
			mv.addObject("storehouse_name", storehouse_name);
		}
		
		maps.put("orderBy", "addTime");
		maps.put("orderType", "desc");
		IPageList pList = this.inventoryOperationService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/inventory_history.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 库存列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "库存列表", value = "/inventory_add*", rtype = "admin", rname = "库存管理", rcode = "admin_store_inventory", rgroup = "自营")
	@RequestMapping({ "/inventory_add" })
	public ModelAndView add(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/inventory_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		maps.put("pageSize", 5);
		
		maps.put("goods_type", 0);
		
		if ((goods_name != null) && (!goods_name.equals(""))) {
			
			maps.put("goods_name_like", goods_name);
			mv.addObject("goods_name", goods_name);
		}
		
		IPageList pList = this.goodsService.list(maps);
		String goods_url = CommUtil.getURL(request) + "/inventory_items";
		mv.addObject("goods", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(goods_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		return mv;
	}
	
	/**
	 * 库存管理
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "库存管理", value = "/inventory_edit*", rtype = "admin", rname = "库存管理", rcode = "admin_store_inventory", rgroup = "自营")
	@RequestMapping({ "/inventory_edit" })
	public ModelAndView edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/inventory_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			Inventory inventory = this.inventoryService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			mv.addObject("obj", inventory);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 库存管理
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param storehouse_id
	 * @param type
	 */
	@SecurityMapping(title = "库存管理", value = "/inventory_save*", rtype = "admin", rname = "库存管理", rcode = "admin_store_inventory", rgroup = "自营")
	@RequestMapping({ "/inventory_save" })
	public void saveEntity(HttpServletRequest request, HttpServletResponse response,
			String goods_id, String storehouse_id, String type) {
		boolean flag = false;
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		StoreHouse storeHouse = this.storeHouseService.selectByPrimaryKey(CommUtil
				.null2Long(storehouse_id));
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_id", CommUtil.null2Long(goods_id));
		params.put("storehouse_id", CommUtil.null2Long(storehouse_id));
		
		List<Inventory> list = this.inventoryService.queryPageList(params);
		InventoryOperation inventoryOperation;
		if (goods.getInventory_type().equals("all")) {
			String count = request.getParameter("spec_").toString();
			if (CommUtil.null2Int(count) > 0) {
				Inventory inventory;
				if (list.size() > 0) {
					inventory = (Inventory) list.get(0);
					if (type.equals("in")) {
						inventory.setCount(inventory.getCount()
								+ CommUtil.null2Int(count));
					} else {
						inventory.setCount(inventory.getCount()
								- CommUtil.null2Int(count));
					}
					
					this.inventoryService.updateById(inventory);
					flag = true;
				} else {
					inventory = new Inventory();
					inventory.setAddTime(new Date());
					inventory.setStorehouse_id(CommUtil.null2Long(storehouse_id));
					inventory.setStorehouse_name(storeHouse.getSh_name());
					inventory.setGoods_id(CommUtil.null2Long(goods_id));
					inventory.setGoods_name(goods.getGoods_name());
					inventory.setSpec_id("");
					inventory.setSpec_name("无");
					inventory.setPrice(goods.getGoods_current_price());
					inventory.setCount(CommUtil.null2Int(count));
					this.inventoryService.saveEntity(inventory);
					flag = true;
				}
				inventoryOperation = new InventoryOperation();
				inventoryOperation.setAddTime(new Date());
				inventoryOperation.setCount(CommUtil.null2Int(count));
				inventoryOperation.setGoods_name(inventory.getGoods_name());
				inventoryOperation.setStorehouse_name(inventory.getStorehouse_name());
				inventoryOperation.setInventory_id(inventory.getId());
				inventoryOperation.setSpec_name(inventory.getSpec_name());
				if (type.equals("in")) {
					inventoryOperation.setType(1);
				} else {
					inventoryOperation.setType(3);
				}
				this.inventoryOperationService.saveEntity(inventoryOperation);
			}
		} else {
			String str = goods.getGoods_inventory_detail();
			List<Map> inventory_detail = JSON.parseArray(str, Map.class);
			for (Map map : inventory_detail) {
				String count = request.getParameter("spec_" + map.get("id"));
				if (CommUtil.null2Int(count) > 0) {
					String specname = request.getParameter("specname_"
							+ map.get("id"));
					params.clear();
					params.put("goods_id", CommUtil.null2Long(goods_id));

					String spec = map.get("id").toString();
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
					list = this.inventoryService.queryPageList(params);
					
					Inventory inventory;
					if (list.size() > 0) {
						inventory = (Inventory) list.get(0);
						if (type.equals("in")) {
							inventory.setCount(inventory.getCount()
									+ CommUtil.null2Int(count));
						} else {
							inventory.setCount(inventory.getCount()
									- CommUtil.null2Int(count));
						}
						this.inventoryService.updateById(inventory);
						flag = true;
					} else {
						inventory = new Inventory();
						inventory.setAddTime(new Date());
						inventory.setStorehouse_id(CommUtil
								.null2Long(storehouse_id));
						inventory.setStorehouse_name(storeHouse.getSh_name());
						inventory.setGoods_id(CommUtil.null2Long(goods_id));
						inventory.setGoods_name(goods.getGoods_name());

						spec = map.get("id").toString();
						sp = spec.split("_");
						Arrays.sort(sp);
						spec = "";
						for (String s : sp) {

							spec = spec + s + ",";
						}
						if (spec.length() > 0) {
							spec = spec.substring(0, spec.length() - 1);
						}
						inventory.setSpec_id(spec);
						inventory.setSpec_name(specname);
						inventory.setPrice(goods.getGoods_current_price());
						inventory.setCount(CommUtil.null2Int(count));
						this.inventoryService.saveEntity(inventory);
						flag = true;
					}
					inventoryOperation = new InventoryOperation();
					inventoryOperation.setAddTime(new Date());
					inventoryOperation.setCount(CommUtil.null2Int(count));
					inventoryOperation.setGoods_name(inventory.getGoods_name());
					inventoryOperation.setStorehouse_name(inventory
							.getStorehouse_name());
					inventoryOperation.setInventory_id(inventory.getId());
					inventoryOperation.setSpec_name(inventory.getSpec_name());
					if (type.equals("in")) {
						inventoryOperation.setType(1);
					} else {
						inventoryOperation.setType(3);
					}
					this.inventoryOperationService.saveEntity(inventoryOperation);
				}
			}
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("ret", Boolean.valueOf(flag));

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
	 * 库存管理
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "库存管理", value = "/inventory_del*", rtype = "admin", rname = "库存管理", rcode = "admin_store_inventory", rgroup = "自营")
	@RequestMapping({ "/inventory_del" })
	public String delete(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		if(ids!=null && ids.length>0){
			this.inventoryService.batchDeleteByIds(RedPigCommonUtil.getListByArr(ids));
		}
		return "redirect:inventory_list?currentPage=" + currentPage;
	}
	
	/**
	 * 库存管理
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "库存管理", value = "/inventory_ajax*", rtype = "admin", rname = "库存管理", rcode = "admin_store_inventory", rgroup = "自营")
	@RequestMapping({ "/inventory_ajax" })
	public void ajax(HttpServletRequest request, HttpServletResponse response,
			String id, String fieldName, String value)
			throws ClassNotFoundException {
		Inventory obj = this.inventoryService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		Field[] fields = Inventory.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		
		this.inventoryService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(val.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "商品列表", value = "/inventory_items*", rtype = "admin", rname = "库存管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/inventory_items" })
	public ModelAndView inventory_goods(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/inventory_goods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, 5, "addTime", "desc");
		
		maps.put("goods_type", 0);
		if ((goods_name != null) && (!goods_name.equals(""))) {
			maps.put("goods_name_like", goods_name);
			mv.addObject("goods_name", goods_name);
		}
		
		IPageList pList = this.goodsService.list(maps);
		String goods_url = CommUtil.getURL(request) + "/inventory_items";
		mv.addObject("goods", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(goods_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		return mv;
	}
	
	/**
	 * 该商品的库存列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param goods_id
	 * @return
	 */
	@SecurityMapping(title = "该商品的库存列表", value = "/single_goods_storehouse*", rtype = "admin", rname = "库存管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/single_goods_storehouse" })
	public ModelAndView single_goods_storehouse(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/inventory_storehouse.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, 3, "sh_sequence", "asc");
		
		maps.put("sh_status", 1);
		IPageList pList = this.storeHouseService.list(maps);
		
		String goods_url = CommUtil.getURL(request)
				+ "/single_goods_storehouse";
		mv.addObject("storehouses", pList.getResult());
		mv.addObject("goods_id", goods_id);
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(goods_url,
				"", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		return mv;
	}
	
	/**
	 * 该商品的库存列表
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param type
	 * @param storehouse_id
	 * @return
	 */
	@SecurityMapping(title = "该商品的库存列表", value = "/single_goods_inventory_edit*", rtype = "admin", rname = "库存管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/single_goods_inventory_edit" })
	public ModelAndView single_goods_inventory_edit(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String type,
			String storehouse_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/inventory_edit.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_id", CommUtil.null2Long(goods_id));
		params.put("storehouse_id", CommUtil.null2Long(storehouse_id));
		
		List<Inventory> list = this.inventoryService.queryPageList(params);

		List<Map> inventory_detail = Lists.newArrayList();
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		if (goods != null) {
			if (goods.getInventory_type().equals("all")) {
				Map<String, Object> map = Maps.newHashMap();
				if (list.size() == 0) {
					map.put("count", Integer.valueOf(0));
				} else {
					map.put("count", Integer.valueOf(((Inventory) list.get(0))
							.getCount()));
				}
				inventory_detail.add(map);
			} else {
				String str = goods.getGoods_inventory_detail();
				inventory_detail = JSON.parseArray(str, Map.class);
				for (Map map : inventory_detail) {

					map.put("count", Integer.valueOf(0));
					for (Inventory inventory : list) {
						String spec = map.get("id").toString();
						String[] sp = spec.split("_");
						Arrays.sort(sp);
						spec = "";

						for (String s : sp) {

							spec = spec + s + ",";
						}
						if (spec.length() > 0) {
							spec = spec.substring(0, spec.length() - 1);
						}
						if (spec.equals(inventory.getSpec_id())) {
							map.put("count",
									Integer.valueOf(inventory.getCount()));
							map.put("inventory_id", inventory.getId());
						}
					}
				}
			}
		}
		mv.addObject("type", type);
		mv.addObject("obj", goods);
		mv.addObject("goods_id", goods_id);
		mv.addObject("storehouse_id", storehouse_id);
		mv.addObject("inventory_detail", inventory_detail);
		return mv;
	}
	
	/**
	 * 该商品的库存列表
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @param max
	 * @return
	 */
	@SecurityMapping(title = "该商品的库存列表", value = "/single_inventory_update*", rtype = "admin", rname = "库存管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/single_inventory_update" })
	public ModelAndView single_inventory_updateById(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String max) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/inventory_single_update.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("id", id);
		mv.addObject("type", type);
		if ((type.equals("out")) && (CommUtil.null2Int(max) > 0)) {
			mv.addObject("max", max);
		}
		return mv;
	}
	
	/**
	 * 该商品的库存列表
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @param count
	 * @param operation_info
	 * @return
	 */
	@SecurityMapping(title = "该商品的库存列表", value = "/single_inventory_save*", rtype = "admin", rname = "库存管理", rcode = "admin_goods", rgroup = "商品")
	@RequestMapping({ "/single_inventory_save" })
	public String single_inventory_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String count,
			String operation_info) {
		Inventory inventory = this.inventoryService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (type.equals("out")) {
			int temp_count = inventory.getCount() - CommUtil.null2Int(count);
			if (temp_count <= 0) {
				temp_count = 0;
			}
			inventory.setCount(temp_count);
		} else {
			inventory.setCount(inventory.getCount() + CommUtil.null2Int(count));
		}
		this.inventoryService.updateById(inventory);
		InventoryOperation inventoryOperation = new InventoryOperation();
		inventoryOperation.setAddTime(new Date());
		inventoryOperation.setCount(CommUtil.null2Int(count));
		inventoryOperation.setGoods_name(inventory.getGoods_name());
		inventoryOperation.setStorehouse_name(inventory.getStorehouse_name());
		inventoryOperation.setInventory_id(inventory.getId());
		inventoryOperation.setSpec_name(inventory.getSpec_name());
		if (type.equals("in")) {
			inventoryOperation.setType(1);
		} else {
			inventoryOperation.setType(3);
		}
		this.inventoryOperationService.saveEntity(inventoryOperation);
		return "redirect:" + CommUtil.getURL(request) + "/inventory_list";
	}
}
