package com.redpigmall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
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
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Consult;
import com.redpigmall.domain.ConsultSatis;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsTypeProperty;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.virtual.GoodsCompareView;
import com.redpigmall.view.web.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigGoodsViewAction.java
 * </p>
 * 
 * <p>
 * Description: 商品前台控制器,用来显示商品列表、商品详情、商品其他信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012-2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2014-4-28
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Controller
public class RedPigGoodsView4Action extends BaseAction{
	
	@RequestMapping({ "/goods_fal" })
	public ModelAndView goods_fal(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_fal.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10, "addTime", "desc");
		maps.put("freegoods_id", CommUtil.null2Long(goods_id));
		
		IPageList pList = this.freeApplyLogService.list(maps);
		String url = CommUtil.getURL(request) + "/goods_fal";
		mv.addObject("fal_objs", pList.getResult());
		mv.addObject("fal_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		mv.addObject("evaluateViewTools", this.evaluateViewTools);
		return mv;
	}
	
	/**
	 * 商品详情
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping({ "/goods_detail" })
	public ModelAndView goods_detail(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_detail.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/goods_order" })
	public ModelAndView goods_order(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_order.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10,"addTime", "desc");
		maps.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		
		IPageList order_eva_pList = this.evaluateService.list(maps);
		String url = CommUtil.getURL(request) + "/goods_order";
		mv.addObject("order_objs", order_eva_pList.getResult());
		mv.addObject(
				"order_gotoPageAjaxHTML",
				CommUtil.showPageAjaxHtml(url, "",
						order_eva_pList.getCurrentPage(),
						order_eva_pList.getPages(),
						order_eva_pList.getPageSize()));

		return mv;
	}

	@RequestMapping({ "/goods_consult" })
	public ModelAndView goods_consult(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String consult_type,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_consult.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10,"addTime", "desc");
		
		maps.put("goods_id", CommUtil.null2Long(goods_id));
		
		if (!CommUtil.null2String(consult_type).equals("")) {
			maps.put("consult_type", consult_type);
		}
		
		IPageList pList = this.consultService.list(maps);
		String url2 = CommUtil.getURL(request) + "/goods_consult";
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consult_gotoPageAjaxHTML", 
				CommUtil.showPageAjaxHtml(url2, "", pList.getCurrentPage(), pList.getPages(),pList.getPageSize()));
		
		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;
	}

	@RequestMapping({ "/goods_inventory" })
	public void goods_inventory(HttpServletRequest request,
			HttpServletResponse response, String city_name, String goods_id,
			String city_id, String gsp) {
		Map<String, Object> map = Maps.newHashMap();
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		
		int inventory = this.inventoryService.queryGoodsInventory(goods_id,city_id, gsp);
		
		this.areaViewTools.setDefaultArea(request, city_id);
		float mail_fee = 0.0F;
		float express_fee = 0.0F;
		float ems_fee = 0.0F;
		if ((goods != null) && (goods.getTransport() != null)) {
			mail_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "mail",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			express_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()),
					"express", CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
			ems_fee = this.transportTools.cal_goods_trans_fee(
					CommUtil.null2String(goods.getTransport().getId()), "ems",
					CommUtil.null2String(goods.getGoods_weight()),
					CommUtil.null2String(goods.getGoods_volume()), city_name);
		}
		map.put("inventory", Integer.valueOf(inventory));
		map.put("mail_fee", Float.valueOf(mail_fee));
		map.put("express_fee", Float.valueOf(express_fee));
		map.put("ems_fee", Float.valueOf(ems_fee));
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

	@RequestMapping({ "/goods_share" })
	public ModelAndView goods_share(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView("goods_share.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		mv.addObject("obj", goods);
		return mv;
	}

	private Set<Long> genericIds(Long id) {
		Set<Long> ids = new HashSet();
		if (id != null) {
			ids.add(id);
			Map<String, Object> params = Maps.newHashMap();
			params.put("parent", id);
			
			List id_list = this.goodsClassService.queryPageList(params);
			
			ids.addAll(id_list);
			for (int i = 0; i < id_list.size(); i++) {
				Long cid = CommUtil.null2Long(id_list.get(i));
				Set<Long> cids = genericIds(cid);
				ids.add(cid);
				ids.addAll(cids);
			}
		}
		return ids;
	}

	@RequestMapping({ "/goods_consult_win" })
	public ModelAndView goods_consult_win(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView("default/goods_consult_win.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("goods_id", goods_id);
		mv.addObject("time", CommUtil.formatLongDate(new Date()));
		return mv;
	}

	@RequestMapping({ "/goods_consult_save" })
	public ModelAndView goods_consult_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String goods_id,
			String consult_content, String consult_type, String consult_code) {
		String verify_code = CommUtil.null2String(request.getSession(false)
				.getAttribute("verify_code"));
		boolean visit_consult = true;
		
		if ((!this.configService.getSysConfig().getVisitorConsult())
				&& (SecurityUserHolder.getCurrentUser() == null)) {
			visit_consult = false;
		}
		
		boolean save_ret = true;
		if ((visit_consult)
				&& (CommUtil.null2String(consult_code).equals(verify_code))) {
			Consult obj = new Consult();
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			obj.setAddTime(new Date());
			obj.setConsult_type(consult_type);
			obj.setConsult_content(consult_content);
			
			User user = SecurityUserHolder.getCurrentUser();
			if (user != null) {
				obj.setConsult_user_id(user.getId());
				obj.setConsult_user_name(user.getUserName());
				obj.setConsult_email(user.getEmail());
			} else {
				obj.setConsult_user_name("游客");
			}
			
			List<Map> maps = Lists.newArrayList();
			Map<String, Object> map = Maps.newHashMap();
			map.put("goods_id", goods.getId());
			map.put("goods_name", goods.getGoods_name());
			map.put("goods_main_photo", goods.getGoods_main_photo().getPath()
					+ "/" + goods.getGoods_main_photo().getName() + "_small."
					+ goods.getGoods_main_photo().getExt());
			
			map.put("goods_price", goods.getGoods_current_price());
			
			String goods_domainPath = CommUtil.getURL(request) + "/items_" + goods.getId() + "";
			if ((this.configService.getSysConfig().getSecond_domain_open())
					&& (goods.getGoods_store() != null)
					&& (goods.getGoods_store().getStore_second_domain() != "")
					&& (goods.getGoods_type() == 1)) {
				String store_second_domain = "http://"
						+ goods.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request);
				goods_domainPath = store_second_domain + "/items_"
						+ goods.getId() + "";
			}
			map.put("goods_domainPath", goods_domainPath);
			maps.add(map);
			obj.setGoods_info(JSON.toJSONString(maps));
			obj.setGoods_id(goods.getId());
			if (goods.getGoods_store() != null) {
				obj.setStore_id(goods.getGoods_store().getId());
				obj.setStore_name(goods.getGoods_store().getStore_name());
			} else {
				obj.setWhether_self(1);
			}
			
			this.consultService.saveEntity(obj);
			save_ret = true;
			request.getSession(false).removeAttribute("consult_code");
		}
		ModelAndView mv = new RedPigJModelAndView("default/goods_consult.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams("1",10, "addTime","desc");
		
		maps.put("goods_id", CommUtil.null2Long(goods_id));
		
		if (!CommUtil.null2String(consult_type).equals("")) {
			maps.put("consult_type", consult_type);
		}
		
		IPageList pList = this.consultService.list(maps);
		String url2 = CommUtil.getURL(request) + "/goods_consult";
		mv.addObject("consult_objs", pList.getResult());
		mv.addObject("consult_gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(
				url2, "", pList.getCurrentPage(), pList.getPages(),
				pList.getPageSize()));
		mv.addObject("goods_id", goods_id);
		mv.addObject("consultViewTools", this.consultViewTools);
		mv.addObject("consult_type", CommUtil.null2String(consult_type));
		return mv;
	}
	/**
	 * 用户对某条咨询点击满意
	 * 
	 * @param request
	 * @param response
	 * @param consult_id
	 */
	@RequestMapping({ "/goods_consult_satisfy" })
	public void goods_consult_satisfy(HttpServletRequest request,
			HttpServletResponse response, String consult_id) {
		User user = SecurityUserHolder.getCurrentUser();
		
		Consult obj = this.consultService.selectByPrimaryKey(CommUtil.null2Long(consult_id));
		
		if (user != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("cs_user_id", user.getId());
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			
			List<ConsultSatis> css = this.consultsatisService.queryPageList(params);
			
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(0);
				cs.setCs_user_id(user.getId());
				this.consultsatisService.saveEntity(cs);
				
				obj.setSatisfy(obj.getSatisfy() + 1);
				this.consultService.updateById(obj);
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("cs_ip", CommUtil.getIpAddr(request));
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			
			List<ConsultSatis> css = this.consultsatisService.queryPageList(params);
			
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(0);
				this.consultsatisService.saveEntity(cs);

				obj.setSatisfy(obj.getSatisfy() + 1);
				this.consultService.updateById(obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(obj.getSatisfy());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 用户对某条评论点不满意
	 * 
	 * @param request
	 * @param response
	 * @param consult_id
	 */
	@RequestMapping({ "/goods_consult_unsatisfy" })
	public void goods_consult_unsatisfy(HttpServletRequest request,
			HttpServletResponse response, String consult_id) {
		User user = SecurityUserHolder.getCurrentUser();
		Consult obj = this.consultService.selectByPrimaryKey(CommUtil.null2Long(consult_id));
		if (user != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("cs_user_id", user.getId());
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			
			List<ConsultSatis> css = this.consultsatisService.queryPageList(params);
			
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(-1);
				cs.setCs_user_id(user.getId());
				this.consultsatisService.saveEntity(cs);

				obj.setUnsatisfy(obj.getUnsatisfy() + 1);
				this.consultService.updateById(obj);
			}
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("cs_ip", CommUtil.getIpAddr(request));
			params.put("cs_consult_id", CommUtil.null2Long(consult_id));
			
			List<ConsultSatis> css = this.consultsatisService.queryPageList(params);
			
			if (css.size() == 0) {
				ConsultSatis cs = new ConsultSatis();
				cs.setAddTime(new Date());
				cs.setCs_consult_id(CommUtil.null2Long(consult_id));
				cs.setCs_ip(CommUtil.getIpAddr(request));
				cs.setCs_type(-1);
				this.consultsatisService.saveEntity(cs);

				obj.setUnsatisfy(obj.getUnsatisfy() + 1);
				this.consultService.updateById(obj);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(obj.getUnsatisfy());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 加载店铺评分信息
	 * 
	 * @param store
	 * @param mv
	 */
	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0.0D;
		double service_result = 0.0D;
		double ship_result = 0.0D;
		GoodsClass gc = this.goodsClassService.selectByPrimaryKey(store.getGc_main_id());
		if ((store != null) && (gc != null) && (store.getPoint() != null)) {
			float description_evaluate = CommUtil.null2Float(gc.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint().getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint().getShip_evaluate());

			description_result = CommUtil.div(
					Float.valueOf(store_description_evaluate - description_evaluate),
					Float.valueOf(description_evaluate));
			
			service_result = CommUtil.div(
					Float.valueOf(store_service_evaluate - service_evaluate),
					Float.valueOf(service_evaluate));
			
			ship_result = CommUtil.div(
					Float.valueOf(store_ship_evaluate - ship_evaluate),
					Float.valueOf(ship_evaluate));
			
		}
		if (description_result > 0.0D) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(description_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil.mul(Double.valueOf(description_result),Integer.valueOf(100))))
							+ "%");
		}
		if (description_result == 0.0D) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0.0D) {
			mv.addObject("description_css", "value_light");
			mv.addObject(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-description_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result > 0.0D) {
			mv.addObject("service_css", "value_strong");
			mv.addObject(
					"service_result",

					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(service_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(service_result),
									Integer.valueOf(100))))
							+ "%");
		}
		if (service_result == 0.0D) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0.0D) {
			mv.addObject("service_css", "value_light");
			mv.addObject(
					"service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result > 0.0D) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject(
					"ship_result",

					CommUtil.null2String(Double
							.valueOf(CommUtil.mul(Double.valueOf(ship_result),
									Integer.valueOf(100)) > 100.0D ? 100.0D
									: CommUtil.mul(Double.valueOf(ship_result),
											Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result == 0.0D) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0.0D) {
			mv.addObject("ship_css", "value_light");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-ship_result), Integer.valueOf(100))))
							+ "%");
		}
	}
	
	/**
	 * 商品对比
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/goods_compare" })
	public ModelAndView goods_compare(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("goods_compare.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_compare_cart = (List) request.getSession()
				.getAttribute("goods_compare_cart");
		List<GoodsCompareView> goods_compare_list = Lists.newArrayList();
		if ((goods_compare_cart != null) && (goods_compare_cart.size() > 0)) {
			Goods goods = (Goods) goods_compare_cart.get(goods_compare_cart
					.size() - 1);
			goods = this.goodsService.selectByPrimaryKey(goods.getId());
			GoodsClass gc = goods.getGc();
			if (goods.getGc().getParent() != null) {
				gc = goods.getGc().getParent();
			}
			if ((goods.getGc().getParent() != null)
					&& (goods.getGc().getParent().getParent() != null)) {
				gc = goods.getGc().getParent().getParent();
			}
			mv.addObject("gc", gc);

			List<GoodsTypeProperty> gtps = null;
			if (gc.getGoodsType() != null) {
				gtps = gc.getGoodsType().getProperties();
			}
			mv.addObject("gtps", gtps);
			for (Goods obj : goods_compare_cart) {
				obj = this.goodsService.selectByPrimaryKey(obj.getId());
				GoodsCompareView gcv = new GoodsCompareView();
				gcv.setBad_evaluate(CommUtil.mul(obj.getBad_evaluate(),
						Integer.valueOf(100))
						+ "%");
				gcv.setGoods_brand(obj.getGoods_brand() != null ? obj
						.getGoods_brand().getName() : "-----");
				gcv.setGoods_cod(obj.getGoods_cod() == 0 ? "支持" : "不支持");
				gcv.setGoods_id(obj.getId());
				gcv.setGoods_img(generic_goods_img(request, obj));
				gcv.setGoods_name(obj.getGoods_name());
				gcv.setGoods_price(obj.getGoods_current_price());
				gcv.setGoods_url(generic_goods_url(request, obj));
				gcv.setGoods_weight(obj.getGoods_weight());
				gcv.setMiddle_evaluate(CommUtil.mul(obj.getMiddle_evaluate(),
						Integer.valueOf(100)) + "%");
				gcv.setTax_invoice(obj.getTax_invoice() == 0 ? "不支持" : "支持");
				gcv.setWell_evaluate(CommUtil.mul(obj.getWell_evaluate(),
						Integer.valueOf(100)) + "%");
				if (gtps != null) {
					List<Map> list = JSON.parseArray(obj.getGoods_property(),
							Map.class);
					Map gcv_props = Maps.newHashMap();
					for (GoodsTypeProperty gtp : gtps) {

						for (Map map : list) {
							if (CommUtil.null2Long(map.get("id")).equals(
									gtp.getId())) {
								if (!CommUtil.null2String(map.get("val"))
										.equals("")) {
									gcv_props
											.put(gtp.getName(), map.get("val"));
								} else {
									gcv_props.put(gtp.getName(), "-----");
								}
							}
						}
					}
					gcv.setProps(gcv_props);
				}
				goods_compare_list.add(gcv);
			}
		}
		mv.addObject("goods_compare_list", goods_compare_list);
		return mv;
	}

	private String generic_goods_img(HttpServletRequest request, Goods obj) {
		String img = "";
		if (obj.getGoods_main_photo() != null) {
			img =
			CommUtil.getURL(request) + "/"
					+ obj.getGoods_main_photo().getPath() + "/"
					+ obj.getGoods_main_photo().getName() + "_middle."
					+ obj.getGoods_main_photo().getExt();
		} else {
			img =

			CommUtil.getURL(request)
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getPath()
					+ "/"
					+ this.configService.getSysConfig().getGoodsImage()
							.getName();
		}
		return img;
	}

	private String generic_goods_url(HttpServletRequest request, Goods obj) {
		String url = CommUtil.getURL(request) + "/items_" + obj.getId() + "";
		if ((this.configService.getSysConfig().getSecond_domain_open())
				&& (obj.getGoods_type() == 1)) {
			if (!CommUtil.null2String(
					obj.getGoods_store().getStore_second_domain()).equals("")) {
				url =

				"http://" + obj.getGoods_store().getStore_second_domain() + "."
						+ CommUtil.generic_domain(request) + "/" + "/items_"
						+ obj.getId() + "";
			}
		}
		return url;
	}
	
	/**
	 * 添加商品对比到购物车
	 * @param request
	 * @param response
	 * @param goods_id
	 * @return
	 */
	@RequestMapping({ "/add_goods_compare_cart" })
	public ModelAndView add_goods_compare_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView("goods_compare_cart_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Goods> goods_compare_cart = (List) request.getSession()
				.getAttribute("goods_compare_cart");
		if (goods_compare_cart == null) {
			goods_compare_cart = Lists.newArrayList();
		}
		int compare_goods_flag = 0;
		if (goods_compare_cart.size() < 4) {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil
					.null2Long(goods_id));
			boolean add = true;
			Long gc_id = obj.getGc().getParent().getId();
			if (obj.getGc().getParent().getParent() != null) {
				gc_id = obj.getGc().getParent().getParent().getId();
			}
			for (Goods goods : goods_compare_cart) {
				if (goods.getId().equals(obj.getId())) {
					add = false;
				}
			}
			for (Goods compare_goods : goods_compare_cart) {
				if (compare_goods != null) {
					compare_goods = this.goodsService.selectByPrimaryKey(compare_goods
							.getId());
					if (!compare_goods.getGc().getParent().getParent().getId()
							.equals(CommUtil.null2Long(gc_id))) {
						compare_goods_flag = 1;
					}
				}
			}
			if ((add) && (compare_goods_flag == 0)) {
				goods_compare_cart.add(0, obj);
			}
		}
		mv.addObject("goods_id", goods_id);
		mv.addObject("compare_goods_flag", Integer.valueOf(compare_goods_flag));
		request.getSession(false).setAttribute("goods_compare_cart",
				goods_compare_cart);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	@RequestMapping({ "/remove_goods_compare_cart" })
	public ModelAndView remove_goods_compare_cart(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView("goods_compare_cart_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		List<Goods> goods_compare_cart = (List) request.getSession().getAttribute("goods_compare_cart");
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		
		for (int i = 0; i < goods_compare_cart.size(); i++) {
			if (goods_compare_cart.get(i).getId().equals(obj.getId())) {
				goods_compare_cart.remove(i);
				break;
			}
		}
		
		request.getSession(false).setAttribute("goods_compare_cart",goods_compare_cart);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	@RequestMapping({ "/remove_all_goods_compare_cart" })
	public ModelAndView remove_all_goods_compare_cart(
			HttpServletRequest request, HttpServletResponse response) {
		List<Goods> goods_compare_cart = (List) request.getSession()
				.getAttribute("goods_compare_cart");
		if (goods_compare_cart != null) {
			goods_compare_cart.clear();
		}
		request.getSession(false).removeAttribute("goods_compare_cart");
		ModelAndView mv = new RedPigJModelAndView("goods_compare_cart_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("objs", goods_compare_cart);
		return mv;
	}

	@RequestMapping({ "/remove_goods_compart" })
	public String remove_goods_compart(HttpServletRequest request,
			HttpServletResponse response, String goods_id) {
		
		List<Goods> goods_compare_cart = (List) request.getSession().getAttribute("goods_compare_cart");
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		
		for (int i = 0; i < goods_compare_cart.size(); i++) {
			if (goods_compare_cart.get(i).getId().equals(obj.getId())) {
				goods_compare_cart.remove(i);
				break;
			}
		}
		request.getSession(false).setAttribute("goods_compare_cart",goods_compare_cart);
		return "redirect:goods_compare";
	}
	
	
}
