package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "nuke")
public class Nuke extends IdEntity {
	private String nuke_name;// 秒杀活动名称
	private Date beginTime;// 开始时间
	private Date endTime;// 结束时间
	private Date joinEndTime;// 报名截止时间
	private int status;// 状态，0为正常，-1为关闭,-2为已经结束,1为即将开始
	private int nuke_type;// 0为商品秒杀，1为生活类秒杀，生活类需要发送消费码及商家验证消费码

	private String remark;//活动说明
	private Integer timeout;//超时时间，以分钟计算

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	@OneToMany(mappedBy = "nuke")
	private List<NukeGoods> ng_list = new ArrayList<NukeGoods>();// 对应的秒杀商品列表
	
	@Transient
	private int ng_status = 0;// 大于0的情况则是还有正在进行的秒杀商品 无法关闭货删除秒杀,秒杀审核状态 1为通过 -1为未通过 0为待审核 -2为过期 2为审核通过未开始
	
	public int getNg_status() {
		for (NukeGoods gg : this.ng_list) {
			if (gg.getNg_status() == 1) {
				this.ng_status += 1;
			}
		}
		return this.ng_status;
	}

	public Nuke(Long id, Date addTime) {
		super(id, addTime);
	}

	public Nuke() {
	}

	public String getNuke_name() {
		return nuke_name;
	}

	public void setNuke_name(String nuke_name) {
		this.nuke_name = nuke_name;
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

	public Date getJoinEndTime() {
		return joinEndTime;
	}

	public void setJoinEndTime(Date joinEndTime) {
		this.joinEndTime = joinEndTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getNuke_type() {
		return nuke_type;
	}

	public void setNuke_type(int nuke_type) {
		this.nuke_type = nuke_type;
	}

	public List<NukeGoods> getNg_list() {
		return ng_list;
	}

	public void setNg_list(List<NukeGoods> ng_list) {
		this.ng_list = ng_list;
	}

	public void setNg_status(int ng_status) {
		this.ng_status = ng_status;
	}

}
