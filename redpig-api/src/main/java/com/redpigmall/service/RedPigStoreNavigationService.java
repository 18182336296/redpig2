package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.StoreNavigation;
import com.redpigmall.dao.StoreNavigationMapper;
import com.redpigmall.service.RedPigStoreNavigationService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStoreNavigationService extends BaseService<StoreNavigation>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<StoreNavigation> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStoreNavigationMapper.batchDelete(objs);
		}
	}


	public StoreNavigation getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<StoreNavigation> objs = redPigStoreNavigationMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<StoreNavigation> selectObjByProperty(Map<String, Object> maps) {
		return redPigStoreNavigationMapper.selectObjByProperty(maps);
	}


	public List<StoreNavigation> queryPages(Map<String, Object> params) {
		return redPigStoreNavigationMapper.queryPages(params);
	}


	public List<StoreNavigation> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStoreNavigationMapper.queryPageListWithNoRelations(param);
	}


	public List<StoreNavigation> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigStoreNavigationMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StoreNavigationMapper redPigStoreNavigationMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStoreNavigationMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(StoreNavigation obj) {
		redPigStoreNavigationMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(StoreNavigation obj) {
		redPigStoreNavigationMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStoreNavigationMapper.deleteById(id);
	}


	public StoreNavigation selectByPrimaryKey(Long id) {
		return redPigStoreNavigationMapper.selectByPrimaryKey(id);
	}


	public List<StoreNavigation> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<StoreNavigation> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStoreNavigationMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
