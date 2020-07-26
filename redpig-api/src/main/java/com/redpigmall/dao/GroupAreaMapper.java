package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupArea;

public interface GroupAreaMapper extends SupperMapper {

	void batchDelete(List<GroupArea> objs);

	List<GroupArea> selectObjByProperty(Map<String, Object> maps);

	GroupArea selectByPrimaryKey(Long id);

	List<GroupArea> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupArea> queryByIds(List<Long> ids);

	List<GroupArea> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GroupArea obj);

	void updateById(GroupArea obj);

	void deleteById(@Param(value="id")Long id);
	List<GroupArea> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupArea> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupArea> queryPageListWithNoRelations(Map<String,Object> params);

}
