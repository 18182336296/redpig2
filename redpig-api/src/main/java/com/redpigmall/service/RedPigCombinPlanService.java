package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CombinPlan;
import com.redpigmall.dao.CombinPlanMapper;
import com.redpigmall.service.RedPigCombinPlanService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCombinPlanService extends BaseService<CombinPlan>   {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CombinPlan> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCombinPlanMapper.batchDelete(objs);
		}
	}


	public CombinPlan getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CombinPlan> objs = redPigCombinPlanMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CombinPlan> selectObjByProperty(Map<String, Object> maps) {
		return redPigCombinPlanMapper.selectObjByProperty(maps);
	}


	public List<CombinPlan> queryPages(Map<String, Object> params) {
		return redPigCombinPlanMapper.queryPages(params);
	}


	public List<CombinPlan> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCombinPlanMapper.queryPageListWithNoRelations(param);
	}


	public List<CombinPlan> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCombinPlanMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private CombinPlanMapper redPigCombinPlanMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCombinPlanMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CombinPlan obj) {
		redPigCombinPlanMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CombinPlan obj) {
		redPigCombinPlanMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCombinPlanMapper.deleteById(id);
	}


	public CombinPlan selectByPrimaryKey(Long id) {
		return redPigCombinPlanMapper.selectByPrimaryKey(id);
	}


	public List<CombinPlan> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CombinPlan> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCombinPlanMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}
}
