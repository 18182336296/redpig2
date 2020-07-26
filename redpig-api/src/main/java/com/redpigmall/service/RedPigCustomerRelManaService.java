package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.CustomerRelMana;
import com.redpigmall.dao.CustomerRelManaMapper;
import com.redpigmall.service.RedPigCustomerRelManaService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCustomerRelManaService extends BaseService<CustomerRelMana>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CustomerRelMana> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCustomerRelManaMapper.batchDelete(objs);
		}
	}


	public CustomerRelMana getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CustomerRelMana> objs = redPigCustomerRelManaMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CustomerRelMana> selectObjByProperty(Map<String, Object> maps) {
		return redPigCustomerRelManaMapper.selectObjByProperty(maps);
	}


	public List<CustomerRelMana> queryPages(Map<String, Object> params) {
		return redPigCustomerRelManaMapper.queryPages(params);
	}


	public List<CustomerRelMana> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCustomerRelManaMapper.queryPageListWithNoRelations(param);
	}


	public List<CustomerRelMana> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCustomerRelManaMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CustomerRelManaMapper redPigCustomerRelManaMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCustomerRelManaMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CustomerRelMana obj) {
		redPigCustomerRelManaMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CustomerRelMana obj) {
		redPigCustomerRelManaMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCustomerRelManaMapper.deleteById(id);
	}


	public CustomerRelMana selectByPrimaryKey(Long id) {
		return redPigCustomerRelManaMapper.selectByPrimaryKey(id);
	}


	public List<CustomerRelMana> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CustomerRelMana> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCustomerRelManaMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
