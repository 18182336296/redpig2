package com.redpigmall.kuaidi.pojo;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 
 * <p>
 * Title: Result.java
 * </p>
 * 
 * <p>
 * Description: 该实体类来自快递100提供的接口
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
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 2016
 */
public class Result {
	@JsonIgnore
	private String status = "";
	@JsonIgnore
	private String message = "";
	@JsonIgnore
	private LastResult lastResult = new LastResult();

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LastResult getLastResult() {
		return this.lastResult;
	}

	public void setLastResult(LastResult lastResult) {
		this.lastResult = lastResult;
	}
}
