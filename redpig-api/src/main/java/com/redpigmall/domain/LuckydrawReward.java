package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

/**
 * <p>
 * Title: LuckydrawReward.java
 * </p>
 *
 * <p>
 * Description:抽奖活动对应的奖品实体类
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
 * @date 2018-9-9
 *
 *
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "luckydraw_reward")
public class LuckydrawReward extends IdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Luckydraw luckydraw;//对应的抽奖活动名称
    private Integer grade;//奖品等级，奖品等级，对应tab页的标签顺序
    private Integer reward_type;//奖品类型：1为积分，2为优惠券，3为赠品,
    private Integer integral_number;//如果是积分，赠送的积分数量
    @OneToOne(fetch = FetchType.LAZY)
    private Coupon coupon;//如果是优惠券，在这里关联优惠券的id
    @OneToOne(fetch = FetchType.LAZY)
    private Goods goods;//如果是赠品，在这里关联商品的id
    private Integer reward_amout;//奖品份数
    private String remark;//参与限制次数

    public Luckydraw getLuckydraw() {
        return luckydraw;
    }

    public void setLuckydraw(Luckydraw luckydraw) {
        this.luckydraw = luckydraw;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public Integer getReward_type() {
        return reward_type;
    }

    public void setReward_type(Integer reward_type) {
        this.reward_type = reward_type;
    }

    public Integer getIntegral_number() {
        return integral_number;
    }

    public void setIntegral_number(Integer integral_number) {
        this.integral_number = integral_number;
    }

    public Coupon getCoupon() {
        return coupon;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Integer getReward_amout() {
        return reward_amout;
    }

    public void setReward_amout(Integer reward_amout) {
        this.reward_amout = reward_amout;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
