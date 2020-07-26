package com.redpigmall.manage.admin.action.decorate;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.manage.admin.tools.RedPigStoreTools;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreAdjustInfo;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigStoreAdjustInfoService;
import com.redpigmall.service.RedPigStoreGradeService;

/**
 * 
 * <p>
 * Title: RedPigStoreAdjustInfoManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台处理商家店铺调整申请的控制器
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
 * @date 2015-1-6
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigStoreAdjustInfoManageAction extends BaseAction{
	
	/**
	 * 申请列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "申请列表", value = "/adjust_info*", rtype = "admin", rname = "调整申请", rcode = "adjust_manage", rgroup = "店铺")
	@RequestMapping({ "/adjust_info" })
	public ModelAndView adjust_info(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/adjust_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		IPageList pList = this.storeadjustinfoService.queryPagesWithNoRelations(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/adjust_info.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 申请详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "申请详情", value = "/adjust_info*", rtype = "admin", rname = "调整申请", rcode = "adjust_manage", rgroup = "店铺")
	@RequestMapping({ "/adjust_info_view" })
	public ModelAndView adjust_info_view(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/adjust_info_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreAdjustInfo storeAdjustInfo = this.storeadjustinfoService.selectByPrimaryKey(CommUtil.null2Long(id));
		List<Map> json = Lists.newArrayList();
		String gc_details_info = storeAdjustInfo.getAdjust_gc_info();
		if ((gc_details_info != null) && (!"".equals(gc_details_info))) {
			json = JSON.parseArray(gc_details_info, Map.class);
		}
		mv.addObject("gc_details_info", json);
		mv.addObject("adjustInfo", storeAdjustInfo);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 申请审核
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "申请审核", value = "/adjust_info*", rtype = "admin", rname = "调整申请", rcode = "adjust_manage", rgroup = "店铺")
	@RequestMapping({ "/adjust_info_audit" })
	public ModelAndView adjust_info_audit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		StoreAdjustInfo storeAdjustInfo = this.storeadjustinfoService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if ("succeed".equals(type)) {
			Store store = this.storeService.selectByPrimaryKey(storeAdjustInfo
					.getStore_id());
			StoreGrade grade = this.storeGradeService
					.selectByPrimaryKey(storeAdjustInfo.getAdjust_storeGrade_id());
			System.out.println(store.getGrade());
			store.setGrade(grade);
			store.setGc_main_id(storeAdjustInfo.getAdjust_gc_main_id());
			store.setGc_detail_info(getStoreGC_detail_info(storeAdjustInfo.getAdjust_gc_info()));
			
			this.storeService.updateById(store);
			this.storeadjustinfoService.deleteById(storeAdjustInfo.getId());

			mv.addObject("op_title", "操作成功");
		}
		if ("defeat".equals(type)) {
			storeAdjustInfo.setApply_status(5);
			this.storeadjustinfoService.updateById(storeAdjustInfo);
			mv.addObject("op_title", "操作成功");
		}
		mv.addObject("list_url", "/adjust_info?currentPage="
				+ currentPage);
		return mv;
	}

	public String getStoreGC_detail_info(String adjust_gc_info) {
		if (adjust_gc_info == null) {
			return null;
		}
		List<Map> fromMap = JSON.parseArray(adjust_gc_info, Map.class);
		if (fromMap == null) {
			return null;
		}
		List<Map> toList = Lists.newArrayList();
		Iterator<Map> it = fromMap.iterator();
		while (it.hasNext()) {
			Map<String, Object> map = (Map) it.next();
			for (int j = 0; j < toList.size(); j++) {
				if (map.get("parent_id").equals(
						((Map) toList.get(j)).get("m_id"))) {
					List gc_list = (List) ((Map) toList.get(j)).get("gc_list");
					gc_list.add(Integer.valueOf(CommUtil.null2Int(map
							.get("gc_id"))));
					Map toMap = Maps.newHashMap();
					toMap.put("m_id", map.get("parent_id"));
					toMap.put("gc_list", gc_list);
					Map new_map = Maps.newHashMap();
					new_map = (Map) ((Map) toList.get(j)).get("gc_commission");
					new_map.put("gc_" + CommUtil.null2Int(map.get("gc_id")), "");
					toMap.put("gc_commission", new_map);
					toList.set(j, toMap);
					break;
				}
			}
			if (toList.size() == 0) {
				List gc_list = Lists.newArrayList();
				gc_list.add(Integer.valueOf(CommUtil.null2Int(map.get("gc_id"))));
				Map toMap = Maps.newHashMap();
				toMap.put("m_id", map.get("parent_id"));
				toMap.put("gc_list", gc_list);
				Map new_map = Maps.newHashMap();
				new_map.put("gc_" + CommUtil.null2Int(map.get("gc_id")), "");
				toMap.put("gc_commission", new_map);
				toList.add(toMap);
			}
		}
		return JSON.toJSONString(toList);
	}
	
	/**
	 * 商家详细类目管理
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_adjust_change" })
	public ModelAndView store_adjut_change(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/store_adjust_change.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		List<GoodsClass> gc_test_list = Lists.newArrayList();
		String gc_details_info = store.getGc_detail_info();
		if ((gc_details_info != null) && (!"".equals(gc_details_info))) {
			List<Map> list = JSON.parseArray(gc_details_info, Map.class);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					List test = (List) ((Map) list.get(i)).get("gc_list");
					for (Object s_test : test) {
						GoodsClass gc = this.goodsClassService
								.selectByPrimaryKey(CommUtil.null2Long(s_test));
						gc_test_list.add(gc);
					}
				}
			}
		}
		mv.addObject("storeTools", this.storeTools);
		if (store.getGrade().getMain_limit() == 1) {
			Long gc_main_id = store.getGc_main_id();
			Map<String, Object> map = Maps.newHashMap();
			map.put("parent_id", gc_main_id);
			
			List<GoodsClass> gc_ids = this.goodsClassService.queryPageList(map);
			mv.addObject("gc_ids", gc_ids);
			mv.addObject("mark", Integer.valueOf(0));
		} else {
			Map<String, Object> map = Maps.newHashMap();
			map.put("level", 0);
			List<GoodsClass> gc_all = this.goodsClassService.queryPageList(map);
			
			mv.addObject("gc_all", gc_all);
			mv.addObject("mark", Integer.valueOf(1));
		}
		mv.addObject("id", id);
		mv.addObject("objs", gc_test_list);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param pgc_id
	 * @param gc_id
	 * @param commission
	 * @param id
	 * @return
	 */
	@RequestMapping({ "/store_adjust_change_save" })
	public ModelAndView store_adjust_change_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String pgc_id, String gc_id,
			String commission, String id) {
		ModelAndView mv = null;
		String ret = "";
		Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		List<Map> list = Lists.newArrayList();
		String gc_details_info = store.getGc_detail_info();
		if (gc_details_info != null) {
			list = JSON.parseArray(gc_details_info, Map.class);
		}
		GoodsClass gc_n = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		
		if ((gc_id != null) && (!"".equals(gc_id)) && (pgc_id == null)) {
			pgc_id = gc_n.getParent().getId().toString();
		}
		if ((list == null) || (list.size() == 0)) {
			list = Lists.newArrayList();
			List mm_list = Lists.newArrayList();
			if ((!"".equals(gc_id)) && (!"".equals(pgc_id))) {
				for (int k = 0; k < list.size(); k++) {
					mm_list.add(((Map) list.get(k)).get("m_id"));
				}
				if (!mm_list
						.contains(Integer.valueOf(CommUtil.null2Int(pgc_id)))) {
					Map<String, Object> map = Maps.newHashMap();
					List list_n = Lists.newArrayList();
					list_n.add(Integer.valueOf(CommUtil.null2Int(gc_id)));
					map.put("gc_list", list_n);
					Map map_n = Maps.newHashMap();
					map_n.put("gc_" + gc_id, commission);
					map.put("gc_list", list_n);
					map.put("m_id", pgc_id);
					map.put("gc_commission", map_n);
					list.add(map);
				}
			} else if (gc_n == null) {
				gc_id = pgc_id;
				Map<String, Object> map = Maps.newHashMap();
				List list_n = Lists.newArrayList();
				list_n.add(Integer.valueOf(CommUtil.null2Int(gc_id)));
				map.put("gc_list", list_n);
				Map map_n = Maps.newHashMap();
				map_n.put("gc_" + gc_id, commission);
				map.put("gc_list", list_n);
				map.put("m_id", gc_id);
				map.put("gc_commission", map_n);
				list.add(map);
			} else {
				Map<String, Object> map = Maps.newHashMap();
				List list_n = Lists.newArrayList();
				list_n.add(Integer.valueOf(CommUtil.null2Int(gc_id)));
				map.put("gc_list", list_n);
				Map map_n = Maps.newHashMap();
				map_n.put("gc_" + gc_id, commission);
				map.put("gc_list", list_n);
				GoodsClass gc2 = this.goodsClassService.selectByPrimaryKey(CommUtil
						.null2Long(gc_id));
				map.put("m_id", Integer.valueOf(CommUtil.null2Int(gc2
						.getParent().getId())));
				map.put("gc_commission", map_n);
				list.add(map);
			}
		} else {
			List k_list = Lists.newArrayList();
			List m_list = Lists.newArrayList();
			String km = "";
			String mk = "";
			String p = "0";
			for (int k = 0; k < list.size(); k++) {
				if (((List) ((Map) list.get(k)).get("gc_list"))
						.contains(((Map) list.get(k)).get("m_id"))) {
					m_list.add(((Map) list.get(k)).get("m_id"));
				}
				GoodsClass gc_ = this.goodsClassService.selectByPrimaryKey(CommUtil
						.null2Long(gc_id));
				if (gc_id == "") {
					gc_ = this.goodsClassService.selectByPrimaryKey(CommUtil
							.null2Long(null));
				}
				if (gc_ != null) {
					if (gc_.getParent().getId().toString()
							.equals(((Map) list.get(k)).get("m_id"))) {
						km = ((Map) list.get(k)).get("m_id").toString();
						mk = "p";
					}
				}
			}
			if (!"p".equals(mk)) {
				mk = "pp";
			}
			int m = 0;
			for (int i = 0; i < list.size(); i++) {
				List test = (List) ((Map) list.get(i)).get("gc_list");
				for (Object s_test : test) {
					k_list.add(CommUtil.null2String(s_test));
				}
			}
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = (Map) list.get(i);
				Map map1 = (Map) map.get("gc_commission");
				List list1 = (List) map.get("gc_list");
				GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
						.null2Long(gc_id));
				int g = 0;
				if (gc == null) {
					gc = this.goodsClassService.selectByPrimaryKey(CommUtil
							.null2Long(pgc_id));
					g = 1;
				}
				String ret1 = "0";
				if (pgc_id.toString().equals(map.get("m_id").toString())) {
					list1.add(Integer.valueOf(CommUtil.null2Int(gc_id)));
					map1.put("gc_" + gc_id, commission);
					map.put("gc_commission", map1);
					map.put("gc_list", list1);
					list.remove(i);
					list.add(map);
					p = "1";
					break;
				}
			}
			if (p == "0") {
				Map<String, Object> map = Maps.newHashMap();
				List list_n = Lists.newArrayList();
				list_n.add(Integer.valueOf(CommUtil.null2Int(gc_id)));
				map.put("gc_list", list_n);
				Map map_n = Maps.newHashMap();
				map_n.put("gc_" + gc_id, commission);
				map.put("gc_list", list_n);
				map.put("m_id", pgc_id);
				map.put("gc_commission", map_n);
				list.add(map);
			}
		}
		String new_json = JSON.toJSONString(list);
		store.setGc_detail_info(new_json);
		if (!"false".equals(ret)) {
			this.storeService.updateById(store);
			ret = "true";
		}
		if (ret.equals("false")) {
			mv = new RedPigJModelAndView("/blue/fail.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "增加详细类目失败");
			mv.addObject("list_url", "d");
			mv.addObject("add_url", CommUtil.getURL(request)
					+ "/store_adjust_change?id=" + id);
		} else {
			mv = new RedPigJModelAndView("/blue/success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "新增详细类目成功");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/store_adjust_change?id=" + id);
		}
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param store_id
	 * @param commission
	 */
	@RequestMapping({ "/store_commission_change" })
	public void gc_data(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String store_id,
			String commission) {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		Store store = this.storeService
				.selectByPrimaryKey(CommUtil.null2Long(store_id));
		String gc_detail_info = store.getGc_detail_info();
		List<Map> list = JSON.parseArray(gc_detail_info, Map.class);
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = (Map) list.get(i);

			Map map1 = (Map) map.get("gc_commission");

			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
			if (map1 != null) {
				if (map1.containsKey("gc_" + gc_id)) {
					map1.put("gc_" + gc_id, commission);
					map.put("gc_commission", map1);
					list.remove(i);
					list.add(map);
					break;
				}
			} else {
				Map gc_com = Maps.newHashMap();
				List gc_list = (List) map.get("gc_list");
				for (int g = 0; g < gc_list.size(); g++) {
					if (gc_list.get(g).toString().equals(gc_id)) {
						gc_com.put("gc_" + gc_list.get(g).toString(),
								commission);
					} else {
						gc_com.put("gc_" + gc_list.get(g), "");
					}
				}
				map.put("gc_commission", gc_com);
				list.remove(i);
				list.add(map);
				break;
			}
		}
		String new_gc_detail_info = JSON.toJSONString(list);
		store.setGc_detail_info(new_gc_detail_info);
		this.storeService.updateById(store);
		try {
			response.getWriter().print("true");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param store_id
	 */
	@RequestMapping({ "/gc_detail_info_del" })
	public void gc_detail_info_del(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String store_id) {
		String ret = "0";
		response.setCharacterEncoding("utf-8");
		response.setHeader("Cahce-Control", "no-cache");
		response.setContentType("text/plain");
		Store store = this.storeService
				.selectByPrimaryKey(CommUtil.null2Long(store_id));
		String gc_detail_info = store.getGc_detail_info();
		List<Map> gc_detail_list = JSON.parseArray(gc_detail_info, Map.class);
		GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		if (gc != null) {
			List<Map> del_list = Lists.newArrayList();
			if (gc.getLevel() == 1) {
				for (int m = 0; m < gc_detail_list.size(); m++) {
					String parent_id = CommUtil.null2String(gc.getParent()
							.getId());
					Map gc_map = (Map) gc_detail_list.get(m);
					if (gc_map.get("m_id").toString().equals(parent_id)) {
						List gc_list1 = (List) gc_map.get("gc_list");
						for (int n = 0; n < gc_list1.size(); n++) {
							if (gc_list1.get(n).toString().equals(gc_id)) {
								Map del_map = Maps.newHashMap();
								gc_list1.remove(n);
								del_map.put("id", gc_id);
								gc_map.remove("gc_list");
								gc_map.put("gc_list", gc_list1);
								gc_detail_list.remove(m);
								del_list.add(del_map);
								break;
							}
						}
						Map gc_com_map = (Map) gc_map.get("gc_commission");
						if (!gc_map.get("gc_list").toString().equals("[]")) {
							gc_com_map.remove("gc_" + gc_id);
							gc_map.put("gc_commission", gc_com_map);
							gc_detail_list.add(gc_map);
							ret = "1";
						}
					}
					if (ret == "1") {
						break;
					}
				}
			}
			String del_ids = JSON.toJSONString(del_list);
			String new_gc_detail = JSON.toJSONString(gc_detail_list);
			store.setGc_detail_info(new_gc_detail);
			this.storeService.updateById(store);
			try {
				response.getWriter().print(del_ids);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				response.getWriter().print("no");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param pgc_id
	 * @param store_id
	 */
	@RequestMapping({ "/store_adjust_gc" })
	public void store_adjust_gc(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String pgc_id,
			String store_id) {
		String ret = "";
		Store store = this.storeService
				.selectByPrimaryKey(CommUtil.null2Long(store_id));
		if (store_id != null) {
			String gc_detail_info = store.getGc_detail_info();
			if ((gc_detail_info != null) && (!"".equals(gc_detail_info))) {
				List<Map> gc_list = JSON.parseArray(gc_detail_info, Map.class);
				List gc_two = Lists.newArrayList();
				List gc_one = Lists.newArrayList();
				for (int i = 0; i < gc_list.size(); i++) {
					List list = (List) ((Map) gc_list.get(i)).get("gc_list");
					for (int j = 0; j < list.size(); j++) {
						GoodsClass gc = this.goodsClassService
								.selectByPrimaryKey(CommUtil.null2Long(list.get(j)));
						if (gc.getLevel() == 0) {
							gc_one.add(list.get(j));
						}
						if (gc.getLevel() == 1) {
							gc_two.add(list.get(j));
						}
					}
				}
				for (int k = 0; k < gc_one.size(); k++) {
					if (gc_one.get(k).toString().equals(pgc_id)) {
						ret = "ok";
						break;
					}
				}
				response.setCharacterEncoding("utf-8");
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				try {
					response.getWriter().print(ret);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 */
	@RequestMapping({ "/get_gc_commisssion" })
	public void get_gc_commisssion(HttpServletRequest request,
			HttpServletResponse response, String gc_id) {
		String ret = "";
		GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		ret = gc.getCommission_rate().toString();
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		try {
			response.getWriter().print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param gc_id
	 * @param pgc_id
	 * @param store_id
	 */
	@RequestMapping({ "/store_adjust_gc_re" })
	public void store_adjust_gc_re(HttpServletRequest request,
			HttpServletResponse response, String gc_id, String pgc_id,
			String store_id) {
		String ret = "";
		Store store = this.storeService
				.selectByPrimaryKey(CommUtil.null2Long(store_id));
		if (store_id != null) {
			String gc_detail_info = store.getGc_detail_info();
			if ((gc_detail_info != null) && (!"".equals(gc_detail_info))) {
				List<Map> gc_list = JSON.parseArray(gc_detail_info, Map.class);
				List gc_two = Lists.newArrayList();
				List gc_one = Lists.newArrayList();
				for (int i = 0; i < gc_list.size(); i++) {
					List list = (List) ((Map) gc_list.get(i)).get("gc_list");
					for (int j = 0; j < list.size(); j++) {
						GoodsClass gc = this.goodsClassService
								.selectByPrimaryKey(CommUtil.null2Long(list.get(j)));
						if (gc.getLevel() == 0) {
							gc_one.add(list.get(j));
						}
						if (gc.getLevel() == 1) {
							gc_two.add(list.get(j));
						}
					}
				}
				if ((gc_id != null) && (!"".equals(gc_id))) {
					for (int k = 0; k < gc_two.size(); k++) {
						if (gc_two.get(k).toString().equals(gc_id)) {
							ret = "ok";
							break;
						}
					}
				} else {
					for (int k = 0; k < gc_one.size(); k++) {
						if (gc_one.get(k).toString().equals(pgc_id)) {
							ret = "ok";
							break;
						}
					}
				}
			}
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			try {
				response.getWriter().print(ret);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
