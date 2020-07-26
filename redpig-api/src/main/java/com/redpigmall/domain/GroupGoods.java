package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: GroupGoods.java
 * </p>
 * 
 * <p>
 * Description: 团购商品管理控制类，用来管理团购商品信息
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
 * @date 2014-4-25
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "group_goods")
public class GroupGoods extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;// 对应的团购
	private Date beginTime;// 开始时间
	private Date endTime;// 结束时间
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupClass gg_gc;// 团购类型
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupArea gg_ga;// 团购区域
	private String gg_name;// 团购商品名称
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods gg_goods;// 团购商品
	@Column(precision = 12, scale = 2)
	private BigDecimal gg_price;// 团购价格
	private int gg_count;// 团购商品库存数量
	private int gg_group_count;// 成团数量
	@Column(columnDefinition = "int default 0")
	private int gg_selled_count;// 已经售出的数量
	@Column(precision = 12, scale = 2)
	private BigDecimal gg_rebate;// 团购折扣率
	private int gg_status;// 团购状态，0为待审核，1为审核通过并已开始 2为审核通过未开始，-1为审核拒绝 -2为过期
	private Date gg_audit_time;// 审核时间
	@Column(columnDefinition = "bit default false")
	private Boolean gg_recommend;// 推荐状态，0为为推荐，1为推荐，推荐团购在首页显示
	private Date gg_recommend_time;// 团购推荐时间
	@Column(columnDefinition = "LongText")
	private String gg_content;// 团购商品描述
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory gg_img;// 团购商品图片

	@Column(columnDefinition = "bit default false")
	private boolean weixin_shop_recommend;// 微信商城推荐，推荐后出现在微信商城（平台大商城）首页，
	@Temporal(TemporalType.DATE)
	private Date weixin_shop_recommendTime;// 微信商城推荐时间

	@Column(columnDefinition = "int default 0")
	private int mobile_recommend;// 手机客户端推荐， 1为推荐，推荐后在手机客户端首页显示
	@Temporal(TemporalType.DATE)
	private Date mobile_recommendTime;// 手机推荐时间，

	@Column(columnDefinition = "int default 0")
	private int weixin_recommend;// 微信端推荐， 1为推荐，推荐后在微信端首页显示
	@Temporal(TemporalType.DATE)
	private Date weixin_recommendTime;// 微信推荐时间，
	
	
	
	public GroupGoods(Long id, Date addTime) {
		super(id, addTime);
	}

	public GroupGoods(Long id, BigDecimal gg_price, Accessory gg_img) {
		super.setId(id);
		setGg_price(gg_price);
		setGg_img(gg_img);
	}

	public GroupGoods() {
	}

	public int getWeixin_recommend() {
		return this.weixin_recommend;
	}

	public void setWeixin_recommend(int weixin_recommend) {
		this.weixin_recommend = weixin_recommend;
	}

	public Date getWeixin_recommendTime() {
		return this.weixin_recommendTime;
	}

	public void setWeixin_recommendTime(Date weixin_recommendTime) {
		this.weixin_recommendTime = weixin_recommendTime;
	}

	public int getMobile_recommend() {
		return this.mobile_recommend;
	}

	public void setMobile_recommend(int mobile_recommend) {
		this.mobile_recommend = mobile_recommend;
	}

	public Date getMobile_recommendTime() {
		return this.mobile_recommendTime;
	}

	public void setMobile_recommendTime(Date mobile_recommendTime) {
		this.mobile_recommendTime = mobile_recommendTime;
	}

	public int getGg_selled_count() {
		return this.gg_selled_count;
	}

	public void setGg_selled_count(int gg_selled_count) {
		this.gg_selled_count = gg_selled_count;
	}

	public Boolean getGg_recommend() {
		return gg_recommend;
	}
	
	public void setGg_recommend(Boolean gg_recommend) {
		this.gg_recommend = gg_recommend;
	}
	
	public Date getBeginTime() {
		return this.beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Boolean getWeixin_shop_recommend() {
		return this.weixin_shop_recommend;
	}
	
	public void setWeixin_shop_recommend(boolean weixin_shop_recommend) {
		this.weixin_shop_recommend = weixin_shop_recommend;
	}

	public Date getWeixin_shop_recommendTime() {
		return this.weixin_shop_recommendTime;
	}

	public void setWeixin_shop_recommendTime(Date weixin_shop_recommendTime) {
		this.weixin_shop_recommendTime = weixin_shop_recommendTime;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getGg_name() {
		return this.gg_name;
	}

	public void setGg_name(String gg_name) {
		this.gg_name = gg_name;
	}

	public Goods getGg_goods() {
		return this.gg_goods;
	}

	public void setGg_goods(Goods gg_goods) {
		this.gg_goods = gg_goods;
	}

	public BigDecimal getGg_price() {
		return this.gg_price;
	}

	public void setGg_price(BigDecimal gg_price) {
		this.gg_price = gg_price;
	}

	public int getGg_group_count() {
		return this.gg_group_count;
	}

	public void setGg_group_count(int gg_group_count) {
		this.gg_group_count = gg_group_count;
	}

	public BigDecimal getGg_rebate() {
		return this.gg_rebate;
	}

	public void setGg_rebate(BigDecimal gg_rebate) {
		this.gg_rebate = gg_rebate;
	}

	public int getGg_status() {
		return this.gg_status;
	}

	public void setGg_status(int gg_status) {
		this.gg_status = gg_status;
	}

	public String getGg_content() {
		return this.gg_content;
	}

	public void setGg_content(String gg_content) {
		this.gg_content = gg_content;
	}

	public GroupClass getGg_gc() {
		return this.gg_gc;
	}

	public void setGg_gc(GroupClass gg_gc) {
		this.gg_gc = gg_gc;
	}

	public GroupArea getGg_ga() {
		return this.gg_ga;
	}

	public void setGg_ga(GroupArea gg_ga) {
		this.gg_ga = gg_ga;
	}

	public int getGg_count() {
		return this.gg_count;
	}

	public void setGg_count(int gg_count) {
		this.gg_count = gg_count;
	}

	public Accessory getGg_img() {
		return this.gg_img;
	}

	public void setGg_img(Accessory gg_img) {
		this.gg_img = gg_img;
	}

	public Date getGg_audit_time() {
		return this.gg_audit_time;
	}

	public void setGg_audit_time(Date gg_audit_time) {
		this.gg_audit_time = gg_audit_time;
	}

	public Date getGg_recommend_time() {
		return this.gg_recommend_time;
	}

	public void setGg_recommend_time(Date gg_recommend_time) {
		this.gg_recommend_time = gg_recommend_time;
	}
}
