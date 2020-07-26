package com.redpigmall.module.chatting.view.tools;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigChattingUserService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: RedPigChattingViewTools.java
 * </p>
 * 
 * <p>
 * Description: 客服工具
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Component
public class RedPigChattingViewTools {
	@Autowired
	private RedPigChattingUserService chattingUserService;
	@Autowired
	private RedPigUserService userService;
	
	/**
	 * 获取当前聊天ID
	 * @return
	 */
	public Long getCurrentServiceId() {
		User service = SecurityUserHolder.getCurrentUser();
		if (service == null) {
			return null;
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("chatting_user_id", service.getId());
		List<ChattingUser> cusers = this.chattingUserService.queryPageList(params, 0, 1);
		if (cusers.size() > 0) {
			return CommUtil.null2Long(cusers.get(0).getId());
		}
		ChattingUser cu = new ChattingUser();
		cu.setAddTime(new Date());
		cu.setChatting_name(service.getUserName());
		cu.setChatting_user_id(service.getId());
		cu.setChatting_user_name(service.getUserName());
		if (service.getUserRole().equals("SELLER")) {
			cu.setChatting_user_form("seller");
		} else {
			cu.setChatting_user_form("plat");
		}
		cu.setChatting_status(1);
		this.chattingUserService.saveEntity(cu);
		return cu.getChatting_user_id();
	}

	public Long getServiceIdByUser(User user) {
		Long service_id = null;
		Map<String, Object> params = Maps.newHashMap();
		params.put("chatting_user_id", user.getId());
		List<ChattingUser> cusers = this.chattingUserService.queryPageList(
				params, 0, 1);
		if (cusers.size() > 0) {
			service_id = CommUtil.null2Long(cusers.get(0).getId());
		}
		return service_id;
	}

	public String getChattingUserToken(Long user_id) {
		String token = "";
		User user = this.userService.selectByPrimaryKey(user_id);
		if ((user.getApp_seller_login_token() != null)
				&& (!user.getApp_seller_login_token().equals(""))) {
			token = user.getApp_seller_login_token();
		} else {
			token = CommUtil.randomString(12) + user.getId();
			user.setApp_seller_login_token(token.toLowerCase());
			this.userService.updateById(user);
		}
		return token;
	}

	public String getChattingServiceToken(Long service_id) {
		String token = "";
		ChattingUser cu = this.chattingUserService.selectByPrimaryKey(service_id);
		if (cu != null) {
			User seller = this.userService.selectByPrimaryKey(cu.getChatting_user_id());
			if ((seller.getApp_seller_login_token() != null)
					&& (!seller.getApp_seller_login_token().equals(""))) {
				token = seller.getApp_seller_login_token();
			} else {
				token = CommUtil.randomString(12) + seller.getId();
				seller.setApp_seller_login_token(token.toLowerCase());
				this.userService.updateById(seller);
			}
		}
		return token;
	}

	public String getChattingServiceName(Long service_id) {
		ChattingUser cu = this.chattingUserService
				.selectByPrimaryKey(service_id);
		return cu.getChatting_name();
	}

	public ChattingUser getChattingService(Long service_id) {
		return this.chattingUserService.getObjByProperty("chatting_user_id", "=", service_id);
//		return this.chattingUserService.selectByPrimaryKey(service_id);
	}
}
