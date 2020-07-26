package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.FreeClass;
import com.redpigmall.dao.FreeClassMapper;
import com.redpigmall.service.RedPigFreeClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigFreeClassService extends BaseService<FreeClass> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<FreeClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigFreeClassMapper.batchDelete(objs);
		}
	}


	public FreeClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<FreeClass> objs = redPigFreeClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<FreeClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigFreeClassMapper.selectObjByProperty(maps);
	}


	public List<FreeClass> queryPages(Map<String, Object> params) {
		return redPigFreeClassMapper.queryPages(params);
	}


	public List<FreeClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigFreeClassMapper.queryPageListWithNoRelations(param);
	}


	public List<FreeClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigFreeClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private FreeClassMapper redPigFreeClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigFreeClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(FreeClass obj) {
		redPigFreeClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(FreeClass obj) {
		redPigFreeClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigFreeClassMapper.deleteById(id);
	}


	public FreeClass selectByPrimaryKey(Long id) {
		return redPigFreeClassMapper.selectByPrimaryKey(id);
	}


	public List<FreeClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<FreeClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigFreeClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
