package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: StoreHouse.java
 * </p>
 * 
 * <p>
 * Description:店铺仓库
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "storehouse")
public class StoreHouse extends IdEntity {
	private String sh_name;//仓库名称
	@Column(columnDefinition = "int default 0")
	private int sh_sequence;//序号
	private String sh_type;//类型
	@Column(columnDefinition = "LongText")
	private String sh_area;//地区
	@Column(columnDefinition = "LongText")
	private String sh_info;//仓库信息
	private int sh_status;//状态

	public StoreHouse() {
	}

	public StoreHouse(Long id, Date addTime) {
		super(id, addTime);
	}

	public int getSh_status() {
		return this.sh_status;
	}

	public void setSh_status(int sh_status) {
		this.sh_status = sh_status;
	}

	public String getSh_name() {
		return this.sh_name;
	}

	public void setSh_name(String sh_name) {
		this.sh_name = sh_name;
	}

	public int getSh_sequence() {
		return this.sh_sequence;
	}

	public void setSh_sequence(int sh_sequence) {
		this.sh_sequence = sh_sequence;
	}

	public String getSh_type() {
		return this.sh_type;
	}

	public void setSh_type(String sh_type) {
		this.sh_type = sh_type;
	}

	public String getSh_area() {
		return this.sh_area;
	}

	public void setSh_area(String sh_area) {
		this.sh_area = sh_area;
	}

	public String getSh_info() {
		return this.sh_info;
	}

	public void setSh_info(String sh_info) {
		this.sh_info = sh_info;
	}
}
