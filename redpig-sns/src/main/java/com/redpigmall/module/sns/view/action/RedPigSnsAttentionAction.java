package com.redpigmall.module.sns.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.sns.manage.base.BaseAction;
import com.redpigmall.domain.SnsAttention;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigSnsAttentionAction.java
 * </p>
 * 
 * <p>
 * Description: 个人主页 关注及取消关注功能
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
 * @date 2014-12-18
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class RedPigSnsAttentionAction extends BaseAction{
	
	/**
	 * sns关注买家
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "sns关注买家", value = "/sns/attention_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/sns/attention_save" })
	public void attention_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id) {
		int ret = 1;
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((user != null) && (SecurityUserHolder.getCurrentUser() != null)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("fromUser_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("toUser_id", user.getId());
			
			List<SnsAttention> list = this.snsAttentionService.queryPageList(params);
			
			if (list.size() == 0) {
				SnsAttention sa = new SnsAttention();
				sa.setFromUser(SecurityUserHolder.getCurrentUser());
				sa.setToUser(user);
				sa.setAddTime(new Date());
				this.snsAttentionService.saveEntity(sa);
				ret = 0;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * sns取消关注
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "sns取消关注", value = "/sns/attention_cancel*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/sns/attention_cancel" })
	public void attention_cancel(HttpServletRequest request,
			HttpServletResponse response, String id) {
		int ret = 1;
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((user != null) && (SecurityUserHolder.getCurrentUser() != null)) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("fromUser_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("toUser_id", user.getId());
			
			List<SnsAttention> list = this.snsAttentionService.queryPageList(params);
			
			for (SnsAttention sa : list) {
				this.snsAttentionService.deleteById(sa.getId());
			}
			ret = 0;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
