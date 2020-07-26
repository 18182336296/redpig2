package com.redpigmall.module.weixin.view.tools;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.Store;
import com.redpigmall.domain.virtual.FootPointView;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigStoreService;


@Component
public class RedPigWeixinFootPointTools {
	
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

	public Store goods_second_domain(String id, String type) {
		Store store = null;
		if (type.equals("store")) {
			store = this.storeService.selectByPrimaryKey(CommUtil.null2Long(id));
		}
		if (type.equals("goods")) {
			Goods goods = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			if ((goods != null) && (goods.getGoods_store() != null)) {
				store = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id)).getGoods_store();
			}
		}
		return store;
	}

	public Map<String, Object> gettalk_content_info(String talk_content) {
		Map<String, Object> map = Maps.newHashMap();
		String str = "";
		String[] s = talk_content.split(" ");
		for (int r1 = 0; r1 < s.length; r1++) {
			if (r1 == 0) {
				map.put("name", s[0]);
			}
			if (r1 == 1) {
				map.put("time1", s[1]);
			}
			if (r1 == 2) {
				map.put("time2", s[2].substring(0, s[2].length() - 2));
			}
			if (r1 >= 3) {
				str = str + s[r1] + " ";
			}
		}
		map.put("content", str);
		return map;
	}
}
