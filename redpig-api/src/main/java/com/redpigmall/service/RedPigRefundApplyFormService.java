package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.RefundApplyForm;
import com.redpigmall.dao.RefundApplyFormMapper;
import com.redpigmall.service.RedPigRefundApplyFormService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigRefundApplyFormService extends BaseService<RefundApplyForm>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<RefundApplyForm> objs) {
		if (objs != null && objs.size() > 0) {
			redPigRefundApplyFormMapper.batchDelete(objs);
		}
	}


	public RefundApplyForm getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<RefundApplyForm> objs = redPigRefundApplyFormMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<RefundApplyForm> selectObjByProperty(Map<String, Object> maps) {
		return redPigRefundApplyFormMapper.selectObjByProperty(maps);
	}


	public List<RefundApplyForm> queryPages(Map<String, Object> params) {
		return redPigRefundApplyFormMapper.queryPages(params);
	}


	public List<RefundApplyForm> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigRefundApplyFormMapper.queryPageListWithNoRelations(param);
	}


	public List<RefundApplyForm> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigRefundApplyFormMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private RefundApplyFormMapper redPigRefundApplyFormMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigRefundApplyFormMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(RefundApplyForm obj) {
		redPigRefundApplyFormMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(RefundApplyForm obj) {
		redPigRefundApplyFormMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigRefundApplyFormMapper.deleteById(id);
	}


	public RefundApplyForm selectByPrimaryKey(Long id) {
		return redPigRefundApplyFormMapper.selectByPrimaryKey(id);
	}


	public List<RefundApplyForm> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<RefundApplyForm> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigRefundApplyFormMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
