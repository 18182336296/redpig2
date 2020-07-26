package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.UserTag;
import com.redpigmall.dao.UserTagMapper;
import com.redpigmall.service.RedPigUserTagService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigUserTagService extends BaseService<UserTag>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<UserTag> objs) {
		if (objs != null && objs.size() > 0) {
			redPigUserTagMapper.batchDelete(objs);
		}
	}


	public UserTag getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<UserTag> objs = redPigUserTagMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<UserTag> selectObjByProperty(Map<String, Object> maps) {
		return redPigUserTagMapper.selectObjByProperty(maps);
	}


	public List<UserTag> queryPages(Map<String, Object> params) {
		return redPigUserTagMapper.queryPages(params);
	}


	public List<UserTag> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigUserTagMapper.queryPageListWithNoRelations(param);
	}


	public List<UserTag> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigUserTagMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private UserTagMapper redPigUserTagMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigUserTagMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(UserTag obj) {
		redPigUserTagMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(UserTag obj) {
		redPigUserTagMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigUserTagMapper.deleteById(id);
	}


	public UserTag selectByPrimaryKey(Long id) {
		return redPigUserTagMapper.selectByPrimaryKey(id);
	}


	public List<UserTag> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<UserTag> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigUserTagMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
