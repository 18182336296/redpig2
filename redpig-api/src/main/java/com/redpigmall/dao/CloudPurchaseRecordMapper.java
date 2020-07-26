package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CloudPurchaseRecord;

public interface CloudPurchaseRecordMapper extends SupperMapper {

	void batchDelete(List<CloudPurchaseRecord> objs);

	List<CloudPurchaseRecord> selectObjByProperty(Map<String, Object> maps);

	CloudPurchaseRecord selectByPrimaryKey(Long id);

	List<CloudPurchaseRecord> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CloudPurchaseRecord> queryByIds(List<Long> ids);

	List<CloudPurchaseRecord> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CloudPurchaseRecord obj);

	void updateById(CloudPurchaseRecord obj);

	void deleteById(@Param(value="id")Long id);
	List<CloudPurchaseRecord> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CloudPurchaseRecord> queryPagesWithNoRelations(Map<String,Object> params);

	List<CloudPurchaseRecord> queryPageListWithNoRelations(Map<String,Object> params);

}
