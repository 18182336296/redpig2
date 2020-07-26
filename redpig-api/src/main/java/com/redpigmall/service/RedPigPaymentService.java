package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Payment;
import com.redpigmall.dao.PaymentMapper;
import com.redpigmall.service.RedPigPaymentService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigPaymentService extends BaseService<Payment>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Payment> objs) {
		if (objs != null && objs.size() > 0) {
			redPigPaymentMapper.batchDelete(objs);
		}
	}


	public Payment getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Payment> objs = redPigPaymentMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Payment> selectObjByProperty(Map<String, Object> maps) {
		return redPigPaymentMapper.selectObjByProperty(maps);
	}


	public List<Payment> queryPages(Map<String, Object> params) {
		return redPigPaymentMapper.queryPages(params);
	}


	public List<Payment> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigPaymentMapper.queryPageListWithNoRelations(param);
	}


	public List<Payment> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigPaymentMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private PaymentMapper redPigPaymentMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigPaymentMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Payment obj) {
		redPigPaymentMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Payment obj) {
		redPigPaymentMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigPaymentMapper.deleteById(id);
	}


	public Payment selectByPrimaryKey(Long id) {
		return redPigPaymentMapper.selectByPrimaryKey(id);
	}


	public List<Payment> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Payment> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigPaymentMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	@Transactional(readOnly = false)
	public void update(Payment obj) {
		redPigPaymentMapper.update(obj);
	}


	@Transactional(readOnly = false)
	public void save(Payment obj) {
		redPigPaymentMapper.save(obj);
	}
}
