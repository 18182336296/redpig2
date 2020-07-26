package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.ShipAddress;

public interface ShipAddressMapper extends SupperMapper {

	void batchDelete(List<ShipAddress> objs);

	List<ShipAddress> selectObjByProperty(Map<String, Object> maps);

	ShipAddress selectByPrimaryKey(Long id);

	List<ShipAddress> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<ShipAddress> queryByIds(List<Long> ids);

	List<ShipAddress> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(ShipAddress obj);

	void updateById(ShipAddress obj);

	void deleteById(@Param(value="id")Long id);
	List<ShipAddress> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<ShipAddress> queryPagesWithNoRelations(Map<String,Object> params);

	List<ShipAddress> queryPageListWithNoRelations(Map<String,Object> params);

}
