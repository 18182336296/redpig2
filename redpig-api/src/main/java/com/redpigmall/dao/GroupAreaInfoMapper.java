package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.GroupAreaInfo;

public interface GroupAreaInfoMapper extends SupperMapper {

	void batchDelete(List<GroupAreaInfo> objs);

	List<GroupAreaInfo> selectObjByProperty(Map<String, Object> maps);

	GroupAreaInfo selectByPrimaryKey(Long id);

	List<GroupAreaInfo> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<GroupAreaInfo> queryByIds(List<Long> ids);

	List<GroupAreaInfo> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(GroupAreaInfo obj);

	void updateById(GroupAreaInfo obj);

	void deleteById(@Param(value="id")Long id);
	List<GroupAreaInfo> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<GroupAreaInfo> queryPagesWithNoRelations(Map<String,Object> params);

	List<GroupAreaInfo> queryPageListWithNoRelations(Map<String,Object> params);

}
