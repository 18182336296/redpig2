package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.domain.IntegralLog;
import com.redpigmall.dao.IntegralLogMapper;
import com.redpigmall.service.RedPigIntegralLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigIntegralLogService extends BaseService<IntegralLog> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<IntegralLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigIntegralLogMapper.batchDelete(objs);
		}
	}


	public IntegralLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<IntegralLog> objs = redPigIntegralLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<IntegralLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigIntegralLogMapper.selectObjByProperty(maps);
	}


	public List<IntegralLog> queryPages(Map<String, Object> params) {
		return redPigIntegralLogMapper.queryPages(params);
	}


	public List<IntegralLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigIntegralLogMapper.queryPageListWithNoRelations(param);
	}


	public List<IntegralLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigIntegralLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private IntegralLogMapper redPigIntegralLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigIntegralLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(IntegralLog obj) {
		if (obj != null && obj.getOperate_user() == null) {
			obj.setOperate_user(SecurityUserHolder.getCurrentUser());
		}
		redPigIntegralLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(IntegralLog obj) {
		if (obj != null && obj.getOperate_user() == null) {
			obj.setOperate_user(SecurityUserHolder.getCurrentUser());
		}
		redPigIntegralLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigIntegralLogMapper.deleteById(id);
	}


	public IntegralLog selectByPrimaryKey(Long id) {
		return redPigIntegralLogMapper.selectByPrimaryKey(id);
	}


	public List<IntegralLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<IntegralLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigIntegralLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
