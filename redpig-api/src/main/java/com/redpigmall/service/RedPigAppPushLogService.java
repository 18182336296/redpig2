package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.AppPushLog;
import com.redpigmall.dao.AppPushLogMapper;
import com.redpigmall.service.RedPigAppPushLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAppPushLogService extends BaseService<AppPushLog> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<AppPushLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAppPushLogMapper.batchDelete(objs);
		}
	}


	public AppPushLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<AppPushLog> objs = redPigAppPushLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<AppPushLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigAppPushLogMapper.selectObjByProperty(maps);
	}


	public List<AppPushLog> queryPages(Map<String, Object> params) {
		return redPigAppPushLogMapper.queryPages(params);
	}


	public List<AppPushLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAppPushLogMapper.queryPageListWithNoRelations(param);
	}


	public List<AppPushLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigAppPushLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private AppPushLogMapper redPigAppPushLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAppPushLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(AppPushLog obj) {
		redPigAppPushLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(AppPushLog obj) {
		redPigAppPushLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAppPushLogMapper.deleteById(id);
	}


	public AppPushLog selectByPrimaryKey(Long id) {
		return redPigAppPushLogMapper.selectByPrimaryKey(id);
	}


	public List<AppPushLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<AppPushLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAppPushLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}

}
