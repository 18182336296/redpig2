package com.redpigmall.manage.buyer.tools;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.virtual.FootPointView;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigStoreService;


/**
 * 
 * <p>
 * Title: RedPigFootPointTools.java
 * </p>
 * 
 * <p>
 * Description:足迹处理控制器，用来解析足迹数据
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
 * @date 2014-10-17
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigFootPointTools {

	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigStoreService storeService;

	@SuppressWarnings("rawtypes")
	public List<FootPointView> generic_fpv(String json) {
		List<FootPointView> fpvs = Lists.newArrayList();
		if (!CommUtil.null2String(json).equals("")) {
			try {
				List<Map> list = JSON.parseArray(json, Map.class);
				for (Map map : list) {
					FootPointView fpv = new FootPointView();
					fpv.setFpv_goods_id(CommUtil.null2Long(map.get("goods_id")));
					fpv.setFpv_goods_img_path(CommUtil.null2String(map.get("goods_img_path")));
					fpv.setFpv_goods_name(CommUtil.null2String(map.get("goods_name")));
					fpv.setFpv_goods_sale(CommUtil.null2Int(map.get("goods_sale")));
					fpv.setFpv_goods_price(BigDecimal.valueOf(CommUtil.null2Double(map.get("goods_price"))));
					fpv.setFpv_goods_class_id(CommUtil.null2Long(map.get("goods_class_id")));
					fpv.setFpv_goods_class_name(CommUtil.null2String(map.get("goods_class_name")));
					fpvs.add(fpv);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fpvs;
	}

	/**
	 * 根据店铺id查询是否开启了二级域名。
	 * 
	 * @param id为参数
	 *            type为store时查询store type为goods时查询商品
	 * @return
	 */
	public Store goods_second_domain(String id, String type) {
		Store store = null;
		if (type.equals("store")) {
			store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		}
		if (type.equals("goods")) {
			store = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id)).getGoods_store();
		}
		return store;
	}
}
