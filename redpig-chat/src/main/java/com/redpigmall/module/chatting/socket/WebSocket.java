package com.redpigmall.module.chatting.socket;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.JsonUtils;
import com.redpigmall.api.tools.SpringUtil;
import com.redpigmall.domain.ChattingLog;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigChattingLogService;
import com.redpigmall.service.RedPigChattingUserService;
import com.redpigmall.service.RedPigUserService;
/**
 * <p>
 * Title: WebSocket.java
 * </p>
 * 
 * <p>
 * Description:WebSocket管理
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
 * @date 2016-5-14
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "finally", "rawtypes" })
@ServerEndpoint("/websocket")
public class WebSocket {
	private RedPigChattingLogService chattinglogService = null;
	private RedPigUserService userService = null;
	private RedPigChattingUserService chattingUserService = null;
	
	/**
	 * 打开session
	 * @param session
	 */
	@OnOpen
	public void open(Session session) {
		System.out.println("=======建立链接========");
		
		this.chattinglogService = ((RedPigChattingLogService) SpringUtil.getBean(RedPigChattingLogService.class));
		
		this.userService =  ((RedPigUserService) SpringUtil.getBean(RedPigUserService.class));
		
		this.chattingUserService =  ((RedPigChattingUserService) SpringUtil.getBean(RedPigChattingUserService.class));
	}
	
	/**
	 * 关闭session
	 * @param session
	 */
	@OnClose
	public void close(Session session) {
		System.out.println("=======关闭链接========");
		ChatSessionHandler chatSessionHandler = (ChatSessionHandler) SpringUtil.getBean(ChatSessionHandler.class);
		chatSessionHandler.removeSession(session);
	}
	
	/**
	 * error
	 * @param error
	 */
	@OnError
	public void onError(Throwable error) {
		if(error!=null){
			System.out.println("聊天异常："+error.getMessage());
		}else{
			System.out.println("聊天异常：onError");
		}
	}
	
	/**
	 * 处理message
	 * @param session
	 * @param data_json
	 */
	@OnMessage
	public void handleMessage(Session session, String data_json) {
		
		Map json_map = JSON.parseObject(data_json);
		String type = CommUtil.null2String(json_map.get("type"));
		
		if (json_map.containsKey("chatlog_id")) {
			Long chatlog_id = CommUtil.null2Long(json_map.get("chatlog_id"));
			setAlreadyRead(chatlog_id, type);
			return;
		}
		Long user_id = CommUtil.null2Long(json_map.get("user_id"));
		String token = CommUtil.null2String(json_map.get("token"));
		String content = CommUtil.null2String(json_map.get("content"));
		Long service_id = CommUtil.null2Long(json_map.get("service_id"));
		
		ChatSessionHandler chatSessionHandler = (ChatSessionHandler) SpringUtil.getBean(ChatSessionHandler.class);
		
		if ((token != null) && (!token.equals(""))) {
			if (verify_token(user_id, service_id, type, token)) {
				chatSessionHandler.addSession(session, user_id, service_id,type);
				
				List<ChattingLog> logs = Lists.newArrayList();
				if (type.equals("user")) {
					logs = this.chattinglogService.queryUserUnread(user_id,service_id);
				}
				if (type.equals("service")) {
					logs = this.chattinglogService.queryServiceUnread(service_id, user_id);
				}
				if (logs.size() == 0) {
					logs = getLastMessage(user_id, service_id,type);
				}
				try {
					for (ChattingLog obj : logs) {
						send_message(session, obj);
					} 
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					session.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					return;
				}
			}
		} else {
			String font = CommUtil.null2String(json_map.get("font"));
			String font_size = CommUtil.null2String(json_map.get("font_size"));
			String font_colour = CommUtil.null2String(json_map.get("font_colour"));
			
			ChattingLog chattingLog = new ChattingLog();
			
			if ((type.equals("user")) && (content != null) && (!content.equals(""))) {
				chattingLog = this.chattinglogService.saveUserChattLog(content,service_id, user_id, font, font_colour, font_size);
			}
			
			if ((type.equals("service")) && (content != null) && (!content.equals(""))) {
				chattingLog = this.chattinglogService.saveServiceChattLog(
						content, service_id, user_id, font, font_colour,
						font_size);
			}
			
			Session other_session = chatSessionHandler.getOtherSession(user_id,service_id, type);
			send_message(other_session, chattingLog);
		}
		System.out.println("-----------------------------");
	}
	
	/**
	 * 发送message
	 * @param other_session
	 * @param chattingLog
	 */
	private void send_message(Session other_session, ChattingLog chattingLog) {
		if (other_session != null) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", chattingLog.getId());
			map.put("content", chattingLog.getContent());
			map.put("addTime", chattingLog.getAddTime());
			map.put("service_id", chattingLog.getService_id());
			map.put("service_name", chattingLog.getService_name());
			map.put("user_id", chattingLog.getUser_id());
			map.put("user_name", chattingLog.getUser_name());
			map.put("send_from", chattingLog.getSend_from());
			map.put("font", chattingLog.getFont());
			map.put("font_colour", chattingLog.getFont_colour());
			map.put("font_size", chattingLog.getFont_size());
			RemoteEndpoint.Basic other = other_session.getBasicRemote();
			try {
				other.sendText(JSON.toJSONString(map));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 验证token
	 * @param user_id
	 * @param service_id
	 * @param type
	 * @param token
	 * @return
	 */
	private boolean verify_token(Long user_id, Long service_id, String type,String token) {
		boolean ret = false;
		if (type.equals("user")) {
			User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(user_id));
			
			if (((user.getApp_seller_login_token() != null)  && (user.getApp_seller_login_token().equals(token)))
					|| ((user.getApp_login_token() != null) && (user.getApp_login_token().equals(token)))) {
				ret = true;
			}
		} else {
			ChattingUser cu = this.chattingUserService.selectByPrimaryKey(service_id);
			User seller = this.userService.selectByPrimaryKey(cu.getChatting_user_id());
			if (seller.getApp_seller_login_token().equals(token)) {
				ret = true;
			}
		}
		return ret;
	}
	
	/**
	 * 设置消息已读
	 * @param id
	 * @param type
	 * @return
	 */
	private ChattingLog setAlreadyRead(Long id, String type) {
		ChattingLog log = this.chattinglogService.selectByPrimaryKey(id);
		if (type.equals("user")) {
			log.setUser_read(1);
		}
		if (type.equals("service")) {
			log.setService_read(1);
		}
		this.chattinglogService.updateById(log);
		
		log = this.chattinglogService.selectByPrimaryKey(id);
		System.out.println("log_user_read-->"+log.getUser_read());
		System.out.println("log_service_read-->"+log.getService_read());
		
		return log;
	}
	
	/**
	 * 获取最新消息
	 * @param user_id
	 * @param service_id
	 * @return
	 */
	private List<ChattingLog> getLastMessage(Long user_id, Long service_id,String type) {
		
		/**
		 * 由于chattinglog表中
		 * send_from service_id service_name user_id
		 * 当send_from为user service_id为user.id用户id
		 * 当send_from为service service_id为chatting_user表的主键id,chatting_user表的chatting_user_id才是service端user.id用户id
		 * 所以查询聊天记录要一起查询，需要把service_id转换为service_name查询
		 * 注意：聊天记录不能分开查询再整合到一起，因为聊天记录必须是有序的，最好一起查询。
		 */
		String service_name = null;
		if("service".equals(type)) {
			ChattingUser chattingUser = this.chattingUserService.selectByPrimaryKey(service_id);
			User serviceUser = this.userService.selectByPrimaryKey(chattingUser.getChatting_user_id());
			service_name = serviceUser.getUserName();
		}
		
		if("user".equals(type)) {
			User serviceUser = this.userService.selectByPrimaryKey(service_id);
			service_name = serviceUser.getUserName();
		}
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", user_id);
		params.put("service_name", service_name);
		
		List<ChattingLog> logs = this.chattinglogService.queryPageList(params,0,10);
		
		Collections.reverse(logs);
		System.out.println(JsonUtils.objectToJson(logs));
		
		return logs;
	}
}
