/**   
 * Copyright © 2018 eSunny Info. Tech Ltd. All rights reserved.
 * @Package: com.redpigmall.domain.virtual 
 * @author: zxq@yihexinda.com  
 * @date: 2018年9月13日 下午7:02:37 
 */
package com.redpigmall.domain.virtual;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName WithdrawRecordView
 * @author zxq@yihexinda.com
 * @date 2018年9月13日 下午7:02:37
 * @version 1.0
 * <p>Company: http://www.yihexinda.com</p>
 */
public class WithdrawRecordView {
	/**
	 * 提现申请ID
	 */
	private Long id;
	/**
	 * 提现方式
	 */
	private Byte withdrawalWay;
	/**
	 * 申请时间
	 */
	private Date createTime;
	/**
	 * 受理状态
	 */
	private Byte acceptStatus;
	/**
	 * 受理时间
	 */
	private Date acceptTime;
	/**
	 * 提现状态
	 */
	private Byte withdrawalStatus;
	/**
	 * 提现金额
	 */
	private BigDecimal withdrawalMoney;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 联系方式
	 */
	private String mobile;
	/**
	 * 账号
	 */
	private String userName;
	/**
	 * 可提现金额
	 */
	private BigDecimal balancePrice;
	/**
	 * 剩余金额
	 */
	private BigDecimal balance;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Byte getWithdrawalStatus() {
		return withdrawalStatus;
	}

	public void setWithdrawalStatus(Byte withdrawalStatus) {
		this.withdrawalStatus = withdrawalStatus;
	}

	public BigDecimal getWithdrawalMoney() {
		return withdrawalMoney;
	}

	public void setWithdrawalMoney(BigDecimal withdrawalMoney) {
		this.withdrawalMoney = withdrawalMoney;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public BigDecimal getBalancePrice() {
		return balancePrice;
	}

	public void setBalancePrice(BigDecimal balancePrice) {
		this.balancePrice = balancePrice;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	
}
