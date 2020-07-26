package com.redpigmall.view.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
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
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.CombinPlan;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.GoodsFormat;
import com.redpigmall.domain.Group;
import com.redpigmall.domain.MerchantServices;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
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
public class RedPigGoodsViewAction extends BaseAction{

	/**
	 * 获取价格
	 * @param request
	 * @param response
	 * @param parts_ids
	 * @param gid
	 */
	@RequestMapping({ "/getPartsPrice" })
	public void genericCombinPartsPrice(HttpServletRequest request,
			HttpServletResponse response, String parts_ids, String gid) {
		double all_price = 0.0D;
		if ((gid != null) && (!gid.equals(""))) {
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(gid));
			all_price = CommUtil.null2Double(obj.getGoods_current_price());
		}
		if ((parts_ids != null) && (!parts_ids.equals(""))) {
			String[] ids = parts_ids.split(",");
			for (String id : ids) {

				if (!id.equals("")) {
					Goods obj = this.goodsService.selectByPrimaryKey(CommUtil
							.null2Long(id));
					all_price = CommUtil.add(Double.valueOf(all_price),
							obj.getGoods_current_price());
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(CommUtil.formatMoney(Double.valueOf(all_price)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 商品详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping({ "/items" })
	public ModelAndView goods(
			HttpServletRequest request,
			HttpServletResponse response,
			String id) throws Exception {
		
		ModelAndView mv = null;
		
		if (StringUtils.isBlank(id)) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，商品查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		if (obj != null) {
			//这里的cookie主要就是处理浏览历史记录
			Cookie[] cookies = request.getCookies();
			Cookie goodscookie = null;
			int k = 0;
			//获取cookie中的商品
			if (cookies != null) {
				for (Cookie cookie:cookies) {
					
					if (cookie.getName().equals("goodscookie")) {
						String goods_ids = URLDecoder.decode(cookie.getValue(), "UTF-8");
						int m = 6;//根据浏览猜你喜欢>功能显示五个
						int n = goods_ids.split(",").length;
						if (m > n) {
							m = n + 1;
						}
						
						String[] new_goods_ids = goods_ids.split(",", m);
						//如果数组中有为空值的后面的数据前移一个位置
						for (int i = 0; i < new_goods_ids.length; i++) {
							if ("".equals(new_goods_ids[i])) {
								for (int j = i + 1; j < new_goods_ids.length; j++) {
									new_goods_ids[i] = new_goods_ids[j];
								}
							}
						}
						
						String[] new_ids = new String[6];
						for (int i = 0; i < m - 1; i++) {
							if (id.equals(new_goods_ids[i])) {//如果当前浏览的是以及浏览过的就+1
								k++;
							}
						}
						
						if (k == 0) {//表示当前商品未被浏览过
							new_ids[0] = id;
							//把当前浏览的放到数组第一位,以前浏览过的放到后面
							for (int j = 1; j < m; j++) {
								new_ids[j] = new_goods_ids[(j - 1)];
							}
							
							goods_ids = id + ",";
							//按照规则组装
							if (m == 2) {
								for (int i = 1; i <= m - 1; i++) {
									goods_ids = goods_ids + new_ids[i] + ",";
								}
							} else {
								for (int i = 1; i < m; i++) {
									goods_ids = goods_ids + new_ids[i] + ",";
								}
							}
							//存储到cookie中
							goodscookie = new Cookie("goodscookie", URLEncoder.encode(goods_ids, "UTF-8"));
						} else {
							//当前商品被浏览过,把当前商品排到前面显示
							new_ids = new_goods_ids;
							goods_ids = "";
							for (int i = 0; i < m - 1; i++) {
								goods_ids = goods_ids + new_ids[i] + ",";
							}
							//存储到cookie中
							goodscookie = new Cookie("goodscookie", URLEncoder.encode(goods_ids, "UTF-8"));
						}
						//设置cookie时间
						goodscookie.setMaxAge(604800);
						goodscookie.setDomain(CommUtil.generic_domain(request));
						response.addCookie(goodscookie);
						break;
					}else {
						goodscookie = new Cookie("goodscookie", URLEncoder.encode(id + ",", "UTF-8"));
						goodscookie.setMaxAge(604800);
						goodscookie.setDomain(CommUtil.generic_domain(request));
						response.addCookie(goodscookie);
					}
					
				}
			} else {
				goodscookie = new Cookie("goodscookie", URLEncoder.encode(id + ",", "UTF-8"));
				goodscookie.setMaxAge(604800);
				goodscookie.setDomain(CommUtil.generic_domain(request));
				response.addCookie(goodscookie);
			}
			
			User current_user = SecurityUserHolder.getCurrentUser();
			
			if (current_user != null) {
				this.goodsViewTools.record_footPoint(request, current_user, obj);
				current_user = this.userService.selectByPrimaryKey(current_user.getId());
			}
			//保持点击日志
			this.goodsTools.save_click_goodsLog(request, obj);
			//上架状态
			if ((obj.getGoods_status() == 0) ) {
				mv = new RedPigJModelAndView("default/store_goods.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				//商品版式
				GoodsFormat gf = this.redPigGoodsFormatService.selectByPrimaryKey(obj.getGoods_top_format_id());
				mv.addObject("gf",gf);
				obj.setGoods_click(obj.getGoods_click() + 1);//点击量+1
				//z判断直通车是否开启1、商城要开启,2、该商品要参加直通车
				if ((this.configService.getSysConfig().getZtc_status()) && (obj.getZtc_status() == 2)) {
					obj.setZtc_click_num(obj.getZtc_click_num() + 1);//直通车点击量+1
				}
				
				int count = -1;//限购商品,当前用户可以购买的数量
				Map map;
				//限购
				if ((obj.getGoods_limit() == 1) && (obj.getGoods_limit_count() > 0)) {
					count = obj.getGoods_limit_count();//限购商品,当前用户可以购买的数量
					if (current_user != null) {
						String info = CommUtil.null2String(current_user.getBuy_goods_limit_info());//限购商品信息
						if (!info.equals("")) {
							Map maps = JSON.parseObject(CommUtil.null2String(info));//json解析
							List<Map> list = (List) maps.get("data");//限购商品数据
							for (Map map1 : list) {
								String gid = CommUtil.null2String(map1.get("gid"));//限购商品ID
								if (CommUtil.null2Int(gid) == obj.getId()) {//如果当前用户购买过限购商品
									count = obj.getGoods_limit_count() - CommUtil.null2Int(map1.get("count"));//限购商品,当前用户可以购买的数量-以及购买的
									if (count < 1) {
										count = 0;//可以购买的限购商品数量为0
									}
								}
							}
						}
					}
				} else {
					count = -1;
				}
				
				mv.addObject("count", Integer.valueOf(count));//限购商品,当前用户可以购买的数量
				//这里主要是判断活动是否过期,如果过期在这里做状态更新
				if (obj.getActivity_status() == 1 || obj.getActivity_status() == 2) {
					if (!CommUtil.null2String(obj.getActivity_goods_id()).equals("")) {
						ActivityGoods ag = this.actgoodsService.selectByPrimaryKey(obj.getActivity_goods_id());
						if ((ag != null) && (ag.getAct().getAc_end_time().before(new Date()))) {//活动结束时间在当前时间之前,也就是活动已经结束
							ag.setAg_status(-2);//设置活动状态为已过期
							this.actgoodsService.updateById(ag);
							obj.setActivity_status(0);//设置没有参加活动
							obj.setActivity_goods_id(null);//活动ID置为空,后面做更新
						}
					}
				}
				//这里是如果团购活动已经过期,商品就和团购活动接触关系
				if ((obj.getGroup() != null) && (obj.getGroup_buy() == 2)) {
					Group group = obj.getGroup();
					if (group.getEndTime().before(new Date())) {
						obj.setGroup(null);
						obj.setGroup_buy(0);
						obj.setGoods_current_price(obj.getStore_price());
					}
				}
				
				//组合销售,同样通过查询到组合计划已经过期,判断当前商品是否参加该计划,如果参加了要解除关系
				if (obj.getCombin_status() == 1) {
					Map<String,Object> params = Maps.newHashMap();
					params.put("endTime_less_than_equal", new Date());
					params.put("main_goods_id", obj.getId());
					
					List<CombinPlan> combins = this.combinplanService.queryPageList(params);
					
					if (combins.size() > 0) {
						for (CombinPlan com : combins) {
							if (com.getCombin_type() == 0) {
								if (obj.getCombin_suit_id().equals(com.getId())) {
									obj.setCombin_suit_id(null);
								}
							} else if (com.getId().equals(obj.getCombin_parts_id())) {
								obj.setCombin_parts_id(null);
							}
							obj.setCombin_status(-2);
						}
					}
				}
				/**
				 * 这里的逻辑:
				 * 如果该商品不是满就送商品,那么就有可能是参加了满就送活动,判断该商品满就送时间是否过期
				 * 如果过期就把他所要送的赠品的状态设置为非满就送商品
				 */
				Goods goods;
				if (obj.getOrder_enough_give_status() == 1) {
					BuyGift bg = this.buyGiftService.selectByPrimaryKey(obj.getBuyGift_id());
					if ((bg != null) && (bg.getEndTime().before(new Date()))) {
						bg.setGift_status(20);
						List<Map> maps = JSON.parseArray(bg.getGift_info(),Map.class);
						maps.addAll(JSON.parseArray(bg.getGoods_info(),Map.class));
						for (Map map1 : maps) {
							goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(map1.get("goods_id")));
							if (goods != null) {
								goods.setOrder_enough_give_status(0);
								goods.setOrder_enough_if_give(0);
								goods.setBuyGift_id(null);
							}
						}
						this.buyGiftService.updateById(bg);
					}
					if ((bg != null) && (bg.getGift_status() == 10) && (bg.getBeginTime().before(new Date()))) {
						mv.addObject("isGift", Boolean.valueOf(true));
					}
				}
				
				//判断该商品满就送活动状态
				if (obj.getOrder_enough_if_give() == 1) {
					BuyGift bg = this.buyGiftService.selectByPrimaryKey(obj.getBuyGift_id());
					if ((bg != null) && (bg.getGift_status() == 10) && (bg.getBeginTime().before(new Date()))) {
						mv.addObject("isGive", Boolean.valueOf(true));
					}
				}
				
				String goods_id;
				if (obj.getEnough_reduce() == 1) {
					EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(obj.getOrder_enough_reduce_id()));
					if ((er.getErstatus() == 10)
							&& (er.getErbegin_time().before(new Date()))
							&& (er.getErend_time().after(new Date()))) {
						mv.addObject("enoughreduce", er);
					} else if (er.getErend_time().before(new Date())) {
						er.setErstatus(20);
						this.enoughReduceService.updateById(er);
						String goods_json = er.getErgoods_ids_json();
						List<String> goods_id_list = JSON.parseArray(goods_json, String.class);
						for (String goods_id1 : goods_id_list) {
							Goods ergood = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id1));
							ergood.setEnough_reduce(0);
							ergood.setOrder_enough_reduce_id(null);
							this.goodsService.updateById(ergood);
						}
					}
				}
				
				this.goodsService.updateById(obj);
				
				List<Map<String,Object>> service_list = Lists.newArrayList();
				if (obj.getMerchantService_info() != null) {
					List<String> ms_list = JSON.parseArray(obj.getMerchantService_info(), String.class);
					for (String ms_id : ms_list) {
						MerchantServices ms = this.merchantServicesService
								.selectByPrimaryKey(CommUtil.null2Long(ms_id));
						if (ms != null) {
							Map<String,Object> m = Maps.newHashMap();
							m.put("id", ms_id);
							m.put("name", ms.getServe_name());
							m.put("img", ms.getService_img());
							m.put("path", ms.getService_img() != null ? ms
									.getService_img().getPath()
									+ "/"
									+ ms.getService_img().getName() : "");
							service_list.add(m);
						}
					}
				}
				
				List<Map> advance_list = Lists.newArrayList();
				if ((obj.getAdvance_sale_type() == 1) && (obj.getAdvance_sale_info() != null)) {
					advance_list = JSON.parseArray(obj.getAdvance_sale_info(),Map.class);
				}
				
				mv.addObject("advance_list", advance_list);
				mv.addObject("service_list", service_list);
				mv.addObject("obj", obj);
				mv.addObject("goodsViewTools", this.goodsViewTools);
				mv.addObject("transportTools", this.transportTools);
				
				this.areaViewTools.getUserAreaInfo(request, mv);
				
				Map<String, Object> params = Maps.newHashMap();
				params.put("parent", obj.getGc().getParent().getId());
				params.put("display", Boolean.valueOf(true));
				params.put("orderBy", "sequence");
				params.put("orderType", "asc");
				
				List<GoodsClass> about_gcs = this.goodsClassService.queryPageList(params);
				
				mv.addObject("about_gcs", about_gcs);
				mv.addObject("userTools", this.userTools);
				mv.addObject("goodsViewTools", this.goodsViewTools);
				mv.addObject("activityViewTools", this.activityViewTools);
				
				if (obj.getGoods_type() == 1) {
					generic_evaluate(obj.getGoods_store(), mv);
					mv.addObject("store", obj.getGoods_store());
				}
				
				Map<String,Object> maps= this.redPigQueryTools.getParams("1","addTime", "desc");
		        maps.put("evaluate_goods_id", CommUtil.null2Long(id));
		        maps.put("evaluate_type", "goods");
		        maps.put("evaluate_status", 0);
		        
				IPageList eva_pList = this.evaluateService.list(maps);
				String url = CommUtil.getURL(request) + "/goods_evaluation";
				mv.addObject("eva_objs", eva_pList.getResult());
				
				mv.addObject(
						"eva_gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(url, "",
								eva_pList.getCurrentPage(),
								eva_pList.getPages(), eva_pList.getPageSize()));
				
				mv.addObject("evaluateViewTools", this.evaluateViewTools);
				mv.addObject("orderFormTools", this.orderFormTools);
				
				maps= this.redPigQueryTools.getParams("1",10,"addTime", "desc");
				maps.put("freegoods_id", CommUtil.null2Long(id));
				
				IPageList fal_pList = this.freeApplyLogService.list(maps);
				
				String fal_url = CommUtil.getURL(request) + "/goods_fal";
				mv.addObject("fal_objs", fal_pList.getResult());
				
				mv.addObject(
						"fal_gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(fal_url, "",
								fal_pList.getCurrentPage(),
								fal_pList.getPages(), fal_pList.getPageSize()));
				
				mv.addObject("evaluateViewTools", this.evaluateViewTools);
				mv.addObject("couponViewTools", this.couponViewTools);
				
				maps= this.redPigQueryTools.getParams("1",10,"addTime", "desc");
				maps.put("evaluate_goods_id", CommUtil.null2Long(id));
				
				IPageList order_eva_pList = this.evaluateService.list(maps);
				url = CommUtil.getURL(request) + "/goods_order";
				mv.addObject("order_objs", order_eva_pList.getResult());
				
				mv.addObject("order_gotoPageAjaxHTML", CommUtil
						.showPageAjaxHtml(url, "",
								order_eva_pList.getCurrentPage(),
								order_eva_pList.getPages(),
								order_eva_pList.getPageSize()));
				
				maps= this.redPigQueryTools.getParams("1",10,"addTime", "desc");
				maps.put("goods_id", CommUtil.null2Long(id));
				
				IPageList pList = this.consultService.list(maps);
				url = CommUtil.getURL(request) + "/goods_consult";
				mv.addObject("consult_objs", pList.getResult());
				
				mv.addObject("consult_gotoPageAjaxHTML",
						CommUtil.showPageAjaxHtml(url, "",pList.getCurrentPage(), pList.getPages(),pList.getPageSize()));
				
				mv.addObject("consultViewTools", this.consultViewTools);
				
				List<Goods> goods_compare_list = (List) request.getSession(false).getAttribute("goods_compare_cart");
				
				int compare = 0;
				if (goods_compare_list != null) {
					for (Goods goods1 : goods_compare_list) {
						if (goods1.getId().equals(obj.getId())) {
							compare = 1;
						}
					}
				} else {
					goods_compare_list = Lists.newArrayList();
				}
				
				mv.addObject("goods_compare_list", goods_compare_list);
				mv.addObject("compare", Integer.valueOf(compare));
				mv.addObject("goodsViewTools", this.goodsViewTools);
				
				List<Accessory> acs = obj.getGoods_photos();
				
				for (Accessory acc : acs) {
					System.out.println("id:"+acc.getId()+"path:"+acc.getPath()+"name:"+acc.getName()+"ext:"+acc.getExt());
				}
				
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，商品查看失败，商品已下架");
				mv.addObject("url", CommUtil.getURL(request) + "/index");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，商品查看失败，商品不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			return mv;
		}
		return mv;
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
	 * 加载店铺评分信息
	 * 
	 * @param store
	 * @param mv
	 */
	private void generic_evaluateStaticHtml(Store store, Map<String, Object> velocityParams) {
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
			velocityParams.put("description_css", "value_strong");
			velocityParams.put(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(description_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil.mul(Double.valueOf(description_result),Integer.valueOf(100))))
							+ "%");
		}
		if (description_result == 0.0D) {
			velocityParams.put("description_css", "value_normal");
			velocityParams.put("description_result", "-----");
		}
		if (description_result < 0.0D) {
			velocityParams.put("description_css", "value_light");
			velocityParams.put(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-description_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result > 0.0D) {
			velocityParams.put("service_css", "value_strong");
			velocityParams.put(
					"service_result",

					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(service_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(service_result),
									Integer.valueOf(100))))
							+ "%");
		}
		if (service_result == 0.0D) {
			velocityParams.put("service_css", "value_normal");
			velocityParams.put("service_result", "-----");
		}
		if (service_result < 0.0D) {
			velocityParams.put("service_css", "value_light");
			velocityParams.put(
					"service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result > 0.0D) {
			velocityParams.put("ship_css", "value_strong");
			velocityParams.put(
					"ship_result",

					CommUtil.null2String(Double
							.valueOf(CommUtil.mul(Double.valueOf(ship_result),
									Integer.valueOf(100)) > 100.0D ? 100.0D
									: CommUtil.mul(Double.valueOf(ship_result),
											Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result == 0.0D) {
			velocityParams.put("ship_css", "value_normal");
			velocityParams.put("ship_result", "-----");
		}
		if (ship_result < 0.0D) {
			velocityParams.put("ship_css", "value_light");
			velocityParams.put(
					"ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-ship_result), Integer.valueOf(100))))
							+ "%");
		}
	}
	
}
