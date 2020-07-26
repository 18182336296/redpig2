package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;

public class WithdrawalSetting {
    private Long id;

    private BigDecimal quota;

    private Date withdrawalBeginDate;

    private Date withdrawalEndDate;

    private String withdrawalWay;

    private Date updateTime;
    
    private Byte isDistribeWithdrawal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getQuota() {
        return quota;
    }

    public void setQuota(BigDecimal quota) {
        this.quota = quota;
    }

    public Date getWithdrawalBeginDate() {
        return withdrawalBeginDate;
    }

    public void setWithdrawalBeginDate(Date withdrawalBeginDate) {
        this.withdrawalBeginDate = withdrawalBeginDate;
    }

    public Date getWithdrawalEndDate() {
        return withdrawalEndDate;
    }

    public void setWithdrawalEndDate(Date withdrawalEndDate) {
        this.withdrawalEndDate = withdrawalEndDate;
    }

    public String getWithdrawalWay() {
        return withdrawalWay;
    }

    public void setWithdrawalWay(String withdrawalWay) {
        this.withdrawalWay = withdrawalWay;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public Byte getIsDistribeWithdrawal() {
		return isDistribeWithdrawal;
	}

	public void setIsDistribeWithdrawal(Byte isDistribeWithdrawal) {
		this.isDistribeWithdrawal = isDistribeWithdrawal;
	}
    
    
}