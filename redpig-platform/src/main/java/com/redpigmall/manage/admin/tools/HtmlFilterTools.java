package com.redpigmall.manage.admin.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
/**
 * 
 * 
 * Title: HtmlFilterTools.java
 * 
 * Description:处理HTML
 * 
 * Copyright: Copyright (c) 2018
 * 
 * Company: RedPigMall
 * 
 * @author redpig
 * 
 * @date 2018年7月10日 上午9:48:53
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@Component
public class HtmlFilterTools {
	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
	private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
	private static final String regEx_html = "<[^>]+>";
	private static final String regEx_space = "\\s*|\t|\r|\n";
	
	/**
	 * 删除脚本里面的tag
	 *
	 * @author redpig
	 * @param str
	 * @return
	 * @since JDK 1.8
	 */
	public static String delScriptTag(String str) {
		Pattern p_script = Pattern.compile(regEx_script, 2);
		Matcher m_script = p_script.matcher(str);
		str = m_script.replaceAll("");
		return str.trim();
	}
	
	/**
	 * 删除样式里面tag
	 *
	 * @author redpig
	 * @param str
	 * @return
	 * @since JDK 1.8
	 */
	public static String delStyleTag(String str) {
		Pattern p_style = Pattern.compile(regEx_style,2);
		Matcher m_style = p_style.matcher(str);
		str = m_style.replaceAll("");
		return str.trim();
	}
	
	/**
	 * 删除HTML里面的tag
	 *
	 * @author redpig
	 * @param str
	 * @return
	 * @since JDK 1.8
	 */
	public static String delHTMLTag(String str) {
		Pattern p_html = Pattern.compile(regEx_html, 2);
		Matcher m_html = p_html.matcher(str);
		str = m_html.replaceAll("");
		return str.trim();
	}
	
	/**
	 * 删除空格里面的tag
	 *
	 * @author redpig
	 * @param str
	 * @return
	 * @since JDK 1.8
	 */
	public static String delSpaceTag(String str) {
		Pattern p_space = Pattern.compile(regEx_space, 2);
		Matcher m_space = p_space.matcher(str);
		str = m_space.replaceAll("");
		return str.trim();
	}
	
	/**
	 * 删除所有的tag
	 *
	 * @author redpig
	 * @param str
	 * @return
	 * @since JDK 1.8
	 */
	public static String delAllTag(String str) {
		str = delHTMLTag(str);
		str = delScriptTag(str);
		str = delSpaceTag(str);
		str = delStyleTag(str);
		return str.trim();
	}
}
