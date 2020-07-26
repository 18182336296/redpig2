package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.domain.Navigation;
import com.redpigmall.service.RedPigNavigationService;


/**
 * 
 * <p>
 * Title: RedPigNavViewTools.java
 * </p>
 * 
 * <p>
 * Description:前台导航工具类，查询显示对应的导航信息
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
 * @date 2014-8-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigNavViewTools {
	@Autowired
	private RedPigNavigationService navService;


	public List<Navigation> queryNav(int location, int count) {
		List<Navigation> navs = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("display", Boolean.valueOf(true));
		params.put("location", Integer.valueOf(location));
		params.put("type", "sparegoods");
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		navs = this.navService.queryPageList(params, 0, count);
		return navs;
	}
}
