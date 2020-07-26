package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.UserDynamic;
import com.redpigmall.dao.UserDynamicMapper;
import com.redpigmall.service.RedPigUserDynamicService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigUserDynamicService extends BaseService<UserDynamic>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<UserDynamic> objs) {
		if (objs != null && objs.size() > 0) {
			redPigUserDynamicMapper.batchDelete(objs);
		}
	}


	public UserDynamic getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<UserDynamic> objs = redPigUserDynamicMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<UserDynamic> selectObjByProperty(Map<String, Object> maps) {
		return redPigUserDynamicMapper.selectObjByProperty(maps);
	}


	public List<UserDynamic> queryPages(Map<String, Object> params) {
		return redPigUserDynamicMapper.queryPages(params);
	}


	public List<UserDynamic> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigUserDynamicMapper.queryPageListWithNoRelations(param);
	}


	public List<UserDynamic> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigUserDynamicMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private UserDynamicMapper redPigUserDynamicMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigUserDynamicMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(UserDynamic obj) {
		redPigUserDynamicMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(UserDynamic obj) {
		redPigUserDynamicMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigUserDynamicMapper.deleteById(id);
	}


	public UserDynamic selectByPrimaryKey(Long id) {
		return redPigUserDynamicMapper.selectByPrimaryKey(id);
	}


	public List<UserDynamic> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<UserDynamic> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigUserDynamicMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
