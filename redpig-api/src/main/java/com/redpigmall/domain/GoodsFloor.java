package com.redpigmall.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: GoodsFloor.java
 * </p>
 * 
 * <p>
 * Description: 首页楼层管理类,首页楼层商城管理员可以使用拖拽式管理完成楼层配置，商城首页按照配置的楼层信息显示对应的商品、品牌、广告
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
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goods_floor")
public class GoodsFloor extends IdEntity {
	private String gf_name;// 分类名称
	/**
	 * 老版本:楼层色调，目前系统提供4种色调
	 * 新版本(京东模式):楼层样式,1~12 ,brand
	 */
	private String gf_css;
	
	/**
	 * 楼层样式:
	 * 1、style1、style2为老版本使用的【楼层tab】两种样式
	 * 2、style_jd_floor_1、style_jd_floor_2为新版本的楼层样式
	 * 3、style_jd_tab_1、style_jd_tab_2、style_jd_tab_3、style_jd_tab_4为新版本的【楼层tab】样式
	 */
	@Column(columnDefinition = "varchar(255) default 'style1'")
	private String gf_style;
	private int gf_sequence;// 分类序号，升序排列
	private int gf_goods_count;// 显示商品个数
	@OneToMany(mappedBy = "parent", cascade = { javax.persistence.CascadeType.REMOVE })
	@OrderBy("gf_sequence asc")
	private List<GoodsFloor> childs = Lists.newArrayList();
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsFloor parent;// 父级分类
	private int gf_level;// 层级
	private Boolean gf_display;// 是否显示
	@ManyToOne(fetch = FetchType.LAZY)
	private Accessory icon;// 楼层导航小图标,24*24
	@Column(columnDefinition = "LongText")
	private String gf_gc_list;// 楼层显示分类商品列表,使用json管理[{"pid":1,"gc_id":1,"gc_id":2},{"pid":1,"gc_id":1,"gc_id":2}]
	@Column(columnDefinition = "LongText")
	private String gf_gc_goods;// 楼层显示分类商品,使用JSON管理{"goods_id1":1,"goods_id2":32768}
	
	@Column(columnDefinition = "LongText")
	private String gf_list_goods;// 楼层排行部分显示商品，使用JSON管理{"goods_id":1,"goods_id":32768}
	
	@Column(columnDefinition = "LongText")
	private String gf_left_adv;// 楼层左侧广告，json管理{"acc_id":1,"acc_url":"www.redpigmall.com","adv_id":1}
	@Column(columnDefinition = "LongText")
	private String gf_right_adv;// 楼层右侧广告，json管理{"acc_id":1,"acc_url":"www.redpigmall.com","adv_id":1}
	@Column(columnDefinition = "LongText")
	private String gf_brand_list;// 楼层品牌信息，json管理{"brand_id1":1,"brand_id2":2}
	@Column(columnDefinition = "LongText")
	private String gf_style2_goods;// style2样式显示9个图像,可以是商品，图片，广告，格式为[{"module_id":1,"type":"goods","goods_id":1,"img_url":"xxxx","goods_price":xxx,"store_price":xxxx,"href_url":"xxxx"},{"type":"adv","adv_id":1},{"type":"img","img_id":1,"img_url":"xxxx"}]
	
	@Column(columnDefinition = "int default 0")
	private int gf_type;//商品楼层类型
	private String wide_template;
	@Column(columnDefinition = "LongText")
	private String wide_adv_brand;
	@Column(columnDefinition = "LongText")
	private String wide_adv_rectangle_four;
	@Column(columnDefinition = "LongText")
	private String wide_adv_square_four;
	@Column(columnDefinition = "LongText")
	private String wide_goods;
	@Column(columnDefinition = "LongText")
	private String wide_adv_five;
	@Column(columnDefinition = "LongText")
	private String wide_adv_eight;
	
	public String getWide_adv_brand() {
		return this.wide_adv_brand;
	}

	public void setWide_adv_brand(String wide_adv_brand) {
		this.wide_adv_brand = wide_adv_brand;
	}

	public String getWide_adv_rectangle_four() {
		return this.wide_adv_rectangle_four;
	}

	public void setWide_adv_rectangle_four(String wide_adv_rectangle_four) {
		this.wide_adv_rectangle_four = wide_adv_rectangle_four;
	}

	public String getWide_adv_square_four() {
		return this.wide_adv_square_four;
	}

	public void setWide_adv_square_four(String wide_adv_square_four) {
		this.wide_adv_square_four = wide_adv_square_four;
	}

	public String getWide_goods() {
		return this.wide_goods;
	}

	public void setWide_goods(String wide_goods) {
		this.wide_goods = wide_goods;
	}

	public String getWide_adv_five() {
		return this.wide_adv_five;
	}

	public void setWide_adv_five(String wide_adv_five) {
		this.wide_adv_five = wide_adv_five;
	}

	public String getWide_adv_eight() {
		return this.wide_adv_eight;
	}

	public void setWide_adv_eight(String wide_adv_eight) {
		this.wide_adv_eight = wide_adv_eight;
	}

	public String getWide_template() {
		return this.wide_template;
	}

	public void setWide_template(String wide_template) {
		this.wide_template = wide_template;
	}

	public GoodsFloor() {
	}
	
	public int getGf_type() {
		return this.gf_type;
	}

	public void setGf_type(int gf_type) {
		this.gf_type = gf_type;
	}
	public String getGf_style2_goods() {
		return this.gf_style2_goods;
	}

	public void setGf_style2_goods(String gf_style2_goods) {
		this.gf_style2_goods = gf_style2_goods;
	}

	public String getGf_style() {
		return this.gf_style;
	}

	public void setGf_style(String gf_style) {
		this.gf_style = gf_style;
	}

	public Accessory getIcon() {
		return this.icon;
	}

	public void setIcon(Accessory icon) {
		this.icon = icon;
	}

	public String getGf_brand_list() {
		return this.gf_brand_list;
	}

	public void setGf_brand_list(String gf_brand_list) {
		this.gf_brand_list = gf_brand_list;
	}

	public String getGf_name() {
		return this.gf_name;
	}

	public void setGf_name(String gf_name) {
		this.gf_name = gf_name;
	}

	public String getGf_css() {
		return this.gf_css;
	}

	public void setGf_css(String gf_css) {
		this.gf_css = gf_css;
	}

	public int getGf_sequence() {
		return this.gf_sequence;
	}

	public void setGf_sequence(int gf_sequence) {
		this.gf_sequence = gf_sequence;
	}

	public int getGf_goods_count() {
		return this.gf_goods_count;
	}

	public void setGf_goods_count(int gf_goods_count) {
		this.gf_goods_count = gf_goods_count;
	}

	public List<GoodsFloor> getChilds() {
		return this.childs;
	}

	public void setChilds(List<GoodsFloor> childs) {
		this.childs = childs;
	}

	public GoodsFloor getParent() {
		return this.parent;
	}

	public void setParent(GoodsFloor parent) {
		this.parent = parent;
	}

	public int getGf_level() {
		return this.gf_level;
	}

	public void setGf_level(int gf_level) {
		this.gf_level = gf_level;
	}

	public Boolean getGf_display() {
		return this.gf_display;
	}

	public void setGf_display(Boolean gf_display) {
		this.gf_display = gf_display;
	}

	public String getGf_gc_list() {
		return this.gf_gc_list;
	}

	public void setGf_gc_list(String gf_gc_list) {
		this.gf_gc_list = gf_gc_list;
	}

	public String getGf_gc_goods() {
		return this.gf_gc_goods;
	}

	public void setGf_gc_goods(String gf_gc_goods) {
		this.gf_gc_goods = gf_gc_goods;
	}

	public String getGf_list_goods() {
		return this.gf_list_goods;
	}

	public void setGf_list_goods(String gf_list_goods) {
		this.gf_list_goods = gf_list_goods;
	}

	public String getGf_left_adv() {
		return this.gf_left_adv;
	}

	public void setGf_left_adv(String gf_left_adv) {
		this.gf_left_adv = gf_left_adv;
	}

	public String getGf_right_adv() {
		return this.gf_right_adv;
	}

	public void setGf_right_adv(String gf_right_adv) {
		this.gf_right_adv = gf_right_adv;
	}

}
