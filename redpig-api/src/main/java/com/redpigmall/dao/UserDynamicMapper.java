package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.UserDynamic;

public interface UserDynamicMapper extends SupperMapper {

	void batchDelete(List<UserDynamic> objs);

	List<UserDynamic> selectObjByProperty(Map<String, Object> maps);

	UserDynamic selectByPrimaryKey(Long id);

	List<UserDynamic> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<UserDynamic> queryByIds(List<Long> ids);

	List<UserDynamic> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(UserDynamic obj);

	void updateById(UserDynamic obj);

	void deleteById(@Param(value="id")Long id);
	List<UserDynamic> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<UserDynamic> queryPagesWithNoRelations(Map<String,Object> params);

	List<UserDynamic> queryPageListWithNoRelations(Map<String,Object> params);

}
