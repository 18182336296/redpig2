package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsLog;
import com.redpigmall.dao.GoodsLogMapper;
import com.redpigmall.service.RedPigGoodsLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsLogService extends BaseService<GoodsLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsLogMapper.batchDelete(objs);
		}
	}


	public GoodsLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsLog> objs = redPigGoodsLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsLogMapper.selectObjByProperty(maps);
	}


	public List<GoodsLog> queryPages(Map<String, Object> params) {
		return redPigGoodsLogMapper.queryPages(params);
	}


	public List<GoodsLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsLogMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigGoodsLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsLogMapper redPigGoodsLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsLog obj) {
		redPigGoodsLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsLog obj) {
		redPigGoodsLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsLogMapper.deleteById(id);
	}


	public GoodsLog selectByPrimaryKey(Long id) {
		return redPigGoodsLogMapper.selectByPrimaryKey(id);
	}


	public List<GoodsLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<Map<String, Object>> queryByGroup(Map<String, Object> params, int i, int j) {
		params.put("currentPage", i);
		params.put("pageSize", j);
		return redPigGoodsLogMapper.queryByGroup(params);
	}
}
