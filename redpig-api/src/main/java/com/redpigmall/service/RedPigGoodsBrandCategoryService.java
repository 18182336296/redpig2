package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.GoodsBrandCategory;
import com.redpigmall.dao.GoodsBrandCategoryMapper;
import com.redpigmall.service.RedPigGoodsBrandCategoryService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsBrandCategoryService extends BaseService<GoodsBrandCategory> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsBrandCategory> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsBrandCategoryMapper.batchDelete(objs);
		}
	}


	public GoodsBrandCategory getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsBrandCategory> objs = redPigGoodsBrandCategoryMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsBrandCategory> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsBrandCategoryMapper.selectObjByProperty(maps);
	}


	public List<GoodsBrandCategory> queryPages(Map<String, Object> params) {
		return redPigGoodsBrandCategoryMapper.queryPages(params);
	}


	public List<GoodsBrandCategory> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsBrandCategoryMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsBrandCategory> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsBrandCategoryMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private GoodsBrandCategoryMapper redPigGoodsBrandCategoryMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigGoodsBrandCategoryMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsBrandCategory obj) {
		redPigGoodsBrandCategoryMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsBrandCategory obj) {
		redPigGoodsBrandCategoryMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsBrandCategoryMapper.deleteById(id);
	}


	public GoodsBrandCategory selectByPrimaryKey(Long id) {
		return redPigGoodsBrandCategoryMapper.selectByPrimaryKey(id);
	}


	public List<GoodsBrandCategory> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsBrandCategory> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsBrandCategoryMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void save(GoodsBrandCategory cat) {
		redPigGoodsBrandCategoryMapper.save(cat);
	}
}
