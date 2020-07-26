package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.AppTopics;

public interface AppTopicsMapper extends SupperMapper {

	void batchDelete(List<AppTopics> objs);

	List<AppTopics> selectObjByProperty(Map<String, Object> maps);

	AppTopics selectByPrimaryKey(Long id);

	List<AppTopics> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<AppTopics> queryByIds(List<Long> ids);

	List<AppTopics> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(AppTopics obj);

	void updateById(AppTopics obj);

	void deleteById(@Param(value="id")Long id);
	List<AppTopics> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<AppTopics> queryPagesWithNoRelations(Map<String,Object> params);

	List<AppTopics> queryPageListWithNoRelations(Map<String,Object> params);

}
