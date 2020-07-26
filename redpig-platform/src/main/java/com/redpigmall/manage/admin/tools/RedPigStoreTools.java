package com.redpigmall.manage.admin.tools;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.GoodsSpecification;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * <p>
 * Title: RedPigStoreTools.java
 * </p>
 * 
 * <p>
 * Description: 后台管理店铺、商品工具类
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
 * @date 2014-5-21
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class RedPigStoreTools {
	@Autowired
	private RedPigGoodsClassService goodsClassService;
	@Autowired
	private RedPigStoreService storeService;
	@Autowired
	private RedPigSysConfigService configService;

	public String genericProperty(GoodsSpecification spec) {
		String val = "";
		for (GoodsSpecProperty gsp : spec.getProperties()) {
			val = val + "," + gsp.getValue();
		}
		if (!val.equals("")) {
			return val.substring(1);
		}
		return "";
	}

	public String createUserFolder(HttpServletRequest request, Store store) {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		if (config.getImageSaveType().equals("sidImg")) {
			path =

			request.getSession().getServletContext().getRealPath("/")
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId();
		}
		if (config.getImageSaveType().equals("sidYearImg")) {
			path =

			request.getSession().getServletContext().getRealPath("/")
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {
			path =

			request.getSession().getServletContext().getRealPath("/")
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {
			path =

			request.getSession().getServletContext().getRealPath("/")
					+ uploadFilePath + File.separator + "store"
					+ File.separator + store.getId() + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date()) + File.separator
					+ CommUtil.formatTime("dd", new Date());
		}
		if (!CommUtil.fileExist(path)) {
			CommUtil.createFolder(path);
		}
		return path;
	}

	public String createUserFolderURL(Store store) {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		if (config.getImageSaveType().equals("sidImg")) {
			path = uploadFilePath + "/store/" + store.getId().toString();
		}
		if (config.getImageSaveType().equals("sidYearImg")) {
			path = uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {
			path =

			uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {
			path =

			uploadFilePath + "/store/" + store.getId() + "/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date()) + "/"
					+ CommUtil.formatTime("dd", new Date());
		}
		return path;
	}

	public String generic_goods_class_info(GoodsClass gc) {
		if (gc != null) {
			String goods_class_info = generic_the_goods_class_info(gc);
			return goods_class_info.substring(0, goods_class_info.length() - 1);
		}
		return "";
	}

	private String generic_the_goods_class_info(GoodsClass gc) {
		if (gc != null) {
			String goods_class_info = gc.getClassName() + ">";
			if (gc.getParent() != null) {
				String class_info = generic_the_goods_class_info(gc.getParent());
				goods_class_info = class_info + goods_class_info;
			}
			return goods_class_info;
		}
		return "";
	}

	public String query_user_store_url(String user_id, String url) {

		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", CommUtil.null2Long(user_id));

		List<Store> stores = this.storeService.queryPageList(params);

		if (stores != null && stores.size() > 0) {
			Store store = stores.get(0);
			String store_url = url + "/store_" + store.getId() + "";
			if (this.configService.getSysConfig().getSecond_domain_open()) {
				if (!"".equals(CommUtil.null2String(store
						.getStore_second_domain()))) {
					String serverName = url.toLowerCase();
					String secondDomain = CommUtil.null2String(serverName
							.substring(0, serverName.indexOf(".")));
					if (serverName.indexOf(".") == serverName.lastIndexOf(".")) {
						secondDomain = "www";
					}
					store_url = "http://" + store.getStore_second_domain()
							+ "." + secondDomain;
				}
			}
			return store_url;
		}
		return "";
	}

	public Map query_MainGc_Map(String m_id, String json) {
		Map map_temp = null;
		if ((json != null) && (!json.equals(""))) {
			List<Map> list_map = JSON.parseArray(json, Map.class);
			for (Map map : list_map) {
				if (m_id.equals(CommUtil.null2String(map.get("m_id")))) {
					map_temp = map;
					break;
				}
			}
		}
		return map_temp;
	}

	public List<GoodsClass> query_store_detail_MainGc(String json) {
		List<GoodsClass> gc_list = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			List<Map> list_map = JSON.parseArray(json, Map.class);
			for (Map map : list_map) {
				GoodsClass gc = this.goodsClassService
						.selectByPrimaryKey(CommUtil.null2Long(map.get("m_id")));
				if ((gc != null) && (!gc_list.contains(gc))) {
					gc_list.add(gc);
				}
			}
		}
		return gc_list;
	}

	public Set<GoodsClass> query_store_DetailGc(String json) {
		Set<GoodsClass> gc_list = Sets.newTreeSet();
		if ((json != null) && (!json.equals(""))) {
			List<Map> all_list = JSON.parseArray(json, Map.class);
			for (Map map : all_list) {
				List<Integer> ls = (List) map.get("gc_list");
				if (ls != null) {
					for (Integer l : ls) {
						GoodsClass gc = this.goodsClassService
								.selectByPrimaryKey(CommUtil.null2Long(l));
						gc_list.add(gc);
					}
				}
			}
		}
		return gc_list;
	}

	public String createAdminFolder(HttpServletRequest request) {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		if (config.getImageSaveType().equals("sidImg")) {
			path =

			request.getSession().getServletContext().getRealPath("/")
					+ uploadFilePath + File.separator + "system"
					+ File.separator + "self_goods";
		}
		if (config.getImageSaveType().equals("sidYearImg")) {
			path =

			request.getSession().getServletContext().getRealPath("/")
					+ uploadFilePath + File.separator + "system"
					+ File.separator + "self_goods" + File.separator
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {
			path =

			request.getSession().getServletContext().getRealPath("/")
					+ uploadFilePath + File.separator + "system"
					+ File.separator + "self_goods" + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {
			path =

			request.getSession().getServletContext().getRealPath("/")
					+ uploadFilePath + File.separator + "system"
					+ File.separator + "self_goods" + File.separator
					+ CommUtil.formatTime("yyyy", new Date()) + File.separator
					+ CommUtil.formatTime("MM", new Date()) + File.separator
					+ CommUtil.formatTime("dd", new Date());
		}
		CommUtil.createFolder(path);
		return path;
	}

	public String createAdminFolderURL() {
		SysConfig config = this.configService.getSysConfig();
		String path = "";
		String uploadFilePath = config.getUploadFilePath();
		if (config.getImageSaveType().equals("sidImg")) {
			path = uploadFilePath + "/system/self_goods";
		}
		if (config.getImageSaveType().equals("sidYearImg")) {
			path = uploadFilePath + "/system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthImg")) {
			path =

			uploadFilePath + "/system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date());
		}
		if (config.getImageSaveType().equals("sidYearMonthDayImg")) {
			path =

			uploadFilePath + "/system/self_goods/"
					+ CommUtil.formatTime("yyyy", new Date()) + "/"
					+ CommUtil.formatTime("MM", new Date()) + "/"
					+ CommUtil.formatTime("dd", new Date());
		}
		return path;
	}

	public String getCommission(Object gc_id, Object store_id) {
		String ret = "";
		Store store = this.storeService.selectByPrimaryKey(CommUtil
				.null2Long(store_id));
		if (store != null) {
			String gc_detail_info = store.getGc_detail_info();
			List<Map> gc_info = JSON.parseArray(gc_detail_info, Map.class);
			for (int i = 0; i < gc_info.size(); i++) {
				Map<String, Object> map = (Map) ((Map) gc_info.get(i))
						.get("gc_commission");
				if (map != null) {
					boolean exsit = map.containsKey("gc_" + gc_id);
					if (exsit) {
						ret = (String) ((Map) ((Map) gc_info.get(i))
								.get("gc_commission")).get("gc_" + gc_id);
					}
				} else {
					GoodsClass gc = this.goodsClassService
							.selectByPrimaryKey(CommUtil.null2Long(gc_id));

					ret = gc.getCommission_rate().toString();
				}
			}
		}
		return ret;
	}

	public String get_gc_Commission(Object gc_id) {
		String ret = "";

		GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));

		ret = gc.getCommission_rate().toString();

		return ret;
	}

	public String queryJson(String store_info, String key) {
		String value = "";
		List<Map> store_list = JSON.parseArray(store_info, Map.class);
		if ("store_point1".equals(key)) {
			if ((((Map) store_list.get(0)).get("store_point") != null)
					&& (!"".equals(((Map) store_list.get(0)).get("store_point")))) {
				Float value1 = Float.valueOf(Float.parseFloat(((Map) store_list
						.get(0)).get("store_point").toString()));
				int new_value = (int) CommUtil.mul(value1, Integer.valueOf(20));
				value = String.valueOf(new_value);
			}
		} else if ((((Map) store_list.get(0)).get(key) != null)
				&& (!"".equals(((Map) store_list.get(0)).get(key)))) {
			value = ((Map) store_list.get(0)).get(key).toString();
		}
		return value;
	}

	public List<GoodsClass> query_store_gc(Store store) {
		List<GoodsClass> gcs = Lists.newArrayList();
		if ((store.getGc_detail_info() != null)
				&& (!store.getGc_detail_info().equals(""))) {
			Set<GoodsClass> gc_list = query_store_DetailGc(store
					.getGc_detail_info());
			gcs.addAll(gc_list);
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", store.getGc_main_id());

			gcs = this.goodsClassService.queryPageList(params);

		}
		return gcs;
	}
}
