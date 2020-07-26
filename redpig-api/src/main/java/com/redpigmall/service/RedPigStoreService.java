package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.StoreMapper;
import com.redpigmall.domain.Store;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStoreService extends BaseService<Store> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Store> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStoreMapper.batchDelete(objs);
		}
	}


	public Store getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Store> objs = redPigStoreMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Store> selectObjByProperty(Map<String, Object> maps) {
		return redPigStoreMapper.selectObjByProperty(maps);
	}


	public List<Store> queryPages(Map<String, Object> params) {
		return redPigStoreMapper.queryPages(params);
	}


	public List<Store> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStoreMapper.queryPageListWithNoRelations(param);
	}


	public List<Store> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigStoreMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StoreMapper redPigStoreMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStoreMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Store obj) {
		redPigStoreMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Store obj) {
		redPigStoreMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStoreMapper.deleteById(id);
	}


	public Store selectByPrimaryKey(Long id) {
		return redPigStoreMapper.selectByPrimaryKey(id);
	}


	public List<Store> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Store> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStoreMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}


	public Store queryByProperty(Map<String, Object> params) {
		List<Store> stores = this.queryPageList(params);
		if (stores != null && stores.size() > 0) {
			return stores.get(0);
		}
		return null;
	}


	@Transactional(readOnly = false)
	public void save(Store store) {
		redPigStoreMapper.save(store);
	}


	public List<Store> listByNoRelation(Map<String, Object> params, Integer begin, Integer max) {
		return redPigStoreMapper.listByNoRelation(params);
	}


	public IPageList listByNoRelation(Map<String, Object> params) {
		return super.listByNoRelation(params);
	}
}
