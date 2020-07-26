package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.WaterMark;
import com.redpigmall.dao.WaterMarkMapper;
import com.redpigmall.service.RedPigWaterMarkService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigWaterMarkService extends BaseService<WaterMark>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<WaterMark> objs) {
		if (objs != null && objs.size() > 0) {
			redPigWaterMarkMapper.batchDelete(objs);
		}
	}


	public WaterMark getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<WaterMark> objs = redPigWaterMarkMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<WaterMark> selectObjByProperty(Map<String, Object> maps) {
		return redPigWaterMarkMapper.selectObjByProperty(maps);
	}


	public List<WaterMark> queryPages(Map<String, Object> params) {
		return redPigWaterMarkMapper.queryPages(params);
	}


	public List<WaterMark> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigWaterMarkMapper.queryPageListWithNoRelations(param);
	}


	public List<WaterMark> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigWaterMarkMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private WaterMarkMapper redPigWaterMarkMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigWaterMarkMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(WaterMark obj) {
		redPigWaterMarkMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(WaterMark obj) {
		redPigWaterMarkMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigWaterMarkMapper.deleteById(id);
	}


	public WaterMark selectByPrimaryKey(Long id) {
		return redPigWaterMarkMapper.selectByPrimaryKey(id);
	}


	public List<WaterMark> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<WaterMark> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigWaterMarkMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
