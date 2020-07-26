package com.redpigmall.msg;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.redpigmall.api.tools.SpringUtil;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.service.RedPigSysConfigService;


/**
 * 
 * <p>
 * Title: RedPigSmsBase.java
 * </p>
 * 
 * <p>
 * Description: 系统手机短信发送类，结合第三方短信平台进行管理使用
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
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class RedPigSmsBase {
	
	private RedPigSysConfigService configService = SpringUtil.getBean(RedPigSysConfigService.class);
	private String jxt_url;
	private String jxt_id;
	private String jxt_pwd;
//	private String ymrt_url;
//	private String ymrt_id;
//	private String ymrt_pwd;

	public RedPigSmsBase(String url, String id, String pwd) {
		this.jxt_url = url;
		this.jxt_id = id;
		this.jxt_pwd = pwd;
	}

	public String SendSms(String mobile, String content)
			 {
		String result = "";
		SysConfig config = this.configService.getSysConfig();
		if (config.getSmsPlatType() == 0) {
			result = sendByJXT(mobile, content);
		}
		if (config.getSmsPlatType() == 1) {
			result = sendByYMRT(mobile, content);
		}
		return result;
	}

	private String sendByJXT(String mobile, String content)
			 {
//		Integer x_ac = 10;// 发送信息
		HttpURLConnection httpconn = null;
		String result = "-20";
		System.out.println(content);
		content = Jsoup.clean(content, Whitelist.none()).replace("&nbsp;", "")
				.trim();
		;// 过滤所有html代码
		System.out.println(content);
		StringBuilder sb = new StringBuilder();
		sb.append(jxt_url);
		sb.append("?id=").append(jxt_id);
		sb.append("&pwd=").append(jxt_pwd);
		sb.append("&to=").append(mobile);
		try {
			sb.append("&content=").append(URLEncoder.encode(content, "gb2312")); // 注意乱码的话换成gb2312编码
			URL url = new URL(sb.toString());
			httpconn = (HttpURLConnection) url.openConnection();
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					httpconn.getInputStream()));
			result = rd.readLine();
			rd.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpconn != null) {
				httpconn.disconnect();
				httpconn = null;
			}

		}
		return result;
	}

	private String sendByYMRT(String mobile, String content) {
		return "";
	}
}
