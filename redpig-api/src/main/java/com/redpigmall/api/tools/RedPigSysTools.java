package com.redpigmall.api.tools;

/**
 * 
 * <p>
 * Title: RedPigSysTools.java
 * </p>
 * 
 * <p>
 * Description: 系统工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2016-10-07
 * 
 * @version redpigmall_b2b2c v6.0 2016版
 */
public class RedPigSysTools {
	/**
	 * 判断是否是linux系统
	 * 
	 * @return
	 */
	public static boolean isLinuxOS() {

		String osname = System.getProperties().getProperty("os.name");

		String windows = "windows";

		String linux = "linux";
//		return true;
		if (osname != null && osname.toLowerCase().contains(windows)) {
			return false;
		} else if (osname != null && osname.toLowerCase().contains(linux)) {
			return true;
		}
		
		return false;
	}

	public static boolean isWindsOS() {
		return !isLinuxOS();
	}
}
