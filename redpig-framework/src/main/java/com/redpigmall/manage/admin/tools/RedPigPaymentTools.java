package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.redpigmall.domain.Payment;
import com.redpigmall.service.RedPigPaymentService;

/**
 * 
 * <p>
 * Title: RedPigPaymentTools.java
 * </p>
 * 
 * <p>
 * Description: 支付方式处理工具类，用来管理支付方式信息，主要包括查询支付方式等
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
 * @date 2014-5-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigPaymentTools {
	@Autowired
	private RedPigPaymentService paymentService;
	
	
	public boolean queryPayment(String mark) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark);
		
		List<Payment> objs = this.paymentService.queryPageList(params);
		
		if (objs.size() > 0) {
			return ((Payment) objs.get(0)).getInstall();
		}
		return false;
	}

	public Map<String,Object> queryShopPayment(String mark) {
		Map<String,Object> ret = Maps.newHashMap();
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", mark);
		List<Payment> objs = this.paymentService.queryPageList(params);
		
		if (objs.size() == 1) {
			ret.put("install",Boolean.valueOf(((Payment) objs.get(0)).getInstall()));
			ret.put("content", ((Payment) objs.get(0)).getContent());
		} else {
			ret.put("install", Boolean.valueOf(false));
			ret.put("content", "");
		}
		return ret;
	}
}
