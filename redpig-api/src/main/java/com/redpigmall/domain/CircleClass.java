package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: CircleClass.java
 * </p>
 * 
 * <p>
 * Description: 圈子分类管理类
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
 * @date 2014-11-20
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "circle_class")
public class CircleClass extends IdEntity {
	private String className;// 类别名称
	@Column(columnDefinition = "int default 0")
	private int sequence;// 索引
	@Column(columnDefinition = "int default 0")
	private int recommend;// 默认为0,1为推荐,推荐后在圈子列表热门分类显示
	@Column(columnDefinition = "int default 0")
	private int nav_index;// 默认为0,1为在圈子导航显示

	public int getNav_index() {
		return this.nav_index;
	}

	public void setNav_index(int nav_index) {
		this.nav_index = nav_index;
	}

	public int getRecommend() {
		return this.recommend;
	}

	public void setRecommend(int recommend) {
		this.recommend = recommend;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
}
