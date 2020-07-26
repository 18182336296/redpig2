package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CloudPurchaseEveryColor;

public interface CloudPurchaseEveryColorMapper extends SupperMapper {

	void batchDelete(List<CloudPurchaseEveryColor> objs);

	List<CloudPurchaseEveryColor> selectObjByProperty(Map<String, Object> maps);

	CloudPurchaseEveryColor selectByPrimaryKey(Long id);

	List<CloudPurchaseEveryColor> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CloudPurchaseEveryColor> queryByIds(List<Long> ids);

	List<CloudPurchaseEveryColor> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CloudPurchaseEveryColor obj);

	void updateById(CloudPurchaseEveryColor obj);

	void deleteById(@Param(value="id")Long id);
	List<CloudPurchaseEveryColor> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CloudPurchaseEveryColor> queryPagesWithNoRelations(Map<String,Object> params);

	List<CloudPurchaseEveryColor> queryPageListWithNoRelations(Map<String,Object> params);

}
