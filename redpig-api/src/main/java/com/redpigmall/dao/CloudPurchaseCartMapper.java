package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.CloudPurchaseCart;

public interface CloudPurchaseCartMapper extends SupperMapper {

	void batchDelete(List<CloudPurchaseCart> objs);

	List<CloudPurchaseCart> selectObjByProperty(Map<String, Object> maps);

	CloudPurchaseCart selectByPrimaryKey(Long id);

	List<CloudPurchaseCart> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<CloudPurchaseCart> queryByIds(List<Long> ids);

	List<CloudPurchaseCart> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(CloudPurchaseCart obj);

	void updateById(CloudPurchaseCart obj);

	void deleteById(@Param(value="id")Long id);
	List<CloudPurchaseCart> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<CloudPurchaseCart> queryPagesWithNoRelations(Map<String,Object> params);

	List<CloudPurchaseCart> queryPageListWithNoRelations(Map<String,Object> params);

}
