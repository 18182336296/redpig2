package com.redpigmall.plug.login.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigAlbumService;
import com.redpigmall.service.RedPigIntegralLogService;
import com.redpigmall.service.RedPigRoleService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigUserConfigService;
import com.redpigmall.service.RedPigUserService;

/**
 * 
 * <p>
 * Title: RedPigQQLoginPlug.java
 * </p>
 * 
 * <p>
 * Description: QQ登录插件，该插件完成QQ登录授权，登录后自动完成用户注册并自动登录，默认注册用户密码为123456
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
 * @date 2014-5-16
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Controller
public class QQLoginPlug {
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigUserConfigService userConfigService;
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigRoleService roleService;
	@Autowired
	private RedPigAlbumService albumService;
	@Autowired
	private RedPigIntegralLogService integralLogService;
//	private String qq_login_url = "https://graph.qq.com/oauth2.0/authorize";
//	private String qq_access_token = "https://graph.qq.com/oauth2.0/authorize";
	
	/**
	 * 导向QQ登录授权页面
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping({ "/qq_login_api" })
	public void qq_login_api(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String redirect_uri = CommUtil.encode(CommUtil.getURL(request)
				+ "/qq_login_bind");
		String auth_url = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id="
				+ this.configService.getSysConfig().getQq_login_id()
				+ "&redirect_uri="
				+ redirect_uri
				+ "&state=redpigmall&scope=get_user_info";
		response.sendRedirect(auth_url);
	}
	/**
	 * 完成QQ授权回到商城页面，读取QQ用户名信息，自动注册一个账号并完成登录，用户可以选择修改密码或者绑定已经有的账号
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping({ "/qq_login_bind" })
	public String qq_login_bind(HttpServletRequest request,
			HttpServletResponse response, String code) {
		String redirect_uri = CommUtil.encode(CommUtil.getURL(request)
				+ "/qq_login_bind");
		String token_url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="
				+ this.configService.getSysConfig().getQq_login_id()
				+ "&client_secret="
				+ this.configService.getSysConfig().getQq_login_key()
				+ "&code=" + code + "&redirect_uri=" + redirect_uri;

		String[] access_token_callback = CommUtil.null2String(
				getHttpContent(token_url, "UTF-8", "GET")).split("&");
		String access_token = access_token_callback[0].split("=")[1];
		String me_url = "https://graph.qq.com/oauth2.0/me?access_token="
				+ access_token;

		String me_callback = CommUtil
				.null2String(getHttpContent(me_url, "UTF-8", "GET"))
				.replaceAll("callback\\(", "").replaceAll("\\);", "");
		Map me_map = JSON.parseObject(me_callback);
		String qq_openid = CommUtil.null2String(me_map.get("openid"));
		String user_info_url = "https://graph.qq.com/user/get_user_info?access_token="
				+ access_token
				+ "&oauth_consumer_key="
				+ me_map.get("client_id") + "&openid=" + qq_openid;
		String user_info_callback = getHttpContent(user_info_url, "UTF-8",
				"GET");
		Map user_map = JSON.parseObject(user_info_callback);
		System.out.println("用户名：" + user_map.get("nickname"));
		if (SecurityUserHolder.getCurrentUser() == null) {
			User user = this.userService.getObjByProperty( "qq_openid","=", qq_openid);
			if (user == null) {
				request.getSession(false).removeAttribute("verify_code");
				request.getSession(false).setAttribute("qq_openid", qq_openid);
				return "redirect:" + CommUtil.getURL(request)
						+ "/qq_add_account";
			}
			request.getSession(false).removeAttribute("verify_code");
			return "redirect:" + CommUtil.getURL(request)
					+ "/redpigmall_login?username="
					+ CommUtil.encode(user.getUsername()) + "&password="
					+ "redpigmall_thid_login_" + user.getPassword();
		}
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		user.setQq_openid(qq_openid);
		this.userService.updateById(user);
		return "redirect:" + CommUtil.getURL(request) + "/buyer/account_bind";
	}

	@RequestMapping({ "/qq_add_account" })
	public ModelAndView qq_add_account(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("qq_login_bind.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		return mv;
	}
	/**
	 * * 完成QQ账号绑定，默认账号密码为123456，到这里用户已经自动登录，用户可以选择修改密码，或者绑定已有商城用户，如绑定现有用户，
	 * 需要输入用户密码进行确认，密码正确后， 自动完成绑定并用现有用户账户登录
	 * 
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param bind_already
	 * @return
	 */
	@RequestMapping({ "/qq_login_bind_finish" })
	public String qq_login_bind_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String bind_already, String email) {
		String url = "redirect:" + CommUtil.getURL(request) + "/index";
		String qq_openid = request.getSession(false).getAttribute("qq_openid")
				.toString();
		if (!CommUtil.null2String(bind_already).equals("")) {
			User user = this.userService.getObjByProperty( "userName","=",
					userName);
			if (user == null) {
				request.getSession(false).setAttribute("op_title", "用户名错误");
				request.getSession(false).setAttribute("url",
						CommUtil.getURL(request) + "/qq_add_account");
				url = "redirect:" + CommUtil.getURL(request) + "/error";
			} else if (Md5Encrypt.md5(password).toLowerCase()
					.equals(user.getPassword())) {
				request.getSession(false).removeAttribute("verify_code");

				user.setQq_openid(qq_openid);
				this.userService.updateById(user);

				url = "redirect:" + CommUtil.getURL(request)
						+ "/redpigmall_login?username="
						+ CommUtil.encode(user.getUsername()) + "&password="
						+ password;
			} else {
				request.getSession(false).setAttribute("op_title", "密码错误");
				request.getSession(false).setAttribute("url",
						CommUtil.getURL(request) + "/qq_add_account");
				url = "redirect:" + CommUtil.getURL(request) + "/error";
			}
		} else {
			User user = new User();
			user.setUserName(userName);
			user.setPassword(Md5Encrypt.md5(password).toLowerCase());
			user.setUserRole("BUYER");

			user.setQq_openid(qq_openid);
			user.setAddTime(new Date());
			Map<String, Object> params = Maps.newHashMap();
			params.put("type", "BUYER");
			
			List<Role> roles = this.roleService.queryPageList(params);
			
			user.getRoles().addAll(roles);
			if (this.configService.getSysConfig().getIntegral()) {
				user.setIntegral(this.configService.getSysConfig()
						.getMemberRegister());
				this.userService.saveEntity(user);
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("注册赠送积分:"
						+ this.configService.getSysConfig().getMemberRegister());
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
			request.getSession(false).removeAttribute("verify_code");
			url = "redirect:" + CommUtil.getURL(request)
					+ "/redpigmall_login?username="
					+ CommUtil.encode(user.getUsername()) + "&password="
					+ "redpigmall_thid_login_" + user.getPassword();
		}
		request.getSession(false).removeAttribute("verify_code");
		request.getSession(false).removeAttribute("qq_openid");
		return url;
	}
	/**
	 * 模拟http访问，返回相关数据
	 * 
	 * @param url
	 * @param charSet
	 * @param method
	 * @return
	 */
	public static String getHttpContent(String url, String charSet,
			String method) {
		HttpURLConnection connection = null;
		String content = "";
		try {
			URL address_url = new URL(url);
			connection = (HttpURLConnection) address_url.openConnection();
			connection.setRequestMethod("GET");

			connection.setConnectTimeout(1000000);
			connection.setReadTimeout(1000000);

			int response_code = connection.getResponseCode();
			if (response_code == 200) {
				InputStream in = connection.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in, charSet));
				String line = null;
				while ((line = reader.readLine()) != null) {
					content = content + line;
				}
				return content;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		if (connection != null) {
			connection.disconnect();
		}
		return "";
	}
	/**
	 * 生成不重复的用户名，这里使用1000000作为随即因子，系统用户超过千万级，可以修改该因子
	 * 
	 * @param userName
	 *            第三方账号用户名
	 * @return
	 */
	@SuppressWarnings("unused")
	private String generic_username(String userName) {
		String name = userName;
		User user = this.userService.getObjByProperty( "userName","=", name);
		if (user != null) {
			for (int i = 1; i < 1000000; i++) {
				name = name + i;
				user = this.userService.getObjByProperty( "userName","=", name);
				if (user == null) {
					break;
				}
			}
		}
		return name;
	}
	/**
	 * 测试QQ登录，需要手工在浏览器中输入相关信息配合测试工作
	 * 
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static void testmai(String[] args) {
		SysConfig config = new SysConfig();
		config.setQq_login_id("100359491");
		config.setQq_login_key("a34bcaef0487e650238983abc0fbae7c");
		String redirect_uri = CommUtil
				.encode("http://redpigmall.eicp.net/qq_login_bind");
		String auth_url = "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id="
				+ config.getQq_login_id()
				+ "&redirect_uri="
				+ redirect_uri
				+ "&state=redpigmall&scope=get_user_info";
		System.out.println(auth_url);

		String token_url = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id="
				+ config.getQq_login_id()
				+ "&client_secret="
				+ config.getQq_login_key()
				+ "&code=9873676D49030659CF025A0B9FF9F0B8&redirect_uri="
				+ redirect_uri;

		String me_url = "https://graph.qq.com/oauth2.0/me?access_token=1CA359B424836978AAA1424B83C1B5A3";

		String user_info_url = "https://graph.qq.com/user/get_user_info?access_token=1CA359B424836978AAA1424B83C1B5A3&oauth_consumer_key=100359491&openid=9A6383AD4B58E8B1ACF65DC68E0B3B68";

		System.out.println("返回值为："
				+ getHttpContent(user_info_url, "UTF-8", "GET"));
	}
}
