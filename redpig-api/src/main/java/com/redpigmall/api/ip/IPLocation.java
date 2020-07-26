package com.redpigmall.api.ip;

/**
 * 
 * <p>
 * Title: IPLocation.java
 * </p>
 * 
 * <p>
 * Description: 纯真ip查询,该类用来读取地址信息
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
public class IPLocation {
	private String country;
	private String area;

	public IPLocation() {
		this.country = (this.area = "");
	}

	public IPLocation getCopy() {
		IPLocation ret = new IPLocation();
		ret.country = this.country;
		ret.area = this.area;
		return ret;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getArea() {
		return this.area;
	}

	public void setArea(String area) {
		// 如果为局域网，纯真IP地址库的地区会显示CZ88.NET,这里把它去掉
		if (area.trim().equals("CZ88.NET")) {
			this.area = "本机";
		} else {
			this.area = area;
		}
	}
}
