package com.redpigmall.view.web.tools;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Advert;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsBrand;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigAdvertPositionService;
import com.redpigmall.service.RedPigGoodsBrandService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsService;

/**
 * 
 * <p>
 * Title: RedPigGoodsFloorViewTools.java
 * </p>
 * 
 * <p>
 * Description: 楼层管理json转换工具
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
 * @version redpigmall_b2b2c v8.0 2016版
 */
@Component
public class RedPigGoodsFloorViewTools {

	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigGoodsClassService goodsClassService;
	@Autowired
	private RedPigAccessoryService accessoryService;
	@Autowired
	private RedPigAdvertPositionService advertPositionService;

	@Autowired
	private RedPigGoodsBrandService goodsBrandService;

	@SuppressWarnings({ "rawtypes"})
	public List<GoodsClass> generic_gf_gc(String json) {
		List<GoodsClass> gcs = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			try {
				List<Map> list = JSON.parseArray(json, Map.class);
				Set<Long> ids = Sets.newHashSet();
				Map<String, Object> params = Maps.newHashMap();
				for (Map map : list) {
					GoodsClass the_gc = this.goodsClassService.selectByPrimaryKey(CommUtil.null2Long(map.get("pid")));
					ids.add(CommUtil.null2Long(map.get("pid")));
					params.put("ids", ids);
					
					List<GoodsClass> the_gcs = this.goodsClassService.queryPageList(params, 0, 1);
					
					if (the_gcs.size() > 0) {
						the_gc = (GoodsClass) the_gcs.get(0);
						if (the_gc != null) {
							int count = CommUtil.null2Int(map.get("gc_count"));
							ids.clear();
							params.clear();
							for (int i = 1; i <= count; i++) {
								ids.add(CommUtil.null2Long(map.get("gc_id" + i)));
							}
							ids.add(CommUtil.null2Long(map.get("pid")));
							params.put("ids", ids);
							List<GoodsClass> childs = this.goodsClassService.queryPageList(params);
							
							the_gc.getChilds().addAll(childs);
							gcs.add(the_gc);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return gcs;
	}

	public List<Goods> generic_goods(String json) {
		List<Goods> goods_list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		if ((json != null) && (!json.equals(""))) {
			try {
				Map<String, Object> map = JSON.parseObject(json);
				for (int i = 1; i <= 10; i++) {
					String key = "goods_id" + i;
					params.put("ids", CommUtil.null2Long(map.get(key)));
					List<Goods> temp_goods_list = this.goodsService.queryPageList(params);
					
					if (temp_goods_list.size() > 0) {
						goods_list.add((Goods) temp_goods_list.get(0));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return goods_list;
	}

	@SuppressWarnings({ "rawtypes" })
	public Map generic_goods_list(String json) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("list_title", "商品排行");
		if ((json != null) && (!json.equals(""))) {
			try {
				Map list = JSON.parseObject(json);
				map.put("list_title",
						CommUtil.null2String(list.get("list_title")));
				Map<String, Object> params = Maps.newHashMap();
				for (int i = 1; i <= 6; i++) {
					params.clear();
					params.put("id",CommUtil.null2Long(list.get("goods_id" + i)));
					
					List<Goods> temps = this.goodsService.queryPageList(params);
					
					if (temps.size() > 0) {
						map.put("goods" + i, temps.get(0));
					} else {
						map.put("goods" + i, null);
					}
				}
			} catch (Exception e) {
				map.put("list_title", "");
				map.put("goods1", null);
				map.put("goods2", null);
				map.put("goods3", null);
				map.put("goods4", null);
				map.put("goods5", null);
				map.put("goods6", null);
				e.printStackTrace();
			}
		}
		return map;
	}

	public String generic_adv(String web_url, String json) {
		String template = "<div style='float:left;overflow:hidden;'>";
		if ((json != null) && (!json.equals(""))) {
			try {
				Map<String, Object> map = JSON.parseObject(json);
				if (CommUtil.null2String(map.get("adv_id")).equals("")) {
					Accessory img = this.accessoryService.selectByPrimaryKey(CommUtil.null2Long(map.get("acc_id")));
					if (img != null) {
						String url = CommUtil.null2String(map.get("acc_url"));
						template = template + "<a href='" + url
								+ "' target='_blank'><img src='" + web_url
								+ "/" + img.getPath() + "/" + img.getName()
								+ "' /></a>";
					}
				} else {
					AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil.null2Long(map.get("adv_id")));
					AdvertPosition obj = new AdvertPosition();
					obj.setAp_type(ap.getAp_type());
					obj.setAp_status(ap.getAp_status());
					obj.setAp_show_type(ap.getAp_show_type());
					obj.setAp_width(ap.getAp_width());
					obj.setAp_height(ap.getAp_height());
					List<Advert> advs = Lists.newArrayList();
					for (Advert temp_adv : ap.getAdvs()) {
						if (temp_adv.getAd_status() == 1) {
							if ((temp_adv.getAd_begin_time().before(new Date()))
									&& (temp_adv.getAd_end_time()
											.after(new Date()))) {
								advs.add(temp_adv);
							}
						}
					}
					if (advs.size() > 0) {
						if (obj.getAp_type().equals("img")) {
							if (obj.getAp_show_type() == 0) {
								obj.setAp_acc(((Advert) advs.get(0))
										.getAd_acc());
								obj.setAp_acc_url(((Advert) advs.get(0))
										.getAd_url());
								obj.setAdv_id(CommUtil
										.null2String(((Advert) advs.get(0))
												.getId()));
							}
							if (obj.getAp_show_type() == 1) {
								Random random = new Random();
								int i = random.nextInt(advs.size());
								obj.setAp_acc(((Advert) advs.get(i))
										.getAd_acc());
								obj.setAp_acc_url(((Advert) advs.get(i))
										.getAd_url());
								obj.setAdv_id(CommUtil
										.null2String(((Advert) advs.get(i))
												.getId()));
							}
						}
					} else {
						obj.setAp_acc(ap.getAp_acc());
						obj.setAp_text(ap.getAp_text());
						obj.setAp_acc_url(ap.getAp_acc_url());
						Advert adv = new Advert();
						adv.setAd_url(obj.getAp_acc_url());
						adv.setAd_acc(ap.getAp_acc());
						obj.getAdvs().add(adv);
					}
					template =

					template + "<a href='" + obj.getAp_acc_url()
							+ "' target='_blank'><img src='" + web_url + "/"
							+ obj.getAp_acc().getPath() + "/"
							+ obj.getAp_acc().getName() + "' /></a>";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		template = template + "</div>";
		return template;
	}

	public List<GoodsBrand> generic_brand(String json) {
		List<GoodsBrand> brands = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			try {
				Map<String, Object> map = JSON.parseObject(json);
				for (int i = 1; i <= 11; i++) {
					String key = "brand_id" + i;
					GoodsBrand brand = this.goodsBrandService.selectByPrimaryKey(CommUtil.null2Long(map.get(key)));
					if (brand != null) {
						brands.add(brand);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return brands;
	}

	@SuppressWarnings("rawtypes")
	public Map generic_style2_goods(String json, String module_id) {
		try {
			List<Map> maps = JSON.parseArray(json, Map.class);
			for (Map map : maps) {
				if (map.get("module_id").equals(module_id)) {
					return map;
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map generic_gf_wide_template(String json, String wide_template,
			String pos) {
		Map<String, Object> map = null;
		if (((wide_template.equals("wide_adv_rectangle_four"))
				|| (wide_template.equals("wide_adv_five"))
				|| (wide_template.equals("wide_adv_square_four"))
				|| (wide_template.equals("wide_adv_eight")) || (wide_template
					.equals("wide_adv_brand")))
				&& (json != null)
				&& (!json.equals(""))) {
			List<Map> maps = JSON.parseArray(json, Map.class);
			for (Map obj : maps) {
				if (CommUtil.null2String(obj.get("pos")).equals(pos)) {
					map = obj;
					break;
				}
			}
		}
		if (map == null) {
			map = Maps.newHashMap();
		}
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<GoodsBrand> generic_gf_wide_brand_template(String json,
			String wide_template, String pos) {

		List<GoodsBrand> list = Lists.newArrayList();
		Map<String, Object> map = null;
		if (((wide_template.equals("wide_adv_rectangle_four"))
				|| (wide_template.equals("wide_adv_five"))
				|| (wide_template.equals("wide_adv_square_four"))
				|| (wide_template.equals("wide_adv_eight")) || (wide_template
					.equals("wide_adv_brand")))
				&& (json != null)
				&& (!json.equals(""))) {
			List<Map> maps = JSON.parseArray(json, Map.class);
			for (Map obj : maps) {
				if (CommUtil.null2String(obj.get("pos")).equals(pos)) {
					map = obj;
					break;
				}
			}
		}

		if (map != null) {
			for (Object obj : map.keySet()) {
				if (CommUtil.null2String(obj).indexOf("brand_id") >= 0) {
					Map<String, Object> params = Maps.newHashMap();
					params.put("id", CommUtil.null2Long(map.get(obj)));
					List<GoodsBrand> gbs = this.goodsBrandService.queryPageList(params,0,1);
					
					if (gbs.size() > 0) {
						list.add(gbs.get(0));
					}
				}
			}
		}
		return list;
	}
}
