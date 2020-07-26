package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ReturnGoodsLog;
import com.redpigmall.dao.ReturnGoodsLogMapper;
import com.redpigmall.service.RedPigReturnGoodsLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigReturnGoodsLogService extends BaseService<ReturnGoodsLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ReturnGoodsLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigReturnGoodsLogMapper.batchDelete(objs);
		}
	}


	public ReturnGoodsLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ReturnGoodsLog> objs = redPigReturnGoodsLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ReturnGoodsLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigReturnGoodsLogMapper.selectObjByProperty(maps);
	}


	public List<ReturnGoodsLog> queryPages(Map<String, Object> params) {
		return redPigReturnGoodsLogMapper.queryPages(params);
	}


	public List<ReturnGoodsLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigReturnGoodsLogMapper.queryPageListWithNoRelations(param);
	}


	public List<ReturnGoodsLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigReturnGoodsLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ReturnGoodsLogMapper redPigReturnGoodsLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigReturnGoodsLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ReturnGoodsLog obj) {
		redPigReturnGoodsLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ReturnGoodsLog obj) {
		redPigReturnGoodsLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigReturnGoodsLogMapper.deleteById(id);
	}


	public ReturnGoodsLog selectByPrimaryKey(Long id) {
		return redPigReturnGoodsLogMapper.selectByPrimaryKey(id);
	}


	public List<ReturnGoodsLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ReturnGoodsLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigReturnGoodsLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
