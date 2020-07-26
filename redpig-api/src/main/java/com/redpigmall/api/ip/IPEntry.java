package com.redpigmall.api.ip;

/**
 * 
 * <p>
 * Title: IPEntry.java
 * </p>
 * 
 * <p>
 * Description:纯真ip查询，该类用来读取QQWry.dat中的的IP记录信息
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
public class IPEntry {
	public String beginIp;
	public String endIp;
	public String country;
	public String area;

	/**
	 * 构造函数
	 */
	public IPEntry() {
		this.beginIp = (this.endIp = this.country = this.area = "");
	}
}
