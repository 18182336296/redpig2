package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.SalesLog;
import com.redpigmall.dao.SalesLogMapper;
import com.redpigmall.service.RedPigSalesLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigSalesLogService extends BaseService<SalesLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<SalesLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigSalesLogMapper.batchDelete(objs);
		}
	}


	public SalesLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<SalesLog> objs = redPigSalesLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<SalesLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigSalesLogMapper.selectObjByProperty(maps);
	}


	public List<SalesLog> queryPages(Map<String, Object> params) {
		return redPigSalesLogMapper.queryPages(params);
	}


	public List<SalesLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigSalesLogMapper.queryPageListWithNoRelations(param);
	}


	public List<SalesLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigSalesLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private SalesLogMapper redPigSalesLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigSalesLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(SalesLog obj) {
		redPigSalesLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(SalesLog obj) {
		redPigSalesLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigSalesLogMapper.deleteById(id);
	}


	public SalesLog selectByPrimaryKey(Long id) {
		return redPigSalesLogMapper.selectByPrimaryKey(id);
	}


	public List<SalesLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<SalesLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigSalesLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
