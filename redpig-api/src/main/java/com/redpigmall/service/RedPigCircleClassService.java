package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CircleClass;
import com.redpigmall.dao.CircleClassMapper;
import com.redpigmall.service.RedPigCircleClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCircleClassService extends BaseService<CircleClass>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CircleClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCircleClassMapper.batchDelete(objs);
		}
	}


	public CircleClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CircleClass> objs = redPigCircleClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CircleClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigCircleClassMapper.selectObjByProperty(maps);
	}


	public List<CircleClass> queryPages(Map<String, Object> params) {
		return redPigCircleClassMapper.queryPages(params);
	}


	public List<CircleClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCircleClassMapper.queryPageListWithNoRelations(param);
	}


	public List<CircleClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCircleClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CircleClassMapper redPigCircleClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCircleClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CircleClass obj) {
		redPigCircleClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CircleClass obj) {
		redPigCircleClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCircleClassMapper.deleteById(id);
	}


	public CircleClass selectByPrimaryKey(Long id) {
		return redPigCircleClassMapper.selectByPrimaryKey(id);
	}


	public List<CircleClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CircleClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCircleClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}
}
