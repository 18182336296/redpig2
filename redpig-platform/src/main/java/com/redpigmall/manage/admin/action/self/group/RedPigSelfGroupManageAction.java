package com.redpigmall.manage.admin.action.self.group;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
import com.redpigmall.api.tools.JsonUtils;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
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
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigSelfGroupManageAction.java
 * </p>
 * 
 * <p>
 * Description:后台平台商自营团购控制器。平台商也可以发布团购商品
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
 * @date 2014-5-20
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigSelfGroupManageAction extends BaseAction {
	/**
	 * 自营商品类团购商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param gg_name
	 * @return
	 */
	@SecurityMapping(title = "自营商品类团购商品列表", value = "/group_self*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_self" })
	public ModelAndView group_self(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String gg_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_self.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		params.put("goods_type", 0);
		
		if (!CommUtil.null2String(gg_name).equals("")) {
			params.put("gg_name_like", gg_name);
		}
		
		IPageList pList = this.groupGoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gg_name", gg_name);
		return mv;
	}
	
	/**
	 * 自营生活类团购商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gg_name
	 * @return
	 */
	@SecurityMapping(title = "自营生活类团购商品列表", value = "/grouplife_self*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/grouplife_self" })
	public ModelAndView grouplife_self(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gg_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/grouplife_self.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		params.put("goods_type", 1);
		if (!CommUtil.null2String(gg_name).equals("")) {
			
			params.put("gg_name_like", gg_name);
		}
		
		IPageList pList = this.grouplifegoodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("gg_name", gg_name);
		return mv;
	}
	
	/**
	 * 自营商品类团购商品添加
	 * @param request
	 * @param response
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "自营商品类团购商品添加", value = "/group_self_add*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_self_add" })
	public ModelAndView group_self_add(HttpServletRequest request,
			HttpServletResponse response, String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_self_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		int gc_type = 0;
		if ("life".equals(type)) {
			gc_type = 1;
			mv = new RedPigJModelAndView("admin/blue/grouplife_self_add.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("joinEndTime_more_than_equal", new Date());
		params.put("group_type", gc_type);
		params.put("status0", 0);
		params.put("status1", 1);
		List<Group> groups = this.groupService.queryPageList(params);
		
		List<GroupArea> gas = this.groupAreaService.queryPageList(RedPigMaps.newParent(-1));
		params.clear();
		params.put("gc_type", gc_type);
		params.put("parent", -1);
		
		List<GroupClass> gcs = this.groupClassService.queryPageList(params);
		
		params.clear();
		params.put("gai_type", 0);
		
		List<GroupAreaInfo> gai = this.groupAreaInfoService.queryPageList(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		mv.addObject("gai", gai);
		mv.addObject("groups", groups);
		return mv;
	}
	
	/**
	 * 自营团购商品编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "自营团购商品编辑", value = "/group_self_edit*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_self_edit" })
	public ModelAndView group_self_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_self_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<GroupArea> gas = this.groupAreaService.queryPageList(RedPigMaps.newParent(-1));
		List<GroupClass> gcs = Lists.newArrayList();
		if ("goods".equals(type)) {
			GroupGoods obj = this.groupGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			mv.addObject("obj", obj);
			Map<String, Object> params = Maps.newHashMap();
			params.put("group_type", 0);
			params.put("status0", 0);
			params.put("status1", 1);
			
			List<Group> groups = this.groupService.queryPageList(params);
			params.clear();
			params.put("parent", -1);
			params.put("gc_type", 0);
			
			gcs = this.groupClassService.queryPageList(params);
			
			mv.addObject("groups", groups);
		} else {
			GroupLifeGoods obj = this.grouplifegoodsService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			String gg_describe = obj.getGg_describe();
			Map<?, ?> describe_map = JsonUtils.jsonToPojo(gg_describe, Map.class);
			mv = new RedPigJModelAndView("admin/blue/grouplife_self_add.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("obj", obj);
			mv.addObject("describe_map", describe_map);
			
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("group_type", 1);
			params.put("status0", 0);
			params.put("status1", 1);
			
			List<Group> groups = this.groupService.queryPageList(params);
			params.clear();
			params.put("gai_type", 0);
			
			mv.addObject("groups", groups);
			List<GroupAreaInfo> gai = this.groupAreaInfoService.queryPageList(params);
			
			mv.addObject("gai", gai);
			params.clear();
			params.put("parent", -1);
			params.put("gc_type", 1);
			
			gcs = this.groupClassService.queryPageList(params);
		}
		mv.addObject("gcs", gcs);
		mv.addObject("gas", gas);
		return mv;
	}
	
	/**
	 * 自营团购商品
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自营团购商品", value = "/group_goods_self*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_goods_self" })
	public ModelAndView group_goods_self(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_goods_self.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<GoodsClass> gcs = this.GoodsClassService.queryPageListWithNoRelations(RedPigMaps.newParent(-1));
		
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 自营团购商品加载
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param gc_id
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/group_goods_self_load" })
	public void group_goods_self_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_type", 0);
		params.put("goods_status", 0);
		params.put("activity_status", 0);
		params.put("group_buy", 0);
		params.put("combin_status", 0);
		params.put("order_enough_give_status", 0);
		params.put("enough_reduce", 0);
		params.put("f_sale_type", 0);
		params.put("advance_sale_type", 0);
		params.put("order_enough_if_give", 0);
		params.put("whether_free", 0);
		params.put("goods_limit", 0);
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.GoodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			Set<Long> ids = genericGcIds(gc);
			params.put("gc_ids", ids);
		}
		
		List<Goods> goods = this.goodsService.queryPageList(params);
		List<Map> list = Lists.newArrayList();
		for (Goods obj : goods) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getGoods_current_price());
			map.put("goods_name", obj.getGoods_name());
			map.put("store_inventory",Integer.valueOf(obj.getGoods_inventory()));
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
	 * @return
	 */
	@SuppressWarnings("unused")
	@SecurityMapping(title = "商品类团购商品保存", value = "/group_goods_self_save*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_goods_self_save" })
	public ModelAndView group_goods_self_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String goods_id, String gc_id, String ga_id, String gg_price) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		GroupGoods gg = null;
		if (id.equals("")) {
			gg = (GroupGoods) WebForm.toPo(request, GroupGoods.class);
			gg.setAddTime(new Date());
		} else {
			GroupGoods obj = this.groupGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			gg = (GroupGoods) WebForm.toPo(request, obj);
		}
		
		gg.setGg_recommend(true);
		
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
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		boolean wrire_vo = gg.getBeginTime().before(new Date());
		if (wrire_vo) {
			gg.setGg_status(1);
			goods.setGroup_buy(2);
			goods.setGoods_current_price(gg.getGg_price());
			goods.setGroup(gg.getGroup());
		} else {
			gg.setGg_status(2);
			goods.setGroup_buy(4);
		}
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		if (id.equals("")) {
			this.groupGoodsService.saveEntity(gg);
			if (wrire_vo) {
				this.goodsTools.addGroupGoodsLucene(gg);
			}
		} else {
			this.groupGoodsService.updateById(gg);
			if (wrire_vo) {
				this.goodsTools.updateGroupGoodsLucene(gg);
			}
		}
		this.goodsService.updateById(goods);
		mv.addObject("list_url", CommUtil.getURL(request) + "/group_self");
		if ((id != null) && (!id.equals(""))) {
			mv.addObject("op_title", "团购商品编辑成功");
		} else {
			mv.addObject("op_title", "团购商品申请成功");
			mv.addObject("add_url", CommUtil.getURL(request)
					+ "/group_self_add?type=goods");
		}
		return mv;
	}
	
	/**
	 * 生活类团购商品保存
	 * @param request
	 * @param response
	 * @param id
	 * @param group_id
	 * @param gc_id
	 * @param ga_id
	 * @param beginTime
	 * @param endTime
	 * @param group_price
	 * @param cost_price
	 * @param gai_id
	 * @param gg_phone
	 * @param gg_timerengestart
	 * @param gg_timerengeend
	 * @param gg_scope
	 * @param gg_rules
	 * @return
	 */
	@SecurityMapping(title = "生活类团购商品保存", value = "/group_lifegoods_self_save*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_lifegoods_self_save" })
	public ModelAndView group_lifegoods_self_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String group_id,
			String gc_id, String ga_id, String beginTime, String endTime,
			String group_price, String cost_price, String gai_id,
			String gg_phone, String gg_timerengestart, String gg_timerengeend,
			String gg_scope, String gg_rules) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		
		GroupLifeGoods grouplifegoods = null;
		if (id.equals("")) {
			grouplifegoods = (GroupLifeGoods) WebForm.toPo(request,
					GroupLifeGoods.class);
			grouplifegoods.setAddTime(new Date());
		} else {
			GroupLifeGoods obj = this.grouplifegoodsService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			grouplifegoods = (GroupLifeGoods) WebForm.toPo(request, obj);
		}
		grouplifegoods.setGoods_type(1);
		grouplifegoods.setGroup_status(1);
		GroupArea gg_ga = this.groupAreaService.selectByPrimaryKey(CommUtil
				.null2Long(ga_id));
		grouplifegoods.setGg_ga(gg_ga);
		GroupAreaInfo gai = this.groupAreaInfoService.selectByPrimaryKey(CommUtil
				.null2Long(gai_id));
		GroupClass gg_gc = this.groupClassService.selectByPrimaryKey(CommUtil
				.null2Long(gc_id));
		grouplifegoods.setGg_gc(gg_gc);
		grouplifegoods.setGg_gai(gai);
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
		if (id.equals("")) {
			this.grouplifegoodsService.saveEntity(grouplifegoods);
			this.goodsTools.addGroupLifeLucene(grouplifegoods);
		} else {
			this.grouplifegoodsService.updateById(grouplifegoods);
			this.goodsTools.updateGroupLifeLucene(grouplifegoods);
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/grouplife_self");
		if ((id != null) && (!id.equals(""))) {
			mv.addObject("op_title", "团购商品编辑成功");
		} else {
			mv.addObject("op_title", "团购商品申请成功");
			mv.addObject("add_url", CommUtil.getURL(request)
					+ "/group_self_add?type=life");
		}
		return mv;
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
	@SecurityMapping(title = "团购商品删除", value = "/group_self_del*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_self_del" })
	public String group_self_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		
		for (String id : ids) {
			
			GroupGoods gg = this.groupGoodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			Goods goods = gg.getGg_goods();
			if(goods != null){
				goods.setGroup_buy(0);
				goods.setGroup(null);
				goods.setGoods_current_price(goods.getStore_price());
				this.goodsService.updateById(goods);
				this.goodsTools.updateGoodsLucene(goods);
			}
			
			this.goodsTools.deleteGroupGoodsLucene(gg);
			RedPigCommonUtil.del_acc(request, gg.getGg_img());
			
			this.groupGoodsService.deleteById(CommUtil.null2Long(id));
		}
		return "redirect:/group_self?currentPage=" + currentPage;
	}
	
	/**
	 * 生活类团购商品删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "生活类团购商品删除", value = "/group_lifeself_del*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_lifeself_del" })
	public String group_lifeself_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {

				
//				String goods_lucene_path = ClusterSyncTools.getClusterRoot()
//						+ File.separator + "luence" + File.separator
//						+ "lifegoods";
//				File file = new File(goods_lucene_path);
//				if (!file.exists()) {
//					CommUtil.createFolder(goods_lucene_path);
//				}
//				RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
//				RedPigLuceneUtil.setIndex_path(goods_lucene_path);
//				lucene.delete_index(CommUtil.null2String(id));
				this.grouplifegoodsService.deleteById(Long.valueOf(Long.parseLong(id)));
				
			}
		}
		return "redirect:grouplife_self?currentPage=" + currentPage;
	}

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
			HttpServletResponse response, String endTime, String group_id) {
		boolean ret = false;
		Group group = this.groupService
				.selectByPrimaryKey(CommUtil.null2Long(group_id));
		Date date = CommUtil.formatDate(endTime);
		if (date.before(group.getEndTime())) {
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
	
	/**
	 * 自营生活类团购ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "自营生活类团购ajax更新", value = "/group_lifeself_ajax*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_lifeself_ajax" })
	public void group_lifeself_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		GroupLifeGoods obj = this.grouplifegoodsService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = GroupLifeGoods.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.grouplifegoodsService.updateById(obj);
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
	 * 自营生活类团购ajax更新
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 */
	@SecurityMapping(title = "自营生活类团购ajax更新", value = "/group_self_ajax*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/group_self_ajax" })
	public void group_self_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value)  {
		GroupGoods obj = this.groupGoodsService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = GroupGoods.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.groupGoodsService.updateById(obj);
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
	 * 生活购订单列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "生活购订单列表", value = "/grouplife_selforder*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/grouplife_selforder" })
	public ModelAndView grouplife_selforder(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id,
			String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/grouplife_selforder.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		params.put("order_form", 1);
		params.put("order_main", 0);
		params.put("order_cat", 2);
		
		if ((status != null) && (!status.equals(""))) {
			params.put("order_status", CommUtil.null2Int(status));
		}
		
		if (!CommUtil.null2String(order_id).equals("")) {
			
			params.put("order_id_like", order_id);
			mv.addObject("order_id", order_id);
		}
		
		mv.addObject("orderFormTools", this.orderFormTools);
		IPageList pList = this.orderFormService.list(params);
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
	 * @param status
	 * @param info_id
	 * @return
	 */
	@SecurityMapping(title = "生活购消费码列表", value = "/grouplife_selfinfo*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/grouplife_selfinfo" })
	public ModelAndView grouplife_selfinfo(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status,
			String info_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/grouplife_selfinfo.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, null, null);
		
		maps.put("lifeGoods_goods_type", 1);
		if (!CommUtil.null2String(info_id).equals("")) {
			
			maps.put("group_sn", info_id);
			mv.addObject("info_id", info_id);
		}
		if ((status != null) && (!status.equals(""))) {
			
			maps.put("status", CommUtil.null2Int(status));
		}
		
		IPageList pList = this.groupinfoService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/buyer/grouplife_selfinfo.html", "", params, pList, mv);
		mv.addObject("status", status);
		return mv;
	}
	
	/**
	 * 生活购订单取消
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "生活购订单取消", value = "/lifeorder_cancel*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/lifeorder_cancel" })
	public String lifeorder_cancel(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(id));
		of.setOrder_status(0);
		this.orderFormService.updateById(of);
		return "redirect:/grouplife_selforder";
	}
	
	/**
	 * 生活购订单详细
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @param info_id
	 * @param status
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "生活购订单详细", value = "/lifeorder_view*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/lifeorder_view" })
	public ModelAndView lifeorder_view(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id,
			String info_id, String status) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/lifeorder_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj != null) && (obj.getOrder_form() == 1)
				&& (obj.getOrder_status() == 20)) {
			Map json = this.orderFormTools.queryGroupInfo(obj.getGroup_info());
			String url = this.configService.getSysConfig().getAddress();
			if ((url == null) || (url.equals(""))) {
				url = CommUtil.getURL(request);
			}
			String params = "";
			Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, null, null);
			
			if ((status != null) && (!status.equals(""))) {
				
				maps.put("status", CommUtil.null2Int(status));
			}
			
			maps.put("order_id", obj.getId());
			
			maps.put("lifeGoods_id",  CommUtil.null2Long(json.get("goods_id").toString()));
			
			if ((info_id != null) && (!info_id.equals(""))) {
				
				maps.put("group_sn", info_id);
			}
			
			IPageList pList = this.groupinfoService.list(maps);
			CommUtil.saveIPageList2ModelAndView(url
					+ "/buyer/lifeorder_view.html", "", params, pList, mv);
			GroupLifeGoods goods = this.groupLifeGoodsService
					.selectByPrimaryKey(CommUtil.null2Long(json.get("goods_id")));
			mv.addObject("infos", pList.getResult());
			mv.addObject("order", obj);
			mv.addObject("goods", goods);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "订单编号错误");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/group");
		}
		return mv;
	}
	
	/**
	 * 团购码管理
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "团购码管理", value = "/group_code*", rtype = "admin", rname = "团购码验证", rcode = "group_self_code", rgroup = "自营")
	@RequestMapping({ "/group_code" })
	public ModelAndView group_code(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/group_code.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 生活购订单使用
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "生活购订单使用", value = "/use_lifeinfo*", rtype = "admin", rname = "团购管理", rcode = "group_self", rgroup = "自营")
	@RequestMapping({ "/use_lifeinfo" })
	public String use_lifeinfo(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		GroupInfo info = this.groupinfoService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((info != null) && (info.getStatus() == 0)) {
			info.setStatus(1);
			this.groupinfoService.updateById(info);
		}
		return "redirect:/grouplife_selforder";
	}
	
	/**
	 * 生活购订单使用
	 * @param request
	 * @param response
	 * @param value
	 */
	@SecurityMapping(title = "生活购订单使用", value = "/check_group_code*", rtype = "admin", rname = "团购码验证", rcode = "group_self_code", rgroup = "自营")
	@RequestMapping({ "/check_group_code" })
	public void check_group_code(HttpServletRequest request,
			HttpServletResponse response, String value) {
		String code = "0";
		
		GroupInfo info = this.groupinfoService.getObjByProperty("group_sn","=", value);
		
		
		if (info != null) {
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
				this.groupinfoService.updateById(info);
				code = "100";
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
}
