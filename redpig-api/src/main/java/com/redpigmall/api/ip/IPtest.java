package com.redpigmall.api.ip;

/**
 * 
 * <p>
 * Title: IPtest.java
 * </p>
 * 
 * <p>
 * Description:
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
public class IPtest {
	public static void testmai(String[] args) {// 指定纯真数据库的文件名，所在文件夹
		IPSeeker ip = new IPSeeker("QQWry.Dat", "f:/");
		String temp = "192.168.1.1";
		// 测试IP 192.168.1.1
		System.out.println(ip.getIPLocation(temp).getCountry() + ":"
				+ ip.getIPLocation(temp).getArea());
	}
}
