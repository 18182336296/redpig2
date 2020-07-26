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
			//如果系统开启了积分商城,记录登录积分
			saveUserIntegralLog(user);
			
			user.setLoginDate(new Date());//用户登录时间
			user.setLoginIp(CommUtil.getIpAddr(request));//用户登录IP
			user.setLoginCount(user.getLoginCount() + 1);//用户登录次数加1
			this.userService.updateById(user);
			
			HttpSession session = request.getSession(false);
			session.setAttribute("user", user);
			
			SecurityUserHolder.setCurrentUser(user);
			
			session.setAttribute("userName", user.getUsername());//用户名放到session中
			session.setAttribute("lastLoginDate", new Date());// 设置登录时间
			session.setAttribute("loginIp", CommUtil.getIpAddr(request));// 设置登录IP
			session.setAttribute("login", true);// 设置登录标识
			
			String role = user.getUserRole();
			String url = CommUtil.getURL(request) + "/user_login_success";
			if (!CommUtil.null2String(
					request.getSession(false).getAttribute("refererUrl"))
					.equals("")) {
				
				url = CommUtil.null2String(request.getSession(false).getAttribute("refererUrl"));
			}
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
				if (login_role.equalsIgnoreCase("admin")) {
					if (role.indexOf("ADMIN") >= 0) {
						url = CommUtil.getURL(request) + "/index";
						request.getSession(false).setAttribute("admin_login",true);
					}
				}
				if (login_role.equalsIgnoreCase("seller")
						&& role.indexOf("SELLER") >= 0) {
					url = CommUtil.getURL(request) + "/index";
					request.getSession(false).setAttribute("seller_login", true);
				}
				if (!CommUtil.null2String(
						request.getSession(false).getAttribute("refererUrl"))
						.equals("")) {
					url = CommUtil.null2String(request.getSession(false).getAttribute("refererUrl"));
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
	 * 如果系统开启了积分商城
	 * 记录登录积分
	 * @param user
	 */
	private void saveUserIntegralLog(User user) {
		if (this.configService.getSysConfig().getIntegral()) {
			if (user.getLoginDate() == null
					|| user.getLoginDate().before(new Date())) {
				
				user.setIntegral(user.getIntegral()+ this.configService.getSysConfig().getMemberDayLogin());
				
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
			targetUrl = RedPigCommUtil.getURL(request) + "/wap/index";
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
			mv.addObject("url", RedPigCommUtil.getURL(request) + "/wap/index");
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
	 * 系统提醒页
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param orderBy
	 * @param orderType
	 * @return
	 */
	@SecurityMapping(title = "系统提醒页", value = "/sys_tip_list*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping({ "/sys_tip_list" })
	public ModelAndView sys_tip_list(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String orderBy,
			String orderType) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/sys_tip_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage, orderBy, orderType);
		maps.put("orderBy", "st_status asc,");
		maps.put("orderType", "st_level desc,addTime desc");
		
		IPageList pList = this.systemTipService.list(maps);
		RedPigCommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 系统提醒删除
	 * @param request
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "系统提醒删除", value = "/sys_tip_del*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping({ "/sys_tip_del" })
	public String sys_tip_del(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		
		List<Long> sIds = Lists.newArrayList();
		if( ids!=null && ids.length>0 ){
			for(String id : ids){
				if (!id.equals("")) {
					sIds.add(Long.valueOf(id.trim()));
				}
			}
			this.systemTipService.batchDeleteByIds(sIds);
		}
		
		return "redirect:sys_tip_list";
	}
	
	/**
	 * 系统提醒处理
	 * @param request
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "系统提醒处理", value = "/sys_tip_do*", rtype = "admin", rname = "系统提示页", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping({ "/sys_tip_do" })
	public String sys_tip_do(HttpServletRequest request, String mulitId) {
		String[] ids = mulitId.split(",");
		
		for (String id : ids) {
			if (!id.equals("")) {
				SystemTip st = this.systemTipService.selectByPrimaryKey(RedPigCommUtil.null2Long(id));
				st.setSt_status(1);
				this.systemTipService.updateById(st);
			}
		}
		
		return "redirect:sys_tip_list";
	}
	
	/**
	 * 保存商城配置
	 * @param request
	 * @param response
	 * @param id 系统配置SysConfig主键ID
	 * @param list_url 
	 * @param op_title
	 * @param app_download
	 * @param android_download
	 * @param ios_download
	 * @param android_seller_download
	 * @param ios_seller_download
	 * @param app_seller_download
	 * @return
	 */
	@SecurityMapping(title = "保存商城配置", value = "/sys_config_save*", rtype = "admin", display = false, rname = "保存商城配置", rcode = "admin_config_save", rgroup = "设置")
	@RequestMapping({ "/sys_config_save" })
	public ModelAndView sys_config_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String list_url,
			String op_title, String app_download, String android_download,
			String ios_download, String android_seller_download,
			String ios_seller_download, String app_seller_download) {
		
		SysConfig obj = this.configService.getSysConfig();
		WebForm wf = new WebForm();
		SysConfig sysConfig = null;
		if (id.equals("")) {
			sysConfig = (SysConfig) WebForm.toPo(request, SysConfig.class);
			sysConfig.setAddTime(new Date());
		} else {
			sysConfig = (SysConfig) WebForm.toPo(request, obj);
		}
		
		if ((sysConfig.getAddress() != null) && (!sysConfig.getAddress().equals(""))) {
			String address = sysConfig.getAddress();
			if (address.indexOf("http://") < 0) {
				address = "http://" + address;
				sysConfig.setAddress(address);
			}
		}
		
		String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath + File.separator + "system";
		
		RedPigCommUtil.createFolder(saveFilePathName);
		
		Map<String, Object> map = Maps.newHashMap();
		
		//网站logo上传
		map = uploadWebSiteLogo(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		
		//宽屏网站logo
		map.clear();
		map = uploadWebSiteLogoWide(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		
		//商品图片logo
		map = uploadGoodsImage(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		//店铺图片logo
		map.clear();
		map = uploadStoreImage(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		//默认会员图片
		map.clear();
		map = uploadMemberIcon(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		//平台登录Logo
		map.clear();
		map = uploadAdminLoginImg(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		// 平台管理Logo
		map.clear();
		map = uploadAdminManageImg(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		//平台上传ico，考虑系统效率，本图片不上传至ftp
		map.clear();
		map = uploadWebSiteIcon(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		//上传系统二维码中心Logo图片,考虑系统效率，本图片不上传至ftp，
		map.clear();
		map = uploadQRLogo(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		// 开启app下载生成下载链接二维码
		appQrCreate(request, app_download, android_download, ios_download, android_seller_download, ios_seller_download,app_seller_download, sysConfig, uploadFilePath);
		
		//热搜
		if ((sysConfig.getHotSearch() != null) && (!sysConfig.getHotSearch().equals(""))) {
			sysConfig.setHotSearch(sysConfig.getHotSearch().replaceAll("，", ","));// 替换全角分隔号
		}
		
		//关键字
		if ((sysConfig.getKeywords() != null) && (!sysConfig.getKeywords().equals(""))) {
			sysConfig.setKeywords(sysConfig.getKeywords().replaceAll("，", ","));
		}
		
		//图片后缀
		String imageSuffix = sysConfig.getImageSuffix();
		String[] suffix_list = { "php", "asp", "jsp", "html", "htm", "cgi","action", "js", "css" };
		for (String suffix : suffix_list) {
			imageSuffix = RedPigCommUtil.null2String(imageSuffix).replaceAll(suffix,"");
		}
		
		sysConfig.setImageSuffix(imageSuffix);//设置图片后缀
		
		if (id.equals("")) {
			this.configService.saveEntity(sysConfig);
		} else {
			this.configService.updateById(sysConfig);
		}
		
		//登录页面的左侧图片上传
		map = uploadLeftLogoImage(request, sysConfig, uploadFilePath, saveFilePathName, map);
		
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("op_title", op_title);
		mv.addObject("list_url", list_url);
		return mv;
	}

	private Map<String, Object> uploadLeftLogoImage(HttpServletRequest request, SysConfig sysConfig,
			String uploadFilePath, String saveFilePathName, Map<String, Object> map) {
		for (int i = 0; i < 4; i++) {
			try {
				map.clear();
				String fileName = "";
				if (sysConfig.getLogin_imgs().size() > i) {
					fileName = sysConfig.getLogin_imgs().get(i).getName();
				}
				
				map = RedPigCommUtil.saveFileToServer(request, "img" + i, saveFilePathName, fileName, null);
				if (fileName.equals("")) {
					if (map.get("fileName") != "") {
						Accessory logo = new Accessory();
						logo.setName(RedPigCommUtil.null2String(map.get("fileName")));
						logo.setExt(RedPigCommUtil.null2String(map.get("mime")));
						logo.setSize(BigDecimal.valueOf(RedPigCommUtil.null2Double(map.get("fileSize"))));
						logo.setPath(uploadFilePath + "/system");
						logo.setWidth(RedPigCommUtil.null2Int(map.get("width")));
						logo.setHeight(RedPigCommUtil.null2Int(map.get("height")));
						logo.setAddTime(new Date());
						logo.setConfig(sysConfig);
						this.accessoryService.saveEntity(logo);
					}
				} else if (map.get("fileName") != "") {
					Accessory logo = sysConfig.getLogin_imgs().get(i);
					logo.setName(RedPigCommUtil.null2String(map.get("fileName")));
					logo.setExt(RedPigCommUtil.null2String(map.get("mime")));
					logo.setSize(BigDecimal.valueOf(RedPigCommUtil.null2Double(map.get("fileSize"))));
					logo.setPath(uploadFilePath + "/system");
					logo.setWidth(RedPigCommUtil.null2Int(map.get("width")));
					logo.setHeight(RedPigCommUtil.null2Int(map.get("height")));
					logo.setConfig(sysConfig);
					this.accessoryService.updateById(logo);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	private void appQrCreate(HttpServletRequest request, String app_download, String android_download,
			String ios_download, String android_seller_download, String ios_seller_download, String app_seller_download,
			SysConfig sysConfig, String uploadFilePath) {
		if ((!"".equals(app_download)) && (app_download != null)) {
			sysConfig.setApp_download(RedPigCommUtil.null2Int(app_download));
			sysConfig.setAndroid_download(android_download);
			sysConfig.setIos_download(ios_download);
		}
		
		if (RedPigCommUtil.null2Int(app_download) == 1) {
			String destPath = ClusterSyncTools.getClusterRoot()
					+ uploadFilePath + File.separator + "app";
			if (!RedPigCommUtil.fileExist(destPath)) {
				RedPigCommUtil.createFolder(destPath);
			}
			String logoPath = "";
			if (this.configService.getSysConfig().getQr_logo() != null) {
				logoPath = request.getSession().getServletContext().getRealPath("/")
						+ this.configService.getSysConfig().getQr_logo().getPath()
						+ File.separator
						+ this.configService.getSysConfig().getQr_logo().getName();
			}
			String download_url = RedPigCommUtil.getURL(request) + "/app_download";
			QRCodeUtil.encode(download_url, logoPath, destPath + File.separator + "app_dowload.png", true);
		}
		
		if ((!"".equals(app_seller_download)) && (app_seller_download != null)) {
			sysConfig.setApp_seller_download(RedPigCommUtil.null2Int(app_seller_download));
			sysConfig.setAndroid_seller_download(android_seller_download);
			sysConfig.setIos_seller_download(ios_seller_download);
		}
		if (RedPigCommUtil.null2Int(app_seller_download) == 1) {
			String destPath = ClusterSyncTools.getClusterRoot()
					+ uploadFilePath + File.separator + "app";
			if (!RedPigCommUtil.fileExist(destPath)) {
				RedPigCommUtil.createFolder(destPath);
			}
			String logoPath = "";
			if (this.configService.getSysConfig().getQr_logo() != null) {
				logoPath =

				request.getSession().getServletContext().getRealPath("/")
						+ this.configService.getSysConfig().getQr_logo()
								.getPath()
						+ File.separator
						+ this.configService.getSysConfig().getQr_logo()
								.getName();
			}
			String download_url = RedPigCommUtil.getURL(request)
					+ "/app_seller_download";
			QRCodeUtil.encode(download_url, logoPath, destPath + File.separator
					+ "app_seller_download.png", true);
		}
	}

	private Map<String, Object> uploadQRLogo(HttpServletRequest request, SysConfig sysConfig, String uploadFilePath,
			String saveFilePathName, Map<String, Object> map) {
		try {
			map = RedPigCommUtil.saveFileToServer(request, "qrLogo",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getQr_logo() == null ? ""
					: this.configService.getSysConfig().getQr_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = saveImage(uploadFilePath, map);
					sysConfig.setQr_logo(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getQr_logo();
				updateImage(uploadFilePath, map, logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, Object> uploadWebSiteIcon(HttpServletRequest request, SysConfig sysConfig,
			String uploadFilePath, String saveFilePathName, Map<String, Object> map) {
		try {
			map = RedPigCommUtil.saveFileToServer(request, "website_ico",
					saveFilePathName, "favicon.ico", null);
			String fileName = this.configService.getSysConfig()
					.getWebsite_ico() == null ? "" : this.configService
					.getSysConfig().getWebsite_ico().getName();
			if ((fileName != null) && (fileName.equals(""))) {
				if (map.get("fileName") != "") {
					Accessory logo = saveImage(uploadFilePath, map);
					sysConfig.setWebsite_ico(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getWebsite_ico();
				updateImage(uploadFilePath, map, logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, Object> uploadAdminManageImg(HttpServletRequest request, SysConfig sysConfig,
			String uploadFilePath, String saveFilePathName, Map<String, Object> map) {
		try {
			map = RedPigCommUtil.saveFileToServer(request, "admin_manage_img",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig()
					.getAdmin_manage_logo() == null ? "" : this.configService
					.getSysConfig().getAdmin_manage_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = saveImage(uploadFilePath, map);
					
					sysConfig.setAdmin_manage_logo(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getAdmin_manage_logo();
				updateImage(uploadFilePath, map, logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, Object> uploadAdminLoginImg(HttpServletRequest request, SysConfig sysConfig,
			String uploadFilePath, String saveFilePathName, Map<String, Object> map) {
		try {
			map = RedPigCommUtil.saveFileToServer(request, "admin_login_img",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig()
					.getAdmin_login_logo() == null ? "" : this.configService
					.getSysConfig().getAdmin_login_logo().getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory photo = saveImage(uploadFilePath, map);
					
					sysConfig.setAdmin_login_logo(photo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getAdmin_login_logo();
				updateImage(uploadFilePath, map, logo);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, Object> uploadMemberIcon(HttpServletRequest request, SysConfig sysConfig, String uploadFilePath,
			String saveFilePathName, Map<String, Object> map) {
		try {
			map = RedPigCommUtil.saveFileToServer(request, "memberIcon",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getMemberIcon() == null ? ""
					: this.configService.getSysConfig().getMemberIcon()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = saveImage(uploadFilePath, map);
					
					sysConfig.setMemberIcon(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getMemberIcon();
				updateImage(uploadFilePath, map, logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, Object> uploadStoreImage(HttpServletRequest request, SysConfig sysConfig, String uploadFilePath,
			String saveFilePathName, Map<String, Object> map) {
		try {
			map = RedPigCommUtil.saveFileToServer(request, "storeImage",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getStoreImage() == null ? ""
					: this.configService.getSysConfig().getStoreImage()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = saveImage(uploadFilePath, map);
					
					sysConfig.setStoreImage(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getStoreImage();
				updateImage(uploadFilePath, map, logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, Object> uploadGoodsImage(HttpServletRequest request, SysConfig sysConfig, String uploadFilePath,
			String saveFilePathName, Map<String, Object> map) {
		map.clear();
		try {
			map = RedPigCommUtil.saveFileToServer(request, "goodsImage",
					saveFilePathName, null, null);
			String fileName = this.configService.getSysConfig().getGoodsImage() == null ? ""
					: this.configService.getSysConfig().getGoodsImage()
							.getName();
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = saveImage(uploadFilePath, map);
					
					sysConfig.setGoodsImage(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getGoodsImage();
				updateImage(uploadFilePath, map, logo);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, Object> uploadWebSiteLogoWide(HttpServletRequest request, SysConfig sysConfig,
			String uploadFilePath, String saveFilePathName, Map<String, Object> map) {
		try {
			String fileName = this.configService.getSysConfig()
					.getWebsiteLogo_wide() == null ? "" : this.configService
							.getSysConfig().getWebsiteLogo_wide().getName();
			
			map = RedPigCommUtil.saveFileToServer(request, "websiteLogo_wide",saveFilePathName, null, null);
			
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = saveImage(uploadFilePath, map);
					
					sysConfig.setWebsiteLogo_wide(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getWebsiteLogo_wide();
				updateImage(uploadFilePath, map, logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private Map<String, Object> uploadWebSiteLogo(HttpServletRequest request, SysConfig sysConfig,
			String uploadFilePath, String saveFilePathName, Map<String, Object> map) {
		try {
			String fileName = this.configService.getSysConfig()
					.getWebsiteLogo() == null ? "" : this.configService
					.getSysConfig().getWebsiteLogo().getName();
			
			map = RedPigCommUtil.saveFileToServer(request, "websiteLogo",saveFilePathName, fileName, null);
			
			if (fileName.equals("")) {
				if (map.get("fileName") != "") {
					Accessory logo = saveImage(uploadFilePath, map);
					
					sysConfig.setWebsiteLogo(logo);
				}
			} else if (map.get("fileName") != "") {
				Accessory logo = sysConfig.getWebsiteLogo();
				updateImage(uploadFilePath, map, logo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	private void updateImage(String uploadFilePath, Map<String, Object> map, Accessory logo) {
		logo.setName(RedPigCommUtil.null2String(map.get("fileName")));
		logo.setExt(RedPigCommUtil.null2String(map.get("mime")));
		logo.setSize(BigDecimal.valueOf(RedPigCommUtil.null2Double(map.get("fileSize"))));
		logo.setPath(uploadFilePath + "/system");
		logo.setWidth(RedPigCommUtil.null2Int(map.get("width")));
		logo.setHeight(RedPigCommUtil.null2Int(map.get("height")));
		this.accessoryService.updateById(logo);
	}

	private Accessory saveImage(String uploadFilePath, Map<String, Object> map) {
		Accessory logo = new Accessory();
		logo.setName(RedPigCommUtil.null2String(map.get("fileName")));
		logo.setExt(RedPigCommUtil.null2String(map.get("mime")));
		logo.setSize(BigDecimal.valueOf(RedPigCommUtil.null2Double(map.get("fileSize"))));
		logo.setPath(uploadFilePath + "/system");
		logo.setWidth(RedPigCommUtil.null2Int(map.get("width")));
		logo.setHeight(RedPigCommUtil.null2Int(map.get("heigh")));
		logo.setAddTime(new Date());
		this.accessoryService.saveEntity(logo);
		return logo;
	}
	
	/**
	 * 二级域名设置保存
	 * @param request
	 * @param response
	 * @param id
	 * @param domain_allow_count
	 * @param sys_domain
	 * @param second_domain_open
	 * @return
	 */
	@SecurityMapping(title = "二级域名设置保存", value = "/set_second_domain_save*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping({ "/set_second_domain_save" })
	public ModelAndView set_second_domain_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String domain_allow_count,
			String sys_domain, String second_domain_open) {
		String serverName = request.getServerName().toLowerCase();
		ModelAndView mv = new RedPigJModelAndView("admin/blue/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);

		SysConfig config = this.configService.getSysConfig();
		config.setDomain_allow_count(RedPigCommUtil.null2Int(domain_allow_count));
		config.setSys_domain(sys_domain);
		config.setSecond_domain_open(RedPigCommUtil.null2Boolean(second_domain_open));
		if (StringUtils.isNotBlank(id)) {
			this.configService.updateById(config);
		}
		mv.addObject("op_title", "二级域名保存成功");
		mv.addObject("list_url", RedPigCommUtil.getURL(request)
				+ "/set_second_domain");

		return mv;
	}
	
	/**
	 * QQ互联登录
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "QQ互联登录", value = "/set_site_qq*", rtype = "admin", rname = "二级域名", rcode = "admin_set_second_domain", rgroup = "设置")
	@RequestMapping({ "/set_site_qq" })
	public ModelAndView set_site_qq(
			HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"admin/blue/set_second_domain.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 管理员退出
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/logout" })
	public String logout(HttpServletRequest request,
			HttpServletResponse response) {
		
//		if (SecurityUserHolder.getCurrentUser() != null) {
//			Authentication authentication = new UsernamePasswordAuthenticationToken(
//					SecurityContextHolder.getContext().getAuthentication()
//							.getPrincipal(), SecurityContextHolder.getContext()
//							.getAuthentication().getCredentials(),
//							Lists.newArrayList(SecurityUserHolder.getCurrentUser().get_common_Authorities()));
//			SecurityContextHolder.getContext().setAuthentication(authentication);
//		}
		return "redirect:../index";
	}
	
	/**
	 * 管理员登陆
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping({ "/login" })
	public ModelAndView login(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/login.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		request.getSession(false).removeAttribute("verify_code");
		if (SecurityUserHolder.getCurrentUser() != null) {
			mv.addObject("user", SecurityUserHolder.getCurrentUser());
		}
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
		mv.addObject("op_title",request.getSession(false).getAttribute("op_title"));
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
	@RequestMapping({ "/RedpigError" })
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
	
	/**
	 * 初始化系统默认图片
	 * @param request
	 * @param response
	 * @param type
	 * @throws IOException
	 */
	@SecurityMapping(title = "初始化系统默认图片", value = "/restore_img*", rtype = "admin", rname = "商城后台管理", rcode = "admin_index", display = false, rgroup = "设置")
	@RequestMapping({ "/restore_img" })
	public void restore_img(HttpServletRequest request,
			HttpServletResponse response, String type) throws IOException {
		SysConfig config = this.configService.getSysConfig();
		Map<String, Object> map = Maps.newHashMap();
		if ((type != null) && (type.equals("member"))) {// 恢复系统默认会员头像
			Accessory acc = config.getMemberIcon();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getMemberIcon();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("member.jpg");
			config.setMemberIcon(acc);
			this.configService.updateById(config);
			map.put("path", RedPigCommUtil.getURL(request) + "/resources/style/common/images/member.jpg");
		}
		if ((type != null) && (type.equals("goods"))) {// 恢复系统默认商品头像
			Accessory acc = config.getGoodsImage();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getGoodsImage();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("good.jpg");
			config.setGoodsImage(acc);
			this.configService.updateById(config);
			map.put("path", RedPigCommUtil.getURL(request) + "/resources/style/common/images/good.jpg");
		}
		if ((type != null) && (type.equals("store"))) {// 恢复系统默认店铺头像
			Accessory acc = config.getStoreImage();
			if (acc == null) {
				acc = new Accessory();
			} else {
				acc = config.getStoreImage();
			}
			acc.setPath("resources/style/common/images");
			acc.setName("store.jpg");
			config.setStoreImage(acc);
			this.configService.updateById(config);
			map.put("path", RedPigCommUtil.getURL(request) + "/resources/style/common/images/store.jpg");
		}
		if ((type != null) && (type.equals("admin_login_img"))) {// 恢复平台管理登录页左上角Logo
			Accessory acc = config.getAdmin_login_logo();
			config.setAdmin_login_logo(null);
			this.configService.updateById(config);
			if (acc != null) {
				this.accessoryService.delete(acc.getId());
			}
			map.put("path",RedPigCommUtil.getURL(request) + "/resources/style/system/manage/blue/images/login/login_logo.png");
		}
		if ((type != null) && (type.equals("admin_manage_img"))) {// 恢复平台管理中心左上角的Logo
			Accessory acc = config.getAdmin_manage_logo();
			config.setAdmin_manage_logo(null);
			this.configService.updateById(config);
			if (acc != null) {
				this.accessoryService.delete(acc.getId());
			}
			map.put("path", RedPigCommUtil.getURL(request)
					+ "/resources/style/system/manage/blue/images/logo.png");
		}
		map.put("type", type);
		HttpSession session = request.getSession(false);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter writer = response.getWriter();
		writer.print(JSON.toJSONString(map));
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
	
	/**
	 * 商城后台邮件测试
	 * @param response
	 * @param email
	 */
	@SecurityMapping(title = "商城后台邮件测试", value = "/test_mail*", rtype = "admin", rname = "商城后台管理", rcode = "admin_test_mail", display = false, rgroup = "设置")
	@RequestMapping({ "/test_mail" })
	public void test_email(HttpServletResponse response, String email) {
		String subject = this.configService.getSysConfig().getTitle() + "测试邮件";
		boolean ret = this.msgTools.sendEmail(email, subject, subject);
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
	 * 商城后台短信测试
	 * @param response
	 * @param mobile
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "商城后台短信测试", value = "/test_sms*", rtype = "admin", rname = "商城后台管理", rcode = "admin_test_sms", display = false, rgroup = "设置")
	@RequestMapping({ "/test_sms" })
	public void test_sms(HttpServletResponse response, String mobile)
			throws UnsupportedEncodingException {
		String content = this.configService.getSysConfig().getTitle()
				+ "亲,如果您收到短信，说明发送成功！";
		boolean ret = this.msgTools.sendSMS(mobile, content);
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
	 * websiteCss设置
	 * @param request
	 * @param response
	 * @param webcss
	 */
	@SecurityMapping(title = "websiteCss设置", value = "/set_websiteCss*", rtype = "admin", rname = "常规设置", rcode = "admin_set_site", rgroup = "设置")
	@RequestMapping({ "/set_websiteCss" })
	public void set_websiteCss(HttpServletRequest request,
			HttpServletResponse response, String webcss) {
		SysConfig obj = this.configService.getSysConfig();
		if ((webcss != null) && (!webcss.equals("blue"))
				&& (!webcss.equals("black"))) {
			webcss = "blue";
		}
		obj.setWebsiteCss(webcss);
		this.configService.updateById(obj);
	}
}
