package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: Channel.java
 * </p>
 * 
 * <p>
 * Description:频道
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
 * @date 2016-9-22
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "channel")
public class Channel extends IdEntity {
	private String ch_name;//频道名称
	@Column(columnDefinition = "LongText")
	private String ch_nav_gc_list;//频道中商品类型GoodsClass信息
	/**
	 * [
	 * 	{"href":"/","name":"商场大牌","seq":"1"},
	 *  {"href":"/","name":"精品女装","seq":"2"},
	 *  {"href":"/","name":"冬季尚新","seq":"3"},
	 *  {"href":"/","name":"明星同款","seq":"4"},
	 *  {"href":"/","name":"特价促销","seq":"6"},
	 *  {"href":"/","name":"百搭配饰","seq":"5"},
	 *  {"href":"/","name":"潮流搭配","seq":"7"}
	 * ]
	 */
	@Column(columnDefinition = "LongText")
	private String ch_nav_word_list;//频道导航名称集合
	private Long ch_nav_advp_id;//广告id
	@Column(columnDefinition = "LongText")
	private String ch_nav_right_img_list;//导航右侧图片
	@Column(columnDefinition = "LongText")
	private String ch_nav_bottom_adv_list;//导航底部广告
	@Column(columnDefinition = "LongText")
	private String ch_floors;//频道楼层
	private String ch_nav_style;//导航页面样式累心
	private int ch_sequence;//序号

	public int getCh_sequence() {
		return this.ch_sequence;
	}

	public void setCh_sequence(int ch_sequence) {
		this.ch_sequence = ch_sequence;
	}

	public String getCh_nav_style() {
		return this.ch_nav_style;
	}

	public void setCh_nav_style(String ch_nav_style) {
		this.ch_nav_style = ch_nav_style;
	}

	public String getCh_name() {
		return this.ch_name;
	}

	public void setCh_name(String ch_name) {
		this.ch_name = ch_name;
	}

	public String getCh_nav_gc_list() {
		return this.ch_nav_gc_list;
	}

	public void setCh_nav_gc_list(String ch_nav_gc_list) {
		this.ch_nav_gc_list = ch_nav_gc_list;
	}

	public String getCh_nav_word_list() {
		return this.ch_nav_word_list;
	}

	public void setCh_nav_word_list(String ch_nav_word_list) {
		this.ch_nav_word_list = ch_nav_word_list;
	}

	public Long getCh_nav_advp_id() {
		return this.ch_nav_advp_id;
	}

	public void setCh_nav_advp_id(Long ch_nav_advp_id) {
		this.ch_nav_advp_id = ch_nav_advp_id;
	}

	public String getCh_nav_right_img_list() {
		return this.ch_nav_right_img_list;
	}

	public void setCh_nav_right_img_list(String ch_nav_right_img_list) {
		this.ch_nav_right_img_list = ch_nav_right_img_list;
	}

	public String getCh_nav_bottom_adv_list() {
		return this.ch_nav_bottom_adv_list;
	}

	public void setCh_nav_bottom_adv_list(String ch_nav_bottom_adv_list) {
		this.ch_nav_bottom_adv_list = ch_nav_bottom_adv_list;
	}

	public String getCh_floors() {
		return this.ch_floors;
	}

	public void setCh_floors(String ch_floors) {
		this.ch_floors = ch_floors;
	}
}
