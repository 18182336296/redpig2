package com.redpigmall.plug.login.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Album;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.domain.Role;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigAlbumService;
import com.redpigmall.service.RedPigIntegralLogService;
import com.redpigmall.service.RedPigRoleService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigUserConfigService;
import com.redpigmall.service.RedPigUserService;

@Controller
public class WeixinLoginPlug {
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
	@Autowired
	private RedPigAccessoryService accessoryService;
//	private static final String CODE_URL = "https://open.weixin.qq.com/connect/qrconnect";

	@RequestMapping({ "/wechat_login_api" })
	public void wechat_login_api(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String redirect_uri = CommUtil.encode(CommUtil.getURL(request)
				+ "/wechat_login_bind");
		String state = CommUtil.randomString(32);
		request.getSession(false).setAttribute("state_session", state);
		String auth_url = "https://open.weixin.qq.com/connect/qrconnect?appid="
				+ this.configService.getSysConfig().getOpen_weixin_appId()
				+ "&redirect_uri=" + redirect_uri
				+ "&response_type=code&scope=snsapi_login&state=" + state
				+ "#wechat_redirect";
		response.sendRedirect(auth_url);
	}

	@SuppressWarnings({"rawtypes" })
	@RequestMapping({ "/wechat_login_bind" })
	public String wechat_login_bind(HttpServletRequest request,
			HttpServletResponse response, String code, String state) {
		String session_state = request.getSession(false)
				.getAttribute("state_session").toString();
		String redirect_uri = "index";
		if (session_state.equals(state)) {
			request.getSession(false).removeAttribute("state_session");
			String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
					+ this.configService.getSysConfig().getOpen_weixin_appId()
					+ "&secret="
					+ this.configService.getSysConfig()
							.getOpen_weixin_appSecret()
					+ "&code="
					+ code
					+ "&grant_type=authorization_code";
			String result = getHttpContent(url, "UTF-8");
			Map json = JSON.parseObject(result);
			String unionid = json.get("unionid") != null ? json.get("unionid")
					.toString() : "";
			if (!"".equals(unionid)) {
				if (SecurityUserHolder.getCurrentUser() == null) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("weixin_unionID", unionid);
					
					List<User> users = this.userService.queryPageList(params);
					
					User user = users.size() == 1 ? (User) users.get(0) : null;
					if (user != null) {
						request.getSession(false)
								.removeAttribute("verify_code");
						return "redirect:" + CommUtil.getURL(request)
								+ "/redpigmall_login?username="
								+ CommUtil.encode(user.getUsername())
								+ "&password=" + "redpigmall_thid_login_"
								+ user.getPassword();
					}
					request.getSession(false).removeAttribute("verify_code");
					request.getSession(false).setAttribute("bind_info", json);
					return "redirect:" + CommUtil.getURL(request)
							+ "/wechat_add_account";
				}
				User user = this.userService.selectByPrimaryKey(SecurityUserHolder
						.getCurrentUser().getId());
				user.setWeixin_unionID(unionid);
				this.userService.updateById(user);
				return "redirect:" + CommUtil.getURL(request)
						+ "/buyer/account_bind";
			}
		}
		request.getSession(false).removeAttribute("state_session");
		return "redirect:" + redirect_uri;
	}

	@SuppressWarnings({"rawtypes" })
	@RequestMapping({ "/wechat_login_bind_finish" })
	public String wechat_login_bind_finish(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String bind_already, String email, String bind_tokens) {
		String url = "redirect:" + CommUtil.getURL(request) + "/index";
		String token = request.getSession(false).getAttribute("bind_tokens") != null ? request
				.getSession(false).getAttribute("bind_tokens").toString() : "";
		if (bind_tokens.equals(token)) {
			if (!CommUtil.null2String(bind_already).equals("")) {
				User user = this.userService.getObjByProperty( "userName","=",userName);
				if (user == null) {
					request.getSession(false)
							.setAttribute("op_title", "用户绑定失败");
					request.getSession(false).setAttribute("url", url);
					url = "redirect:" + CommUtil.getURL(request) + "/error";
				} else if (Md5Encrypt.md5(password).toLowerCase()
						.equals(user.getPassword())) {
					Map json = (Map) request.getSession(false).getAttribute("bind_info");
					if ((json != null) && (json.get("unionid") != null)) {
						if ((user.getWeixin_unionID() == null)
								|| ("".equals(user.getWeixin_unionID()))) {
							user.setWeixin_unionID(json.get("unionid").toString());
							this.userService.updateById(user);
							request.getSession(false).removeAttribute("verify_code");
							url = "redirect:" + CommUtil.getURL(request)
									+ "/redpigmall_login?username="
									+ CommUtil.encode(user.getUsername())
									+ "&password=" + password;
						} else {
							request.getSession(false).setAttribute("op_title",
									"用户绑定失败,此账号已绑定微信");
							request.getSession(false).setAttribute("url", url);
							url = "redirect:" + CommUtil.getURL(request)
									+ "/error";
						}
					} else {
						request.getSession(false).setAttribute("op_title",
								"用户绑定失败");
						request.getSession(false).setAttribute("url", url);
						url = "redirect:" + CommUtil.getURL(request) + "/error";
					}
				} else {
					request.getSession(false)
							.setAttribute("op_title", "用户绑定失败");
					request.getSession(false).setAttribute("url",
							CommUtil.getURL(request) + "/index");
					url = "redirect:" + CommUtil.getURL(request) + "/error";
				}
			} else {
				Map json = (Map) request.getSession(false).getAttribute(
						"bind_info");
				if ((json != null) && (json.get("unionid") != null)) {
					User user = new User();
					String unionid = json.get("unionid").toString();
					String openID = json.get("openid") != null ? json.get(
							"openid").toString() : "";
					String access_token = json.get("access_token") != null ? json
							.get("access_token").toString() : "";
					String userInfo = getHttpContent(
							"https://api.weixin.qq.com/sns/userinfo?access_token="
									+ access_token + "&openid=" + openID,
							"UTF-8");

					json.clear();
					json = JSON.parseObject(userInfo);
					String nickname = json.get("nickname") != null ? json.get(
							"nickname").toString() : "";
					String sex = json.get("sex").toString();
					user.setNickName(generic_username(filterEmoji(nickname)));
					user.setUserRole("BUYER");
					user.setWeixin_unionID(unionid);
					user.setAddTime(new Date());
					request.getSession(false).removeAttribute("verify_code");
					user.setUserName(userName);
					user.setEmail(email);
					user.setPassword(Md5Encrypt.md5(password).toLowerCase());
					String fileName = "";
					try {
						fileName = getWeiXin_userPhoto(json.get("headimgurl")
								.toString(), request);
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (!"".equals(fileName)) {
						Accessory photo = new Accessory();
						photo.setAddTime(new Date());
						photo.setExt(".jpg");
						photo.setName(fileName);
						String uploadFilePath = this.configService
								.getSysConfig().getUploadFilePath()
								+ "/account";
						photo.setPath(uploadFilePath);
						this.accessoryService.saveEntity(photo);

						user.setPhoto(photo);
					}
					if ("1".equals(sex)) {
						user.setSex(1);
					} else {
						user.setSex(0);
					}
					this.userService.saveEntity(user);
					Map<String, Object> params = Maps.newHashMap();
					params.put("type", "BUYER");
					
					List<Role> roles = this.roleService.queryPageList(params);
					
					user.getRoles().addAll(roles);
					
					this.userService.saveUserRole(user.getId(), user.getRoles());
					
					if (this.configService.getSysConfig().getIntegral()) {
						user.setIntegral(this.configService.getSysConfig()
								.getMemberRegister());
						this.userService.updateById(user);
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
						this.userService.updateById(user);
					}
					Album album = new Album();
					album.setAddTime(new Date());
					album.setAlbum_default(true);
					album.setAlbum_name("默认相册");
					album.setAlbum_sequence(55536);
					album.setUser(user);
					this.albumService.saveEntity(album);
					url = "redirect:" + CommUtil.getURL(request)
							+ "/redpigmall_login?username="
							+ CommUtil.encode(user.getUsername())
							+ "&password=" + "redpigmall_thid_login_"
							+ user.getPassword();
				} else {
					request.getSession(false)
							.setAttribute("op_title", "用户绑定失败");
					request.getSession(false).setAttribute("url",
							CommUtil.getURL(request) + "/index");
					url = "redirect:" + CommUtil.getURL(request) + "/error";
				}
			}
		}
		request.getSession(false).removeAttribute("verify_code");
		request.getSession(false).removeAttribute("bind_info");
		return url;
	}

	@RequestMapping({ "/wechat_add_account" })
	public ModelAndView wechat_add_account(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("wechat_login_bind.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String token = CommUtil.randomInt(20);
		request.getSession(false).setAttribute("bind_tokens", token);
		mv.addObject("bind_tokens", token);
		return mv;
	}

	public static String getHttpContent(String url, String charSet) {
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

	private String generic_username(String userName) {
		String name = userName;
		User user = this.userService.getObjByProperty( "userName","=", name);
		if (user != null) {
			for (int i = 1; i < 1000000; i++) {
				name = name + i;
				user = this.userService
						.getObjByProperty( "userName","=", name);
				if (user == null) {
					break;
				}
			}
		}
		return name;
	}

	public static boolean containsEmoji(String source) {
		if (StringUtils.isBlank(source)) {
			return false;
		}
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (isEmojiCharacter(codePoint)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isEmojiCharacter(char codePoint) {
		return (codePoint == 0) || (codePoint == '\t') || (codePoint == '\n')
				|| (codePoint == '\r')
				|| ((codePoint >= ' ') && (codePoint <= 55295))
				|| ((codePoint >= 57344) && (codePoint <= 65533))
				|| ((codePoint >= 65536) && (codePoint <= 1114111));
	}

	public static String filterEmoji(String source) {
		if (!containsEmoji(source)) {
			return source;
		}
		StringBuilder buf = null;

		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (isEmojiCharacter(codePoint)) {
				if (buf == null) {
					buf = new StringBuilder(source.length());
				}
				buf.append(codePoint);
			}
		}
		if (buf == null) {
			return source;
		}
		if (buf.length() == len) {
			buf = null;
			return source;
		}
		return buf.toString();
	}

	private String getWeiXin_userPhoto(String urlString,
			HttpServletRequest request) throws IOException {
		URL url = new URL(urlString);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(30000);
		InputStream is = con.getInputStream();
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "account";
		byte[] bs = new byte['Ѐ'];

		File sf = new File(saveFilePathName);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		String fileName = CommUtil.formatTime("yyyyMMddHHmmss", new Date())
				+ ".jpg";
		OutputStream os = new FileOutputStream(sf.getPath() + File.separator
				+ fileName);
		int len;
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.close();
		is.close();
		return fileName;
	}
}
