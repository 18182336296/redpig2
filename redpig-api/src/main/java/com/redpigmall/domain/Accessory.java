package com.redpigmall.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.collect.Lists;
import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * 
 * <p>
 * Title: Accessory.java
 * </p>
 * 
 * <p>
 * Description:系统附件管理类，用来管理系统所有附件信息，包括图片附件、rar附件等等
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
 * @version redpigmall_b2b2c v8.0 2016版 
 */
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "accessory")
@SuppressWarnings("serial")
public class Accessory extends IdEntity {
	private String name;// 附件名称
	private String path;// 存放路径
	@Column(precision = 12, scale = 2)
	private BigDecimal size;// 附件大小
	private int width;// 宽度
	private int height;// 高度
	private String ext;// 扩展名，不包括.
	private String info;// 附件说明
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;// 附件对应的用户，可以精细化管理用户附件
	@ManyToOne(fetch = FetchType.LAZY)
	private Album album;// 图片对应的相册
	
	@OneToOne(mappedBy = "album_cover", fetch = FetchType.LAZY)
	private Album cover_album;// 相册封面对应的图片
	@ManyToOne(fetch = FetchType.LAZY)
	private SysConfig config;// 对应登录页左侧图片
	@OneToMany(mappedBy = "goods_main_photo")
	private List<Goods> goods_main_list = Lists.newArrayList();
	@ManyToMany(mappedBy = "goods_photos")
	private List<Goods> goods_list = Lists.newArrayList();// 商品列表
	
	public Accessory(Long id, Date addTime) {
		super(id, addTime);
	}
	
	public Accessory(Long id) {
		super.setId(id);
	}
	
	public Accessory() {
	}

	public void setSize(BigDecimal size) {
		this.size = size;
	}

	public List<Goods> getGoods_main_list() {
		return this.goods_main_list;
	}

	public void setGoods_main_list(List<Goods> goods_main_list) {
		this.goods_main_list = goods_main_list;
	}

	public List<Goods> getGoods_list() {
		return this.goods_list;
	}

	public void setGoods_list(List<Goods> goods_list) {
		this.goods_list = goods_list;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInfo() {
		return this.info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public BigDecimal getSize() {
		return this.size;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getExt() {
		return this.ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public Album getAlbum() {
		return this.album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public SysConfig getConfig() {
		return this.config;
	}

	public void setConfig(SysConfig config) {
		this.config = config;
	}
}
