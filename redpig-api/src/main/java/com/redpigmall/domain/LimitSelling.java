package com.redpigmall.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: LimitSelling.java
 * </p>
 * 
 * <p>
 * Description:秒杀管理控制类，用来描述描述信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2018
 * </p>
 * 
 * <p>
 * Company: www.redpigmall.net
 * </p>
 * 
 * @author redpig
 * 
 * @date 2018-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "limit")
public class LimitSelling extends IdEntity {
	
	private String limit_name;// 秒杀活动名称
	private Date beginTime;// 开始时间
	private Date endTime;// 结束时间
	private int limit_status;// 秒杀状态：0：不开启，1：开启
	
	private List<Goods> limitSellingGoods;//参加秒杀活动的商品
	
	public String getLimit_name() {
		return limit_name;
	}
	public void setLimit_name(String limit_name) {
		this.limit_name = limit_name;
	}
	public Date getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public int getLimit_status() {
		return limit_status;
	}
	public void setLimit_status(int limit_status) {
		this.limit_status = limit_status;
	}
	public List<Goods> getLimitSellingGoods() {
		return limitSellingGoods;
	}
	public void setLimitSellingGoods(List<Goods> limitSellingGoods) {
		this.limitSellingGoods = limitSellingGoods;
	}
	
}
