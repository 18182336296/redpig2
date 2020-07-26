package com.redpigmall.manage.admin.tools;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.ChannelFloor;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsClass;
import com.redpigmall.service.RedPigChannelFloorService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigGoodsService;

@SuppressWarnings("rawtypes")
@Component
public class RedPigChannelTools {
	
	@Autowired
	private RedPigGoodsService goodsService;
	@Autowired
	private RedPigGoodsClassService goodsClassService;
	
	@Autowired
	private RedPigChannelFloorService channelfloorService;
	
	public List getrc_list(Long id) {
		ChannelFloor obj = this.channelfloorService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		List<Map> rc_list = Lists.newArrayList();
		if (obj != null) {
			String cel = obj.getCf_extra_list();
			if (cel != null) {
				List<Map> r_list = JSON.parseArray(cel, Map.class);
				for (Map map : r_list) {
					if ("true".equals(CommUtil.null2String(map.get("show")))) {
						rc_list.add(map);
					}
				}
			}
		}
		return rc_list;
	}
	
	public List<GoodsClass> getGc_list(String json, String seq) {
		List<GoodsClass> gcs = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			try {
				List<Map> list = JSON.parseArray(json, Map.class);
				for (Map map : list) {
					if (seq.equals(CommUtil.null2String(map.get("seq")))) {
						GoodsClass the_gc = this.goodsClassService
								.selectByPrimaryKey(CommUtil.null2Long(map.get("pid")));
						if (the_gc != null) {
							int count = CommUtil.null2Int(map.get("count"));
							GoodsClass gc = new GoodsClass();
							gc.setId(the_gc.getId());
							gc.setClassName(the_gc.getClassName());
							for (int i = 1; i <= count; i++) {
								GoodsClass child = this.goodsClassService
										.selectByPrimaryKey(CommUtil.null2Long(map
												.get("gc_id_" + i)));
								if (child != null) {
									gc.getChilds().add(child);
								}
							}
							gcs.add(gc);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return gcs;
	}

	public List<Map> getInfo_list(String json, String seq) {
		List<Map> wcs = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			try {
				List<Map> list = JSON.parseArray(json, Map.class);
				for (Map map : list) {
					if (seq.equals(CommUtil.null2String(map.get("seq")))) {
						wcs.add(map);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return wcs;
	}

	public List<Goods> getGoodsInfo(String id) {
		List<Goods> goods_list = Lists.newArrayList();
		if ((id != null) && (!"".equals(id))) {
			Goods gs = this.goodsService.selectByPrimaryKey(CommUtil.null2Long(id));
			if (gs != null) {
				goods_list.add(gs);
			}
		}
		return goods_list;
	}

	public String getAdv_id(String json, String seq) {
		String info = "";
		if ((json != null) && (!json.equals(""))) {
			try {
				List<Map> list = JSON.parseArray(json, Map.class);
				for (Map map : list) {
					if (seq.equals(CommUtil.null2String(map.get("seq")))) {
						if ("adv".equals(CommUtil.null2String(map.get("type")))) {
							info = CommUtil.null2String(map.get("img_id"));
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return info;
	}
}
