package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: ExpressCompany.java
 * </p>
 * 
 * <p>
 * Description:快递公司管理，系统默认有多个快递公司信息，用户可以在订单页面，查询快递配送信息，V1.3版开始，目前使用快递100接口查询，
 * 后续公司内部自己设计快递查询接口 快递公司为系统数据，管理员随意添加需要遵循快递100的公司编码，和程序绑定使用
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
 * @date 2014-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "express_company")
public class ExpressCompany extends IdEntity {
	private String company_name;// 快递公司名称
	private String company_mark;// 快递公司代码，根据该代码查询对应的物流信息，代码见：http://code.google.com/p/kuaidi-api/wiki/Open_API_API_URL
	@Column(columnDefinition = "int default 0")
	private int company_sequence;// 公司序号，默认按照升序排列
	@Column(columnDefinition = "varchar(255) default 'EXPRESS'")
	private String company_type;// 快递公司类型，POST为平邮、EXPRESS为快递、EMS
	@Column(columnDefinition = "int default 0")
	private int company_status;// 快递公司状态，0为启用，-1为关闭状态
	@Column(columnDefinition = "LongText")
	private String company_template;// 快递模板路径
	@Column(columnDefinition = "int default 0")
	private int company_template_width;// 快递模板宽度,单位为毫米
	@Column(columnDefinition = "int default 0")
	private int company_template_heigh;// 快递模板高度，单位为毫米
	@Column(columnDefinition = "LongText")
	private String company_template_offset;// 快递模板各个参数的偏移量，用来定位模板上的相关信息

	public ExpressCompany() {
	}

	public ExpressCompany(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getCompany_template_width() {
		return this.company_template_width;
	}

	public void setCompany_template_width(int company_template_width) {
		this.company_template_width = company_template_width;
	}

	public int getCompany_template_heigh() {
		return this.company_template_heigh;
	}

	public void setCompany_template_heigh(int company_template_heigh) {
		this.company_template_heigh = company_template_heigh;
	}

	public String getCompany_template() {
		return this.company_template;
	}

	public void setCompany_template(String company_template) {
		this.company_template = company_template;
	}

	public String getCompany_template_offset() {
		return this.company_template_offset;
	}

	public void setCompany_template_offset(String company_template_offset) {
		this.company_template_offset = company_template_offset;
	}

	public String getCompany_name() {
		return this.company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public String getCompany_mark() {
		return this.company_mark;
	}

	public void setCompany_mark(String company_mark) {
		this.company_mark = company_mark;
	}

	public String getCompany_type() {
		return this.company_type;
	}

	public void setCompany_type(String company_type) {
		this.company_type = company_type;
	}

	public int getCompany_status() {
		return this.company_status;
	}

	public void setCompany_status(int company_status) {
		this.company_status = company_status;
	}

	public int getCompany_sequence() {
		return this.company_sequence;
	}

	public void setCompany_sequence(int company_sequence) {
		this.company_sequence = company_sequence;
	}
}
