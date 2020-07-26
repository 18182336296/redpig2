package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupFloor;

public interface GroupFloorMapper extends SupperMapper {

	void batchDelete(List<GroupFloor> objs);

	List<GroupFloor> selectObjByProperty(Map<String, Object> maps);

	GroupFloor selectByPrimaryKey(Long id);

	List<GroupFloor> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupFloor> queryByIds(List<Long> ids);

	List<GroupFloor> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GroupFloor obj);

	void updateById(GroupFloor obj);

	void deleteById(@Param(value="id")Long id);

	List<GroupFloor> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupFloor> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupFloor> queryPageListWithNoRelations(Map<String,Object> params);

}
