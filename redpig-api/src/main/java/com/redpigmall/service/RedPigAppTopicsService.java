package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.AppTopics;
import com.redpigmall.dao.AppTopicsMapper;
import com.redpigmall.service.RedPigAppTopicsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAppTopicsService extends BaseService<AppTopics> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<AppTopics> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAppTopicsMapper.batchDelete(objs);
		}
	}


	public AppTopics getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<AppTopics> objs = redPigAppTopicsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<AppTopics> selectObjByProperty(Map<String, Object> maps) {
		return redPigAppTopicsMapper.selectObjByProperty(maps);
	}


	public List<AppTopics> queryPages(Map<String, Object> params) {
		return redPigAppTopicsMapper.queryPages(params);
	}


	public List<AppTopics> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAppTopicsMapper.queryPageListWithNoRelations(param);
	}


	public List<AppTopics> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigAppTopicsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private AppTopicsMapper redPigAppTopicsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAppTopicsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(AppTopics obj) {
		redPigAppTopicsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(AppTopics obj) {
		redPigAppTopicsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAppTopicsMapper.deleteById(id);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	public AppTopics selectByPrimaryKey(Long id) {
		return redPigAppTopicsMapper.selectByPrimaryKey(id);
	}


	public List<AppTopics> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<AppTopics> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAppTopicsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
