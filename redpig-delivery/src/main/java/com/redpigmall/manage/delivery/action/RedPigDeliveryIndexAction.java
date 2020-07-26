package com.redpigmall.manage.delivery.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.DeliveryAddress;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigDeliveryIndexAction.java
 * </p>
 * 
 * <p>
 * Description: 自提点管理控制器
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
 * @date 2014-11-25
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class RedPigDeliveryIndexAction extends BaseAction{
	
	/**
	 * 自提登陆
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/delivery/login" })
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("delivery/delivery_login.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		return mv;
	}
	
	/**
	 * 自提点管理首页
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param keyword
	 * @return
	 */
	@SecurityMapping(title = "自提点管理首页", value = "/delivery/index*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping({ "/delivery/index" })
	public ModelAndView index(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String keyword) {
		ModelAndView mv = new RedPigJModelAndView("delivery/delivery_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		DeliveryAddress deliveryaddress = this.deliveryAddressService.selectByPrimaryKey(user.getDelivery_id());
		
		maps.put("delivery_address_id",user.getDelivery_id());
		maps.put("order_main",1);
		maps.put("order_cat_no",2);
		maps.put("order_status_more_than",20);
		
		if (!CommUtil.null2String(keyword).equals("")) {
			
			maps.put("keyword_like",CommUtil.null2String(keyword));
			
			mv.addObject("keyword", keyword);
		}
		
		mv.addObject("orderFormTools", this.orderFormTools);
		IPageList pList = this.orderFormService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		mv.addObject("deliaddr", deliveryaddress);
		
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		mv.addObject("areas", areas);
		return mv;
	}
	
	/**
	 * 自提点管理首页
	 * @param request
	 * @param response
	 * @param id
	 * @param da_service_day
	 * @param area3
	 */
	@SecurityMapping(title = "自提点管理首页", value = "/delivery/ajax_base_save*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping({ "/delivery/ajax_base_save" })
	public void ajax_base_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String da_service_day,
			String area3) {
		
		DeliveryAddress deliveryaddress = null;
		DeliveryAddress obj = this.deliveryAddressService.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
		deliveryaddress = (DeliveryAddress) WebForm.toPo(request, obj);
		deliveryaddress.setDa_area(this.areaService.selectByPrimaryKey(CommUtil.null2Long(area3)));
		deliveryaddress.setDa_service_day(da_service_day.toString());
		this.deliveryAddressService.updateById(deliveryaddress);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 自提点管理首页
	 * @param request
	 * @param response
	 * @param id
	 * @param mark
	 */
	@SecurityMapping(title = "自提点管理首页", value = "/delivery/set_divery_status*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping({ "/delivery/set_divery_status" })
	public void set_divery_status(HttpServletRequest request,
			HttpServletResponse response, String id, String mark) {
		DeliveryAddress obj = this.deliveryAddressService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(id)));
		if (mark.equals("start")) {
			obj.setDa_status(10);
		}
		if (mark.equals("stop")) {
			obj.setDa_status(5);
		}
		this.deliveryAddressService.updateById(obj);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 自提点管理接收快件
	 * @param request
	 * @param response
	 * @param id
	 * @throws ParseException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "自提点管理接收快件", value = "/delivery/confirm_order*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping({ "/delivery/confirm_order" })
	public void confirm_order(HttpServletRequest request,
			HttpServletResponse response, String id) throws ParseException,
			UnsupportedEncodingException {
		OrderForm obj = this.orderFormService.selectByPrimaryKey(Long.valueOf(Long
				.parseLong(id)));
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((obj != null)
				&& (obj.getDelivery_address_id().equals(user.getDelivery_id()))) {
			obj.setOrder_status(35);
			String da_code = update_deliveryInfo_code(obj);
			this.orderFormService.updateById(obj);
			boolean ret = true;
			if (ret) {
				if (obj.getOrder_main() == 1) {
					if (!CommUtil.null2String(obj.getChild_order_detail())
							.equals("")) {
						List<Map> maps = this.orderFormTools.queryGoodsInfo(obj
								.getChild_order_detail());
						for (Map map : maps) {
							OrderForm child_order = this.orderFormService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("order_id")));
							child_order.setOrder_status(35);
							this.orderFormService.updateById(child_order);
						}
					}
				}
				OrderFormLog ofl = new OrderFormLog();
				ofl.setAddTime(new Date());
				ofl.setLog_info("自提点确认代收货");
				ofl.setLog_user_id(SecurityUserHolder.getCurrentUser().getId());
				ofl.setLog_user_name(SecurityUserHolder.getCurrentUser().getUserName());
				ofl.setOf(obj);
				this.orderFormLogService.saveEntity(ofl);
				User buyer = this.userService.selectByPrimaryKey(CommUtil.null2Long(obj.getUser_id()));
				DeliveryAddress deladd = this.deliveryAddressService.selectByPrimaryKey(user.getDelivery_id());
				String msg_content = "尊敬的买家您好，你购买的订单号为：" + obj.getOrder_id()
						+ "的商品快件已经到达" + deladd.getDa_name() + "，取件六位码为"
						+ da_code + ",请持该号码到指定自提点领取你的快件。";
				
				if (this.configService.getSysConfig().getSmsEnbale()) {
					String buyer_mobile = obj.getReceiver_mobile();
					if ((buyer_mobile != null) && (!buyer_mobile.equals(""))) {
						this.msgTools.sendSMS(buyer_mobile, msg_content);
					}
				}
				if (this.configService.getSysConfig().getEmailEnable()) {
					String buyerEmail = buyer.getEmail();
					if ((buyerEmail != null) && (!buyerEmail.equals(""))) {
						this.msgTools.sendEmail(buyerEmail, this.configService.getSysConfig().getTitle() + "系统消息",msg_content);
					}
				}
				Message msg = new Message();
				msg.setAddTime(new Date());
				msg.setStatus(0);
				msg.setType(1);
				msg.setContent(msg_content);
				msg.setFromUser(user);
				msg.setToUser(buyer);
				this.messageService.saveEntity(msg);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 自提点管理重发六位码
	 * @param request
	 * @param response
	 * @param orderForm_id
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "自提点管理重发六位码", value = "/delivery/delivery_code_again*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping({ "/delivery/delivery_code_again" })
	private void delivery_code_again(HttpServletRequest request,
			HttpServletResponse response, String orderForm_id)
			throws UnsupportedEncodingException {
		OrderForm orderForm = this.orderFormService.selectByPrimaryKey(Long.valueOf(Long.parseLong(orderForm_id)));
		DeliveryAddress deliveryAddress = this.deliveryAddressService.selectByPrimaryKey(orderForm.getDelivery_address_id());
		String notice = "重发六位码送失败！";
		if (deliveryAddress.getDa_user_id().equals(SecurityUserHolder.getCurrentUser().getId())) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			String da_code = update_deliveryInfo_code(orderForm);
			User buyer = this.userService.selectByPrimaryKey(CommUtil.null2Long(orderForm.getUser_id()));
			String msg_content = "尊敬的买家您好，你购买的订单号为：" + orderForm.getOrder_id()
					+ "的商品快件已经到达" + deliveryAddress.getDa_name() + "，取件六位码为"
					+ da_code + ",请持该号码到指定自提点领取您的快件。";
			notice = "新六位码已成功发至";
			Boolean ret = Boolean.valueOf(false);
			if (this.configService.getSysConfig().getSmsEnbale()) {
				String buyer_mobile = orderForm.getReceiver_mobile();
				if ((buyer_mobile != null) && (!buyer_mobile.equals(""))) {
					ret = Boolean.valueOf(this.msgTools.sendSMS(buyer_mobile,
							msg_content));
					if (ret.booleanValue()) {
						notice = notice + "手机";
					}
				}
			}
			if (this.configService.getSysConfig().getEmailEnable()) {
				String buyerEmail = buyer.getEmail();
				if ((buyerEmail != null) && (!buyerEmail.equals(""))) {
					ret = Boolean.valueOf(this.msgTools.sendEmail(buyerEmail,
							this.configService.getSysConfig().getTitle()
									+ "系统消息", msg_content));
					if (ret.booleanValue()) {
						notice = notice + ",邮箱";
					}
				}
			}
			Message msg = new Message();
			msg.setAddTime(new Date());
			msg.setStatus(0);
			msg.setType(1);
			msg.setContent(msg_content);
			msg.setFromUser(user);
			msg.setToUser(buyer);
			this.messageService.saveEntity(msg);
			ret = Boolean.valueOf(true);
			if (ret.booleanValue()) {
				notice = notice + ",站内信。";
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(notice);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 自提点管理六位码
	 * @param request
	 * @param response
	 * @param orderForm_id
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "自提点管理六位码", value = "/delivery/delivery_code*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping({ "/delivery/delivery_code" })
	private ModelAndView delivery_code(HttpServletRequest request,
			HttpServletResponse response, String orderForm_id,
			String currentPage) {
		ModelAndView mv = new RedPigJModelAndView("delivery/delivery_code.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("orderForm_id", orderForm_id);
		mv.addObject("currentPage", currentPage);
		return mv;
	}
	
	/**
	 * 自提点管理六位码验证
	 * @param request
	 * @param response
	 * @param delivery_code
	 * @param orderForm_id
	 * @param currentPage
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SecurityMapping(title = "自提点管理六位码验证", value = "/delivery/delivery_code_verify*", rtype = "delivery", rname = "自提点管理", rcode = "delivery_center", rgroup = "自提点管理")
	@RequestMapping({ "/delivery/delivery_code_verify" })
	private void delivery_code_verify(HttpServletRequest request,
			HttpServletResponse response, String delivery_code,
			String orderForm_id, String currentPage) throws Exception {
		String verify = "defeat";
		OrderForm orderForm = this.orderFormService.selectByPrimaryKey(Long
				.valueOf(Long.parseLong(orderForm_id)));
		DeliveryAddress deliveryAddress = this.deliveryAddressService
				.selectByPrimaryKey(orderForm.getDelivery_address_id());
		if (deliveryAddress.getDa_user_id().equals(
				SecurityUserHolder.getCurrentUser().getId())) {
			Map<String, String> infoMap = (Map) JSON.parseObject(orderForm.getDelivery_info(), Map.class);
			String infoCode = (String) infoMap.get("da_code");
			if ((infoCode != null) && (infoCode.equals(delivery_code))) {
				this.handelOrderFormService.confirmOrder(request, orderForm);
				verify = "success";
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(verify);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String update_deliveryInfo_code(OrderForm of) {
		Map<String,Object> infoMap = JSON.parseObject(of.getDelivery_info());
		String da_code = CommUtil.randomInt(6);
		infoMap.put("da_code", da_code);
		of.setDelivery_info(JSON.toJSONString(infoMap));
		return da_code;
	}
}
