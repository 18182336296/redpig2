package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: StoreSlide.java
 * </p>
 * 
 * <p>
 * Description: 店铺幻灯类,用来显示店铺首页大幻灯信息
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
 * @version redpigmall_b2b2c v8.0 2016版 

 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_slide")
public class StoreSlide extends IdEntity {
	private String url;//url
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory acc;//图片
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;//店铺
	@Column(columnDefinition = "int default 0")
	private int slide_type;// 0为店铺设置默认幻灯，1为店铺装修中设置的模块幻灯

	public int getSlide_type() {
		return this.slide_type;
	}

	public void setSlide_type(int slide_type) {
		this.slide_type = slide_type;
	}

	public StoreSlide() {
	}

	public StoreSlide(Long id, Date addTime) {
		super(id, addTime);
	}

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Accessory getAcc() {
		return this.acc;
	}

	public void setAcc(Accessory acc) {
		this.acc = acc;
	}
}
