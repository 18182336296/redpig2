package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.StoreStatMapper;
import com.redpigmall.domain.StoreStat;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStoreStatService extends BaseService<StoreStat>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<StoreStat> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStoreStatMapper.batchDelete(objs);
		}
	}


	public StoreStat getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<StoreStat> objs = redPigStoreStatMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<StoreStat> selectObjByProperty(Map<String, Object> maps) {
		return redPigStoreStatMapper.selectObjByProperty(maps);
	}


	public List<StoreStat> queryPages(Map<String, Object> params) {
		return redPigStoreStatMapper.queryPages(params);
	}


	public List<StoreStat> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStoreStatMapper.queryPageListWithNoRelations(param);
	}


	public List<StoreStat> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigStoreStatMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StoreStatMapper redPigStoreStatMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStoreStatMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(StoreStat obj) {
		redPigStoreStatMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(StoreStat obj) {
		redPigStoreStatMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStoreStatMapper.deleteById(id);
	}


	public StoreStat selectByPrimaryKey(Long id) {
		return redPigStoreStatMapper.selectByPrimaryKey(id);
	}


	public List<StoreStat> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<StoreStat> queryPageList(Map<String, Object> params) {
		if (params == null) {
			params = Maps.newHashMap();
		}
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStoreStatMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
