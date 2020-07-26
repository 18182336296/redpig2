package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.IntegralLog;

public interface IntegralLogMapper extends SupperMapper {

	void batchDelete(List<IntegralLog> objs);

	List<IntegralLog> selectObjByProperty(Map<String, Object> maps);

	IntegralLog selectByPrimaryKey(Long id);

	List<IntegralLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<IntegralLog> queryByIds(List<Long> ids);

	List<IntegralLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(IntegralLog obj);

	void updateById(IntegralLog obj);

	void deleteById(@Param(value="id")Long id);
	List<IntegralLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<IntegralLog> queryPagesWithNoRelations(Map<String,Object> params);
	
	List<IntegralLog> queryPageListWithNoRelations(Map<String,Object> params);
	
	
	
}
