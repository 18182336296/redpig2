package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.service.RedPigOrderFormService;


/**
 * 
 * <p>
 * Title: RedPigOrderViewTools.java
 * </p>
 * 
 * <p>
 * Description: 前端订单处理工具类，用来查询订单信息显示在前端
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
 * @date 2014-5-22
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Component
public class RedPigOrderViewTools {
	@Autowired
	private RedPigOrderFormService orderFormService;

	public int query_user_order(String order_status) {
		Map<String, Object> params = Maps.newHashMap();
		int status = -1;
		if (order_status.equals("order_submit")) {
			status = 10;
		}
		if (order_status.equals("order_pay")) {
			status = 20;
		}
		if (order_status.equals("order_shipping")) {
			status = 30;
		}
		if (order_status.equals("order_receive")) {
			status = 40;
		}
		if (order_status.equals("order_finish")) {
			status = 60;
		}
		if (order_status.equals("order_cancel")) {
			status = 0;
		}
		
		params.put("status", Integer.valueOf(status));
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		List<OrderForm> ofs = this.orderFormService.queryPageList(params);
		
		return ofs.size();
	}

	public int query_store_order(String order_status) {
		if (SecurityUserHolder.getCurrentUser().getStore() != null) {
			Map<String, Object> params = Maps.newHashMap();
			int status = -1;
			if (order_status.equals("order_submit")) {
				status = 10;
			}
			if (order_status.equals("order_pay")) {
				status = 20;
			}
			if (order_status.equals("order_shipping")) {
				status = 30;
			}
			if (order_status.equals("order_receive")) {
				status = 40;
			}
			if (order_status.equals("order_finish")) {
				status = 60;
			}
			if (order_status.equals("order_cancel")) {
				status = 0;
			}
			
			params.put("status", Integer.valueOf(status));
			params.put("store_id", SecurityUserHolder.getCurrentUser().getStore().getId());
			
			List<OrderForm> ofs = this.orderFormService.queryPageList(params);
			
			return ofs.size();
		}
		return 0;
	}
}
