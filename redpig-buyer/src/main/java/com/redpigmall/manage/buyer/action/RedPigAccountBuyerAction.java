package com.redpigmall.manage.buyer.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.RedPigMaps;
import com.redpigmall.api.tools.WebForm;
import com.redpigmall.manage.buyer.action.base.BaseAction;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VerifyCode;

/**
 * 
 * <p>
 * Title: RedPigAccountBuyerAction.java
 * </p>
 * 
 * <p>
 * Description:“我的账户”管理控制器
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
 * @author redpig@author redpig
 * 
 * @date 2014-4-28
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Controller
public class RedPigAccountBuyerAction extends BaseAction{

	/** 默认的头像文件扩展名 */
	private static final String DEFAULT_AVATAR_FILE_EXT = ".jpg";
	/** 上传成功 */
	public static final String OPERATE_RESULT_CODE_SUCCESS = "200";
	
	/**
	 * 个人信息导航
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "个人信息导航", value = "/buyer/account_nav*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_nav" })
	public ModelAndView account_nav(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account_nav.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//操作标识
		String op = CommUtil.null2String(request.getAttribute("op"));
		mv.addObject("op", op);
		//当前登陆用户
		mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()));
		return mv;
	}
	
	/**
	 * 个人信息
	 * @param request 请求
	 * @param response 响应
	 * @return
	 */
	@SecurityMapping(title = "个人信息", value = "/buyer/account*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account" })
	public ModelAndView account(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//当前登陆用户
		mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()));
		
		List<Area> areas = this.areaService.queryPageList(RedPigMaps.newParent(-1));
		//地区
		mv.addObject("areas", areas);
		return mv;
	}
	
	/**
	 * 个人信息保存
	 * @param request 请求
	 * @param response 响应
	 * @param area_id
	 * @param birthday
	 * @return
	 */
	@SecurityMapping(title = "个人信息保存", value = "/buyer/account_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_save" })
	public ModelAndView account_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String area_id, String birthday) {
		//更新成功页面
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		//首先查询数据库中当前登陆用户的信息,之后将前台传过来的用户数据赋值到库中的对象上,实现更新效果
		User user = (User) WebForm.toPo(request, this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()));
		
		//地区查询
		if ((area_id != null) && (!area_id.equals(""))) {
			Area area = this.areaService.selectByPrimaryKey(CommUtil.null2Long(area_id));
			user.setArea(area);
		}
		
		//用户年龄换算
		if ((birthday != null) && (!birthday.equals(""))) {
			String[] y = birthday.split("-");
			Calendar calendar = new GregorianCalendar();
			int years = calendar.get(1) - CommUtil.null2Int(y[0]);
			user.setYears(years);
		}
		
		this.userService.updateById(user);//更新
		//更新成功提示
		mv.addObject("op_title", "个人信息修改成功");
		//更新完后跳转目标页面
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account");
		return mv;
	}
	
	/**
	 * 密码修改
	 * @param request 请求
	 * @param response 响应
	 * @return
	 */
	@SecurityMapping(title = "密码修改", value = "/buyer/account_password*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_password" })
	public ModelAndView account_password(HttpServletRequest request,
			HttpServletResponse response) {
		//修改密码跳转页面
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account_password.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		return mv;
	}
	
	/**
	 * 密码修改保存
	 * @param request 请求
	 * @param response 响应
	 * @param old_password
	 * @param new_password
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "密码修改保存", value = "/buyer/account_password_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_password_save" })
	public ModelAndView account_password_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String old_password,
			String new_password) throws Exception {
		//修改密码保存成功页面
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		//当前登陆的用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		if (user.getPassword().equals(Md5Encrypt.md5(old_password).toLowerCase())) {
			
			user.setPassword(Md5Encrypt.md5(new_password).toLowerCase());
			this.userService.updateById(user);
			boolean ret = false;//这里要判断是否要发短信,没有测试帐号就先默认不发送
			//修改成功提示
			mv.addObject("op_title", "密码修改成功");
			
			if (ret) {
				//短信内容
				String content = "尊敬的"
						+ SecurityUserHolder.getCurrentUser().getUserName()
						+ "您好，您于" + CommUtil.formatLongDate(new Date())
						+ "修改密码成功，新密码为：" + new_password + ",请妥善保管。["
						+ this.configService.getSysConfig().getTitle() + "]";
				//发短信
				this.msgTools.sendSMS(user.getMobile(), content);
			}
		} else {
			//原始密码输入错误，修改失败
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "原始密码输入错误，修改失败");
		}
		
		//成功后跳转页
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_password");
		return mv;
	}
	
	/**
	 * 支付密码修改
	 * @param request 请求
	 * @param response 响应
	 * @return
	 */
	@SecurityMapping(title = "支付密码修改", value = "/buyer/account_pay_password*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_pay_password" })
	public ModelAndView account_pay_password(
			HttpServletRequest request,
			HttpServletResponse response) {
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		//支付页面
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account_pay_password.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		mv.addObject("user", user);
		//验证手机规则
		if ((user.getMobile() != null) && (user.getMobile().length()>8)) {
			String show_tel = user.getMobile().substring(0, 3) + "*****" + user.getMobile().substring(8);
			//手机号中间四位数屏蔽
			mv.addObject("show_tel", show_tel);
		}
		
		String ran = CommUtil.randomInt(8) + SecurityUserHolder.getCurrentUser().getId();
		request.getSession(false).setAttribute("SessionCode", ran);
		mv.addObject("SessionCode", ran);
		return mv;
	}
	
	/**
	 * 邮箱修改
	 * @param request 请求
	 * @param response 响应
	 * @return
	 */
	@SecurityMapping(title = "邮箱修改", value = "/buyer/account_email*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_email" })
	public ModelAndView account_email(
			HttpServletRequest request,
			HttpServletResponse response) {
		//邮箱修改页面
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account_email.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 发送验证邮件:类型我们平时注册用户,需要发送邮件验证链接一样
	 * @param request 请求
	 * @param response 响应
	 * @param password
	 * @param email
	 * @return
	 */
	@SecurityMapping(title = "发邮件", value = "/buyer/account_email_send*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_email_send" })
	public ModelAndView account_email_send(HttpServletRequest request,
			HttpServletResponse response, String password, String email) {
		//成功提示页面
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		//根据邮件查询用户
		Map<String, Object> map = Maps.newHashMap();
		map.put("email", email);
		
		List<User> users = this.userService.queryPageList(map);
		//验证密码必须正确,并且该邮箱没有被其他人注册过
		if ((user.getPassword().equals(Md5Encrypt.md5(password).toLowerCase())) && (users.size() == 0)) {
			map.clear();
			users.clear();
			map.put("userName", user.getUserName());
			map.put("email", user.getEmail());
			
			//查询验证码
			List<VerifyCode> vcs = this.mobileverifycodeService.queryPageList(map);
			
			String code = UUID.randomUUID().toString().replaceAll("-", "");
			VerifyCode vc = null;
			if (vcs.size() == 1) {
				vc = (VerifyCode) vcs.get(0);
			} else {
				vc = new VerifyCode();
			}
			vc.setCode(code);
			vc.setUserName(user.getUserName());
			vc.setEmail(email);
			
			if (vcs.size() == 1) {
				this.mobileverifycodeService.updateById(vc);
			} else {
				this.mobileverifycodeService.saveEntity(vc);
			}
			//验证邮件链接内容
			String content = this.configService.getSysConfig().getTitle()
					+ "提醒您，请点击链接完成邮箱绑定." + CommUtil.getURL(request)
					+ "/buyer/account_email_save?code=" + code + "&email="
					+ email;
			this.msgTools.sendEmail(email, "绑定邮箱", content);
			mv.addObject("op_title", "请在邮箱中确认绑定");
		} else {
			//密码输入错误，邮箱修改失败
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			
			mv.addObject("op_title", "密码错误或者邮箱已存在，邮箱修改失败");
			
		}
		
		//邮箱修改成功跳转页面
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_email");
		return mv;
	}
	
	/**
	 * 邮箱修改保存
	 * @param request 请求
	 * @param response 响应
	 * @param code
	 * @param email
	 * @return
	 */
	@SecurityMapping(title = "邮箱修改保存", value = "/buyer/account_email_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_email_save" })
	public ModelAndView account_email_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String code, 
			String email) {
		//邮箱保存成功
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		//查询验证邮箱
		Map<String, Object> map = Maps.newHashMap();
		map.put("email", email);
		map.put("code", code);
		List<VerifyCode> vcs = this.mobileverifycodeService.queryPageList(map);
		//如果查询到就进行绑定
		if (vcs.size() == 1) {
			//绑定成功提示
			mv.addObject("op_title", "邮箱绑定成功");
			map.clear();
			map.put("email", email);
			List<User> users = this.userService.queryPageList(map);
			
			if (users.size() == 0) {
				map.clear();
				users.clear();
				map.put("userName", vcs.get(0).getUserName());
				users = this.userService.queryPageList(map);
				
				if (users.size() == 1) {
					((User) users.get(0)).setEmail(email);
					this.userService.updateById((User) users.get(0));
					this.mobileverifycodeService.deleteById(vcs.get(0).getId());
				} else {
					mv = new RedPigJModelAndView("error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					//绑定失败提示
					mv.addObject("op_title", "邮箱绑定失败");
				}
			} else {
				//绑定失败提示
				mv = new RedPigJModelAndView("error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "邮箱绑定失败");
			}
		} else {
			//绑定失败提示
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "邮箱绑定失败");
		}
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_email");
		return mv;
	}
	
	/**
	 * 图像修改
	 * @param request 请求
	 * @param response 响应
	 * @return
	 */
	@SecurityMapping(title = "图像修改", value = "/buyer/account_avatar*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_avatar" })
	public ModelAndView account_avatar(HttpServletRequest request,
			HttpServletResponse response) {
		//修改用户头像
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account_avatar.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//当前登陆用户
		mv.addObject("user", this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId()));
		
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}
	
	/**
	 * 图像更换
	 * @param request 请求
	 * @param response 响应
	 * @param value
	 * @param type
	 */
	@SuppressWarnings("rawtypes")
	@SecurityMapping(title = "图像更换", value = "/buyer/upload_avatar*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/upload_avatar" })
	public void upload_avatar(
			HttpServletRequest request,
			HttpServletResponse response, 
			String value, String type) {
		//修改头像是否成功
		Boolean flag = false;
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		if (user != null) {
			Accessory photo = new Accessory();
			//如果用户头像不为空
			if (user.getPhoto() != null) {
				photo = user.getPhoto();
			} else {
				//新建一个用户头像
				photo.setAddTime(new Date());
				photo.setWidth(132);
				photo.setHeight(132);
			}
			//如果是系统图片
			if (("sys".equals(type)) && (value != null) && (!"".equals(value))) {
				String[] strs = value.split("resources");
				String[] info = strs[1].split("/portrait/");
				photo.setName(info[1]);
				photo.setExt(DEFAULT_AVATAR_FILE_EXT);
				photo.setPath("resources" + info[0] + "/portrait");
				flag = true;
			} else if ("upload".equals(type)) {//如果是上传头像
				String uploadFilePath = this.configService.getSysConfig().getUploadFilePath();
				String saveFilePathName = request.getSession()
						.getServletContext().getRealPath("/")
						+ uploadFilePath + File.separator + "user_icon";
				
				Map m = Maps.newHashMap();
				try {
					m = CommUtil.saveFileToServer(request, "icon",saveFilePathName, "", null);
					
					if (m.get("fileName") != "") {
						photo.setName(CommUtil.null2String(m.get("fileName")));
						photo.setExt(CommUtil.null2String(m.get("mime")));
						photo.setPath(uploadFilePath + "/user_icon");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				flag = true;
			}
			if (user.getPhoto() == null) {
				//保存用户头像
				this.accessoryService.saveEntity(photo);
				//设置头像到用户上
				user.setPhoto(photo);
				//更新用户信息
				this.userService.updateById(user);
			} else {
				//更新用户信息
				this.accessoryService.updateById(photo);
			}
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(flag);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 手机号码修改
	 * @param request 请求
	 * @param response 响应
	 * @return
	 */
	@SecurityMapping(title = "手机号码修改", value = "/buyer/account_mobile*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_mobile" })
	public ModelAndView account_mobile(HttpServletRequest request,
			HttpServletResponse response) {
		//手机号码修改页面
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account_mobile.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		String ran = CommUtil.randomInt(8) + SecurityUserHolder.getCurrentUser().getId();
		
		request.getSession(false).setAttribute("SessionCode", ran);
		mv.addObject("SessionCode", ran);
		mv.addObject("url", CommUtil.getURL(request));
		return mv;
	}
	
	/**
	 * 手机号码保存
	 * @param request 请求
	 * @param response 响应
	 * @param mobile_verify_code
	 * @param mobile
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "手机号码保存", value = "/buyer/account_mobile_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_mobile_save" })
	public ModelAndView account_mobile_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mobile_verify_code,
			String mobile) throws Exception {
		//手机号码保存成功页面
		ModelAndView mv = new RedPigJModelAndView("success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		//手机验证码
		VerifyCode vc = this.mobileverifycodeService.getObjByProperty("mobile","=", mobile);
		
		if ((vc != null) && (vc.getCode().equalsIgnoreCase(mobile_verify_code))) {
			//设置用户手机
			user.setMobile(mobile);
			//更新用户信息
			this.userService.updateById(user);
			//删除手机验证码
			this.mobileverifycodeService.deleteById(vc.getId());
			//手机绑定成功提示
			mv.addObject("op_title", "手机绑定成功");
			//短信通知内容
			String content = "尊敬的"
					+ SecurityUserHolder.getCurrentUser().getUserName()
					+ "您好，您于" + CommUtil.formatLongDate(new Date())
					+ "绑定手机号成功。["
					+ this.configService.getSysConfig().getTitle() + "]";
			
			this.msgTools.sendSMS(user.getMobile(), content);
			
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/account");
		} else {
			//修改失败提示页面
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			//验证码错误、绑定失败
			mv.addObject("op_title", "验证码错误，手机绑定失败");
			
			mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_mobile");
			
		}
		return mv;
	}

	/**
	 * 手机短信发送
	 * 
	 * @param request 请求
	 * @param response 响应
	 * @param type
	 * @param mobile
	 * @param t 修改标识:first第一次
	 * @param SessionCode
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "手机短信发送", value = "/buyer/account_mobile_sms*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_mobile_sms" })
	public void account_mobile_sms(
			HttpServletRequest request,
			HttpServletResponse response,
			String type, String mobile, String t,String SessionCode)  {
		
		String SessionCode1 = CommUtil.null2String(request.getSession(false).getAttribute("SessionCode"));
		
		String ret = "100";
		if ((SessionCode1 != null) && (SessionCode1.equals(SessionCode))) {
			request.getSession(false).removeAttribute("SessionCode");
			if (type.equals("mobile_vetify_code")) {//如果是手机验证码
				String code = CommUtil.randomInt(4);
				String content = "";//短信内容
				if ("frist".equals(t)) {
					content =
					"尊敬的"
							+ SecurityUserHolder.getCurrentUser().getUserName()
							+ "您好，您在试图修改"
							+ this.configService.getSysConfig()
									.getWebsiteName() + "的支付密码，" + "手机验证码为："
							+ code + "。["
							+ this.configService.getSysConfig().getTitle()
							+ "]";
				} else {
					content =
					"尊敬的"
							+ SecurityUserHolder.getCurrentUser().getUserName()
							+ "您好，您在试图修改"
							+ this.configService.getSysConfig()
									.getWebsiteName() + "用户绑定手机，手机验证码为：" + code
							+ "。["
							+ this.configService.getSysConfig().getTitle()
							+ "]";
				}
				if (this.configService.getSysConfig().getSmsEnbale()) {//判断是否开启了短信通知
					boolean ret1 = this.msgTools.sendSMS(mobile, content);//开始短信通知
					if (ret1) {//验证是否发送成功
						VerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  mobile);//查询手机短信验证码
						if (mvc == null) {
							mvc = new VerifyCode();
						}
						mvc.setAddTime(new Date());
						mvc.setCode(code);
						mvc.setMobile(mobile);
						this.mobileverifycodeService.updateById(mvc);//更新短信验证码
					} else {
						ret = OPERATE_RESULT_CODE_SUCCESS;
					}
				} else {
					ret = "300";
				}
			}
			if (type.equals("find_mobile_verify_code")) {//如果是查找手机验证码
				String code = CommUtil.randomString(4).toUpperCase();
				//短信内容
				String content = "尊敬的"
						+ SecurityUserHolder.getCurrentUser().getUserName()
						+ "您好，您在试图找回账户的支付密码，" + "手机验证码为：" + code + "。["
						+ this.configService.getSysConfig().getTitle() + "]";
				
				if (this.configService.getSysConfig().getSmsEnbale()) {//判断是否开启短信通知
					boolean ret1 = this.msgTools.sendSMS(mobile, content);//短信发送
					if (ret1) {//判断是否发送成功
						User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
						//查询手机短信验证码
						VerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile","=", user.getMobile());
						if (mvc == null) {
							mvc = new VerifyCode();
						}
						mvc.setAddTime(new Date());
						mvc.setCode(code);
						mvc.setMobile(user.getMobile());
						//更新短信验证码
						this.mobileverifycodeService.updateById(mvc);
					} else {
						ret = "200";
					}
				} else {
					ret = "300";
				}
			}
		} else {
			ret = "200";
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
	 * 账号绑定
	 * @param request 请求
	 * @param response 响应
	 * @param error
	 * @return
	 */
	@SecurityMapping(title = "账号绑定", value = "/buyer/account_bind*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_bind" })
	public ModelAndView account_bind(
			HttpServletRequest request,
			HttpServletResponse response, String error) {
		//账号绑定页面
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account_bind.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		mv.addObject("user", user);
		mv.addObject("error", error);
		return mv;
	}
	
	/**
	 * 账号解除绑定
	 * @param request 请求 请求
	 * @param response 响应
	 * @param account
	 * @return
	 */
	@SecurityMapping(title = "账号解除绑定", value = "/buyer/account_bind_cancel*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_bind_cancel" })
	public String account_bind_cancel(
			HttpServletRequest request,
			HttpServletResponse response, 
			String account) {
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		//如果接触的是QQ,设置QQ绑定为null
		if (CommUtil.null2String(account).equals("qq")) {
			user.setQq_openid(null);
		}
		//如果接触的是sina,sina绑定为null
		if (CommUtil.null2String(account).equals("sina")) {
			user.setSina_openid(null);
		}
		//如果接触的是wechat,wechat绑定为null
		if (CommUtil.null2String(account).equals("wechat")) {
			user.setWeixin_unionID(null);
		}
		//更新用户信息
		this.userService.updateById(user);
		return "redirect:account_bind";//跳转到账户绑定页面
	}
	
	/**
	 * 验证验证码和原密码
	 * @param request 请求
	 * @param response 响应
	 * @param old 原始密码
	 * @param code 验证码
	 */
	@SecurityMapping(title = "验证验证码和原密码", value = "/verity_old_password_code*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/verity_old_password_code" })
	public void old_password_code(
			HttpServletRequest request,
			HttpServletResponse response, 
			String old, String code) {
		
		String ret = "";
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		//如果支付密码为空返回nothing
		if (user.getPay_password() == null) {
			ret = "nothing";
		} else if (user.getPay_password().equals(Md5Encrypt.md5(old))) {//如果支付密码和原密码一样
			ret = "nothing";
		} else {
			ret = "old";
		}
		response.setCharacterEncoding("utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/plain");
		try {
			response.getWriter().print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 第一次设置支付密码
	 * @param request 请求
	 * @param response 响应
	 * @param old
	 * @param code
	 */
	@SecurityMapping(title = "第一次设置支付密码", value = "/frist_set_pay_password*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/frist_set_pay_password" })
	public void frist_set(
			HttpServletRequest request,
			HttpServletResponse response, 
			String old, String code) {
		
		String ret = "";
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		//用户手机不为空
		if (user.getMobile() != null) {
			//查询手机验证码
			VerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  user.getMobile());
			//支付密码为空
			if (user.getPay_password() == null) {
				if ((mvc != null) && (mvc.getCode().equalsIgnoreCase(code))) {
					ret = "nothing";
				} else {
					ret = "code";
				}
			}
		} else {
			ret = "tel";
		}
		response.setCharacterEncoding("utf-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("text/plain");
		try {
			response.getWriter().print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 修改支付密码
	 * @param request 请求
	 * @param response	响应
	 * @param new_password 新密码
	 * @param update_new_password 更新密码
	 * @param s_code 原来的code
	 * @param code 现有code
	 * @param p
	 * @param phone 手机号
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("null")
	@SecurityMapping(title = "修改支付密码", value = "/buyer/account_pay_password_save*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_pay_password_save" })
	public ModelAndView account_pay_password_saveEntity(
			HttpServletRequest request,
			HttpServletResponse response, 
			String new_password,
			String update_new_password, 
			String s_code, String code, String p,
			String phone) throws Exception {
		ModelAndView mv = null;
		
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		String j_mobile = user.getMobile();
		
		if (((!"".equals(s_code)) && (s_code != null))
				|| ((!"".equals(code)) && (code != null))) {
			//如果用户手机号为空
			if ((user.getMobile() == null) || ("".equals(user.getMobile()))) {
				j_mobile = phone;
			}
			//根据手机号查询验证码
			VerifyCode vc = this.mobileverifycodeService.getObjByProperty("mobile","=",  j_mobile);
			//验证码不为空
			if (vc != null) {
				//输入的验证码和查询的验证码一致
				if ((vc.getCode().equalsIgnoreCase(s_code)) || (vc.getCode().equalsIgnoreCase(code))) {
					if ((user.getMobile() == null) || ("".equals(user.getMobile()))) {
						user.setMobile(phone);
						//删除验证码
						this.mobileverifycodeService.deleteById(vc.getId());
						//手机绑定成功提示
						mv.addObject("op_title", "手机绑定成功");
						//短信内容
						String content = "尊敬的"
								+ SecurityUserHolder.getCurrentUser()
										.getUserName() + "您好，您于"
								+ CommUtil.formatLongDate(new Date())
								+ "绑定手机号成功。["
								+ this.configService.getSysConfig().getTitle()
								+ "]";
						//发送短信
						this.msgTools.sendSMS(user.getMobile(), content);
					}
					//如果设置的新密码不为空就重新新的支付密码
					if ((!"".equals(new_password)) && (new_password != null)) {
						user.setPay_password(Md5Encrypt.md5(new_password));
					}
					//如果更新的密码不为空就设置新的支付密码
					if ((!"".equals(update_new_password)) && (update_new_password != null)) {
						user.setPay_password(Md5Encrypt.md5(update_new_password));
					}
					//更新用户
					this.userService.updateById(user);
					
					//成功提示
					mv = new RedPigJModelAndView("/success.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					//支付密码修改成功
					mv.addObject("op_title", "支付密码修改成功");
				} else {
					//支付密码修改失败
					mv = new RedPigJModelAndView("/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "支付密码修改失败");
				}
			} else {
				//违规操作
				mv = new RedPigJModelAndView("/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "不要做违规操作，超过一定次数将冻结预存款");
			}
		} else if ("p".equals(p)) {
			//设置新的支付密码
			user.setPay_password(Md5Encrypt.md5(new_password));
			this.userService.updateById(user);
			//支付密码修改成功
			mv = new RedPigJModelAndView("/success.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			//成功提示页面
			mv.addObject("op_title", "支付密码修改成功");
		} else {
			//违规操作提示
			mv = new RedPigJModelAndView("/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			//违规操作提示内容
			mv.addObject("op_title", "不要做违规操作，超过一定次数将冻结预存款");
		}
		//提示之后跳转的目标页面
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/account_security");
		return mv;
	}
	
	/**
	 * 验证手机密码
	 * @param request 请求
	 * @param response 响应
	 * @param mobile_code 手机验证码
	 */
	@SecurityMapping(title = "验证手机密码", value = "/buyer/forget_password_verify_mobile*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/forget_password_verify_mobile" })
	public void forget_password_verify_mobile(
			HttpServletRequest request,
			HttpServletResponse response, 
			String mobile_code) {
		//返回信息
		String ret = "";
		//登陆的当前用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		//手机验证码
		VerifyCode vc = this.mobileverifycodeService.getObjByProperty("mobile","=",  user.getMobile());
		//手机验证码判断
		if ((!"".equals(mobile_code)) && (mobile_code != null)) {
			if ((vc != null) && (mobile_code.equalsIgnoreCase(vc.getCode()))) {
				//验证成功
				ret = "ok";
			}
		} else {
			//验证失败
			ret = "no";
		}
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("status", ret);
		map.put("code", vc.getCode());
		String new_map = JSON.toJSONString(map);
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(new_map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 账户安全
	 * @param request 请求
	 * @param response 响应
	 * @return
	 */
	@SecurityMapping(title = "账户安全", value = "/buyer/account_security*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping({ "/buyer/account_security" })
	public ModelAndView account_security(
			HttpServletRequest request,
			HttpServletResponse response) {
		//账号安全页面
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/account_security.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		//当前登陆用户
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		//当前用户不为空
		if (user != null) {
			int safe = 0;//安全评分
			if ((user.getEmail() != null) && (!"".equals(user.getEmail()))) {
				safe += 10;
			}
			
			if ((user.getMobile() != null) && (!"".equals(user.getMobile()))) {
				safe += 10;
			}
			
			if ((user.getPay_password() != null)
					&& (!"".equals(user.getPassword()))) {
				safe += 10;
			}
			
			mv.addObject("safe", safe);
			mv.addObject("user", user);
		} else {
			//操作失败页面
			mv = new RedPigJModelAndView("error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			//操作失败提示
			mv.addObject("op_title", "操作失败，请重新登录");
			//跳转的目标页
			mv.addObject("url", CommUtil.getURL(request) + "/user/login");
		}
		return mv;
	}
}
