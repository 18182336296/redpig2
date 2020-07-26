package com.redpigmall.manage.seller.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;


/**
 * 
 * 
 * <p>
 * Title: RedPigMessageSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 系统消息
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
 * @date 2014年4月24日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigMessageSellerAction extends BaseAction {
	/**
	 * 系统通知
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "系统通知", value = "/message*", rtype = "seller", rname = "我的店铺", rcode = "store_message", rgroup = "我的店铺")
	@RequestMapping({ "/message" })
	public ModelAndView message(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_message.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		maps.put("toStore_id", SecurityUserHolder.getCurrentUser().getStore().getId());
		maps.put("type", 0);
		maps.put("parent", -1);
		
		IPageList pList = this.messageService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 系统通知删除
	 * @param request
	 * @param response
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "系统通知删除", value = "/message_del*", rtype = "seller", rname = "我的店铺", rcode = "store_message", rgroup = "我的店铺")
	@RequestMapping({ "/message_del" })
	public String message_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId) {
		String[] ids = mulitId.split(",");
		User user = SecurityUserHolder.getCurrentUser();
		
		Store store = user.getStore();
		
		for (String id : ids) {

			if (!id.equals("")) {
				Message msg = this.messageService.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((msg != null) 
						&& (msg.getFromUser() != null)
						&& (store != null)) {
					if (CommUtil.null2String(store.getId()).equals(CommUtil.null2String(msg.getToStore_id()))) {
						this.messageService.deleteById(CommUtil.null2Long(id));
					}
				}
			}
		}
		return "redirect:/message";
	}
	
	/**
	 * 系统通知查看
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "系统通知查看", value = "/message_info*", rtype = "seller", rname = "我的店铺", rcode = "store_message", rgroup = "我的店铺")
	@RequestMapping({ "/message_info" })
	public ModelAndView message_info(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/message_info.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((id != null) && (!"".equals(id))) {
			Message obj = this.messageService
					.selectByPrimaryKey(CommUtil.null2Long(id));
			if (obj != null) {
				if (CommUtil.null2String(obj.getToStore_id()).equals(
						CommUtil.null2String(SecurityUserHolder
								.getCurrentUser().getStore().getId()))) {
					obj.setStatus(1);
					this.messageService.updateById(obj);
				}
				
				Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
				maps.put("parent_id", obj.getId());
				
				IPageList pList = this.messageService.list(maps);
				
				CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
				mv.addObject("obj", obj);
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "参数错误，站内信查看失败");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/message");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，站内信查看失败");
			mv.addObject("url", CommUtil.getURL(request) + "/message");
		}
		return mv;
	}
}
