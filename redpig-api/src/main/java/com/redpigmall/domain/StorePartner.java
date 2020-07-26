package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: StorePartner.java
 * </p>
 * 
 * <p>
 * Description: 店铺合作伙伴(友情链接)管理类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_partner")
public class StorePartner extends IdEntity {
	private String title;// 链接标题
	private String url;// 链接地址
	private int sequence;// 链接序号
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 或作伙伴对应的店铺

	public StorePartner() {
	}

	public StorePartner(Long id, Date addTime) {
		super(id, addTime);
	}

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
