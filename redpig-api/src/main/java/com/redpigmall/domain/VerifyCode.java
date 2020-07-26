package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * 
 * <p>
 * Title: VerifyCode.java
 * </p>
 * 
 * <p>
 * Description:验证码(包括手机验证码、邮件验证码)保存,该实体不需要缓存，存在即时性,系统开通短信发送后，用户修改手机号码，
 * 可以使用手机短信验证用户的合法性,验证码有效期为30分钟，30分钟后系统定时器会自动删除验证码信息
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
 * @date 2014-4-25
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "verifycode")
public class VerifyCode extends IdEntity {
	private String userName;// 索取验证码的用户名
	private String email;// 验证码对应的邮箱号
	private String mobile;// 验证码对应的手机号码
	private String code;// 对应的验证码

	public VerifyCode() {
	}

	public VerifyCode(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
