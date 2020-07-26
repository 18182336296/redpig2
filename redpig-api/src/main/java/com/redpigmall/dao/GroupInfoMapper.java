package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupInfo;

public interface GroupInfoMapper extends SupperMapper {

	void batchDelete(List<GroupInfo> objs);

	List<GroupInfo> selectObjByProperty(Map<String, Object> maps);

	GroupInfo selectByPrimaryKey(Long id);

	List<GroupInfo> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupInfo> queryByIds(List<Long> ids);

	List<GroupInfo> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GroupInfo obj);

	void updateById(GroupInfo obj);

	void deleteById(@Param(value="id")Long id);
	List<GroupInfo> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupInfo> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupInfo> queryPageListWithNoRelations(Map<String,Object> params);

}
