package com.redpigmall.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: GoodsTag.java
 * </p>
 * 
 * <p>
 * Description: 商品标签
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_tag")
public class GoodsTag extends IdEntity {
	String tagname;//标签名称
	Long usertag_id;//用户标签id

	public Long getUsertag_id() {
		return this.usertag_id;
	}

	public void setUsertag_id(Long usertag_id) {
		this.usertag_id = usertag_id;
	}

	public GoodsTag(Long id, Date addTime) {
		super(id, addTime);
	}

	public GoodsTag(Long id) {
		super.setId(id);
	}

	public GoodsTag() {
	}

	public String getTagname() {
		return this.tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}
}
