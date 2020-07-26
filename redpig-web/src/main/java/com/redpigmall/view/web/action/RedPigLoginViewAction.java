package com.redpigmall.view.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.qrcode.QRCodeUtil;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.Document;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.QRLogin;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VerifyCode;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigLoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: 用户登录、注册管理控制器，用来管理用户登录、注册、密码找回
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
 * @date 2014-5-13
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("unchecked")
@Controller
public class RedPigLoginViewAction extends BaseAction{
	
	private static final String REGEX1 = "(.*管理员.*)";
	private static final String REGEX2 = "(.*admin.*)";
	
	/**
	 * 用户登录页面
	 * @param request
	 * @param response
	 * @param url
	 * @return
	 */
	@RequestMapping({ "/user/login" })
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response, String url) {
		SysConfig sysConfig = this.configService.getSysConfig();
		ModelAndView mv = new RedPigJModelAndView("login.html", sysConfig,
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		request.getSession(false).removeAttribute("verify_mobile_code");
		
		String verify_mobile_code = CommUtil.randomString(10);
		request.getSession(false).setAttribute("verify_mobile_code",verify_mobile_code);
		boolean domain_error = CommUtil.null2Boolean(request.getSession(false).getAttribute("domain_error"));
		if ((url != null) && (!url.equals(""))) {
			request.getSession(false).setAttribute("refererUrl", url);
		}
		if (domain_error) {
			mv.addObject("op_title", "域名绑定错误，请与http://www.redpigmall.net联系");
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		} else {
			mv.addObject("imageViewTools", this.imageViewTools);
		}
		mv.addObject("verify_mobile_code", verify_mobile_code);
		if (this.configService.getSysConfig().getQr_login() == 1) {
			mv.addObject("uuid",
					UUID.randomUUID().toString().replaceAll("-", ""));
		}
		mv.addObject("referer", request.getHeader("Referer"));
		return mv;
	}
	
	/**
	 * 用户注册
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/register" })
	public ModelAndView register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("register.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String user_agent = request.getHeader("User-Agent").toLowerCase();
		if ((user_agent != null) && (user_agent.indexOf("mobile") >= 0)) {
			mv = new RedPigJModelAndView("weixin/register.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
		}
		request.getSession(false).removeAttribute("verify_code");
		request.getSession(false).removeAttribute("verify_mobile_code");

		String verify_mobile_code = CommUtil.randomString(10);
		request.getSession(false).setAttribute("verify_mobile_code",verify_mobile_code);
		Document doc = this.documentService.getObjByProperty("mark","=", "reg_agree");
		mv.addObject("doc", doc);
		mv.addObject("verify_mobile_code", verify_mobile_code);
		return mv;
	}
	
	/**
	 * 企业用户注册，打开企业注册入口页面
	 * @param request
	 * @param response
	 * @return
	 */
	
	@RequestMapping({ "/company/register" })
	public ModelAndView company_register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("company_register.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_code");
		Document doc = this.documentService.getObjByProperty("mark","=", "reg_agree");
		mv.addObject("doc", doc);
		Map<String,Object> maps = Maps.newHashMap();
		maps.put("parent", -1);
		List<Area> areas = this.areaService.queryPageList(maps);
		mv.addObject("areas", areas);
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param id
	 */
	@RequestMapping({ "/send_again" })
	public void send_again(HttpServletRequest request,
			HttpServletResponse response, String id) {
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
		boolean isSend = false;
		if ((user != null) && (!user.getValidateCode().equals(""))) {
			StringBuffer sb = new StringBuffer(
					"点击下面链接激活账号，48小时生效，否则重新注册账号，链接只能使用一次，请尽快激活！</br>");
			sb.append("<a href=" + CommUtil.getURL(request) + "/validate?e=");
			sb.append(user.getEmail());
			sb.append("&v=");
			sb.append(user.getValidateCode());
			sb.append("\">" + CommUtil.getURL(request) + "/validate?e=");
			sb.append(user.getEmail());
			sb.append("&v=");
			sb.append(user.getValidateCode());
			sb.append("</a>");
			if (this.configService.getSysConfig().getEmailEnable()) {
				isSend = this.msgTools.sendEmail(user.getEmail(), "感谢您注册"
						+ this.configService.getSysConfig().getTitle(),
						sb.toString());
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(isSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册完成
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param code
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping({ "/register_finish" })
	public String register_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String code) throws HttpException, IOException,
			InterruptedException {
		try {
			boolean reg = true;
			if ((code != null) && (!code.equals(""))) {
				code = CommUtil.filterHTML(code);
			}
			
			if (this.configService.getSysConfig().getSecurityCodeRegister()) {
				if (!request.getSession(false).getAttribute("verify_code").equals(code)) {
					reg = false;
				}
			}
			if ((CommUtil.null2String(userName).matches(REGEX1))
					||
					(CommUtil.null2String(userName).toLowerCase().matches(REGEX2))) {
				reg = false;
			}
			if (reg) {
				register_sava_user(userName, password, "", "", "", request);
//				request.getSession(false).removeAttribute("verify_code");
				return "redirect:redpig_web_login?username="
						+ CommUtil.encode(userName) + "&password=" + password
						+ "&encode=true"+"&code="+code;
			}
			return "redirect:register";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:index";
	}
	
	/**
	 * 保存注册用户
	 * @param userName
	 * @param password
	 * @param email
	 * @param mobile
	 * @param url
	 * @param request
	 * @return
	 */
	public boolean register_sava_user(String userName, String password,
			String email, String mobile, String url, HttpServletRequest request) {
		User user = new User();
		User user1 = null;
		if (!CommUtil.null2String(email).equals("")) {
			user1 = this.userService.getObjByProperty("email","=", email);
		}
		if ((mobile != null) && (!mobile.equals(""))) {
			user.setUserName(mobile + "@phone");
			String nickName = user.getUserName();
			String part1 = nickName.split("@")[0];
			String re = part1.substring(3, 9);
			nickName = nickName.replace(re, "******");
			user.setNickName(nickName);
			user.setMobile(mobile);
		}
		if ((!CommUtil.null2String(email).equals(""))
				&& (this.configService.getSysConfig().getEmailEnable())) {
			if (user1 != null) {
				user = user1;
			}
			user.setUserName(email + "@email");
			String nickName = user.getUserName();
			String part1 = nickName.split("@email")[0];
			String re = part1.substring(3, part1.length());
			nickName = nickName.replace(re, "******");
			user.setNickName(nickName);
			user.setEmail(email);
			String validateCode = Md5Encrypt.md5(email + new Date());
			user.setValidateCode(validateCode);
			user.setStatus(1);
			StringBuffer sb = new StringBuffer(
					"点击下面链接激活账号，48小时生效，否则重新注册账号，链接只能使用一次，请尽快激活！</br>");
			sb.append("<a href=\"");
			sb.append(url);
			sb.append("/validate?e=");
			sb.append(email);
			sb.append("&v=");
			sb.append(user.getValidateCode());
			sb.append("\">");
			sb.append(CommUtil.getURL(request) + "/validate?e=");
			sb.append(email);
			sb.append("&v=");
			sb.append(user.getValidateCode());
			sb.append("</a>");
			if (this.configService.getSysConfig().getEmailEnable()) {
				boolean r = this.msgTools.sendEmail(email, "感谢您注册"
						+ this.configService.getSysConfig().getTitle(),
						sb.toString());
				if (!r) {
					return false;
				}
			}
		}
		if ((userName != null) && (!userName.equals(""))) {
			user.setUserName(userName);
		}
		user.setUserRole("BUYER");
		user.setAddTime(new Date());
		user.setAvailableBalance(BigDecimal.valueOf(0L));
		user.setFreezeBlance(BigDecimal.valueOf(0L));
		user.setPassword(Md5Encrypt.md5(password).toLowerCase());
		Map<String, Object> params = Maps.newHashMap();
		params.put("type", "BUYER");
		
		List<Role> roles = this.roleService.queryPageList(params);
		
		user.getRoles().addAll(roles);
		
		
		if (this.configService.getSysConfig().getIntegral()) {
			user.setIntegral(this.configService.getSysConfig().getMemberRegister());
			if (user1 == null) {
				this.userService.saveEntity(user);
				this.userService.saveUserRole(user.getId(), user.getRoles());
			} else {
				this.userService.updateById(user);
			}
			IntegralLog log = new IntegralLog();
			log.setAddTime(new Date());
			log.setContent("用户注册增加"
					+ this.configService.getSysConfig().getMemberRegister()
					+ "分");
			log.setIntegral(this.configService.getSysConfig()
					.getMemberRegister());
			log.setIntegral_user(user);
			log.setType("reg");
			this.integralLogService.saveEntity(log);
		} else if (user1 == null) {
			this.userService.saveEntity(user);
			this.userService.saveUserRole(user.getId(), roles);
		} else {
			this.userService.updateById(user);
		}
		Album album = new Album();
		album.setAddTime(new Date());
		album.setAlbum_default(true);
		album.setAlbum_name("默认相册");
		album.setAlbum_sequence(55536);
		album.setUser(user);
		this.albumService.saveEntity(album);
		return true;
	}
	
	/**
	 * 手机注册完成
	 * @param request
	 * @param response
	 * @param mobile
	 * @param phone_password
	 * @param phone_code
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping({ "/phone_register_finish" })
	public String phone_register_finish(HttpServletRequest request,
			HttpServletResponse response, String mobile, String phone_password,
			String phone_code) throws HttpException, IOException,
			InterruptedException {
		try {
			boolean reg = true;
			if ((phone_code != null) && (!phone_code.equals(""))) {
				phone_code = CommUtil.filterHTML(phone_code);
			}
			if (this.configService.getSysConfig().getSecurityCodeRegister()) {
				if (!request.getSession(false).getAttribute("verify_code").equals(phone_code)) {
					reg = false;
				}
			}
			if (reg) {
				register_sava_user("", phone_password, "", mobile, "", request);
				request.getSession(false).removeAttribute("verify_code");
				System.out.println("redirect:redpigmall_login");
				return "redirect:redpigmall_login?username=" + mobile + "&password=" + phone_password + "&encode=true";
			}
			return "redirect:register";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:index";
	}
	
	/**
	 * 邮箱注册完成
	 * @param request
	 * @param response
	 * @param email_password
	 * @param email
	 * @param email_code
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping({ "/email_register_finish" })
	public String email_register_finish(HttpServletRequest request,
			HttpServletResponse response, String email_password, String email,
			String email_code) throws HttpException, IOException,
			InterruptedException {
		try {
			boolean reg = true;
			if ((email_code != null) && (!email_code.equals(""))) {
				email_code = CommUtil.filterHTML(email_code);
			}
			
			if (this.configService.getSysConfig().getSecurityCodeRegister()) {
				if (!request.getSession(false).getAttribute("verify_code")
						.equals(email_code)) {
					reg = false;
				}
			}
			if (reg) {
				String url = CommUtil.getURL(request);
				boolean r = register_sava_user("", email_password, email, "",url, request);
				if (!r) {
					return "redirect:register_result?type=send_false";
				}
				request.getSession(false).removeAttribute("verify_code");
				return "redirect:register_last?email=" + email;
			}
			return "redirect:register";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:index";
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
	 * 
	 * @param request
	 * @param response
	 * @param email
	 * @return
	 */
	@RequestMapping({ "/register_last" })
	public ModelAndView register_last(HttpServletRequest request,
			HttpServletResponse response, String email) {
		ModelAndView mv = new RedPigJModelAndView("register_last.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.getObjByProperty("email","=",CommUtil.null2String(email));
		mv.addObject("obj", user);
		return mv;
	}
	
	/**
	 * 登出成功
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/out_login_success" })
	public ModelAndView out_login_success(HttpServletRequest request,
			HttpServletResponse response) {
		String bind = CommUtil.null2String(request.getSession(false)
				.getAttribute("bind"));
		ModelAndView mv = new RedPigJModelAndView(bind + "_login_bind.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("user", SecurityUserHolder.getCurrentUser());
		request.getSession(false).removeAttribute("bind");
		return mv;
	}
	
	/**
	 * 弹窗登录页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/user_dialog_login" })
	public ModelAndView user_dialog_login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("user_dialog_login.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (this.configService.getSysConfig().getQr_login() == 1) {
			mv.addObject("uuid",
					UUID.randomUUID().toString().replaceAll("-", ""));
		}
		return mv;
	}
	
	/**
	 * 找回密码第一步
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/forget1" })
	public ModelAndView forget1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("forget1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		
		if ((!config.getEmailEnable()) && (!config.getSmsEnbale())) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 找回密码
	 * @param request
	 * @param response
	 * @param accept_type
	 * @param email
	 * @param mobile
	 * @param userName
	 * @param verify_code
	 * @return
	 */
	@RequestMapping({ "/forget3" })
	public ModelAndView forget3(HttpServletRequest request,
			HttpServletResponse response, String accept_type, String email,
			String mobile, String userName, String verify_code) {
		ModelAndView mv = new RedPigJModelAndView("forget3.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (accept_type.equals("email")) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty("email","=",  email);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget2?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64)
							.toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(false).setAttribute("verify_session",
							verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.deleteById(vc.getId());
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/forget2?userName=" + userName);
			}
		}
		if (accept_type.equals("mobile")) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty("mobile","=",  mobile);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "验证码输入错误");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget2?userName=" + userName);
				} else {
					String verify_session = CommUtil.randomString(64)
							.toLowerCase();
					mv.addObject("verify_session", verify_session);
					request.getSession(false).setAttribute("verify_session",
							verify_session);
					mv.addObject("userName", userName);
					this.verifyCodeService.deleteById(vc.getId());
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "验证码输入错误");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/forget2?userName=" + userName);
			}
		}
		return mv;
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param verify_session
	 * @return
	 */
	@RequestMapping({ "/forget4" })
	public ModelAndView forget4(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String verify_session) {
		ModelAndView mv = new RedPigJModelAndView("forget4.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String verify_session1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("verify_session"));
		if ((!verify_session1.equals(""))
				&& (verify_session1.equals(verify_session))) {
			User user = this.userService.getObjByProperty("userName","=",userName);
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			this.userService.updateById(user);
			request.getSession(false).removeAttribute("verify_session");
			mv.addObject("op_title", "密码修改成功，请使用新密码登录");
			mv.addObject("url", CommUtil.getURL(request) + "/user/login");
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "会话过期，找回密码失败");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1");
		}
		return mv;
	}

	@RequestMapping({ "/reg_code_get" })
	public void reg_code_get(HttpServletRequest request,
			HttpServletResponse response, String accept_type, String email,
			String mobile, String userName, String SessionCode)
			throws UnsupportedEncodingException {
		int ret = 0;
		String SessionCode1 = CommUtil.null2String(request.getSession(false)
				.getAttribute("SessionCode"));
		if ((SessionCode1 != null) && (SessionCode1.equals(SessionCode))) {
			request.getSession(false).removeAttribute("SessionCode");
			if (accept_type.equals("email")) {
				if (this.configService.getSysConfig().getEmailEnable()) {
					String subject = this.configService.getSysConfig()
							.getWebsiteName() + " 邮件验证码";
					String code = CommUtil.randomString(4).toUpperCase();
					VerifyCode vc = this.verifyCodeService.getObjByProperty("email","=",  email);
					if (vc == null) {
						vc = new VerifyCode();
						vc.setAddTime(new Date());
						vc.setCode(code);
						vc.setEmail(email);
						vc.setUserName(userName);
						this.verifyCodeService.saveEntity(vc);
					} else {
						vc.setAddTime(new Date());
						vc.setCode(code);
						vc.setEmail(email);
						vc.setUserName(userName);
						this.verifyCodeService.updateById(vc);
					}
					String content = "您的邮件验证码为:" + code + ",验证码有效时间为30分钟！";
					boolean ret1 = this.msgTools.sendEmail(email, subject,
							content);
					if (ret1) {
						ret = 1;
					}
				} else {
					ret = -1;
				}
			}
			if (accept_type.equals("mobile")) {
				if (this.configService.getSysConfig().getSmsEnbale()) {
					String code = CommUtil.randomInt(4);
					VerifyCode vc = this.verifyCodeService.getObjByProperty("mobile","=",  mobile);
					if (vc == null) {
						vc = new VerifyCode();
						vc.setAddTime(new Date());
						vc.setCode(code);
						vc.setMobile(mobile);
						vc.setUserName(userName);
						this.verifyCodeService.saveEntity(vc);
					} else {
						vc.setAddTime(new Date());
						vc.setCode(code);
						vc.setMobile(mobile);
						vc.setUserName(userName);
						this.verifyCodeService.updateById(vc);
					}
					String content = "您的短信验证码为:"
							+ code
							+ ",验证码有效时间为30分钟！【"
							+ this.configService.getSysConfig()
									.getWebsiteName() + "】";
					boolean ret1 = this.msgTools.sendSMS(mobile, content);
					if (ret1) {
						ret = 1;
					}
				} else {
					ret = -2;
				}
			}
		} else {
			ret = 0;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 找回密码
	 * @param request
	 * @param response
	 * @param userName
	 * @param email
	 * @param code
	 * @return
	 */
	@RequestMapping({ "/find_pws" })
	public ModelAndView find_pws(HttpServletRequest request,
			HttpServletResponse response, String userName, String email,
			String code) {
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		HttpSession session = request.getSession(false);
		String verify_code = (String) session.getAttribute("verify_code");
		if (code.toUpperCase().equals(verify_code)) {
			User user = this.userService.getObjByProperty("userName","=",userName);
			if (user.getEmail().equals(email.trim())) {
				String pws = CommUtil.randomString(6).toLowerCase();
				String subject = this.configService.getSysConfig().getTitle()
						+ "密码找回邮件";
				String content = user.getUsername() + ",您好！您通过密码找回功能重置密码，新密码为："
						+ pws;
				boolean ret = this.msgTools.sendEmail(email, subject, content);
				if (ret) {
					user.setPassword(Md5Encrypt.md5(pws));
					this.userService.updateById(user);
					mv.addObject("op_title", "新密码已经发送到邮箱:<font color=red>"
							+ email + "</font>，请查收后重新登录");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/user/login");
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "邮件发送失败，密码暂未执行重置");
					mv.addObject("url", CommUtil.getURL(request) + "/forget1");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "用户名、邮箱不匹配");
				mv.addObject("url", CommUtil.getURL(request) + "/forget1");
			}
		} else {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "验证码不正确");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1");
		}
		return mv;
	}
	
	/**
	 * 二维码登陆
	 * @param request
	 * @param response
	 * @param uuid
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "/qr_login_img" })
	public void qr_login_img(HttpServletRequest request,
			HttpServletResponse response, String uuid)
			throws UnsupportedEncodingException {
		generic_qr(request, response, uuid);
	}
	
	/**
	 * 二维码登陆
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "/qr_login" })
	public void qr_login(HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException {
		String qr_session_id = CommUtil.null2String(request.getSession(false)
				.getAttribute("qr_session_id"));
		String[] session_ids = qr_session_id.split(",");
		Map<String, Object> params = Maps.newHashMap();
		params.put("qr_session_id", session_ids.length == 2 ? session_ids[0]
				: "");
		
		List<QRLogin> qrlist = this.qRLoginService.queryPageList(params);
		
		Map<String, Object> map = Maps.newHashMap();
		if (qrlist.size() > 0) {
			QRLogin qrlogin = (QRLogin) qrlist.get(0);
			map.put("ret", "true");
			map.put("user_id", qrlogin.getUser_id());
			this.qRLoginService.deleteById(qrlogin.getId());
			String qr_log_mark = CommUtil.randomString(16);
			map.put("qr_log_mark", qr_log_mark);
			HttpSession session = request.getSession(false);
			session.setAttribute("qr_log_mark", qr_log_mark);
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 找回密码
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@RequestMapping({ "/forget2" })
	public ModelAndView forget2(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		ModelAndView mv = new RedPigJModelAndView("forget2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		String ran = CommUtil.randomInt(5) + CommUtil.randomString(5);
		request.getSession(false).setAttribute("SessionCode", ran);
		mv.addObject("SessionCode", ran);
		
		if ((!config.getEmailEnable()) && (!config.getSmsEnbale())) {
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("userName_email_mobile", userName);
			
			List<User> users = this.userService.queryPageList(params);
			
			if (users.size() > 0) {
				User user = (User) users.get(0);
				if ((!CommUtil.null2String(user.getEmail()).equals(""))
						|| (!CommUtil.null2String(user.getMobile()).equals(""))) {
					mv.addObject("user", user);
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "用户没有绑定邮箱和手机，无法找回");
					mv.addObject("url", CommUtil.getURL(request) + "/forget1");
				}
			} else {
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "不存在该用户");
				mv.addObject("url", CommUtil.getURL(request) + "/forget1");
			}
		}
		return mv;
	}
	
	/**
	 * 二维码登陆确认
	 * @param request
	 * @param response
	 * @param user_id
	 * @param qr_log_mark
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "/qr_login_confirm" })
	public String qr_login_confirm(HttpServletRequest request,
			HttpServletResponse response, String user_id, String qr_log_mark)
			throws UnsupportedEncodingException {
		HttpSession session = request.getSession(false);
		String session_qr_log_mark = session.getAttribute("qr_log_mark")
				.toString();
		String url = "";
		if ((qr_log_mark != null) && (qr_log_mark.equals(session_qr_log_mark))) {
			User user = this.userService
					.selectByPrimaryKey(CommUtil.null2Long(user_id));
			request.getSession(false).removeAttribute("verify_code");
			url = "redirect:" + CommUtil.getURL(request)
					+ "/redpigmall_login?username="
					+ CommUtil.encode(user.getUsername()) + "&password="
					+ "redpigmall_thid_login_" + user.getPassword();
		}
		return url;
	}
	
	/**
	 * 二维码弹出登陆
	 * @param request
	 * @param response
	 * @param user_id
	 * @param qr_log_mark
	 * @param code
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "/dialog_qr_login_confirm" })
	public String dialog_qr_login_confirm(HttpServletRequest request,
			HttpServletResponse response, String user_id, String qr_log_mark,
			String code) throws UnsupportedEncodingException {
		HttpSession session = request.getSession(false);
		String session_qr_log_mark = session.getAttribute("qr_log_mark")
				.toString();
		String url = "";
		if ((qr_log_mark != null) && (qr_log_mark.equals(session_qr_log_mark))) {
			User user = this.userService
					.selectByPrimaryKey(CommUtil.null2Long(user_id));
			url = "redirect:" + CommUtil.getURL(request)
					+ "/redpigmall_login?username="
					+ CommUtil.encode(user.getUsername()) + "&password="
					+ "redpigmall_thid_login_" + user.getPassword() + "&code="
					+ code + "&ajax_login=true";
			request.getSession(false).removeAttribute("verify_code");
		}
		return url;
	}

	
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/send_mcode" })
	public void send_mcode(HttpServletRequest request,
			HttpServletResponse response, String mobile, String type,
			String verify_mobile_code) throws UnsupportedEncodingException {
		Map json_map = Maps.newHashMap();
		Map<String, Object> params = Maps.newHashMap();
		String msg = "";
		String code = CommUtil.randomInt(6);
		int ret = 100;

		String verify_mobile_code_temp = CommUtil.null2String(request
				.getSession(false).getAttribute("verify_mobile_code"));
		if (verify_mobile_code_temp.equals(verify_mobile_code)) {
			request.getSession(false).removeAttribute("verify_mobile_code");
			String content = "您正在使用手机登录，动态登录密码为：" + code + "。["
					+ this.configService.getSysConfig().getTitle() + "]";
			if (CommUtil.null2String(type).equals("register")) {
				content = "请输入手机动态验证码：" + code + "完成注册。["
						+ this.configService.getSysConfig().getTitle() + "]";
			}
			if ((mobile == null) || (mobile.equals(""))) {
				ret = 200;
				msg = "短信发送失败，请检查号码！";
			}
			if ((ret == 100)
					&& (!this.configService.getSysConfig().getSmsEnbale())) {
				ret = 300;
				msg = "系统未开启短信服务，请使用账号登录！";
			}
			if ((ret == 100)
					&& (!CommUtil.null2String(type).equals("register"))) {
				params.clear();
				params.put("mobile", mobile);
				
				List<User> users = this.userService.queryPageList(params);
				
				if ((users.size() <= 0) || (users == null)) {
					ret = 500;
					msg = "该手机号码没有绑定任何商城账号！";
				}
			}
			if ((ret == 100)
					&& (!CommUtil.null2String(type).equals("register"))) {
				params.clear();
				params.put("mobile", mobile);
				
				List<User> users = this.userService.queryPageList(params);
				
				for (User user : users) {
					if (user.getDay_msg_count() >= 5) {
						ret = 600;
						msg = "您今天使用手机动态密码登录次数过多，请使用账号登录！";
						break;
					}
				}
			}
			if (ret == 100) {
				params.clear();
				params.put("mobile", mobile);
				params.put("orderBy", "addTime");
				params.put("orderType", "desc");
				
				List<VerifyCode> ver_codes = this.mobileverifycodeService.queryPageList(params);
				
				Calendar cal = Calendar.getInstance();
				for (VerifyCode obj : ver_codes) {
					Calendar cal2 = Calendar.getInstance();
					cal2.setTime(obj.getAddTime());
					long l = cal.getTimeInMillis() - cal2.getTimeInMillis();
					int seconds = new Long(l / 1000L).intValue();
					if (seconds < 60) {
						ret = 400;
						msg = "您短时间内发送的动态密码数量过多！";
						break;
					}
				}
			}
			if (ret == 100) {
				boolean ret1 = this.msgTools.sendSMS(mobile, content);
				if (ret1) {
					params.clear();
					params.put("mobile", mobile);
					
					List<VerifyCode> codes = this.mobileverifycodeService.queryPageList(params);
					
					for (VerifyCode cd : codes) {
						this.mobileverifycodeService.deleteById(cd.getId());
					}
					VerifyCode mvc = new VerifyCode();
					mvc.setAddTime(new Date());
					mvc.setCode(code);
					mvc.setMobile(mobile);
					this.mobileverifycodeService.updateById(mvc);
					msg = "短信发送成功，请注意查收！";
					if (!CommUtil.null2String(type).equals("register")) {
						params.clear();
						params.put("mobile", mobile);
						
						List<User> users = this.userService.queryPageList(params);
						
						User user = users.get(0);
						user.setDay_msg_count(user.getDay_msg_count() + 1);
						this.userService.updateById(user);
					}
				} else {
					ret = 200;
					msg = "短信发送失败，请检查网络！";
				}
			}
		} else {
			ret = -100;
			msg = "短信发送验证失败！";
		}
		json_map.put("ret", Integer.valueOf(ret));
		json_map.put("msg", msg);
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
	 * 手机登陆确认
	 * @param request
	 * @param response
	 * @param mobile
	 * @param dy_code
	 * @param mobile_code
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping({ "/mobile_login_confirm" })
	public void mobile_login_confirm(HttpServletRequest request,
			HttpServletResponse response, String mobile, String dy_code,
			String mobile_code) throws UnsupportedEncodingException {
		String ret = "100";
		String msg = "";
		Map<String,Object> json_map = Maps.newHashMap();
		Map<String, Object> params = Maps.newHashMap();
		params.put("mobile", mobile);
		
		params.clear();
		params.put("mobile", mobile);
		List<VerifyCode> mvcs = this.mobileverifycodeService.queryPageList(params);
		
		if (mvcs.size() > 0) {
			VerifyCode mv = (VerifyCode) mvcs.get(0);
			if (!mv.getCode().equals(dy_code)) {
				ret = "-100";
				msg = "动态密码错误！";
			}
		}
		
		if (this.configService.getSysConfig().getSecurityCodeLogin()) {
			if (!request.getSession(false).getAttribute("verify_code")
					.equals(mobile_code)) {
				ret = "-200";
				msg = "验证码错误！";
			}
		}
		json_map.put("ret", ret);
		json_map.put("msg", msg);
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
	 * 
	 * @param request
	 * @param response
	 * @param mobile
	 * @param dy_code
	 * @param mobile_code
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	
	@RequestMapping({ "/mobile_login_confirm2" })
	public String mobile_login_confirm2(HttpServletRequest request,
			HttpServletResponse response, String mobile, String dy_code,
			String mobile_code) throws UnsupportedEncodingException {
		String ret = "100";
		String url = "redirect:" + CommUtil.getURL(request)
				+ "/mobile_login_error";
		Map<String, Object> params = Maps.newHashMap();
		params.put("mobile", mobile);
		
		List<User> users = this.userService.queryPageList(params, 0, 1);
		
		if (users.size() > 0) {
			User user = (User) users.get(0);
			params.clear();
			params.put("mobile", mobile);
			
			List<VerifyCode> mvcs = this.mobileverifycodeService.queryPageList(params);
			
			if (mvcs.size() > 0) {
				VerifyCode mv = (VerifyCode) mvcs.get(0);
				if (!mv.getCode().equals(dy_code)) {
					ret = "-100";
				}
			} else {
				ret = "-100";
			}
			
			if (this.configService.getSysConfig().getSecurityCodeLogin()) {
				if (!request.getSession(false).getAttribute("verify_code")
						.equals(mobile_code)) {
					ret = "-200";
				}
			}
			if (ret == "100") {
				for (VerifyCode obj : mvcs) {
					this.verifyCodeService.deleteById(obj.getId());
				}
				url =

				"redirect:" + CommUtil.getURL(request)
						+ "/redpigmall_login?username="
						+ CommUtil.encode(user.getUsername()) + "&password="
						+ "redpigmall_thid_login_" + user.getPassword()
						+ "&code=" + mobile_code + "&ajax_login=true";
				request.getSession(false).removeAttribute("verify_code");
			}
		}
		return url;
	}
	
	/**
	 * 手机登陆出错
	 * @param request
	 * @param response
	 */
	@RequestMapping({ "/mobile_login_error" })
	public void mobile_login_redirect(HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print("error");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String generic_qr(HttpServletRequest request,
			HttpServletResponse response, String uuid) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String strDate = formatter.format(new Date());
		String rand_str = uuid + "," + strDate;
		String login_url = CommUtil.getURL(request)
				+ "/redpigmall_login?qr_session_id=" + rand_str;
		request.getSession().setAttribute("qr_session_id", rand_str);
		String logoPath = "";
		if (this.configService.getSysConfig().getQr_logo() != null) {
			logoPath =

			request.getSession().getServletContext().getRealPath("/")
					+ this.configService.getSysConfig().getQr_logo().getPath()
					+ File.separator
					+ this.configService.getSysConfig().getQr_logo().getName();
		}
		QRCodeUtil.encode(login_url, logoPath, response, true);
		return rand_str;
	}

	@RequestMapping({ "/register_login" })
	public String register_login(HttpServletRequest request,
			HttpServletResponse response, String id) {
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (user != null) {
			return

			"redirect:redpigmall_login?username="
					+ CommUtil.encode(user.getUsername()) + "&password="
					+ "redpigmall_thid_login_" + user.getPassword()
					+ "&encode=true";
		}
		return "redirect:user/login";
	}

	@RequestMapping({ "/register_result" })
	public ModelAndView register_result(HttpServletRequest request,
			HttpServletResponse response, String id, String type) {
		ModelAndView mv = new RedPigJModelAndView("register_result.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (user != null) {
			mv.addObject("id", id);
		}
		mv.addObject("type", type);
		return mv;
	}

	@RequestMapping({ "/validate" })
	public String validate(HttpServletRequest request,
			HttpServletResponse response, String e, String v) {
		User user = this.userService.getObjByProperty("email","=", e);
		if ((user != null) && (user.getStatus() == 1)
				&& (user.getValidateCode().equals(v))) {
			user.setStatus(0);
			user.setValidateCode("");
			this.userService.updateById(user);
			request.getSession(false).removeAttribute("verify_code");
			return "redirect:register_result?id=" + user.getId();
		}
		return "redirect:register_result";
	}
}
