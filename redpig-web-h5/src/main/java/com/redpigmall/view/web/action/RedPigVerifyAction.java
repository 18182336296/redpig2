package com.redpigmall.view.web.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.redpigmall.redis.RedisCache;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Maps;

import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.User;
import com.redpigmall.domain.VerifyCode;
import com.redpigmall.view.web.action.base.BaseAction;

/**
 * 
 * <p>
 * Title: RedPigVerifyAction.java
 * </p>
 * 
 * <p>
 * Description: 系统验证控制器，用来管理系统验证码生成、用户名验证、邮箱验证等各类验证请求
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
public class RedPigVerifyAction extends BaseAction{
	
	private int codeCount = 4;
	private int lineCount = 20;
	private String code = null;
	private BufferedImage buffImg = null;
	private Graphics g = null;
	Random random = new Random();
	
	/**
	 * 验证码ajax验证方法
	 * @param request
	 * @param response
	 * @param code
	 * @param code_name
	 */
	@RequestMapping({ "/verify_code" })
	public void validate_code(HttpServletRequest request,
			HttpServletResponse response, String code, String code_name) {
		HttpSession session = request.getSession(false);
		String verify_code = "";
		if (CommUtil.null2String(code_name).equals("")) {
			verify_code = (String) session.getAttribute("verify_code");
		} else {
			verify_code = (String) session.getAttribute(code_name);
		}
		boolean ret = true;
		if ((verify_code != null)
				&& (!verify_code.equals(""))
				&& (!CommUtil.null2String(code.toUpperCase()).equals(
						verify_code))) {
			ret = false;
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
	 * 用户名ajax验证方法
	 * @param request
	 * @param response
	 * @param password
	 * @param username
	 * @param code
	 */
	@RequestMapping({ "/verity_password" })
	public void verity_password(HttpServletRequest request,
			HttpServletResponse response, String password, String username,
			String code) {
		String ret = "";

		String verify_code = (String) request.getSession().getAttribute(
				"verify_code");

		Map<String, Object> map = Maps.newHashMap();
		map.put("password", Md5Encrypt.md5(password));
		map.put("userName", username);
		
		map.put("status", Integer.valueOf(0));
		
		List<User> user_list = this.userService.verityUserNamePasswordStatus(map);
		
		if ((code != null) && (!"".equals(code))) {
			if ((user_list.size() == 1) && (code.equals(verify_code))) {
				ret = "ok";
			}
			if ((code.equals(verify_code)) && (user_list.size() != 1)) {
				ret = "user";
			}
			if ((user_list.size() == 1) && (!code.equals(verify_code))) {
				ret = "code";
			}
			if (((user_list.size() == 1) && (!code.equals(verify_code)))
					|| ((user_list.size() != 1) && (!code.equals(verify_code)))) {
				ret = "user_code";
			}
		} else if (user_list.size() == 1) {
			ret = "ok";
		} else {
			ret = "no";
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
	 * 验证user
	 * @param request
	 * @param response
	 * @param password
	 * @param username
	 * @param code
	 */
	@RequestMapping({ "/verity_user" })
	public void verity_user(HttpServletRequest request,
			HttpServletResponse response, String password, String username,
			String code) {
		HttpSession session = request.getSession(false);
		int ret = 100;

		int times = CommUtil.null2Int(RedisCache.getObject("login_validate_" + username));
		if (times < 3) {
			if (this.configService.getSysConfig().getSecurityCodeLogin()) {
				if (request.getSession(false).getAttribute("verify_code") == null) {
					ret = 200;
				} else if (!request.getSession(false)
						.getAttribute("verify_code").equals(code)) {
					ret = 200;
				}
			}
			if (ret == 100) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("password", Md5Encrypt.md5(password));
				map.put("userName", username);
				
				List<?> user_list = this.userService.verityUserNamePassword(map);
				
				if (user_list.size() != 1) {
					ret = 300;
				} else {
					User user = this.userService.selectByPrimaryKey(CommUtil
							.null2Long(((User)user_list.get(0)).getId()));
					if (user.getStatus() == 1) {
						ret = 400;
					}
				}
			}
			if (ret == 300) {
				if (times == 0) {
					RedisCache.putObject("login_validate_" + username,Integer.valueOf(times + 1));
				} else {
					RedisCache.putObject("login_validate_" + username, Integer.valueOf(times + 1));
				}
				if (times + 1 >= 3) {
					ret = 500;
				}
			}
		} else {
			if (RedisCache.getObject("verify_code_" + session.getId()) == null) {
				ret = 200;
			} else if (!RedisCache.getObject("verify_code_" + session.getId()).equals(
					code)) {
				ret = 200;
			}
			if (ret == 100) {
				Map<String, Object> map = Maps.newHashMap();
				map.put("password", Md5Encrypt.md5(password));
				map.put("username", username);
				
				List<User> user_list = this.userService.verityUserNamePassword(map);
				
				if (user_list.size() != 1) {
					ret = 300;
					if (times + 1 >= 3) {
						ret = 500;
					}
				} else {
					User user = this.userService.selectByPrimaryKey(CommUtil
							.null2Long(user_list.get(0)));
					if (user.getStatus() == 1) {
						ret = 400;
					}
				}
			}
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
	 * 
	 * @param request
	 * @param response
	 * @param userName
	 * @param id
	 */
	@RequestMapping({ "/verify_username" })
	public void verify_username(HttpServletRequest request,
			HttpServletResponse response, String userName, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("userName_email_mobile2", userName);
		params.put("id_no", CommUtil.null2Long(id));
		
		int count = this.userService.selectCount(params);
		
		if (count > 0) {
			ret = false;
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

	@RequestMapping({ "/verify_email" })
	public void verify_email(HttpServletRequest request,
			HttpServletResponse response, String email, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("email", email);
		params.put("id_no", CommUtil.null2Long(id));
		params.put("status_no", Integer.valueOf(1));
		
		List<User> users = this.userService.queryPageList(params);
		
		if ((users != null) && (users.size() > 0)) {
			ret = false;
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

	@RequestMapping({ "/verify_storename" })
	public void verify_storename(HttpServletRequest request,
			HttpServletResponse response, String store_name, String id) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("store_name", store_name);
		params.put("id_no", CommUtil.null2Long(id));
		
		List<Store> users = this.storeService.queryPageList(params);
		
		if ((users != null) && (users.size() > 0)) {
			ret = false;
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
	
	@RequestMapping({ "/verify_mobile" })
	public void verify_mobile(HttpServletRequest request,
			HttpServletResponse response, String mobile, String id, String type) {
		boolean ret = true;
		Map<String, Object> params = Maps.newHashMap();
		params.put("mobile", mobile);
		params.put("id_no", CommUtil.null2Long(id));
		
		List<User> users = this.userService.queryPageList(params);
		
		if ((type != null) && (type.equals("login"))) {
			ret = false;
			if ((users != null) && (users.size() > 0)) {
				ret = true;
			}
		} else if ((users != null) && (users.size() > 0)) {
			ret = false;
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

	
	@RequestMapping({ "/verify_mcode" })
	private void verify_mcode(HttpServletRequest request,
			HttpServletResponse response, String mcode, String mobile) {
		boolean ret = false;
		Map<String, Object> params = Maps.newHashMap();
		params.put("mobile", mobile);
		
		List<VerifyCode> mvcs = this.mobileverifycodeService.queryPageList(params);
		
		if (mvcs.size() > 0) {
			VerifyCode mv = (VerifyCode) mvcs.get(0);
			if (mv.getCode().equals(mcode)) {
				ret = true;
			}
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

	@RequestMapping({ "/verify" })
	public void verify(HttpServletRequest request,
			HttpServletResponse response, String name, String w, String h)
			throws IOException {

		response.setContentType("image/jpeg");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0L);
		HttpSession session = request.getSession(false);
		creatImage1(w, h);
		if (CommUtil.null2String(name).equals("")) {
			session.setAttribute("verify_code", this.code);
			RedisCache.removeObject("verify_code_" + session.getId());
			RedisCache.putObject("verify_code_" + session.getId(), this.code);
		} else {
			session.setAttribute(name, this.code);
		}
		this.g.dispose();
		ServletOutputStream responseOutputStream = response.getOutputStream();

		ImageIO.write(this.buffImg, "JPEG", responseOutputStream);

		responseOutputStream.flush();
		responseOutputStream.close();
	}

	private void creatImage1(String w, String h) {
		int width = 86;
		int height = 40;
		if (!CommUtil.null2String(w).equals("")) {
			width = CommUtil.null2Int(w);
		}
		if (!CommUtil.null2String(h).equals("")) {
			height = CommUtil.null2Int(h);
		}
		this.codeCount = 4;
		this.lineCount = 16;
		creatImage2(width, height);
	}

	private void creatImage2(int width, int height) {
		int fontWidth = width / this.codeCount;
		int fontHeight = height - 5;
		int codeY = height - 8;

		this.buffImg = new BufferedImage(width, height, 1);
		this.g = this.buffImg.getGraphics();

		this.g.setColor(getRandColor(230, 250));
		this.g.fillRect(0, 0, width, height);

		Font font = new Font("Fixedsys", 1, fontHeight);
		this.g.setFont(font);
		for (int i = 0; i < this.lineCount; i++) {
			int xs = this.random.nextInt(width);
			int ys = this.random.nextInt(height);
			int xe = xs + this.random.nextInt(width);
			int ye = ys + this.random.nextInt(height);
			this.g.setColor(getRandColor(1, 250));
			this.g.drawLine(xs, ys, xe, ye);
		}
		float yawpRate = 0.01F;
		int area = (int) (yawpRate * width * height);
		for (int i = 0; i < area; i++) {
			int x = this.random.nextInt(width);
			int y = this.random.nextInt(height);

			this.buffImg.setRGB(x, y, this.random.nextInt(255));
		}
		String str1 = randomStr(this.codeCount);
		this.code = str1;
		for (int i = 0; i < this.codeCount; i++) {
			String strRand = str1.substring(i, i + 1);
			this.g.setColor(getRandColor(1, 229));
			this.g.drawString(strRand, i * fontWidth + 3, codeY);
		}
	}

	private String randomStr(int n) {
		String str1 = "1234567890";
		String str2 = "";
		int len = str1.length() - 1;
		for (int i = 0; i < n; i++) {
			double r = Math.random() * len;
			str2 = str2 + str1.charAt((int) r);
		}
		return str2;
	}

	private Color getRandColor(int fc, int bc) {
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + this.random.nextInt(bc - fc);
		int g = fc + this.random.nextInt(bc - fc);
		int b = fc + this.random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}
