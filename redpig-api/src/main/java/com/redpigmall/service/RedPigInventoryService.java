package com.redpigmall.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redpigmall.dao.NukeGoodsMapper;
import com.redpigmall.domain.NukeGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GroupGoods;
import com.redpigmall.domain.Inventory;
import com.redpigmall.dao.GoodsMapper;
import com.redpigmall.dao.InventoryMapper;
import com.redpigmall.service.RedPigInventoryService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigInventoryService extends BaseService<Inventory>  {
	@Autowired
	private GoodsMapper goodsMapper;
	
	@Autowired
	private InventoryMapper redPigInventoryMapper;

	@Autowired
	private NukeGoodsMapper nukeGoodsMapper;
	

	@Transactional(readOnly = false)
	public void batchDelObjs(List<Inventory> objs) {
		if (objs != null && objs.size() > 0) {
			redPigInventoryMapper.batchDelete(objs);
		}
	}


	public Inventory getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Inventory> objs = redPigInventoryMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Inventory> selectObjByProperty(Map<String, Object> maps) {
		return redPigInventoryMapper.selectObjByProperty(maps);
	}


	public List<Inventory> queryPages(Map<String, Object> params) {
		return redPigInventoryMapper.queryPages(params);
	}


	public List<Inventory> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigInventoryMapper.queryPageListWithNoRelations(param);
	}


	public List<Inventory> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigInventoryMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigInventoryMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Inventory obj) {
		redPigInventoryMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Inventory obj) {
		redPigInventoryMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigInventoryMapper.deleteById(id);
	}


	public Inventory selectByPrimaryKey(Long id) {
		return redPigInventoryMapper.selectByPrimaryKey(id);
	}


	public List<Inventory> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Inventory> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigInventoryMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}


	public List<Map<String, Object>> getGoodsId(Map<String, Object> params) {
		return redPigInventoryMapper.getGoodsId(params);
	}

	/**
	 * 查询商品库存
	 * @param goods_id
	 * @param area_id
	 * @param gsp
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = false)
	public int queryGoodsInventory(String goods_id, String area_id, String gsp) {
		Goods goods = (Goods) this.goodsMapper.selectByPrimaryKey(CommUtil.null2Long(goods_id));
		int inventory = 0;
		if (goods != null) {
			// 如果属于团购
			if ((goods.getGroup() != null) && (goods.getGroup_buy() == 2)) {
				for (GroupGoods gg : goods.getGroup_goods_list()) {
					if (gg.getGroup().getId().equals(goods.getGroup().getId())) {
						inventory = gg.getGg_count();
					}
				}
				//如果是秒杀
			} else if(goods.getNuke_buy()==2&&goods.getNuke()!=null){
				Map<String,Object> map = new HashMap<>();
				map.put("ng_goods_id",goods.getId());
				map.put("nuke_id",goods.getNuke().getId());
				String []spcArray = gsp.split(",");
				String goods_spec_id = "";
				for (String str:spcArray){
                    goods_spec_id+=(str+"_");
                }
				map.put("goods_spec_id",goods_spec_id);
				List<NukeGoods>nukeGoodsList = this.nukeGoodsMapper.selectObjByProperty(map);
				// 如果商品分规格，则进行
				inventory = goods.getGoods_inventory();//goods.getInventory_type().equals("all")
				if (goods.getInventory_type().equals("spec")) {
					List<HashMap> list = JSON.parseArray(goods.getGoods_inventory_detail(), HashMap.class);
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {

						String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							// 如果秒杀活动里面有，则设置当前库存为秒杀库存
							if (nukeGoodsList!=null&&nukeGoodsList.size()>0){
								NukeGoods nukeGoods = nukeGoodsList.get(0);
								inventory = nukeGoods.getNg_count() - nukeGoods.getNg_nuke_count();
							}else{
								// 否则，设置为默认库存
								inventory = CommUtil.null2Int(temp.get("count"));
							}
							break;
						}
					}
				}

			} else {
				// 否则：判断是属于一个规格和多个规格
				inventory = goods.getGoods_inventory();//goods.getInventory_type().equals("all")
				if (goods.getInventory_type().equals("spec")) {
					List<HashMap> list = JSON.parseArray(goods.getGoods_inventory_detail(), HashMap.class);
					String[] gsp_ids = gsp.split(",");
					for (Map temp : list) {
						
						String[] temp_ids = CommUtil.null2String(temp.get("id")).split("_");
						Arrays.sort(gsp_ids);
						Arrays.sort(temp_ids);
						if (Arrays.equals(gsp_ids, temp_ids)) {
							inventory = CommUtil.null2Int(temp.get("count"));
						}
					}
				}
			}
		}
		return inventory;
	}

}
