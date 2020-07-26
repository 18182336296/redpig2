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
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.chatting.manage.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.ArticleClass;
import com.redpigmall.domain.ChattingConfig;
import com.redpigmall.domain.ChattingLog;
import com.redpigmall.domain.ChattingUser;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;


/**
 * 
 * <p>
 * Title: RedPigChattingViewAction.java
 * </p>
 * 
 * <p>
 * Description: 系统聊天工具,作为单独聊天系统，可以集成其他系统,用户使用聊天系统超过session时长自动关闭会话窗口
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
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({"unchecked","rawtypes"})
@Controller
public class RedPigUserChattingViewAction extends BaseAction{
	

	/**
	 * 用户聊天窗口，开启窗口同时创建会话session，会话session到期后自动关闭会话窗口
	 * 
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param type
	 * @param store_id
	 * @return
	 */
	@RequestMapping({ "/user_chatting" })
	public ModelAndView user_chatting(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String type,
			String store_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/user_chatting.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		if ((user_agent.indexOf("iphone") >= 0)
				|| (user_agent.indexOf("android") >= 0)) {
			mv.addObject("device_type", "wap");
		}
		User user = SecurityUserHolder.getCurrentUser();
		if (user == null) {
			mv.addObject("chatting_error", Boolean.valueOf(true));
			return mv;
		}
		HttpSession session = request.getSession(false);
		session.setAttribute("chatting_session", "chatting_session");
		Long service_id = CommUtil.null2Long(session.getAttribute("chatting_service_id"));
		String token = this.ChattTools.getChattingUserToken(user.getId());
		ChattingConfig chattconfig = this.chattingconfigService.getChattingConfig(user.getId(), null);
		mv.addObject("chattingConfig", chattconfig);
		mv.addObject("user", user);
		mv.addObject("token", token);
		System.out.println("user_chatting---->"+service_id);
		mv.addObject("service_id", service_id);
		ChattingUser server_user = this.ChattTools.getChattingService(service_id);
		mv.addObject("service_name", server_user.getChatting_name());
		mv.addObject("service", server_user.getChatting_name());
		ChattingConfig config = this.chattingconfigService.getChattingConfig(null, service_id);
		mv.addObject("service_config", config);
		if ((goods_id != null) && (!goods_id.equals(""))) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			mv.addObject("goods", goods);
			mv.addObject("goodsViewTools", this.goodsViewTools);
			mv.addObject("activityViewTools", this.activityViewTools);
			if ((goods != null) && (goods.getGoods_type() == 1)) {
				mv.addObject("store", goods.getGoods_store());
				generic_evaluate(goods.getGoods_store(), mv);
			}
		} else {
			Map<String, Object> map = Maps.newHashMap();
			if (type.equals("store")) {
				Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(store_id));
				if (store == null) {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "请求参数错误");
					return mv;
				}
				map.clear();
				map.put("store_id", store.getId());
				map.put("goods_status", Integer.valueOf(0));
				map.put("orderBy", "goods_salenum");
				map.put("orderType", "desc");
				
				List<Goods> recommends = this.goodsService.queryPageList(map, 0,5);
				
				mv.addObject("recommends", recommends);
				mv.addObject("store", store);
				generic_evaluate(store, mv);
			}
			if (type.equals("plat")) {
				map.clear();
				map.put("goods_type", Integer.valueOf(0));
				map.put("goods_status", Integer.valueOf(0));
				map.put("orderBy", "goods_salenum");
				map.put("orderType", "desc");
				
				List<Goods> recommends = this.goodsService.queryPageList(map, 0,5);
				
				mv.addObject("recommends", recommends);
			}
		}
		generic_goodsinfo(mv, user);
		return mv;
	}

	/**
	 * 用户端定时刷新请求， type;// 对话类型，0为用户和商家对话，1为用户和平台对话 user_read:用户没有读过的信息
	 * 
	 * @param request
	 * @param response
	 * @param service_id
	 * @return
	 */
	@RequestMapping({ "/user_chatting_refresh" })
	public ModelAndView user_chatting_refresh(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/user_chatting_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			
			List<ChattingLog> logs = this.chattinglogService.queryServiceUnread(user.getId(), CommUtil.null2Long(chatting_id));
			mv.addObject("objs", logs);
			
			HttpSession session = request.getSession(false);
			String chatting_session = CommUtil.null2String(session.getAttribute("chatting_session"));
			if (session != null) {
				mv.addObject("chatting_session", chatting_session);
			}
		}
		
		return mv;
	}

	/**
	 * 用户端定时刷新请求， type;// 对话类型，0为用户和商家对话，1为用户和平台对话 user_read:用户没有读过的信息
	 * 
	 * @param request
	 * @param response
	 * @param service_id
	 * @return
	 */
	@RequestMapping({ "/user_chatting_refresh2" })
	public ModelAndView user_chatting_refresh2(HttpServletRequest request,
			HttpServletResponse response, String chatting_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/user_chatting_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			
			List<ChattingLog> logs = this.chattinglogService.queryUserUnread(user.getId(), CommUtil.null2Long(chatting_id));
			mv.addObject("objs", logs);
			
			HttpSession session = request.getSession(false);
			String chatting_session = CommUtil.null2String(session.getAttribute("chatting_session"));
			if (session != null) {
				mv.addObject("chatting_session", chatting_session);
			}
		}
		
		return mv;
	}
	
	/**
	 * 用户聊天保存
	 * @param request
	 * @param response
	 * @param text
	 * @param service_id
	 * @return
	 */
	@RequestMapping({ "/user_chatting_save" })
	public ModelAndView user_chatting_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String text, String service_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/user_chatting_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		boolean ret = true;
		User user = SecurityUserHolder.getCurrentUser();
		if (user == null) {
			ret = false;
		}
		if (ret) {
			ChattingLog log = this.chattinglogService.saveUserChattLog(text,CommUtil.null2Long(service_id), user.getId(), "", "", "");
			List<ChattingLog> logs = Lists.newArrayList();
			logs.add(log);
			mv.addObject("objs", logs);
			
			HttpSession session = request.getSession(false);
			session.removeAttribute("chatting_session");
			session.setAttribute("chatting_session", "chatting_session");
			String chatting_session = CommUtil.null2String(session
					.getAttribute("chatting_session"));
			if (session != null) {
				mv.addObject("chatting_session", chatting_session);
			}
		} else {
			mv.addObject("chatting_error", Boolean.valueOf(true));
		}
		return mv;
	}
	
	/**
	 * 用户聊天图片上传
	 * @param request
	 * @param response
	 * @param service_id
	 * @throws FileUploadException
	 */
	@RequestMapping({ "/user_img_upload" })
	public void user_img_upload(HttpServletRequest request,
			HttpServletResponse response, String service_id)
			throws FileUploadException {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "chatting";
			Map json_map = Maps.newHashMap();
			Map<String, Object> map = Maps.newHashMap();
			Accessory photo = null;
			try {
				String[] suffix = this.configService.getSysConfig()
						.getImageSuffix().split("\\|");
				map = CommUtil.saveFileToServer(request, "image",
						saveFilePathName, "", suffix);
				if (map.get("fileName") != null) {
					photo = new Accessory();
					photo.setName((String) map.get("fileName"));
					photo.setExt("." + (String) map.get("mime"));
					photo.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
							.get("fileSize"))));
					photo.setPath(uploadFilePath + "/chatting");
					photo.setWidth(((Integer) map.get("width")).intValue());
					photo.setHeight(((Integer) map.get("height")).intValue());
					photo.setAddTime(new Date());
					this.accessoryService.saveEntity(photo);
					String src = CommUtil.getURL(request) + "/"
							+ photo.getPath() + "/" + photo.getName();
					String contentSrc = this.redPigSysConfigService.getSysConfig().getImageWebServer() + "/"
							+ photo.getPath() + "/" + photo.getName();
					
					String content = "<img id='waiting_img' src='"
							+ contentSrc
							+ "' onclick='show_image(this)' style='max-height:50px;cursor:pointer'/>";

					this.chattinglogService.saveUserChattLog(content,
							CommUtil.null2Long(service_id), user.getId(), "",
							"", "");
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
	}
	
	/**
	 * 订单ajax
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/chatting_order_ajax" })
	public ModelAndView chatting_order_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("chatting/user_order_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
			params.put("orderBy", "addTime");
			params.put("orderType", "desc");
			
			List<OrderForm> orders = this.ofService.queryPageList(params);
			
			mv.addObject("orders", orders);
			mv.addObject("orderformTools", this.orderformTools);
		}
		return mv;
	}
	
	/**
	 * 聊天返回
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@RequestMapping({ "/chatting_return_ajax" })
	public ModelAndView chatting_return_ajax(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("chatting/user_return_ajax.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (SecurityUserHolder.getCurrentUser() != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
			params.put("orderBy", "addTime");
			params.put("orderType", "desc");
			
			List<ReturnGoodsLog> returnlogs = this.returngoodslogService.queryPageList(params,CommUtil.null2Int(currentPage), 1);
			
			mv.addObject("returnlogs", returnlogs);
			mv.addObject("orderformTools", this.orderformTools);
		}
		return mv;
	}
	
	/**
	 * 用户显示聊天历史
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param service_id
	 * @return
	 */
	@RequestMapping({ "/user_show_history" })
	public ModelAndView user_show_history(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String service_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/user_history_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = SecurityUserHolder.getCurrentUser();
		if (user != null) {
			Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,20, "addTime", "desc");
			maps.put("user_id", user.getId());
			maps.put("service_id", CommUtil.null2Long(service_id));
			IPageList pList = this.chattinglogService.list(maps);
			CommUtil.saveIPageList2ModelAndView(CommUtil.getURL(request) + "/user_show_history.html", "", "", pList, mv);
		}
		return mv;
	}
	
	private void generic_evaluate(Store store, ModelAndView mv) {
		double description_result = 0.0D;
		double service_result = 0.0D;
		double ship_result = 0.0D;
		GoodsClass gc = this.goodsClassService
				.selectByPrimaryKey(store.getGc_main_id());
		if ((store != null) && (gc != null) && (store.getPoint() != null)) {
			float description_evaluate = CommUtil.null2Float(gc
					.getDescription_evaluate());
			float service_evaluate = CommUtil.null2Float(gc
					.getService_evaluate());
			float ship_evaluate = CommUtil.null2Float(gc.getShip_evaluate());

			float store_description_evaluate = CommUtil.null2Float(store
					.getPoint().getDescription_evaluate());
			float store_service_evaluate = CommUtil.null2Float(store.getPoint()
					.getService_evaluate());
			float store_ship_evaluate = CommUtil.null2Float(store.getPoint()
					.getShip_evaluate());

			description_result = CommUtil.div(
					Float.valueOf(store_description_evaluate
							- description_evaluate),
					Float.valueOf(description_evaluate));
			service_result = CommUtil.div(
					Float.valueOf(store_service_evaluate - service_evaluate),
					Float.valueOf(service_evaluate));
			ship_result = CommUtil.div(
					Float.valueOf(store_ship_evaluate - ship_evaluate),
					Float.valueOf(ship_evaluate));
		}
		if (description_result > 0.0D) {
			mv.addObject("description_css", "value_strong");
			mv.addObject(
					"description_result",

					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(description_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(description_result),
									Integer.valueOf(100))))
							+ "%");
		}
		if (description_result == 0.0D) {
			mv.addObject("description_css", "value_normal");
			mv.addObject("description_result", "-----");
		}
		if (description_result < 0.0D) {
			mv.addObject("description_css", "value_light");
			mv.addObject(
					"description_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-description_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (service_result > 0.0D) {
			mv.addObject("service_css", "value_strong");
			mv.addObject(
					"service_result",

					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(service_result),
							Integer.valueOf(100)) > 100.0D ? 100.0D : CommUtil
							.mul(Double.valueOf(service_result),
									Integer.valueOf(100))))
							+ "%");
		}
		if (service_result == 0.0D) {
			mv.addObject("service_css", "value_normal");
			mv.addObject("service_result", "-----");
		}
		if (service_result < 0.0D) {
			mv.addObject("service_css", "value_light");
			mv.addObject(
					"service_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-service_result),
							Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result > 0.0D) {
			mv.addObject("ship_css", "value_strong");
			mv.addObject(
					"ship_result",

					CommUtil.null2String(Double
							.valueOf(CommUtil.mul(Double.valueOf(ship_result),
									Integer.valueOf(100)) > 100.0D ? 100.0D
									: CommUtil.mul(Double.valueOf(ship_result),
											Integer.valueOf(100))))
							+ "%");
		}
		if (ship_result == 0.0D) {
			mv.addObject("ship_css", "value_normal");
			mv.addObject("ship_result", "-----");
		}
		if (ship_result < 0.0D) {
			mv.addObject("ship_css", "value_light");
			mv.addObject(
					"ship_result",
					CommUtil.null2String(Double.valueOf(CommUtil.mul(
							Double.valueOf(-ship_result), Integer.valueOf(100))))
							+ "%");
		}
	}

	private void generic_goodsinfo(ModelAndView mv, User user) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<OrderForm> orders = this.ofService.queryPageList(params,0,1);
		
		params.clear();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<OrderForm> all_orders = this.ofService.queryPageList(params);

		params.clear();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		
		List<ReturnGoodsLog> returnlogs = this.returngoodslogService.queryPageList(params,0,1);
		
		params.clear();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		List<ReturnGoodsLog> all_returnlogs = this.returngoodslogService.queryPageList(params);
		
		params.clear();
		params.put("two_type", "chat");
		params.put("one_type", 1);
		
		List<ArticleClass> ac_c = this.articleClassService.queryPageList(params);
		
		if (ac_c.size() > 0) {
			mv.addObject("article", this.articleViewTools.chat_list(ac_c.get(0).getId()));
		}
		mv.addObject("returnlogs", returnlogs);
		mv.addObject("all_returnlogs", Integer.valueOf(all_returnlogs.size()));
		mv.addObject("orders", orders);
		mv.addObject("all_orders", Integer.valueOf(all_orders.size()));
		mv.addObject("orderformTools", this.orderformTools);
	}
}
