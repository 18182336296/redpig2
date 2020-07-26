package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ExpressInfo;
import com.redpigmall.dao.ExpressInfoMapper;
import com.redpigmall.service.RedPigExpressInfoService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigExpressInfoService extends BaseService<ExpressInfo>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ExpressInfo> objs) {
		if (objs != null && objs.size() > 0) {
			redPigExpressInfoMapper.batchDelete(objs);
		}
	}


	public ExpressInfo getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ExpressInfo> objs = redPigExpressInfoMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ExpressInfo> selectObjByProperty(Map<String, Object> maps) {
		return redPigExpressInfoMapper.selectObjByProperty(maps);
	}


	public List<ExpressInfo> queryPages(Map<String, Object> params) {
		return redPigExpressInfoMapper.queryPages(params);
	}


	public List<ExpressInfo> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigExpressInfoMapper.queryPageListWithNoRelations(param);
	}


	public List<ExpressInfo> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigExpressInfoMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ExpressInfoMapper redPigExpressInfoMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigExpressInfoMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ExpressInfo obj) {
		redPigExpressInfoMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ExpressInfo obj) {
		redPigExpressInfoMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigExpressInfoMapper.deleteById(id);
	}


	public ExpressInfo selectByPrimaryKey(Long id) {
		return redPigExpressInfoMapper.selectByPrimaryKey(id);
	}


	public List<ExpressInfo> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ExpressInfo> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigExpressInfoMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
