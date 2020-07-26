package com.redpigmall.manage.admin.action.self.enoughReduce;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
import com.redpigmall.api.tools.ObjectUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;

/**
 * 
 * <p>
 * Title: RedPigSelfEnoughReduceManageAction.java
 * </p>
 * 
 * <p>
 * Description: 自营自营满就减控制器
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
 * 
 * @date 2014-9-22
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSelfEnoughReduceManageAction extends BaseAction {
	/**
	 * 自营满就减活动列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param ertitle
	 * @param erstatus
	 * @param erbegin_time
	 * @param erend_time
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动列表", value = "/enoughreduce_self_list*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_self_list" })
	public ModelAndView enoughreduce_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String ertitle, String erstatus,
			String erbegin_time, String erend_time) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/enoughreduce_self_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		maps.put("er_type", 0);
		
		if ((orderBy != null) && (!"".equals(orderBy))) {
			
			maps.put("orderBy", orderBy);
			
			mv.addObject("orderBy", orderBy);
		}
		if ((orderType != null) && (!"".equals(orderType))) {
			maps.put("orderType", orderType);
			mv.addObject("orderType", orderType);
		}
		if ((ertitle != null) && (!"".equals(ertitle))) {
			
			maps.put("ertitle_like", ertitle);
			mv.addObject("ertitle", ertitle);
		}
		if ((erstatus != null) && (!"".equals(erstatus))) {
			
			maps.put("erstatus", CommUtil.null2Int(erstatus));
			mv.addObject("erstatus", erstatus);
		}
		if ((erbegin_time != null) && (!erbegin_time.equals(""))) {
			
			maps.put("erend_time_more_than_equal", erbegin_time);
			mv.addObject("erbegin_time", erbegin_time);
		}
		if ((erend_time != null) && (!erend_time.equals(""))) {
			
			maps.put("erend_time_less_than_equal", erend_time);
			mv.addObject("erend_time", erend_time);
		}
		
		IPageList pList = this.enoughreduceService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url
				+ "/enoughreduce_self_list.html", "", params, pList, mv);
		return mv;
	}
	
	/**
	 * 自营满就减活动添加
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动添加", value = "/enoughreduce_self_add*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_self_add" })
	public ModelAndView enoughreduce_add(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/enoughreduce_self_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 自营满就减活动修改
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动修改", value = "/enoughreduce_self_edit*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_self_edit" })
	public ModelAndView enoughreduce_edit(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/enoughreduce_self_add.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!id.equals(""))) {
			EnoughReduce enoughreduce = this.enoughreduceService.selectByPrimaryKey(Long.parseLong(id));
			mv.addObject("obj", enoughreduce);
			mv.addObject("currentPage", currentPage);
			mv.addObject("edit", true);
		}
		return mv;
	}
	
	/**
	 * 自营满就减活动保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param add_url
	 * @param count
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动保存", value = "/enoughreduce_self_save*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_self_save" })
	public ModelAndView enoughreduce_self_save(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String add_url, String count) {
		
		EnoughReduce enoughreduce = null;
		if (id.equals("")) {
			enoughreduce = (EnoughReduce) WebForm.toPo(request,
					EnoughReduce.class);
			enoughreduce.setAddTime(new Date());
			enoughreduce.setEr_type(0);
		} else {
			EnoughReduce obj = this.enoughreduceService.selectByPrimaryKey(Long
					.valueOf(Long.parseLong(id)));
			enoughreduce = (EnoughReduce) WebForm.toPo(request, obj);
		}
		TreeMap<Double, Double> jsonmap = Maps.newTreeMap();
		for (int i = 1; i <= CommUtil.null2Int(count); i++) {
			String enoughMoney = CommUtil.null2String(request.getParameter("enoughMoney_" + i));
			String reduceMoney = CommUtil.null2String(request.getParameter("reduceMoney_" + i));
			if ((enoughMoney != null) 
					&& (!"".equals(enoughMoney))
					&& (reduceMoney != null) 
					&& (!"".equals(reduceMoney))) {
				jsonmap.put(CommUtil.null2Double(new BigDecimal(enoughMoney)), CommUtil.null2Double(new BigDecimal(reduceMoney)));
			}
		}
		enoughreduce.setEr_json(JSON.toJSONString(jsonmap));
		String ertag = "";
		Iterator<Double> it = jsonmap.keySet().iterator();
		while (it.hasNext()) {
			double key = it.next();
			double value = jsonmap.get(key);
			ertag = ertag + "满" + key + "减" + value + ",";
		}
		
		ertag = ertag.substring(0, ertag.length() - 1);
		enoughreduce.setErtag(ertag);
		enoughreduce.setErstatus(10);
		
		if (id.equals("")) {
			this.enoughreduceService.saveEntity(enoughreduce);
		} else {
			this.enoughreduceService.updateById(enoughreduce);
		}
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("list_url", list_url);
		mv.addObject("op_title", "保存满就减活动成功");
		if (add_url != null) {
			mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
		}
		return mv;
	}
	
	/**
	 * 自营满就减活动删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动删除", value = "/enoughreduce_self_del*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_self_del" })
	public String enoughreduce_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				EnoughReduce enoughreduce = this.enoughreduceService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));

				String goods_json = enoughreduce.getErgoods_ids_json();
				if ((goods_json != null) && (!goods_json.equals(""))) {
					List<String> goods_id_list = JSON.parseArray(goods_json,
							String.class);
					for (String goods_id : goods_id_list) {
						Goods ergood = this.goodsService.selectByPrimaryKey(CommUtil
								.null2Long(goods_id));
						if (ergood != null) {
							ergood.setEnough_reduce(0);
							ergood.setOrder_enough_reduce_id(null);
							this.goodsService.updateById(ergood);
						}
					}
				}
				this.enoughreduceService.deleteById(Long.valueOf(Long.parseLong(id)));
			}
		}
		return "redirect:enoughreduce_self_list?currentPage=" + currentPage;
	}
	
	/**
	 * 自营满就减活动商品列表
	 * 这里的逻辑是:
	 * 1、已结参加本活动的商品
	 * 2、未参加任何活动的商品
	 * @param request
	 * @param response
	 * @param er_id
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param class_id
	 * @param brand_id
	 * @param goods_name
	 * @return
	 */
	@SecurityMapping(title = "自营满就减活动商品列表", value = "/enoughreduce_self_goods_list*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_self_goods_list" })
	public ModelAndView enoughreduce_self_goods_list(
			HttpServletRequest request, HttpServletResponse response,
			String er_id, String currentPage, String orderBy, String orderType,
			String class_id, String brand_id, String goods_name) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/enoughreduce_self_goods.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		EnoughReduce er = this.enoughreduceService.selectByPrimaryKey(CommUtil.null2Long(er_id));
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		params.put("order_enough_reduce_id_or_not", er.getId());
		if (er !=null && er.getErstatus() != 20) {
			
			if ((er.getErgoods_ids_json() != null)
					&& (!er.getErgoods_ids_json().equals(""))) {//参加本活动的商品
				
				params.put("ids_or", genericIds(er.getErgoods_ids_json()));
			}
			
			params.put("goods_type", 0);
			
			
			this.queryTools.shieldGoodsStatus(params, null);//未参加任何活动的商品
			
			if ((goods_name != null) && (!goods_name.equals(""))) {
				
				params.put("goods_name_like", goods_name);
				
				mv.addObject("goods_name", goods_name);
				
			}
			if ((class_id != null) && (!class_id.equals(""))) {
				params.put("rgc_id", CommUtil.null2Long(class_id));
				params.put("rgc_parent_id", CommUtil.null2Long(class_id));
				params.put("rgc_parent_parent_id", CommUtil.null2Long(class_id));
				
				
				mv.addObject("class_id", class_id);
				
			}
			if ((brand_id != null) && (!brand_id.equals(""))) {
				
				params.put("goods_brand_id", CommUtil.null2Long(brand_id));
				
				mv.addObject("brand_id", brand_id);
				
			}
			
		} else if (er !=null &&  er.getErgoods_ids_json().length() > 2) {//如果是非审核通过的状态,只显示已结参加活动的商品
			
			params.put("ids_or", genericIds(er.getErgoods_ids_json()));//参加本活动的商品
			
			if ((goods_name != null) && (!goods_name.equals(""))) {
				
				params.put("goods_name_like", goods_name);
				mv.addObject("goods_name", goods_name);
			}
			if ((class_id != null) && (!class_id.equals(""))) {
				
				params.put("rgc_id", CommUtil.null2Long(class_id));
				params.put("rgc_parent_id", CommUtil.null2Long(class_id));
				params.put("rgc_parent_parent_id", CommUtil.null2Long(class_id));
				
				mv.addObject("class_id", class_id);
			}
			if ((brand_id != null) && (!brand_id.equals(""))) {
				
				params.put("goods_brand_id", CommUtil.null2Long(brand_id));
				mv.addObject("brand_id", brand_id);
			}
		} 
		
		IPageList pList = this.goodsService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		params.clear();
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params);
		params.clear();
		
		params.put("parent", -1);
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageListWithNoRelations(params);
		
		mv.addObject("gcs", gcs);
		mv.addObject("gbs", gbs);
		mv.addObject("er", er);
		return mv;
	}
	
	/**
	 * 满就减商品AJAX更新
	 * @param request
	 * @param response
	 * @param id
	 * @param er_id
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "满就减商品AJAX更新", value = "/enoughreduce_goods_ajax*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_goods_ajax" })
	public void enoughreduce_goods_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String er_id)
			throws ClassNotFoundException {
		Goods obj = this.goodsService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		EnoughReduce er = this.enoughreduceService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(er_id)));
		int flag = obj.getEnough_reduce();
		boolean data = false;
		obj.setEnough_reduce(flag);
		String json = er.getErgoods_ids_json();
		List jsonlist;
		if ((json != null) && (!"".equals(json))) {
			jsonlist = JSON.parseArray(json);
		} else {
			jsonlist = Lists.newArrayList();
		}
		if (flag == 0) {
			data = true;
			if ((obj.getCombin_status() == 0) && (obj.getGroup_buy() == 0)
					&& (obj.getGoods_type() == 0)
					&& (obj.getActivity_status() == 0)
					&& (obj.getF_sale_type() == 0)
					&& (obj.getAdvance_sale_type() == 0)
					&& (obj.getOrder_enough_give_status() == 0)) {
				obj.setEnough_reduce(1);
				obj.setOrder_enough_reduce_id(er_id);
				jsonlist.add(id);
				er.setErgoods_ids_json(JSON.toJSONString(jsonlist));
			}
		} else {
			data = false;
			obj.setEnough_reduce(0);
			obj.setOrder_enough_reduce_id("");
			if (jsonlist.contains(id)) {
				jsonlist.remove(id);
			}
			er.setErgoods_ids_json(JSON.toJSONString(jsonlist));
		}
		this.enoughreduceService.updateById(er);
		this.goodsService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 自营满就减活动商品批量管理
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @param er_id
	 * @param type
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "自营满就减活动商品批量管理", value = "/enoughreduce_goods_admin*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_goods_admin" })
	public String enoughreduce_goods_admin(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage,
			String er_id, String type) {
		EnoughReduce enoughreduce = this.enoughreduceService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(er_id)));
		String goods_json = enoughreduce.getErgoods_ids_json();
		List goods_id_list = null;
		if ((goods_json != null) && (!"".equals(goods_json))) {
			goods_id_list = JSON.parseArray(goods_json);
		} else {
			goods_id_list = Lists.newArrayList();
		}
		String[] ids = mulitId.split(",");

		for (String id : ids) {

			if (!id.equals("")) {
				Goods ergood = this.goodsService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				if ((ergood.getEnough_reduce() == 0)
						|| (ergood.getOrder_enough_reduce_id().equals(er_id))) {
					if (type.equals("add")) {
						if ((ergood.getCombin_status() == 0)
								&& (ergood.getGroup_buy() == 0)
								&& (ergood.getGoods_type() == 0)
								&& (ergood.getActivity_status() == 0)
								&& (ergood.getF_sale_type() == 0)
								&& (ergood.getAdvance_sale_type() == 0)
								&& (ergood.getOrder_enough_give_status() == 0)) {
							goods_id_list.add(id);
							ergood.setEnough_reduce(1);
							ergood.setOrder_enough_reduce_id(er_id);
						}
					} else {
						if (goods_id_list.contains(id)) {
							goods_id_list.remove(id);
						}
						ergood.setEnough_reduce(0);
						ergood.setOrder_enough_reduce_id("");
					}
				}
				this.goodsService.updateById(ergood);
			}
		}
		enoughreduce.setErgoods_ids_json(JSON.toJSONString(goods_id_list));
		this.enoughreduceService.updateById(enoughreduce);

		return "redirect:enoughreduce_self_goods_list?currentPage="
				+ currentPage + "&er_id=" + er_id;
	}
	
	/**
	 * 自营满就减活动ajax
	 * @param request
	 * @param response
	 * @param id
	 * @param fieldName
	 * @param value
	 * @throws ClassNotFoundException
	 */
	@SecurityMapping(title = "自营满就减活动ajax", value = "/enoughreduce_self_ajax*", rtype = "admin", rname = "满就减管理", rcode = "enoughreduce_self_admin", rgroup = "自营")
	@RequestMapping({ "/enoughreduce_self_ajax" })
	public void enoughreduce_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String fieldName,
			String value) throws ClassNotFoundException {
		EnoughReduce obj = this.enoughreduceService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		Field[] fields = EnoughReduce.class.getDeclaredFields();
		
		Object val = ObjectUtils.setValue(fieldName, value, obj, fields);
		this.enoughreduceService.updateById(obj);
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

	@SuppressWarnings("rawtypes")
	private Set<Long> genericIds(String str) {
		Set<Long> ids = Sets.newHashSet();
		List list = JSON.parseArray(str);
		for (Object object : list) {
			ids.add(CommUtil.null2Long(object));
		}
		return ids;
	}
}
