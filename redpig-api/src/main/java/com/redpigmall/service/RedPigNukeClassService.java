package com.redpigmall.service;

import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.domain.NukeClass;
import com.redpigmall.dao.NukeClassMapper;
import com.redpigmall.service.RedPigNukeClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigNukeClassService extends BaseService<NukeClass> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<NukeClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigNukeClassMapper.batchDelete(objs);
		}
	}


	public NukeClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<NukeClass> objs = redPigNukeClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<NukeClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigNukeClassMapper.selectObjByProperty(maps);
	}


	public List<NukeClass> queryPages(Map<String, Object> params) {
		return redPigNukeClassMapper.queryPages(params);
	}


	public List<NukeClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigNukeClassMapper.queryPageListWithNoRelations(param);
	}


	public List<NukeClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigNukeClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private NukeClassMapper redPigNukeClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigNukeClassMapper.batchDeleteByIds(ids);
	}
	
	@Transactional(readOnly = false)
	public void batchDeleteByIds(String[] ids) {
		List<Long> lists = Lists.newArrayList();
		for (String id : ids) {
			lists.add(CommUtil.null2Long(id));
		}
		
		
		redPigNukeClassMapper.batchDeleteByIds(lists);
	}
	
	@Transactional(readOnly = false)
	public void saveEntity(NukeClass obj) {
		redPigNukeClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(NukeClass obj) {
		redPigNukeClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigNukeClassMapper.deleteById(id);
	}


	public NukeClass selectByPrimaryKey(Long id) {
		return redPigNukeClassMapper.selectByPrimaryKey(id);
	}


	public List<NukeClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<NukeClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigNukeClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<NukeClass> queryPageListByParentIsNull(Map<String, Object> params, int begin, int max) {

		return redPigNukeClassMapper.queryPageListByParentIsNull(params);
	}
}
