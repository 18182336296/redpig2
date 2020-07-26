package com.redpigmall.manage.seller.tools;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;

/**
 * 
 * <p>
 * Title: RedPigMenuTools.java
 * </p>
 * 
 * <p>
 * Description: 菜单管理工具，V1.3版开始使用，卖家中心快捷菜单采用json管理，该工具使用解析json数据
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
 * @date 2014-6-10
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigMenuTools {
	
	@SuppressWarnings("rawtypes")
	public List<Map> generic_seller_quick_menu(String menu_json) {
		List<Map> list = Lists.newArrayList();
		if ((menu_json != null) && (!menu_json.equals(""))) {
			list = JSON.parseArray(menu_json, Map.class);
		}
		return list;
	}
}
