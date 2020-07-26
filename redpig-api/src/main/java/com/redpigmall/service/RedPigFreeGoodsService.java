package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.FreeGoods;
import com.redpigmall.dao.FreeGoodsMapper;
import com.redpigmall.service.RedPigFreeGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigFreeGoodsService extends BaseService<FreeGoods>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<FreeGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigFreeGoodsMapper.batchDelete(objs);
		}
	}


	public FreeGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<FreeGoods> objs = redPigFreeGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<FreeGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigFreeGoodsMapper.selectObjByProperty(maps);
	}


	public List<FreeGoods> queryPages(Map<String, Object> params) {
		return redPigFreeGoodsMapper.queryPages(params);
	}


	public List<FreeGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigFreeGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<FreeGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigFreeGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private FreeGoodsMapper redPigFreeGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigFreeGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(FreeGoods obj) {
		redPigFreeGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(FreeGoods obj) {
		redPigFreeGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigFreeGoodsMapper.deleteById(id);
	}


	public FreeGoods selectByPrimaryKey(Long id) {
		return redPigFreeGoodsMapper.selectByPrimaryKey(id);
	}


	public List<FreeGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<FreeGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigFreeGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
