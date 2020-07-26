package com.redpigmall.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.redpigmall.domain.Subject;

public interface SubjectMapper extends SupperMapper {

	void batchDelete(List<Subject> objs);

	List<Subject> selectObjByProperty(Map<String, Object> maps);

	Subject selectByPrimaryKey(Long id);

	List<Subject> queryPageList(Map<String, Object> maps);

	Integer selectCount(Map<String, Object> maps);

	List<Subject> queryByIds(List<Long> ids);

	List<Subject> queryPageListByParentIsNull(Map<String, Object> params);

	void saveEntity(Subject obj);

	void updateById(Subject obj);

	void deleteById(@Param(value="id")Long id);
	List<Subject> queryPages(Map<String,Object> params);

	void batchDeleteByIds(List<Long> ids);

	List<Subject> queryPagesWithNoRelations(Map<String,Object> params);

	List<Subject> queryPageListWithNoRelations(Map<String,Object> params);

}
