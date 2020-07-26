package com.redpigmall.view.web.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigAdvertPositionService;
import com.redpigmall.service.RedPigGoodsClassService;

/**
 * 
 * <p>
 * Title: RedPigGoodsClassViewTools.java
 * </p>
 * 
 * <p>
 * Description:
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
 * @date 2014-8-25
 * 
 * @version redpigmall_b2b2c 2014
 */
@Component
public class RedPigGoodsClassViewTools {
	@Autowired
	private RedPigGoodsClassService gcService;
	@Autowired
	private RedPigAdvertPositionService apService;
	@Autowired
	private RedPigAccessoryService accessoryService;
	/**
	 * 查询指定数量的商品分类信息
	 * 
	 * @param pid
	 * @param count
	 * @return
	 */
	public List<GoodsClass> query_gc(String pid, int count) {
		List<GoodsClass> gcs = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("display", Boolean.valueOf(true));
		if (!CommUtil.null2String(pid).equals("")) {
			params.put("parent", CommUtil.null2Long(pid));
			params.put("orderBy", "sequence");
			params.put("orderType", "asc");
			gcs = this.gcService.queryPageList(params, 0, count);
		} else {
			gcs = this.gcService.queryPageList(params, 0, count);
		}
		return gcs;
	}
	/**
	 * 查询三级分类的推荐分类
	 * 
	 * @param count
	 * @return
	 */
	public List<GoodsClass> query_second_rec(String pid, int count) {
		List<GoodsClass> gcs = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("pid", CommUtil.null2Long(pid));
		params.put("display", Boolean.valueOf(true));
		params.put("recommend", Boolean.valueOf(true));
		params.put("orderBy", "sequence");
		params.put("orderType", "asc");
		gcs = this.gcService.queryPageList(params, 0, count);
		
		return gcs;
	}
	/**
	 * 查询主分类中显示的品牌信息
	 * 
	 * @param count
	 * @return
	 */
	public List<GoodsClass> query_third_rec(String pid, int count) {
		List<GoodsClass> gcs = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("pid", CommUtil.null2Long(pid));
		params.put("display", Boolean.valueOf(true));
		params.put("recommend", Boolean.valueOf(true));
		gcs = this.gcService.queryPageList(params, 0, count);
		
		return gcs;
	}

	@SuppressWarnings("rawtypes")
	public List<Map> query_gc_brand(String gc_id) {
		List<Map> map_list = Lists.newArrayList();
		GoodsClass gc = this.gcService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		
		if ((gc.getGb_info() != null) && (!gc.getGb_info().equals(""))) {
			map_list = JSON.parseArray(gc.getGb_info(), Map.class);
		}
		return map_list;
	}

	@SuppressWarnings("rawtypes")
	public List<Map> query_gc_brand2(String gc_id, String count) {
		List<Map> map_list = Lists.newArrayList();
		GoodsClass gc = this.gcService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		if ((gc.getGb_info() != null) && (!gc.getGb_info().equals(""))) {
			List<Map> maps = JSON.parseArray(gc.getGb_info(), Map.class);
			int mark = 0;
			for (int i = maps.size() - 1; i >= 0; i--) {
				if (mark < CommUtil.null2Int(count)) {
					map_list.add((Map) maps.get(i));
					mark++;
				}
			}
		}
		return map_list;
	}
	/**
	 * 查询主分类中显示的广告信息
	 * 
	 * @param count
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map query_gc_advert(String gc_id, String web_url) {
		Map adv_map = Maps.newHashMap();
		GoodsClass gc = this.gcService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
		if ((gc.getGc_advert() != null) && (!gc.getGc_advert().equals(""))) {
			Map map_temp = JSON.parseObject(gc.getGc_advert());
			if (CommUtil.null2Int(map_temp.get("adv_type")) == 0) {
				AdvertPosition ap = this.apService.selectByPrimaryKey(CommUtil.null2Long(map_temp.get("adv_id")));
				if (ap != null) {
					if ((ap.getAp_acc_url() != null)
							&& (!ap.getAp_acc_url().equals(""))) {
						adv_map.put("advert_url", web_url
								+ "/advert_redirect?url=" + ap.getAp_acc_url()
								+ "&id=" + ap.getId());
					}
					if (ap.getAp_acc() != null) {
						adv_map.put("advert_img", web_url + "/"
								+ ap.getAp_acc().getPath() + "/"
								+ ap.getAp_acc().getName());
					}
				}
			}
			if (CommUtil.null2Int(map_temp.get("adv_type")) == 1) {
				Accessory acc = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(map_temp.get("acc_id")));
				if (acc != null) {
					adv_map.put("advert_url",
							CommUtil.null2String(map_temp.get("acc_url")));
					adv_map.put("advert_img", web_url + "/" + acc.getPath()
							+ "/" + acc.getName());
				}
			}
		}
		return adv_map;
	}
}
