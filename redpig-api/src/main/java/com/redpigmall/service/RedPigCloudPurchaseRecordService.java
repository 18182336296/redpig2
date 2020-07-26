package com.redpigmall.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CloudPurchaseRecord;
import com.redpigmall.dao.CloudPurchaseRecordMapper;
import com.redpigmall.service.RedPigCloudPurchaseRecordService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCloudPurchaseRecordService extends BaseService<CloudPurchaseRecord>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CloudPurchaseRecord> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCloudPurchaseRecordMapper.batchDelete(objs);
		}
	}


	public CloudPurchaseRecord getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CloudPurchaseRecord> objs = redPigCloudPurchaseRecordMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CloudPurchaseRecord> selectObjByProperty(Map<String, Object> maps) {
		return redPigCloudPurchaseRecordMapper.selectObjByProperty(maps);
	}


	public List<CloudPurchaseRecord> queryPages(Map<String, Object> params) {
		return redPigCloudPurchaseRecordMapper.queryPages(params);
	}


	public List<CloudPurchaseRecord> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCloudPurchaseRecordMapper.queryPageListWithNoRelations(param);
	}


	public List<CloudPurchaseRecord> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCloudPurchaseRecordMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CloudPurchaseRecordMapper redPigCloudPurchaseRecordMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCloudPurchaseRecordMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CloudPurchaseRecord obj) {
		redPigCloudPurchaseRecordMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CloudPurchaseRecord obj) {
		redPigCloudPurchaseRecordMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCloudPurchaseRecordMapper.deleteById(id);
	}


	public CloudPurchaseRecord selectByPrimaryKey(Long id) {
		return redPigCloudPurchaseRecordMapper.selectByPrimaryKey(id);
	}


	public List<CloudPurchaseRecord> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CloudPurchaseRecord> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCloudPurchaseRecordMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}

	public List<CloudPurchaseRecord> getLatest50(Date date) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("add_Time_less_than_equal", new Date());
		List<CloudPurchaseRecord> order_list = queryPageList(params, 0, 50);
		return order_list;
	}

}
