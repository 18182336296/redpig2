package com.redpigmall.api.tools;

import javax.servlet.http.HttpServletRequest;

import com.redpigmall.domain.SysConfig;

/**
 * 语言信息提取工具类
 * @author admin
 *
 */
public class LangUtils {
	/**
	 * 默认语言控制
	 * @param request
	 * @return
	 */
	public static String getLang(SysConfig sysConfig, int type, HttpServletRequest request){
		String lang = (String) request.getSession().getAttribute("lang");
		if(type == 0){
			return null == lang ? sysConfig.getSysLanguage() : lang;
		}else if(type == 1){
			return null == lang ? sysConfig.getSysPcLanguage() : lang;
		}else {
			return null == lang ? sysConfig.getSysStoreLanguage() : lang;
		}

	}

}
