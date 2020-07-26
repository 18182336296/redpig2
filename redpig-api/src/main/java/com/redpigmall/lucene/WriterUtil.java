package com.redpigmall.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.index.LogMergePolicy;
import org.apache.lucene.store.FSDirectory;

import com.redpigmall.api.tools.ClusterSyncTools;

/**
 * 
 * <p>
 * Title: WriterUtil.java
 * </p>
 * 
 * <p>
 * Description:系统Lucene
 * indexWriter实例管理器，第一次初始化时候进行一次实例化处理，此后一直单例使用，除非gc回收WriteUtil
 * ，再次重新初始化WriteUtil，各个IndexWriter再次初始化
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
 * @date 2015-1-9
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
public class WriterUtil {
	private static IndexWriter indexWriter;
	private static IndexWriter indexWriter_group;
	private static IndexWriter indexWriter_life;
	private static Path path;
	private static final String GOODS = ClusterSyncTools.getClusterRoot() 
			+ File.separator
			+ "luence"
			+ File.separator + "goods";
	private static final String GROUP = ClusterSyncTools.getClusterRoot() 
			+ File.separator
			+ "luence"
			+ File.separator + "groupgoods";
	private static final String LIFE = ClusterSyncTools.getClusterRoot()
			+ File.separator
			+ "luence"
			+ File.separator + "lifegoods";

	static {
		try {
			WriterUtil.path = LuceneUtil.index_path;
			LogMergePolicy mergePolicy = new LogDocMergePolicy();
			// 索引基本配置
			// 设置segment添加文档(Document)时的合并频率
			// 值较小,建立索引的速度就较慢
			// 值较大,建立索引的速度就较快,>10适合批量建立索引
//			Analyzer analyzer = new IKAnalyzer();
			Analyzer analyzer = new SmartChineseAnalyzer();
			
			mergePolicy.setMergeFactor(30);

			IndexWriterConfig iwc1 = new IndexWriterConfig(analyzer);
			iwc1.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			// 设置segment最大合并文档(Document)数
			// 值较小有利于追加索引的速度
			// 值较大,适合批量建立索引和更快的搜索
			mergePolicy.setMaxMergeDocs(5000);
			iwc1.setMaxBufferedDocs(10000);
			iwc1.setMergePolicy(mergePolicy);
			iwc1.setRAMBufferSizeMB(64.0D);

			IndexWriterConfig iwc2 = new IndexWriterConfig(analyzer);
			iwc2.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			iwc2.setMaxBufferedDocs(5000);
			iwc2.setMaxBufferedDocs(10000);
			iwc2.setMergePolicy(mergePolicy);
			iwc2.setRAMBufferSizeMB(64.0D);

			IndexWriterConfig iwc3 = new IndexWriterConfig(analyzer);
			iwc3.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			iwc3.setMaxBufferedDocs(5000);
			iwc3.setMaxBufferedDocs(10000);
			iwc3.setMergePolicy(mergePolicy);
			iwc3.setRAMBufferSizeMB(64.0D);

			indexWriter = new IndexWriter(FSDirectory.open(path), iwc1);
			indexWriter_group = new IndexWriter(FSDirectory.open(path), iwc2);
			indexWriter_life = new IndexWriter(FSDirectory.open(path), iwc3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 在线程结束时，自动关闭IndexWriter */
	public static IndexWriter getIndexWriter(Path path) {
		setPath(path);
		String url = path.toString();
		if (url.equals(GOODS)) {
			return indexWriter;
		}
		if (url.equals(GROUP)) {
			return indexWriter_group;
		}
		if (url.equals(LIFE)) {
			return indexWriter_life;
		}
		return indexWriter;
	}

	public static IndexWriter getIndexWriterGroup() {
		return indexWriter_group;
	}

	public static IndexWriter getIndexWriterLife() {
		return indexWriter_life;
	}

	public static Path getPath() {
		return path;
	}

	public static void setPath(Path path) {
		WriterUtil.path = path;
	}

	/**
	 * 关闭IndexWriter
	 * 
	 * @throws IOException
	 * @throws CorruptIndexException
	 */
	public static void closeIndexWriter() throws Exception {
		if (indexWriter != null) {
			indexWriter.close();
		}
		if (indexWriter_group != null) {
			indexWriter_group.close();
		}
		if (indexWriter_life != null) {
			indexWriter_life.close();
		}
	}

	protected void finalize() throws Throwable {
		super.finalize();
		closeIndexWriter();
	}
}
