package com.redpigmall.domain;

import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: ShowClass.java
 * </p>
 * 
 * <p>
 * Description:展示类目
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
 * @date 2014-11-11
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "showclass")
public class ShowClass extends IdEntity {
	private String showName;//类目名称
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@OneToMany(mappedBy = "parent")
	@OrderBy("sequence asc")
	private Set<ShowClass> childs = new TreeSet();//子类
	@ManyToOne(fetch = FetchType.LAZY)
	private ShowClass parent;//父类
	private int sequence;//序号
	private int level;//等级
	@Column(columnDefinition = "int default 0")
	private int show_type;//展示类型
	private String url;//地址
	private Long channel_id;//频道id
	private boolean display;//是否显示
	private boolean recommend;//是否推荐
	@Column(columnDefinition = "int default 0")
	private int icon_type;//一级分类图标显示类型，0为系统图标，1为上传图标
	private String icon_sys;//系统图标
	private Long photo_id;//图片id
	private String sc_color;//分类颜色
	@Column(columnDefinition = "LongText")
	private String sc_advert;//分类广告
	@Column(columnDefinition = "LongText")
	private String scb_info;//分类品牌
	@Column(columnDefinition = "LongText")
	private String seo_keywords;//seo关键字
	@Column(columnDefinition = "LongText")
	private String seo_description;//seo描述
	private Long weixinChannel_id;//微信频道id

	public Long getWeixinChannel_id() {
		return this.weixinChannel_id;
	}

	public void setWeixinChannel_id(Long weixinChannel_id) {
		this.weixinChannel_id = weixinChannel_id;
	}

	public String getShowName() {
		return this.showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Set<ShowClass> getChilds() {
		return this.childs;
	}

	public void setChilds(Set<ShowClass> childs) {
		this.childs = childs;
	}

	public ShowClass getParent() {
		return this.parent;
	}

	public void setParent(ShowClass parent) {
		this.parent = parent;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getChannel_id() {
		return this.channel_id;
	}

	public void setChannel_id(Long channel_id) {
		this.channel_id = channel_id;
	}

	public int getShow_type() {
		return this.show_type;
	}

	public void setShow_type(int show_type) {
		this.show_type = show_type;
	}
	
	public boolean getDisplay() {
		return this.display;
	}
	
	public void setDisplay(boolean display) {
		this.display = display;
	}

	public boolean getRecommend() {
		return this.recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}

	public int getIcon_type() {
		return this.icon_type;
	}

	public void setIcon_type(int icon_type) {
		this.icon_type = icon_type;
	}

	public String getIcon_sys() {
		return this.icon_sys;
	}

	public void setIcon_sys(String icon_sys) {
		this.icon_sys = icon_sys;
	}

	public Long getPhoto_id() {
		return this.photo_id;
	}

	public void setPhoto_id(Long photo_id) {
		this.photo_id = photo_id;
	}

	public String getSc_color() {
		return this.sc_color;
	}

	public void setSc_color(String sc_color) {
		this.sc_color = sc_color;
	}

	public String getSc_advert() {
		return this.sc_advert;
	}

	public void setSc_advert(String sc_advert) {
		this.sc_advert = sc_advert;
	}

	public String getScb_info() {
		return this.scb_info;
	}

	public void setScb_info(String scb_info) {
		this.scb_info = scb_info;
	}

	public String getSeo_keywords() {
		return this.seo_keywords;
	}

	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}

	public String getSeo_description() {
		return this.seo_description;
	}

	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}
}
