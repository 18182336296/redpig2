package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Store;

public interface StoreMapper extends SupperMapper {

	void batchDelete(List<Store> objs);

	List<Store> selectObjByProperty(Map<String, Object> maps);

	Store selectByPrimaryKey(Long id);

	List<Store> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Store> queryByIds(List<Long> ids);

	List<Store> queryPageListByParentIsNull(Map<String, Object> params);

	void save(Store store);
	

	void saveEntity(Store obj);

	void updateById(Store obj);

	void deleteById(@Param(value="id")Long id);
	
	List<Store> listByNoRelation(Map<String, Object> params);
	List<Store> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Store> queryPagesWithNoRelations(Map<String,Object> params);

	List<Store> queryPageListWithNoRelations(Map<String,Object> params);

}
