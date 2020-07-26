package com.redpigmall.module.weixin.view.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.Document;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VerifyCode;
import com.redpigmall.module.weixin.view.action.base.BaseAction;


/**
 * 
 * <p>
 * Title: RedPigWapLoginViewAction.java
 * </p>
 * 
 * <p>
 * Description: 手机端登录请求管理类
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
 * @date 2014-7-22
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "unchecked", "rawtypes"})
@Controller
public class RedPigWeixinLoginViewAction extends BaseAction{
	private static final String REGEX1 = "(.*管理员.*)";
	private static final String REGEX2 = "(.*admin.*)";
	
	/**
	 * 手机端商城登陆
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 */
	@RequestMapping({ "/redpigmall_user_login" })
	public void mobile_login(HttpServletRequest request,
			HttpServletResponse response, String userName, String password) {
		String code = "-300";
		Map json_map = Maps.newHashMap();
		boolean verify = CommUtil.null2Boolean(request.getHeader("verify"));
		if (verify) {
			String user_id = "";
			String user_name = "";
			String login_token = "";
			if ((userName != null) && (!userName.equals(""))
					&& (password != null) && (!password.equals(""))) {
				password = Md5Encrypt.md5(password).toLowerCase();
				Map<String, Object> map = Maps.newHashMap();
				map.put("userName", userName);
				
				List<User> users = this.userService.queryPageList(map);
				
				if (users.size() > 0) {
					for (User u : users) {
						if (!u.getPassword().equals(password)) {
							code = "-200";
						} else if ((u.getUserRole().equals("admin"))
								|| (u.getUserRole().equals("ADMIN"))) {
							code = "-100";
						} else {
							user_id = CommUtil.null2String(u.getId());
							user_name = u.getUserName();
							code = "100";
							login_token = CommUtil.randomString(12) + user_id;
							u.setApp_login_token(login_token.toLowerCase());
							this.userService.updateById(u);
						}
					}
				} else {
					code = "-100";
				}
			}
			if (code.equals("100")) {
				json_map.put("user_id", user_id.toString());
				json_map.put("userName", user_name);
				json_map.put("token", login_token);
			}
		}
		json_map.put("code", code);
		String json = JSON.toJSONString(json_map);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 登陆跳转
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/login" })
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/login.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("referer", request.getHeader("Referer"));
		return mv;
	}
	
	/**
	 * 手机端注册跳转
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/register" })
	public ModelAndView register(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/register.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		
		String directSellingParent_id = request.getParameter("directSellingParent_id");
		
		if(StringUtils.isNotBlank(directSellingParent_id)) {
			mv.addObject("directSellingParent_id",directSellingParent_id);
		}
		
		return mv;
	}
	
	/**
	 * 手机端手机注册
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/register_mobile" })
	public ModelAndView register_mobile(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/register_mobile.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		request.getSession(false).removeAttribute("verify_mobile_code");

		String verify_mobile_code = CommUtil.randomString(10);
		request.getSession(false).setAttribute("verify_mobile_code",
				verify_mobile_code);
		mv.addObject("verify_mobile_code", verify_mobile_code);
		return mv;
	}
	
	/**
	 * 注册完成
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param email
	 * @param code
	 * @param user_type
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping({ "/register_finish" })
	public String register_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String email, String code, String user_type,String directSellingParent_id) throws HttpException,
			IOException, InterruptedException {
		try {
			boolean reg = true;
			if ((code != null) && (!code.equals(""))) {
				code = CommUtil.filterHTML(code);
			}
			if (this.configService.getSysConfig().getSecurityCodeRegister()) {
				if (!request.getSession(false).getAttribute("verify_code")
						.equals(code)) {
					reg = false;
				}
			}
			if ((userName.matches(REGEX1))
					|| (userName.toLowerCase().matches(REGEX2))) {
				reg = false;
			}
			if (reg) {
				User user = new User();
				user.setUserName(userName);
				user.setUserRole("BUYER");
				user.setAddTime(new Date());
				user.setEmail(email);
				user.setAvailableBalance(BigDecimal.valueOf(0L));
				user.setFreezeBlance(BigDecimal.valueOf(0L));
				if(directSellingParent_id!=null) {
					User directSellingParent = this.userService.selectByPrimaryKey(CommUtil.null2Long(directSellingParent_id));
					user.setDirectSellingParent(directSellingParent);
				}
				if ((user_type != null) && (!user_type.equals(""))) {
					user.setUser_type(CommUtil.null2Int(user_type));
					user.setContact_user(request.getParameter("contact_user"));
					user.setDepartment(request.getParameter("department"));
					user.setTelephone(request.getParameter("telephone"));
					user.setMobile(request.getParameter("mobile"));
					user.setCompany_name(request.getParameter("company_name"));
					Area area = this.areaService.selectByPrimaryKey(CommUtil
							.null2Long(request.getParameter("area_id")));
					user.setCompany_address(area.getParent().getParent()
							.getAreaName()
							+ area.getParent().getAreaName()
							+ area.getAreaName()
							+ " "
							+ request.getParameter("company_address"));
					if (request.getParameter("company_purpose") != null) {
						if (!request.getParameter("company_purpose").equals("")) {
							user.setCompany_purpose(request.getParameter(
									"company_purpose").substring(
									0,
									request.getParameter("company_purpose")
											.length() - 1));
						}
					}
					user.setCompany_url(request.getParameter("company_url"));
					user.setCompany_person_num(request
							.getParameter("company_person_num"));
					user.setCompany_trade(request.getParameter("company_trade"));
					user.setCompany_nature(request
							.getParameter("company_nature"));
				}
				user.setPassword(Md5Encrypt.md5(password).toLowerCase());
				Map<String, Object> params = Maps.newHashMap();
				params.put("type", "BUYER");
				
				List<Role> roles = this.roleService.queryPageList(params);
				
				this.userService.saveUserRole(user.getId(), roles);
				
				if (this.configService.getSysConfig().getIntegral()) {
					user.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					try {
						this.userService.saveEntity(user);
					} catch (Exception e) {
						e.printStackTrace();
					}
					IntegralLog log = new IntegralLog();
					log.setAddTime(new Date());
					log.setContent("用户注册增加"
							+ this.configService.getSysConfig()
									.getMemberRegister() + "分");
					log.setIntegral(this.configService.getSysConfig()
							.getMemberRegister());
					log.setIntegral_user(user);
					log.setType("reg");
					this.integralLogService.saveEntity(log);
				} else {
					this.userService.saveEntity(user);
				}
				Album album = new Album();
				album.setAddTime(new Date());
				album.setAlbum_default(true);
				album.setAlbum_name("默认相册");
				album.setAlbum_sequence(55536);
				album.setUser(user);
				this.albumService.saveEntity(album);
				
				return "redirect:redpigmall_login?username="
						+ CommUtil.encode(userName) + "&password=" + password
						+ "&encode=true&code="+code;
			}
			return "redirect:register";
		} catch (Exception e) {
		}
		return "redirect:index";
	}
	
	/**
	 * 通过手机号注册完成
	 * @param request
	 * @param response
	 * @param mobile
	 * @param phone_password
	 * @param code
	 * @return
	 * @throws HttpException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@RequestMapping({ "/phone_register_finish" })
	public String phone_register_finish(HttpServletRequest request,
			HttpServletResponse response, String mobile, String phone_password,
			String code) throws HttpException, IOException,
			InterruptedException {
		try {
			boolean reg = true;
			if ((code != null) && (!code.equals(""))) {
				code = CommUtil.filterHTML(code);
			}
			if (this.configService.getSysConfig().getSecurityCodeRegister()) {
				if (!request.getSession(false).getAttribute("verify_code")
						.equals(code)) {
					reg = false;
				}
			}
			if (reg) {
				register_sava_user("", phone_password, "", mobile, "");
				request.getSession(false).removeAttribute("verify_code");
				return "redirect:redpigmall_login?username=" + mobile
						+ "&password=" + phone_password + "&encode=true";
			}
			return "redirect:register_mobile";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:index";
	}

	public boolean register_sava_user(String userName, String password,
			String email, String mobile, String url) {
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
			sb.append("http://localhost/validate?e=");
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
		
		this.userService.saveUserRole(user.getId(), roles);
		
		if (this.configService.getSysConfig().getIntegral()) {
			user.setIntegral(this.configService.getSysConfig()
					.getMemberRegister());
			if (user1 == null) {
				this.userService.saveEntity(user);
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
	 * 注册文章
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/register_doc" })
	public ModelAndView mobile_register_doc(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/register_doc.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		Document doc = this.documentService.getObjByProperty("mark","=","reg_agree");
		mv.addObject("doc", doc);
		return mv;
	}
	
	/**
	 * 忘记密码
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/forget1" })
	public ModelAndView forget1(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("weixin/forget1.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if ((!config.getEmailEnable()) && (!config.getSmsEnbale())) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		}
		return mv;
	}
	
	/**
	 * 忘记密码
	 * @param request
	 * @param response
	 * @param userName
	 * @return
	 */
	@RequestMapping({ "/forget2" })
	public ModelAndView forget2(HttpServletRequest request,
			HttpServletResponse response, String userName) {
		ModelAndView mv = new RedPigJModelAndView("weixin/forget2.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		SysConfig config = this.configService.getSysConfig();
		if ((!config.getEmailEnable()) && (!config.getSmsEnbale())) {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "系统关闭邮件及手机短信功能，不能找回密码");
			mv.addObject("url", CommUtil.getURL(request) + "/index");
		} else {
			Map<String, Object> params = Maps.newHashMap();
			params.put("userName", userName);
			params.put("email", userName);
			params.put("mobile", userName);
			
			List<User> users = this.userService.queryPageList(params);
			
			if (users.size() > 0) {
				User user = (User) users.get(0);
				if ((!CommUtil.null2String(user.getEmail()).equals(""))
						|| (!CommUtil.null2String(user.getMobile()).equals(""))) {
					mv.addObject("user", user);
				} else {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "用户没有绑定邮箱和手机，无法找回");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/forget1");
				}
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
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
	 * 忘记密码
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
		ModelAndView mv = new RedPigJModelAndView("weixin/forget3.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		if (accept_type.equals("email")) {
			VerifyCode vc = this.verifyCodeService.getObjByProperty("email","=",  email);
			if (vc != null) {
				if (!vc.getCode().equals(verify_code)) {
					mv = new RedPigJModelAndView("weixin/error.html",
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
				mv = new RedPigJModelAndView("weixin/error.html",
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
					mv = new RedPigJModelAndView("weixin/error.html",
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
				mv = new RedPigJModelAndView("weixin/error.html",
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
	 * 忘记
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
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
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
			mv.addObject("url", CommUtil.getURL(request) + "/login");
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "会话过期，找回密码失败");
			mv.addObject("url", CommUtil.getURL(request) + "/forget1");
		}
		return mv;
	}
}
