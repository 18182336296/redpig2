package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.IntegralGoods;
import com.redpigmall.dao.IntegralGoodsMapper;
import com.redpigmall.service.RedPigIntegralGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigIntegralGoodsService extends BaseService<IntegralGoods>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<IntegralGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigIntegralGoodsMapper.batchDelete(objs);
		}
	}


	public IntegralGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<IntegralGoods> objs = redPigIntegralGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<IntegralGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigIntegralGoodsMapper.selectObjByProperty(maps);
	}


	public List<IntegralGoods> queryPages(Map<String, Object> params) {
		return redPigIntegralGoodsMapper.queryPages(params);
	}


	public List<IntegralGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigIntegralGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<IntegralGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigIntegralGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private IntegralGoodsMapper redPigIntegralGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigIntegralGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(IntegralGoods obj) {
		redPigIntegralGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(IntegralGoods obj) {
		redPigIntegralGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigIntegralGoodsMapper.deleteById(id);
	}


	public IntegralGoods selectByPrimaryKey(Long id) {
		return redPigIntegralGoodsMapper.selectByPrimaryKey(id);
	}


	public List<IntegralGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<IntegralGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigIntegralGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
