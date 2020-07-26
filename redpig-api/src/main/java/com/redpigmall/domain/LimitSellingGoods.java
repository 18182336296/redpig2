package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: LimitSellingGoods.java
 * </p>
 * 
 * <p>
 * Description: 秒杀商品
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
 * @date 2018-4-19
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "redpigmall_limit_goods")
public class LimitSellingGoods extends IdEntity {
	
	private Date beginTime;// 开始时间
	
	private Date endTime;// 结束时间
	
	private Integer limit_selling_goods_status;// 秒杀商品是否开启秒杀：0：不开启，1：开启
	
	private LimitSelling limit_selling;// 对应的秒杀活动
	
	private Goods limit_selling_goods;// 对应的秒杀商品
	
	
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

	public LimitSelling getLimit_selling() {
		return limit_selling;
	}

	public void setLimit_selling(LimitSelling limit_selling) {
		this.limit_selling = limit_selling;
	}

	public Goods getLimit_selling_goods() {
		return limit_selling_goods;
	}

	public void setLimit_selling_goods(Goods limit_selling_goods) {
		this.limit_selling_goods = limit_selling_goods;
	}

	public Integer getLimit_selling_goods_status() {
		return limit_selling_goods_status;
	}

	public void setLimit_selling_goods_status(Integer limit_selling_goods_status) {
		this.limit_selling_goods_status = limit_selling_goods_status;
	}
	
	
	
}
