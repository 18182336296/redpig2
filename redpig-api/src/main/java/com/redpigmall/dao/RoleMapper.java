package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Role;

public interface RoleMapper extends SupperMapper {

	void batchDelete(List<Role> objs);

	List<Role> selectObjByProperty(Map<String, Object> maps);

	Role selectByPrimaryKey(Long id);

	List<Role> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Role> queryByIds(List<Long> ids);

	List<Role> queryPageListByParentIsNull(Map<String, Object> params);

	List<Role> queryPageListByDisplayAndType(Map<String, Object> params);

	void saveEntity(Role obj);

	void updateById(Role obj);

	void deleteById(@Param(value="id")Long id);
	List<Role> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Role> queryPagesWithNoRelations(Map<String,Object> params);

	List<Role> queryPageListWithNoRelations(Map<String,Object> params);
	
	void deleteRoleRes(Map<String, Object> params);
	
	void saveRoleRes(Map<String, Object> params);

}
