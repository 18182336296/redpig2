package com.redpigmall.domain;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * 分销用户关联
 */
@SuppressWarnings({ "serial" })
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "user_distribution")
public class UserDistribution extends IdEntity   {
	/**
	 * 用户
	 */
	private User user;

	/**
	 * 父类id(推广人)
	 */
	private long parent_user_id;

	/**是否为内部员工*/
	@Column(columnDefinition = "int default 0")
	private int is_inner_staff;

	public UserDistribution() {
	}

	public UserDistribution(Long id) {
		super.setId(id);
	}

	public UserDistribution(Long id, Date addTime) {
		super(id, addTime);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getParent_user_id() {
		return parent_user_id;
	}

	public void setParent_user_id(long parent_user_id) {
		this.parent_user_id = parent_user_id;
	}

	public int getIs_inner_staff() {
		return is_inner_staff;
	}

	public void setIs_inner_staff(int is_inner_staff) {
		this.is_inner_staff = is_inner_staff;
	}
}
