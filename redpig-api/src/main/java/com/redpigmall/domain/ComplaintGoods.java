package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
import com.redpigmall.manage.admin.tools.HtmlFilterTools;

/**
 * 
 * <p>
 * Title: ComplaintGoods.java
 * </p>
 * 
 * <p>
 * Description:投诉商品管理类，用来描述投诉的商品信息
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "complaint_goods")
public class ComplaintGoods extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods goods;// 被投诉的商品
	@Column(columnDefinition = "LongText")
	private String content;// 投诉该商品意见
	@ManyToOne(fetch = FetchType.LAZY)
	private Complaint complaint;// 对应的投诉

	public ComplaintGoods(Long id, Date addTime) {
		super(id, addTime);
	}

	public ComplaintGoods() {
	}

	public Goods getGoods() {
		return this.goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		content = HtmlFilterTools.delAllTag(content);
		this.content = content;
	}

	public Complaint getComplaint() {
		return this.complaint;
	}

	public void setComplaint(Complaint complaint) {
		this.complaint = complaint;
	}
}
