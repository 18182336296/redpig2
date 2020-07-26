package com.redpigmall.api.domain.virtual;

import java.util.Date;

/**
 * 
 * <p>
 * Title: ShopData.java
 * </p>
 * 
 * <p>
 * Description: 数据备份信息，该信息对应备份文件夹，和数据库表无关联
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
 * @author redpigmall
 * 
 * @date 2016-4-24
 * 
 * @version redpigmall_b2b2c 8.0
 */
public class ShopData {
	/** 备份名称 */
	private String name;
	/** 存储的物理路径 */
	private String phyPath;
	/** 数据大小 */
	private double size;
	/** 分卷数 */
	private int boundSize;
	/** 备份时间 */
	private Date addTime;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhyPath() {
		return this.phyPath;
	}

	public void setPhyPath(String phyPath) {
		this.phyPath = phyPath;
	}

	public double getSize() {
		return this.size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public Date getAddTime() {
		return this.addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public int getBoundSize() {
		return this.boundSize;
	}

	public void setBoundSize(int boundSize) {
		this.boundSize = boundSize;
	}
}
