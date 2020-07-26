package com.redpigmall.lucene;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * <p>
 * Title: LuceneThread.java
 * </p>
 * 
 * <p>
 * Description: lucene搜索工具类，该类使用线程处理索引的建立，默认每天凌晨更新一次商城索引文件
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
 * @date 2014-4-24
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class LuceneThread implements Runnable {
	private String path;
	private List<LuceneVo> vo_list = Lists.newArrayList();

	public LuceneThread(String path, List<LuceneVo> vo_list) {
		this.path = path;
		this.vo_list = vo_list;
	}

	public void run() {
		LuceneUtil lucene = LuceneUtil.instance();
		
		LuceneUtil.setIndex_path(this.path);
		
		lucene.batchUpdate(this.vo_list);
	}
}
