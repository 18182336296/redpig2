package com.redpigmall.api.tools;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * <p>
 * Title: RedPigMaps.java
 * </p>
 * 
 * <p>
 * Description:mybatis查询条件通用工具类
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
 * @author redpig
 * 
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class RedPigMaps {
	public static Map<String, Object> newMap() {
		return Maps.newHashMap();
	}

	public static Map<String, Object> newParent(Object obj) {
		Map<String, Object> maps = newMap();

		maps.put("parent", obj);
		return maps;
	}

	public static Map<String, Object> newParent(Object obj, String orderBy,String orderType) {
		Map<String, Object> maps = newMap();
		
		maps.put("parent", obj);
		maps.put("orderBy", orderBy);
		maps.put("orderType", orderType);

		return maps;
	}

	public static Map<String, Object> newMaps(String orderBy,String orderType) {
		Map<String, Object> maps = newMap();
		
		maps.put("orderBy", orderBy);
		maps.put("orderType", orderType);
		
		return maps;
	}
	
}
