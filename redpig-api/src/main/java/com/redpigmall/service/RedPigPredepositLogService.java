package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.PredepositLog;
import com.redpigmall.dao.PredepositLogMapper;
import com.redpigmall.service.RedPigPredepositLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigPredepositLogService extends BaseService<PredepositLog> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<PredepositLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigPredepositLogMapper.batchDelete(objs);
		}
	}


	public PredepositLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<PredepositLog> objs = redPigPredepositLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<PredepositLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigPredepositLogMapper.selectObjByProperty(maps);
	}


	public List<PredepositLog> queryPages(Map<String, Object> params) {
		return redPigPredepositLogMapper.queryPages(params);
	}


	public List<PredepositLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigPredepositLogMapper.queryPageListWithNoRelations(param);
	}


	public List<PredepositLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigPredepositLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private PredepositLogMapper redPigPredepositLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigPredepositLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(PredepositLog obj) {
		redPigPredepositLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(PredepositLog obj) {
		redPigPredepositLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigPredepositLogMapper.deleteById(id);
	}


	public PredepositLog selectByPrimaryKey(Long id) {
		return redPigPredepositLogMapper.selectByPrimaryKey(id);
	}


	public List<PredepositLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<PredepositLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigPredepositLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
