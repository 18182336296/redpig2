package com.redpigmall.manage.buyer.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.ChattingLog;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigMessageBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:用户中心站内短信控制器
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
 * @date 2014-10-15
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigMessageBuyerAction extends BaseAction{
	
	/**
	 * 站内短信
	 * @param request
	 * @param response
	 * @param type
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "站内短信", value = "/buyer/message*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/message" })
	public ModelAndView message(HttpServletRequest request,
			HttpServletResponse response, String type, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/message.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, null, null);
		
		if ((CommUtil.null2String(type).equals("sys"))
				|| (CommUtil.null2String(type).equals("my"))
				|| (CommUtil.null2String(type).equals(""))) {
			if (CommUtil.null2String(type).equals("")) {
				
				maps.put("toUser_id", SecurityUserHolder.getCurrentUser().getId());
				
				maps.put("type", 1);
			}
			if (CommUtil.null2String(type).equals("my")) {
				
				maps.put("fromUser_id", SecurityUserHolder.getCurrentUser().getId());
				
			}
			if (CommUtil.null2String(type).equals("sys")) {
				maps.put("toUser_id", SecurityUserHolder.getCurrentUser().getId());
				maps.put("type", 0);
			}
			maps.put("parent", -1);
			
	        maps.put("orderBy", "addTime");
	        maps.put("orderType", "desc");
	        
			IPageList pList = this.messageService.queryPagesWithNoRelations(maps);
			
			CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
			cal_msg_info(mv);
			if ((!CommUtil.null2String(type).equals("my"))
					&& (!CommUtil.null2String(type).equals("sys"))) {
				type = "";
			}
			mv.addObject("type", type);
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您所访问的地址不存在");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 站内短信删除
	 * @param request
	 * @param response
	 * @param type
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "站内短信删除", value = "/buyer/message_del*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/message_del" })
	public String message_del(HttpServletRequest request,
			HttpServletResponse response, String type, String mulitId) {
		String[] ids = mulitId.split(",");
		User user = SecurityUserHolder.getCurrentUser();
		
		for (String id : ids) {
			if (!id.equals("")) {
				Message msg = this.messageService.selectByPrimaryKey(CommUtil.null2Long(id));
				
				if ((msg.getFromUser() != null && (msg.getFromUser().getId().equals(user.getId()))) 
						|| ((msg.getToUser() != null) && (msg.getToUser().getId().equals(user.getId())))) {// 只允许删除自己发送的和收到的站内短信
					this.messageService.deleteById(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:message?type=" + type;
	}
	
	/**
	 * 站内短信查看
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "站内短信查看", value = "/buyer/message_info*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/message_info" })
	public ModelAndView message_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/message_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		if ((id != null) && (!"".equals(id))) {
			Message obj = this.messageService.selectByPrimaryKey(CommUtil.null2Long(id));
			
			if (obj != null) {
				if (obj.getToUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
					obj.setStatus(1);// 标注短信已读
					this.messageService.updateById(obj);
				}
				if (obj.getFromUser() != null) {
					if (obj.getFromUser().getId().equals(SecurityUserHolder.getCurrentUser().getId())) {
						obj.setReply_status(0);// 标注短信回复已读
						this.messageService.updateById(obj);
					}
				}
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
				maps.put("parent_id", obj.getId());
				
				IPageList pList = this.messageService.list(maps);
				
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				mv.addObject("obj", obj);
				cal_msg_info(mv);
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，站内信查看失败");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/message");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，站内信查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/message");
		}
		return mv;
	}
	
	/**
	 * 站内短信发送
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@SecurityMapping(title = "站内短信发送", value = "/buyer/message_send*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/message_send" })
	public ModelAndView message_send(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/message_send.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		this.cal_msg_info(mv);
		
		if ((userName != null) && (!userName.equals(""))) {
			mv.addObject("userName", userName);
		}
		return mv;
	}
	
	/**
	 * 站内短信保存
	 * @param request
	 * @param response
	 * @param users
	 * @param content
	 */
	@SecurityMapping(title = "站内短信保存", value = "/buyer/message_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/message_save" })
	public void message_save(HttpServletRequest request,
			HttpServletResponse response, String users, String content) {
		String[] userNames = users.split(",");
		for (String userName : userNames) {
			User toUser = this.userService.getObjByProperty("userName","=",userName);
			if (toUser != null) {
				Message msg = new Message();
				msg.setAddTime(new Date());
				
				content = content.replaceAll("\n", "redpigmall_br");
				msg.setContent(Jsoup.clean(content, Whitelist.basic()).replaceAll("redpigmall_br", "\n"));
				msg.setFromUser(SecurityUserHolder.getCurrentUser());
				msg.setToUser(toUser);
				msg.setType(1);
				this.messageService.saveEntity(msg);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 站内短信回复
	 * @param request
	 * @param response
	 * @param pid
	 * @param content
	 */
	@SecurityMapping(title = "站内短信回复", value = "/buyer/message_reply*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/message_reply" })
	public void message_reply(HttpServletRequest request,
			HttpServletResponse response, String pid, String content) {
		Message parent = this.messageService.selectByPrimaryKey(Long.valueOf(Long.parseLong(pid)));
		boolean ret = false;
		if (!parent.getFromUser().getUserRole().equalsIgnoreCase("admin")) {
			Message reply = new Message();
			reply.setAddTime(new Date());
			reply.setContent(content);
			reply.setFromUser(SecurityUserHolder.getCurrentUser());
			reply.setToUser(parent.getFromUser());
			reply.setType(1);
			reply.setParent(parent);
			reply.setMsg_cat(1);// 设置该信息为回复信息
			this.messageService.saveEntity(reply);
			parent.setReply_status(1);
			this.messageService.updateById(parent);
			ret = true;
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
	 * 发送信息用户验证
	 * @param request
	 * @param response
	 * @param users
	 */
	@SecurityMapping(title = "发送信息用户验证", value = "/buyer/message_validate_user*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/message_validate_user" })
	public void message_validate_user(HttpServletRequest request,
			HttpServletResponse response, String users) {
		String[] userNames = users.replaceAll("，", ",").trim().split(",");
		boolean ret = true;
		for (String userName : userNames) {
			if (!userName.trim().equals("")) {
				User user = this.userService.getObjByProperty("userName","=",userName.trim());
				if (user == null) {
					ret = false;
				} else if (user.getUserRole().equalsIgnoreCase("admin")) {
					ret = false;
				}
			}
			if (ret) {
				if (userName.equals(SecurityUserHolder.getCurrentUser()
						.getUserName())) {
					ret = false;
				}
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
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "发送信息用户验证", value = "/buyer/new_message_remind*", rtype = "buyer", rname = "new_message_remind", rcode = "user_center", rgroup = "new_message_remind")
	@RequestMapping({ "/buyer/new_message_remind" })
	public ModelAndView new_message_remind(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv= new RedPigJModelAndView(
				"user/default/usercenter/new_message_remind.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			List<ChattingLog> logs = this.chattinglogService.queryUserUnread(user.getId(), null);
			
			if (logs.size() > 0) {
				mv.addObject("chatting", logs.get(0));
				return mv;
			}
			Map<String, Object> params = Maps.newHashMap();
			params.clear();
			params.put("toUser_id", user.getId());
			params.put("status", Integer.valueOf(0));
			params.put("parent", -1);
			
			Date time = CommUtil.formatDate((String) request.getSession(false).getAttribute("last_request_time"), "yyyy-MM-dd hh:mm:ss");
			if (time != null) {
				
				params.put("add_Time_more_than_equal", time);
			}
			
			params.put("orderBy", "addTime");
			params.put("orderType", "desc");
			
			List<Message> msgs = this.messageService.queryPageListWithNoRelations(params);
			
			if (msgs.size() > 0) {
				Message msg = this.messageService.selectByPrimaryKey(CommUtil.null2Long(msgs.get(0).getId()));
				
				mv.addObject("msgUser", msg.getFromUser().getUserName());
				mv.addObject("msgId", msg.getId());
				mv.addObject("msgContent", msg.getContent());
				mv.addObject("msgType", Integer.valueOf(msg.getType()));
			}
			mv.addObject("msgCount", Integer.valueOf(msgs.size()));
			request.getSession(false).setAttribute("last_request_time",CommUtil.formatLongDate(new Date()));
		}
		return mv;
	}
	
	private void cal_msg_info(ModelAndView mv) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("status", Integer.valueOf(0));
		params.put("type", Integer.valueOf(1));
		params.put("toUser_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("parent", -1);
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		// 查询当前用户收到的未读站内信
		List<Message> user_msgs = this.messageService.queryPageListWithNoRelations(params);
		
		// 查询系统发送给当前用户且未读的站内信
		params.clear();
		params.put("status", Integer.valueOf(0));
		params.put("type", Integer.valueOf(0));
		params.put("toUser_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("parent", -1);
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<Message> sys_msgs = this.messageService.queryPageListWithNoRelations(params);
		
		// 查询当前用户发送且有新回复的站内信
		params.clear();
		params.put("msg_cat", Integer.valueOf(0));
		params.put("reply_status", Integer.valueOf(1));
		params.put("fromUser_id", SecurityUserHolder.getCurrentUser().getId());
		
		List<Message> replys = this.messageService.queryPageListWithNoRelations(params);
		
		mv.addObject("user_msgs", user_msgs);
		mv.addObject("sys_msgs", sys_msgs);
		mv.addObject("reply_msgs", replys);
	}
}
