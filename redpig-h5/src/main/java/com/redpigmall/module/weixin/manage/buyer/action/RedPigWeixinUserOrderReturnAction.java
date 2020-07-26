package com.redpigmall.module.weixin.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import com.redpigmall.domain.ExpressCompany;
import com.redpigmall.domain.ExpressInfo;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupInfo;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.RefundApplyForm;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.domain.User;
import com.redpigmall.domain.virtual.TransInfo;

/**
 * <p>
 * Title: RedPigWapUserOrderReturnAction.java
 * </p>
 * 
 * <p>
 * Description: wap端退货退款管理
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
 * @date 2015年1月23日
 * 
 * @version b2b2c_2015
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigWeixinUserOrderReturnAction extends BaseAction{
	
	
	/**
	 * 买家未发货退货
	 * @param request
	 * @param response
	 * @param oid
	 */
	@SecurityMapping(title = "买家未发货退货", value = "/buyer/order_form_refund*", rtype = "buyer", rname = "用户中心退货管理", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_form_refund" })
	public void order_form_refund(HttpServletRequest request,
			HttpServletResponse response, String oid) {
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(oid));
		User currentUser = SecurityUserHolder.getCurrentUser();
		if ((obj.getOrder_status() == 20)
				&& (obj.getUser_id().equals(currentUser.getId().toString()))) {
			obj.setOrder_status(21);
			this.orderFormService.updateById(obj);
			RefundApplyForm refundApplyForm = new RefundApplyForm();
			refundApplyForm.setOrder_form_id(obj.getId());
			refundApplyForm.setOrder_id(obj.getOrder_id());
			refundApplyForm.setRefund_price(obj.getTotalPrice());
			refundApplyForm.setStatus(0);
			refundApplyForm.setUser_id(CommUtil.null2Long(obj.getUser_id()));
			refundApplyForm.setUser_name(obj.getUser_name());
			refundApplyForm.setAddTime(new Date());
			if (obj.getStore_id() == null) {
				refundApplyForm.setStore_id("self");
				refundApplyForm.setStore_name("自营");
			} else {
				refundApplyForm.setStore_id(obj.getStore_id());
				refundApplyForm.setStore_name(obj.getStore_name());
			}
			this.refundApplyFormService.saveEntity(refundApplyForm);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(oid);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 买家退货申请
	 * @param request
	 * @param response
	 * @param id
	 * @param oid
	 * @param goods_gsp_ids
	 * @return
	 */
	@SecurityMapping(title = "买家退货申请", value = "/buyer/order_return_apply*", rtype = "buyer", rname = "用户中心退货管理", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_apply" })
	public ModelAndView order_return_apply(HttpServletRequest request,
			HttpServletResponse response, String id, String oid,
			String goods_gsp_ids) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_return_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(oid));
		if (obj.getUser_id().equals(
				SecurityUserHolder.getCurrentUser().getId().toString())) {
			if (obj.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId().toString())) {
				List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
						.getGoods_info());
				Map return_goods = getReturnGoodsMapInJson(
						obj.getReturn_goods_info(), id, goods_gsp_ids);
				Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				for (Map m : maps) {
					if (CommUtil.null2String(m.get("goods_id")).equals(id)) {
						if (CommUtil.null2String(m.get("goods_gsp_ids"))
								.equals(goods_gsp_ids)) {
							mv.addObject("goods", goods);
							int count = CommUtil.null2Int(m.get("goods_count"));
							if ((return_goods != null)
									&& (return_goods.size() > 0)) {
								count = CommUtil.null2Int(m.get("goods_count"))
										- CommUtil.null2Int(return_goods
												.get("return_goods_count"));
							}
							if (CommUtil.null2String(
									m.get("goods_return_status")).equals("5")) {
								mv.addObject("view", Boolean.valueOf(true));
								List<Map> return_maps = this.orderFormTools
										.queryGoodsInfo(obj
												.getReturn_goods_info());
								for (Map map : return_maps) {
									if (CommUtil.null2String(
											map.get("return_goods_id")).equals(
											id)) {
										mv.addObject("return_content",
												map.get("return_goods_content"));
										count = CommUtil.null2Int(m
												.get("goods_count"))
												- CommUtil
														.null2Int(m
																.get("return_goods_count"));
									}
								}
							}
							if (m.get("combin_suit_info") != null) {
								mv.addObject("orderFormTools",
										this.orderFormTools);
								mv.addObject("combin_suit_info",
										m.get("combin_suit_info"));
							}
							mv.addObject("return_count", Integer.valueOf(count));
						}
					}
				}
			}
		}
		mv.addObject("oid", oid);
		mv.addObject("goods_gsp_ids", goods_gsp_ids);
		return mv;
	}
	
	private Map getReturnGoodsMapInJson(String json, String goods_id,
			String goods_gsp_ids) {
		Map<String, Object> map = null;
		List<Map> map_list = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			map_list = JSON.parseArray(json, Map.class);
			for (Map m : map_list) {
				if (CommUtil.null2String(m.get("return_goods_id")).equals(
						goods_id)) {
					if (CommUtil.null2String(m.get("goods_gsp_ids")).equals(
							goods_gsp_ids)) {
						map = m;
						break;
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 买家退货申请保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param return_goods_content
	 * @param goods_id
	 * @param return_goods_count
	 * @param goods_gsp_ids
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@SecurityMapping(title = "买家退货申请保存", value = "/buyer/order_return_apply_save*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_apply_save" })
	public String order_return_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String return_goods_content, String goods_id,
			String return_goods_count, String goods_gsp_ids) throws Exception {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		Goods goods = null;
		List<Map> goods_maps = this.orderFormTools.queryGoodsInfo(obj
				.getGoods_info());
		Object combin_suit_info = null;
		for (Map goods_map : goods_maps) {
			if (CommUtil.null2String(goods_map.get("goods_id"))
					.equals(goods_id)) {
				goods = this.goodsService.selectByPrimaryKey(CommUtil
						.null2Long(goods_id));
				if (goods_map.get("combin_suit_info") == null) {
					break;
				}
				combin_suit_info = goods_map.get("combin_suit_info");

				break;
			}
		}
		Map returned_goods = null;
		int got = 0;
		if (obj != null) {
			if ((obj.getUser_id().equals(SecurityUserHolder.getCurrentUser()
					.getId().toString()))
					&& (goods != null)) {
				List<Map> list = this.orderFormTools.queryGoodsInfo(obj
						.getReturn_goods_info());
				Map goods_map = getGoodsMapInJson(obj.getGoods_info(),
						goods_id, goods_gsp_ids);
				if (goods_map == null) {
					return "redirect:order_return_list";
				}
				if ((list != null) && (list.size() == 0)) {
					if ((CommUtil.null2Int(goods_map.get("goods_count")) < CommUtil
							.null2Int(return_goods_count))
							|| (CommUtil.null2Int(return_goods_count) < 1)) {
						return "redirect:order_return_list";
					}
					list = Lists.newArrayList();
					Map json = Maps.newHashMap();
					json.put("return_goods_id", goods.getId());
					json.put("return_goods_content",
							CommUtil.filterHTML(return_goods_content));
					json.put("return_goods_count", return_goods_count);
					json.put("return_goods_price", goods.getStore_price());
					json.put("return_goods_commission_rate", Double
							.valueOf(this.orderFormTools
									.CommRateOfOrderByGoods(
											obj.getGoods_info(), goods)));
					json.put("return_order_id", id);
					json.put("goods_gsp_ids", goods_gsp_ids);
					list.add(json);
				} else {
					for (Map map : list) {
						if (CommUtil.null2String(map.get("return_goods_id"))
								.equals(goods_id)) {
							if (goods_gsp_ids.equals(CommUtil.null2String(map
									.get("goods_gsp_ids")))) {
								int count = CommUtil.null2Int(goods_map
										.get("goods_count"))
										- CommUtil.null2Int(map
												.get("return_goods_count"));
								if (count
										- CommUtil.null2Int(return_goods_count) < 0) {
									return "redirect:order_return_list";
								}
								map.put("return_goods_count",
										Integer.valueOf(CommUtil.null2Int(map
												.get("return_goods_count")) +

										CommUtil.null2Int(return_goods_count)));
								returned_goods = map;

								got++;
								break;
							}
						}
					}
					if (got == 0) {
						Map json = Maps.newHashMap();
						json.put("return_goods_id", goods.getId());
						json.put("return_goods_content",
								CommUtil.filterHTML(return_goods_content));
						json.put("return_goods_count", return_goods_count);
						json.put("return_goods_price", goods.getStore_price());
						json.put("return_goods_commission_rate", Double
								.valueOf(this.orderFormTools
										.CommRateOfOrderByGoods(
												obj.getGoods_info(), goods)));
						json.put("return_order_id", id);
						json.put("goods_gsp_ids", goods_gsp_ids);
						list.add(json);
					}
				}
				obj.setReturn_goods_info(JSON.toJSONString(list));
				List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
						.getGoods_info());
				Object gls = Maps.newHashMap();
				label893: for (Map m : maps) {
					if (CommUtil.null2String(m.get("goods_id"))
							.equals(goods_id)) {
						if (goods_gsp_ids.equals(CommUtil.null2String(m
								.get("goods_gsp_ids")))) {
							if (returned_goods != null) {
								if (CommUtil
										.null2String(m.get("goods_count"))
										.equals(CommUtil.null2String(returned_goods
												.get("return_goods_count")))) {
									m.put("goods_return_status",
											Integer.valueOf(5));
									break label893;
								}
							}
							if (CommUtil.null2String(m.get("goods_count"))
									.equals(return_goods_count)) {
								m.put("goods_return_status", Integer.valueOf(5));
							}
							((Map) gls).putAll(m);
							break;
						}
					}
				}
				obj.setGoods_info(JSON.toJSONString(maps));
				this.orderFormService.updateById(obj);
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());

				ReturnGoodsLog rlog = new ReturnGoodsLog();
				rlog.setReturn_service_id("re" + user.getId()
						+ CommUtil.formatTime("yyyyMMddHHmmss", new Date()));
				rlog.setUser_name(user.getUserName());
				rlog.setUser_id(user.getId());
				rlog.setReturn_content(CommUtil
						.filterHTML(return_goods_content));
				BigDecimal price = new BigDecimal(CommUtil.mul(Double
						.valueOf(CommUtil.null2Double(((Map) gls)
								.get("goods_price"))), Double.valueOf(CommUtil
						.null2Double(return_goods_count))));
				rlog.setGoods_all_price(price.toString());
				rlog.setGoods_count(return_goods_count);
				rlog.setGoods_id(CommUtil.null2Long(CommUtil
						.null2String(((Map) gls).get("goods_id"))));
				rlog.setGoods_mainphoto_path(CommUtil.null2String(((Map) gls)
						.get("goods_mainphoto_path")));
				rlog.setGoods_commission_rate(BigDecimal.valueOf(CommUtil
						.null2Double(((Map) gls).get("goods_commission_rate"))));
				rlog.setGoods_name(CommUtil.null2String(((Map) gls)
						.get("goods_name")));
				rlog.setGoods_price(CommUtil.null2String(((Map) gls)
						.get("goods_price")));
				rlog.setGoods_return_status("5");
				rlog.setAddTime(new Date());
				rlog.setReturn_order_num(CommUtil.null2Long(obj.getOrder_id()));
				rlog.setReturn_order_id(CommUtil.null2Long(id).longValue());
				rlog.setGoods_type(goods.getGoods_type());
				rlog.setGoods_gsp_val(CommUtil.null2String(((Map) gls)
						.get("goods_gsp_val")));
				rlog.setGoods_gsp_ids(CommUtil.null2String(((Map) gls)
						.get("goods_gsp_ids")));
				rlog.setReturn_combin_info(CommUtil
						.null2String(combin_suit_info));
				if (goods.getGoods_store() != null) {
					rlog.setStore_id(goods.getGoods_store().getId());
				}
				this.returnGoodsLogService.saveEntity(rlog);
				if (this.configService.getSysConfig().getKuaidi_type() == 1) {
					JSONObject info = new JSONObject();
					Map express_map = JSON.parseObject(obj.getExpress_info());
					info.put("company", CommUtil.null2String(express_map
							.get("express_company_mark")));
					info.put("number", obj.getShipCode());
					info.put("from", CommUtil.null2String(obj.getShip_addr()));
					info.put("to", obj.getReceiver_area());
					info.put("key", this.configService.getSysConfig()
							.getKuaidi_id2());
					JSONObject param_info = new JSONObject();
					param_info.put("callbackurl", CommUtil.getURL(request)
							+ "/kuaidi_callback?order_id=" + obj.getId()
							+ "&orderType=0");
					param_info.put("salt",
							Md5Encrypt.md5(CommUtil.null2String(obj.getId()))
									.substring(0, 16));
					info.put("parameters", param_info);
					try {
						String result = ShipTools
								.Post("http://highapi.kuaidi.com/openapi-receive.html",
										info.toString());
						Map remap = JSON.parseObject(result);
						if ("success".equals(CommUtil.null2String(remap
								.get("message")))) {
							ExpressInfo ei = new ExpressInfo();
							ei.setAddTime(new Date());
							ei.setOrder_id(obj.getId());
							ei.setOrder_express_id(obj.getShipCode());
							ei.setOrder_type(0);
							Map ec_map = JSON.parseObject(CommUtil
									.null2String(obj.getExpress_info()));
							if (ec_map != null) {
								ei.setOrder_express_name(CommUtil
										.null2String(ec_map
												.get("express_company_name")));
							}
							this.expressInfoService.saveEntity(ei);
							System.out.println("订阅成功");
						} else {
							System.out.println("订阅失败");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (obj.getOrder_form() == 0) {
					User seller = this.userService.selectByPrimaryKey(this.storeService
							.selectByPrimaryKey(CommUtil.null2Long(obj.getStore_id()))
							.getUser().getId());
					Map<String, Object> map = Maps.newHashMap();
					map.put("buyer_id", user.getId().toString());
					map.put("seller_id", seller.getId().toString());
					this.msgTools.sendEmailCharge(CommUtil.getURL(request),
							"email_toseller_order_return_apply_notify",
							seller.getEmail(), JSON.toJSONString(map), null,
							obj.getStore_id());
					map.clear();
					map.put("buyer_id", user.getId().toString());
					map.put("seller_id", seller.getId().toString());
					this.msgTools.sendSmsCharge(CommUtil.getURL(request),
							"sms_toseller_order_return_apply_notify",
							seller.getMobile(), JSON.toJSONString(map), null,
							obj.getStore_id());
				}
			}
		}
		return "redirect:order_return_list";
	}

	private Map getGoodsMapInJson(String json, String goods_id,
			String goods_gsp_ids) {
		Map<String, Object> map = null;
		List<Map> map_list = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			map_list = JSON.parseArray(json, Map.class);
			for (Map m : map_list) {
				if (CommUtil.null2String(m.get("goods_id")).equals(goods_id)) {
					if (CommUtil.null2String(m.get("goods_gsp_ids")).equals(
							goods_gsp_ids)) {
						map = m;
						break;
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * 买家退货申请取消
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param return_goods_content
	 * @param goods_id
	 * @param return_goods_count
	 * @param goods_gsp_ids
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "买家退货申请取消", value = "/buyer/order_return_apply_cancel*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_apply_cancel" })
	public String order_return_apply_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String return_goods_content, String goods_id,
			String return_goods_count, String goods_gsp_ids) throws Exception {
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		List<Goods> goods_list = this.orderFormTools.queryOfGoods(obj);
		Goods goods = null;
		for (Goods g : goods_list) {
			if (g.getId().toString().equals(goods_id)) {
				goods = g;
			}
		}
		if (obj != null) {
			if ((obj.getUser_id().equals(SecurityUserHolder.getCurrentUser()
					.getId().toString()))
					&& (goods != null)) {
				obj.setReturn_goods_info("");
				List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
						.getGoods_info());
				List new_maps = Lists.newArrayList();
				Map gls = Maps.newHashMap();
				for (Map m : maps) {
					if (m.get("goods_id").toString().equals(goods_id)) {
						if (goods_gsp_ids.equals(m.get("goods_gsp_ids")
								.toString())) {
							m.put("goods_return_status", "");
							gls.putAll(m);
						}
					}
					new_maps.add(m);
				}
				obj.setGoods_info(JSON.toJSONString(new_maps));
				this.orderFormService.updateById(obj);
				
				Map<String,Object> map = Maps.newHashMap();
				map.put("goods_id",
						CommUtil.null2Long(gls.get("goods_id").toString()));
				map.put("return_order_id", CommUtil.null2Long(id));
				map.put("user_id", SecurityUserHolder.getCurrentUser()
						.getId());
				map.put("goods_gsp_ids", goods_gsp_ids);
				
				List<ReturnGoodsLog> objs = this.returnGoodsLogService.queryPageList(map);
				
				for (ReturnGoodsLog rl : objs) {
					this.returnGoodsLogService.deleteById(rl.getId());
				}
			}
		}
		return "redirect:order_return_list";
	}
	
	/**
	 * 买家退货物流信息
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家退货物流信息", value = "/buyer/order_return_ship*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_ship" })
	public ModelAndView order_return_ship(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_return_ship.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.orderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId().toString())) {
				mv.addObject("obj", obj);
				mv.addObject("currentPage", currentPage);

				List<Goods> list_goods = this.orderFormTools.queryOfGoods(obj);
				List<Goods> deliveryGoods = Lists.newArrayList();
				boolean physicalGoods = false;
				for (Goods g : list_goods) {
					if (g.getGoods_choice_type() == 1) {
						deliveryGoods.add(g);
					} else {
						physicalGoods = true;
					}
				}
				Map<String, Object> params = Maps.newHashMap();
				params.put("company_status", Integer.valueOf(0));
				params.put("orderBy", "company_sequence");
				params.put("orderType", "asc");
				
				List<ExpressCompany> expressCompanys = this.expressCompayService.queryPageList(params);
				
				mv.addObject("expressCompanys", expressCompanys);
				mv.addObject("physicalGoods", Boolean.valueOf(physicalGoods));
				mv.addObject("deliveryGoods", deliveryGoods);
			}
		} else {

			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有编号为" + id + "的订单！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_list");
		}
		return mv;
	}
	
	/**
	 * 买家退货物流信息保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param express_id
	 * @param express_code
	 * @return
	 */
	@SecurityMapping(title = "买家退货物流信息保存", value = "/buyer/order_return_ship_save*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_ship_save" })
	public ModelAndView order_return_ship_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String express_id, String express_code) {
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		ReturnGoodsLog obj = this.returnGoodsLogService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((obj != null) && (obj.getUser_id().equals(user.getId()))) {
			ExpressCompany ec = this.expressCompayService.selectByPrimaryKey(CommUtil
					.null2Long(express_id));
			Map json_map = Maps.newHashMap();
			json_map.put("express_company_id", ec.getId());
			json_map.put("express_company_name", ec.getCompany_name());
			json_map.put("express_company_mark", ec.getCompany_mark());
			json_map.put("express_company_type", ec.getCompany_type());
			String express_json = JSON.toJSONString(json_map);
			obj.setReturn_express_info(express_json);
			obj.setExpress_code(express_code);
			obj.setGoods_return_status("7");
			this.returnGoodsLogService.updateById(obj);
			mv.addObject("op_title", "保存退货物流成功！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog");
			OrderForm return_of = this.orderFormService.selectByPrimaryKey(Long
					.valueOf(obj.getReturn_order_id()));
			List<Map> maps = this.orderFormTools.queryGoodsInfo(return_of
					.getGoods_info());
			List<Map> new_maps = Lists.newArrayList();
			Map gls = Maps.newHashMap();
			for (Map m : maps) {
				if (m.get("goods_id").toString()
						.equals(CommUtil.null2String(obj.getGoods_id()))) {
					m.put("goods_return_status", Integer.valueOf(7));
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			return_of.setGoods_info(JSON.toJSONString(new_maps));
			this.orderFormService.updateById(return_of);
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有为" + obj.getReturn_service_id()
					+ "的退货服务号！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog");
		}
		return mv;
	}
	
	/**
	 * 买家退货申请列表
	 * @param request
	 * @param response
	 * @param id
	 * @param view
	 * @param currentPage
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "买家退货申请列表", value = "/buyer/order_return_list*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_list" })
	public ModelAndView order_return_list(HttpServletRequest request,
			HttpServletResponse response, String id, String view,
			String currentPage, String order_id, String beginTime,
			String endTime) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_return_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/order_return_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        
		maps.put("user_id",SecurityUserHolder.getCurrentUser().getId().toString());
		maps.put("order_main",1);
		
		if (!CommUtil.null2String(order_id).equals("")) {
			
			maps.put("order_id_like",order_id);
			
			mv.addObject("order_id", order_id);
		}
		maps.put("order_cat_no",2);
		maps.put("order_status_more_than",40);
		maps.put("return_shipTime_more_than_equal",new Date());
		
		IPageList pList = this.orderFormService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 生活购退款列表
	 * @param request
	 * @param response
	 * @param id
	 * @param view
	 * @param currentPage
	 * @param order_id
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "生活购退款列表", value = "/buyer/group_life_return*", rtype = "buyer", rname = "用户中心", rcode = "wap_wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/group_life_return" })
	public ModelAndView group_life_return(HttpServletRequest request,
			HttpServletResponse response, String id, String view,
			String currentPage, String order_id, String beginTime,
			String endTime) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/group_life_return.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/group_life_return.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("user_id",SecurityUserHolder.getCurrentUser().getId());
        
		maps.put("status_no",1);
		maps.put("refund_Time_more_than_equal",new Date());
		
		IPageList pList = this.groupinfoService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 生活购退款申请
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "生活购退款申请", value = "/buyer/group_life_return_apply*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/group_life_return_apply" })
	public ModelAndView group_life_return_apply(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/group_life_return_apply.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		GroupInfo obj = this.groupinfoService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getUser_id()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			mv.addObject("obj", obj);
		}
		return mv;
	}
	
	/**
	 * 生活购退款申请取消
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "生活购退款申请取消", value = "/buyer/grouplife_return_apply_cancel*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/grouplife_return_apply_cancel" })
	public String grouplife_return_apply_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		GroupInfo obj = this.groupinfoService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj.getUser_id()
				.equals(SecurityUserHolder.getCurrentUser().getId())) {
			obj.setStatus(0);
			this.groupinfoService.updateById(obj);
		}
		return "redirect:group_life_return";
	}
	
	/**
	 * 生活购退款申请保存
	 * @param request
	 * @param response
	 * @param id
	 * @param return_group_content
	 * @param reasion
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "生活购退款申请保存", value = "/buyer/grouplife_return_apply_save*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/grouplife_return_apply_save" })
	public String grouplife_return_apply_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id,
			String return_group_content, String reasion) throws Exception {
		GroupInfo obj = this.groupinfoService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getUser_id().equals(
					SecurityUserHolder.getCurrentUser().getId())) {
				String mark = "";
				if ((reasion != null) && (!reasion.equals(""))) {
					String[] rs_ids = reasion.split(",");
					for (String rid : rs_ids) {
						if (!rid.equals("")) {
							if (rid.equals("1")) {
								mark = "买错了/重新买";
							} else if (rid.equals("2")) {
								mark = "计划有变，没时间消费";
							} else if (rid.equals("3")) {
								mark = "预约不上";
							} else if (rid.equals("4")) {
								mark = "去过了，不太满意";
							} else if (rid.equals("5")) {
								mark = "其他";
							}
						}
						obj.setStatus(3);
						obj.setRefund_reasion(mark + "[" + return_group_content
								+ "]");
						this.groupinfoService.updateById(obj);
						OrderForm order = this.orderFormService.selectByPrimaryKey(obj
								.getOrder_id());
						if (order.getOrder_form() == 0) {
							User seller = this.userService
									.selectByPrimaryKey(this.storeService
											.selectByPrimaryKey(
													CommUtil.null2Long(order
															.getStore_id()))
											.getUser().getId());
							Map<String, Object> map = Maps.newHashMap();
							map.put("buyer_id", SecurityUserHolder
									.getCurrentUser().getId().toString());
							map.put("seller_id", seller.getId().toString());
							String map_json = JSON.toJSONString(map);
							this.msgTools.sendEmailCharge(
									CommUtil.getURL(request),
									"email_toseller_order_refund_apply_notify",
									seller.getEmail(), map_json, null,
									order.getStore_id());
							map.clear();
							map.put("buyer_id", SecurityUserHolder
									.getCurrentUser().getId().toString());
							map.put("seller_id", seller.getId().toString());
							this.msgTools.sendSmsCharge(
									CommUtil.getURL(request),
									"sms_toseller_order_refund_apply_notify",
									seller.getMobile(), map_json, null,
									order.getStore_id());
						}
					}
				}
			}
		}
		return "redirect:group_life_return";
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "买家退货申请列表记录", value = "/buyer/order_return_listlog*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_return_listlog" })
	public ModelAndView order_return_listlog(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_return_listlog.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/order_return_listlog_data.html",
					this.configService.getSysConfig(), this.userConfigService
							.getUserConfig(), 0, request, response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		maps.put("user_id",user.getId());
		maps.put("goods_return_status_no","1");
		
		IPageList pList = this.returnGoodsLogService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 买家退货填写物流
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "买家退货填写物流", value = "/buyer/order_returnlog_view*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_returnlog_view" })
	public ModelAndView order_returnlog_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_returnlog_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLog obj = this.returnGoodsLogService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (obj.getUser_id().equals(user.getId())) {
			if ((obj.getGoods_return_status().equals("6"))
					|| (obj.getGoods_return_status().equals("7"))) {
				Map<String, Object> params = Maps.newHashMap();
				params.put("company_status", Integer.valueOf(0));
				params.put("orderBy", "company_sequence");
				params.put("orderType", "asc");
				
				List<ExpressCompany> expressCompanys = this.expressCompayService.queryPageList(params);
				
				mv.addObject("expressCompanys", expressCompanys);
				mv.addObject("obj", obj);
				mv.addObject("user", user);
				OrderForm of = this.orderFormService.selectByPrimaryKey(Long
						.valueOf(obj.getReturn_order_id()));
				mv.addObject("of", of);
				Goods goods = this.goodsService.selectByPrimaryKey(obj.getGoods_id());
				if (goods.getGoods_type() == 1) {
					mv.addObject("name", goods.getGoods_store().getStore_name());
					mv.addObject("store_id", goods.getGoods_store().getId());
				} else {
					mv.addObject("name", "平台商");
				}
				if (obj.getGoods_return_status().equals("7")) {
					TransInfo transInfo = this.ShipTools
							.query_Returnship_getData(CommUtil.null2String(obj
									.getId()));
					mv.addObject("transInfo", transInfo);
					Map<String, Object> map = JSON.parseObject(obj
							.getReturn_express_info());
					mv.addObject("express_company_name",
							map.get("express_company_name"));
				}
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "当前状态无法对退货服务单进行操作");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order_return_listlog");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有为" + obj.getReturn_service_id()
					+ "的退货服务号！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/order_return_listlog");
		}
		return mv;
	}
}
