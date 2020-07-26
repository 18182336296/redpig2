package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.IntegralGoodsOrder;

public interface IntegralGoodsOrderMapper extends SupperMapper {

	void batchDelete(List<IntegralGoodsOrder> objs);

	List<IntegralGoodsOrder> selectObjByProperty(Map<String, Object> maps);

	IntegralGoodsOrder selectByPrimaryKey(Long id);

	List<IntegralGoodsOrder> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<IntegralGoodsOrder> queryByIds(List<Long> ids);

	List<IntegralGoodsOrder> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(IntegralGoodsOrder obj);

	void updateById(IntegralGoodsOrder obj);

	void deleteById(@Param(value="id")Long id);

	List<IntegralGoodsOrder> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<IntegralGoodsOrder> queryPagesWithNoRelations(Map<String,Object> params);

	List<IntegralGoodsOrder> queryPageListWithNoRelations(Map<String,Object> params);

}
