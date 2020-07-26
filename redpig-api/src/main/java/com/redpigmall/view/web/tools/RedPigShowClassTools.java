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
import com.redpigmall.domain.ShowClass;
import com.redpigmall.service.RedPigAccessoryService;
import com.redpigmall.service.RedPigAdvertPositionService;
import com.redpigmall.service.RedPigGoodsClassService;
import com.redpigmall.service.RedPigShowClassService;
@SuppressWarnings({"rawtypes","unchecked"})
@Component
public class RedPigShowClassTools {

	@Autowired
	private RedPigGoodsClassService goodsClassService;
	@Autowired
	private RedPigShowClassService showClassService;
	@Autowired
	private RedPigAccessoryService accessoryService;
	@Autowired
	private RedPigAdvertPositionService advertPositionService;

	public ShowClass getSc(Long show_class_id) {
		ShowClass sc = null;
		if ((show_class_id != null) && (!"".equals(show_class_id))) {
			sc = this.showClassService.selectByPrimaryKey(show_class_id);
		}
		return sc;
	}

	
	public List getSclist(Long show_class_id) {
		Map<String,Object> map = Maps.newHashMap();
		map.put("showClass_id", show_class_id);
		map.put("display", Boolean.valueOf(true));
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		
		List list = this.goodsClassService.queryPageList(map);
		
		return list;
	}

	public List<ShowClass> getSecondSclist(Long show_class_id, String con) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("parent", show_class_id);
		map.put("display", Boolean.valueOf(true));
		map.put("recommend", Boolean.valueOf(true));
		map.put("orderBy", "sequence");
		map.put("orderType", "asc");
		
		List<ShowClass> list = this.showClassService.queryPageList(map, 0, CommUtil.null2Int(con));
		
		return list;
	}

	@Autowired
	private RedPigShowClassService redPigShowClassService;

	/**
	 * 
	 * @param show_class_id
	 * @param con
	 * @return
	 */
	public List getSecondSclistRedPig(Long show_class_id, String con) {
		Map<String, Object> map = Maps.newHashMap();
		
		map.put("parent", show_class_id);
		map.put("display", 1);
		map.put("recommend", 1);
		
		return this.redPigShowClassService.queryPageList(map, 0,CommUtil.null2Int(con));
	}

	/**
	 * 根据acc id获取acc
	 * 
	 * @param acc_id
	 * @return
	 */
	public Accessory getaccessory(Long acc_id) {
		Accessory accessory = this.accessoryService.selectByPrimaryKey(acc_id);
		return accessory;
	}

	@Autowired
	private RedPigAccessoryService redPigAccessoryService;

	/**
	 * 根据acc id获取acc
	 * 
	 * @param acc_id
	 * @return
	 */
	public com.redpigmall.domain.Accessory getaccessoryRedPig(Long acc_id) {
		return this.redPigAccessoryService.selectByPrimaryKey(acc_id);
	}

	public List getbrandlist(Long show_class_id) {
		Map<String,Object> map = Maps.newHashMap();
		map.put("parent", show_class_id);
		
		List list = this.showClassService.queryPageList(map,0,3);
		
		return list;
	}

	public List<Map> getscbrand(Long sc_id) {
		List map_list = Lists.newArrayList();
		ShowClass sc = this.showClassService.selectByPrimaryKey(sc_id);
		if ((sc.getScb_info() != null) && (!"".equals(sc.getScb_info()))) {
			map_list = JSON.parseArray(sc.getScb_info());
		}
		return map_list;
	}

	public List<Map> getscbrandsrc(Long sc_id, String count) {
		List map_list = Lists.newArrayList();
		ShowClass sc = this.showClassService.selectByPrimaryKey(sc_id);
		if ((sc.getScb_info() != null) && (!"".equals(sc.getScb_info()))) {
			List maps = (List) JSON.parse(sc.getScb_info());
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

	public Map query_sc_advert(Long sc_id, String web_url) {
		Map adv_map = Maps.newHashMap();
		ShowClass sc = this.showClassService.selectByPrimaryKey(sc_id);
		if ((sc != null) && (sc.getSc_advert() != null)
				&& (!sc.getSc_advert().equals(""))) {
			Map map_temp = JSON.parseObject(sc.getSc_advert());
			if (CommUtil.null2Int(map_temp.get("adv_type")) == 0) {
				AdvertPosition ap = this.advertPositionService.selectByPrimaryKey(CommUtil.null2Long(map_temp.get("adv_id")));
				if (ap != null) {
					if ((ap.getAp_acc_url() != null)
							&& (!ap.getAp_acc_url().equals(""))) {
						adv_map.put(
								"advert_url",
								web_url + "/advert_redirect?url="
										+ ap.getAp_acc_url() + "&id="
										+ ap.getId());
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
	

	public Map query_sc_advert_colony(Long sc_id, String web_url,String imageWebServer) {
		Map adv_map = Maps.newHashMap();
		ShowClass sc = this.showClassService.selectByPrimaryKey(sc_id);
		if ((sc != null) && (sc.getSc_advert() != null)
				&& (!sc.getSc_advert().equals(""))) {
			Map map_temp = JSON.parseObject(sc.getSc_advert());
			if (CommUtil.null2Int(map_temp.get("adv_type")) == 0) {
				AdvertPosition ap = this.advertPositionService
						.selectByPrimaryKey(CommUtil.null2Long(map_temp.get("adv_id")));
				if (ap != null) {
					if ((ap.getAp_acc_url() != null)
							&& (!ap.getAp_acc_url().equals(""))) {
						adv_map.put(
								"advert_url",
								web_url + "/advert_redirect?url="
										+ ap.getAp_acc_url() + "&id="
										+ ap.getId());
					}
					if (ap.getAp_acc() != null) {
						adv_map.put("advert_img", imageWebServer + "/"
								+ ap.getAp_acc().getPath() + "/"
								+ ap.getAp_acc().getName());
					}
				}
			}
			if (CommUtil.null2Int(map_temp.get("adv_type")) == 1) {
				Accessory acc = this.accessoryService.selectByPrimaryKey(CommUtil
						.null2Long(map_temp.get("acc_id")));
				if (acc != null) {
					adv_map.put("advert_url",
							CommUtil.null2String(map_temp.get("acc_url")));
					adv_map.put("advert_img", imageWebServer + "/" + acc.getPath()
							+ "/" + acc.getName());
				}
			}
		}
		return adv_map;
	}
	
}
