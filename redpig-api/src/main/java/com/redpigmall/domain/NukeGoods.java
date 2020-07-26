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
 * Description: 秒杀商品管理控制类，用来管理秒杀商品信息
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
public class NukeGoods extends IdEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private Nuke nuke;// 对应的秒杀
	private Date beginTime;// 开始时间
	private Date endTime;// 结束时间
	@ManyToOne(fetch = FetchType.LAZY)
	private NukeClass ng_nc;// 秒杀类型

	private String ng_name;// 秒杀商品名称
	@ManyToOne(fetch = FetchType.LAZY)
	private Goods ng_goods;// 秒杀商品
	@Column(precision = 12, scale = 2)
	private BigDecimal origin_price;// 设置秒杀时先存商品原价，便于秒杀活动后恢复原价
	@Column(precision = 12, scale = 2)
	private BigDecimal ng_price;// 秒杀价格
	private int ng_count;// 秒杀商品库存数量
	private int ng_nuke_count;// 秒杀数量
	@Column(columnDefinition = "int default 0")
	private int ng_selled_count;// 已经售出的数量
	@Column(precision = 12, scale = 2)
	private BigDecimal ng_rebate;// 秒杀折扣率
	private int ng_status;// 秒杀状态，0为待审核，1为审核通过未开始 2为审核通过已开始，-1为审核拒绝 -2为过期
	private Date ng_audit_time;// 审核时间
	@Column(columnDefinition = "bit default false")
	private Boolean ng_recommend;// 推荐状态，0为为推荐，1为推荐，推荐秒杀在首页显示
	private Date ng_recommend_time;// 秒杀推荐时间
	@Column(columnDefinition = "LongText")
	private String ng_content;// 秒杀商品描述
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Accessory ng_img;// 秒杀商品图片
	private String goods_spec_id;// 商品规格id，格式为：“527_或527_628_”，一对一，一个秒杀活动的商品只能有一个规格
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

	private Integer limit_number;//限购份数

	public Nuke getNuke() {
		return nuke;
	}
	public void setNuke(Nuke nuke) {
		this.nuke = nuke;
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
	public NukeClass getNg_nc() {
		return ng_nc;
	}
	public void setNg_nc(NukeClass ng_nc) {
		this.ng_nc = ng_nc;
	}
	public String getNg_name() {
		return ng_name;
	}
	public void setNg_name(String ng_name) {
		this.ng_name = ng_name;
	}
	public Goods getNg_goods() {
		return ng_goods;
	}
	public void setNg_goods(Goods ng_goods) {
		this.ng_goods = ng_goods;
	}
	public BigDecimal getNg_price() {
		return ng_price;
	}
	public void setNg_price(BigDecimal ng_price) {
		this.ng_price = ng_price;
	}
	public int getNg_count() {
		return ng_count;
	}
	public void setNg_count(int ng_count) {
		this.ng_count = ng_count;
	}
	public int getNg_nuke_count() {
		return ng_nuke_count;
	}
	public void setNg_nuke_count(int ng_nuke_count) {
		this.ng_nuke_count = ng_nuke_count;
	}
	public int getNg_selled_count() {
		return ng_selled_count;
	}
	public void setNg_selled_count(int ng_selled_count) {
		this.ng_selled_count = ng_selled_count;
	}
	public BigDecimal getNg_rebate() {
		return ng_rebate;
	}
	public void setNg_rebate(BigDecimal ng_rebate) {
		this.ng_rebate = ng_rebate;
	}
	public int getNg_status() {
		return ng_status;
	}
	public void setNg_status(int ng_status) {
		this.ng_status = ng_status;
	}
	public Date getNg_audit_time() {
		return ng_audit_time;
	}
	public void setNg_audit_time(Date ng_audit_time) {
		this.ng_audit_time = ng_audit_time;
	}
	public Boolean getNg_recommend() {
		return ng_recommend;
	}
	public void setNg_recommend(Boolean ng_recommend) {
		this.ng_recommend = ng_recommend;
	}
	public Date getNg_recommend_time() {
		return ng_recommend_time;
	}
	public void setNg_recommend_time(Date ng_recommend_time) {
		this.ng_recommend_time = ng_recommend_time;
	}
	public String getNg_content() {
		return ng_content;
	}
	public void setNg_content(String ng_content) {
		this.ng_content = ng_content;
	}
	public Accessory getNg_img() {
		return ng_img;
	}
	public void setNg_img(Accessory ng_img) {
		this.ng_img = ng_img;
	}
	public boolean isWeixin_shop_recommend() {
		return weixin_shop_recommend;
	}
	public void setWeixin_shop_recommend(boolean weixin_shop_recommend) {
		this.weixin_shop_recommend = weixin_shop_recommend;
	}
	public Date getWeixin_shop_recommendTime() {
		return weixin_shop_recommendTime;
	}
	public void setWeixin_shop_recommendTime(Date weixin_shop_recommendTime) {
		this.weixin_shop_recommendTime = weixin_shop_recommendTime;
	}
	public int getMobile_recommend() {
		return mobile_recommend;
	}
	public void setMobile_recommend(int mobile_recommend) {
		this.mobile_recommend = mobile_recommend;
	}
	public Date getMobile_recommendTime() {
		return mobile_recommendTime;
	}
	public void setMobile_recommendTime(Date mobile_recommendTime) {
		this.mobile_recommendTime = mobile_recommendTime;
	}
	public int getWeixin_recommend() {
		return weixin_recommend;
	}
	public void setWeixin_recommend(int weixin_recommend) {
		this.weixin_recommend = weixin_recommend;
	}
	public Date getWeixin_recommendTime() {
		return weixin_recommendTime;
	}
	public void setWeixin_recommendTime(Date weixin_recommendTime) {
		this.weixin_recommendTime = weixin_recommendTime;
	}
	public String getGoods_spec_id() {
		return goods_spec_id;
	}

	public void setGoods_spec_id(String goods_spec_id) {
		this.goods_spec_id = goods_spec_id;
	}

	public BigDecimal getOrigin_price() {
		return origin_price;
	}

	public void setOrigin_price(BigDecimal origin_price) {
		this.origin_price = origin_price;
	}

	public Integer getLimit_number() {
		return limit_number;
	}

	public void setLimit_number(Integer limit_number) {
		this.limit_number = limit_number;
	}
}
