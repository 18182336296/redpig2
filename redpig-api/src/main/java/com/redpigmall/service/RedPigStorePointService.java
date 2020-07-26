package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.StorePoint;
import com.redpigmall.dao.StorePointMapper;
import com.redpigmall.service.RedPigStorePointService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStorePointService extends BaseService<StorePoint>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<StorePoint> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStorePointMapper.batchDelete(objs);
		}
	}


	public StorePoint getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<StorePoint> objs = redPigStorePointMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<StorePoint> selectObjByProperty(Map<String, Object> maps) {
		return redPigStorePointMapper.selectObjByProperty(maps);
	}


	public List<StorePoint> queryPages(Map<String, Object> params) {
		return redPigStorePointMapper.queryPages(params);
	}


	public List<StorePoint> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStorePointMapper.queryPageListWithNoRelations(param);
	}


	public List<StorePoint> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigStorePointMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StorePointMapper redPigStorePointMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStorePointMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(StorePoint obj) {
		redPigStorePointMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(StorePoint obj) {
		redPigStorePointMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStorePointMapper.deleteById(id);
	}


	public StorePoint selectByPrimaryKey(Long id) {
		return redPigStorePointMapper.selectByPrimaryKey(id);
	}


	public List<StorePoint> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<StorePoint> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStorePointMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
