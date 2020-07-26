package com.redpigmall.manage.admin.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.GroupClass;
import com.redpigmall.domain.GroupLifeGoods;
import com.redpigmall.service.RedPigGroupClassService;
import com.redpigmall.service.RedPigGroupLifeGoodsService;
@SuppressWarnings({"rawtypes","unchecked"})
@Component
public class RedPigGroupTools {
	
	@Autowired
	private RedPigGroupClassService groupClassService;
	@Autowired
	private RedPigGroupLifeGoodsService groupLifeGoodsService;
	
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

	public boolean getCountint(String json, String seq) {
		boolean flag = false;
		
		if ((json != null) && (!json.equals(""))) {
			try {
				List<Map> list = JSON.parseArray(json, Map.class);
				for (Map map : list) {
					if (seq.equals(CommUtil.null2String(map.get("seq")))) {
						flag = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public Map<String, GroupLifeGoods> getGrouplifeGoods(String ids) {
		Map<String, GroupLifeGoods> glgs = Maps.newHashMap();
		List<GroupLifeGoods> list = Lists.newArrayList();
		if ((ids != null) && (!ids.equals(""))) {
			String[] Ids = ids.split(",");
			Set<Long> idset = new HashSet();

			for (String id : Ids) {
				idset.add(CommUtil.null2Long(id));
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("ids", idset);
			
			list = this.groupLifeGoodsService.queryPageList(params);

			if (list.size() > 0) {
				
				for (GroupLifeGoods glf : list) {
					glgs.put("goods" + Ids.length, glf);
				}
			}
		}
		return glgs;
	}

	public List<GroupClass> getHotClass(String ids) {
		List<GroupClass> gclist = Lists.newArrayList();
		if ((ids != null) && (!ids.equals(""))) {
			String[] Ids = ids.split(",");
			Set<Long> idset = new HashSet();
			for (String id : Ids) {
				idset.add(CommUtil.null2Long(id));
			}
			Map<String, Object> params = Maps.newHashMap();
			params.put("ids", idset);
			
			gclist = this.groupClassService.queryPageList(params);
			
		}
		return gclist;
	}
}
