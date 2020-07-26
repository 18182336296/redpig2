package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import com.redpigmall.domain.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.AddressMapper;
import com.redpigmall.service.RedPigAddressService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigAddressService extends BaseService<Address>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Address> objs) {
		if (objs != null && objs.size() > 0) {
			redPigAddressMapper.batchDelete(objs);
		}
	}


	public Address getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Address> objs = redPigAddressMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Address> selectObjByProperty(Map<String, Object> maps) {
		return redPigAddressMapper.selectObjByProperty(maps);
	}


	public List<Address> queryPages(Map<String, Object> params) {
		return redPigAddressMapper.queryPages(params);
	}


	public List<Address> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigAddressMapper.queryPageListWithNoRelations(param);
	}


	public List<Address> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigAddressMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private AddressMapper redPigAddressMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigAddressMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Address obj) {
		redPigAddressMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Address obj) {
		System.out.println(obj.getArea().getId());
		redPigAddressMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigAddressMapper.deleteById(id);
	}


	public Address selectByPrimaryKey(Long id) {
		return redPigAddressMapper.selectByPrimaryKey(id);
	}


	public List<Address> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Address> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigAddressMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
