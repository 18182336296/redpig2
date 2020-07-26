package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Circle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.CircleMapper;
import com.redpigmall.service.RedPigCircleService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCircleService extends BaseService<Circle>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Circle> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCircleMapper.batchDelete(objs);
		}
	}


	public Circle getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Circle> objs = redPigCircleMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Circle> selectObjByProperty(Map<String, Object> maps) {
		return redPigCircleMapper.selectObjByProperty(maps);
	}


	public List<Circle> queryPages(Map<String, Object> params) {
		return redPigCircleMapper.queryPages(params);
	}


	public List<Circle> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCircleMapper.queryPageListWithNoRelations(param);
	}


	public List<Circle> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigCircleMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CircleMapper redPigCircleMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCircleMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Circle obj) {
		redPigCircleMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Circle obj) {
		redPigCircleMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCircleMapper.deleteById(id);
	}


	public Circle selectByPrimaryKey(Long id) {
		return redPigCircleMapper.selectByPrimaryKey(id);
	}


	public List<Circle> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Circle> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCircleMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
