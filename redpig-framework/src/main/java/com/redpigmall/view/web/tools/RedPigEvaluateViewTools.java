package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.RedPigFreeApplyLogService;

/**
 * 
 * <p>
 * Title: RedPigEvaluateViewTools.java
 * </p>
 * 
 * <p>
 * Description:商品评价查询类，用来查询商品评价信息并在前台显示
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
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class RedPigEvaluateViewTools {
	@Autowired
	protected RedPigEvaluateService evaluateService;
	@Autowired
	protected RedPigAccessoryService accessoryService;
	@Autowired
	protected RedPigFreeApplyLogService freeapplylogService;

	public List<Evaluate> queryByEva(String goods_id, String eva) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		List<Evaluate> list = Lists.newArrayList();
		params.put("evaluate_type", "goods");
		params.put("evaluate_status", Integer.valueOf(0));
		if (eva.equals("100")) {
			list = this.evaluateService.queryPageList(params);
		} else if (eva.equals("all")) {
			list = this.evaluateService.queryPageList(params);
		} else {
			params.put("evaluate_buyer_val",
					Integer.valueOf(CommUtil.null2Int(eva)));
			list = this.evaluateService.queryPageList(params);
		}
		return list;
	}

	public List<FreeApplyLog> queryByfal(String goods_id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("freegoods_id", CommUtil.null2Long(goods_id));
		List<FreeApplyLog> list = this.freeapplylogService
				.queryPageList(params);
		return list;
	}

	public List queryEvaImgSrc(String ids) {
		List list = Lists.newArrayList();
		if ((ids != null) && (!ids.equals(""))) {
			for (String str : ids.split(",")) {
				if ((str != null) && (!str.equals(""))) {
					Accessory img = this.accessoryService
							.selectByPrimaryKey(CommUtil.null2Long(str));
					if (img != null) {
						list.add(img);
					}
				}
			}
		}
		return list;
	}
}
