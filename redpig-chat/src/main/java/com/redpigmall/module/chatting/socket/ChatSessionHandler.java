package com.redpigmall.module.chatting.socket;

import java.util.Iterator;
import java.util.Map;

import javax.websocket.Session;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
/**
 * <p>
 * Title: ChatSessionHandler.java
 * </p>
 * 
 * <p>
 * Description:聊天session处理
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
@SuppressWarnings({"rawtypes","unchecked"})
@Component
public class ChatSessionHandler {
	private final Map session_map = Maps.newHashMap();
	
	/**
	 * 添加session
	 * @param session
	 * @param user_id
	 * @param service_id
	 * @param type
	 */
	public void addSession(Session session, Long user_id, Long service_id,
			String type) {
		if (!sessionIsExist(session)) {
			String key = "";
			if (type.equals("user")) {
				key = "user|" + user_id + "_" + service_id;
			}
			if (type.equals("service")) {
				key = "service|" + service_id;
			}
			this.session_map.put(key, session);
		}
	}
	
	/**
	 * 删除session
	 * @param session
	 */
	public void removeSession(Session session) {
		for (Iterator iter = this.session_map.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Session session1 = (Session) this.session_map.get(key);
			if (session1.getId().equals(session.getId())) {
				this.session_map.remove(key);
				break;
			}
		}
	}
	
	/**
	 * 判断session是否存在
	 * @param session
	 * @return
	 */
	private boolean sessionIsExist(Session session) {
		boolean ret = false;
		for (Iterator iter = this.session_map.keySet().iterator(); iter
				.hasNext();) {
			String key = (String) iter.next();
			Session session1 = (Session) this.session_map.get(key);
			if (session1.getId().equals(session.getId())) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * 获得其他session
	 * @param user_id
	 * @param service_id
	 * @param type
	 * @return
	 */
	public Session getOtherSession(Long user_id, Long service_id, String type) {
		System.out.println("获取对方角色：" + type);
		String key = "";
		Object obj = null;
		try {
			if (type.equals("user")) {
				key = "service|" + service_id;
			}
			if (type.equals("service")) {
				key = "user|" + user_id + "_" + service_id;
			}
			obj = this.session_map.get(key);
			if (obj == null) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (Session) obj;
	}
}
