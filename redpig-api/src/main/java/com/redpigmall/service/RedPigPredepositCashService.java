package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.PredepositCash;
import com.redpigmall.dao.PredepositCashMapper;
import com.redpigmall.service.RedPigPredepositCashService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigPredepositCashService extends BaseService<PredepositCash>
		 {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<PredepositCash> objs) {
		if (objs != null && objs.size() > 0) {
			redPigPredepositCashMapper.batchDelete(objs);
		}
	}


	public PredepositCash getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<PredepositCash> objs = redPigPredepositCashMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<PredepositCash> selectObjByProperty(Map<String, Object> maps) {
		return redPigPredepositCashMapper.selectObjByProperty(maps);
	}


	public List<PredepositCash> queryPages(Map<String, Object> params) {
		return redPigPredepositCashMapper.queryPages(params);
	}


	public List<PredepositCash> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigPredepositCashMapper.queryPageListWithNoRelations(param);
	}


	public List<PredepositCash> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigPredepositCashMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private PredepositCashMapper redPigPredepositCashMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigPredepositCashMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(PredepositCash obj) {
		redPigPredepositCashMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(PredepositCash obj) {
		redPigPredepositCashMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigPredepositCashMapper.deleteById(id);
	}


	public PredepositCash selectByPrimaryKey(Long id) {
		return redPigPredepositCashMapper.selectByPrimaryKey(id);
	}


	public List<PredepositCash> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<PredepositCash> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigPredepositCashMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
