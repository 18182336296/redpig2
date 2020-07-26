package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupIndex;

public interface GroupIndexMapper extends SupperMapper {

	void batchDelete(List<GroupIndex> objs);

	List<GroupIndex> selectObjByProperty(Map<String, Object> maps);

	GroupIndex selectByPrimaryKey(Long id);

	List<GroupIndex> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupIndex> queryByIds(List<Long> ids);

	List<GroupIndex> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GroupIndex obj);

	void updateById(GroupIndex obj);

	void deleteById(@Param(value="id")Long id);
	List<GroupIndex> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupIndex> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupIndex> queryPageListWithNoRelations(Map<String,Object> params);

}
