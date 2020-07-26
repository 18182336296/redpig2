package com.redpigmall.manage.admin.tools;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.qrcode.QRCodeUtil;
import com.redpigmall.api.tools.ClusterSyncTools;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.lucene.RedPigLuceneUtil;
import com.redpigmall.lucene.tools.RedPigLuceneVoTools;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsCart;
import com.redpigmall.domain.GoodsLog;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.domain.SysConfig;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigGoodsCartService;
import com.redpigmall.service.RedPigGoodsLogService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigSysConfigService;
import com.redpigmall.view.web.tools.RedPigGoodsViewTools;

/**
 * 
 * <p>
 * Title: RedPigGoodsTools.java
 * </p>
 * 
 * <p>
 * Description:商品管理工具
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
 * @date 2014-12-10
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class RedPigGoodsTools {
	@Autowired
	private RedPigSysConfigService configService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigGoodsLogService goodsLogService;
	@Autowired
	private RedPigAccessoryService accessoryService;
	@Autowired
	private RedPigGoodsCartService goodsCartService;
	@Autowired
	private RedPigLuceneVoTools luceneVoTools;
	@Autowired
	private RedPigGoodsViewTools goodsViewTools;
	
	
	/**
	 * 异步生成商品二维码,使用@Async@Transactional，有时session、商品主图片获取不到
	 * 
	 * @param web_url
	 * @param goods_id
	 * @param uploadFilePath
	 * @param goods_main_id
	 * @return
	 */
	@Async
	@Transactional
	private String createSelfGoodsQR(String web_url, Long goods_id,
			String uploadFilePath, Long goods_main_id) {
		String qr_img_path = null;
		try {
			qr_img_path = web_url + "/" + uploadFilePath + "/" + "goods_qr"
					+ "/" + goods_id + "_qr.jpg";
			String destPath = uploadFilePath + File.separator + "goods_qr";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			Accessory main_img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(goods_main_id));
			destPath = destPath + File.separator + goods_id + "_qr.jpg";
			
			if(CommUtil.fileExist(destPath)){
				CommUtil.deleteFile(destPath);
			}
			
			String logoPath = "";
			if (main_img != null) {
				logoPath = this.configService.getSysConfig().getImageWebServer()  
						+ main_img.getPath()
						+ File.separator + main_img.getName();
			} else {
				logoPath = this.configService.getSysConfig().getImageWebServer()
						+ this.configService.getSysConfig().getGoodsImage().getPath()
						+ File.separator
						+ this.configService.getSysConfig().getGoodsImage().getName();
			}
			String goods_url = web_url + "/items_" + goods_id + "";
			QRCodeUtil.encode(goods_url, logoPath, destPath, true);
			
			String srcDir = uploadFilePath + File.separator + "goods_qr";
			
			CommUtil.uploadToSFTPServer(srcDir, goods_id + "_qr.jpg");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qr_img_path;
	}

	@Async
	@Transactional
	private String createUserGoodsQR(String web_url, String second_domain,
			Long goods_id, String uploadFilePath, Long goods_main_id) {
		String qr_img_path = null;
		try {
			qr_img_path = web_url + "/" + uploadFilePath + "/" + "goods_qr"
					+ "/" + goods_id + "_qr.jpg";
			Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(goods_id));
			String destPath = uploadFilePath + File.separator + "goods_qr";
			if (!CommUtil.fileExist(destPath)) {
				CommUtil.createFolder(destPath);
			}
			if(CommUtil.fileExist(destPath)){
				CommUtil.deleteFile(destPath);
			}
			Accessory main_img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(goods_main_id));
			
			destPath = destPath + File.separator + goods_id + "_qr.jpg";
			String logoPath = "";
			if (main_img != null) {
				logoPath = this.configService.getSysConfig().getImageWebServer() 
						+ main_img.getPath() 
						+ File.separator 
						+ main_img.getName();
			} else {
				logoPath = this.configService.getSysConfig().getImageWebServer()
						+ this.configService.getSysConfig().getGoodsImage().getPath()
						+ File.separator
						+ this.configService.getSysConfig().getGoodsImage().getName();
			}
			String goods_url = web_url + "/items_" + goods_id + "";
			if (second_domain != null) {
				goods_url =

				"http://" + obj.getGoods_store().getStore_second_domain() + "."
						+ second_domain + "/items_" + goods_id + "";
			}
			QRCodeUtil.encode(goods_url, logoPath, destPath, true);
			
			String srcDir = uploadFilePath + File.separator + "goods_qr";
			
			CommUtil.uploadToSFTPServer(srcDir, goods_id + "_qr.jpg");
			
			obj.setQr_img_path(web_url + "/" + uploadFilePath + "/" + "goods_qr" + "/" + obj.getId() + "_qr.jpg");
			
			this.goodsService.update(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return qr_img_path;
	}

	public String createGoodsQR(HttpServletRequest request, Goods goods) {
		String qr_img_path = null;
		String uploadFilePath = this.configService.getSysConfig()
				.getUploadFilePath();
		Long goods_main_id = null;
		if (goods != null) {
			if (goods.getGoods_main_photo() != null) {
				goods_main_id = goods.getGoods_main_photo().getId();
			}
			if (goods.getGoods_type() == 0) {
				qr_img_path = createSelfGoodsQR(this.configService.getSysConfig().getH5Url(),
						goods.getId(), uploadFilePath, goods_main_id);
			} else {
				String second_domain = null;
				if (this.configService.getSysConfig().getSecond_domain_open()) {
					if (!CommUtil.null2String(
							goods.getGoods_store().getStore_second_domain())
							.equals("")) {
						second_domain = CommUtil.generic_domain(request);
					}
				}
				qr_img_path = createUserGoodsQR(this.configService.getSysConfig().getH5Url(),
						second_domain, goods.getId(), uploadFilePath,
						goods_main_id);
			}
		}
		return qr_img_path;
	}

	@Autowired
	private RedPigLuceneUtil lucene;

	@Async
	@Transactional
	public void addGoodsLucene(Goods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneVo vo = this.luceneVoTools.setGoodsVo(goods);
		SysConfig config = this.configService.getSysConfig();

		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		lucene.writeIndex(vo);
	}

	public void updateGoodsLucene(Goods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneVo vo = this.luceneVoTools.setGoodsVo(goods);
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		SysConfig config = this.configService.getSysConfig();
		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		lucene.update(CommUtil.null2String(goods.getId()), vo);
	}

	@Async
	@Transactional
	public void deleteGoodsLucene(Goods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ "luence" + File.separator + "goods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		SysConfig config = this.configService.getSysConfig();
		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		lucene.delete_index(CommUtil.null2String(goods.getId()));
	}

	@Async
	@Transactional
	public void addGroupGoodsLucene(GroupGoods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		LuceneVo vo = this.luceneVoTools.setGroupGoodsVo(goods);
		SysConfig config = this.configService.getSysConfig();
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		lucene.writeIndex(vo);
	}

	public void updateGroupGoodsLucene(GroupGoods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		SysConfig config = this.configService.getSysConfig();
		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		LuceneVo vo = this.luceneVoTools.setGroupGoodsVo(goods);
		lucene.update(CommUtil.null2String(goods.getId()), vo);
	}

	@Async
	@Transactional
	public void deleteGroupGoodsLucene(GroupGoods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "groupgoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		SysConfig config = this.configService.getSysConfig();
		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		lucene.delete_index(CommUtil.null2String(goods.getId()));
	}

	@Async
	@Transactional
	public void addGroupLifeLucene(GroupLifeGoods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "lifegoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		SysConfig config = this.configService.getSysConfig();
		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		LuceneVo vo = this.luceneVoTools.setLifeGoodsVo(goods);
		lucene.writeIndex(vo);
	}

	public void updateGroupLifeLucene(GroupLifeGoods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "lifegoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		SysConfig config = this.configService.getSysConfig();
		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		LuceneVo vo = this.luceneVoTools.setLifeGoodsVo(goods);
		lucene.update(CommUtil.null2String(goods.getId()), vo);
	}

	@Async
	@Transactional
	public void deleteGroupLifeLucene(GroupLifeGoods goods) {
		String goods_lucene_path = ClusterSyncTools.getClusterRoot()
				+ File.separator + "luence" + File.separator + "lifegoods";
		File file = new File(goods_lucene_path);
		if (!file.exists()) {
			CommUtil.createFolder(goods_lucene_path);
		}
		RedPigLuceneUtil lucene = RedPigLuceneUtil.instance();
		SysConfig config = this.configService.getSysConfig();
		RedPigLuceneUtil.setConfig(config);
		RedPigLuceneUtil.setIndex_path(goods_lucene_path);
		lucene.delete_index(CommUtil.null2String(goods.getId()));
	}

	public List<Map> analysis_color_img(String color_json) {
		List<Map> color_list = Lists.newArrayList();
		if ((color_json != null) && (!color_json.equals(""))) {
			List<Map> color_arr = (List) JSON.parseObject(color_json).get("data");
			int num = 0;
			for (Map color : color_arr) {
				Map color_map = Maps.newHashMap();
				String color_id = CommUtil.null2String(color.get("color_id"));
				String img_ids = CommUtil.null2String(color.get("img_ids"));
				String[] ids = img_ids.split("-");
				List img_list = Lists.newArrayList();
				for (int l = 0; l < 5; l++) {
					img_list.add(null);
				}
				for (String img_id : ids) {
					if (!img_id.equals("")) {
						Map temp_map = Maps.newHashMap();
						if (img_id.indexOf("m") >= 0) {
							String[] main_id = img_id.split("_");
							Accessory img = this.accessoryService
									.selectByPrimaryKey(CommUtil
											.null2Long(main_id[1]));
							if (img != null) {
								temp_map.put("id", img_id);
								temp_map.put("bigImg", img.getPath() + "/"
										+ img.getName());
							}
						} else {
							Accessory img = this.accessoryService
									.selectByPrimaryKey(CommUtil
											.null2Long(img_id));
							if (img != null) {
								temp_map.put("id", img_id);
								temp_map.put("bigImg", img.getPath() + "/"
										+ img.getName());
							}
						}
						img_list.add(0, temp_map);
					}
				}
				color_map.put("color_id", color_id);
				color_map.put("img_list", img_list.subList(0, 5));
				color_list.add(num, color_map);
				num++;
			}
		}
		return color_list;
	}

	public boolean color_img_mid(String color_json, String mid) {
		boolean result = false;
		if ((color_json != null) && (!color_json.equals(""))) {
			List<Map> color_arr = (List) JSON.parseObject(color_json).get(
					"data");
			for (Map color : color_arr) {
				String img_ids = CommUtil.null2String(color.get("img_ids"));
				String[] ids = img_ids.split("-");
				for (String img_id : ids) {
					if ((!img_id.equals("")) && (img_id.indexOf("m") >= 0)
							&& (img_id.equals(mid))) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

	public BigDecimal queryGoodsAdvancePrice(String gc_id, String step) {
		BigDecimal price = BigDecimal.valueOf(0.0D);
		if ((gc_id != null) && (!"".equals(gc_id))) {
			GoodsCart gc = this.goodsCartService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
			if (gc != null) {
				BigDecimal count = new BigDecimal(gc.getCount());
				Goods g = gc.getGoods();
				if ((g != null) && (g.getAdvance_sale_type() == 1)
						&& (g.getAdvance_sale_info() != null)) {
					List<Map> ad_list = JSON.parseArray(
							g.getAdvance_sale_info(), Map.class);
					if (step.equals("1")) {
						for (Map m : ad_list) {
							BigDecimal din = new BigDecimal(
									CommUtil.null2String(m.get("earnest")));
							price = price.add(din.multiply(count));
						}
					} else {
						for (Map m : ad_list) {
							BigDecimal wei = new BigDecimal(
									CommUtil.null2String(m.get("final_earnest")));
							price = price.add(wei.multiply(count));
						}
					}
				}
			}
		}
		return price;
	}

	public GoodsLog getTodayGoodsLog(long id) {
		Map<String, Object> logParams = Maps.newHashMap();
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(11, 0);
		cal.set(12, 0);
		cal.set(13, 0);
		logParams.put("add_Time_more_than_equal", cal.getTime());
		now = cal.getTime();
		cal.add(6, 1);
		logParams.put("add_Time_less_than_equal", cal.getTime());
		logParams.put("goods_id", Long.valueOf(id));
		List<GoodsLog> goodsLogList = this.goodsLogService
				.queryPageList(logParams);

		if (goodsLogList.size() == 0) {
			Goods good = this.goodsService.selectByPrimaryKey(Long.valueOf(id));
			GoodsLog todayGoodsLog = new GoodsLog();
			todayGoodsLog.setAddTime(now);
			todayGoodsLog.setGoods_id(Long.valueOf(id));
			todayGoodsLog.setGoods_name(good.getGoods_name());
			if (good.getGoods_main_photo() != null) {
				todayGoodsLog.setImg_id(good.getGoods_main_photo().getId());
			}
			todayGoodsLog.setGc_id(good.getGc().getId());
			if (good.getGoods_brand() != null) {
				todayGoodsLog.setGoods_brand_id(good.getGoods_brand().getId());
			}
			todayGoodsLog.setPrice(good.getGoods_current_price());
			Map<String, Object> map = Maps.newHashMap();
			todayGoodsLog.setGoods_order_type(JSON.toJSONString(map));
			todayGoodsLog.setGoods_sale_info(JSON.toJSONString(map));
			if (good.getGoods_type() == 0) {
				todayGoodsLog.setLog_form(0);
			} else {
				todayGoodsLog.setLog_form(1);
				todayGoodsLog.setStore_id(good.getGoods_store().getId()
						.longValue());
				todayGoodsLog.setStore_name(good.getGoods_store()
						.getStore_name());
			}
			Map preferentialMap = this.goodsViewTools
					.query_goods_preferential(id);
			todayGoodsLog.setPreferential(preferentialMap.get("name")
					.toString());
			todayGoodsLog.setPreferential_info(preferentialMap.get("info")
					.toString());
			this.goodsLogService.saveEntity(todayGoodsLog);
			return todayGoodsLog;
		}
		return (GoodsLog) goodsLogList.get(0);
	}

	public void save_salenum_goodsLog(OrderForm order, Goods goods,
			int goods_count, String spectype) {
		GoodsLog todayGoodsLog = getTodayGoodsLog(goods.getId().longValue());
		todayGoodsLog.setGoods_salenum(todayGoodsLog.getGoods_salenum()
				+ goods_count);
		Map<String, Integer> logordermap = (Map) JSON.parseObject(todayGoodsLog
				.getGoods_order_type());

		String ordertype = order.getOrder_type();
		if (logordermap.containsKey(ordertype)) {
			logordermap.put(
					ordertype,
					Integer.valueOf(((Integer) logordermap.get(ordertype))
							.intValue() + goods_count));
		} else {
			logordermap.put(ordertype, Integer.valueOf(goods_count));
		}
		todayGoodsLog.setGoods_order_type(JSON.toJSONString(logordermap));
		Map<String, Integer> logspecmap = (Map) JSON.parseObject(todayGoodsLog
				.getGoods_sale_info());
		if (logspecmap.containsKey(spectype)) {
			logspecmap.put(
					spectype,
					Integer.valueOf(((Integer) logspecmap.get(spectype))
							.intValue() + goods_count));
		} else {
			logspecmap.put(spectype, Integer.valueOf(goods_count));
		}
		todayGoodsLog.setGoods_sale_info(JSON.toJSONString(logspecmap));
		this.goodsLogService.updateById(todayGoodsLog);
	}

	public void save_click_goodsLog(HttpServletRequest request, Goods obj) {
		GoodsLog todayGoodsLog = getTodayGoodsLog(obj.getId().longValue());
		todayGoodsLog.setGoods_click(todayGoodsLog.getGoods_click() + 1);
		String click_from_str = todayGoodsLog.getGoods_click_from();
		Map<String, Integer> clickmap = (click_from_str != null)
				&& (!click_from_str.equals("")) ? (Map) JSON.parseObject(
				click_from_str, Map.class) : Maps.newHashMap();
		String from = clickfrom_to_chinese(CommUtil.null2String(request
				.getParameter("from")));
		if ((from != null) && (!from.equals(""))) {
			if (clickmap.containsKey(from)) {
				clickmap.put(from, Integer.valueOf(((Integer) clickmap
						.get(from)).intValue() + 1));
			} else {
				clickmap.put(from, Integer.valueOf(1));
			}
		} else if (clickmap.containsKey("unknow")) {
			clickmap.put("unknow", Integer.valueOf(((Integer) clickmap
					.get("unknow")).intValue() + 1));
		} else {
			clickmap.put("unknow", Integer.valueOf(1));
		}
		todayGoodsLog.setGoods_click_from(JSON.toJSONString(clickmap));
		this.goodsLogService.updateById(todayGoodsLog);
	}

	public String clickfrom_to_chinese(String key) {
		String str = "其它";
		if (key.equals("search")) {
			str = "搜索";
		}
		if (key.equals("floor")) {
			str = "首页楼层";
		}
		if (key.equals("gcase")) {
			str = "橱窗";
		}
		return str;
	}
}
