package com.redpigmall.domain;

import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: MerchantServices.java
 * </p>
 * 
 * <p>
 * Description:商家服务
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
 * @date 2017-03-22
 * 
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "merchant_services")
public class MerchantServices extends IdEntity {
	private String serve_name;//服务器名称
	private int serve_price;//价格
	private int sequence;//序号
	@OneToOne(fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
	private Accessory service_img;//图片

	public Accessory getService_img() {
		return this.service_img;
	}

	public void setService_img(Accessory service_img) {
		this.service_img = service_img;
	}

	public String getServe_name() {
		return this.serve_name;
	}

	public void setServe_name(String serve_name) {
		this.serve_name = serve_name;
	}

	public int getServe_price() {
		return this.serve_price;
	}

	public void setServe_price(int serve_price) {
		this.serve_price = serve_price;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
