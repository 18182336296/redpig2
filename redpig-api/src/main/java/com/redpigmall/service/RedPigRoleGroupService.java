package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.RoleGroup;
import com.redpigmall.dao.RoleGroupMapper;
import com.redpigmall.service.RedPigRoleGroupService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigRoleGroupService extends BaseService<RoleGroup>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<RoleGroup> objs) {
		if (objs != null && objs.size() > 0) {
			redPigRoleGroupMapper.batchDelete(objs);
		}
	}


	public RoleGroup getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<RoleGroup> objs = redPigRoleGroupMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<RoleGroup> selectObjByProperty(Map<String, Object> maps) {
		return redPigRoleGroupMapper.selectObjByProperty(maps);
	}


	public List<RoleGroup> queryPages(Map<String, Object> params) {
		return redPigRoleGroupMapper.queryPages(params);
	}


	public List<RoleGroup> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigRoleGroupMapper.queryPageListWithNoRelations(param);
	}


	public List<RoleGroup> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigRoleGroupMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private RoleGroupMapper redPigRoleGroupMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigRoleGroupMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(RoleGroup obj) {
		redPigRoleGroupMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(RoleGroup obj) {
		redPigRoleGroupMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigRoleGroupMapper.deleteById(id);
	}


	public RoleGroup selectByPrimaryKey(Long id) {
		return redPigRoleGroupMapper.selectByPrimaryKey(id);
	}


	public List<RoleGroup> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<RoleGroup> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigRoleGroupMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}
	

	public List<String> queryGroupNameLists(Long id) {
		return redPigRoleGroupMapper.queryGroupNameLists(id);
	}
}
