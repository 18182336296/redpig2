package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.PayoffLog;
import com.redpigmall.dao.PayoffLogMapper;
import com.redpigmall.service.RedPigPayoffLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigPayoffLogService extends BaseService<PayoffLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<PayoffLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigPayoffLogMapper.batchDelete(objs);
		}
	}


	public PayoffLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<PayoffLog> objs = redPigPayoffLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<PayoffLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigPayoffLogMapper.selectObjByProperty(maps);
	}


	public List<PayoffLog> queryPages(Map<String, Object> params) {
		return redPigPayoffLogMapper.queryPages(params);
	}


	public List<PayoffLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigPayoffLogMapper.queryPageListWithNoRelations(param);
	}


	public List<PayoffLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigPayoffLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private PayoffLogMapper redPigPayoffLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigPayoffLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(PayoffLog obj) {
		redPigPayoffLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(PayoffLog obj) {
		redPigPayoffLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigPayoffLogMapper.deleteById(id);
	}


	public PayoffLog selectByPrimaryKey(Long id) {
		return redPigPayoffLogMapper.selectByPrimaryKey(id);
	}


	public List<PayoffLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<PayoffLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigPayoffLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
