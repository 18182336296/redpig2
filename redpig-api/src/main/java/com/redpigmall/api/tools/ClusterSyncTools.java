package com.redpigmall.api.tools;

import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * <p>
 * Title: ClusterSyncTools.java
 * </p>
 * 
 * <p>
 * Description: 集群下同步文件
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
public class ClusterSyncTools {
	

	public static boolean isUnix() {
		String osname = System.getProperties().getProperty("os.name");
		String windows = "windows";
		String linux = "linux";
		if (osname != null && osname.toLowerCase().contains(windows)) {
			return false;
		} else if (osname != null && osname.toLowerCase().contains(linux)) {
			return true;
		}
		return true;
	}

	public static boolean isWins() {
		String osname = System.getProperties().getProperty("os.name");
		String windows = "windows";
		String linux = "linux";
		if (osname != null && osname.toLowerCase().contains(windows)) {
			return true;
		} else if (osname != null && osname.toLowerCase().contains(linux)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取项目资源路径
	 * 
	 * @return
	 */
	public static String getClusterRoot() {
		RedPigSysConfigService sysConfigService =  SpringUtil.getBean(RedPigSysConfigService.class);
		
		return sysConfigService.getSysConfig().getLucenePath();
	}
	
	/**
	 * 获取项目资源路径
	 * 
	 * @return
	 */
	public static String getCDN() {
		RedPigSysConfigService sysConfigService =  SpringUtil.getBean(RedPigSysConfigService.class);
		
		return sysConfigService.getSysConfig().getImageWebServer();
	}

}
