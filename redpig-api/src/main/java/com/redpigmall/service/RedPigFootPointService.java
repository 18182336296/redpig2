package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.FootPoint;
import com.redpigmall.dao.FootPointMapper;
import com.redpigmall.service.RedPigFootPointService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigFootPointService extends BaseService<FootPoint>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<FootPoint> objs) {
		if (objs != null && objs.size() > 0) {
			redPigFootPointMapper.batchDelete(objs);
		}
	}


	public FootPoint getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<FootPoint> objs = redPigFootPointMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<FootPoint> selectObjByProperty(Map<String, Object> maps) {
		return redPigFootPointMapper.selectObjByProperty(maps);
	}


	public List<FootPoint> queryPages(Map<String, Object> params) {
		return redPigFootPointMapper.queryPages(params);
	}


	public List<FootPoint> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigFootPointMapper.queryPageListWithNoRelations(param);
	}


	public List<FootPoint> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigFootPointMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private FootPointMapper redPigFootPointMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigFootPointMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(FootPoint obj) {
		redPigFootPointMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(FootPoint obj) {
		redPigFootPointMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigFootPointMapper.deleteById(id);
	}


	public FootPoint selectByPrimaryKey(Long id) {
		return redPigFootPointMapper.selectByPrimaryKey(id);
	}


	public List<FootPoint> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<FootPoint> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigFootPointMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
