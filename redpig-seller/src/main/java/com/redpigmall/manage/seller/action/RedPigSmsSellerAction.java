package com.redpigmall.manage.seller.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.SmsGoldLog;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.Template;
import com.redpigmall.domain.User;
import com.redpigmall.manage.seller.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigSmsSellerAction.java
 * </p>
 * 
 * <p>
 * Description: 商家购买平台短信邮件控制器
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
 * @date 2014-10-31
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Controller
public class RedPigSmsSellerAction extends BaseAction{
	/**
	 * 短信邮件
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "短信邮件", value = "/sms_email*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email" })
	public ModelAndView sms_email(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/sms_email.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		if ((sc.getSms_buy() != 1) && (sc.getEmail_buy() != 1)) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", CommUtil.getURL(request) + "/index");
			mv.addObject("op_title", "系统没有开启邮件和短信购买功能");
			return mv;
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		String sms_email_session = "sms_email_" + UUID.randomUUID();
		request.getSession(false).setAttribute("sms_email_session",
				sms_email_session);
		mv.addObject("account_gold", Integer.valueOf(user.getGold()));
		mv.addObject("account_sms",
				Integer.valueOf(user.getStore().getStore_sms_count()));
		mv.addObject("account_email",
				Integer.valueOf(user.getStore().getStore_email_count()));
		mv.addObject("sms_email_session", sms_email_session);
		return mv;
	}
	
	/**
	 * 短信邮件购买
	 * @param request
	 * @param response
	 * @param type
	 * @param count
	 * @param sms_email_session
	 * @return
	 */
	@SecurityMapping(title = "短信邮件购买", value = "/sms_email_buy*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email_buy" })
	public ModelAndView sms_email_buy(HttpServletRequest request,
			HttpServletResponse response, String type, String count,
			String sms_email_session) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/seller_error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("url", "/sms_email");
		mv.addObject("op_title", "参数错误或者重复提交");
		SysConfig sc = this.configService.getSysConfig();
		if ((sc.getSms_buy() != 1) && (sc.getEmail_buy() != 1)) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/index");
			mv.addObject("op_title", "系统没有开启邮件和短信购买功能");
			return mv;
		}
		boolean ret = true;
		if (type == null) {
			ret = false;
		} else if ((!type.equals("sms")) && (!type.equals("email"))) {
			ret = false;
		}
		if (count == null) {
			ret = false;
		} else {
			int temp_count = CommUtil.null2Int(count);
			if (temp_count < 0) {
				ret = false;
			}
		}
		if (ret) {
			String temp_session = CommUtil.null2String(request
					.getSession(false).getAttribute("sms_email_session"));
			if ((temp_session != null)
					&& (sms_email_session.equals(temp_session))) {
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());
				user = user.getParent() == null ? user : user.getParent();
				int price = this.configService.getSysConfig().getSms_buy_cost();
				if (type.equals("email")) {
					price = this.configService.getSysConfig()
							.getEmail_buy_cost();
				}
				SmsGoldLog obj = new SmsGoldLog();
				if (type.equals("sms")) {
					obj.setTitle("短信");
				} else {
					obj.setTitle("邮件");
				}
				obj.setLog_type(type);
				obj.setAddTime(new Date());
				obj.setSeller_id(user.getId().longValue());
				obj.setStore_name(user.getStore().getStore_name());
				obj.setCount(CommUtil.null2Int(count));
				obj.setGold(price);
				obj.setAll_gold((int) CommUtil.mul(Integer.valueOf(CommUtil
						.null2Int(count)), Integer.valueOf(CommUtil
						.null2Int(Integer.valueOf(price)))));
				obj.setLog_status(0);
				mv = new RedPigJModelAndView(
						"user/default/sellercenter/sms_email_buy.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
				mv.addObject("obj", obj);
				mv.addObject("sms_email_session", sms_email_session);
			}
		}
		return mv;
	}
	
	/**
	 * 短信邮件购买
	 * @param request
	 * @param response
	 * @param count
	 * @param log_type
	 * @param id
	 */
	@SecurityMapping(title = "短信邮件购买", value = "/sms_email_buy_save*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email_buy_save" })
	public void sms_email_buy_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String count, String log_type,
			String id) {
		int code = -100;
		boolean ret = true;
		int all_gold = 0;
		int gold = 0;
		int temp_count = 0;
		String title = "";
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if (log_type == null) {
			code = -100;
			ret = false;
		} else if ((!log_type.equals("sms")) && (!log_type.equals("email"))) {
			code = -100;
			ret = false;
		}
		if (ret) {
			if (CommUtil.null2Int(count) <= 0) {
				code = -100;
				ret = false;
			} else {
				temp_count = CommUtil.null2Int(count);
			}
		}
		if (request.getSession(false).getAttribute("sms_email_session") == null) {
			code = 65136;
			ret = false;
		}
		if (ret) {
			if (log_type.equals("sms")) {
				gold = this.configService.getSysConfig().getSms_buy_cost();
				all_gold = gold * temp_count;
				title = "短信";
			}
			if (log_type.equals("email")) {
				gold = this.configService.getSysConfig().getEmail_buy_cost();
				all_gold = gold * temp_count;
				title = "邮件";
			}
		}
		SmsGoldLog obj = new SmsGoldLog();
		if ((ret) || (code == 65236)) {
			obj.setTitle(title);
			obj.setLog_type(log_type);
			obj.setAddTime(new Date());
			obj.setSeller_id(user.getId().longValue());
			obj.setStore_name(user.getStore().getStore_name());
			obj.setCount(temp_count);
			obj.setGold(gold);
			obj.setAll_gold(all_gold);
			obj.setLog_status(0);
			this.SmsGoldLogService.saveEntity(obj);
		}
		if ((id != null) && (!id.equals(""))) {
			SmsGoldLog sgl = this.SmsGoldLogService.selectByPrimaryKey(CommUtil
					.null2Long(id));
			if ((sgl != null)
					&& (sgl.getSeller_id() == user.getId().longValue())) {
				ret = true;
				obj = sgl;
				log_type = obj.getLog_type();
				temp_count = obj.getCount();
				all_gold = obj.getAll_gold();
			}
		}
		if (ret) {
			if (user.getGold() >= all_gold) {
				obj.setLog_status(5);
				this.SmsGoldLogService.updateById(obj);

				user.setGold(user.getGold() - obj.getAll_gold());
				this.userService.updateById(user);
				if (obj.getLog_type().equals("sms")) {
					store.setStore_sms_count(store.getStore_sms_count()
							+ obj.getCount() * 100);
				}
				if (obj.getLog_type().equals("email")) {
					store.setStore_email_count(store.getStore_email_count()
							+ obj.getCount() * 100);
				}
				this.storeService.updateById(store);
				GoldLog log = new GoldLog();
				log.setAddTime(new Date());
				if (log_type.equals("sms")) {
					log.setGl_content("购买短信扣除金币");
				}
				if (log_type.equals("email")) {
					log.setGl_content("购买邮件扣除金币");
				}
				log.setGl_count(obj.getAll_gold());
				log.setGl_user(user);
				log.setGl_type(-1);
				log.setGl_money(obj.getAll_gold());
				this.goldLogService.saveEntity(log);
				boolean flag = true;
				if (flag) {
					code = 100;
				}
				request.getSession(false).removeAttribute("sms_email_session");
			} else {
				code = 65236;
				ret = false;
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(code);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 短信邮件记录
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "短信邮件记录", value = "/sms_email_log*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email_log" })
	public ModelAndView sms_email_log(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/sms_email_log.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		if ((sc.getSms_buy() != 1) && (sc.getEmail_buy() != 1)) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/index");
			mv.addObject("op_title", "系统没有开启邮件和短信购买功能");
			return mv;
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,15,"addTime", "desc");
		maps.put("seller_id", user.getId());
		
		IPageList pList = this.SmsGoldLogService.list(maps);
		
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 短信邮件记录删除
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param id
	 * @return
	 */
	@SecurityMapping(title = "短信邮件记录删除", value = "/sms_email_log_dele*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email_log_dele" })
	public String sms_email_log_dele(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String id) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		SmsGoldLog obj = this.SmsGoldLogService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if ((obj != null) && (obj.getSeller_id() == user.getId().longValue())) {
			this.SmsGoldLogService.deleteById(obj.getId());
		}
		return "redirect:/sms_email_log?currentPage=" + currentPage;
	}
	
	/**
	 * 短信邮件功能设置
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "短信邮件功能设置", value = "/sms_email_set*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email_set" })
	public ModelAndView sms_email_set(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/sellercenter/sms_email_set.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		SysConfig sc = this.configService.getSysConfig();
		if ((sc.getSms_buy() != 1) && (sc.getEmail_buy() != 1)) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/index");
			mv.addObject("op_title", "系统没有开启邮件和短信购买功能");
			return mv;
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((store.getStore_sms_count() == 0)
				&& (store.getStore_email_count() == 0)) {
			mv = new RedPigJModelAndView(
					"user/default/sellercenter/seller_error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
			mv.addObject("url", "/sms_email");
			mv.addObject("op_title", "购买短信或者邮件后才能开启本功能");
			return mv;
		}
		if (store.getSms_email_info() != null) {
			List<Map> functions = JSON.parseArray(store.getSms_email_info(),
					Map.class);
			mv.addObject("objs", functions);
		}
		return mv;
	}
	
	/**
	 * 短信邮件功能数据初始化
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "短信邮件功能数据初始化", value = "/sms_email_init*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email_init" })
	public String sms_email_init(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		Map<String, Object> map = Maps.newHashMap();
		Set params = Sets.newTreeSet();
		params.add("email_tobuyer_selforder_ship_notify");
		params.add("email_tobuyer_selforder_cancel_notify");
		params.add("email_tobuyer_selforder_update_fee_notify");
		params.add("email_tobuyer_order_outline_pay_ok_notify");
		params.add("email_toseller_outline_pay_ok_notify");
		params.add("sms_tobuyer_selforder_ship_notify");
		params.add("sms_tobuyer_selforder_cancel_notify");
		params.add("sms_tobuyer_selforder_fee_notify");
		params.add("sms_tobuyer_pws_modify_notify");
		params.add("sms_tobuyer_mobilebind_notify");
		params.add("sms_toseller_outline_pay_ok_notify");
		params.add("sms_tobuyer_order_outline_pay_ok_notify");
		params.add("msg_toseller_store_update_refuse_notify");
		map.put("marks", params);
		map.put("type", "msg");
		map.put("orderBy", "addTime");
		map.put("orderType", "desc");
		
		List<Template> objs = this.templateService.queryPageList(map);
		
		List<Map> map_list = Lists.newArrayList();
		for (Template obj : objs) {
			Map temp = Maps.newHashMap();
			temp.put("id", obj.getId());
			temp.put("mark", obj.getMark());
			temp.put("type", obj.getType());
			temp.put("title", obj.getTitle());
			temp.put("sms_count", Integer.valueOf(0));
			temp.put("sms_open", Integer.valueOf(0));
			temp.put("email_count", Integer.valueOf(0));
			temp.put("email_open", Integer.valueOf(0));
			map_list.add(temp);
		}
		String json = JSON.toJSONString(map_list);
		store.setSms_email_info(json);
		this.storeService.updateById(store);
		return "redirect:/sms_email_set";
	}
	
	/**
	 * 短信邮件功能开启
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @param all
	 * @return
	 */
	@SecurityMapping(title = "短信邮件功能开启", value = "/sms_email_open*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email_open" })
	public String sms_email_open(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String all) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((all != null) && (all.equals("all"))) {
			if (store.getSms_email_info() != null) {
				List<Map> objs = JSON.parseArray(store.getSms_email_info(),
						Map.class);
				for (Map map : objs) {
					if ((type.equals("sms")) && (map.get("type").equals("sms"))) {
						map.put("sms_open", Integer.valueOf(1));
					}
					if ((type.equals("email"))
							&& (map.get("type").equals("email"))) {
						map.put("email_open", Integer.valueOf(1));
					}
				}
				String json = JSON.toJSONString(objs);
				store.setSms_email_info(json);
				this.storeService.updateById(store);
			}
		} else if ((id != null) && (!id.equals("")) && (type != null)
				&& (!type.equals("")) && (store.getSms_email_info() != null)) {
			List<Map> objs = JSON.parseArray(store.getSms_email_info(),
					Map.class);
			for (Map map : objs) {
				if (id.equals(CommUtil.null2String(map.get("id")))) {
					if (type.equals("sms")) {
						map.put("sms_open", Integer.valueOf(1));
						break;
					}
					map.put("email_open", Integer.valueOf(1));

					break;
				}
			}
			String json = JSON.toJSONString(objs);
			store.setSms_email_info(json);
			this.storeService.updateById(store);
		}
		return "redirect:/sms_email_set";
	}
	
	/**
	 * 短信邮件功能关闭
	 * @param request
	 * @param response
	 * @param id
	 * @param type
	 * @param all
	 * @return
	 */
	@SecurityMapping(title = "短信邮件功能关闭", value = "/sms_email_close*", rtype = "seller", rname = "短信邮件", rcode = "sms_email_seller", rgroup = "其他管理")
	@RequestMapping({ "/sms_email_close" })
	public String sms_email_close(HttpServletRequest request,
			HttpServletResponse response, String id, String type, String all) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user = user.getParent() == null ? user : user.getParent();
		Store store = user.getStore();
		if ((all != null) && (all.equals("all"))) {
			if (store.getSms_email_info() != null) {
				List<Map> objs = JSON.parseArray(store.getSms_email_info(),
						Map.class);
				for (Map map : objs) {
					if ((type.equals("sms")) && (map.get("type").equals("sms"))) {
						map.put("sms_open", Integer.valueOf(0));
					}
					if ((type.equals("email"))
							&& (map.get("type").equals("email"))) {
						map.put("email_open", Integer.valueOf(0));
					}
				}
				String json = JSON.toJSONString(objs);
				store.setSms_email_info(json);
				this.storeService.updateById(store);
			}
		} else if ((id != null) && (!id.equals("")) && (type != null)
				&& (!type.equals("")) && (store.getSms_email_info() != null)) {
			List<Map> objs = JSON.parseArray(store.getSms_email_info(),
					Map.class);
			for (Map map : objs) {
				if (id.equals(CommUtil.null2String(map.get("id")))) {
					if (type.equals("sms")) {
						map.put("sms_open", Integer.valueOf(0));
						break;
					}
					map.put("email_open", Integer.valueOf(0));

					break;
				}
			}
			String json = JSON.toJSONString(objs);
			store.setSms_email_info(json);
			this.storeService.updateById(store);
		}
		return "redirect:/sms_email_set";
	}
}
