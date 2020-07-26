package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * <p>
 * Title: CollageBuy.java
 * </p>
 *
 * <p>
 * Description:拼团实体类
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
 * @date 2018-9-5
 *
 *
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "collage_buy")
public class CollageBuy extends IdEntity {
    private String collage_name;//'拼团名称',
    private Integer collage_person_set;//需要多少人成团
    private Date beginTime;//'开始时间',
    private Date endTime;//'结束时间',
    @ManyToOne(fetch = FetchType.LAZY)
    private Goods goods;//'拼团的商品',
    @Column(columnDefinition = "LongText")
    private String cg_content;//'拼团商品描述',
    private Integer cg_total_count;//'拼团商品库存数量',
    private Integer cg_collage_count;//'已经拼团的商品数量',
    @Column(columnDefinition = "int default 0")
    private int cg_selled_count;//'已经售出的数量',（暂时不用）
    private String cg_name;//'拼团的商品名称',
    @Column(precision = 12, scale = 2)
    private BigDecimal cg_price;//'拼团价',
    @Column(precision = 12, scale = 2)
    private BigDecimal cg_discount;//'拼团折扣率',
    @Column(columnDefinition = "int default 0")
    private Integer cg_recommend;//'推荐状态，0为未推荐，1为推荐，推荐拼团在首页显示'',',
    @Temporal(TemporalType.DATE)
    private Date cg_recommend_time;//'拼团推荐时间',
    private int cg_status;//'拼团状态，0为待审核，1为审核通过未开始 2为审核通过已开始，-1为审核拒绝 -2为过期',
    @Column(columnDefinition = "int default 0")
    private Integer mobile_recommend;//'是否为手机推荐，0为否，1为是',
    @Temporal(TemporalType.DATE)
    private Date mobile_recommend_time;//'手机推荐时间',
    @Column(columnDefinition = "int default 0")
    private int weixin_recommend;//'是否为微信推荐，0为否，1为是',
    @Temporal(TemporalType.DATE)
    private Date weixin_recommend_time;//'微信推荐时间',
    private int collage_type;//'拼团类型',
    private Integer limit_number;//限购份数
    private Integer timeout;//超时时间，以分钟计算
    private String remark;//备注
    private String goods_spec_id;// 商品规格id，格式为：“527_或527_628_”，一对一，一个拼团活动的商品只能有一个规格
    @Column(precision = 12, scale = 2)
    private BigDecimal origin_price;// 设置拼团时先存商品原价，便于拼团活动后恢复原价

    public String getCollage_name() {
        return collage_name;
    }

    public void setCollage_name(String collage_name) {
        this.collage_name = collage_name;
    }

    public Integer getCollage_person_set() {
        return collage_person_set;
    }

    public void setCollage_person_set(Integer collage_person_set) {
        this.collage_person_set = collage_person_set;
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

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public String getCg_content() {
        return cg_content;
    }

    public void setCg_content(String cg_content) {
        this.cg_content = cg_content;
    }

    public Integer getCg_total_count() {
        return cg_total_count;
    }

    public void setCg_total_count(Integer cg_total_count) {
        this.cg_total_count = cg_total_count;
    }

    public Integer getCg_collage_count() {
        return cg_collage_count;
    }

    public void setCg_collage_count(Integer cg_collage_count) {
        this.cg_collage_count = cg_collage_count;
    }

    public String getCg_name() {
        return cg_name;
    }

    public void setCg_name(String cg_name) {
        this.cg_name = cg_name;
    }

    public BigDecimal getCg_price() {
        return cg_price;
    }

    public void setCg_price(BigDecimal cg_price) {
        this.cg_price = cg_price;
    }

    public BigDecimal getCg_discount() {
        return cg_discount;
    }

    public void setCg_discount(BigDecimal cg_discount) {
        this.cg_discount = cg_discount;
    }

    public Integer getCg_recommend() {
        return cg_recommend;
    }

    public void setCg_recommend(Integer cg_recommend) {
        this.cg_recommend = cg_recommend;
    }

    public Date getCg_recommend_time() {
        return cg_recommend_time;
    }

    public void setCg_recommend_time(Date cg_recommend_time) {
        this.cg_recommend_time = cg_recommend_time;
    }

    public int getCg_selled_count() {
        return cg_selled_count;
    }

    public void setCg_selled_count(int cg_selled_count) {
        this.cg_selled_count = cg_selled_count;
    }

    public int getCg_status() {
        return cg_status;
    }

    public void setCg_status(int cg_status) {
        this.cg_status = cg_status;
    }

    public Integer getMobile_recommend() {
        return mobile_recommend;
    }

    public void setMobile_recommend(Integer mobile_recommend) {
        this.mobile_recommend = mobile_recommend;
    }

    public Date getMobile_recommend_time() {
        return mobile_recommend_time;
    }

    public void setMobile_recommend_time(Date mobile_recommend_time) {
        this.mobile_recommend_time = mobile_recommend_time;
    }

    public int getWeixin_recommend() {
        return weixin_recommend;
    }

    public void setWeixin_recommend(int weixin_recommend) {
        this.weixin_recommend = weixin_recommend;
    }

    public Date getWeixin_recommend_time() {
        return weixin_recommend_time;
    }

    public void setWeixin_recommend_time(Date weixin_recommend_time) {
        this.weixin_recommend_time = weixin_recommend_time;
    }

    public int getCollage_type() {
        return collage_type;
    }

    public void setCollage_type(int collage_type) {
        this.collage_type = collage_type;
    }

    public Integer getLimit_number() {
        return limit_number;
    }

    public void setLimit_number(Integer limit_number) {
        this.limit_number = limit_number;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
}
