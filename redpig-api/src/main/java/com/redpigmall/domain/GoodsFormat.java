package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: GoodsFormat.java
 * </p>
 * 
 * <p>
 * Description: 商品版式管理类，商品详细信息可能需要共同的顶部、底部信息，通过版式加载，减去重复编辑商品信息共同内容的麻烦
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
 * @date 2014-10-18
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_format")
public class GoodsFormat extends IdEntity {
	private String gf_name;// 版式名称
	@Column(columnDefinition = "int default 0")
	private int gf_type;// 版式位置，0为顶部版式，1为底部版式
	@Column(columnDefinition = "LongText")
	private String gf_content;// 版式内容
	private Long gf_store_id;// 版式对应的店铺
	@Column(columnDefinition = "int default 0")
	private Integer gf_cat;// 版式分类，0为商铺版式，1为自营版式

	public GoodsFormat(Long id, Date addTime) {
		super(id, addTime);
	}

	public GoodsFormat() {
	}

	public String getGf_name() {
		return this.gf_name;
	}

	public void setGf_name(String gf_name) {
		this.gf_name = gf_name;
	}

	public int getGf_type() {
		return this.gf_type;
	}

	public void setGf_type(int gf_type) {
		this.gf_type = gf_type;
	}

	public String getGf_content() {
		return this.gf_content;
	}

	public void setGf_content(String gf_content) {
		this.gf_content = gf_content;
	}

	public Integer getGf_cat() {
		return this.gf_cat;
	}

	public void setGf_cat(Integer gf_cat) {
		this.gf_cat = gf_cat;
	}

	public Long getGf_store_id() {
		return this.gf_store_id;
	}

	public void setGf_store_id(Long gf_store_id) {
		this.gf_store_id = gf_store_id;
	}
}
