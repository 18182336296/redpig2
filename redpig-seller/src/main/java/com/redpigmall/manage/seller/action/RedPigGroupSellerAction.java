package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Group;
import com.redpigmall.domain.GroupArea;
import com.redpigmall.domain.GroupAreaInfo;
import com.redpigmall.domain.GroupClass;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupInfo;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.SalesLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title:RedPigGroupSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心团购管理控制器
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
 * @date 2014年4月24日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigGroupSellerAction extends BaseAction{
	/**
	 * 卖家团购列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gg_name
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "卖家团购列表", value = "/group*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group" })
	public ModelAndView group(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gg_name,
			String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		if (("goods".equals(type)) || (CommUtil.null2String(type).equals(""))) {
			type = "goods";
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
			maps.put("gg_goods_goods_store_user_id", user.getId());
			
			if (!CommUtil.null2String(gg_name).equals("")) {
				maps.put("gg_name_like", gg_name);
			}
			
			IPageList pList = this.groupGoodsService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("gg_name", gg_name);
		} else {
			mv = new RedPigJModelAndView("user/default/sellercenter/group_life.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			type = "life";
			
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
			maps.put("user_id", user.getId());
			
			
			if (!CommUtil.null2String(gg_name).equals("")) {
				maps.put("gg_name_like", gg_name);
			}
			
			IPageList pList = this.groupLifeGoodsService.list(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			mv.addObject("gg_name", gg_name);
		}
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * 卖家团购添加
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "卖家团购添加", value = "/group_add*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_add" })
	public ModelAndView group_add(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		boolean ret = true;
		if (store.getGroup_meal_endTime() == null) {
			ret = false;
		} else if (new Date().after(store.getGroup_meal_endTime())) {
			ret = false;
		}
		if (ret) {
			if (CommUtil.null2String(type).equals("")) {
				type = "life";
			}
			if (type.equals("life")) {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/group_life_add.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				params.put("add_Time_less_than", new Date());
				params.put("group_status_no", -1);
				params.put("user_id", user.getId());
				
				params.clear();
				params.put("group_type", Integer.valueOf(1));
				params.put("joinEndTime_more_than_equal", new Date());
				params.put("status0", 0);
				params.put("status1", 1);
				
				List<Group> groups = this.groupService.queryPageList(params);
				
				params.clear();
				params.put("store_id", store.getId());
				List<GroupAreaInfo> gai = this.groupAreaInfoService.queryPageList(params);
				
				params.clear();
				params.put("gc_type", Integer.valueOf(1));
				params.put("parent", -1);
				params.put("orderBy", "gc_sequence");
				params.put("orderType", "asc");
				
				List<GroupClass> gcs = this.groupClassService.queryPageList(params);
				
				if ((groups.size() == 0) || (gcs.size() == 0)) {
					mv = new RedPigJModelAndView(
							"user/default/sellercenter/seller_error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "尚未有团购开启");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/group");
				}
				mv.addObject("gcs", gcs);
				mv.addObject("gai", gai);
				mv.addObject("groups", groups);
			} else {
				params.clear();
				params.put("beginTime_more_than_equal", new Date());
				params.put("gg_status_no", -1);
				params.put("gg_goods_goods_store_id", user.getStore().getId());
				
				params.clear();
				params.put("group_type", Integer.valueOf(0));
				params.put("joinEndTime_more_than_equal", new Date());
				params.put("status0", 0);
				params.put("status1", 1);
				
				List<Group> groups = this.groupService.queryPageList(params);
				
				params.clear();
				params.put("parent_id", -1);
				params.put("orderBy", "ga_sequence");
				params.put("orderType", "asc");
				
				List<GroupArea> gas = this.groupAreaService.queryPageList(params);
				
				params.clear();
				params.put("gc_type", Integer.valueOf(0));
				params.put("parent", -1);
				params.put("orderBy", "gc_sequence");
				params.put("orderType", "asc");
				
				List<GroupClass> gcs = this.groupClassService.queryPageList(params);
				
				if ((groups.size() == 0) || (gas.size() == 0)
						|| (gcs.size() == 0)) {
					mv = new RedPigJModelAndView(
							"user/default/sellercenter/seller_error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 0, request,
							response);
					mv.addObject("op_title", "尚未有团购开启");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/group");
				}
				mv.addObject("gcs", gcs);
				mv.addObject("gas", gas);
				mv.addObject("groups", groups);
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "您没有购买团购套餐");
			mv.addObject("url", CommUtil.getURL(request) + "/group_meal");
		}
		return mv;
	}
	
	/**
	 * 卖家团购编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家团购编辑", value = "/group_lifeedit*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_lifeedit" })
	public ModelAndView group_lifeedit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group_life_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String, Object> params = Maps.newHashMap();
		params.put("joinEndTime_more_than_equal", new Date());
		params.put("group_type", 1);
		params.put("status0", 0);
		params.put("status1", 1);
		
		List<Group> groups = this.groupService.queryPageList(params);
		
		params.clear();
		params.put("store_id", store.getId());
		
		List<GroupAreaInfo> gai = this.groupAreaInfoService.queryPageList(params);
		
		params.clear();
		params.put("parent", -1);
		params.put("orderBy", "gc_sequence");
		params.put("orderType", "asc");
		
		List<GroupClass> gcs = this.groupClassService.queryPageList(params);
		
		GroupLifeGoods obj = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		String describe = obj.getGg_describe();
		Map<String, Object> map = JSON.parseObject(describe);
		mv.addObject("describe_map", map);
		mv.addObject("obj", obj);
		mv.addObject("gcs", gcs);
		mv.addObject("gai", gai);
		mv.addObject("groups", groups);
		return mv;
	}
	
	/**
	 * 卖家团购编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家团购编辑", value = "/group_edit*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_edit" })
	public ModelAndView group_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("joinEndTime_more_than_equal", new Date());
		params.put("group_type", 0);
		params.put("status0", 0);
		params.put("status1", 1);
		
		List<Group> groups = this.groupService.queryPageList(params);
		
		params.clear();
		params.put("parent", -1);
		params.put("orderBy", "ga_sequence");
		params.put("orderType", "asc");
		
		List<GroupArea> gas = this.groupAreaService.queryPageList(params);
		
		params.clear();
		params.put("parent", -1);
		params.put("orderBy", "gc_sequence");
		params.put("orderType", "asc");
		List<GroupClass> gcs = this.groupClassService.queryPageList(params);
		
		GroupGoods obj = this.groupGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		mv.addObject("obj", obj);
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("groups", groups);
		return mv;
	}
	
	/**
	 * 卖家团购商品
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "卖家团购商品", value = "/group_items*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_items" })
	public ModelAndView group_goods(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		List<GoodsClass> gcs = this.storeTools.query_store_gc(user.getStore());
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param gc_id
	 */
	@RequestMapping({ "/group_goods_load" })
	public void group_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_store_id", store.getId());
		params.put("goods_type", Integer.valueOf(1));

		this.queryTools.shildGoodsStatusParams(params);
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.GoodsClassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
			Set<Long> ids = genericGcIds(gc);
			
			params.put("gc_ids", ids);
		}
		
		List<Goods> goods = this.goodsService.queryPageList(params);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		for (Goods obj : goods) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", obj.getId());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_price", obj.getGoods_current_price());
			map.put("store_inventory",
					Integer.valueOf(obj.getGoods_inventory()));
			list.add(map);
		}
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(list));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品类团购商品保存
	 * @param request
	 * @param response
	 * @param id
	 * @param group_id
	 * @param goods_id
	 * @param gc_id
	 * @param ga_id
	 * @param gg_price
	 */
	
	@SecurityMapping(title = "商品类团购商品保存", value = "/group_goods_save*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_goods_save" })
	public void group_goods_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String goods_id, String gc_id, String ga_id, String gg_price) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		GroupGoods gg = null;
		if (id.equals("")) {
			gg = (GroupGoods) WebForm.toPo(request, GroupGoods.class);
			gg.setAddTime(new Date());
		} else {
			GroupGoods obj = this.groupGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			gg = (GroupGoods) WebForm.toPo(request, obj);
		}
		gg.setGg_count(gg.getGg_count());
		Group group = this.groupService
				.selectByPrimaryKey(CommUtil.null2Long(group_id));
		gg.setGroup(group);
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		gg.setGg_goods(goods);
		GroupClass gc = this.groupClassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		gg.setGg_gc(gc);
		GroupArea ga = this.groupAreaService.selectByPrimaryKey(CommUtil
				.null2Long(ga_id));
		gg.setGg_ga(ga);
		gg.setGg_rebate(BigDecimal.valueOf(CommUtil.mul(Integer.valueOf(10),
				Double.valueOf(CommUtil.div(gg_price, goods.getStore_price())))));
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "group";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = gg.getGg_img() == null ? "" : gg.getGg_img()
					.getName();
			map = CommUtil.saveFileToServer(request, "gg_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					gg_img.setPath(uploadFilePath + "/group");
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.saveEntity(gg_img);
					gg.setGg_img(gg_img);
				}
			} else if (map.get("fileName") != "") {
				Accessory gg_img = gg.getGg_img();
				gg_img.setName(CommUtil.null2String(map.get("fileName")));
				gg_img.setExt(CommUtil.null2String(map.get("mime")));
				gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				gg_img.setPath(uploadFilePath + "/group");
				gg_img.setWidth(CommUtil.null2Int(map.get("width")));
				gg_img.setHeight(CommUtil.null2Int(map.get("height")));
				gg_img.setAddTime(new Date());
				this.accessoryService.updateById(gg_img);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		gg.setGg_rebate(BigDecimal.valueOf(CommUtil.div(Double.valueOf(CommUtil
				.mul(gg.getGg_price(), Integer.valueOf(10))), gg.getGg_goods()
				.getGoods_price())));
		if (id.equals("")) {
			this.groupGoodsService.saveEntity(gg);
		} else {
			this.groupGoodsService.updateById(gg);
		}
		goods.setGroup_buy(1);
		
		this.goodsService.updateById(goods);
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(true));
		json.put("op_title", "申请团购成功");
		json.put("url", CommUtil.getURL(request) + "/group?type=goods");
		return_json(JSON.toJSONString(json), response);
	}
	
	/**
	 * 生活类团购商品保存
	 * @param request
	 * @param response
	 * @param id
	 * @param group_id
	 * @param gc_id
	 * @param gai_id
	 * @param beginTime
	 * @param endTime
	 * @param group_price
	 * @param cost_price
	 * @param gg_phone
	 * @param gg_timerengestart
	 * @param gg_timerengeend
	 * @param gg_scope
	 * @param gg_rules
	 */
	
	@SecurityMapping(title = "生活类团购商品保存", value = "/grouplife_goods_save*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/grouplife_goods_save" })
	public void grouplife_goods_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String gc_id, String gai_id, String beginTime, String endTime,
			String group_price, String cost_price, String gg_phone,
			String gg_timerengestart, String gg_timerengeend, String gg_scope,
			String gg_rules) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		GroupLifeGoods grouplifegoods = null;
		if (id.equals("")) {
			grouplifegoods = (GroupLifeGoods) WebForm.toPo(request,
					GroupLifeGoods.class);
			grouplifegoods.setAddTime(new Date());
		} else {
			GroupLifeGoods obj = this.groupLifeGoodsService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			grouplifegoods = (GroupLifeGoods) WebForm.toPo(request, obj);
		}
		grouplifegoods.setGoods_type(0);
		grouplifegoods.setGroup_status(0);

		GroupAreaInfo gai = this.groupAreaInfoService.selectByPrimaryKey(CommUtil
				.null2Long(gai_id));
		grouplifegoods.setGg_gai(gai);
		GroupClass gg_gc = this.groupClassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		grouplifegoods.setGg_gc(gg_gc);
		Group group = this.groupService
				.selectByPrimaryKey(CommUtil.null2Long(group_id));
		grouplifegoods.setGroup(group);
		grouplifegoods.setGg_phone(gg_phone);
		String describe = grouplifegoods.getGg_describe();
		Map<String, Object> map = Maps.newHashMap();
		map.put("gg_scope", gg_scope);
		map.put("gg_rules", gg_rules);
		map.put("gg_timerengestart", gg_timerengestart);
		map.put("gg_timerengeend", gg_timerengeend);
		describe = JSON.toJSONString(map);
		grouplifegoods.setGg_describe(describe);
		grouplifegoods.setUser(user);
		grouplifegoods.setBeginTime(CommUtil.formatDate(beginTime));
		grouplifegoods.setEndTime(CommUtil.formatDate(endTime));
		grouplifegoods.setGg_rebate(BigDecimal.valueOf(CommUtil.mul(
				Integer.valueOf(10),
				Double.valueOf(CommUtil.div(group_price, cost_price)))));
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "group";
		map.clear();
		try {
			String fileName = grouplifegoods.getGroup_acc() == null ? ""
					: grouplifegoods.getGroup_acc().getName();
			map = CommUtil.saveFileToServer(request, "group_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory gg_img = new Accessory();
					gg_img.setName(CommUtil.null2String(map.get("fileName")));
					gg_img.setExt(CommUtil.null2String(map.get("mime")));
					gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					gg_img.setPath(uploadFilePath + "/group");
					gg_img.setWidth(CommUtil.null2Int(map.get("width")));
					gg_img.setHeight(CommUtil.null2Int(map.get("height")));
					gg_img.setAddTime(new Date());
					this.accessoryService.saveEntity(gg_img);
					grouplifegoods.setGroup_acc(gg_img);
				}
			} else if (map.get("fileName") != "") {
				Accessory gg_img = grouplifegoods.getGroup_acc();
				gg_img.setName(CommUtil.null2String(map.get("fileName")));
				gg_img.setExt(CommUtil.null2String(map.get("mime")));
				gg_img.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				gg_img.setPath(uploadFilePath + "/group");
				gg_img.setWidth(CommUtil.null2Int(map.get("width")));
				gg_img.setHeight(CommUtil.null2Int(map.get("height")));
				gg_img.setAddTime(new Date());
				this.accessoryService.updateById(gg_img);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "lifegoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		
		RedPigLuceneUtil.setConfig(this.configService.getSysConfig());
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		if (id.equals("")) {
			this.groupLifeGoodsService.saveEntity(grouplifegoods);
			this.goodsTools.addGroupLifeLucene(grouplifegoods);
		} else {
			this.groupLifeGoodsService.updateById(grouplifegoods);
			this.goodsTools.updateGroupLifeLucene(grouplifegoods);
		}
		
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(true));
		json.put("op_title", "生活类团购商品保存成功");
		json.put("url", CommUtil.getURL(request) + "/group?type=life");
		return_json(JSON.toJSONString(json), response);
	}

	private Set<Long> genericGcIds(GoodsClass gc) {
		Set<Long> ids = Sets.newHashSet();
		if (gc != null) {
			ids.add(gc.getId());
			for (GoodsClass child : gc.getChilds()) {
				Set<Long> cids = genericGcIds(child);
				for (Long cid : cids) {
					ids.add(cid);
				}
				ids.add(child.getId());
			}
		}
		return ids;
	}
	
	/**
	 * 团购商品删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购商品删除", value = "/group_del*", rtype = "seller", rname = "商品购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_del" })
	public String group_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		
		for (String id : ids) {
			
			GroupGoods gg = this.groupGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			
			Goods goods = gg.getGg_goods();
			goods.setGroup_buy(0);

			this.goodsTools.deleteGroupGoodsLucene(gg);
			this.goodsService.updateById(goods);
			
			RedPigCommonUtil.del_acc(request, gg.getGg_img());
			this.groupGoodsService.deleteById(CommUtil.null2Long(id));
		}
		return "redirect:group?currentPage=" + currentPage;
	}
	
	/**
	 * 团购商品删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购商品删除", value = "/group_lifedel*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_lifedel" })
	public String group_lifedel(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			GroupLifeGoods gg = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			
			if ((gg != null) && (gg.getUser().getId().equals(user.getId()))) {
				
				RedPigCommonUtil.del_acc(request, gg.getGroup_acc());
				this.goodsTools.deleteGroupLifeLucene(gg);
				this.groupLifeGoodsService.deleteById(CommUtil.null2Long(id));
			}
		}
		return "redirect:group?type=life&currentPage=" + currentPage
				+ "type=life";
	}
	
	/**
	 * 团购套餐购买
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "团购套餐购买", value = "/group_meal*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_meal" })
	public ModelAndView group_meal(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group_meal.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("user", user);
		return mv;
	}
	
	/**
	 * 团购套餐购买
	 * @param request
	 * @param response
	 * @param meal_day
	 * @throws ParseException
	 */
	@SecurityMapping(title = "团购套餐购买", value = "/group_meal_save*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_meal_save" })
	public void group_meal_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String meal_day)
			throws ParseException {
		Map<String,Object> json = Maps.newHashMap();
		json.put("ret", Boolean.valueOf(false));
		if (this.configService.getSysConfig().getGroupBuy()) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			
			user = user.getParent() == null ? user : user.getParent();
			int cost = this.configService.getSysConfig().getGroup_meal_gold();
			int days = 30;
			if ((meal_day != null) && (meal_day.equals("30"))) {
				days = 30;
			}
			if ((meal_day != null) && (meal_day.equals("90"))) {
				days = 90;
			}
			if ((meal_day != null) && (meal_day.equals("180"))) {
				days = 180;
			}
			if ((meal_day != null) && (meal_day.equals("360"))) {
				days = 360;
			}
			int costday = days / 30;
			if (user.getGold() >= costday * cost) {
				user.setGold(user.getGold() - costday * cost);
				this.userService.updateById(user);
				Date day = user.getStore().getGroup_meal_endTime();
				if (day != null) {
					if (day.after(new Date())) {
						user.getStore()
								.setGroup_meal_endTime(
										addDate(user.getStore()
												.getGroup_meal_endTime(),
												CommUtil.null2Long(
														Integer.valueOf(days))
														.longValue()));
						this.storeService.updateById(user.getStore());

						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买团购套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.saveEntity(log);

						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore()
								.getGroup_meal_endTime());
						c_log.setEnd_time(addDate(user.getStore()
								.getGroup_meal_endTime(),
								CommUtil.null2Long(Integer.valueOf(days))
										.longValue()));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(4);
						this.salesLogService.saveEntity(c_log);
					} else {
						Calendar ca = Calendar.getInstance();
						ca.add(5, days);
						SimpleDateFormat bartDateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String latertime = bartDateFormat.format(ca.getTime());
						user.getStore().setGroup_meal_endTime(
								CommUtil.formatDate(latertime,
										"yyyy-MM-dd HH:mm:ss"));
						this.storeService.updateById(user.getStore());

						GoldLog log = new GoldLog();
						log.setAddTime(new Date());
						log.setGl_content("购买团购套餐");
						log.setGl_count(costday * cost);
						log.setGl_user(user);
						log.setGl_type(-1);
						this.goldLogService.saveEntity(log);

						SalesLog c_log = new SalesLog();
						c_log.setAddTime(new Date());
						c_log.setBegin_time(user.getStore()
								.getGroup_meal_endTime());
						c_log.setEnd_time(addDate(user.getStore()
								.getGroup_meal_endTime(),
								CommUtil.null2Long(Integer.valueOf(days))
										.longValue()));
						c_log.setGold(costday * cost);
						c_log.setSales_info("套餐总时间增加" + days + "天");
						c_log.setStore_id(user.getStore().getId());
						c_log.setSales_type(4);
						this.salesLogService.saveEntity(c_log);
					}
				} else {
					Calendar ca = Calendar.getInstance();
					ca.add(5, days);
					SimpleDateFormat bartDateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					String latertime = bartDateFormat.format(ca.getTime());
					user.getStore().setGroup_meal_endTime(
							CommUtil.formatDate(latertime,
									"yyyy-MM-dd HH:mm:ss"));
					this.storeService.updateById(user.getStore());

					GoldLog log = new GoldLog();
					log.setAddTime(new Date());
					log.setGl_content("购买团购套餐");
					log.setGl_count(costday * cost);
					log.setGl_user(user);
					log.setGl_type(-1);
					this.goldLogService.saveEntity(log);

					SalesLog c_log = new SalesLog();
					c_log.setAddTime(new Date());
					c_log.setBegin_time(user.getStore().getGroup_meal_endTime());
					c_log.setEnd_time(addDate(user.getStore()
							.getGroup_meal_endTime(),
							CommUtil.null2Long(Integer.valueOf(days))
									.longValue()));
					c_log.setGold(costday * cost);
					c_log.setSales_info("套餐总时间增加" + days + "天");
					c_log.setStore_id(user.getStore().getId());
					c_log.setSales_type(4);
					this.salesLogService.saveEntity(c_log);
				}
				json.put("ret", Boolean.valueOf(true));
				json.put("op_title", "购买成功");
				json.put("url", CommUtil.getURL(request) + "/group");
				return_json(JSON.toJSONString(json), response);
			} else {
				json.put("ret", Boolean.valueOf(false));
				json.put("op_title", "您的金币不足，无法购买团购套餐");
				json.put("url", CommUtil.getURL(request) + "/group");
				return_json(JSON.toJSONString(json), response);
			}
		} else {
			json.put("ret", Boolean.valueOf(false));
			json.put("op_title", "购买失败");
			json.put("url", CommUtil.getURL(request) + "/group");
			return_json(JSON.toJSONString(json), response);
		}
	}

	public static Date addDate(Date d, long day) throws ParseException {
		long time = d.getTime();
		day = day * 24L * 60L * 60L * 1000L;
		time += day;
		return new Date(time);
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param beginTime
	 * @param group_id
	 */
	@RequestMapping({ "/verify_gourp_begintime" })
	public void verify_gourp_begintime(HttpServletRequest request,
			HttpServletResponse response, String beginTime, String group_id) {
		boolean ret = false;
		Group group = this.groupService
				.selectByPrimaryKey(CommUtil.null2Long(group_id));
		Date date = CommUtil.formatDate(beginTime);
		if ((date.after(group.getBeginTime()))
				||

				(CommUtil.formatLongDate(date).equals(CommUtil
						.formatLongDate(group.getBeginTime())))) {
			ret = true;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/verify_gourp_endtime" })
	public void verify_gourp_endtime(HttpServletRequest request,
			HttpServletResponse response, String endTime, String group_id,
			String beginTime) {
		boolean ret = false;
		Group group = this.groupService.selectByPrimaryKey(CommUtil.null2Long(group_id));
		
		Date date = CommUtil.formatDate(endTime);
		Date bdate = CommUtil.formatDate(beginTime);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((date.before(group.getEndTime()))
				&& (date.before(store.getGroup_meal_endTime()))) {
			ret = true;
			if (date.after(bdate)) {
				ret = true;
			} else {
				ret = false;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生活购订单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "生活购订单列表", value = "/grouplife_order*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/grouplife_order" })
	public ModelAndView grouplife_selforder(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/grouplife_order.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
        maps.put("order_form",0);
        maps.put("order_main", 0);
        maps.put("order_cat", 2);
        maps.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId().toString());
        
		if ((status != null) && (!status.equals(""))) {
			maps.put("order_status", CommUtil.null2Int(status));
		}
		if (!CommUtil.null2String(order_id).equals("")) {
			maps.put("order_id_like", order_id);
			mv.addObject("order_id", order_id);
		}
		
		mv.addObject("orderFormTools", this.orderFormTools);
		IPageList pList = this.orderFormService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("status", status);
		return mv;
	}
	
	/**
	 * 生活购消费码列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param info_id
	 * @return
	 */
	@SecurityMapping(title = "生活购消费码列表", value = "/grouplife_selfinfo*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/grouplife_selfinfo" })
	public ModelAndView grouplife_selfinfo(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String info_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/grouplife_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, null, null);
        maps.put("lifeGoods_goods_type", 0);
        maps.put("lifeGoods_user_id", user.getId());
        
		if (!CommUtil.null2String(info_id).equals("")) {
			maps.put("group_sn", info_id);
			mv.addObject("info_id", info_id);
		}
		
		maps.put("status_no", 0);
		
		IPageList pList = this.groupInfoService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 订单取消
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "订单取消", value = "/lifeorder_cancel*", rtype = "seller", rname = "生活购管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/lifeorder_cancel" })
	public String lifeorder_cancel(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if (of.getStore_id().equals(user.getStore().getId().toString())) {
			of.setOrder_status(0);
			this.orderFormService.updateById(of);
		}
		return "redirect:/grouplife_order";
	}
	
	/**
	 * 使用消费码
	 * @param request
	 * @param response
	 * @param value
	 */
	@SecurityMapping(title = "使用消费码", value = "/check_group_code*", rtype = "seller", rname = "团购码管理", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping({ "/check_group_code" })
	public void check_group_code(HttpServletRequest request,
			HttpServletResponse response, String value) {
		String code = "0";
		GroupInfo info = this.groupInfoService.getObjByProperty("group_sn", "=",value);
		
		if (info != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			if (info.getLifeGoods().getUser().getId().equals(user.getId())) {
				if (info.getStatus() == 1) {
					code = "-30";
				}
				if (info.getStatus() == -1) {
					code = "-50";
				}
				if (info.getStatus() == 3) {
					code = "-100";
				}
				if (info.getStatus() == 5) {
					code = "-150";
				}
				if (info.getStatus() == 7) {
					code = "-200";
				}
				if (info.getStatus() == 0) {
					info.setStatus(1);
					this.groupInfoService.updateById(info);
					code = "100";
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void return_json(String json, HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生活团购码验证
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "生活团购码验证", value = "/group_code*", rtype = "seller", rname = "团购套餐", rcode = "group_code_seller", rgroup = "交易管理")
	@RequestMapping({ "/group_code" })
	public ModelAndView group_code(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group_code.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		return mv;
	}
	
	/**
	 * 满就送销售套餐日志
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "满就送销售套餐日志", value = "/group_meal_log*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/group_meal_log" })
	public ModelAndView group_meal_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group_meal_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
        maps.put("store_id", store.getId());
        maps.put("sales_type", 4);
        
        maps.put("orderBy", "addTime");
        maps.put("orderType", "asc");

		IPageList pList = this.salesLogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}

	
	@RequestMapping({ "/group_life_area_update" })
	public void group_life_area_updateById(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String, Object> params = Maps.newHashMap();
		params.put("storeId", store.getId());
		List<GroupAreaInfo> gailist = this.groupAreaInfoService.queryPageList(params);
		
		List<Map<String, Object>> list = Lists.newArrayList();
		if ((gailist != null) && (gailist.size() > 0)) {
			for (GroupAreaInfo gai : gailist) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", gai.getId());
				map.put("gai_name", gai.getGai_name());
				list.add(map);
			}
		}
		return_json(JSON.toJSONString(list), response);
	}
	
	/**
	 * 团购添加获取下级分类
	 * @param request
	 * @param response
	 * @param pid
	 * @return
	 */
	@SecurityMapping(title = "团购添加获取下级分类", value = "/getGroupClass*", rtype = "seller", rname = "团购购买管理", rcode = "group_seller", rgroup = "团购管理")
	@RequestMapping({ "/getGroupClass" })
	public ModelAndView getGroupClass(HttpServletRequest request,
			HttpServletResponse response, String pid) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/group_class_select.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("parent_id", CommUtil.null2Long(pid));
		
		List<GroupClass> gclist = this.groupClassService.queryPageList(params);
		
		mv.addObject("gclist", gclist);
		return mv;
	}
}
