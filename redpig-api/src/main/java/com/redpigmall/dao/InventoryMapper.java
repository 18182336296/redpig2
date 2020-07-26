package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Inventory;

public interface InventoryMapper extends SupperMapper {

	void batchDelete(List<Inventory> objs);

	List<Inventory> selectObjByProperty(Map<String, Object> maps);

	Inventory selectByPrimaryKey(Long id);

	List<Inventory> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Inventory> queryByIds(List<Long> ids);

	List<Inventory> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Inventory obj);

	void updateById(Inventory obj);

	void deleteById(@Param(value="id")Long id);
	List<Inventory> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Inventory> queryPagesWithNoRelations(Map<String,Object> params);

	List<Inventory> queryPageListWithNoRelations(Map<String,Object> params);

	List<Map<String,Object>> getGoodsId(Map<String, Object> params);

}
