package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.StringUtils;
import com.redpigmall.dao.SubjectMapper;
import com.redpigmall.domain.Subject;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigSubjectService extends BaseService<Subject> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Subject> objs) {
		if (objs != null && objs.size() > 0) {
			redPigSubjectMapper.batchDelete(objs);
		}
	}


	public Subject getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Subject> objs = redPigSubjectMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Subject> selectObjByProperty(Map<String, Object> maps) {
		return redPigSubjectMapper.selectObjByProperty(maps);
	}


	public List<Subject> queryPages(Map<String, Object> params) {
		return redPigSubjectMapper.queryPages(params);
	}


	public List<Subject> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigSubjectMapper.queryPageListWithNoRelations(param);
	}


	public List<Subject> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigSubjectMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private SubjectMapper redPigSubjectMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigSubjectMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Subject obj) {
		redPigSubjectMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Subject obj) {
		redPigSubjectMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigSubjectMapper.deleteById(id);
	}


	public Subject selectByPrimaryKey(Long id) {
		return redPigSubjectMapper.selectByPrimaryKey(id);
	}


	public List<Subject> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Subject> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigSubjectMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void batchDeleteByIds(String[] ids) {
		List<Long> listIds = Lists.newArrayList();

		for (String id : ids) {
			if (StringUtils.isNotBlank(id)) {
				listIds.add(Long.parseLong(id));
			}
		}

		redPigSubjectMapper.batchDeleteByIds(listIds);
	}
}
