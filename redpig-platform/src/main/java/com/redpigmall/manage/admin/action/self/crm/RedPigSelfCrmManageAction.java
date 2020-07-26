package com.redpigmall.manage.admin.action.self.crm;

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
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.CustomerRelMana;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigSelfCrmManageAction.java
 * </p>
 * 
 * <p>
 * Description: 商家中心crm管理控制器
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigSelfCrmManageAction extends BaseAction{
	/**
	 * 自营crm管理
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param type
	 * @param userName
	 * @param email
	 * @param message
	 * @return
	 */
	@SecurityMapping(title = "自营crm管理", value = "/crm_list*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping({ "/crm_list" })
	public ModelAndView crm_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String type,
			String userName, String email, String message) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/crm_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		maps.put("whether_self", 1);
		
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
		mv.addObject("type", type);
		mv.addObject("userName", userName);
		return mv;
	}
	
	/**
	 * 自营crm管理
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "自营crm管理", value = "/send_crm_info*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping({ "/send_crm_info" })
	public ModelAndView send_crm_info(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String type) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/send_crm_email.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if ((type != null) && (type.equals("message"))) {
			mv = new RedPigJModelAndView("admin/blue/send_crm_message.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		mv.addObject("ids", mulitId);
		return mv;
	}
	
	/**
	 * 自营crm管理
	 * @param request
	 * @param response
	 * @param ids
	 * @param message
	 */
	@SecurityMapping(title = "自营crm管理", value = "/send_email_save*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping({ "/send_email_save" })
	public void send_email_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String ids, String message) {
		String subject = this.configService.getSysConfig().getTitle() + "邮件";
		String status = "success";
		if ((message != null) && (!message.equals(""))) {
			String[] cids = ids.split(",");

			for (String id : cids) {

				CustomerRelMana crm = this.customerRelManaService
						.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((crm != null) && (crm.getWhether_self() == 1)) {
					User buyer = this.userService.selectByPrimaryKey(CommUtil
							.null2Long(crm.getUser_id()));
					if ((buyer != null) && (buyer.getEmail() != null)
							&& (!buyer.getEmail().equals(""))) {
						boolean ret = this.msgTools.sendEmail(buyer.getEmail(),
								subject, message);
						if (ret) {
							crm.setWhether_send_email(1);
							this.customerRelManaService.updateById(crm);
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
	 * 自营crm管理
	 * @param request
	 * @param response
	 * @param ids
	 * @param message
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "自营crm管理", value = "/send_message_save*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping({ "/send_message_save" })
	public void send_message_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String ids, String message)
			throws UnsupportedEncodingException {
		String subject = this.configService.getSysConfig().getTitle();
		String status = "success";
		if ((message != null) && (!message.equals(""))) {
			String[] cids = ids.split(",");

			for (String id : cids) {

				CustomerRelMana crm = this.customerRelManaService
						.selectByPrimaryKey(CommUtil.null2Long(id));
				if ((crm != null) && (crm.getWhether_self() == 1)) {
					User buyer = this.userService.selectByPrimaryKey(CommUtil
							.null2Long(crm.getUser_id()));
					if ((buyer != null) && (buyer.getMobile() != null)
							&& (!buyer.getMobile().equals(""))) {
						boolean ret = this.msgTools.sendSMS(buyer.getMobile(),
								subject + ":" + message);
						if (ret) {
							crm.setWhether_send_message(1);
							this.customerRelManaService.updateById(crm);
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
	 * 自营crm管理
	 * @param request
	 * @param response
	 * @param mulitId
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自营crm管理", value = "/crm_del*", rtype = "admin", rname = "客户管理", rcode = "crm_admin", rgroup = "自营")
	@RequestMapping({ "/crm_del" })
	public String crm_del(HttpServletRequest request,
			HttpServletResponse response, String mulitId, String currentPage) {
		if (mulitId == null) {
			mulitId = "";
		}
		String[] ids = mulitId.split(",");
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();

		for (String id : ids) {

			if (!id.equals("")) {
				CustomerRelMana customerrelmana = this.customerRelManaService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				if ((customerrelmana != null)
						&& (customerrelmana.getWhether_self() == 1)) {
					this.customerRelManaService.deleteById(Long.valueOf(Long.parseLong(id)));
					
				}
			}
		}
		return "redirect:crm_list?currentPage=" + currentPage;
	}
}
