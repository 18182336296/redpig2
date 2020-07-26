package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsFormat;
import com.redpigmall.dao.GoodsFormatMapper;
import com.redpigmall.service.RedPigGoodsFormatService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsFormatService extends BaseService<GoodsFormat>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsFormat> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsFormatMapper.batchDelete(objs);
		}
	}


	public GoodsFormat getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsFormat> objs = redPigGoodsFormatMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsFormat> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsFormatMapper.selectObjByProperty(maps);
	}


	public List<GoodsFormat> queryPages(Map<String, Object> params) {
		return redPigGoodsFormatMapper.queryPages(params);
	}


	public List<GoodsFormat> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsFormatMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsFormat> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsFormatMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsFormatMapper redPigGoodsFormatMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsFormatMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsFormat obj) {
		redPigGoodsFormatMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsFormat obj) {
		redPigGoodsFormatMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsFormatMapper.deleteById(id);
	}


	public GoodsFormat selectByPrimaryKey(Long id) {
		return redPigGoodsFormatMapper.selectByPrimaryKey(id);
	}


	public List<GoodsFormat> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsFormat> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsFormatMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
