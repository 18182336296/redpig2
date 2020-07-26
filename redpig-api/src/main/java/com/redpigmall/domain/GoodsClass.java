package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.collect.Lists;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
import com.redpigmall.api.tools.CommUtil;
/**
 * <p>
 * Title: GoodsClass.java
 * </p>
 * 
 * <p>
 * Description: 商品分类管理类，用来管理商城商品分类信息
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
 * @date 2014-9-16
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({ "serial", "rawtypes" })
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "goodsclass")
public class GoodsClass extends IdEntity implements Comparable {
	private String className;//分类名称
	@OneToMany(mappedBy = "parent")
	@OrderBy("sequence asc")
	private List<GoodsClass> childs = Lists.newArrayList();//子类
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsClass parent;//父类
	@Column(columnDefinition = "int default 0")
	private Integer sequence=0;//序号
	@Column(columnDefinition = "int default 0")
	private Integer level=0;//等级
	private Boolean display;//是否显示
	private Boolean recommend;//推荐
	@ManyToOne(fetch = FetchType.LAZY)
	private GoodsType goodsType;//商品类型
	@Column(columnDefinition = "LongText")
	private String seo_keywords;//seo关键字
	@Column(columnDefinition = "LongText")
	private String seo_description;//seo描述
	@Column(columnDefinition = "int default 0")
	private Integer icon_type;//一级分类图标显示类型，0为系统图标，1为上传图标
	private String icon_sys;//系统图标
	@OneToOne(fetch = FetchType.LAZY)
	private Accessory icon_acc;//上传图标
	@Column(precision = 12, scale = 2)
	private BigDecimal commission_rate;//佣金比例
	@Column(precision = 12, scale = 0)
	private BigDecimal guarantee;//保证金
	@ManyToMany(mappedBy = "spec_goodsClass_detail")
	private List<GoodsSpecification> spec_detail = Lists.newArrayList();//作为详细类目对应的规格属性
	@Column(precision = 4, scale = 1)
	private BigDecimal description_evaluate;//商品分类描述相符评分，同行业均分
	@Column(precision = 4, scale = 1)
	private BigDecimal service_evaluate;//商品分类服务态度评价，同行业均分
	@Column(precision = 4, scale = 1)
	private BigDecimal ship_evaluate;// 商品分类发货速度评价，同行业均分
	@Column(columnDefinition = "LongText")
	private String gc_advert;//分类广告，使用json管理{"acc_id":1,"acc_url":"www.xxx.com","adv_id":1,"adv_type":0},adv_type广告类型，0为自定义广告，1为系统广告，adv_id广告位id
	@Column(columnDefinition = "LongText")
	private String gb_info;//分类弹出层显示品牌信息，使用json管理[{"id":2,"name":"阿迪"},"id":2,"name":"阿迪"}]
	@Column(columnDefinition = "int default 0")
	private Integer mobile_recommend;//手机端推荐
	@Temporal(TemporalType.DATE)
	private Date mobile_recommendTime;//手机端推荐时间
	private String app_icon;// 移动端一级分类的图标
	private String gc_color;// 分类底层颜色
	private String wx_icon;// 微商城一级分类的图标
	private Long showClass_id;//显示分类id

	public Long getShowClass_id() {
		return this.showClass_id;
	}

	public void setShowClass_id(Long showClass_id) {
		this.showClass_id = showClass_id;
	}

	public GoodsClass(Long id, Date addTime) {
		super(id, addTime);
	}

	public Integer getMobile_recommend() {
		return this.mobile_recommend;
	}

	public void setMobile_recommend(Integer mobile_recommend) {
		this.mobile_recommend = mobile_recommend;
	}

	public Date getMobile_recommendTime() {
		return this.mobile_recommendTime;
	}

	public void setMobile_recommendTime(Date mobile_recommendTime) {
		this.mobile_recommendTime = mobile_recommendTime;
	}

	public GoodsClass(Long id, String className) {
		super.setId(id);
		setClassName(className);
	}

	public GoodsClass() {
	}

	public String getWx_icon() {
		return this.wx_icon;
	}

	public void setWx_icon(String wx_icon) {
		this.wx_icon = wx_icon;
	}

	public String getApp_icon() {
		return this.app_icon;
	}

	public void setApp_icon(String app_icon) {
		this.app_icon = app_icon;
	}

	public String getGb_info() {
		return this.gb_info;
	}

	public void setGb_info(String gb_info) {
		this.gb_info = gb_info;
	}

	public String getGc_color() {
		return this.gc_color;
	}

	public void setGc_color(String gc_color) {
		this.gc_color = gc_color;
	}

	public String getGc_advert() {
		return this.gc_advert;
	}

	public void setGc_advert(String gc_advert) {
		this.gc_advert = gc_advert;
	}

	public List<GoodsSpecification> getSpec_detail() {
		return this.spec_detail;
	}

	public void setSpec_detail(List<GoodsSpecification> spec_detail) {
		this.spec_detail = spec_detail;
	}

	public BigDecimal getCommission_rate() {
		return this.commission_rate;
	}

	public void setCommission_rate(BigDecimal commission_rate) {
		this.commission_rate = commission_rate;
	}

	public Integer getIcon_type() {
		return this.icon_type;
	}

	public void setIcon_type(Integer icon_type) {
		this.icon_type = icon_type;
	}

	public String getIcon_sys() {
		return this.icon_sys;
	}

	public void setIcon_sys(String icon_sys) {
		this.icon_sys = icon_sys;
	}

	public Accessory getIcon_acc() {
		return this.icon_acc;
	}

	public void setIcon_acc(Accessory icon_acc) {
		this.icon_acc = icon_acc;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Integer getSequence() {
		if(this.sequence == null){
			this.sequence =0;
		}
		return this.sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Boolean getDisplay() {
		return this.display;
	}
	
	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public Boolean getRecommend() {
		return this.recommend;
	}

	public void setRecommend(Boolean recommend) {
		this.recommend = recommend;
	}

	public GoodsType getGoodsType() {
		return this.goodsType;
	}

	public void setGoodsType(GoodsType goodsType) {
		this.goodsType = goodsType;
	}

	public GoodsClass getParent() {
		return this.parent;
	}

	public void setParent(GoodsClass parent) {
		this.parent = parent;
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

	public BigDecimal getDescription_evaluate() {
		return this.description_evaluate;
	}

	public void setDescription_evaluate(BigDecimal description_evaluate) {
		this.description_evaluate = description_evaluate;
	}

	public BigDecimal getService_evaluate() {
		return this.service_evaluate;
	}

	public void setService_evaluate(BigDecimal service_evaluate) {
		this.service_evaluate = service_evaluate;
	}

	public BigDecimal getShip_evaluate() {
		return this.ship_evaluate;
	}

	public void setShip_evaluate(BigDecimal ship_evaluate) {
		this.ship_evaluate = ship_evaluate;
	}

	public BigDecimal getGuarantee() {
		return this.guarantee;
	}

	public void setGuarantee(BigDecimal guarantee) {
		this.guarantee = guarantee;
	}

	public List<GoodsClass> getChilds() {
		return this.childs;
	}

	public void setChilds(List<GoodsClass> childs) {
		this.childs = childs;
	}

	public int compareTo(Object obj) {
		if (obj != null) {
			GoodsClass gc = (GoodsClass) obj;
			return CommUtil.null2Int(Long.valueOf(getId().longValue()
					- gc.getId().longValue()));
		}
		return 0;
	}
}
