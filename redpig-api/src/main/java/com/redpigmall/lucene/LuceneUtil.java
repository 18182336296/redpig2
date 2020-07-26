package com.redpigmall.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.sandbox.queries.DuplicateFilter;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import org.wltea.analyzer.lucene.IKAnalyzer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.api.tools.SpringUtil;
import com.redpigmall.lucene.parse.ShopQueryParser;
import com.redpigmall.lucene.pool.LuceneThreadPool;
import com.redpigmall.manage.admin.tools.RedPigQueryTools;
import com.redpigmall.domain.SysConfig;


/**
 * 
 * <p>
 * Title: LuceneUtil.java
 * </p>
 * 
 * <p>
 * Description: lucene搜索工具类,用来写入索引，搜索数据
 * 
 * 注：
 * 1、本商城lucene索引主要也就是围绕goods商品,如果需要改造为solr、elasticSearch等可以自定更改
 * 2、本类有点长，不要进行格式化，已经手动格式化并且添加相关注解说明。自动格式化后无法查看！
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
 * @date 2014-6-5
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({"deprecation","unused"})
@Component
public class LuceneUtil {
	@Autowired
	private  RedPigQueryTools queryTools ;
	
	private static LuceneUtil lucence = new LuceneUtil(null);// 搜索工具类单例
	
	public static Path index_path;// 索引路径

	private static IndexReader reader = null;//索引实例

	private static File index_file = null;// 索引文件
	/*
	 *  搜索分词器,需要支持中文的,本系统使用中科院提供的:SmartChineseAnalyzer
	 *  也是apache子包,maven即可下载无须自己添加
	 */
	private static Analyzer analyzer = null;

	private static QueryParser parser;// 查询解析器

	private static int gc_size;// 商品分类大小
	private int textmaxlength = 100;// 截取字符串长度，该长度类关键词高亮显示
	/*
	 * 高亮显示自定义,使用lucene自带的高亮包
	 */
	private static String prefixHTML = "<font color='red'>";// 高亮html前置
	private static String suffixHTML = "</font>";// 高亮html后置
	//搜索分页每页数据量
	private int pageSize = 24;
	
	private static SysConfig sysConfig;// 系统配置

	@Autowired
	public LuceneUtil(RedPigQueryTools queryTools) {
		analyzer = new SmartChineseAnalyzer();//中文分词
		parser = new ShopQueryParser("title", analyzer);//自定义lucene查询分析器
	}

	public static Analyzer getAnalyzer() {
		return analyzer;
	}

	public static File getIndex_filer() {
		return index_file;
	}

	public static LuceneUtil instance() {
		return (LuceneUtil) SpringUtil.getBean("luceneUtil");
	}

	public static void setIndex_path(String index_path) {
		LuceneUtil.index_path = Paths.get(index_path, new String[0]);
		index_file = new File(index_path);
	}
	
	public static void setGc_size(int gc_size) {
		LuceneUtil.gc_size = gc_size;
	}

	public static void setConfig(SysConfig config) {
		sysConfig = config;
	}
	
	/**
	 * 根据多个关键字进行搜索
	 * 注:本系统是将足够多的信息放到lucene中，搜索完直接可以用于返回到前端显示
	 * 
	 * @param keyword 搜索关键字
	 * @param currentPage 当前页
	 * @param goods_inventory 库存量
	 * @param goods_type 商品类型
	 * @param goods_class 商品分类
	 * @param goods_transfee 物流
	 * @param goods_cod 是否支持货到付款，默认为0：支持货到付款，-1为不支持货到付款
	 * @param sort 根据哪个字段排序
	 * @param gb_name 品牌名称
	 * @param goods_pro 商品属性字段：goods_property
	 * @param salesStr 
	 * @param goods_current_price_floor 
	 * @param goods_current_price_ceiling
	 * @param goods_area_id 商品地区id
	 * @return
	 */
	public LuceneResult search(String keyword, int currentPage,
			String goods_inventory, String goods_type, String goods_class,
			String goods_transfee, String goods_cod, Sort sort, String gb_name,
			String goods_pro, String salesStr,
			String goods_current_price_floor,
			String goods_current_price_ceiling, String goods_area_id) {
		LuceneResult pList = new LuceneResult();//
		IndexSearcher isearcher = null;
		List<LuceneVo> vo_list = Lists.newArrayList();
		IndexReader reader = null;
		int pages = 0;
		int rows = 0;
		String params = "";
		try {
			if (Files.isDirectory(index_path, new LinkOption[0])) {
				reader = DirectoryReader.open(FSDirectory.open(index_path));
				isearcher = new IndexSearcher(reader);
				if ((gb_name != null) && (!gb_name.equals(""))) {
					gb_name = gb_name.trim();
				} else if ((keyword != null) && (!"".equals(keyword)) && (keyword.indexOf("title:") < 0)) {
					params = "(title:" + keyword + " OR content:" + keyword + ")";
				} else {
					params = "(title:*)";
				}
				
				if ((goods_type != null) && (!goods_type.equals(""))) {
					params = params + " AND goods_type:" + goods_type;
				}
				
				if ((goods_class != null) && (!goods_class.equals(""))) {
					params = params + " AND goods_class:" + goods_class;
				}
				
				if ((goods_transfee != null) && (!goods_transfee.equals(""))) {
					params = params + " AND goods_transfee:" + goods_transfee;
				}
				
				if ((goods_cod != null) && (!goods_cod.equals(""))) {
					params = params + " AND goods_cod:" + goods_cod;
				}
				
				if (salesStr != null) {
					params = params + " AND " + salesStr;
				}
				
				if (!CommUtil.null2String(goods_current_price_floor).equals("")) {
					if (!CommUtil.null2String(goods_current_price_ceiling).equals("")) {
						params = params + " AND (store_price:[" + goods_current_price_floor + " TO " + goods_current_price_ceiling + "])";
					}
				}
				
				if ((goods_inventory != null) && (goods_inventory.equals("0"))) {
					if(queryTools == null) {
						queryTools = new RedPigQueryTools();
					}
					params = queryTools.queryGoodsInventoryByLucene(params,goods_area_id, goods_type);
				}
				BooleanQuery query = getLuceneTargetQuery(params, goods_pro,gb_name);
				if ((keyword != null) && (!keyword.equals(""))) {
					Query query1 = new TermQuery(new Term("goods_brand",keyword));
					query.add(query1, BooleanClause.Occur.SHOULD);
				}
				/*
				 * 以下开始分页搜索,注:lucene有点类似mysql分页
				 */
				TopDocs topDocs = null;
				int start = (currentPage - 1) * this.pageSize;
				if (currentPage == 0) {
					if (!"".equals(sort) && sort !=null) {
						topDocs = isearcher.search(query, this.pageSize, sort);
					} else {
						topDocs = isearcher.search(query, this.pageSize);
					}
					pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize;
					rows = topDocs.totalHits;
					currentPage = 1;
					start = 0;
				} else if (currentPage != 0) {
					if ((sort != null) && (!sort.equals(""))) {
						topDocs = isearcher.search(query,start + this.pageSize, sort);
					} else {
						topDocs = isearcher.search(query, start + this.pageSize);
					}
					pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize;
					if (currentPage > pages) {
						currentPage = pages;
						start = (currentPage - 1) * this.pageSize;
					}
					rows = topDocs.totalHits;
				}
				int end = this.pageSize + start < topDocs.totalHits ? this.pageSize
						+ start
						: topDocs.totalHits;
				/* 
				 * 迭代搜索结果,从document中get出来,类似jdbc从result中get出来放到你的VO中.
				 */
				for (int i = start; i < end; i++) {
					Document doc = isearcher.doc(topDocs.scoreDocs[i].doc);
					LuceneVo vo = new LuceneVo();
					
					String title = doc.get("title");
					if (((gb_name == null) || (gb_name.equals("")))
							&& (!"(title:*)".equals(keyword))
							&& (!"(title:*)".equals(Integer.valueOf(pages)))) {
						
						SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(prefixHTML, suffixHTML);
						Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
						highlighter.setTextFragmenter(new SimpleFragmenter(this.textmaxlength));
						title = highlighter.getBestFragment(analyzer, keyword,doc.get("title"));
					}
					vo.setVo_id(CommUtil.null2Long(doc.get("id")));
					if (title == null) {
						vo.setVo_title(doc.get("title"));
					} else {
						vo.setVo_title(title);
					}
					
					vo.setVo_main_photo_url(doc.get("main_photo_url"));
					vo.setVo_photos_url(doc.get("photos_url"));

					vo.setVo_store_price(CommUtil.null2Double(doc.get("store_price")));
					vo.setVo_goods_salenum(CommUtil.null2Int(doc.get("goods_salenum")));
					vo.setVo_goods_evas(CommUtil.null2Int(doc.get("goods_evas")));
					vo.setVo_goods_type(CommUtil.null2Int(doc.get("goods_type")));
					vo.setVo_whether_active(CommUtil.null2Int(doc.get("whether_active")));
					vo.setVo_f_sale_type(CommUtil.null2Int(doc.get("f_sale_type")));
					vo.setVo_seller_id(doc.get("seller_id"));
					vo_list.add(vo);
				}
				pList.setPages(pages);
				pList.setRows(rows);
				pList.setCurrentPage(currentPage);
				pList.setVo_list(vo_list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return pList;
	}
	
	/**
	 * 
	 * searchGroupLife:搜索团购. <br/>
	 *
	 * @author redpig
	 * @param keyword 关键字
	 * @param currentPage 当前页
	 * @param areaIds 地区
	 * @param gcIds 商品分类id
	 * @param sort 排序
	 * @param goods_current_price_floor
	 * @param goods_current_price_ceiling
	 * @return
	 * @since JDK 1.8
	 */
	@SuppressWarnings("rawtypes")
	public LuceneResult searchGroupLife(
			String keyword, int currentPage,
			List areaIds, List gcIds, Sort sort,
			String goods_current_price_floor, 
			String goods_current_price_ceiling) {
		
		LuceneResult pList = new LuceneResult();
		IndexSearcher isearcher = null;
		List<LuceneVo> vo_list = Lists.newArrayList();
		int pages = 0;
		int rows = 0;
		String params = "";
		try {
			if (Files.isDirectory(index_path, new LinkOption[0])) {
				reader = DirectoryReader.open(FSDirectory.open(index_path));
				isearcher = new IndexSearcher(reader);
				if ((keyword != null) && (!"".equals(keyword)) && (keyword.indexOf("title:") < 0)) {
					params = "(title:" + keyword + " OR content:" + keyword + ")";
				} else {
					params = "(title:*)";
				}
				
				if ((gcIds != null) && (gcIds.size() > 0)) {
					String start_str = "(";
					for (int j = 0; j < gcIds.size(); j++) {
						if (gcIds.size() == j + 1) {
							start_str = start_str + "(cat:" + gcIds.get(j) + "))";
						} else {
							start_str = start_str + "(cat:" + gcIds.get(j) + ") " + "OR ";
						}
					}
					params = params + "AND" + start_str;
				}
				
				if (areaIds != null) {
					if (areaIds.size() > 0) {
						String start_str = "(";
						for (int j = 0; j < areaIds.size(); j++) {
							if (areaIds.size() == j + 1) {
								start_str = start_str + "(gainfo_id:" + areaIds.get(j) + "))";
							} else {
								start_str = start_str + "(gainfo_id:" + areaIds.get(j) + ") " + "OR ";
							}
						}
						params = params + "AND" + start_str;
					} else {
						params = params + "AND" + "(gainfo_id:0)";
					}
				}
				
				if (!CommUtil.null2String(goods_current_price_floor).equals("")) {
					if (!CommUtil.null2String(goods_current_price_ceiling).equals("")) {
						params = params + " AND (store_price:["
								+ goods_current_price_floor + " TO "
								+ goods_current_price_ceiling + "])";
					}
				}
				
				System.out.println(params);
				BooleanQuery query = getLuceneTargetQuery(params, null, null);
				/*
				 * 分页开始
				 */
				TopDocs topDocs = null;
				int start = (currentPage - 1) * this.pageSize;
				if (currentPage == 0) {
					if ((sort != null) && (!sort.equals(""))) {
						topDocs = isearcher.search(query, this.pageSize, sort);
					} else {
						topDocs = isearcher.search(query, this.pageSize);
					}
					pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize;
					rows = topDocs.totalHits;
					currentPage = 1;
					start = 0;
				} else if (currentPage != 0) {
					if ((sort != null) && (!sort.equals(""))) {
						topDocs = isearcher.search(query,start + this.pageSize, sort);
					} else {
						topDocs = isearcher.search(query, start + this.pageSize);
					}
					pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize;
					if (currentPage > pages) {
						currentPage = pages;
						start = (currentPage - 1) * this.pageSize;
					}
					rows = topDocs.totalHits;
				}
				int end = this.pageSize + start < topDocs.totalHits ? this.pageSize
						+ start
						: topDocs.totalHits;
				/*
				 * 将分页搜索结果放到VO结果集中
				 */
				for (int i = start; i < end; i++) {
					Document doc = isearcher.doc(topDocs.scoreDocs[i].doc);
					LuceneVo vo = new LuceneVo();

					String title = doc.get("title");
					if ((!"(title:*)".equals(keyword)) && (!"(title:*)".equals(Integer.valueOf(pages)))) {
						SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter(prefixHTML, suffixHTML);
						Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
						highlighter.setTextFragmenter(new SimpleFragmenter(this.textmaxlength));
						title = highlighter.getBestFragment(analyzer, keyword,doc.get("title"));
					}
					vo.setVo_id(CommUtil.null2Long(doc.get("id")));
					if (title == null) {
						vo.setVo_title(doc.get("title"));
					} else {
						vo.setVo_title(title);
					}
					
					vo.setVo_main_photo_url(doc.get("main_photo_url"));
					vo.setVo_photos_url(doc.get("photos_url"));

					vo.setVo_store_price(CommUtil.null2Double(doc.get("store_price")));
					vo.setVo_goods_salenum(CommUtil.null2Int(doc.get("goods_salenum")));
					vo.setVo_rate(doc.get("goods_rate"));
					vo.setVo_cost_price(CommUtil.null2Double(doc.get("cost_price")));
					vo_list.add(vo);
				}
				pList.setPages(pages);
				pList.setRows(rows);
				pList.setCurrentPage(currentPage);
				pList.setVo_list(vo_list);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (isearcher == null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
		return pList;
	}
	
	/**
	 * 
	 * searchGroupGoods:搜索团购商品. <br/>
	 *
	 * @author redpig
	 * @param keyword 关键字
	 * @param currentPage 当前页
	 * @param sort 排序
	 * @param gc_ids 商品类型id
	 * @param goods_current_price_floor
	 * @param goods_current_price_ceiling
	 * @return
	 * @since JDK 1.8
	 */
	@SuppressWarnings("rawtypes")
	public LuceneResult searchGroupGoods(
			String keyword, int currentPage,
			Sort sort, List gc_ids, 
			String goods_current_price_floor,
			String goods_current_price_ceiling) {
		LuceneResult pList = new LuceneResult();
		IndexSearcher isearcher = null;
		List<LuceneVo> vo_list = Lists.newArrayList();
		int pages = 0;
		int rows = 0;
		String params = "";
		try {
			if (Files.isDirectory(index_path, new LinkOption[0])) {
				reader = DirectoryReader.open(FSDirectory.open(index_path));
				isearcher = new IndexSearcher(reader);
				/*
				 * 组织搜索条件
				 */
				if ((keyword != null) && (!"".equals(keyword)) && (keyword.indexOf("title:") < 0)) {
					params = "(title:" + keyword + " OR content:" + keyword + ")";
				} else {
					params = "(title:*)";
				}
				
				if ((gc_ids != null) && (gc_ids.size() > 0)) {
					String start_str = "(";
					for (int j = 0; j < gc_ids.size(); j++) {
						if (gc_ids.size() == j + 1) {
							start_str = start_str + "(cat:" + gc_ids.get(j) + "))";
						} else {
							start_str = start_str + "(cat:" + gc_ids.get(j) + ") " + "OR ";
						}
					}
					params = params + "AND" + start_str;
				}
				
				if (!CommUtil.null2String(goods_current_price_floor).equals("")) {
					if (!CommUtil.null2String(goods_current_price_ceiling)
							.equals("")) {
						params = params + " AND (store_price:["
								+ goods_current_price_floor + " TO "
								+ goods_current_price_ceiling + "])";
					}
				}
				System.out.println("===========:" + params);
				BooleanQuery query = getLuceneTargetQuery(params, null, null);
				TopDocs topDocs = null;
				/*
				 * 搜索分页开始
				 */
				int start = (currentPage - 1) * this.pageSize;
				if (currentPage == 0) {
					if ((sort != null) && (!sort.equals(""))) {
						topDocs = isearcher.search(query, this.pageSize, sort);
					} else {
						topDocs = isearcher.search(query, this.pageSize);
					}
					pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize;
					rows = topDocs.totalHits;
					currentPage = 1;
					start = 0;
				} else if (currentPage != 0) {
					if ((sort != null) && (!sort.equals(""))) {
						topDocs = isearcher.search(query,start + this.pageSize, sort);
					} else {
						topDocs = isearcher.search(query, start + this.pageSize);
					}
					pages = (topDocs.totalHits + this.pageSize - 1) / this.pageSize;
					if (currentPage > pages) {
						currentPage = pages;
						start = (currentPage - 1) * this.pageSize;
					}
					rows = topDocs.totalHits;
				}
				
				int end = this.pageSize + start < topDocs.totalHits ? this.pageSize
						+ start
						: topDocs.totalHits;
				/*
				 * 将搜索结果放到结果集中
				 */
				for (int i = start; i < end; i++) {
					Document doc = isearcher.doc(topDocs.scoreDocs[i].doc);
					LuceneVo vo = new LuceneVo();

					String title = doc.get("title");

					vo.setVo_id(CommUtil.null2Long(doc.get("id")));
					if (title == null) {
						vo.setVo_title(doc.get("title"));
					} else {
						vo.setVo_title(title);
					}
					vo.setVo_main_photo_url(doc.get("main_photo_url"));
					vo.setVo_photos_url(doc.get("photos_url"));
					
					vo.setVo_store_price(CommUtil.null2Double(doc.get("store_price")));
					vo.setVo_goods_salenum(CommUtil.null2Int(doc.get("goods_salenum")));
					vo.setVo_goods_evas(CommUtil.null2Int(doc.get("goods_evas")));
					vo.setVo_goods_type(CommUtil.null2Int(doc.get("goods_type")));
					vo.setVo_whether_active(CommUtil.null2Int(doc.get("whether_active")));
					vo.setVo_f_sale_type(CommUtil.null2Int(doc.get("f_sale_type")));
					vo.setVo_seller_id(doc.get("seller_id"));
					vo.setVo_rate(doc.get("goods_rate"));
					vo.setVo_cost_price(CommUtil.null2Double(doc.get("cost_price")));
					vo_list.add(vo);
				}
				pList.setPages(pages);
				pList.setRows(rows);
				pList.setCurrentPage(currentPage);
				pList.setVo_list(vo_list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (isearcher == null) {
				
			}
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pList;
	}
	
	/**
	 * 
	 * writeIndex:写索引实例. <br/>
	 *
	 * @author redpig
	 * @param vo
	 * @since JDK 1.8
	 */
	public void writeIndex(LuceneVo vo) {
		IndexWriter writer = null;
		//启用队列:也就是多线程写索引
		if ((sysConfig != null) && (sysConfig.getLucenen_queue() == 1)) {
			LuceneThreadPool pool = LuceneThreadPool.instance();
			final LuceneVo lu_vo = vo;
			pool.addThread(new Runnable() {

				public void run() {
					IndexWriter writer = WriterUtil.getIndexWriter(LuceneUtil.index_path);
					try {
						if(writer!=null){
						writer.addDocument(LuceneUtil.builderDocument(lu_vo));
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						try {
							writer.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} finally {
						try {
							if(writer!=null){
								writer.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
		} else {
			try {
				IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
				
				iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
				writer = new IndexWriter(FSDirectory.open(index_path), iwc);
				writer.addDocument(builderDocument(vo));
			} catch (IOException e1) {
				e1.printStackTrace();
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * builderDocument:构建lucene的Document. <br/>
	 *
	 * @author redpig
	 * @param luceneVo
	 * @return
	 * @since JDK 1.8
	 */
	private static Document builderDocument(LuceneVo luceneVo) {
		Document document = new Document();
		//商品
		if ("goods".equals(luceneVo.getVo_type())) {
			Field id = new StringField("id", luceneVo.getVo_id().toString(),Field.Store.YES);//存储ID
			Field title = new Field("title", luceneVo.getVo_title(),Field.Store.YES, Field.Index.ANALYZED);//存储标题
			title.setBoost(10.0F);//权重10,很高
			Field content = new TextField("content", Jsoup.clean(
					luceneVo.getVo_content(), Whitelist.none()),
					Field.Store.YES);//存储content
			
			Field type = new StringField("type", luceneVo.getVo_type(),Field.Store.YES);//存储类型
			LongField add_time = new LongField("add_time",luceneVo.getVo_add_time(), Field.Store.YES);//存储创建时间

			IntField goods_salenum = new IntField("goods_salenum",
					luceneVo.getVo_goods_salenum(),
					INT_FIELD_TYPE_STORED_SORTED);//存储商品销量
			
			IntField goods_collect = new IntField("goods_collect",
					luceneVo.getVo_goods_collect(),
					INT_FIELD_TYPE_STORED_SORTED);//存储商品收藏次数
			
			DoubleField well_evaluate = new DoubleField("well_evaluate",
					luceneVo.getVo_well_evaluate(),
					DOUBLE_FIELD_TYPE_STORED_SORTED);//存储商品好评率
			
			DoubleField store_price = new DoubleField("store_price",
					luceneVo.getVo_store_price(),
					DOUBLE_FIELD_TYPE_STORED_SORTED);//存储店铺价格
			
			IntField goods_inventory = new IntField("goods_inventory",
					luceneVo.getVo_goods_inventory(), Field.Store.YES);//存储商品库存
			
			Field goods_type = new StringField("goods_type",
					CommUtil.null2String(Integer.valueOf(luceneVo
							.getVo_goods_type())), Field.Store.YES);//存储商品类型
			
			if (luceneVo.getVo_main_photo_url() != null) {
				Field photo_url = new StringField("main_photo_url",
						CommUtil.null2String(luceneVo.getVo_main_photo_url()),
						Field.Store.YES);//存储商品主图
				document.add(photo_url);//添加到document
			}
			
			Field photos_url = new StringField("photos_url",
					CommUtil.null2String(luceneVo.getVo_photos_url()),
					Field.Store.YES);//存储头像图片
			
			Field vo_goods_evas = new StringField("goods_evas",
					CommUtil.null2String(Integer.valueOf(luceneVo
							.getVo_goods_evas())), Field.Store.YES);//存储商品用户评价

			String gb_name = CommUtil.null2String(
					luceneVo.getVo_goods_brandname()).trim();
			Field vo_goods_brandname = new StringField("goods_brand", gb_name,
					Field.Store.YES);//存储商品品牌
			
			Field vo_goods_class = new StringField("goods_class",
					CommUtil.null2String(luceneVo.getVo_goods_class()),
					Field.Store.YES);//商品分类
			
			Field vo_goods_transfee = new StringField("goods_transfee",
					CommUtil.null2String(luceneVo.getVo_goods_transfee()),
					Field.Store.YES);//商品物流
			
			Field vo_goods_cod = new StringField("goods_cod",
					CommUtil.null2String(Integer.valueOf(luceneVo
							.getVo_goods_cod())), Field.Store.YES);//是否支持货到付款，默认为0：支持货到付款，-1为不支持货到付款
			
			Field vo_whether_active = new StringField("whether_active",
					CommUtil.null2String(Integer.valueOf(luceneVo
							.getVo_whether_active())), Field.Store.YES);//是否支持零元购
			
			Field vo_f_sale_type = new StringField("f_sale_type",
					CommUtil.null2String(Integer.valueOf(luceneVo
							.getVo_f_sale_type())), Field.Store.YES);//是否为F码销售商品，0为不是F码销售商品，1为F码销售商品，F码商品不可以参加任何商城活动
			
			Field vo_goods_properties = new StringField("goods_properties",
					luceneVo.getVo_goods_properties(), Field.Store.YES);//商品属性
			
			Field vo_seller_id = new StringField("seller_id",
					luceneVo.getVo_seller_id(), Field.Store.YES);//卖家id
			
			//将上面的字段添加到document,用于索引存储
			document.add(id);
			document.add(title);
			document.add(content);
			document.add(type);
			document.add(add_time);
			document.add(goods_salenum);
			document.add(goods_collect);
			document.add(well_evaluate);
			document.add(store_price);
			document.add(goods_inventory);
			document.add(goods_type);
			document.add(photos_url);
			document.add(vo_goods_evas);
			document.add(vo_goods_brandname);
			document.add(vo_goods_class);
			document.add(vo_goods_transfee);
			document.add(vo_goods_cod);
			document.add(vo_whether_active);
			document.add(vo_f_sale_type);
			document.add(vo_goods_properties);
			document.add(vo_seller_id);
		}
		
		if (("lifegoods".equals(luceneVo.getVo_type())) || ("groupgoods".equals(luceneVo.getVo_type()))) {
			Field id = new Field("id", String.valueOf(luceneVo.getVo_id()),Field.Store.YES, Field.Index.ANALYZED);
			Field title = new Field("title", Jsoup.clean(
					luceneVo.getVo_title(), Whitelist.none()), Field.Store.YES,
					Field.Index.ANALYZED);
			title.setBoost(10.0F);
			Field content = new Field("content", Jsoup.clean(
					luceneVo.getVo_content(), Whitelist.none()),
					Field.Store.YES, Field.Index.ANALYZED);
			Field type = new Field("type", luceneVo.getVo_type(),
					Field.Store.YES, Field.Index.NOT_ANALYZED);

			DoubleField store_price = new DoubleField("store_price",
					luceneVo.getVo_store_price(),
					DOUBLE_FIELD_TYPE_STORED_SORTED);
			Field add_time = new Field("add_time", CommUtil.null2String(Long
					.valueOf(luceneVo.getVo_add_time())), Field.Store.YES,
					Field.Index.NOT_ANALYZED);
			DoubleField goods_salenum = new DoubleField("goods_salenum",
					CommUtil.null2Double(Integer.valueOf(luceneVo
							.getVo_goods_salenum())),
					DOUBLE_FIELD_TYPE_STORED_SORTED);
			DoubleField goods_rate = new DoubleField("goods_rate",
					CommUtil.null2Double(luceneVo.getVo_rate()),
					DOUBLE_FIELD_TYPE_STORED_SORTED);
			DoubleField cost_price = new DoubleField("cost_price",
					CommUtil.null2Double(Double.valueOf(luceneVo
							.getVo_cost_price())),
					DOUBLE_FIELD_TYPE_STORED_SORTED);
			Field goods_cat = new Field("cat", CommUtil.null2String(luceneVo
					.getVo_cat()), Field.Store.YES, Field.Index.NOT_ANALYZED);
			if (luceneVo.getVo_main_photo_url() != null) {
				Field goods_main_photo = new Field("main_photo_url",
						CommUtil.null2String(luceneVo.getVo_main_photo_url()),
						Field.Store.YES, Field.Index.NOT_ANALYZED);
				document.add(goods_main_photo);
			}
			Field goods_area = new Field("goods_area",
					CommUtil.null2String(luceneVo.getVo_goods_area()),
					Field.Store.YES, Field.Index.NOT_ANALYZED);
			if (luceneVo.getVo_gainfo_id() != null) {
				Field vo_gainfo_id = new Field("gainfo_id",
						luceneVo.getVo_gainfo_id(), Field.Store.YES,
						Field.Index.NOT_ANALYZED);
				document.add(vo_gainfo_id);
			}
			document.add(id);
			document.add(title);
			document.add(content);
			document.add(type);
			document.add(store_price);
			document.add(add_time);
			document.add(goods_salenum);
			document.add(goods_cat);
			document.add(goods_rate);
			document.add(cost_price);
			document.add(goods_area);
		}
		return document;
	}

	public String getLuceneQueryString(String oldStr) {
		if (CommUtil.null2String(oldStr).equals("")) {
			return null;
		}
		StringBuilder targetStr = new StringBuilder();
		String[] oldStrs = oldStr.split(",");
		targetStr.append("*_").append(oldStrs[0]).append("_*");
		return targetStr.toString();
	}

	public BooleanQuery getLuceneTargetQuery(String params, String goods_pro,String gb_name) {
		BooleanQuery query = new BooleanQuery();
		parser.setAllowLeadingWildcard(true);
		try {
			if ((params != null) && (!"".equals(params))) {
				query.add(parser.parse(params), BooleanClause.Occur.SHOULD);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if ((gb_name != null) && (!gb_name.equals(""))) {
			Query query1 = new TermQuery(new Term("goods_brand", gb_name));
			query.add(query1, BooleanClause.Occur.MUST);
		}
		if ((goods_pro != null) && (!goods_pro.equals(""))) {
			String[] strs = goods_pro.trim().split("\\|");
			for (int i = 0; i < strs.length; i++) {
				String str = getLuceneQueryString(strs[i]);
				if (str != null) {
					str = "(goods_properties:" + str + ")";
					try {
						query.add(parser.parse(str), BooleanClause.Occur.MUST);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return query;
	}

	static void indexDocs(IndexWriter writer, LuceneVo vo) throws IOException {
		indexDoc(writer, vo);
	}

	static void indexDoc(IndexWriter writer, LuceneVo vo) throws IOException {
		Document doc = builderDocument(vo);
		if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
			writer.addDocument(doc);
		} else {
			writer.updateDocument(new Term("id", vo.getVo_id().toString()), doc);
		}
	}

	private static final FieldType DOUBLE_FIELD_TYPE_STORED_SORTED = new FieldType();
	private static final FieldType INT_FIELD_TYPE_STORED_SORTED;

	static {
		DOUBLE_FIELD_TYPE_STORED_SORTED.setTokenized(true);
		DOUBLE_FIELD_TYPE_STORED_SORTED.setOmitNorms(true);
		DOUBLE_FIELD_TYPE_STORED_SORTED.setIndexOptions(IndexOptions.DOCS);
		DOUBLE_FIELD_TYPE_STORED_SORTED
				.setNumericType(FieldType.NumericType.DOUBLE);
		DOUBLE_FIELD_TYPE_STORED_SORTED.setStored(true);
		DOUBLE_FIELD_TYPE_STORED_SORTED.setDocValuesType(DocValuesType.NUMERIC);
		DOUBLE_FIELD_TYPE_STORED_SORTED.freeze();

		INT_FIELD_TYPE_STORED_SORTED = new FieldType();

		INT_FIELD_TYPE_STORED_SORTED.setTokenized(true);
		INT_FIELD_TYPE_STORED_SORTED.setOmitNorms(true);
		INT_FIELD_TYPE_STORED_SORTED.setIndexOptions(IndexOptions.DOCS);
		INT_FIELD_TYPE_STORED_SORTED.setNumericType(FieldType.NumericType.INT);
		INT_FIELD_TYPE_STORED_SORTED.setStored(true);
		INT_FIELD_TYPE_STORED_SORTED.setDocValuesType(DocValuesType.NUMERIC);
		INT_FIELD_TYPE_STORED_SORTED.freeze();
	}

	public java.util.Set<String> LoadData_goods_class(String keyword) {
		IndexSearcher searcher = null;
		Set<String> list = Sets.newHashSet();
		try {
			reader = DirectoryReader.open(FSDirectory.open(index_path));
			searcher = new IndexSearcher(reader);
			if ((keyword != null) && (!"".equals(keyword))
					&& (keyword.indexOf("title:") < 0)) {
				keyword = "(title:" + keyword + " OR content:" + keyword
						+ " OR goods_brand: " + keyword + ")";
			}
			parser.setAllowLeadingWildcard(true);
			Query query = parser.parse(keyword);
			TopDocs topDocs = null;
			Filter filter = new DuplicateFilter("goods_class");
			topDocs = searcher.search(query, filter, gc_size);
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for (int i = 0; i < scoreDocs.length; i++) {
				Document doc = searcher.doc(scoreDocs[i].doc);
				String gc = doc.get("goods_class");
				list.add(gc);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public void batchUpdate(List<LuceneVo> vos) {
		Long time1 = Long.valueOf(System.currentTimeMillis());
		IndexWriter writer = null;
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(FSDirectory.open(index_path), iwc);
			for (int i = 0; i < vos.size(); i++) {
				indexDocs(writer, (LuceneVo) vos.get(i));
			}
		} catch (Exception e) {
			
		} finally {
			try {
				writer.close();
				Long time2 = Long.valueOf(System.currentTimeMillis());
				Long time3 = Long
						.valueOf(time2.longValue() - time1.longValue());
				System.out.println("本次更新索引数量：" + vos.size() + "/共耗时：" + time3);
			} catch (IOException e) {
				
			}
		}
	}

	public void update(String id, LuceneVo vo) {
		IndexWriter writer = null;
		try {
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
			writer = new IndexWriter(FSDirectory.open(index_path), iwc);
			indexDocs(writer, vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("static-access")
	public void delete_index(String id) {
		IndexWriter writer = null;
		try {
			Directory directory = FSDirectory.open(index_path);
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			writer = new IndexWriter(directory, iwc);
			if (writer.isLocked(directory)) {
				writer.getDirectory().obtainLock(IndexWriter.WRITE_LOCK_NAME);
				writer.getDirectory().deleteFile(IndexWriter.WRITE_LOCK_NAME);
			}
			Term term = new Term("id", String.valueOf(id));
			writer.deleteDocuments(new Term[] { term });
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {

			}
		}
	}
	
	public static void deleteAllLuceneIndex(File file) {
		try{
			System.out.println("file="+file.getAbsolutePath());
			if (file.isFile() || file.list().length == 0) {
				file.delete();
			} else {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteAllLuceneIndex(files[i]);
					files[i].delete();
				}
				
				if (file.exists()) // 如果文件本身就是目录 ，就要删除目录
					file.delete();
			}
		}catch(Exception e){
			
		}
	}
	
}
