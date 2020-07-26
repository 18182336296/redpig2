package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.UserTag;

public interface UserTagMapper extends SupperMapper {

	void batchDelete(List<UserTag> objs);

	List<UserTag> selectObjByProperty(Map<String, Object> maps);

	UserTag selectByPrimaryKey(Long id);

	List<UserTag> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<UserTag> queryByIds(List<Long> ids);

	List<UserTag> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(UserTag obj);

	void updateById(UserTag obj);

	void deleteById(@Param(value="id")Long id);
	List<UserTag> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<UserTag> queryPagesWithNoRelations(Map<String,Object> params);

	List<UserTag> queryPageListWithNoRelations(Map<String,Object> params);

}
