package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupClass;

public interface GroupClassMapper extends SupperMapper {

	void batchDelete(List<GroupClass> objs);

	List<GroupClass> selectObjByProperty(Map<String, Object> maps);

	GroupClass selectByPrimaryKey(Long id);

	List<GroupClass> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupClass> queryByIds(List<Long> ids);

	List<GroupClass> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GroupClass obj);

	void updateById(GroupClass obj);

	void deleteById(@Param(value="id")Long id);
	List<GroupClass> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupClass> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupClass> queryPageListWithNoRelations(Map<String,Object> params);

}
