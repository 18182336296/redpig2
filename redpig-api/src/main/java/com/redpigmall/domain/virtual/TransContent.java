package com.redpigmall.domain.virtual;

/**
 * 
 * <p>
 * Title: TransContent.java
 * </p>
 * 
 * <p>
 * Description: 快递返回值的详细信息，该类不对应任何数据表，用在解析快递接口数据使用
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
 * @date 2014-5-26
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class TransContent {
	/** 快递处理时间 */
	private String time;
	/** 快递处理的详细信息 */
	private String context;

	public String getTime() {
		return this.time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContext() {
		return this.context;
	}

	public void setContext(String context) {
		this.context = context;
	}
}
