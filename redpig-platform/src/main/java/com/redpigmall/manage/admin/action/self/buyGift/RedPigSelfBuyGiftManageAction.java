package com.redpigmall.manage.admin.action.self.buyGift;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
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
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;

/**
 * 
 * <p>
 * Title: RedPigSelfBuyGiftManageAction.java
 * </p>
 * 
 * <p>
 * Description:自营满就送促销管理
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
 * @date 2014-9-23
 * 
 * @version redpigmall_b2b2c 2016
 */
@Controller
public class RedPigSelfBuyGiftManageAction extends BaseAction{
	/**
	 * 自营满就送列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param gift_status
	 * @param beginTime
	 * @param endTime
	 * @param store_name
	 * @return
	 */
	@SecurityMapping(title = "自营满就送列表", value = "/buygift_self_list*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping({ "/buygift_self_list" })
	public ModelAndView buygift_self_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String gift_status, String beginTime,
			String endTime, String store_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/buygift_self_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if ((gift_status != null) && (!gift_status.equals(""))) {
			
			maps.put("gift_status", CommUtil.null2Int(gift_status));
			mv.addObject("gift_status", gift_status);
		}
		if ((beginTime != null) && (!beginTime.equals(""))) {
			
			maps.put("beginTime_more_than_equal", CommUtil.formatDate(beginTime));
			mv.addObject("beginTime", beginTime);
		}
		if ((endTime != null) && (!endTime.equals(""))) {
			maps.put("beginTime_less_than_equal", CommUtil.formatDate(endTime));
			mv.addObject("endTime", endTime);
		}
		
		maps.put("gift_type", 0);
		
		IPageList pList = this.buygiftService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buygift_list.html",
				"", params, pList, mv);
		return mv;
	}
	
	/**
	 * 自营满就送添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营满就送添加", value = "/buygift_self_add*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping({ "/buygift_self_add" })
	public ModelAndView buygift_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/buygift_self_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 自营满就送商品
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自营满就送商品", value = "/buy_goods_self*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping({ "/buy_goods_self" })
	public ModelAndView buy_goods_self(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/buy_goods_self.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(RedPigMaps.newParent(-1, "sequence", "asc"));
		
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 自营满就送load
	 * @param request
	 * @param response
	 * @param goods_name
	 * @param gc_id
	 * @param goods_ids
	 */
	@SuppressWarnings({"rawtypes" })
	@SecurityMapping(title = "自营满就送load", value = "/buy_goods_self_load*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping({ "/buy_goods_self_load" })
	public void buy_goods_self_load(HttpServletRequest request,
			HttpServletResponse response, String goods_name, String gc_id,
			String goods_ids) {
		
		Map<String, Object> params = Maps.newHashMap();
		if(StringUtils.isNotBlank(goods_name)){
			params.put("goods_name_like", goods_name.trim());
		}
		params.put("goods_type", 0);

		this.queryTools.shildGoodsStatusParams(params);
		
		if ((gc_id != null) && (!gc_id.equals(""))) {
			GoodsClass gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			Set<Long> ids = genericGcIds(gc);
			params.put("gc_ids", ids);
			
		}
		String[] ids = goods_ids.split(",");
		List<String> ids_list = Arrays.asList(ids);
		List<Goods> goods = this.goodsService.queryPageList(params);
		
		List<Map> list = Lists.newArrayList();
		for (Goods obj : goods) {
			if (!ids_list.contains(obj.getId().toString())) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", obj.getId());
				map.put("store_price", obj.getStore_price());
				map.put("goods_name", obj.getGoods_name());
				map.put("store_inventory", Integer.valueOf(obj.getGoods_inventory()));
				if (obj.getGoods_main_photo() != null) {
					map.put("img", obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName() + "_small."
							+ obj.getGoods_main_photo().getExt());
				} else {
					map.put("img", this.configService.getSysConfig()
							.getGoodsImage().getPath()
							+ "/"
							+ this.configService.getSysConfig().getGoodsImage()
									.getName());
				}
				map.put("img",this.redPigSysConfigService.getSysConfig().getImageWebServer()+File.separator+map.get("img"));
				list.add(map);
			}
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
	 * 自营满就送保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param goods_ids
	 * @param gift_ids
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "自营满就送保存", value = "/buygift_self_save*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping({ "/buygift_self_save" })
	public ModelAndView buygift_self_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String goods_ids, String gift_ids) {
		
		String[] ids = goods_ids.split(",");
		String[] gids = gift_ids.split(",");
		Set ids_set = Sets.newTreeSet();
		ids_set.addAll(Arrays.asList(ids));
		Set gids_set = Sets.newTreeSet();
		gids_set.addAll(Arrays.asList(gids));
		BuyGift buygift = null;
		List<Goods> gift_goods = Lists.newArrayList();
		if (id.equals("")) {
			buygift = (BuyGift) WebForm.toPo(request, BuyGift.class);
			buygift.setAddTime(new Date());
		} else {
			BuyGift obj = this.buygiftService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			buygift = (BuyGift) WebForm.toPo(request, obj);
		}
		List<Map> goodses = Lists.newArrayList();
		Goods goods;
		for (Object goods_id : ids_set) {
			goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			gift_goods.add(goods);
			goods.setOrder_enough_give_status(1);
			goods.setBuyGift_amount(BigDecimal.valueOf(CommUtil
					.null2Double(request.getParameter("condition_amount"))));
			this.goodsService.updateById(goods);
			Map<String, Object> map = Maps.newHashMap();
			map.put("goods_id", goods.getId());
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName() + "_small."
					+ goods.getGoods_main_photo().getExt());
			map.put("big_goods_main_photo", goods.getGoods_main_photo()
					.getPath() + "/" + goods.getGoods_main_photo().getName());
			map.put("goods_price", goods.getGoods_current_price());
			String goods_domainPath = "items_" + goods.getId() + "";
			if ((this.configService.getSysConfig().getSecond_domain_open())
					&& (goods.getGoods_store().getStore_second_domain() != "")
					&& (goods.getGoods_type() == 1)) {
				String store_second_domain = "http://"
						+ goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/items_" + goods.getId() + "";
			}
			map.put("goods_domainPath", goods_domainPath);
			goodses.add(map);
		}
		buygift.setGoods_info(JSON.toJSONString(goodses));

		List<Map> gifts = Lists.newArrayList();
		for (Object gift_id : gids_set) {
			goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gift_id));
			gift_goods.add(goods);
			int count = CommUtil.null2Int(request.getParameter("gift_"
					+ goods.getId()));
			goods.setOrder_enough_if_give(1);
			Map<String, Object> map = Maps.newHashMap();
			if (count >= goods.getGoods_inventory()) {
				map.put("storegoods_count", Integer.valueOf(1));
			} else {
				map.put("storegoods_count", Integer.valueOf(0));
				map.put("goods_count", Integer.valueOf(count));
				goods.setGoods_inventory(goods.getGoods_inventory() - count);
			}
			map.put("goods_id", goods.getId());
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName() + "_small."
					+ goods.getGoods_main_photo().getExt());
			map.put("big_goods_main_photo", goods.getGoods_main_photo()
					.getPath() + "/" + goods.getGoods_main_photo().getName());
			map.put("goods_price", goods.getGoods_current_price());
			String goods_domainPath = "items_" + goods.getId() + "";
			if ((this.configService.getSysConfig().getSecond_domain_open())
					&& (goods.getGoods_store().getStore_second_domain() != "")
					&& (goods.getGoods_type() == 1)) {
				String store_second_domain = "http://"
						+ goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/items_" + goods.getId() + "";
			}
			map.put("goods_domainPath", goods_domainPath);
			goods.setBuyGift_amount(BigDecimal.valueOf(CommUtil.null2Double(request.getParameter("condition_amount"))));
			this.goodsService.updateById(goods);
			gifts.add(map);
		}
		buygift.setGift_info(JSON.toJSONString(gifts));
		buygift.setGift_status(10);
		if (id.equals("")) {
			this.buygiftService.saveEntity(buygift);
		} else {
			this.buygiftService.updateById(buygift);
		}
		for (Goods g : gift_goods) {
			g.setBuyGift_id(buygift.getId());
			this.goodsService.updateById(g);
		}
		System.out.println(buygift.getId());
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/buygift_self_list");
		mv.addObject("op_title", "保存满就送成功");
		return (ModelAndView) mv;
	}
	
	/**
	 * 自营满就送商品
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自营满就送商品", value = "/buy_gift_self*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping({ "/buy_gift_self" })
	public ModelAndView buy_gift_self(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/buy_gift_self.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(RedPigMaps.newParent(-1, "sequence", "asc"));
		mv.addObject("gcs", gcs);
		return mv;
	}
	
	/**
	 * 自营满就送详情
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营满就送详情", value = "/buygift_self_info*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping({ "/buygift_self_info" })
	public ModelAndView buygift_self_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/buygift_self_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			BuyGift buygift = this.buygiftService.selectByPrimaryKey(Long.valueOf(Long
					.parseLong(id)));
			mv.addObject("obj", buygift);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 自营满就送停止
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "自营满就送停止", value = "/buygift_stop*", rtype = "admin", rname = "满就送管理", rcode = "buygift_self_manage", rgroup = "自营")
	@RequestMapping({ "/buygift_stop" })
	public ModelAndView buygift_stop(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		BuyGift bg = this.buygiftService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((bg != null) && (bg.getGift_type() == 0)
				&& (bg.getGift_status() == 10)) {
			bg.setGift_status(20);
			List<Map> maps = JSON.parseArray(bg.getGift_info(), Map.class);
			maps.addAll(JSON.parseArray(bg.getGoods_info(), Map.class));
			for (Map map : maps) {
				Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
						.null2Long(map.get("goods_id")));
				if (goods != null) {
					goods.setOrder_enough_give_status(0);
					goods.setOrder_enough_if_give(0);
					goods.setBuyGift_id(null);
					goods.setBuyGift_amount(new BigDecimal(0.0D));
					this.goodsService.updateById(goods);
				}
			}
			this.buygiftService.updateById(bg);
		}
		mv.addObject("list_url", CommUtil.getURL(request)
				+ "/buygift_self_list");
		mv.addObject("op_title", "停止满就送成功");
		return mv;
	}

	@RequestMapping({ "/gift_count_adjust" })
	public void gift_count_adjust(HttpServletRequest request,
			HttpServletResponse response, String gid, String count) {
		String code = "100";
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
		if ((goods != null)
				&& (CommUtil.null2Int(count) > goods.getGoods_inventory())) {
			count = goods.getGoods_inventory() + "";
			code = "200";
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("count", count);
		map.put("code", code);
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
