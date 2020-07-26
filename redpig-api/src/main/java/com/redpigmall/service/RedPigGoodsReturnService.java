package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsReturn;
import com.redpigmall.dao.GoodsReturnMapper;
import com.redpigmall.service.RedPigGoodsReturnService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsReturnService extends BaseService<GoodsReturn>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsReturn> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsReturnMapper.batchDelete(objs);
		}
	}


	public GoodsReturn getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsReturn> objs = redPigGoodsReturnMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsReturn> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsReturnMapper.selectObjByProperty(maps);
	}


	public List<GoodsReturn> queryPages(Map<String, Object> params) {
		return redPigGoodsReturnMapper.queryPages(params);
	}


	public List<GoodsReturn> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsReturnMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsReturn> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsReturnMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsReturnMapper redPigGoodsReturnMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsReturnMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsReturn obj) {
		redPigGoodsReturnMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsReturn obj) {
		redPigGoodsReturnMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsReturnMapper.deleteById(id);
	}


	public GoodsReturn selectByPrimaryKey(Long id) {
		return redPigGoodsReturnMapper.selectByPrimaryKey(id);
	}


	public List<GoodsReturn> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsReturn> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsReturnMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
