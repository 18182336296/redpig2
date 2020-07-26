package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GroupClass;
import com.redpigmall.dao.GroupClassMapper;
import com.redpigmall.service.RedPigGroupClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupClassService extends BaseService<GroupClass> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupClassMapper.batchDelete(objs);
		}
	}


	public GroupClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupClass> objs = redPigGroupClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupClassMapper.selectObjByProperty(maps);
	}


	public List<GroupClass> queryPages(Map<String, Object> params) {
		return redPigGroupClassMapper.queryPages(params);
	}


	public List<GroupClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupClassMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupClassMapper redPigGroupClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGroupClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupClass obj) {
		redPigGroupClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupClass obj) {
		redPigGroupClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupClassMapper.deleteById(id);
	}


	public GroupClass selectByPrimaryKey(Long id) {
		return redPigGroupClassMapper.selectByPrimaryKey(id);
	}


	public List<GroupClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<GroupClass> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigGroupClassMapper.queryPageListByParentIsNull(params);
	}
}
