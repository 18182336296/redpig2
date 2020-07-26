package com.redpigmall.manage.admin.action.self.freeGoods;

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
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.FreeClass;
import com.redpigmall.domain.FreeGoods;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.ShipAddress;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigSelfFreeGoodsManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营0元试用商品
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
 * @date 2014年11月12日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigSelfFreeGoodsManageAction extends BaseAction {
	/**
	 * 自营0元试用商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param free_name
	 * @param beginTime
	 * @param endTime
	 * @param cls
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "自营0元试用商品列表", value = "/self_freegoods_list*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/self_freegoods_list" })
	public ModelAndView self_freegoods_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String free_name, String beginTime,
			String endTime, String cls, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_freegoods_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		maps.put("freeType", 1);
		if ((free_name != null) && (!free_name.equals(""))) {
			maps.put("free_name_like", free_name);
			mv.addObject("free_name", free_name);
		}
		if ((cls != null) && (!cls.equals(""))) {
			
			maps.put("class_id", CommUtil.null2Long(cls));
			mv.addObject("cls_id", cls);
		}
		if ((status != null) && (status.equals("going"))) {
			
			maps.put("freeStatus", 5);
			mv.addObject("status", status);
		}
		if ((status != null) && (status.equals("finish"))) {
			
			maps.put("freeStatus", 10);
			mv.addObject("status", status);
		}
		if ((beginTime != null) && (!beginTime.equals(""))) {
			
			maps.put("beginTime_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		if ((endTime != null) && (!endTime.equals(""))) {
			
			maps.put("beginTime_less_than_equal", CommUtil.formatDate(endTime));
			mv.addObject("endTime", endTime);
		}
		
		IPageList pList = this.freegoodsService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/self_freegoods_list.html", "", params, pList, mv);
		
		List<FreeClass> fcls = this.freeClassService.queryPageList(RedPigMaps.newMap());
		
		mv.addObject("fcls", fcls);
		mv.addObject("freeTools", this.freeTools);
		return mv;
	}
	
	/**
	 * 0元试用商品添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品添加", value = "/self_freegoods_add*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/self_freegoods_add" })
	public ModelAndView self_freegoods_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_freegoods_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		
		List<FreeClass> cls = this.freeClassService.queryPageList(RedPigMaps.newMap());
		
		mv.addObject("cls", cls);
		return mv;
	}
	
	/**
	 * 0元试用商品编辑
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品编辑", value = "/self_freegoods_edit*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/self_freegoods_edit" })
	public ModelAndView self_freegoods_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_freegoods_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			FreeGoods freegoods = this.freegoodsService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			
			List<FreeClass> cls = this.freeClassService.queryPageList(RedPigMaps.newMap());
			
			mv.addObject("cls", cls);
			if (freegoods != null) {
				Goods goods = this.goodsService.selectByPrimaryKey(freegoods
						.getClass_id());
				mv.addObject("goods", goods);
			}
			mv.addObject("obj", freegoods);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 0元试用商品保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param class_id
	 * @param goods_id
	 * @param default_count
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品保存", value = "/self_freegoods_save*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/self_freegoods_save" })
	public ModelAndView self_freegoods_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String class_id,
			String goods_id, String default_count) {
		
		FreeGoods freegoods = null;
		if (id.equals("")) {
			freegoods = (FreeGoods) WebForm.toPo(request, FreeGoods.class);
			freegoods.setAddTime(new Date());
		} else {
			FreeGoods obj = this.freegoodsService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			freegoods = (FreeGoods) WebForm.toPo(request, obj);
		}
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "free";
		Map<String, Object> map = Maps.newHashMap();
		try {
			String fileName = freegoods.getFree_acc() == null ? "" : freegoods
					.getFree_acc().getName();
			map = CommUtil.saveFileToServer(request, "free_acc",
					saveFilePathName, fileName, null);
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory free_acc = new Accessory();
					free_acc.setName(CommUtil.null2String(map.get("fileName")));
					free_acc.setExt(CommUtil.null2String(map.get("mime")));
					free_acc.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					free_acc.setPath(uploadFilePath + "/free");
					free_acc.setWidth(CommUtil.null2Int(map.get("width")));
					free_acc.setHeight(CommUtil.null2Int(map.get("height")));
					free_acc.setAddTime(new Date());
					this.accessoryService.saveEntity(free_acc);
					freegoods.setFree_acc(free_acc);
				}
			} else if (map.get("fileName") != "") {
				Accessory free_acc = freegoods.getFree_acc();
				free_acc.setName(CommUtil.null2String(map.get("fileName")));
				free_acc.setExt(CommUtil.null2String(map.get("mime")));
				free_acc.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				free_acc.setPath(uploadFilePath + "/free");
				free_acc.setWidth(CommUtil.null2Int(map.get("width")));
				free_acc.setHeight(CommUtil.null2Int(map.get("height")));
				free_acc.setAddTime(new Date());
				this.accessoryService.updateById(free_acc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		freegoods.setClass_id(CommUtil.null2Long(class_id));
		freegoods.setCurrent_count(CommUtil.null2Int(default_count));
		freegoods.setDefault_count(CommUtil.null2Int(default_count));
		Goods goods = this.goodsService
				.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		freegoods.setGoods_id(CommUtil.null2Long(goods_id));
		if (goods != null) {
			freegoods.setGoods_name(goods.getGoods_name());
			goods.setWhether_free(1);
			this.goodsService.updateById(goods);
		}
		freegoods.setFreeType(1);
		freegoods.setFreeStatus(5);
		if (id.equals("")) {
			this.freegoodsService.saveEntity(freegoods);
		} else {
			this.freegoodsService.updateById(freegoods);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/self_freegoods_list");
		mv.addObject("op_title", "保存0元试用成功");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/self_freegoods_add" + "?currentPage=" + currentPage);
		return mv;
	}
	
	/**
	 * 0元试用商品删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "0元试用商品删除", value = "/self_freegoods_del*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/self_freegoods_del" })
	public String self_freegoods_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				FreeGoods freegoods = this.freegoodsService.selectByPrimaryKey(Long
						.valueOf(Long.parseLong(id)));
				if ((freegoods != null) && (freegoods.getFreeStatus() != 5)
						&& (freegoods.getFreeType() == 1)) {
					Goods goods = this.goodsService.selectByPrimaryKey(freegoods.getGoods_id());
					if(goods!=null){
						goods.setWhether_free(0);
						this.goodsService.updateById(goods);
					}
					this.freegoodsService.deleteById(Long.valueOf(Long.parseLong(id)));
					
				}
			}
		}
		return "redirect:self_freegoods_list?currentPage=" + currentPage;
	}
	
	/**
	 * 0元试用申请记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param id
	 * @param userName
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "0元试用申请记录", value = "/self_freeapply_logs*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/self_freeapply_logs" })
	public ModelAndView self_freeapply_logs(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String id, String userName, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_freeapplylog_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("freegoods_id", CommUtil.null2Long(id));
		if ((userName != null) && (!userName.equals(""))) {
			
			maps.put("user_name_like", userName);
			
			mv.addObject("userName", userName);
		}
		if ((status != null) && (status.equals("yes"))) {
			
			maps.put("apply_status", 5);
			mv.addObject("status", status);
		}
		if ((status != null) && (status.equals("waiting"))) {
			
			maps.put("apply_status", 0);
			mv.addObject("status", status);
		}
		if ((status != null) && (status.equals("no"))) {
			
			maps.put("apply_status", -5);
			mv.addObject("status", status);
		}
		
		IPageList pList = this.freeapplylogService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/self_freeapply_logs.html", "", params, pList, mv);
		mv.addObject("id", id);
		return mv;
	}
	
	/**
	 * 申请记录详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "申请记录详情", value = "/self_apply_log_info*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/self_apply_log_info" })
	public ModelAndView self_apply_log_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_freeapplylog_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			FreeApplyLog freeapplylog = this.freeapplylogService
					.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
			Map<String, Object> params = Maps.newHashMap();
			params.put("company_status", 0);
			params.put("orderBy", "company_sequence");
			params.put("orderType", "asc");
			
			List<ExpressCompany> expressCompanys = this.expressCompayService.queryPageList(params);
			
			params.clear();
			params.put("sa_type", 1);
			
			List<ShipAddress> shipAddrs = this.shipAddressService.queryPageList(params);
			mv.addObject("shipAddrs", shipAddrs);
			mv.addObject("expressCompanys", expressCompanys);
			mv.addObject("obj", freeapplylog);
		}
		return mv;
	}
	
	/**
	 * 申请记录详情保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param status
	 * @param shipCode
	 * @param ec_id
	 * @param sa_id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "申请记录详情保存", value = "/self_apply_log_save*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/self_apply_log_save" })
	public ModelAndView self_apply_log_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String status, String shipCode, String ec_id, String sa_id) {
		
		FreeApplyLog freeapplylog = null;
		if (id.equals("")) {
			freeapplylog = (FreeApplyLog) WebForm.toPo(request,
					FreeApplyLog.class);
			freeapplylog.setAddTime(new Date());
		} else {
			FreeApplyLog obj = this.freeapplylogService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			freeapplylog = (FreeApplyLog) WebForm.toPo(request, obj);
		}
		if (status.equals("yes")) {
			ExpressCompany ec = this.expressCompayService.selectByPrimaryKey(CommUtil
					.null2Long(ec_id));
			ShipAddress sa = this.shipAddressService.selectByPrimaryKey(CommUtil
					.null2Long(sa_id));
			Area area = this.areaService.selectByPrimaryKey(sa.getSa_area_id());
			freeapplylog.setShip_addr(area.getParent().getParent()
					.getAreaName()
					+ area.getParent().getAreaName()
					+ area.getAreaName()
					+ sa.getSa_addr());
			freeapplylog.setShip_addr_id(sa.getId());
			freeapplylog.setShipCode(shipCode);
			Map json_map = Maps.newHashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			freeapplylog.setExpress_info(JSON.toJSONString(json_map));
			freeapplylog.setApply_status(5);
			FreeGoods fg = this.freegoodsService.selectByPrimaryKey(freeapplylog
					.getFreegoods_id());
			int count = fg.getCurrent_count() - 1;
			fg.setCurrent_count(count);
			if (count <= 0) {
				fg.setFreeStatus(10);
				this.freegoodsService.updateById(fg);
			}
			String msg_content = "您申请的0元试用：" + freeapplylog.getFreegoods_name()
					+ "已通过审核。";

			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			User buyer = this.userService.selectByPrimaryKey(freeapplylog.getUser_id());
			msg.setToUser(buyer);
			this.messageService.saveEntity(msg);
		} else {
			freeapplylog.setApply_status(-5);
			freeapplylog.setEvaluate_status(2);
			String msg_content = "您申请的0元试用：" + freeapplylog.getFreegoods_name()
					+ "未过审核。";

			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			User buyer = this.userService.selectByPrimaryKey(freeapplylog.getUser_id());
			msg.setToUser(buyer);
			this.messageService.saveEntity(msg);
		}
		if (id.equals("")) {
			this.freeapplylogService.saveEntity(freeapplylog);
		}
		this.freeapplylogService.updateById(freeapplylog);
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		mv.addObject("list_url",
				CommUtil.getURL(request) + "/self_freeapply_logs?id="
						+ freeapplylog.getFreegoods_id());
		mv.addObject("op_title", "审核申请成功");
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@RequestMapping({ "/self_freegoods_ajax" })
	public void freegoods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		FreeGoods obj = this.freegoodsService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		Field[] fields = FreeGoods.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.freegoodsService.updateById(obj);
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
	 * 自营0元试用商品
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自营0元试用商品", value = "/free_goods_self*", rtype = "admin", rname = "0元试用管理", rcode = "self_freegoods_admin", rgroup = "自营")
	@RequestMapping({ "/free_goods_self" })
	public ModelAndView free_goods_self(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/free_goods_self.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(RedPigMaps.newParent(-1, "sequence", "asc"));
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	@SuppressWarnings({ "rawtypes", "unused" })
	@RequestMapping({ "/free_goods_self_load" })
	public void free_goods_self_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_name_like", goods_name.trim());
		params.put("goods_type", 0);
		
		this.queryTools.shildGoodsStatusParams(params);
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			Set<Long> ids = genericGcIds(gc);
			
			params.put("gc_ids", ids);
		}
		
		List<Goods> goods = this.goodsService.queryPageList(params);
		
		List<Map> list = Lists.newArrayList();
		for (Goods obj : goods) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", obj.getId());
			map.put("store_price", obj.getStore_price());
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
}
