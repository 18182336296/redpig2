package com.redpigmall.manage.admin.action.luckydraw;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.User;
import com.redpigmall.manage.admin.action.base.BaseAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Title: RedPigNukeGoodsManagerAction.java
 * 
 * Description:抽奖订单管理
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月30日 下午8:52:48
 * 
 * @version redpigmall_b2b2c v8.0 2018版  
 */
@Controller
public class RedPigLuckydrawOrderManagerAction extends BaseAction {

	/**
	 * 抽奖订单列表
	 * @param request
	 * @param response
	 * @param order_status
	 * @param type
	 * @param type_data
	 * @param payment
	 * @param beginTime
	 * @param endTime
	 * @param begin_price
	 * @param end_price
	 * @param currentPage
	 * @param order_cat
	 * @return
	 */
	@RequestMapping({ "/luckydraw_order_list" })
	public ModelAndView nuke_order_list(HttpServletRequest request,
										HttpServletResponse response,String order_id,String user_name,String mobile,
										String order_status, String type,String status,
										String type_data, String payment, String beginTime, String endTime,
										String begin_price, String end_price, String currentPage,String order_cat) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw/luckydraw_order_list.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> params = this.redPigQueryTools.getParams(currentPage, "addTime", "desc");

		//3代表抽奖订单
		params.put("order_cat", 4);

		if (!CommUtil.null2String(order_status).equals("")) {
			if ("20".equals(order_status)) {
				Set<Integer> statuss = Sets.newTreeSet();
				statuss.add(Integer.valueOf(16));
				statuss.add(Integer.valueOf(20));

				params.put("order_statuss", statuss);
			} else {
				params.put("order_status", CommUtil.null2Int(order_status));
			}
		}
		if (!CommUtil.null2String(type_data).equals("")) {
			if (type.equals("store")) {
				params.put("store_name", type_data);
			}

			if (type.equals("buyer")) {
				params.put("user_name", type_data);
			}

			if (type.equals("order")) {
				params.put("order_id", type_data);
			}
		}
		if (CommUtil.null2String(payment).equals("alipay")) {
			params.put("payment_mark", payment);
			params.put("payment_mark2", "alipay_app");
			params.put("payment_mark3", "alipay_wap");

		} else if (CommUtil.null2String(payment).equals("apyafter")) {
			params.put("payType", "payafter");
		} else if (CommUtil.null2String(payment).equals("wx_app")) {
			params.put("payment_mark", "wx_app");
			params.put("payment_mark2", "wx_pay");

		} else if (CommUtil.null2String(payment).equals("unionpay")) {
			params.put("payment_mark", "unionpay");
		} else if (!CommUtil.null2String(payment).equals("")) {
			params.put("payment_mark", payment);
		}
		if (!CommUtil.null2String(beginTime).equals("")) {
			params.put("addTime_more_than_equal", Timestamp.valueOf(beginTime));
		}
		if (!CommUtil.null2String(endTime).equals("")) {

			params.put("addTime_less_than_equal", Timestamp.valueOf(endTime));
		}
		if (!CommUtil.null2String(begin_price).equals("")) {
			params.put("totalPrice_more_than_equal", CommUtil.null2Double(begin_price));

		}
		if (!CommUtil.null2String(end_price).equals("")) {
			params.put("totalPrice_less_than_equal", CommUtil.null2Double(end_price));

		}
		if (!CommUtil.null2String(order_id).equals("")){
			params.put("order_id", order_id);
		}
		if (!CommUtil.null2String(user_name).equals("")){
			params.put("user_name", user_name);
		}
		if (!CommUtil.null2String(mobile).equals("")){
			//通过手机号查询，先查出user
			Map<String, Object> map = new HashMap<>();
			map.put("mobile",mobile);
			List<User> users = this.userService.selectObjByProperty(map);
			if (users!=null && !users.isEmpty())
			params.put("user_id", users.get(0).getId());
		}

		IPageList pList = this.redPigOrderFormService.list(params);

		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("order_status", order_status);
		mv.addObject("type", type);
		mv.addObject("type_data", type_data);
		mv.addObject("payment", payment);
		mv.addObject("beginTime", beginTime);
		mv.addObject("endTime", endTime);
		mv.addObject("begin_price", begin_price);
		mv.addObject("end_price", end_price);
		mv.addObject("order_id", order_id);
		mv.addObject("user_name", user_name);
		mv.addObject("mobile", mobile);
		mv.addObject("status",status);
		mv.addObject("orderFormTools", this.redPigOrderFormTools);
		return mv;
		
	}


	/**
	 *
	 * @param request
	 * @param response
	 * @param id
	 * @param view_type
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "订单详情", value = "/order_view*", rtype = "admin", rname = "订单管理", rcode = "order_admin", rgroup = "交易")
	@RequestMapping({ "/luckydraw_order_view" })
	public ModelAndView order_view(HttpServletRequest request,
								   HttpServletResponse response, String id, String view_type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/luckydraw/luckydraw_order_view.html",
				this.redPigSysConfigService.getSysConfig(),
				this.redPigUserConfigService.getUserConfig(), 0, request, response);
		OrderForm obj = this.redPigOrderFormService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			// 获取发票信息
			String temp = obj.getSpecial_invoice();
			if ((temp != null) && (!"".equals(temp))) {
				Map of_va = JSON.parseObject(temp);
				mv.addObject("of_va", of_va);
			}
		}
		mv.addObject("express_company_name", this.redPigOrderFormTools.queryExInfo(obj.getExpress_info(), "express_company_name"));
		mv.addObject("orderFormTools", this.redPigOrderFormTools);
		mv.addObject("obj", obj);
		mv.addObject("view_type", view_type);
		return mv;
	}
}

