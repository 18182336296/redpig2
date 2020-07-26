package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CloudPurchaseGoods;
import com.redpigmall.dao.CloudPurchaseGoodsMapper;
import com.redpigmall.service.RedPigCloudPurchaseGoodsService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCloudPurchaseGoodsService extends BaseService<CloudPurchaseGoods>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CloudPurchaseGoods> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCloudPurchaseGoodsMapper.batchDelete(objs);
		}
	}


	public CloudPurchaseGoods getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CloudPurchaseGoods> objs = redPigCloudPurchaseGoodsMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CloudPurchaseGoods> selectObjByProperty(Map<String, Object> maps) {
		return redPigCloudPurchaseGoodsMapper.selectObjByProperty(maps);
	}


	public List<CloudPurchaseGoods> queryPages(Map<String, Object> params) {
		return redPigCloudPurchaseGoodsMapper.queryPages(params);
	}


	public List<CloudPurchaseGoods> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCloudPurchaseGoodsMapper.queryPageListWithNoRelations(param);
	}


	public List<CloudPurchaseGoods> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCloudPurchaseGoodsMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CloudPurchaseGoodsMapper redPigCloudPurchaseGoodsMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCloudPurchaseGoodsMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CloudPurchaseGoods obj) {
		redPigCloudPurchaseGoodsMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CloudPurchaseGoods obj) {
		redPigCloudPurchaseGoodsMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCloudPurchaseGoodsMapper.deleteById(id);
	}


	public CloudPurchaseGoods selectByPrimaryKey(Long id) {
		return redPigCloudPurchaseGoodsMapper.selectByPrimaryKey(id);
	}


	public List<CloudPurchaseGoods> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CloudPurchaseGoods> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCloudPurchaseGoodsMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
