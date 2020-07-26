package com.redpigmall.module.weixin.view.action;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.Md5Encrypt;
import com.redpigmall.api.tools.XMLUtil;
import com.redpigmall.module.weixin.view.action.base.BaseAction;
/**
 * 
 * <p>
 * Title: RedPigWapRechargeAction.java
 * </p>
 * 
 * <p>
 * Description:wap系统充值控制器,用来查询并计算充值应缴纳的金额、手机充值等服务
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
 * @date 2014-1-4
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Controller
public class RedPigWeixinRechargeAction extends BaseAction{
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping({ "/recharge" })
	public ModelAndView recharge(HttpServletRequest request,
			HttpServletResponse response, String mobile, String rc_amount) {
		ModelAndView mv = new RedPigJModelAndView("recharge.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 1, request, response);
		String userid = this.configService.getSysConfig().getOfcard_userid();
		String userpws = Md5Encrypt.md5(this.configService.getSysConfig()
				.getOfcard_userpws());
		String query_url = "http://api2.ofpay.com/telquery.do?userid=" + userid
				+ "&userpws=" + userpws + "&phoneno=" + mobile + "&pervalue="
				+ rc_amount + "&version=6.0";
		String return_xml = getHttpContent(query_url, "gb2312", "POST");
		Map<String, Object> map = XMLUtil.parseXML(return_xml, true);
		double inprice = CommUtil.null2Double(map.get("inprice"));
		if (CommUtil.null2Double(map.get("inprice")) <= CommUtil
				.null2Double(rc_amount)) {
			inprice = CommUtil.add(map.get("inprice"), this.configService
					.getSysConfig().getOfcard_mobile_profit());
			if (inprice > CommUtil.null2Double(rc_amount)) {
				inprice = CommUtil.null2Double(rc_amount);
			}
		}
		map.put("inprice", Double.valueOf(inprice));
		String recharge_session = CommUtil.randomString(64);
		request.getSession(false).setAttribute("recharge_session",
				recharge_session);
		mv.addObject("recharge_session", recharge_session);
		mv.addObject("map", map);
		mv.addObject("rc_amount", rc_amount);
		mv.addObject("mobile", mobile);
		return mv;
	}

	private static String getHttpContent(String url, String charSet,
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
		} catch (Exception e) {
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
}
