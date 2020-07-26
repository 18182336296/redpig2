package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.UserLevel;

public interface UserLevelMapper extends SupperMapper {

	void batchDelete(List<UserLevel> objs);

	List<UserLevel> selectObjByProperty(Map<String, Object> maps);

	UserLevel selectByPrimaryKey(Long id);

	List<UserLevel> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<UserLevel> queryByIds(List<Long> ids);

	List<UserLevel> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(UserLevel obj);

	void updateById(UserLevel obj);

	void deleteById(@Param(value="id")Long id);
	List<UserLevel> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<UserLevel> queryPagesWithNoRelations(Map<String,Object> params);

	List<UserLevel> queryPageListWithNoRelations(Map<String,Object> params);

}
