package com.redpigmall.domain;

import javax.persistence.Column;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: ChannelFloor.java
 * </p>
 * 
 * <p>
 * Description:频道楼层
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "channel_floor")
public class ChannelFloor extends IdEntity {
	private String cf_name;//楼层名称
	private String cf_style;//楼层样式累心
	@Column(columnDefinition = "int default 0")
	private int cf_sequence;//序号
	@Column(columnDefinition = "int default 0")
	private int cf_type;//楼层累心
	private Long cf_ch_id;//楼层频道ID
	/**
	 * [{"id":"1","name":"风衣","on":"1","show":"true","url":"/"},
	 * {"id":"2","name":"羽绒服","on":"2","show":"true","url":"/"},
	 * {"id":"3","name":"短外套","on":"3","show":"true","url":"/"},
	 * {"id":"4","name":"皮草","on":"4","show":"true","url":"/"}
	 * ]
	 */
	@Column(columnDefinition = "LongText")
	private String cf_extra_list;//分类/品牌名称
	/**
	 * [{"img_id":7394,"img_url":"/","photo_url":"/upload/channelfloor/57d928b4-ac54-4efa-b06a-f550d3b6ab45.jpg","position":"1","type":"img"},
	 * {"before_price":128.00,"goods_id":"34","goods_name":"春装气质韩范宽松长袖中长款牛仔衬衫女牛仔衬衣牛仔衣韩国外套潮","goods_price":100.00,"img_id":3747,"photo_url":"/upload/store/1/1dd0e781-9a15-48ef-82cc-16a1ae1a2200.jpg","position":"2","type":"goods"},
	 * {"before_price":89.00,"goods_id":"43","goods_name":"秋冬长裙半身裙加厚羊毛呢格子半裙复古文艺A字冬裙高腰大摆裙子","goods_price":78.00,"img_id":3989,"photo_url":"/upload/store/1/50135c20-d9e8-4666-ae67-51c853484007.jpg","position":"3","type":"goods"},
	 * {"before_price":50000.00,"goods_id":"46","goods_name":"冬季新款水貂皮草整貂貂皮大衣女士裘皮中长款带帽外套","goods_price":4800.00,"img_id":4072,"photo_url":"/upload/store/1/676fdcac-eecf-4163-a149-b2dc1ef84d85.jpg","position":"4","type":"goods"},
	 * {"before_price":600,"goods_id":"69","goods_name":"天然玉石缅甸老坑A货翡翠吊坠 女款冰种阳绿平安无事牌玉坠戴证书","goods_price":484,"img_id":4460,"photo_url":"/upload/store/1/10a4a7c0-37f8-4f85-8012-e87de66285f8.jpg","position":"5","type":"goods"},
	 * {"before_price":228.00,"goods_id":"64","goods_name":"韩国女士手表奢华水钻女表精钢时尚防水钢带手表女学生镶钻石英表","goods_price":108.00,"img_id":4356,"photo_url":"/upload/store/1/83464dc9-b33a-4df6-b7ca-e06a32d20e03.jpg","position":"6","type":"goods"},
	 * {"before_price":300.00,"goods_id":"4","goods_name":"SHOCK AMIU吃了膨化剂的双层内胆面包服棉服","goods_price":248.00,"img_id":3440,"photo_url":"/upload/store/1/2ac32f79-edd9-47a4-ab0e-918484aa0e7a.jpg","position":"7","type":"goods"}]
	 */
	@Column(columnDefinition = "LongText")
	private String cf_centent_list;//楼层内容

	public Long getCf_ch_id() {
		return this.cf_ch_id;
	}

	public void setCf_ch_id(Long cf_ch_id) {
		this.cf_ch_id = cf_ch_id;
	}

	public String getCf_style() {
		return this.cf_style;
	}

	public void setCf_style(String cf_style) {
		this.cf_style = cf_style;
	}

	public String getCf_extra_list() {
		return this.cf_extra_list;
	}

	public void setCf_extra_list(String cf_extra_list) {
		this.cf_extra_list = cf_extra_list;
	}

	public String getCf_centent_list() {
		return this.cf_centent_list;
	}

	public void setCf_centent_list(String cf_centent_list) {
		this.cf_centent_list = cf_centent_list;
	}

	public String getCf_name() {
		return this.cf_name;
	}

	public void setCf_name(String cf_name) {
		this.cf_name = cf_name;
	}

	public int getCf_sequence() {
		return this.cf_sequence;
	}

	public void setCf_sequence(int cf_sequence) {
		this.cf_sequence = cf_sequence;
	}

	public int getCf_type() {
		return this.cf_type;
	}

	public void setCf_type(int cf_type) {
		this.cf_type = cf_type;
	}
}
