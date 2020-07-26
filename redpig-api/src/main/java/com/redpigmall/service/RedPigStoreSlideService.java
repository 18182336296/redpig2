package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.StoreSlideMapper;
import com.redpigmall.domain.StoreSlide;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigStoreSlideService extends BaseService<StoreSlide>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<StoreSlide> objs) {
		if (objs != null && objs.size() > 0) {
			redPigStoreSlideMapper.batchDelete(objs);
		}
	}


	public StoreSlide getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<StoreSlide> objs = redPigStoreSlideMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<StoreSlide> selectObjByProperty(Map<String, Object> maps) {
		return redPigStoreSlideMapper.selectObjByProperty(maps);
	}


	public List<StoreSlide> queryPages(Map<String, Object> params) {
		return redPigStoreSlideMapper.queryPages(params);
	}


	public List<StoreSlide> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigStoreSlideMapper.queryPageListWithNoRelations(param);
	}


	public List<StoreSlide> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigStoreSlideMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private StoreSlideMapper redPigStoreSlideMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigStoreSlideMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(StoreSlide obj) {
		redPigStoreSlideMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(StoreSlide obj) {
		redPigStoreSlideMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigStoreSlideMapper.deleteById(id);
	}


	public StoreSlide selectByPrimaryKey(Long id) {
		return redPigStoreSlideMapper.selectByPrimaryKey(id);
	}


	public List<StoreSlide> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<StoreSlide> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigStoreSlideMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
