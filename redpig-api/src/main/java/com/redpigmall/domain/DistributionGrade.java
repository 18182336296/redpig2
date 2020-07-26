package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Auther: Administrator
 * @Date: 2018/8/27 11:37
 * @Description:
 */
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "distribution_grade")
public class DistributionGrade extends IdEntity {
    /**分销等级*/
    private String grade;
    /**累计客户数*/
    private int count_user;
    /**累计订单金额*/
    @Column(precision = 12, scale = 2)
    private BigDecimal count_price;
    /**下线累计订单金额*/
    @Column(precision = 12, scale = 2)
    private BigDecimal down_count_price;
    /**对内返利百分比*/
    private int inner_rebate;
    /**对外返利百分比*/
    private int out_rebate;
    /**操作人*/
    private Long operator;
    /**更新时间*/
    private Date update_time;

    public DistributionGrade() {
    }

    public DistributionGrade(Long id) {
        super.setId(id);
    }

    public DistributionGrade(Long id, Date addTime) {
        super(id, addTime);
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getCount_user() {
        return count_user;
    }

    public void setCount_user(int count_user) {
        this.count_user = count_user;
    }

    public BigDecimal getCount_price() {
        return count_price;
    }

    public void setCount_price(BigDecimal count_price) {
        this.count_price = count_price;
    }

    public BigDecimal getDown_count_price() {
        return down_count_price;
    }

    public void setDown_count_price(BigDecimal down_count_price) {
        this.down_count_price = down_count_price;
    }

    public int getInner_rebate() {
        return inner_rebate;
    }

    public void setInner_rebate(int inner_rebate) {
        this.inner_rebate = inner_rebate;
    }

    public int getOut_rebate() {
        return out_rebate;
    }

    public void setOut_rebate(int out_rebate) {
        this.out_rebate = out_rebate;
    }

    public Long getOperator() {
        return operator;
    }

    public void setOperator(Long operator) {
        this.operator = operator;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
