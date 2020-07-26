package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.StoreAdjustInfo;
import com.redpigmall.domain.StoreGrade;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigStoreAdjustApplyAction.java
 * </p>
 * 
 * <p>
 * Description: 商家店铺相关调整申请控制器
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
 * @date 2015-1-5
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigStoreAdjustApplyAction extends BaseAction{
	
	/**
	 * 调整类目申请
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "调整类目申请", value = "/adjust_goodsclass*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping({ "/adjust_goodsclass" })
	public ModelAndView adjust_goodsclass(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String, Object> map = Maps.newHashMap();
		map.put("store_id", store.getId());
		
		List<StoreAdjustInfo> infos = this.adjustInfoService.queryPageList(map);
		
		if (infos.size() == 0) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/adjust_goodsclass.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			map.clear();
			map.put("orderBy", "sequence");
			map.put("orderType", "asc");
			
			List<StoreGrade> grades = this.storeGradeService.queryPageList(map);
			
			mv.addObject("grades", grades);
			map.clear();
			map.put("parent", -1);
			List<GoodsClass> gcs = this.goodsclassService.queryPageList(map);
			
			mv.addObject("goodsClass", gcs);
			mv.addObject("store", store);
			
			if (user.getStore().getGc_detail_info() != null) {
				Set<GoodsClass> detail_gcs = this.storeTools.query_store_DetailGc(user.getStore().getGc_detail_info());
				mv.addObject("detail_gcs", detail_gcs);
			}
			GoodsClass main_gc = this.goodsClassService.selectByPrimaryKey(user.getStore().getGc_main_id());
			mv.addObject("main_gc", main_gc);
			return mv;
		}
		StoreAdjustInfo adjustInfo = (StoreAdjustInfo) infos.get(0);
		mv = new RedPigJModelAndView("user/default/sellercenter/adjust_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (adjustInfo.getApply_status() == 0) {
			mv.addObject("title", "我们正在处理您提交的申请...");
		}
		if (adjustInfo.getApply_status() == 5) {
			mv.addObject("title", "您提交的调整申请已经被拒绝！");
			mv.addObject("again", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 重新提交申请
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "重新提交申请", value = "/adjust_again*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping({ "/adjust_again" })
	public String adjust_again(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String, Object> map = Maps.newHashMap();
		map.put("store_id", store.getId());
		
		List<StoreAdjustInfo> infos = this.adjustInfoService.queryPageList(map);
		
		for (StoreAdjustInfo info : infos) {
			this.adjustInfoService.deleteById(info.getId());
		}
		return "redirect:adjust_goodsclass";
	}
	
	/**
	 * 新增经营类目
	 * @param request
	 * @param response
	 * @param grade_id
	 * @param gc_main_id
	 */
	@SecurityMapping(title = "新增经营类目", value = "/add_gc_detail*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping({ "/add_gc_detail" })
	public void add_gc_detail(HttpServletRequest request,
			HttpServletResponse response, String grade_id, String gc_main_id) {
		String jsonList = "";
		StoreGrade storeGrade = this.storeGradeService.selectByPrimaryKey(CommUtil
				.null2Long(grade_id));
		if ((storeGrade != null) && (storeGrade.getMain_limit() == 1)) {
			GoodsClass gc = this.goodsclassService.selectByPrimaryKey(CommUtil.null2Long(gc_main_id));
			List<Map> list = Lists.newArrayList();
			for (GoodsClass child : gc.getChilds()) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", child.getId());
				map.put("name", child.getClassName());
				list.add(map);
			}
			jsonList = JSON.toJSONString(list);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(jsonList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存类目
	 * @param request
	 * @param response
	 * @param ids
	 * @param gc_detail_info
	 */
	@SecurityMapping(title = "保存类目", value = "/adjust_gc_save*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping({ "/adjust_gc_save" })
	public void adjust_gc_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String ids, String gc_detail_info) {
		Map mapJson = null;
		String[] idsStr = ids.split(",");
		
		for (String id : idsStr) {
			GoodsClass gc = this.goodsclassService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (gc != null) {
				GoodsClass parent = gc.getParent();
				if ((gc_detail_info != null) && (!gc_detail_info.equals(""))) {
					mapJson = JSON.parseObject(gc_detail_info);
					Map<String, Object> map = Maps.newHashMap();
					map.put("gc_id", id);
					map.put("gc_name", gc.getClassName());
					map.put("parent_name", parent.getClassName());
					map.put("parent_id", parent.getId());
					mapJson.put(id, map);
				} else {
					mapJson = Maps.newHashMap();
					Map<String, Object> map = Maps.newHashMap();
					map.put("gc_id", id);
					map.put("gc_name", gc.getClassName());
					map.put("parent_name", parent.getClassName());
					map.put("parent_id", parent.getId());
					mapJson.put(id, map);
				}
				gc_detail_info = JSON.toJSONString(mapJson);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(gc_detail_info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 删除类目
	 * @param request
	 * @param response
	 * @param id
	 * @param gc_detail_info
	 */
	@SecurityMapping(title = "删除类目", value = "/adjust_gc_del*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping({ "/adjust_gc_del" })
	public void adjust_gc_del(HttpServletRequest request,
			HttpServletResponse response, String id, String gc_detail_info) {
		GoodsClass gc = this.goodsclassService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((gc_detail_info != null) && (!gc_detail_info.equals(""))
				&& (gc != null)) {
			Map mapJson = JSON.parseObject(gc_detail_info);
			mapJson.remove(id);
			gc_detail_info = JSON.toJSONString(mapJson);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(gc_detail_info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 调整类目申请保存
	 * @param request
	 * @param response
	 * @param gc_main
	 * @param store_grade
	 * @param gc_ids
	 * @return
	 */
	@SecurityMapping(title = "调整类目申请保存", value = "/adjust_apply_save*", rtype = "seller", rname = "经营类目调整", rcode = "adjust_seller", rgroup = "我的店铺")
	@RequestMapping({ "/adjust_apply_save" })
	public ModelAndView adjust_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String gc_main, String store_grade,
			String gc_ids) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		ModelAndView mv = null;
		Map<String, Object> map = Maps.newHashMap();
		map.put("store_id", store.getId());
		List<StoreAdjustInfo> infos = this.adjustInfoService.queryPageList(map);
		
		if (infos.size() == 0) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/adjust_info.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			StoreAdjustInfo storeAdjustInfo = new StoreAdjustInfo();
			storeAdjustInfo.setAddTime(new Date());
			storeAdjustInfo.setStore_id(store.getId());
			storeAdjustInfo.setStore_name(store.getStore_name());
			storeAdjustInfo.setAdjust_type("gc");
			storeAdjustInfo.setApply_status(0);
			String[] gcIds = gc_ids.split(",");
			List<Map> json = Lists.newArrayList();
			String gc_details_info = storeAdjustInfo.getAdjust_gc_info();
			if ((gc_details_info != null) && (!"".equals(gc_details_info))) {
				json = JSON.parseArray(gc_details_info, Map.class);
			}
			for (int i = 0; i < gcIds.length; i++) {
				if ((gcIds[i] != null) && (!"".equals(gcIds[i]))) {
					GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil
							.null2Long(gcIds[i]));
					if (gc != null) {
						GoodsClass parent = gc.getParent();
						Map m = Maps.newHashMap();
						m.put("gc_id", gc.getId());
						m.put("gc_name", gc.getClassName());
						m.put("parent_name", parent.getClassName());
						m.put("parent_id", parent.getId());
						json.add(m);
					}
				}
			}
			storeAdjustInfo.setAdjust_gc_info(JSON.toJSONString(json));
			GoodsClass gc = this.goodsclassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_main));
			storeAdjustInfo.setAdjust_gc_main(gc.getClassName());
			storeAdjustInfo.setAdjust_gc_main_id(CommUtil.null2Long(gc_main));
			StoreGrade sg = this.storeGradeService.selectByPrimaryKey(CommUtil
					.null2Long(store_grade));
			storeAdjustInfo.setAdjust_store_grade(sg.getGradeName());
			storeAdjustInfo.setAdjust_storeGrade_id(CommUtil
					.null2Long(store_grade));
			this.adjustInfoService.saveEntity(storeAdjustInfo);
			mv.addObject("title", "申请成功，我们会尽快为您处理...");
			return mv;
		}
		mv = new RedPigJModelAndView("user/default/sellercenter/adjust_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("title", "我们正在处理您提交的申请...");

		return mv;
	}
}
