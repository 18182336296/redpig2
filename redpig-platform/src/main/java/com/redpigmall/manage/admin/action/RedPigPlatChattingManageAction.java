package com.redpigmall.manage.admin.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.manage.admin.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: ChattingViewAction.java
 * </p>
 * 
 * <p>
 * Description: 系统聊天工具,作为单独聊天系统系统，可以集成其他系统
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
 * @date 2014年5月22日
 * 
 * @version redpigmall_b2b2c 5.0
 */
@Controller
public class RedPigPlatChattingManageAction extends BaseAction {

	
	/**
	 * 
	 * plat_chatting_save:保存聊天信息. <br/>
	 *
	 * @author redpig
	 * @param request
	 * @param response
	 * @param type
	 * @param content
	 * @param user_id
	 * @param service_id
	 * @param font
	 * @param font_size
	 * @param font_colour
	 * @return
	 * @since JDK 1.8
	 */
	@RequestMapping("/plat_chatting_save")
	public void plat_chatting_save(HttpServletRequest request, HttpServletResponse response, String type,
			String content, String user_id, String service_id, String font, String font_size, String font_colour) {
		
		if ((type.equals("user")) && (content != null) && (!content.equals(""))) {
			this.chattinglogService.saveUserChattLog(content, CommUtil.null2Long(service_id),
					CommUtil.null2Long(user_id), font, font_colour, font_size);
		}
		
		if ((type.equals("service")) && (content != null) && (!content.equals(""))) {
			this.chattinglogService.saveServiceChattLog(content, CommUtil.null2Long(service_id),
					CommUtil.null2Long(user_id), font, font_colour, font_size);
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

}
