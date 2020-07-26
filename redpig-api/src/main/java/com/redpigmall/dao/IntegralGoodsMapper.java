package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.IntegralGoods;

public interface IntegralGoodsMapper extends SupperMapper {

	void batchDelete(List<IntegralGoods> objs);

	List<IntegralGoods> selectObjByProperty(Map<String, Object> maps);

	IntegralGoods selectByPrimaryKey(Long id);

	List<IntegralGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<IntegralGoods> queryByIds(List<Long> ids);

	List<IntegralGoods> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(IntegralGoods obj);

	void updateById(IntegralGoods obj);

	void deleteById(@Param(value="id")Long id);

	List<IntegralGoods> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<IntegralGoods> queryPagesWithNoRelations(Map<String,Object> params);

	List<IntegralGoods> queryPageListWithNoRelations(Map<String,Object> params);

}
