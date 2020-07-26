package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.AppIndexFloor;
import com.redpigmall.dao.AppIndexFloorMapper;
import com.redpigmall.service.RedPigAppIndexFloorService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAppIndexFloorService extends BaseService<AppIndexFloor> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<AppIndexFloor> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAppIndexFloorMapper.batchDelete(objs);
		}
	}


	public AppIndexFloor getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<AppIndexFloor> objs = redPigAppIndexFloorMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<AppIndexFloor> selectObjByProperty(Map<String, Object> maps) {
		return redPigAppIndexFloorMapper.selectObjByProperty(maps);
	}


	public List<AppIndexFloor> queryPages(Map<String, Object> params) {
		return redPigAppIndexFloorMapper.queryPages(params);
	}


	public List<AppIndexFloor> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAppIndexFloorMapper.queryPageListWithNoRelations(param);
	}


	public List<AppIndexFloor> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigAppIndexFloorMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private AppIndexFloorMapper redPigAppIndexFloorMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAppIndexFloorMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(AppIndexFloor obj) {
		redPigAppIndexFloorMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(AppIndexFloor obj) {
		redPigAppIndexFloorMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAppIndexFloorMapper.deleteById(id);
	}


	public AppIndexFloor selectByPrimaryKey(Long id) {
		return redPigAppIndexFloorMapper.selectByPrimaryKey(id);
	}


	public List<AppIndexFloor> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<AppIndexFloor> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAppIndexFloorMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}

}
