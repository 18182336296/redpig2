package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CloudPurchaseClass;
import com.redpigmall.dao.CloudPurchaseClassMapper;
import com.redpigmall.service.RedPigCloudPurchaseClassService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCloudPurchaseClassService extends BaseService<CloudPurchaseClass> {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CloudPurchaseClass> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCloudPurchaseClassMapper.batchDelete(objs);
		}
	}


	public CloudPurchaseClass getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CloudPurchaseClass> objs = redPigCloudPurchaseClassMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CloudPurchaseClass> selectObjByProperty(Map<String, Object> maps) {
		return redPigCloudPurchaseClassMapper.selectObjByProperty(maps);
	}


	public List<CloudPurchaseClass> queryPages(Map<String, Object> params) {
		return redPigCloudPurchaseClassMapper.queryPages(params);
	}


	public List<CloudPurchaseClass> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCloudPurchaseClassMapper.queryPageListWithNoRelations(param);
	}


	public List<CloudPurchaseClass> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCloudPurchaseClassMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CloudPurchaseClassMapper redPigCloudPurchaseClassMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCloudPurchaseClassMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CloudPurchaseClass obj) {
		redPigCloudPurchaseClassMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CloudPurchaseClass obj) {
		redPigCloudPurchaseClassMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCloudPurchaseClassMapper.deleteById(id);
	}


	public CloudPurchaseClass selectByPrimaryKey(Long id) {
		return redPigCloudPurchaseClassMapper.selectByPrimaryKey(id);
	}


	public List<CloudPurchaseClass> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CloudPurchaseClass> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCloudPurchaseClassMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
