package com.redpigmall.view.web.action.base;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.Log;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.qrcode.QRCodeUtil;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.RedPigCommUtil;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.corework.domain.LogType;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.domain.IntegralGoodsOrder;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.PredepositCash;
import com.redpigmall.domain.SnsAttention;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.SysLog;
import com.redpigmall.domain.SystemTip;
import com.redpigmall.domain.User;

/**
 * 
 * <p>
 * Title: BaseManageAction.java
 * </p>
 * 
 * <p>
 * Description:基础类
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
 * @date 2014-5-21
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "unused" })
@Controller
public class BaseManageAction extends BaseAction{
	
	private Logger logger = LoggerFactory.getLogger(BaseManageAction.class);
	/**
	 * 用户登陆
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@Log(title = "用户登陆", type = LogType.LOGIN)
	@RequestMapping({ "/login_success" })
	public void login_success(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		if (SecurityUserHolder.getCurrentUser() != null) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			if (this.configService.getSysConfig().getIntegral()) {
				if (user.getLoginDate() == null
						|| user.getLoginDate().before(
								CommUtil.formatDate(CommUtil
										.formatShortDate(new Date())))) {
					user.setIntegral(user.getIntegral()
							+ this.configService.getSysConfig()
									.getMemberDayLogin());
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户"
							+ CommUtil.formatLongDate(new Date())
							+ "登录增加"
							+ this.configService.getSysConfig()
									.getMemberDayLogin() + "分");
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("login");
					this.integralLogService.saveEntity(log);
				}
			}
			user.setLoginDate(new Date());
			user.setLoginIp(CommUtil.getIpAddr(request));
			user.setLoginCount(user.getLoginCount() + 1);
			this.userService.updateById(user);
			HttpSession session = request.getSession(false);
			session.setAttribute("user", user);
			session.setAttribute("userName", user.getUsername());
			session.setAttribute("lastLoginDate", new Date());// 设置登录时间
			session.setAttribute("loginIp", CommUtil.getIpAddr(request));// 设置登录IP
			session.setAttribute("login", true);// 设置登录标识
			String role = user.getUserRole();
			String url = CommUtil.getURL(request) + "/user_login_success";
			if (!CommUtil.null2String(
					request.getSession(false).getAttribute("refererUrl"))
					.equals("")) {
				url = CommUtil.null2String(request.getSession(false)
						.getAttribute("refererUrl"));
			}
			String login_role = (String) session.getAttribute("login_role");
			boolean ajax_login = CommUtil.null2Boolean(session
					.getAttribute("ajax_login"));
			if (ajax_login) {
				response.setContentType("text/plain");
				response.setHeader("Cache-Control", "no-cache");
				response.setCharacterEncoding("UTF-8");
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print("success");
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if (login_role.equalsIgnoreCase("admin")) {
					if (role.indexOf("ADMIN") >= 0) {
						url = CommUtil.getURL(request) + "/index";
						request.getSession(false).setAttribute("admin_login",
								true);
					}
				}
				if (login_role.equalsIgnoreCase("seller")
						&& role.indexOf("SELLER") >= 0) {
					url = CommUtil.getURL(request) + "/index";
					request.getSession(false)
							.setAttribute("seller_login", true);
				}
				if (!CommUtil.null2String(
						request.getSession(false).getAttribute("refererUrl"))
						.equals("")) {
					url = CommUtil.null2String(request.getSession(false)
							.getAttribute("refererUrl"));
					request.getSession(false).removeAttribute("refererUrl");
				}
				String userAgent = request.getHeader("user-agent");
				if (userAgent != null && userAgent.indexOf("Mobile") > 0) {
					url = CommUtil.getURL(request) + "/index";
				}
				response.sendRedirect(url);
			}
		} else {
			String url = CommUtil.getURL(request) + "/index";
			response.sendRedirect(url);
		}

	
	}
	
	/**
	 * 登陆成功
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/user_login_success" })
	public String user_login_success(HttpServletRequest request,
			HttpServletResponse response) {
		String url = CommUtil.getURL(request) + "/index";
		HttpSession session = request.getSession(false);
		if ((session.getAttribute("refererUrl") != null)
				&& (!session.getAttribute("refererUrl").equals(""))) {
			url = (String) session.getAttribute("refererUrl");
			session.removeAttribute("refererUrl");
		}
		String bind = CommUtil.null2String(request.getSession(false)
				.getAttribute("bind"));
		if (!bind.equals("")) {
			return "redirect:out_login_success";
		}
		return "redirect:" + url;
	}
	
	/**
	 * 退出成功
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping({ "/logout_success" })
	public void logout_success(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		HttpSession session = request.getSession(false);
		boolean admin_login = RedPigCommUtil.null2Boolean(session.getAttribute("admin_login"));
		
		boolean seller_login = RedPigCommUtil.null2Boolean(session.getAttribute("seller_login"));
		
		String targetUrl = RedPigCommUtil.getURL(request) + "/index";
		
		String userName = RedPigCommUtil.null2String(session.getAttribute("userName"));
		
		this.sessionRegistry.removeSessionRegistry(userName);
		
		session.removeAttribute("admin_login");
		session.removeAttribute("seller_login");
		session.removeAttribute("user");
		session.removeAttribute("userName");
		session.removeAttribute("login");
		session.removeAttribute("role");
		session.removeAttribute("cart");
		
		((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession(false).removeAttribute(Globals.USER_LOGIN);
		
		String userAgent = request.getHeader("user-agent");
		if ((userAgent != null) && (userAgent.indexOf("Mobile") > 0)) {
			targetUrl = RedPigCommUtil.getURL(request) + "/index";
		}
		
		Map<String, Object> map = JSON.parseObject(session
				.getAttribute("weixin_bind") != null ? session.getAttribute(
				"weixin_bind").toString() : "");
		
		//注释掉,微信绑定之后需要开启
//		targetUrl = weixinBind(request, session, targetUrl, map);
		
		response.sendRedirect(targetUrl);
	}

	private String weixinBind(HttpServletRequest request, HttpSession session,
			String targetUrl, Map<String, Object> map) {
		String userName;
		if (map != null) {
			if (RedPigCommUtil.null2Boolean(map.get("login"))) {
				boolean login = RedPigCommUtil.null2Boolean(map.get("login").toString());
				if ((login) && (map.get("userName") != null) && (map.get("passwd") != null)) {
					
					userName = map.get("userName").toString();
					String passwd = map.get("passwd").toString();
					
					targetUrl = RedPigCommUtil.getURL(request)
							+ "/redpigmall_login?username="
							+ RedPigCommUtil.encode(userName) + "&password="
							+ "redpigmall_thid_login_" + passwd
							+ "&encode=true&login_role=user";
					
					if (RedPigCommUtil.null2Boolean(map.get("login"))) {
						Long id = RedPigCommUtil.null2Long(map.get("userId"));
						User user = this.userService.selectByPrimaryKey(id);
						user.getRoles().clear();
						this.userService.deleteUserRole(user.getId(), user.getRoles());
						user.getStore();
						for (CouponInfo ci : user.getCouponinfos()) {
							this.couponInfoService.deleteById(ci.getId());
						}
						user.getCouponinfos().remove(user.getCouponinfos());
						for (Accessory acc : user.getFiles()) {
							if ((acc.getAlbum() != null) && (acc.getAlbum().getAlbum_cover() != null)) {
								if (acc.getAlbum().getAlbum_cover().getId().equals(acc.getId())) {
									acc.getAlbum().setAlbum_cover(null);
									this.albumService.updateById(acc.getAlbum());
								}
							}
							RedPigCommUtil.del_acc(request, acc);
							this.accessoryService.delete(acc.getId());
						}
						
						user.getFiles().removeAll(user.getFiles());
						
						this.goodsCartService.batchDelObjs(user.getGoodscarts());
						
						Map<String, Object> params = Maps.newHashMap();
						params.put("cash_user_id", user.getId());
						List<PredepositCash> PredepositCash_list = this.redepositcashService.queryPageList(params);
						this.redepositcashService.batchDelObjs(PredepositCash_list);
						
						params.clear();
						params.put("igo_user_id", user.getId());
						List<IntegralGoodsOrder> integralGoodsOrders = this.integralGoodsOrderService.queryPageList(params);
						this.integralGoodsOrderService.batchDelObjs(integralGoodsOrders);
						
						params.clear();
						params.put("integral_user_id", user.getId());
						List<IntegralLog> integralLogs = this.integralLogService.queryPageList(params);
						this.integralLogService.batchDelObjs(integralLogs);
						
						params.clear();
						params.put("gl_user_id", user.getId());
						List<GoldLog> GoldLog_list = this.goldlogService.queryPageList(params);
						this.goldlogService.batchDelObjs(GoldLog_list);
						
						params.clear();
						params.put("user_id", user.getId());
						List<StorePoint> storepoint_list = this.storepointService.queryPageList(params);
						this.storepointService.batchDelObjs(storepoint_list);
						
						params.clear();
						params.put("ad_user_id", user.getId());
						List<Advert> adv_list = this.advertService.queryPageList(params);
						this.advertService.batchDelObjs(adv_list);
						
						if ((user.getDelivery_id() != null) && (!user.getDelivery_id().equals(""))) {
							this.deliveryAddressService.deleteById(user.getDelivery_id());
						}
						
						params.clear();
						params.put("redpig_fromUser_id", user.getId());
						params.put("redpig_toUser_id", user.getId());
						List<SnsAttention> snsAttentions = this.snsAttentionService.queryPageList(params);
						this.snsAttentionService.batchDelObjs(snsAttentions);
						
						
						params.clear();
						params.put("user_id", user.getId());
						List<SysLog> logs = this.syslogService.queryPageList(params);
						
						this.syslogService.batchDelObjs(logs);
						
						this.userService.deleteById(user.getId());
						
						
					}
				}
			}
			session.removeAttribute("weixin_bind");
		}
		return targetUrl;
	}
	
	/**
	 * 登陆失败
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/login_error" })
	public ModelAndView login_error(HttpServletRequest request,
			HttpServletResponse response) {
		String login_role = (String) request.getSession(false).getAttribute(
				"login_role");
		ModelAndView mv = null;
		String userAgent = request.getHeader("user-agent");
		if ((userAgent != null) && (userAgent.indexOf("Mobile") > 0)) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("url", RedPigCommUtil.getURL(request) + "/index");
		} else {
			if (login_role == null) {
				login_role = "user";
			}
			if (login_role.equalsIgnoreCase("admin")) {
				mv = new RedPigJModelAndView("admin/blue/login_error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 0, request,
						response);
			}
			if (login_role.equalsIgnoreCase("seller")) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", RedPigCommUtil.getURL(request) + "/seller/login");
			}
			if (login_role.equalsIgnoreCase("user")) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", RedPigCommUtil.getURL(request) + "/user/login");
			}
		}
		mv.addObject("op_title", "登录失败");
		return mv;
	}
	

	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/success" })
	public ModelAndView success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/RedPigError" })
	public ModelAndView error(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((SecurityUserHolder.getCurrentUser() != null) && (SecurityUserHolder.getCurrentUser().getUserRole().equalsIgnoreCase("ADMIN"))) {
			mv = new RedPigJModelAndView("admin/blue/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		mv.addObject("op_title",
				request.getSession(false).getAttribute("op_title"));
		mv.addObject("list_url", request.getSession(false).getAttribute("url"));
		mv.addObject("url", request.getSession(false).getAttribute("url"));
		request.getSession(false).removeAttribute("op_title");
		request.getSession(false).removeAttribute("url");
		return mv;
	}

	@RequestMapping({ "/exception" })
	public ModelAndView exception(HttpServletRequest request,
			HttpServletResponse response) {
		User user = (User) request.getSession().getAttribute("user");
		ModelAndView mv = new RedPigJModelAndView("error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if ((user != null) && (user.getUserRole().equalsIgnoreCase("ADMIN"))) {
			mv = new RedPigJModelAndView("admin/blue/exception.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		} else {
			mv.addObject("op_title", "系统出现异常");
			mv.addObject("url", RedPigCommUtil.getURL(request) + "/index");
		}
		return mv;
	}

	@RequestMapping({ "/authority" })
	public ModelAndView authority(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/authority.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		boolean domain_error = false;// RedPigCommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
		if (domain_error) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "域名绑定错误，请与http://www.redpigmall.net联系");
		}
		return mv;
	}

	@RequestMapping({ "/voice" })
	public ModelAndView voice(HttpServletRequest request,
			HttpServletResponse response) {
		return new RedPigJModelAndView("include/flash/soundPlayer.swf",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), request, response);
	}

	@RequestMapping({ "/getCode" })
	public void getCode(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession(false);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print("result=true&code=" + (String) session.getAttribute("verify_code"));
	}
	
	@RequestMapping({ "/upload" })
	public void upload(HttpServletRequest request, HttpServletResponse response)
			throws ClassNotFoundException {
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ this.configService.getSysConfig().getUploadFilePath()
				+ File.separator + "common";
		String webPath = request.getContextPath().equals("/") ? "" : request
				.getContextPath();
		if ((this.configService.getSysConfig().getAddress() != null)
				&& (!this.configService.getSysConfig().getAddress().equals(""))) {
			webPath = this.configService.getSysConfig().getAddress() + webPath;
		}
		JSONObject obj = new JSONObject();
		try {
			Map<String, Object> map = RedPigCommUtil.saveFileToServer(request,
					"imgFile", saveFilePathName, null, null);
			String url = webPath + "/"
					+ this.configService.getSysConfig().getUploadFilePath()
					+ "/common/" + map.get("fileName");
			obj.put("error", Integer.valueOf(0));
			obj.put("url", url);
		} catch (IOException e) {
			obj.put("error", Integer.valueOf(1));
			obj.put("message", e.getMessage());
			e.printStackTrace();
		}
		response.setContentType("text/html");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(obj.toJSONString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping({ "/js" })
	public ModelAndView js(HttpServletRequest request,
			HttpServletResponse response, String js) {
		ModelAndView mv = new RedPigJModelAndView("resources/js/" + js + ".js",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		return mv;
	}
	
}
