package com.redpigmall.view.web.tools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Area;
import com.redpigmall.domain.GroupAreaInfo;
import com.redpigmall.domain.GroupClass;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.domain.Store;
import com.redpigmall.service.RedPigAreaService;
import com.redpigmall.service.RedPigGroupAreaInfoService;
import com.redpigmall.service.RedPigGroupClassService;
import com.redpigmall.service.RedPigGroupGoodsService;
import com.redpigmall.service.RedPigGroupLifeGoodsService;

@SuppressWarnings({"unchecked","rawtypes"})
@Component
public class RedPigGroupViewTools {

	@Autowired
	private RedPigGroupGoodsService groupGoodsService;

	@Autowired
	private RedPigGroupAreaInfoService groupAreaInfoService;
	@Autowired
	private RedPigAreaService areaService;
	@Autowired
	private RedPigGroupLifeGoodsService groupLifeGoodsService;
	@Autowired
	private RedPigGroupClassService groupClassService;

	public List<GroupGoods> query_goods(String group_id, int count) {
		List<GroupGoods> list = Lists.newArrayList();
		Map<String, Object> params = Maps.newHashMap();
		params.put("group_id", CommUtil.null2Long(group_id));
		params.put("orderBy", "addTime");
		params.put("orderType", "desc");
		
		list = this.groupGoodsService.queryPageList(params, 0, count);
		
		return list;
	}

	public static Double getRate(Double group_price, Double cost_price) {
		double ret = 0.0D;
		if ((!CommUtil.null2String(group_price).equals(""))
				&& (!CommUtil.null2String(cost_price).equals(""))) {
			BigDecimal e = new BigDecimal(CommUtil.null2String(group_price));
			BigDecimal f = new BigDecimal(CommUtil.null2String(cost_price));
			if (CommUtil.null2Float(f) > 0.0F) {
				ret = e.divide(f, 3, 1).doubleValue();
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		Double re = Double.valueOf(CommUtil.mul(Double.valueOf(df.format(ret)),
				Integer.valueOf(10)));
		return re;
	}

	public List<GroupLifeGoods> getGroupFloor_LifeGoods(String group_area_id,
			String gc_id) {
		List<GroupLifeGoods> glg_list = Lists.newArrayList();
		if ((gc_id != null) && (!"".equals(gc_id))) {
			GroupClass gc = this.groupClassService.selectByPrimaryKey(CommUtil.null2Long(gc_id));
			if (gc != null) {
				Set gc_ids = Sets.newHashSet();
				
				for (GroupClass gc1 : gc.getChilds()) {
					for (GroupClass gc2 : gc1.getChilds()) {
						gc_ids.add(gc2.getId());
					}
				}
				
				Map<String, Object> params = Maps.newHashMap();
				if ((gc_ids != null) && (gc_ids.size() > 0)) {
					params.put("gg_gc_ids", gc_ids);
					if ((group_area_id != null) && (!"".equals(group_area_id))) {
						Map<String,Object> area_list_maps = Maps.newHashMap();
						area_list_maps.put("parent", CommUtil.null2Long(group_area_id));
						
						List<Area> area_list = this.areaService.queryPageList(area_list_maps);
						
						for (Area area : area_list) {
							Map<String,Object> area_maps = Maps.newHashMap();
							area_maps.put("area_id", area.getId());
							
							List<GroupAreaInfo> gai_list = this.groupAreaInfoService.queryPageList(area_maps);
							
							if (gai_list.size() > 0) {
								for (GroupAreaInfo gai : gai_list) {
									params.put("gg_gai_id", gai.getId());
									
									List<GroupLifeGoods> g_list = this.groupLifeGoodsService.queryPageList(params);
									
									glg_list.addAll(g_list);
								}
							}
						}
						if (glg_list.size() < 1) {
							params.put("group_status", 1);
							
							List<GroupLifeGoods> g_list = this.groupLifeGoodsService.queryPageList(params);
							
							glg_list.addAll(g_list);
						}
					} else {
						params.put("group_status", 1);
						
						List<GroupLifeGoods> g_list = this.groupLifeGoodsService.queryPageList(params);
						
						glg_list.addAll(g_list);
					}
				}
			}
		}
		glg_list = LifeGoods_Sort(glg_list);
		return glg_list;
	}

	public List<GroupLifeGoods> LifeGoods_Sort(List list) {
		Collections.sort(list, new Comparator<GroupLifeGoods>() {
			public int compare(GroupLifeGoods glg1, GroupLifeGoods glg2) {
				return glg2.getSelled_count() - glg1.getSelled_count();
			}
		});
		return list;
	}

	public String getStoreName(String gfg_id) {
		String storeName = "";
		if ((gfg_id != null) && (!"".equals(gfg_id))) {
			GroupLifeGoods gfg = this.groupLifeGoodsService.selectByPrimaryKey(CommUtil
					.null2Long(gfg_id));
			if (gfg != null) {
				if (gfg.getGoods_type() == 0) {
					Store store = gfg.getUser().getStore();
					storeName = store != null ? store.getStore_name() : "";
				} else {
					storeName = "自营";
				}
			}
		}
		return storeName;
	}

	public List<GroupClass> getChild_GroupClass(String gc_id) {
		List<GroupClass> child_gc_list = Lists.newArrayList();
		if ((gc_id != null) && (!"".equals(gc_id))) {
			GroupClass gc = this.groupClassService.selectByPrimaryKey(CommUtil
					.null2Long(gc_id));
			if (gc != null) {
				child_gc_list = gc.getChilds();
			}
		}
		return child_gc_list;
	}

	public List<Map<String, String>> getClassNav(String classNav) {
		List<Map<String, String>> class_list = null;
		if ((classNav != null) && (!classNav.equals(""))) {
			class_list = (List) JSON.parseObject(classNav, new TypeReference() {
			}, new Feature[0]);
		}
		return class_list;
	}

	public List<Map> getInfo_list(String json, String seq) {
		List wcs = Lists.newArrayList();
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

	public int getButtonSize(String json) {
		List<Map> list = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			try {
				list = JSON.parseArray(json, Map.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list.size();
	}

	public List<GroupClass> getGc_list(String json, String seq) {
		List gcs = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			try {
				List<Map> list = JSON.parseArray(json, Map.class);
				for (Map map : list) {
					if (seq.equals(CommUtil.null2String(map.get("seq")))) {
						GroupClass the_gc = this.groupClassService
								.selectByPrimaryKey(CommUtil.null2Long(map.get("pid")));
						if (the_gc != null) {
							int count = CommUtil.null2Int(map.get("count"));
							GroupClass gc = new GroupClass();
							gc.setId(the_gc.getId());
							gc.setGc_name(the_gc.getGc_name());
							for (int i = 0; i < count; i++) {
								GroupClass child = this.groupClassService
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

	public List<GroupClass> getGc_list2(String json, String seq) {
		List<GroupClass> gclist = Lists.newArrayList();
		if ((json != null) && (!json.equals(""))) {
			try {
				List<Map> list = JSON.parseArray(json, Map.class);
				for (Map map : list) {
					if (seq.equals(CommUtil.null2String(map.get("seq")))) {
						int count = CommUtil.null2Int(map.get("count"));
						for (int i = 0; i < count; i++) {
							GroupClass gc = this.groupClassService
									.selectByPrimaryKey(CommUtil.null2Long(map
											.get("gc_id_" + i)));
							if (gc != null) {
								gclist.add(gc);
							}
						}
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return gclist;
	}

	public Map<String, String> getSingleThirdClass(String id) {
		GroupClass gc = this.groupClassService.selectByPrimaryKey(CommUtil
				.null2Long(id));
		Map<String, String> map = Maps.newHashMap();
		map.put("id", gc.getId().toString());
		map.put("name", gc.getGc_name());
		return map;
	}

	public List<Map<String, String>> getAllThirdClass(String id) {
		Map<String, Object> params = Maps.newHashMap();
		List<Map<String, String>> list = Lists.newArrayList();
		params.put("parent", CommUtil.null2Long(id));
		
		List<GroupClass> gclist = this.groupClassService.queryPageList(params);
		
		if ((gclist != null) && (gclist.size() > 0)) {
			for (GroupClass gc : gclist) {
				Map map = Maps.newHashMap();
				map.put("id", gc.getId());
				map.put("gcname", gc.getGc_name());
				list.add(map);
			}
		}
		return list;
	}

	public List<GroupClass> getRecom(Long id) {
		GroupClass gc1 = this.groupClassService.selectByPrimaryKey(id);
		List<GroupClass> list = Lists.newArrayList();
		if (gc1 != null) {
			for (GroupClass gc2 : gc1.getChilds()) {
				List<GroupClass> gclist = gc2.getChilds();
				if ((gclist == null) || (gclist.size() <= 0)) {
					break;
				}
				for (GroupClass gc3 : gclist) {
					if (gc3.getGc_recommend() == 1) {
						list.add(gc3);
					}
				}
			}

		}
		return list;
	}

	public List<GroupLifeGoods> getGrouplifeGoods(String ids) {
		List<GroupLifeGoods> glgs = Lists.newArrayList();
		if ((ids != null) && (!ids.equals(""))) {
			String[] Ids = ids.split(",");
			Set<Long> idset = Sets.newHashSet();
			for (String id : Ids) {
				idset.add(CommUtil.null2Long(id));
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("ids", idset);
			glgs = this.groupLifeGoodsService.queryPageList(params);
			
		}
		return glgs;
	}

	
	public List<GroupClass> getHotClass(String ids) {
		List<GroupClass> gclist = Lists.newArrayList();
		if ((ids != null) && (!ids.equals(""))) {
			String[] Ids = ids.split(",");
			Set<Long> idset = Sets.newHashSet();
			for (String id : Ids) {
				idset.add(CommUtil.null2Long(id));
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("ids", idset);
			
			gclist = this.groupClassService.queryPageList(params);
			
		}
		return gclist;
	}

	public Map<String, List<GroupClass>> getItems(List<GroupClass> list) {
		Map<String, List<GroupClass>> map = Maps.newHashMap();
		if (list.size() < 14) {
			map.put("0", list);
			return map;
		}
		int i = list.size();
		List<GroupClass> list0 = Lists.newArrayList();
		List<GroupClass> list1 = Lists.newArrayList();
		List<GroupClass> list2 = Lists.newArrayList();
		for (int j = 0; j < i; j++) {
			if (j < 14) {
				list0.add((GroupClass) list.get(j));
			} else if ((j >= 14) && (j < 28)) {
				list1.add((GroupClass) list.get(j));
			} else {
				list2.add((GroupClass) list.get(j));
			}
		}
		map.put("0", list0);
		map.put("1", list1);
		map.put("2", list2);
		return map;
	}
}
