package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.FreeApplyLog;

public interface FreeApplyLogMapper extends SupperMapper {

	void batchDelete(List<FreeApplyLog> objs);

	List<FreeApplyLog> selectObjByProperty(Map<String, Object> maps);

	FreeApplyLog selectByPrimaryKey(Long id);

	List<FreeApplyLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<FreeApplyLog> queryByIds(List<Long> ids);

	List<FreeApplyLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(FreeApplyLog obj);

	void updateById(FreeApplyLog obj);

	void deleteById(@Param(value="id")Long id);
	List<FreeApplyLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<FreeApplyLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<FreeApplyLog> queryPageListWithNoRelations(Map<String,Object> params);

}
