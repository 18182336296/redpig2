package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.UserLevel;
import com.redpigmall.dao.UserLevelMapper;
import com.redpigmall.service.RedPigUserLevelService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigUserLevelService extends BaseService<UserLevel> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<UserLevel> objs) {
		if (objs != null && objs.size() > 0) {
			redPigUserLevelMapper.batchDelete(objs);
		}
	}


	public UserLevel getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<UserLevel> objs = redPigUserLevelMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<UserLevel> selectObjByProperty(Map<String, Object> maps) {
		return redPigUserLevelMapper.selectObjByProperty(maps);
	}


	public List<UserLevel> queryPages(Map<String, Object> params) {
		return redPigUserLevelMapper.queryPages(params);
	}


	public List<UserLevel> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigUserLevelMapper.queryPageListWithNoRelations(param);
	}


	public List<UserLevel> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigUserLevelMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private UserLevelMapper redPigUserLevelMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigUserLevelMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(UserLevel obj) {
		redPigUserLevelMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(UserLevel obj) {
		redPigUserLevelMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigUserLevelMapper.deleteById(id);
	}


	public UserLevel selectByPrimaryKey(Long id) {
		return redPigUserLevelMapper.selectByPrimaryKey(id);
	}


	public List<UserLevel> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<UserLevel> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigUserLevelMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
