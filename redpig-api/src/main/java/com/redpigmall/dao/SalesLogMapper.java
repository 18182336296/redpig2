package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.SalesLog;

public interface SalesLogMapper extends SupperMapper {

	void batchDelete(List<SalesLog> objs);

	List<SalesLog> selectObjByProperty(Map<String, Object> maps);

	SalesLog selectByPrimaryKey(Long id);

	List<SalesLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<SalesLog> queryByIds(List<Long> ids);

	List<SalesLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(SalesLog obj);

	void updateById(SalesLog obj);

	void deleteById(@Param(value="id")Long id);
	List<SalesLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<SalesLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<SalesLog> queryPageListWithNoRelations(Map<String,Object> params);

}
