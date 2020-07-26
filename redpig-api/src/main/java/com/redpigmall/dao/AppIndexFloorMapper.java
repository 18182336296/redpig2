package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.AppIndexFloor;

public interface AppIndexFloorMapper extends SupperMapper {

	void batchDelete(List<AppIndexFloor> objs);

	List<AppIndexFloor> selectObjByProperty(Map<String, Object> maps);

	AppIndexFloor selectByPrimaryKey(Long id);

	List<AppIndexFloor> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<AppIndexFloor> queryByIds(List<Long> ids);

	List<AppIndexFloor> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(AppIndexFloor obj);

	void updateById(AppIndexFloor obj);

	void deleteById(@Param(value="id")Long id);
	List<AppIndexFloor> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<AppIndexFloor> queryPagesWithNoRelations(Map<String,Object> params);

	List<AppIndexFloor> queryPageListWithNoRelations(Map<String,Object> params);

}
