package com.redpigmall.module.sns.view.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: RedPigFreeTools.java
 * </p>
 * 
 * <p>
 * Description:0元试用相关工具类
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
 * @date 2014-11-11
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Component
public class RedPigSnsFreeTools {
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigUserService userService;
	
	public Goods queryGoods(String goods_id) {
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		return goods;
	}
	
	public User queryEvaluteUser(String user_id) {
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(user_id));
		return user;
	}
}
