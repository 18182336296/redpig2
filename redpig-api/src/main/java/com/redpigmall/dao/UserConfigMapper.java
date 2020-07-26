package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.UserConfig;

public interface UserConfigMapper extends SupperMapper {

	void batchDelete(List<UserConfig> objs);

	List<UserConfig> selectObjByProperty(Map<String, Object> maps);

	UserConfig selectByPrimaryKey(Long id);

	List<UserConfig> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<UserConfig> queryByIds(List<Long> ids);

	List<UserConfig> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(UserConfig obj);

	void updateById(UserConfig obj);

	void deleteById(@Param(value="id")Long id);
	List<UserConfig> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<UserConfig> queryPagesWithNoRelations(Map<String,Object> params);

	List<UserConfig> queryPageListWithNoRelations(Map<String,Object> params);

}
