package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.tools.RedPigCommonUtil;
import com.redpigmall.domain.GoodsCase;
import com.redpigmall.dao.GoodsCaseMapper;
import com.redpigmall.service.RedPigGoodsCaseService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigGoodsCaseService extends BaseService<GoodsCase> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<GoodsCase> objs) {
		if (objs != null && objs.size() > 0) {
			redPigGoodsCaseMapper.batchDelete(objs);
		}
	}


	public GoodsCase getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<GoodsCase> objs = redPigGoodsCaseMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<GoodsCase> selectObjByProperty(Map<String, Object> maps) {
		return redPigGoodsCaseMapper.selectObjByProperty(maps);
	}


	public List<GoodsCase> queryPages(Map<String, Object> params) {
		return redPigGoodsCaseMapper.queryPages(params);
	}


	public List<GoodsCase> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigGoodsCaseMapper.queryPageListWithNoRelations(param);
	}


	public List<GoodsCase> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigGoodsCaseMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private GoodsCaseMapper redPigGoodsCaseMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		if (RedPigCommonUtil.isNotNull(ids)) {
			redPigGoodsCaseMapper.batchDeleteByIds(ids);
		}
	}


	@Transactional(readOnly = false)
	public void saveEntity(GoodsCase obj) {
		redPigGoodsCaseMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(GoodsCase obj) {
		redPigGoodsCaseMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigGoodsCaseMapper.deleteById(id);
	}


	public GoodsCase selectByPrimaryKey(Long id) {
		return redPigGoodsCaseMapper.selectByPrimaryKey(id);
	}


	public List<GoodsCase> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<GoodsCase> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigGoodsCaseMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}
}
