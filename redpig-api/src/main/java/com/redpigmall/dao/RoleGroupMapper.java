package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.RoleGroup;

public interface RoleGroupMapper extends SupperMapper {

	void batchDelete(List<RoleGroup> objs);

	List<RoleGroup> selectObjByProperty(Map<String, Object> maps);

	RoleGroup selectByPrimaryKey(Long id);

	List<RoleGroup> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<RoleGroup> queryByIds(List<Long> ids);

	List<RoleGroup> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(RoleGroup obj);

	void updateById(RoleGroup obj);

	void deleteById(@Param(value="id")Long id);
	List<RoleGroup> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<RoleGroup> queryPagesWithNoRelations(Map<String,Object> params);

	List<RoleGroup> queryPageListWithNoRelations(Map<String,Object> params);

	List<String> queryGroupNameLists(Long id);

}
