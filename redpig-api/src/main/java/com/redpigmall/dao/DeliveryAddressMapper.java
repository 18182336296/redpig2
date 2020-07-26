package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.DeliveryAddress;

public interface DeliveryAddressMapper extends SupperMapper {

	void batchDelete(List<DeliveryAddress> objs);

	List<DeliveryAddress> selectObjByProperty(Map<String, Object> maps);

	DeliveryAddress selectByPrimaryKey(Long id);

	List<DeliveryAddress> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<DeliveryAddress> queryByIds(List<Long> ids);

	List<DeliveryAddress> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(DeliveryAddress obj);

	void updateById(DeliveryAddress obj);

	void deleteById(@Param(value="id")Long id);

	List<DeliveryAddress> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<DeliveryAddress> queryPagesWithNoRelations(Map<String,Object> params);

	List<DeliveryAddress> queryPageListWithNoRelations(Map<String,Object> params);

}
