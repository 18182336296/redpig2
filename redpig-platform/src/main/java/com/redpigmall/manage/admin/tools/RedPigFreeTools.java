package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.domain.FreeClass;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigFreeApplyLogService;
import com.redpigmall.service.RedPigFreeClassService;
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
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigFreeTools {
	@Autowired
	protected RedPigFreeClassService freeClassService;
	@Autowired
	protected RedPigGoodsService goodsService;
	@Autowired
	protected RedPigUserService userService;
	@Autowired
	protected RedPigFreeApplyLogService freeApplyLogService;

	public FreeClass queyFreeClass(String class_id) {
		FreeClass fc = this.freeClassService.selectByPrimaryKey(CommUtil
				.null2Long(class_id));

		return fc;
	}

	public Goods queryGoods(String goods_id) {
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil
				.null2Long(goods_id));
		return goods;
	}

	public List<User> queryUser(String free_id) {
		List<User> users = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		map.put("freegoods_id", CommUtil.null2Long(free_id));
		List<FreeApplyLog> fals = this.freeApplyLogService.queryPageList(map,
				0, 10);

		for (FreeApplyLog fal : fals) {
			User user = this.userService.selectByPrimaryKey(CommUtil
					.null2Long(fal.getUser_id()));
			if (user != null) {
				users.add(user);
			}
		}
		return users;
	}

	public User queryEvaluteUser(String user_id) {
		User user = this.userService.selectByPrimaryKey(CommUtil
				.null2Long(user_id));
		return user;
	}
}
