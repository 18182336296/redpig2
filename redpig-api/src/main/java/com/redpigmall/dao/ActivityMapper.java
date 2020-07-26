package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Activity;

public interface ActivityMapper extends SupperMapper {

	void batchDelete(List<Activity> objs);

	List<Activity> selectObjByProperty(Map<String, Object> maps);
	
	Activity selectByPrimaryKey(Long id);

	List<Activity> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Activity> queryByIds(List<Long> ids);

	List<Activity> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Activity obj);

	void updateById(Activity obj);

	void deleteById(@Param(value="id")Long id);

	List<Activity> queryPages(Map<String, Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Activity> queryPagesWithNoRelations(Map<String,Object> params);

	List<Activity> queryPageListWithNoRelations(Map<String,Object> params);

}
