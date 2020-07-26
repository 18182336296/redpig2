package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.UserShare;

public interface UserShareMapper extends SupperMapper {

	void batchDelete(List<UserShare> objs);

	List<UserShare> selectObjByProperty(Map<String, Object> maps);

	UserShare selectByPrimaryKey(Long id);

	List<UserShare> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<UserShare> queryByIds(List<Long> ids);

	List<UserShare> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(UserShare obj);

	void updateById(UserShare obj);

	void deleteById(@Param(value="id")Long id);
	List<UserShare> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<UserShare> queryPagesWithNoRelations(Map<String,Object> params);

	List<UserShare> queryPageListWithNoRelations(Map<String,Object> params);

}
