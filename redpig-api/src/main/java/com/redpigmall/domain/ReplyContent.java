package com.redpigmall.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.redpigmall.api.constant.Globals;
import com.redpigmall.api.domain.IdEntity;
/**
 * <p>
 * Title: ReplyContent.java
 * </p>
 * 
 * <p>
 * Description:回复内容
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
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "replycontent")
public class ReplyContent extends IdEntity {
	
	@ManyToOne(fetch = FetchType.LAZY)
	private VMenu reply_content;//回复内容
	private String author;//作者
	private String title;//标题
	private Date createTime;//创建时间
	@Column(columnDefinition = "LongText")
	private String digest;//摘要
	@Column(columnDefinition = "int default 0")
	private int sequence;//序号
	@Column(columnDefinition = "LongText")
	private String content;//内容
	@Column(columnDefinition = "int default 0")
	private int way;//方式
	@OneToOne
	private Accessory img;//图片
	@Column(columnDefinition = "int default 0")
	private int count;//数量
	@Column(columnDefinition = "int default 0")
	private int counter;//计数
	@OneToOne
	private Accessory img_1;//图片
	@OneToMany(mappedBy = "praise_info")
	private List<Praise> reply_praise;//回复点赞

	public List<Praise> getReply_praise() {
		return this.reply_praise;
	}

	public void setReply_praise(List<Praise> reply_praise) {
		this.reply_praise = reply_praise;
	}

	public int getCounter() {
		return this.counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public Accessory getImg_1() {
		return this.img_1;
	}

	public void setImg_1(Accessory img_1) {
		this.img_1 = img_1;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getDigest() {
		return this.digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public VMenu getReply_content() {
		return this.reply_content;
	}

	public void setReply_content(VMenu reply_content) {
		this.reply_content = reply_content;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getWay() {
		return this.way;
	}

	public void setWay(int way) {
		this.way = way;
	}

	public Accessory getImg() {
		return this.img;
	}

	public void setImg(Accessory img) {
		this.img = img;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}
