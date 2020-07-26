package com.redpigmall.module.chatting.view.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.chatting.manage.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.ChattingConfig;
import com.redpigmall.domain.ChattingLog;
import com.redpigmall.domain.ChattingUser;

/**
 * <p>
 * Title: RedPigChattingIndexViewAction.java
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
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigServiceChattingViewAction extends BaseAction {

	/**
	 * 打开即时对话窗口
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/service_chatting" })
	public ModelAndView service_chatting(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("chatting/service_chatting.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		HttpSession session = request.getSession(false);
		session.setAttribute("chatting_session", "chatting_session");

		Long service_id = this.ChattTools.getCurrentServiceId();
		ChattingConfig config = this.chattingconfigService.getChattingConfig(null, service_id);
		String token = this.ChattTools.getChattingServiceToken(service_id);
		mv.addObject("chattingConfig", config);
		mv.addObject("service_id", service_id);
		mv.addObject("token", token);

		return mv;
	}

	/**
	 * 打开聊天窗口
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @return
	 */
	@RequestMapping({ "/service_chatting_open" })
	public ModelAndView service_chatting_open(HttpServletRequest request, HttpServletResponse response,
			String user_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/service_chatting_open.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 1, request, response);
		Long service_id = this.ChattTools.getCurrentServiceId();
		List<ChattingLog> logs = this.chattinglogService.queryServiceUnread(service_id, CommUtil.null2Long(user_id));
		mv.addObject("objs", logs);
		return mv;
	}

	/**
	 * 刷新聊天内容
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @return
	 */
	@RequestMapping({ "/service_chatting_refresh" })
	public ModelAndView service_chatting_refresh(HttpServletRequest request, HttpServletResponse response,
			String user_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/s_chatting_log.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long service_id = this.ChattTools.getCurrentServiceId();
		List<ChattingLog> logs = this.chattinglogService.queryServiceUnread(service_id, CommUtil.null2Long(user_id));
		mv.addObject("objs", logs);
		HttpSession session = request.getSession(false);
		String chatting_session = CommUtil.null2String(session.getAttribute("chatting_session"));
		if (session != null) {
			mv.addObject("chatting_session", chatting_session);
		}
		return mv;
	}

	/**
	 * 刷新聊天用户列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/service_refresh_users" })
	public ModelAndView service_refresh_users(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("chatting/chatting_users.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		List<Map> maps = this.chattingUserService.queryUsers();

		mv.addObject("users", maps);
		return mv;
	}

	/**
	 * 聊天保存
	 * 
	 * @param request
	 * @param response
	 * @param text
	 * @param user_id
	 * @return
	 */
	@RequestMapping({ "/service_chatting_save" })
	public ModelAndView service_chatting_saveEntity(HttpServletRequest request, HttpServletResponse response,
			String text, String user_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/plat_chatting_log.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long service_id = this.ChattTools.getCurrentServiceId();
		ChattingLog log = this.chattinglogService.saveServiceChattLog(text, service_id, CommUtil.null2Long(user_id), "",
				"", "");
		List<ChattingLog> logs = Lists.newArrayList();
		logs.add(log);
		mv.addObject("objs", logs);

		HttpSession session = request.getSession(false);
		session.removeAttribute("chatting_session");
		session.setAttribute("chatting_session", "chatting_session");
		String chatting_session = CommUtil.null2String(session.getAttribute("chatting_session"));
		if (session != null) {
			mv.addObject("chatting_session", chatting_session);
		}
		return mv;
	}

	/**
	 * 聊天设置
	 * 
	 * @param request
	 * @param response
	 * @param kf_name
	 * @param content
	 * @param reply_open
	 */
	@RequestMapping({ "/service_chatting_set" })
	public void service_chatting_set(HttpServletRequest request, HttpServletResponse response, String kf_name,
			String content, String reply_open) {
		Long service_id = this.ChattTools.getCurrentServiceId();
		ChattingUser chattingUser = this.chattingUserService.selectByPrimaryKey(service_id);
		chattingUser.setChatting_name(kf_name);
		this.chattingUserService.updateById(chattingUser);
		ChattingConfig config = this.chattingconfigService.getChattingConfig(null, service_id);
		if ((kf_name != null) && (!kf_name.equals(""))) {
			config.setKf_name(kf_name);
		}
		if ((content != null) && (!content.equals(""))) {
			config.setQuick_reply_content(content);
		}
		if ((reply_open != null) && (!reply_open.equals(""))) {
			config.setQuick_reply_open(CommUtil.null2Int(reply_open));
			if ((reply_open.equals("1")) && (config.getQuick_reply_content() == null)) {
				config.setQuick_reply_content("不能及时回复，敬请原谅！");
			}
		}
		this.chattingconfigService.updateById(config);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(config.getQuick_reply_open());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 聊天图片上传
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @throws FileUploadException
	 */
	@RequestMapping({ "/service_img_upload" })
	public void service_img_upload(HttpServletRequest request, HttpServletResponse response, String user_id)
			throws FileUploadException {
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext().getRealPath("/") + uploadFilePath
				+ File.separator + "chatting";
		Map<String, Object> map = Maps.newHashMap();
		Map json_map = Maps.newHashMap();
		Accessory photo = null;
		try {
			String[] suffix = this.configService.getSysConfig().getImageSuffix().split("\\|");
			map = CommUtil.saveFileToServer(request, "image", saveFilePathName, "", suffix);
			if (map.get("fileName") != null) {
				photo = new Accessory();
				photo.setName((String) map.get("fileName"));
				photo.setExt("." + (String) map.get("mime"));
				photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
				photo.setPath(uploadFilePath + "/chatting");
				photo.setWidth(((Integer) map.get("width")).intValue());
				photo.setHeight(((Integer) map.get("height")).intValue());
				photo.setAddTime(new Date());
				this.accessoryService.saveEntity(photo);
				String src = CommUtil.getURL(request) + "/" + photo.getPath() + "/" + photo.getName();
				String content = "<img id='waiting_img' src='" + src
						+ "' onclick='show_image(this)' style='max-height:50px;cursor:pointer'/>";
				Long service_id = this.ChattTools.getCurrentServiceId();
				this.chattinglogService.saveServiceChattLog(content, service_id, CommUtil.null2Long(user_id), "", "",
						"");
				json_map.put("src", src);
				json_map.put("code", "success");
			} else {
				json_map.put("code", "error");
			}
			String json = JSON.toJSONString(json_map);
			try {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");

				PrintWriter writer = response.getWriter();
				writer.print(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示聊天历史
	 * 
	 * @param request
	 * @param response
	 * @param user_id
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/service_show_history" })
	public ModelAndView service_show_history(HttpServletRequest request, HttpServletResponse response, String user_id,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("chatting/history_log.html", this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Long service_id = this.ChattTools.getCurrentServiceId();

		Map<String, Object> maps = this.redPigQueryTools.getParams(currentPage, 20, "addTime", "desc");
		maps.put("service_id", service_id);
		maps.put("user_id", CommUtil.null2Long(user_id));

		IPageList pList = this.chattinglogService.list(maps);
		CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/service_show_history.html", "", "", pList, mv);
		return mv;
	}

}
