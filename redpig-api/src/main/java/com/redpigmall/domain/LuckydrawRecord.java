package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * <p>
 * Title: LuckydrawRecord.java
 * </p>
 *
 * <p>
 * Description:对应的用户抽奖记录
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "luckydraw_record")
public class LuckydrawRecord extends IdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;//对应的用户
    @ManyToOne(fetch = FetchType.LAZY)
    private Luckydraw luckydraw;//对应的抽奖活动
    private Integer timer;//用户的第几次抽奖
    private Integer is_win;//是否中奖，0为未中奖，1为已中奖
    @ManyToOne(fetch = FetchType.LAZY)
    private LuckydrawReward luckydrawReward;//对应的奖品信息
    private Integer reward_amout;//中奖数量
    private Integer status;//状态
    private String remark;//抽奖记录说明

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Luckydraw getLuckydraw() {
        return luckydraw;
    }

    public void setLuckydraw(Luckydraw luckydraw) {
        this.luckydraw = luckydraw;
    }

    public Integer getTimer() {
        return timer;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    public Integer getIs_win() {
        return is_win;
    }

    public void setIs_win(Integer is_win) {
        this.is_win = is_win;
    }

    public LuckydrawReward getLuckydrawReward() {
        return luckydrawReward;
    }

    public void setLuckydrawReward(LuckydrawReward luckydrawReward) {
        this.luckydrawReward = luckydrawReward;
    }

    public Integer getReward_amout() {
        return reward_amout;
    }

    public void setReward_amout(Integer reward_amout) {
        this.reward_amout = reward_amout;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
