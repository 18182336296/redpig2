package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GroupArea;
import com.redpigmall.dao.GroupAreaMapper;
import com.redpigmall.service.RedPigGroupAreaService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGroupAreaService extends BaseService<GroupArea>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GroupArea> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGroupAreaMapper.batchDelete(objs);
		}
	}


	public GroupArea getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GroupArea> objs = redPigGroupAreaMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GroupArea> selectObjByProperty(Map<String, Object> maps) {
		return redPigGroupAreaMapper.selectObjByProperty(maps);
	}


	public List<GroupArea> queryPages(Map<String, Object> params) {
		return redPigGroupAreaMapper.queryPages(params);
	}


	public List<GroupArea> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGroupAreaMapper.queryPageListWithNoRelations(param);
	}


	public List<GroupArea> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGroupAreaMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GroupAreaMapper redPigGroupAreaMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGroupAreaMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GroupArea obj) {
		redPigGroupAreaMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GroupArea obj) {
		redPigGroupAreaMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGroupAreaMapper.deleteById(id);
	}


	public GroupArea selectByPrimaryKey(Long id) {
		return redPigGroupAreaMapper.selectByPrimaryKey(id);
	}


	public List<GroupArea> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GroupArea> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGroupAreaMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<GroupArea> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigGroupAreaMapper.queryPageListByParentIsNull(params);
	}
}
