package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Title: Nuke.java
 * </p>
 * 
 * <p>
 * Description:
 * 秒杀管理控制类，用来描述系统秒杀信息，秒杀由超级管理员发起，所有卖家都可以申请参加，多个秒杀时间不允许交叉，秒杀商品审核通过后在对应的秒杀活动中显示
 * ,秒杀到期后，系统自恢复秒杀商品为普通商品
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_nuke_record")
public class UserNukeRecord extends IdEntity {

	private NukeGoods nukeGoods;// 对应的秒杀活动
	private OrderForm orderForm;// 订单
	private Integer nuke_count;// 本次秒杀的数量
	private String remark;// 备注

	public NukeGoods getNukeGoods() {
		return nukeGoods;
	}

	public void setNukeGoods(NukeGoods nukeGoods) {
		this.nukeGoods = nukeGoods;
	}

	public OrderForm getOrderForm() {
		return orderForm;
	}

	public void setOrderForm(OrderForm orderForm) {
		this.orderForm = orderForm;
	}

	public Integer getNuke_count() {
		return nuke_count;
	}

	public void setNuke_count(Integer nuke_count) {
		this.nuke_count = nuke_count;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
