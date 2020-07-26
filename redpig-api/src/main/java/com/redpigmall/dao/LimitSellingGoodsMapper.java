package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.LimitSellingGoods;

public interface LimitSellingGoodsMapper extends SupperMapper {

	void batchDelete(List<LimitSellingGoods> objs);

	List<LimitSellingGoods> selectObjByProperty(Map<String, Object> maps);

	LimitSellingGoods selectByPrimaryKey(Long id);

	List<LimitSellingGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<LimitSellingGoods> queryByIds(List<Long> ids);

	List<LimitSellingGoods> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(LimitSellingGoods obj);

	void updateById(LimitSellingGoods obj);

	void deleteById(@Param(value = "id") Long id);

	List<LimitSellingGoods> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<LimitSellingGoods> queryPagesWithNoRelations(Map<String, Object> params);

	List<LimitSellingGoods> queryPageListWithNoRelations(Map<String, Object> params);

}
