package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Evaluate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.EvaluateMapper;
import com.redpigmall.service.RedPigEvaluateService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigEvaluateService extends BaseService<Evaluate>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Evaluate> objs) {
		if (objs != null && objs.size() > 0) {
			redPigEvaluateMapper.batchDelete(objs);
		}
	}


	public Evaluate getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Evaluate> objs = redPigEvaluateMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Evaluate> selectObjByProperty(Map<String, Object> maps) {
		return redPigEvaluateMapper.selectObjByProperty(maps);
	}


	public List<Evaluate> queryPages(Map<String, Object> params) {
		return redPigEvaluateMapper.queryPages(params);
	}


	public List<Evaluate> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigEvaluateMapper.queryPageListWithNoRelations(param);
	}


	public List<Evaluate> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigEvaluateMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private EvaluateMapper redPigEvaluateMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigEvaluateMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Evaluate obj) {
		redPigEvaluateMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Evaluate obj) {
		redPigEvaluateMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigEvaluateMapper.deleteById(id);
	}


	public Evaluate selectByPrimaryKey(Long id) {
		return redPigEvaluateMapper.selectByPrimaryKey(id);
	}


	public List<Evaluate> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Evaluate> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigEvaluateMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}


	@Transactional(readOnly = false)
	public void delete(Evaluate e) {
		redPigEvaluateMapper.delete(e);
	}


	@Transactional(readOnly = false)
	public void batchDeleteEvaluates(List<Evaluate> evaluates) {
		if (evaluates != null && evaluates.size() > 0)
			redPigEvaluateMapper.batchDeleteEvaluates(evaluates);
	}
}
