package com.redpigmall.service;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.dao.OrderFormMapper;
import com.redpigmall.domain.OrderForm;
import com.redpigmall.service.base.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class RedPigOrderFormService extends BaseService<OrderForm> {

	@Autowired
	public OrderFormMapper redPigOrderFormMapper;
	

	@Transactional(readOnly = false)
	public void batchDelObjs(List<OrderForm> objs) {
		if (objs != null && objs.size() > 0) {
			redPigOrderFormMapper.batchDelete(objs);
		}
	}


	public OrderForm getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<OrderForm> objs = redPigOrderFormMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<OrderForm> selectObjByProperty(Map<String, Object> maps) {
		return redPigOrderFormMapper.selectObjByProperty(maps);
	}


	public List<OrderForm> queryPages(Map<String, Object> params) {
		return redPigOrderFormMapper.queryPages(params);
	}


	public List<OrderForm> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigOrderFormMapper.queryPageListWithNoRelations(param);
	}


	public List<OrderForm> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigOrderFormMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}




	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigOrderFormMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(OrderForm obj) {
		redPigOrderFormMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(OrderForm obj) {
		redPigOrderFormMapper.updateById(obj);
	}

	@Transactional(readOnly =false)
	public void updateDistributionStatus(OrderForm obj){
		redPigOrderFormMapper.updateDistributionStatus(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigOrderFormMapper.deleteById(id);
	}


	public OrderForm selectByPrimaryKey(Long id) {
		return redPigOrderFormMapper.selectByPrimaryKey(id);
	}


	public List<OrderForm> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<OrderForm> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigOrderFormMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}


	public List<Map<String, Object>> getCountByArea(Map<String, Object> params) {
//		return redPigOrderFormMapper.getCountByArea(params);
		return redPigOrderFormMapper.countByArea(params);
	}


	public List<Map<String, Object>> querySum(Map<String, Object> params, int i, int j) {
		params.put("begin", i);
		params.put("end", j);
		return redPigOrderFormMapper.querySum(params);
	}


	public List<Map<String, Object>> countByArea(Map<String, Object> params) {
		return redPigOrderFormMapper.countByArea(params);
	}


	public List<Map<String, Object>> selectOrderList(Map<String, Object> params) {
		return redPigOrderFormMapper.selectOrderList(params);
	}
	/**通过用户id查询订单*/
	public List<OrderForm> queryOrderById(Long userId){
		return redPigOrderFormMapper.queryOrderById(userId);
	}
	/**通过用户id查询全部订单*/
	public List<OrderForm> queryOrderAllById(Long userId){
		return redPigOrderFormMapper.queryOrderAllById(userId);
	}

}
