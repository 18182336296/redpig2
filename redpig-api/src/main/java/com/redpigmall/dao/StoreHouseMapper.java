package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.StoreHouse;

public interface StoreHouseMapper extends SupperMapper {

	void batchDelete(List<StoreHouse> objs);

	List<StoreHouse> selectObjByProperty(Map<String, Object> maps);

	StoreHouse selectByPrimaryKey(Long id);

	List<StoreHouse> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<StoreHouse> queryByIds(List<Long> ids);

	List<StoreHouse> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(StoreHouse obj);

	void updateById(StoreHouse obj);

	void deleteById(@Param(value="id")Long id);
	List<StoreHouse> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<StoreHouse> queryPagesWithNoRelations(Map<String,Object> params);

	List<StoreHouse> queryPageListWithNoRelations(Map<String,Object> params);

}
