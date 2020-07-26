package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.UserShare;
import com.redpigmall.dao.UserShareMapper;
import com.redpigmall.service.RedPigUserShareService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigUserShareService extends BaseService<UserShare>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<UserShare> objs) {
		if (objs != null && objs.size() > 0) {
			redPigUserShareMapper.batchDelete(objs);
		}
	}


	public UserShare getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<UserShare> objs = redPigUserShareMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<UserShare> selectObjByProperty(Map<String, Object> maps) {
		return redPigUserShareMapper.selectObjByProperty(maps);
	}


	public List<UserShare> queryPages(Map<String, Object> params) {
		return redPigUserShareMapper.queryPages(params);
	}


	public List<UserShare> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigUserShareMapper.queryPageListWithNoRelations(param);
	}


	public List<UserShare> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigUserShareMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private UserShareMapper redPigUserShareMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigUserShareMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(UserShare obj) {
		redPigUserShareMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(UserShare obj) {
		redPigUserShareMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigUserShareMapper.deleteById(id);
	}


	public UserShare selectByPrimaryKey(Long id) {
		return redPigUserShareMapper.selectByPrimaryKey(id);
	}


	public List<UserShare> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<UserShare> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigUserShareMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
