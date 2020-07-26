package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ExpressCompanyCommon;
import com.redpigmall.dao.ExpressCompanyCommonMapper;
import com.redpigmall.service.RedPigExpressCompanyCommonService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigExpressCompanyCommonService extends BaseService<ExpressCompanyCommon>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ExpressCompanyCommon> objs) {
		if (objs != null && objs.size() > 0) {
			redPigExpressCompanyCommonMapper.batchDelete(objs);
		}
	}


	public ExpressCompanyCommon getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ExpressCompanyCommon> objs = redPigExpressCompanyCommonMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ExpressCompanyCommon> selectObjByProperty(Map<String, Object> maps) {
		return redPigExpressCompanyCommonMapper.selectObjByProperty(maps);
	}


	public List<ExpressCompanyCommon> queryPages(Map<String, Object> params) {
		return redPigExpressCompanyCommonMapper.queryPages(params);
	}


	public List<ExpressCompanyCommon> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigExpressCompanyCommonMapper.queryPageListWithNoRelations(param);
	}


	public List<ExpressCompanyCommon> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigExpressCompanyCommonMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ExpressCompanyCommonMapper redPigExpressCompanyCommonMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigExpressCompanyCommonMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ExpressCompanyCommon obj) {
		redPigExpressCompanyCommonMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ExpressCompanyCommon obj) {
		redPigExpressCompanyCommonMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigExpressCompanyCommonMapper.deleteById(id);
	}


	public ExpressCompanyCommon selectByPrimaryKey(Long id) {
		return redPigExpressCompanyCommonMapper.selectByPrimaryKey(id);
	}


	public List<ExpressCompanyCommon> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ExpressCompanyCommon> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigExpressCompanyCommonMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
