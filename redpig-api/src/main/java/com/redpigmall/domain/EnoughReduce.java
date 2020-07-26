package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: OrderEnoughReduce.java
 * </p>
 * 
 * <p>
 * Description: 满就减实体类。
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
 * @author lixiaoyang
 * 
 * @date 2014-9-19
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "enough_reduce")
public class EnoughReduce extends IdEntity {
	private String ertitle;// 活动标题
	@Temporal(TemporalType.DATE)
	private Date erbegin_time;// 开始时间
	@Temporal(TemporalType.DATE)
	private Date erend_time;// 结束时间
	private int ersequence;// 活动序号
	/**
	 * 审核状态 默认为
	 * 	0待审核 
	 * 	10为 审核通过 
	 * -10为审核未通过 
	 * 	20为已结束。
	 * 	5为提交审核，此时商家不能再修改
	 */
	@Column(columnDefinition = "int default 0")
	private int erstatus;// 审核状态 默认为0待审核 10为 审核通过 -10为审核未通过 20为已结束。5为提交审核，此时商家不能再修改
	@Column(columnDefinition = "LongText")
	private String failed_reason;// 审核失败原因

	@Column(columnDefinition = "LongText")
	private String ercontent;// 活动说明
	private String ertag;// 活动的标识,满xxx减xxx
	private String store_id;// 对应的店铺id
	private String store_name;// 对应的店铺名字
	private int er_type;// 满就减类型，0为自营，1为商家
	@Column(columnDefinition = "LongText")
	private String ergoods_ids_json;// 活动商品json
	/**
	 * 例如:满200减10 满400减20格式如下:
	 * {200:10,400:20}
	 * 
	 */
	@Column(columnDefinition = "LongText")
	private String er_json;// 满、减金额的json

	public EnoughReduce() {
	}

	public EnoughReduce(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getFailed_reason() {
		return this.failed_reason;
	}

	public void setFailed_reason(String failed_reason) {
		this.failed_reason = failed_reason;
	}

	public String getStore_name() {
		return this.store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public int getEr_type() {
		return this.er_type;
	}

	public void setEr_type(int er_type) {
		this.er_type = er_type;
	}

	public String getErtitle() {
		return this.ertitle;
	}

	public void setErtitle(String ertitle) {
		this.ertitle = ertitle;
	}

	public Date getErbegin_time() {
		return this.erbegin_time;
	}

	public void setErbegin_time(Date erbegin_time) {
		this.erbegin_time = erbegin_time;
	}

	public Date getErend_time() {
		return this.erend_time;
	}

	public void setErend_time(Date erend_time) {
		this.erend_time = erend_time;
	}

	public int getErsequence() {
		return this.ersequence;
	}

	public void setErsequence(int ersequence) {
		this.ersequence = ersequence;
	}

	public int getErstatus() {
		return this.erstatus;
	}

	public void setErstatus(int erstatus) {
		this.erstatus = erstatus;
	}

	public String getErcontent() {
		return this.ercontent;
	}

	public void setErcontent(String ercontent) {
		this.ercontent = ercontent;
	}

	public String getErtag() {
		return this.ertag;
	}

	public void setErtag(String ertag) {
		this.ertag = ertag;
	}

	public String getStore_id() {
		return this.store_id;
	}

	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}

	public String getErgoods_ids_json() {
		return this.ergoods_ids_json;
	}

	public void setErgoods_ids_json(String ergoods_ids_json) {
		this.ergoods_ids_json = ergoods_ids_json;
	}

	public String getEr_json() {
		return this.er_json;
	}

	public void setEr_json(String er_json) {
		this.er_json = er_json;
	}
}
