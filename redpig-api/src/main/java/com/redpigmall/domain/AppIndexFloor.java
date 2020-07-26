package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "app_index_floor")
public class AppIndexFloor extends IdEntity {
	private String title;
	private int sequence;
	private int display;
	@Column(columnDefinition = "LongText")
	private String lines_info;
	private Long topics_id;

	public Long getTopics_id() {
		return this.topics_id;
	}

	public void setTopics_id(Long topics_id) {
		this.topics_id = topics_id;
	}

	public AppIndexFloor(Long id, Date addTime) {
		super(id, addTime);
	}

	public AppIndexFloor(Long id) {
		super.setId(id);
	}

	public AppIndexFloor() {
	}

	public int getDisplay() {
		return this.display;
	}

	public void setDisplay(int display) {
		this.display = display;
	}

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

	public String getLines_info() {
		return this.lines_info;
	}

	public void setLines_info(String lines_info) {
		this.lines_info = lines_info;
	}
}
