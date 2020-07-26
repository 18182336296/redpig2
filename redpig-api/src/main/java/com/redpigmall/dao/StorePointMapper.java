package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.StorePoint;

public interface StorePointMapper extends SupperMapper {

	void batchDelete(List<StorePoint> objs);

	List<StorePoint> selectObjByProperty(Map<String, Object> maps);

	StorePoint selectByPrimaryKey(Long id);

	List<StorePoint> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<StorePoint> queryByIds(List<Long> ids);

	List<StorePoint> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(StorePoint obj);

	void updateById(StorePoint obj);

	void deleteById(@Param(value="id")Long id);
	List<StorePoint> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<StorePoint> queryPagesWithNoRelations(Map<String,Object> params);

	List<StorePoint> queryPageListWithNoRelations(Map<String,Object> params);

}
