package com.redpigmall.module.chatting.view.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.module.chatting.manage.base.BaseAction;
import com.redpigmall.domain.Goods;
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
@Controller
public class RedPigChattingIndexViewAction extends BaseAction{
	
	/**
	 * 聊天页
	 * @param request
	 * @param response
	 * @param goods_id
	 * @param store_id
	 * @return
	 */
	@RequestMapping({ "/chatting_index" })
	public ModelAndView chatting_index(HttpServletRequest request,
			HttpServletResponse response, String goods_id, String store_id) {
		ModelAndView mv = new RedPigJModelAndView("chatting/chatting_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("goods_id", goods_id);
		mv.addObject("store_id", store_id);
		return mv;
	}
	
	/**
	 * 打开聊天窗口
	 * @param request
	 * @param response
	 * @param service_type
	 * @param goods_id
	 * @param store_id
	 * @param old_service_id
	 * @return
	 */
	@RequestMapping({ "/chatting_distribute" })
	public String chatting_distribute(HttpServletRequest request,
			HttpServletResponse response, String service_type, String goods_id,
			String store_id, String old_service_id) {
		String chatting_type = "";
		
		if ((goods_id != null) && (!goods_id.equals(""))) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			if (goods.getGoods_type() == 1) {
				chatting_type = "store";
				store_id = CommUtil.null2String(goods.getGoods_store().getId());
			} else {
				chatting_type = "plat";
			}
		}
		Long service_id = null;
		if (old_service_id != null && !old_service_id.trim().equals("undefined")) {
			service_id = CommUtil.null2Long(old_service_id);
		} else {
			if ((store_id != null) && (!store_id.equals(""))) {
				chatting_type = "store";
			}
			if (service_type.equals("before-sales")) {
				service_id = this.chattingUserService.distribute_service(store_id, 0);
			}
			if (service_type.equals("after-sales")) {
				service_id = this.chattingUserService.distribute_service(store_id, 1);
			}
			if (service_type.equals("seller-service")) {
				service_id = this.chattingUserService.distribute_service(store_id, 2);
			}
		}
		HttpSession session = request.getSession(false);
		session.setAttribute("chatting_service_id", service_id);
		return "redirect:/user_chatting?goods_id=" + goods_id + "&type=" + chatting_type + "&store_id=" + store_id;
	}
}
