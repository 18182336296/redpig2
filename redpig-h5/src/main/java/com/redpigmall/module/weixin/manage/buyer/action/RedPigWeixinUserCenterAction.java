package com.redpigmall.module.weixin.manage.buyer.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.qrcode.QRCodeUtil;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Favorite;
import com.redpigmall.domain.FootPoint;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Message;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VerifyCode;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.module.weixin.view.action.base.BaseAction;



/**
 * 
 * 
 * <p>
 * Title: RedPigWeixinUserCenterAction
 * </p>
 * 
 * <p>
 * Description: 移动端用户中心
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
 * @date 2014年8月20日
 * 
 * @version redpigmall_b2b2c_2015
 */
@SuppressWarnings({"unchecked"})
@Controller
public class RedPigWeixinUserCenterAction extends BaseAction{
	
	/**
	 * 手机客户端商城首页
	 * 
	 * @param request
	 * @param response
	 * @param store_id
	 * @return
	 */
	@SecurityMapping(title = "用户中心", value = "/buyer/center*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/center" })
	public ModelAndView center(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/user_index.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User u = SecurityUserHolder.getCurrentUser();
		if ((u != null) && (!u.equals(""))) {
			User user = this.userService.selectByPrimaryKey(SecurityUserHolder
					.getCurrentUser().getId());
			Map<String,Object> foot_params = Maps.newHashMap();
			foot_params.put("fp_user_id", user.getId());
			
			List<FootPoint> fps = this.footPointService.queryPageList(foot_params);
			
			mv.addObject("objs", fps);
			mv.addObject("footsize", Integer.valueOf(fps.size()));
			Map<String, Object> params = Maps.newHashMap();
			params.put("user_id", user.getId());
			params.put("status", Integer.valueOf(0));
			
			mv.addObject("couponInfos",Integer.valueOf(this.couponInfoService.selectCount(params)));
			
			params.clear();
			params.put("status", Integer.valueOf(10));
			params.put("user_id", user.getId().toString());
			mv.addObject("orders_10",Integer.valueOf(this.orderformService.selectCount(params)));
			
			mv.addObject("integralViewTools", this.integralViewTools);
			params.clear();
			params.put("type", Integer.valueOf(0));
			params.put("user_id", user.getId());
			
			List<Favorite> favorite_goods = this.favoriteService.queryPageList(params);
			
			mv.addObject("favorite_goods", favorite_goods);
			params.clear();
			params.put("type", Integer.valueOf(1));
			params.put("user_id", user.getId());
			
			mv.addObject("favorite_store",this.favoriteService.queryPageList(params));
			
			params.clear();
			params.put("order_status", Integer.valueOf(10));
			params.put("user_id", user.getId().toString());
			params.put("order_main", Integer.valueOf(1));
			
			List<OrderForm> order_nopays = this.orderformService.queryPageList(params);
			
			String order_nopay = "";
			if (CommUtil.null2Int(order_nopays.size()) > 9) {
				order_nopay = "9+";
			} else {
				order_nopay = order_nopays.size() + "";
			}
			
			mv.addObject("order_nopay", order_nopay);
			params.clear();
			params.put("order_status", Integer.valueOf(20));
			params.put("user_id", user.getId().toString());
			params.put("order_main", Integer.valueOf(1));

			List<OrderForm> order_noships = this.orderformService.queryPageList(params);
			
			String order_noship = "";
			if (CommUtil.null2Int(order_noships.size()) > 9) {
				order_noship = "9+";
			} else {
				order_noship = order_noships.size() + "";
			}
			
			mv.addObject("order_noship", order_noship);
			params.clear();
			params.put("order_status1", Integer.valueOf(30));
			params.put("order_status2", Integer.valueOf(35));
			params.put("user_id", user.getId().toString());
			params.put("order_main", Integer.valueOf(1));
			
			List<OrderForm> order_notakes = this.orderformService.queryPageList(params);
			
			String order_notake = "";
			if (CommUtil.null2Int(order_notakes.size()) > 9) {
				order_notake = "9+";
			} else {
				order_notake = order_notakes.size() + "";
			}
			mv.addObject("order_notake", order_notake);
			
			params.clear();
			params.put("status", Integer.valueOf(0));
			params.put("user_id", user.getId());
			params.put("parent", -1);
			
			List<Message> msgs  = this.messageService.queryPageList(params);
			
			mv.addObject("msg_size", msgs.get(0));
			mv.addObject("user", user);
			mv.addObject("integralViewTools", this.integralViewTools);
		}
		return mv;
	}
	
	/**
	 * 个人二维码
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@SecurityMapping(title = "个人二维码", value = "/buyer/datum*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/myQrcode" })
	public ModelAndView myQrcode(HttpServletRequest request,
			HttpServletResponse response,String type) throws Exception {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/myQrcode.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		
		User user = SecurityUserHolder.getCurrentUser();
		user = this.userService.selectByPrimaryKey(user.getId());
		if(user.getDirect_selling_qr_path() == null || "create".equals(type)) {
			Accessory photo = user.getPhoto();
			if(photo == null) {
				photo = this.configService.getSysConfig().getQr_logo();
			}
			String logoPath = this.configService.getSysConfig().getImageWebServer()  
					+ photo.getPath()
					+ File.separator + photo.getName();
			
			String uploadFilePath = this.configService.getSysConfig()
					.getUploadFilePath();
			
			String destPath = uploadFilePath + File.separator + "direct_selling_qr";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			String imageId = UUID.randomUUID().toString();
			destPath = destPath + File.separator+imageId+"_qr.jpg";
			
	        String web_url = this.configService.getSysConfig().getH5Url();
	        
	        String content = web_url + "/" + "register" +"?directSellingParent_id="+user.getId();
	        QRCodeUtil.encode(content, logoPath, destPath,true);
	        
	        String srcDir =uploadFilePath + File.separator + "direct_selling_qr";
			
			CommUtil.uploadToSFTPServer(srcDir, imageId + "_qr.jpg");
	        
			user.setDirect_selling_qr_path(uploadFilePath + "/" + "direct_selling_qr" +File.separator+ imageId + "_qr.jpg");
			
			this.userService.updateById(user);
			
			user = this.userService.selectByPrimaryKey(user.getId());
			
			SecurityUserHolder.setCurrentUser(user);
		}
		mv.addObject("user",user);
		
		return mv;
	}
	
	/**
	 * 已有账号绑定
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "已有账号绑定", value = "/buyer/datum*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/datum" })
	public ModelAndView datum(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/datum_bind.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 用户中心账户安全
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "用户中心账户安全", value = "/buyer/account_safe*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/account_safe" })
	public ModelAndView account_safe(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/account_safe.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 用户中心修改登陆密码
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "用户中心修改登陆密码", value = "/buyer/edit_password*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/edit_password" })
	public ModelAndView edit_password(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/edit_password.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 用户中心修改密码保存
	 * @param request
	 * @param response
	 * @param password
	 * @param new_password
	 * @param new_password_confirm
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "用户中心修改密码保存", value = "/buyer/edit_password_save*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/edit_password_save" })
	public ModelAndView edit_password_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String password, String new_password,
			String new_password_confirm) throws UnsupportedEncodingException {
		ModelAndView mv = new RedPigJModelAndView("weixin/error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((!"".equals(password)) && (password != null)) {
			if (user.getPassword().equals(Md5Encrypt.md5(password))) {
				if ((!"".equals(new_password)) && (new_password != null)
						&& (!"".equals(new_password_confirm))
						&& (new_password_confirm != null)) {
					if (new_password.equals(new_password_confirm)) {
						if (!Md5Encrypt.md5(new_password).equals(
								Md5Encrypt.md5(user.getPassword()))) {
							user.setPassword(Md5Encrypt.md5(new_password));
							this.userService.updateById(user);
							boolean ret = true;
							
							if (ret) {
								String content = "尊敬的"
										+ SecurityUserHolder.getCurrentUser()
												.getUserName()
										+ "您好，您于"
										+ CommUtil.formatLongDate(new Date())
										+ "修改密码成功，新密码为："
										+ new_password
										+ ",请妥善保管。["
										+ this.configService.getSysConfig()
												.getTitle() + "]";
								this.msgTools
										.sendSMS(user.getMobile(), content);
							}
							mv = new RedPigJModelAndView("weixin/success.html",
									this.configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "修改密码成功！");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/redpigmall_logout");
						} else {
							mv.addObject("op_title", "新密码不能与原始密码相同！");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/buyer/edit_password");
						}
					} else {
						mv.addObject("op_title", "两次新密码输入不相同！");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/edit_password");
					}
				} else {
					mv.addObject("op_title", "新密码不能为空！");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/edit_password");
				}
			} else {
				mv.addObject("op_title", "原始密码错误！");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/edit_password");
			}
		} else {
			mv.addObject("op_title", "原始密码不能为空！");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/edit_password");
		}
		return mv;
	}
	
	/**
	 * 用户中心支付密码修改
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "用户中心支付密码修改", value = "/buyer/account_pay_password*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/account_pay_password" })
	public ModelAndView account_pay_password(HttpServletRequest request,
			HttpServletResponse response) {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/account_pay_password.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("user", user);
		if ((user.getMobile() != null) && (!user.getMobile().equals(""))) {
			String show_tel = user.getMobile().substring(0, 3) + "*****"
					+ user.getMobile().substring(8, 11);
			mv.addObject("show_tel", show_tel);
			mv.addObject("first", Integer.valueOf(-1));
		} else {
			mv.addObject("first", "1");
		}
		return mv;
	}
	
	/**
	 * 修改支付密码提交
	 * @param request
	 * @param response
	 * @param pay_password
	 * @param pay_password_confirm
	 * @param phone_number
	 * @param code
	 * @param t
	 * @return
	 */
	@SecurityMapping(title = "修改支付密码提交", value = "/buyer/account_pay_password_save*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/account_pay_password_save" })
	public ModelAndView account_pay_password_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String pay_password,
			String pay_password_confirm, String phone_number, String code,
			String t) {
		ModelAndView mv = null;
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((!"".equals(code)) && (code != null)) {
			VerifyCode vc = null;
			if ("1".equals(t)) {
				vc = this.mobileverifycodeService.getObjByProperty("mobile","=",  phone_number);
			} else if ("-1".equals(t)) {
				vc = this.mobileverifycodeService.getObjByProperty("mobile","=",  user.getMobile());
			}
			if (vc != null) {
				if (code.equalsIgnoreCase(vc.getCode())) {
					if ((!"".equals(pay_password)) && (pay_password != null)
							&& (!"".equals(pay_password_confirm))
							&& (pay_password_confirm != null)) {
						if (pay_password.equals(pay_password_confirm)) {
							if ("1".equals(t)) {
								user.setMobile(phone_number);
							}
						} else {
							mv = new RedPigJModelAndView("weixin/error.html",
									this.configService.getSysConfig(),
									this.userConfigService.getUserConfig(), 1,
									request, response);
							mv.addObject("op_title", "两次密码输入不相同");
							mv.addObject("url", CommUtil.getURL(request)
									+ "/buyer/account_pay_password");
							return mv;
						}
					}
				} else {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "验证码错误，支付密码修改失败");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/account_pay_password");
					return mv;
				}
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "不要做违规操作，超过一定次数将冻结预存款");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/account_pay_password");
				return mv;
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "验证码不能为空，支付密码修改失败");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/account_pay_password");
			return mv;
		}
		user.setPay_password(Md5Encrypt.md5(pay_password));
		this.userService.updateById(user);
		mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "支付密码修改成功");
		mv.addObject("url", CommUtil.getURL(request) + "/index");
		return mv;
	}
	
	/**
	 * 手机短信发送
	 * @param request
	 * @param response
	 * @param type
	 * @param mobile
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "手机短信发送", value = "wap/buyer/account_mobile_sms*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "wap/buyer/account_mobile_sms" })
	public void account_mobile_sms(HttpServletRequest request,
			HttpServletResponse response, String type, String mobile)
			throws UnsupportedEncodingException {
		String ret = "100";
		if (type.equals("pay_mobile_vetify_code")) {
			String code = CommUtil.randomInt(4);
			String content = "";
			content = "尊敬的" + SecurityUserHolder.getCurrentUser().getUserName()
					+ "您好，您在试图修改"
					+ this.configService.getSysConfig().getWebsiteName()
					+ "的支付密码，" + "手机验证码为：" + code + "。["
					+ this.configService.getSysConfig().getTitle() + "]";
			if (this.configService.getSysConfig().getSmsEnbale()) {
				boolean ret_op = this.msgTools.sendSMS(mobile, content);
				if (ret_op) {
					VerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  mobile);
					if (mvc == null) {
						mvc = new VerifyCode();
					}
					mvc.setAddTime(new Date());
					mvc.setCode(code);

					mvc.setMobile(mobile);
					this.mobileverifycodeService.updateById(mvc);
				} else {
					ret = "200";
				}
			} else {
				ret = "300";
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
	}
	
	/**
	 * 修改绑定手机
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "修改绑定手机", value = "/buyer/account_mobile_bind*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/account_mobile_bind" })
	public ModelAndView account_mobile_password(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/account_mobile_bind.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((user.getMobile() != null) && (!user.getMobile().equals(""))) {
			String show_tel = user.getMobile().substring(0, 3) + "*****"
					+ user.getMobile().substring(8, 11);
			mv.addObject("show_tel", show_tel);
			mv.addObject("first", "-1");
		} else {
			mv.addObject("first", "1");
		}
		return mv;
	}
	
	/**
	 * 手机号码提交
	 * @param request
	 * @param response
	 * @param code
	 * @param new_mobile
	 * @param t
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "手机号码提交", value = "wap/buyer/account_mobile_bind_save*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "wap/buyer/account_mobile_bind_save" })
	public ModelAndView account_mobile_bind_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String code, String new_mobile,
			String t) throws Exception {
		ModelAndView mv = new RedPigJModelAndView("weixin/success.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		if ((!"".equals(code)) && (code != null)) {
			VerifyCode mvc = null;
			if ("1".equals(t)) {
				mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  new_mobile);
			} else if ("-1".equals(t)) {
				mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  user.getMobile());
			}
			if (mvc != null) {
				if (mvc.getCode().equalsIgnoreCase(code)) {
					if (!new_mobile.equals(user.getMobile())) {
						user.setMobile(new_mobile);
						this.userService.updateById(user);
						this.mobileverifycodeService.deleteById(mvc.getId());
						mv.addObject("op_title", "手机绑定成功");
						String content = "尊敬的"
								+ SecurityUserHolder.getCurrentUser()
										.getUserName() + "您好，您于"
								+ CommUtil.formatLongDate(new Date())
								+ "绑定手机号成功。["
								+ this.configService.getSysConfig().getTitle()
								+ "]";
						this.msgTools.sendSMS(user.getMobile(), content);
						mv.addObject("op_title", "手机绑定成功");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/account_safe");
					} else {
						mv = new RedPigJModelAndView("weixin/error.html",
								this.configService.getSysConfig(),
								this.userConfigService.getUserConfig(), 1,
								request, response);
						mv.addObject("op_title", "新旧输入电话号码不能相同，手机绑定失败");
						mv.addObject("url", CommUtil.getURL(request)
								+ "/buyer/account_mobile_bind");
					}
				} else {
					mv = new RedPigJModelAndView("weixin/error.html",
							this.configService.getSysConfig(),
							this.userConfigService.getUserConfig(), 1, request,
							response);
					mv.addObject("op_title", "验证码错误，手机绑定失败");
					mv.addObject("url", CommUtil.getURL(request)
							+ "/buyer/account_mobile_bind");
				}
			} else {
				mv = new RedPigJModelAndView("weixin/error.html",
						this.configService.getSysConfig(),
						this.userConfigService.getUserConfig(), 1, request,
						response);
				mv.addObject("op_title", "请填写正确的验证码和手机号码，手机绑定失败");
				mv.addObject("url", CommUtil.getURL(request)
						+ "/buyer/account_mobile_bind");
			}
		} else {
			mv = new RedPigJModelAndView("weixin/error.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 1, request,
					response);
			mv.addObject("op_title", "验证码不能为空，支付密码修改失败");
			mv.addObject("url", CommUtil.getURL(request)
					+ "/buyer/account_mobile_bind");
			return mv;
		}
		return mv;
	}
	
	/**
	 * 手机绑定短信发送
	 * @param request
	 * @param response
	 * @param new_mobile
	 * @param type
	 * @param t
	 * @throws UnsupportedEncodingException
	 */
	@SecurityMapping(title = "手机绑定短信发送", value = "wap/buyer/account_mobile_bind_sms*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "wap/buyer/account_mobile_bind_sms" })
	public void account_mobile_bind_sms(HttpServletRequest request,
			HttpServletResponse response, String new_mobile, String type,
			String t) throws UnsupportedEncodingException {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		String ret = "100";
		if (type.equals("mobile_bind_vetify_code")) {
			String code = CommUtil.randomInt(4);
			String content = "";
			content = "尊敬的" + SecurityUserHolder.getCurrentUser().getUserName()
					+ "您好，您在试图修改"
					+ this.configService.getSysConfig().getWebsiteName()
					+ "的绑定手机号码，" + "手机验证码为：" + code + "。["
					+ this.configService.getSysConfig().getTitle() + "]";
			if (this.configService.getSysConfig().getSmsEnbale()) {
				boolean ret_op;
				if ("1".equals(t)) {
					ret_op = this.msgTools.sendSMS(new_mobile, content);
				} else {
					if ("-1".equals(t)) {
						ret_op = this.msgTools.sendSMS(user.getMobile(),
								content);
					} else {
						ret_op = false;
					}
				}
				if (ret_op) {
					VerifyCode mvc = null;
					if ("1".equals(t)) {
						mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  new_mobile);
					} else if ("-1".equals(t)) {
						mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  user.getMobile());
					}
					if (mvc == null) {
						mvc = new VerifyCode();
					}
					mvc.setAddTime(new Date());
					mvc.setCode(code);
					if ("1".equals(t)) {
						mvc.setMobile(new_mobile);
					} else if ("-1".equals(t)) {
						mvc.setMobile(user.getMobile());
					}
					this.mobileverifycodeService.updateById(mvc);
				} else {
					ret = "200";
				}
			} else {
				ret = "300";
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
	}
	
	/**
	 * 已有账号绑定保存
	 * @param request
	 * @param response
	 * @param userName
	 * @param password
	 * @param mobile_verify_code
	 * @param mobile
	 * @throws HttpException
	 * @throws IOException
	 */
	@SecurityMapping(title = "已有账号绑定保存", value = "/buyer/datum2*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/datum2" })
	public void datum2(HttpServletRequest request,
			HttpServletResponse response, String userName, String password,
			String mobile_verify_code, String mobile) throws HttpException,
			IOException {
		VerifyCode mvc = this.mobileverifycodeService.getObjByProperty("mobile","=",  mobile);
		String passwd = Md5Encrypt.md5(password).toLowerCase();
		Map<String, Object> map = Maps.newHashMap();
		map.put("userName", userName);
		map.put("passwd", passwd);
		
		List<User> users = this.userService.queryPageList(map);
		
		if ((mvc != null)
				&& (mvc.getCode().equalsIgnoreCase(mobile_verify_code))
				&& (users.size() > 0)) {
			User bind_user = (User) users.get(0);
			if (CommUtil.null2String(bind_user.getOpenId()).equals("")) {
				User current_user = this.userService
						.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
				if (current_user != null) {
					bind_user.setOpenId(current_user.getOpenId());
					bind_user.setUserMark(null);
					this.userService.updateById(bind_user);
					Map<String,Object> json = Maps.newHashMap();
					json.put("login", Boolean.valueOf(true));
					json.put("userName", userName);
					json.put("passwd", passwd);
					json.put("userId", current_user.getId());
					request.getSession(false).setAttribute("weixin_bind",
							JSON.toJSONString(json));
					response.sendRedirect(CommUtil.getURL(request)
							+ "/redpigmall_logout");
				}
			} else {
				response.sendRedirect(CommUtil.getURL(request)
						+ "/buyer/datum_error");
			}
		} else {
			response.sendRedirect(CommUtil.getURL(request)
					+ "/buyer/datum_error");
		}
	}
	
	/**
	 * 已有账号绑定保存错误提示
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "已有账号绑定保存错误提示", value = "/buyer/datum_error*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/datum_error" })
	public ModelAndView datum_error(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("/error.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		mv.addObject("op_title", "用户名或验证码输入错误！");
		mv.addObject("url", CommUtil.getURL(request) + "/buyer/center");
		return mv;
	}
	
	/**
	 * 用户优惠券
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "用户优惠券", value = "/buyer/coupon*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/coupon" })
	public ModelAndView buyer_coupon(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/coupon.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		if (CommUtil.null2Int(currentPage) > 1) {
			mv = new RedPigJModelAndView(
					"user/default/usercenter/weixin/coupon_data.html",
					this.configService.getSysConfig(),
					this.userConfigService.getUserConfig(), 0, request,
					response);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder.getCurrentUser().getId());
		
		if (user != null) {
			maps.put("user_id", user.getId());
		}
		
		IPageList pList = this.couponInfoService.list(maps);
		CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
		return mv;
	}
	
	/**
	 * 移动端商品收藏
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "移动端商品收藏", value = "/buyer/favorite*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/favorite" })
	public ModelAndView favorite(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/favorite.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("type", 0);
        
		maps.put("user_id",SecurityUserHolder.getCurrentUser().getId());
		
		IPageList pList = this.favoriteService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite.html", "",
				"", pList, mv);
		mv.addObject("userTools", this.userTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	
	
	/**
	 * 移动端商品收藏追加数据
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "移动端商品收藏追加数据", value = "/buyer/favorite_data*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/favorite_data" })
	public ModelAndView favorite_data(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/favorite_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("type", 1);
        maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		IPageList pList = this.favoriteService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite.html", "",
				"", pList, mv);
		mv.addObject("userTools", this.userTools);
		mv.addObject("goodsViewTools", this.goodsViewTools);
		return mv;
	}
	
	/**
	 * 移动端店铺收藏
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "移动端店铺收藏", value = "/buyer/favorite_store*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/favorite_store" })
	public ModelAndView favorite_store(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/favorite_store.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("type", 1);
        maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		IPageList pList = this.favoriteService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_store.html",
				"", params, pList, mv);
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("weixinStoreViewTools", this.weixinStoreViewTools);
		return mv;
	}
	
	/**
	 * 移动端店铺收藏追加数据
	 * @param request
	 * @param response
	 * @param currentPage
	 * @return
	 */
	@SecurityMapping(title = "移动端店铺收藏追加数据", value = "/buyer/favorite_store_data*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/favorite_store_data" })
	public ModelAndView favorite_store_data(HttpServletRequest request,
			HttpServletResponse response, String currentPage) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/favorite_store_data.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String url = this.configService.getSysConfig().getAddress();
		if ((url == null) || (url.equals(""))) {
			url = CommUtil.getURL(request);
		}
		String params = "";
		Map<String,Object> maps= this.redPigQueryTools.getParams(currentPage,"addTime", "desc");
        maps.put("type", 1);
        maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
		
		IPageList pList = this.favoriteService.list(maps);
		CommUtil.saveIPageList2ModelAndView(url + "/buyer/favorite_store.html",
				"", params, pList, mv);
		mv.addObject("storeViewTools", this.storeViewTools);
		mv.addObject("weixinStoreViewTools", this.weixinStoreViewTools);
		return mv;
	}
	
	/**
	 * 移动端商品取消收藏
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "移动端商品取消收藏", value = "/buyer/favorite_del*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/favorite_del" })
	public String collecttion_goods_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Favorite favorite = this.favoriteService.selectByPrimaryKey(CommUtil
						.null2Long(id));
				if (favorite.getGoods_id() != null) {
					String goods_lucene_path = ClusterSyncTools
							.getClusterRoot()
							+ File.separator
							+ "luence"
							+ File.separator + "goods";
					File file = new File(goods_lucene_path);
					if (!file.exists()) {
						CommUtil.createFolder(goods_lucene_path);
					}
					
					RedPigLuceneUtil.setIndex_path(goods_lucene_path);
					Goods goods = this.goodsService.selectByPrimaryKey(favorite
							.getGoods_id());

					goods.setGoods_collect(goods.getGoods_collect() - 1);
					this.goodsService.updateById(goods);
					this.favoriteService.deleteById(CommUtil.null2Long(id));
				}
			}
		}
		return "redirect:/buyer/favorite";
	}
	
	/**
	 * 移动端店铺取消收藏
	 * @param request
	 * @param response
	 * @param currentPage
	 * @param mulitId
	 * @return
	 */
	@SecurityMapping(title = "移动端店铺取消收藏", value = "/buyer/favorite_store_del*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/favorite_store_del" })
	public String collecttion_store_del(HttpServletRequest request,
			HttpServletResponse response, String currentPage, String mulitId) {
		String[] ids = mulitId.split(",");
		for (String id : ids) {
			if (!id.equals("")) {
				Favorite favorite = this.favoriteService.selectByPrimaryKey(CommUtil
						.null2Long(id));

				Store store = this.storeService.selectByPrimaryKey(favorite
						.getStore_id());
				store.setFavorite_count(store.getFavorite_count() - 1);
				this.storeService.updateById(store);
				this.favoriteService.deleteById(CommUtil.null2Long(id));
			}
		}
		return "redirect:/buyer/favorite_store";
	}
	
	/**
	 * 用户消息
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "用户消息", value = "/buyer/message_list*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/message_list" })
	public ModelAndView message_list(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/message_list.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		long user_id = SecurityUserHolder.getCurrentUser().getId().longValue();
		Map<String, Object> map = Maps.newHashMap();
		map.put("toUser_id", Long.valueOf(user_id));
		map.put("orderBy", "status,addTime");
		map.put("orderType", "desc");
		
		List<Message> messages = this.messageService.queryPageList(map);
		
		mv.addObject("objs", messages);
		return mv;
	}
	
	/**
	 * 用户消息查看
	 * @param request
	 * @param response
	 * @param id
	 */
	@SecurityMapping(title = "用户消息查看", value = "/buyer/message_info*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/message_info" })
	public void message_info(HttpServletRequest request,
			HttpServletResponse response, String id) {
		Message obj = this.messageService.selectByPrimaryKey(CommUtil.null2Long(id));
		if (obj != null) {
			if (obj.getToUser().getId()
					.equals(SecurityUserHolder.getCurrentUser().getId())) {
				obj.setStatus(1);
				this.messageService.updateById(obj);
			}
			if (obj.getFromUser() != null) {
				if (obj.getFromUser().getId()
						.equals(SecurityUserHolder.getCurrentUser().getId())) {
					obj.setReply_status(0);
					this.messageService.updateById(obj);
				}
			}
		}
	}
	
	/**
	 * 用户服务中心
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "用户服务中心", value = "/buyer/service_center*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/service_center" })
	public ModelAndView service_center(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/service_center.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		return mv;
	}
	
	/**
	 * 用户中心完善资料
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "用户中心完善资料", value = "/buyer/account*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/account" })
	public ModelAndView account(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView(
				"user/default/usercenter/weixin/account_mobile.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		long user_id = SecurityUserHolder.getCurrentUser().getId().longValue();
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", Long.valueOf(user_id));
		
		List<User> User = this.userService.queryPageList(map);
		
		mv.addObject("obj", User.get(0));
		mv.addObject("imageFilesize", Integer.valueOf(this.configService.getSysConfig().getImageFilesize()));
		return mv;
	}
	
	/**
	 * 用户中资料保存
	 * @param request
	 * @param response
	 * @param USER_AGE
	 * @param card
	 * @param userName
	 * @param sex
	 * @return
	 * @throws Exception
	 */
	@SecurityMapping(title = "用户中资料保存", value = "/buyer/account_save*", rtype = "buyer", rname = "移动端用户中心", rcode = "wap_user_center", rgroup = "移动端用户中心")
	@RequestMapping({ "/buyer/account_save" })
	public String account_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String USER_AGE, String card,
			String userName, String sex) throws Exception {
		User user = this.userService.selectByPrimaryKey(SecurityUserHolder
				.getCurrentUser().getId());
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		String saveFilePathName = request.getSession().getServletContext()
				.getRealPath("/")
				+ uploadFilePath;
		Map<String, Object> map = Maps.newHashMap();
		try {
			map = CommUtil.saveFileToServer(request, "photo", saveFilePathName,
					"", null);
			if (map.get("fileName") != "") {
				Accessory photo = new Accessory();
				photo.setName((String) map.get("fileName"));
				photo.setExt("." + (String) map.get("mime"));
				BigDecimal bd = new BigDecimal(CommUtil.null2String(map
						.get("fileSize")));
				photo.setSize(bd);
				photo.setPath(uploadFilePath);
				photo.setWidth(((Integer) map.get("width")).intValue());
				photo.setHeight(((Integer) map.get("height")).intValue());
				photo.setAddTime(new Date());
				this.accessoryService.saveEntity(photo);
				user.setPhoto(photo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if ((userName != null) && (!userName.equals(""))) {
			user.setTrueName(userName);
		}
		if ((USER_AGE != null) && (!USER_AGE.equals(""))) {
			user.setBirthday(CommUtil.formatDate(USER_AGE));
		}
		if ((card != null) && (!card.equals(""))) {
			user.setCard(card);
		}
		if ((sex != null) && (!sex.equals(""))) {
			user.setSex(CommUtil.null2Int(sex));
		}
		this.userService.updateById(user);
		return "redirect:/buyer/center";
	}
}
