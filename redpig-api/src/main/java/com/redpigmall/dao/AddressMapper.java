package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Address;

public interface AddressMapper extends SupperMapper {

	void batchDelete(List<Address> objs);

	List<Address> selectObjByProperty(Map<String, Object> maps);

	Address selectByPrimaryKey(Long id);

	List<Address> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Address> queryByIds(List<Long> ids);

	List<Address> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Address obj);

	void updateById(Address obj);

	void deleteById(@Param(value="id")Long id);
	
	List<Address> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Address> queryPagesWithNoRelations(Map<String,Object> params);

	List<Address> queryPageListWithNoRelations(Map<String,Object> params);

}
