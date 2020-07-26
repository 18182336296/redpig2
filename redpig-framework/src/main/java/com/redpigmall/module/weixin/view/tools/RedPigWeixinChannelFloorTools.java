package com.redpigmall.module.weixin.view.tools;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map> getFloorInfo(Long id) {
		WeixinChannelFloor weixinChannelFloor = this.weixinchannelfloorService.selectByPrimaryKey(id);
		List list = JSON.parseArray(weixinChannelFloor.getLines_info(),Map.class);
		return list;
	}

	public List<GoodsClass> getGcBySecondShowClass(ShowClass sec_sh_class) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("show_class_id", sec_sh_class.getId());
		params.put("display", true);
		
		List<GoodsClass> goodsClasses = this.goodsClassService.queryPageList(params);
		
		if ((goodsClasses != null) && (goodsClasses.size() > 0)) {
			return goodsClasses;
		}
		return Lists.newArrayList();
	}

	@SuppressWarnings("unchecked")
	public List<Goods> getGoodsByGc(Collection<GoodsClass> goodsClass_set) {
		Map<String,Object> maps = Maps.newHashMap();
		Set<Long> set = Sets.newHashSet();
		if ((goodsClass_set != null) && (goodsClass_set.size() > 0)) {
			for (GoodsClass goodsClass : goodsClass_set) {
				set.addAll(genericGcIds(goodsClass));
			}
		}
		
		if ((set.size() > 0) && (set != null)) {
			maps.put("gc_ids", set);
		}
        maps.put("orderBy", "goods_salenum");
        maps.put("orderType", "desc");
        maps.put("pageSize",4);
        
		IPageList page = this.goodsService.list(maps);
		return page.getResult();
	}

	public AdvertPosition getAdvPosById(Long id) {
		if (id != null) {
			return this.advertPositionService.selectByPrimaryKey(id);
		}
		return null;
	}

	private Set<Long> genericGcIds(GoodsClass ugc) {
		Set<Long> ids = Sets.newHashSet();
		ids.add(ugc.getId());
		for (GoodsClass child : ugc.getChilds()) {
			ids.add(child.getId());
		}
		return ids;
	}
}
