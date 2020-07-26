package com.redpigmall.manage.admin.action.tools.lucene;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.annotation.SecurityMapping;
import com.redpigmall.api.mv.RedPigJModelAndView;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.lucene.LuceneThread;
import com.redpigmall.lucene.LuceneUtil;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.manage.admin.action.base.BaseAction;
import com.redpigmall.domain.Article;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.SysConfig;

/**
 * 
 * <p>
 * Title: RedPigLuceneManageAction.java
 * </p>
 * 
 * <p>
 * Description: 全文检索处理器
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
 * @date 2014年5月27日
 * 
 * @version redpigmall_b2b2c 8.0
 */
@SuppressWarnings({  "unused" })
@Controller
public class RedPigLuceneManageAction extends BaseAction {
	
	/**
	 * 全文检索设置
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "全文检索设置", value = "/lucene*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping({ "/lucene" })
	public ModelAndView lucene(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new RedPigJModelAndView("admin/blue/lucene.html",
				this.configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		String path = ClusterSyncTools.getClusterRoot() + "luence";
		File file = new File(path);
		if (!file.exists()) {
			CommUtil.createFolder(path);
		}
		mv.addObject("lucene_disk_size",CommUtil.fileSize(file));
		mv.addObject("lucene_disk_path", path);
		return mv;
	}
	
	/**
	 * 全文检索关键字保存
	 * @param request
	 * @param response
	 * @param id
	 * @param hotSearch
	 * @param lucenen_queue
	 */
	@SecurityMapping(title = "全文检索关键字保存", value = "/lucene_hot_save*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping({ "/lucene_hot_save" })
	public void lucene_hot_saveEntity(HttpServletRequest request,
			HttpServletResponse response, String id, String hotSearch,
			String lucenen_queue,
			String lucenen_cluster) {
		SysConfig obj = this.configService.getSysConfig();
		boolean ret = false;
		if (id.equals("")) {
			obj.setHotSearch(hotSearch);
			obj.setLucenen_queue(CommUtil.null2Int(lucenen_queue));
			obj.setLucenen_cluster(CommUtil.null2Int(lucenen_cluster));
			obj.setAddTime(new Date());
			this.configService.saveEntity(obj);
			ret = true;
		} else {
			obj.setHotSearch(hotSearch);
			obj.setLucenen_queue(CommUtil.null2Int(lucenen_queue));
			obj.setLucenen_cluster(CommUtil.null2Int(lucenen_cluster));
			this.configService.updateById(obj);
			ret = true;
		}
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		try {
			PrintWriter writer = response.getWriter();
			writer.print(ret);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 全文检索更新
	 * @param request
	 * @param response
	 * @param id
	 * @param hotSearch
	 */
	@SecurityMapping(title = "全文检索更新", value = "/lucene_update*", rtype = "admin", rname = "全文检索", rcode = "luence_manage", rgroup = "工具")
	@RequestMapping({ "/lucene_update" })
	public void lucene_updateById(HttpServletRequest request,
			HttpServletResponse response, String id, String hotSearch) {
		
		LuceneUtil.deleteAllLuceneIndex(new File(ClusterSyncTools.getClusterRoot()+File.separator+"luence"+File.separator));
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_status", 0);
		List<Goods> goods_list = this.goodsService.queryPageList(params);
		params.clear();
		params.put("group_status", 1);
		List<GroupLifeGoods> lifeGoods_list = this.groupLifeGoodsService.queryPageList(params);
		params.clear();
		params.put("gg_status", 1);
		List<GroupGoods> groupGoods_list = this.groupGoodsService.queryPageList(params);
		params.clear();
		params.put("display", true);
		List<Article> article_list = this.articleService.queryPageList(params);
		
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
				try {
					vo = this.luceneVoTools.setGroupGoodsVo(goods);
					groupgoods_vo_list.add(vo);
				} catch (Exception e) {
					
				}
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
		
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		response.setCharacterEncoding("UTF-8");
		
		try {
			PrintWriter writer = response.getWriter();
			writer.print(JSON.toJSONString(map));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
