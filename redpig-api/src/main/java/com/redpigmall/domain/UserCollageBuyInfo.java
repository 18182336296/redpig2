package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户拼团团表
 * @author dell
 *
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "collage_buy_info")
public class UserCollageBuyInfo extends IdEntity{
	private Date addTime;// 创建时间
	@ManyToOne(fetch = FetchType.LAZY)
	private UserCollageList collageBuy;// 团购活动表
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 用户表ID
	private Integer collageNumber;//商品已经拼团数量
	private Integer status;//0:未付款 1:拼团中，2:拼团成功, 3：拼团失败
	private Date finishTime;//
	private String ImageHeadurl;//头像地址
	private String remark;// 备注



	public UserCollageBuyInfo() {
	}

	public UserCollageBuyInfo(Long id, Date addTime) {
		super(id, addTime);
	}

	@Override
	public Date getAddTime() {
		return addTime;
	}

	@Override
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}


	public UserCollageList getCollageBuy() {
		return collageBuy;
	}

	public void setCollageBuy(UserCollageList collageBuy) {
		this.collageBuy = collageBuy;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getCollageNumber() {
		return collageNumber;
	}

	public void setCollageNumber(Integer collageNumber) {
		this.collageNumber = collageNumber;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	public String getImageHeadurl() {
		return ImageHeadurl;
	}

	public void setImageHeadurl(String imageHeadurl) {
		ImageHeadurl = imageHeadurl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
