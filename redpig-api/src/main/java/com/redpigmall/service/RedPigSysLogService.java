package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.SysLog;
import com.redpigmall.dao.SysLogMapper;
import com.redpigmall.service.RedPigSysLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigSysLogService extends BaseService<SysLog> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<SysLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigSysLogMapper.batchDelete(objs);
		}
	}


	public SysLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<SysLog> objs = redPigSysLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<SysLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigSysLogMapper.selectObjByProperty(maps);
	}


	public List<SysLog> queryPages(Map<String, Object> params) {
		return redPigSysLogMapper.queryPages(params);
	}


	public List<SysLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigSysLogMapper.queryPageListWithNoRelations(param);
	}


	public List<SysLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigSysLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private SysLogMapper redPigSysLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigSysLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(SysLog obj) {
		redPigSysLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(SysLog obj) {
		redPigSysLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigSysLogMapper.deleteById(id);
	}


	public SysLog selectByPrimaryKey(Long id) {
		return redPigSysLogMapper.selectByPrimaryKey(id);
	}


	public List<SysLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<SysLog> queryPageList(Map<String, Object> params) {
		if (params == null)
			params = Maps.newHashMap();
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigSysLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void batchDelete(List<Long> list) {
		if (list != null && list.size() > 0) {
			redPigSysLogMapper.batchDeleteByIds(list);
		}
	}

	@Transactional(readOnly = false)

	public void batchDeleteObj(List<SysLog> list) {
		List<Long> ids = Lists.newArrayList();
		for (SysLog sysLog : list) {
			ids.add(sysLog.getId());
		}
		batchDelete(ids);
	}


	@Transactional(readOnly = false)
	public void delete(Long id) {
		List<Long> ids = Lists.newArrayList(id);
		batchDelete(ids);
	}

}
