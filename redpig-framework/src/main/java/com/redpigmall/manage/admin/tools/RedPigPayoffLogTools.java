package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: RedPigPayoffLogTools.java<／p>
 * 
 * <p>
 * Description: 结算账单工具类 <／p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015<／p>
 * 
 * <p>
 * Company: www.redpigmall.net<／p>
 * 
 * @author redpig
 * 
 * @date 2014-5-6
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigPayoffLogTools {

	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigOrderFormService ofService;


	@SuppressWarnings("rawtypes")
	public List<Map> queryGoodsInfo(String json) {
		List<Map> map_list = JSON.parseArray(json, Map.class);
		return map_list;
	}

	public OrderForm queryOrderInfo(String order_id) {
		OrderForm of = new OrderForm();
		if ((order_id != null) && (!order_id.equals(""))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("order_id", order_id);
			params.put("store_id", user.getStore().getId().toString());
			params.put("orderBy", "addTime");
			params.put("orderType", "asc");
			
			List<OrderForm> ofs = this.ofService.queryPageList(params,0,1);
			
			if (ofs.size() > 0) {
				of = (OrderForm) ofs.get(0);
			}
		}
		return of;
	}

	public OrderForm adminqueryOrderInfo(String order_id) {
		OrderForm of = new OrderForm();
		if ((order_id != null) && (!order_id.equals(""))) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("order_id", order_id);
			params.put("orderBy", "addTime");
			params.put("orderType", "asc");
			List<OrderForm> ofs = this.ofService.queryPageList(params, 0,1);
			if (ofs.size() > 0) {
				of = (OrderForm) ofs.get(0);
			}
		}
		return of;
	}
}
