package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CloudPurchaseClass;

public interface CloudPurchaseClassMapper extends SupperMapper {

	void batchDelete(List<CloudPurchaseClass> objs);

	List<CloudPurchaseClass> selectObjByProperty(Map<String, Object> maps);

	CloudPurchaseClass selectByPrimaryKey(Long id);

	List<CloudPurchaseClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CloudPurchaseClass> queryByIds(List<Long> ids);

	List<CloudPurchaseClass> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CloudPurchaseClass obj);

	void updateById(CloudPurchaseClass obj);

	void deleteById(@Param(value="id")Long id);
	List<CloudPurchaseClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CloudPurchaseClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<CloudPurchaseClass> queryPageListWithNoRelations(Map<String,Object> params);

}
