package com.redpigmall.manage.admin.action.self.combin;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.CombinPlan;
import com.redpigmall.domain.Goods;

/**
 * 
 * <p>
 * Title: RedPigSelfCombinManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台自营组合销售管理控制器
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
 * @date 2014-10-15
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSelfCombinManageAction extends BaseAction{
	/**
	 * 组合销售商品列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param type
	 * @param combin_status
	 * @param goods_name
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "组合销售商品列表", value = "/self_combin*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_combin" })
	public ModelAndView combin(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String combin_status, String goods_name, String beginTime,
			String endTime) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_combin_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, 10,"addTime", "desc");
		
		params.put("combin_form", 0);
		if ((type != null) && (!type.equals(""))) {
			
			params.put("combin_type", CommUtil.null2Int(type));
			mv.addObject("type", type);
		} else {
			
			params.put("combin_type", 0);
		}
		if ((combin_status != null) && (!combin_status.equals(""))) {
			
			params.put("combin_status", CommUtil.null2Int(combin_status));
			mv.addObject("combin_status", combin_status);
		}
		if ((goods_name != null) && (!goods_name.equals(""))) {
			
			params.put("main_goods_name_like", CommUtil.null2String(goods_name));
			mv.addObject("goods_name", goods_name);
		}
		if ((beginTime != null) && (!beginTime.equals(""))) {
			
			params.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		if ((endTime != null) && (!endTime.equals(""))) {
			
			params.put("add_Time_less_than_equal", CommUtil.formatDate(endTime));
			mv.addObject("endTime", endTime);
		}
		
		IPageList pList = this.combinplanService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("combinTools", this.combinTools);
		return mv;
	}
	
	/**
	 * 组合销售商品添加
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "组合销售商品添加", value = "/self_combin_add*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_combin_add" })
	public ModelAndView combin_add(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/self_combin_add.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Date now = new Date();
		mv.addObject("now", CommUtil.formatShortDate(now));
		return mv;
	}
	
	/**
	 * 验证商品两个组合类型是否存在
	 * @param request
	 * @param response
	 * @param gid
	 * @param combin_mark
	 * @param id
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = " 验证商品两个组合类型是否存在", value = "/self_verify_combin*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_verify_combin" })
	public void verify_combin(HttpServletRequest request,
			HttpServletResponse response, String gid, String combin_mark,
			String id) {
		boolean ret = true;
		String code = "参数错误";
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
		if (goods == null) {
			ret = false;
			code = "主体商品信息错误";
		}
		if ((combin_mark == null) || (combin_mark.equals(""))) {
			ret = false;
			code = "参数错误";
		} else if ((!combin_mark.equals("0")) && (!combin_mark.equals("1"))) {
			ret = false;
			code = "参数错误";
		}
		if (ret) {
			if (combin_mark.equals("0")) {
				if ((goods.getCombin_suit_id() != null) && (id.equals(""))) {
					ret = false;
					code = "该主商品已经存在组合套餐，请先将存在的组合套餐删除掉再添加新的组合套餐";
				}
			} else if ((goods.getCombin_parts_id() != null) && (id.equals(""))) {
				ret = false;
				code = "该主商品已经存在组合配件，请先将存在的组合配件删除掉再添加新的组合配件";
			}
		}
		Map json_map = Maps.newHashMap();
		json_map.put("ret", Boolean.valueOf(ret));
		json_map.put("code", code);
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");

			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 组合销售添加时获得商品全部价格
	 * @param request
	 * @param response
	 * @param other_ids
	 * @param main_goods_id
	 */
	@SecurityMapping(title = "组合销售添加时获得商品全部价格", value = "/self_getPrice*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_getPrice" })
	public void combin_getPrice(HttpServletRequest request,
			HttpServletResponse response, String other_ids, String main_goods_id) {
		double all_price = 0.0D;
		if (!main_goods_id.equals("")) {
			Goods main = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(main_goods_id));
			all_price = CommUtil.null2Double(main.getGoods_current_price());
		}
		if ((other_ids != null) && (!other_ids.equals(""))) {
			String[] ids = other_ids.split(",");

			for (String id : ids) {

				if (!id.equals("")) {
					Goods other = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(id));

					all_price = all_price
							+ CommUtil.null2Double(other
									.getGoods_current_price());
				}
			}
		}
		try {
			response.setContentType("text/plain");
			response.setHeader("Cache-Control", "no-cache");
			response.setCharacterEncoding("UTF-8");

			PrintWriter writer = response.getWriter();
			writer.print(all_price);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 组合套餐设置
	 * @param request
	 * @param response
	 * @param type
	 * @param plan_count
	 * @return
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/self_combin_set_items*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_combin_set_items" })
	public ModelAndView combin_set_goods(HttpServletRequest request,
			HttpServletResponse response, String type, String plan_count) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_combin_set_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("type", type);
		mv.addObject("plan_count", plan_count);
		return mv;
	}
	
	/**
	 * 组合套餐设置
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param currentPage
	 * @param type
	 * @param plan_count
	 * @return
	 */
	@SecurityMapping(title = "组合套餐设置", value = "/self_combin_set_goods_load*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_combin_set_goods_load" })
	public ModelAndView combin_set_goods_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name,
			String currentPage, String type, String plan_count) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/self_combin_set_goods_load.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, 14,null, null);
		
		
		if (!CommUtil.null2String(goods_name).equals("")) {
			
			maps.put("goods_name_like", goods_name);
		}
		
		maps.put("combin_suit_id1", -1);
		maps.put("combin_parts_id2", -1);
		
		List<String> str_list = Lists.newArrayList();
		str_list.add("combin_status");
		
		this.queryTools.shieldGoodsStatus(maps, str_list);
		
		maps.put("goods_type", 0);
		IPageList pList = this.goodsService.list(maps);
		String url = CommUtil.getURL(request)
				+ "/self_combin_set_goods_load";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		mv.addObject("type", type);
		if ((plan_count == null) || (plan_count.equals(""))) {
			plan_count = "1";
		}
		mv.addObject("plan_count", plan_count);
		
		return mv;
	}
	
	/**
	 * 组合套餐设置
	 * @param request
	 * @param response
	 * @param plan_num
	 * @param main_goods_id
	 * @param old_main_goods_id
	 * @param beginTime
	 * @param endTime
	 * @param id
	 * @param combin_mark
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "组合套餐设置", value = "/self_combin_plan_save*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_combin_plan_save" })
	public ModelAndView combin_plan_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String plan_num,
			String main_goods_id, String old_main_goods_id, String beginTime,
			String endTime, String id, String combin_mark) {
		if ((old_main_goods_id != null) && (!old_main_goods_id.equals(""))) {
			Goods old_main_goods = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(old_main_goods_id));
			old_main_goods.setCombin_parts_id(null);
			old_main_goods.setCombin_suit_id(null);
			old_main_goods.setCombin_status(0);
			this.goodsService.updateById(old_main_goods);
		}
		double all_price = 0.0D;
		Goods main_goods = this.goodsService.selectByPrimaryKey(CommUtil
				.null2Long(main_goods_id));
		CombinPlan combinplan = null;
		if ((id != null) && (!id.equals(""))) {
			combinplan = this.combinplanService.selectByPrimaryKey(CommUtil
					.null2Long(id));
		} else {
			combinplan = new CombinPlan();
			combinplan.setAddTime(new Date());
			combinplan.setCombin_type(CommUtil.null2Int(combin_mark));
		}
		Map<String,Object> main_map = Maps.newHashMap();
		main_map.put("id", main_goods.getId());
		main_map.put("name", main_goods.getGoods_name());
		main_map.put("price", main_goods.getGoods_current_price());
		main_map.put("store_price", main_goods.getStore_price());
		main_map.put("inventory",
				Integer.valueOf(main_goods.getGoods_inventory()));
		String goods_domainPath = CommUtil.getURL(request) + "/items_"
				+ main_goods.getId() + "";
		main_map.put("url", goods_domainPath);
		String img = CommUtil.getURL(request) + "/"
				+ this.configService.getSysConfig().getGoodsImage().getPath()
				+ "/"
				+ this.configService.getSysConfig().getGoodsImage().getName();
		if (main_goods.getGoods_main_photo() != null) {
			img =

			main_goods.getGoods_main_photo().getPath() + "/"
					+ main_goods.getGoods_main_photo().getName() + "_small."
					+ main_goods.getGoods_main_photo().getExt();
		}
		main_map.put("img", img);
		combinplan.setMain_goods_info(JSON.toJSONString(main_map));
		combinplan.setMain_goods_id(main_goods.getId());
		combinplan.setMain_goods_name(main_goods.getGoods_name());
		List plan_list = Lists.newArrayList();
		String[] nums = plan_num.split(",");

		for (String count : nums) {

			all_price = CommUtil.null2Double(main_goods
					.getGoods_current_price());
			if (!count.equals("")) {
				String other_goods_ids = request
						.getParameter("other_goods_ids_" + count);
				String[] other_ids = other_goods_ids.split(",");
				List goods_list = Lists.newArrayList();
				for (String other_id : other_ids) {

					if (!other_id.equals("")) {
						Goods obj = this.goodsService.selectByPrimaryKey(CommUtil
								.null2Long(other_id));

						all_price = all_price
								+ CommUtil.null2Double(obj
										.getGoods_current_price());
						Map<String,Object> temp_map = Maps.newHashMap();
						temp_map.put("id", obj.getId());
						temp_map.put("name", obj.getGoods_name());
						temp_map.put("price", obj.getGoods_current_price());
						temp_map.put("store_price", obj.getStore_price());
						temp_map.put("inventory",
								Integer.valueOf(obj.getGoods_inventory()));
						String goods_url = CommUtil.getURL(request) + "/items_"
								+ obj.getId() + "";
						temp_map.put("url", goods_url);
						String img2 = this.configService.getSysConfig()
								.getGoodsImage().getPath()
								+ "/"
								+ this.configService.getSysConfig()
										.getGoodsImage().getName();
						if (obj.getGoods_main_photo() != null) {
							img2 =

							obj.getGoods_main_photo().getPath() + "/"
									+ obj.getGoods_main_photo().getName()
									+ "_small."
									+ obj.getGoods_main_photo().getExt();
						}
						temp_map.put("img", img2);
						goods_list.add(temp_map);
					}
				}
				Map combin_goods_map = Maps.newHashMap();
				combin_goods_map.put("goods_list", goods_list);
				combin_goods_map.put("plan_goods_price",
						request.getParameter("combin_price_" + count));
				combin_goods_map.put("all_goods_price",
						CommUtil.formatMoney(Double.valueOf(all_price)));
				plan_list.add(combin_goods_map);
			}
		}
		String plan_list_json = JSON.toJSONString(plan_list);
		combinplan.setCombin_plan_info(plan_list_json);
		combinplan.setCombin_status(1);
		combinplan.setBeginTime(CommUtil.formatDate(beginTime));
		combinplan.setEndTime(CommUtil.formatDate(endTime));
		combinplan.setCombin_form(0);
		if ((id != null) && (!id.equals(""))) {
			this.combinplanService.updateById(combinplan);
		} else {
			this.combinplanService.saveEntity(combinplan);
		}
		main_goods.setCombin_status(1);
		if (combinplan.getCombin_type() == 0) {
			main_goods.setCombin_suit_id(combinplan.getId());
		} else {
			main_goods.setCombin_parts_id(combinplan.getId());
		}
		this.goodsService.updateById(main_goods);
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/self_combin");
		mv.addObject("add_url", CommUtil.getURL(request)
				+ "/self_combin_add");
		mv.addObject("op_title", "组合销售添加成功");
		return (ModelAndView) mv;
	}
	
	/**
	 * 组合销售商品编辑
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "组合销售商品编辑", value = "/self_combin_plan_edit*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_combin_plan_edit" })
	public ModelAndView combin_plan_edit(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/self_combin_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		CombinPlan obj = this.combinplanService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		mv.addObject("edit", Boolean.valueOf(true));
		mv.addObject("obj", obj);
		mv.addObject("combinTools", this.combinTools);
		return mv;
	}
	
	/**
	 * 组合销售删除
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "组合销售删除", value = "/self_combin_plan_delete*", rtype = "admin", rname = "组合销售", rcode = "self_combin", rgroup = "自营")
	@RequestMapping({ "/self_combin_plan_delete" })
	public String combin_plan_delete(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String type) {
		if ((id != null) && (!id.equals(""))) {
			CombinPlan obj = this.combinplanService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if (obj != null) {
				Goods goods = this.goodsService.selectByPrimaryKey(obj.getMain_goods_id());
				if(goods!=null){
					goods.setCombin_status(0);
					if ((obj.getCombin_type() == 0)
							&& (goods.getCombin_suit_id() != null)) {
						if (goods.getCombin_suit_id().equals(obj.getId())) {
							goods.setCombin_suit_id(null);
						}
					} else if ((obj.getCombin_type() == 1)
							&& (goods.getCombin_parts_id() != null)
							&& (goods.getCombin_parts_id().equals(obj.getId()))) {
						goods.setCombin_parts_id(null);
					}
					this.goodsService.updateById(goods);
				}
				boolean ret = true;
				if (ret) {
					this.combinplanService.deleteById(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:/self_combin?currentPage=" + currentPage
				+ "&type=" + type;
	}
}
