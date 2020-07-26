package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.OrderFormLog;
import com.redpigmall.dao.OrderFormLogMapper;
import com.redpigmall.service.RedPigOrderFormLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigOrderFormLogService extends BaseService<OrderFormLog> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<OrderFormLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigOrderFormLogMapper.batchDelete(objs);
		}
	}


	public OrderFormLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<OrderFormLog> objs = redPigOrderFormLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<OrderFormLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigOrderFormLogMapper.selectObjByProperty(maps);
	}


	public List<OrderFormLog> queryPages(Map<String, Object> params) {
		return redPigOrderFormLogMapper.queryPages(params);
	}


	public List<OrderFormLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigOrderFormLogMapper.queryPageListWithNoRelations(param);
	}


	public List<OrderFormLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigOrderFormLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private OrderFormLogMapper redPigOrderFormLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigOrderFormLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(OrderFormLog obj) {
		redPigOrderFormLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(OrderFormLog obj) {
		redPigOrderFormLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigOrderFormLogMapper.deleteById(id);
	}


	public OrderFormLog selectByPrimaryKey(Long id) {
		return redPigOrderFormLogMapper.selectByPrimaryKey(id);
	}


	public List<OrderFormLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<OrderFormLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigOrderFormLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
