package com.redpigmall.module.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Praise;
import com.redpigmall.domain.ReplyContent;
import com.redpigmall.module.weixin.view.action.base.BaseAction;

/**
 * <p>
 * Title: RedPigWeixinReplyContentViewAction.java
 * </p>
 * 
 * <p>
 * Description: 微信回复内容
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
 * @date 2017-4-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigWeixinReplyContentViewAction extends BaseAction {
	/**
	 * 用户中心
	 * @param request
	 * @param response
	 * @param id
	 * @param user_name
	 * @return
	 */
	@SecurityMapping(title = "用户中心", value = "/reply*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_reply", rgroup = "移动端用户中心")
	@RequestMapping({ "/reply" })
	public ModelAndView reply_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String user_name) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/reply_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String name = this.configService.getSysConfig().getWeixin_account();
		if ((id != null) && (!"".equals(id)) && (user_name != null)
				&& (!"".equals(user_name))) {
			ReplyContent rc = this.replycontentService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			Map<String, Object> map = Maps.newHashMap();
			map.put("praise_info_id", CommUtil.null2Long(id));
			map.put("name", user_name);
			
			List<Praise> list = this.praiseService.queryPageList(map);
			
			if (list.size() > 0) {
				Praise p = (Praise) list.get(0);
				if ("add".equals(p.getStatus())) {
					mv.addObject("p_status", "yes");
				} else {
					p.setStatus("cut");
					mv.addObject("p_status", "no");
					this.praiseService.updateById(p);
				}
			} else {
				Praise p = new Praise();
				p.setPraise_info(rc);
				p.setName(user_name);
				p.setStatus("cut");
				this.praiseService.saveEntity(p);
				mv.addObject("p_status", "no");
			}
			if ((rc.getImg() != null) && (!"".equals(rc.getImg()))) {
				String img_path = CommUtil.getURL(request) + "/"
						+ rc.getImg().getPath() + "/" + rc.getImg().getName();
				mv.addObject("img_path", img_path);
			}
			if ((rc.getImg_1() != null) && (!"".equals(rc.getImg_1()))) {
				String img_path_1 = CommUtil.getURL(request) + "/"
						+ rc.getImg_1().getPath() + "/"
						+ rc.getImg_1().getName();
				mv.addObject("img_path_1", img_path_1);
			}
			mv.addObject("title", rc.getTitle());
			mv.addObject("content", rc.getContent());
			System.out.println(rc.getContent());
			mv.addObject("time", CommUtil.formatShortDate(rc.getAddTime()));
			mv.addObject("name", name);
			mv.addObject("id", id);
			mv.addObject("count", Integer.valueOf(rc.getCount()));
			int counter = rc.getCounter();
			counter++;
			rc.setCounter(counter);
			this.replycontentService.updateById(rc);
			mv.addObject("counter", Integer.valueOf(counter));
			mv.addObject("user_name", user_name.toString());
			System.out.println(mv.getModel().get("user_name"));
		}
		return mv;
	}
	
	/**
	 * 用户中心
	 * @param request
	 * @param response
	 * @param id
	 * @param status
	 * @param user_name
	 */
	@SecurityMapping(title = "用户中心", value = "/count_ajax*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_count_ajax", rgroup = "移动端用户中心")
	@RequestMapping({ "/count_ajax" })
	public void count_ajax(HttpServletRequest request,
			HttpServletResponse response, String id, String status,
			String user_name) {
		ReplyContent rc = this.replycontentService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		int count = rc.getCount();
		Map<String, Object> map = Maps.newHashMap();
		map.put("praise_info_id", CommUtil.null2Long(id));
		map.put("name", CommUtil.null2String(user_name));
		
		List<Praise> list = this.praiseService.queryPageList(map);
		
		if (list.size() > 0) {
			Praise p = (Praise) list.get(0);
			if ("add".equals(status)) {
				count++;
				p.setStatus(status);
				this.praiseService.updateById(p);
			} else if ("cut".equals(status)) {
				if (count > 0) {
					count--;
				} else {
					count = 0;
				}
				p.setStatus(status);
				this.praiseService.updateById(p);
			}
		}
		rc.setCount(count);
		this.replycontentService.updateById(rc);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(count);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
