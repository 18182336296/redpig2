package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Coupon;

public interface CouponMapper extends SupperMapper {

	void batchDelete(List<Coupon> objs);

	List<Coupon> selectObjByProperty(Map<String, Object> maps);

	Coupon selectByPrimaryKey(Long id);

	List<Coupon> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Coupon> queryByIds(List<Long> ids);

	List<Coupon> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Coupon obj);

	void updateById(Coupon obj);

	void deleteById(@Param(value="id")Long id);
	List<Coupon> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Coupon> queryPagesWithNoRelations(Map<String,Object> params);

	List<Coupon> queryPageListWithNoRelations(Map<String,Object> params);

}
