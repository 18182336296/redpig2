package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsTag;
import com.redpigmall.dao.GoodsTagMapper;
import com.redpigmall.service.RedPigGoodsTagService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsTagService extends BaseService<GoodsTag>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsTag> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsTagMapper.batchDelete(objs);
		}
	}


	public GoodsTag getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsTag> objs = redPigGoodsTagMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsTag> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsTagMapper.selectObjByProperty(maps);
	}


	public List<GoodsTag> queryPages(Map<String, Object> params) {
		return redPigGoodsTagMapper.queryPages(params);
	}


	public List<GoodsTag> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsTagMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsTag> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigGoodsTagMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsTagMapper redPigGoodsTagMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsTagMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsTag obj) {
		redPigGoodsTagMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsTag obj) {
		redPigGoodsTagMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsTagMapper.deleteById(id);
	}


	public GoodsTag selectByPrimaryKey(Long id) {
		return redPigGoodsTagMapper.selectByPrimaryKey(id);
	}


	public List<GoodsTag> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsTag> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsTagMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
