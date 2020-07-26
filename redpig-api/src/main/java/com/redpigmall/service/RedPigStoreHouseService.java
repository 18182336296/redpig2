package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.StoreHouse;
import com.redpigmall.dao.StoreHouseMapper;
import com.redpigmall.service.RedPigStoreHouseService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStoreHouseService extends BaseService<StoreHouse>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<StoreHouse> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStoreHouseMapper.batchDelete(objs);
		}
	}


	public StoreHouse getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<StoreHouse> objs = redPigStoreHouseMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<StoreHouse> selectObjByProperty(Map<String, Object> maps) {
		return redPigStoreHouseMapper.selectObjByProperty(maps);
	}


	public List<StoreHouse> queryPages(Map<String, Object> params) {
		return redPigStoreHouseMapper.queryPages(params);
	}


	public List<StoreHouse> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStoreHouseMapper.queryPageListWithNoRelations(param);
	}


	public List<StoreHouse> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigStoreHouseMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StoreHouseMapper redPigStoreHouseMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStoreHouseMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(StoreHouse obj) {
		redPigStoreHouseMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(StoreHouse obj) {
		redPigStoreHouseMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStoreHouseMapper.deleteById(id);
	}


	public StoreHouse selectByPrimaryKey(Long id) {
		return redPigStoreHouseMapper.selectByPrimaryKey(id);
	}


	public List<StoreHouse> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<StoreHouse> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStoreHouseMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
