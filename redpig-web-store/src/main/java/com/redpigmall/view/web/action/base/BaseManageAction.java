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
	@RequestMapping({ "/footer1" })
	public ModelAndView footer1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("footer1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("navTools", this.navTools);
		return mv;
	}
	
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
		//判断用户是否在线
		if (SecurityUserHolder.getCurrentUser() != null) {
			//查询用户
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
			//如果开通了积分商城
			if (this.configService.getSysConfig().getIntegral()) {
				//判断用户是否已经登录,如果已经登录则送每日积分
				if (user.getLoginDate() == null || user.getLoginDate().before(
								CommUtil.formatDate(CommUtil.formatShortDate(new Date())))) {
					//在原有基础上新增
					user.setIntegral(user.getIntegral() + this.configService.getSysConfig().getMemberDayLogin());
					//记录积分日志,在管理平台端可以查阅
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户"
							+ CommUtil.formatLongDate(new Date())
							+ "登录增加"
							+ this.configService.getSysConfig().getMemberDayLogin() 
							+ "分");
					
					log.setIntegral(this.configService.getSysConfig().getMemberRegister());
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
			
			/*
			 *前台登录完跳转到登录成功地址，在那个方法中会判断,页面中是否带了 refererUrl其他需要跳转的地址
			 *比如pc、admin、h5端,登录完直接跳转到首页index就可以,但是如果有其他的cms、crm等二级地址
			 *可以在页面上写上用来跳转到目标地址
			 */
			String url = CommUtil.getURL(request) + "/user_login_success";
			if (!CommUtil.null2String(request.getSession(false).getAttribute("refererUrl")).equals("")) {
				url = CommUtil.null2String(request.getSession(false).getAttribute("refererUrl"));
			}
			//登录角色
			String login_role = (String) session.getAttribute("login_role");
			boolean ajax_login = CommUtil.null2Boolean(session.getAttribute("ajax_login"));
			
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
				//如果是管理员,则记录管理员登录
				if (login_role.equalsIgnoreCase("admin")) {
					if (role.indexOf("ADMIN") >= 0) {
						url = CommUtil.getURL(request) + "/index";
						request.getSession(false).setAttribute("admin_login",true);
					}
				}
				//如果是商家登录，则记录商家登录
				if (login_role.equalsIgnoreCase("seller")
						&& role.indexOf("SELLER") >= 0) {
					url = CommUtil.getURL(request) + "/index";
					request.getSession(false)
							.setAttribute("seller_login", true);
				}
				//如果是指定目标地址,则修改url为refererUrl
				if (!CommUtil.null2String(
						request.getSession(false).getAttribute("refererUrl"))
						.equals("")) {
					url = CommUtil.null2String(request.getSession(false)
							.getAttribute("refererUrl"));
					request.getSession(false).removeAttribute("refererUrl");
				}
				//如果是手机端则跳转到手机h5 index页面
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
		
		String targetUrl = RedPigCommUtil.getURL(request) + "/login";
		
		String userName = RedPigCommUtil.null2String(session.getAttribute("userName"));
		
		this.sessionRegistry.removeSessionRegistry(userName);
		//删除所有登录状态信息
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
	
	/**
	 * 微信绑定
	 * @param request
	 * @param session
	 * @param targetUrl
	 * @param map
	 * @return
	 */
	private String weixinBind(HttpServletRequest request, HttpSession session,String targetUrl, Map<String, Object> map) {
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
				mv.addObject("url", RedPigCommUtil.getURL(request) + "/login");
			}
			if (login_role.equalsIgnoreCase("user")) {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("url", RedPigCommUtil.getURL(request) + "/login");
			}
		}
		mv.addObject("op_title", "登录失败");
		return mv;
	}
	
	/**
	 * 异常页面
	 * @param request
	 * @param response
	 * @return
	 */
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
	
	/**
	 * 没有权限
	 * @param request
	 * @param response
	 * @return
	 */
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
	
	/**
	 * 登录声音文件
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/voice" })
	public ModelAndView voice(HttpServletRequest request,
			HttpServletResponse response) {
		return new RedPigJModelAndView("include/flash/soundPlayer.swf",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), request, response);
	}
	
	/**
	 * 获取验证码
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping({ "/getCode" })
	public void getCode(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		HttpSession session = request.getSession(false);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print("result=true&code=" + (String) session.getAttribute("verify_code"));
	}
	
	/**
	 * 上传文件
	 * @param request
	 * @param response
	 * @throws ClassNotFoundException
	 */
	@RequestMapping({ "/upload" })
	public void upload(HttpServletRequest request, HttpServletResponse response)
			throws ClassNotFoundException {
		//要被保存的文件路径名
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ this.configService.getSysConfig().getUploadFilePath()
				+ File.separator + "common";
		//获取服务器地址
		String webPath = request.getContextPath().equals("/") ? "" : request
				.getContextPath();
		
		if ((this.configService.getSysConfig().getAddress() != null)
				&& (!this.configService.getSysConfig().getAddress().equals(""))) {
			webPath = this.configService.getSysConfig().getAddress() + webPath;
		}
		JSONObject obj = new JSONObject();
		try {
			//上传文件到服务器
			Map<String, Object> map = RedPigCommUtil.saveFileToServer(request,"imgFile", saveFilePathName, null, null);
			String url = webPath + "/"
					+ this.configService.getSysConfig().getUploadFilePath()
					+ "/common/" + map.get("fileName");
			obj.put("error", 0);
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
	
	/**
	 * js访问方式
	 * @param request
	 * @param response
	 * @param js
	 * @return
	 */
	@RequestMapping({ "/js" })
	public ModelAndView js(HttpServletRequest request,
			HttpServletResponse response, String js) {
		ModelAndView mv = new RedPigJModelAndView("resources/js/" + js + ".js",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 2, request, response);
		return mv;
	}
}
