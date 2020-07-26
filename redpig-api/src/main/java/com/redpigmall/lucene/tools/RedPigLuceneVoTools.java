package com.redpigmall.lucene.tools;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.lucene.LuceneVo;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Activity;
import com.redpigmall.domain.BuyGift;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.service.RedPigActivityService;
import com.redpigmall.service.RedPigBuyGiftService;
import com.redpigmall.service.RedPigEnoughReduceService;
import com.redpigmall.view.web.tools.RedPigGroupViewTools;

/**
 * 
 * <p>
 * Title: LuceneUpdateTools.java
 * </p>
 * 
 * <p>
 * Description: lucene更新时的调用方法
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
 * @date 2016-6-5
 * 
 * 
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigLuceneVoTools {
	
	@Autowired
	private RedPigActivityService activityService;
	
	@Autowired
	private RedPigBuyGiftService buyGiftService;
	
	@Autowired
	private RedPigEnoughReduceService enoughReduceService;
	

	/**
	 * 设置生活类团购商品的索引
	 * 
	 * @param goods
	 * @return LuceneVo
	 */
	public LuceneVo setLifeGoodsVo(GroupLifeGoods goods) {
		LuceneVo vo = new LuceneVo();
		vo.setVo_id(goods.getId());
		vo.setVo_title(goods.getGg_name());
		vo.setVo_content(goods.getGroup_details());
		vo.setVo_type("lifegoods");
		vo.setVo_store_price(CommUtil.null2Double(goods.getGroup_price()));
		vo.setVo_add_time(goods.getAddTime().getTime());
		vo.setVo_goods_salenum(goods.getSelled_count());
		if (goods.getGroup_acc() != null) {
			vo.setVo_main_photo_url(goods.getGroup_acc().getPath() + "/" + goods.getGroup_acc().getName());
		}
		vo.setVo_cost_price(CommUtil.null2Double(goods.getCost_price()));
		if (goods.getGg_gc() != null) {
			vo.setVo_cat(goods.getGg_gc().getId().toString());
		}
		String rate = RedPigGroupViewTools.getRate(
				Double.valueOf(CommUtil.null2Double(goods.getGroup_price())),
				Double.valueOf(CommUtil.null2Double(goods.getCost_price())))
				.toString();
		vo.setVo_rate(rate);
		if (goods.getGg_ga() != null) {
			vo.setVo_goods_area(goods.getGg_ga().getId().toString());
		}
		if (goods.getGg_gai() != null) {
			vo.setVo_gainfo_id(goods.getGg_gai().getId().toString());
		}
		return vo;
	}
	
	/**
	 * 封装商品信息到lucene索引
	 * 
	 * @param goods
	 * @return lucene索引
	 */
	@SuppressWarnings("rawtypes")
	public LuceneVo setGoodsVo(Goods goods) {
		LuceneVo vo = new LuceneVo();
		vo.setVo_id(goods.getId());
		vo.setVo_title(goods.getGoods_name());
		vo.setVo_content(goods.getGoods_details());
		vo.setVo_type("goods");
		vo.setVo_store_price(CommUtil.null2Double(goods.getStore_price()));
		vo.setVo_add_time(goods.getAddTime().getTime());
		vo.setVo_goods_salenum(goods.getGoods_salenum());
		vo.setVo_goods_collect(goods.getGoods_collect());
		vo.setVo_well_evaluate(CommUtil.null2Double(goods.getWell_evaluate()));
		vo.setVo_goods_inventory(goods.getGoods_inventory());
		vo.setVo_goods_type(goods.getGoods_type());
		if (goods.getGoods_brand() != null) {
			vo.setVo_goods_brandname(goods.getGoods_brand().getName());
		}
		if (goods.getGoods_main_photo() != null) {
			vo.setVo_main_photo_url(goods.getGoods_main_photo().getPath() + "/"
					+ goods.getGoods_main_photo().getName() + "_middle."
					+ goods.getGoods_main_photo().getExt());
		}
		
		if ((goods.getGoods_store() != null)
				&& (goods.getGoods_store().getUser() != null)) {
			vo.setVo_store_username(goods.getGoods_store().getUser().getUserName());
		}
		
		List<String> list = Lists.newArrayList();
		for (Accessory obj : goods.getGoods_photos()) {
			list.add(obj.getPath() + "/" + obj.getName() + "_middle." + obj.getExt());
		}
		String str = JSON.toJSONString(list);
		vo.setVo_photos_url(str);
		vo.setVo_goods_evas(goods.getEvaluates().size());
		
		if (goods.getGc().getLevel() == 2 && goods.getGc().getParent() != null) {
			vo.setVo_goods_class(
					CommUtil.null2String(goods.getGc().getParent().getId())
					+ "_" + CommUtil.null2String(goods.getGc().getId()));
		} else {
			vo.setVo_goods_class(CommUtil.null2String(goods.getGc().getId()) + "_");
		}
		vo.setVo_goods_transfee(String.valueOf(goods.getGoods_transfee()));
		vo.setVo_goods_cod(goods.getGoods_cod());
		if (goods.getOrder_enough_give_status() == 1) {
			vo.setVo_whether_active(1);
		}
		if (goods.getOrder_enough_give_status() == 0) {
			vo.setVo_whether_active(0);
		}
		
		int active = 0;
		Date nowDate = new Date();
		if ((goods.getGroup_buy() == 2)
				&& (goods.getGroup().getBeginTime().before(nowDate))) {
			active = 1;
		}
		if (goods.getActivity_status() == 2) {
			Activity ac = this.activityService.selectByPrimaryKey(CommUtil.null2Long(goods.getActivity_goods_id()));
			if ((ac != null) && (ac.getAc_begin_time().before(nowDate))) {
				active = 2;
			}
		}
		
		if (goods.getOrder_enough_give_status() == 1) {
			BuyGift bg = this.buyGiftService.selectByPrimaryKey(goods.getBuyGift_id());
			if ((bg != null) && (bg.getBeginTime().before(nowDate))) {
				active = 3;
			}
		}
		if (goods.getEnough_reduce() == 1) {
			EnoughReduce er = this.enoughReduceService.selectByPrimaryKey(CommUtil.null2Long(goods.getOrder_enough_reduce_id()));
			
			if ((er != null) && (er.getErbegin_time().before(nowDate))) {
				active = 4;
			}
		}
		if (goods.getCombin_status() == 1) {
			active = 5;
		}
		if (goods.getF_sale_type() == 1) {
			active = 6;
		}
		if (goods.getAdvance_sale_type() == 1) {
			active = 7;
		}
		vo.setVo_whether_active(active);
		vo.setVo_f_sale_type(goods.getF_sale_type());
		String tempStr = "";
		if (goods.getGoods_property() != null) {
			List<Map> list1 = JSON.parseArray(goods.getGoods_property(),
					Map.class);
			for (Map map : list1) {
				tempStr = tempStr + "_" + map.get("val");
			}
			tempStr = tempStr + "_";
		}
		vo.setVo_goods_properties(tempStr);
		String seller_id = "";
		if (goods.getGoods_store() != null && goods.getGoods_store().getUser()!=null) {
			seller_id = goods.getGoods_store().getUser().getId().toString();
		}
		vo.setVo_seller_id(seller_id);
		return vo;
	}
	
	/**
	 * 设置团购商品的索引
	 * 
	 * @param goods
	 * @return LuceneVo
	 */
	public LuceneVo setGroupGoodsVo(GroupGoods goods) {
		LuceneVo vo = new LuceneVo();
		vo.setVo_id(goods.getId());
		vo.setVo_title(goods.getGg_name());
		vo.setVo_content(goods.getGg_content());
		vo.setVo_type("groupgoods");
		vo.setVo_store_price(CommUtil.null2Double(goods.getGg_price()));
		vo.setVo_add_time(goods.getAddTime().getTime());
		vo.setVo_goods_salenum(goods.getGg_selled_count());
		vo.setVo_cost_price(CommUtil.null2Double(goods.getGg_goods().getGoods_price()));
		if (goods.getGg_img() != null) {
			vo.setVo_main_photo_url(goods.getGg_img().getPath() + "/"
					+ goods.getGg_img().getName());
		}
		vo.setVo_cat(goods.getGg_gc().getId().toString());
		vo.setVo_rate(CommUtil.null2String(goods.getGg_rebate()));
		if (goods.getGg_ga() != null) {
			vo.setVo_goods_area(goods.getGg_ga().getId().toString());
		}
		return vo;
	}

}
