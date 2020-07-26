package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CouponInfo;

public interface CouponInfoMapper extends SupperMapper {

	void batchDelete(List<CouponInfo> objs);

	List<CouponInfo> selectObjByProperty(Map<String, Object> maps);

	CouponInfo selectByPrimaryKey(Long id);

	List<CouponInfo> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CouponInfo> queryByIds(List<Long> ids);

	List<CouponInfo> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CouponInfo obj);

	void updateById(CouponInfo obj);

	void deleteById(@Param(value="id")Long id);
	List<CouponInfo> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CouponInfo> queryPagesWithNoRelations(Map<String,Object> params);

	List<CouponInfo> queryPageListWithNoRelations(Map<String,Object> params);

}
