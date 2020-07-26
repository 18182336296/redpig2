package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Accessory;
import com.redpigmall.domain.Goods;
import com.redpigmall.domain.GoodsSpecProperty;
import com.redpigmall.domain.UserGoodsClass;
import com.redpigmall.dao.GoodsMapper;
import com.redpigmall.service.RedPigGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsService extends BaseService<Goods>  {
	@Autowired
	private GoodsMapper redPigGoodsMapper;
	

	@Transactional(readOnly = false)
	public void batchDelObjs(List<Goods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsMapper.batchDelete(objs);
		}
	}


	public Goods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Goods> objs = redPigGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Goods> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsMapper.selectObjByProperty(maps);
	}


	public List<Goods> queryPages(Map<String, Object> params) {
		return redPigGoodsMapper.queryPages(params);
	}


	public List<Goods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<Goods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {

		if (currentPage != null) {
			params.put("currentPage", currentPage);
		}

		if (pageSize != null) {
			params.put("pageSize", pageSize);
		}

		return redPigGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Goods obj) {
		redPigGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Goods obj) {
		redPigGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsMapper.deleteById(id);
	}


	public Goods selectByPrimaryKey(Long id) {
		return redPigGoodsMapper.selectByPrimaryKey(id);
	}


	public List<Goods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Goods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;
	}

	@Transactional(readOnly = false)

	public void batchDeleteGoodsPhotos(Long goodsId, List<Accessory> goodsPhotos) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goodsId", goodsId);
		params.put("goodsPhotos", goodsPhotos);
		if (goodsId != null && goodsPhotos != null && goodsPhotos.size() > 0) {
			redPigGoodsMapper.batchDeleteGoodsPhotos(params);
		}
	}

	@Transactional(readOnly = false)

	public void batchDeleteUserGoodsClass(Long goodsId, List<UserGoodsClass> ugcs) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goodsId", goodsId);
		params.put("ugcs", ugcs);
		if (goodsId != null && ugcs != null && ugcs.size() > 0) {
			redPigGoodsMapper.batchDeleteUserGoodsClass(params);
		}
	}

	@Transactional(readOnly = false)

	public void batchDeleteGoodsSpecProperty(Long goodsId, List<GoodsSpecProperty> gsps) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goodsId", goodsId);
		params.put("gsps", gsps);
		if (goodsId != null && gsps != null && gsps.size() > 0) {
			redPigGoodsMapper.batchDeleteGoodsSpecProperty(params);
		}
	}

	@Transactional(readOnly = false)

	public void deleteGoodsMainPhoto(Long id) {
		redPigGoodsMapper.deleteGoodsMainPhoto(id);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		redPigGoodsMapper.delete(id);
	}


	@Transactional(readOnly = false)
	public void update(Goods obj) {
		redPigGoodsMapper.update(obj);
	}


	public List<Goods> queryGoodsByGoodsClassIds(Map<String, Object> params) {
		List<Goods> lists = Lists.newArrayList();
		if (params != null && params.size() > 0) {
			lists = redPigGoodsMapper.queryGoodsByGoodsClassIds(params);
		}

		return lists;
	}

	@Transactional(readOnly = false)

	public void removeGoodsBrandByGoodsBrandId(Map<String, Object> params) {
		redPigGoodsMapper.removeGoodsBrandByGoodsBrandId(params);
	}

	@Transactional(readOnly = false)

	public void saveGoodsPhotos(Long id, List<Accessory> goods_photos) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_id", id);
		params.put("goods_photos", goods_photos);
		if (goods_photos != null && goods_photos.size() > 0) {
			redPigGoodsMapper.batchInsertGoodsPhotos(params);
		}
	}

	@Transactional(readOnly = false)

	public void saveGoodsSpecProperty(Long id, List<GoodsSpecProperty> gsps) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_id", id);
		params.put("gsps", gsps);
		if (gsps != null && gsps.size() > 0) {
			redPigGoodsMapper.batchInsertGoodsSpecs(params);
		}
	}
	
	@Transactional(readOnly = false)

	public void saveGoodsSpecProperty2(Long id, List<GoodsSpecProperty> gsps) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_id", id);
		params.put("gsps", gsps);
		if (gsps != null && gsps.size() > 0) {
			redPigGoodsMapper.batchInsertGoodsSpecs2(params);
		}
	}
	
	@Transactional(readOnly = false)

	public void saveGoodsUserGoodsClass(Long id, List<UserGoodsClass> goods_ugcs) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("goods_id", id);
		params.put("goods_ugcs", goods_ugcs);
		if (goods_ugcs != null && goods_ugcs.size() > 0) {
			redPigGoodsMapper.saveGoodsUserGoodsClass(params);
		}
	}


	public List<Map<String, Object>> queryGoodsList(Map<String, Object> params) {
		params.put("deleteStatus", "0");
		return redPigGoodsMapper.queryGoodsList(params);
		
	}


	public Map<String, Object> selectGoodsById(Long id) {
		return redPigGoodsMapper.selectGoodsById(id);
	}

}
