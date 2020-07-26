package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CloudPurchaseCode;

public interface CloudPurchaseCodeMapper extends SupperMapper {

	void batchDelete(List<CloudPurchaseCode> objs);

	List<CloudPurchaseCode> selectObjByProperty(Map<String, Object> maps);

	CloudPurchaseCode selectByPrimaryKey(Long id);

	List<CloudPurchaseCode> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CloudPurchaseCode> queryByIds(List<Long> ids);

	List<CloudPurchaseCode> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CloudPurchaseCode obj);

	void updateById(CloudPurchaseCode obj);

	void deleteById(@Param(value="id")Long id);
	List<CloudPurchaseCode> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CloudPurchaseCode> queryPagesWithNoRelations(Map<String,Object> params);

	List<CloudPurchaseCode> queryPageListWithNoRelations(Map<String,Object> params);

}
