package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: WeixinFloor.java
 * </p>
 * 
 * <p>
 * Description:微信楼层
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "weixin_floor")
public class WeixinFloor extends IdEntity {
	private String title;//主题
	private int sequence;//序号
	@Column(columnDefinition = "int default 0")
	private int display;//是否显示
	@Column(columnDefinition = "LongText")
	private String lines_info;//
	private String more_link;

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getDisplay() {
		return this.display;
	}

	public void setDisplay(int display) {
		this.display = display;
	}

	public String getLines_info() {
		return this.lines_info;
	}

	public void setLines_info(String lines_info) {
		this.lines_info = lines_info;
	}

	public String getMore_link() {
		return this.more_link;
	}

	public void setMore_link(String more_link) {
		this.more_link = more_link;
	}
}
