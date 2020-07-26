package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoldLog;
import com.redpigmall.dao.GoldLogMapper;
import com.redpigmall.service.RedPigGoldLogService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoldLogService extends BaseService<GoldLog>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoldLog> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoldLogMapper.batchDelete(objs);
		}
	}


	public GoldLog getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoldLog> objs = redPigGoldLogMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoldLog> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoldLogMapper.selectObjByProperty(maps);
	}


	public List<GoldLog> queryPages(Map<String, Object> params) {
		return redPigGoldLogMapper.queryPages(params);
	}


	public List<GoldLog> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoldLogMapper.queryPageListWithNoRelations(param);
	}


	public List<GoldLog> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigGoldLogMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoldLogMapper redPigGoldLogMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoldLogMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoldLog obj) {
		redPigGoldLogMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoldLog obj) {
		redPigGoldLogMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoldLogMapper.deleteById(id);
	}


	public GoldLog selectByPrimaryKey(Long id) {
		return redPigGoldLogMapper.selectByPrimaryKey(id);
	}


	public List<GoldLog> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoldLog> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoldLogMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
