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


	/**
	 * 查询页面导航
	 * 
	 * @param position
	 *            导航位置，-1为顶部，0为中间，1为底部
	 * @param count
	 *            导航数目，查询导航数目，-1为查询所有
	 * @return
	 */
	public List<Navigation> queryNav(int location, int count) {
		List<Navigation> navs = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("display", Boolean.valueOf(true));
		params.put("location", Integer.valueOf(location));
		params.put("type_no", "sparegoods");
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		if(count == -1){
			count = Integer.MAX_VALUE;
		}
		
		navs = this.navService.queryPageList(params, 0, count);
		
		return navs;
	}


	/**
	 * 查询页面导航
	 * 
	 * @param position
	 *            导航位置，-1为顶部，0为中间，1为底部
	 * @param count
	 *            导航数目，查询导航数目，-1为查询所有
	 * @return
	 */
	public List<Navigation> queryNavRedPig(int location, int count) {
		List<Navigation> navs = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("display", Boolean.valueOf(true));
		params.put("location", Integer.valueOf(location));
		params.put("type_no", "sparegoods");
		
		if(count == -1){
			count = Integer.MAX_VALUE;
		}
		
		navs = this.navService.queryPageList(params, 0, count);
		
		return navs;
	}

}
