package com.redpigmall.manage.buyer.tools;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Evaluate;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * <p>
 * Title: RedPigEvaluateTools.java
 * </p>
 * 
 * <p>
 * Description: 评价工具类
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
 * @date 2014-5-4
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigEvaluateTools {
	@Autowired
	private RedPigSysConfigService configService;

	@Autowired
	private RedPigEvaluateService evaluateService;

	/**
	 * 查询订单中某件是否评价
	 * 
	 * @param order_id
	 * @param goods_id
	 * @return
	 */
	public Evaluate query_order_evaluate(Object order_id, Object goods_id) {
		Map<String,Object> para = Maps.newHashMap();
		para.put("of_id", CommUtil.null2Long(order_id));
		para.put("evaluate_goods_id", CommUtil.null2Long(goods_id));
		
		List<Evaluate> list = this.evaluateService.queryPageList(para);
		
		if (list.size() > 0) {
			return (Evaluate) list.get(0);
		}
		return null;
	}

	/**
	 * 判断是否可修改评价
	 * 
	 * @param date
	 * @return
	 */
	public int evaluate_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000L;
			if (day <= config.getEvaluate_edit_deadline()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 判断是否可追加评价
	 * 
	 * @param date
	 * @return
	 */
	public int evaluate_add_able(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			SysConfig config = this.configService.getSysConfig();
			long day = (end - begin) / 86400000L;
			if (day <= config.getEvaluate_add_deadline()) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * 计算今天到指定时间天数
	 * 
	 * @param date
	 * @return
	 */
	public int how_soon(Date date) {
		if (date != null) {
			long begin = date.getTime();
			long end = new Date().getTime();
			long day = (end - begin) / 86400000L;
			return CommUtil.null2Int(Long.valueOf(day));
		}
		return 999;
	}
}
