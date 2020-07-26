package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.RefundLog;
import com.redpigmall.dao.RefundLogMapper;
import com.redpigmall.service.RedPigRefundLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigRefundLogService extends BaseService<RefundLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<RefundLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigRefundLogMapper.batchDelete(objs);
		}
	}


	public RefundLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<RefundLog> objs = redPigRefundLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<RefundLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigRefundLogMapper.selectObjByProperty(maps);
	}


	public List<RefundLog> queryPages(Map<String, Object> params) {
		return redPigRefundLogMapper.queryPages(params);
	}


	public List<RefundLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigRefundLogMapper.queryPageListWithNoRelations(param);
	}


	public List<RefundLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigRefundLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private RefundLogMapper redPigRefundLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigRefundLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(RefundLog obj) {
		redPigRefundLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(RefundLog obj) {
		redPigRefundLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigRefundLogMapper.deleteById(id);
	}


	public RefundLog selectByPrimaryKey(Long id) {
		return redPigRefundLogMapper.selectByPrimaryKey(id);
	}


	public List<RefundLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<RefundLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigRefundLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
