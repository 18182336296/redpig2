package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.DeliveryAddress;
import com.redpigmall.dao.DeliveryAddressMapper;
import com.redpigmall.service.RedPigDeliveryAddressService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigDeliveryAddressService extends BaseService<DeliveryAddress>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<DeliveryAddress> objs) {
		if (objs != null && objs.size() > 0) {
			redPigDeliveryAddressMapper.batchDelete(objs);
		}
	}


	public DeliveryAddress getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<DeliveryAddress> objs = redPigDeliveryAddressMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<DeliveryAddress> selectObjByProperty(Map<String, Object> maps) {
		return redPigDeliveryAddressMapper.selectObjByProperty(maps);
	}


	public List<DeliveryAddress> queryPages(Map<String, Object> params) {
		return redPigDeliveryAddressMapper.queryPages(params);
	}


	public List<DeliveryAddress> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigDeliveryAddressMapper.queryPageListWithNoRelations(param);
	}


	public List<DeliveryAddress> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigDeliveryAddressMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}

	@Autowired
	private DeliveryAddressMapper redPigDeliveryAddressMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigDeliveryAddressMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(DeliveryAddress obj) {
		redPigDeliveryAddressMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(DeliveryAddress obj) {
		redPigDeliveryAddressMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigDeliveryAddressMapper.deleteById(id);
	}


	public DeliveryAddress selectByPrimaryKey(Long id) {
		return redPigDeliveryAddressMapper.selectByPrimaryKey(id);
	}


	public List<DeliveryAddress> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<DeliveryAddress> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigDeliveryAddressMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}
}
