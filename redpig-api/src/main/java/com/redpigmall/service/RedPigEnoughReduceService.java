package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.EnoughReduce;
import com.redpigmall.dao.EnoughReduceMapper;
import com.redpigmall.service.RedPigEnoughReduceService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigEnoughReduceService extends BaseService<EnoughReduce>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<EnoughReduce> objs) {
		if (objs != null && objs.size() > 0) {
			redPigEnoughReduceMapper.batchDelete(objs);
		}
	}


	public EnoughReduce getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<EnoughReduce> objs = redPigEnoughReduceMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<EnoughReduce> selectObjByProperty(Map<String, Object> maps) {
		return redPigEnoughReduceMapper.selectObjByProperty(maps);
	}


	public List<EnoughReduce> queryPages(Map<String, Object> params) {
		return redPigEnoughReduceMapper.queryPages(params);
	}


	public List<EnoughReduce> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigEnoughReduceMapper.queryPageListWithNoRelations(param);
	}


	public List<EnoughReduce> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigEnoughReduceMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private EnoughReduceMapper redPigEnoughReduceMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigEnoughReduceMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(EnoughReduce obj) {
		redPigEnoughReduceMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(EnoughReduce obj) {
		redPigEnoughReduceMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigEnoughReduceMapper.deleteById(id);
	}


	public EnoughReduce selectByPrimaryKey(Long id) {
		return redPigEnoughReduceMapper.selectByPrimaryKey(id);
	}


	public List<EnoughReduce> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<EnoughReduce> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigEnoughReduceMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
