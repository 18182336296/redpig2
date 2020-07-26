package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: CmsIndexTemplate.java
 * </p>
 * 
 * <p>
 * Description: cms咨询模板
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
 * @date 2014-12-9
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "cmsindextemplate")
public class CmsIndexTemplate extends IdEntity {
	//不同风格楼层中采用的不同展示方式。采用json管理 分为4种。
	//key的命名方式
	//商品id goods_id 名称 goods_name  价格goods_price 图片goods_acc
	//资讯id info_id 名称 info_name 简述info_synopsis 图片info_acc
	//品牌 brand_id brand_name brand_acc
	//0元试用  free_id free_name free_acc 
	//圈子  circle_id circle_name  circle_acc
	@Column(columnDefinition = "LongText")
	private String floor_info1;
	
	@Column(columnDefinition = "LongText")
	private String floor_info2;
	
	@Column(columnDefinition = "LongText")
	private String floor_info3;
	
	@Column(columnDefinition = "LongText")
	private String floor_info4;
	
	private String type;//不同类型的楼层风格分为5种 info-info,goods-class,goods,info-info-goods-brand,goods-free-circle
	private String title;//楼层标题
	@Column(columnDefinition = "int default 0")
	private int sequence;//排序
	@Column(columnDefinition = "int default 0")
	private int whether_show;//是否显示 0为否 1为是

	public int getWhether_show() {
		return this.whether_show;
	}

	public void setWhether_show(int whether_show) {
		this.whether_show = whether_show;
	}

	public String getFloor_info1() {
		return this.floor_info1;
	}

	public void setFloor_info1(String floor_info1) {
		this.floor_info1 = floor_info1;
	}

	public String getFloor_info2() {
		return this.floor_info2;
	}

	public void setFloor_info2(String floor_info2) {
		this.floor_info2 = floor_info2;
	}

	public String getFloor_info3() {
		return this.floor_info3;
	}

	public void setFloor_info3(String floor_info3) {
		this.floor_info3 = floor_info3;
	}

	public String getFloor_info4() {
		return this.floor_info4;
	}

	public void setFloor_info4(String floor_info4) {
		this.floor_info4 = floor_info4;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
