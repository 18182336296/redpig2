package com.redpigmall.manage.admin.action.deal.refund;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupInfo;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.PayoffLog;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.domain.RefundLog;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;

/**
 * 
 * 
 * <p>
 * Title: RefundManageAction.java
 * </p>
 * 
 * <p>
 * Description: 平台向买家进行退款，退款统一给买家发放预存款，买家通过预存款兑换现金
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
 * @date 2014年5月14日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "unused" })
@Controller
public class RedPigRefundManageAction extends  BaseAction{
	/**
	 * 商品退款列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param name
	 * @param user_name
	 * @param refund_status
	 * @return
	 */
	@SecurityMapping(title = "商品退款列表", value = "/refund_list*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping({ "/refund_list" })
	public ModelAndView refund_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String name, String user_name,
			String refund_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/refund_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		params.put("goods_return_status", 10);
		
		params.put("goods_return_status1", 11);
		
		if ((user_name != null) && (!user_name.equals(""))) {
			params.put("user_name", user_name);
		}
		
		if ((name != null) && (!name.equals(""))) {
			params.put("goods_name_like", name);
		}
		
		IPageList pList = this.returngoodslogService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("name", name);
		mv.addObject("user_name", user_name);
		return mv;
	}
	
	/**
	 * 消费码退款列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param group_sn
	 * @param user_name
	 * @param refund_status
	 * @return
	 */
	@SecurityMapping(title = "消费码退款列表", value = "/groupinfo_refund_list*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping({ "/groupinfo_refund_list" })
	public ModelAndView groupinfo_refund_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String group_sn, String user_name,
			String refund_status) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/groupinfo_refund_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		params.put("status", 5);
		
		
		if ((group_sn != null) && (!group_sn.equals(""))) {
			params.put("group_sn", group_sn);
			
			mv.addObject("group_sn", group_sn);
		}
		
		if ((user_name != null) && (!user_name.equals(""))) {
			
			params.put("user_name", user_name);
			mv.addObject("user_name", user_name);
		}
		if ((refund_status != null) && (!refund_status.equals(""))) {
			params.put("status", CommUtil.null2Int(refund_status));
			
			mv.addObject("refund_status", refund_status);
		}
		
		IPageList pList = this.groupinfoService.list(params);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 查看退款
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "查看退款", value = "/refund_view*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping({ "/refund_view" })
	public ModelAndView refund_view(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/refund_predeposit_modify.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((type != null) && (!type.equals(""))) {
			if (type.equals("groupinfo")) {
				mv.addObject("type", type);
				GroupInfo gi = this.groupinfoService
						.selectByPrimaryKey(CommUtil.null2Long(id));
				User user = this.userService
						.selectByPrimaryKey(gi.getUser_id());
				mv.addObject("refund_money", gi.getLifeGoods().getGroup_price());
				mv.addObject("user", user);
				mv.addObject("gi", gi);
				mv.addObject(
						"msg",
						gi.getLifeGoods().getGg_name() + "消费码"
								+ gi.getGroup_sn() + "退款成功，预存款"
								+ gi.getLifeGoods().getGroup_price()
								+ "元已存入您的账户");
			}
		} else {
			ReturnGoodsLog obj = this.returngoodslogService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			OrderForm of = this.orderFormService.selectByPrimaryKey(Long
					.valueOf(obj.getReturn_order_id()));
			double temp_refund_money = 0.0D;
			if ((of.getCoupon_info() != null)
					&& (!of.getCoupon_info().equals(""))) {
				Map<String, Object> map = this.orderFormTools.queryCouponInfo(of.getCoupon_info());
				BigDecimal rate = new BigDecimal(map.get("coupon_goods_rate")
						.toString());
				BigDecimal old_price = new BigDecimal(obj.getGoods_all_price());
				BigDecimal coupon_price = new BigDecimal(map.get(
						"coupon_amount").toString());
				BigDecimal price = new BigDecimal(map.get("price").toString());
				double refund_money = CommUtil.mul(
						Double.valueOf(1.0D
								- CommUtil.null2Double(coupon_price)
								/ CommUtil.null2Double(price)), old_price);
				temp_refund_money = refund_money;
				mv.addObject("refund_money", Double.valueOf(refund_money));
			} else {
				temp_refund_money = CommUtil.null2Double(obj
						.getGoods_all_price());
				mv.addObject("refund_money", obj.getGoods_all_price());
			}
			mv.addObject("msg", "退货服务号为" + obj.getReturn_service_id()
					+ "的商品退款成功，预存款" + temp_refund_money + "元已存入您的账户");
			if (CommUtil.null2Double(of.getEnough_reduce_amount()) > 0.0D) {
				Map er_info = JSON.parseObject(of.getEnough_reduce_info());
				Iterator<String> it = er_info.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					if (key.substring(0, 1).equals("a")) {
						String key2 = key.substring(4, key.length());
						List list = (List) er_info.get(key2);
						for (Object good_id : list) {
							if (CommUtil.null2Double(good_id) == obj
									.getGoods_id().longValue()) {
								double goods_price = CommUtil.null2Double(obj
										.getGoods_all_price());
								double all = CommUtil.null2Double(er_info.get(
										key).toString());
								double enouhg = CommUtil.null2Double(er_info
										.get("enouhg_" + key2).toString());
								if (all - goods_price < enouhg) {
									double reduce = CommUtil
											.null2Double(er_info.get(
													"reduce_" + key2)
													.toString());
									double return_account = goods_price / all
											* reduce;
									temp_refund_money = CommUtil
											.null2Double(new BigDecimal(
													temp_refund_money
															- return_account));
									mv.addObject("refund_money",
											Double.valueOf(temp_refund_money));
									mv.addObject(
											"msg",
											"退货服务号为"
													+ obj.getReturn_service_id()
													+ "的商品退款成功，预存款"
													+ temp_refund_money
													+ "元已存入您的账户,其中扣除了"
													+ return_account + "元满减金额");
								}
							}
						}
					}
				}
			}
			mv.addObject("obj", obj);
			User user = this.userService.selectByPrimaryKey(obj.getUser_id());
			mv.addObject("user", user);
		}
		return mv;
	}
	
	/**
	 * 退款日志列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @param refund_id
	 * @param user_name
	 * @param return_service_id
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	@SecurityMapping(title = "退款日志列表", value = "/refundlog_list*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping({ "/refundlog_list" })
	public ModelAndView refundlog_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType, String refund_id, String user_name,
			String return_service_id, String beginTime, String endTime) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/refundlog_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps = this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		
		if ((user_name != null) && (!user_name.equals(""))) {
			maps.put("returnLog_userName", user_name);
			
		}
		
		if ((refund_id != null) && (!refund_id.equals(""))) {
			maps.put("refund_id", refund_id);
			
		}
		
		if ((return_service_id != null) && (!return_service_id.equals(""))) {
			maps.put("returnService_id", return_service_id);
			
		}
		
		if (!CommUtil.null2String(beginTime).equals("")) {
			maps.put("add_Time_more_than_equal", CommUtil.formatDate(beginTime));
			
		}
		
		if (!CommUtil.null2String(endTime).equals("")) {
			maps.put("add_Time_less_than_equal", CommUtil.formatDate(beginTime));
		}
		
		
		IPageList pList = this.refundLogService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/refundlog_list.html",
				"", params, pList, mv);
		mv.addObject("refund_id", refund_id);
		mv.addObject("user_name", user_name);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("return_service_id", return_service_id);
		return mv;
	}
	
	/**
	 * 平台退款完成
	 * @param request
	 * @param response
	 * @param user_id
	 * @param amount
	 * @param type
	 * @param info
	 * @param list_url
	 * @param refund_user_id
	 * @param obj_id
	 * @param gi_id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "平台退款完成", value = "/refund_finish*", rtype = "admin", rname = "退款管理", rcode = "refund_log", rgroup = "交易")
	@RequestMapping({ "/refund_finish" })
	public ModelAndView refund_finish(
			HttpServletRequest request, HttpServletResponse response,
			String user_id, String amount, String type, String info,
			String list_url, String refund_user_id, String obj_id, String gi_id) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (this.configService.getSysConfig().getDeposit()) {
			User user = null;
			if ((user_id != null) && (!user_id.equals(""))) {
				user = this.userService.selectByPrimaryKey(CommUtil.null2Long(user_id));
			} else {
				user = this.userService.selectByPrimaryKey(CommUtil.null2Long(refund_user_id));
			}
			user.setAvailableBalance(BigDecimal.valueOf(CommUtil.add(user.getAvailableBalance(), amount)));
			this.userService.updateById(user);
			
			PredepositLog log = new PredepositLog();
			log.setPd_log_admin(SecurityUserHolder.getCurrentUser());
			log.setAddTime(new Date());
			log.setPd_log_amount(BigDecimal.valueOf(CommUtil.null2Double(amount)));
			log.setPd_log_info(info);
			log.setPd_log_user(user);
			log.setPd_op_type("人工退款");
			log.setPd_type("可用预存款");
			this.predepositLogService.saveEntity(log);
			
			
			/**-------------------------------直销佣金扣除Start------------------------------------------*/
			User directSellingFirstParent = user.getDirectSellingParent();
			User directSellingSecondParent = null;
			if(directSellingFirstParent!=null) {
				directSellingSecondParent = directSellingFirstParent.getDirectSellingParent();
			}
			//一级获得佣金
			if(directSellingFirstParent != null) {
				directSellingFirstParent = this.userService.selectByPrimaryKey(directSellingFirstParent.getId());
				
				BigDecimal availableBalance = directSellingFirstParent.getAvailableBalance();
				//第一级佣金:原来余额+订单总金额*佣金比例
				
				availableBalance = availableBalance.subtract(
						BigDecimal.valueOf(
								CommUtil.mul(amount, this.configService.getSysConfig().getDirect_selling_first_level_rate())));
				
				directSellingFirstParent.setAvailableBalance(availableBalance);
				this.userService.updateById(directSellingFirstParent);
				
				log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_user(directSellingFirstParent);
				log.setPd_op_type("获得佣金");//佣金：订单总金额*佣金比例
				log.setPd_log_amount(
						BigDecimal.valueOf(-
								CommUtil.mul(amount, this.configService.getSysConfig().getDirect_selling_first_level_rate())));
				log.setPd_log_info("买家:"+user.getUserName()+" 退单:"+obj_id + " 获得扣除");
				log.setPd_type("可用预存款");
				this.predepositlogService.saveEntity(log);
			}
			
			//二级获得佣金
			if(directSellingSecondParent != null) {
				directSellingSecondParent = this.userService.selectByPrimaryKey(directSellingSecondParent.getId());
				
				BigDecimal availableBalance = directSellingSecondParent.getAvailableBalance();
				//第二级佣金:原来余额+订单总金额*佣金比例
				
				availableBalance = availableBalance.subtract(
						BigDecimal.valueOf(
								CommUtil.mul(amount, this.configService.getSysConfig().getDirect_selling_second_level_rate())));
				
				directSellingSecondParent.setAvailableBalance(availableBalance);
				this.userService.updateById(directSellingSecondParent);
				
				log = new PredepositLog();
				log.setAddTime(new Date());
				log.setPd_log_user(directSellingSecondParent);
				log.setPd_op_type("获得佣金");//佣金：订单总金额*佣金比例
				log.setPd_log_amount(
						BigDecimal.valueOf(-
								CommUtil.mul(amount, this.configService.getSysConfig().getDirect_selling_second_level_rate())));
				log.setPd_log_info("买家:"+user.getUserName()+" 退单:"+obj_id + " 获得扣除");
				log.setPd_type("可用预存款");
				this.predepositlogService.saveEntity(log);
			}
			
			/**------------------------------直销佣金扣除End-------------------------------------------*/
			
			if ((obj_id != null) && (!obj_id.equals(""))) {
				ReturnGoodsLog rgl = this.returnGoodsLogService.selectByPrimaryKey(CommUtil.null2Long(obj_id));
				rgl.setRefund_status(1);
				rgl.setGoods_return_status("11");
				this.returnGoodsLogService.updateById(rgl);
				RefundLog r_log = new RefundLog();
				r_log.setAddTime(new Date());
				r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss",new Date()) + user.getId());
				r_log.setReturnLog_id(rgl.getId());
				r_log.setReturnService_id(rgl.getReturn_service_id());
				r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				r_log.setRefund_log(info);
				r_log.setRefund_type("预存款");
				r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
				r_log.setReturnLog_userName(rgl.getUser_name());
				r_log.setReturnLog_userId(rgl.getUser_id());
				this.refundLogService.saveEntity(r_log);
				OrderForm of = this.orderFormService.selectByPrimaryKey(Long.valueOf(rgl.getReturn_order_id()));
				Goods goods = this.goodsService.selectByPrimaryKey(rgl.getGoods_id());
				if (goods.getGoods_type() == 1) {
					Store store = this.goodsService.selectByPrimaryKey(rgl.getGoods_id()).getGoods_store();
					PayoffLog pol = new PayoffLog();
					pol.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					pol.setAddTime(new Date());
					pol.setGoods_info(of.getReturn_goods_info());
					pol.setRefund_user_id(rgl.getUser_id());
					pol.setSeller(goods.getGoods_store().getUser());
					pol.setRefund_userName(rgl.getUser_name());
					pol.setReturn_service_id(rgl.getReturn_service_id());
					pol.setPayoff_type(-1);
					pol.setPl_info("退款完成");
					BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount));
					BigDecimal mission = BigDecimal.valueOf(CommUtil.subtract(Integer.valueOf(1),rgl.getGoods_commission_rate()));
					BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(Integer.valueOf(0), Double.valueOf(CommUtil.mul(price, mission))));
					pol.setTotal_amount(final_money);
					List<Map> list = Lists.newArrayList();
					Map json = Maps.newHashMap();
					json.put("goods_id", rgl.getGoods_id());
					json.put("goods_name", rgl.getGoods_name());
					json.put("goods_price", rgl.getGoods_price());
					json.put("goods_mainphoto_path",rgl.getGoods_mainphoto_path());
					json.put("goods_commission_rate",rgl.getGoods_commission_rate());
					json.put("goods_count", rgl.getGoods_count());
					json.put("goods_all_price", rgl.getGoods_all_price());
					json.put(
							"goods_commission_price",
							Double.valueOf(CommUtil.mul(
									rgl.getGoods_all_price(),
									rgl.getGoods_commission_rate())));
					json.put("goods_payoff_price", final_money);
					list.add(json);
					pol.setReturn_goods_info(JSON.toJSONString(list));
					pol.setO_id(CommUtil.null2String(Long.valueOf(rgl
							.getReturn_order_id())));
					pol.setOrder_id(of.getOrder_id());
					pol.setCommission_amount(BigDecimal.valueOf(0L));
					pol.setOrder_total_price(final_money);
					this.payoffLogService.saveEntity(pol);
					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_sale_amount(), amount)));
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil.subtract(store.getStore_payoff_amount(), Double.valueOf(CommUtil.mul(price, mission)))));
					this.storeService.updateById(store);
					
					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_sale(), amount)));
					sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil.subtract(sc.getPayoff_all_amount(), Double.valueOf(CommUtil.mul(price, mission)))));
					sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil.add(pol.getReality_amount(),sc.getPayoff_all_amount_reality())));
					this.configService.updateById(sc);
				}
				String msg_content = "成功为订单号：" + of.getOrder_id() + "退款" + amount + "元，请到收支明细中查看。";
				
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(user);
				this.messageService.saveEntity(msg);
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/refund_list");
			}
			if ((gi_id != null) && (!gi_id.equals(""))) {
				GroupInfo gi = this.groupinfoService.selectByPrimaryKey(CommUtil.null2Long(gi_id));
				gi.setStatus(7);
				this.groupinfoService.updateById(gi);
				OrderForm of = this.orderFormService.selectByPrimaryKey(gi.getOrder_id());
				if (of.getOrder_form() == 0) {
					Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(of.getStore_id()));
					PayoffLog pol = new PayoffLog();
					pol.setPl_sn("pl"
							+ CommUtil.formatTime("yyyyMMddHHmmss", new Date())
							+ store.getUser().getId());
					pol.setAddTime(new Date());
					pol.setGoods_info(of.getReturn_goods_info());
					pol.setRefund_user_id(gi.getUser_id());
					pol.setSeller(store.getUser());
					pol.setRefund_userName(gi.getUser_name());
					pol.setPayoff_type(-1);
					pol.setPl_info("退款完成");
					BigDecimal price = BigDecimal.valueOf(CommUtil.null2Double(amount));
					BigDecimal final_money = BigDecimal.valueOf(CommUtil.subtract(Integer.valueOf(0), price));
					pol.setTotal_amount(final_money);
					
					List<Map> Map_list = Lists.newArrayList();
					Map group_map = this.orderFormTools.queryGroupInfo(of
							.getGroup_info());
					Map_list.add(group_map);
					pol.setReturn_goods_info(JSON.toJSONString(Map_list));
					pol.setO_id(of.getId().toString());
					pol.setOrder_id(of.getOrder_id());
					pol.setCommission_amount(BigDecimal.valueOf(0L));
					pol.setOrder_total_price(final_money);
					this.payoffLogService.saveEntity(pol);

					store.setStore_sale_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_sale_amount(), amount)));
					store.setStore_payoff_amount(BigDecimal.valueOf(CommUtil
							.subtract(store.getStore_payoff_amount(), price)));
					this.storeService.updateById(store);

					SysConfig sc = this.configService.getSysConfig();
					sc.setPayoff_all_sale(BigDecimal.valueOf(CommUtil.subtract(
							sc.getPayoff_all_sale(), amount)));
					sc.setPayoff_all_amount(BigDecimal.valueOf(CommUtil
							.subtract(
									sc.getPayoff_all_amount(),
									Double.valueOf(CommUtil.mul(amount,
											Integer.valueOf(0))))));
					sc.setPayoff_all_amount_reality(BigDecimal.valueOf(CommUtil
							.add(pol.getReality_amount(),
									sc.getPayoff_all_amount_reality())));
					this.configService.updateById(sc);
				}
				RefundLog r_log = new RefundLog();
				r_log.setAddTime(new Date());
				r_log.setRefund_id(CommUtil.formatTime("yyyyMMddHHmmss",
						new Date()) + user.getId());
				r_log.setReturnLog_id(gi.getId());
				r_log.setReturnService_id(gi.getGroup_sn());
				r_log.setRefund(BigDecimal.valueOf(CommUtil.null2Double(amount)));
				r_log.setRefund_log(info);
				r_log.setRefund_type("预存款");
				r_log.setRefund_user(SecurityUserHolder.getCurrentUser());
				r_log.setReturnLog_userName(gi.getUser_name());
				r_log.setReturnLog_userId(gi.getUser_id());
				this.refundLogService.saveEntity(r_log);
				String msg_content = "您的团购商品：" + gi.getLifeGoods().getGg_name()
						+ "消费码已经成功退款，退款金额为："
						+ gi.getLifeGoods().getGroup_price() + "，退款消费码:"
						+ gi.getGroup_sn();

				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(0);
				msg.setContent(msg_content);
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(user);
				this.messageService.saveEntity(msg);
				mv.addObject("op_title", "退款成功");
				mv.addObject("list_url", CommUtil.getURL(request)
						+ "/groupinfo_refund_list");
			}
		} else {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "系统未开启预存款");
			mv.addObject("list_url", CommUtil.getURL(request)
					+ "/operation_base_set");
		}
		return mv;
	}
}
