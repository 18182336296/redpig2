package com.redpigmall.api.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p>
 * Title: ClientInfo.java
 * </p>
 * 
 * <p>
 * Description: 返回客户端信息工具类
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
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class ClientInfo {
	private String info = "";
	private String explorerVer = "未知";
	private String OSVer = "未知";

	public ClientInfo(String info) {
		this.info = info;
	}

	/**
	 * 获取核心浏览器名称
	 * 
	 * @return
	 */
	public String getExplorerName() {
		String str = "未知";
		Pattern pattern = Pattern.compile("");
		if (this.info.indexOf("MSIE") != -1) {
			str = "MSIE";// 微软IE
			pattern = Pattern.compile(str + "\\s([0-9.]+)");
		} else if (this.info.indexOf("Firefox") != -1) {
			str = "Firefox";// 火狐
			pattern = Pattern.compile(str + "\\/([0-9.]+)");
		} else if (this.info.indexOf("Chrome") != -1) {
			str = "Chrome";// Google
			pattern = Pattern.compile(str + "\\/([0-9.]+)");
		} else if (this.info.indexOf("Opera") != -1) {
			str = "Opera";// Opera
			pattern = Pattern.compile("Version\\/([0-9.]+)");
		}
		Matcher matcher = pattern.matcher(this.info);
		if (matcher.find()) {
			this.explorerVer = matcher.group(1);
		}
		return str;
	}

	/**
	 * 获取核心浏览器版本
	 * 
	 * @return
	 */
	public String getExplorerVer() {
		return this.explorerVer;
	}

	/**
	 * 获取浏览器插件名称（例如：遨游、世界之窗等）
	 * 
	 * @return
	 */
	public String getExplorerPlug() {
		String str = "无";
		if (this.info.indexOf("Maxthon") != -1) {
			str = "Maxthon";
		}
		return str;
	}

	/**
	 * 获取操作系统名称
	 * 
	 * @return
	 */
	public String getOSName() {
		String str = "未知";
		Pattern pattern = Pattern.compile("");
		if (this.info.indexOf("Windows") != -1) {
			str = "Windows";
			pattern = Pattern.compile(str + "\\s([a-zA-Z0-9]+\\s[0-9.]+)");
		}
		Matcher matcher = pattern.matcher(this.info);
		if (matcher.find()) {
			this.OSVer = matcher.group(1);
		}
		return str;
	}

	/**
	 * 获取操作系统版本
	 * 
	 * @return
	 */
	public String getOSVer() {
		return this.OSVer;
	}
}
