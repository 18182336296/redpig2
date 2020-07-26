package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.StoreStat;

public interface StoreStatMapper extends SupperMapper {

	void batchDelete(List<StoreStat> objs);

	List<StoreStat> selectObjByProperty(Map<String, Object> maps);

	StoreStat selectByPrimaryKey(Long id);

	List<StoreStat> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<StoreStat> queryByIds(List<Long> ids);

	List<StoreStat> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(StoreStat obj);

	void updateById(StoreStat obj);

	void deleteById(@Param(value="id")Long id);
	List<StoreStat> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<StoreStat> queryPagesWithNoRelations(Map<String,Object> params);

	List<StoreStat> queryPageListWithNoRelations(Map<String,Object> params);

}
