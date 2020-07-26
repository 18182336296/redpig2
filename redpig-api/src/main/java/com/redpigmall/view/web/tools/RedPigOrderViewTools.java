package com.redpigmall.view.web.tools;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.service.RedPigOrderFormService;


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
		params.put("order_status", Integer.valueOf(status));
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		return this.orderFormService.selectCount(params);
		
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
			params.put("order_status", Integer.valueOf(status));
			params.put("store_id", SecurityUserHolder.getCurrentUser()
					.getStore().getId());
			
			return this.orderFormService.selectCount(params);
		}
		return 0;
	}
}
