package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.SysLog;

public interface SysLogMapper extends SupperMapper {

	void batchDelete(List<SysLog> objs);

	List<SysLog> selectObjByProperty(Map<String, Object> maps);

	SysLog selectByPrimaryKey(Long id);

	List<SysLog> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<SysLog> queryByIds(List<Long> ids);

	List<SysLog> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(SysLog obj);

	void updateById(SysLog obj);

	void deleteById(@Param(value="id")Long id);
	List<SysLog> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<SysLog> queryPagesWithNoRelations(Map<String,Object> params);

	List<SysLog> queryPageListWithNoRelations(Map<String,Object> params);

}
