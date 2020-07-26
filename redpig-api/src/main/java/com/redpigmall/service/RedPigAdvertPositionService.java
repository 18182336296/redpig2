package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.AdvertPosition;
import com.redpigmall.dao.AdvertPositionMapper;
import com.redpigmall.service.RedPigAdvertPositionService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAdvertPositionService extends BaseService<AdvertPosition> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<AdvertPosition> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAdvertPositionMapper.batchDelete(objs);
		}
	}


	public AdvertPosition getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<AdvertPosition> objs = redPigAdvertPositionMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<AdvertPosition> selectObjByProperty(Map<String, Object> maps) {
		return redPigAdvertPositionMapper.selectObjByProperty(maps);
	}


	public List<AdvertPosition> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAdvertPositionMapper.queryPageListWithNoRelations(param);
	}


	public List<AdvertPosition> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigAdvertPositionMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private AdvertPositionMapper redPigAdvertPositionMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAdvertPositionMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(AdvertPosition obj) {
		redPigAdvertPositionMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(AdvertPosition obj) {
		redPigAdvertPositionMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAdvertPositionMapper.deleteById(id);
	}


	public AdvertPosition selectByPrimaryKey(Long id) {
		return redPigAdvertPositionMapper.selectByPrimaryKey(id);
	}


	public List<AdvertPosition> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<AdvertPosition> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAdvertPositionMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<AdvertPosition> queryPages(Map<String, Object> params) {
		return redPigAdvertPositionMapper.queryPages(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}
}
