package com.redpigmall.view.web.action;

import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.redpigmall.redis.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.sec.SessionRegistry;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.api.tools.database.RedPigDatabaseTools;
import com.redpigmall.domain.SysLog;
import com.redpigmall.domain.User;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigAdvertService;
import com.redpigmall.service.RedPigAlbumService;
import com.redpigmall.service.RedPigChattingUserService;
import com.redpigmall.service.RedPigComplaintGoodsService;
import com.redpigmall.service.RedPigCouponInfoService;
import com.redpigmall.service.RedPigDeliveryAddressService;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.RedPigFavoriteService;
import com.redpigmall.service.RedPigGoldLogService;
import com.redpigmall.service.RedPigGoldRecordService;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigGoodsSpecPropertyService;
import com.redpigmall.service.RedPigGoodsSpecificationService;
import com.redpigmall.service.RedPigGroupInfoService;
import com.redpigmall.service.RedPigGroupLifeGoodsService;
import com.redpigmall.service.RedPigIntegralGoodsOrderService;
import com.redpigmall.service.RedPigIntegralLogService;
import com.redpigmall.service.RedPigMessageService;
import com.redpigmall.service.RedPigOrderFormLogService;
import com.redpigmall.service.RedPigOrderFormService;
import com.redpigmall.service.RedPigPayoffLogService;
import com.redpigmall.service.RedPigPredepositCashService;
import com.redpigmall.service.RedPigPredepositService;
import com.redpigmall.service.RedPigRoleService;
import com.redpigmall.service.RedPigSnsAttentionService;
import com.redpigmall.service.RedPigStoreGradeService;
import com.redpigmall.service.RedPigStorePointService;
import com.redpigmall.service.RedPigStoreService;
import com.redpigmall.service.RedPigStoreStatService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.service.RedPigSysLogService;
import com.redpigmall.service.RedPigSystemTipService;
import com.redpigmall.service.RedPigUserConfigService;
import com.redpigmall.service.RedPigUserGoodsClassService;
import com.redpigmall.service.RedPigUserService;

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
@SuppressWarnings({"unused" })
@Controller
public class RedPigManageAction  {
	
	private Logger logger = LoggerFactory.getLogger(RedPigManageAction.class);
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigUserConfigService userConfigService;
	@Autowired
	private RedPigUserService userService;
	@Autowired
	private RedPigRoleService roleService;
	@Autowired
	private RedPigStoreGradeService storeGradeService;
	@Autowired
	private RedPigMessageService messageService;
	@Autowired
	private RedPigAlbumService albumService;
	@Autowired
	private RedPigAccessoryService accessoryService;
	@Autowired
	private RedPigAdvertService advertService;
	@Autowired
	private RedPigPredepositService predepositService;
	@Autowired
	private RedPigEvaluateService evaluateService;
	@Autowired
	private RedPigGoodsCartService goodsCartService;
	@Autowired
	private RedPigUserGoodsClassService ugcService;
	@Autowired
	private RedPigSysLogService syslogService;
	@Autowired
	private RedPigOrderFormService orderFormService;
	@Autowired
	private RedPigOrderFormLogService orderFormLogService;
	@Autowired
	private RedPigGroupLifeGoodsService grouplifegoodsService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigGroupInfoService groupinfoService;
	@Autowired
	private RedPigCouponInfoService couponInfoService;
	@Autowired
	private RedPigPayoffLogService paylogService;
	@Autowired
	private RedPigGoodsSpecPropertyService specpropertyService;
	@Autowired
	private RedPigGoodsSpecificationService specService;
	@Autowired
	private RedPigGoldLogService goldlogService;
	@Autowired
	private RedPigFavoriteService favoriteService;
	@Autowired
	private RedPigComplaintGoodsService complaintGoodsService;
	@Autowired
	private RedPigStoreService storeService;
	@Autowired
	private RedPigGoldRecordService grService;
	@Autowired
	private RedPigStorePointService storepointService;
	@Autowired
	private RedPigGoldLogService glService;
	@Autowired
	private RedPigPredepositCashService redepositcashService;

	@Autowired
	private RedPigIntegralGoodsOrderService integralGoodsOrderService;
	@Autowired
	private RedPigIntegralLogService integralLogService;
	@Autowired
	private RedPigSnsAttentionService snsAttentionService;
	@Autowired
	private RedPigDeliveryAddressService deliveryAddressService;

	@Autowired
	private RedPigStoreStatService storeStatService;
	@Autowired
	private RedPigSystemTipService systemTipService;
	@Autowired
	private RedPigChattingUserService chattingUserService;

	@Autowired
	private RedPigDatabaseTools databaseTools;
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	@Autowired
	private RedPigSysLogService sysLogService;


	/**
	 * 用户登录页面
	 * @param request
	 * @param response
	 * @param username
	 * @param password
	 * @param lang
	 * @throws Exception
	 */
	@RequestMapping({ "/redpigmall_login" })
	public void redpigmall_login(
			HttpServletRequest request,
			HttpServletResponse response, 
			String username,
			String password,
			String lang) throws Exception {
		
		// 状态， admin表示后台，user表示前台,seller表示商家
		String login_role = request.getParameter("login_role");
		if ((login_role == null) || (login_role.equals(""))) {
			login_role = "user";
		}
		boolean ajax_login = CommUtil.null2Boolean(request.getParameter("ajax_login"));
		HttpSession session = request.getSession();
		session.setAttribute("login_role", login_role);
		session.setAttribute("ajax_login", ajax_login);
		session.setAttribute("lang",lang);
		session.setAttribute("refererUrl", request.getParameter("refererUrl"));
		
		String referer = request.getParameter("referer");
		String url = CommUtil.getURL(request);
		if ((StringUtils.isNotBlank(referer)) && (referer.contains(url))) {
			request.getSession(false).setAttribute("refererUrl",request.getParameter("referer"));
		}
		
		boolean flag = true;
		
		int times = CommUtil.null2Int(RedisCache.getObject("login_validate_" + username));
		
		if (times > 2) {
			String verify_code = CommUtil.null2String(RedisCache.getObject("verify_code_" + session.getId()));
			String code = request.getParameter("code") != null ? request.getParameter("code").toUpperCase() : "";
			if (!verify_code.equals(code)) {
				flag = false;
			}
			
		} else if ((this.configService.getSysConfig().getSecurityCodeLogin()) || (session.getAttribute("verify_code") != null)) {
			String code = request.getParameter("code") != null ? request.getParameter("code").toUpperCase() : "";
			if (!code.equals(session.getAttribute("verify_code"))) {
				flag = false;
			}
		}
		
//		if (!flag) {
//			// 验证码不正确清空密码禁止登陆
//			
//			if (times == 0) {
//				client.add("login_validate_" + username, Integer.valueOf(times + 1));
//			} else {
//				client.update("login_validate_" + username, Integer.valueOf(times + 1));
//			}
//			
//			response.sendRedirect("/login");
//			return;
//		}
		
		//这里校验一次账号密码、然后存入session
		Map<String, Object> map = Maps.newHashMap();
		map.put("password", Md5Encrypt.md5(password));
		map.put("userName", username);

		List<User> user_list = this.userService.verityUserNamePassword(map);
		
		if(RedPigCommonUtil.isNotNull(user_list) && user_list.size()==1){
			
			SecurityUserHolder.setCurrentUser(user_list.get(0));

			sessionRegistry.removeSessionRegistry(user_list.get(0).getUserName());

			
			if(ajax_login){
				request.getRequestDispatcher("/login_success").forward(request, response);
			}else{
				response.sendRedirect("/login_success");
			}
			
		}else{
			if(ajax_login){
				request.getRequestDispatcher("/login_error").forward(request, response);
			}else{
				response.sendRedirect("login_error");
			}
		}
		
	}

    /**
     * 用户登录页面
     * @param request
     * @param response
     * @param username
     * @param password
     * @throws Exception
     */
	@RequestMapping({ "/redpig_store_login" })
	public void redpig_store_login(
			HttpServletRequest request,
			HttpServletResponse response, 
			String username,
			String password) throws Exception {
		
		// 状态， admin表示后台，user表示前台,seller表示商家
		String login_role = request.getParameter("login_role");
		if ((login_role == null) || (login_role.equals(""))) {
			login_role = "user";
		}
		boolean ajax_login = CommUtil.null2Boolean(request.getParameter("ajax_login"));
		HttpSession session = request.getSession();
		session.setAttribute("login_role", login_role);
		session.setAttribute("ajax_login", ajax_login);
		
		session.setAttribute("refererUrl", request.getParameter("refererUrl"));
		
		String referer = request.getParameter("referer");
		String url = CommUtil.getURL(request);
		if ((StringUtils.isNotBlank(referer)) && (referer.contains(url))) {
			request.getSession(false).setAttribute("refererUrl",request.getParameter("referer"));
		}
		
		boolean flag = true;
		

		
		int times = CommUtil.null2Int(RedisCache.getObject("login_validate_" + username));
		
		if (times > 2) {
			String verify_code = CommUtil.null2String(RedisCache.getObject("verify_code_" + session.getId()));
			String code = request.getParameter("code") != null ? request.getParameter("code").toUpperCase() : "";
			if (!verify_code.equals(code)) {
				flag = false;
			}
			
		} else if ((this.configService.getSysConfig().getSecurityCodeLogin()) || (session.getAttribute("verify_code") != null)) {
			String code = request.getParameter("code") != null ? request.getParameter("code").toUpperCase() : "";
			if (!code.equals(session.getAttribute("verify_code"))) {
				flag = false;
			}
		}
		
		if (!flag) {
			// 验证码不正确清空密码禁止登陆
			
			if (times == 0) {
				RedisCache.putObject("login_validate_" + username, Integer.valueOf(times + 1));
			} else {
				RedisCache.putObject("login_validate_" + username, Integer.valueOf(times + 1));
			}
			
			response.sendRedirect("/login");
			return;
		}
		
		//这里校验一次账号密码、然后存入session
		Map<String, Object> map = Maps.newHashMap();
		map.put("password", Md5Encrypt.md5(password));
		map.put("userName", username);
		map.put("userRole", "SELLER");
		List<User> user_list = this.userService.verityUserNamePasswordAndRole(map);
		
		if(RedPigCommonUtil.isNotNull(user_list) && user_list.size()==1){
			
			SecurityUserHolder.setCurrentUser(user_list.get(0));
			
			sessionRegistry.removeSessionRegistry(user_list.get(0).getUserName());
			
			if(ajax_login){
				request.getRequestDispatcher("/login_success").forward(request, response);
			}else{
				response.sendRedirect("/login_success");
			}
			
		}else{
			if(ajax_login){
				request.getRequestDispatcher("/login_error").forward(request, response);
			}else{
				response.sendRedirect("/login_error");
			}
		}
		
	}
	
	
	/**
	 * 用户登录页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping({ "/redpig_web_login" })
	public void redpig_web_login(
			HttpServletRequest request,
			HttpServletResponse response, 
			String username,
			String password) throws Exception {
		
		// 状态， admin表示后台，user表示前台,seller表示商家
		String login_role = request.getParameter("login_role");
		if ((login_role == null) || (login_role.equals(""))) {
			login_role = "user";
		}
		boolean ajax_login = CommUtil.null2Boolean(request.getParameter("ajax_login"));
		HttpSession session = request.getSession();
		session.setAttribute("login_role", login_role);
		session.setAttribute("ajax_login", ajax_login);
		
		session.setAttribute("refererUrl", request.getParameter("refererUrl"));
		
		String referer = request.getParameter("referer");
		String url = CommUtil.getURL(request);
		if ((StringUtils.isNotBlank(referer)) && (referer.contains(url))) {
			request.getSession(false).setAttribute("refererUrl",request.getParameter("referer"));
		}
		
		boolean flag = true;
		

		
		int times = CommUtil.null2Int(RedisCache.getObject("login_validate_" + username));
		
		if (times > 2) {
			String verify_code = CommUtil.null2String(RedisCache.getObject("verify_code_" + session.getId()));
			String code = request.getParameter("code") != null ? request.getParameter("code").toUpperCase() : "";
			if (!verify_code.equals(code)) {
				flag = false;
			}
			
		} else if ((this.configService.getSysConfig().getSecurityCodeLogin()) || (session.getAttribute("verify_code") != null)) {
			String code = request.getParameter("code") != null ? request.getParameter("code").toUpperCase() : "";
			if (!code.equals(session.getAttribute("verify_code"))) {
				flag = false;
			}
		}
		request.getSession(false).removeAttribute("verify_code");
		if (!flag) {
			// 验证码不正确清空密码禁止登陆
			
			if (times == 0) {
				RedisCache.putObject("login_validate_" + username, Integer.valueOf(times + 1));
			} else {
				RedisCache.putObject("login_validate_" + username, Integer.valueOf(times + 1));
			}
			
			response.sendRedirect("/login");
			return;
		}
		
		//这里校验一次账号密码、然后存入session
		Map<String, Object> map = Maps.newHashMap();
		map.put("password", Md5Encrypt.md5(password));
		map.put("userName", username);
		map.put("userRole", "BUYER");
		List<User> user_list = this.userService.verityUserNamePasswordAndRole(map);
		
		if(RedPigCommonUtil.isNotNull(user_list) && user_list.size()==1){
			
			SecurityUserHolder.setCurrentUser(user_list.get(0));
			
			sessionRegistry.removeSessionRegistry(user_list.get(0).getUserName());
			
			if(ajax_login){
				request.getRequestDispatcher("/login_success").forward(request, response);
			}else{
				response.sendRedirect("/login_success");
			}
			
		}else{
			if(ajax_login){
				request.getRequestDispatcher("/login_error").forward(request, response);
			}else{
				response.sendRedirect("/login_error");
			}
		}
		
	}
	
	/**
	 * 用户登录页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping({ "/redpigmall_logout" })
	public void redpigmall_logout(
			HttpServletRequest request,
			HttpServletResponse response, 
			String username,
			String password) throws Exception  {
		
		HttpSession session = request.getSession(false);
		String redpigmall_view_type = CommUtil.null2String(request.getParameter("redpigmall_view_type"));
		session.setAttribute("redpigmall_view_type", redpigmall_view_type);
		User u = (User) session.getAttribute(Globals.USER_LOGIN);
		
		if (u != null) {
			User user = this.userService.selectByPrimaryKey(u.getId());
			user.setLastLoginDate((Date) session.getAttribute("lastLoginDate"));
			user.setLastLoginIp((String) session.getAttribute("loginIp"));
			this.userService.updateById(user);
			SysLog log = new SysLog();
			log.setAddTime(new Date());
			
			String name = null;
			if(StringUtils.isBlank(user.getTrueName())) {
				name = user.getUserName();
			}else {
				name = user.getTrueName();
			}
			
			log.setContent(name + "于"
					+ CommUtil.formatTime("yyyy-MM-dd HH:mm:ss", new Date())
					+ "退出系统");
			
			log.setTitle("用户退出");
			log.setType(0);
			log.setUser_id(user.getId());
			log.setIp(CommUtil.getIpAddr(request));
			
			this.sysLogService.saveEntity(log);
			
			this.sessionRegistry.removeSessionRegistry(u.getUserName());
			
		}
		
		SecurityUserHolder.removeCurrentUser();
		
		response.sendRedirect("/logout_success");
		
		
	}
	
}
