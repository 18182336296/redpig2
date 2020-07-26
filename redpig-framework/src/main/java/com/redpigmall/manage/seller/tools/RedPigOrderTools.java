package com.redpigmall.manage.seller.tools;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * <p>
 * Title: RedPigOrderTools.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心订单处理工具类，用来计算订单收货倒计时等等功能
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
 * @date 2014-11-12
 * 
 * @version redpigmall_b2b2c 2016
 */
@Component
public class RedPigOrderTools {
	@Autowired
	private RedPigOrderFormService orderFormService;
	@Autowired
	private RedPigSysConfigService configService;

	public Date cal_confirm_time(String order_id) {
		OrderForm obj = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));

		if (obj != null) {
			Date ship_time = obj.getShipTime();
			Calendar cal = Calendar.getInstance();
			cal.setTime(ship_time);
			cal.add(6, this.configService.getSysConfig()
					.getAuto_order_confirm() + CommUtil.null2Int(obj.getOrder_confirm_delay()));
			Date confirm_time = cal.getTime();
			return confirm_time;
		}
		return null;
	}
}
