package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Goods;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigSysConfigService;


/**
 * <p>
 * Title: RedPigSubjectTools.java
 * </p>
 * 
 * <p>
 * Description: 专题json解析工具类
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
 * @date 2014-11-14
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigSubjectTools {
	@Autowired
	private RedPigSysConfigService configService;

	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigAccessoryService accessoryService;

	/**
	 * 平台设置专题获取状体热点信息方法
	 * 
	 * @param areaInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<Map> getAreaInfo(String areaInfo) {
		List<Map> maps = Lists.newArrayList();
		if ((areaInfo != null) && (!areaInfo.equals(""))) {
			String[] infos = areaInfo.split("-");
			for (String obj : infos) {
				if (!obj.equals("")) {
					Map<String, Object> map = Maps.newHashMap();
					String[] detail_infos = obj.split("==");
					detail_infos[0] = detail_infos[0].replace("_", ",");
					String coords = detail_infos[0];
					map.put("coords", coords);
					map.put("url", detail_infos[1]);
					map.put("width", Integer.valueOf(getWidth(detail_infos[0])));
					map.put("height",
							Integer.valueOf(getHeight(detail_infos[0])));
					map.put("top", Integer.valueOf(getTop(detail_infos[0])));
					map.put("left", Integer.valueOf(getLeft(detail_infos[0])));
					maps.add(map);
				}
			}
		}
		System.out.println("maps:" + maps);
		return maps;
	}

	public int getWidth(String str) {
		int width = 0;
		String[] strs = str.split(",");
		int temp_width = CommUtil.null2Int(strs[0])
				- CommUtil.null2Int(strs[2]);
		if (temp_width > 0) {
			width = temp_width;
		} else {
			width = 0 - temp_width;
		}
		return width;
	}

	public int getHeight(String str) {
		int height = 0;
		String[] strs = str.split(",");
		int temp_height = CommUtil.null2Int(strs[1])
				- CommUtil.null2Int(strs[3]);
		if (temp_height > 0) {
			height = temp_height;
		} else {
			height = 0 - temp_height;
		}
		return height;
	}

	public int getTop(String str) {
		int top = 0;
		String[] strs = str.split(",");
		top = CommUtil.null2Int(strs[1]);
		return top;
	}

	public int getLeft(String str) {
		int left = 0;
		String[] strs = str.split(",");
		left = CommUtil.null2Int(strs[0]);
		return left;
	}

	@SuppressWarnings("rawtypes")
	public List<Map> getGoodsInfos(String goods_ids) {
		List<Map> maps = Lists.newArrayList();
		if ((goods_ids != null) && (!goods_ids.equals(""))) {
			String[] ids = goods_ids.split(",");
			for (String id : ids) {
				Map<String, Object> map = Maps.newHashMap();
				Goods obj = this.goodsService
						.selectByPrimaryKey(CommUtil.null2Long(id));
				if (obj != null) {
					map.put("id", obj.getId());
					map.put("name", obj.getGoods_name());
					map.put("price", obj.getGoods_current_price());
					map.put("img", obj.getGoods_main_photo().getPath() + "/"
							+ obj.getGoods_main_photo().getName());
					maps.add(map);
				}
			}
		}
		return maps;
	}

	/**
	 * 前台专题详情获取是否需要二级域名
	 * 
	 * @param id
	 * @return
	 */
	public String getGoodsUrl(String id, String webUrl) {
		String ret = "false";
		Goods obj = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
		if ((obj.getGoods_type() == 1)
				&& (this.configService.getSysConfig().getSecond_domain_open())
				&& (obj.getGoods_store().getStore_second_domain() != "")) {
			ret = "true";
		}
		return ret;
	}

	/**
	 * 获取图片宽度，在编辑专业页详情中调用该方法，用作定位图片外层div的定位位置。
	 * 
	 * @param str
	 * @return
	 */
	public int getImageWidth(String id) {
		int width = 1640;
		Accessory image = this.accessoryService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		if (image.getWidth() < width) {
			width = image.getWidth();
		}
		return width;
	}
}
