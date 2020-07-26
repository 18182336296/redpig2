package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.ShipAddress;
import com.redpigmall.dao.ShipAddressMapper;
import com.redpigmall.service.RedPigShipAddressService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigShipAddressService extends BaseService<ShipAddress>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<ShipAddress> objs) {
		if (objs != null && objs.size() > 0) {
			redPigShipAddressMapper.batchDelete(objs);
		}
	}


	public ShipAddress getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<ShipAddress> objs = redPigShipAddressMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<ShipAddress> selectObjByProperty(Map<String, Object> maps) {
		return redPigShipAddressMapper.selectObjByProperty(maps);
	}


	public List<ShipAddress> queryPages(Map<String, Object> params) {
		return redPigShipAddressMapper.queryPages(params);
	}


	public List<ShipAddress> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigShipAddressMapper.queryPageListWithNoRelations(param);
	}


	public List<ShipAddress> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigShipAddressMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private ShipAddressMapper redPigShipAddressMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigShipAddressMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(ShipAddress obj) {
		redPigShipAddressMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(ShipAddress obj) {
		redPigShipAddressMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigShipAddressMapper.deleteById(id);
	}


	public ShipAddress selectByPrimaryKey(Long id) {
		return redPigShipAddressMapper.selectByPrimaryKey(id);
	}


	public List<ShipAddress> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<ShipAddress> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigShipAddressMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
