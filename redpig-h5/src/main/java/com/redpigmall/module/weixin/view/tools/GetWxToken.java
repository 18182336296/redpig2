package com.redpigmall.module.weixin.view.tools;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.redpigmall.api.tools.CommUtil;

/**
 * 
 * <p>
 * Title: GetWxToken.java
 * </p>
 * 
 * <p>
 * Description:微信相关获取的accessToken
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
 * @date 2014-12-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Component
public class GetWxToken {
	private static GetWxToken getAccessToken = new GetWxToken();
	private static String wx_accessToken = "";
	private static Date wx_date = new Date();
	private static String EncodingAESKey = "";

	static {
		Properties prop = new Properties();
		InputStream in = GetWxToken.class
				.getResourceAsStream("/redpigmall.properties");
		try {
			prop.load(in);
			String key = prop.getProperty("EncodingAESKey").trim();
			EncodingAESKey = key;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static GetWxToken instance() {
		return getAccessToken;
	}

	public String getEncodingAESKey() {
		return EncodingAESKey;
	}
	// 获取微商城accessToken
	public String getWxToken(String appId, String appSecret) {
		if ((wx_date != null) && (wx_date.before(new Date()))) {
			String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
					+ appId + "&secret=" + appSecret;
			System.out.println(url);
			try {
				URL urlGet = new URL(url);
				HttpURLConnection http = (HttpURLConnection) urlGet
						.openConnection();
				http.setRequestMethod("GET");// 必须是get方式请求
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
					wx_accessToken = token;

					Calendar cal = Calendar.getInstance();
					cal.set(12, cal.get(12) + 20);
					wx_date = cal.getTime();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return wx_accessToken;
	}
}
