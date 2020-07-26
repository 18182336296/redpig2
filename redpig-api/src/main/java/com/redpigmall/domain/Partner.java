package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: Partner.java
 * </p>
 * 
 * <p>
 * Description:网站管理 合作伙伴,在首页底部合作伙伴位置显示
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
 * @date 2014-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "partner")
public class Partner extends IdEntity {
	private int sequence;// 排序
	private String url;// 连接
	private String title;// 标题
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory image;// 标识图片

	public Partner() {
	}

	public Partner(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Accessory getImage() {
		return this.image;
	}

	public void setImage(Accessory image) {
		this.image = image;
	}
}
