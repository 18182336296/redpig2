package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * Title: Luckydraw.java
 * </p>
 *
 * <p>
 * Description:抽奖活动实体类
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "luckydraw")
public class Luckydraw extends IdEntity {

    private String luckydraw_name;//'抽奖名称',
    private Date beginTime;//'开始时间',
    private Date endTime;//'结束时间',
    private String remark;//活动说明
    @ManyToOne(fetch = FetchType.LAZY)
    private UserLevel userLevel;//'会员等级',
    private Integer consume_integral;//'消耗积分数量',
    private Integer reward_integral;//'参与送积分的数量',
    private Integer is_parti_reward;//是否参与送积分,当勾选送未中奖的用户时，设为1，否则为0
    private Integer limit_number;//参与次数限制：1为一人一次，2为一天一次，3为一天两次
    private Integer win_rate;//中奖概率，精确到小数点后两位，如37.12%，存储为3712
    private Integer status;//抽奖状态，0为待审核，1为审核通过未开始, 2为审核通过已开始，-1为审核拒绝 -2为过期
    private String notwin_remark;//未中奖说明

    public String getLuckydraw_name() {
        return luckydraw_name;
    }

    public void setLuckydraw_name(String luckydraw_name) {
        this.luckydraw_name = luckydraw_name;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UserLevel getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(UserLevel userLevel) {
        this.userLevel = userLevel;
    }

    public Integer getConsume_integral() {
        return consume_integral;
    }

    public void setConsume_integral(Integer consume_integral) {
        this.consume_integral = consume_integral;
    }

    public Integer getReward_integral() {
        return reward_integral;
    }

    public void setReward_integral(Integer reward_integral) {
        this.reward_integral = reward_integral;
    }

    public Integer getIs_parti_reward() {
        return is_parti_reward;
    }

    public void setIs_parti_reward(Integer is_parti_reward) {
        this.is_parti_reward = is_parti_reward;
    }

    public Integer getLimit_number() {
        return limit_number;
    }

    public void setLimit_number(Integer limit_number) {
        this.limit_number = limit_number;
    }

    public Integer getWin_rate() {
        return win_rate;
    }

    public void setWin_rate(Integer win_rate) {
        this.win_rate = win_rate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNotwin_remark() {
        return notwin_remark;
    }

    public void setNotwin_remark(String notwin_remark) {
        this.notwin_remark = notwin_remark;
    }
}
