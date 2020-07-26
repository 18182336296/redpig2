package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CloudPurchaseEveryColor;
import com.redpigmall.dao.CloudPurchaseEveryColorMapper;
import com.redpigmall.service.RedPigCloudPurchaseEveryColorService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCloudPurchaseEveryColorService extends BaseService<CloudPurchaseEveryColor>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CloudPurchaseEveryColor> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCloudPurchaseEveryColorMapper.batchDelete(objs);
		}
	}


	public CloudPurchaseEveryColor getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CloudPurchaseEveryColor> objs = redPigCloudPurchaseEveryColorMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CloudPurchaseEveryColor> selectObjByProperty(Map<String, Object> maps) {
		return redPigCloudPurchaseEveryColorMapper.selectObjByProperty(maps);
	}


	public List<CloudPurchaseEveryColor> queryPages(Map<String, Object> params) {
		return redPigCloudPurchaseEveryColorMapper.queryPages(params);
	}


	public List<CloudPurchaseEveryColor> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCloudPurchaseEveryColorMapper.queryPageListWithNoRelations(param);
	}


	public List<CloudPurchaseEveryColor> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCloudPurchaseEveryColorMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CloudPurchaseEveryColorMapper redPigCloudPurchaseEveryColorMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCloudPurchaseEveryColorMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CloudPurchaseEveryColor obj) {
		redPigCloudPurchaseEveryColorMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CloudPurchaseEveryColor obj) {
		redPigCloudPurchaseEveryColorMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCloudPurchaseEveryColorMapper.deleteById(id);
	}


	public CloudPurchaseEveryColor selectByPrimaryKey(Long id) {
		return redPigCloudPurchaseEveryColorMapper.selectByPrimaryKey(id);
	}


	public List<CloudPurchaseEveryColor> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CloudPurchaseEveryColor> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCloudPurchaseEveryColorMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
