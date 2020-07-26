package com.redpigmall.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;

/**
 * <p>
 * Title: NukeClass.java
 * </p>
 * 
 * <p>
 * Description: 秒杀分类管理类
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
 * @date 2018-4-25
 * 
 * @version redpigmall_b2b2c v8.0 2018版
 */
@SuppressWarnings("serial")
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "nuke_class")
public class NukeClass extends IdEntity {
	private String nc_name;// 团购分类名称
	private int nk_sequence;// 分了序号，按照升序排列

	@Column(columnDefinition = "int default 0")
	private int nk_recommend;

	@OneToMany(mappedBy = "ng_gc", cascade = CascadeType.REMOVE)
	private List<NukeGoods> ngs = new ArrayList<NukeGoods>();// 团购分类中对应的团购商品

	private int nk_level;// 团购等级

	public String getNc_name() {
		return nc_name;
	}

	public void setNc_name(String nc_name) {
		this.nc_name = nc_name;
	}

	public int getNk_sequence() {
		return nk_sequence;
	}

	public void setNk_sequence(int nk_sequence) {
		this.nk_sequence = nk_sequence;
	}

	public int getNk_recommend() {
		return nk_recommend;
	}

	public void setNk_recommend(int nk_recommend) {
		this.nk_recommend = nk_recommend;
	}

	public List<NukeGoods> getNgs() {
		return ngs;
	}

	public void setNgs(List<NukeGoods> ngs) {
		this.ngs = ngs;
	}

	public int getNk_level() {
		return nk_level;
	}

	public void setNk_level(int nk_level) {
		this.nk_level = nk_level;
	}

}
