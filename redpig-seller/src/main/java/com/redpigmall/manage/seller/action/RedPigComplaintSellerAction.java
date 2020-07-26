package com.redpigmall.manage.seller.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Complaint;
import com.redpigmall.domain.ComplaintSubject;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: ComplaintSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 卖家中心投诉管理，V1.3版开始将卖家投诉中心、买家投诉分开管理，更加合理
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
 * @date 2014-9-19
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Controller
public class RedPigComplaintSellerAction extends BaseAction{
	
	/**
	 * 卖家投诉发起
	 * @param request
	 * @param response
	 * @param order_id
	 * @return
	 */
	@SecurityMapping(title = "卖家投诉发起", value = "/complaint_handle*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping({ "/complaint_handle" })
	public ModelAndView complaint_handle(HttpServletRequest request,
			HttpServletResponse response, String order_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/complaint_handle.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_id));
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(6, -this.configService.getSysConfig().getComplaint_time());
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		boolean result = true;
		if ((of.getOrder_status() == 60)
				&& (of.getFinishTime().before(calendar.getTime()))) {
			result = false;
		}
		boolean result1 = true;
		if (of.getComplaints().size() > 0) {
			for (Complaint complaint : of.getComplaints()) {
				if (complaint.getFrom_user().getId().equals(user.getId())) {
					result1 = false;
				}
			}
		}
		if (result) {
			if (result1) {
				Complaint obj = new Complaint();
				obj.setFrom_user(user);
				obj.setStatus(0);
				obj.setType("seller");
				obj.setOf(of);
				User buyer = this.userService.selectByPrimaryKey(CommUtil.null2Long(of.getUser_id()));
				obj.setTo_user(buyer);
				mv.addObject("obj", obj);
				Map<String, Object> params = Maps.newHashMap();
				params.put("type", "seller");
				
				List<ComplaintSubject> css = this.complaintSubjectService.queryPageList(params);
				
				mv.addObject("css", css);
			} else {
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/seller_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("op_title", "该订单已经投诉，不允许重复投诉");
				mv.addObject("url", CommUtil.getURL(request) + "/order");
			}
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
			mv.addObject("url", CommUtil.getURL(request) + "/order");
		}
		return mv;
	}
	
	/**
	 * 卖家被投诉列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param status
	 * @return
	 */
	@SecurityMapping(title = "卖家被投诉列表", value = "/complaint*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping({ "/complaint" })
	public ModelAndView complaint_seller(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_complaint.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		maps.put("to_user_id", user.getId());
		if (!CommUtil.null2String(status).equals("")) {
			maps.put("status", CommUtil.null2Int(status));
		} else {
			maps.put("status_more_than_equal", 0);
		}
		
		IPageList pList = this.complaintService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("status", status);
		return mv;
	}
	
	/**
	 * 卖家查看投诉详情
	 * @param request
	 * @param response
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "卖家查看投诉详情", value = "/complaint_view*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping({ "/complaint_view" })
	public ModelAndView complaint_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_complaint_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		if ((obj.getFrom_user().getId().equals(user.getId()))
				|| (obj.getTo_user().getId().equals(user.getId()))) {
			mv.addObject("obj", obj);
			mv.addObject("orderFormTools", this.orderFormTools);
		} else {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("op_title", "参数错误，不存在该投诉");
			mv.addObject("url", CommUtil.getURL(request) + "/complaint");
		}
		return mv;
	}
	
	/**
	 * 卖家查看投诉详情
	 * @param request
	 * @param response
	 * @param id
	 * @param to_user_content
	 */
	@SuppressWarnings("unchecked")
	@SecurityMapping(title = "卖家查看投诉详情", value = "/complaint_appeal*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping({ "/complaint_appeal" })
	public void complaint_appeal(HttpServletRequest request,
			HttpServletResponse response, String id, String to_user_content) {
		Complaint obj = this.complaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setStatus(2);
		obj.setTo_user_content(to_user_content);
		obj.setAppeal_time(new Date());
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "complaint";
		Map<String, Object> map = Maps.newHashMap();
		try {
			map = CommUtil.saveFileToServer(request, "img1", saveFilePathName,null, null);
			if (map.get("fileName") != "") {
				Accessory to_acc1 = new Accessory();
				to_acc1.setName(CommUtil.null2String(map.get("fileName")));
				to_acc1.setExt(CommUtil.null2String(map.get("mime")));
				to_acc1.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				to_acc1.setPath(uploadFilePath + "/complaint");
				to_acc1.setWidth(CommUtil.null2Int(map.get("width")));
				to_acc1.setHeight(CommUtil.null2Int(map.get("height")));
				to_acc1.setAddTime(new Date());
				this.accessoryService.saveEntity(to_acc1);
				obj.setTo_acc1(to_acc1);
			}
			map.clear();
			map = CommUtil.saveFileToServer(request, "img2", saveFilePathName,null, null);
			if (map.get("fileName") != "") {
				Accessory to_acc2 = new Accessory();
				to_acc2.setName(CommUtil.null2String(map.get("fileName")));
				to_acc2.setExt(CommUtil.null2String(map.get("mime")));
				to_acc2.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				to_acc2.setPath(uploadFilePath + "/complaint");
				to_acc2.setWidth(CommUtil.null2Int(map.get("width")));
				to_acc2.setHeight(CommUtil.null2Int(map.get("height")));
				to_acc2.setAddTime(new Date());
				this.accessoryService.saveEntity(to_acc2);
				obj.setTo_acc2(to_acc2);
			}
			map.clear();
			map = CommUtil.saveFileToServer(request, "img3", saveFilePathName,null, null);
			if (map.get("fileName") != "") {
				Accessory to_acc3 = new Accessory();
				to_acc3.setName(CommUtil.null2String(map.get("fileName")));
				to_acc3.setExt(CommUtil.null2String(map.get("mime")));
				to_acc3.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				to_acc3.setPath(uploadFilePath + "/complaint");
				to_acc3.setWidth(CommUtil.null2Int(map.get("width")));
				to_acc3.setHeight(CommUtil.null2Int(map.get("height")));
				to_acc3.setAddTime(new Date());
				this.accessoryService.saveEntity(to_acc3);
				obj.setTo_acc3(to_acc3);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.complaintService.updateById(obj);
		Map<String,Object> json = Maps.newHashMap();
		json.put("url", CommUtil.getURL(request) + "/complaint");
		json.put("op_title", "提交申诉成功");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发布投诉对话
	 * @param request
	 * @param response
	 * @param id
	 * @param talk_content
	 * @throws IOException
	 */
	@SecurityMapping(title = "发布投诉对话", value = "/complaint_talk*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping({ "/complaint_talk" })
	public void complaint_talk(HttpServletRequest request,
			HttpServletResponse response, String id, String talk_content)
			throws IOException {
		Complaint obj = this.complaintService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (!CommUtil.null2String(talk_content).equals("")) {
			String user_role = "";
			if (SecurityUserHolder.getCurrentUser().getId()
					.equals(obj.getFrom_user().getId())) {
				user_role = "投诉人";
			}
			if (SecurityUserHolder.getCurrentUser().getId()
					.equals(obj.getTo_user().getId())) {
				user_role = "申诉人";
			}
			String temp = user_role + "["
					+ SecurityUserHolder.getCurrentUser().getUsername() + "] "
					+ CommUtil.formatLongDate(new Date()) + "说: "
					+ talk_content;
			if (obj.getTalk_content() == null) {
				obj.setTalk_content(temp);
			} else {
				obj.setTalk_content(temp + "\n\r" + obj.getTalk_content());
			}
			this.complaintService.updateById(obj);
		}
		List<Map<String, Object>> maps = Lists.newArrayList();
		for (String s : CommUtil.str2list(obj.getTalk_content())) {
			Map<String, Object> map = Maps.newHashMap();
			map.put("content", s);
			if (s.indexOf("管理员") == 0) {
				map.put("role", "admin");
			}
			if (s.indexOf("投诉") == 0) {
				map.put("role", "from_user");
			}
			if (s.indexOf("申诉") == 0) {
				map.put("role", "to_user");
			}
			maps.add(map);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(maps));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 申请仲裁成功
	 * @param request
	 * @param response
	 * @param id
	 * @param to_user_content
	 */
	@SecurityMapping(title = "申请仲裁成功", value = "/complaint_arbitrate*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping({ "/complaint_arbitrate" })
	public void complaint_arbitrate(HttpServletRequest request,
			HttpServletResponse response, String id, String to_user_content) {
		Complaint obj = this.complaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setStatus(3);
		this.complaintService.updateById(obj);
		Map<String, Object> json = Maps.newHashMap();
		json.put("url", CommUtil.getURL(request) + "/complaint");
		json.put("op_title", "申请仲裁成功");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 申请仲裁成功
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "申请仲裁成功", value = "/complaint_img*", rtype = "seller", rname = "投诉管理", rcode = "complaint_seller", rgroup = "客户服务")
	@RequestMapping({ "/complaint_img" })
	public ModelAndView complaint_img(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/complaint_img.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("type", type);
		mv.addObject("obj", obj);
		return mv;
	}
}
