package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.LimitSellingGoods;
import com.redpigmall.dao.LimitSellingGoodsMapper;
import com.redpigmall.service.RedPigLimitSellingGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigLimitSellingGoodsService extends BaseService<LimitSellingGoods>
		 {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<LimitSellingGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigLimitSellingGoodsMapper.batchDelete(objs);
		}
	}


	public LimitSellingGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<LimitSellingGoods> objs = redPigLimitSellingGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<LimitSellingGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigLimitSellingGoodsMapper.selectObjByProperty(maps);
	}


	public List<LimitSellingGoods> queryPages(Map<String, Object> params) {
		return redPigLimitSellingGoodsMapper.queryPages(params);
	}


	public List<LimitSellingGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigLimitSellingGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<LimitSellingGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigLimitSellingGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private LimitSellingGoodsMapper redPigLimitSellingGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigLimitSellingGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(LimitSellingGoods obj) {
		redPigLimitSellingGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(LimitSellingGoods obj) {
		redPigLimitSellingGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigLimitSellingGoodsMapper.deleteById(id);
	}


	public LimitSellingGoods selectByPrimaryKey(Long id) {
		return redPigLimitSellingGoodsMapper.selectByPrimaryKey(id);
	}


	public List<LimitSellingGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<LimitSellingGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigLimitSellingGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

}
