package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.AppPushLog;

public interface AppPushLogMapper extends SupperMapper {

	void batchDelete(List<AppPushLog> objs);

	List<AppPushLog> selectObjByProperty(Map<String, Object> maps);

	AppPushLog selectByPrimaryKey(Long id);

	List<AppPushLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<AppPushLog> queryByIds(List<Long> ids);

	List<AppPushLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(AppPushLog obj);

	void updateById(AppPushLog obj);

	void deleteById(@Param(value="id")Long id);
	List<AppPushLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<AppPushLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<AppPushLog> queryPageListWithNoRelations(Map<String,Object> params);

}
