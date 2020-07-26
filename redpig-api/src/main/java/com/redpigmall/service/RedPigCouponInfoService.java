package com.redpigmall.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.redpigmall.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.redpigmall.api.query.support.IPageList;
import com.redpigmall.api.sec.SecurityUserHolder;
import com.redpigmall.api.tools.CommUtil;
import com.redpigmall.dao.UserMapper;
import com.redpigmall.domain.Coupon;
import com.redpigmall.domain.CouponInfo;
import com.redpigmall.dao.CouponInfoMapper;
import com.redpigmall.dao.CouponMapper;
import com.redpigmall.service.RedPigCouponInfoService;
import com.redpigmall.service.base.BaseService;

@Service
@Transactional(readOnly = true)
public class RedPigCouponInfoService extends BaseService<CouponInfo>  {


	@Transactional(readOnly = false)
	public void batchDelObjs(List<CouponInfo> objs) {
		if (objs != null && objs.size() > 0) {
			redPigCouponInfoMapper.batchDelete(objs);
		}
	}


	public CouponInfo getObjByProperty(String key, String operation_symbol, Object value) {
		Map<String, Object> maps = Maps.newHashMap();
		maps.put("operation_property", key);
		maps.put("operation_symbol", operation_symbol);
		maps.put("operation_value", value);
		List<CouponInfo> objs = redPigCouponInfoMapper.selectObjByProperty(maps);
		if (objs != null && objs.size() > 0) {
			return objs.get(0);
		}
		return null;
	}


	public List<CouponInfo> selectObjByProperty(Map<String, Object> maps) {
		return redPigCouponInfoMapper.selectObjByProperty(maps);
	}


	public List<CouponInfo> queryPages(Map<String, Object> params) {
		return redPigCouponInfoMapper.queryPages(params);
	}


	public List<CouponInfo> queryPageListWithNoRelations(Map<String, Object> param) {
		return redPigCouponInfoMapper.queryPageListWithNoRelations(param);
	}


	public List<CouponInfo> queryPagesWithNoRelations(Map<String, Object> params, Integer currentPage,
			Integer pageSize) {
		return redPigCouponInfoMapper.queryPagesWithNoRelations(params);
	}


	public IPageList queryPagesWithNoRelations(Map<String, Object> params) {
		return super.queryPagesWithNoRelations(params);
	}


	public IPageList list(Map<String, Object> params) {
		return super.listPages(params);
	}

	@Autowired
	private CouponInfoMapper redPigCouponInfoMapper;

	@Autowired
	private CouponMapper couponMapper;

	@Autowired
	private UserMapper userMapper;


	@Transactional(readOnly = false)
	public void batchDeleteByIds(List<Long> ids) {
		redPigCouponInfoMapper.batchDeleteByIds(ids);
	}


	@Transactional(readOnly = false)
	public void saveEntity(CouponInfo obj) {
		redPigCouponInfoMapper.saveEntity(obj);
	}


	@Transactional(readOnly = false)
	public void updateById(CouponInfo obj) {
		redPigCouponInfoMapper.updateById(obj);
	}


	@Transactional(readOnly = false)
	public void deleteById(Long id) {
		redPigCouponInfoMapper.deleteById(id);
	}


	public CouponInfo selectByPrimaryKey(Long id) {
		return redPigCouponInfoMapper.selectByPrimaryKey(id);
	}


	public List<CouponInfo> queryPageList(Map<String, Object> params, Integer begin, Integer max) {
		return super.queryPageList(params, begin, max);
	}


	public List<CouponInfo> queryPageList(Map<String, Object> params) {
		return this.queryPageList(params, null, null);
	}


	public int selectCount(Map<String, Object> params) {
		Integer c = redPigCouponInfoMapper.selectCount(params);
		if (c == null) {
			return 0;
		}
		return c;
	}

	@Transactional(readOnly = false)
	public void getCouponInfo(String coupon_id, String app_user_id) {
		Coupon coupon = (Coupon) this.couponMapper.selectByPrimaryKey(CommUtil.null2Long(coupon_id));

		User user = SecurityUserHolder.getCurrentUser();

		if (user == null) {
			user = (User) this.userMapper.selectByPrimaryKey(CommUtil.null2Long(app_user_id));
		}

		CouponInfo info = new CouponInfo();
		info.setAddTime(new Date());
		info.setCoupon(coupon);
		info.setCoupon_sn(UUID.randomUUID().toString());
		info.setUser(user);
		info.setCoupon_amount(coupon.getCoupon_amount());
		info.setCoupon_order_amount(coupon.getCoupon_order_amount());
		info.setEndDate(coupon.getCoupon_end_time());
		if (coupon.getCoupon_type() == 0) {
			info.setStore_id(CommUtil.null2Long(Integer.valueOf(0)));
			info.setStore_name("平台自营");
		} else if (coupon.getStore() != null) {
			info.setStore_id(coupon.getStore().getId());
			info.setStore_name(coupon.getStore().getStore_name());
		} else {
			info.setStore_id(CommUtil.null2Long(Integer.valueOf(-1)));
		}

		this.redPigCouponInfoMapper.saveEntity(info);

		coupon.setCoupon_surplus_amount(coupon.getCoupon_surplus_amount() - 1);
		if (coupon.getCoupon_surplus_amount() <= 0) {
			coupon.setStatus(-1);
		}

		this.couponMapper.updateById(coupon);
	}

}
