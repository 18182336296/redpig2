package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ActivityGoods;
import com.redpigmall.dao.ActivityGoodsMapper;
import com.redpigmall.service.RedPigActivityGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigActivityGoodsService extends BaseService<ActivityGoods>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ActivityGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigActivityGoodsMapper.batchDelete(objs);
		}
	}


	public ActivityGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ActivityGoods> objs = redPigActivityGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ActivityGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigActivityGoodsMapper.selectObjByProperty(maps);
	}


	public List<ActivityGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigActivityGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<ActivityGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigActivityGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private ActivityGoodsMapper redPigActivityGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigActivityGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ActivityGoods obj) {
		redPigActivityGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ActivityGoods obj) {
		redPigActivityGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigActivityGoodsMapper.deleteById(id);
	}


	public ActivityGoods selectByPrimaryKey(Long id) {
		return redPigActivityGoodsMapper.selectByPrimaryKey(id);
	}


	public List<ActivityGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ActivityGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigActivityGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<ActivityGoods> queryPages(Map<String, Object> params) {
		return redPigActivityGoodsMapper.queryPages(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}
}
