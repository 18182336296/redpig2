package com.redpigmall.lucene.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

/**
 * 
 * <p>
 * Title: LuceneThreadPool.java
 * </p>
 * 
 * <p>
 * Description:写索引线程池。默认为单线程池。Executors可创建各类线程池。
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
 * @date 2014年12月1日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@Component
public class LuceneThreadPool {
	ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();
	private static LuceneThreadPool pool = new LuceneThreadPool();

	/**
	 * 向线程池中添加一个任务
	 */
	public void addThread(Runnable r) {
		this.fixedThreadPool.execute(r);
	}

	/**
	 * 获取一个单例
	 */
	public static LuceneThreadPool instance() {
		return pool;
	}

	/**
	 * 获取池对象
	 */
	public ExecutorService getService() {
		return this.fixedThreadPool;
	}
}
