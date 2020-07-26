package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import com.redpigmall.domain.CustomerRelMana;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;


/**
 * 
 * 
 * <p>
 * Title: RedPigSellerCrmManageAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心crm管理控制器
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
 * @date 2014年11月4日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSellerCrmManageAction extends BaseAction{
	/**
	 * 卖家crm管理
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param type
	 * @param userName
	 * @param email
	 * @param message
	 * @return
	 */
	@SecurityMapping(title = "卖家crm管理", value = "/crm_list*", rtype = "seller", rname = "客户管理", rcode = "crm_seller", rgroup = "客户服务")
	@RequestMapping({ "/crm_list" })
	public ModelAndView crm_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String userName, String email, String message) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/crm_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		maps.put("store_id", user.getStore().getId());
		
		if (email != null) {
			if (email.equals("yes")) {
				maps.put("whether_send_email", 1);
			}
			if (email.equals("no")) {
				maps.put("whether_send_email", 0);
			}
			mv.addObject("email", email);
		}
		if (message != null) {
			if (message.equals("yes")) {
				maps.put("whether_send_message", 1);
			}
			if (message.equals("no")) {
				maps.put("whether_send_message", 0);
			}
			mv.addObject("message", message);
		}
		if ((type != null) && (type.equals("order"))) {
			maps.put("cus_type", 0);
		}
		if ((type != null) && (type.equals("consult"))) {
			maps.put("cus_type", 1);
		}
		if ((type != null) && (type.equals("fav"))) {
			maps.put("cus_type", 2);
		}
		if ((userName != null) && (!userName.equals(""))) {
			maps.put("userName", userName);
		}
		
		IPageList pList = this.customerRelManaService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView(url + "/crm_list.html", "",
				params, pList, mv);
		Store store = this.storeService.selectByPrimaryKey(user.getStore().getId());
		mv.addObject("sms_count", Integer.valueOf(store.getStore_sms_count()));
		mv.addObject("email_count",
				Integer.valueOf(store.getStore_email_count()));
		mv.addObject("send_email_count",
				Integer.valueOf(store.getSend_email_count()));
		mv.addObject("send_message_count",
				Integer.valueOf(store.getSend_sms_count()));
		mv.addObject("type", type);
		mv.addObject("userName", userName);
		return mv;
	}
	
	/**
	 * 卖家crm管理
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "卖家crm管理", value = "/send_crm_info*", rtype = "seller", rname = "客户管理", rcode = "crm_seller", rgroup = "客户服务")
	@RequestMapping({ "/send_crm_info" })
	public ModelAndView send_crm_info(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/send_crm_email.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((type != null) && (type.equals("message"))) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/send_crm_message.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		mv.addObject("ids", mulitId);
		return mv;
	}
	
	/**
	 * 卖家crm管理
	 * @param request
	 * @param response
	 * @param ids
	 * @param message
	 */
	@SecurityMapping(title = "卖家crm管理", value = "/send_email_save*", rtype = "seller", rname = "客户管理", rcode = "crm_seller", rgroup = "客户服务")
	@RequestMapping({ "/send_email_save" })
	public void send_email_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String ids, String message) {
		String subject = this.configService.getSysConfig().getTitle() + "邮件";
		String status = "success";
		if ((message != null) && (!message.equals(""))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int count = user.getStore().getStore_email_count();
			String[] cids = ids.split(",");

			for (String id : cids) {

				CustomerRelMana crm = this.customerRelManaService.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((crm != null)
						&& (crm.getStore_id().equals(user.getStore().getId()))) {
					User buyer = this.userService.selectByPrimaryKey(CommUtil
							.null2Long(crm.getUser_id()));
					if ((buyer != null) && (buyer.getEmail() != null)
							&& (!buyer.getEmail().equals(""))) {
						if (count > 0) {
							boolean ret = this.msgTools.sendEmail(
									buyer.getEmail(), subject, message);
							if (ret) {
								count--;
								crm.setWhether_send_email(1);
								this.customerRelManaService.updateById(crm);
								Store store = user.getStore();
								store.setStore_email_count(store
										.getStore_email_count() - 1);
								store.setSend_email_count(store
										.getSend_email_count() + 1);
								this.storeService.updateById(store);
							}
						} else {
							status = "failed";
							break;
						}
					}
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(status);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 卖家crm管理
	 * @param request
	 * @param response
	 * @param ids
	 * @param message
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "卖家crm管理", value = "/send_message_save*", rtype = "seller", rname = "客户管理", rcode = "crm_seller", rgroup = "客户服务")
	@RequestMapping({ "/send_message_save" })
	public void send_message_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String ids, String message)
			throws UnsupportedEncodingException {
		String subject = this.configService.getSysConfig().getTitle();
		String status = "success";
		if ((message != null) && (!message.equals(""))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			user = user.getParent() == null ? user : user.getParent();
			int count = user.getStore().getStore_sms_count();
			String[] cids = ids.split(",");

			for (String id : cids) {

				CustomerRelMana crm = this.customerRelManaService
						.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((crm != null)
						&& (crm.getStore_id().equals(user.getStore().getId()))) {
					User buyer = this.userService.selectByPrimaryKey(CommUtil
							.null2Long(crm.getUser_id()));
					if ((buyer != null) && (buyer.getMobile() != null)
							&& (!buyer.getMobile().equals(""))) {
						if (count > 0) {
							boolean ret = this.msgTools.sendSMS(buyer.getMobile(), subject + ":" + message);
							if (ret) {
								count--;
								crm.setWhether_send_message(1);
								this.customerRelManaService.updateById(crm);
								Store store = user.getStore();
								store.setStore_sms_count(store
										.getStore_sms_count() - 1);
								store.setSend_sms_count(store
										.getSend_sms_count() + 1);
								this.storeService.updateById(store);
							}
						} else {
							status = "failed";
							break;
						}
					}
				}
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(status);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 卖家crm管理
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "卖家crm管理", value = "/crm_del*", rtype = "seller", rname = "客户管理", rcode = "crm_seller", rgroup = "客户服务")
	@RequestMapping({ "/crm_del" })
	public String crm_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		String[] ids = mulitId.split(",");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		for (String id : ids) {

			if (!id.equals("")) {
				CustomerRelMana customerrelmana = this.customerRelManaService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if ((customerrelmana != null & customerrelmana.getStore_id()
						.equals(user.getStore().getId()))) {
					this.customerRelManaService.deleteById(Long.valueOf(Long.parseLong(id)));
				}
			}
		}
		return "redirect:crm_list?currentPage=" + currentPage;
	}
}
