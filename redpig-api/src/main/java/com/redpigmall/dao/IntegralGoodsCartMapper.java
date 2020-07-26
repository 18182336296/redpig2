package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.IntegralGoodsCart;

public interface IntegralGoodsCartMapper extends SupperMapper {

	void batchDelete(List<IntegralGoodsCart> objs);

	List<IntegralGoodsCart> selectObjByProperty(Map<String, Object> maps);

	IntegralGoodsCart selectByPrimaryKey(Long id);

	List<IntegralGoodsCart> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<IntegralGoodsCart> queryByIds(List<Long> ids);

	List<IntegralGoodsCart> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(IntegralGoodsCart obj);

	void updateById(IntegralGoodsCart obj);

	void deleteById(@Param(value="id")Long id);
	List<IntegralGoodsCart> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<IntegralGoodsCart> queryPagesWithNoRelations(Map<String,Object> params);

	List<IntegralGoodsCart> queryPageListWithNoRelations(Map<String,Object> params);

}
