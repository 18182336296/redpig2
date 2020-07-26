package com.redpigmall.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: StoreNavigation.java
 * </p>
 * 
 * <p>
 * Description:店铺导航管理类，用来描述店铺导航信息，在店铺默认导航外，卖家可以自定义导航信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "store_nav")
public class StoreNavigation extends IdEntity {
	private String title;// 导航标题
	private String url;// 导航url
	private String sequence;// 导航序号
	private String win_type;// 窗口打开方式，分为新窗口new_win和当前页cur_win
	private boolean display;// 是否显示
	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;// 导航对应的store
	@Lob
	@Column(columnDefinition = "LongText")
	private String content;// 导航内容

	public StoreNavigation() {
	}

	public StoreNavigation(Long id, Date addTime) {
		super(id, addTime);
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSequence() {
		return this.sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public String getWin_type() {
		return this.win_type;
	}

	public void setWin_type(String win_type) {
		this.win_type = win_type;
	}

	public Boolean getDisplay() {
		return this.display;
	}

	public void setDisplay(boolean display) {
		this.display = display;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Store getStore() {
		return this.store;
	}

	public void setStore(Store store) {
		this.store = store;
	}
}
