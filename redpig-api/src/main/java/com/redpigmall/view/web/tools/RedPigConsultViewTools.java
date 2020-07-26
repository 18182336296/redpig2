package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Consult;
import com.redpigmall.service.RedPigConsultService;

/**
 * 
 * <p>
 * Title: RedPigConsultViewTools.java
 * </p>
 * 
 * <p>
 * Description: 商品咨询管理类，用于前端velocity中的信息查询并显示
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
 * @date 2014-9-29
 * 
 * @version redpigmall_b2b2c 2016
 */
@Component
public class RedPigConsultViewTools {
	@Autowired
	private RedPigConsultService consultService;

	/**
	 * 根据分类查询所有该分类的商品咨询
	 * 
	 * @param type
	 *            咨询类型
	 * @return 返回商品咨询列表
	 */
	public List<Consult> queryByType(String type, String goods_id) {
		List<Consult> list = Lists.newArrayList();
		if (!CommUtil.null2String(type).equals("")) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("consult_type", CommUtil.null2String(type));
			params.put("goods_id", CommUtil.null2Long(goods_id));
			
			list = this.consultService.queryPageList(params);
			
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("goods_id", CommUtil.null2Long(goods_id));
			
			list = this.consultService.queryPageList(params);
			
		}
		return list;
	}
}
