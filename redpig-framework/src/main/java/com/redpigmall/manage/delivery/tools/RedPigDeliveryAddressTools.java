package com.redpigmall.manage.delivery.tools;

import org.springframework.stereotype.Component;

/**
 * 
 * <p>
 * Title: RedPigDeliveryAddressTools.java
 * </p>
 * 
 * <p>
 * Description: 自提点有关操作工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @date 2014-11-21
 * 
 * @version redpigmall_b2b2c_2016
 */
@Component
public class RedPigDeliveryAddressTools {
	/**
	 * 查询自提的服务时间
	 * 
	 * @param str
	 * @return
	 */
	public String query_service_day(String str) {
		String[] strings = str.split(",");
		String service_day = "";
		for (String s : strings) {
			service_day = service_day + " "
					+ query_what_day(Integer.valueOf(s).intValue());
		}
		return service_day;
	}

	public String query_what_day(int key) {
		switch (key) {
		case 1:
			return "星期一";
		case 2:
			return "星期二";
		case 3:
			return "星期三";
		case 4:
			return "星期四";
		case 5:
			return "星期五";
		case 6:
			return "星期六";
		case 7:
			return "星期日";
		}
		return null;
	}
}
