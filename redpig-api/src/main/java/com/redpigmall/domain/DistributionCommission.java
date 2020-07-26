package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * csh
 * 分销拥金
 */
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "distribution_commission")
public class DistributionCommission extends IdEntity {
    /**分销商id*/
    private User user;
    /**分销商名称*/
    private String user_name;
    /**剩余可提现金额*/
    @Column(precision = 12, scale = 2,columnDefinition = "BigDecimal default 0")
    private BigDecimal balance_price;
    /**总提现*/
    @Column(precision = 12, scale = 2,columnDefinition = "BigDecimal default 0")
    private BigDecimal sum_price;
    /**更新时间*/
    private Date update_time;
    /**分销等级id*/
    private Long grade_id;
    /**分销等级名称*/
    private String grade;
    /**昵称*/
    private String nickName;
    /**累计成交笔数*/
    private Integer sum_order;
    /**累计客户数*/
    private Integer sum_user;
    /**下线累计成交金额*/
    @Column(precision = 12, scale = 2,columnDefinition = "BigDecimal default 0")
    private BigDecimal down_sum_price;
    /**累计成交金额*/
    @Column(precision = 12, scale = 2,columnDefinition = "BigDecimal default 0")
    private BigDecimal sum_deal_price;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public BigDecimal getBalance_price() {
        return balance_price;
    }

    public void setBalance_price(BigDecimal balance_price) {
        this.balance_price = balance_price;
    }

    public BigDecimal getSum_price() {
        return sum_price;
    }

    public void setSum_price(BigDecimal sum_price) {
        this.sum_price = sum_price;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getSum_order() {
        return sum_order;
    }

    public void setSum_order(Integer sum_order) {
        this.sum_order = sum_order;
    }

    public Integer getSum_user() {
        return sum_user;
    }

    public void setSum_user(Integer sum_user) {
        this.sum_user = sum_user;
    }

    public BigDecimal getDown_sum_price() {
        return down_sum_price;
    }

    public void setDown_sum_price(BigDecimal down_sum_price) {
        this.down_sum_price = down_sum_price;
    }

    public BigDecimal getSum_deal_price() {
        return sum_deal_price;
    }

    public void setSum_deal_price(BigDecimal sum_deal_price) {
        this.sum_deal_price = sum_deal_price;
    }

    public DistributionCommission() {
    }

    public DistributionCommission(Long id) {
        super.setId(id);
    }

    public DistributionCommission(Long id, Date addTime) {
        super(id, addTime);
    }

    public Long getGrade_id() {
        return grade_id;
    }

    public void setGrade_id(Long grade_id) {
        this.grade_id = grade_id;
    }
}
