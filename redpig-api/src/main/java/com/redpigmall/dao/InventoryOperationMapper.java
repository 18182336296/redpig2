package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.InventoryOperation;

public interface InventoryOperationMapper extends SupperMapper {

	void batchDelete(List<InventoryOperation> objs);

	List<InventoryOperation> selectObjByProperty(Map<String, Object> maps);

	InventoryOperation selectByPrimaryKey(Long id);

	List<InventoryOperation> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<InventoryOperation> queryByIds(List<Long> ids);

	List<InventoryOperation> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(InventoryOperation obj);

	void updateById(InventoryOperation obj);

	void deleteById(@Param(value="id")Long id);
	List<InventoryOperation> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<InventoryOperation> queryPagesWithNoRelations(Map<String,Object> params);

	List<InventoryOperation> queryPageListWithNoRelations(Map<String,Object> params);

}
