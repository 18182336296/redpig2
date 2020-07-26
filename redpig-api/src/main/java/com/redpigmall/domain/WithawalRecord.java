package com.redpigmall.domain;

import java.util.Date;

public class WithawalRecord {
	/**
	 * 主键
	 */
    private Long id;
    /**
     * 申请人ID
     */
    private Long userId;
    /**
     * 提现方式（提现方式：1：支付宝；2：微信；3：银联；与提现设置中的一致）
     */
    private Byte withdrawalWay;
    /**
     * 申请时间
     */
    private Date createTime;
    /**
     * 受理人ID
     */
    private Long acceptId;
    /**
     * 受理人名称
     */
    private String receiver;
    /**
     * 受理状态（受理状态：1未受理；2已受理（默认未受理））
     */
    private Byte acceptStatus;
    /**
     * 受理时间
     */
    private Date acceptTime;
    /**
     * 更新时间（不需要插入或者更新此字段。数据库会自动更新）
     */
    private Date updateTime;
    /**
     * 提现状态（提现状态：1：未提现；2：提现成功；3:：提现失败）
     */
    private Byte withdrawalStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getWithdrawalWay() {
        return withdrawalWay;
    }

    public void setWithdrawalWay(Byte withdrawalWay) {
        this.withdrawalWay = withdrawalWay;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getAcceptId() {
        return acceptId;
    }

    public void setAcceptId(Long acceptId) {
        this.acceptId = acceptId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver == null ? null : receiver.trim();
    }

    public Byte getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(Byte acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public Byte getWithdrawalStatus() {
		return withdrawalStatus;
	}

	public void setWithdrawalStatus(Byte withdrawalStatus) {
		this.withdrawalStatus = withdrawalStatus;
	}
    
}