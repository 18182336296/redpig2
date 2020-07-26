package com.redpigmall.pay.weixin;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Payment;
import com.redpigmall.service.RedPigPaymentService;

/**
 * 
 * <p>
 * Title: GetAccessToken.java
 * </p>
 * 
 * <p>
 * Description:微信支付处理接口，用来处理微信token
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
 * 
 * @date 2014-12-29
 * 
 * @version redpigmall_b2b2c 2016
 */
@Component
public class GetAccessToken {
	@Autowired
	private RedPigPaymentService paymentService;
	private static GetAccessToken getAccessToken = new GetAccessToken();//创建单例
	private static String accessToken="zzw111";//存放token
	private static Date date =  null;//超时时间
	//获取单例
	public static GetAccessToken instance() {
		return getAccessToken;
	}
	// 获取accessToken
	public String getToken() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("mark", "wx_app");
		List<Payment> objs = this.paymentService.queryPageList(params);
			
		if (objs.size() > 0) {
			if ((date != null) && (date.after(new Date()))) {
				return accessToken;
			}
			String appid = ((Payment) objs.get(0)).getWx_appid();
			String appSecret = ((Payment) objs.get(0)).getWx_appSecret();
			String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
					+ appid + "&secret=" + appSecret;
			try {
				URL urlGet = new URL(url);
				HttpURLConnection http = (HttpURLConnection) urlGet
						.openConnection();
				http.setRequestMethod("GET");
				http.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				http.setDoOutput(true);
				http.setDoInput(true);
				System.setProperty("sun.net.client.defaultConnectTimeout",
						"30000");
				System.setProperty("sun.net.client.defaultReadTimeout", "30000");
				System.setProperty("jsse.enableSNIExtension", "false");
				http.connect();
				InputStream is = http.getInputStream();
				int size = is.available();
				byte[] jsonBytes = new byte[size];
				is.read(jsonBytes);
				String message = new String(jsonBytes, "UTF-8");
				Map<String, Object> map = JSON.parseObject(message);
				String token = CommUtil.null2String(map.get("access_token"));
				is.close();
				if ((token != null) && (!token.equals(""))) {
					accessToken = token;
					// 设置20分钟后重新获取token
					Calendar cal = Calendar.getInstance();
					cal.set(12, cal.get(12) + 20);
					date = cal.getTime();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return accessToken;
		}
		return "error";
	}
}
