package com.redpigmall.manage.delivery.action;

import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.DeliveryAddress;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: RedPigDeliveryApplyAction.java
 * </p>
 * 
 * <p>
 * Description: 会员自提点申请控制器
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
 * @date 2014-11-19
 * 
 * @version redpigmall_b2b2c_2015
 */
@Controller
public class RedPigDeliveryApplyAction extends BaseAction {
	
	/**
	 * 自提点申请入口
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping({ "/delivery_apply0" })
	public ModelAndView delivery_apply0(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("delivery/delivery_apply0.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String, Object> params = Maps.newHashMap();
		params.put("display", Boolean.valueOf(true));
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
        
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(params,0, 12);
		
		mv.addObject("gcs", gcs);
		mv.addObject("navTools", this.navTools);
		mv.addObject("gcViewTools", this.gcViewTools);
		String op = CommUtil.null2String(request.getAttribute("op"));
		if ("free".equals(op)) {
			mv.addObject("mark", "free/index");
		}
		head(request, response, mv);
		params.clear();
		params.put("parent", -1);
		List<Area> areas = this.areaService.queryPageList(params);
		
		mv.addObject("areas", areas);
		return mv;
	}

	/**
	 * 自提点申请1
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@SecurityMapping(title = "自提点申请第一步", value = "/delivery_apply1*", rtype = "buyer", rname = "自提点申请", rcode = "delivery_apply", rgroup = "自提点申请")
	@RequestMapping({ "/delivery_apply1" })
	public ModelAndView delivery_apply1(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("delivery/delivery_apply1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if (user.getDelivery_id() == null) {
			return mv;
		}
		DeliveryAddress da = this.deliveryAddressService.selectByPrimaryKey(user.getDelivery_id());
		
		mv = new RedPigJModelAndView("delivery/delivery_notice.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		notice(request, response, mv);
		if (da.getDa_status() == 0) {
			mv.addObject("notice", "我们正在审核您的申请单...");
			return mv;
		}
		if (da.getDa_status() == 4) {
			mv.addObject("notice", "您的申请单未通过审核...");
			mv.addObject("again", "true");
			return mv;
		}
		if (da.getDa_status() > 4) {
			mv.addObject("notice", "您的申请单已经通过！");
			return mv;
		}
		return mv;
	}

	/**
	 * 自提点申请2
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "自提点申请第二步", value = "/delivery_apply2*", rtype = "buyer", rname = "自提点申请", rcode = "delivery_apply", rgroup = "自提点申请")
	@RequestMapping({ "/delivery_apply2" })
	public ModelAndView delivery_apply2(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("delivery/delivery_apply2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Map<String,Object> maps = Maps.newHashMap();
        maps.put("parent", -1);
        
		List<Area> areas = this.areaService.queryPageList(maps);
		
		mv.addObject("areas", areas);
		String delivery_session = CommUtil.randomString(32);
		request.getSession(false).setAttribute("delivery_session",delivery_session);
		mv.addObject("delivery_session", delivery_session);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		mv.addObject("obj",this.deliveryAddressService.selectByPrimaryKey(user.getDelivery_id()));
		return mv;
	}

	/**
	 * 自提点申请3
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@SecurityMapping(title = "自提点申请第三步", value = "/delivery_apply3*", rtype = "buyer", rname = "自提点申请", rcode = "delivery_apply", rgroup = "自提点申请")
	@RequestMapping({ "/delivery_apply3" })
	public ModelAndView delivery_apply3(HttpServletRequest request,
			HttpServletResponse response, String id, String da_service_day,
			String area3, String delivery_session) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("delivery/delivery_notice.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String delivery_session1 = (String) request.getSession(false)
				.getAttribute("delivery_session");
		if (CommUtil.null2String(delivery_session1).equals(delivery_session)) {
			request.getSession(false).removeAttribute("delivery_session");
			
			DeliveryAddress deliveryaddress = null;
			if ((id == null) || (id.equals(""))) {
				deliveryaddress = (DeliveryAddress) WebForm.toPo(request,
						DeliveryAddress.class);
				deliveryaddress.setAddTime(new Date());
			} else {
				DeliveryAddress obj = this.deliveryAddressService
						.selectByPrimaryKey(Long.valueOf(Long.parseLong(id)));
				deliveryaddress = (DeliveryAddress) WebForm.toPo(request, obj);
			}
			deliveryaddress.setDa_area(this.areaService.selectByPrimaryKey(CommUtil
					.null2Long(area3)));
			deliveryaddress.setDa_service_day(da_service_day.toString());
			deliveryaddress.setDa_type(1);
			deliveryaddress.setDa_status(0);
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			deliveryaddress.setDa_user_id(user.getId());
			deliveryaddress.setDa_user_name(user.getUserName());
			if ((id == null) || (id.equals(""))) {
				this.deliveryAddressService.saveEntity(deliveryaddress);
			} else {
				this.deliveryAddressService.updateById(deliveryaddress);
			}
			user.setDelivery_id(deliveryaddress.getId());
			this.userService.updateById(user);
			mv.addObject("notice", "您已成功提交申请，我们会尽快处理...");
		} else {
			mv.addObject("notice", "我们正在审核您的申请单...");
		}
		notice(request, response, mv);
		return mv;
	}

	private void head(HttpServletRequest request, HttpServletResponse response,
			ModelAndView mv) throws Exception {
		String type = CommUtil.null2String(request.getAttribute("type"));
		String cart_session_id = "";
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			
			
			for (Cookie cookie : cookies) {
				
				if (cookie.getName().equals("cart_session_id")) {
					cart_session_id = CommUtil.null2String(URLDecoder.decode(cookie.getValue(), "UTF-8"));
				}
			}
		}
		if (cart_session_id.equals("")) {
			cart_session_id = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("cart_session_id", cart_session_id);
			cookie.setDomain(CommUtil.generic_domain(request));
		}
		List<GoodsCart> carts_list = Lists.newArrayList();
		List<GoodsCart> carts_cookie = Lists.newArrayList();
		List<GoodsCart> carts_user = Lists.newArrayList();
		User user = SecurityUserHolder.getCurrentUser();
		Map<String,Object> cart_map = Maps.newHashMap();
		if (user != null) {
			user = this.userService.selectByPrimaryKey(user.getId());
			if (!cart_session_id.equals("")) {
				cart_map.clear();
				cart_map.put("cart_session_id", cart_session_id);
				cart_map.put("cart_status", Integer.valueOf(0));
				
				carts_cookie = this.goodsCartService.queryPageList(cart_map);
				
				if (user.getStore() != null) {
					for (GoodsCart gc : carts_cookie) {
						if (gc.getGoods().getGoods_type() == 1) {
							if (gc.getGoods().getGoods_store().getId()
									.equals(user.getStore().getId())) {
								this.goodsCartService.deleteById(gc.getId());
							}
						}
					}
				}
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", Integer.valueOf(0));
				
				carts_user = this.goodsCartService.queryPageList(cart_map);
				
			} else {
				cart_map.clear();
				cart_map.put("user_id", user.getId());
				cart_map.put("cart_status", Integer.valueOf(0));
				
				carts_user = this.goodsCartService.queryPageList(cart_map);
				
			}
		} else if (!cart_session_id.equals("")) {
			cart_map.clear();
			cart_map.put("cart_session_id", cart_session_id);
			cart_map.put("cart_status", Integer.valueOf(0));
			
			carts_cookie = this.goodsCartService.queryPageList(cart_map);
			
		}
		boolean add;
		if (user != null) {
			for (GoodsCart cookie : carts_cookie) {
				add = true;
				for (GoodsCart gc2 : carts_user) {
					if ((cookie.getGoods().getId().equals(gc2.getGoods()
							.getId()))
							&& (cookie.getSpec_info()
									.equals(gc2.getSpec_info()))) {
						add = false;
						this.goodsCartService.deleteById(cookie.getId());
					}
				}
				if (add) {
					cookie.setCart_session_id(null);
					cookie.setUser(user);
					this.goodsCartService.updateById(cookie);
					carts_list.add(cookie);
				}
			}
		} else {
			for (GoodsCart gc : carts_cookie) {
				carts_list.add(gc);
			}
		}
		for (GoodsCart gc : carts_user) {
			carts_list.add(gc);
		}
		List<GoodsCart> combin_carts_list = Lists.newArrayList();
		for (GoodsCart gc : carts_list) {
			if ((gc.getCart_type() != null)
					&& (gc.getCart_type().equals("combin"))
					&& (gc.getCombin_main() != 1)) {
				combin_carts_list.add(gc);
			}
		}
		if (combin_carts_list.size() > 0) {
			carts_list.removeAll(combin_carts_list);
		}
		mv.addObject("carts", carts_list);
		mv.addObject("type", type.equals("") ? "goods" : type);
	}

	/**
	 * 自提点申请入口，查询本城市自提点
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/query_area_delivery" })
	public ModelAndView query_area_delivery(HttpServletRequest request,
			HttpServletResponse response, String city_id, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"delivery/query_area_delivery.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,10,"addTime", "desc");
        maps.put("da_area_parent_id",CommUtil.null2Long(city_id));
        maps.put("da_status",10);
        
		IPageList pList = this.deliveryAddressService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		String url2 = CommUtil.getURL(request) + "/query_area_delivery";
		mv.addObject("objs", pList.getResult());
		mv.addObject("gotoPageAjaxHTML", CommUtil.showPageAjaxHtml(url2, "",
				pList.getCurrentPage(), pList.getPages(), pList.getPageSize()));
		mv.addObject("DeliveryAddressTools", this.DeliveryAddressTools);
		return mv;
	}

	private void notice(HttpServletRequest request,
			HttpServletResponse response, ModelAndView mv) throws Exception {
		Map<String, Object> params = Maps.newHashMap();
		params.put("display", Boolean.valueOf(true));
		params.put("parent", -1);
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		
		List<GoodsClass> gcs = this.goodsClassService.queryPageList(params,0, 12);
		
		mv.addObject("gcs", gcs);
		mv.addObject("navTools", this.navTools);
		mv.addObject("gcViewTools", this.gcViewTools);
		String op = CommUtil.null2String(request.getAttribute("op"));
		if ("free".equals(op)) {
			mv.addObject("mark", "free/index");
		}
		head(request, response, mv);
	}
}
