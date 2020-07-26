package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Predeposit;
import com.redpigmall.dao.PredepositMapper;
import com.redpigmall.service.RedPigPredepositService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigPredepositService extends BaseService<Predeposit> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Predeposit> objs) {
		if (objs != null && objs.size() > 0) {
			redPigPredepositMapper.batchDelete(objs);
		}
	}


	public Predeposit getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Predeposit> objs = redPigPredepositMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Predeposit> selectObjByProperty(Map<String, Object> maps) {
		return redPigPredepositMapper.selectObjByProperty(maps);
	}


	public List<Predeposit> queryPages(Map<String, Object> params) {
		return redPigPredepositMapper.queryPages(params);
	}


	public List<Predeposit> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigPredepositMapper.queryPageListWithNoRelations(param);
	}


	public List<Predeposit> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigPredepositMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private PredepositMapper redPigPredepositMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigPredepositMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Predeposit obj) {
		redPigPredepositMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Predeposit obj) {
		redPigPredepositMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigPredepositMapper.deleteById(id);
	}


	public Predeposit selectByPrimaryKey(Long id) {
		return redPigPredepositMapper.selectByPrimaryKey(id);
	}


	public List<Predeposit> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Predeposit> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigPredepositMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
