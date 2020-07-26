package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CloudPurchaseGoods;

public interface CloudPurchaseGoodsMapper extends SupperMapper {

	void batchDelete(List<CloudPurchaseGoods> objs);

	List<CloudPurchaseGoods> selectObjByProperty(Map<String, Object> maps);

	CloudPurchaseGoods selectByPrimaryKey(Long id);

	List<CloudPurchaseGoods> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CloudPurchaseGoods> queryByIds(List<Long> ids);

	List<CloudPurchaseGoods> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CloudPurchaseGoods obj);

	void updateById(CloudPurchaseGoods obj);

	void deleteById(@Param(value="id")Long id);
	List<CloudPurchaseGoods> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CloudPurchaseGoods> queryPagesWithNoRelations(Map<String,Object> params);

	List<CloudPurchaseGoods> queryPageListWithNoRelations(Map<String,Object> params);

}
