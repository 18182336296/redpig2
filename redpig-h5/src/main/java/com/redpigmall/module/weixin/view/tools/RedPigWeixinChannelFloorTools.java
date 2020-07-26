package com.redpigmall.module.weixin.view.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.domain.ShowClass;
import com.redpigmall.domain.WeixinChannelFloor;
import com.redpigmall.service.RedPigAdvertPositionService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.RedPigWeixinChannelFloorService;
import com.redpigmall.view.web.tools.RedPigQueryUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Component
public class RedPigWeixinChannelFloorTools {

	@Autowired
	private RedPigWeixinChannelFloorService weixinchannelfloorService;
	@Autowired
	private RedPigGoodsClassService goodsClassService;
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigAdvertPositionService advertPositionService;

	@Autowired
	private RedPigQueryUtils redPigQueryTools;

	public List<Map> getFloorInfo(Long id) {
		WeixinChannelFloor weixinChannelFloor = this.weixinchannelfloorService
				.selectByPrimaryKey(id);
		List list = JSON.parseArray(weixinChannelFloor.getLines_info(),
				Map.class);
		return list;
	}

	public List<GoodsClass> getGcBySecondShowClass(ShowClass sec_sh_class) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("show_class_id", sec_sh_class.getId());
		params.put("display", 1);
		List<GoodsClass> goodsClasses = this.goodsClassService
				.queryPageList(params);
		if ((goodsClasses != null) && (goodsClasses.size() > 0)) {
			return goodsClasses;
		}
		return Lists.newArrayList();
	}

	public List<Goods> getGoodsByGc(Collection<GoodsClass> goodsClass_set) {

		Map paras = redPigQueryTools.getParams("0", 4, "goods_salenum", "desc");

		Set<Long> set = new HashSet();
		if ((goodsClass_set != null) && (goodsClass_set.size() > 0)) {
			for (GoodsClass goodsClass : goodsClass_set) {
				set.addAll(genericGcIds(goodsClass));
			}
		}

		paras.put("redPig_gc_ids", set);
		IPageList page = this.goodsService.list(paras);
		return page.getResult();
	}

	public AdvertPosition getAdvPosById(Long id) {
		if (id != null) {
			return this.advertPositionService.selectByPrimaryKey(id);
		}
		return null;
	}

	private Set<Long> genericGcIds(GoodsClass ugc) {
		Set ids = new HashSet();
		ids.add(ugc.getId());
		for (GoodsClass child : ugc.getChilds()) {
			ids.add(child.getId());
		}
		return ids;
	}
}
