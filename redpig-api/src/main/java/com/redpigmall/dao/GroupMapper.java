package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Group;

public interface GroupMapper extends SupperMapper {

	void batchDelete(List<Group> objs);

	List<Group> selectObjByProperty(Map<String, Object> maps);

	Group selectByPrimaryKey(Long id);

	List<Group> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Group> queryByIds(List<Long> ids);

	List<Group> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Group obj);

	void updateById(Group obj);

	void deleteById(@Param(value="id")Long id);

	List<Group> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Group> queryPagesWithNoRelations(Map<String,Object> params);

	List<Group> queryPageListWithNoRelations(Map<String,Object> params);

}
