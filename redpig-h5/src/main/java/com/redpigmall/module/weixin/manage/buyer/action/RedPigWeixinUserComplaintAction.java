package com.redpigmall.module.weixin.manage.buyer.action;

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
import com.redpigmall.module.weixin.view.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Complaint;
import com.redpigmall.domain.ComplaintGoods;
import com.redpigmall.domain.ComplaintSubject;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;


/**
 * 
 * 
 * 
 * 
 * <p>
 * Title: RedPigwap端投诉
 * </p>
 * 
 * <p>
 * Description: RedPigWeixinUserComplaintAction
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2015年1月26日
 * 
 * @version b2b2c_2015
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigWeixinUserComplaintAction extends BaseAction{
	
	
	/**
	 * wap端买家投诉列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param order_id
	 * @return
	 */
	@SecurityMapping(title = "wap端买家投诉列表", value = "/buyer/order_complaint_list*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_complaint_list" })
	public ModelAndView order_complaint_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String order_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/order_complaint_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/order_complaint_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		maps.put("order_main", 1);
		
		if (!CommUtil.null2String(order_id).equals("")) {
			
			maps.put("order_id_like", order_id);
			mv.addObject("order_id", order_id);
		}
		
		maps.put("order_status_more_than_equal", 20);
		IPageList pList = this.orderFormService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * wap端买家我的投诉列表
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param status
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "wap端买家我的投诉列表", value = "/buyer/complaint*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint" })
	public ModelAndView complaint(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status,
			String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/buyer_complaint.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/buyer_complaint_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
		
		maps.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
		
		if (!CommUtil.null2String(status).equals("")) {
			
			maps.put("status", CommUtil.null2Int(status));
			
		}
		
		Complaint obj = this.complaintService.selectByPrimaryKey(CommUtil.null2Long(id));
		
		IPageList pList = this.complaintService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("obj", obj);
		mv.addObject("status", status);
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * wap端买家投诉发起
	 * @param request
	 * @param response
	 * @param order_id
	 * @param goods_id
	 * @return
	 */
	@SecurityMapping(title = "wap端买家投诉发起", value = "/buyer/complaint_handle*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_handle" })
	public ModelAndView complaint_handle(HttpServletRequest request,
			HttpServletResponse response, String order_id, String goods_id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/complaint_handle.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_id));
		if (!of.getUser_id().equals(
				SecurityUserHolder.getCurrentUser().getId().toString())) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有该订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
			return mv;
		}
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		mv.addObject("of", of);
		mv.addObject("goods", goods);
		mv.addObject("goods_ids", goods_id);
		Calendar calendar = Calendar.getInstance();
		calendar.add(6, -this.configService.getSysConfig().getComplaint_time());
		boolean result = true;
		if ((of.getOrder_status() == 60)
				&& (of.getFinishTime().before(calendar.getTime()))) {
			result = false;
		}
		boolean result1 = true;
		
		Map<String,Object> maps= Maps.newHashMap();
		maps.put("type", "seller");
		
		List<ComplaintSubject> subs = this.complaintSubjectService.queryPageList(maps);
		
		if (result) {
			if (result1) {
				if (subs.size() > 0) {
					Complaint obj = new Complaint();
					obj.setFrom_user(SecurityUserHolder.getCurrentUser());
					obj.setStatus(0);
					obj.setType("buyer");
					obj.setOf(of);
					Store store = this.storeService.selectByPrimaryKey(CommUtil
							.null2Long(of.getStore_id()));
					if (store != null) {
						obj.setTo_user(store.getUser());
					}
					mv.addObject("obj", obj);
					mv.addObject("subs", subs);
				} else {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "系统未设置投诉主题不可投诉，请联系客服");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/index");
				}
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已经投诉，不允许重复投诉");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/order");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * wap端买家投诉保存
	 * @param request
	 * @param response
	 * @param order_id
	 * @param cs_id
	 * @param from_user_content
	 * @param goods_ids
	 * @param to_user_id
	 * @param type
	 * @param goods_gsp_ids
	 */
	
	@SecurityMapping(title = "wap端买家投诉保存", value = "/buyer/complaint_save*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_save" })
	public void complaint_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String order_id, String cs_id,
			String from_user_content, String goods_ids, String to_user_id,
			String type, String goods_gsp_ids) {
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil
				.null2Long(order_id));
		List<Map> maps = this.orderFormTools.queryGoodsInfo(of.getGoods_info());
		List<Map> new_maps = Lists.newArrayList();
		Map gls = Maps.newHashMap();
		for (Map m : maps) {
			if ((m.get("goods_id").toString().equals(goods_ids))
					&& (goods_gsp_ids.equals(m.get("goods_gsp_ids").toString()))) {
				m.put("goods_complaint_status", Integer.valueOf(1));
				gls.putAll(m);
			}
			new_maps.add(m);
		}
		of.setGoods_info(JSON.toJSONString(new_maps));
		this.orderFormService.updateById(of);
		Complaint obj = new Complaint();
		obj.setAddTime(new Date());
		ComplaintSubject cs = this.complaintSubjectService.selectByPrimaryKey(CommUtil.null2Long(cs_id));
		obj.setCs(cs);
		obj.setFrom_user_content(from_user_content);
		obj.setFrom_user(SecurityUserHolder.getCurrentUser());
		obj.setTo_user(this.userService.selectByPrimaryKey(CommUtil.null2Long(to_user_id)));
		obj.setType(type);
		obj.setOf(of);
		
		
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "complaint";
		Map<String, Object> map = Maps.newHashMap();
		try {
			map = CommUtil.saveFileToServer(request, "img1", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory from_acc1 = new Accessory();
				from_acc1.setName(CommUtil.null2String(map.get("fileName")));
				from_acc1.setExt(CommUtil.null2String(map.get("mime")));
				from_acc1.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				from_acc1.setPath(uploadFilePath + "/complaint");
				from_acc1.setWidth(CommUtil.null2Int(map.get("width")));
				from_acc1.setHeight(CommUtil.null2Int(map.get("height")));
				from_acc1.setAddTime(new Date());
				this.accessoryService.saveEntity(from_acc1);
				obj.setFrom_acc1(from_acc1);
			}
			map.clear();
			map = CommUtil.saveFileToServer(request, "img2", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory from_acc2 = new Accessory();
				from_acc2.setName(CommUtil.null2String(map.get("fileName")));
				from_acc2.setExt(CommUtil.null2String(map.get("mime")));
				from_acc2.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				from_acc2.setPath(uploadFilePath + "/complaint");
				from_acc2.setWidth(CommUtil.null2Int(map.get("width")));
				from_acc2.setHeight(CommUtil.null2Int(map.get("height")));
				from_acc2.setAddTime(new Date());
				this.accessoryService.saveEntity(from_acc2);
				obj.setFrom_acc2(from_acc2);
			}
			map.clear();
			map = CommUtil.saveFileToServer(request, "img3", saveFilePathName,
					null, null);
			if (map.get("fileName") != "") {
				Accessory from_acc3 = new Accessory();
				from_acc3.setName(CommUtil.null2String(map.get("fileName")));
				from_acc3.setExt(CommUtil.null2String(map.get("mime")));
				from_acc3.setSize(BigDecimal.valueOf(CommUtil.null2Double(map
						.get("fileSize"))));
				from_acc3.setPath(uploadFilePath + "/complaint");
				from_acc3.setWidth(CommUtil.null2Int(map.get("width")));
				from_acc3.setHeight(CommUtil.null2Int(map.get("height")));
				from_acc3.setAddTime(new Date());
				this.accessoryService.saveEntity(from_acc3);
				obj.setFrom_acc3(from_acc3);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.complaintService.saveEntity(obj);
		
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_ids));
		
		ComplaintGoods cg = new ComplaintGoods();
		cg.setAddTime(new Date());
		cg.setComplaint(obj);
		cg.setGoods(goods);
		cg.setContent(CommUtil.null2String(request.getParameter("content_"+ goods_ids)));
		this.complaintGoodsService.saveEntity(cg);
		
		obj.getCgs().add(cg);
		
		try {
			response.sendRedirect(CommUtil.getURL(request) + "/buyer/complaint");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * wap端买家查看投诉详情
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "wap端买家查看投诉详情", value = "/buyer/complaint_view*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_view" })
	public ModelAndView complaint_view(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/complaint_view.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj.getFrom_user().getId().equals(SecurityUserHolder
				.getCurrentUser().getId()))
				||

				(obj.getTo_user().getId().equals(SecurityUserHolder
						.getCurrentUser().getId()))) {
			mv.addObject("obj", obj);
			mv.addObject("type", type);
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，不存在该投诉");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/complaint");
		}
		if (obj.getOf().getStore_id() != null) {
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
					.getOf().getStore_id()));
			mv.addObject("store", store);
			String store_name = store.getStore_name();
			mv.addObject("store_name", store_name);
		}
		if (obj.getOf().getStore_id() != null) {
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
					.getOf().getStore_id()));
			mv.addObject("store", store);
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		mv.addObject("weixinFootPointTools", this.weixinFootPointTools);
		mv.addObject("type", type);
		return mv;
	}
	
	/**
	 * wap端买家取消投诉
	 * @param request
	 * @param response
	 * @param id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "wap端买家取消投诉", value = "/buyer/complaint_cancel*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_cancel" })
	public String complaint_cancel(HttpServletRequest request,
			HttpServletResponse response, String id, String currentPage) {
		Complaint obj = this.complaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		User user = SecurityUserHolder.getCurrentUser();
		if (obj.getFrom_user().getId().equals(user.getId())) {
			this.complaintService.deleteById(CommUtil.null2Long(id));
		}
		return "redirect:complaint?currentPage=" + currentPage;
	}
	
	/**
	 * wap端投诉图片
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "wap端投诉图片", value = "/buyer/complaint_img*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_img" })
	public ModelAndView complaint_img(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/complaint_img.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("type", type);
		mv.addObject("obj", obj);
		return mv;
	}
	
	/**
	 * wap端发布投诉对话
	 * @param request
	 * @param response
	 * @param id
	 * @param talk_content
	 * @throws IOException
	 */
	@SecurityMapping(title = "wap端发布投诉对话", value = "/buyer/complaint_talk*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_talk" })
	public void complaint_talk(HttpServletRequest request,
			HttpServletResponse response, String id, String talk_content)
			throws IOException {
		Complaint obj = this.complaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
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
			talk_content = talk_content.replace("\n", "<br>");
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
		List<Map> maps = Lists.newArrayList();
		for (String s : CommUtil.str2list(obj.getTalk_content())) {
			if ((s != null) && (!"".equals(s))) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("content", s);
				if (s.indexOf("投诉") == 0) {
					map.put("role", "");
				} else {
					map.put("role", "other");
				}
				String str = "";
				String[] s_info = s.split(" ");
				for (int r1 = 0; r1 < s_info.length; r1++) {
					if (r1 == 0) {
						map.put("name", s_info[0]);
					}
					if (r1 == 1) {
						map.put("time1", s_info[1]);
					}
					if (r1 == 2) {
						map.put("time2",
								s_info[2].substring(0, s_info[2].length() - 2));
					}
					if (r1 >= 3) {
						str = str + s_info[r1] + " ";
					}
				}
				map.put("content", str);
				maps.add(map);
			}
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
	 * wap端申诉提交仲裁
	 * @param request
	 * @param response
	 * @param id
	 * @param to_user_content
	 */
	@SecurityMapping(title = "wap端申诉提交仲裁", value = "/buyer/complaint_arbitrate*", rtype = "buyer", rname = "用户中心", rcode = "wap_user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_arbitrate" })
	public void complaint_arbitrate(HttpServletRequest request,
			HttpServletResponse response, String id, String to_user_content) {
		Complaint obj = this.complaintService
				.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setStatus(3);
		this.complaintService.updateById(obj);
	}
}
