package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CloudPurchaseCode;
import com.redpigmall.dao.CloudPurchaseCodeMapper;
import com.redpigmall.service.RedPigCloudPurchaseCodeService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCloudPurchaseCodeService extends BaseService<CloudPurchaseCode>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CloudPurchaseCode> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCloudPurchaseCodeMapper.batchDelete(objs);
		}
	}


	public CloudPurchaseCode getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CloudPurchaseCode> objs = redPigCloudPurchaseCodeMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CloudPurchaseCode> selectObjByProperty(Map<String, Object> maps) {
		return redPigCloudPurchaseCodeMapper.selectObjByProperty(maps);
	}


	public List<CloudPurchaseCode> queryPages(Map<String, Object> params) {
		return redPigCloudPurchaseCodeMapper.queryPages(params);
	}


	public List<CloudPurchaseCode> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCloudPurchaseCodeMapper.queryPageListWithNoRelations(param);
	}


	public List<CloudPurchaseCode> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCloudPurchaseCodeMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CloudPurchaseCodeMapper redPigCloudPurchaseCodeMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCloudPurchaseCodeMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CloudPurchaseCode obj) {
		redPigCloudPurchaseCodeMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CloudPurchaseCode obj) {
		redPigCloudPurchaseCodeMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCloudPurchaseCodeMapper.deleteById(id);
	}


	public CloudPurchaseCode selectByPrimaryKey(Long id) {
		return redPigCloudPurchaseCodeMapper.selectByPrimaryKey(id);
	}


	public List<CloudPurchaseCode> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CloudPurchaseCode> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCloudPurchaseCodeMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
