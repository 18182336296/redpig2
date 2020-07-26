package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.FreeApplyLog;
import com.redpigmall.dao.FreeApplyLogMapper;
import com.redpigmall.service.RedPigFreeApplyLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigFreeApplyLogService extends BaseService<FreeApplyLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<FreeApplyLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigFreeApplyLogMapper.batchDelete(objs);
		}
	}


	public FreeApplyLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<FreeApplyLog> objs = redPigFreeApplyLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<FreeApplyLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigFreeApplyLogMapper.selectObjByProperty(maps);
	}


	public List<FreeApplyLog> queryPages(Map<String, Object> params) {
		return redPigFreeApplyLogMapper.queryPages(params);
	}


	public List<FreeApplyLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigFreeApplyLogMapper.queryPageListWithNoRelations(param);
	}


	public List<FreeApplyLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigFreeApplyLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private FreeApplyLogMapper redPigFreeApplyLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigFreeApplyLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(FreeApplyLog obj) {
		redPigFreeApplyLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(FreeApplyLog obj) {
		redPigFreeApplyLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigFreeApplyLogMapper.deleteById(id);
	}


	public FreeApplyLog selectByPrimaryKey(Long id) {
		return redPigFreeApplyLogMapper.selectByPrimaryKey(id);
	}


	public List<FreeApplyLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<FreeApplyLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigFreeApplyLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
