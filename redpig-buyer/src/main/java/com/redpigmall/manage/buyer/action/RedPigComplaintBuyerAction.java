package com.redpigmall.manage.buyer.action;

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
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Complaint;
import com.redpigmall.domain.ComplaintGoods;
import com.redpigmall.domain.ComplaintSubject;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Store;

/**
 * <p>
 * Title: RedPigComplaintBuyerAction.java
 * </p>
 * 
 * <p>
 * Description: 买家投诉管理器
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
 * @date 2014-6-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigComplaintBuyerAction extends BaseAction{
	
	/**
	 * 买家投诉列表
	 * @param request 请求
	 * @param response 响应
	 * @param currentPage 当前页
	 * @param order_id 订单ID
	 * @return
	 */
	@SecurityMapping(title = "买家投诉列表", value = "/buyer/order_complaint_list*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/order_complaint_list" })
	public ModelAndView order_complaint_list(
			HttpServletRequest request,
			HttpServletResponse response, 
			String currentPage, 
			String order_id) {
		//投诉列表
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/order_complaint_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//查询参数
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		//用户ID
		maps.put("user_id", SecurityUserHolder.getCurrentUser().getId().toString());
		//主订单
		maps.put("order_main", 1);
		//订单ID不为空
		if (!CommUtil.null2String(order_id).equals("")) {
			//订单ID SQL的like判断
			maps.put("order_id_like", order_id);
			//订单ID
			mv.addObject("order_id", order_id);
		}
		
		//订单状态>=20
		maps.put("order_status_more_than_equal", 20);
		//分页查询
		IPageList pList = this.orderFormService.list(maps);
		//生成分页代码
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		//订单工具类
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 买家投诉列表
	 * @param request 请求
	 * @param response 响应
	 * @param currentPage 当前页
	 * @param status 订单状态
	 * @return
	 */
	@SecurityMapping(title = "买家投诉列表", value = "/buyer/complaint*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint" })
	public ModelAndView complaint(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String status) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/buyer_complaint.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//查询条件
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, "addTime", "desc");
		//投诉用户
		maps.put("from_user_id", SecurityUserHolder.getCurrentUser().getId());
		
		if (!CommUtil.null2String(status).equals("")) {
			maps.put("status", CommUtil.null2Int(status));
		}
		//投诉举报列表查询
		IPageList pList = this.complaintService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("status", status);
		return mv;
	}
	
	/**
	 * 买家投诉发起
	 * @param request 请求
	 * @param response 响应
	 * @param order_id 订单ID
	 * @param goods_id 商品ID
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	@SecurityMapping(title = "买家投诉发起", value = "/buyer/complaint_handle*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_handle" })
	public ModelAndView complaint_handle(
			HttpServletRequest request,
			HttpServletResponse response, 
			String order_id, 
			String goods_id) {
		//买家投诉处理页面
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/complaint_handle.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		//查询订单
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_id));
		//如果该订单非当前用户订单,则显示没有订单提示!
		if (!of.getUser_id().equals(
				SecurityUserHolder.getCurrentUser().getId().toString())) {
			
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "您没有该订单！");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
			return mv;
		}
		//根据商品ID查询商品
		Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		//订单
		mv.addObject("of", of);
		//商品
		mv.addObject("goods", goods);
		//商品ID
		mv.addObject("goods_ids", goods_id);
		//日期
		Calendar calendar = Calendar.getInstance();
		//订单失效日期
		calendar.add(Calendar.DAY_OF_YEAR, -this.configService.getSysConfig().getComplaint_time());
		boolean result = true;
		//订单状态:60:完成时间  并且订单完成时间在订单失效日期之前
		if ((of.getOrder_status() == 60)
				&& (of.getFinishTime().before(calendar.getTime()))) {
			result = false;
		}
		
		Map<String,Object> params = Maps.newHashMap();
		params.put("type", "seller");
		//查询卖家投诉主题
		List<ComplaintSubject> subs = this.complaintSubjectService.queryPageList(params);
		
		if (result) {//判断订单是否在投诉有效期内
			//判断如果订单已经被投诉就不能重复投诉
			boolean result1 = true;
			List<Map> maps = this.orderFormTools.queryGoodsInfo(of.getGoods_info());
			if(maps!=null && maps.size() > 0)
				if("1".equals(maps.get(0).get("goods_complaint_status"))){
					result1 = false;
				}
			
			if (result1) {
				if (subs.size() > 0) {
					Complaint obj = new Complaint();
					obj.setFrom_user(SecurityUserHolder.getCurrentUser());
					obj.setStatus(0);
					obj.setType("buyer");
					obj.setOf(of);
					//根据订单查询店铺
					Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(of.getStore_id()));
					
					if (store != null) {
						obj.setTo_user(store.getUser());
					}
					//投诉
					mv.addObject("obj", obj);
					//
					mv.addObject("subs", subs);
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "系统未设置投诉主题不可投诉，请联系客服");
					mv.addObject("url", CommUtil.getURL(request) + "/buyer/index");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "该订单已经投诉，不允许重复投诉");
				mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "该订单已经超过投诉有效期，不能投诉");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/order");
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		String cart_session = CommUtil.randomString(32);
		request.getSession(false).setAttribute("cart_session", cart_session);
		mv.addObject("cart_session", cart_session);
		return mv;
	}
	
	/**
	 * 买家投诉列表
	 * @param request 请求
	 * @param response 响应
	 * @param order_id 订单ID
	 * @param cs_id
	 * @param from_user_content 投诉来源
	 * @param goods_ids 商品ID
	 * @param to_user_id 被投诉
	 * @param type 
	 * @param goods_gsp_ids
	 * @param cart_session
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@SecurityMapping(title = "买家投诉列表", value = "/buyer/complaint_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_save" })
	public void complaint_save(
			HttpServletRequest request,
			HttpServletResponse response, 
			String order_id, String cs_id,
			String from_user_content, String goods_ids, 
			String to_user_id,String type, 
			String goods_gsp_ids, String cart_session) {
		//查询订单
		OrderForm of = this.orderFormService.selectByPrimaryKey(CommUtil.null2Long(order_id));
		//OrderForm goods_info字段
		List<Map> maps = this.orderFormTools.queryGoodsInfo(of.getGoods_info());
		List<Map> new_maps = Lists.newArrayList();
		Map gls = Maps.newHashMap();
		Map json_map = Maps.newHashMap();
		String cart_session1 = (String) request.getSession(false).getAttribute("cart_session");
		if (cart_session.equals(cart_session1)) {
			request.getSession(false).removeAttribute("cart_session");
			for (Map m : maps) {
				if ((m.get("goods_id").toString().equals(goods_ids))
						&& (goods_gsp_ids.equals(m.get("goods_gsp_ids")
								.toString()))) {
					m.put("goods_complaint_status", 1);
					gls.putAll(m);
				}
				new_maps.add(m);
			}
			of.setGoods_info(JSON.toJSONString(new_maps));
			this.orderFormService.updateById(of);
			//新建投诉
			Complaint obj = new Complaint();
			obj.setAddTime(new Date());
			//投诉主题
			ComplaintSubject cs = this.complaintSubjectService.selectByPrimaryKey(CommUtil.null2Long(cs_id));
			obj.setCs(cs);
			obj.setFrom_user_content(from_user_content);
			obj.setFrom_user(SecurityUserHolder.getCurrentUser());
			obj.setTo_user(this.userService.selectByPrimaryKey(CommUtil.null2Long(to_user_id)));
			obj.setType(type);
			obj.setOf(of);
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_ids));
			//投诉商品
			ComplaintGoods cg = new ComplaintGoods();
			cg.setAddTime(new Date());
			cg.setComplaint(obj);
			cg.setGoods(goods);
			cg.setContent(CommUtil.null2String(request.getParameter("content_" + goods_ids)));
			obj.getCgs().add(cg);
			String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
			String saveFilePathName = request.getSession().getServletContext()
					.getRealPath("/")
					+ uploadFilePath + File.separator + "complaint";
			Map<String, Object> map = Maps.newHashMap();
			try {
				map = CommUtil.saveFileToServer(request, "img1",
						saveFilePathName, null, null);
				if (map.get("fileName") != "") {
					Accessory from_acc1 = new Accessory();
					from_acc1.setName(CommUtil.null2String(map.get("fileName")));
					from_acc1.setExt(CommUtil.null2String(map.get("mime")));
					from_acc1.setSize(BigDecimal.valueOf(CommUtil.null2Double(map.get("fileSize"))));
					from_acc1.setPath(uploadFilePath + "/complaint");
					from_acc1.setWidth(CommUtil.null2Int(map.get("width")));
					from_acc1.setHeight(CommUtil.null2Int(map.get("height")));
					from_acc1.setAddTime(new Date());
					this.accessoryService.saveEntity(from_acc1);
					obj.setFrom_acc1(from_acc1);
				}
				map.clear();
				map = CommUtil.saveFileToServer(request, "img2",saveFilePathName, null, null);
				if (map.get("fileName") != "") {
					Accessory from_acc2 = new Accessory();
					from_acc2
							.setName(CommUtil.null2String(map.get("fileName")));
					from_acc2.setExt(CommUtil.null2String(map.get("mime")));
					from_acc2.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					from_acc2.setPath(uploadFilePath + "/complaint");
					from_acc2.setWidth(CommUtil.null2Int(map.get("width")));
					from_acc2.setHeight(CommUtil.null2Int(map.get("height")));
					from_acc2.setAddTime(new Date());
					this.accessoryService.saveEntity(from_acc2);
					obj.setFrom_acc2(from_acc2);
				}
				map.clear();
				map = CommUtil.saveFileToServer(request, "img3",
						saveFilePathName, null, null);
				if (map.get("fileName") != "") {
					Accessory from_acc3 = new Accessory();
					from_acc3
							.setName(CommUtil.null2String(map.get("fileName")));
					from_acc3.setExt(CommUtil.null2String(map.get("mime")));
					from_acc3.setSize(BigDecimal.valueOf(CommUtil
							.null2Double(map.get("fileSize"))));
					from_acc3.setPath(uploadFilePath + "/complaint");
					from_acc3.setWidth(CommUtil.null2Int(map.get("width")));
					from_acc3.setHeight(CommUtil.null2Int(map.get("height")));
					from_acc3.setAddTime(new Date());
					this.accessoryService.saveEntity(from_acc3);
					obj.setFrom_acc3(from_acc3);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			json_map.put("op_title", "投诉提交成功");
			json_map.put("url", CommUtil.getURL(request) + "/buyer/complaint");
			this.complaintService.saveEntity(obj);
			
			List<ComplaintGoods> cgs = obj.getCgs();
			
			for (ComplaintGoods complaintGoods : cgs) {
				complaintGoods.setComplaint(obj);
			}
			
			this.redPigComplaintGoodsService.batchInsert(obj.getCgs());
			
		} else {
			json_map.put("op_title", "请勿重复提交");
			json_map.put("url", CommUtil.getURL(request) + "/buyer/complaint");
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(json_map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 买家查看投诉详情
	 * @param request 请求
	 * @param response 响应
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "买家查看投诉详情", value = "/buyer/complaint_view*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_view" })
	public ModelAndView complaint_view(HttpServletRequest request,
			HttpServletResponse response, String id) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/complaint_view.html",
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
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "参数错误，不存在该投诉");
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/complaint");
		}
		if (obj.getOf().getStore_id() != null) {
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
					.getOf().getStore_id()));
			mv.addObject("store", store);
		}
		if (obj.getOf().getStore_id() != null) {
			Store store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(obj
					.getOf().getStore_id()));
			mv.addObject("store", store);
		}
		mv.addObject("orderFormTools", this.orderFormTools);
		return mv;
	}
	
	/**
	 * 买家取消投诉
	 * @param request 请求
	 * @param response 响应
	 * @param id
	 * @param currentPage 当前页
	 * @return
	 */
	@SecurityMapping(title = "买家取消投诉", value = "/buyer/complaint_cancel*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_cancel" })
	public String complaint_cancel(HttpServletRequest request, HttpServletResponse response, String id,
			String currentPage) {
		this.complaintService.deleteById(CommUtil.null2Long(id));
		return "redirect:complaint?currentPage=" + currentPage;
	}
	
	/**
	 * 投诉图片
	 * @param request 请求
	 * @param response 响应
	 * @param id
	 * @param type
	 * @return
	 */
	@SecurityMapping(title = "投诉图片", value = "/buyer/complaint_img*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_img" })
	public ModelAndView complaint_img(HttpServletRequest request, HttpServletResponse response, String id,
			String type) {
		ModelAndView mv = new RedPigJModelAndView("user/default/usercenter/complaint_img.html",
				this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request, response);
		Complaint obj = this.complaintService.selectByPrimaryKey(CommUtil.null2Long(id));
		mv.addObject("type", type);
		mv.addObject("obj", obj);
		return mv;
	}
	
	/**
	 * 发布投诉对话
	 * @param request 请求
	 * @param response 响应
	 * @param id
	 * @param talk_content
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "发布投诉对话", value = "/buyer/complaint_talk*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_talk" })
	public void complaint_talk(HttpServletRequest request,
			HttpServletResponse response, String id, String talk_content)
			throws IOException {
		Complaint obj = this.complaintService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (!CommUtil.null2String(talk_content).equals("")) {
			String user_role = "";
			if (SecurityUserHolder.getCurrentUser().getId().equals(obj.getFrom_user().getId())) {
				user_role = "投诉人";
			}
			if (SecurityUserHolder.getCurrentUser().getId().equals(obj.getTo_user().getId())) {
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
	 * 申诉提交仲裁
	 * @param request 请求
	 * @param response 响应
	 * @param id
	 * @param to_user_content
	 */
	@SecurityMapping(title = "申诉提交仲裁", value = "/buyer/complaint_arbitrate*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/complaint_arbitrate" })
	public void complaint_arbitrate(HttpServletRequest request, HttpServletResponse response, String id,
			String to_user_content) {
		Complaint obj = this.complaintService.selectByPrimaryKey(CommUtil.null2Long(id));
		obj.setStatus(3);
		this.complaintService.updateById(obj);
	}
}
