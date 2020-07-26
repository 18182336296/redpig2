package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.GroupMapper;
import com.redpigmall.service.RedPigGroupService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupService extends BaseService<Group>  {
	
	@Autowired
	private GroupMapper redPigGroupMapper;
	

	@Transactional(readOnly = false)
	public void batchDelObjs(List<Group> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupMapper.batchDelete(objs);
		}
	}


	public Group getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Group> objs = redPigGroupMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Group> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupMapper.selectObjByProperty(maps);
	}


	public List<Group> queryPages(Map<String, Object> params) {
		return redPigGroupMapper.queryPages(params);
	}


	public List<Group> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupMapper.queryPageListWithNoRelations(param);
	}


	public List<Group> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigGroupMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGroupMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Group obj) {
		redPigGroupMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Group obj) {
		redPigGroupMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupMapper.deleteById(id);
	}


	public Group selectByPrimaryKey(Long id) {
		return redPigGroupMapper.selectByPrimaryKey(id);
	}


	public List<Group> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		List<Group> groups = super.queryPageList(params, begin, max);
		if (groups == null) {
			return Lists.newArrayList();
		}
		return groups;
	}


	public List<Group> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
