package com.redpigmall.manage.seller.action;

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
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.virtual.TransInfo;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * 
 * <p>
 * Title: RedPigReturnSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商品退货管理，查看商品的退货申请。以及对退货的一些操作。
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
 * @date 2014年5月15日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigReturnSellerAction extends BaseAction{
	/**
	 * 退货列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param name
	 * @param user_name
	 * @param return_service_id
	 * @return
	 */
	@SecurityMapping(title = "退货列表", value = "/return*", rtype = "seller", rname = "退货管理", rcode = "seller_return", rgroup = "客户服务")
	@RequestMapping({ "/return" })
	public ModelAndView seller_return(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String user_name,
			String return_service_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/return.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("goods_type", 1);
		
		if ((user_name != null) && (!user_name.equals(""))) {
			maps.put("user_name", user_name);
		}
		
		if ((name != null) && (!name.equals(""))) {
			maps.put("goods_name_like", name);
		}
		
		if ((return_service_id != null) && (!return_service_id.equals(""))) {
			maps.put("return_service_id", return_service_id);
		}
		
		User userParent = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		if(userParent!=null && userParent.getParent()!=null){
			userParent = userParent.getParent();
		}
		User parent = this.userService.selectByPrimaryKey(userParent.getId());
		Store store = parent.getStore();
		System.out.println("parent="+parent);
		System.out.println("store="+store);
		
		maps.put("store_id", store.getId());
		
		IPageList pList = this.returngoodslogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("user_name", user_name);
		mv.addObject("return_service_id", return_service_id);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("store", user.getStore());
		return mv;
	}
	
	/**
	 * 退货列表查看
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "退货列表查看", value = "/return_check*", rtype = "seller", rname = "退货管理", rcode = "seller_return", rgroup = "客户服务")
	@RequestMapping({ "/return_check" })
	public ModelAndView return_check(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/return_check.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		ReturnGoodsLog obj = this.returngoodslogService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (obj.getGoods_return_status().equals("7")) {
			TransInfo transInfo = this.ShipTools.query_Ordership_getData(CommUtil.null2String(obj.getId()));
			mv.addObject("transInfo", transInfo);
			Map<String, Object> map = JSON.parseObject(obj
					.getReturn_express_info());
			mv.addObject("express_company_name",
					map.get("express_company_name"));
		}
		if (obj.getReturn_combin_info() != null) {
			Map combin_map = JSON.parseObject(obj.getReturn_combin_info());
			mv.addObject("combin_map", combin_map);
			mv.addObject("orderFormTools", this.orderFormTools);
		}
		mv.addObject("obj", obj);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 退货列表查看保存
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @param cmd
	 * @param list_url
	 * @param goods_return_status
	 * @param self_address
	 */
	@SecurityMapping(title = "退货列表查看保存", value = "/return_check_save*", rtype = "seller", rname = "退货管理", rcode = "seller_return", rgroup = "客户服务")
	@RequestMapping({ "/return_check_save" })
	public void return_check_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage,
			String cmd, String list_url, String goods_return_status,
			String self_address) {
		User seller = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		ReturnGoodsLog obj = this.returngoodslogService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		seller = seller.getParent() == null ? seller : seller.getParent();
		Map<String,Object> json = Maps.newHashMap();
		json.put("op_title", "审核为未通过");
		if ((seller.getStore() != null)
				&& (seller.getStore().getId().equals(obj.getStore_id()))) {
			obj.setGoods_return_status(goods_return_status);
			obj.setSelf_address(self_address);
			this.returngoodslogService.updateById(obj);
			User user = this.userService.selectByPrimaryKey(obj.getUser_id());
			String msg_content = "订单号：" + obj.getReturn_service_id()
					+ "退货申请审核未通过，请在'退货/退款'-'查看返修/退换记录'中提交退货物流信息。";
			OrderForm of = this.orderformService.selectByPrimaryKey(Long.valueOf(obj.getReturn_order_id()));

			List<Map> bought_goods = this.orderFormTools.queryGoodsInfo(of.getGoods_info());

			List<Map> return_goods = this.orderFormTools.queryGoodsInfo(of.getReturn_goods_info());
			if (goods_return_status.equals("6")) {
				json.put("op_title", "审核通过");
				msg_content = "退货申请审核通过，请在'退货/退款'-'查看返修/退换记录'中提交退货物流信息。";

				Map this_map = null;
				int i = 0;
				int index = -1;
				while (i < bought_goods.size()) {
					if ((return_goods.size() <= bought_goods.size())
							&& (this_map == null) && (return_goods.size() > 0)) {
						if (((Map) return_goods.get(i)).get("return_goods_id")
								.toString()
								.equals(obj.getGoods_id().toString())) {
							if (CommUtil.null2String(
									((Map) return_goods.get(i))
											.get("goods_gsp_ids")).equals(
									obj.getGoods_gsp_ids())) {
								this_map = (Map) return_goods.get(i);
							}
						}
					}
					if (((Map) bought_goods.get(i)).get("goods_id").toString()
							.equals(obj.getGoods_id().toString())) {
						if (CommUtil.null2String(
								((Map) bought_goods.get(i))
										.get("goods_gsp_ids")).equals(
								obj.getGoods_gsp_ids())) {
							index = i;
						}
					}
					if ((index == -1) || (this_map == null)) {
						i++;
					}
					if ((this_map != null) && (index != -1)) {
						break;
					}
				}
				if ((index != -1) && (this_map != null)) {
					if (((Map) bought_goods.get(index))
							.get("goods_count")
							.toString()
							.equals(this_map.get("return_goods_count")
									.toString())) {
						((Map) bought_goods.get(index)).put(
								"goods_return_status", Integer.valueOf(6));
					}
				}
			} else {
				int count = CommUtil.null2Int(obj.getGoods_count());
				int index = -1;
				int return_index = -1;
				int i = 0;
				while (i < bought_goods.size()) {
					if ((return_goods.size() <= bought_goods.size())
							&& (return_index == -1)
							&& (return_goods.size() > 0)) {
						if (((Map) return_goods.get(i)).get("return_goods_id")
								.toString()
								.equals(obj.getGoods_id().toString())) {
							if (CommUtil.null2String(
									((Map) return_goods.get(i))
											.get("goods_gsp_ids")).equals(
									obj.getGoods_gsp_ids())) {
								count = CommUtil.null2Int(((Map) return_goods
										.get(i)).get("return_goods_count"))
										- count;
								((Map) return_goods.get(i)).put(
										"return_goods_count",
										Integer.valueOf(count));
								return_index = i;
							}
						}
					}
					if (index == -1) {
						if (((Map) bought_goods.get(i)).get("goods_id")
								.toString()
								.equals(obj.getGoods_id().toString())) {
							if (CommUtil.null2String(
									((Map) bought_goods.get(i))
											.get("goods_gsp_ids")).equals(
									obj.getGoods_gsp_ids())) {
								index = i;
							}
						}
					}
					if ((index == -1) || (return_index == -1)) {
						i++;
					}
					if ((index != -1) && (return_index != -1)) {
						break;
					}
				}
				((Map) bought_goods.get(index))
						.put("goods_return_status", null);
				of.setReturn_goods_info(JSON.toJSONString(return_goods));
			}
			of.setGoods_info(JSON.toJSONString(bought_goods));
			this.orderformService.updateById(of);

			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(0);
			msg.setContent(msg_content);
			msg.setFromUser(SecurityUserHolder.getCurrentUser());
			msg.setToUser(user);
			this.messageService.saveEntity(msg);
		} else {
			json.put("op_title", "审核失败");
		}
		json.put("ret", Boolean.valueOf(true));
		json.put("url", CommUtil.getURL(request)
				+ "/return?currentPage=" + currentPage);
		return_json(JSON.toJSONString(json), response);
	}
	
	/**
	 * 确认退货收货
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "确认退货收货", value = "/return_confirm*", rtype = "seller", rname = "退货管理", rcode = "seller_return", rgroup = "客户服务")
	@RequestMapping({ "/return_confirm" })
	public ModelAndView return_confirm(HttpServletRequest request,
			HttpServletResponse response, String id) {
		User seller = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		ReturnGoodsLog obj = this.returngoodslogService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		seller = seller.getParent() == null ? seller : seller.getParent();
		if ((seller.getStore() != null)
				&& (seller.getStore().getId().equals(obj.getStore_id()))) {
			obj.setGoods_return_status("10");
			this.returngoodslogService.updateById(obj);
			OrderForm of = this.orderformService.selectByPrimaryKey(Long.valueOf(obj.getReturn_order_id()));

			List<Map> bought_goods = this.orderFormTools.queryGoodsInfo(of.getGoods_info());

			List<Map> return_goods = this.orderFormTools.queryGoodsInfo(of.getReturn_goods_info());
			
			Map this_map = null;
			int i = 0;
			int index = -1;
			while (i < bought_goods.size()) {
				if ((return_goods.size() - 1 <= i) && (this_map == null)) {
					if (((Map) return_goods.get(i)).get("return_goods_id")
							.toString().equals(obj.getGoods_id().toString())) {
						if (((Map) return_goods.get(i)).get("goods_gsp_ids")
								.equals(obj.getGoods_gsp_ids())) {
							this_map = (Map) return_goods.get(i);
						}
					}
				}
				if (((Map) bought_goods.get(i)).get("goods_id").toString()
						.equals(obj.getGoods_id().toString())) {
					if (((Map) bought_goods.get(i)).get("goods_gsp_ids")
							.equals(obj.getGoods_gsp_ids())) {
						index = i;
					}
				}
				if ((index == -1) || (this_map == null)) {
					i++;
				}
				if ((this_map != null) && (index != -1)) {
					break;
				}
			}
			if ((index != -1) && (this_map != null)) {
				if (((Map) bought_goods.get(index)).get("goods_count")
						.toString()
						.equals(this_map.get("return_goods_count").toString())) {
					((Map) bought_goods.get(index)).put("goods_return_status",
							Integer.valueOf(8));
				}
			}
			of.setGoods_info(JSON.toJSONString(bought_goods));
			this.orderformService.updateById(of);
		}
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("op_title", "确认退货成功!");
		mv.addObject("url", CommUtil.getURL(request) + "/return");
		return mv;
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
}
