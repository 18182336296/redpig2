package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.FetchType;

import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 用户拼团信息表
 * @author dell
 *
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_collage_list")
public class UserCollageList extends IdEntity{
			
	private Date addTime;// 拼团时间
	
	@ManyToOne(fetch = FetchType.LAZY)
	private CollageBuy collageBuy;// 团购活动表
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 用户表ID
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods goods;// 拼团的商品ID
	
	private String goods_spec_id;// 商品规格ID
	
	private String remark;// 备注

	@ManyToOne(fetch = FetchType.LAZY)
	private UserCollageBuyInfo group;//获取团长ID
	
	public UserCollageList() {
	}
	
	public UserCollageList(Long id, Date addTime) {
		super(id, addTime);
		// TODO Auto-generated constructor stub
	}

	public UserCollageBuyInfo getGroup() {
		return group;
	}

	public void setGroup(UserCollageBuyInfo group) {
		this.group = group;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public CollageBuy getCollageBuy() {
		return collageBuy;
	}

	public void setCollageBuy(CollageBuy collageBuy) {
		this.collageBuy = collageBuy;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Goods getGoods() {
		return goods;
	}

	public void setGoods(Goods goods) {
		this.goods = goods;
	}

	public String getGoods_spec_id() {
		return goods_spec_id;
	}

	public void setGoods_spec_id(String goods_spec_id) {
		this.goods_spec_id = goods_spec_id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


}
