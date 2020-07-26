package com.redpigmall.domain;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "app_topics")
public class AppTopics extends IdEntity {
	private int sequence;
	private String topics_name;

	public String getTopics_name() {
		return this.topics_name;
	}

	public void setTopics_name(String topics_name) {
		this.topics_name = topics_name;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
