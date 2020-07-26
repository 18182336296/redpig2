package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.AppPushUser;

public interface AppPushUserMapper extends SupperMapper {

	void batchDelete(List<AppPushUser> objs);

	List<AppPushUser> selectObjByProperty(Map<String, Object> maps);

	AppPushUser selectByPrimaryKey(Long id);

	List<AppPushUser> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<AppPushUser> queryByIds(List<Long> ids);

	List<AppPushUser> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(AppPushUser obj);

	void updateById(AppPushUser obj);

	void deleteById(@Param(value="id")Long id);
	List<AppPushUser> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<AppPushUser> queryPagesWithNoRelations(Map<String,Object> params);

	List<AppPushUser> queryPageListWithNoRelations(Map<String,Object> params);

}
