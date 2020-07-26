package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;


import com.redpigmall.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.redpigmall.api.sec.SessionRegistry;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigChattingUserService;
import com.redpigmall.service.RedPigUserService;

/**
 * <p>
 * Title: RedPigUserTools.java
 * </p>
 * 
 * <p>
 * Description: 在线用户查询管理工具类，查询所有登录用户，判断用户是否在线
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
 * @date 2014-7-28
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigUserTools {

	@Autowired
	private RedPigUserService userSerivce;
	@Autowired
	private RedPigChattingUserService chattingUserService;
	@Autowired
	private SessionRegistry sessionRegistry;
	
	/**
	 * 获取所有在线用户列表
	 * 
	 * @return
	 */
	public List<User> query_user() {
		List<User> users = Lists.newArrayList();
		List<String> objs = this.sessionRegistry.getAllPrincipals();
		for (int i = 0; i < objs.size(); i++) {
			User user = this.userSerivce.getObjByProperty("userName","=",CommUtil.null2String(objs.get(i)));
			users.add(user);
		}
		return users;
	}
	
	/**
	 * 根据用户名判断用户是否在线
	 * 
	 * @param userName
	 * @return
	 */
	public boolean userOnLine(String userName) {
		// return this.sessionRegistry.getPrincipals().containsKey(userName);
		List<String> userNames = this.sessionRegistry.getAllPrincipals();
		if (userNames != null)
			for (Object un : userNames) {
				if (un != null && un.equals(userName)) {
					return true;
				}
			}
		return true;
	}

	/**
	 * 判断是否有管理员在线
	 * 
	 * @return
	 */
	public boolean adminOnLine() {
		boolean admin_onLine = false;
		String key = "plat-admin-isOnline";

		Object ret = RedisCache.getObject(key);

		if (CommUtil.null2Boolean(ret)) {
			admin_onLine = true;
		} else {
			Map<String, Object> map = Maps.newHashMap();
			map.put("chatting_user_form", "plat");
			map.put("chatting_status", Integer.valueOf(1));
			List<ChattingUser> users = this.chattingUserService.queryPageList(map);
			for (ChattingUser u : users) {
				if (userOnLine(u.getChatting_user_name())) {
					RedisCache.putObject(key, Boolean.valueOf(true));
					admin_onLine = true;
					break;
				}
			}
		}
		return admin_onLine;
	}
}
