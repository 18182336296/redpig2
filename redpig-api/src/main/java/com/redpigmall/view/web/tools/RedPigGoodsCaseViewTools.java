package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCase;
import com.redpigmall.service.RedPigGoodsCaseService;
import com.redpigmall.service.RedPigGoodsService;

/**
 * 
 * <p>
 * Title: RedPigGoodsCaseViewTools.java
 * </p>
 * 
 * <p>
 * Description: 橱窗工具类
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
 * @author lixiaoyang
 * 
 * @date 2014-11-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Component
public class RedPigGoodsCaseViewTools {
	@Autowired
	private RedPigGoodsCaseService goodscaseService;
	@Autowired
	private RedPigGoodsService goodsService;

	public List<GoodsCase> queryGoodsCase(String case_id) {
		Map<String,Object> params = Maps.newHashMap();
		params.put("case_id", case_id);
		params.put("display", Integer.valueOf(1));
		
		List<GoodsCase> list = this.goodscaseService.queryPageList(params,0, 5);
		
		return list;
	}

	@SuppressWarnings("rawtypes")
	public List<Goods> queryCaseGoods(String case_content) {
		if ((case_content != null) && (!case_content.equals(""))) {
			List list = JSON.parseArray(case_content);
			List<Goods> goods_list = Lists.newArrayList();
			if (list.size() > 5) {
				for (Object id : list.subList(0, 5)) {
					Map<String,Object> params = Maps.newHashMap();
					params.put("id", CommUtil.null2Long(id));
					
					List<Goods> objs = this.goodsService.queryPageList(params,0, 1);
					
					if (objs.size() > 0) {
						goods_list.add((Goods) objs.get(0));
					}
				}
			} else {
				for (Object id : list) {
					Map<String,Object> params = Maps.newHashMap();
					params.put("id", CommUtil.null2Long(id));
					
					List<Goods> objs = this.goodsService.queryPageList(params,0, 1);
					
					if (objs.size() > 0) {
						goods_list.add((Goods) objs.get(0));
					}
				}
			}
			return goods_list;
		}
		return null;
	}
}
