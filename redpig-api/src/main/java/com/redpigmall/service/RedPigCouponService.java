package com.redpigmall.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.domain.Coupon;
import com.redpigmall.dao.CouponMapper;
import com.redpigmall.service.RedPigCouponService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCouponService extends BaseService<Coupon>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<Coupon> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCouponMapper.batchDelete(objs);
		}
	}


	public Coupon getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<Coupon> objs = redPigCouponMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<Coupon> selectObjByProperty(Map<String, Object> maps) {
		return redPigCouponMapper.selectObjByProperty(maps);
	}


	public List<Coupon> queryPages(Map<String, Object> params) {
		return redPigCouponMapper.queryPages(params);
	}


	public List<Coupon> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCouponMapper.queryPageListWithNoRelations(param);
	}


	public List<Coupon> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage, Integer pageSize) {
		return redPigCouponMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CouponMapper redPigCouponMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCouponMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(Coupon obj) {
		redPigCouponMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(Coupon obj) {
		redPigCouponMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCouponMapper.deleteById(id);
	}


	public Coupon selectByPrimaryKey(Long id) {
		return redPigCouponMapper.selectByPrimaryKey(id);
	}


	public List<Coupon> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<Coupon> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCouponMapper.selectCount(params);
		if (c == null) {
			return 0;
		}

		return c;

	}
}
