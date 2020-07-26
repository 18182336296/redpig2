package com.redpigmall.domain.virtual;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * <p>
 * Title: TransInfo.java
 * </p>
 * 
 * <p>
 * Description:快递查询信息返回值，该类不对应任何数据表，用在解析快递接口数据使用
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
public class TransInfo {
	/** 快递公司信息 */
	private String express_company_name;
	/** 快递单号 */
	private String express_ship_code;
	/** 查询失败返回信息 */
	private String reason;
	/** 查询返回状态 */
	private String status;
	/** 正确返回后的详细信息 */
	private String exname;

	List<TransContent> data = Lists.newArrayList();

	public String getExname() {
		return this.exname;
	}

	public void setExname(String exname) {
		this.exname = exname;
	}

	public String getExpress_company_name() {
		return this.express_company_name;
	}

	public void setExpress_company_name(String express_company_name) {
		this.express_company_name = express_company_name;
	}

	public String getExpress_ship_code() {
		return this.express_ship_code;
	}

	public void setExpress_ship_code(String express_ship_code) {
		this.express_ship_code = express_ship_code;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<TransContent> getData() {
		return this.data;
	}

	public void setData(List<TransContent> data) {
		this.data = data;
	}
}
