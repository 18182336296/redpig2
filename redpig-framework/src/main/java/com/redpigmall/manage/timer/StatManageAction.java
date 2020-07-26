package com.redpigmall.manage.timer;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.logic.service.RedPigQuartzService;
import com.redpigmall.lucene.LuceneThread;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.lucene.tools.RedPigLuceneVoTools;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.service.RedPigArticleService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigGroupGoodsService;
import com.redpigmall.service.RedPigGroupLifeGoodsService;
import com.redpigmall.service.RedPigSysConfigService;

/**
 * 
 * <p>
 * Title: StatManageAction.java
 * </p>
 * 
 * <p>
 * Description:系统统计管理控制器，目前统计系统用户、系统订单、系统访问量
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
 * @date 2014-5-30
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component("shop_stat")
public class StatManageAction {
	@Autowired
	protected RedPigGoodsService goodsService;
	@Autowired
	protected RedPigGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	protected RedPigGroupGoodsService groupgoodsService;
	@Autowired
	protected RedPigArticleService articleService;
	@Autowired
	protected RedPigGroupGoodsService redPigGroupGoodsService;
	@Autowired
	protected RedPigLuceneVoTools luceneVoTools;
	@Autowired
	protected RedPigSysConfigService configService;
	@Autowired
	private RedPigQuartzService quartzService;
	
	public void update_lucene(){

		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_status", 0);
		List<Goods> goods_list = this.goodsService.queryPageList(params);
		params.clear();
		params.put("group_status", 1);
		List<GroupLifeGoods> lifeGoods_list = this.groupLifeGoodsService.queryPageList(params);
		params.clear();
		params.put("gg_status", 1);
		List<GroupGoods> groupGoods_list = this.redPigGroupGoodsService.queryPageList(params);
		params.clear();
		params.put("display", true);
//		List<Article> article_list = this.articleService.queryPageList(params);
		
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "goods";
		
		String grouplifegoods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "lifegoods";
		
		String groupgoods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "groupgoods";
		
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		
		file = new File(grouplifegoods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(grouplifegoods_lucene_path);
		}
		
		file = new File(groupgoods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(groupgoods_lucene_path);
		}
		
		List<LuceneVo> goods_vo_list = Lists.newArrayList();
		LuceneVo vo;
		for (Goods goods : goods_list) {
			vo = this.luceneVoTools.setGoodsVo(goods);
			goods_vo_list.add(vo);
		}
		
		List<LuceneVo> lifegoods_vo_list = Lists.newArrayList();

		for (GroupLifeGoods goods : lifeGoods_list) {
			vo = this.luceneVoTools.setLifeGoodsVo(goods);
			lifegoods_vo_list.add(vo);
		}
		
		List<LuceneVo> groupgoods_vo_list = Lists.newArrayList();
		for (GroupGoods goods : groupGoods_list) {
			vo = this.luceneVoTools.setGroupGoodsVo(goods);
			groupgoods_vo_list.add(vo);
		}
		
		LuceneThread goods_thread = new LuceneThread(goods_lucene_path,goods_vo_list);
		LuceneThread lifegoods_thread = new LuceneThread(grouplifegoods_lucene_path, lifegoods_vo_list);
		LuceneThread groupgoods_thread = new LuceneThread(groupgoods_lucene_path, groupgoods_vo_list);
		
		Date d1 = new Date();
		goods_thread.run();
		lifegoods_thread.run();
		groupgoods_thread.run();
		Date d2 = new Date();
		
		String path = ClusterSyncTools.getClusterRoot() + File.separator + "luence";
		
		Map<String, Object> map = Maps.newHashMap();
		map.put("run_time", Long.valueOf(d2.getTime() - d1.getTime()));
		map.put("file_size", Double.valueOf(CommUtil.fileSize(new File(path))));
		map.put("update_time", CommUtil.formatLongDate(new Date()));
		SysConfig config = this.configService.getSysConfig();
		config.setLucene_update(new Date());
		this.configService.updateById(config);
	}
	
	public void execute() throws Exception {
		System.out.println("定时器类:"+this.getClass()+"被注释,线上需要打开。。。。");
		
		
		try {
			this.quartzService.halfHour_1();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_2();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_3();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_4();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_5();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_6();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_7();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_8();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_9();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_10();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_11();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.quartzService.halfHour_12();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
