package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: FootPoint.java
 * </p>
 * 
 * <p>
 * Description: 用户足迹管理类，用来记录用户自己信息
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
 * @date 2017-04-16
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "foot_point")
public class FootPoint extends IdEntity {
	@Temporal(TemporalType.DATE)
	private Date fp_date;// YYYY-MM-dd格式时间
	private String fp_user_name;// 足迹对应的商品名称
	private Long fp_user_id;// 足迹对应的用户id
	@Column(columnDefinition = "LongText")
	private String fp_goods_content;// 足迹内容，使用json管理，每一天访问的内容记录在同一个足迹中[{"goods_id":1,"goods_name":"xxxx","goods_img_path":"xxxx","goods_time":xxxxx,"goods_sale":1,"goods_price":xxx,"goods_class_id":xxx,"goods_class_name":xxxx}]
	@Column(columnDefinition = "int default 0")
	private int fp_goods_count;// 当日浏览足迹的数量

	public FootPoint() {
	}

	public FootPoint(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getFp_goods_count() {
		return this.fp_goods_count;
	}

	public void setFp_goods_count(int fp_goods_count) {
		this.fp_goods_count = fp_goods_count;
	}

	public Date getFp_date() {
		return this.fp_date;
	}

	public void setFp_date(Date fp_date) {
		this.fp_date = fp_date;
	}

	public String getFp_user_name() {
		return this.fp_user_name;
	}

	public void setFp_user_name(String fp_user_name) {
		this.fp_user_name = fp_user_name;
	}

	public Long getFp_user_id() {
		return this.fp_user_id;
	}

	public void setFp_user_id(Long fp_user_id) {
		this.fp_user_id = fp_user_id;
	}

	public String getFp_goods_content() {
		return this.fp_goods_content;
	}

	public void setFp_goods_content(String fp_goods_content) {
		this.fp_goods_content = fp_goods_content;
	}
}
