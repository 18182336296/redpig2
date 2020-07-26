package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CloudPurchaseOrder;

public interface CloudPurchaseOrderMapper extends SupperMapper {

	void batchDelete(List<CloudPurchaseOrder> objs);

	List<CloudPurchaseOrder> selectObjByProperty(Map<String, Object> maps);

	CloudPurchaseOrder selectByPrimaryKey(Long id);

	List<CloudPurchaseOrder> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CloudPurchaseOrder> queryByIds(List<Long> ids);

	List<CloudPurchaseOrder> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CloudPurchaseOrder obj);

	void updateById(CloudPurchaseOrder obj);

	void deleteById(@Param(value="id")Long id);
	List<CloudPurchaseOrder> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CloudPurchaseOrder> queryPagesWithNoRelations(Map<String,Object> params);

	List<CloudPurchaseOrder> queryPageListWithNoRelations(Map<String,Object> params);

}
