package com.redpigmall.module.sns.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.sns.manage.base.BaseAction;
import com.redpigmall.domain.User;
import com.redpigmall.domain.UserShare;

/**
 * 
 * <p>
 * Title: RedPigSnsUserShareAction.java
 * </p>
 * 
 * <p>
 * Description: 前台分享控制器
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
 * @date 2014-12-12
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSnsUserShareAction extends BaseAction{
	/**
	 * 商品分享
	 * @param response
	 * @param share_content
	 * @param share_goods_id
	 * @param share_goods_name
	 * @param share_goods_photo
	 */
	@SecurityMapping(title = "商品分享", value = "/share_items*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/share_items" })
	public void share_goods(HttpServletResponse response, String share_content,
			String share_goods_id, String share_goods_name,
			String share_goods_photo) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		UserShare userShare = new UserShare();
		userShare.setAddTime(new Date());
		userShare.setUser_name(user.getUserName());
		userShare.setUser_id(user.getId());
		userShare.setShare_content(share_content);
		userShare.setShare_goods_id(CommUtil.null2Long(share_goods_id));
		userShare.setShare_goods_name(share_goods_name);
		userShare.setShare_goods_photo(share_goods_photo);
		this.userShareService.saveEntity(userShare);
		boolean ret = true;
		if (ret) {
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
}
