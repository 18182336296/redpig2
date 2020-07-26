package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.FreeClass;

public interface FreeClassMapper extends SupperMapper {

	void batchDelete(List<FreeClass> objs);

	List<FreeClass> selectObjByProperty(Map<String, Object> maps);

	FreeClass selectByPrimaryKey(Long id);

	List<FreeClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<FreeClass> queryByIds(List<Long> ids);

	List<FreeClass> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(FreeClass obj);

	void updateById(FreeClass obj);

	void deleteById(@Param(value="id")Long id);
	List<FreeClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<FreeClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<FreeClass> queryPageListWithNoRelations(Map<String,Object> params);

}
